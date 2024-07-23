package com.inter.claroca.dth.enquiry;



/**
 * @(#)CAClaroDTHEnquiryHandler.java
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
 *                                        the DTH Enquiry for Central America Claro.
 *                                        
 *                                        
 *                                        */

import java.net.URL;
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
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.logging.TransactionLog;


public class CAClaroDTHEnquiryHandler implements InterfaceHandler {
    private Log _log = LogFactory.getLog(CAClaroDTHEnquiryHandler.class.getName());
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
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            String validateRequired = FileCache.getValue(_interfaceID, _requestMap.get("REQ_SERVICE") + "_" + _requestMap.get("USER_TYPE"));
            if ("N".equals(validateRequired)) {
                _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                return;
            }
            setInterfaceParameters(CAClaroDTHEnquiryI.ACTION_DTH_ENQUIRY, _requestMap);
            
            sendRequestToIN(CAClaroDTHEnquiryI.ACTION_DTH_ENQUIRY, _requestMap);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
            
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
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
    public void credit(HashMap <String,String> p_requestMap) throws BTSLBaseException, Exception {}// end of credit

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
            if (p_action == CAClaroDTHEnquiryI.ACTION_DTH_ENQUIRY) {
                _responseMap = sendDTHEnquiryRequest(_requestMap);
                
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[sendRequestToIN]", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE") + " Action = " + p_action, "System Exception=" + e.getMessage());
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
            switch (p_action) {

            case CAClaroDTHEnquiryI.ACTION_DTH_ENQUIRY: 
                                                               
                             

                String endPoint = FileCache.getValue(_interfaceID, "END_POINT");
                if (InterfaceUtil.isNullString(endPoint)) {
                    _log.error("setInterfaceParameters", "Value of END_POINT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "END_POINT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("END_POINT", endPoint.trim());

                String userName = FileCache.getValue(_interfaceID, "USERNAME");
                if (InterfaceUtil.isNullString(userName)) {
                    _log.error("setInterfaceParameters", "Value of USERNAME is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "USERNAME is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("USERNAME", userName.trim());

                String password = FileCache.getValue(_interfaceID, "PASSWORD");
                if (InterfaceUtil.isNullString(password)) {
                    _log.error("setInterfaceParameters", "Value of PASSWORD is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "PASSWORD is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("PASSWORD", password.trim());

                String timeout = FileCache.getValue(_interfaceID, "TIME_OUT");
                if (InterfaceUtil.isNullString(timeout)) {
                    _log.error("setInterfaceParameters", "Value of TIME_OUT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TIME_OUT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("TIME_OUT", timeout.trim());
                
                String tipoProduct = FileCache.getValue(_interfaceID, "TIPO_PRODUCT");
                if (InterfaceUtil.isNullString(tipoProduct)) {
                    _log.error("setInterfaceParameters", "Value of TIPO_PRODUCT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroDTHEnquiryHandler[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TIPO_PRODUCT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("TIPO_PRODUCT", timeout.trim());

                

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
     * 
     * 
     * @throws BTSLBaseException
     */
    private HashMap<String,String> sendDTHEnquiryRequest(HashMap<String, String> p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("sendPaymentRequest", "Entered p_requestMap = " + p_requestMap);

        _requestMap = p_requestMap;
        HashMap <String,String >responseMap = new HashMap<String, String>();
        AutogestionWsLocator service=null;
        AutogestionWsSoap12Stub stub= null;
        ConSinPagarFacturasResponseConSinPagarFacturasResult output =null;
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
                                                        // Request.
                _requestMap.put("IN_START_TIME", String.valueOf(startTime));
                output=stub.conSinPagarFacturas(_requestMap.get("MSISDN"), _requestMap.get("MSISDN"), _requestMap.get("MSISDN"), _requestMap.get("TIPO_PRODUCT"));
                if (_log.isDebugEnabled())
                    _log.debug("sendPaymentRequest", "output=" + output);
            }
            if (output == null) {
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                _log.error("sendPaymentRequest: ", "Response Object is not coming from WSDL.");
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            }
            endTime = System.currentTimeMillis();// End Time of Request.
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            
            
            try {
            	messageElement=output.get_any();
            	
                responseMap.put("PARAM1",messageElement[0].toString());
                responseMap.put("PARAM2",messageElement[1].toString());
                responseMap.put("STATUS_CODE", CAClaroDTHEnquiryI.SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String resultCode = String.valueOf(responseMap.get("STATUS_CODE"));
            if (CAClaroDTHEnquiryI.SUCCESS.equals(resultCode)) {
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
}


