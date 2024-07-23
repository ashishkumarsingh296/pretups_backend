package com.inter.ethopiabillpayment;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.apache.axis.AxisFault;

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
import com.btsl.pretups.inter.ethopiabillpayment.stub.AccountService_ETCSoap_PortType;
import com.btsl.pretups.inter.ethopiabillpayment.stub.AcctItemExDto;
import com.btsl.pretups.inter.ethopiabillpayment.stub.OweAcctItemDto;
import com.btsl.pretups.inter.ethopiabillpayment.stub.PaymentDto;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.inter.billpayment.BillPaymentI;

public class BillPaymentINHandler implements InterfaceHandler {

    private Log _log = LogFactory.getLog(BillPaymentINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the respose of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID

    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _interfaceLiveStatus = null;
    private InterfaceCloserVO _interfaceCloserVO = null;
    private InterfaceCloser _interfaceCloser = null;
    private boolean _isSameRequest = false;
    private String _interfaceClosureSupport = null;
    private String _endPointAddress = null, _userId = null, _password = null;

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @param p_requestMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void payBill(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("BillPaymentINHandler payBill()", "Entered " + InterfaceUtil.getPrintMap(p_requestMap));
        _requestMap = p_requestMap;
        AccountService_ETCSoap_PortType clientStub = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            String serviceUser = FileCache.getValue(_interfaceID, "PAYBILL_USER");
            if (InterfaceUtil.isNullString(serviceUser)) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get SERVICE USER");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_INVALID_SERVICE_USER);
            }
            _requestMap.put("SERVICE_USER", serviceUser);
            String multplicaionFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multplicaionFactor)) {
                _log.error("credit", "multFactor:" + multplicaionFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "payBill", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            double rerquestedAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            double inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(rerquestedAmtDouble, Double.parseDouble(multplicaionFactor));

            String partyCode = null;
            partyCode = (String) FileCache.getValue(_interfaceID, "PARTY_CODE");
            if (InterfaceUtil.isNullString(partyCode)) {
                partyCode = "pretups";
            }
            _requestMap.put("PARTY_CODE", partyCode);

            String roundFlag = null;
            roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "Y";
                _requestMap.put("ROUND_FLAG", roundFlag);
            }
            String amountStr = null;

            if ("Y".equals(roundFlag)) {
                amountStr = String.valueOf(Math.round(inAmount));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            } else {
                amountStr = String.valueOf(inAmount);
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }

            String userName = FileCache.getValue(_interfaceID, "AUTH_USERNAME");
            if (InterfaceUtil.isNullString(userName)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get USER Id");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_USER_ID);

            }
            _requestMap.put("AUTH_USERNAME", userName);

            String userPass = FileCache.getValue(_interfaceID, "AUTH_PASSWORD");
            if (InterfaceUtil.isNullString(userPass)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER password");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_USER_ERROR_EXPIRY);

            }
            _requestMap.put("AUTH_PASSWORD", userPass);

            String customerPass = FileCache.getValue(_interfaceID, "VALIDATE_USER_PASSSWORD");
            if (InterfaceUtil.isNullString(serviceUser)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER Password");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_INVALID_PASSWORD);

            }
            _requestMap.put("password", customerPass);

            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();

            _msisdn = (String) _requestMap.get("MSISDN");
            _inTXNID = (String) _requestMap.get("TRANSACTION_ID");
            _requestMap.put("IN_TXN_ID", _inTXNID);

            HashMap responseMap = null;
            _endPointAddress = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "END_POINT_ADDRESS");
            // Get The Pay Bill Client Object
            clientStub = getPayBillClient(userName, userPass);
            if (clientStub == null) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get Client Object");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            if (_requestMap != null) {
                if (_log.isDebugEnabled())
                    _log.debug("BillPaymentINHandler payBill()", "Before Sending Request To IN");

                responseMap = sendRequestToIN(BillPaymentI.ACTION_PAY_BILL, _requestMap, clientStub);
                if (_log.isDebugEnabled())
                    _log.debug("BillPaymentINHandler payBill()", "After Sending Request To IN");

                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Going to Validate the Pay Bill Response");
                if (responseMap != null && responseMap.get("TRANSACTION_STATUS").toString().equalsIgnoreCase(InterfaceErrorCodesI.SUCCESS)) {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("INTERFACE_POST_BALANCE", InterfaceUtil.getSystemAmountFromINAmount((String) responseMap.get("INTERFACE_POST_BALANCE"), Double.parseDouble(multplicaionFactor)));
                } else {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);

                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Final Validate for Retry Payment Response");
                    throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                }
            } else {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.PBP_INVALID_REQUEST);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Final Validate for Retry Payment Response");
                throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
            }
        } catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), be.getBtslMessages().getMessageKey());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("BillPaymentINHandler payBill()", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in payBill");
                throw new BTSLBaseException(this, "payBill", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[payBill]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while payBill");
            throw e;
        } finally {
            p_requestMap = _requestMap;
            if (_log.isDebugEnabled())
                _log.debug("BillPaymentINHandler payBill()", "Exit " + InterfaceUtil.getPrintMap(p_requestMap));
        }
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @param userName
     * @param Password
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    private AccountService_ETCSoap_PortType getPayBillClient(String userName, String Password) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("BillPaymentINHandler getPayBillClient()", "Entered" + InterfaceUtil.getPrintMap(_requestMap));
        AccountService_ETCSoap_PortType clientStub = null;
        try {
            int timeout = 0;
            try {
                timeout = Integer.parseInt(FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "TIME_OUT"));
            } catch (Exception e) {
                timeout = 4000;
            }
            // Getting End Point Address
            _endPointAddress = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "END_POINT_ADDRESS");
            if (BTSLUtil.isNullString(_endPointAddress))
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_CLIENT_OBJECT_INITIALIZATION);

            BillPaymentConnectionManager serviceConnection = new BillPaymentConnectionManager(_endPointAddress, timeout, userName, Password);
            clientStub = serviceConnection.getAccountETCPaymentClient();

        } catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[getPayBillClient]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get Client Object");
            clientStub = null;
            throw be;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[getPayBillClient]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get Client Object");
            clientStub = null;
            throw e;
        }
        if (_log.isDebugEnabled())
            _log.debug("BillPaymentINHandler getPayBillClient()", "Exit" + InterfaceUtil.getPrintMap(_requestMap) + "clientStub=" + clientStub);
        return clientStub;
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @param p_action
     * @param _requestMap
     * @param clientStub
     * @return
     * @throws BTSLBaseException
     */
    private HashMap sendRequestToIN(int p_action, HashMap _requestMap, AccountService_ETCSoap_PortType clientStub) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("BillPaymentINHandler sendRequestToIN()", "Entered" + InterfaceUtil.getPrintMap(_requestMap));
        TransactionLog.log(_interfaceID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "", PretupsI.TXN_LOG_REQTYPE_REQ, "_requestMap: " + _requestMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, " " + "");
        long startTime = 0, endTime = 0;
        PaymentDto paymentDto = null;
        OweAcctItemDto oweAcctItemDto = null;
        HashMap responseMap = null;
        try {
            startTime = System.currentTimeMillis();
            try {

                if (p_action == BillPaymentI.ACTION_PAY_BILL) {
                    if (_requestMap.get("MSISDN") != null) {
                        paymentDto = clientStub.payment(String.valueOf(_requestMap.get("MSISDN")), "", "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), String.valueOf(_requestMap.get("IN_TXN_ID")));
                    } else {
                        paymentDto = clientStub.payment("", String.valueOf(_requestMap.get("billID")), "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), String.valueOf(_requestMap.get("IN_TXN_ID")));
                    }
                    responseMap = parasePaymentResponseObject(paymentDto, _requestMap);
                } else if (p_action == BillPaymentI.ACTION_ROLLBACK_PAYMENT) {
                    if (_requestMap.get("MSISDN") != null) {
                        paymentDto = clientStub.payment(String.valueOf(_requestMap.get("MSISDN")), "", "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), "");
                    } else {
                        paymentDto = clientStub.payment("", String.valueOf(_requestMap.get("billID")), "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), "");
                    }
                    responseMap = parasePaymentResponseObject(paymentDto, _requestMap);
                } else if (p_action == BillPaymentI.ACTION_RETRY_PAYMENT) {
                    if (_requestMap.get("MSISDN") != null) {
                        paymentDto = clientStub.payment(String.valueOf(_requestMap.get("MSISDN")), "", "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), "");
                    } else {
                        paymentDto = clientStub.payment("", String.valueOf(_requestMap.get("billID")), "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), "");
                    }
                    responseMap = parasePaymentResponseObject(paymentDto, _requestMap);
                } else if (p_action == BillPaymentI.ACTION_DEPOSIT) {
                    if (_requestMap.get("MSISDN") != null) {
                        paymentDto = clientStub.payment(String.valueOf(_requestMap.get("MSISDN")), "", "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), "");
                    } else {
                        paymentDto = clientStub.payment("", String.valueOf(_requestMap.get("billID")), "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), "");
                    }
                    responseMap = parasePaymentResponseObject(paymentDto, _requestMap);
                } else if (p_action == BillPaymentI.ACTION_DEBIT_ADJUST) {
                    if (_requestMap.get("MSISDN") != null) {
                        paymentDto = clientStub.payment(String.valueOf(_requestMap.get("MSISDN")), "", "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), "");
                    } else {
                        paymentDto = clientStub.payment("", String.valueOf(_requestMap.get("billID")), "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), "");
                    }
                    responseMap = parasePaymentResponseObject(paymentDto, _requestMap);
                } else if (p_action == BillPaymentI.ACTION_CREDIT_ADJUST) {
                    if (_requestMap.get("MSISDN") != null) {
                        paymentDto = clientStub.payment(String.valueOf(_requestMap.get("MSISDN")), "", "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), "");
                    } else {
                        paymentDto = clientStub.payment("", String.valueOf(_requestMap.get("billID")), "", String.valueOf(_requestMap.get("password")), String.valueOf(_requestMap.get("INTERFACE_ROUND_AMOUNT")), String.valueOf(_requestMap.get("PARTY_CODE")), "");
                    }
                    responseMap = parasePaymentResponseObject(paymentDto, _requestMap);
                } else if (p_action == BillPaymentI.ACTION_VALIDATE) {
                    if (_requestMap.get("MSISDN") != null) {
                        oweAcctItemDto = clientStub.qryOweAcctItem(String.valueOf(_requestMap.get("MSISDN")), "", "", String.valueOf(_requestMap.get("password")));
                    } else {
                        oweAcctItemDto = clientStub.qryOweAcctItem("", String.valueOf(_requestMap.get("billNbr")), "", String.valueOf(_requestMap.get("password")));
                    }
                    responseMap = paraseValidateResponseObject(oweAcctItemDto, _requestMap);
                }

                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN", "Response = " + responseMap);
                endTime = System.currentTimeMillis();
                // if(_log.isDebugEnabled())
                // _log.debug("sendRequestToIN"," Request = "+_requestMap+" , Response = "+if(responseMap!=null)InterfaceUtil.getPrintMap(responseMap));
            } catch (java.rmi.RemoteException re) {
                responseMap = parseException(re, _requestMap);
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[sendRequestToIN]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Remote Exception occured. So marking the transaction as AMBIGUOUS");
                _log.error("BillPaymentINHandler sendRequestToIN", "Remote Exception occured. So marking the response as AMBIGUOUS");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                if (responseMap != null && responseMap.get("responseMessage") != null) {
                    throw new BTSLBaseException(String.valueOf(responseMap.get("responseMessage")));
                } else {
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
            } catch (Exception e) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[sendRequestToIN]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Error Message:" + e.getMessage());
                _log.error("BillPaymentINHandler sendRequestToIN", "Error Message :" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "Error Message:" + e.getMessage());
            _log.error("BillPaymentINHandler sendRequestToIN", "Error Message :" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (endTime == 0)
                endTime = System.currentTimeMillis();
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            _log.error("BillPaymentINHandler sendRequestToIN", "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);
        }
        if (_log.isDebugEnabled())
            _log.debug("BillPaymentINHandler sendRequestToIN()", "Exit" + InterfaceUtil.getPrintMap(_requestMap));

        TransactionLog.log(_interfaceID, _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "_requestMap: " + _requestMap + ", _responseMap: " + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + p_action);
        return responseMap;
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @param re
     * @param p_requestMap
     * @return
     */
    private HashMap parseException(RemoteException re, HashMap p_requestMap) {
        HashMap responseMap = null;
        try {
            AxisFault axisFault = (AxisFault) re;
            if (re != null) {
                responseMap = new HashMap();
                if (String.valueOf(axisFault.getFaultCode()).equalsIgnoreCase("S-SIM-03001"))
                    responseMap.put("responseMessage", InterfaceErrorCodesI.PBP_ERROR_INVALID_SERVICE_USER);
                if (String.valueOf(axisFault.getFaultCode()).equalsIgnoreCase("S-PSM-11210"))
                    responseMap.put("responseMessage", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                if (String.valueOf(axisFault.getFaultCode()).equalsIgnoreCase("S-CSP-0101"))
                    responseMap.put("responseMessage", InterfaceErrorCodesI.PBP_ERROR_USER_PASSWORD);
                if (String.valueOf(axisFault.getFaultCode()).equalsIgnoreCase("S-PSM-1205"))
                    responseMap.put("responseMessage", InterfaceErrorCodesI.PBP_ERROR_DUPLICATE_TXN);
                if (String.valueOf(axisFault.getFaultCode()).equalsIgnoreCase("S-ODE-8502"))
                    responseMap.put("responseMessage", InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
                if (String.valueOf(axisFault.getFaultCode()).equalsIgnoreCase("S-PSM-1040"))
                    responseMap.put("responseMessage", InterfaceErrorCodesI.PBP_ERROR_PAYMENT_FAILED);
                if (String.valueOf(axisFault.getFaultCode()).equalsIgnoreCase("S-ODE-9001"))
                    responseMap.put("responseMessage", InterfaceErrorCodesI.PBP_ERROR_PAYMENT_FAILED);
                else
                    responseMap = null;
            } else {
                responseMap = null;
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Exception in payment response");
                return responseMap;
            }

        } catch (Exception e) {
            responseMap = null;
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Exception in payment response");
            return responseMap;
        }
        return responseMap;
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @param oweAcctItemDto
     * @param p_requestMap
     * @return
     */
    private HashMap paraseValidateResponseObject(OweAcctItemDto oweAcctItemDto, HashMap p_requestMap) {

        HashMap responseMap = null;
        try {
            if (oweAcctItemDto != null) {
                if (!oweAcctItemDto.getPrePostpaidFlag().equalsIgnoreCase("1")) {
                    responseMap = new HashMap();
                    responseMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.PBP_UNKNOWN_USER);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Not a Post paid number");
                    return responseMap;
                } else {
                    responseMap = new HashMap();
                    responseMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

                    if (oweAcctItemDto.getBalance() != null) {
                        responseMap.put("BILL_AMOUNT_BAL", oweAcctItemDto.getBalance());
                        responseMap.put("INTERFACE_PREV_BALANCE", oweAcctItemDto.getBalance());
                    } else {
                        responseMap.put("BILL_AMOUNT_BAL", "0");
                        responseMap.put("INTERFACE_PREV_BALANCE", 0);
                    }

                    AcctItemExDto[] accountItem = oweAcctItemDto.getAcctItemExDtoList();
                    for (int i = 0; i < accountItem.length; i++) {
                        AcctItemExDto acctItemExDto = (AcctItemExDto) accountItem[0];
                        if (acctItemExDto.getAcctItemTypeID().equalsIgnoreCase(String.valueOf(p_requestMap.get("VALIDATE_USER_ACCOUNT")))) {
                            responseMap.put("SUBS_ID", acctItemExDto.getSubsID());
                        }
                    }
                    responseMap.put("OLD_EXPIRY_DATE", String.valueOf(p_requestMap.get("VALIDATE_USER_ACCOUNT")));
                }

            } else {
                responseMap = null;
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Exception in payment response");
                return responseMap;
            }
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Exception in payment response");
            responseMap = null;
        }
        return responseMap;
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @param paymentDto
     * @param p_requestMap
     * @return
     */
    private HashMap parasePaymentResponseObject(PaymentDto paymentDto, HashMap p_requestMap) {
        HashMap responseMap = null;

        try {
            if (paymentDto != null) {
                responseMap = new HashMap();
                responseMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                responseMap.put("INTERFACE_POST_BALANCE", paymentDto.getBalance());
            } else {
                responseMap = null;
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Exception in payment response");
                return responseMap;
            }
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Exception in payment response");
            responseMap = null;
        }
        return responseMap;
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @param p_requestMap
     * @return
     */

    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust()", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        AccountService_ETCSoap_PortType clientStub = null;
        try {
            if ("0".equals((String) p_requestMap.get("INTERFACE_AMOUNT")))
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _msisdn = (String) _requestMap.get("MSISDN");
            _inTXNID = (String) _requestMap.get("TRANSACTION_ID");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            String serviceUser = FileCache.getValue(_interfaceID, "CREDIT_ADJUST_USER");
            if (InterfaceUtil.isNullString(serviceUser)) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get SERVICE USER");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_INVALID_SERVICE_USER);
            }
            _requestMap.put("SERVICE_USER", serviceUser);
            String multplicaionFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multplicaionFactor)) {
                _log.error("credit", "multFactor:" + multplicaionFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "payBill", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            double rerquestedAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            double inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(rerquestedAmtDouble, Double.parseDouble(multplicaionFactor));

            String partyCode = null;
            partyCode = (String) FileCache.getValue(_interfaceID, "PARTY_CODE");
            if (InterfaceUtil.isNullString(partyCode)) {
                partyCode = "pretups";
            }
            _requestMap.put("PARTY_CODE", partyCode);

            String roundFlag = null;
            roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "Y";
                _requestMap.put("ROUND_FLAG", roundFlag);
            }
            String amountStr = null;
            if ("Y".equals(roundFlag)) {
                amountStr = String.valueOf(Math.round(inAmount));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            } else {
                amountStr = String.valueOf(inAmount);
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }

            HashMap responseMap = null;
            String userName = FileCache.getValue(_interfaceID, "AUTH_USERNAME");
            if (InterfaceUtil.isNullString(userName)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get USER Id");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_USER_ID);

            }
            _requestMap.put("AUTH_USERNAME", userName);

            String userPass = FileCache.getValue(_interfaceID, "AUTH_PASSWORD");
            if (InterfaceUtil.isNullString(userPass)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER password");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_USER_ERROR_EXPIRY);

            }
            _requestMap.put("AUTH_PASSWORD", userPass);

            String customerPass = FileCache.getValue(_interfaceID, "VALIDATE_USER_PASSSWORD");
            if (InterfaceUtil.isNullString(serviceUser)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER Password");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_INVALID_PASSWORD);

            }
            _requestMap.put("password", customerPass);

            _endPointAddress = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "END_POINT_ADDRESS");
            // Get The Pay Bill Client Object
            clientStub = getPayBillClient(userName, userPass);
            if (clientStub == null) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get Client Object");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            if (_requestMap != null) {
                if (_log.isDebugEnabled())
                    _log.debug("creditAdjust()", "Before Sending Request To IN ");

                responseMap = sendRequestToIN(BillPaymentI.ACTION_CREDIT_ADJUST, _requestMap, clientStub);
                if (_log.isDebugEnabled())
                    _log.debug("creditAdjust()", "After Sending Request To IN ");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Going to Validate the Pay Bill Response");
                if (responseMap != null && responseMap.get("TRANSACTION_STATUS").toString().equalsIgnoreCase(InterfaceErrorCodesI.SUCCESS)) {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("INTERFACE_POST_BALANCE", InterfaceUtil.getSystemAmountFromINAmount((String) responseMap.get("INTERFACE_POST_BALANCE"), Double.parseDouble(multplicaionFactor)));

                } else {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Final Validate for Retry Payment Response");
                    throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                }
            } else {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.PBP_INVALID_REQUEST);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Final Validate for Retry Payment Response");
                throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
            }
        } catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), be.getBtslMessages().getMessageKey());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("BillPaymentINHandler creditAdjust()", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in BillPaymentINHandler[creditAdjust]");
                throw new BTSLBaseException(this, "creditAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[creditAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while credit");
            throw e;
        } finally {
            p_requestMap = _requestMap;
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust()", "Exit " + InterfaceUtil.getPrintMap(p_requestMap));
        }
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @param p_requestMap
     * @return
     */

    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        AccountService_ETCSoap_PortType clientStub = null;
        try {
            if ("0".equals((String) p_requestMap.get("INTERFACE_AMOUNT")))
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_INVALID_AMOUNT);
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _msisdn = (String) _requestMap.get("MSISDN");
            _inTXNID = (String) _requestMap.get("TRANSACTION_ID");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            String serviceUser = FileCache.getValue(_interfaceID, "DEBIT_ADJUST_USER");
            if (InterfaceUtil.isNullString(serviceUser)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get SERVICE USER");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_INVALID_SERVICE_USER);

            }
            _requestMap.put("SERVICE_USER", serviceUser);
            String multplicaionFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multplicaionFactor)) {
                _log.error("credit", "multFactor:" + multplicaionFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "payBill", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            double rerquestedAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            double inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(rerquestedAmtDouble, Double.parseDouble(multplicaionFactor));
            String partyCode = null;
            partyCode = (String) FileCache.getValue(_interfaceID, "PARTY_CODE");
            if (InterfaceUtil.isNullString(partyCode)) {
                partyCode = "pretups";
            }
            _requestMap.put("PARTY_CODE", partyCode);

            String roundFlag = null;
            roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "Y";
                _requestMap.put("ROUND_FLAG", roundFlag);
            }
            String amountStr = null;
            if ("Y".equals(roundFlag)) {
                amountStr = String.valueOf(Math.round(inAmount));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            } else {
                amountStr = String.valueOf(inAmount);
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }

            String userName = FileCache.getValue(_interfaceID, "AUTH_USERNAME");
            if (InterfaceUtil.isNullString(userName)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get USER Id");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_USER_ID);

            }
            _requestMap.put("AUTH_USERNAME", userName);

            String userPass = FileCache.getValue(_interfaceID, "AUTH_PASSWORD");
            if (InterfaceUtil.isNullString(userPass)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER password");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_USER_ERROR_EXPIRY);

            }
            _requestMap.put("AUTH_PASSWORD", userPass);

            String customerPass = FileCache.getValue(_interfaceID, "VALIDATE_USER_PASSSWORD");
            if (InterfaceUtil.isNullString(serviceUser)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER Password");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_INVALID_PASSWORD);

            }
            _requestMap.put("password", customerPass);

            HashMap responseMap = null;
            // Getting End Point Address
            _endPointAddress = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "END_POINT_ADDRESS");
            // Get The Pay Bill Client Object
            clientStub = getPayBillClient(userName, userPass);
            if (clientStub == null) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get Client Object");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            if (_requestMap != null) {
                if (_log.isDebugEnabled())
                    _log.debug("BillPaymentINHandler credit()", "Before Sending Request To IN");

                responseMap = sendRequestToIN(BillPaymentI.ACTION_DEBIT_ADJUST, _requestMap, clientStub);

                if (_log.isDebugEnabled())
                    _log.debug("BillPaymentINHandler credit()", "After Sending Request To IN");

                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Going to Validate the Pay Bill Response");

                if (responseMap != null && responseMap.get("TRANSACTION_STATUS").toString().equalsIgnoreCase(InterfaceErrorCodesI.SUCCESS)) {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("INTERFACE_POST_BALANCE", InterfaceUtil.getSystemAmountFromINAmount((String) responseMap.get("INTERFACE_POST_BALANCE"), Double.parseDouble(multplicaionFactor)));
                } else {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Final Validate for Retry Payment Response");
                    throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                }
            } else {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.PBP_INVALID_REQUEST);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Final Validate for Retry Payment Response");
                throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
            }
        } catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), be.getBtslMessages().getMessageKey());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("BillPaymentINHandler debitAdjust()", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in BillPaymentINHandler[debitAdjust]");
                throw new BTSLBaseException(this, "debitAdjust", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[debitAdjust]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while credit");
            throw e;
        } finally {
            p_requestMap = _requestMap;
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust()", "Exit " + InterfaceUtil.getPrintMap(p_requestMap));
        }
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @param p_requestMap
     * @return
     */

    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate() ", " Entered p_requestMap = " + p_requestMap);
        _requestMap = p_requestMap;
        AccountService_ETCSoap_PortType clientStub = null;
        String serviceStartDateDiff = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _msisdn = (String) _requestMap.get("MSISDN");
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));

            if (BTSLUtil.isNullString(validateRequired))
                _log.error("validate ", "Value of validateRequired is not present in inerface " + _interfaceID);
            else if ("N".equals(validateRequired)) {

                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                _requestMap.put("INTERFACE_PREV_BALANCE", "0");
                _requestMap.put("BILL_AMOUNT_BAL", "0");
                _requestMap.put("SERVICE_CLASS", "ALL");
                _requestMap.put("ACCOUNT_STATUS", "Active");
                _requestMap.put("OLD_EXPIRY_DATE", "01012090");
                _requestMap.put("SUBS_ID", "01012090");

                return;
            }
            String multplicaionFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multplicaionFactor)) {
                _log.error("credit", "multFactor:" + multplicaionFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "payBill", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            if (BTSLUtil.isNullString(FileCache.getValue(_interfaceID, "NO_OF_DAYS_ALLOWED_BW_SERVICE_ST_DATE_AND_CURNT_DATE")))
                serviceStartDateDiff = "30";
            serviceStartDateDiff = FileCache.getValue(_interfaceID, "NO_OF_DAYS_ALLOWED_BW_SERVICE_ST_DATE_AND_CURNT_DATE");
            _requestMap.put("SERVICE_START_DATE_DIFF", serviceStartDateDiff);
            _inTXNID = (String) _requestMap.get("TRANSACTION_ID");
            _requestMap.put("IN_TXN_ID", _inTXNID);

            String userName = FileCache.getValue(_interfaceID, "AUTH_USERNAME");
            if (InterfaceUtil.isNullString(userName)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get USER Id");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_USER_ID);

            }
            _requestMap.put("AUTH_USERNAME", userName);

            String userPass = FileCache.getValue(_interfaceID, "AUTH_PASSWORD");
            if (InterfaceUtil.isNullString(userPass)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER password");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_USER_ERROR_EXPIRY);

            }
            _requestMap.put("AUTH_PASSWORD", userPass);

            String userExpiry = FileCache.getValue(_interfaceID, "USER_EXPIRY_DATE");
            if (InterfaceUtil.isNullString(userExpiry)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER Expiry Date");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_USER_ERROR_EXPIRY);

            }
            _requestMap.put("OLD_EXPIRY_DATE", userExpiry);

            String userValideAccount = FileCache.getValue(_interfaceID, "VALIDATE_USER_ACCOUNT");
            if (InterfaceUtil.isNullString(userValideAccount)) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER Account Info");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_USER_ACCOUNT_INFO);
            }
            _requestMap.put("VALIDATE_USER_ACCOUNT", userValideAccount);

            String serviceUser = FileCache.getValue(_interfaceID, "VALIDATE_USER");
            if (InterfaceUtil.isNullString(serviceUser)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get SERVICE USER");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_INVALID_SERVICE_USER);

            }
            _requestMap.put("SERVICE_USER", serviceUser);

            String customerPass = FileCache.getValue(_interfaceID, "VALIDATE_USER_PASSSWORD");
            if (InterfaceUtil.isNullString(serviceUser)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER Password");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_INVALID_PASSWORD);

            }
            _requestMap.put("password", customerPass);

            String serviceClass = FileCache.getValue(_interfaceID, "SERVICE_CLASS");
            if (InterfaceUtil.isNullString(serviceUser)) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER Service Class");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_SERVICE_CLASS);
            }
            _requestMap.put("SERVICE_CLASS", serviceClass);

            String accountStatus = FileCache.getValue(_interfaceID, "ACCOUNT_STATUS");
            if (InterfaceUtil.isNullString(serviceUser)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER Account Status");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_ACCOUNT_STATUS);
            }
            _requestMap.put("ACCOUNT_STATUS", accountStatus);

            HashMap responseMap = null;
            _endPointAddress = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "END_POINT_ADDRESS");

            // Get The Pay Bill Client Object
            clientStub = getPayBillClient(userName, userPass);
            if (clientStub == null) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get Client Object");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            if (_requestMap != null) {
                if (_log.isDebugEnabled())
                    _log.debug("validate()", "Before Sending Request To IN");

                responseMap = sendRequestToIN(BillPaymentI.ACTION_VALIDATE, _requestMap, clientStub);

                if (_log.isDebugEnabled())
                    _log.debug("validate()", "After Sending Request To IN");

                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Going to Validate the Pay Bill Response");

                if (responseMap != null && responseMap.get("TRANSACTION_STATUS").toString().equalsIgnoreCase(InterfaceErrorCodesI.SUCCESS)) {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("INTERFACE_PREV_BALANCE", InterfaceUtil.getSystemAmountFromINAmount((String) responseMap.get("INTERFACE_PREV_BALANCE"), Double.parseDouble(multplicaionFactor)));
                    _requestMap.put("BILL_AMOUNT_BAL", InterfaceUtil.getSystemAmountFromINAmount((String) responseMap.get("BILL_AMOUNT_BAL"), Double.parseDouble(multplicaionFactor)));
                    _requestMap.put("SUBS_ID", (String) responseMap.get("SUBS_ID"));
                } else {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Fail in  Validate for Response");
                    throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                }
            } else {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.PBP_INVALID_REQUEST);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Fail in  Validate for Response");
                throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
            }
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException be=" + be.getMessage());
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO, _requestMap);
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate()", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[validate]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate()", " Exited _requestMap:" + _requestMap);
        }
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @throws BTSLBaseException
     */
    private void checkInterfaceB4SendingRequest() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("BillPaymentINHandler checkInterfaceB4SendingRequest", "Entered");

        try {
            _interfaceCloserVO = (InterfaceCloserVO) InterfaceCloserController._interfaceCloserVOTable.get(_interfaceID);
            _interfaceLiveStatus = (String) _requestMap.get("INT_ST_TYPE");
            _interfaceCloserVO.setControllerIntStatus(_interfaceLiveStatus);
            _interfaceCloser = _interfaceCloserVO.getInterfaceCloser();
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
                    throw new BTSLBaseException(this, "BillPaymentINHandler checkInterfaceB4SendingRequest", InterfaceErrorCodesI.INTERFACE_SUSPENDED);
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
            _log.error("BillPaymentINHandler checkInterfaceB4SendingRequest", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "checkInterfaceB4SendingRequest", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("BillPaymentINHandler checkInterfaceB4SendingRequest", "Exited");
        }
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     * @param p_requestMap
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void deposit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("deposit()", "Entered " + InterfaceUtil.getPrintMap(p_requestMap));
        _requestMap = p_requestMap;
        AccountService_ETCSoap_PortType clientStub = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport = FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _msisdn = (String) _requestMap.get("MSISDN");
            _inTXNID = (String) _requestMap.get("TRANSACTION_ID");
            _requestMap.put("IN_TXN_ID", _inTXNID);
            String serviceUser = FileCache.getValue(_interfaceID, "DEPOSIT_USER");
            if (InterfaceUtil.isNullString(serviceUser)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get SERVICE USER");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_INVALID_SERVICE_USER);

            }
            _requestMap.put("SERVICE_USER", serviceUser);
            String multplicaionFactor = (String) FileCache.getValue(_interfaceID, "MULTIPLICATION_FACTOR");
            // Handle the event if the Multiplication factor is not defined to
            // the INFile
            if (InterfaceUtil.isNullString(multplicaionFactor)) {
                _log.error("credit", "multFactor:" + multplicaionFactor);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", _inTXNID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Multiplication factor is not defined in IN File");
                throw new BTSLBaseException(this, "payBill", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            double rerquestedAmtDouble = Double.parseDouble((String) _requestMap.get("INTERFACE_AMOUNT"));
            double inAmount = InterfaceUtil.getINAmountFromSystemAmountToIN(rerquestedAmtDouble, Double.parseDouble(multplicaionFactor));
            String partyCode = null;
            partyCode = (String) FileCache.getValue(_interfaceID, "PARTY_CODE");
            if (InterfaceUtil.isNullString(partyCode)) {
                partyCode = "pretups";
            }
            _requestMap.put("PARTY_CODE", partyCode);
            String roundFlag = null;
            roundFlag = (String) FileCache.getValue(_interfaceID, "ROUND_FLAG");
            if (InterfaceUtil.isNullString(roundFlag)) {
                roundFlag = "Y";
                _requestMap.put("ROUND_FLAG", roundFlag);
            }
            String amountStr = null;
            if ("Y".equals(roundFlag)) {
                amountStr = String.valueOf(Math.round(inAmount));
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            } else {
                amountStr = String.valueOf(inAmount);
                _requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
            }

            String userName = FileCache.getValue(_interfaceID, "AUTH_USERNAME");
            if (InterfaceUtil.isNullString(userName)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get USER Id");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_USER_ID);

            }
            _requestMap.put("AUTH_USERNAME", userName);

            String userPass = FileCache.getValue(_interfaceID, "AUTH_PASSWORD");
            if (InterfaceUtil.isNullString(userPass)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER password");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_USER_ERROR_EXPIRY);

            }
            _requestMap.put("AUTH_PASSWORD", userPass);

            String customerPass = FileCache.getValue(_interfaceID, "VALIDATE_USER_PASSSWORD");
            if (InterfaceUtil.isNullString(serviceUser)) {

                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get  USER Password");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_INVALID_PASSWORD);

            }
            _requestMap.put("password", customerPass);

            HashMap responseMap = null;
            // Getting End Point Address
            _endPointAddress = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + "END_POINT_ADDRESS");
            // Get The Pay Bill Client Object
            clientStub = getPayBillClient(userName, userPass);
            if (clientStub == null) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", "REFERENCE ID = " + _inTXNID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Unable to get Client Object");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            if (_requestMap != null) {
                if (_log.isDebugEnabled())
                    _log.debug("deposit()", "Before Sending Request To IN");

                responseMap = sendRequestToIN(BillPaymentI.ACTION_DEPOSIT, _requestMap, clientStub);

                if (_log.isDebugEnabled())
                    _log.debug("deposit()", "After Sending Request To IN");

                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Going to Validate the Pay Bill Response");

                if (responseMap != null && responseMap.get("TRANSACTION_STATUS").toString().equalsIgnoreCase(InterfaceErrorCodesI.SUCCESS)) {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                    _requestMap.put("INTERFACE_POST_BALANCE", InterfaceUtil.getSystemAmountFromINAmount((String) responseMap.get("INTERFACE_POST_BALANCE"), Double.parseDouble(multplicaionFactor)));
                } else {
                    _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.FAIL);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Final Validate for Retry Payment Response");
                    throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
                }
            } else {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.PBP_INVALID_REQUEST);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Final Validate for Retry Payment Response");
                throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
            }
        } catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), be.getBtslMessages().getMessageKey());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("BillPaymentINHandler deposit()", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in BillPaymentINHandler[deposit]");
                throw new BTSLBaseException(this, "deposit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[deposit]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while deposit");
            throw e;
        } finally {
            p_requestMap = _requestMap;
            if (_log.isDebugEnabled())
                _log.debug("deposit()", "Exit " + InterfaceUtil.getPrintMap(p_requestMap));
        }
    }

    /**
     * @author vipan.kumar
     * @Date 06 Sep ,2013
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit()", "Entered " + InterfaceUtil.getPrintMap(p_requestMap));
        try {
            if (PretupsI.SERVICE_TYPE_CHNL_BILLPAY.equals(p_requestMap.get("REQ_SERVICE")))
                payBill(p_requestMap);
            else if (PretupsI.SERVICE_TYPE_POSTPAID_BILL_DEPOSIT.equals(p_requestMap.get("REQ_SERVICE")))
                deposit(p_requestMap);
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BillPaymentINHandler[credit]", (String) _requestMap.get("TRANSACTION_ID"), (String) _requestMap.get("MSISDN"), (String) _requestMap.get("NETWORK_CODE"), "Exception while deposit");
            throw e;
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
        // int cancelRetryCount=0;
        try {
            _requestMap.put("REMARK1", FileCache.getValue(_interfaceID, "REMARK1"));
            _requestMap.put("REMARK2", FileCache.getValue(_interfaceID, "REMARK2"));
            _requestMap.put("SYSTEM_STATUS_MAPPING", FileCache.getValue(_interfaceID, "SYSTEM_STATUS_MAPPING"));
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", FileCache.getValue(_interfaceID, "CANCEL_COMMAND_STATUS_MAPPING"));
            // get reconciliation log object associated with interface
            reconLog = ReconcialiationLog.getLogObject(_interfaceID);
            if (_log.isDebugEnabled())
                _log.debug("handleCancelTransaction", "reconLog." + reconLog);
            cancelTxnAllowed = (String) FileCache.getValue(_interfaceID, "CANCEL_TXN_ALLOWED");
            // if cancel transaction is not supported by IN, get error codes
            // from mapping present in IN fILE,write it
            // into reconciliation log and throw exception (This exception tells
            // the final status of transaction which was ambiguous) which would
            // be handled by validate, credit or debitAdjust methods
            if ("N".equals(cancelTxnAllowed)) {
                cancelNA = (String) FileCache.getValue(_interfaceID, "CANCEL_NA");// Cancel
                                                                                  // command
                                                                                  // status
                                                                                  // as
                                                                                  // NA.
                cancelCommandStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap, cancelNA, "CANCEL_COMMAND_STATUS_MAPPING");
                _requestMap.put("MAPPED_CANCEL_STATUS", cancelCommandStatus);
                interfaceStatus = (String) _requestMap.get("INTERFACE_STATUS");
                cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap, interfaceStatus, "SYSTEM_STATUS_MAPPING"); // PreTUPs
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
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.AMBIGOUS);
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

    public void validityAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception {
        // TODO Auto-generated method stub

    }
}
