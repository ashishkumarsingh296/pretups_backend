/*
 * @# C2SPostPaidReversalController
 * This class is the controller class of the Channel user Module.
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Feb 14, 2014 Vipan Kumar Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2013 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.channel.transfer.requesthandler;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Format;
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
import com.btsl.pretups.adjustments.businesslogic.DiffCalBL;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.SMSChargingLog;
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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.ResumeSuspendProcess;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.client.pretups.gateway.businesslogic.USSDPushMessage;

public class C2SPostPaidReversalController extends C2SBaseController implements ServiceKeywordControllerI, Runnable {
    private static Log _log = LogFactory.getLog(C2SPostPaidReversalController.class.getName());
    private C2STransferVO _c2sTransferVO = null;
    private TransferItemVO _senderTransferItemVO = null;
    private TransferItemVO _receiverTransferItemVO = null;
    private String _senderMSISDN;
    private String _receiverMSISDN;
    private ChannelUserVO _channelUserVO;
    private ReceiverVO _receiverVO;
    private String _senderSubscriberType;
    private String _senderNetworkCode;
    private Date _currentDate = null;
    private long _requestID;
    private String _requestIDStr;
    private String _transferID;
    private ArrayList _itemList = null;
    private String _intModCommunicationTypeS;
    private String _intModIPS;
    private int _intModPortS;
    private String _intModClassNameS;
    private boolean _transferDetailAdded = false;
    private boolean _isCounterDecreased = false;
    private String _type;
    private String _serviceType;
    private boolean _finalTransferStatusUpdate = false;
    private boolean _transferEntryReqd = false;
    private boolean _decreaseTransactionCounts = false;
    private UserBalancesVO _userBalancesVO = null;
    private boolean _creditBackEntryDone = false;
    private boolean _receiverInterfaceInfoInDBFound = false;
    private String _receiverAllServiceClassID = PretupsI.ALL;
    private Locale _senderLocale = null;
    private Locale _receiverLocale = null;
    private String _externalID = null;
    private RequestVO _requestVO = null;
    private boolean _processedFromQueue = false;
    private boolean _recValidationFailMessageRequired = false;
    private boolean _recTopupFailMessageRequired = false;
    private String _notAllowedSendMessGatw;
    private String _notAllowedRecSendMessGatw;
    private String _receiverSubscriberType = null;
    private String _imsi = null;
    private NetworkPrefixVO _networkPrefixVO = null;
    public static OperatorUtilI _operatorUtil = null;
    private String _interfaceStatusType = null;
    private static int _transactionIDCounter = 0;
    private static int _prevMinut = 0;
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    private boolean _receiverMessageSendReq = false;
    private boolean _ussdReceiverMessageSendReq=false;
    private String _senderPushMessageMsisdn = null;
    private boolean _oneLog = true;

    private String _receiverBundleID = null;
    private String _oldInfo1 = null;
    private String _oldInfo2 = null;
    private String _oldInfo3 = null;

    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SC2SPostPaidReversalController[initialize]", "", "",
                "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public C2SPostPaidReversalController() {
        _c2sTransferVO = new C2STransferVO();
        _currentDate = new Date();

        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("REVERSAL_REC_GEN_FAIL_MSG_REQD_V")))) {
            _recValidationFailMessageRequired = true;
        }
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("REVERSAL_REC_GEN_FAIL_MSG_REQD_T")))) {
            _recTopupFailMessageRequired = true;
        }
        _notAllowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("REVERSAL_SEN_MSG_NOT_REQD_GW"));
        _notAllowedRecSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("REVERSAL_REC_MSG_NOT_REQD_GW"));
    }

    /**
     * Method to process the request of the C2S transfer
     * 
     * @param object
     *            of the RequestVO
     */
    public void process(RequestVO p_requestVO) {
    	StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "process";
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered for Request ID=");
        	loggerValue.append(p_requestVO.getRequestID());
        	loggerValue.append(" MSISDN=");
        	loggerValue.append(p_requestVO.getFilteredMSISDN());
            _log.debug(methodName, p_requestVO.getRequestIDStr(), loggerValue);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            _requestVO = p_requestVO;
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            TransactionLog.log("", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_REQ,
                PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "Received Request From Receiver", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            _senderLocale = p_requestVO.getSenderLocale();
            _senderNetworkCode = _channelUserVO.getNetworkID();

            // Populatig C2STransferVO from the request VO
            populateVOFromRequest(p_requestVO);

            _requestID = p_requestVO.getRequestID();
            _requestIDStr = p_requestVO.getRequestIDStr();
            _type = p_requestVO.getType();
            _serviceType = p_requestVO.getServiceType();

            // Getting oracle connection
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // FOR USSD

            _channelUserVO.setCellID(p_requestVO.getCellId());
            _c2sTransferVO.setCellId(p_requestVO.getCellId());
            _c2sTransferVO.setSwitchId(p_requestVO.getSwitchId());
            _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
            _receiverVO.setServiceClassCode(_receiverAllServiceClassID);
            // Validating user message incomming in the request
            _operatorUtil.validateC2SReverrsalRequest(con, _c2sTransferVO, p_requestVO);
            _c2sTransferVO.setActiveUserId(_channelUserVO.getUserID());

            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));

            if (!_receiverVO.getSubscriberType().equals(_type)) {
                _networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _type);
                if (_networkPrefixVO != null) {
                    _receiverVO.setNetworkCode(_networkPrefixVO.getNetworkCode());
                    _receiverVO.setPrefixID(_networkPrefixVO.getPrefixID());
                    _receiverVO.setSubscriberType(_networkPrefixVO.getSeriesType());
                } else {
                    // Refuse the Request
                    _log.error(this, "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type);
                    EventHandler
                        .handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPostPaidReversalController[process]", "", "", "",
                            "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But alternate Category Routing was required on interface");
                    throw new BTSLBaseException("", methodName, PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { _receiverVO.getMsisdn() }, null);
                }
            }

            _receiverLocale = p_requestVO.getReceiverLocale();
            _senderLocale = p_requestVO.getSenderLocale();

            _receiverVO.setModule(_c2sTransferVO.getModule());
            _receiverVO.setCreatedDate(_currentDate);
            _receiverVO.setLastTransferOn(_currentDate);
            _senderMSISDN = (_channelUserVO.getUserPhoneVO()).getMsisdn();
            _senderPushMessageMsisdn = p_requestVO.getMessageSentMsisdn();
            _receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
            _c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
            _c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
            _c2sTransferVO.setTxnType(PretupsI.TXNTYPE_X);

            // Added By Vipan
            _c2sTransferVO.setSenderNetworkCode(_senderNetworkCode);
            _c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
            _c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());

            _receiverSubscriberType = _receiverVO.getSubscriberType();

            // Validates the network service status
            PretupsBL.validateNetworkService(_c2sTransferVO);

            _receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW, _receiverVO.getNetworkCode(), _serviceType))
                .booleanValue();
            _ussdReceiverMessageSendReq=((Boolean)PreferenceCache.getControlPreference(PreferenceI.USSD_REC_MSG_SEND_ALLOW,_receiverVO.getNetworkCode(),_serviceType)).booleanValue();

            // Chcking senders out transfer status, it should not be suspended
            if (PretupsI.YES.equalsIgnoreCase(_channelUserVO.getOutSuspened())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND_REVERSAL);
            }

            // Loading C2S receiver's controll parameters
            // added by PN(25/03/08) to resolve the issude of duplicate request
            // processing
            _c2sTransferVO.setUnderProcessCheckReqd(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());

            PretupsBL.loadRecieverControlLimits(con, p_requestVO.getRequestIDStr(), _c2sTransferVO);
            _receiverVO.setUnmarkRequestStatus(true);

            // commiting transaction after updating receiver's controll
            // parameters
            try {
                con.commit();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            // checking if SKey is required for the C2S transfers
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED)).booleanValue()) {
                // It is the case of SKey forwarding request to generate the
                // SKEY
                processSKeyGen(con);
                // Set Sender Message and throw Exception
            } else {
                // forwarding request to process the transfer request
                processTransfer(con);

                p_requestVO.setTransactionID(_transferID);
                _receiverVO.setLastTransferID(_transferID);
                // making entry in the transaction log
                TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_INT,
                    PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Generated Transfer ID", PretupsI.TXN_LOG_STATUS_SUCCESS,
                    "Source Type=" + _c2sTransferVO.getSourceType() + " Gateway Code=" + _c2sTransferVO.getRequestGatewayCode() + " ,Old Txn Id=" + _c2sTransferVO
                        .getOldTxnId());
                // populate payment and service interface details
                // p_requestVO.setReqSelector(_c2sTransferVO.getSelectorCode());
                final ServiceSelectorMappingVO serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(
                    p_requestVO.getServiceType() + "_" + p_requestVO.getReqSelector());
                if (serviceSelectorMappingVO != null) {
                    _receiverBundleID = serviceSelectorMappingVO.getReceiverBundleID();
                    _c2sTransferVO.setReceiverBundleID(_receiverBundleID);
                }
                _c2sTransferVO.setSubService(p_requestVO.getReqSelector());

                _operatorUtil.validateReversalOldTxnId(con, _c2sTransferVO, p_requestVO);

                _oldInfo1 = _c2sTransferVO.getInfo1();
                _oldInfo2 = _c2sTransferVO.getInfo2();
                _oldInfo3 = _c2sTransferVO.getInfo3();

                generateCollVaiable(p_requestVO);

                populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

                _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);

                // Change is done for ID=SUBTYPVALRECLMT
                // This chenge is done to set the receiver subscriber type in
                // transfer VO
                // This will be used in validate ReceiverLimit method of
                // PretupsBL when receiverTransferItemVO is null
                _c2sTransferVO.setReceiverSubscriberType(_receiverSubscriberType);

                // validate receiver limits before Interface Validations
                // PretupsBL.validateRecieverLimitsReversal(con,_c2sTransferVO,PretupsI.TRANS_STAGE_BEFORE_INVAL,PretupsI.C2S_MODULE);

                // Validate Sender Transaction profile checks and balance
                // availablility for user
                // ChannelUserBL.validateSenderAvailableControls(con,_transferID,_c2sTransferVO);

                // setting validation status
                _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

                // commiting transaction and closing the transaction as it is
                // not requred
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }
                if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#process");mcomCon=null;}
                con = null;

                // Checking the Various loads and setting flag to decrease the
                // transaction count
                checkTransactionLoad();
                _decreaseTransactionCounts = true;

                if (!_channelUserVO.isStaffUser()) {
                    (_channelUserVO.getUserPhoneVO()).setLastTransferID(_transferID);
                    (_channelUserVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);
                } else {
                    (_channelUserVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferID(_transferID);
                    (_channelUserVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);
                }
                // Checking the flow type of the transfer request, whether it is
                // common or thread
                if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
                    // Process validation requests and start thread for the bill
                    // payment
                    processValidationRequest();
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    p_requestVO.setSenderMessageRequired(_c2sTransferVO.isUnderProcessMsgReq());
                    p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());

                    // Parameter set to indicate that instance counters will not
                    // be decreased in receiver for this transaction
                    p_requestVO.setDecreaseLoadCounters(false);
                }// starting validation and topup process in thread
                else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    // Check if message needs to be sent in case of Thread
                    // implmentation
                    p_requestVO.setSenderReturnMessage(getSndrUPMsgBeforeValidation());
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    final Thread _controllerThread = new Thread(this);
                    // starting thread
                    _controllerThread.start();
                    _oneLog = false;
                    // Parameter set to indicate that instance counters will not
                    // be decreased in receiver for this transaction
                    p_requestVO.setDecreaseLoadCounters(false);
                } else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    processValidationRequest();
                    run();
                    final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL
                        .getDisplayAmount(_senderTransferItemVO.getPostBalance()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPreviousBalance()), String
                        .valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), String
                        .valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService() };
                    p_requestVO.setMessageArguments(messageArgArray);
                }

            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            p_requestVO.setSuccessTxn(false);
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) { // getting
                    // database
                    // connection
                    // if
                    // it
                    // is
                    // not
                    // already
                    // there
                    if (mcomCon == null) {
                    	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    }
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (_recValidationFailMessageRequired) {
                // setting receiver return message
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL, new String[] { PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, String.valueOf(_transferID) }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                            .getRequestedAmount()) }));
                    }

                }
            }
            // getting return message friom the C2StransferVO and setting it to
            // the requestVO
            if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
                p_requestVO.setSenderReturnMessage(_c2sTransferVO.getSenderReturnMessage());
            }

            if (be.isKey()) // checking if baseexception has key
            {
                if (_c2sTransferVO.getErrorCode() == null) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                }
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            // checking whether need to decrease the transaction load, if it is
            // already increased
            if (_transferID != null && _decreaseTransactionCounts) {
                // decreasing transaction load
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            // making entry in the transaction log
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            // setting success transaction status flag to false
            p_requestVO.setSuccessTxn(false);
            try {
                // getting database connection to unmark the users transaction
                // to completed
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    if (mcomCon == null) {
                    	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    }
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            // checking condition whether channel receiver required the general
            // failure message
            if (_recValidationFailMessageRequired) {
                // if receivermessage is null or it is not key
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // setting receiver return message
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL, new String[] { PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()), String.valueOf(_transferID) }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _log.errorTrace(methodName, e);

            // decreasing the transaction load count
            if (_transferID != null && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            // raising alarm
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPostPaidReversalController[process]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            // logging in the transaction log
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL,
                "Getting Code=" + p_requestVO.getMessageCode() + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

        }// end of catch
        finally {
            try {
                // Getting connection if it is null
                if (mcomCon == null) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
                // makking entry in the transfer table if transfer entry has not
                // been made and message gateway flow is common, i.e. validation
                // is not in thread
                // if(_transferID!=null && !_transferDetailAdded &&
                // p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON))
                if (_transferID != null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || p_requestVO
                    .getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(
                    PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS)))) {
                    // added by nilesh: consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        addEntryInTransfers(con);
                    }
                }

                else if (_transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    _log.info(methodName, p_requestVO.getRequestIDStr(),
                        "Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + _transferID + " But not added entry in Transfers yet");
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
            	loggerValue.append("Exception:");
            	loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPostPaidReversalController[process]",
                    _transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
            }
            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (_isCounterDecreased) {
                p_requestVO.setDecreaseLoadCounters(false);
            }
            if (con != null) {
                // committing transaction and closing connection
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#process");mcomCon=null;}
                con = null;
            }// end if

            if (_receiverMessageSendReq && _recValidationFailMessageRequired && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
                .equals(_notAllowedRecSendMessGatw)) {
                // checking if receiver message is not null and receiver return
                // message is key
                if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // generating message and pushing it to receiver
                    final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                    (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                        _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                } else if (_c2sTransferVO.getReceiverReturnMsg() != null) {
                    (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                        .push();
                }
            }
            if(_ussdReceiverMessageSendReq&&_recValidationFailMessageRequired&&!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)&&!"ALL".equals(_notAllowedRecSendMessGatw))
			{
				//checking if receiver message is not null and receiver return message is key
				if(_c2sTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
				{
					//generating message and pushing it to receiver
					BTSLMessages btslRecMessages=(BTSLMessages)_c2sTransferVO.getReceiverReturnMsg();
					(new USSDPushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
				}
				else if(_c2sTransferVO.getReceiverReturnMsg()!=null) //pushing message to receiver
					(new USSDPushMessage(_receiverMSISDN,(String)_c2sTransferVO.getReceiverReturnMsg(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
			}
            // added by nilesh : consolidated for logger
            if (_oneLog) {
                OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
            }
            // making entry in the transaction log
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS,
                "Getting Code=" + p_requestVO.getMessageCode() + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }// end of finally
    }// end of process

    /**
     * Method to process the request if SKEY is required for this transaction
     * 
     * @param p_con
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processSKeyGen(Connection p_con) throws BTSLBaseException, Exception {
        final String methodName = "processSKeyGen";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        try {
            // validate skey details for generation and generate skey
            PretupsBL.generateSKey(p_con, _c2sTransferVO);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPostPaidReversalController[processSKeyGen]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }// end of processSKeyGen

    private void generateCollVaiable(RequestVO p_requestvo) {
        if (_log.isDebugEnabled()) {
            _log.debug("generateCollVaiable", "Entered");
        }
        final String METHOD_NAME = "generateCollVaiable";

        try {

            String chars = null;
            try {
                chars = Constants.getProperty("PIN_GEN_ARG");
            } catch (Exception e) {
                chars = "123456789013579";
                _log.errorTrace(METHOD_NAME, e);
            }

            int passLengthCB = 0;
            try {
                passLengthCB = Integer.parseInt(Constants.getProperty("PIN_LENGTH_CodBanco"));
            } catch (Exception e) {
                passLengthCB = 6;
                _log.errorTrace(METHOD_NAME, e);
            }

            int passLengthT = 0;
            try {
                passLengthT = Integer.parseInt(Constants.getProperty("PIN_LENGTH_Trace"));
            } catch (Exception e) {
                passLengthT = 6;
                _log.errorTrace(METHOD_NAME, e);
            }
            int passLengthNP = 0;
            try {
                passLengthNP = Integer.parseInt(Constants.getProperty("PIN_LENGTH_NroOperacion"));
            } catch (Exception e) {
                passLengthNP = 12;
                _log.errorTrace(METHOD_NAME, e);
            }

            String valueNP = generatePIN(chars, passLengthNP);
            if (BTSLUtil.isNullString(valueNP)) {
                valueNP = "000095096674";
            }
            p_requestvo.setInfo1(valueNP);
            _c2sTransferVO.setInfo1(valueNP);
            String valueT = generatePIN(chars, passLengthT);
            if (BTSLUtil.isNullString(valueT)) {
                valueT = "300010";
            }
            p_requestvo.setInfo2(valueT);
            _c2sTransferVO.setInfo2(valueT);
            String valueCB = generatePIN(chars, passLengthCB);
            if (BTSLUtil.isNullString(valueCB)) {
                valueCB = "624300";
            }
            p_requestvo.setInfo3(valueCB);
            _c2sTransferVO.setInfo3(valueCB);

        } catch (Exception e) {
            _log.error("process", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("generateCollVaiable", "exit");
        }
    }

    // added for PIN generetion
    synchronized public String generatePIN(String chars, int passLength) throws BTSLBaseException {
        final String METHOD_NAME = "generatePIN";
        StringBuffer temp = null;
        try {
            if (passLength > chars.length()) {
                throw new BTSLBaseException(this, METHOD_NAME,"Random number minimum length should be less than the provided chars list length");
            }
           final SecureRandom m_generator = new SecureRandom();
           m_generator.setSeed(System.nanoTime()) ;
            final char[] availableChars = chars.toCharArray();
            int availableCharsLeft = availableChars.length;
            temp = new StringBuffer(passLength);
            int pos = 0;
            for (int i = 0; i < passLength;) {
                pos = BTSLUtil.parseDoubleToInt(availableCharsLeft * m_generator.nextDouble());
                if (i == 0) {
                    if (!("0".equalsIgnoreCase(String.valueOf(availableChars[pos])))) {
                        i++;
                        temp.append(availableChars[pos]);
                        availableChars[pos] = availableChars[availableCharsLeft - 1];
                        --availableCharsLeft;
                    }
                } else {
                    temp.append(availableChars[pos]);
                    i++;
                    availableChars[pos] = availableChars[availableCharsLeft - 1];
                    --availableCharsLeft;
                }
            }
        } catch (Exception e) {
            temp = null;
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME,"Exception In generating PIN");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("generatePIN Exiting", "");
        }
        return String.valueOf(temp);
    }

    /**
     * Method to process the request and perform the validation of the request
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    public void processTransfer(Connection p_con) throws BTSLBaseException {
        final String methodName = "processTransfer";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        try {
            // Generating the C2S transfer ID
            _c2sTransferVO.setTransferDate(_currentDate);
            _c2sTransferVO.setTransferDateTime(BTSLUtil.getTimestampFromUtilDate(_currentDate));
            // PretupsBL.generateC2STransferID(_c2sTransferVO);
            generateReversalTransferID(_c2sTransferVO);
            _transferID = _c2sTransferVO.getTransferID();
            _receiverVO.setLastTransferID(_transferID);

            // Set sender transfer item details
            setSenderTransferItemVO();

            // set receiver transfer item details
            setReceiverTransferItemVO();

            // Get the product Info based on the service type
            PretupsBL.getProductFromServiceType(p_con, _c2sTransferVO, _serviceType, PretupsI.C2S_MODULE);
            _transferEntryReqd = true;

            // Here logic will come for Commission profile for sale center
            if ((_channelUserVO.getCategoryVO()).getDomainTypeCode().equals(PretupsI.DOMAIN_TYPE_SALECENTER)) {
                _senderTransferItemVO.setTransferValue(_c2sTransferVO.getRequestedAmount());
            } else {
                _senderTransferItemVO.setTransferValue(_c2sTransferVO.getTransferValue());
            }

        } catch (BTSLBaseException be) {
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (be.isKey()) {
                _c2sTransferVO.setErrorCode(be.getMessageKey());
            } else {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            throw be;
        } catch (Exception e) {
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                if (_transferID != null) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL, new String[] { PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, String.valueOf(_transferID) }));
                } else {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                        .getRequestedAmount()) }));
                }
            }
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPostPaidReversalController[processTransfer]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited");
            }
        }
    }

    /**
     * Thread to perform IN related operations
     */
    public void run() {
        final String methodName = "run";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, _transferID, "Entered");
        }
        BTSLMessages btslMessages = null;
        _userBalancesVO = null;
        CommonClient commonClient = null;
        Connection con = null;MComConnectionI mcomCon = null;
        DiffCalBL diffCalBL = null;
        ArrayList diffList = null;
        try {
            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !_processedFromQueue) {
                // Processing validation request in Thread
                processValidationRequestInThread();
            }

            // send validation request for sender
            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_TOP);

            // send validation request for receiver
            commonClient = new CommonClient();

            // Getting the receiver credit string from C2S transfer VO to be
            // sent to the Interface Module
            final String requestStr = getReceiverDebitStr();

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "" + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());
            // Sending request to the common client
            final String receiverCreditResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                receiverCreditResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "" + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, _transferID, "Got the response from IN Module receiverCreditResponse=" + receiverCreditResponse + " ,Old Txn Id=" + _c2sTransferVO
                    .getOldTxnId());
            }

            // Getting Database connection
            try {
                // updating receiver credit response
                updateForReceiverDebitResponse(receiverCreditResponse);

                // decreasing response counters
                LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
            } catch (BTSLBaseException be) {

                TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _c2sTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO
                        .getInterfaceResponseCode() + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());

                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                }
                // Update the sender back for fail transaction
                // Check Status if Ambigous then credit back preference wise
                // Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimitsReversal(_c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
                if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }

                throw be;
            }// end catch BTSLBaseException
            catch (Exception e) {

                TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _c2sTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO
                        .getInterfaceResponseCode() + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());
                _log.errorTrace(methodName, e);
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                }
                // Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimitsReversal(_c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
                if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }

                throw new BTSLBaseException(this, methodName, "");

            }// end of catch Exception

            // For increaseing the counters in network and service type
            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) || _c2sTransferVO.getMsgGatewayFlowType().equals(
                PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
                ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                    _serviceType, _transferID, LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, _receiverVO.getNetworkCode());
                ReqNetworkServiceLoadController.decrementUnderProcessCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),
                    _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_UNDERPROCESS_REQUEST, 0, false, _receiverVO.getNetworkCode());
            }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _c2sTransferVO.setSenderReturnMessage(null);

            // checking whether differential commission is applicable or not
            if (PretupsI.YES.equals(_c2sTransferVO.getDifferentialAllowedForService()) && PretupsI.YES.equals(_c2sTransferVO.getDifferentialGiven())) {
                if (mcomCon == null) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
                // Caluculate Differential if transaction successful
                try {
                    diffCalBL = new DiffCalBL();
                    diffList = diffCalBL.loadDifferentialCalculationsReversal(con, _c2sTransferVO, PretupsI.C2S_MODULE);
                } catch (BTSLBaseException be) {
                    _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_EXCEPTION);
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append( "For _transferID=");
                    	loggerValue.append(_transferID);
                    	loggerValue.append(" Diff applicable=" );
                    	loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
                    	loggerValue.append(" Diff Given=");
                    	loggerValue.append(_c2sTransferVO.getDifferentialGiven());
                    	loggerValue.append(" Not able to give Diff commission getting BTSL Base Exception=");
                    	loggerValue.append(be.getMessage());
                    	loggerValue.append(" Leaving transaction status as Under process");
                    	loggerValue.append(" ,Old Txn Id=");
                    	loggerValue.append(_c2sTransferVO.getOldTxnId());
                        _log.debug("C2SPostPaidReversalController",loggerValue );
                    }
                    _log.errorTrace(methodName, be);
                    loggerValue.setLength(0);
                    loggerValue.append("Exception:");
                    loggerValue.append( be.getMessage());
                    
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPostPaidReversalController[run]",
                        _c2sTransferVO.getTransferID(), _c2sTransferVO.getSenderMsisdn(), _c2sTransferVO.getNetworkCode(),  loggerValue.toString());
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_EXCEPTION);
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append( "For _transferID=");
                    	loggerValue.append(_transferID);
                    	loggerValue.append(" Diff applicable=" );
                    	loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
                    	loggerValue.append(" Diff Given=");
                    	loggerValue.append(_c2sTransferVO.getDifferentialGiven());
                    	loggerValue.append(" Not able to give Diff commission getting Exception=" );
                    	loggerValue.append(e.getMessage());
                    	loggerValue.append( " Leaving transaction status as Under process");
                    	loggerValue.append(" ,Old Txn Id=" );
                    	loggerValue.append(_c2sTransferVO.getOldTxnId());
                        _log.debug( "C2SPostPaidReversalController",loggerValue );
                    }
                }
                if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#run");mcomCon=null;}
                con = null;
                _finalTransferStatusUpdate = true;
            } else {
                _finalTransferStatusUpdate = true;
            }

            _c2sTransferVO.setErrorCode(null);

            // TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"After Differential Calculation",PretupsI.TXN_LOG_STATUS_SUCCESS,"Differential Appl="+_c2sTransferVO.getDifferentialApplicable()+" Diff Given="+_c2sTransferVO.getDifferentialGiven());

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("For _transferID=");
            	loggerValue.append(_transferID);
            	loggerValue.append(" Diff applicable=");
            	loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
            	loggerValue.append(" Diff Given=" );
            	loggerValue.append(_c2sTransferVO.getDifferentialGiven() );
            	loggerValue.append(" ,Old Txn Id=" );
            	loggerValue.append(_c2sTransferVO.getOldTxnId());
                _log.debug("C2SPostPaidReversalController",loggerValue );
            }
        }// end try
        catch (BTSLBaseException be) {
            _requestVO.setSuccessTxn(false);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }
            }// end if
            if (be.isKey() && _c2sTransferVO.getSenderReturnMessage() == null) {
                btslMessages = be.getBtslMessages();
            } else if (_c2sTransferVO.getSenderReturnMessage() == null) {
                _c2sTransferVO.setSenderReturnMessage(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Error Code:" );
            	loggerValue.append(_c2sTransferVO.getErrorCode());
            	loggerValue.append(" ,Old Txn Id=");
            	loggerValue.append(_c2sTransferVO.getOldTxnId());
                _log.debug(methodName, _transferID, loggerValue);
            }

            // For increaseing the counters in network and service type
            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) || _c2sTransferVO.getMsgGatewayFlowType().equals(
                PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
                ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                    _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
                ReqNetworkServiceLoadController.decrementUnderProcessCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),
                    _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_UNDERPROCESS_REQUEST, 0, false, _receiverVO.getNetworkCode());
            }
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            _log.errorTrace(methodName, be);

        }// end catch BTSLBaseException
        catch (Exception e) {
            _requestVO.setSuccessTxn(false);
            // try{if(con!=null) con.rollback() ;}catch(Exception ex){}

            _log.errorTrace(methodName, e);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "{C2SPostPaidReversalController[run]", _transferID,
                _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
            btslMessages = new BTSLMessages(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

            // For increaseing the counters in network and service type
            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) || _c2sTransferVO.getMsgGatewayFlowType().equals(
                PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
                ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                    _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
                ReqNetworkServiceLoadController.decrementUnderProcessCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),
                    _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_UNDERPROCESS_REQUEST, 0, false, _receiverVO.getNetworkCode());
            }
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

        }// end catch Exception
        finally {
            if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO
                .getReceiverReturnMsg()).isKey())) {
                _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                    .getRequestedAmount()), _senderPushMessageMsisdn, String.valueOf(_transferID) }));
            }

            // decreasing transaction load count
            LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);

            int updateCount = 0;
            try {
                if (mcomCon == null) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }

                // _c2sTransferVO.setReferenceID(_c2sTransferVO.getOldTxnId());

                if (BTSLUtil.isNullString(_c2sTransferVO.getModifiedBy())) {
                    _c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                }

                _c2sTransferVO.setModifiedOn(_currentDate);

                _c2sTransferVO.setSenderTransferValue(_c2sTransferVO.getTransferValue());

                if (_finalTransferStatusUpdate) {
                    // Update Previous billpayment Success Request
                    updateCount = ChannelTransferBL.updateOldC2STransferDetailsReversal(con, _c2sTransferVO);

                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        if (updateCount > 0) {
                            updateCount = 0;
                            // Credit back the credited billpayment amount
                            updateCount = ChannelUserBL.creditUserBalanceForProductReversal(con, _transferID, _c2sTransferVO);
                        }
                    }

                    if (updateCount > 0) {
                        PretupsBL.validateRecieverLimitsReversal(_c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
                    }

                } else {
                    updateCount = 1;
                }
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    if (updateCount > 0) {
                        updateCount = 0;
                        updateCount = ChannelTransferBL.updateC2STransferDetailsReversal(con, _c2sTransferVO);
                    }
                }

                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
                }
            } catch (BTSLBaseException be) {
                updateCount = 0;
                try {
                    con.rollback();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.errorTrace(methodName, be);
                if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }

            } catch (Exception e) {
                updateCount = 0;
                try {
                    con.rollback();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
                loggerValue.append("Exception while updating transfer details in database , Exception:");
                loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPostPaidReversalController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
                if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }

            } finally {
                if (updateCount > 0) {
                    try {
                        con.commit();
                    } catch (Exception e) {
                        _log.errorTrace(methodName, e);
                    }

                    if (_finalTransferStatusUpdate && diffList != null && !diffList.isEmpty()) {
                        try {
                            updateCount = 0;
                            diffCalBL = new DiffCalBL();
                            // Credit back the debited billpayment amount
                            updateCount = diffCalBL.differentialAdjustmentForReversal(con, _c2sTransferVO, diffList);
                        } catch (Exception e) {
                            try {
                                con.rollback();
                            } catch (Exception ec) {
                                _log.errorTrace(methodName, ec);
                            }
                            _log.errorTrace(methodName, e);
                        } finally {
                            if (updateCount > 0) {
                                try {
                                    con.commit();
                                } catch (Exception e) {
                                    _log.errorTrace(methodName, e);
                                }
                            } else {
                                try {
                                    con.rollback();
                                } catch (Exception ec) {
                                    _log.errorTrace(methodName, ec);
                                }
                            }
                        }
                    }
                } else {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        _log.errorTrace(methodName, e);
                    }
                }
                if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#run");mcomCon=null;}
                con = null;
            }

            _senderTransferItemVO.setPostBalance(_c2sTransferVO.getPostBalance());

            // If transaction is fail and grouptype counters need to be decrease
            // then decrease the counters
            // This change has been done by ankit on date 14/07/06 for SMS
            // charging
            if (!_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && _requestVO.isDecreaseGroupTypeCounter() && ((ChannelUserVO) _requestVO
                .getSenderVO()).getUserControlGrouptypeCounters() != null) {
                PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO) _requestVO.getSenderVO()).getUserControlGrouptypeCounters());
            }

            if (_receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
                .equals(_notAllowedRecSendMessGatw)) {
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    if (_c2sTransferVO.getReceiverReturnMsg() == null) {
                        (new PushMessage(_receiverMSISDN, getReceiverSuccessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                    } else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                        final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                        (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                            _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                    } else {
                        (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                            .push();
                    }
                } else if (_recTopupFailMessageRequired && _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    if (_c2sTransferVO.getReceiverReturnMsg() == null) {
                        (new PushMessage(_receiverMSISDN, getReceiverAmbigousMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                    } else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                        final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                        (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                            _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                    } else {
                        (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                            .push();
                    }
                } else if (_recTopupFailMessageRequired && _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    if (_c2sTransferVO.getReceiverReturnMsg() == null) {
                    	//method getReceiverFailMessage is moved to C2SBaseController on refactoring on 27/05/16
                    	final String message=getReceiverFailMessage( _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_senderPushMessageMsisdn, _channelUserVO
                                .getUserName(), _requestVO.getPosUserMSISDN(),_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY_REVERSAL,_requestVO.getRequestGatewayType());
                        (new PushMessage(_receiverMSISDN,message , _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                    } else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                        final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                        (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                            _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                    } else {
                        (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                            .push();
                    }
                }
            }
            if(_ussdReceiverMessageSendReq&&!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)&&!"ALL".equals(_notAllowedRecSendMessGatw))
			{
				if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))
				{
					if(_c2sTransferVO.getReceiverReturnMsg()==null)
						(new USSDPushMessage(_receiverMSISDN,getReceiverSuccessMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
					else if(_c2sTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
					{
						BTSLMessages btslRecMessages=(BTSLMessages)_c2sTransferVO.getReceiverReturnMsg();
						(new USSDPushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
					}
					else 
						(new USSDPushMessage(_receiverMSISDN,(String)_c2sTransferVO.getReceiverReturnMsg(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
				}
				else if(_recTopupFailMessageRequired&&_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS))
				{
					if(_c2sTransferVO.getReceiverReturnMsg()==null)
						(new USSDPushMessage(_receiverMSISDN,getReceiverAmbigousMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
					else if(_c2sTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
					{
						BTSLMessages btslRecMessages=(BTSLMessages)_c2sTransferVO.getReceiverReturnMsg();
						(new USSDPushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
					}
					else 
						(new USSDPushMessage(_receiverMSISDN,(String)_c2sTransferVO.getReceiverReturnMsg(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
				}
				else if(_recTopupFailMessageRequired && _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL))
				{
					if(_c2sTransferVO.getReceiverReturnMsg()==null) {
						final String message=getReceiverFailMessage( _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_senderPushMessageMsisdn, _channelUserVO
                                .getUserName(), _requestVO.getPosUserMSISDN(),_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY_REVERSAL,_requestVO.getRequestGatewayType());
                        (new USSDPushMessage(_receiverMSISDN,message , _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
					}
              	else if(_c2sTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
					{
						BTSLMessages btslRecMessages=(BTSLMessages)_c2sTransferVO.getReceiverReturnMsg();
						(new USSDPushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
					}
					else
						(new USSDPushMessage(_receiverMSISDN,(String)_c2sTransferVO.getReceiverReturnMsg(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
				}
			}
            if (!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedSendMessGatw)) {
                PushMessage pushMessages = null;
                if (btslMessages != null) {
                    // push final error message to sender
                    pushMessages = (new PushMessage(_senderPushMessageMsisdn, BTSLUtil.getMessage(_senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs()),
                        _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale));
                } else {
                    // push Additional Commission success message to sender and
                    // final status to sender
                    if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
                        pushMessages = (new PushMessage(_senderPushMessageMsisdn, _c2sTransferVO.getSenderReturnMessage(), _transferID,
                            _c2sTransferVO.getRequestGatewayCode(), _senderLocale));
                    } else if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                        pushMessages = new PushMessage(_senderPushMessageMsisdn, getSenderSuccessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale);
                    }
                }// end if
                 // If transaction is successfull then if group type counters
                 // reach limit then send message using gateway that is
                 // associated with group type profile
                 // This change has been done by ankit on date 14/07/06 for SMS
                 // charging
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED))
                    .indexOf(_requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE.equals(_requestVO.getGroupType())) {
                    try {
                        GroupTypeProfileVO groupTypeProfileVO = null;
                        // load the user running and profile counters
                        // Check the counters
                        // update the counters
                        groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(_requestVO, PretupsI.GRPT_TYPE_CHARGING);
                        // if group type counters reach limit then send message
                        // using gateway that is associated with group type
                        // profile
                        if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
                            pushMessages.push(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());// new
                            // method
                            // will
                            // be
                            // called
                            // here
                            SMSChargingLog.log(((ChannelUserVO) _requestVO.getSenderVO()).getUserID(), (((ChannelUserVO) _requestVO.getSenderVO())
                                .getUserChargeGrouptypeCounters()).getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(),
                                groupTypeProfileVO.getResGatewayType(), groupTypeProfileVO.getNetworkCode(), _requestVO.getGroupType(), _requestVO.getServiceType(),
                                _requestVO.getModule());
                        } else {
                            pushMessages.push();
                        }
                    } catch (Exception e) {
                        _log.errorTrace(methodName, e);
                    }
                } else {
                    pushMessages.push();
                }

                if (!BTSLUtil.isNullString(_requestVO.getParentMsisdnPOS()) && !("".equals(_requestVO.getParentMsisdnPOS())) && _channelUserVO.isStaffUser()) {
                    final PushMessage pushParentMessages = (new PushMessage(_requestVO.getParentMsisdnPOS(), getSenderParentSuccessMessage(), _transferID, _c2sTransferVO
                        .getRequestGatewayCode(), _senderLocale));
                    pushParentMessages.push();
                }
            }
            // Log the credit back entry in the balance log
            if (_creditBackEntryDone) {
                BalanceLogger.log(_userBalancesVO);
            }
            // added by nilesh: consolidated for logger
            if (!_oneLog) {
                OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
            }
            TransactionLog
                .log(
                    _transferID,
                    _requestIDStr,
                    _senderMSISDN,
                    _senderNetworkCode,
                    PretupsI.TXN_LOG_REQTYPE_RES,
                    PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Transaction Ending",
                    PretupsI.TXN_LOG_STATUS_SUCCESS,
                    "Trans Status=" + _c2sTransferVO.getTransferStatus() + " Error Code=" + _c2sTransferVO.getErrorCode() + " Diff Appl=" + _c2sTransferVO
                        .getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO.getDifferentialGiven() + " Message=" + _c2sTransferVO.getSenderReturnMessage() + " ,Old Txn Id=" + _c2sTransferVO
                        .getOldTxnId());

            btslMessages = null;
            _userBalancesVO = null;
            commonClient = null;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, _transferID, "Exiting");
            }
        }// end of finally
    }

    /**
     * Method to populate C2S Transfer VO from request VO for further use
     * 
     * @param p_requestVO
     */
    private void populateVOFromRequest(RequestVO p_requestVO) {
        _c2sTransferVO.setSenderVO(_channelUserVO);
        _c2sTransferVO.setRequestID(p_requestVO.getRequestIDStr());
        _c2sTransferVO.setModule(p_requestVO.getModule());
        _c2sTransferVO.setInstanceID(p_requestVO.getInstanceID());
        _c2sTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        _c2sTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        _c2sTransferVO.setServiceType(p_requestVO.getServiceType());
        _c2sTransferVO.setSourceType(p_requestVO.getSourceType());
        _c2sTransferVO.setCreatedOn(_currentDate);
        _c2sTransferVO.setCreatedBy(_channelUserVO.getUserID());
        _c2sTransferVO.setModifiedOn(_currentDate);
        _c2sTransferVO.setModifiedBy(_channelUserVO.getUserID());
        _c2sTransferVO.setTransferDate(_currentDate);
        _c2sTransferVO.setTransferDateTime(_currentDate);
        _c2sTransferVO.setSenderMsisdn((_channelUserVO.getUserPhoneVO()).getMsisdn());
        _c2sTransferVO.setSenderID(_channelUserVO.getUserID());
        _c2sTransferVO.setNetworkCode(_channelUserVO.getNetworkID());
        _c2sTransferVO.setLocale(_senderLocale);
        _c2sTransferVO.setLanguage(_c2sTransferVO.getLocale().getLanguage());
        _c2sTransferVO.setCountry(_c2sTransferVO.getLocale().getCountry());
        _c2sTransferVO.setMsgGatewayFlowType(p_requestVO.getMessageGatewayVO().getFlowType());
        _c2sTransferVO.setMsgGatewayResponseType(p_requestVO.getMessageGatewayVO().getResponseType());
        _c2sTransferVO.setMsgGatewayTimeOutValue(p_requestVO.getMessageGatewayVO().getTimeoutValue());
        (_channelUserVO.getUserPhoneVO()).setLocale(_senderLocale);
        _c2sTransferVO.setReferenceID(p_requestVO.getExternalReferenceNum());
        _c2sTransferVO.setActiveUserId(_channelUserVO.getActiveUserID());
    }// end populateVOFromRequest

    /**
     * Method to process the response of the receiver validation from IN
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForReceiverValidateResponse(String str) throws BTSLBaseException {
        final String methodName = "updateForReceiverValidateResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");

        final boolean isRequired = false;

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        // MSISDN is not found on interface and
        if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
            // If use interface language field is Y in service type table then
            // update the receiver locale.
            if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
                // update the receiver locale if language code returned from IN
                // is not null
                updateReceiverLocale((String) map.get("IN_LANG"));
            }
            if (!BTSLUtil.isNullString(_receiverSubscriberType)) {
                _receiverTransferItemVO.setSubscriberType(_receiverSubscriberType);
            }
            _receiverVO.setSubscriberType(_receiverTransferItemVO.getSubscriberType());
            _receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
            // in case of whitelist we already set the account status.
            if (BTSLUtil.isNullString(_receiverTransferItemVO.getAccountStatus())) {
                _receiverTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
            }
            _receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
            _receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
            _receiverTransferItemVO.setValidationStatus(status);
            _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
            // set from IN Module
            // if(BTSLUtil.isNumeric((String)map.get("IN_TXN_ID")))
            // {
            try {
                _receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "C2SPostPaidReversalController[updateForReceiverValidateResponse]", _transferID, _senderMSISDN, _senderNetworkCode,
                    "Exception while parsing for interface txn ID , Exception:" + e.getMessage());
            }
            // }

            // If status is other than Success in validation stage mark sender
            // request as Not applicable and
            // Make transaction Fail
            String[] strArr = null;

            if (!InterfaceErrorCodesI.SUCCESS.equals(status)) {
                _c2sTransferVO.setErrorCode(status + "_R");
                _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                _receiverTransferItemVO.setTransferStatus(status);
                _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _receiverMSISDN };
                throw new BTSLBaseException("C2SPostPaidReversalController", methodName, _c2sTransferVO.getErrorCode(), 0, strArr, null);
            }
            try {
                _receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            ;
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            // in case of whitelist service class code is already set
            _receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
            // Done so that receiver check can be brough to common
            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
            _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
            // Update the Previous Balance in case of Post Paid Offline
            // interface with Credit Limit - Monthly Transfer Amount
            if (_receiverVO.isPostOfflineInterface()) {
                final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(_receiverVO.getLastSuccessOn(), _currentDate, BTSLUtil.PERIOD_MONTH);
                if (!isPeriodChange) {
                    _receiverTransferItemVO.setPreviousBalance(_receiverTransferItemVO.getPreviousBalance() - _receiverVO.getMonthlyTransferAmount());
                }
            }

        }
    }

    /**
     * Method to process the response of the receiver top up from IN
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForReceiverDebitResponse(String str) throws BTSLBaseException {
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        final String methodName = "updateForReceiverDebitResponse";
        StringBuilder loggerValue= new StringBuilder(); 
        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Mape from response=");
        	loggerValue.append(map);
        	loggerValue.append(" status=" );
        	loggerValue.append(status);
        	loggerValue.append(" interface Status=" );
        	loggerValue.append(interfaceStatusType);
        	loggerValue.append(" ,Old Txn Id=");
            loggerValue.append(_c2sTransferVO.getOldTxnId());
            _log.debug("updateForReceiverCreditResponse",loggerValue );
        }
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        _receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        _receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        String updateStatus = (String) map.get("UPDATE_STATUS");

        // setting transaction status for restricted subscriber feature
        if (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getRestrictedMsisdns())) {
            if (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getTransferToListOnly())) {
                ((RestrictedSubscriberVO) ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getRestrictedSubscriberVO()).setTempStatus(status);
            }
        }

        if (BTSLUtil.isNullString(updateStatus)) {
            updateStatus = status;
        }
        _receiverTransferItemVO.setUpdateStatus(updateStatus);
        _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
        // set from IN Module
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
            try {
                _receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
                loggerValue.append("Exception while parsing for interface txn ID , Exception:" );
                loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "C2SPostPaidReversalController[updateForReceiverCreditResponse]", _transferID, _senderMSISDN, _senderNetworkCode,
                    loggerValue.toString() );
            }
        }

        if (!BTSLUtil.isNullString((String) map.get("IN_RECON_ID"))) {
            _receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
        }
        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        String[] strArr = null;
        // if(BTSLUtil.isNullString(status)
        // ||(!status.equals(InterfaceErrorCodesI.SUCCESS) &&
        // !status.equals(InterfaceErrorCodesI.AMBIGOUS)))
        if ((!InterfaceErrorCodesI.SUCCESS.equals(status)) && (!InterfaceErrorCodesI.AMBIGOUS.equals(status))) {
            _c2sTransferVO.setErrorCode(status + "_R");
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(status);
            strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", _c2sTransferVO.getErrorCode(), 0, strArr, null);
        } else if (InterfaceErrorCodesI.AMBIGOUS.equals(status)) {
            _c2sTransferVO.setErrorCode(InterfaceErrorCodesI.ERROR_REVERSAL_AMBIGOUS);
            _c2sTransferVO.setTransferStatus(InterfaceErrorCodesI.SUCCESS);
            _receiverTransferItemVO.setTransferStatus(InterfaceErrorCodesI.SUCCESS);
            _receiverVO.setTransactionStatus(InterfaceErrorCodesI.SUCCESS);
            _receiverTransferItemVO.setUpdateStatus(InterfaceErrorCodesI.SUCCESS);
            strArr = new String[] { _transferID, _receiverTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        } else {
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _receiverTransferItemVO.setUpdateStatus(status);
        }
    }

    /**
     * Method to populate the service interface details based on the action and
     * service type
     * 
     * @param action
     * @throws BTSLBaseException
     */
    public void populateServiceInterfaceDetails(Connection p_con, String action) throws BTSLBaseException {
        final String receiverNetworkCode = _receiverVO.getNetworkCode();
        final long receiverPrefixID = _receiverVO.getPrefixID();
        boolean isReceiverFound = false;
        // Check if receiver info is already found in DB or not
        // If yes then not go for ineterface routing again because it was getted
        // at the time of validation
        // This block is executed for update and validate both
        if ((!_receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
            .equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
            isReceiverFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, receiverPrefixID, _type, receiverNetworkCode, _c2sTransferVO.getServiceType(), _type,
                PretupsI.USER_TYPE_RECEIVER, action);
        } else {
            isReceiverFound = true;
        }

        if (!isReceiverFound) {
            throw new BTSLBaseException("C2SPostPaidReversalController", "populateServiceInterfaceDetails", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING);
        }
    }

    /**
     * Check the transaction load
     * 
     * @throws BTSLBaseException
     */
    private void checkTransactionLoad() throws BTSLBaseException {
        final String methodName = "checkTransactionLoad";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Checking load for transfer ID=");
        	loggerValue.append(_transferID);
        	loggerValue.append(" ,Old Txn Id=" );
        	loggerValue.append(_c2sTransferVO.getOldTxnId());
            _log.debug(methodName,  loggerValue );
        }
        int recieverLoadStatus = 0;
        try {
            _c2sTransferVO.setRequestVO(_requestVO);
            _c2sTransferVO.setSenderTransferItemVO(_senderTransferItemVO);
            _c2sTransferVO.setReceiverTransferItemVO(_receiverTransferItemVO);
            recieverLoadStatus = LoadController.checkInterfaceLoad(_c2sTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _transferID,
                _c2sTransferVO, true);
            if (recieverLoadStatus == 0) {
                LoadController.checkTransactionLoad(((ReceiverVO) _c2sTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(),
                    PretupsI.C2S_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
                if (_log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("_transferID=");
                	loggerValue.append(_transferID);
                	loggerValue.append(" Successfully through load");
                    _log.debug("C2SPostPaidReversalController[checkTransactionLoad]", loggerValue);
                }
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                final String strArr[] = { _receiverMSISDN, String.valueOf(PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())) };
                throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("Refusing request getting Exception:");
        	loggerValue.append(be.getMessage());
            _log.error("C2SPostPaidReversalController[checkTransactionLoad]",  loggerValue);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
    }

    /**
     * Method to do the validation of the receiver and perform the steps before
     * the topup stage
     * 
     * @param p_con
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequest() throws BTSLBaseException,SQLException {
        final String methodName = "processValidationRequest";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered and performing validations for transfer ID=");
        	loggerValue.append(_transferID);
        	loggerValue.append(" ");
        	loggerValue.append(_c2sTransferVO.getModule());
        	loggerValue.append(" " );
        	loggerValue.append(_c2sTransferVO.getReceiverNetworkCode() );
        	loggerValue.append(" " );
        	loggerValue.append(_type);
            _log.debug(methodName,  loggerValue );
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            // get the inferface module details from the cache
            final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_c2sTransferVO.getModule(),
                _c2sTransferVO.getReceiverNetworkCode(), _type);
            _intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
            _intModIPS = networkInterfaceModuleVOS.getIP();
            _intModPortS = networkInterfaceModuleVOS.getPort();
            _intModClassNameS = networkInterfaceModuleVOS.getClassName();
            // get the request string send for validation
            final String requestStr = getReceiverValidateStr();
            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "" + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());
            String receiverValResponse = null;
            final CommonClient commonClient = new CommonClient();
            // send the validation request and receive response
            receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "" + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Got the validation response from IN Module receiverValResponse=");
            	loggerValue.append(receiverValResponse);
                _log.debug(methodName, _transferID,  loggerValue );
            }

            _itemList = new ArrayList();
            _itemList.add(_senderTransferItemVO);
            _itemList.add(_receiverTransferItemVO);
            _c2sTransferVO.setTransferItemList(_itemList);

            try {
                updateForReceiverValidateResponse(receiverValResponse);
            } catch (BTSLBaseException be) {
                LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                // TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+_receiverVO.getInterfaceResponseCode());
                // validate receiver limits after Interface Validations
                PretupsBL.validateRecieverLimitsReversal(_c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
                if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }

                throw be;
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                // TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,_receiverVO.getInterfaceResponseCode());
                // validate receiver limits after Interface Validations
                PretupsBL.validateRecieverLimitsReversal((TransferVO) _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
                if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }

                throw new BTSLBaseException(this, methodName, "");
            }
            LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

            // If request is taking more time till validation of subscriber than
            // reject the request.
            InterfaceVO interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
            if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > interfaceVO.getValExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                    "C2SPostPaidReversalController[processValidationRequest]", _transferID, _senderMSISDN, _senderNetworkCode,
                    "Exception: System is taking more time till validation");
                throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
            }
            if (mcomCon == null) {
            	mcomCon = new MComConnection();con=mcomCon.getConnection();
            }

            // Get the service Class ID based on the code
            // In case of white list the service class is of the interface which
            // we get from the white list for update.
            // PretupsBL.validateServiceClassChecks(con,_receiverTransferItemVO,_c2sTransferVO,PretupsI.C2S_MODULE,_requestVO.getServiceType());
            // _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());

            // validate sender receiver service class,validate transfer value
            // PretupsBL.validateTransferRule(con,_c2sTransferVO,PretupsI.C2S_MODULE);

            // validate receiver limits after Interface Validations
            // PretupsBL.validateRecieverLimitsReversal(_c2sTransferVO,PretupsI.TRANS_STAGE_AFTER_INVAL,PretupsI.C2S_MODULE);

            // calculate card group details
            // CardGroupBL.calculateCardGroupDetails(con,_c2sTransferVO,PretupsI.C2S_MODULE,true);

            TransactionLog
                .log(
                    _transferID,
                    _requestIDStr,
                    _receiverMSISDN,
                    _receiverVO.getNetworkCode(),
                    PretupsI.TXN_LOG_REQTYPE_INT,
                    PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "After Card Group Set Id=" + _c2sTransferVO.getCardGroupSetID() + " Code" + _c2sTransferVO.getCardGroupCode() + " Card ID=" + _c2sTransferVO
                        .getCardGroupID() + " Access fee=" + _c2sTransferVO.getReceiverAccessFee() + " Tax1 =" + _c2sTransferVO.getReceiverTax1Value() + " Tax2=" + _c2sTransferVO
                        .getReceiverTax1Value() + " Bonus=" + _c2sTransferVO.getReceiverBonusValue() + " Val Type=" + _c2sTransferVO.getReceiverValPeriodType() + " Validity=" + _c2sTransferVO
                        .getReceiverValidity() + " Talk Time=" + _c2sTransferVO.getReceiverTransferValue(), PretupsI.TXN_LOG_STATUS_SUCCESS,
                    "" + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());

            // Here the code for debiting the user account will come
            // _userBalancesVO=ChannelUserBL.debitUserBalanceForProduct(con,_transferID,_c2sTransferVO);

            // Update Transfer Out Counts for the sender
            ChannelTransferBL.decreaseC2STransferOutCounts(con, _c2sTransferVO);

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

            populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            // Method to insert the record in c2s transfer table
            // added by nilesh: consolidated for logger
            if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO);
            }
            _transferDetailAdded = true;

            // Commit the transaction and relaease the locks
            try {
                con.commit();
            } catch (Exception be) {
                _log.errorTrace(methodName, be);
            }
            if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#processValidationRequest");mcomCon=null;}
            con = null;

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Marked Under process", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "" + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());

            // Log the details if the transfer Details were added i.e. if User
            // was debitted

            // Push Under Process Message to Sender and Reciever , this might
            // have to be implemented on flag basis whether to send message or
            // not
            if (_c2sTransferVO.isUnderProcessMsgReq() && _receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
                .equals(_notAllowedRecSendMessGatw)) {
                (new PushMessage(_receiverMSISDN, getReceiverUnderProcessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
            }
            if(_c2sTransferVO.isUnderProcessMsgReq() && _ussdReceiverMessageSendReq&&!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)&&!"ALL".equals(_notAllowedRecSendMessGatw))
				(new USSDPushMessage(_receiverMSISDN,getReceiverUnderProcessMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();


            // Thread.sleep(5000);
            // If request is taking more time till topup of subscriber than
            // reject the request.
            // interfaceVO=(InterfaceVO)NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
            if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > interfaceVO.getTopUpExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPostPaidReversalController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till topup");
                throw new BTSLBaseException("C2SPostPaidReversalController", "run", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
            }
            interfaceVO = null;

            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue) {
                // create new Thread
                final Thread _controllerThread = new Thread(this);
                _controllerThread.start();
                _oneLog = false;
            }
        } catch (BTSLBaseException be) {
            _requestVO.setSuccessTxn(false);
            if (con != null) {
                con.rollback();
            }
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL, new String[] { PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, String.valueOf(_transferID) }));
                }
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }
            }
            loggerValue.setLength(0);
        	loggerValue.append("Getting BTSL Base Exception:" );
        	loggerValue.append( be.getMessage());
            _log.error("C2SPostPaidReversalController[processValidationRequest]", loggerValue);
            throw be;
        } catch (Exception e) {
            _requestVO.setSuccessTxn(false);
            _log.errorTrace(methodName, e);
            if (con != null) {
                con.rollback();
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL, new String[] { PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, String.valueOf(_transferID) }));
                }
            }
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
        	if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#processValidationRequest");mcomCon=null;}
            con = null;
        }
    }

    /**
     * Method to get the reciever validate String
     * 
     * @return
     */
    public String getReceiverValidateStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getReceiverCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
        strBuff.append("&SERVICE_TYPE=" + PretupsI.SERVICE_TYPE_BILLPAYMENT);
        strBuff.append("&SERVICE_CLASS=" + _receiverTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + _receiverTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + _receiverTransferItemVO.getAccountStatus());
        strBuff.append("&CREDIT_LIMIT=" + _receiverTransferItemVO.getPreviousBalance());
        return strBuff.toString();
    }

    /**
     * Method to get the string to be sent to the interface for topup
     * 
     * @return
     */
    public String getReceiverDebitStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getReceiverCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_DEBIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + _c2sTransferVO.getReceiverTransferValue());
        strBuff.append("&CARD_GROUP=" + _c2sTransferVO.getCardGroupCode());
        strBuff.append("&BONUS_AMOUNT=" + _c2sTransferVO.getReceiverBonusValue());
        strBuff.append("&SERVICE_CLASS=" + _receiverTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + _receiverTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + _receiverTransferItemVO.getAccountStatus());
        strBuff.append("&SOURCE_TYPE=" + _c2sTransferVO.getSourceType());
        strBuff.append("&SERVICE_TYPE=" + PretupsI.SERVICE_TYPE_BILLPAYMENT);
        strBuff.append("&SENDER_ID=" + ((ChannelUserVO) _requestVO.getSenderVO()).getUserID());
        strBuff.append("&PRODUCT_CODE=" + _c2sTransferVO.getProductCode());
        strBuff.append("&TAX_AMOUNT=" + (_c2sTransferVO.getReceiverTax1Value() + _c2sTransferVO.getReceiverTax2Value()));
        strBuff.append("&ACCESS_FEE=" + _c2sTransferVO.getReceiverAccessFee());
        strBuff.append("&USER_TYPE=R");
        strBuff.append("&SENDER_MSISDN=" + _senderMSISDN);
        strBuff.append("&EXTERNAL_ID=" + _externalID);
        strBuff.append("&GATEWAY_CODE=" + _requestVO.getRequestGatewayCode());
        strBuff.append("&GATEWAY_TYPE=" + _requestVO.getRequestGatewayType());
        strBuff.append("&IMSI=" + BTSLUtil.NullToString(_imsi));
        strBuff.append("&INTERFACE_PREV_BALANCE=" + _receiverTransferItemVO.getPreviousBalance());
        strBuff.append("&CARD_GROUP=" + _c2sTransferVO.getCardGroupCode());
        // Avinash send the requested amount to IN. to use card group only for
        // reporting purpose.
        strBuff.append("&REQUESTED_AMOUNT=" + _c2sTransferVO.getRequestedAmount());
        // added by vikask for card group updation

        strBuff.append("&BONUS1=" + _c2sTransferVO.getReceiverBonus1());
        strBuff.append("&BONUS2=" + _c2sTransferVO.getReceiverBonus2());
        strBuff.append("&BONUS1_VAL=" + _c2sTransferVO.getReceiverBonus1Validity());
        strBuff.append("&BONUS2_VAL=" + _c2sTransferVO.getReceiverBonus2Validity());
        strBuff.append("&CREDIT_BONUS_VAL=" + _c2sTransferVO.getReceiverCreditBonusValidity());

        return strBuff.toString();
    }

    /**
     * Method to get the string to be sent to the interface for topup
     * 
     * @return
     */
    private String getReceiverCommonString() {
        final String methodName = "getReceiverCommonString";
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + _receiverMSISDN);
        strBuff.append("&TRANSACTION_ID=" + _transferID);
        strBuff.append("&NETWORK_CODE=" + _receiverVO.getNetworkCode());
        strBuff.append("&INTERFACE_ID=" + _receiverTransferItemVO.getInterfaceID());
        strBuff.append("&INTERFACE_HANDLER=" + _receiverTransferItemVO.getInterfaceHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeS);
        strBuff.append("&INT_MOD_IP=" + _intModIPS);
        strBuff.append("&INT_MOD_PORT=" + _intModPortS);
        strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameS);
        strBuff.append("&MODULE=" + PretupsI.C2S_MODULE);
        // added for CRE_INT_CR00029 by ankit Zindal
        strBuff.append("&CARD_GROUP_SELECTOR=" + _requestVO.getReqSelector());
        strBuff.append("&USER_TYPE=R");
        strBuff.append("&REQ_SERVICE=" + _serviceType);
        strBuff.append("&INT_ST_TYPE=" + _c2sTransferVO.getReceiverInterfaceStatusType());
        strBuff.append("&RECEIVER_MSISDN=" + _receiverMSISDN);
        strBuff.append("&SOURCE_TYPE=" + _requestVO.getSourceType());
        strBuff.append("&SELECTOR_BUNDLE_ID=" + _receiverBundleID);
        strBuff.append("&SUB_SERVICE=" + _c2sTransferVO.getSubService());

		// FIXME added for fix 17005 error on ClaroCollEnqWSINHandler.setInterfaceParameters(888) -- Emmanuel Leguizamon
		strBuff.append("&SENDER_NAME="+_channelUserVO.getUserName());
		strBuff.append("&SENDER_ID="+_channelUserVO.getUserID());
	
        try {
            final Format formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            final String dateString = formatter.format(_currentDate);
            strBuff.append("&TXN_DATE=" + dateString);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        strBuff.append("&INFO1=" + _requestVO.getInfo1());
        strBuff.append("&INFO2=" + _requestVO.getInfo2());
        strBuff.append("&INFO3=" + _requestVO.getInfo3());
        try {
            final Format formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            final String dateString = formatter.format(_c2sTransferVO.getOldtransferDateTime());
            strBuff.append("&BILL_PAY_TXN_DATE=" + dateString);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        strBuff.append("&BILL_PAY_INFO1=" + _oldInfo1);
        strBuff.append("&BILL_PAY_INFO2=" + _oldInfo2);
        strBuff.append("&BILL_PAY_INFO3=" + _oldInfo3);
    	strBuff.append("&OWNER_NAME="+_channelUserVO.getOwnerName());
	    strBuff.append("&COMPANY="+_channelUserVO.getOwnerCompany());
	    strBuff.append("&OWNER_ID="+_channelUserVO.getOwnerID());
        return strBuff.toString();
    }

    /**
     * Method to get the success message to be sent to receiver
     * 
     * @return
     */
    private String getReceiverSuccessMessage() {
        final String methodName = "getReceiverSuccessMessage";
        String[] messageArgArray = null;
        String key = null;
        messageArgArray = new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), _senderPushMessageMsisdn, _transferID, _c2sTransferVO
            .getSubService(), _channelUserVO.getUserName(), String.valueOf(_c2sTransferVO.getReceiverBonus1()), String.valueOf(_c2sTransferVO.getReceiverBonus2()), String
            .valueOf(_c2sTransferVO.getReceiverBonus1Validity()), String.valueOf(_c2sTransferVO.getReceiverBonus2Validity()), String.valueOf(_c2sTransferVO
            .getReceiverCreditBonusValidity()), _requestVO.getPosUserMSISDN() };
        key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_REVERSAL;
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_BILLPAY))).booleanValue()) {
            String message = null;
            try {
                message = BTSLUtil.getMessage(_receiverLocale, key + "_" + _receiverTransferItemVO.getServiceClass(), messageArgArray, _requestVO.getRequestGatewayType());
                if (!BTSLUtil.isNullString(message)) {
                    return message;
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        return BTSLUtil.getMessage(_receiverLocale, key, messageArgArray, _requestVO.getRequestGatewayType());
    }

    private String getSenderSuccessMessage() {
        final String[] messageArgArray = { PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), _transferID, _requestVO.getSid(), PretupsBL
            .getDisplayAmount(_senderTransferItemVO.getPostBalance()), _c2sTransferVO.getSubService() };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_SUCCESS_REVERSAL, messageArgArray);
    }

    /**
     * Method to get the under process message to be sent to receiver
     * 
     * @return
     */
    private String getReceiverUnderProcessMessage() {
        final String[] messageArgArray = { PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, _transferID, _channelUserVO
            .getUserName(), _requestVO.getPosUserMSISDN() };
        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_UNDERPROCESS_REVERSAL, messageArgArray, _requestVO.getRequestGatewayType());
    }

    /**
     * Method to get the success message to be sent to sender
     * 
     * @return
     */
    private String getSenderUnderProcessMessage() {
        final String[] messageArgArray = { PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _transferID, _requestVO.getSid(), PretupsBL
            .getDisplayAmount(_senderTransferItemVO.getPostBalance()) };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS_REVERSAL, messageArgArray);
    }

    /**
     * Method to get the under process message before validation to be sent to
     * sender
     * 
     * @return
     */
    private String getSndrUPMsgBeforeValidation() {
        final String[] messageArgArray = { PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _transferID, _requestVO.getSid() };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS_B4VAL_REVERSAL, messageArgArray);
    }

    private String getReceiverAmbigousMessage() {
        final String[] messageArgArray = { _senderPushMessageMsisdn, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _channelUserVO
            .getUserName(), _requestVO.getPosUserMSISDN() };
        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_AMBIGOUS_REVERSAL, messageArgArray, _requestVO.getRequestGatewayType());
    }
  

    /**
     * Sets the sender transfer Items VO for the channel user
     * 
     */
    private void setSenderTransferItemVO() {
        _senderTransferItemVO = new C2STransferItemVO();
        // set sender transfer item details
        _senderTransferItemVO.setSNo(1);
        _senderTransferItemVO.setMsisdn(_senderMSISDN);
        _senderTransferItemVO.setRequestValue(_c2sTransferVO.getRequestedAmount());
        _senderTransferItemVO.setSubscriberType(_senderSubscriberType);
        _senderTransferItemVO.setTransferDate(_currentDate);
        _senderTransferItemVO.setTransferDateTime(_currentDate);
        _senderTransferItemVO.setTransferID(_c2sTransferVO.getTransferID());
        _senderTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
        _senderTransferItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
        _senderTransferItemVO.setEntryDate(_currentDate);
        _senderTransferItemVO.setEntryDateTime(_currentDate);
        _senderTransferItemVO.setEntryType(PretupsI.DEBIT);
        _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
        _senderTransferItemVO.setPrefixID((_channelUserVO.getUserPhoneVO()).getPrefixID());
    }

    /**
     * Sets the receiever transfer Items VO for the subscriber
     * 
     */
    private void setReceiverTransferItemVO() {
        _receiverTransferItemVO = new C2STransferItemVO();
        _receiverTransferItemVO.setSNo(2);
        _receiverTransferItemVO.setMsisdn(_receiverMSISDN);
        _receiverTransferItemVO.setRequestValue(_c2sTransferVO.getRequestedAmount());
        _receiverTransferItemVO.setSubscriberType(_receiverVO.getSubscriberType());
        _receiverTransferItemVO.setTransferDate(_currentDate);
        _receiverTransferItemVO.setTransferDateTime(_currentDate);
        _receiverTransferItemVO.setTransferID(_c2sTransferVO.getTransferID());
        _receiverTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
        _receiverTransferItemVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
        _receiverTransferItemVO.setEntryDate(_currentDate);
        _receiverTransferItemVO.setEntryDateTime(_currentDate);
        _receiverTransferItemVO.setEntryType(PretupsI.CREDIT);
        _receiverTransferItemVO.setPrefixID(_receiverVO.getPrefixID());
        _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.RECEIVER_UNDERPROCESS_SUCCESS);
    }

    /**
     * Method that will perform the validation request in thread
     * 
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequestInThread() throws BTSLBaseException, Exception {
        final String methodName = "processValidationRequestInThread";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered and performing validations for transfer ID=");
        	loggerValue.append(_transferID);
            _log.debug(methodName,  loggerValue );
        }
        try {
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "" + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());
            processValidationRequest();
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("Getting BTSL Base Exception:");
        	loggerValue.append( be.getMessage());
            _log.error("C2SPostPaidReversalController[processValidationRequestInThread]",  loggerValue.toString());
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey() + " ,Old Txn Id=" + _c2sTransferVO
                    .getOldTxnId());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                    .getRequestedAmount()), _senderPushMessageMsisdn, String.valueOf(_transferID) }));
            }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            loggerValue.setLength(0);
            loggerValue.append("Exception:" );
            loggerValue.append( e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "C2SPostPaidReversalController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage() + " ,Old Txn Id=" + _c2sTransferVO
                    .getOldTxnId());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_transferID != null) {
                Connection con = null;MComConnectionI mcomCon = null;
                try {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    // added by nilesh:consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        addEntryInTransfers(con);
                    }
                    if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        _finalTransferStatusUpdate = false; // No need to update
                        // the status of
                        // transaction in
                        // run method
                    }
                } catch (BTSLBaseException be) {
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (Exception ex) {
                            _log.errorTrace(methodName, ex);
                        }
                    }
                    _log.errorTrace(methodName, be);
                } catch (Exception e) {
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (Exception ex) {
                            _log.errorTrace(methodName, ex);
                        }
                    }
                    _log.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "C2SPostPaidReversalController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
                } finally {
                	if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#processValidationRequestInThread");mcomCon=null;}
                    con = null;
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    /**
     * Method that will add entry in Transfer Table if not added else update the
     * records
     * 
     * @param p_con
     */
    private void addEntryInTransfers(Connection p_con) {
        final String methodName = "addEntryInTransfers";
        try {
            // METHOD FOR INSERTING AND UPDATION IN C2S Transfer Table
            if (!_transferDetailAdded && _transferEntryReqd) {
                ChannelTransferBL.addC2STransferDetails(p_con, _c2sTransferVO);// add
                // transfer
                // details
                // in
                // database
            } else if (_transferDetailAdded) {
                _c2sTransferVO.setModifiedOn(new Date());
                _c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                ChannelTransferBL.updateC2STransferDetails(p_con, _c2sTransferVO);// add
                // transfer
                // details
                // in
                // database
            }
            p_con.commit();
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            if (!_isCounterDecreased && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPostPaidReversalController[addEntryInTransfers]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            if (!_isCounterDecreased && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
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
     *            : RECEIVER ONLY
     * @param p_action
     *            : VALIDATE OR UPDATE
     * @return
     */
    private boolean getInterfaceRoutingDetails(Connection p_con, String p_msisdn, long p_prefixID, String p_subscriberType, String p_networkCode, String p_serviceType, String p_interfaceCategory, String p_userType, String p_action) throws BTSLBaseException {
        final String methodName = "getInterfaceRoutingDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered with MSISDN=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(" Prefix ID=");
        	loggerValue.append(p_prefixID);
        	loggerValue.append(" p_subscriberType=");
        	loggerValue.append(p_subscriberType);
        	loggerValue.append( " p_networkCode=" );
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_serviceType=" );
        	loggerValue.append(p_serviceType);
        	loggerValue.append(" p_interfaceCategory=");
        	loggerValue.append(p_interfaceCategory);
        	loggerValue.append(" p_userType=" );
        	loggerValue.append(p_userType);
        	loggerValue.append(" p_action=" );
        	loggerValue.append(p_action);
            _log.debug(methodName,loggerValue );
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
        final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
            .getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
        try {
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(" subscriberRoutingControlVO=" );
            	loggerValue.append(subscriberRoutingControlVO);
                _log.debug(methodName, loggerValue );
            }

            if (subscriberRoutingControlVO != null) {
                if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)) {
                        final WhiteListVO whiteListVO = PretupsBL.validateNumberInWhiteList(p_con, p_msisdn);
                        if (whiteListVO != null) {
                            final ListValueVO listValueVO = whiteListVO.getListValueVO();
                            if (p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                _receiverInterfaceInfoInDBFound = true;
                            }
                            _externalID = listValueVO.getIDValue();
                            _interfaceStatusType = listValueVO.getStatusType();
                            isSuccess = true;
                            _c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
                            _receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
                            _receiverTransferItemVO.setInterfaceType(_type);
                            _receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
                            if (PretupsI.YES.equals(listValueVO.getType())) {
                                _c2sTransferVO.setUnderProcessMsgReq(true);
                            }
                            _receiverAllServiceClassID = listValueVO.getTypeName();
                            _receiverVO.setPostOfflineInterface(true);
                            if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
                                // ChangeID=LOCALEMASTER
                                // Check which language message to be sent from
                                // the locale master table for the perticuler
                                // locale.
                                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                } else {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
                            }
                            _receiverTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
                            _receiverTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
                            _receiverTransferItemVO.setReferenceID(whiteListVO.getAccountID());
                            _receiverTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
                            _receiverSubscriberType = p_interfaceCategory;
                            _imsi = whiteListVO.getImsi();
                            // If use interface language flag is Y in service
                            // types table then update the receiver locale
                            if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
                                try {
                                    _receiverLocale = new Locale(whiteListVO.getLanguage(), whiteListVO.getCountry());
                                } catch (Exception e) {
                                    _log.errorTrace(methodName, e);
                                    loggerValue.setLength(0);
                                	loggerValue.append("Exception: Notification language from white list is not defined in system p_language: " );
                                	loggerValue.append(whiteListVO.getLanguage());
                                	loggerValue.append(" country=");
                                	loggerValue.append(whiteListVO.getCountry());
                                    EventHandler
                                        .handle(
                                            EventIDI.SYSTEM_INFO,
                                            EventComponentI.SYSTEM,
                                            EventStatusI.RAISED,
                                            EventLevelI.MAJOR,
                                            "C2SPostPaidReversalController[getInterfaceRoutingDetails]",
                                            _transferID,
                                            _receiverMSISDN,
                                            "",loggerValue.toString());
                                }
                            }
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {

                            isSuccess = performSeriesBasedRouting(p_prefixID, p_subscriberType, p_action, p_networkCode);
                        } else {
                            isSuccess = false;
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NUMBER_NOT_EXISTS_IN_WHITELIST);
                        }
                    }
                } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {

                    isSuccess = performSeriesBasedRouting(p_prefixID, p_subscriberType, p_action, p_networkCode);
                } else {
                    isSuccess = false;
                }
            } else {
                // This event is raised by ankit Z on date 3/8/06 for case when
                // entry not found in routing control and considering series
                // based routing
            	
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                    "C2SPostPaidReversalController[getInterfaceRoutingDetails]", _transferID, _senderMSISDN, _senderNetworkCode,
                    "Exception:Routing control information not defined so performing series based routing");
                isSuccess = performSeriesBasedRouting(p_prefixID, p_subscriberType, p_action, p_networkCode);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "C2SPostPaidReversalController[getInterfaceRoutingDetails]", _transferID, _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
            isSuccess = false;
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exiting with isSuccess=");
        	loggerValue.append(isSuccess);
            _log.debug(methodName,  loggerValue);
        }
        return isSuccess;
    }

    /**
     * Method to validate the Validate response
     * 
     * @param str
     * @param p_attempt
     * @param p_altSize
     * @throws BTSLBaseException
     */
    public void receiverValidateResponse(String str, int p_attempt, int p_altSize) throws BTSLBaseException {
        final String methodName = "receiverValidateResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) {
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        }
        if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
            // update the receiver locale if language code returned from IN is
            // not null
            updateReceiverLocale((String) map.get("IN_LANG"));
        }
        if (!BTSLUtil.isNullString(_receiverSubscriberType)) {
            _receiverTransferItemVO.setSubscriberType(_receiverSubscriberType);
        }
        _receiverVO.setSubscriberType(_receiverTransferItemVO.getSubscriberType());
        _receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        _receiverTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
        _receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        _receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
        _receiverTransferItemVO.setValidationStatus(status);
        _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());

        // set from IN Module
        // if(BTSLUtil.isNumeric((String)map.get("IN_TXN_ID")))
        // {
        try {
            _receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                "C2SPostPaidReversalController[receiverValidateResponse]", _transferID, _senderMSISDN, _senderNetworkCode,
                "Exception while parsing for interface txn ID , Exception:" + e.getMessage());
        }
        // }

        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        String[] strArr = null;

        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            _c2sTransferVO.setErrorCode(status + "_R");
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(status);
            _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderMSISDN, _transferID, };
            throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL, 0, strArr, null);
        }
        try {
            _receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        _receiverTransferItemVO.setTransferStatus(status);
        _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        _receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        // Done so that receiver check can be brough to common
        _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
        _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
        // Update the Previous Balance in case of Post Paid Offline interface
        // with Credit Limit - Monthly Transfer Amount
        if (_receiverVO.isPostOfflineInterface()) {
            final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(_receiverVO.getLastSuccessOn(), _currentDate, BTSLUtil.PERIOD_MONTH);
            if (!isPeriodChange) {
                _receiverTransferItemVO.setPreviousBalance(_receiverTransferItemVO.getPreviousBalance() - _receiverVO.getMonthlyTransferAmount());
            }
        }

    }

    /**
     * Method to process request from queue
     * 
     * @param p_transferVO
     */
    public void processFromQueue(TransferVO p_transferVO) {
        final String methodName = "processFromQueue";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            _c2sTransferVO = (C2STransferVO) p_transferVO;
            _requestVO = _c2sTransferVO.getRequestVO();
            _channelUserVO = (ChannelUserVO) _requestVO.getSenderVO();
            _type = _requestVO.getType();
            _requestID = _requestVO.getRequestID();
            _requestIDStr = _requestVO.getRequestIDStr();
            _receiverLocale = _requestVO.getReceiverLocale();
            _transferID = _c2sTransferVO.getTransferID();
            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
            _senderMSISDN = (_channelUserVO.getUserPhoneVO()).getMsisdn();
            _receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
            _senderLocale = _requestVO.getSenderLocale();
            _senderNetworkCode = _channelUserVO.getNetworkID();
            _serviceType = _requestVO.getServiceType();
            _senderTransferItemVO = _c2sTransferVO.getSenderTransferItemVO();
            _receiverTransferItemVO = _c2sTransferVO.getReceiverTransferItemVO();
            _transferEntryReqd = true;
            _receiverSubscriberType = _c2sTransferVO.getReceiverSubscriberType();

            LoadController.checkTransactionLoad(((ReceiverVO) _c2sTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE,
                _transferID, true, LoadControllerI.USERTYPE_SENDER);
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            // Loading C2S receiver's controll parameters
            // added by PN(25/03/08) to resolve the issude of duplicate request
            // processing
            _c2sTransferVO.setUnderProcessCheckReqd(_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
            PretupsBL.loadRecieverControlLimits(con, _requestIDStr, _c2sTransferVO);
            _receiverVO.setUnmarkRequestStatus(true);
            try {
                con.commit();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#processFromQueue");mcomCon=null;}
            con = null;

            if (_log.isDebugEnabled()) {
                _log.debug("C2SPostPaidReversalController[processFromQueue]", "_transferID=" + _transferID + " Successfully through load");
            }
            _processedFromQueue = true;

            processValidationRequest();
            // Set under process message for the sender and reciever
            p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
            final String[] messageArgArray = { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
            p_transferVO.setMessageArguments(messageArgArray);
        } catch (BTSLBaseException be) {
        	if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#processFromQueue");mcomCon=null;}
            con = null;
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) { // getting
                    // database
                    // connection
                    // if
                    // it
                    // is
                    // not
                    // already
                    // there
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (be.isKey()) // checking if baseexception has key
            {
                _c2sTransferVO.setErrorCode(be.getMessageKey());
                _c2sTransferVO.setMessageCode(be.getMessageKey());
                _c2sTransferVO.setMessageArguments(be.getArgs());
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            // checking whether need to decrease the transaction load, if it is
            // already increased
            LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            _isCounterDecreased = true;
            // making entry in the transaction log
            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode() + " ,Old Txn Id=" + _c2sTransferVO
                    .getOldTxnId());
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
        	if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#processFromQueue");mcomCon=null;}
            con = null;
            _log.errorTrace(methodName, e);
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            // checking condition whether channel receiver required the general
            // failure message
            if (_recValidationFailMessageRequired) {
                // if receivermessage is null or it is not key
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // setting receiver return message
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_REVERSAL, new String[] { PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, String.valueOf(_transferID) }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _c2sTransferVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

            _log.errorTrace(methodName, e);

            // decreasing the transaction load count
            LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            _isCounterDecreased = true;

            // raising alarm
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPostPaidReversalController[processFromQueue]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            // logging in the transaction log
            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode() + " ,Old Txn Id=" + _c2sTransferVO
                    .getOldTxnId());
        } finally {
            try {
                if (mcomCon == null) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
                // makking entry in the transfer table if transfer entry has not
                // been made and message gateway flow is common, i.e. validation
                // is not in thread
                if (_transferID != null && !_transferDetailAdded) {
                    // added by nilesh:consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        addEntryInTransfers(con);
                    }
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
                loggerValue.append("Exception:" );
                loggerValue.append( e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPostPaidReversalController[processFromQueue]",
                    _transferID, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            }

            if (BTSLUtil.isNullString(_c2sTransferVO.getMessageCode())) {
                _c2sTransferVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }

            if (con != null) {
                // committing transaction and closing connection
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                if(mcomCon != null){mcomCon.close("C2SPostPaidReversalController#processFromQueue");mcomCon=null;}
                con = null;
            }// end if

            if (_receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
                .equals(_notAllowedRecSendMessGatw)) {
                // checking if receiver message is not null and receiver return
                // message is key
                if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // generating message and pushing it to receiver
                    final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                    (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                        _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                } else if (_c2sTransferVO.getReceiverReturnMsg() != null) {
                    (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                        .push();
                }
            }
            if(_ussdReceiverMessageSendReq&&!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)&&!"ALL".equals(_notAllowedRecSendMessGatw))
			{
				//checking if receiver message is not null and receiver return message is key
				if(_c2sTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)_c2sTransferVO.getReceiverReturnMsg()).isKey())
				{
					//generating message and pushing it to receiver
					BTSLMessages btslRecMessages=(BTSLMessages)_c2sTransferVO.getReceiverReturnMsg();
					(new USSDPushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale,btslRecMessages.getMessageKey(),btslRecMessages.getArgs()),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
				}
				else if(_c2sTransferVO.getReceiverReturnMsg()!=null) //pushing message to receiver
					(new USSDPushMessage(_receiverMSISDN,(String)_c2sTransferVO.getReceiverReturnMsg(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
			}
            // making entry in the transaction log
            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + _requestVO.getMessageCode() + " ,Old Txn Id=" + _c2sTransferVO.getOldTxnId());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
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
    public void updateReceiverLocale(String p_languageCode) {
        final String methodName = "updateReceiverLocale";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_languageCode=" + p_languageCode);
        }
        // check if language is returned fron IN or not.
        // If not then send alarm and not set the locale
        // otherwise set the local corresponding to the code returned from the
        // IN.
        if (!BTSLUtil.isNullString(p_languageCode)) {
            try {
                if (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode) == null) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                        "C2SPostPaidReversalController[updateReceiverLocale]", _transferID, _receiverMSISDN, "",
                        "Exception: Notification language returned from IN is not defined in system p_languageCode: " + p_languageCode);
                } else {
                    _receiverLocale = (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode));
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited _receiverLocale=" + _receiverLocale);
        }
    }

    /**
     * Method: performSeriesBasedRouting
     * This method to perform series based interface routing
     * 
     * @param p_prefixID
     *            long
     * @param p_subscriberType
     *            String
     * @param p_action
     *            String
     * 
     * @return boolean
     */
    // VASTRIX CHANGES
    public boolean performSeriesBasedRouting(long p_prefixID, String p_subscriberType, String p_action, String p_networkCode) throws BTSLBaseException{
        boolean isSuccess = false;
        StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "performSeriesBasedRouting";
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_prefixID=");
        	loggerValue.append(p_prefixID);
        	loggerValue.append(" p_subscriberType=");
        	loggerValue.append(p_subscriberType);
        	loggerValue.append(" p_action=");
        	loggerValue.append(p_action);
        	loggerValue.append("p_networkCode");
        	loggerValue.append(p_networkCode);
            _log.debug(methodName,  loggerValue);
        }
        try {
            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
            // added by rahul.d to check service selector based check load of
            // interface
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache
                    .getObject(_serviceType + "_" + _c2sTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                if (interfaceMappingVO1 != null) {
                    _receiverTransferItemVO.setInterfaceID(interfaceMappingVO1.getInterfaceID());
                    _receiverTransferItemVO.setInterfaceType(_type);
                    _receiverTransferItemVO.setInterfaceHandlerClass(interfaceMappingVO1.getHandlerClass());
                    if (PretupsI.YES.equals(interfaceMappingVO1.getUnderProcessMsgRequired())) {
                        _c2sTransferVO.setUnderProcessMsgReq(true);
                    }
                    _receiverAllServiceClassID = interfaceMappingVO1.getAllServiceClassID();
                    _externalID = interfaceMappingVO1.getExternalID();
                    _interfaceStatusType = interfaceMappingVO1.getStatusType();
                    isSuccess = true;
                    _receiverSubscriberType = p_subscriberType;
                    if (!PretupsI.YES.equals(interfaceMappingVO1.getInterfaceStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceMappingVO1.getStatusType())) {
                        if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                            _c2sTransferVO.setSenderReturnMessage(interfaceMappingVO1.getLanguage1Message());
                        } else {
                            _c2sTransferVO.setSenderReturnMessage(interfaceMappingVO1.getLanguage2Message());
                        }
                        throw new BTSLBaseException(this, "getInterfaceRoutingDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
                    }
                }
            }
            if (interfaceMappingVO1 == null) {
                final MSISDNPrefixInterfaceMappingVO interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID,
                    p_subscriberType, p_action);
                _receiverTransferItemVO.setInterfaceID(interfaceMappingVO.getInterfaceID());
                _receiverTransferItemVO.setInterfaceType(_type);
                _receiverTransferItemVO.setInterfaceHandlerClass(interfaceMappingVO.getHandlerClass());
                if (PretupsI.YES.equals(interfaceMappingVO.getUnderProcessMsgRequired())) {
                    _c2sTransferVO.setUnderProcessMsgReq(true);
                }
                _receiverAllServiceClassID = interfaceMappingVO.getAllServiceClassID();
                _externalID = interfaceMappingVO.getExternalID();
                _interfaceStatusType = interfaceMappingVO.getStatusType();
                _c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
                _receiverSubscriberType = p_subscriberType;
                isSuccess = true;

                if (!PretupsI.YES.equals(interfaceMappingVO.getInterfaceStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceMappingVO.getStatusType())) {
                    // ChangeID=LOCALEMASTER
                    // Check which language message to be sent from the locale
                    // master table for the perticuler locale.
                    if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                        _c2sTransferVO.setSenderReturnMessage(interfaceMappingVO.getLanguage1Message());
                    } else {
                        _c2sTransferVO.setSenderReturnMessage(interfaceMappingVO.getLanguage2Message());
                    }
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "");
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Exited isSuccess=");
        	loggerValue.append(isSuccess);
            _log.debug(methodName, loggerValue);
        }
        return isSuccess;
    }

    private static synchronized void generateReversalTransferID(TransferVO p_transferVO) {
        final String methodName = "generateReversalTransferID";
        String transferID = null;
        String minut2Compare = null;
        Date mydate = null;
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
                throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = _operatorUtil.formatTransferID(p_transferVO, _transactionIDCounter, "X");
            if (transferID == null) {
                throw new BTSLBaseException("C2SPostPaidReversalController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
    }

    private String getSenderParentSuccessMessage() {
        final String[] messageArgArray = { PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), _transferID, _requestVO.getSid(), PretupsBL
            .getDisplayAmount(_senderTransferItemVO.getPostBalance()), _c2sTransferVO.getSubService(), _requestVO.getSenderLoginID() };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_PARENT_SUCCESS_REVERSAL, messageArgArray);
    }
}