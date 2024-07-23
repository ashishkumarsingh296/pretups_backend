package com.inter.meditel.invoice;

/**
 * @(#)OrangeInvoicePaymentINHandler.java
 *                                        Copyright(c) 2007, Bharti Telesoft
 *                                        Int. Public Ltd.
 *                                        All Rights Reserved
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Author Date History
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Narendra Kumar JAN 02, 2014 Initial
 *                                        Creation
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        --------------------
 *                                        This class is the Handler class for
 *                                        the Oange Meditel interface.
 *                                        This File is used for PPB Invoice
 *                                        Payment handler and calling Flat file
 *                                        in Meditel .
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.ManagePaymentPaymentManagementBindingStub;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.EBSModel.EP.In.Currency;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.EBSModel.EP.In.CustomerOrder;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.EBSModel.EP.In.CustomerOrderItem;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.EBSModel.EP.In.FunctionSpecification;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.EBSModel.EP.In.InstalledPublicKey;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.EBSModel.EP.In.PaymentViewForExecutePaymentRequest;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.EBSModel.EP.In.ProductOrderItem;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.EBSModel.EP.In.ProductSpecification;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.EBSModel.EP.Out.PaymentViewForExecutePaymentResponse;

public class OrangeInvoicePaymentINHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(OrangeInvoicePaymentINHandler.class.getName());
    private HashMap _requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the response of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.

    /**
     * This method would be used to validate the subscriber's account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OrangeInvoicePaymentINHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting with  _requestMap: " + _requestMap);
        }
    }// end of validate

    /**
     * This method would be used to credit the subscriber's account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit", "Entered p_requestMap: " + p_requestMap);
        double orangeMultFactorDouble = 0;
        _requestMap = p_requestMap;
        String local_ExternalPaymentID = null;
        String amount = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            local_ExternalPaymentID = "T" + _referenceID;
            _requestMap.put("NEW_TRANSACTION_ID", local_ExternalPaymentID);
            String orangeMultiplicationFactor = FileCache.getValue(_interfaceID, "ORANGE_MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "orangeMultiplicationFactor:" + orangeMultiplicationFactor);
            if (InterfaceUtil.isNullString(orangeMultiplicationFactor)) {
                _log.error("credit", "ORANGE_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OrangeInvoicePaymentINHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "ORANGE_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            orangeMultiplicationFactor = orangeMultiplicationFactor.trim();
            _requestMap.put("ORANGE_MULT_FACTOR", orangeMultiplicationFactor);
            orangeMultFactorDouble = Double.parseDouble(orangeMultiplicationFactor);
            amount = (String) _requestMap.get("REQUESTED_AMOUNT"); // narendra
                                                                   // doubt

            amount = InterfaceUtil.getSystemAmountFromINAmount(amount, orangeMultFactorDouble);
            _requestMap.put("AMOUNT", amount);
            // Set the interface parameters into requestMap
            setInterfaceParameters(OrangeInvoicePaymentI.ACTION_INVOICE_PAYMENT, _requestMap);
            // sending the Voucher Re-charge request to IN along with recharge
            // action defined in HuaweiI interface
            sendRequestToIN(OrangeInvoicePaymentI.ACTION_INVOICE_PAYMENT, _requestMap);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME", InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit", "BTSLBaseException be:" + be.getMessage());
            if (!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try {
                _requestMap.put("TRANSACTION_TYPE", "CR");
                handleCancelTransaction();
            } catch (BTSLBaseException bte) {
                throw bte;
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OrangeInvoicePaymentINHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }
    }// end of credit

    /**
     * This method would be used to adjust the credit of subscriber account at
     * the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }

    /**
     * This method would be used to adjust the debit of subscriber account at
     * the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }// end of debitAdjust

    /**
     * This method is responsible to send the request to IN.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */
    public void sendRequestToIN(int p_action, HashMap<String, String> p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", " p_action=" + p_action);

        _requestMap = p_requestMap;
        // Put the request string, action, interface id, network code in the
        // Transaction log.
        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "_requestMap: " + _requestMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + p_action);
        try {
            if (p_action == OrangeInvoicePaymentI.ACTION_INVOICE_PAYMENT) {
                _responseMap = sendInvoicePaymentRequestToOrangeServer(_requestMap);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN : ACTION_INVOICE_PAYMENT ", "Received Response Map =" + _responseMap);
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "ACTION_INVOICE_PAYMENT=" + p_action);
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OrangeInvoicePaymentINHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "System Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exited _responseMap: " + _responseMap);

        }// end of finally
    }// end of sendRequestToIN

    public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }// end of validityAdjust

    /**
     * This method is used to set the interface parameters into request map.
     * 
     * @param int p_action
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void setInterfaceParameters(int p_action, HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_action = " + p_action, "Entered p_action = " + p_requestMap);
        try {
            Date date = new Date();
            _requestMap = p_requestMap;
            String txnDate = null;
            String txnTime = null;
            switch (p_action) {

            case OrangeInvoicePaymentI.ACTION_INVOICE_PAYMENT: // Need to get
                                                               // the values for
                                                               // Voucher
                                                               // recharge

                String POSValue = FileCache.getValue(_interfaceID, "POS_VALUE");
                POSValue = POSValue.trim();
                _requestMap.put("POS_VALUE", POSValue);

                String localChannel = FileCache.getValue(_interfaceID, "LOCAL_CHANNEL");
                localChannel = localChannel.trim();
                _requestMap.put("LOCAL_CHANNEL", localChannel);

                String fractionDigits = FileCache.getValue(_interfaceID, "FRACTION_DIGITS");
                _requestMap.put("FRACTION_DIGITS", fractionDigits.trim());

                String currencyCode = FileCache.getValue(_interfaceID, "CURRENCY_CODE");
                _requestMap.put("CURRENCY_CODE", currencyCode.trim());

                String reqType = FileCache.getValue(_interfaceID, "REQ_TYPE");
                _requestMap.put("REQ_TYPE", reqType.trim());

                String RemoteAddress = FileCache.getValue(_interfaceID, "REMOTE_ADDRESS");
                _requestMap.put("REMOTE_ADDRESS", RemoteAddress.trim());

                String functionSpecificationLabel = FileCache.getValue(_interfaceID, "FUNCTION_LABEL");
                _requestMap.put("FUNCTION_LABEL", functionSpecificationLabel.trim());

                String url = FileCache.getValue(_interfaceID, "URL");
                if (InterfaceUtil.isNullString(url)) {
                    _log.error("setInterfaceParameters", "Value of URL is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OrangeInvoicePaymentINHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "URL is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("URL", url.trim());

                String userName = FileCache.getValue(_interfaceID, "USERNAME");
                _requestMap.put("USERNAME", userName.trim());

                String password = FileCache.getValue(_interfaceID, "PASSWORD");
                _requestMap.put("PASSWORD", password.trim());

                String timeout = FileCache.getValue(_interfaceID, "TIME_OUT");
                _requestMap.put("TIME_OUT", timeout.trim());

                txnDate = String.valueOf(BTSLUtil.getDateStringFromDate(date, "yyyy-MM-dd"));
                txnTime = String.valueOf(BTSLUtil.getDateStringFromDate(date, "HH:mm:ss"));
                String txnDateTime = txnDate + "T" + txnTime;
                _requestMap.put("TXN_DATE_TIME", txnDateTime.trim());

                break;
            }
        }// end of try block
        catch (BTSLBaseException be) {
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
     * Method to send EVR request to IN to Recharge.
     * 
     * @throws BTSLBaseException
     */
    private HashMap sendInvoicePaymentRequestToOrangeServer(HashMap<String, String> p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendInvoicePaymentRequestToOrangeServer", "Entered p_requestMap = " + p_requestMap);

        _requestMap = p_requestMap;
        HashMap responseMap = new HashMap<String, String>();
        ManagePaymentPaymentManagementBindingStub _servicePayment = null;
        PaymentViewForExecutePaymentResponse[] paymentViewForExecutePaymentResponse = null;
        PaymentViewForExecutePaymentRequest paymentViewForExecutePaymentRequestObj = null;
        PaymentViewForExecutePaymentResponse paymentViewForExecutePaymentResponseObj = null;
        PaymentViewForExecutePaymentRequest[] paymentRequestObject = null;
        PaymentViewForExecutePaymentRequest paymentRequestObjectforIN = null;
        FunctionSpecification functionSpecification = null;
        InstalledPublicKey installedPublicKey = null;
        ProductSpecification productSpecification = null;
        InstalledPublicKey[] installedProduct = null;
        ProductOrderItem productOrderItem = null;
        CustomerOrderItem customerOrderItem = null;
        CustomerOrder customerOrder = null;
        Currency currency = null;
        Calendar myCal = new GregorianCalendar();
        InvoiceTestConnector serviceConnection = null;
        long startTime = 0;
        long endTime = 0;
        PostPaidFlatFile postPaidFlatFile = new PostPaidFlatFile();

        try {
            paymentViewForExecutePaymentRequestObj = new PaymentViewForExecutePaymentRequest();
            installedPublicKey = new InstalledPublicKey();
            functionSpecification = new FunctionSpecification();
            paymentViewForExecutePaymentResponseObj = new PaymentViewForExecutePaymentResponse();

            paymentViewForExecutePaymentRequestObj.setLocal_ExternalPaymentID(String.valueOf(_requestMap.get("NEW_TRANSACTION_ID")));
            paymentViewForExecutePaymentRequestObj.setLocal_Channel(String.valueOf(_requestMap.get("LOCAL_CHANNEL")));
            paymentViewForExecutePaymentRequestObj.setAmount(Integer.valueOf(String.valueOf(_requestMap.get("AMOUNT"))));
            paymentViewForExecutePaymentRequestObj.setFractionDigits(Integer.valueOf(String.valueOf(_requestMap.get("FRACTION_DIGITS"))));

            Date nextScheduleDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            nextScheduleDate = myCal.getTime();
            sdf.format(nextScheduleDate).toString();
            myCal.setTime(nextScheduleDate);
            paymentViewForExecutePaymentRequestObj.setPaymentDate(myCal);

            functionSpecification.setFunctionSpecificationLabel((String) _requestMap.get("FUNCTION_LABEL"));
            installedPublicKey.setFunctionValue(String.valueOf(_requestMap.get("MSISDN")));
            installedProduct = new InstalledPublicKey[1];
            installedProduct[0] = installedPublicKey;
            installedPublicKey.setFunctionSpecification(functionSpecification);

            productSpecification = new ProductSpecification(null, null, null, installedProduct);
            productOrderItem = new ProductOrderItem(productSpecification, null);
            ProductOrderItem[] items = new ProductOrderItem[1];
            items[0] = productOrderItem;
            customerOrderItem = new CustomerOrderItem(null, items, null);
            CustomerOrderItem[] customerOrder1 = new CustomerOrderItem[2];
            customerOrder1[0] = customerOrderItem;
            customerOrder = new CustomerOrder(null, customerOrder1);
            currency = new Currency();
            currency.setCurrencyCode((String) _requestMap.get("CURRENCY_CODE"));
            CustomerOrder[] customerOrderobj = new CustomerOrder[1];
            customerOrderobj[0] = customerOrder;

            paymentRequestObjectforIN = new PaymentViewForExecutePaymentRequest(paymentViewForExecutePaymentRequestObj.getLocal_ExternalPaymentID(), paymentViewForExecutePaymentRequestObj.getLocal_Channel(), null, paymentViewForExecutePaymentRequestObj.getAmount(), paymentViewForExecutePaymentRequestObj.getFractionDigits(), paymentViewForExecutePaymentRequestObj.getPaymentDate(), null, null, null, null, null, null, null, null, null, customerOrderobj, currency, null);
            paymentRequestObject = new PaymentViewForExecutePaymentRequest[17];
            paymentRequestObject[0] = paymentRequestObjectforIN;
            try {
                serviceConnection = new InvoiceTestConnector(_requestMap);
                _servicePayment = (ManagePaymentPaymentManagementBindingStub) serviceConnection.getService();
                System.out.println("Entered the logger$$$$" + _servicePayment);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (_servicePayment == null) {
                _log.error("sendInvoicePaymentRequestToOrangeServer: ", "Remote exception from interface.Connection not Established properly.");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            } else {
                startTime = System.currentTimeMillis(); // Start Time of
                                                        // Request.
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                paymentViewForExecutePaymentResponse = _servicePayment.executePayment(paymentRequestObject);
                if (_log.isDebugEnabled())
                    _log.debug("sendInvoicePaymentRequestToOrangeServer", "PaymentViewForExecutePaymentResponse=" + paymentViewForExecutePaymentResponse);
            }
            if (paymentViewForExecutePaymentResponse == null) {
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                _log.error("sendInvoicePaymentRequestToOrangeServer: ", "Response Object is not coming from WSDL.");
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            }
            endTime = System.currentTimeMillis();// End Time of Request.
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            String timeOutStr = FileCache.getValue(_interfaceID, "TIME_OUT");
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "WAITING FOR IN RESPONSE _socketConnection:");
            try {
                if (!InterfaceUtil.isNullString(timeOutStr)) {
                    long timeOut = Long.parseLong(timeOutStr);
                    if (endTime - startTime > timeOut) {
                        _log.info("sendInvoicePaymentRequestToOrangeServer", "TIME_OUT time reaches startTime: " + startTime + " endTime: " + endTime + " timeOut: " + timeOut + " time taken: " + (endTime - startTime));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "sendInvoicePaymentRequestToOrangeServer[sendInvoicePaymentRequestToOrangeServer]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID, "Huawei IN is taking more time than the warning threshold. Total Time taken is: " + (endTime - startTime));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("sendInvoicePaymentRequestToOrangeServer", " Error occoured while reading response message Exception e :" + e.getMessage());
                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "sendInvoicePaymentRequestToOrangeServer[sendInvoicePaymentRequestToOrangeServer]", _inTXNID, _msisdn, (String) _requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + _interfaceID, "Error occoured while reading response message Exception e :" + e.getMessage());
            }
            paymentViewForExecutePaymentResponseObj = paymentViewForExecutePaymentResponse[0];
            try {
                responseMap.put("LOCAL_EXTERNALPAYMENTID", paymentViewForExecutePaymentResponseObj.getPaymentID());
                responseMap.put("LOCAL_CHANNEL", paymentViewForExecutePaymentResponseObj.getLocal_Channel());
                responseMap.put("START_DATE", paymentViewForExecutePaymentResponseObj.getPaymentStatus().getStartDate());
                responseMap.put("STATUS_CODE", paymentViewForExecutePaymentResponseObj.getPaymentStatus().getStatusCode());
            } catch (Exception e) {
                e.printStackTrace();
            }

            String resultCode = String.valueOf(responseMap.get("STATUS_CODE"));
            if (OrangeInvoicePaymentI.SUCCESS.equals(resultCode)) {
                _log.error("sendInvoicePaymentRequestToOrangeServer: ", "Response is succesfull from the IN : : " + resultCode);
                postPaidFlatFile.createFlatFile(_requestMap);
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
            } else {
                _log.error("sendInvoicePaymentRequestToOrangeServer: ", "FAIL response from interface. ResponseCode=" + _responseMap.get("RESULT_CODE"));
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OrangeInvoicePaymentINHandler[sendInvoicePaymentRequestToOrangeServer] Credit", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Remote Exception occured while getting the connection Object.");
            _log.error("sendInvoicePaymentRequestToOrangeServer", "Remote Exception occured while getting the connection Object.");
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
        } finally {
            _servicePayment.clearAttachments();
            _servicePayment.clearHeaders();
            if (_log.isDebugEnabled())
                _log.debug("sendInvoicePaymentRequestToOrangeServer", "Exited responseMap: " + responseMap);
        }
        return responseMap;
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
        ;
        try {
            _requestMap.put("REMARK1", FileCache.getValue(_interfaceID, "REMARK1"));
            _requestMap.put("REMARK2", FileCache.getValue(_interfaceID, "REMARK2"));
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
}
