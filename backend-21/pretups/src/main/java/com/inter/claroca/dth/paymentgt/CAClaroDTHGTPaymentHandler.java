package com.inter.claroca.dth.paymentgt;
import java.net.URL;
import java.util.Random;
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
public class CAClaroDTHGTPaymentHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(CAClaroDTHGTPaymentHandler.class.getName());
 private HashMap <String,String>_requestMap = null;// Contains the request parameter as key
	private HashMap <String,String> _responseMap = null;// Contains the response of the request
		private String _interfaceID = null;// Contains the interfaceID
private String _inTXNID = null;// Used to represent the Transaction ID
private String _msisdn = null;// Used to store the MSISDN
private String _referenceID = null;// Used to store the reference of
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
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting with  _requestMap: " + _requestMap);
        }
    }

 public void credit(HashMap <String,String> p_requestMap) throws BTSLBaseException, Exception {
         final String METHOD_NAME="credit";
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PAYMENT_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            paymentMultiplicationFactor = paymentMultiplicationFactor.trim();
            _requestMap.put("PAYMENT_MULT_FACTOR", paymentMultiplicationFactor);
            orangeMultFactorDouble = Double.parseDouble(paymentMultiplicationFactor);
            amount = (String) _requestMap.get("REQUESTED_AMOUNT"); 
                                                                   

            amount = InterfaceUtil.getSystemAmountFromINAmount(amount, orangeMultFactorDouble);
            _requestMap.put("AMOUNT", amount);
            setInterfaceParameters(CAClaroDTHGTPaymentI.ACTION_PAYMENT, _requestMap);
            _requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
            sendRequestToIN(CAClaroDTHGTPaymentI.ACTION_PAYMENT, _requestMap);
 _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
           _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
        } catch (BTSLBaseException be) {
            p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error(METHOD_NAME,"BTSLBaseException be:"+be.getMessage());
            if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try
            {
                    _requestMap.put("TRANSACTION_TYPE","CR");
                    handleCancelTransaction();
            }
            catch(BTSLBaseException bte)
            {
                    throw bte;
            }
            catch(Exception e)
            {
                    _log.error(METHOD_NAME,e);
                    _log.error(METHOD_NAME,"Exception e:"+e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[credit]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                    throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
           
                    throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
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

   public void sendRequestToIN(int p_action, HashMap<String, String> p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendRequestToIN", " p_action=" + p_action);

        _requestMap = p_requestMap;

        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "_requestMap: " + _requestMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + p_action);
        try {
            if (p_action == CAClaroDTHGTPaymentI.ACTION_PAYMENT) {
            	_responseMap = sendPaymentRequestXMLOverHttp(_requestMap);
                if (_log.isDebugEnabled())
                    _log.debug("sendRequestToIN : ACTION_PAYMENT ", "Received Response Map =" + _responseMap);
                TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "ACTION_PAYMENT=" + p_action);
            }
        } catch (BTSLBaseException be) {
            _log.error("sendRequestToIN", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }
        catch (Exception e) {
            e.printStackTrace();
            _log.error("sendRequestToIN", "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "System Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally {
            if (_log.isDebugEnabled())
                _log.debug("sendRequestToIN", "Exited _responseMap: " + _responseMap);

        }
}
public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }
    public void setInterfaceParameters(int p_action, HashMap <String,String>p_requestMap) throws BTSLBaseException, Exception {
        final String METHOD_NAME="setInterfaceParameters";
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_action = " + p_action, "Entered p_action = " + p_requestMap);
        try {
            
            _requestMap = p_requestMap;
            
                String endPoint = FileCache.getValue(_interfaceID, "END_POINT");
                if (InterfaceUtil.isNullString(endPoint)) {
                    _log.error("setInterfaceParameters", "Value of END_POINT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "END_POINT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("END_POINT", endPoint.trim());

                String userName = FileCache.getValue(_interfaceID, "USERNAME");
                if (InterfaceUtil.isNullString(userName)) {
                    _log.error("setInterfaceParameters", "Value of USERNAME is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "USERNAME is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("USERNAME", userName.trim());

                String password = FileCache.getValue(_interfaceID, "PASSWORD");
                if (InterfaceUtil.isNullString(password)) {
                    _log.error("setInterfaceParameters", "Value of PASSWORD is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "PASSWORD is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("PASSWORD", password.trim());

                String timeout = FileCache.getValue(_interfaceID, "TIME_OUT");
                if (InterfaceUtil.isNullString(timeout)) {
                    _log.error("setInterfaceParameters", "Value of TIME_OUT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TIME_OUT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("TIME_OUT", timeout.trim());
                
                String tipoProduct = FileCache.getValue(_interfaceID, "TIPO_PRODUCT");
                if (InterfaceUtil.isNullString(tipoProduct)) {
                    _log.error("setInterfaceParameters", "Value of TIPO_PRODUCT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TIPO_PRODUCT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("TIPO_PRODUCT", tipoProduct.trim());

               	String countryCode = FileCache.getValue(_interfaceID, "COUNTRY_CODE");
                if (InterfaceUtil.isNullString(countryCode)) {
                    _log.error("setInterfaceParameters", "Value of COUNTRY_CODE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "COUNTRY_CODE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("COUNTRY_CODE",countryCode.trim());
		String msisdnPadding = FileCache.getValue(_interfaceID, "MSISDN_PADDING_LEN");
                if (InterfaceUtil.isNullString(msisdnPadding)) {
                    _log.error("setInterfaceParameters", "Value of MSISDN_PADDING_LEN is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "MSISDN_PADDING_LEN is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("MSISDN_PADDING_LEN",msisdnPadding.trim());
                String cancelTxnAllowed = FileCache.getValue(_interfaceID,"CANCEL_TXN_ALLOWED");
                if(InterfaceUtil.isNullString(cancelTxnAllowed))
                {
                        _log.error(METHOD_NAME,"Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5CaINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                        throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());

                String systemStatusMappingCr = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
                if(InterfaceUtil.isNullString(systemStatusMappingCr))
                {
                        _log.error(METHOD_NAME,"Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS5CaINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                        throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());
        }
catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("setInterfaceParameters", "Exception e = " + e.getMessage());
            throw e;
        }
        finally {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap" + _requestMap);
        }
}
    private HashMap<String,String> sendPaymentRequest(HashMap<String, String> p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendPaymentRequest", "Entered p_requestMap = " + p_requestMap);

        _requestMap = p_requestMap;
        HashMap <String,String >responseMap = new HashMap<String, String>();
		PaymentsPOS_bindQSServiceLocator service=null;
		PaymentsPOS_bindStub stub=null;
        PaymentRequest input = null;
		PaymentResponse output =null;
        long startTime = 0;
        long endTime = 0;
        

        try {
        	
        	input= new PaymentRequest();
		input.setCountryCode(_requestMap.get("COUNTRY_CODE"));
		//input.setPhoneNumber(InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")));
		input.setPhoneNumber(InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"),(String)(padZeroesToLeft((String) p_requestMap.get("MSISDN"),Integer.valueOf(String.valueOf(_requestMap.get("MSISDN_PADDING_LEN")))))));
	//	input.setTipo_producto("0"+_requestMap.get("CARD_GROUP_SELECTOR"));
		input.setTipo_producto(_requestMap.get("TIPO_PRODUCT"));
	        //input.setOriginTrxnId(Integer.valueOf((String.valueOf(_requestMap.get("NEW_TRANSACTION_ID"))).substring(11)));
	        input.setOriginTrxnId(Integer.valueOf(String.valueOf(_requestMap.get("NEW_TRANSACTION_ID"))));
	 //       input.setOriginTrxnId(123456);
                input.setAmount(Integer.valueOf(String.valueOf(_requestMap.get("AMOUNT"))));
		input.setDistId(_requestMap.get("USERNAME"));
		input.setDistPass(_requestMap.get("PASSWORD"));
            
            
            try {
            	 URL url = new URL(_requestMap.get("END_POINT"));
            	 service = new PaymentsPOS_bindQSServiceLocator();
                 stub= new PaymentsPOS_bindStub(url,service);

                 _log.error("sendPaymentRequest: ", "sendPaymentRequest : Got Stub");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stub == null) {
                _log.error("sendPaymentRequest: ", "Remote exception from interface.Connection not Established properly.");
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            } else {
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                output=stub.paymentsPOS(input);
                if (_log.isDebugEnabled())
                    _log.debug("sendPaymentRequest", "output=" + output);
            }
            if (output == null) {
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                _log.error("sendPaymentRequest: ", "Response Object is not coming from WSDL.");
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            }
            endTime = System.currentTimeMillis();_requestMap.put("IN_END_TIME", String.valueOf(endTime));
            
            
            try {

                responseMap.put("TXN_ID_FROMIN",output.getTransactionId());
                responseMap.put("STATUS_CODE", output.getRespCode());
            } catch (Exception e) {
                e.printStackTrace();
            }

            String resultCode = String.valueOf(responseMap.get("STATUS_CODE"));
            if (CAClaroDTHGTPaymentI.SUCCESS.equals(resultCode)) {
                _log.error("sendPaymentRequest: ", "Response is succesfull from the IN : : " + resultCode);
 
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
            } else {
                _log.error("sendPaymentRequest: ", "FAIL response from interface. ResponseCode=" + _responseMap.get("RESULT_CODE"));
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.FAIL);
                throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
            }
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[sendPaymentRequest] Credit", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Remote Exception occured while getting the connection Object.");
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
private HashMap<String,String> sendPaymentRequestXMLOverHttp(HashMap<String, String> p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendPaymentRequest", "Entered p_requestMap = " + p_requestMap);
        _requestMap = p_requestMap;
        HashMap <String,String >responseMap = new HashMap<String, String>();
        long startTime = 0;
        long endTime = 0;
        HttpURLConnection _urlConnection = null;
        String _request=null;
        PrintWriter _out=null;
        String _responseStr=null;
        BufferedReader _in=null;
        try {
            try {
                 URL url = new URL(_requestMap.get("END_POINT"));
                 _urlConnection = (HttpURLConnection) url.openConnection();
                 _urlConnection.setConnectTimeout(10000);
                 _urlConnection.setReadTimeout(10000);
                 _urlConnection.setDoOutput(true);
                 _urlConnection.setDoInput(true);
                 _urlConnection.setRequestMethod("POST");
                 _request=prepareRequest(_requestMap);
                 _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())), true);
                 _out.flush();
                 _out.println(_request);
                 _out.flush();
                 _log.error("sendPaymentRequest: ", "sendPaymentRequest : Got Stub");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try{
                startTime = System.currentTimeMillis();
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                StringBuffer buffer = new StringBuffer();
                String response = "";
                _in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
                while ((response = _in.readLine()) != null) {
                    buffer.append(response);
                }
                _responseStr = buffer.toString();
                if (_log.isDebugEnabled())
                    _log.debug("sendPaymentRequest", "output=" + _responseStr);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            if (_responseStr == null) {
                _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
//                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                _log.error("sendPaymentRequest: ", "Response Object is not coming from WSDL.");
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            endTime = System.currentTimeMillis();_requestMap.put("IN_END_TIME", String.valueOf(endTime));
            try {
               String responseCode =parseResponse(_responseStr);
                responseMap.put("RESPONSE_CODE", responseCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String resultCode = String.valueOf(responseMap.get("RESPONSE_CODE"));
            if (CAClaroDTHGTPaymentI.SUCCESS.equals(resultCode)) {
                _log.error("sendPaymentRequest: ", "Response is succesfull from the IN : : " + resultCode);
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
            } else {
                _log.error("sendPaymentRequest: ", "FAIL response from interface. ResponseCode=" + _responseMap.get("RESPONSE_CODE"));
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.FAIL);
                throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
            }
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHGTPaymentHandler[sendPaymentRequest] Credit", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Remote Exception occured while getting the connection Object.");
            _log.error("sendPaymentRequest", "Remote Exception occured while getting the connection Object.");
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
        } finally {
        	try {
                if (_urlConnection != null)
                	_urlConnection.disconnect();
            } catch (Exception e) {
            }
        	try {
                if (_out != null)
                    _out.close();
            } catch (Exception e) {
            }
            try {
                if (_in != null)
                    _in.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("sendPaymentRequest", "Exited responseMap: " + responseMap);
        }
        return responseMap;
    }
protected String getINTransactionID(HashMap<String,String> p_map)
    {
        if(_log.isDebugEnabled())_log.debug("getINTransactionID","Entered");
	Random r = new Random();
        String inReconID=null;
        String inTxnId=p_map.get("TRANSACTION_ID");
        //inTxnId=inTxnId.substring(1,inTxnId.length());
      //  inTxnId=inTxnId.replace(".","");
        //inReconID=inTxnId;
	Integer temp=(Integer)(r.nextInt(100000000));
	inReconID=String.valueOf(temp);
	p_map.put("IN_RECON_ID",inReconID);

        if(_log.isDebugEnabled())
            _log.debug("getINTransactionID","Exiting with IN_RECON_ID ="+inReconID+"and IN_TXN_ID=" +inTxnId);
        return inReconID;
    }
	protected  String padZeroesToLeft(String p_strValue, int p_strLength) {
	        if (_log.isDebugEnabled())
	            _log.debug("padZeroesToLeft()", "Entered with p_strValue= " + p_strValue + " p_strLength:" + p_strLength);
	        int cntr = p_strLength - p_strValue.length();
	        if (cntr > 0) {
	            for (int i = 0; i < cntr; i++) {
	                p_strValue = "0" + p_strValue;
	            }
	        }
	        if (_log.isDebugEnabled())
	            _log.debug("padZeroesToLeft()", "Exiting with p_strValue= " + p_strValue);
	        return p_strValue;
    } 
	protected String prepareRequest(HashMap<String,String> p_map)
    {
        if(_log.isDebugEnabled())_log.debug("prepareRequest","Entered");
        String requestStr = null;
        StringBuffer stringBuffer = null;
        try
        {
        	stringBuffer = new StringBuffer(1028);
        	stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body><Payment_Rq xmlns=\"http://esb.claro.com.gt/Payment\">");
        	stringBuffer.append("<countryCode>" + p_map.get("COUNTRY_CODE") + "</countryCode>");
        	stringBuffer.append("<originTrxnId>" + Integer.valueOf(String.valueOf(_requestMap.get("NEW_TRANSACTION_ID"))) + "</originTrxnId>");
        	stringBuffer.append("<amount>" + p_map.get("AMOUNT") + "</amount>");
        	stringBuffer.append("<tipo_producto>" + p_map.get("TIPO_PRODUCT") + "</tipo_producto>");
        	stringBuffer.append("<phoneNumber>" + InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"),(String)(padZeroesToLeft((String) p_map.get("MSISDN"),Integer.valueOf(String.valueOf(_requestMap.get("MSISDN_PADDING_LEN")))))) + "</phoneNumber>");
        	stringBuffer.append("<distId>" + p_map.get("USERNAME") + "</distId>");
        	stringBuffer.append("<distPass>" + p_map.get("PASSWORD") + "</distPass>");
        	stringBuffer.append("</Payment_Rq></soapenv:Body></soapenv:Envelope>");
        	requestStr = stringBuffer.toString();
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
        if(_log.isDebugEnabled())
            _log.debug("prepareRequest","Exiting with Request ="+requestStr);
        return requestStr;
    }
	protected  String parseResponse(String str){
        	if(_log.isDebugEnabled())_log.debug("parseResponse","Entered :"+str);
            String finalString="";          
            int indexStart = str.indexOf("<tns:respCode>")+"<tns:respCode>".length();
            int indexEnd = str.indexOf("</tns:respCode>", indexStart);
            finalString=str.substring(indexStart,indexEnd); 
            if(_log.isDebugEnabled())
                _log.debug("prepareRequest","Exiting with finalString ="+finalString);
            return finalString;
    } 
    private void handleCancelTransaction() throws BTSLBaseException
    {
        String METHOD_NAME="handleCancelTransaction";
        if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME, "Entered.");
        String cancelTxnAllowed = null;
        String cancelTxnStatus = null;
        String reconciliationLogStr = null;
        String cancelCommandStatus=null;
        String cancelNA=null;
        String interfaceStatus=null;
        Log reconLog = null;
        String systemStatusMapping=null;
        try
        {
                _requestMap.put("REMARK1",FileCache.getValue(_interfaceID,"REMARK1"));
                _requestMap.put("REMARK2",FileCache.getValue(_interfaceID,"REMARK2"));
                reconLog = ReconcialiationLog.getLogObject(_interfaceID);
                if (_log.isDebugEnabled())_log.debug(METHOD_NAME, "reconLog."+reconLog);
                cancelTxnAllowed=(String)_requestMap.get("CANCEL_TXN_ALLOWED");
                if("N".equals(cancelTxnAllowed))
                {
                        cancelNA=(String)_requestMap.get("CANCEL_NA");//Cancel command status as NA.
                        cancelCommandStatus=InterfaceUtil.getErrorCodeFromMapping(_requestMap,cancelNA,"CANCEL_COMMAND_STATUS_MAPPING");
                        _requestMap.put("MAPPED_CANCEL_STATUS",cancelCommandStatus);
                        interfaceStatus=(String)_requestMap.get("INTERFACE_STATUS");
                        systemStatusMapping=(String)_requestMap.get("SYSTEM_STATUS_MAPPING");
                        cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap,interfaceStatus,systemStatusMapping); //PreTUPs Transaction status as FAIL/AMBIGUOUS based on value of SYSTEM_STATUS_MAPPING
                        _requestMap.put("MAPPED_SYS_STATUS",cancelTxnStatus);
                        reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(_requestMap);
                        reconLog.info("",reconciliationLogStr);
                        if(!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
                                throw new BTSLBaseException(this,METHOD_NAME,cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also. ??????)
                        _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
                }
        }
        catch(BTSLBaseException be)
        {
                throw be;
        }
        catch(Exception e)
        {
                _log.error(METHOD_NAME,e);
                _log.error(METHOD_NAME,"Exception e:"+e.getMessage());
                throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
                if (_log.isDebugEnabled())
                        _log.debug(METHOD_NAME, "Exited");
        }
    }
}
