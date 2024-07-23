package com.inter.claroca.tecnotree;



/**
 * @(#)CAClaroTecnoTree.java
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
 *                                        the credit request for TecnoTree for Central America Claro.
 *                                        
 *                                        
 *                                        */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;

import org.omg.CORBA.ORB;

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
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionDAO;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;

import TINC.PE_AccountDetails;
import TINC.PaymentEngine;
import TINC.PaymentEngine_Factory;
import TINC.PaymentEngine_FactoryHelper;
import TINC.ServiceKeyRec;
import TINC.ServiceKeySeqHolder;
import TINC.UserInfo;


public class CAClaroTecnoTree implements InterfaceHandler {
    private Log _log = LogFactory.getLog(CAClaroTecnoTree.class.getName());
    private HashMap <String,String>_requestMap = null;// Contains the request parameter as key
                                       // and value pair.
    private HashMap <String,String> _responseMap = null;// Contains the response of the request
                                        // as key and value pair.
    private String _interfaceID = null;// Contains the interfaceID
    private String _inTXNID = null;// Used to represent the Transaction ID
    private String _msisdn = null;// Used to store the MSISDN
    private String _referenceID = null;// Used to store the reference of
                                       // transaction id.
    private static final int PAYMENT = 100;
	private static final String VERSION_ID = "$Revision: 1.5 $";
	private static final String PRODUCT_NAME = "Comviva Testing";
	private static String processName = "rechargeClient";
//	The key that will be used to get and release engines from the server
	private static int factoryKey = -1;
//	The engineId id used in releaseing the Engine 
	private static int engineId = -1;
//	The operator id that the rquests will carried out under
//	Note: This will be the operator that can be seen on the CDR
	private static int operatorId = -1;
//	The handle to the Factory
	private static PaymentEngine_Factory paymentFactory = null;
	// The wrapper class the talk to the Engine
	private static PaymentEngine_impl paymentEngine_impl = null;
	private static String User = "";
	private static String Password = "";
	// The default location of the Auth IOR file 
	// this can be changed using the �I  boolean line  boolean r
        private static String AuthIorPath = "/pretupshome/tomcat8_smsr/conf/pretups/INFiles/authServer.ior";

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
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validate", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[validate]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "While validate the subscriber get the Exception e:" + e.getMessage());
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
                final String METHOD_NAME="credit";
        if (_log.isDebugEnabled())
                        _log.debug(METHOD_NAME, "Entered p_requestMap: " + p_requestMap);
        double multFactorDouble = 0;
        _requestMap = p_requestMap;
        String local_ExternalPaymentID = null;
        String amount = null;
                Connection con=null;MComConnectionI mcomCon = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
                _msisdn = (String) _requestMap.get("MSISDN");
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            local_ExternalPaymentID = getINTransactionID(_requestMap);
            _requestMap.put("NEW_TRANSACTION_ID", local_ExternalPaymentID);
            String paymentMultiplicationFactor = FileCache.getValue(_interfaceID, "PAYMENT_MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("credit", "paymentMultiplicationFactor:" + paymentMultiplicationFactor);
            if (InterfaceUtil.isNullString(paymentMultiplicationFactor)) {
                _log.error("credit", "PAYMENT_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[credit]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PAYMENT_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            paymentMultiplicationFactor = paymentMultiplicationFactor.trim();
            _requestMap.put("PAYMENT_MULT_FACTOR", paymentMultiplicationFactor);
            multFactorDouble = Double.parseDouble(paymentMultiplicationFactor);
                        amount = (String) _requestMap.get("INTERFACE_AMOUNT");
                        setInterfaceParameters(CAClaroTecnoTreeI.ACTION_DIRECT_FUND_TRANSFER, _requestMap);
                        _requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
                        _requestMap.put("OTT_CODE",_requestMap.get("TXN_TYPE"));
                        if(!BTSLUtil.isNullString(_requestMap.get("SENDER_MSISDN")) && (String.valueOf(_requestMap.get("OTT_MSISDN_MAPNG")).contains(String.valueOf(_requestMap.get("SENDER_MSISDN")))))
                        {
                                String ottList[] =      _requestMap.get("OTT_MSISDN_MAPNG").split(",");
                                for(int count=0;count<ottList.length;count++){
                                        String tempMSISDN = ottList[count].split(":")[0];
                                        String tempOTTCode = ottList[count].split(":")[1];
                                        if(tempMSISDN.equalsIgnoreCase(_requestMap.get("SENDER_MSISDN").trim())){
                                                _requestMap.put("OTT_CODE", tempOTTCode.trim());
                                                break;
                                        }
                                }
                        }
                        if(!BTSLUtil.isNullString(_requestMap.get("USD_AMNT_APPL")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("USD_AMNT_APPL"))){
                                CurrencyConversionDAO currencyConversionDAO=new CurrencyConversionDAO();
                                mcomCon = new MComConnection();con=mcomCon.getConnection();
                                double currncyMultFactor = currencyConversionDAO.getCovertedAmount(con,_requestMap.get("CURRENCY"), _requestMap.get("NETWORK_CODE"));
                                amount = String.valueOf(InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble(amount), currncyMultFactor));
                        }
            amount = InterfaceUtil.getSystemAmountFromINAmount(amount, multFactorDouble);
            _requestMap.put("AMOUNT", amount);
            // Set the interface parameters into requestMap

            
            sendRequestToIN(CAClaroTecnoTreeI.ACTION_DIRECT_FUND_TRANSFER, _requestMap);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("CAClaroTecnoTree#credit");
				mcomCon = null;
			}
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
    public void debitAdjust(HashMap <String,String>p_requestMap) throws BTSLBaseException, Exception {
    	

        if (_log.isDebugEnabled())
            _log.debug("debitAdjust", "Entered p_requestMap: " + p_requestMap);
        double orangeMultFactorDouble = 0;
        _requestMap = p_requestMap;
        String local_ExternalPaymentID = null;
        String amount = null;
                String interfaceAmount = null;
                String taxAmount = null;
		Connection con=null;MComConnectionI mcomCon = null;
        try {
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            _referenceID = (String) _requestMap.get("TRANSACTION_ID");
            local_ExternalPaymentID = getINTransactionID(_requestMap);
            _requestMap.put("NEW_TRANSACTION_ID", local_ExternalPaymentID);
            String paymentMultiplicationFactor = FileCache.getValue(_interfaceID, "PAYMENT_MULT_FACTOR");
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "paymentMultiplicationFactor:" + paymentMultiplicationFactor);
            if (InterfaceUtil.isNullString(paymentMultiplicationFactor)) {
                _log.error("debitAdjust", "PAYMENT_MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[debitAdjust]", _referenceID, _msisdn + " INTERFACE ID = " + _interfaceID, (String) _requestMap.get("NETWORK_CODE"), "PAYMENT_MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            paymentMultiplicationFactor = paymentMultiplicationFactor.trim();
            _requestMap.put("PAYMENT_MULT_FACTOR", paymentMultiplicationFactor);
            orangeMultFactorDouble = Double.parseDouble(paymentMultiplicationFactor);
                        setInterfaceParameters(CAClaroTecnoTreeI.ACTION_DIRECT_DEBIT_TRANSFER, _requestMap);
                        interfaceAmount = (String) _requestMap.get("INTERFACE_AMOUNT");
                        taxAmount =  (String) _requestMap.get("TAX_AMOUNT");
                        amount = String.valueOf(Integer.parseInt(interfaceAmount) - (Integer.parseInt(_requestMap.get("CG_MULT_FACTOR"))*Integer.parseInt(taxAmount)));
                         _requestMap.put("INTERFACE_AMOUNT",amount);
			if(!BTSLUtil.isNullString(_requestMap.get("USD_AMNT_APPL")) && PretupsI.YES.equalsIgnoreCase(_requestMap.get("USD_AMNT_APPL"))){
				CurrencyConversionDAO currencyConversionDAO=new CurrencyConversionDAO();
				mcomCon = new MComConnection();con=mcomCon.getConnection();
				double currncyMultFactor = currencyConversionDAO.getCovertedAmount(con,_requestMap.get("CURRENCY"), _requestMap.get("NETWORK_CODE"));
				amount = String.valueOf(InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble(amount), currncyMultFactor));
			}
            amount = InterfaceUtil.getSystemAmountFromINAmount(amount, orangeMultFactorDouble);
            _requestMap.put("AMOUNT", amount);
                        _requestMap.put("OTT_CODE",_requestMap.get("OTT_DEBIT"));
                        sendRequestToIN(CAClaroTecnoTreeI.ACTION_DIRECT_DEBIT_TRANSFER, _requestMap);
                        _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // Set the interface parameters into requestMap
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                        if(!BTSLUtil.isNullString(_responseMap.get("INTERFACE_STATUS")) && InterfaceErrorCodesI.SUCCESS.equalsIgnoreCase(_responseMap.get("INTERFACE_STATUS"))){
                        amount = InterfaceUtil.getSystemAmountFromINAmount(taxAmount, orangeMultFactorDouble);
                        _requestMap.put("AMOUNT", amount);
            setInterfaceParameters(CAClaroTecnoTreeI.ACTION_DIRECT_DEBIT_TRANSFER, _requestMap);
                        _requestMap.put("OTT_CODE",_requestMap.get("OTT_DEBIT_COMM"));
            sendRequestToIN(CAClaroTecnoTreeI.ACTION_DIRECT_DEBIT_TRANSFER, _requestMap);
            // set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            // set INTERFACE_POST_BALANCE into request map as obtained thru
            // response map.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
                        }
        } catch (BTSLBaseException be) {
           
           
                throw be;
           
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[debitAdjust]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust get the Exception e:" + e.getMessage());
            throw new BTSLBaseException(this, "credit", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("CAClaroTecnoTree#debitAdjust");
				mcomCon = null;
			}
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }
    
    	
    }// end of debitAdjust

    /**
     * This method is responsible to send the request to IN.
     * 
     * @param String
     *            p_inRequestStr
     * @param int p_action
     * @throws BTSLBaseException
     */


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
                final String METHOD_NAME="setInterfaceParameters";
        if (_log.isDebugEnabled())
            _log.debug("setInterfaceParameters", "Entered p_action = " + p_action, "Entered p_action = " + p_requestMap);
        try {
            Date date = new Date();
            _requestMap = p_requestMap;


                        String noofConnections = FileCache.getValue(_interfaceID, "NUMBER_OF_CONNECTIONS");
                        if (InterfaceUtil.isNullString(noofConnections)) {
                                _log.error("setInterfaceParameters", "Value of NUMBER_OF_CONNECTIONS is not defined in the INFile");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "NUMBER_OF_CONNECTIONS is not defined in the INFile.");
                                throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("NUMBER_OF_CONNECTIONS", noofConnections.trim());

                        for(int i=1;i<=Integer.valueOf(noofConnections);i++)
                        {
                                String endPoint = FileCache.getValue(_interfaceID, "END_POINT_"+i);
                if (InterfaceUtil.isNullString(endPoint)) {
                                        _log.error("setInterfaceParameters", "Value of END_POINT "+i+ " is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "END_POINT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                                _requestMap.put("END_POINT_"+i, endPoint.trim());

                                String userName = FileCache.getValue(_interfaceID, "USERNAME_"+i);
                if (InterfaceUtil.isNullString(userName)) {
                                        _log.error("setInterfaceParameters", "Value of USERNAME "+i+"is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "USERNAME is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                                _requestMap.put("USERNAME_"+i, userName.trim());

                                String password = FileCache.getValue(_interfaceID, "PASSWORD_"+i);
                if (InterfaceUtil.isNullString(password)) {
                                        _log.error("setInterfaceParameters", "Value of PASSWORD "+i+"is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "PASSWORD is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                                _requestMap.put("PASSWORD_"+i, password.trim());
                        }
                String timeout = FileCache.getValue(_interfaceID, "TIME_OUT");
                if (InterfaceUtil.isNullString(timeout)) {
                    _log.error("setInterfaceParameters", "Value of TIME_OUT is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TIME_OUT is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("TIME_OUT", timeout.trim());
                
                String txnType = FileCache.getValue(_interfaceID, "TXN_TYPE");
                if (InterfaceUtil.isNullString(txnType)) {
                    _log.error("setInterfaceParameters", "Value of TXN_TYPE is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]", _referenceID, "INTERFACE ID" + _interfaceID + " MSISDN " + _msisdn, (String) _requestMap.get("NETWORK_CODE"), "TXN_TYPE is not defined in the INFile.");
                    throw new BTSLBaseException(this, "setInterfaceParameters", InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
                _requestMap.put("TXN_TYPE", txnType.trim());
String cancelTxnAllowed = FileCache.getValue(_interfaceID,"CANCEL_TXN_ALLOWED");
                    if(InterfaceUtil.isNullString(cancelTxnAllowed))
                    {
                            _log.error(METHOD_NAME,"Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                            throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());
String systemStatusMappingCr = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
                    if(InterfaceUtil.isNullString(systemStatusMappingCr))
                    {
                            _log.error(METHOD_NAME,"Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                            throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());
                    String usdAmountApplicable=FileCache.getValue(_interfaceID, "USD_AMNT_APPL");
                    if(InterfaceUtil.isNullString(usdAmountApplicable))
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CAClaroTecnoTree[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "EXTERNAL_DATA2 is not defined in IN File ");
                    _requestMap.put("USD_AMNT_APPL",usdAmountApplicable.trim());
                        String currency=FileCache.getValue(_interfaceID, "CURRENCY");
                    if(InterfaceUtil.isNullString(currency))
                    {
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "CURRENCY is not defined in IN File ");
                            throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("CURRENCY",currency.trim());
                  String ottMSISDN=FileCache.getValue(_interfaceID, "OTT_MSISDN_MAPNG");
                    if(InterfaceUtil.isNullString(ottMSISDN))
                    {
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "OTT MSISDN Mapping is not defined in IN File ");
                            throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("OTT_MSISDN_MAPNG",ottMSISDN.trim());
                        String ottDebit=FileCache.getValue(_interfaceID, "OTT_DEBIT");
                    if(InterfaceUtil.isNullString(ottDebit))
                    {
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "OTT_DEBIT is not defined in IN File ");
                            throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("OTT_DEBIT",ottDebit.trim());
                    String ottDebitComm=FileCache.getValue(_interfaceID, "OTT_DEBIT_COMM");
                    if(InterfaceUtil.isNullString(ottDebitComm))
                    {
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "OTT_DEBIT_COMM is not defined in IN File ");
                            throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("OTT_DEBIT_COMM",ottDebitComm.trim());
                String cardGroupMultFactor=FileCache.getValue(_interfaceID, "CG_MULT_FACTOR");
                    if(InterfaceUtil.isNullString(ottDebitComm))
                    {
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "CG_MULT_FACTOR is not defined in IN File ");
                            throw new BTSLBaseException(this,METHOD_NAME,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("CG_MULT_FACTOR",cardGroupMultFactor.trim());

          
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
    private HashMap<String,String> sendRequestToIN(int p_action,HashMap<String, String> p_requestMap) throws BTSLBaseException {
                final String METHOD_NAME="sendRequestToIN";
        if (_log.isDebugEnabled())
                        _log.debug(METHOD_NAME, "Entered p_requestMap = " + p_requestMap);

        _requestMap = p_requestMap;
        _responseMap = new HashMap<String, String>();
        TransactionLog.log(_interfaceID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_REQ, "_requestMap: " + _requestMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "action = " + p_action);
        long startTime = 0;
        long endTime = 0;
        boolean paymentEngineAvailable=false;
        PE_AccountDetails ad= null;

        try {
        	
        	            
            
            try {
                AuthIorPath= (String) p_requestMap.get("END_POINT");
                String [] param={VERSION_ID,(String) p_requestMap.get("USERNAME"),(String) p_requestMap.get("PASSWORD"),AuthIorPath};
                                paymentEngine_impl=CAClaroTecnoTreeSingleton.getInstance(p_requestMap).getPaymentEngineImplementation();
                                System.out.println("###"+p_requestMap.get("BONUS_VALIDITY_DAYS")+p_requestMap.get("VALIDITY_DAYS"));
                                if(paymentEngine_impl!=null)
            	{
            		if(p_action==CAClaroTecnoTreeI.ACTION_DIRECT_FUND_TRANSFER)
            		{
            			ad= new PE_AccountDetails();
                                ad.result=-1;
            			startTime = System.currentTimeMillis();
            			_requestMap.put("IN_START_TIME", String.valueOf(startTime));
                                                ad = paymentEngine_impl.onlineFundTransfer(InterfaceUtil.getFilterMSISDN(_interfaceID,p_requestMap.get("MSISDN")),
            					Integer.parseInt(p_requestMap.get("AMOUNT")),
                                                                Short.parseShort(p_requestMap.get("OTT_CODE")),Short.parseShort(p_requestMap.get("BONUS_VALIDITY_DAYS")));
            			
            		}
            		else if (p_action==CAClaroTecnoTreeI.ACTION_DIRECT_DEBIT_TRANSFER)
            		{
            			ad= new PE_AccountDetails();
                                ad.result=-1;
            			startTime = System.currentTimeMillis();
            			_requestMap.put("IN_START_TIME", String.valueOf(startTime));
                                                ad = paymentEngine_impl.onlineFundTransfer(InterfaceUtil.getFilterMSISDN(_interfaceID,p_requestMap.get("MSISDN")),
                                                                (0-Integer.parseInt(p_requestMap.get("AMOUNT"))),
                                                                Short.parseShort(p_requestMap.get("OTT_CODE")),Short.parseShort("0"));
            			
            			
            		}
            			
            		
            			
            	}
            	else
            	{
            		 _log.error("sendPaymentRequest: ", "Remote exception from interface.Connection not Established properly.");
                     _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                     throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            	}
            		
            	
            		

                
            } 
            
    		
            catch (Exception e) {
                e.printStackTrace();
            }
                        if(ad == null){
                                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CAClaroTecnoTree[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Blank response from CAClaroTecnoTree");
                                _log.error(METHOD_NAME, "NULL response from interface");
                                _log.error(METHOD_NAME,"Read time out occured. Retry attempts exceeded so throwing AMBIGOUS exception");
                                EventHandler.handle(EventIDI.INTERFACE_RESPONSE_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "CAClaroTecnoTree[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,  "NULL response from IN. Retry attempts exceeded so throwing AMBIGOUS exception");
                                _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
                                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
            if (ad.result == -1) {
                _responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
                _log.error("sendPaymentRequest: ", "Response Object is not OK.");
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
            }
            endTime = System.currentTimeMillis();// End Time of Request.
            _requestMap.put("IN_END_TIME", String.valueOf(endTime));
            
            
            try {
                _responseMap.put("STATUS_CODE", String.valueOf(ad.result));
                _responseMap.put("ACCOUNT_BAL",String.valueOf(ad.accountBalance));
                _responseMap.put("ERROR_CODE", String.valueOf(ad.transferResult));
                _responseMap.put("UNCHARGED_BAL", String.valueOf(ad.amountBalance));
                _responseMap.put("SERVICE_STATUS", String.valueOf(ad.serviceStatus));
                _responseMap.put("ACCOUNT_STATUS", String.valueOf(ad.accountStatus));
                _responseMap.put("EXPIRY_DATE", String.valueOf(ad.expiryDate));
                _responseMap.put("PROFILE_ID", String.valueOf(ad.profileId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            TransactionLog.log(_inTXNID, _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), String.valueOf(p_action), PretupsI.TXN_LOG_REQTYPE_RES, "Response string:" + _responseMap, PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "ACTION_PAYMENT=" + p_action);
            String resultCode = String.valueOf(_responseMap.get("STATUS_CODE"));
            if (CAClaroTecnoTreeI.SUCCESS.equals(resultCode)) {
                _log.error("sendPaymentRequest: ", "Response is succesfull from the IN : : " + resultCode);
 
                _responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
            } else
        {
                                 _log.error("sendPaymentRequest: ", "Response from IN :: " + resultCode);
                                if(CAClaroTecnoTreeI.MSISDN_NOT_FOUND.equals(String.valueOf(resultCode)))
            {
                _log.error("sendPaymentRequest: ", "FAIL response from interface. ResponseCode=" + _responseMap.get("STATUS_CODE")+"MSISDN : "+_msisdn+" ErrorCode = " + _responseMap.get("ERROR_CODE") );
                _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
            }
            else
            {
                        _log.error("sendPaymentRequest: ", "FAIL response from interface. ResponseCode=" + _responseMap.get("STATUS_CODE")+"MSISDN : "+_msisdn+" ErrorCode = " + _responseMap.get("ERROR_CODE") );
                        _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            }
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CAClaroTecnoTree[sendPaymentRequest] Credit", "REFERENCE ID = " + _referenceID + "MSISDN = " + _msisdn, "INTERFACE ID = " + _interfaceID, "Network code = " + (String) _requestMap.get("NETWORK_CODE"), "Remote Exception occured while getting the connection Object.");
            _log.error("sendPaymentRequest", "Remote Exception occured while getting the connection Object.");
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CONNECTION_EXCEPTION);
        } finally {
                      paymentEngine_impl=null;
            
            if (_log.isDebugEnabled())
                _log.debug("sendPaymentRequest", "Exited _responseMap: " + _responseMap);
        }
        return _responseMap;
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
    
    
    private static  boolean getPaymentEngine(String[] args,HashMap<String,String> p_requestMap)
	{

		System.out.println("Getting Payment Engine : "+"Path to Auth Server IOR: "+AuthIorPath);
		boolean Continue = false;

		try
		{
			//
			// Step 1. Validate the user with the AuthServer and get an IOR List
			//
			String ior = new String();
			// construct a holder for the inout sequence
			ServiceKeyRec[] emptyServiceKeyRec = new ServiceKeyRec[]{};
			ServiceKeySeqHolder serviceKeySeq = new ServiceKeySeqHolder(emptyServiceKeyRec);

			// create an AuthFactory handle
			
			System.out.println("Getting Payment Engine : "+" Creating AuthFactory_impl... ");
			AuthFactory_impl factory = new AuthFactory_impl(args,AuthIorPath);

			// Talk with the AuthServer via the wrapper class
			
			System.out.println("Getting Payment Engine : "+" Validating User/Password... ");
			UserInfo uf = factory.login((String) p_requestMap.get("USERNAME"),(String) p_requestMap.get("PASSWORD"),serviceKeySeq);
			if(uf.id == -1 || uf.type == -1) {
					System.out.println("Getting Payment Engine : "+" Failed to Login... ");
				Continue = false;
			} else  {
				// The operatorId will be used in the CDR of the recharge
				operatorId = uf.id;

				System.out.println("Getting Payment Engine : "+" Logged in User " +(String) p_requestMap.get("USERNAME") +" Id "   +uf.id +" Type " +uf.type);
				Continue = true;
			}

			// Step 2. Search the serviceKeySeq list for a paymentEngine Factory record
			if (Continue)
			{
				int elements = serviceKeySeq.value.length;
				
				System.out.println("Getting Payment Engine : "+" No of elements in serviceKeySeq : "+elements);
				for (int i=0;i<elements;i++)
				{
					if(serviceKeySeq.value[i].service == PAYMENT)
					{
						// extract the key that�s later used to login to the paymentEngine Factory
						factoryKey = serviceKeySeq.value[i].key;
						// extract the CORBA IOR to allow use to connect to the paymentEngine Factory
						ior = serviceKeySeq.value[i].ior;
						
						System.out.println("Getting Payment Engine : "+" Found PAYMENT with a key : "+factoryKey);
						Continue = true;
						break;
					}
				}
			}

			if(Continue)
			{
				// Step 3. Using the IOR get a handle on the paymentEngine Factory
				ORB orb = ORB.init(args,null);
				org.omg.CORBA.Object paymentFactoryObj = orb.string_to_object(ior) ;

				if(paymentFactoryObj == null)
				{
					
					System.out.println("Getting Payment Engine : "+" paymentFactoryObj Object is not valid ");
					Continue = false;
				}

				// narrow the CORBA object to a PaymentEngine_Factory
				paymentFactory =  PaymentEngine_FactoryHelper.narrow(paymentFactoryObj);

				if(paymentFactory == null)
				{
					
					System.out.println("Getting Payment Engine : "+" paymentFactory Object is not a factory ");
					Continue = false;
				}

				// Step 4. Using the paymentEngine Factory get a handle on the paymentEngine
				//         using the key to login to the Factory.
				PaymentEngine paymentEngine = paymentFactory.getPaymentEngine(factoryKey);

				if  (paymentEngine == null)
				{
				
					System.out.println("Getting Payment Engine : "+" paymentEngine Object is not a factory ");
					Continue = false;
				}
				else
				{

					System.out.println("Getting Payment Engine : "+" Connected to paymentEngine... ");
					// The engineId is used for releasing the paymentEngine later
					engineId = paymentEngine.engineId();

					// Setup a wrapper class with the paymentEngine and the operId
					paymentEngine_impl = new PaymentEngine_impl(paymentEngine,operatorId);
					Continue = true;
				}
			}
		}
		catch (org.omg.CORBA.COMM_FAILURE corbEx)
		{

			System.out.println("Getting Payment Engine : "+" Exception :COMM_FAILURE "+corbEx);
			corbEx.printStackTrace();
			Continue = false ;
		}
		catch (org.omg.CORBA.SystemException  systemEx)
		{

			System.out.println("Getting Payment Engine : "+" Exception :Exception:SystemException "+systemEx);
			systemEx.printStackTrace();
			Continue = false;
		}
		catch(Exception ex)
		{

			System.out.println("Getting Payment Engine : "+" Exception :Exception:SystemException "+ex);
			ex.printStackTrace();
			Continue = false;
		}

		System.out.println("Getting Payment Engine : "+" Exiting "+Continue);
		return Continue;
	}
    
    private static  boolean releasePaymentEngine()
	{

		System.out.println("Payment Engine : "+" Path to Auth Server IOR: "+AuthIorPath);

		boolean Continue = false;

		try
		{
			if(factoryKey >= 0)
			{
//				Trace.(4,"PaymentClient :> Release PaymentEngine["+engineId+"]");
				Continue = paymentFactory.releasePaymentEngine(factoryKey, engineId);
			}
		}
		catch (org.omg.CORBA.COMM_FAILURE corbEx)
		{
//			Trace.(1, �PaymentClient :>Exception :COMM_FAILURE �) ;
//			Trace.Exception(1, corbEx) ;
			Continue = false ;
		}
		catch (org.omg.CORBA.SystemException  systemEx)
		{
//			Trace.(1,"PaymentClient :> Exception:SystemException");
//			Trace.Exception(1, systemEx);
			Continue = false;
		}
		catch(Exception ex)
		{
//			Trace.(1,"PaymentClient :> Exception");
//			Trace.Exception(1, ex);
			Continue = false;
		}

		return Continue;
	}
    
	private static void directFundTransfer(HashMap <String,String> p_requestMap)
	{
//		Trace.(4,"PaymentClient :> options()");

		boolean Continue = false;
		String subIdStr = new String();
		String amountStr = new String();
		String tranTypeStr = new String();

		// declare a holder for the returned data struct
		PE_AccountDetails ad= new PE_AccountDetails();
		ad.result = -1;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try { 

			System.out.println("Sub Id:");
			subIdStr = br.readLine(); 

			System.out.println("Amount :");
			amountStr = br.readLine(); 

			System.out.println("Tranasation Type :");
			tranTypeStr = br.readLine(); 

			Continue = true;

			// Send the request to the paymentEngine for processing
			ad = paymentEngine_impl.directFundTransfer(subIdStr,
					Integer.parseInt(amountStr),
					Short.parseShort(tranTypeStr));
		} 
		catch (IOException ioe) 
		{ 
			System.out.println("IO error trying to read input!");
			Continue = false;
		}
		catch (NumberFormatException nfe) 
		{ 
			System.out.println("Invalid Number");
			Continue = false;
		}

		// The Account Details are only valid when the operation was a success
                System.out.println("****************************");
                System.out.println("");
                System.out.println("Result : "+ad.result);
                System.out.println("TransferResult : "+ad.transferResult);
                if(Continue && (ad.result != -1))
                {
                        System.out.println("Account Balance : "+ad.accountBalance);
                        System.out.println("Service Status : "+ad.serviceStatus);
                        System.out.println("Account Status : "+ad.accountStatus);
                        System.out.println("ExpiryDate : "+ad.expiryDate.day +"/"
                                        + ad.expiryDate.month + "/"
                                        + ad.expiryDate.year + " "
                                        + ad.expiryDate.hour + ":"
                                        + ad.expiryDate.minute + ":"
                                        + ad.expiryDate.second);
                        System.out.println("Profile Date : "+ad.profileId);
                        System.out.println("SubOptions : "+ad.subOptions);
                        System.out.println("IVR Query Expiry Date : "+ad.ivrQueryExpiryDate.day + "/"
                                        + ad.ivrQueryExpiryDate.month + "/"
                                        + ad.ivrQueryExpiryDate.year + " "
                                        + ad.ivrQueryExpiryDate.hour + ":"
                                        + ad.ivrQueryExpiryDate.minute + ":"
                                        + ad.ivrQueryExpiryDate.second);
                        System.out.println("IVR Query Counter  "+ad.ivrQueryCounter);
                }
                System.out.println("");
                System.out.println("****************************");
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
    private static void onlineFundTransfer(HashMap <String,String> p_requestMap)
    {
            boolean Continue = false;
            String subIdStr = new String();
            String amountStr = new String();
            String tranTypeStr = new String();
            PE_AccountDetails ad= new PE_AccountDetails();
            ad.result = -1;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                    System.out.println("Sub Id:");
                    subIdStr = br.readLine();
                    System.out.println("Amount :");
                    amountStr = br.readLine();
                    System.out.println("Tranasation Type :");
                    tranTypeStr = br.readLine();
                    Continue = true;
                    ad = paymentEngine_impl.onlineFundTransfer(subIdStr,
                                    Integer.parseInt(amountStr),
                                    Short.parseShort(tranTypeStr),Short.parseShort(p_requestMap.get("BONUS_VALIDITY_DAYS")));
            }
            catch (IOException ioe)
            {
                    System.out.println("IO error trying to read input!");
                    Continue = false;
            }
            catch (NumberFormatException nfe)
            {
                    System.out.println("Invalid Number");
                    Continue = false;
            }
		System.out.println("****************************");
		System.out.println("");
		System.out.println("Result : "+ad.result);
		System.out.println("TransferResult : "+ad.transferResult);
		if(Continue && (ad.result != -1))
		{
			System.out.println("Account Balance : "+ad.accountBalance);
			System.out.println("Service Status : "+ad.serviceStatus);
			System.out.println("Account Status : "+ad.accountStatus);
			System.out.println("ExpiryDate : "+ad.expiryDate.day +"/"
					+ ad.expiryDate.month + "/"
					+ ad.expiryDate.year + " "
					+ ad.expiryDate.hour + ":"
					+ ad.expiryDate.minute + ":"
					+ ad.expiryDate.second);
			System.out.println("Profile Date : "+ad.profileId);
			System.out.println("SubOptions : "+ad.subOptions);
			System.out.println("IVR Query Expiry Date : "+ad.ivrQueryExpiryDate.day + "/"
					+ ad.ivrQueryExpiryDate.month + "/"
					+ ad.ivrQueryExpiryDate.year + " "
					+ ad.ivrQueryExpiryDate.hour + ":"
					+ ad.ivrQueryExpiryDate.minute + ":"
					+ ad.ivrQueryExpiryDate.second);
			System.out.println("IVR Query Counter  "+ad.ivrQueryCounter);
		}
		System.out.println("");
		System.out.println("****************************");
	}
}


