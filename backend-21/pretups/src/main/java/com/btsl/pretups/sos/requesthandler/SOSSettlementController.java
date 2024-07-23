package com.btsl.pretups.sos.requesthandler;

/**
 * @(#)SOSSettlementController.java
 *                                  Copyright(c) 2010, Comviva technologies Ltd.
 *                                  All Rights Reserved
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Shamit Jain Jan 10,2010 Initial Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  --------
 */
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.CommonClient;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingVO;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.sos.businesslogic.SOSVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.sos.businesslogic.SOSTxnDAO;

public class SOSSettlementController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(SOSSettlementController.class.getName());
    private String _sosMSISDN;
    private String _sosNetworkCode;
    private Date _currentDate = null;
    private String _intModCommunicationTypeS;
    private String _intModIPS;
    private int _intModPortS;
    private String _intModClassNameS;
    private String _intModCommunicationTypeR;
    private String _intModIPR;
    private int _intModPortR;
    private String _intModClassNameR;
    private String _requestIDStr;
    private Locale _locale = null;
    private String _type;
    private String _serviceType;
    private boolean _sosInterfaceInfoInDBFound = false;
    private String _sosAllServiceClassID = PretupsI.ALL;
    private String _sosPostBalanceAvailable;
    private String _sosExternalID = null;

    private boolean _failMessageRequired = false; // Whether Receiver Fail
                                                  // Message is required before
                                                  // validation
    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
    private boolean _useAlternateCategory = false; // Whether to use alternate
                                                   // interface category
    private boolean _performIntfceCatRoutingBeforeVal = false; // Whether we
                                                               // need to
                                                               // perform
                                                               // alternate
                                                               // interface
                                                               // category
                                                               // routing before
                                                               // sending
                                                               // Receiver
                                                               // Validation
                                                               // Request
    private boolean _interfaceCatRoutingDone = false; // To indicate that
                                                      // interface category
                                                      // routing has been done
                                                      // for the process
    private String _oldInterfaceCategory = null; // The initial interface
                                                 // category that has to be used
    private String _newInterfaceCategory = null; // The alternate interface
                                                 // category that has to be used
    private boolean _sosDeletionReqFromSubRouting = false; // Whether to update
                                                           // in Subscriber
                                                           // Routing for sender
                                                           // MSISDN

    private final int SRC_BEFORE_INRESP_CAT_ROUTING = 1; // To denote the
                                                         // process from where
                                                         // interface routing
                                                         // has been called,
                                                         // Before IN Validation
                                                         // of Receiver
    private final int SRC_AFTER_INRESP_CAT_ROUTING = 2; // To denote the process
                                                        // from where interface
                                                        // routing has been
                                                        // called, After IN
                                                        // Validation of
                                                        // Receiver
    private NetworkPrefixVO _networkPrefixVO = null;
    private String _oldDefaultSelector = null;
    private String _newDefaultSelector = null;
    private static OperatorUtilI _operatorUtil = null;
    private String _transactionID;
    private SOSVO _sosvo = null;
    private SOSTxnDAO _sosTxndao = new SOSTxnDAO();
    private static final float EPSILON=0.0000001f;
    public SOSSettlementController() {
        _currentDate = new Date();
        if (BTSLUtil.NullToString(Constants.getProperty("SOS_FAIL_MESSAGE_REQ")).equals("Y")) {
            _failMessageRequired = true;
        }

    }

    // Loads operator specific class
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static block", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
	 * 
	 */
    public void process(RequestVO p_sosVO) {
        Connection con = null;
        MComConnectionI mcomCon = null;
        _requestIDStr = p_sosVO.getRequestIDStr();
        _transactionID = _requestIDStr;
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, _requestIDStr, " Entered");
        }

        try {
            _sosvo = (SOSVO) p_sosVO;
            _sosvo.setTransactionID(_transactionID);
            _sosvo.setCreatedOn(_currentDate);
            _sosvo.setUserID(PretupsI.SYSTEM_USER);
            _locale = _sosvo.getLocale();

            if (_locale == null) {
                _locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, _requestIDStr, "_locale=" + _locale);
            }

            _sosMSISDN = _sosvo.getSubscriberMSISDN();
            TransactionLog.log("", _requestIDStr, _sosMSISDN, _sosvo.getNetworkCode() + " Amount=" + _sosvo.getDebitAmount(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "SOS Settlement request", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            _type = _sosvo.getType();
            _serviceType = _sosvo.getServiceType();
            _sosNetworkCode = _sosvo.getNetworkCode();
            String paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(_sosvo.getServiceType(), _sosvo.getSubscriberType());

            if (paymentMethodType == null) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            }

            _sosvo.setPaymentMethodType(paymentMethodType);

            _sosvo.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_sosMSISDN));
            NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(_sosMSISDN, PretupsI.USER_TYPE_SENDER);
            _sosvo.setPrefixID(networkPrefixVO.getPrefixID());

            // Get the Interface Category routing details based on the receiver
            // Network Code and Service type
            if (_type.equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
                // service_interface_routing table name
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_sosNetworkCode + "_" + _sosvo.getServiceType() + "_" + _sosvo.getSubscriberType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, _requestIDStr, "For =" + _sosNetworkCode + "_" + _sosvo.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO.getAlternateInterfaceType() + " _oldDefaultSelector=" + _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "_newDefaultSelector= " + _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode());
                    }

                    _type = _serviceInterfaceRoutingVO.getInterfaceType();
                    _oldInterfaceCategory = _type;
                    _oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                    _useAlternateCategory = _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    _newInterfaceCategory = _serviceInterfaceRoutingVO.getAlternateInterfaceType();
                    _newDefaultSelector = _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
                } else {
                    _log.info(METHOD_NAME, _requestIDStr, "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SOSSettlementController[process]", "", _sosMSISDN, _sosNetworkCode, "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    _type = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_sosvo.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        _oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                }
            } else {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_sosNetworkCode + "_" + _sosvo.getServiceType() + "_" + _sosvo.getSubscriberType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, _requestIDStr, "For =" + _sosNetworkCode + "_" + _sosvo.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO.getAlternateInterfaceType() + " _oldDefaultSelector=" + _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "_newDefaultSelector= " + _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode());
                    }
                    _oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                } else {
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_sosvo.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        _oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                    _log.info(METHOD_NAME, _requestIDStr, "Service Interface Routing control Not defined, thus using default Selector=" + _oldDefaultSelector);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSSettlementController[process]", "", _sosMSISDN, _sosNetworkCode, "Service Interface Routing control Not defined, thus using default selector=" + _oldDefaultSelector);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, _requestIDStr, "_sosVO:" + _sosvo);
            }

            // populate payment and service interface details for validate
            // action
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION); // shamit
                                                                                                              // need
                                                                                                              // to
                                                                                                              // know
                                                                                                              // the
                                                                                                              // purpose

            if (BTSLUtil.isNullString(_sosvo.getRequestGatewayCode())) {
                _sosvo.setRequestGatewayCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE)));
            }

            processSOSSettlementRequest(con);
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "Exception be:" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            _sosvo.setSuccessTxn(false);
            if (be.isKey()) {
                if (BTSLUtil.isNullString(_sosvo.getErrorCode())) {
                    _sosvo.setErrorCode(be.getMessageKey());
                }
                _sosvo.setMessageCode(be.getMessageKey());
                _sosvo.setMessageArguments(be.getArgs());
            } else {
                _sosvo.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            if (!(_sosvo.getTransactionStatus() != null && _sosvo.getTransactionStatus().length() > 0)) {
                _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            }

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[process]", _transactionID, _sosMSISDN, _sosNetworkCode, "ErrorCode:" + _sosvo.getErrorCode() + " BTSL Base Exception:" + be.getMessage());
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Base Exception " + be.getMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _sosvo.getMessageCode() + "ErrorCode:" + _sosvo.getErrorCode());
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            _sosvo.setSuccessTxn(false);
            if (!(_sosvo.getTransactionStatus() != null && _sosvo.getTransactionStatus().length() > 0)) {
                _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            }
            _sosvo.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            _sosvo.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            _log.error(METHOD_NAME, "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[process]", _transactionID, _sosMSISDN, _sosNetworkCode, "ErrorCode:" + _sosvo.getErrorCode() + " Exception:" + e.getMessage());
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Base Exception " + e.getMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _sosvo.getMessageCode() + "ErrorCode:" + _sosvo.getErrorCode());
        } finally {
            try {
                if (BTSLUtil.isNullString(_sosvo.getMessageCode())) {
                    _sosvo.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                }

                if (mcomCon == null) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                }
                try {
                    updateSOSDetails(con);
                } catch (BTSLBaseException bex) {
                    _log.errorTrace(METHOD_NAME, bex);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[process]", _transactionID, _sosMSISDN, _sosNetworkCode, "Not able to update Subscriber Last Details Exception:" + e.getMessage());
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(METHOD_NAME, bex);
                try {
                    if (con != null) {
                        mcomCon.finalRollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
                _log.error(METHOD_NAME, _transactionID, "BTSL Base Exception while updating transfer details in database:" + bex.getMessage());
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                try {
                    if (con != null) {
                       mcomCon.finalRollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
                _log.error(METHOD_NAME, _transactionID, "Exception while updating transfer details in database:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[process]", _transactionID, _sosMSISDN, _sosNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
            } finally {
                if (con != null) {
                    try {
                        mcomCon.finalCommit();
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                }
				if (mcomCon != null) {
					mcomCon.close("SOSSettlementController#process");
					mcomCon = null;
				}
                con=null;
            }
            if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_sosvo.getSOSReturnMsg() == null || !((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey())) {
                _sosvo.setSOSReturnMsg(new BTSLMessages((PretupsErrorCodesI.SOS_SETTLEMENT_FAIL), new String[] { String.valueOf(_transactionID), PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()) }));
            }

            if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                if (_sosvo.getSOSReturnMsg() == null) {
                    (new PushMessage(_sosMSISDN, getSOSSuccessMessage(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                } else if (_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey()) {
                    BTSLMessages btslRecMessages = (BTSLMessages) _sosvo.getSOSReturnMsg();
                    (new PushMessage(_sosMSISDN, BTSLUtil.getMessage(_locale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                } else {
                    (new PushMessage(_sosMSISDN, (String) _sosvo.getSOSReturnMsg(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                }
            } else if (_failMessageRequired) {
                if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    if (_sosvo.getSOSReturnMsg() == null) {
                        (new PushMessage(_sosMSISDN, getSOSFailMessage(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else if (_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey()) {
                        BTSLMessages btslRecMessages = (BTSLMessages) _sosvo.getSOSReturnMsg();
                        (new PushMessage(_sosMSISDN, BTSLUtil.getMessage(_locale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else {
                        (new PushMessage(_sosMSISDN, (String) _sosvo.getSOSReturnMsg(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    }
                }

                else if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    if (_sosvo.getSOSReturnMsg() == null) {
                        (new PushMessage(_sosMSISDN, getReceiverAmbigousMessage(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else if (_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey()) {
                        BTSLMessages btslRecMessages = (BTSLMessages) _sosvo.getSOSReturnMsg();
                        (new PushMessage(_sosMSISDN, BTSLUtil.getMessage(_locale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else {
                        (new PushMessage(_sosMSISDN, (String) _sosvo.getSOSReturnMsg(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    }
                }
            }
            TransactionLog.log(_transactionID, _sosvo.getRequestIDStr(), _sosvo.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + _sosvo.getMessageCode());
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting");
            }
        }
    }

    public void process(Connection p_con, RequestVO p_sosVO) {
        final String METHOD_NAME = "process";
        // Connection con=null;
        _requestIDStr = p_sosVO.getRequestIDStr();
        if (p_sosVO.getTransactionID() != null) {
            _transactionID = p_sosVO.getTransactionID();
        } else {
            _transactionID = _requestIDStr;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestIDStr, " Entered");
        }
        try {
            _sosvo = (SOSVO) p_sosVO;
            _sosvo.setTransactionID(_transactionID);
            _sosvo.setCreatedOn(_currentDate);
            _sosvo.setUserID(PretupsI.SYSTEM_USER);
            _locale = _sosvo.getLocale();

            if (_locale == null) {
                _locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }

            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestIDStr, "_locale=" + _locale);
            }

            _sosMSISDN = _sosvo.getSubscriberMSISDN();
            TransactionLog.log("", _requestIDStr, _sosMSISDN, _sosvo.getNetworkCode() + " Amount=" + _sosvo.getDebitAmount(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "SOS Settlement request", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            _type = _sosvo.getType();
            _serviceType = _sosvo.getServiceType();
            _sosNetworkCode = _sosvo.getNetworkCode();
            String paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(_sosvo.getServiceType(), _sosvo.getSubscriberType());

            if (paymentMethodType == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            }

            _sosvo.setPaymentMethodType(paymentMethodType);

            _sosvo.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_sosMSISDN));
            NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(_sosMSISDN, PretupsI.USER_TYPE_SENDER);
            _sosvo.setPrefixID(networkPrefixVO.getPrefixID());

            // Get the Interface Category routing details based on the receiver
            // Network Code and Service type
            if (_type.equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
                // service_interface_routing table name
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_sosNetworkCode + "_" + _sosvo.getServiceType() + "_" + _sosvo.getSubscriberType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", _requestIDStr, "For =" + _sosNetworkCode + "_" + _sosvo.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO.getAlternateInterfaceType() + " _oldDefaultSelector=" + _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "_newDefaultSelector= " + _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode());
                    }

                    _type = _serviceInterfaceRoutingVO.getInterfaceType();
                    _oldInterfaceCategory = _type;
                    _oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                    _useAlternateCategory = _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    _newInterfaceCategory = _serviceInterfaceRoutingVO.getAlternateInterfaceType();
                    _newDefaultSelector = _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
                } else {
                    _log.info("process", _requestIDStr, "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SOSSettlementController[process]", "", _sosMSISDN, _sosNetworkCode, "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    _type = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_sosvo.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        _oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                }
            } else {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_sosNetworkCode + "_" + _sosvo.getServiceType() + "_" + _sosvo.getSubscriberType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", _requestIDStr, "For =" + _sosNetworkCode + "_" + _sosvo.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO.getAlternateInterfaceType() + " _oldDefaultSelector=" + _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "_newDefaultSelector= " + _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode());
                    }
                    _oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                } else {
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_sosvo.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        _oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                    _log.info("process", _requestIDStr, "Service Interface Routing control Not defined, thus using default Selector=" + _oldDefaultSelector);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSSettlementController[process]", "", _sosMSISDN, _sosNetworkCode, "Service Interface Routing control Not defined, thus using default selector=" + _oldDefaultSelector);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestIDStr, "_sosVO:" + _sosvo);
            }

            // populate payment and service interface details for validate
            // action
            populateServicePaymentInterfaceDetails(p_con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION); // shamit
                                                                                                                // need
                                                                                                                // to
                                                                                                                // know
                                                                                                                // the
                                                                                                                // purpose

            if (BTSLUtil.isNullString(_sosvo.getRequestGatewayCode())) {
                _sosvo.setRequestGatewayCode(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE)));
            }

            processSOSSettlementRequest(p_con);

        } catch (BTSLBaseException be) {
            _log.error("process", "Exception be:" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            _sosvo.setSuccessTxn(false);
            if (be.isKey()) {
                if (BTSLUtil.isNullString(_sosvo.getErrorCode())) {
                    _sosvo.setErrorCode(be.getMessageKey());
                }
                _sosvo.setMessageCode(be.getMessageKey());
                _sosvo.setMessageArguments(be.getArgs());
            } else {
                _sosvo.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            if (!(_sosvo.getTransactionStatus() != null && _sosvo.getTransactionStatus().length() > 0)) {
                _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            }

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[process]", _transactionID, _sosMSISDN, _sosNetworkCode, "ErrorCode:" + _sosvo.getErrorCode() + " BTSL Base Exception:" + be.getMessage());
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Base Exception " + be.getMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _sosvo.getMessageCode() + "ErrorCode:" + _sosvo.getErrorCode());

        } catch (Exception e) {
            _log.error("process", "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            _sosvo.setSuccessTxn(false);
            if (!(_sosvo.getTransactionStatus() != null && _sosvo.getTransactionStatus().length() > 0)) {
                _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            }

            _sosvo.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            _sosvo.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            _log.error("process", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[process]", _transactionID, _sosMSISDN, _sosNetworkCode, "ErrorCode:" + _sosvo.getErrorCode() + " Exception:" + e.getMessage());
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Base Exception " + e.getMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _sosvo.getMessageCode() + "ErrorCode:" + _sosvo.getErrorCode());
        } finally {
            try {
                if (BTSLUtil.isNullString(_sosvo.getMessageCode())) {
                    _sosvo.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                }

                try {
                    updateSOSDetails(p_con);
                } catch (BTSLBaseException bex) {
                    _log.errorTrace(METHOD_NAME, bex);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[process]", _transactionID, _sosMSISDN, _sosNetworkCode, "Not able to update Subscriber Last Details Exception:" + e.getMessage());
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
                _log.error("process", _transactionID, "Exception while updating transfer details in database:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[process]", _transactionID, _sosMSISDN, _sosNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
            }
            if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_sosvo.getSOSReturnMsg() == null || !((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey())) {
                _sosvo.setSOSReturnMsg(new BTSLMessages((PretupsErrorCodesI.SOS_SETTLEMENT_FAIL), new String[] { String.valueOf(_transactionID), PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()) }));
            }

            if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                if (_sosvo.getSOSReturnMsg() == null) {
                    (new PushMessage(_sosMSISDN, getSOSSuccessMessage(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                } else if (_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey()) {
                    BTSLMessages btslRecMessages = (BTSLMessages) _sosvo.getSOSReturnMsg();
                    (new PushMessage(_sosMSISDN, BTSLUtil.getMessage(_locale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                } else {
                    (new PushMessage(_sosMSISDN, (String) _sosvo.getSOSReturnMsg(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                }
            } else if (_failMessageRequired) {
                if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    if (_sosvo.getSOSReturnMsg() == null) {
                        (new PushMessage(_sosMSISDN, getSOSFailMessage(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else if (_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey()) {
                        BTSLMessages btslRecMessages = (BTSLMessages) _sosvo.getSOSReturnMsg();
                        (new PushMessage(_sosMSISDN, BTSLUtil.getMessage(_locale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else {
                        (new PushMessage(_sosMSISDN, (String) _sosvo.getSOSReturnMsg(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    }
                }

                else if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    if (_sosvo.getSOSReturnMsg() == null) {
                        (new PushMessage(_sosMSISDN, getReceiverAmbigousMessage(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else if (_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey()) {
                        BTSLMessages btslRecMessages = (BTSLMessages) _sosvo.getSOSReturnMsg();
                        (new PushMessage(_sosMSISDN, BTSLUtil.getMessage(_locale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else {
                        (new PushMessage(_sosMSISDN, (String) _sosvo.getSOSReturnMsg(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    }
                }
            }
            TransactionLog.log(_transactionID, _sosvo.getRequestIDStr(), _sosvo.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + _sosvo.getMessageCode());
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
            }
        }
    }

    /**
     * Method to perform validation request
     * 
     * @param p_con
     *            TODO
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processSOSSettlementRequest(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "processSOSSettlementRequest";
        if (_log.isDebugEnabled()) {
            _log.debug("processSOSSettlementRequest", "Entered and performing validations for _transactionID=" + _transactionID);
        }
        try {
            NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(PretupsI.P2P_MODULE, _sosNetworkCode, _sosvo.getPaymentMethodType());
            _intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
            _intModIPS = networkInterfaceModuleVOS.getIP();
            _intModPortS = networkInterfaceModuleVOS.getPort();
            _intModClassNameS = networkInterfaceModuleVOS.getClassName();
            CommonClient commonClient = new CommonClient();
            String sosValStr = null;
            sosValStr = getSOSValidateStr();
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, sosValStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            String sosValResponse = commonClient.process(sosValStr, _transactionID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, sosValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            if (_log.isDebugEnabled()) {
                _log.debug("processSOSSettlementRequest", "sosValResponse From IN Module=" + sosValResponse);
            }
            try {
                // Get the SOS Subscriber validate response and processes the
                // same
                updateForSOSValidateResponse(p_con, sosValResponse);
            } catch (BTSLBaseException be) {
                TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "processSOSSettlementRequest Transaction Failed in Validation", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _sosvo.getInterfaceResponseCode() + " SOS VO" + _sosvo.toString());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSSettlementController[processSOSSettlementRequest] Transaction Failed in Validation", _transactionID, _sosMSISDN, _sosNetworkCode, "ErrorCode:" + _sosvo.getErrorCode() + " Exception:" + be.getMessage());

                if (_sosDeletionReqFromSubRouting && _sosvo.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
                    PretupsBL.deleteSubscriberInterfaceRouting(_sosMSISDN, PretupsI.INTERFACE_CATEGORY_PRE);
                }
                _log.errorTrace(METHOD_NAME, be);
                throw be;
            }
            if (Math.abs(_sosvo.getLmbAmountAtIN()-0)<0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[processSOSSettlementRequest]", _sosvo.getTransactionID(), _sosMSISDN, "Subscriber has been already debited earlier so no debit now :" + _sosvo.getLmbAmountAtIN(), " and LMB amount at DB :" + _sosvo.getDebitAmount());
                throw new BTSLBaseException(this, "processSOSSettlementRequest", PretupsErrorCodesI.SOS_REQ_ALREADY_PROCESSED);
            } else {
                try {
                    try {
                        if ((BTSLUtil.getDifferenceInUtilDates(_sosvo.getPreviousExpiry(), _currentDate) > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMB_VALIDITY_DAYS_FORCESETTLE))).intValue()) && (!BTSLUtil.isNullString(_sosvo.getAccountStatus()) && _sosvo.getAccountStatus().contains("Suspend"))) {
                            _sosvo.setErrorCode(PretupsErrorCodesI.FORCEFUL_LMB_SUCCESS_SETTLEMENT_FOR_OPERATOR_CALL);
                        }
                    } catch (Exception be) {
                        _log.errorTrace(METHOD_NAME, be);
                    }
                    if (!BTSLUtil.isNullString(_sosvo.getErrorCode()) && PretupsErrorCodesI.FORCEFUL_LMB_SUCCESS_SETTLEMENT_FOR_OPERATOR_CALL.equalsIgnoreCase(_sosvo.getErrorCode())) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[processSOSSettlementRequest]", _sosvo.getTransactionID(), _sosMSISDN, "Subscriber's Expiry<120 days so no debit now : " + _sosvo.getLmbAmountAtIN(), " and LMB settled forcefully amount at DB :" + _sosvo.getDebitAmount());
                        throw new BTSLBaseException(this, "processSOSSettlementRequest", PretupsErrorCodesI.FORCEFUL_LMB_SUCCESS_SETTLEMENT_FOR_OPERATOR_CALL);
                    } else {
                        boolean subscriberDebitReqd = new SOSTxnDAO().isSubscriberdebitrequired(p_con, _sosvo);
                        if (!subscriberDebitReqd) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[processSOSSettlementRequest]", _sosvo.getTransactionID(), _sosMSISDN, "Subscriber has been already debited earlier so no debit now :" + _sosvo.getLmbAmountAtIN(), " and LMB amount at DB :" + _sosvo.getDebitAmount());
                            throw new BTSLBaseException(this, "processSOSSettlementRequest", PretupsErrorCodesI.SOS_REQ_ALREADY_PROCESSED);
                        }
                    }
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    throw be;
                }
            }

            if (_sosvo.getPreviousBalance() < _sosvo.getDebitAmount()) {
                _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[processSOSSettlementRequest]", _sosvo.getTransactionID(), _sosMSISDN, "Debit amount at IN & DB is less.Core debit amount at IN :" + _sosvo.getPreviousBalance(), " and Core amount to be debitted at DB :" + _sosvo.getDebitAmount());
                throw new BTSLBaseException(this, "processSOSSettlementRequest", PretupsErrorCodesI.SOS_CORE_BAL_LESS);
            }


            populateServicePaymentInterfaceDetails(p_con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            
            String requestStr = getSOSDebitStr();

            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            String sosDebitResponse = commonClient.process(requestStr, _transactionID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP, sosDebitResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug("processSOSSettlementRequest", _transactionID, "senderDebitResponse From IN Module=" + sosDebitResponse);
            }

            try {
                // Get the SOS Debit response and processes the same
                updateForSOSDebitResponse(sosDebitResponse);
            } catch (BTSLBaseException be) {
                TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed during SOS Settlement debit", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _sosvo.getTransferStatus() + " Getting Code=" + _sosvo.getInterfaceResponseCode());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSSettlementController[processSOSSettlementRequest] Transaction Failed during SOS Settlement debit", _transactionID, _sosMSISDN, _sosNetworkCode, "ErrorCode:" + _sosvo.getErrorCode() + " Exception:" + be.getMessage());
                _log.errorTrace(METHOD_NAME, be);
                throw be;
            }

        } catch (BTSLBaseException be) {
            if (BTSLUtil.isNullString(_sosvo.getErrorCode())) {
                if (be.isKey()) {
                    _sosvo.setErrorCode(be.getMessageKey());
                } else {
                    _sosvo.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            }
            _log.error("SOSSettlementController[processSOSSettlementRequest]", "Getting BTSL Base Exception:" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {

            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed caught in Exception", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _sosvo.getTransferStatus() + " Getting Code=" + _sosvo.getInterfaceResponseCode());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSSettlementController[processSOSSettlementRequest]  caught in Exception", _transactionID, _sosMSISDN, _sosNetworkCode, "ErrorCode:" + _sosvo.getErrorCode() + " Exception:" + e.getMessage());

            if (BTSLUtil.isNullString(_sosvo.getErrorCode())) {
                _sosvo.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            _log.error("SOSSettlementController[processSOSSettlementRequest]", "Getting Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "Exception while performing validation request");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("processSOSSettlementRequest Exiting ", _transactionID, "" + _sosMSISDN + " SOSVO " + _sosvo.toString());
            }
        }

    }

    /**
     * Method to update the buddy details
     * 
     * @param p_con
     * @param p_transferVO
     * @param p_currentDate
     * @throws BTSLBaseException
     */
    public void updateSOSDetails(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "updateSOSDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("updateSOSDetails", "Entered with Transfer ID=" + _transactionID + " p_currentDate=" + _sosMSISDN);
        }
        try {
            int updateCounters = 0;
            updateCounters = _sosTxndao.updateSettelementDetails(p_con, _sosvo);
            if (updateCounters <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[updateSOSDetails]", _transactionID, _sosMSISDN, _sosNetworkCode, "Not able to update Settlement details in LMB transaction table");
                throw new BTSLBaseException("SOSSettlementController", "updateSOSDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("updateBuddyDetails", "Exception :" + be);
            throw new BTSLBaseException("SOSSettlementController", "updateSOSDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("updateSOSDetails", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[updateSOSDetails]", _transactionID, "", "", "Not able to update Settlement details in LMB transaction table,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("SOSSettlementController", "updateSOSDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("updateSOSDetails", "Exiting for Transfer ID=" + _transactionID);
        }
    }

    /**
     * Method to get the Receiver Ambigous Message
     * 
     * @return
     */
    private String getReceiverAmbigousMessage() {
        String[] messageArgArray = { _sosMSISDN, _transactionID, PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()) };
        return BTSLUtil.getMessage(_locale, PretupsErrorCodesI.P2P_RECEIVER_AMBIGOUS_MESSAGE_KEY, messageArgArray);
    }

    /**
     * Method to get the SOS settlement Fail Message
     * 
     * @return
     */
    private String getSOSFailMessage() {
        String[] messageArgArray = { _sosMSISDN, _transactionID, PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()) };
        return BTSLUtil.getMessage(_locale, PretupsErrorCodesI.SOS_SETTLEMENT_FAIL, messageArgArray);
    }

    /***
     * 
     * Method updated for notification message using service class date 15/05/06
     */
    private String getSOSSuccessMessage() {
        final String METHOD_NAME = "getSOSSuccessMessage";
        String[] messageArgArray = null;
        String key = null;
        if (!"N".equals(_sosPostBalanceAvailable)) {
            String dateStr = null;
            try {
                dateStr = BTSLUtil.getDateStringFromDate(_sosvo.getNewExpiry());
            } catch (Exception e) {
                dateStr = String.valueOf(_sosvo.getNewExpiry());
                _log.errorTrace(METHOD_NAME, e);
            }
            messageArgArray = new String[] { _transactionID, PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()), PretupsBL.getDisplayAmount(_sosvo.getPostBalance()), dateStr, PretupsBL.getDisplayAmount(_sosvo.getDebitAmount() - _sosvo.getCreditAmount()) };
            key = PretupsErrorCodesI.SOS_SETTLEMENT_SUCCESS;
        } else {
            messageArgArray = new String[] { _transactionID, PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()), PretupsBL.getDisplayAmount(_sosvo.getDebitAmount() - _sosvo.getCreditAmount()) };
            key = PretupsErrorCodesI.SOS_SETTLEMENT_SUCCESS_WITHOUT_POSTBAL;
        }
        return BTSLUtil.getMessage(_locale, key, messageArgArray);
    }

    /**
     * Method to populate the Interface Details of the sender and receiver based
     * on action specified
     * 
     * @param action
     *            Can be Validate / Topup
     * @throws BTSLBaseException
     */
    public void populateServicePaymentInterfaceDetails(Connection p_con, String action) throws BTSLBaseException {
        boolean isSOSFound = false;
        if (_log.isDebugEnabled()) {
            _log.debug(this, "Getting interface details For Action=" + action + " _senderInterfaceInfoInDBFound=" + _sosInterfaceInfoInDBFound + " _receiverInterfaceInfoInDBFound=" + _sosInterfaceInfoInDBFound);
        }

        // Avoid searching in the loop again if in validation details was found
        // in database
        if ((!_sosInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {

            isSOSFound = getInterfaceRoutingDetails(p_con, _sosMSISDN, _sosvo.getPrefixID(), _type, _sosNetworkCode, _sosvo.getServiceType(), _type, PretupsI.USER_TYPE_SENDER, action);
            // If receiver Not found and we need to perform the alternate
            // category routing before IN Validation and it has not been
            // performed before then do Category Routing
            if (action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION) && !isSOSFound && _performIntfceCatRoutingBeforeVal && _useAlternateCategory && !_interfaceCatRoutingDone) {
                // Get the alternate interface category and check whether it is
                // valid in that category.
                _log.info(this, "********* Performing ALTERNATE INTERFACE CATEGORY routing for receiver before IN Validations on Interface=" + _newInterfaceCategory + " *********");

                _type = _newInterfaceCategory;
                _interfaceCatRoutingDone = true;

                _sosvo.setReqSelector(_newDefaultSelector);

                // Load the new prefix ID against the interface category , If
                // Not required then give the error

                _networkPrefixVO = null;
                _networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_sosvo.getMsisdnPrefix(), _type);
                if (_networkPrefixVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(this, "Getting Reeciver Prefix ID for MSISDN=" + _sosMSISDN + " as " + _networkPrefixVO.getPrefixID());
                    }
                    _sosvo.setNetworkCode(_networkPrefixVO.getNetworkCode());
                    _sosvo.setPrefixID(_networkPrefixVO.getPrefixID());
                    _sosvo.setSubscriberType(_networkPrefixVO.getSeriesType());
                    isSOSFound = getInterfaceRoutingDetails(p_con, _sosMSISDN, _sosvo.getPrefixID(), _type, _sosNetworkCode, _sosvo.getServiceType(), _type, PretupsI.USER_TYPE_SENDER, action);
                } else {
                    _log.error(this, "Series Not Defined for Alternate Interface =" + _type + " For Series=" + _sosvo.getMsisdnPrefix());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSSettlementController[populateServicePaymentInterfaceDetails]", "", "", "", "Series =" + _sosvo.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But alternate Category Routing was required on interface");
                    isSOSFound = false;
                }
            }
        } else {
            isSOSFound = true;
        }
        if (!isSOSFound) {
            throw new BTSLBaseException("SOSSettlementController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_NOTFOUND_SERVICEINTERFACEMAPPING);
        }
    }

    /**
     * Get the sender Debit Request String
     * 
     * @return
     */
    public String getSOSDebitStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getSOSCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_DEBIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + _sosvo.getDebitAmount());
        strBuff.append("&TRANSACTION_ID=" + _sosvo.getTransactionID());
        strBuff.append("&LMB_DEBIT=" + PretupsI.YES);
        strBuff.append("&CAL_OLD_EXPIRY_DATE=" + _sosvo.getOldExpiryInMillis());
        strBuff.append("&LMB_CREDIT_AMT=" + _sosvo.getLmbAmountAtIN());
        return strBuff.toString();
    }

    /**
     * Get the Receiver Request String to be send to common Client
     * 
     * @return
     */
    private String getSOSCommonString() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + _sosMSISDN);
        strBuff.append("&TRANSACTION_ID=" + _transactionID);
        strBuff.append("&NETWORK_CODE=" + _sosNetworkCode);
        strBuff.append("&INTERFACE_ID=" + _sosvo.getInterfaceID());
        strBuff.append("&INTERFACE_HANDLER=" + _sosvo.getInterfaceHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeR);
        strBuff.append("&INT_MOD_IP=" + _intModIPR);
        strBuff.append("&INT_MOD_PORT=" + _intModPortR);
        strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameR);
        strBuff.append("&MODULE=" + PretupsI.P2P_MODULE);
        strBuff.append("&USER_TYPE=R");
        strBuff.append("&REQ_SERVICE=" + _serviceType);
        return strBuff.toString();
    }

    /**
     * Gets the receiver validate Request String
     * 
     * @return
     */
    public String getSOSValidateStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getSOSCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
        return strBuff.toString();
    }

    /**
     * Method to handle Sender Debit Response
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForSOSDebitResponse(String str) throws BTSLBaseException {
        final String METHOD_NAME = "updateForSOSDebitResponse";
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");

        // added to log the IN validation request sent and request received
        // time.
        if (null != map.get("IN_START_TIME")) {
            _sosvo.setTopUPSenderRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            _sosvo.setTopUPSenderResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
        }

        _sosvo.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        _sosvo.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        _sosvo.setUpdateStatus(status);
        _sosPostBalanceAvailable = ((String) map.get("POST_BALANCE_ENQ_SUCCESS"));

        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
            try {
                _sosvo.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        }

        _sosvo.setReferenceID((String) map.get("IN_RECON_ID"));

        String[] strArr = null;

        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            _sosvo.setErrorCode(status + "_S");
            _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _sosvo.setTransferStatus(status);
            strArr = new String[] { _sosMSISDN, PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()), _transactionID };
            throw new BTSLBaseException(this, "updateForSOSDebitResponse", _sosvo.getErrorCode(), 0, strArr, null);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
            _sosvo.setErrorCode(status + "_S");
            _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _sosvo.setTransferStatus(status);
            _sosvo.setUpdateStatus(status);
            strArr = new String[] { _transactionID, _sosMSISDN, PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()) };
            throw new BTSLBaseException(this, "updateForSOSDebitResponse", PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        } else {
            _sosvo.setTransferStatus(status);
            _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _sosvo.setUpdateStatus(status);
        }
        try {
            _sosvo.setLmbUpdateStatus((String) map.get("LMB_TRANSACTION_STATUS"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        try {
            _sosvo.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        try {
            _sosvo.setNewGraceDate(BTSLUtil.getDateFromDateString((String) map.get("NEW_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        try {
            _sosvo.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
    }

    /**
     * Method to handle receiver validation response
     * This method will perform the Alternate interface routing is mobile is not
     * found on the interface
     * If not found on any interface then perform the alternate category routing
     * if that is not done
     * Earlier.
     * 
     * @param con
     *            TODO
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForSOSValidateResponse(Connection p_con, String str) throws BTSLBaseException {
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");
        ArrayList altList = null;
        boolean isRequired = false;
        if (null != map.get("IN_START_TIME")) {
            _sosvo.setValidationSenderRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            _sosvo.setValidationSenderResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
        }

        // If we get the MSISDN not found on interface error then perform
        // interface routing
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
            _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(_sosvo.getInterfaceID());
            if (altList != null && !altList.isEmpty()) {
                performSOSAlternateRouting(p_con, altList, SRC_BEFORE_INRESP_CAT_ROUTING);
            } else {
                if (_useAlternateCategory && !_performIntfceCatRoutingBeforeVal && !_interfaceCatRoutingDone) {
                    performAlternateCategoryRouting(p_con);
                } else {
                    isRequired = true;
                }
            }
        }
        if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
            populateSOSDetails(map);
        }
    }

    /**
     * Method to get the interface details based on the parameters
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_prefixID
     * @param p_subscriberType
     * @param p_networkCode
     * @param p_serviceType
     *            : RC or REG etc
     * @param p_interfaceCategory
     *            : PRE or POST
     * @param p_userType
     *            : SENDER or RECEIVER
     * @param p_action
     *            : VALIDATE OR UPDATE
     * @return
     */
    private boolean getInterfaceRoutingDetails(Connection p_con, String p_msisdn, long p_prefixID, String p_subscriberType, String p_networkCode, String p_serviceType, String p_interfaceCategory, String p_userType, String p_action) throws BTSLBaseException {
        final String METHOD_NAME = "getInterfaceRoutingDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("getInterfaceRoutingDetails", _requestIDStr, " Entered with MSISDN=" + p_msisdn + " Prefix ID=" + p_prefixID + " p_subscriberType=" + p_subscriberType + " p_networkCode=" + p_networkCode + " p_serviceType=" + p_serviceType + " p_interfaceCategory=" + p_interfaceCategory + " p_userType=" + p_userType + " p_action=" + p_action);
        }
        boolean isSuccess = false;
        /*
         * Get the routing control parameters based on network code , service
         * and interface category
         * 1. Check if database check is required
         * 2. If required then check in database whether the number is present
         * 3. If present then Get the interface ID from the same and send
         * request to interface to validate the same
         * 4. If not found then Get the interface ID On the Series basis and
         * send request to interface to validate the same
         */
        String interfaceID = null;
        String interfaceHandlerClass = null;
        String externalID = null;
        _performIntfceCatRoutingBeforeVal = false; // Set so that receiver flag
                                                   // is not overridden by
                                                   // sender flag
        SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
        try {
            if (subscriberRoutingControlVO != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("getInterfaceRoutingDetails", _transactionID, " p_userType=" + p_userType + " Database Check Required=" + subscriberRoutingControlVO.isDatabaseCheckBool() + " Series Check Required=" + subscriberRoutingControlVO.isSeriesCheckBool());
                }

                if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)) {
                        ListValueVO listValueVO = PretupsBL.validateNumberInRoutingDatabase(p_con, p_msisdn, p_interfaceCategory);
                        if (listValueVO != null) {
                            isSuccess = true;

                            setInterfaceDetails(p_prefixID, p_userType, listValueVO, false, null);

                            if (p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                _sosInterfaceInfoInDBFound = true;
                                _sosDeletionReqFromSubRouting = true;
                            }
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("getInterfaceRoutingDetails", _transactionID, " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " not found in Database , performing Series Check for Prefix ID=" + p_prefixID);
                            }

                            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            try {
                            	
                            	 if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                                     interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + PretupsI.SERVICE_DEFAULT_SELECTOR + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                                     if (interfaceMappingVO1 != null) {
                                         isSuccess = true;
                                         setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO1);
                                         
                                     }
                                 }
                                 if (interfaceMappingVO1 == null) {
                                     interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                                     isSuccess = true;
                                     setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO);
                                 }

                            } catch (BTSLBaseException be) {
                                _log.errorTrace(METHOD_NAME, be);
                                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                    _performIntfceCatRoutingBeforeVal = true;
                                } else {
                                    throw be;
                                }
                            }
                        } else {
                            _performIntfceCatRoutingBeforeVal = true;
                            isSuccess = false;
                        }
                    } else if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)) {
                        WhiteListVO whiteListVO = PretupsBL.validateNumberInWhiteList(p_con, p_msisdn);
                        if (whiteListVO != null) {
                            isSuccess = true;
                            ListValueVO listValueVO = whiteListVO.getListValueVO();
                            interfaceID = listValueVO.getValue();
                            interfaceHandlerClass = listValueVO.getLabel();
                            externalID = listValueVO.getIDValue();
                            _sosExternalID = externalID;
                            _sosvo.setInterfaceID(interfaceID);
                            _sosvo.setInterfaceHandlerClass(interfaceHandlerClass);

                            if (!PretupsI.YES.equals(listValueVO.getStatus())) {
                                // which language message to be set is
                                // determined from the locale master table for
                                // the requested locale
                                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_locale)).getMessage())) {
                                    _sosvo.setSOSReturnMsg(listValueVO.getOtherInfo());
                                } else {
                                    _sosvo.setSOSReturnMsg(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, "getInterfaceRoutingDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
                            }
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("getInterfaceRoutingDetails", _transactionID, " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " not found in Database , performing Series Check for Prefix ID=" + p_prefixID);
                            }

                            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            try {
                            	
                            	 if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                                     interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" +  PretupsI.SERVICE_DEFAULT_SELECTOR + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                                     if (interfaceMappingVO1 != null) {
                                         isSuccess = true;
                                         setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO1);
                                         
                                     }
                                 }
                                 if (interfaceMappingVO1 == null) {
                                     interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                                     isSuccess = true;
                                     setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO);
                                 }
                            } catch (BTSLBaseException be) {
                                _log.errorTrace(METHOD_NAME, be);
                                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                    _performIntfceCatRoutingBeforeVal = true;
                                } else {
                                    throw be;
                                }
                            }
                        } else {
                            isSuccess = false;
                            _performIntfceCatRoutingBeforeVal = true;
                        }
                    }
                } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("getInterfaceRoutingDetails", _transactionID, " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " performing Series Check for Prefix ID=" + p_prefixID);
                    }
                    ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                    MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                    try {
                    	
                    	 if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                             interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" +  PretupsI.SERVICE_DEFAULT_SELECTOR + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                             if (interfaceMappingVO1 != null) {
                                 isSuccess = true;
                                 setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO1);
                                 
                             }
                         }
                         if (interfaceMappingVO1 == null) {
                             interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                             isSuccess = true;
                             setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO);
                         }
                         
                                         

                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                            _performIntfceCatRoutingBeforeVal = true;
                        } else {
                            throw be;
                        }
                    }
                } else {
                    isSuccess = false;
                }
            } else {
                if (_log.isDebugEnabled()) {
                    _log.debug("getInterfaceRoutingDetails", _transactionID, " By default carrying out series check as routing control not defined for p_userType=" + p_userType + " MSISDN =" + p_msisdn + " performing Series Check for Prefix ID=" + p_prefixID);
                }
                // This event is raised by ankit Z on date 3/8/06 for case when
                // entry not found in routing control and considering series
                // based routing
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSSettlementController[getInterfaceRoutingDetails]", _transactionID, _sosMSISDN, _sosNetworkCode, "Exception:Routing control information not defined so performing series based routing");

                ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                try {
                	
                	 if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                         interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" +  PretupsI.SERVICE_DEFAULT_SELECTOR + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                         if (interfaceMappingVO1 != null) {
                             isSuccess = true;
                             setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO1);
                             
                         }
                     }
                     if (interfaceMappingVO1 == null) {
                         interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                         isSuccess = true;
                         setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO);
                     }
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                        _performIntfceCatRoutingBeforeVal = true;
                    } else {
                        throw be;
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[getInterfaceRoutingDetails]", _transactionID, _sosMSISDN, _sosNetworkCode, "Exception:" + e.getMessage());
            isSuccess = false;
            throw new BTSLBaseException(this, "getInterfaceRoutingDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getInterfaceRoutingDetails", _requestIDStr, " Exiting with isSuccess=" + isSuccess + "_sosAllServiceClassID=" + _sosAllServiceClassID);
        }
        return isSuccess;
    }

    /**
     * Method: updateReceiverLocale
     * This method update the receiver locale with the language code returned
     * from the IN
     * 
     * @param p_languageCode
     *            String
     * @return void
     */
    public void updateSOSLocale(String p_languageCode) {
        final String METHOD_NAME = "updateSOSLocale";
        if (_log.isDebugEnabled()) {
            _log.debug("updateSOSLocale", "Entered p_languageCode=" + p_languageCode);
        }
        // check if language is returned fron IN or not.
        // If not then send alarm and not set the locale
        // otherwise set the local corresponding to the code returned from the
        // IN.
        if (!BTSLUtil.isNullString(p_languageCode)) {
            try {
                if (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode) == null) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SOSSettlementController[updateSOSLocale]", _transactionID, _sosMSISDN, "", "Exception: Notification language returned from IN is not defined in system p_languageCode: " + p_languageCode);
                } else {
                    _locale = (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode));
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("updateSOSLocale", "Exited _locale=" + _locale);
        }
    }

    /**
     * This method will perform the alternate interface category routing if
     * there
     * This method will be called either after validation or after performing
     * interface routing
     * 
     * @param p_con
     *            TODO
     * @throws BTSLBaseException
     */
    public void performAlternateCategoryRouting(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "performAlternateCategoryRouting";
        if (_log.isDebugEnabled()) {
            _log.debug("performAlternateCategoryRouting", "Performing ALternate interface category routing Entered");
        }
        try {
            String requestStr = null;
            CommonClient commonClient = null;
            String sosValResponse = null;
            // if(p_con==null)
            // p_con=OracleUtil.getConnection();//Commented to avoid multiple DB
            // connections
            populateAlternateInterfaceDetails(p_con);
            requestStr = getSOSValidateStr();
            commonClient = new CommonClient();
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Alternate Category Routing");
            sosValResponse = commonClient.process(requestStr, _transactionID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, sosValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            handleSOSValidateResponse(p_con, sosValResponse, SRC_AFTER_INRESP_CAT_ROUTING);
            if (InterfaceErrorCodesI.SUCCESS.equals(_sosvo.getValidationStatus())) {
                // If mobile number found on Post but previously was defined in
                // PRE then delete the number
                if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                    if (_sosDeletionReqFromSubRouting) {
                        PretupsBL.deleteSubscriberInterfaceRouting(_sosMSISDN, _oldInterfaceCategory);
                    }
                } else {
                    // Update in DB for routing interface
                    SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_sosNetworkCode + "_" + _sosvo.getServiceType() + "_" + _newInterfaceCategory);
                    if (!_sosDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("performAlternateCategoryRouting", "Inserting the MSISDN=" + _sosMSISDN + " in Subscriber routing database for further usage");
                        }

                        PretupsBL.insertSubscriberInterfaceRouting(_sosvo.getInterfaceID(), _sosExternalID, _sosMSISDN, _newInterfaceCategory, _sosvo.getUserID(), _currentDate);
                        _sosInterfaceInfoInDBFound = true;
                        _sosDeletionReqFromSubRouting = true;
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[performAlternateCategoryRouting]", _transactionID, _sosMSISDN, _sosNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performAlternateCategoryRouting", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Method to populate the Alternate Interface Details for the Receiver
     * against the new interface category
     * 
     * @throws BTSLBaseException
     */
    public void populateAlternateInterfaceDetails(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("populateAlternateInterfaceDetails", "Entered to get the alternate category");
        }

        boolean isReceiverFound = false;

        if (!_interfaceCatRoutingDone) {
            _interfaceCatRoutingDone = true;
            _type = _newInterfaceCategory;
            _networkPrefixVO = null;

            _sosvo.setReqSelector(_newDefaultSelector);
            // _p2pTransferVO.setSubService(_newDefaultSelector);

            // Load the new prefix ID against the interface category , If Not
            // required then give the error

            if (_log.isDebugEnabled()) {
                _log.debug("populateAlternateInterfaceDetails", "Got the alternate category as =" + _type);
            }

            _networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_sosvo.getMsisdnPrefix(), _type);
            if (_networkPrefixVO != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("populateAlternateInterfaceDetails", "Got the Prefix ID for MSISDN=" + _sosMSISDN + "Prefix ID=" + _sosvo.getPrefixID());
                }

                _sosvo.setNetworkCode(_networkPrefixVO.getNetworkCode());
                _sosvo.setPrefixID(_networkPrefixVO.getPrefixID());
                _sosvo.setSubscriberType(_networkPrefixVO.getSeriesType());
                isReceiverFound = getInterfaceRoutingDetails(p_con, _sosMSISDN, _sosvo.getPrefixID(), _sosvo.getSubscriberType(), _sosNetworkCode, _sosvo.getServiceType(), _type, PretupsI.USER_TYPE_SENDER, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
            } else {
                _log.error(this, "Series Not Defined for Alternate Interface =" + _type + " For Series=" + _sosvo.getMsisdnPrefix());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSSettlementController[populateAlternateInterfaceDetails]", "", "", "", "Series =" + _sosvo.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But required for validation");
                isReceiverFound = false;
            }

            if (!isReceiverFound) {
                throw new BTSLBaseException("SOSSettlementController", "populateAlternateInterfaceDetails", PretupsErrorCodesI.P2P_NOTFOUND_SERVICEINTERFACEMAPPING);
            }
        }
    }

    /**
     * This method handles the receiver validate response after sending request
     * to IN
     * 
     * @param p_con
     *            TODO
     * @param str
     * @param p_source
     * @throws BTSLBaseException
     */
    public void handleSOSValidateResponse(Connection p_con, String str, int p_source) throws BTSLBaseException {
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");
        ArrayList altList = null;
        boolean isRequired = false;

        // If we get the MSISDN not found on interface error then perform
        // interface routing
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
            _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(_sosvo.getInterfaceID());
            if (altList != null && !altList.isEmpty()) {
                performSOSAlternateRouting(p_con, altList, p_source);
            } else {
                isRequired = true;
            }
        }
        if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
            populateSOSDetails(map);
        }
    }

    /**
     * Method to perform the Interface routing for the subscriber MSISDN
     * 
     * @param p_con
     *            TODO
     * @param altList
     * @param p_source
     *            : Determines whether Alternate category needs to be performed
     *            after this or not
     * @throws BTSLBaseException
     */
    private void performSOSAlternateRouting(Connection p_con, ArrayList altList, int p_source) throws BTSLBaseException {
        final String METHOD_NAME = "performSOSAlternateRouting";
        if (_log.isDebugEnabled()) {
            _log.debug("performSOSAlternateRouting", _requestIDStr, " Entered p_source=" + p_source);
        }
        try {
            if (altList != null && !altList.isEmpty()) {
                // Check Interface Routing if not exists then continue
                // else decrease counters
                // Validate All service class checks
                // Decrease Counters for transaction and interface
                // Check Interface and transaction load
                // Send request
                // If success then update the subscriber routing table with new
                // interface ID
                // Also store in global veriables
                // If Not Found repeat the iteration for alt 2
                ListValueVO listValueVO = null;
                String requestStr = null;
                CommonClient commonClient = null;
                String receiverValResponse = null;
                switch (altList.size()) {
                case 1: {
                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(_sosvo.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null);

                    requestStr = getSOSValidateStr();
                    commonClient = new CommonClient();

                    TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performSOSAlternateRouting", "Sending Request For MSISDN=" + _sosMSISDN + " on ALternate Routing 1 to =" + _sosvo.getInterfaceID());
                    }

                    receiverValResponse = commonClient.process(requestStr, _transactionID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        sosValidateResponse(p_con, receiverValResponse, 1, altList.size(), p_source);
                        // If source is before IN validation then if interface
                        // is pre then we need to update in subscriber
                        // Routing but after alternate routing if number is
                        // found on another interface
                        // Then we need to delete the number from subscriber
                        // Routing or Vice versa
                        if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_sosvo.getValidationStatus())) {
                                // Update in DB for routing interface
                                updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, _sosNetworkCode, _sosvo.getInterfaceID(), _sosExternalID, _sosMSISDN, _type, _sosvo.getUserID(), _currentDate);
                            }
                        } else {
                            if (InterfaceErrorCodesI.SUCCESS.equals(_sosvo.getValidationStatus())) {
                                if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                                    if (_sosDeletionReqFromSubRouting) {
                                        PretupsBL.deleteSubscriberInterfaceRouting(_sosMSISDN, _oldInterfaceCategory);
                                    }
                                } else {
                                    // Update in DB for routing interface
                                    SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_sosNetworkCode + "_" + _sosvo.getServiceType() + "_" + _newInterfaceCategory);
                                    if (!_sosDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                        PretupsBL.insertSubscriberInterfaceRouting(_sosvo.getInterfaceID(), _sosExternalID, _sosMSISDN, _newInterfaceCategory, _sosvo.getUserID(), _currentDate);
                                        _sosInterfaceInfoInDBFound = true;
                                        _sosDeletionReqFromSubRouting = true;
                                    }
                                }
                            }
                        }
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        throw be;
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the Interface routing for the subscriber MSISDN when Alt size is 1.");
                    }

                    break;
                }
                case 2: {

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(_sosvo.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null);

                    requestStr = getSOSValidateStr();
                    commonClient = new CommonClient();

                    TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performSOSAlternateRouting", "Sending Request For MSISDN=" + _sosMSISDN + " on ALternate Routing 1 to =" + _sosvo.getInterfaceID());
                    }

                    receiverValResponse = commonClient.process(requestStr, _transactionID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        sosValidateResponse(p_con, receiverValResponse, 1, altList.size(), p_source);
                        // If source is before IN validation then if interface
                        // is pre then we need to update in subscriber
                        // Routing but after alternate routing if number is
                        // found on another interface
                        // Then we need to delete the number from subscriber
                        // Routing or Vice versa

                        if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_sosvo.getValidationStatus())) {
                                // Update in DB for routing interface
                                updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, _sosNetworkCode, _sosvo.getInterfaceID(), _sosExternalID, _sosMSISDN, _type, _sosvo.getUserID(), _currentDate);
                            }
                        } else {
                            if (InterfaceErrorCodesI.SUCCESS.equals(_sosvo.getValidationStatus())) {
                                if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                                    if (_sosDeletionReqFromSubRouting) {
                                        PretupsBL.deleteSubscriberInterfaceRouting(_sosMSISDN, _oldInterfaceCategory);
                                    }
                                } else {
                                    // Update in DB for routing interface
                                    SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_sosNetworkCode + "_" + _sosvo.getServiceType() + "_" + _newInterfaceCategory);
                                    if (!_sosDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                        PretupsBL.insertSubscriberInterfaceRouting(_sosvo.getInterfaceID(), _sosExternalID, _sosMSISDN, _newInterfaceCategory, _sosvo.getUserID(), _currentDate);
                                        _sosInterfaceInfoInDBFound = true;
                                        _sosDeletionReqFromSubRouting = true;
                                    }
                                }
                            }
                        }
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("performSOSAlternateRouting", "Got Status=" + InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND + " After validation Request For MSISDN=" + _sosMSISDN + " Performing Alternate Routing to 2");
                            }

                            listValueVO = (ListValueVO) altList.get(1);

                            setInterfaceDetails(_sosvo.getPrefixID(), PretupsI.USER_TYPE_RECEIVER, listValueVO, false, null);

                            requestStr = getSOSValidateStr();

                            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 2");

                            if (_log.isDebugEnabled()) {
                                _log.debug("performSOSAlternateRouting", "Sending Request For MSISDN=" + _sosMSISDN + " on ALternate Routing 2 to =" + _sosvo.getInterfaceID());
                            }

                            receiverValResponse = commonClient.process(requestStr, _transactionID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                sosValidateResponse(p_con, receiverValResponse, 2, altList.size(), p_source);
                                // If source is before IN validation then if
                                // interface is pre then we need to update in
                                // subscriber
                                // Routing but after alternate routing if number
                                // is found on another interface
                                // Then we need to delete the number from
                                // subscriber Routing or Vice versa

                                if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
                                    if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_sosvo.getValidationStatus())) {
                                        // Update in DB for routing interface
                                        updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, _sosNetworkCode, _sosvo.getInterfaceID(), _sosExternalID, _sosMSISDN, _type, _sosvo.getUserID(), _currentDate);
                                    }
                                } else {
                                    if (InterfaceErrorCodesI.SUCCESS.equals(_sosvo.getValidationStatus())) {
                                        if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                                            if (_sosDeletionReqFromSubRouting) {
                                                PretupsBL.deleteSubscriberInterfaceRouting(_sosMSISDN, _oldInterfaceCategory);
                                            }
                                        } else {
                                            // Update in DB for routing
                                            // interface
                                            SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_sosNetworkCode + "_" + _sosvo.getServiceType() + "_" + _newInterfaceCategory);
                                            if (!_sosDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                                PretupsBL.insertSubscriberInterfaceRouting(_sosvo.getInterfaceID(), _sosExternalID, _sosMSISDN, _newInterfaceCategory, _sosvo.getUserID(), _currentDate);
                                                _sosInterfaceInfoInDBFound = true;
                                                _sosDeletionReqFromSubRouting = true;
                                            }
                                        }
                                    }
                                }
                            } catch (BTSLBaseException bex) {
                                _log.errorTrace(METHOD_NAME, bex);
                                throw bex;
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the Interface routing for the subscriber MSISDN.");
                            }
                        } else {
                            throw be;
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the Interface routing for the subscriber MSISDN when Alt size is 2.");
                    }
                    break;
                }
                }

            } else {
                return;
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[performSOSAlternateRouting]", _transactionID, _sosMSISDN, _sosNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performSOSAlternateRouting", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("performSOSAlternateRouting", _requestIDStr, " Exiting ");
        }
    }

    /**
     * This method validates the response from Interfaces in interface routing
     * 
     * @param p_con
     *            TODO
     * @param str
     * @param p_attempt
     * @param p_altSize
     * @param p_source
     * @throws BTSLBaseException
     */
    public void sosValidateResponse(Connection p_con, String str, int p_attempt, int p_altSize, int p_source) throws BTSLBaseException {
        final String METHOD_NAME = "sosValidateResponse";
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");

        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) {
            _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            throw new BTSLBaseException(this, "sosValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        } else if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == p_altSize && p_source == SRC_BEFORE_INRESP_CAT_ROUTING && _useAlternateCategory && !_interfaceCatRoutingDone) {
            if (_log.isDebugEnabled()) {
                _log.debug(this, " Performing Alternate category routing as MSISDN not found on any interfaces after routing for " + _sosMSISDN);
            }
            performAlternateCategoryRouting(p_con);
        } else {
            if ("Y".equals(_sosvo.getUseInterfaceLanguage())) {
                // update the receiver locale if language code returned from IN
                // is not null
                updateSOSLocale((String) map.get("IN_LANG"));
            }
            _sosvo.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
            _sosvo.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
            _sosvo.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
            _sosvo.setValidationStatus(status);
            _sosvo.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            _sosvo.setReferenceID((String) map.get("IN_RECON_ID"));

            // If status is other than Success in validation stage mark sender
            // request as Not applicable and
            // Make transaction Fail
            String[] strArr = null;

            if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                _sosvo.setErrorCode(status + "_R");
                _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                _sosvo.setTransferStatus(status);
                strArr = new String[] { _sosMSISDN, PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()), _transactionID };
                throw new BTSLBaseException("SOSSettlementController", "sosValidateResponse", PretupsErrorCodesI.P2P_SENDER_FAIL, 0, strArr, null);
            }
            _sosvo.setTransferStatus(status);
            _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _sosvo.setSubscriberType(_type);
            _sosvo.setSubscriberType(_type);

            try {
                _sosvo.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                _sosvo.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;

            _sosvo.setFirstCall((String) map.get("FIRST_CALL"));
            _sosvo.setGraceDaysStr((String) map.get("GRACE_DAYS"));

            _sosvo.setServiceClassCode(URLDecoder.decode((String) map.get("SERVICE_CLASS")));

            try {
                _sosvo.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;

            // TO DO Done for testing purpose should we use it or give exception
            // in this case
            if (_sosvo.getPreviousExpiry() == null) {
                _sosvo.setPreviousExpiry(_currentDate);
            }
        }
    }

    /**
     * This method will populate the receiver Items VO after the response from
     * interfaces
     * 
     * @param p_map
     * @throws BTSLBaseException
     */
    public void populateSOSDetails(HashMap p_map) throws BTSLBaseException {
        final String METHOD_NAME = "populateSOSDetails";
        String status = (String) p_map.get("TRANSACTION_STATUS");

        // receiver language has to be taken from IN then the block below will
        // execute
        if ("Y".equals(_sosvo.getUseInterfaceLanguage())) {
            // update the SOS Subsctriber locale if language code returned from
            // IN is not null
            updateSOSLocale((String) p_map.get("IN_LANG"));
        }
        _sosvo.setProtocolStatus((String) p_map.get("PROTOCOL_STATUS"));
        _sosvo.setAccountStatus((String) p_map.get("ACCOUNT_STATUS"));
        _sosvo.setInterfaceResponseCode((String) p_map.get("INTERFACE_STATUS"));
        _sosvo.setValidationStatus(status);

        if (!BTSLUtil.isNullString((String) p_map.get("IN_TXN_ID"))) {
            try {
                _sosvo.setInterfaceReferenceID((String) p_map.get("IN_TXN_ID"));
            } catch (Exception e) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SOSSettlementController[populateSOSDetails]", _transactionID, _sosMSISDN, _sosNetworkCode, "Exception while parsing for interface txn ID , Exception:" + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
            }
        }

        _sosvo.setReferenceID((String) p_map.get("IN_RECON_ID"));
        String[] strArr = null;
        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            _sosvo.setErrorCode(status + "_R");
            _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _sosvo.setTransferStatus(status);
            strArr = new String[] { _sosMSISDN, PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()), _transactionID };
            if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P.equals(_sosvo.getValidationStatus())) {
                throw new BTSLBaseException("SOSSettlementController", "populateSOSDetails", InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P + "_S", 0, strArr, null);
            } else {
                throw new BTSLBaseException("SOSSettlementController", "populateSOSDetails", _sosvo.getErrorCode(), 0, strArr, null);
            }
        }
        _sosvo.setTransferStatus(status);
        _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

        try {
            _sosvo.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) p_map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        try {
            _sosvo.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) p_map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        _sosvo.setFirstCall((String) p_map.get("FIRST_CALL"));
        _sosvo.setGraceDaysStr((String) p_map.get("GRACE_DAYS"));
        _sosvo.setServiceClassCode(URLDecoder.decode((String) p_map.get("SERVICE_CLASS")));
        try {
            _sosvo.setPreviousBalance(Long.parseLong((String) p_map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        if (_sosvo.getPreviousExpiry() == null) {
            _sosvo.setPreviousExpiry(_currentDate);
        }
        try {
            _sosvo.setOldExpiryInMillis((String) p_map.get("CAL_OLD_EXPIRY_DATE"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        try {
            _sosvo.setLmbAmountAtIN(Double.parseDouble((String) p_map.get("LMB_ALLOWED_VALUE")));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
    }

    /**
     * This method sets the Interface Details based on the VOs values.
     * If p_useInterfacePrefixVO is True then use
     * p_MSISDNPrefixInterfaceMappingVO else use p_listValueVO to populate
     * values
     * 
     * @param p_prefixID
     * @param p_userType
     * @param p_listValueVO
     * @param p_useInterfacePrefixVO
     * @param p_MSISDNPrefixInterfaceMappingVO
     * @throws BTSLBaseException
     */
    private void setInterfaceDetails(long p_prefixID, String p_userType, ListValueVO p_listValueVO, boolean p_useInterfacePrefixVO, MSISDNPrefixInterfaceMappingVO p_MSISDNPrefixInterfaceMappingVO) throws BTSLBaseException {
        final String METHOD_NAME = "setInterfaceDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("setInterfaceDetails", _requestIDStr, " Entered p_prefixID=" + p_prefixID + " p_listValueVO=" + p_listValueVO + " p_useInterfacePrefixVO=" + p_useInterfacePrefixVO + " p_MSISDNPrefixInterfaceMappingVO=" + p_MSISDNPrefixInterfaceMappingVO);
        }
        try {
            String interfaceID = null;
            String interfaceHandlerClass = null;
            String status = null;
            String message1 = null;
            String message2 = null;
            String interfaceStatusTy = null;
            if (p_useInterfacePrefixVO) {
                interfaceID = p_MSISDNPrefixInterfaceMappingVO.getInterfaceID();
                interfaceHandlerClass = p_MSISDNPrefixInterfaceMappingVO.getHandlerClass();
                status = p_MSISDNPrefixInterfaceMappingVO.getInterfaceStatus();
                message1 = p_MSISDNPrefixInterfaceMappingVO.getLanguage1Message();
                message2 = p_MSISDNPrefixInterfaceMappingVO.getLanguage2Message();
                interfaceStatusTy = p_MSISDNPrefixInterfaceMappingVO.getStatusType();
            } else {
                interfaceID = p_listValueVO.getValue();
                interfaceHandlerClass = p_listValueVO.getLabel();
                status = p_listValueVO.getStatus();
                message1 = p_listValueVO.getOtherInfo();
                message2 = p_listValueVO.getOtherInfo2();
                interfaceStatusTy = p_listValueVO.getStatusType();
            }
            _sosvo.setInterfaceID(interfaceID);
            _sosvo.setInterfaceHandlerClass(interfaceHandlerClass);
            // Check if interface status is Active or not.

            if (!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceStatusTy)) {
                // ChangeID=LOCALEMASTER
                // which language message to be set is determined from the
                // locale master table for the requested locale

                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_locale)).getMessage())) {
                    _sosvo.setSOSReturnMsg(message1);
                } else {
                    _sosvo.setSOSReturnMsg(message2);
                }
                throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
            _log.error("setInterfaceDetails", "Getting Base Exception =" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[setInterfaceDetails]", _transactionID, _sosMSISDN, _sosNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("setInterfaceDetails", _requestIDStr, " Exiting with SOS Subscriber Interface ID=" + _sosvo.getInterfaceID());
            }
        }
    }

    /**
     * Method that will update the Subscriber Routing Details If interface is
     * PRE
     * 
     * @param p_userType
     * @param p_networkCode
     * @param p_interfaceID
     * @param p_externalID
     * @param p_msisdn
     * @param p_interfaceCategory
     * @param p_userID
     * @param p_currentDate
     * @throws BTSLBaseException
     */
    private void updateSubscriberRoutingDetails(String p_userType, String p_networkCode, String p_interfaceID, String p_externalID, String p_msisdn, String p_interfaceCategory, String p_userID, Date p_currentDate) throws BTSLBaseException {
        final String METHOD_NAME = "updateSubscriberRoutingDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("updateSubscriberRoutingDetails", _requestIDStr, " Entered p_userType=" + p_userType + " p_networkCode=" + p_networkCode + " p_interfaceID=" + p_interfaceID + " p_externalID=" + p_externalID + " p_msisdn=" + p_msisdn + " p_interfaceCategory=" + p_interfaceCategory + " p_userID=" + p_userID + " p_currentDate=" + p_currentDate);
        }
        try {
            boolean updationReqd = false;
            updationReqd = _sosDeletionReqFromSubRouting;

            if (updationReqd) {
                PretupsBL.updateSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
            } else {
                SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + _sosvo.getServiceType() + "_" + p_interfaceCategory);
                if (!updationReqd && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
                    _sosInterfaceInfoInDBFound = true;
                    _sosDeletionReqFromSubRouting = true;
                }
            }

        } catch (BTSLBaseException be) {
            _log.error("updateSubscriberRoutingDetails", "Getting Base Exception =" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[updateSubscriberRoutingDetails]", _transactionID, _sosMSISDN, _sosNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberRoutingDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateSubscriberRoutingDetails", _requestIDStr, " Exiting ");
            }
        }
    }

    public void processSOSRechargeRequest(SOSVO p_sosvo) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "processSOSRechargeRequest";
        if (_log.isDebugEnabled()) {
            _log.debug("processSOSRechargeRequest", "Entered and performing SOSDebit for _transactionID=" + _transactionID);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        _sosvo = p_sosvo;
        _sosMSISDN = _sosvo.getSubscriberMSISDN();
        _sosNetworkCode = _sosvo.getNetworkCode();
        _serviceType = _sosvo.getServiceType();
        _locale = _sosvo.getLocale();
        _type = _sosvo.getType();
        try {
            _transactionID = _sosvo.getTransactionID();

            String paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(_sosvo.getServiceType(), _sosvo.getSubscriberType());
            if (paymentMethodType == null) {
                throw new BTSLBaseException(this, "processSOSRechargeRequest", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            }

            p_sosvo.setPaymentMethodType(paymentMethodType);
            NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(PretupsI.C2S_MODULE, _sosvo.getNetworkCode(), _sosvo.getPaymentMethodType());
            _intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
            _intModIPS = networkInterfaceModuleVOS.getIP();
            _intModPortS = networkInterfaceModuleVOS.getPort();
            _intModClassNameS = networkInterfaceModuleVOS.getClassName();
            CommonClient commonClient = new CommonClient();
            NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(_sosMSISDN, PretupsI.USER_TYPE_SENDER);
            _sosvo.setPrefixID(networkPrefixVO.getPrefixID());
            populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            
            String requestStr = getSOSDebitThroughRechargeStr();
            if (Math.abs(_sosvo.getLmbAmountAtIN()-0)<0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSSettlementController[processSOSRechargeRequest]", _sosvo.getTransactionID(), _sosMSISDN, "Subscriber has been already debited earlier so no debit now :" + _sosvo.getLmbAmountAtIN(), " and LMB amount at DB :" + _sosvo.getDebitAmount());
                throw new BTSLBaseException(this, "processSOSRechargeRequest", PretupsErrorCodesI.SOS_REQ_ALREADY_PROCESSED);
            }
            TransactionLog.log(_transactionID, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
         
            
            String sosDebitResponse = commonClient.process(requestStr, _transactionID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            TransactionLog.log(_transactionID, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP, sosDebitResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug("processSOSRechargeRequest", _transactionID, "senderDebitResponse From IN Module=" + sosDebitResponse);
            }

            try {
                // Get the SOS Debit response and processes the same
                updateForSOSDebitResponse(sosDebitResponse);
            } catch (BTSLBaseException be) {
                TransactionLog.log(_transactionID, _sosMSISDN, _sosNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed during SOS Settlement debit", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _sosvo.getTransferStatus() + " Getting Code=" + _sosvo.getInterfaceResponseCode());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSSettlementController[processSOSRechargeRequest] Transaction Failed during SOS Settlement debit", _transactionID, _sosMSISDN, _sosNetworkCode, "ErrorCode:" + _sosvo.getErrorCode() + " Exception:" + be.getMessage());
                _log.errorTrace(METHOD_NAME, be);
                throw be;
            }

        } catch (BTSLBaseException be) {
            _log.error("processSOSRechargeRequest", "Exception be:" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            _sosvo.setSuccessTxn(false);
            if (be.isKey()) {
                if (BTSLUtil.isNullString(_sosvo.getErrorCode())) {
                    _sosvo.setErrorCode(be.getMessageKey());
                }
                _sosvo.setMessageCode(be.getMessageKey());
                _sosvo.setMessageArguments(be.getArgs());
            } else {
                _sosvo.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            if (!(_sosvo.getTransactionStatus() != null && _sosvo.getTransactionStatus().length() > 0)) {
                _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            }

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[processSOSRechargeRequest]", _transactionID, _sosMSISDN, _sosNetworkCode, "ErrorCode:" + _sosvo.getErrorCode() + " BTSL Base Exception:" + be.getMessage());
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Base Exception " + be.getMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _sosvo.getMessageCode() + "ErrorCode:" + _sosvo.getErrorCode());

        } catch (Exception e) {
            _log.error("processSOSRechargeRequest", "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            _sosvo.setSuccessTxn(false);
            if (!(_sosvo.getTransactionStatus() != null && _sosvo.getTransactionStatus().length() > 0)) {
                _sosvo.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            }

            _sosvo.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _sosvo.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _log.error("processSOSRechargeRequest", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[processSOSRechargeRequest]", _transactionID, _sosMSISDN, _sosNetworkCode, "ErrorCode:" + _sosvo.getErrorCode() + " Exception:" + e.getMessage());
            TransactionLog.log(_transactionID, _requestIDStr, _sosMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Base Exception " + e.getMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _sosvo.getMessageCode() + "ErrorCode:" + _sosvo.getErrorCode());
        } finally {

            try {
                if (BTSLUtil.isNullString(_sosvo.getMessageCode())) {
                    _sosvo.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                }

                if (mcomCon == null) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                }
                try {
                    updateSOSSettlementThroughRecharge(con);
                } catch (BTSLBaseException bex) {
                    _log.errorTrace(METHOD_NAME, bex);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[processSOSRechargeRequest]", _transactionID, _sosMSISDN, _sosNetworkCode, "Not able to update Subscriber Last Details Exception:" + e.getMessage());
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(METHOD_NAME, bex);
                try {
                    if (con != null) {
                        mcomCon.finalRollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
                _log.error("processSOSRechargeRequest", _transactionID, "BTSL Base Exception while updating transfer details in database:" + bex.getMessage());
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                try {
                    if (con != null) {
                        mcomCon.finalRollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
                _log.error("processSOSRechargeRequest", _transactionID, "Exception while updating transfer details in database:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[processSOSRechargeRequest]", _transactionID, _sosMSISDN, _sosNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
            } finally {
                if (con != null) {
                    try {
                       mcomCon.finalCommit();
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                }
				if (mcomCon != null) {
					mcomCon.close("SOSSettlementController#processSOSRechargeRequest");
					mcomCon = null;
				}
                con=null;
            }

            if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_sosvo.getSOSReturnMsg() == null || !((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey())) {
                _sosvo.setSOSReturnMsg(new BTSLMessages((PretupsErrorCodesI.SOS_SETTLEMENT_FAIL), new String[] { String.valueOf(_transactionID), PretupsBL.getDisplayAmount(_sosvo.getDebitAmount()) }));
            }

            if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {

                if (_sosvo.getSOSReturnMsg() == null) {
                    (new PushMessage(_sosMSISDN, getSOSSuccessMessage(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                } else if (_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey()) {
                    BTSLMessages btslRecMessages = (BTSLMessages) _sosvo.getSOSReturnMsg();
                    (new PushMessage(_sosMSISDN, BTSLUtil.getMessage(_locale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                } else {
                    (new PushMessage(_sosMSISDN, (String) _sosvo.getSOSReturnMsg(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                }
            } else if (_failMessageRequired) {
                if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    if (_sosvo.getSOSReturnMsg() == null) {
                        (new PushMessage(_sosMSISDN, getSOSFailMessage(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else if (_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey()) {
                        BTSLMessages btslRecMessages = (BTSLMessages) _sosvo.getSOSReturnMsg();
                        (new PushMessage(_sosMSISDN, BTSLUtil.getMessage(_locale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else {
                        (new PushMessage(_sosMSISDN, (String) _sosvo.getSOSReturnMsg(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    }
                }

                else if (_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    if (_sosvo.getSOSReturnMsg() == null) {
                        (new PushMessage(_sosMSISDN, getReceiverAmbigousMessage(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else if (_sosvo.getSOSReturnMsg() != null && ((BTSLMessages) _sosvo.getSOSReturnMsg()).isKey()) {
                        BTSLMessages btslRecMessages = (BTSLMessages) _sosvo.getSOSReturnMsg();
                        (new PushMessage(_sosMSISDN, BTSLUtil.getMessage(_locale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    } else {
                        (new PushMessage(_sosMSISDN, (String) _sosvo.getSOSReturnMsg(), _transactionID, _sosvo.getRequestGatewayCode(), _locale)).push();
                    }
                }
            }
            TransactionLog.log(_transactionID, _sosvo.getRequestIDStr(), _sosvo.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + _sosvo.getMessageCode());
            if (_log.isDebugEnabled()) {
                _log.debug("processSOSRechargeRequest", "Exiting");
            }
        }

    }

    public void updateSOSSettlementThroughRecharge(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "updateSOSSettlementThroughRecharge";
        if (_log.isDebugEnabled()) {
            _log.debug("updateSOSSettlementThroughRecharge", "Entered with Transfer ID=" + _transactionID + " p_currentDate=" + _sosMSISDN);
        }
        try {
            int updateCounters = 0;
            updateCounters = _sosTxndao.updateSOSSettlementThroughRecharge(p_con, _sosvo);
            if (updateCounters <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[updateSOSSettlementThroughRecharge]", _transactionID, _sosMSISDN, _sosNetworkCode, "Not able to update SOS details in SOS_TXNS_DETAILS table");
                throw new BTSLBaseException("SOSSettlementController", "updateSOSSettlementThroughRecharge", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("updateSOSSettlementThroughRecharge", "Exception :" + be);
            throw new BTSLBaseException("SOSSettlementController", "updateSOSSettlementThroughRecharge", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("updateSOSSettlementThroughRecharge", "Exception :" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlementController[updateSOSSettlementThroughRecharge]", _transactionID, "", "", "Not able to update SOS details,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("SOSSettlementController", "updateSOSSettlementThroughRecharge", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("updateSOSSettlementThroughRecharge", "Exiting for Transfer ID=" + _transactionID);
        }
    }

    private String getSOSRechargeCommonString() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + _sosMSISDN);
        strBuff.append("&TRANSACTION_ID=" + _transactionID);
        strBuff.append("&NETWORK_CODE=" + _sosNetworkCode);
        strBuff.append("&INTERFACE_ID=" + _sosvo.getInterfaceID());
        strBuff.append("&INTERFACE_HANDLER=" + _sosvo.getInterfaceHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeR);
        strBuff.append("&INT_MOD_IP=" + _intModIPR);
        strBuff.append("&INT_MOD_PORT=" + _intModPortR);
        strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameR);
        strBuff.append("&MODULE=" + PretupsI.C2S_MODULE);
        strBuff.append("&USER_TYPE=R");
        strBuff.append("&REQ_SERVICE=" + _serviceType);
        strBuff.append("&CAL_OLD_EXPIRY_DATE=" + _sosvo.getOldExpiryInMillis());
        return strBuff.toString();
    }

    public String getSOSDebitThroughRechargeStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getSOSRechargeCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_DEBIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + _sosvo.getDebitAmount());
        strBuff.append("&LMB_CREDIT_AMT=" + _sosvo.getLmbAmountAtIN());
        strBuff.append("&TRANSACTION_ID=" + _sosvo.getTransactionID());
        strBuff.append("&LMB_DEBIT=" + PretupsI.YES);

        return strBuff.toString();
    }
}
