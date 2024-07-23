/**
 * Copyright(c) 2012, Comviva Technologies Ltd.
 * All Rights Reserved
 * 
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Gopal 20/04/2012 Initial Creation
 */
package com.inter.claro.vas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import com.inter.claro.vas.stub.EjecutarTrama_PortType;
import com.inter.claro.vas.stub.Input_Paramaters;
import com.inter.claro.vas.stub.WS_Result;
/**
 * @(#)VASClaroINHandler
 *                 Copyright(c) 2016, Comviva Technologies Ltd.
 * 				   All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Pankaj Sharma Spt 28,2016 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 */
public class VASClaroINHandler implements InterfaceHandler {
    private static Log log = LogFactory.getLog(VASClaroINHandler.class.getName());
    private static VASRequestResponseParser vasRequestResponseParser = null; // Used
                                                                              // to
                                                                              // generate
                                                                              // reqeusts
    private HashMap requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap _responseMap = null;// Contains the response of the request
                                        // as key and value pair.
    private String interfaceID = null;// Contains the interfaceID
    private String msisdn = null;// Used to store the MSISDN
    private String referenceID = null;// Used to store the reference of
                                       // transaction id.
    private String inTXNID = null;// Used to represent the Transaction ID
    private String productCode = null;// Product code to be sent to INH
    private String userType=null;
    private static  SimpleDateFormat _sdf = new SimpleDateFormat ("yyMMddHHmm");
	private static int _transactionIDCounter=0;
	private static int _prevMinut=0;
	
    static {
    	final String staticBlock="VASClaroINHandler[static]";
        if (log.isDebugEnabled())
            log.debug(staticBlock, "Entered");
        try {

            vasRequestResponseParser = new VASRequestResponseParser();
        } catch (Exception e) {
            log.errorTrace("While instantiation of VASClaroINHandler get Exception e:: " + staticBlock,  e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, staticBlock, "", "", "", "While instantiation of VASResponseParser get Exception e::" + e.getMessage());
        } finally {
            if (log.isDebugEnabled())
                log.debug(staticBlock, "Exited");
        }
    }

