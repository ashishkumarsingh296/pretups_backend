package com.btsl.pretups.p2p.transfer.requesthandler;

/**
 * @(#)SOSRechargeController.java
 *                                Copyright(c) 2010, Comviva Technologies Ltd.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Abhay Jan 08,2010 Initial Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 */
import java.net.URLDecoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
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
import com.btsl.loadcontroller.LoadController;
import com.btsl.loadcontroller.LoadControllerI;
import com.btsl.loadcontroller.ReqNetworkServiceLoadController;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupBL;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.P2PRequestDailyLog;
import com.btsl.pretups.logging.SOSRequestDailyLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.requesthandler.RegisterationController;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.ResumeSuspendProcess;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingVO;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.sos.businesslogic.SOSVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.subscriber.businesslogic.SubscriberVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.util.BTSLUtil;

public class SOSRechargeController implements ServiceKeywordControllerI, Runnable {

    private static Log _log = LogFactory.getLog(SOSRechargeController.class.getName());

    private P2PTransferVO _p2pTransferVO = null;
    private TransferItemVO _senderTransferItemVO = null;
    private TransferItemVO _receiverTransferItemVO = null;
    private String _senderMSISDN;
    private String _receiverMSISDN;
    private SenderVO _senderVO;
    private ReceiverVO _receiverVO;
    private String _senderSubscriberType;
    private String _senderNetworkCode;
    private Date _currentDate = null;
    private ArrayList _itemList = null;
    private String _intModCommunicationTypeS;
    private String _intModIPS;
    private int _intModPortS;
    private String _intModClassNameS;
    private String _transferID;
    private String _requestIDStr;
    private Locale _senderLocale = null;
    private boolean _isCounterDecreased = false;
    private String _type;
    private String _serviceType;
    private boolean _finalTransferStatusUpdate = true;
    private boolean _decreaseTransactionCounts = false;	
    private boolean _transferDetailAdded = false;
    private boolean _senderInterfaceInfoInDBFound = false;
    private String _senderAllServiceClassID = PretupsI.ALL;
    private String _senderPostBalanceAvailable;
    private String _senderExternalID = null;
    private RequestVO _requestVO = null;
    private boolean _processedFromQueue = false; // Flag to indicate that
    // request has been processed
    // from Queue
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
    private boolean _senderDeletionReqFromSubRouting = false; // Whether to
    // update in
    // Subscriber
    // Routing for
    // sender MSISDN
    private String _senderIMSI = null;
    private static OperatorUtilI _operatorUtil = null;
    private String _senderInterfaceStatusType = null;
    private static int _transactionIDCounter = 0;
    private static int _prevMinut = 0;
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    private String _oldDefaultSelector = null;
    private String _newDefaultSelector = null;
    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
    private boolean _useAlternateCategory = false; // Whether to use alternate
    // interface category
    private String _newInterfaceCategory = null; // The alternate interface
    // category that has to be used
    private String _oldInterfaceCategory = null; // The initial interface

    // category that has to be used

    public SOSRechargeController() {
        _p2pTransferVO = new P2PTransferVO();
        _currentDate = new Date();
    }

