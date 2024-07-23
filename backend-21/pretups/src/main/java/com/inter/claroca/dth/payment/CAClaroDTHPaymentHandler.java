package com.inter.claroca.dth.payment;


/**
 * @(#)CAClaroDTHPaymentHandler.java
 *                                        Copyright(c) 2016, Comviva Technologies Limited
 *                                        
 *                                        All Rights Reserved
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Author Date History
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Gopal 01 AUG, 2016 Initial
 *                                        Creation
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        --------------------
 *                                        This class is the Handler class for
 *                                        the Payment for Central America Claro.
 *                                        
 *                                        
 *                                        */

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.util.ArrayList;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.logging.TransactionLog;


public class CAClaroDTHPaymentHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(CAClaroDTHPaymentHandler.class.getName());
    private HashMap <String,String>_requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap <String,String> _responseMap = null;// Contains the response of the request
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
    public void validate(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception {
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
                setInterfaceParameters(CAClaroDTHI.ACTION_DTH_ENQUIRY, _requestMap);
                sendRequestToIN(CAClaroDTHI.ACTION_DTH_ENQUIRY, _requestMap);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
         _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHPaymentHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
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
    public void credit(HashMap <String,String> p_requestMap) throws BTSLBaseException, Exception {
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
            local_ExternalPaymentID = getINTransactionID(_requestMap);
            _requestMap.put("NEW_TRANSACTION_ID", local_ExternalPaymentID);
            String paymentMultiplicationFactor = FileCache.getValue(_interfaceID, "PAYMENT_MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "paymentMultiplicationFactor:" + paymentMultiplicationFactor);
            if (InterfaceUtil.isNullString(paymentMultiplicationFactor)) {
                _log.error("credit", "PAYMENT_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHPaymentHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PAYMENT_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            paymentMultiplicationFactor = paymentMultiplicationFactor.trim();
            _requestMap.put("PAYMENT_MULT_FACTOR", paymentMultiplicationFactor);
            orangeMultFactorDouble = Double.parseDouble(paymentMultiplicationFactor);
            amount = (String) _requestMap.get("REQUESTED_AMOUNT"); 
                                                                   

            amount = InterfaceUtil.getSystemAmountFromINAmount(amount, orangeMultFactorDouble);
            _requestMap.put("AMOUNT", amount);
            // Set the interface parameters into requestMap
            setInterfaceParameters(CAClaroDTHI.ACTION_PAYMENT, _requestMap);
            
            sendRequestToIN(CAClaroDTHI.ACTION_PAYMENT, _requestMap);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
           
           
                throw be;
           
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHPaymentHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
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

        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "_requestMap: " + _requestMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + p_action);
        try {
                if (p_action == CAClaroDTHI.ACTION_DTH_ENQUIRY) {
                _responseMap = sendDTHEnquiryRequest(_requestMap);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN : ACTION_ENQUIRY_PAYMENT ", "Received Response Map =" + _responseMap);
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "ACTION_PAYMENT=" + p_action);
            }
          else  if (p_action == CAClaroDTHI.ACTION_PAYMENT) {
                _responseMap = sendPaymentRequest(_requestMap);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN : ACTION_PAYMENT ", "Received Response Map =" + _responseMap);
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "ACTION_PAYMENT=" + p_action);
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHPaymentHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "System Exception=" + e.getMessage());
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
    public void setInterfaceParameters(int p_action, HashMap <String,String>p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_action = " + p_action, "Entered p_action = " + p_requestMap);
        try {
                Date date = new Date();
            _requestMap = p_requestMap;
                String txnDate = null;
                String txnTime = null;

                                                               
                             

                String endPoint = FileCache.getValue(_interfaceID, "END_POINT");
                if (InterfaceUtil.isNullString(endPoint)) {
                    _log.error("setInterfaceParameters", "Value of END_POINT is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "END_POINT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("END_POINT", endPoint.trim());

                String countryCode = FileCache.getValue(_interfaceID, "COUNTRY_CODE");
                if (InterfaceUtil.isNullString(countryCode)) {
                    _log.error("setInterfaceParameters", "Value of COUNTRY_CODE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "COUNTRY_CODE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("COUNTRY_CODE",countryCode.trim());

                String tipoProduct = FileCache.getValue(_interfaceID, "TIPO_PRODUCT");
                if (InterfaceUtil.isNullString(tipoProduct)) {
                        _log.error("setInterfaceParameters", "Value of TIPO_PRODUCT is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TIPO_PRODUCT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("TIPO_PRODUCT", tipoProduct.trim());
                String contractFlag = FileCache.getValue(_interfaceID, "CONTRACT_FLAG");
                if (InterfaceUtil.isNullString(contractFlag)) {
                        _log.error("setInterfaceParameters", "Value of CONTRACT_FLAG is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "CONTRACT_FLAG is not defined in the INFile.");
                        throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("CONTRACT_FLAG", contractFlag.trim());
                String orgaMethod = FileCache.getValue(_interfaceID, "ORGA_METHOD");
                if (InterfaceUtil.isNullString(orgaMethod)) {
                        _log.error("setInterfaceParameters", "Value of ORGA_METHOD is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ORGA_METHOD is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("ORGA_METHOD", orgaMethod.trim());
                
                String bankCode=FileCache.getValue(_interfaceID, "BANK_CODE");
                if (InterfaceUtil.isNullString(bankCode)) {
                        _log.error("setInterfaceParameters", "Value of ORGA_METHOD is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "ORGA_METHOD is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("BANK_CODE", bankCode.trim());

                
                String msisdnPrefix = FileCache.getValue(_interfaceID, "MSISDN_PREFIX");
                if (!InterfaceUtil.isNullString(msisdnPrefix)) {
                        _requestMap.put("MSISDN_PREFIX", msisdnPrefix.trim());
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
     * 
     * 
     * @throws BTSLBaseException
     */
    private HashMap<String,String> sendPaymentRequest(HashMap<String, String> p_requestMap) throws BTSLBaseException {
        final String METHOD_NAME="sendPaymentRequest";
        if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME, "Entered p_requestMap = " + p_requestMap);

        _requestMap = p_requestMap;
        HashMap <String,String >responseMap = new HashMap<String, String>();
                 ProPagoPostpagoFijaResponseProPagoPostpagoFijaResult paymentResponse =null;
        ProPagoPostpagoFija_orgaResponseProPagoPostpagoFija_orgaResult paymentResponseOrga =null;
                AutogestionWsLocator service=null;
                AutogestionWsSoap12Stub stub=null;
        org.apache.axis.message.MessageElement [] messageElement=null;
        long startTime = 0;
        long endTime = 0;
        System.out.println("######"+URLDecoder.decode(p_requestMap.get("INVOICE_NUMBER")));
        String arr[] = URLDecoder.decode(p_requestMap.get("INVOICE_NUMBER")).split("[|]");
        SimpleDateFormat formatter1 = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        int n = arr.length;
        if(arr.length>1){
        
                for(int count =0 ;count<(n-1);count++){
                        Date date1;
                        Date date2;
                        String swap="";
        try {
                                for (int d = 0 ; d < n - count - 1; d++)
                                {
                                        if(arr[d].split("[:]")[3].contains("-")){
                                                formatter1 = new SimpleDateFormat("yyyy-MM-dd");
                                                }
                                                else
                                                {
                                                formatter1 =  new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
                                                }
        	
                                        date1 = formatter1.parse(arr[d].split("[:]")[3]);
                                        date2 = formatter1.parse(arr[d+1].split("[:]")[3]);
                                        if (date1.compareTo(date2)>0) /* For decreasing order use < */
                                        {
                                                swap = arr[d];
                                                arr[d] = arr[d+1];
                                                arr[d+1] = swap;
                                        }
                                }
                        }
                        catch(ParseException pe){
                                _log.errorTrace(METHOD_NAME, pe);
                        }
                }
                for(int v=0;v<n;v++)
                        System.out.println(arr[v]);
         //   input.setOriginTrxnId(Integer.valueOf(String.valueOf(_requestMap.get("NEW_TRANSACTION_ID"))));
        }
            
        _requestMap.put("FACTURA",arr[0].split("[:]")[0]);
            
        try {
            try {
            	 URL url = new URL(_requestMap.get("END_POINT"));
                service = new AutogestionWsLocator();
                stub= new AutogestionWsSoap12Stub(url,service);

                 _log.error("sendPaymentRequest: ", "sendPaymentRequest : Got Stub");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stub == null) {
                _log.error("sendPaymentRequest: ", "Remote exception from interface.Connection not Established properly.");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            } else {
                startTime = System.currentTimeMillis(); // Start Time of
                                                        // Request.
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                        String subscriberNumber = _requestMap.get("MSISDN");
                        if(!InterfaceUtil.isNullString(_requestMap.get("MSISDN_PREFIX")))
                                subscriberNumber = _requestMap.get("MSISDN_PREFIX") + subscriberNumber;
                String sec_bank = getBankSequence(_requestMap.get("TRANSACTION_ID"));
                        if ("3".equals(_requestMap.get("CARD_GROUP_SELECTOR"))){
                                if(!InterfaceUtil.isNullString(_requestMap.get("CONTRACT_FLAG")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("CONTRACT_FLAG"))){
                                        if(!InterfaceUtil.isNullString(_requestMap.get("ORGA_METHOD")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("ORGA_METHOD")))
                                                paymentResponseOrga=stub.proPagoPostpagoFija_orga(_requestMap.get("COUNTRY_CODE"),_requestMap.get("BANK_CODE"),"",_requestMap.get("MSISDN"),_requestMap.get("FACTURA"),_requestMap.get("AMOUNT"),sec_bank,"03");
                                        else
                                                paymentResponse=stub.proPagoPostpagoFija(_requestMap.get("COUNTRY_CODE"),_requestMap.get("BANK_CODE"),"",_requestMap.get("MSISDN"),_requestMap.get("FACTURA"),_requestMap.get("AMOUNT"),sec_bank,"03");
                                }
                                else{
                                        if(!InterfaceUtil.isNullString(_requestMap.get("ORGA_METHOD")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("ORGA_METHOD")))
                                                paymentResponseOrga=stub.proPagoPostpagoFija_orga(_requestMap.get("COUNTRY_CODE"),_requestMap.get("BANK_CODE"),_requestMap.get("MSISDN"),"",_requestMap.get("FACTURA"),_requestMap.get("AMOUNT"),sec_bank,"03");
                                        else
                                                paymentResponse=stub.proPagoPostpagoFija(_requestMap.get("COUNTRY_CODE"),_requestMap.get("BANK_CODE"),_requestMap.get("MSISDN"),"",_requestMap.get("FACTURA"),_requestMap.get("AMOUNT"),sec_bank,"03");
                                }
                        }
                        else{
                                if(!InterfaceUtil.isNullString(_requestMap.get("CONTRACT_FLAG")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("CONTRACT_FLAG"))){
                                        if(!InterfaceUtil.isNullString(_requestMap.get("ORGA_METHOD")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("ORGA_METHOD")))
                                                paymentResponseOrga=stub.proPagoPostpagoFija_orga(_requestMap.get("COUNTRY_CODE"),_requestMap.get("BANK_CODE"),"",_requestMap.get("MSISDN"),_requestMap.get("FACTURA"),_requestMap.get("AMOUNT"),sec_bank,"02");
                                        else
                                                paymentResponse=stub.proPagoPostpagoFija(_requestMap.get("COUNTRY_CODE"),_requestMap.get("BANK_CODE"),"",_requestMap.get("MSISDN"),_requestMap.get("FACTURA"),_requestMap.get("AMOUNT"),sec_bank,"02");
                                }
                                else{
                                        if(!InterfaceUtil.isNullString(_requestMap.get("ORGA_METHOD")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("ORGA_METHOD")))
                                                paymentResponseOrga=stub.proPagoPostpagoFija_orga(_requestMap.get("COUNTRY_CODE"),_requestMap.get("BANK_CODE"),_requestMap.get("MSISDN"),"",_requestMap.get("FACTURA"),_requestMap.get("AMOUNT"),sec_bank,"02");
                                        else
                                                paymentResponse=stub.proPagoPostpagoFija(_requestMap.get("COUNTRY_CODE"),_requestMap.get("BANK_CODE"),_requestMap.get("MSISDN"),"",_requestMap.get("FACTURA"),_requestMap.get("AMOUNT"),sec_bank,"02");
                                }
                        }
                        if(!InterfaceUtil.isNullString(_requestMap.get("ORGA_METHOD")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("ORGA_METHOD"))){
                if (_log.isDebugEnabled())
                                        _log.debug("sendPaymentRequest", "enquiryResponse=" + paymentResponseOrga);
                                if (paymentResponseOrga == null) {
                                        responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                                        _log.error("sendEnquiryRequest: ", "Response Object is not coming from WSDL.");
                                        throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            }
                                endTime = System.currentTimeMillis();// End Time of Request.
                                _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                                try {
                                        messageElement = paymentResponseOrga.get_any();
                                        responseMap.put("PARAM1",messageElement[0].toString());
                                        responseMap.put("PARAM2",messageElement[1].toString());
                                        parsePaymentResponse(responseMap);
                                        if(CAClaroDTHPaymentI.SUCCESS.equalsIgnoreCase(responseMap.get("IN_TXN_STATUS")))
                                                responseMap.put("STATUS_CODE", CAClaroDTHI.SUCCESS);
                                        else
                                        {
                                                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                                                _log.error("sendEnquiryRequest: ", "Response Object is not coming from WSDL.");
                                                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                                        }
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                        else
                        {
                                if (_log.isDebugEnabled())
                                        _log.debug("sendPaymentRequest", "enquiryResponse=" + paymentResponse);
            if (paymentResponse == null) {
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                                        _log.error("sendEnquiryRequest: ", "Response Object is not coming from WSDL.");
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            }
            endTime = System.currentTimeMillis();// End Time of Request.
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            
            
            try {
                                        messageElement = paymentResponse.get_any();
                                        responseMap.put("PARAM1",messageElement[0].toString());
                                        responseMap.put("PARAM2",messageElement[1].toString());
                                        parsePaymentResponse(responseMap);
                                        if(CAClaroDTHPaymentI.SUCCESS.equalsIgnoreCase(responseMap.get("IN_TXN_STATUS")))
                                                responseMap.put("STATUS_CODE", CAClaroDTHI.SUCCESS);
                                        else
                                        {
                                                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                                                _log.error("sendEnquiryRequest: ", "Response Object is not coming from WSDL.");
                                                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                                        }
            } catch (Exception e) {
                e.printStackTrace();
            }
                        }

                }
            String resultCode = String.valueOf(responseMap.get("STATUS_CODE"));
            if (CAClaroDTHI.SUCCESS.equals(resultCode)) {
                _log.error("sendPaymentRequest: ", "Response is succesfull from the IN : : " + resultCode);
 
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
            } else {
                _log.error("sendPaymentRequest: ", "FAIL response from interface. ResponseCode=" + _responseMap.get("RESULT_CODE"));
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHPaymentHandler[sendPaymentRequest] Credit", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Remote Exception occured while getting the connection Object.");
            _log.error("sendPaymentRequest", "Remote Exception occured while getting the connection Object.");
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
        } finally {
            stub.clearAttachments();
            stub.clearHeaders();
            stub=null;
            if (_log.isDebugEnabled())
                _log.debug("sendPaymentRequest", "Exited responseMap: " + responseMap);
        }
        return responseMap;
    }
private HashMap<String,String> sendDTHEnquiryRequest(HashMap<String, String> p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendPaymentRequest", "Entered p_requestMap = " + p_requestMap);
        _requestMap = p_requestMap;
        HashMap <String,String >responseMap = new HashMap<String, String>();
        ConSinPagarFacturasResponseConSinPagarFacturasResult enquiryResponse =null;
    ConSinPagarFacturas_orgaResponseConSinPagarFacturas_orgaResult enquiryResponseOrga =null;
        AutogestionWsLocator service=null;
        AutogestionWsSoap12Stub stub=null;
        org.apache.axis.message.MessageElement [] messageElement=null;
        long startTime = 0;
        long endTime = 0;
        try {
                try {
                 URL url = new URL(_requestMap.get("END_POINT"));
                 service = new AutogestionWsLocator();
                 stub= new AutogestionWsSoap12Stub(url,service);
                 _log.error("sendPaymentRequest: ", "sendPaymentRequest : Got Stub");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stub == null) {
                _log.error("sendPaymentRequest: ", "Remote exception from interface.Connection not Established properly.");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            } else {
                startTime = System.currentTimeMillis(); // Start Time of
                 _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                String subscriberNumber = _requestMap.get("MSISDN");
                if(!InterfaceUtil.isNullString(_requestMap.get("MSISDN_PREFIX")))
                        subscriberNumber = _requestMap.get("MSISDN_PREFIX") + subscriberNumber;
                if ("3".equals(_requestMap.get("CARD_GROUP_SELECTOR"))){
                        if(!InterfaceUtil.isNullString(_requestMap.get("CONTRACT_FLAG")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("CONTRACT_FLAG"))){
                                if(!InterfaceUtil.isNullString(_requestMap.get("ORGA_METHOD")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("ORGA_METHOD")))
                                        enquiryResponseOrga=stub.conSinPagarFacturas_orga(_requestMap.get("COUNTRY_CODE"),"",subscriberNumber,"03");
                                else
                                        enquiryResponse=stub.conSinPagarFacturas(_requestMap.get("COUNTRY_CODE"),"",subscriberNumber,"03");
                        }
                        else{
                                if(!InterfaceUtil.isNullString(_requestMap.get("ORGA_METHOD")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("ORGA_METHOD")))
                                        enquiryResponseOrga=stub.conSinPagarFacturas_orga(_requestMap.get("COUNTRY_CODE"),subscriberNumber,"","03");
                        else
                                        enquiryResponse=stub.conSinPagarFacturas(_requestMap.get("COUNTRY_CODE"),subscriberNumber,"","03");
                        }
                }
                else{
                        if(!InterfaceUtil.isNullString(_requestMap.get("CONTRACT_FLAG")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("CONTRACT_FLAG"))){
                                if(!InterfaceUtil.isNullString(_requestMap.get("ORGA_METHOD")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("ORGA_METHOD")))
                                        enquiryResponseOrga=stub.conSinPagarFacturas_orga(_requestMap.get("COUNTRY_CODE"),"",subscriberNumber,"02");
                                else
                                        enquiryResponse=stub.conSinPagarFacturas(_requestMap.get("COUNTRY_CODE"),"",subscriberNumber,"02");
                        }
                        else{
                                if(!InterfaceUtil.isNullString(_requestMap.get("ORGA_METHOD")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("ORGA_METHOD")))
                                        enquiryResponseOrga=stub.conSinPagarFacturas_orga(_requestMap.get("COUNTRY_CODE"),subscriberNumber,"","02");
                                else
                                        enquiryResponse=stub.conSinPagarFacturas(_requestMap.get("COUNTRY_CODE"),subscriberNumber,"","02");
                        }
                }
                if(!InterfaceUtil.isNullString(_requestMap.get("ORGA_METHOD")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("ORGA_METHOD"))){
                if (_log.isDebugEnabled())
                                _log.debug("sendPaymentRequest", "enquiryResponse=" + enquiryResponseOrga);
                        if (enquiryResponseOrga == null) {
                                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                                _log.error("sendEnquiryRequest: ", "Response Object is not coming from WSDL.");
                                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            }
                        endTime = System.currentTimeMillis();// End Time of Request.
                        _requestMap.put("IN_END_TIME", String.valueOf(endTime));
                        try {
                                messageElement=enquiryResponseOrga.get_any();
                                responseMap.put("PARAM1",messageElement[0].toString());
                                responseMap.put("PARAM2",messageElement[1].toString());
                                responseMap.put("STATUS_CODE", CAClaroDTHI.SUCCESS);
                                parseEnquiryResponse(responseMap);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
                else
                {
                        if (_log.isDebugEnabled())
                                _log.debug("sendPaymentRequest", "enquiryResponse=" + enquiryResponse);
            if (enquiryResponse == null) {
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                _log.error("sendEnquiryRequest: ", "Response Object is not coming from WSDL.");
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            }
            endTime = System.currentTimeMillis();
 _requestMap.put("IN_END_TIME", String.valueOf(endTime));
try {
                messageElement=enquiryResponse.get_any();
                responseMap.put("PARAM1",messageElement[0].toString());
                responseMap.put("PARAM2",messageElement[1].toString());
                responseMap.put("STATUS_CODE", CAClaroDTHI.SUCCESS);
                                parseEnquiryResponse(responseMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
                }
        }
            String resultCode = String.valueOf(responseMap.get("STATUS_CODE"));
            if (CAClaroDTHI.SUCCESS.equals(resultCode)) {
                _log.error("sendPaymentRequest: ", "Response is succesfull from the IN : : " + resultCode);
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
            } else {
                _log.error("sendPaymentRequest: ", "FAIL response from interface. ResponseCode=" + _responseMap.get("RESULT_CODE"));
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
         } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[sendPaymentRequest] Credit", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Remote Exception occured while getting the connection Object.");
            _log.error("sendPaymentRequest", "Remote Exception occured while getting the connection Object.");
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
        } finally {
            stub.clearAttachments();
            stub.clearHeaders();
            stub=null;
            if (_log.isDebugEnabled())
                _log.debug("sendPaymentRequest", "Exited responseMap: " + responseMap);
        }
        return responseMap;
    }

    protected String getINTransactionID(HashMap<String,String> p_map)
    {
        if(_log.isDebugEnabled())_log.debug("getINTransactionID","Entered");

        String inReconID=null;
        String inTxnId=p_map.get("TRANSACTION_ID");
        inTxnId=inTxnId.substring(1,inTxnId.length());
        inTxnId=inTxnId.replace(".","");
        inReconID=inTxnId;

        //Put the transaction id into the map.
        p_map.put("IN_RECON_ID",inReconID);

        if(_log.isDebugEnabled())
            _log.debug("getINTransactionID","Exiting with IN_RECON_ID ="+inReconID+"and IN_TXN_ID=" +inTxnId);
        return inReconID;
    } 
    protected void parseEnquiryResponse(HashMap _respMap){
        final String METHOD_NAME="parseEnquiryResponse";
        if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Entered");
         int indexStart = 0;
         int indexEnd = 0;
         int tempIndex = 0;
        String factureListForCedit="";
         String p_responseStr = String.valueOf(_respMap.get("PARAM2"));
         if ("3".equals(_requestMap.get("CARD_GROUP_SELECTOR"))){
                 String factura="";
                 String vencimiento="";
                 String saldo="";
                 String monto="";
                 String emision="";
                          String tempString =  "";
                 String tempResponse = "";
                 factureListForCedit="";
                 ArrayList ind=new ArrayList();
                 int count=0;
                 int index = p_responseStr.indexOf("<FACTURA>");
                 while(index >= 0) {
                         ind.add(index);
                         index = p_responseStr.indexOf("<FACTURA>", index+1);
                         count++;
                 }
                 for(int i=0;i<ind.size();i++){
                         indexStart = (int)ind.get(i);
                         indexEnd = p_responseStr.indexOf("</SALDO>", indexStart) + +"</SALDO>".length();
                         tempString = tempString+p_responseStr.substring(indexStart, indexEnd)+":";
                         tempResponse = p_responseStr.substring(indexStart, indexEnd);
                 indexStart = tempResponse.indexOf("<FACTURA>") + "<FACTURA>".length();
                 indexEnd = tempResponse.indexOf("</FACTURA>", indexStart);
                 factura = tempResponse.substring(indexStart, indexEnd);
                 indexStart = tempResponse.indexOf("<VENCIMIENTO>") + "<VENCIMIENTO>".length();
                 indexEnd = tempResponse.indexOf("</VENCIMIENTO>", indexStart);
                 vencimiento = tempResponse.substring(indexStart, indexEnd);
                 indexStart = tempResponse.indexOf("<SALDO>") + "<SALDO>".length();
                 indexEnd = tempResponse.indexOf("</SALDO>", indexStart);
                 saldo = tempResponse.substring(indexStart, indexEnd);
                 indexStart = tempResponse.indexOf("<EMISION>") + "<EMISION>".length();
                 indexEnd = tempResponse.indexOf("</EMISION>", indexStart);
                 emision = tempResponse.substring(indexStart, indexEnd);
                 indexStart = tempResponse.indexOf("<MONTO>") + "<MONTO>".length();
                 indexEnd = tempResponse.indexOf("</MONTO>", indexStart);
                 monto = tempResponse.substring(indexStart, indexEnd);
                 factureListForCedit = factureListForCedit+factura+":"+saldo+":"+monto+":"+emision.split("[ ]")[0]+":"+vencimiento+"|";
                 _requestMap.put("INVOICE_SIZE", String.valueOf(count));
                 _requestMap.put("TOTAL_PENDING_BALANCE", "Pending Balance");
                 _requestMap.put("SERVICE_NAME", tempString);
                 _requestMap.put("SERVICE_CODE", "TEST_Code");
                 _requestMap.put("SERVICE_NAME_"+i, "SERVICE_NAME");
                 _requestMap.put("SERVICE_CODE_"+i, "SERVICE_CODE");
                 _requestMap.put("INVOICE_NUM_"+i, factura);
                 _requestMap.put("PERIOD_PENDING_BALANCE_"+i, saldo);
                 _requestMap.put("MIN_PENDING_BALANCE_"+i, monto);
                 _requestMap.put("INVOICED_PENDING_BALANCE_"+i, "INVOICED_PENDING_BALANCE" );
                 _requestMap.put("BILL_PERIOD_START_"+i, emision);
                 _requestMap.put("BILL_PERIOD_END_"+i, vencimiento);
                _requestMap.put("INVOICE_NUMBER",factureListForCedit);
                 }
                 tempString = tempString.substring(0,tempString.length()-1);
         }
         else{
                 String factura="";
                 String vencimiento="";
                 String saldo="";
                 String monto="";
                 String emision="";
                 String tempResponse = "";
                 String tempString =  "";
                factureListForCedit="";
                 ArrayList ind=new ArrayList();
                int count=0;
                 int index = p_responseStr.indexOf("<NUMFACTURA>");
                 while(index >= 0) {
                         ind.add(index);
                     index = p_responseStr.indexOf("<NUMFACTURA>", index+1);
                        count++;
                 }
                 for(int i=0;i<ind.size();i++){
                        indexStart = (int)ind.get(i);
                        indexEnd = p_responseStr.indexOf("</FECHA_VENCIMIENTO>", indexStart) + +"</FECHA_VENCIMIENTO>".length();
                 tempString = tempString+p_responseStr.substring(indexStart, indexEnd)+":";
                         tempResponse = p_responseStr.substring(indexStart, indexEnd);
                         indexStart = tempResponse.indexOf("<NUMFACTURA>") + "<NUMFACTURA>".length();
                 indexEnd = tempResponse.indexOf("</NUMFACTURA>", indexStart);
                 factura = tempResponse.substring(indexStart, indexEnd);
                 indexStart = tempResponse.indexOf("<FECHA_VENCIMIENTO>") + "<FECHA_VENCIMIENTO>".length();
                 indexEnd = tempResponse.indexOf("</FECHA_VENCIMIENTO>", indexStart);
                 vencimiento = tempResponse.substring(indexStart, indexEnd);
                 indexStart = tempResponse.indexOf("<SALDO>") + "<SALDO>".length();
                 indexEnd = tempResponse.indexOf("</SALDO>", indexStart);
                 saldo = tempResponse.substring(indexStart, indexEnd);
                 indexStart = tempResponse.indexOf("<FECHA_EMISION>") + "<FECHA_EMISION>".length();
                 indexEnd = tempResponse.indexOf("</FECHA_EMISION>", indexStart);
                 emision = tempResponse.substring(indexStart, indexEnd);
                 indexStart = tempResponse.indexOf("<MONTO>") + "<MONTO>".length();
                 indexEnd = tempResponse.indexOf("</MONTO>", indexStart);
                 monto = tempResponse.substring(indexStart, indexEnd);
                 factureListForCedit = factureListForCedit+factura+":"+saldo+":"+monto+":"+emision.split("[ ]")[0]+":"+vencimiento+"|";
_requestMap.put("INVOICE_SIZE", String.valueOf(count));
_requestMap.put("TOTAL_PENDING_BALANCE", "Pending Balance");
_requestMap.put("SERVICE_NAME", tempString);
         _requestMap.put("SERVICE_CODE", "TEST_Code");
                 _requestMap.put("SERVICE_NAME_"+i, "SERVICE_NAME");
                 _requestMap.put("SERVICE_CODE_"+i, "SERVICE_CODE");
                 _requestMap.put("INVOICE_NUM_"+i, factura);
                 _requestMap.put("PERIOD_PENDING_BALANCE_"+i, saldo);
                 _requestMap.put("MIN_PENDING_BALANCE_"+i, monto);
                 _requestMap.put("INVOICED_PENDING_BALANCE_"+i, "INVOICED_PENDING_BALANCE" );
                 _requestMap.put("BILL_PERIOD_START_"+i, emision);
                 _requestMap.put("BILL_PERIOD_END_"+i, vencimiento);
                _requestMap.put("INVOICE_NUMBER",factureListForCedit );
                 }
                 tempString = tempString.substring(0,tempString.length()-1);
         }
    }
    protected void parsePaymentResponse(HashMap _respMap){
        final String METHOD_NAME="parsePaymentResponse";
        if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Entered");
        int indexStart = 0;
        int indexEnd = 0;
        String p_responseStr = String.valueOf(_respMap.get("PARAM2"));
        indexStart = p_responseStr.indexOf("<TRANSACCION>") + "<TRANSACCION>".length();
        indexEnd = p_responseStr.indexOf("</TRANSACCION>", indexStart);
        String txnIN = p_responseStr.substring(indexStart, indexEnd);
        _respMap.put("IN_TXN_ID", txnIN);
        indexStart = p_responseStr.indexOf("<RESPUESTA>") + "<RESPUESTA>".length();
        indexEnd = p_responseStr.indexOf("</RESPUESTA>", indexStart);
        String txnStatus = p_responseStr.substring(indexStart, indexEnd);
        _respMap.put("IN_TXN_STATUS", txnStatus);
    }
    protected String getBankSequence(String txn_id){
        final String METHOD_NAME = "getBankSequence";
        if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Entered with TXN ID : "+txn_id);
        String bank_seq = txn_id.substring(1,7)+txn_id.substring(8,12)+txn_id.substring(13,19);
        if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Exiting with BANK_SEQ : "+bank_seq);
        return bank_seq;
    }
}