    /**
     * Implements the logic that validate the subscriber and get the subscriber
     * information
     * from the IN.
     * 
     * @param HashMap prequestMap
     * @throws BTSLBaseException
     */
    @Override
    public void validate(HashMap prequestMap) throws BTSLBaseException {
        if (log.isDebugEnabled())
            log.debug("validate", "Entered prequestMap:" + prequestMap);
        requestMap = prequestMap;
        requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        requestMap.put("ACCOUNT_STATUS", "Active");
        return;
    }// end of validate
    
    
    /**
     * Implements the logic that Credit the subscriber and get the subscriber
     * information
     * from the IN.
     * 
     * @param HashMap prequestMap
     * @throws Exception
     */
    @Override
    public void credit(HashMap prequestMap) throws Exception {
    	final String methodName = "credit";
    	final String qualifiedmethodName = "VASTHINHandler[credit]";
    	final String msisdnText="MSISDN = ";
    	
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered prequestMap: " + prequestMap);
        double systemAmtDouble = 0;
        double multFactorDouble = 0;
        String amountStr = null;
        requestMap = prequestMap;
        try {
            interfaceID = (String) requestMap.get("INTERFACE_ID");

            inTXNID = getINTransactionID(requestMap);
            requestMap.put("IN_RECON_ID", inTXNID);
            requestMap.put("IN_TXN_ID", inTXNID);

            referenceID = (String) requestMap.get("TRANSACTION_ID");
            msisdn = (String) requestMap.get("MSISDN");
            userType = (String) requestMap.get("USER_TYPE");
            productCode = (String) requestMap.get("CARD_GROUP_SELECTOR");
            
            String packetCode = FileCache.getValue(interfaceID, (String) requestMap.get("REQ_SERVICE")+"_"+productCode);
           
            
            if (InterfaceUtil.isNullString(packetCode)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), (String) requestMap.get("REQ_SERVICE")+"_"+productCode+" is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("PACKET_CODE", packetCode.trim());
      
            
            // Fetching the MULT_FACTOR from the INFile.
            // While sending the amount to IN, it would be multiplied by this
            // factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(interfaceID, "MULT_FACTOR");
            if (log.isDebugEnabled())
                log.debug(methodName, "multFactor:" + multFactor);
            if (InterfaceUtil.isNullString(multFactor)) {
                log.error(methodName, "MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, qualifiedmethodName, referenceID, msisdn + " INTERFACE ID = " + interfaceID, (String) requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            requestMap.put("MULT_FACTOR", multFactor);

            // Set the interface parameters into requestMap
            setInterfaceParameters();

            try {
                multFactorDouble = Double.parseDouble(multFactor);
                double interfaceAmtDouble = Double.parseDouble((String) requestMap.get("INTERFACE_AMOUNT"));
                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble, multFactorDouble);
                amountStr = String.valueOf(systemAmtDouble);
                // Based on the INFiles ROUND_FLAG flag, we have to decide to
                // round the transfer amount or not.
                String roundFlag = FileCache.getValue(interfaceID, "ROUND_FLAG");
                if (log.isDebugEnabled())
                    log.debug(methodName, "From file cache roundFlag = " + roundFlag);
                // If the ROUND_FLAG is not defined in the INFile
                if (InterfaceUtil.isNullString(roundFlag)) {
                    roundFlag = "Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, qualifiedmethodName, referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code = " + (String) requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                // If rounding of amount is allowed, round the amount value and
                // put this value in request map.
                if ("Y".equals(roundFlag.trim())) {
                    amountStr = String.valueOf(Math.round(systemAmtDouble));
                    requestMap.put("INTERFACE_ROUND_AMOUNT", amountStr);
                }
            } catch (Exception e) {
            	log.errorTrace("Exception in   method ::"+methodName,e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, qualifiedmethodName, "REFERENCE ID = " + referenceID, msisdn + " INTERFACE ID = " + interfaceID, (String) requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            if (log.isDebugEnabled())
                log.debug(methodName, "Transfer_amount:" + amountStr);
            // set transfer_amount in request map as amountStr (which is round
            // value of INTERFACE_AMOUNT)
            requestMap.put("TRANSFER_AMOUNT", amountStr);

            // sending the Re-charge request to IN along with re-charge action
            // defined in VAS interface
            sendRequestToIN(VASClaroI.ACTION_RECHARGE_CREDIT);
            // set TRANSACTION_STATUS as Success in request map
            requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);

            requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            requestMap.put("IN_RECHARGE_TIME", String.valueOf(((Long.valueOf((String) requestMap.get("IN_END_TIME"))).longValue()) - ((Long.valueOf((String) requestMap.get("IN_START_TIME"))).longValue())));

        } catch (BTSLBaseException be) {

            log.error(methodName, "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
        	log.errorTrace("Exception in method ::"+methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, qualifiedmethodName, referenceID, msisdn, (String) requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited requestMap=" + requestMap);
        }

    }
    @Override
    public void creditAdjust(HashMap prequestMap) throws  Exception {
    	//convention
    }
    @Override
    public void debitAdjust(HashMap prequestMap) throws Exception {
    	//convention
    }
    @Override
    public void validityAdjust(HashMap prequestMap) throws  Exception {
    	//convention
    }

    /**
     * This method is used to set the parameters from the IN file.
     * 
     * @return void
     * @throws Exception
     */
    private void setInterfaceParameters() throws Exception {
    	final String msisdnText="MSISDN = ";
    	final String methodName="setInterfaceParameters";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered");
        try {

            String warnTimeStr = FileCache.getValue(interfaceID, "WARN_TIMEOUT");
            if (InterfaceUtil.isNullString(warnTimeStr) || !InterfaceUtil.isNumeric(warnTimeStr)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "VASTHINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("WARN_TIMEOUT", warnTimeStr.trim());

            String url = FileCache.getValue(interfaceID, "VASTRIX_URL");
            if (InterfaceUtil.isNullString(url)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASTHINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), "VASTRIX_URL is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("VASTRIX_URL", url.trim());

            String connectTimeout = FileCache.getValue(interfaceID, "CONNECT_TIME_OUT");
            if (InterfaceUtil.isNullString(connectTimeout)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), "CONNECT_TIME_OUT is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("CONNECT_TIME_OUT", connectTimeout.trim());
            
            
            String username = FileCache.getValue(interfaceID, "USERNAME_1");
            if (InterfaceUtil.isNullString(username)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), "USERNAME_1 is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("USERNAME_1", username.trim());
            
            
            String password = FileCache.getValue(interfaceID, "PASSWORD_1");
            if (InterfaceUtil.isNullString(password)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), "PASSWORD_1 is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("PASSWORD_1", password.trim());
            
            String method = FileCache.getValue(interfaceID, "METHOD");
            if (InterfaceUtil.isNullString(method)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), "METHOD is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("METHOD", method.trim());
            
      
            String comment = FileCache.getValue(interfaceID, "COMMENT");
            if (InterfaceUtil.isNullString(comment)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), "COMMENT is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("COMMENT", comment.trim());
      

            String serialNo = (String) requestMap.get("IN_TXN_ID");
            requestMap.put("SERIAL_NO", serialNo.trim());
      
            
            String packetCode = FileCache.getValue(interfaceID, (String) requestMap.get("REQ_SERVICE")+"_"+(String) requestMap.get("CARD_GROUP_SELECTOR"));
            if (InterfaceUtil.isNullString(packetCode)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), (String) requestMap.get("REQ_SERVICE")+"_"+(String) requestMap.get("CARD_GROUP_SELECTOR")+" is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("PACKET_CODE", packetCode.trim());
      
            String collectionCode = FileCache.getValue(interfaceID, "COLLECTION_CODE");
            if (InterfaceUtil.isNullString(collectionCode)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), "COLLECTION_CODE is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("COLLECTION_CODE", collectionCode.trim());
      
            String separator = FileCache.getValue(interfaceID, "SEPARATOR");
            if (InterfaceUtil.isNullString(separator)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), "SEPARATOR is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("SEPARATOR", separator.trim());
     
            String hostID = FileCache.getValue(interfaceID, "HOST_ID");
            if (InterfaceUtil.isNullString(hostID)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "VASINHandler[setInterfaceParameters]", "REFERENCE ID = " + referenceID + msisdnText + msisdn, " INTERFACE ID = " + interfaceID, "Network code " + (String) requestMap.get("NETWORK_CODE"), "HOST_ID is not defined in IN File ");
                throw new BTSLBaseException(this,methodName, InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("HOST_ID", hostID.trim());
     
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            log.error(methodName, "Exception e=" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited requestMap:" + requestMap);
        }// end of finally
    }
    
    /**
     * This method used to send the request to the IN
     * 
     * @param int pAction
     * @throws BTSLBaseException
     */
    private void sendRequestToIN(int pAction) throws BTSLBaseException {
    	final String methodName="sendRequestToIN";
    	final String qualifiedmethodName = "VASClaroINHandler[sendRequestToIN]";
    	final String msisdnText="MSISDN = ";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered  pAction::" + pAction);
        TransactionLog.log(inTXNID, referenceID, msisdn, (String) requestMap.get("NETWORK_CODE"), String.valueOf(pAction), PretupsI.TXN_LOG_REQTYPE_REQ, "Request string::", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action::" + pAction + "product code" + productCode);
        long startTime = 0;
        long endTime = 0;
        long warnTime = 0;
        EjecutarTrama_PortType vasClientStub = null;
        VASClaroConnector vasServiceConnection = null;
        WS_Result responseObject = null;
        Input_Paramaters inputParamaters = null;
        try {
                
            TransactionLog.log(interfaceID, referenceID, msisdn, (String) requestMap.get("NETWORK_CODE"), qualifiedmethodName, PretupsI.TXN_LOG_REQTYPE_REQ,   PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");
            vasServiceConnection = new VASClaroConnector( interfaceID);
            vasClientStub = vasServiceConnection.getService();
            
            if (vasClientStub == null) {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, qualifiedmethodName, "REFERENCE ID = " + referenceID + " MSISDN = " + msisdn, "INTERFACE ID = " + interfaceID, "Network code = " + (String) requestMap.get("NETWORK_CODE"), "Unable to get Client Object");
                log.error(methodName, "Unable to get Client Object");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            try {
                try {
                    startTime = System.currentTimeMillis();
                    requestMap.put("IN_START_TIME", String.valueOf(startTime));
                    switch (pAction) {
                       
                    	case VASClaroI.ACTION_RECHARGE_CREDIT: {

                    		inputParamaters = vasRequestResponseParser.generateVASRequest(VASClaroI.ACTION_RECHARGE_CREDIT, requestMap);
                            responseObject = vasClientStub.ejecutarTrama(inputParamaters);
                            if (responseObject == null) {
                                requestMap.put("RECHARGE_INTERFACE_STATUS", "ERR_RESP");
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                            }
                            break;
                         
                    	}
                    }
                } 
                catch (java.rmi.RemoteException re) 
                {
                	log.errorTrace("Exception in method ::"+methodName,re);
	                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, qualifiedmethodName, "REFERENCE ID = " + referenceID + msisdnText + msisdn, "INTERFACE ID = " + interfaceID, "Network code = " + (String) requestMap.get("NETWORK_CODE"), "RemoteException Error Message:" + re.getMessage());
	                String respCode = null;
	                // parse error code
	                String requestStr = re.getMessage();
	                int index = requestStr.indexOf("<ErrorCode>");
	                if (index == -1) 
	                {
	                	if (re.getMessage().contains("java.net.ConnectException")) {
                        // In case of connection failure
                        // 1.Decrement the connection counter
                        // 2.set the Node as blocked
                        // 3.set the blocked time
                        // 4.Handle the event with level INFO, show
                        // the message that Node is blocked for some
                        // time (expiry time).
                        // Check if the max retry attempt is reached
                        // raise exception with error code.
                        log.error(methodName, "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
                        EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, qualifiedmethodName, referenceID, msisdn, (String) requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection for VAS Soap Stub with INTERFACE_ID=[" + interfaceID + "]");
                        } 
	                	else if (re.getMessage().contains("java.net.SocketTimeoutException")) 
                        {
	                		log.errorTrace("Exception in method ::"+methodName,re);
                            if (re.getMessage().contains("connect")) {
                                    // In case of connection failure
                                    // 1.Decrement the connection counter
                                    // 2.set the Node as blocked
                                    // 3.set the blocked time
                                    // 4.Handle the event with level INFO,
                                    // show the message that Node is blocked
                                    // for some time (expiry time).
                                    // Check if the max retry attempt is
                                    // reached raise exception with error
                                    // code.
                                    log.error(methodName, "RMI java.net.ConnectException while creating connection re::" + re.getMessage());
                                    EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, qualifiedmethodName, referenceID, msisdn, (String) requestMap.get("NETWORK_CODE"), "RMI java.net.ConnectException while getting the connection for VAS Soap Stub with INTERFACE_ID=[" + interfaceID + "]");
                                }
                                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, qualifiedmethodName, "REFERENCE ID = " + referenceID + msisdnText + msisdn, "INTERFACE ID = " + interfaceID, "Network code = " + (String) requestMap.get("NETWORK_CODE"), "RMI java.net.SocketTimeoutException Message:" + re.getMessage());
                                log.error(methodName, "RMI java.net.SocketTimeoutException Error Message :" + re.getMessage());
                                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                            } 
	                		else if (re.getMessage().contains("java.net.SocketException")) {
	                			log.errorTrace("Exception in method ::"+methodName,re);
                                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, qualifiedmethodName, "REFERENCE ID = " + referenceID + msisdnText + msisdn, "INTERFACE ID = " + interfaceID, "Network code = " + (String) requestMap.get("NETWORK_CODE"), "RMI java.net.SocketException Message:" + re.getMessage());
                                log.error(methodName, "RMI java.net.SocketException Error Message :" + re.getMessage());
                                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                            } 
	                		else
                               throw new Exception(re);
                    }
                    respCode = requestStr.substring(index + "<ErrorCode>".length(), requestStr.indexOf("</ErrorCode>", index));

                    index = requestStr.indexOf("<ErrorDescription>");
                    String respCodeDesc = requestStr.substring(index + "<ErrorDescription>".length(), requestStr.indexOf("</ErrorDescription>", index));
                    log.error(methodName, "Error Message respCode= " + respCode + " respCodeDesc:" + respCodeDesc);

                    if (pAction == VASClaroI.ACTION_RECHARGE_CREDIT) {

                        requestMap.put("INTERFACE_STATUS", respCode);
                        requestMap.put("INTERFACE_DESC", respCodeDesc);
                    }

                    if (respCode.equals(VASClaroI.MSISDN_NOT_FOUND)) {
                        log.error(methodName, "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    }else  if (respCode.equals(VASClaroI.LIMIT_REACHED_IN)) {
                        log.error(methodName, "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED,new String[]{ (String)_responseMap.get("MSISDN")});
                    }  
                    else {
                        log.error(methodName, "Error Message respCode=" + respCode + "  respCodeDesc:" + respCodeDesc);
                        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                    }
            	}
                catch (Exception e) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, qualifiedmethodName, "REFERENCE ID = " + referenceID + msisdnText + msisdn, "INTERFACE ID = " + interfaceID, "Network code = " + (String) requestMap.get("NETWORK_CODE"), "Exception Error Message:" + e.getMessage());
                    log.errorTrace("Exception in method ::"+methodName,e);
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }

                endTime = System.currentTimeMillis();
                        
                } catch (BTSLBaseException be) {
                    throw be;
                } catch (Exception e) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, qualifiedmethodName, "REFERENCE ID = " + referenceID + msisdnText + msisdn, "INTERFACE ID = " + interfaceID, "Network code = " + (String) requestMap.get("NETWORK_CODE"), "Error Message:" + e.getMessage());
                    log.errorTrace("Exception in method ::"+methodName,e);
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                } finally {
                    if (endTime == 0)
                        endTime = System.currentTimeMillis();
                    requestMap.put("IN_END_TIME", String.valueOf(endTime));
                    log.error(methodName, "Request sent to IN at:" + startTime + " Response received from IN at:" + endTime);
                }
            _responseMap = vasRequestResponseParser.parseResponseObject(pAction, responseObject);
            // put value of response
            TransactionLog.log(interfaceID, referenceID, msisdn, (String) requestMap.get("NETWORK_CODE"), String.valueOf(pAction), PretupsI.TXN_LOG_REQTYPE_RES, "Response Map: " + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");
            // Difference of start and end time would be compared against the
            // warn time, if request and response takes more time than that of
            // the warn time, an event with level INFO is handled
            if (endTime - startTime >= warnTime) {
                log.info(methodName, "WARN time reaches startTime= " + startTime + " endTime= " + endTime + " warnTime= " + warnTime + " time taken= " + (endTime - startTime));
            }
            
            String responseCode = (String) _responseMap.get("RESP_CODE");
            requestMap.put("INTERFACE_STATUS", responseCode);
           
            
            Object[] successList = VASClaroI.RESULT_OK.split(",");
            if (!Arrays.asList(successList).contains(responseCode)) {
                requestMap.put("VAS_ERROR_MSG_REQD", PretupsI.YES);
                if (VASClaroI.VAS_INCORRECT_CODE_2.equals(responseCode)) {
                    log.error(methodName, "Invalid response  found with MSISDN::" + msisdn);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, qualifiedmethodName, referenceID, msisdn, (String) requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + interfaceID + " Stage = " + pAction, "Invalid Response Code from WAP IN :" + responseCode);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }else  if (responseCode.equals(VASClaroI.LIMIT_REACHED_IN)) {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, qualifiedmethodName, referenceID, msisdn, (String) requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + interfaceID + " Stage = " + pAction, "Invalid Response Code from WAP IN :" + responseCode);
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED,new String[]{ (String)_responseMap.get("MSISDN")});
                }  else {
                    log.error(methodName, "Invalid response code from WAP IN::" + responseCode);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, qualifiedmethodName, referenceID, msisdn, (String) requestMap.get("NETWORK_CODE") + " INTERFACE ID = " + interfaceID + " Stage = " + pAction, "Invalid Response Code from WAP IN :" + responseCode);
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }// end of checking the subscriber existance.
            }
            
        } catch (BTSLBaseException be) {
            log.error(methodName, "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
        	log.errorTrace("Exception in method ::"+methodName,e);
            log.error(methodName, "Exception=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, qualifiedmethodName, "REFERENCE ID = " + referenceID + msisdnText + msisdn, "INTERFACE ID = " + interfaceID, "Network code = " + (String) requestMap.get("NETWORK_CODE") + " Action = " + pAction, "System Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            vasClientStub = null;
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting pAction=" + pAction);
        }// end of finally
    }

    /**
     * This method is used to get the transactionID from request map
     * 
     * @param HashMap prequestMap
     * @return String.
     * @throws BTSLBaseException
     */
    protected static String getINTransactionID(HashMap prequestMap) throws BTSLBaseException {/*
        if (log.isDebugEnabled())
            log.debug("getINTransactionID", "Entered");
        String userType = (String) prequestMap.get("USER_TYPE");
        String inTxnId = (String) prequestMap.get("TRANSACTION_ID");

        if (!InterfaceUtil.isNullString(userType))
            inTxnId = inTxnId + userType;

        prequestMap.put("IN_RECON_ID", inTxnId);
        prequestMap.put("IN_TXN_ID", inTxnId);
        if (log.isDebugEnabled())
            log.debug("getINTransactionID", "exited");
        return inTxnId;
    */

		String METHOD_NAME="getINTransactionID";
    	String instanceID=null;
		int MAX_COUNTER=9999;
		int inTxnLength=4;
		String serviceType=null;
		String userType=null;
		Date mydate =null;
		String minut2Compare=null;
		String dateStr=null;
		String transactionId=null;
		try
		{
			if("PRC".equals(serviceType) || "PCR".equals(serviceType) || "ACCINFO".equals(serviceType) || "C2SENQ".equals(serviceType) || PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER.equals(serviceType))
				serviceType="6";
			else 
				serviceType="7";
				
			userType = (String)prequestMap.get("USER_TYPE");
			if("S".equals(userType))
				userType="3";
			else if("R".equals(userType))	
				userType="2";
			else 
				userType="1";
			instanceID = FileCache.getValue((String)prequestMap.get("INTERFACE_ID"),"INSTANCE_ID");
			if(InterfaceUtil.isNullString(instanceID))
			{
				log.error(METHOD_NAME,"Parameter INSTANCE_ID is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VASClaroINHandler[validate]","","" , (String) prequestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			mydate = new Date();	
			dateStr = _sdf.format(mydate);
			minut2Compare = dateStr.substring(8,10);
			int currentMinut=Integer.parseInt(minut2Compare);  
			if(currentMinut !=_prevMinut)
			{
				_transactionIDCounter=1;
				_prevMinut=currentMinut;
			}
			else if(_transactionIDCounter > MAX_COUNTER)
				_transactionIDCounter=1;
			else
				_transactionIDCounter++;
			String txnid =String.valueOf(_transactionIDCounter);
			int length = txnid.length();
			int tmpLength=inTxnLength-length;
			if(length<inTxnLength)
			{
				for(int i=0;i<tmpLength;i++)
					txnid = "0"+txnid;
			}
			String inPrefix = FileCache.getValue((String)prequestMap.get("INTERFACE_ID"),"VAS_IN_TXN_ID_PREFIX");
			if(InterfaceUtil.isNullString(inPrefix))
			{
				log.error(METHOD_NAME,"Parameter CS5_IN_TXN_ID_PREFIX is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VASClaroINHandler[validate]","","" , (String) prequestMap.get("NETWORK_CODE"), "[CS5_IN_TXN_ID_PREFIX] is not defined in IN File");
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
		
			//transactionId = serviceType+ dateStr+instanceID+txnid+userType;
			transactionId = inPrefix+ dateStr+instanceID+txnid;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{			
		}
		return transactionId;		
		
    
    }

}