    // Loads operator specific class
    static {
        final String METHOD_NAME = "static";
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * This is the main entry method for SOS transactions
     * It calls all other methods based on the process flow.
     * 1. If any subscriber is sending the SOS recharge request thats will come
     * here fro P2Preceiver servlet.
     * 2. Prepare SenderVO and ReceiverVO. Parse the message send in the
     * request.
     * 3. If the service type used has its Type as BOTH then based on the
     * receiver network code and service type,
     * Get the First interface on which the request will be processed.
     * 4. Validate whether the Service is launched at the Network
     * 5. Check whether the Payment method is allowed for the Service against
     * the Sender Subscriber Type
     * 6. Check whether Receiver MSISDN is barred or not.
     * 7. Check subscriber last SOS recharge details.
     * 8. Generate the Transfer ID
     * 9. Populate the interface details for the Series Type and interface
     * Category for VALIDATE action.
     * 10. Based on the Routing Control, Database Check and Series Check are
     * performed to get the Interface ID.
     * 11. Check the transaction Load Counters.
     * 12. Based on the Flow Type decide whether Validation needs to be done in
     * Thread along with topup or before that
     * 13. Perform the Validation and Send Request for Sender on the Interface,
     * If Number was not found on the interface
     * Then perform the alternate routing of the interfaces to validate the
     * same.
     * If Found then check whether if Database Check was Y and Number was
     * initailly not found in DB then insert the
     * same. If not found even after routing then delete the number from routing
     * database if initially had been found.
     * 14. validate the sender eligibility criteria.
     * 15. validate service class checks and transfer rules.
     * 16. Calculate the Card group based on the service class IDs
     * 17. Insert the record in transaction table with status as Under process.
     * 18. Populate the interface details for the Series Type and interface
     * Category for TOPUP action.
     * If Database check was Y then do not fire query for search again in DB,
     * use the earlier loaded interface ID.
     * 19. Send the Sender Credit request
     * 20. If Success make the transaction status as success and send message
     * accordingly.
     * 21. update the transaction table accordingly.
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
        _requestIDStr = p_requestVO.getRequestIDStr();
        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestIDStr, "Entered");
        }
        try {
        	 _requestVO = p_requestVO;
             _senderVO = (SenderVO) p_requestVO.getSenderVO();
        	if (_senderVO == null) {
                new RegisterationController().regsiterNewUser(p_requestVO);
                _senderVO = (SenderVO) p_requestVO.getSenderVO();
                _senderVO.setDefUserRegistration(true);
                _senderVO.setActivateStatusReqd(true);
                p_requestVO.setSenderLocale(new Locale(_senderVO.getLanguage(), _senderVO.getCountry()));
	            if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)).indexOf(p_requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE
	                .equals(p_requestVO.getGroupType())) {
	                // load the user running and profile counters
	                // Check the counters
	                // update the counters
	                final GroupTypeProfileVO groupTypeProfileVO = PretupsBL.loadAndCheckP2PGroupTypeCounters(p_requestVO, PretupsI.GRPT_TYPE_CONTROLLING);
	                // If counters reach the profile limit them throw exception
	                if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
	                    p_requestVO.setDecreaseGroupTypeCounter(false);
	                    final String arr[] = { String.valueOf(groupTypeProfileVO.getThresholdValue()) };
	                    if (PretupsI.GRPT_TYPE_FREQUENCY_DAILY.equals(groupTypeProfileVO.getFrequency())) {
	                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_D, arr);
	                    }
	                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_M, arr);
	                }
	            }
            }
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            // CHANGES FOR LMB BULK UPLOAD
            try {
                SubscriberBL.validityChk(p_requestVO, con);
            } catch (BTSLBaseException bex) {
                _log.errorTrace(METHOD_NAME, bex);
                throw bex;
            }

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMB_BLK_UPL))).booleanValue()) {
                try {
                    SubscriberBL.updateCreditAmtLMB(p_requestVO, con);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    throw be;
                }

            } // CHANGES END HERE
            //_senderVO = prepareSenderVO(p_requestVO);
            _senderLocale = p_requestVO.getSenderLocale();

            _receiverVO = prepareReceiverVO(p_requestVO);
            _p2pTransferVO.setReceiverVO(_receiverVO);
            // If group type counters are allowed to check for controlling for
            // the request gateway then check them
            // This change has been done by ankit on date 14/07/06 for SMS
            // charging
            
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestIDStr, "_senderLocale=" + _senderLocale);
            }
            TransactionLog.log("", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "Received SOS Recharge Request", PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            _type = p_requestVO.getType();
            _serviceType = p_requestVO.getServiceType();

            populateVOFromRequest(p_requestVO);
            // validate message format
            _operatorUtil.checkSOSMessageFormat(con, p_requestVO, _p2pTransferVO);

            if (BTSLUtil.isNullString(new Long(_p2pTransferVO.getRequestedAmount()).toString())) {
                final long sosRechargeAmt = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_RECHARGE_AMOUNT))).longValue();
                _p2pTransferVO.setRequestedAmount(sosRechargeAmt);
            }
            // Block added to avoid decimal amount in credit transfer
            if (!BTSLUtil.isStringIn(_serviceType, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES))) {
                try {
                    final String displayAmt = PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount());
                    Long.parseLong(displayAmt);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INVALID_AMOUNT);
                }
            }
            PretupsBL.getSelectorValueFromCode(p_requestVO);

            // Get the Interface Category routing details based on the receiver
            // Network Code and Service type
            if (_type.equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                    .getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + _senderVO.getSubscriberType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            "process",
                            _requestIDStr,
                            "For =" + _receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO
                                .getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO
                                .getAlternateInterfaceType() + " _oldDefaultSelector=" + _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "_newDefaultSelector= " + _serviceInterfaceRoutingVO
                                .getAlternateDefaultSelectortCode());
                    }

                    _type = _serviceInterfaceRoutingVO.getInterfaceType();
                    _oldInterfaceCategory = _type;
                    _oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                    _useAlternateCategory = _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    _newInterfaceCategory = _serviceInterfaceRoutingVO.getAlternateInterfaceType();
                    _newDefaultSelector = _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
                } else {
                    _log.info("process", _requestIDStr,
                        "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "PrepaidController[process]", "", _senderMSISDN,
                        _senderNetworkCode, "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    _type = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                    // Changed on 27/05/07 for Service Type selector Mapping
                    final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_p2pTransferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        _oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                }
            } else {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                    .getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + _senderVO.getSubscriberType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            "process",
                            _requestIDStr,
                            "For =" + _receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO
                                .getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO
                                .getAlternateInterfaceType() + " _oldDefaultSelector=" + _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "_newDefaultSelector= " + _serviceInterfaceRoutingVO
                                .getAlternateDefaultSelectortCode());
                    }
                    _oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                } else {

                    // Changed on 27/05/07 for Service Type selector Mapping
                    final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_p2pTransferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        _oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                    _log.info("process", _requestIDStr, "Service Interface Routing control Not defined, thus using default Selector=" + _oldDefaultSelector);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PrepaidController[process]", "", _senderMSISDN,
                        _senderNetworkCode, "Service Interface Routing control Not defined, thus using default selector=" + _oldDefaultSelector);
                }
            }

            // changed for CRE_INT_CR00029 by ankit Zindal
            if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", _requestIDStr, "Selector Not found in Incoming Message Thus using Selector as  " + _oldDefaultSelector);
                }
                p_requestVO.setReqSelector(_oldDefaultSelector);
            } else {
                _newDefaultSelector = p_requestVO.getReqSelector();
            }

            // check service payment mapping
            _senderSubscriberType = _senderVO.getSubscriberType();
            // By Default Entry, will be overridden later in the file
            _p2pTransferVO.setTransferCategory(_senderSubscriberType + "-" + _type);
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestIDStr, "Starting with transfer Category as :" + _p2pTransferVO.getTransferCategory());
            }
            _senderNetworkCode = _senderVO.getNetworkCode();
            _senderMSISDN = ((SubscriberVO) _p2pTransferVO.getSenderVO()).getMsisdn();
            _receiverMSISDN = ((SubscriberVO) _p2pTransferVO.getReceiverVO()).getMsisdn();
            _p2pTransferVO.setSubService(p_requestVO.getReqSelector());
            _p2pTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
            _p2pTransferVO.setReceiverNetworkCode(_senderNetworkCode);

            // for ussd
            _p2pTransferVO.setCellId(p_requestVO.getCellId());
            _p2pTransferVO.setSwitchId(p_requestVO.getSwitchId());
            // Validates the network service status
            PretupsBL.validateNetworkService(_p2pTransferVO);

            // check subscriber barred or not
            try {
                PretupsBL.checkMSISDNBarred(con, _senderMSISDN, _senderNetworkCode, _p2pTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                    _p2pTransferVO.setSenderReturnMessage(PretupsErrorCodesI.ERROR_USERBARRED_R);
                }
                throw be;
            }
            // check subscriber details for skey requirement
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED)).booleanValue() && _senderVO.getSkeyRequired().equals(PretupsI.YES)) {
                // Call the method to handle SKey related transfers
                processSKeyGen(con);
            } else {
                processTransfer(con);

                // check subscriber last SOS recharge
                try {
                    final SOSVO sosVO = SubscriberBL.checkMsisdnLastSOSRecharge(con, _senderMSISDN, _senderNetworkCode);
                    if (sosVO != null) {
                        _p2pTransferVO.setLastTransferId(sosVO.getTransactionID());
                        _p2pTransferVO.setLastTransferDateTime(sosVO.getRechargeDateTime());
                    }
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    throw be;
                }
                p_requestVO.setTransactionID(_transferID);
                TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                    PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Generated Transfer ID", PretupsI.TXN_LOG_STATUS_SUCCESS,
                    "Source Type=" + _p2pTransferVO.getSourceType() + " Gateway Code=" + _p2pTransferVO.getRequestGatewayCode());

                // populate payment and service interface details for validate
                // action
                populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

                _p2pTransferVO.setTransferCategory(_senderSubscriberType + "-" + _type);
                _p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
                mcomCon.finalCommit();
				if (mcomCon != null) {
					mcomCon.close("SOSRechargeController#process");
					mcomCon = null;
				}
                con = null;

                // Checks the Various loads
                checkTransactionLoad();
                _decreaseTransactionCounts = true;

                // Checks If flow type is common then validation will be
                // performed before sending the
                // response to user and if it is thread based then validation
                // will also be performed in thread
                // along with topup
                if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
                    // Process validation requests
                    processValidationRequest();
                    p_requestVO.setSenderMessageRequired(_p2pTransferVO.isUnderProcessMsgReq());
                    p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    p_requestVO.setDecreaseLoadCounters(false);
                } else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    // Check if message needs to be sent in case of Thread
                    // implmentation
                    p_requestVO.setSenderReturnMessage(getSndrUPMsgBeforeValidation());
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    final Thread _controllerThread = new Thread(this);
                    _controllerThread.start();
                    p_requestVO.setDecreaseLoadCounters(false);
                } else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST )){
                	p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());
                	p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    processValidationRequest();
                    run();
                	p_requestVO.setDecreaseLoadCounters(false);
                	
                }
                // Parameter set to indicate that instance counters will not be
                // decreased in receiver for this transaction
                p_requestVO.setDecreaseLoadCounters(false);
            }
        }// end of try block of process()
        catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException be:" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setSuccessTxn(false);
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (!BTSLUtil.isNullString(_p2pTransferVO.getSenderReturnMessage())) {
                p_requestVO.setSenderReturnMessage(_p2pTransferVO.getSenderReturnMessage());
            }

            if (be.isKey()) {
                if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                    _p2pTransferVO.setErrorCode(be.getMessageKey());
                }
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
                final String receiverMessage = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                // added for pushing sms through SMSCin case exception comes
                // before process validation 20.07.10
                new PushMessage(p_requestVO.getFilteredMSISDN(), receiverMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO.getLocale())
                    .push();
            } else {
                _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            if (_transferID != null && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            TransactionLog.log(_transferID, _requestIDStr, p_requestVO.getFilteredMSISDN(), "BTSLBaseException in process method", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _p2pTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Message Code=" + p_requestVO
                    .getMessageCode());

        } catch (Exception e) {
            _log.error("process", "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            if (_transferID != null && _decreaseTransactionCounts) {
                _isCounterDecreased = true;
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[process]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                _p2pTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            SOSRequestDailyLog.log(SOSRequestDailyLog.populateSOSRequestDailyLogVO(_requestVO, _p2pTransferVO));
            P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));
        } finally {
            try {
                if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
                }
                if (_transferID != null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || (p_requestVO
                    .getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(
                    PretupsI.TXN_STATUS_UNDER_PROCESS)))) {
                    addSOSRechargeDetails(con);
                } else if (_transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    _log.info("process", _requestIDStr,
                        "Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + _transferID + " But not added entry in Transfers yet");
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                _log.error("process", "BTSL Base Exception:" + be.getMessage());
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("process", "Exception:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SOSRechargeController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            }
            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (_isCounterDecreased) {
                p_requestVO.setDecreaseLoadCounters(false);
            }
            if (con != null) {
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
				if (mcomCon != null) {
					mcomCon.close("SOSRechargeController#process");
					mcomCon = null;
				}
                con = null;
            }
            if (_senderVO.isActivateStatusReqd()) {
                (new PushMessage(_senderMSISDN, getSenderRegistrationMessage(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _senderLocale)).push();
           }
            OneLineTXNLog.log(_p2pTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
            }
        }
    }// end of process()

    
    
    private String getSenderRegistrationMessage() {
        
    	return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS, null);
    }
    /**
     * This method process the S Key based transactions
     * 
     * @param p_con
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processSKeyGen(Connection p_con) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "processSKeyGen";
        if (_log.isDebugEnabled()) {
            _log.debug("processSKeyGen", "Entered");
        }
        try {
            // validate skey details for generation
            // generate skey
            PretupsBL.generateSKey(p_con, _p2pTransferVO);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.error("processSKeyGen", "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[processSKeyGen]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("SOSRechargeController", "processSKeyGen", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("processSKeyGen", "Exiting");
            }
        }
    }

    /**
     * Method to perform validation request
     * 
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequest() throws BTSLBaseException, Exception {
        final String METHOD_NAME = "processValidationRequest";
        if (_log.isDebugEnabled()) {
            _log.debug("processValidationRequest", "Entered and performing validations for transfer ID=" + _transferID);
        }
		Connection con = null;
		MComConnectionI mcomCon = null;
        try {
            final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_p2pTransferVO.getModule(),
                _senderNetworkCode, _p2pTransferVO.getPaymentMethodType());
            _intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
            _intModIPS = networkInterfaceModuleVOS.getIP();
            _intModPortS = networkInterfaceModuleVOS.getPort();
            _intModClassNameS = networkInterfaceModuleVOS.getClassName();

            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

            final CommonClient commonClient = new CommonClient();

            final String requestStr = getSenderValidateStr();

            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr,
                PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            final String senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug("processValidationRequest", "senderValResponse From IN Module=" + senderValResponse);
            }
            _itemList = new ArrayList();

            _itemList.add(_senderTransferItemVO);
            _itemList.add(_receiverTransferItemVO);
            _p2pTransferVO.setTransferItemList(_itemList);
            try {
                // Get the Sender validate response and processes the same
                updateForSenderValidateResponse(senderValResponse);
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                if (_senderDeletionReqFromSubRouting && _senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
                    PretupsBL.deleteSubscriberInterfaceRouting(_senderMSISDN, PretupsI.INTERFACE_CATEGORY_PRE);
                }
                // This block will send different error code if the user is
                // already registered at a particular interface
                // Category but is not found on that interface while validation
                // request
                if (!_senderVO.isDefUserRegistration() && _senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
                    throw new BTSLBaseException("SOSRechargeController", "processValidationRequest", PretupsErrorCodesI.P2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL, 0,
                        new String[] { ((LookupsVO) LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE, _senderVO.getSubscriberType())).getLookupName() }, null);
                }
                throw be;
            }
            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);

            // If request is taking more time till validation of sender than
            // reject the request.
            InterfaceVO interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_senderTransferItemVO.getInterfaceID());
            if ((System.currentTimeMillis() - _p2pTransferVO.getRequestStartTime()) > interfaceVO.getValExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSRechargeController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till validation of sender");
                throw new BTSLBaseException("SOSRechargeController", "processValidationRequest", PretupsErrorCodesI.P2P_ERROR_EXCEPTION_TKING_TIME_TILL_VAL_S);
            }
            interfaceVO = null;

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            try {
                // validate eligibility criteria for SOS recharge
                SubscriberBL.validateSenderEligibilityCriteria(con, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL);
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                throw be;
            }
            // Get the service Class ID based on the code
            PretupsBL.validateServiceClassChecks(con, _senderTransferItemVO, _p2pTransferVO, PretupsI.P2P_MODULE, _requestVO.getServiceType());
            _senderVO.setServiceClassCode(_senderTransferItemVO.getServiceClass());
            _senderVO.setUsingAllServiceClass(_senderTransferItemVO.isUsingAllServiceClass());
            _receiverVO.setServiceClassCode(_senderTransferItemVO.getServiceClass());
            _receiverVO.setUsingAllServiceClass(_senderTransferItemVO.isUsingAllServiceClass());
            if (_log.isDebugEnabled()) {
                _log.debug("processValidationRequest", "CURRENT USER SERVICE CLASS CODE :" + _senderTransferItemVO.getServiceClassCode());
            }
            // Lohit added for SOS check for service class
            final String serviceClassAllowedForSOS = (String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SERV_CLASS_ALLOW_FOR_SOS, _senderVO.getNetworkCode());
            if (!BTSLUtil.isNullString(serviceClassAllowedForSOS) && !serviceClassAllowedForSOS.contains(_senderTransferItemVO.getServiceClassCode())) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSRechargeController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception: User of this Service class is not allowed to do SOS.");
                throw new BTSLBaseException("SOSRechargeController", "run", PretupsErrorCodesI.P2P_SOS_SERVICE_CLASS_NOT_ALLOWED);
            }
            // Lohit check for SOS age on network
            try {
                final int ageOnNetworkDays = BTSLUtil.getDifferenceInUtilDates(_senderVO.getCreationDateString(), _currentDate);
                if (_log.isDebugEnabled()) {
                    _log.debug("processValidationRequest", "Creation date of subscriber found= " + _senderVO.getCreationDateString() + ",age on network=" + ageOnNetworkDays);
                }
                if ((ageOnNetworkDays < 0) || (ageOnNetworkDays < (((Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SOS_MINIMUM_AON, _senderVO
                    .getNetworkCode()))).intValue())) {
                    // String
                    // arr[]={BTSLUtil.getDateStringFromDate(BTSLUtil.addDaysInUtilDate(_senderVO.getCreationDateString(),
                    // ageOnNetworkDays)),
                    // Integer.toString((Integer)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SOS_MINIMUM_AON,_senderVO.getNetworkCode()))};
                    // Issue fixed LMB eligible date was going wrong now message
                    // will show remains days to eligible the like LMB DAYS THAT
                    // REMAINS TO BE ELIGIBLE (DATE THAT SUBSCRIBER WILL BE
                    // ELIGIBLE �CURRENT DATE]
                    final String arr[] = { Integer.toString(((((Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SOS_MINIMUM_AON, _senderVO.getNetworkCode())))
                        .intValue() - ageOnNetworkDays)), Integer.toString((Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SOS_MINIMUM_AON, _senderVO
                        .getNetworkCode())) };
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSRechargeController[run]", _transferID,
                        _senderMSISDN, _senderNetworkCode, "Exception: User does not have minimum age on the network required for SOS.");
                    throw new BTSLBaseException("SOSRechargeController", "run", PretupsErrorCodesI.P2P_SOS_LESS_AON, arr);
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                throw be;
            }

            // validate sender receiver service class,validate transfer value
            PretupsBL.validateTransferRule(con, _p2pTransferVO, PretupsI.P2P_MODULE);

            // calculate sos card group details
            CardGroupBL.calculateSOSCardGroupDetails(con, _p2pTransferVO, PretupsI.P2P_MODULE, true);

            TransactionLog
                .log(
                    _transferID,
                    _requestIDStr,
                    _senderMSISDN,
                    _senderNetworkCode,
                    PretupsI.TXN_LOG_REQTYPE_INT,
                    PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "After Card Group Set Id=" + _p2pTransferVO.getCardGroupSetID() + " Code" + _p2pTransferVO.getCardGroupCode() + " Card ID=" + _p2pTransferVO
                        .getCardGroupID() + " Sender Access fee=" + _p2pTransferVO.getSenderAccessFee() + " Tax1 =" + _p2pTransferVO.getSenderTax1Value() + " Tax2=" + _p2pTransferVO
                        .getSenderTax1Value() + " Talk Time=" + _p2pTransferVO.getSenderTransferValue() + " Receiver Access fee=" + _p2pTransferVO.getReceiverAccessFee() + " Tax1 =" + _p2pTransferVO
                        .getReceiverTax1Value() + " Tax2=" + _p2pTransferVO.getReceiverTax1Value() + " Bonus=" + _p2pTransferVO.getReceiverBonusValue() + " Val Type=" + _p2pTransferVO
                        .getReceiverValPeriodType() + " Validity=" + _p2pTransferVO.getReceiverValidity() + " Talk Time=" + _p2pTransferVO.getReceiverTransferValue(),
                    PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            _requestVO.setSuccessTxn(false);
            // populate payment and service interface details
            populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);

            // update SOS recharge details in database
            PretupsBL.addSOSRechargeDetails(con, _p2pTransferVO);
            _transferDetailAdded = true;

            mcomCon.finalCommit();
			if (mcomCon != null) {
				mcomCon.close("SOSRechargeController#processValidationRequest");
				mcomCon = null;
			}
            con = null;

            // If request is taking more time till credit transfer of subscriber
            // than reject the request.
            interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_senderTransferItemVO.getInterfaceID());
            if ((System.currentTimeMillis() - _p2pTransferVO.getRequestStartTime()) > interfaceVO.getTopUpExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSRechargeController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till credit transfer");
                throw new BTSLBaseException("SOSRechargeController", "run", PretupsErrorCodesI.P2P_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
            }
            interfaceVO = null;

            if (_p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue) {
                // create new Thread
                final Thread _controllerThread = new Thread(this);
                _controllerThread.start();
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            if (con != null) {
            	mcomCon.finalRollback();;
            }

            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _p2pTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            }
            _log.error("SOSRechargeController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            if (con != null) {
            	mcomCon.finalRollback();
            }

            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            _log.error("SOSRechargeController[processValidationRequest]", "Getting Exception:" + e.getMessage());

            throw (BTSLBaseException)e;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("SOSRechargeController#processValidationRequest");
				mcomCon = null;
			}
            con = null;
        }
    }

    /**
     * Process Transfer Request , Genaerates the Transfer ID and populates the
     * Transfer Items VO
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    public void processTransfer(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "processTransfer";
        if (_log.isDebugEnabled()) {
            _log.debug("processTransfer", _p2pTransferVO.getRequestID(), "Entered");
        }
        try {
            _p2pTransferVO.setTransferDate(_currentDate);
            _p2pTransferVO.setTransferDateTime(_currentDate);
            generateTransferID(_p2pTransferVO);
            _transferID = _p2pTransferVO.getTransferID();
            // set sender transfer item details
            setSenderTransferItemVO();
            _p2pTransferVO.setSenderTransferItemVO(_senderTransferItemVO);
            // set receiver transfer item details
            setReceiverTransferItemVO();
            _p2pTransferVO.setReceiverTransferItemVO(_receiverTransferItemVO);
            // Get the product Info based on the service type
            PretupsBL.getProductFromServiceType(p_con, _p2pTransferVO, _serviceType, PretupsI.P2P_MODULE);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (be.isKey()) {
                _p2pTransferVO.setErrorCode(be.getMessageKey());
            } else {
                _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            throw be;
        } catch (Exception e) {
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[processTransfer]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("SOSRechargeController", "processTransfer", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * This method will perform either topup in thread or both validation and
     * topup on thread based on Flow Type
     */
    public void run() {
        final String METHOD_NAME = "run";
        if (_log.isDebugEnabled()) {
            _log.debug("run", _transferID, "Entered");
        }
        BTSLMessages btslMessages = null;
        final boolean onlyDecreaseCounters = false;
		Connection con = null;
		MComConnectionI mcomCon = null;
        try {
            // Perform the validation of parties if Flow type is thread
            if (_p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !_processedFromQueue) {
                processValidationRequestInThread();
            }

            // send validation request for sender
            final CommonClient commonClient = new CommonClient();
            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_TOP);

            final String requestStr = getSenderCreditAdjustStr();

            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP, requestStr,
                PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            final String senderCreditResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                senderCreditResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug("run", _transferID, "senderDebitResponse From IN Module=" + senderCreditResponse);
            }
            try {
                // Get the Sender Debit response and processes the same
                updateForSenderCreditResponse(senderCreditResponse);
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Getting Code=" + _senderVO
                        .getInterfaceResponseCode());
                LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                throw be;
            }
            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
            _senderVO.setTotalConsecutiveFailCount(0);
            _senderVO.setTotalTransfers(_senderVO.getTotalTransfers() + 1);
            _senderVO.setTotalTransferAmount(_senderVO.getTotalTransferAmount() + _senderTransferItemVO.getRequestValue());
            _senderVO.setLastSuccessTransferDate(_currentDate);
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _requestVO.setSuccessTxn(true);
            _p2pTransferVO.setErrorCode(null);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, _senderNetworkCode);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _p2pTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            }
            if (be.isKey() && _p2pTransferVO.getSenderReturnMessage() == null) {
                btslMessages = be.getBtslMessages();
            } else if (_p2pTransferVO.getSenderReturnMessage() == null) {
                _p2pTransferVO.setSenderReturnMessage(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("run", _transferID, "Error Code:" + btslMessages.print());
            }
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _senderNetworkCode);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            _log.error("run", _transferID, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[run]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            btslMessages = new BTSLMessages(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _senderNetworkCode);
            
        } finally {
            try {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
                }
                try {
                    _p2pTransferVO.setModifiedOn(_currentDate);
                    _p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    PretupsBL.updateSOSRechargeDetails(con, _p2pTransferVO);
                } catch (BTSLBaseException bex) {
                    _log.errorTrace(METHOD_NAME, bex);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[run]", _transferID,
                        _senderMSISDN, _senderNetworkCode, "Not able to update Subscriber Last Details Exception:" + e.getMessage());
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
                _log.error("run", _transferID, "BTSL Base Exception while updating transfer details in database:" + bex.getMessage());
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                try {
                    if (con != null) {
                    	mcomCon.finalRollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
                _log.error("run", _transferID, "Exception while updating transfer details in database:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
            }
            if (con != null) {
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
				if (mcomCon != null) {
					mcomCon.close("SOSRechargeController#run");
					mcomCon = null;
				}
                con = null;
            }

            PushMessage pushMessages = null;
            if (!BTSLUtil.isNullString(_p2pTransferVO.getSenderReturnMessage())) {
                pushMessages = (new PushMessage(_senderMSISDN, _p2pTransferVO.getSenderReturnMessage(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _senderLocale));
            } else if (btslMessages != null) {
                // push error message to sender
                pushMessages = (new PushMessage(_senderMSISDN, BTSLUtil.getMessage(_senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs()), _transferID,
                    _p2pTransferVO.getRequestGatewayCode(), _senderLocale));
            } else if (_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                // push success message to sender
                pushMessages = (new PushMessage(_senderMSISDN, getSenderSuccessMessage(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _senderLocale));
            }
            if (pushMessages != null) {
                pushMessages.push();
            }
            P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Transfer Category=" + _p2pTransferVO
                    .getTransferCategory() + " Error Code=" + _p2pTransferVO.getErrorCode() + " Message=" + _p2pTransferVO.getSenderReturnMessage());

            if (_log.isDebugEnabled()) {
                _log.debug("run", _transferID, "Exiting");
            }
        }
    }// end of run()

    /***
     * 
     * Method for subscriber notification message after SOS recharge
     */
    private String getSenderSuccessMessage() {
        String key = null;
        String[] messageArgArray = null;

        if (!"N".equals(_senderPostBalanceAvailable)) {
            messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_senderTransferItemVO
                .getPostBalance()) };
            key = PretupsErrorCodesI.SOS_SUCCESS;
        } else {
            messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
            key = PretupsErrorCodesI.SOS_SUCCESS_WITHOUT_POSTBAL;
        }
        return BTSLUtil.getMessage(_senderLocale, key, messageArgArray);
    }

    /**
     * Populates the Sender Transfer Items VO
     * 
     */
    private void setSenderTransferItemVO() {
        _senderTransferItemVO = new TransferItemVO();
        _senderTransferItemVO.setSNo(1);
        _senderTransferItemVO.setMsisdn(_senderMSISDN);
        _senderTransferItemVO.setRequestValue(_p2pTransferVO.getTransferValue());
        _senderTransferItemVO.setSubscriberType(_senderSubscriberType);
        _senderTransferItemVO.setTransferDate(_currentDate);
        _senderTransferItemVO.setTransferDateTime(_currentDate);
        _senderTransferItemVO.setTransferID(_p2pTransferVO.getTransferID());
        _senderTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
        _senderTransferItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
        _senderTransferItemVO.setEntryDate(_currentDate);
        _senderTransferItemVO.setEntryDateTime(_currentDate);
        _senderTransferItemVO.setEntryType(PretupsI.CREDIT);
        _senderTransferItemVO.setPrefixID(_senderVO.getPrefixID());
    }

    /**
     * Populates the Receiver Transfer Items VO
     * 
     */
    private void setReceiverTransferItemVO() {
        _receiverTransferItemVO = new TransferItemVO();
        _receiverTransferItemVO.setSNo(2);
        _receiverTransferItemVO.setMsisdn(_receiverMSISDN);
        _receiverTransferItemVO.setRequestValue(_p2pTransferVO.getTransferValue());
        _receiverTransferItemVO.setSubscriberType(_type);
        _receiverTransferItemVO.setTransferDate(_currentDate);
        _receiverTransferItemVO.setTransferDateTime(_currentDate);
        _receiverTransferItemVO.setTransferID(_p2pTransferVO.getTransferID());
        _receiverTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
        _receiverTransferItemVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
        _receiverTransferItemVO.setEntryDate(_currentDate);
        _receiverTransferItemVO.setEntryType(PretupsI.CREDIT);
        _receiverTransferItemVO.setPrefixID(_receiverVO.getPrefixID());
        _receiverTransferItemVO.setEntryDateTime(_currentDate);
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
        final String senderNetworkCode = _senderVO.getNetworkCode();
        final long senderPrefixID = _senderVO.getPrefixID();
        boolean isSenderFound = false;
        if (_log.isDebugEnabled()) {
            _log.debug(this, "Getting interface details For Action=" + action + " _senderInterfaceInfoInDBFound=" + _senderInterfaceInfoInDBFound);
            // Avoid searching in the loop again if in validation details was
            // found
            // in database
            // This condition has been changed so that if payment method is not
            // the
            // dafult one then there may be case that default interface will be
            // used
            // for that.
        }

        if (((!_senderInterfaceInfoInDBFound && (_p2pTransferVO.getPaymentMethodKeywordVO() == null || !PretupsI.YES.equals(_p2pTransferVO.getPaymentMethodKeywordVO()
            .getUseDefaultInterface()))) && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
            .equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
            if (_p2pTransferVO.getPaymentMethodKeywordVO() != null && PretupsI.YES.equals(_p2pTransferVO.getPaymentMethodKeywordVO().getUseDefaultInterface())) {
                if (_log.isDebugEnabled()) {
                    _log.debug(this, "For Sender using the Payment Method Default Interface as=" + _p2pTransferVO.getPaymentMethodKeywordVO().getDefaultInterfaceID());
                }
                _senderTransferItemVO.setPrefixID(senderPrefixID);
                _senderTransferItemVO.setInterfaceID(_p2pTransferVO.getPaymentMethodKeywordVO().getDefaultInterfaceID());
                _senderTransferItemVO.setInterfaceHandlerClass(_p2pTransferVO.getPaymentMethodKeywordVO().getHandlerClass());
                _senderAllServiceClassID = _p2pTransferVO.getPaymentMethodKeywordVO().getAllServiceClassId();
                _senderExternalID = _p2pTransferVO.getPaymentMethodKeywordVO().getExternalID();
                _senderInterfaceStatusType = _p2pTransferVO.getPaymentMethodKeywordVO().getStatusType();
                _p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
                _senderTransferItemVO.setInterfaceType(_p2pTransferVO.getPaymentMethodType());
                _p2pTransferVO.setSenderInterfaceStatusType(_senderInterfaceStatusType);
                if (!PretupsI.YES.equals(_p2pTransferVO.getPaymentMethodKeywordVO().getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(_senderInterfaceStatusType)) {
                    if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                        _p2pTransferVO.setSenderReturnMessage(_p2pTransferVO.getPaymentMethodKeywordVO().getLang1Message());
                    } else {
                        _p2pTransferVO.setSenderReturnMessage(_p2pTransferVO.getPaymentMethodKeywordVO().getLang2Message());
                    }
                    throw new BTSLBaseException(this, "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
                }
                isSenderFound = true;
            } else {
                isSenderFound = getInterfaceRoutingDetails(p_con, _senderMSISDN, senderPrefixID, _senderVO.getSubscriberType(), senderNetworkCode, _p2pTransferVO
                    .getServiceType(), _p2pTransferVO.getPaymentMethodType(), PretupsI.USER_TYPE_SENDER, action);
            }
        } else {
            isSenderFound = true;
        }
        if (!isSenderFound) {
            if (!_senderVO.isDefUserRegistration()) {
                throw new BTSLBaseException("SOSRechargeController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL, 0,
                    new String[] { ((LookupsVO) LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE, _senderVO.getSubscriberType())).getLookupName() }, null);
            }
            throw new BTSLBaseException("SOSRechargeController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_NOTFOUND_PAYMENTINTERFACEMAPPING);
        }
    }

    /**
     * Get the sender String to be send to common Client
     * 
     * @return
     */
    private String getSenderCommonString() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + _senderMSISDN);
        strBuff.append("&TRANSACTION_ID=" + _transferID);
        strBuff.append("&NETWORK_CODE=" + _senderVO.getNetworkCode());
        strBuff.append("&INTERFACE_ID=" + _senderTransferItemVO.getInterfaceID());
        strBuff.append("&INTERFACE_HANDLER=" + _senderTransferItemVO.getInterfaceHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeS);
        strBuff.append("&INT_MOD_IP=" + _intModIPS);
        strBuff.append("&INT_MOD_PORT=" + _intModPortS);
        strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameS);
        strBuff.append("&MODULE=" + PretupsI.P2P_MODULE);
        strBuff.append("&USER_TYPE=S");
        strBuff.append("&CARD_GROUP_SELECTOR=" + _requestVO.getReqSelector());
        strBuff.append("&REQ_SERVICE=" + _serviceType);
        strBuff.append("&INT_ST_TYPE=" + _p2pTransferVO.getSenderInterfaceStatusType());

        return strBuff.toString();
    }

    /**
     * Gets the sender validate Request String
     * 
     * @return
     */
    public String getSenderValidateStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getSenderCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
        strBuff.append("&SERVICE_CLASS=" + _senderTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + _senderTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + _senderTransferItemVO.getAccountStatus());
        strBuff.append("&CREDIT_LIMIT=" + _senderTransferItemVO.getPreviousBalance());
        strBuff.append("&SERVICE_TYPE=" + _senderSubscriberType + "-" + _type);
        return strBuff.toString();
    }

    /**
     * Method to handle sender validation request
     * This method will perform the Alternate interface routing is mobile is not
     * found on the interface
     * If not found on any interface then raise error
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForSenderValidateResponse(String str) throws BTSLBaseException {
        final String METHOD_NAME = "updateForSenderValidateResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        ArrayList altList = null;
        boolean isRequired = false;

        if (null != map.get("IN_START_TIME")) {
            _requestVO.setValidationSenderRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            _requestVO.setValidationSenderResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
        }

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _senderTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        // If we get the MSISDN not found on interface error then perform
        // interface routing
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
            _senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(_senderTransferItemVO.getInterfaceID());
            if (altList != null && !altList.isEmpty()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("updateForSenderValidateResponse",
                        "Got Status=" + status + " After validation Request For MSISDN=" + _senderMSISDN + " Performing Alternate Routing");
                }
                performSenderAlternateRouting(altList); // Method to perform the
                // sender interface
                // routing for
                // validation
            } else {
                isRequired = true;
            }
        }
        if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
            _senderTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
            _senderTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
            _senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
            _senderTransferItemVO.setValidationStatus(status);
            _senderVO.setInterfaceResponseCode(_senderTransferItemVO.getInterfaceResponseCode());

            if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
                try {
                    _senderTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
            _senderTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

            String[] strArr = null;
            if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                _p2pTransferVO.setErrorCode(status + "_S");
                _senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                _senderTransferItemVO.setTransferStatus(status);
                strArr = new String[] { _senderMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
                throw new BTSLBaseException("SOSRechargeController", "updateForSenderValidateResponse", _p2pTransferVO.getErrorCode(), 0, strArr, null);
            }
            _senderTransferItemVO.setTransferStatus(status);
            _senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
                .getRoutingControlDetails(_p2pTransferVO.getNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _p2pTransferVO.getPaymentMethodType());
            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_senderVO.getSubscriberType()) && !_senderDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO
                .isDatabaseCheckBool()) {
                PretupsBL.insertSubscriberInterfaceRouting(_senderTransferItemVO.getInterfaceID(), _senderExternalID, _senderMSISDN, _p2pTransferVO.getPaymentMethodType(),
                    _senderVO.getUserID(), _currentDate);
                _senderInterfaceInfoInDBFound = true;
                _senderDeletionReqFromSubRouting = true;
            }

            try {
                _senderTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                _senderTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _senderTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));// @@ankuj
            // changed
            // due
            // to
            // exception

            try {
                _senderTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _senderTransferItemVO.setBalanceCheckReq(false);
            }
            _senderVO.setCreditLimit(_senderTransferItemVO.getPreviousBalance());

            // Update the Previous Balance in case of Post Paid Offline
            // interface with Credit Limit - Monthly Transfer Amount
            if (_senderVO.isPostOfflineInterface()) {
                final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(_senderVO.getLastSuccessTransferDate(), _currentDate, BTSLUtil.PERIOD_MONTH);
                if (!isPeriodChange) {
                    _senderTransferItemVO.setPreviousBalance(_senderTransferItemVO.getPreviousBalance() - _senderVO.getMonthlyTransferAmount());
                }
            }
            _senderTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
            _senderTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));
            // Lohit for sos aon
            _senderVO.setCreationDateString(new Date(Long.parseLong((String) map.get("AON"))));
            try {
                _senderTransferItemVO.setLmbAllowedBal(Double.parseDouble((String) map.get("LMB_ALLOWED_VALUE")));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            _senderTransferItemVO.setOldExpiryInMillis((String) map.get("CAL_OLD_EXPIRY_DATE"));

            // set here in VO for LMBFLAG also Lohit
            final String balanceArray[] = ((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.SOS_ELIBILITY_ACCOUNT, _senderVO.getNetworkCode())).toString()
                .split(",");
            _senderTransferItemVO.setBalanceMap(new HashMap());
            for (int i = 0; i < balanceArray.length; i++) {
                if (!BTSLUtil.isNullString((String) map.get(balanceArray[i] + "_RESP_BALANCE"))) {
                    _senderTransferItemVO.getBalanceMap().put(balanceArray[i], (String) map.get(balanceArray[i] + "_RESP_BALANCE"));
                }
            }
            System.out.println("Balance Ma at controllerp: " + _senderTransferItemVO.getBalanceMap());
        }
    }

    /**
     * Method to handle Sender Debit Response
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForSenderCreditResponse(String str) throws BTSLBaseException {
        final String METHOD_NAME = "updateForSenderCreditResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        final String lmbStatus = (String) map.get("LMB_TRANSACTION_STATUS");
        if (null != map.get("IN_START_TIME")) {
            _requestVO.setTopUPSenderRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            _requestVO.setTopUPSenderResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
        }

        _senderTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        _senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        _senderTransferItemVO.setUpdateStatus(status);
        _senderVO.setInterfaceResponseCode(_senderTransferItemVO.getInterfaceResponseCode());
        _senderPostBalanceAvailable = ((String) map.get("POST_BALANCE_ENQ_SUCCESS"));

        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
            try {
                _senderTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        }

        _senderTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

        String[] strArr = null;

        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            _p2pTransferVO.setErrorCode(status + "_S");
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _senderTransferItemVO.setTransferStatus(status);
            strArr = new String[] { _senderMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
            throw new BTSLBaseException(this, "updateForSenderDebitResponse", _p2pTransferVO.getErrorCode(), 0, strArr, null);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
            _p2pTransferVO.setErrorCode(status + "_S");
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _senderTransferItemVO.setTransferStatus(status);
            _senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _senderTransferItemVO.setUpdateStatus(status);
            strArr = new String[] { _transferID, _senderTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForSenderDebitResponse", PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        } else {
            _senderTransferItemVO.setTransferStatus(status);
            _senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _senderTransferItemVO.setUpdateStatus(status);
        }
        try {
            _p2pTransferVO.setLmbCreditUpdateStatus(lmbStatus);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
            try {
                _senderTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                _senderTransferItemVO.setNewGraceDate(BTSLUtil.getDateFromDateString((String) map.get("NEW_GRACE_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                _senderTransferItemVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
        }
    }

    /**
     * Method to check the various level of loads whether request can be passed
     * or not
     * 
     * @throws BTSLBaseException
     */
    private void checkTransactionLoad() throws BTSLBaseException {
        final String METHOD_NAME = "checkTransactionLoad";
        if (_log.isDebugEnabled()) {
            _log.debug("checkTransactionLoad", "Checking load for transfer ID=" + _transferID);
        }
        try {
            _requestVO.setPerformIntfceCatRoutingBeforeVal(_performIntfceCatRoutingBeforeVal);
            _p2pTransferVO.setRequestVO(_requestVO);
            _p2pTransferVO.setSenderTransferItemVO(_senderTransferItemVO);
            _requestVO.setSenderDeletionReqFromSubRouting(_senderDeletionReqFromSubRouting);
            _requestVO.setSenderInterfaceInfoInDBFound(_senderInterfaceInfoInDBFound);
            _requestVO.setInterfaceCatRoutingDone(_interfaceCatRoutingDone);

            final int senderLoadStatus = LoadController.checkInterfaceLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), _senderTransferItemVO.getInterfaceID(),
                _transferID, _p2pTransferVO, true);
            // Further process the request
            if (senderLoadStatus == 0) {
                try {
                    LoadController.checkTransactionLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), _senderTransferItemVO.getInterfaceID(),
                        PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
                } catch (BTSLBaseException e) {
                    _log.errorTrace(METHOD_NAME, e);
                    // Decreasing interface load of receiver which we had
                    // incremented before 27/09/06, sender was decreased in the
                    // method
                    LoadController.decreaseTransactionInterfaceLoad(_transferID, ((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(),
                        LoadControllerI.DEC_LAST_TRANS_COUNT);
                    throw e;
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("SOSRechargeController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
                }
            }
            // Request in Queue
            else if (senderLoadStatus == 1) {
                final String strArr[] = { _senderMSISDN, String.valueOf(_p2pTransferVO.getRequestedAmount()) };
                throw new BTSLBaseException("SOSRechargeController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("SOSRechargeController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("SOSRechargeController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("SOSRechargeController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
            throw new BTSLBaseException("SOSRechargeController", "checkTransactionLoad", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * This method will check the transaction load on the given interface
     * 
     * @param p_userType
     * @param p_interfaceID
     * @throws BTSLBaseException
     */
    private void checkTransactionLoad(String p_userType, String p_interfaceID) throws BTSLBaseException {
        final String METHOD_NAME = "checkTransactionLoad";
        if (_log.isDebugEnabled()) {
            _log.debug("checkTransactionLoad", "Checking load for transfer ID=" + _transferID + " on interface=" + p_interfaceID);
        }
        try {
            if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
                final int senderLoadStatus = LoadController.checkInterfaceLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), p_interfaceID, _transferID,
                    _p2pTransferVO, true);
                // Further process the request
                if (senderLoadStatus == 0) {
                    try {
                        LoadController.checkTransactionLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), p_interfaceID, PretupsI.P2P_MODULE, _transferID, true,
                            LoadControllerI.USERTYPE_SENDER);
                    } catch (BTSLBaseException e) {
                        _log.errorTrace(METHOD_NAME, e);
                        throw e;
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("SOSRechargeController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
                    }
                }
                // Request in Queue
                else if (senderLoadStatus == 1) {
                    final String strArr[] = { _senderMSISDN, String.valueOf(_p2pTransferVO.getRequestedAmount()) };
                    throw new BTSLBaseException("SOSRechargeController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
                }
                // Refuse the request
                else {
                    throw new BTSLBaseException("SOSRechargeController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("SOSRechargeController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("SOSRechargeController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
            throw new BTSLBaseException("SOSRechargeController", "checkTransactionLoad", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * Method to populate transfer VO from request VO
     * 
     * @param p_requestVO
     */
    private void populateVOFromRequest(RequestVO p_requestVO) {
        _p2pTransferVO.setSenderVO(_senderVO);
        _p2pTransferVO.setRequestID(p_requestVO.getRequestIDStr());
        _p2pTransferVO.setModule(p_requestVO.getModule());
        _p2pTransferVO.setInstanceID(p_requestVO.getInstanceID());
        _p2pTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        _p2pTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        _p2pTransferVO.setServiceType(p_requestVO.getServiceType());
        _p2pTransferVO.setSourceType(p_requestVO.getSourceType());
        _p2pTransferVO.setCreatedOn(_currentDate);
        _p2pTransferVO.setCreatedBy(PretupsI.SYSTEM_USER);
        _p2pTransferVO.setModifiedOn(_currentDate);
        _p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
        _p2pTransferVO.setTransferDate(_currentDate);
        _p2pTransferVO.setTransferDateTime(_currentDate);
        _p2pTransferVO.setSenderMsisdn(_senderVO.getMsisdn());
        _p2pTransferVO.setSenderID(_senderVO.getUserID());
        _p2pTransferVO.setNetworkCode(_senderVO.getNetworkCode());
        _p2pTransferVO.setLocale(_senderLocale);
        _p2pTransferVO.setLanguage(_p2pTransferVO.getLocale().getLanguage());
        _p2pTransferVO.setCountry(_p2pTransferVO.getLocale().getCountry());
        _p2pTransferVO.setMsgGatewayFlowType(p_requestVO.getMessageGatewayVO().getFlowType());
        _p2pTransferVO.setMsgGatewayResponseType(p_requestVO.getMessageGatewayVO().getResponseType());
        _p2pTransferVO.setMsgGatewayTimeOutValue(p_requestVO.getMessageGatewayVO().getTimeoutValue());
        if(p_requestVO.getRequestMap() != null)
		{
		_p2pTransferVO.setInfo1(p_requestVO.getRequestMap().get("INFO1") != null ? (String)p_requestVO.getRequestMap().get("INFO1") : "");
		_p2pTransferVO.setInfo2(p_requestVO.getRequestMap().get("INFO2") != null ? (String)p_requestVO.getRequestMap().get("INFO2") : "");
		_p2pTransferVO.setInfo3(p_requestVO.getRequestMap().get("INFO3") != null ? (String)p_requestVO.getRequestMap().get("INFO3") : "");
		_p2pTransferVO.setInfo4(p_requestVO.getRequestMap().get("INFO4") != null ? (String)p_requestVO.getRequestMap().get("INFO4") : "");
		_p2pTransferVO.setInfo5(p_requestVO.getRequestMap().get("INFO5") != null ? (String)p_requestVO.getRequestMap().get("INFO5") : "");
		}
    }

    /**
     * Method to perform validation in thread
     * 
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequestInThread() throws BTSLBaseException, Exception {
        final String METHOD_NAME = "processValidationRequestInThread";
        if (_log.isDebugEnabled()) {
            _log.debug("processValidationRequestInThread", "Entered and performing validations for transfer ID=" + _transferID);
        }
        try {
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            processValidationRequest();
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("SOSRechargeController[processValidationRequestInThread]", "Getting BTSL Base Exception:" + be.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            _log.error(this, _transferID, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[run]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
            throw new BTSLBaseException(this, "processValidationRequestInThread", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_transferID != null && !_transferDetailAdded) {
                Connection con = null;MComConnectionI mcomCon = null;
                try {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    addSOSRechargeDetails(con);
                    if (_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        _finalTransferStatusUpdate = false; // No need to update
                        // the status of
                        // transaction in
                        // run method
                    }
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    _log.error("processValidationRequestInThread", "BTSLBaseException:" + be.getMessage());
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error("processValidationRequestInThread", "Exception:" + e.getMessage());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[process]",
                        _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
                } finally {
					if (mcomCon != null) {
						mcomCon.close("SOSRechargeController#processValidationRequestInThread");
						mcomCon = null;
					}
                    con = null;
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("processValidationRequestInThread", "Exiting");
            }
        }
    }

    /**
     * Get the sender Credit Back Adjust String
     * 
     * @return
     */
    public String getSenderCreditAdjustStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getSenderCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_CREDIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + _senderTransferItemVO.getTransferValue());
        strBuff.append("&CARD_GROUP=" + _p2pTransferVO.getCardGroupCode());
        strBuff.append("&SERVICE_CLASS=" + _senderTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + _senderTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + _senderTransferItemVO.getAccountStatus());
        strBuff.append("&SOURCE_TYPE=" + _p2pTransferVO.getSourceType());
        strBuff.append("&PRODUCT_CODE=" + _p2pTransferVO.getProductCode());
        strBuff.append("&TAX_AMOUNT=" + (_p2pTransferVO.getSenderTax1Value() + _p2pTransferVO.getSenderTax2Value()));
        strBuff.append("&ACCESS_FEE=" + _p2pTransferVO.getSenderAccessFee());
        strBuff.append("&SENDER_MSISDN=" + _senderMSISDN);
        strBuff.append("&EXTERNAL_ID=" + _senderExternalID);
        strBuff.append("&GATEWAY_CODE=" + _requestVO.getRequestGatewayCode());
        strBuff.append("&GATEWAY_TYPE=" + _requestVO.getRequestGatewayType());
        strBuff.append("&IMSI=" + BTSLUtil.NullToString(_senderIMSI));
        strBuff.append("&SENDER_ID=" + ((SenderVO) _p2pTransferVO.getSenderVO()).getUserID());
        strBuff.append("&SERVICE_TYPE=" + _senderSubscriberType + "-" + _type);
        if (String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE).equals(_requestVO.getReqSelector())) {
            strBuff.append("&ADJUST=Y");
        }
        strBuff.append("&INTERFACE_PREV_BALANCE=" + _senderTransferItemVO.getPreviousBalance());
        strBuff.append("&REQUESTED_AMOUNT=" + _p2pTransferVO.getRequestedAmount());
        // Lohit
        strBuff.append("&CAL_OLD_EXPIRY_DATE=" + _senderTransferItemVO.getOldExpiryInMillis());
        strBuff.append("&VALIDITY_DAYS=" + _p2pTransferVO.getValidityDaysToExtend());
        strBuff.append("&LMB_DEBIT=" + PretupsI.YES);
        strBuff.append("&LMB_CREDIT_AMT=" + Double.toString(_p2pTransferVO.getSenderSettlementValue() - _senderTransferItemVO.getLmbAllowedBal()));
        return strBuff.toString();
    }

    /**
     * Method to get the success message to be sent to sender
     * 
     * @return
     */
    private String getSenderUnderProcessMessage() {
        final String[] messageArgArray = { _senderMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL
            .getDisplayAmount(_p2pTransferVO.getSenderTransferValue()), PretupsBL.getDisplayAmount(_p2pTransferVO.getSenderAccessFee()) };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.P2P_SENDER_UNDERPROCESS, messageArgArray);
    }

    /**
     * Method that will add entry in Transfer Table if not added else update the
     * records
     * 
     * @param p_con
     */
    private void addSOSRechargeDetails(Connection p_con) {
        final String METHOD_NAME = "addSOSRechargeDetails";
        try {
            if (!_transferDetailAdded) {
                PretupsBL.addSOSRechargeDetails(p_con, _p2pTransferVO);// add
            } else if (_transferDetailAdded) {
                _p2pTransferVO.setModifiedOn(new Date());
                _p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                PretupsBL.updateSOSRechargeDetails(p_con, _p2pTransferVO);// add
                // transfer
                // details
                // in
                // database
            }
            p_con.commit();
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            if (!_isCounterDecreased && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            _log.error("addEntryInTransfers", _transferID, "BTSLBaseException while adding transfer details in database:" + be.getMessage());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[process]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            if (!_isCounterDecreased && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            _log.error("addEntryInTransfers", _transferID, "Exception while adding transfer details in database:" + e.getMessage());
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
            _log.debug(
                "getInterfaceRoutingDetails",
                _requestIDStr,
                " Entered with MSISDN=" + p_msisdn + " Prefix ID=" + p_prefixID + " p_subscriberType=" + p_subscriberType + " p_networkCode=" + p_networkCode + " p_serviceType=" + p_serviceType + " p_interfaceCategory=" + p_interfaceCategory + " p_userType=" + p_userType + " p_action=" + p_action);
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
        _performIntfceCatRoutingBeforeVal = false; // Set so that receiver flag
        // is not overridden by
        // sender flag
        final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
            .getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
        try {
            if (subscriberRoutingControlVO != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("getInterfaceRoutingDetails", _transferID, " p_userType=" + p_userType + " Database Check Required=" + subscriberRoutingControlVO
                        .isDatabaseCheckBool() + " Series Check Required=" + subscriberRoutingControlVO.isSeriesCheckBool());
                }

                if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)) {
                        final ListValueVO listValueVO = PretupsBL.validateNumberInRoutingDatabase(p_con, p_msisdn, p_interfaceCategory);
                        if (listValueVO != null) {
                            isSuccess = true;

                            setInterfaceDetails(p_prefixID, p_userType, listValueVO, false, null, null);

                            if (p_userType.equals(PretupsI.USER_TYPE_SENDER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                _senderInterfaceInfoInDBFound = true;
                                _senderDeletionReqFromSubRouting = true;
                            }
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("getInterfaceRoutingDetails", _transferID,
                                    " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " not found in Database , performing Series Check for Prefix ID=" + p_prefixID);
                            }
                            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            try {
                                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                                    interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache
                                        .getObject(_serviceType + "_" + _p2pTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                                    if (interfaceMappingVO1 != null) {
                                        isSuccess = true;
                                        setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
                                    }
                                }
                                if (interfaceMappingVO1 == null) {
                                    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                                    isSuccess = true;
                                    setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
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
                    }
                } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("getInterfaceRoutingDetails", _transferID,
                            " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " performing Series Check for Prefix ID=" + p_prefixID);
                    }
                    ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                    MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                    try {
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                            interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _p2pTransferVO
                                .getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                            if (interfaceMappingVO1 != null) {
                                isSuccess = true;
                                setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
                            }
                        }
                        if (interfaceMappingVO1 == null) {
                            interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                            isSuccess = true;
                            setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
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
                    _log.debug(
                        "getInterfaceRoutingDetails",
                        _transferID,
                        " By default carrying out series check as routing control not defined for p_userType=" + p_userType + " MSISDN =" + p_msisdn + " performing Series Check for Prefix ID=" + p_prefixID);
                }
                // This event is raised by ankit Z on date 3/8/06 for case when
                // entry not found in routing control and considering series
                // based routing
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSRechargeController[getInterfaceRoutingDetails]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Exception:Routing control information not defined so performing series based routing");
                ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                try {
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                        interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _p2pTransferVO
                            .getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                        if (interfaceMappingVO1 != null) {
                            isSuccess = true;
                            setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
                        }
                    }
                    if (interfaceMappingVO1 == null) {
                        interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                        isSuccess = true;
                        setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
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

            if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                _senderTransferItemVO.setInterfaceType(p_interfaceCategory);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                _senderTransferItemVO.setInterfaceType(p_interfaceCategory);
            }
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[getInterfaceRoutingDetails]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            isSuccess = false;
            throw new BTSLBaseException(this, "getInterfaceRoutingDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getInterfaceRoutingDetails", _requestIDStr, " Exiting with isSuccess=" + isSuccess + "_senderAllServiceClassID=" + _senderAllServiceClassID);
        }
        return isSuccess;
    }

    /**
     * Method to perform the sender alternate intreface routing controls
     * 
     * @param altList
     * @throws BTSLBaseException
     */
    private void performSenderAlternateRouting(ArrayList altList) throws BTSLBaseException {
        final String METHOD_NAME = "performSenderAlternateRouting";
        if (_log.isDebugEnabled()) {
            _log.debug("performSenderAlternateRouting", _requestIDStr, " Entered ");
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
                String senderValResponse = null;
                switch (altList.size()) {
                case 1: {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(_transferID, _p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(_senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null, null);

                    checkTransactionLoad(PretupsI.USER_TYPE_SENDER, _senderTransferItemVO.getInterfaceID());

                    requestStr = getSenderValidateStr();
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + _senderMSISDN + " on ALternate Routing 1 to =" + _senderTransferItemVO
                            .getInterfaceID());
                    }

                    senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(senderValResponse, 1, altList.size());
                        if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
                            // Update in DB for routing interface
                            updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, _p2pTransferVO.getNetworkCode(), _senderTransferItemVO.getInterfaceID(),
                                _senderExternalID, _senderMSISDN, _p2pTransferVO.getPaymentMethodType(), _senderVO.getUserID(), _currentDate);
                        }
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        throw be;
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the sender alternate intreface routing controls when alt size is 1.");
                    }

                    break;
                }
                case 2: {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(_transferID, _p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(_senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null, null);

                    checkTransactionLoad(PretupsI.USER_TYPE_SENDER, _senderTransferItemVO.getInterfaceID());

                    requestStr = getSenderValidateStr();

                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + _senderMSISDN + " on ALternate Routing 1 to =" + _senderTransferItemVO
                            .getInterfaceID());
                    }

                    senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(senderValResponse, 1, altList.size());
                        if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
                            // Update in DB for routing interface
                            updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, _p2pTransferVO.getNetworkCode(), _senderTransferItemVO.getInterfaceID(),
                                _senderExternalID, _senderMSISDN, _p2pTransferVO.getPaymentMethodType(), _senderVO.getUserID(), _currentDate);

                        }
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(
                                    "performSenderAlternateRouting",
                                    "Got Status=" + InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND + " After validation Request For MSISDN=" + _senderMSISDN + " Performing Alternate Routing to 2");
                            }

                            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(_transferID, _p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(1);

                            setInterfaceDetails(_senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null, null);

                            checkTransactionLoad(PretupsI.USER_TYPE_SENDER, _senderTransferItemVO.getInterfaceID());

                            requestStr = getSenderValidateStr();

                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            if (_log.isDebugEnabled()) {
                                _log.debug("performSenderAlternateRouting",
                                    "Sending Request For MSISDN=" + _senderMSISDN + " on ALternate Routing 2 to =" + _senderTransferItemVO.getInterfaceID());
                            }

                            senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                                senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                senderValidateResponse(senderValResponse, 2, altList.size());
                                if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
                                    // Update in DB for routing interface
                                    updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, _p2pTransferVO.getNetworkCode(), _senderTransferItemVO.getInterfaceID(),
                                        _senderExternalID, _senderMSISDN, _p2pTransferVO.getPaymentMethodType(), _senderVO.getUserID(), _currentDate);

                                }
                            } catch (BTSLBaseException bex) {
                                _log.errorTrace(METHOD_NAME, bex);
                                throw bex;
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the sender alternate intreface routing controls.");
                            }
                        } else {
                            throw be;
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing the sender alternate intreface routing controls when alt size is 2.");
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[performSenderAlternateRouting]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performSenderAlternateRouting", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("performSenderAlternateRouting", _requestIDStr, " Exiting ");
            }
        }

    }

    /**
     * Method to handle sender validation response for interface routing
     * 
     * @param str
     * @param p_attempt
     * @param p_altSize
     * @throws BTSLBaseException
     */
    public void senderValidateResponse(String str, int p_attempt, int p_altSize) throws BTSLBaseException {
        final String METHOD_NAME = "senderValidateResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _senderTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        // This has been done so that when Alternate routing has to be performed
        // and when we have to get out and throw error
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) {
            throw new BTSLBaseException(this, "senderValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        }
        _senderTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        _senderTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
        _senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        _senderTransferItemVO.setValidationStatus(status);
        _senderVO.setInterfaceResponseCode(_senderTransferItemVO.getInterfaceResponseCode());
        _senderTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
        _senderTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        String[] strArr = null;

        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            _p2pTransferVO.setErrorCode(status + "_S");
            _senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _senderTransferItemVO.setTransferStatus(status);
            strArr = new String[] { _senderMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
            throw new BTSLBaseException(this, "senderValidateResponse", PretupsErrorCodesI.P2P_SENDER_FAIL, 0, strArr, null);
        }

        _senderTransferItemVO.setTransferStatus(status);
        _senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

        try {
            _senderTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        try {
            _senderTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;

        _senderTransferItemVO.setServiceClassCode(URLDecoder.decode((String) map.get("SERVICE_CLASS")));

        try {
            _senderTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _senderTransferItemVO.setBalanceCheckReq(false);
        }
        _senderVO.setCreditLimit(_senderTransferItemVO.getPreviousBalance());

        // Update the Previous Balance in case of Post Paid Offline interface
        // with Credit Limit - Monthly Transfer Amount
        if (_senderVO.isPostOfflineInterface()) {
            final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(_senderVO.getLastSuccessTransferDate(), _currentDate, BTSLUtil.PERIOD_MONTH);
            if (!isPeriodChange) {
                _senderTransferItemVO.setPreviousBalance(_senderTransferItemVO.getPreviousBalance() - _senderVO.getMonthlyTransferAmount());
            }
        }

        _senderTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
        _senderTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));
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
    private void setInterfaceDetails(long p_prefixID, String p_userType, ListValueVO p_listValueVO, boolean p_useInterfacePrefixVO, MSISDNPrefixInterfaceMappingVO p_MSISDNPrefixInterfaceMappingVO, ServiceSelectorInterfaceMappingVO p_serviceSelectorInterfaceMappingVO) throws BTSLBaseException {
        final String METHOD_NAME = "setInterfaceDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(
                "setInterfaceDetails",
                _requestIDStr,
                " Entered p_prefixID=" + p_prefixID + " p_listValueVO=" + p_listValueVO + " p_useInterfacePrefixVO=" + p_useInterfacePrefixVO + " p_MSISDNPrefixInterfaceMappingVO=" + p_MSISDNPrefixInterfaceMappingVO + "ServiceSelectorInterfaceMappingVO=" + p_serviceSelectorInterfaceMappingVO);
        }
        try {
            String interfaceID = null;
            String interfaceHandlerClass = null;
            String allServiceClassID = null;
            String externalID = null;
            String status = null;
            String message1 = null;
            String message2 = null;
            String interfaceStatusTy = null;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue() && p_serviceSelectorInterfaceMappingVO != null) {
                interfaceID = p_serviceSelectorInterfaceMappingVO.getInterfaceID();
                interfaceHandlerClass = p_serviceSelectorInterfaceMappingVO.getHandlerClass();
                allServiceClassID = p_serviceSelectorInterfaceMappingVO.getAllServiceClassID();
                externalID = p_serviceSelectorInterfaceMappingVO.getExternalID();
                status = p_serviceSelectorInterfaceMappingVO.getInterfaceStatus();
                message1 = p_serviceSelectorInterfaceMappingVO.getLanguage1Message();
                message2 = p_serviceSelectorInterfaceMappingVO.getLanguage2Message();
                interfaceStatusTy = p_serviceSelectorInterfaceMappingVO.getStatusType();
            }
            if (p_useInterfacePrefixVO && p_serviceSelectorInterfaceMappingVO == null) {
                interfaceID = p_MSISDNPrefixInterfaceMappingVO.getInterfaceID();
                interfaceHandlerClass = p_MSISDNPrefixInterfaceMappingVO.getHandlerClass();
                allServiceClassID = p_MSISDNPrefixInterfaceMappingVO.getAllServiceClassID();
                externalID = p_MSISDNPrefixInterfaceMappingVO.getExternalID();
                status = p_MSISDNPrefixInterfaceMappingVO.getInterfaceStatus();
                message1 = p_MSISDNPrefixInterfaceMappingVO.getLanguage1Message();
                message2 = p_MSISDNPrefixInterfaceMappingVO.getLanguage2Message();
                interfaceStatusTy = p_MSISDNPrefixInterfaceMappingVO.getStatusType();
            } else if (p_serviceSelectorInterfaceMappingVO == null) {
                interfaceID = p_listValueVO.getValue();
                interfaceHandlerClass = p_listValueVO.getLabel();
                allServiceClassID = p_listValueVO.getTypeName();
                externalID = p_listValueVO.getIDValue();
                status = p_listValueVO.getStatus();
                message1 = p_listValueVO.getOtherInfo();
                message2 = p_listValueVO.getOtherInfo2();
                interfaceStatusTy = p_listValueVO.getStatusType();
            }

            if (p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                _senderTransferItemVO.setPrefixID(p_prefixID);
                _senderTransferItemVO.setInterfaceID(interfaceID);
                _senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                _senderAllServiceClassID = allServiceClassID;
                _senderExternalID = externalID;
                _senderInterfaceStatusType = interfaceStatusTy;
                _p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
                _p2pTransferVO.setSenderInterfaceStatusType(_senderInterfaceStatusType);

            }
            // Check if interface status is Active or not.
            if (!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceStatusTy)) {
                // ChangeID=LOCALEMASTER
                // which language message to be set is determined from the
                // locale master table for the requested locale
                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                    _p2pTransferVO.setSenderReturnMessage(message1);
                } else {
                    _p2pTransferVO.setSenderReturnMessage(message2);
                }
                throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("setInterfaceDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSRechargeController[setInterfaceDetails]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("setInterfaceDetails", _requestIDStr, " Exiting with Sender Interface ID=" + _senderTransferItemVO.getInterfaceID());
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
            _log.debug(
                "updateSubscriberRoutingDetails",
                _requestIDStr,
                " Entered p_userType=" + p_userType + " p_networkCode=" + p_networkCode + " p_interfaceID=" + p_interfaceID + " p_externalID=" + p_externalID + " p_msisdn=" + p_msisdn + " p_interfaceCategory=" + p_interfaceCategory + " p_userID=" + p_userID + " p_currentDate=" + p_currentDate);
        }
        try {
            boolean updationReqd = false;
            if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
                updationReqd = _senderDeletionReqFromSubRouting;
            }
            if (updationReqd) {
                PretupsBL.updateSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
            } else {
                final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + _p2pTransferVO
                    .getServiceType() + "_" + p_interfaceCategory);
                if (!updationReqd && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
                    if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
                        _senderInterfaceInfoInDBFound = true;
                        _senderDeletionReqFromSubRouting = true;
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("updateSubscriberRoutingDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "SOSRechargeController[updateSubscriberRoutingDetails]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberRoutingDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateSubscriberRoutingDetails", _requestIDStr, " Exiting ");
            }
        }
    }

    public static synchronized void generateTransferID(TransferVO p_transferVO) throws BTSLBaseException {
        final String METHOD_NAME = "generateTransferID";
        String transferID = null;
        Date mydate = null;
        String minut2Compare = null;

        try {
            mydate = new Date();
            p_transferVO.setCreatedOn(mydate);
            minut2Compare = _sdfCompare.format(mydate);
            final int currentMinut = Integer.parseInt(minut2Compare);

            if (currentMinut != _prevMinut) {
                _transactionIDCounter = 1;
                _prevMinut = currentMinut;
            } else {
                _transactionIDCounter++;
            }
            if (_transactionIDCounter == 0) {
                throw new BTSLBaseException("SOSRechargeController", "generateTransferID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = _operatorUtil.formatSOSTransferID(p_transferVO, _transactionIDCounter);
            if (transferID == null) {
                throw new BTSLBaseException("SOSRechargeController", "generateTransferID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("SOSRechargeController", "generateTransferID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
        }
    }

    /**
     * Method to get the under process message before validation to be sent to
     * sender
     * 
     * @return
     */
    private String getSndrUPMsgBeforeValidation() {
        final String[] messageArgArray = { _senderMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.P2P_SENDER_UNDERPROCESS_B4VAL, messageArgArray);
    }

    public SenderVO prepareSenderVO(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareSenderVO", p_requestVO.getTransactionID(), "Entered");
        }
        final SenderVO senderVO = new SenderVO();
        senderVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(p_requestVO.getFilteredMSISDN()));
        senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
        senderVO.setModule(PretupsI.P2P_MODULE);

        final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix());
        senderVO.setPrefixID(networkPrefixVO.getPrefixID());
        senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        senderVO.setSubscriberType(networkPrefixVO.getSeriesType());
        p_requestVO.setSenderVO(_senderVO);
        if (_log.isDebugEnabled()) {
            _log.debug("prepareSenderVO", p_requestVO.getTransactionID(), "Exiting with Subscriber Prefix ID as =" + networkPrefixVO.getPrefixID());
        }
        return senderVO;
    }

    public ReceiverVO prepareReceiverVO(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareReceiverVO", p_requestVO.getTransactionID(), "Entered");
        }
        final ReceiverVO receiverVO = new ReceiverVO();
        receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(p_requestVO.getFilteredMSISDN()));
        receiverVO.setMsisdn(p_requestVO.getFilteredMSISDN());
        receiverVO.setModule(PretupsI.P2P_MODULE);

        final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(receiverVO.getMsisdnPrefix());
        receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
        receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        if (_log.isDebugEnabled()) {
            _log.debug("prepareReceiverVO", p_requestVO.getTransactionID(), "Exiting with Subscriber Prefix ID as =" + networkPrefixVO.getPrefixID());
        }
        return receiverVO;
    }

}
