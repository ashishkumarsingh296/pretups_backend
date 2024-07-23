package com.btsl.pretups.channel.transfer.requesthandler;

/*
 * @(#)C2SBillPaymentController
 * Name Date History
 * ------------------------------------------------------------------------
 * Sourabh Gupta Nov 28, 2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * Controller class for handling Utility Bill payment request
 */
import java.sql.Connection;
import java.sql.SQLException;
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
import com.btsl.pretups.cardgroup.businesslogic.CardGroupBL;
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
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.ResumeSuspendProcess;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberBL;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
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
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class C2SBillPaymentController extends C2SBaseController implements ServiceKeywordControllerI, Runnable {
    private static Log _log = LogFactory.getLog(C2SBillPaymentController.class.getName());
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
    private boolean _finalTransferStatusUpdate = true;
    private boolean _transferEntryReqd = false;
    private boolean _decreaseTransactionCounts = false;
    private UserBalancesVO _userBalancesVO = null;
    private boolean _creditBackEntryDone = false;
    private boolean _receiverInterfaceInfoInDBFound = false;
    private String _receiverAllServiceClassID = PretupsI.ALL;
    private String _receiverPostBalanceAvailable;
    private Locale _senderLocale = null;
    private Locale _receiverLocale = null;
    private String _externalID = null;
    private RequestVO _requestVO = null;
    private boolean _processedFromQueue = false;
    private static boolean _recValidationFailMessageRequired = false;
    private static boolean _recTopupFailMessageRequired = false;
    private static String _notAllowedSendMessGatw;
    private static String _notAllowedRecSendMessGatw;
    private String _receiverSubscriberType = null;
    public static OperatorUtilI _operatorUtil = null;
    private String _receiverNotificationMSISDN = null;
    private boolean _receiverMessageSendReq = false;
    private String _interfaceStatusType = null;
    private static int _transactionIDCounter = 0;
    private static long _prevReqTime = 0;
    private static int _prevMinut = 0;
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    private String _senderPushMessageMsisdn = null;

    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
        // check for validation fail message is send to reciever or not
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("C2S_REC_GEN_FAIL_MSG_REQD_V_UB")))) {
            _recValidationFailMessageRequired = true;
        }
        // check topup fail message is send to reciever or not
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("C2S_REC_GEN_FAIL_MSG_REQD_T_UB")))) {
            _recTopupFailMessageRequired = true;
        }
        // check for message is not send for which gateway
        _notAllowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2S_SEN_MSG_NOT_REQD_GW_UB"));
        _notAllowedRecSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_NOT_REQD_GW_UB"));

    }

    // construtor
    public C2SBillPaymentController() {
        _c2sTransferVO = new C2STransferVO();
        _currentDate = new Date();

    }

    /**
     * Method to process the request of the Utility Bill Payment
     * 
     * @param object
     *            of the RequestVO
     */
    public void process(RequestVO p_requestVO) {
        final String methodName = "process";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered for Request ID=");
        	loggerValue.append(p_requestVO.getRequestID());
        	loggerValue.append(" MSISDN=" );
        	loggerValue.append(p_requestVO.getFilteredMSISDN());
        	loggerValue.append(" _recValidationFailMessageRequired: ");
        	loggerValue.append(_recValidationFailMessageRequired);
        	loggerValue.append(" _recTopupFailMessageRequired");
        	loggerValue.append(_recTopupFailMessageRequired);
        	loggerValue.append(" _notAllowedSendMessGatw: ");
        	loggerValue.append(_notAllowedSendMessGatw );
        	loggerValue.append(" ");
            _log.debug(methodName,p_requestVO.getRequestIDStr(),loggerValue );
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
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
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // Validating user message incomming in the request
            _operatorUtil.validateUtilityBillPaymentRequest(con, _c2sTransferVO, p_requestVO);
            _receiverNotificationMSISDN = p_requestVO.getNotificationMSISDN();
            // Block added to avoid decimal amount in credit transfer
            try {
                _operatorUtil.validateDecimalAmount(_serviceType, _c2sTransferVO.getRequestedAmount());
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERR_INVALID_AMOUNT_UB);
            }
            _receiverLocale = p_requestVO.getReceiverLocale();
            _senderLocale = p_requestVO.getSenderLocale();
            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));
            // check for the subscriber type is defined in the system
            // check if prepaid subcriber wants to use this service
            if (!_receiverVO.getSubscriberType().equals(_type)) {
                // Refuse the Request
            	loggerValue.setLength(0);
            	loggerValue.append("Series =");
            	loggerValue.append(_receiverVO.getMsisdnPrefix());
            	loggerValue.append(" Not Defined for Series type=");
            	loggerValue.append(_type);
                _log.error(methodName,  loggerValue );
                
                loggerValue.setLength(0);
            	loggerValue.append("Series =");
            	loggerValue.append(_receiverVO.getMsisdnPrefix());
            	loggerValue.append(" Not Defined for Series type=");
            	loggerValue.append(_type);
            	loggerValue.append(" But request initiated for the same");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SBillPaymentController[process]", "", "", "",
                		loggerValue.toString());
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERR_NOTFOUND_SERIES_TYPE_UB, 0, new String[] { _receiverVO.getMsisdn() }, null);
            }
            _receiverVO.setModule(_c2sTransferVO.getModule());
            _receiverVO.setCreatedDate(_currentDate);
            _receiverVO.setLastTransferOn(_currentDate);
            _senderMSISDN = (_channelUserVO.getUserPhoneVO()).getMsisdn();
            _senderPushMessageMsisdn = p_requestVO.getMessageSentMsisdn();
            _receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
            _c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
            _c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
            _c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
            _c2sTransferVO.setSubService(p_requestVO.getReqSelector());
            _c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
            _receiverSubscriberType = _receiverVO.getSubscriberType();
            // restricted MSISDN check
            RestrictedSubscriberBL.isRestrictedMsisdnExistForC2S(con, _c2sTransferVO, _channelUserVO, _receiverVO.getMsisdn(), _c2sTransferVO.getRequestedAmount());
            // Validates the network service status
            PretupsBL.validateNetworkService(_c2sTransferVO);
            // _receiverMessageSendReq=true;

            // if service type preference allow to send the receiver SMS.
            _receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW, _receiverVO.getNetworkCode(), _serviceType))
                .booleanValue();

            // checking whether self bill payment is allowed or not
            if (_senderMSISDN.equals(_receiverMSISDN) && !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_ALLOW_SELF_UB))).booleanValue()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_SELF_UTILITYBIL_NTALLOWD);
            }
            // Chcking senders transfer profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getTransferProfileStatus())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_SNDR_TRANPROFILE_SUSPEND_UB);
            }
            // Chcking senders commission profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getCommissionProfileStatus())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_SNDR_COMMPROFILE_SUSPEND_UB);
            } else if (PretupsI.YES.equalsIgnoreCase(_channelUserVO.getOutSuspened())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_SNDR_OUT_SUSPEND_UB);
            }
            // check if receiver barred in PreTUPS or not, user should not be
            // barred.
            try {
                PretupsBL.checkMSISDNBarred(con, _receiverMSISDN, _receiverVO.getNetworkCode(), _c2sTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
            } catch (BTSLBaseException be) {
                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERR_RECEIVER_BARRED_UB, new String[] {}));
                }
                throw be;
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
                mcomCon.partialCommit();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
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
                    "Source Type=" + _c2sTransferVO.getSourceType() + " Gateway Code=" + _c2sTransferVO.getRequestGatewayCode());
                // populate payment and service interface details
                populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
                // This will be used in validate ReceiverLimit method of
                // PretupsBL when receiverTransferItemVO is null
                _c2sTransferVO.setReceiverSubscriberType(_receiverSubscriberType);
                // validate receiver limits before Interface Validations
                PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
                // Validate Sender Transaction profile checks and balance
                // availablility for user
                ChannelUserBL.validateSenderAvailableControls(con, _transferID, _c2sTransferVO);
                // setting validation status
                _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                // commiting transaction and closing the transaction as it is
                // not requred
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
                }
				if (mcomCon != null) {
					mcomCon.close("C2SBillPaymentController#process");
					mcomCon = null;
				}
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
                    // Process validation requests and start thread for the
                    // topup
                    processValidationRequest();
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    p_requestVO.setSenderMessageRequired(_c2sTransferVO.isUnderProcessMsgReq());
                    p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());
                    // Parameter set to indicate that instance counters will not
                    // be decreased in receiver for this transaction
                    p_requestVO.setDecreaseLoadCounters(false);
                }
                // starting validation and topup process in thread
                else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    // Check if message needs to be sent in case of Thread
                    // implmentation
                    p_requestVO.setSenderReturnMessage(getSndrUPMsgBeforeValidation());
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    final Thread _controllerThread = new Thread(this);
                    // starting thread
                    _controllerThread.start();
                    // Parameter set to indicate that instance counters will not
                    // be decreased in receiver for this transaction
                    p_requestVO.setDecreaseLoadCounters(false);
                }
            }
        } catch (BTSLBaseException be) {
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
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                loggerValue.setLength(0);
            	loggerValue.append("Leaving Reciever Unmarked Base Exception:" );
            	loggerValue.append(bex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SBillPaymentController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
                loggerValue.setLength(0);
            	loggerValue.append("Leaving Reciever Unmarked Base Exception:" );
            	loggerValue.append( e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SBillPaymentController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
            }
            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_UB, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_UB, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
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
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
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
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
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
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SBillPaymentController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SBillPaymentController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + ex.getMessage());
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (_recValidationFailMessageRequired) {
                // if receivermessage is null or it is not key
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_UB, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_UB, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            // decreasing the transaction load count
            if (_transferID != null && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            // raising alarm
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[process]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            // logging in the transaction log
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            _log.errorTrace(methodName, e);
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
                if (_transferID != null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || (p_requestVO
                    .getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(
                    PretupsI.TXN_STATUS_UNDER_PROCESS)))) {
                    addEntryInTransfers(con);
                } else if (_transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    _log.info(methodName, p_requestVO.getRequestIDStr(),
                        "Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + _transferID + " But not added entry in Transfers yet");
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            }
            if (con != null) {
                // committing transaction and closing connection
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                if(mcomCon != null){mcomCon.close("C2SBillPaymentController#process");mcomCon=null;}
            }// end if
            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (_isCounterDecreased) {
                p_requestVO.setDecreaseLoadCounters(false);
            }

            if (_receiverMessageSendReq && _recValidationFailMessageRequired && !BTSLUtil.isNullString(_receiverNotificationMSISDN) && !BTSLUtil.isStringIn(_c2sTransferVO
                .getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL".equals(_notAllowedRecSendMessGatw)) {
                // checking if receiver message is not null and receiver return
                // message is key
                if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // generating message and pushing it to receiver
                    final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                    (new PushMessage(_receiverNotificationMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()),
                        _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                } else if (_c2sTransferVO.getReceiverReturnMsg() != null) {
                    (new PushMessage(_receiverNotificationMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
                        _receiverLocale)).push();
                }
            }
            // making entry in the transaction log
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[processSKeyGen]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }// end of processSKeyGen

    /**
     * Method to process prepare the transferitems & find the product
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
            _c2sTransferVO.setTransferDateTime(_currentDate);
            // PretupsBL.generateC2STransferID(_c2sTransferVO);
            // Transfer id is now genereated in the memory- 02-07-07 Ashish
            generateC2SBillPayTransferID(_c2sTransferVO);
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
            // if domain Type is SALE CENTER then transfer value & requested
            // amount is same
            // otherwise transfer value & requested value is different
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
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            }
            throw be;
        } catch (Exception e) {
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                if (_transferID != null) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_UB, new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                } else {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_UB, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                        .getRequestedAmount()) }));
                }
            }
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[processTransfer]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
        }
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
        _c2sTransferVO.setActiveUserId(_channelUserVO.getActiveUserID());
    }// end populateVOFromRequest

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
        // _receiverInterfaceInfoInDBFound checked if already we get the
        // interface details then no need to do it again
        if ((!_receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
            .equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
            isReceiverFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, receiverPrefixID, _receiverVO.getSubscriberType(), receiverNetworkCode, _c2sTransferVO
                .getServiceType(), _type, PretupsI.USER_TYPE_RECEIVER, action);
        } else {
            isReceiverFound = true;
        }
        if (!isReceiverFound) {
            throw new BTSLBaseException(this, "populateServiceInterfaceDetails", PretupsErrorCodesI.C2S_ERR_NOTFOUND_SRVCINTERFACEMAPPING_UB);
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
        	loggerValue.append( " Prefix ID=");
        	loggerValue.append(p_prefixID);
        	loggerValue.append(" p_subscriberType=");
        	loggerValue.append(p_subscriberType);
        	loggerValue.append(" p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_serviceType=");
        	loggerValue.append(p_serviceType);
        	loggerValue.append(" p_interfaceCategory=" );
        	loggerValue.append(p_interfaceCategory);
        	loggerValue.append(" p_userType=");
        	loggerValue.append(p_userType);
        	loggerValue.append(" p_action=");
        	loggerValue.append(p_action);
            _log.debug(methodName,loggerValue);
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
            	loggerValue.append(" subscriberRoutingControlVO=");
            	loggerValue.append(subscriberRoutingControlVO);
                _log.debug(methodName, loggerValue );
            }
            _receiverSubscriberType = p_subscriberType;
            if (subscriberRoutingControlVO != null) {
                if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    final ListValueVO listValueVO = PretupsBL.validateNumberInRoutingDatabase(p_con, p_msisdn, p_interfaceCategory);
                    if (listValueVO != null) {
                        isSuccess = true;
                        setInterfaceDetails(p_prefixID, p_interfaceCategory, p_action, listValueVO, false, null, null);
                    } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                        ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                        // added by rahul.d to check service selector based
                        // check load of interface
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                            interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _c2sTransferVO
                                .getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                            if (interfaceMappingVO1 != null) {
                                isSuccess = true;
                                setInterfaceDetails(p_prefixID, p_interfaceCategory, p_action, null, true, null, interfaceMappingVO1);
                            }
                        }
                        if (interfaceMappingVO1 == null) {
                            final MSISDNPrefixInterfaceMappingVO interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID,
                                p_interfaceCategory, p_action);
                            isSuccess = true;
                            setInterfaceDetails(p_prefixID, p_interfaceCategory, p_action, null, true, interfaceMappingVO, null);
                        }
                    } else {
                        isSuccess = false;
                    }
                } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                    ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                    MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                        interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _c2sTransferVO
                            .getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                    }
                    if (interfaceMappingVO1 == null) {
                        interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_interfaceCategory, p_action);
                        isSuccess = true;
                        setInterfaceDetails(p_prefixID, p_interfaceCategory, p_action, null, true, interfaceMappingVO, null);
                    } else {
                        isSuccess = true;
                        setInterfaceDetails(p_prefixID, p_interfaceCategory, p_action, null, true, null, interfaceMappingVO1);
                    }
                } else {
                    isSuccess = false;
                }
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "C2SBillPaymentController[getInterfaceRoutingDetails]", _transferID, _senderMSISDN, _senderNetworkCode,
                    "Routing control information not defined so performing series based routing");
                ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                // if preference is true load service slector based mapping
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                    interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _c2sTransferVO
                        .getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                }
                if (interfaceMappingVO1 == null) {
                    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_interfaceCategory, p_action);
                    isSuccess = true;
                    setInterfaceDetails(p_prefixID, p_interfaceCategory, p_action, null, true, interfaceMappingVO, null);
                } else {
                    isSuccess = true;
                    setInterfaceDetails(p_prefixID, p_interfaceCategory, p_action, null, true, null, interfaceMappingVO1);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(subscriberRoutingControlVO);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[getInterfaceRoutingDetails]",
                _transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString() );
            isSuccess = false;
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exiting with isSuccess=");
        	loggerValue.append(isSuccess);
            _log.debug(methodName, loggerValue );
        }
        return isSuccess;
    }

    /**
     * Method to set the interface details based on the parameters
     * 
     * @param p_prefixID
     * @param p_interfaceCategory
     *            PRE or POST
     * @param p_action
     *            : VALIDATE OR UPDATE
     * @param p_listValueVO
     * @param p_useInterfacePrefixVO
     * @param p_MSISDNPrefixInterfaceMappingVO
     * @return
     */
    private void setInterfaceDetails(long p_prefixID, String p_interfaceCategory, String p_action, ListValueVO p_listValueVO, boolean p_useInterfacePrefixVO, MSISDNPrefixInterfaceMappingVO p_MSISDNPrefixInterfaceMappingVO, ServiceSelectorInterfaceMappingVO p_serviceSelectorInterfaceMappingVO) throws BTSLBaseException {
        final String methodName = "setInterfaceDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append( " Entered with Prefix ID=");
        	loggerValue.append(p_prefixID);
        	loggerValue.append(" p_interfaceCategory=");
        	loggerValue.append(p_interfaceCategory);
        	loggerValue.append(" p_action=");
        	loggerValue.append(p_action);
        	loggerValue.append(" p_listValueVO=");
        	loggerValue.append(p_listValueVO);
        	loggerValue.append(" p_useInterfacePrefixVO=");
        	loggerValue.append(p_useInterfacePrefixVO);
        	loggerValue.append(" p_MSISDNPrefixInterfaceMappingVO=" );
        	loggerValue.append(p_MSISDNPrefixInterfaceMappingVO);
        	loggerValue.append("ServiceSelectorInterfaceMappingVO=" );
        	loggerValue.append(p_serviceSelectorInterfaceMappingVO);
            _log.debug(methodName,loggerValue);
        }
        try {
            String interfaceID = null;
            String interfaceHandlerClass = null;
            String underProcessMsgReqd = null;
            String allServiceClassID = null;
            String externalID = null;
            String status = null;
            String message1 = null;
            String message2 = null;
            String interfaceTypeSt = null;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue() && p_serviceSelectorInterfaceMappingVO != null) {
                interfaceID = p_serviceSelectorInterfaceMappingVO.getInterfaceID();
                interfaceHandlerClass = p_serviceSelectorInterfaceMappingVO.getHandlerClass();
                underProcessMsgReqd = p_serviceSelectorInterfaceMappingVO.getUnderProcessMsgRequired();
                allServiceClassID = p_serviceSelectorInterfaceMappingVO.getAllServiceClassID();
                externalID = p_serviceSelectorInterfaceMappingVO.getExternalID();
                status = p_serviceSelectorInterfaceMappingVO.getInterfaceStatus();
                message1 = p_serviceSelectorInterfaceMappingVO.getLanguage1Message();
                message2 = p_serviceSelectorInterfaceMappingVO.getLanguage2Message();
                interfaceTypeSt = p_serviceSelectorInterfaceMappingVO.getStatusType();
            }
            if (p_useInterfacePrefixVO && p_serviceSelectorInterfaceMappingVO == null) {
                interfaceID = p_MSISDNPrefixInterfaceMappingVO.getInterfaceID();
                interfaceHandlerClass = p_MSISDNPrefixInterfaceMappingVO.getHandlerClass();
                underProcessMsgReqd = p_MSISDNPrefixInterfaceMappingVO.getUnderProcessMsgRequired();
                allServiceClassID = p_MSISDNPrefixInterfaceMappingVO.getAllServiceClassID();
                externalID = p_MSISDNPrefixInterfaceMappingVO.getExternalID();
                status = p_MSISDNPrefixInterfaceMappingVO.getInterfaceStatus();
                message1 = p_MSISDNPrefixInterfaceMappingVO.getLanguage1Message();
                message2 = p_MSISDNPrefixInterfaceMappingVO.getLanguage2Message();
                interfaceTypeSt = p_MSISDNPrefixInterfaceMappingVO.getStatusType();
            } else if (p_serviceSelectorInterfaceMappingVO == null) {
                interfaceID = p_listValueVO.getValue();
                interfaceHandlerClass = p_listValueVO.getLabel();
                underProcessMsgReqd = p_listValueVO.getType();
                allServiceClassID = p_listValueVO.getTypeName();
                externalID = p_listValueVO.getIDValue();
                status = p_listValueVO.getStatus();
                message1 = p_listValueVO.getOtherInfo();
                message2 = p_listValueVO.getOtherInfo2();
                interfaceTypeSt = p_listValueVO.getStatusType();
                if (p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                    _receiverInterfaceInfoInDBFound = true;
                }
            }
            _receiverTransferItemVO.setPrefixID(p_prefixID);
            _receiverTransferItemVO.setInterfaceID(interfaceID);
            _receiverTransferItemVO.setInterfaceType(p_interfaceCategory);
            _receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
            _externalID = externalID;
            _interfaceStatusType = interfaceTypeSt;
            if (PretupsI.YES.equals(underProcessMsgReqd)) {
                _c2sTransferVO.setUnderProcessMsgReq(true);
            }
            _receiverAllServiceClassID = allServiceClassID;
            _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
            _c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);

            if (!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(_interfaceStatusType)) {
                // Check which language message to be sent from the locale
                // master table for the perticuler locale.
                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                    _c2sTransferVO.setSenderReturnMessage(message1);
                } else {
                    _c2sTransferVO.setSenderReturnMessage(message2);
                }
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_UB);
            }
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Getting Base Exception =" );
        	loggerValue.append(be.getMessage());
            _log.error(methodName, loggerValue);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[setInterfaceDetails]",
                _transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
        } finally {
            if (_log.isDebugEnabled()) {
            	 loggerValue.setLength(0);
             	loggerValue.append(" Exiting with Sender Interface ID=" );
             	loggerValue.append(_senderTransferItemVO.getInterfaceID());
             	loggerValue.append(" Receiver Interface=" );
             	loggerValue.append(_receiverTransferItemVO.getInterfaceID());
                _log.debug(methodName, _requestIDStr,loggerValue);
            }
        }
    }

    /**
     * Check the transaction load
     * 
     * @throws BTSLBaseException
     */
    private void checkTransactionLoad() throws BTSLBaseException {
        final String methodName = "checkTransactionLoad";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Checking load for transfer ID=" + _transferID);
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
                    _log.debug("C2SBillPaymentController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
                }
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                final String strArr[] = { _receiverMSISDN, String.valueOf(PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())) };
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQUEST_IN_QUEUE_UB, 0, strArr, null);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQUEST_REFUSE_UB);
            }
        } catch (BTSLBaseException be) {
            _log.error("C2SBillPaymentController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQUEST_REFUSE_UB);
        }
    }

    /**
     * Method to get the under process message before validation to be sent to
     * sender
     * 
     * @return
     */
    private String getSndrUPMsgBeforeValidation() {
        final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS_B4VAL_UB, messageArgArray);
    }

    /**
     * Method to get the success message to be sent to sender
     * 
     * @return
     */
    private String getSenderUnderProcessMessage() {
        final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL
            .getDisplayAmount(_senderTransferItemVO.getPostBalance()) };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS_UB, messageArgArray);
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
        Connection con = null;
        MComConnectionI mcomCon = null;
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
            final String requestStr = getReceiverCreditStr();
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            // Sending credit request to the common client
            final String receiverCreditResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                receiverCreditResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Got the response from IN Module receiverCreditResponse=");
            	loggerValue.append(receiverCreditResponse);
                _log.debug(methodName, _transferID,  loggerValue );
            }
            // Getting Database connection
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            try {
                // updating receiver credit response
                updateForReceiverCreditResponse(receiverCreditResponse);
                // decreasing response counters
                LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
            } catch (BTSLBaseException be) {
                TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _c2sTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO
                        .getInterfaceResponseCode());
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                }
                // Update the sender back for fail transaction
                // Check Status if Ambigous then credit back preference wise
                if (((_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CRDT_BK_AMB_UB))).booleanValue())) || _c2sTransferVO
                    .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    updateSenderForFailedTransaction(con);
                }
                // Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
                throw be;
            }// end catch BTSLBaseException
            catch (Exception e) {
                TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _c2sTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO
                        .getInterfaceResponseCode());
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                }
                // Update the sender back for fail transaction
                // Check Status if Ambigous then credit back preference wise
                if (((_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS)).booleanValue())) || _c2sTransferVO
                    .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    updateSenderForFailedTransaction(con);
                }
                // Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
                throw new BTSLBaseException(this, methodName, "");
            }// end of catch Exception
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _c2sTransferVO.setErrorCode(null);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, _receiverVO.getNetworkCode());
            // TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Success",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+_c2sTransferVO.getTransferStatus());
            // validate receiver limits after Interface Updation
            PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            _c2sTransferVO.setSenderReturnMessage(null);
            // checking whether differential commission is applicable or not
            if (PretupsI.YES.equals(_c2sTransferVO.getDifferentialAllowedForService())) {
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
                }
				if (mcomCon != null) {
					mcomCon.close("C2SBillPaymentController#run");
					mcomCon = null;
				}
                con = null;
                // Caluculate Differential if transaction successful
                try {
                    new DiffCalBL().differentialCalculations(_c2sTransferVO, PretupsI.C2S_MODULE);
                } catch (BTSLBaseException be) {
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append( "For _transferID=");
                    	loggerValue.append(_transferID);
                    	loggerValue.append(" Diff applicable=");
                    	loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
                    	loggerValue.append(" Diff Given=");
                    	loggerValue.append(_c2sTransferVO.getDifferentialGiven());
                    	loggerValue.append(" Not able to give Diff commission getting BTSL Base Exception=");
                    	loggerValue.append(be.getMessage() );
                    	loggerValue.append(" Leaving transaction status as Under process");
                        _log.debug("C2SBillPaymentController",loggerValue );
                    }
                    _log.errorTrace(methodName, be);
                    loggerValue.setLength(0);
                	loggerValue.append( "Exception:");
                	loggerValue.append(be.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[run]", _c2sTransferVO
                        .getTransferID(), _c2sTransferVO.getSenderMsisdn(), _c2sTransferVO.getNetworkCode(),  loggerValue.toString() );
                } catch (Exception e) {
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append( "For _transferID=");
                    	loggerValue.append(_transferID);
                    	loggerValue.append(" Diff applicable=");
                    	loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
                    	loggerValue.append(" Diff Given=");
                    	loggerValue.append(_c2sTransferVO.getDifferentialGiven());
                    	loggerValue.append(" Not able to give Diff commission getting Exception=" );
                    	loggerValue.append(e.getMessage());
                    	loggerValue.append(" Leaving transaction status as Under process");
                    	
                        _log.debug("C2SBillPaymentController",loggerValue );
                    }
                    _log.errorTrace(methodName, e);
                }
            }// end if
             // TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"After Differential Calculation",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+_c2sTransferVO.getTransferStatus()+" Differential Appl="+_c2sTransferVO.getDifferentialApplicable()+" Diff Given="+_c2sTransferVO.getDifferentialGiven());
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( "For _transferID=");
            	loggerValue.append(_transferID);
            	loggerValue.append(" Diff applicable=");
                loggerValue.append(_c2sTransferVO.getDifferentialApplicable());
                loggerValue.append(" Diff Given=");
                loggerValue.append(_c2sTransferVO.getDifferentialGiven());
                _log.debug("C2SBillPaymentController",loggerValue );
            }
        }// end try
        catch (BTSLBaseException be) {
            // try{if(con!=null) con.rollback() ;}catch(Exception ex){}
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
                }
            }// end if
            if (be.isKey() && _c2sTransferVO.getSenderReturnMessage() == null) {
                btslMessages = be.getBtslMessages();
            } else if (_c2sTransferVO.getSenderReturnMessage() == null) {
                _c2sTransferVO.setSenderReturnMessage(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Error Code:");
            	loggerValue.append(_c2sTransferVO.getErrorCode());
                _log.debug(methodName, _transferID,  loggerValue );
            }
            _log.errorTrace(methodName, be);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
        }// end catch BTSLBaseException
        catch (Exception e) {
            // try{if(con!=null) con.rollback() ;}catch(Exception ex){}
            _log.errorTrace(methodName, e);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[run]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            btslMessages = new BTSLMessages(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
        }// end catch Exception
        finally {
            try {
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO
                    .getReceiverReturnMsg()).isKey())) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                }
                // decreasing transaction load count
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                // Getting database conection if it is null
                if (mcomCon == null) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                }
                // Unmarking the receiver transaction status
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
                }
            }// end try
            catch (BTSLBaseException be) {
                // try{if(con!=null) con.rollback() ;}catch(Exception ex){}
                _log.errorTrace(methodName, be);
            } catch (SQLException e) {
                try {
                    if (con != null) {
                        mcomCon.finalRollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
            	loggerValue.append("Exception while updating Receiver last request status in database , Exception:");
            	loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SBillPaymentController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode,  loggerValue.toString() );
            }// end catch
            try {
                if (_finalTransferStatusUpdate) {
                    // Setting modified on and by
                    _c2sTransferVO.setModifiedOn(new Date());
                    _c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    // Updating C2S Transfer details in database
                    ChannelTransferBL.updateC2STransferDetails(con, _c2sTransferVO);
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
            	loggerValue.append("Exception while updating transfer details in database , Exception:");
            	loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SBillPaymentController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            }
            // if connection is not null then comitting the transaction and
            // closing the connection
            if (con != null) {
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                if(mcomCon != null){mcomCon.close("C2SBillPaymentController#run");mcomCon=null;}
                con = null;
            }
            // If transaction is fail and grouptype counters need to be decrease
            // then decrease the counters
            if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && _requestVO.isDecreaseGroupTypeCounter() && ((ChannelUserVO) _requestVO
                .getSenderVO()).getUserControlGrouptypeCounters() != null) {
                PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO) _requestVO.getSenderVO()).getUserControlGrouptypeCounters());
            }
            if (_receiverMessageSendReq && !BTSLUtil.isNullString(_receiverNotificationMSISDN) && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),
                _notAllowedRecSendMessGatw) && !"ALL".equals(_notAllowedRecSendMessGatw)) {
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    if (_c2sTransferVO.getReceiverReturnMsg() == null) {
                        (new PushMessage(_receiverNotificationMSISDN, getReceiverSuccessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                            .push();
                    } else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                        final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                        (new PushMessage(_receiverNotificationMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()),
                            _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                    } else {
                        (new PushMessage(_receiverNotificationMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
                            _receiverLocale)).push();
                    }
                } else if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    if (_c2sTransferVO.getReceiverReturnMsg() == null) {
                        (new PushMessage(_receiverNotificationMSISDN, getReceiverAmbigousMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                            .push();
                    } else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                        final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                        (new PushMessage(_receiverNotificationMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()),
                            _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                    } else {
                        (new PushMessage(_receiverNotificationMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
                            _receiverLocale)).push();
                    }
                } else if (_recTopupFailMessageRequired && _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    if (_c2sTransferVO.getReceiverReturnMsg() == null) {
                    	
                    	final String message=getReceiverFailMessage(_transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, _channelUserVO
                                .getUserName(),null,_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY_UB,_requestVO.getRequestGatewayType());
                        (new PushMessage(_receiverNotificationMSISDN, message, _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                    } else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                        final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                        (new PushMessage(_receiverNotificationMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()),
                            _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                    } else {
                        (new PushMessage(_receiverNotificationMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
                            _receiverLocale)).push();
                    }
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
                        pushMessages = (new PushMessage(_senderPushMessageMsisdn, getSenderSuccessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
                            _senderLocale));
                    }
                }// end if
                 // If transaction is successfull then if group type counters
                 // reach limit then send message using gateway that is
                 // associated with group type profile

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
            }
            // Log the credit back entry in the balance log
            if (_creditBackEntryDone) {
                BalanceLogger.log(_userBalancesVO);
            }

            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS,
                "Trans Status=" + _c2sTransferVO.getTransferStatus() + " Error Code=" + _c2sTransferVO.getErrorCode() + " Diff Appl=" + _c2sTransferVO
                    .getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO.getDifferentialGiven() + " Message=" + _c2sTransferVO.getSenderReturnMessage());

            btslMessages = null;
            _userBalancesVO = null;
            commonClient = null;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, _transferID, "Exiting");
            }
        }// end of finally
    }

    /**
     * Method to get the string to be sent to the interface for topup
     * 
     * @return
     */
    public String getReceiverCreditStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getReceiverCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_CREDIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + _c2sTransferVO.getReceiverTransferValue());
        strBuff.append("&CARD_GROUP=" + _c2sTransferVO.getCardGroupCode());
        strBuff.append("&MIN_CARD_GROUP_AMT=" + _c2sTransferVO.getMinCardGroupAmount());
        strBuff.append("&SENDER_MSISDN=" + _senderMSISDN);
        strBuff.append("&SENDER_ID=" + _channelUserVO.getUserID());
        strBuff.append("&SENDER_EXTERNAL_CODE=" + _channelUserVO.getExternalCode());
        strBuff.append("&PRODUCT_CODE=" + _c2sTransferVO.getProductCode());
        strBuff.append("&BONUS_AMOUNT=" + _c2sTransferVO.getReceiverBonusValue());
        strBuff.append("&INTERFACE_PREV_BALANCE=" + _receiverTransferItemVO.getPreviousBalance());
        // Avinash send the requested amount to IN. to use card group only for
        // reporting purpose.
        strBuff.append("&REQUESTED_AMOUNT=" + _c2sTransferVO.getRequestedAmount());

        // added by vikas for card group file updation
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
        strBuff.append("&CARD_GROUP_SELECTOR=" + _requestVO.getReqSelector());
        strBuff.append("&USER_TYPE=R");
        strBuff.append("&REQ_SERVICE=" + _serviceType);
        strBuff.append("&INT_ST_TYPE=" + _c2sTransferVO.getReceiverInterfaceStatusType());
        return strBuff.toString();
    }

    /**
     * Method to do the validation of the receiver and perform the steps before
     * the topup stage
     * 
     * @param p_con
     * @throws BTSLBaseException
     * @throws SQLException 
     * @throws Exception
     */
    private void processValidationRequest() throws BTSLBaseException, SQLException {
        Connection con = null;
        MComConnectionI mcomCon = null;
        final String methodName = "processValidationRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered and performing validations for transfer ID=" + _transferID + " " + _c2sTransferVO.getModule() + " " + _c2sTransferVO
                .getReceiverNetworkCode() + " " + _type);
        }
        try {
            final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_c2sTransferVO.getModule(),
                _c2sTransferVO.getReceiverNetworkCode(), _type);
            _intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
            _intModIPS = networkInterfaceModuleVOS.getIP();
            _intModPortS = networkInterfaceModuleVOS.getPort();
            _intModClassNameS = networkInterfaceModuleVOS.getClassName();
            final String requestStr = getReceiverValidateStr();
            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
            final CommonClient commonClient = new CommonClient();
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            final String receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, _transferID, "Got the validation response from IN Module receiverValResponse=" + receiverValResponse);
            }
            _itemList = new ArrayList();
            _itemList.add(_senderTransferItemVO);
            _itemList.add(_receiverTransferItemVO);
            _c2sTransferVO.setTransferItemList(_itemList);
            try {
                updateForReceiverValidateResponse(receiverValResponse);
            } catch (BTSLBaseException be) {
                LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "inside catch of BTSL Base Exception: " + be.getMessage() + " _receiverInterfaceInfoInDBFound: " + _receiverInterfaceInfoInDBFound);
                }
                if (_receiverInterfaceInfoInDBFound && _receiverTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
                    PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _type);
                }
                mcomCon = new MComConnection();
                con=mcomCon.getConnection();
                // validate receiver limits after Interface Validations
                PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
                throw be;
            } catch (Exception e) {
                LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "inside catch of Exception: " + e.getMessage() + " _receiverInterfaceInfoInDBFound: " + _receiverInterfaceInfoInDBFound);
                }
                mcomCon = new MComConnection();con=mcomCon.getConnection();
                // validate receiver limits after Interface Validations
                PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
                throw new BTSLBaseException(this, methodName, "");
            }
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

            // If request is taking more time till validation of subscriber than
            // reject the request.
            InterfaceVO interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
            if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > interfaceVO.getValExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SBillPaymentController[processValidationRequest]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till validation");
                throw new BTSLBaseException("C2SBillPaymentController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
            }

            // Get the service Class ID based on the code
            PretupsBL.validateServiceClassChecks(con, _receiverTransferItemVO, _c2sTransferVO, PretupsI.C2S_MODULE, _requestVO.getServiceType());
            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
            // validate sender receiver service class,validate transfer value
            PretupsBL.validateTransferRule(con, _c2sTransferVO, PretupsI.C2S_MODULE);
            // validate receiver limits after Interface Validations
            PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
            // calculate card group details
            CardGroupBL.calculateCardGroupDetails(con, _c2sTransferVO, PretupsI.C2S_MODULE, true);
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
                        .getReceiverValidity() + " Talk Time=" + _c2sTransferVO.getReceiverTransferValue(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            // Here the code for debiting the user account will come
            _userBalancesVO = ChannelUserBL.debitUserBalanceForProduct(con, _transferID, _c2sTransferVO);
            // Update Transfer Out Counts for the sender
            ChannelTransferBL.increaseC2STransferOutCounts(con, _c2sTransferVO, true);
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            // populate payment and service interface details
            populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            // Method to insert the record in c2s transfer table
            ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO);
            _transferDetailAdded = true;
            // Commit the transaction and relaease the locks
            try {
                mcomCon.finalCommit();
            } catch (SQLException be) {
                _log.errorTrace(methodName, be);
            }
            if(mcomCon != null){mcomCon.close("C2SBillPaymentController#processValidationRequest");mcomCon=null;}
            con = null;
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Marked Under process", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");
            // Log the details if the transfer Details were added i.e. if User
            // was debitted
            if (_transferDetailAdded) {
                BalanceLogger.log(_userBalancesVO);
            }
            // Push Under Process Message to Sender and Reciever , this might
            // have to be implemented on flag basis whether to send message or
            // not
            if (_receiverNotificationMSISDN != null && _c2sTransferVO.isUnderProcessMsgReq() && _receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO
                .getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL".equals(_notAllowedRecSendMessGatw)) {
                (new PushMessage(_receiverNotificationMSISDN, getReceiverUnderProcessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
            }

            // Thread.sleep(5000);
            // If request is taking more time till topup of subscriber than
            // reject the request.
            // interfaceVO=(InterfaceVO)NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
            if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > interfaceVO.getTopUpExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SBillPaymentController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till topup");
                throw new BTSLBaseException("C2SBillPaymentController", "run", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
            }
            interfaceVO = null;

            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue) {
                // create new Thread
                final Thread _controllerThread = new Thread(this);
                _controllerThread.start();
            }
        } catch (BTSLBaseException be) {
            if (con != null) {
                mcomCon.finalRollback();
            }
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_UB), new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                }
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
                }
            }
            _log.error("C2SBillPaymentController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());
            if (_transferDetailAdded) {
                if (mcomCon == null) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
                _userBalancesVO = null;
                // Update the sender back for fail transaction
                updateSenderForFailedTransaction(con);
                // So that we can update with final status here
                addEntryInTransfers(con);
                mcomCon.finalCommit();
                // Log the details if the transfer Details were added i.e. if
                // User was creditted
                if (_creditBackEntryDone) {
                    BalanceLogger.log(_userBalancesVO);
                }
            }
            throw be;
        } catch (SQLException e) {
            _log.errorTrace(methodName, e);
            if (con != null) {
                con.rollback();
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_UB), new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                }
            }
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            }
            if (_transferDetailAdded) {
                if (mcomCon == null) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
                _userBalancesVO = null;
                // Update the sender back for fail transaction
                updateSenderForFailedTransaction(con);
                // So that we can update with final status here
                addEntryInTransfers(con);
                mcomCon.finalCommit();
                // Log the details if the transfer Details were added i.e. if
                // User was creditted
                if (_creditBackEntryDone) {
                    BalanceLogger.log(_userBalancesVO);
                }
            }
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);
        } finally {
        	if(mcomCon != null){mcomCon.close("C2SBillPaymentController#processValidationRequest");mcomCon=null;}
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
        return strBuff.toString();
    }

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
        ArrayList altList = null;
        boolean isRequired = false;

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        // If we get the MSISDN not found on interface error then perform
        // interface routing
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            // Getting routing controll details
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(_receiverTransferItemVO.getInterfaceID());
            if (altList != null && !altList.isEmpty()) {
                performAlternateRouting(altList); // Performing alternate
                // routing for a number if it
                // is not found on the
                // interface
            } else {
                isRequired = true;
            }
        }
        // MSISDN is not found on interface and
        if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
            // receiver language has to be taken from IN then the block below
            // will execute
            if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
                // update the receiver locale if language code returned from IN
                // is not null
                updateReceiverLocale((String) map.get("IN_LANG"));
            }
            if (_receiverNotificationMSISDN == null) {
                updateReceiverNotificationNumber((String) map.get("NOTIFICATION_MSISDN"));
            }
            _receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
            _receiverTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
            _receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
            _receiverTransferItemVO.setValidationStatus(status);
            _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
            _receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            _receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
            _receiverTransferItemVO.setSubscriberType(_receiverSubscriberType);
            // If status is other than Success in validation stage mark sender
            // request as Not applicable and
            // Make transaction Fail
            String[] strArr = null;
            if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                _c2sTransferVO.setErrorCode(status + "_R");
                _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                _c2sTransferVO.setTransferStatus(status);
                _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
                throw new BTSLBaseException(this, methodName, _c2sTransferVO.getErrorCode(), strArr);
            }
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
                .getRoutingControlDetails(_c2sTransferVO.getReceiverNetworkCode() + "_" + _c2sTransferVO.getServiceType() + "_" + _type);
            if (!_receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type, _channelUserVO.getUserID(),
                    _currentDate);
                _receiverInterfaceInfoInDBFound = true;
            }
            _receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
            _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
            try {
                _receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            ;
            _receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
        }
    }

    /**
     * Method to update the reciver notification no to which message is sent to
     * receiver
     * 
     * @return
     */
    public void updateReceiverNotificationNumber(String p_notificationMSISDN) throws BTSLBaseException {
        final String methodName = "updateReceiverNotificationNumber";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with Notication No =" + p_notificationMSISDN);
        }
        // Call operator specif method to validate the receiver notification
        // number returned from IN.
        // This is done because there may be some operator specific validation
        // required for the receiver notification number
        final String notificationNumber = _operatorUtil.validateReceiverNotificationNumber(p_notificationMSISDN);
        if (!BTSLUtil.isNullString(notificationNumber)) {
            _receiverNotificationMSISDN = notificationNumber;
            _requestVO.setNotificationMSISDN(_receiverNotificationMSISDN);
            if (!_receiverMessageSendReq) {
                _receiverMessageSendReq = true;
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited _receiverNotificationMSISDN=" + _receiverNotificationMSISDN);
        }
    }

    /**
     * Method to get the under process message to be sent to receiver
     * 
     * @return
     */
    private String getReceiverUnderProcessMessage() {
        final String[] messageArgArray = { _transferID, String.valueOf(_c2sTransferVO.getTransferValue()), String.valueOf(_c2sTransferVO.getRequestedAmount()), _c2sTransferVO
            .getSenderMsisdn(), String.valueOf(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSenderName() };
        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_UNDERPROCESS_UB, messageArgArray, _requestVO.getRequestGatewayType());
    }

    /**
     * Method to update the channel user back in case of failed transaction
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    private void updateSenderForFailedTransaction(Connection p_con) throws BTSLBaseException {
        final String methodName = "updateSenderForFailedTransaction";
        try {
            _userBalancesVO = ChannelUserBL.creditUserBalanceForProduct(p_con, _c2sTransferVO.getTransferID(), _c2sTransferVO);
            ChannelTransferBL.decreaseC2STransferOutCounts(p_con, _c2sTransferVO);
            _creditBackEntryDone = true;
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Credit Back Done", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (BTSLBaseException be) {
            _finalTransferStatusUpdate = false;
            _c2sTransferVO.setSenderReturnMessage(null);
            PretupsBL.validateRecieverLimits(p_con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage() + " Getting Code=" + be.getMessageKey());
            _log.errorTrace(methodName, be);
            throw be;
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
            if (!_isCounterDecreased && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[addEntryInTransfers]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
        }
    }

    /**
     * Method that will perform the validation request in thread
     * 
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequestInThread() throws BTSLBaseException, Exception {
    	StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "processValidationRequestInThread";
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered and performing validations for transfer ID=");
        	loggerValue.append(_transferID);
            _log.debug(methodName, loggerValue );
        }
        try {
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            processValidationRequest();
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("Getting BTSL Base Exception:");
        	loggerValue.append(be.getMessage());
            _log.error("C2SBillPaymentController[processValidationRequestInThread]",  loggerValue );
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_UB), new String[] { String.valueOf(_transferID), PretupsBL
                    .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            }
            _log.error(methodName, _transferID, "Exception:" + e.getMessage());
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "C2SBillPaymentController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString() );
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());

        } finally {
            // !_transferDetailAdded Condition Added as we think its not require
            // as already done
            if (_transferID != null && !_transferDetailAdded) {
                Connection con = null;MComConnectionI mcomCon = null;
                try {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    addEntryInTransfers(con);
                    if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        _finalTransferStatusUpdate = false; // No need to update
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
                    loggerValue.setLength(0);
                	loggerValue.append("Exception:");
                	loggerValue.append(e.getMessage());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "C2SBillPaymentController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
                } finally {
                	if(mcomCon != null){mcomCon.close("C2SBillPaymentController#processValidationRequestInThread");mcomCon=null;}
                    con = null;
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    /**
     * Method to process the response of the receiver top up from IN
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForReceiverCreditResponse(String str) throws BTSLBaseException {
        final String methodName = "updateForReceiverCreditResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        StringBuilder loggerValue= new StringBuilder(); 
        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Mape from response=");
        	loggerValue.append(map);
        	loggerValue.append(" status=");
        	loggerValue.append(status);
        	loggerValue.append(" interface Status=");
        	loggerValue.append(interfaceStatusType);
            _log.debug(methodName, loggerValue );
        }
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Mape from response=");
        	loggerValue.append(map);
        	loggerValue.append(" status=");
        	loggerValue.append(status);
            _log.debug(methodName,  loggerValue );
        }
        // setting transaction status for restricted subscriber feature
        if (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getRestrictedMsisdns())) {
            if (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getTransferToListOnly())) {
                ((RestrictedSubscriberVO) ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getRestrictedSubscriberVO()).setTempStatus(status);
            }
        }
        _receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        _receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        String updateStatus = (String) map.get("UPDATE_STATUS");
        if (BTSLUtil.isNullString(updateStatus)) {
            updateStatus = status;
        }
        _receiverTransferItemVO.setUpdateStatus(updateStatus);
        // _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
        _receiverTransferItemVO.setUpdateStatus1((String) map.get("UPDATE_STATUS1"));
        _receiverTransferItemVO.setUpdateStatus2((String) map.get("UPDATE_STATUS2"));
        if (!BTSLUtil.isNullString((String) map.get("ADJUST_AMOUNT"))) {
            _receiverTransferItemVO.setAdjustValue(Long.parseLong((String) map.get("ADJUST_AMOUNT")));
        }
        _receiverPostBalanceAvailable = (String) map.get("POST_BALANCE_ENQ_SUCCESS");
        // set from IN Module
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
            try {
                _receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        _receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
        if (BTSLUtil.isNullString(_receiverNotificationMSISDN)) {
            updateReceiverNotificationNumber((String) map.get("NOTIFICATION_MSISDN"));
        }
        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        String[] strArr = null;
        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            _c2sTransferVO.setErrorCode(status + "_R");
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(status);
            strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            // throw new
            // BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.C2S_RECEIVER_FAIL,0,strArr,null);
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", _c2sTransferVO.getErrorCode(), strArr);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
            _c2sTransferVO.setErrorCode(status + "_R");
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _receiverTransferItemVO.setUpdateStatus(status);
            strArr = new String[] { _transferID, _receiverTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, strArr);
        } else {
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _receiverTransferItemVO.setUpdateStatus(status);
        }

        try {
            _receiverTransferItemVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
    }

    /**
     * Method to get the success message to be sent to receiver
     * Method updated for notification message using service class date 15/05/06
     * 
     * @return
     */
    private String getReceiverSuccessMessage() {
        final String methodName = "getReceiverSuccessMessage";
        String[] messageArgArray = null;
        String key = null;
        if (!"N".equals(_receiverPostBalanceAvailable)) {
            // added by vikas kumar for card group updation added on 12/12/2008
            messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), PretupsBL
                .getDisplayAmount(_receiverTransferItemVO.getPostBalance()), _senderPushMessageMsisdn, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL
                .getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO.getUserName(), String.valueOf(_c2sTransferVO
                .getReceiverBonus1()), String.valueOf(_c2sTransferVO.getReceiverBonus2()), String.valueOf(_c2sTransferVO.getReceiverBonus1Validity()), String
                .valueOf(_c2sTransferVO.getReceiverBonus2Validity()), String.valueOf(_c2sTransferVO.getReceiverCreditBonusValidity()) };
            key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_UB;
        } else {
            // added by vikas kumar for sms/mms
            messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), _senderPushMessageMsisdn, PretupsBL
                .getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO
                .getUserName(), String.valueOf(_c2sTransferVO.getReceiverBonus1()), String.valueOf(_c2sTransferVO.getReceiverBonus2()), String.valueOf(_c2sTransferVO
                .getReceiverBonus1Validity()), String.valueOf(_c2sTransferVO.getReceiverBonus2Validity()), String.valueOf(_c2sTransferVO.getReceiverCreditBonusValidity()) };
            key = PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL_UB;// return
            // BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
        }
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_UB))).booleanValue()) {
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

    /**
     * Method to get the Ambigous message to be sent to receiver
     */
    private String getReceiverAmbigousMessage() {
        final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderPushMessageMsisdn, _channelUserVO
            .getUserName() };
        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_AMBIGOUS_KEY_UB, messageArgArray, _requestVO.getRequestGatewayType());
    }

    /**
     * Method to get the success message to be sent to sender
     */
    private String getSenderSuccessMessage() {
        final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL
            .getDisplayAmount(_senderTransferItemVO.getPostBalance()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService() };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_SUCCESS_UB, messageArgArray);
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
                        "C2SBillPaymentController[updateReceiverLocale]", _transferID, _receiverMSISDN, "",
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
     * Method to perform the Interface routing for the subscriber account no
     * 
     * @throws BTSLBaseException
     */
    private void performAlternateRouting(ArrayList altList) throws BTSLBaseException {
        final String methodName = "performAlternateRouting";
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
                    case 1:
                        {
                            LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(_transferID, _c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                            listValueVO = (ListValueVO) altList.get(0);
                            _receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
                            _receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
                            if (PretupsI.YES.equals(listValueVO.getType())) {
                                _c2sTransferVO.setUnderProcessMsgReq(true);
                            }
                            _receiverAllServiceClassID = listValueVO.getTypeName();
                            _externalID = listValueVO.getIDValue();
                            _interfaceStatusType = listValueVO.getStatusType();
                            _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
                            _c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);

                            if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
                                // ChangeID=LOCALEMASTER
                                // Check which language message to be sent from
                                // the
                                // locale master table for the perticuler
                                // locale.
                                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                } else {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_UB);
                            }
                            checkTransactionLoad();
                            // validate receiver limits before Interface
                            // Validations
                            PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
                            requestStr = getReceiverValidateStr();
                            commonClient = new CommonClient();
                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
                            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");
                            receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
                            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
                            try {
                                receiverValidateResponse(receiverValResponse, 1, altList.size());
                                if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
                                    // Update in DB for routing interface
                                    if (_receiverInterfaceInfoInDBFound) {
                                        PretupsBL.updateSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
                                            _channelUserVO.getUserID(), _currentDate);
                                    } else {
                                        final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_c2sTransferVO
                                            .getReceiverNetworkCode() + "_" + _c2sTransferVO.getServiceType() + "_" + _type);
                                        if (!_receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                            PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
                                                _channelUserVO.getUserID(), _currentDate);
                                            _receiverInterfaceInfoInDBFound = true;
                                        }
                                    }
                                }
                            } catch (BTSLBaseException be) {
                                throw new BTSLBaseException(be);
                            } catch (Exception e) {
                                throw new BTSLBaseException(this, methodName, "");
                            }
                            break;
                        }
                    case 2:
                        {
                            LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(_transferID, _c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                            listValueVO = (ListValueVO) altList.get(0);
                            _receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
                            _receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
                            if (PretupsI.YES.equals(listValueVO.getType())) {
                                _c2sTransferVO.setUnderProcessMsgReq(true);
                            }
                            _receiverAllServiceClassID = listValueVO.getTypeName();
                            _externalID = listValueVO.getIDValue();
                            _interfaceStatusType = listValueVO.getStatusType();
                            _c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
                            _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
                            if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
                                // ChangeID=LOCALEMASTER
                                // Check which language message to be sent from
                                // the
                                // locale master table for the perticuler
                                // locale.
                                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                } else {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_UB);
                            }
                            checkTransactionLoad();
                            // validate receiver limits before Interface
                            // Validations
                            PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
                            requestStr = getReceiverValidateStr();
                            commonClient = new CommonClient();
                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
                            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");
                            receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
                            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
                            try {
                                receiverValidateResponse(receiverValResponse, 1, altList.size());
                                if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
                                    // Update in DB for routing interface
                                    if (_receiverInterfaceInfoInDBFound) {
                                        PretupsBL.updateSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
                                            _channelUserVO.getUserID(), _currentDate);
                                    } else {
                                        final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_c2sTransferVO
                                            .getReceiverNetworkCode() + "_" + _c2sTransferVO.getServiceType() + "_" + _type);
                                        if (!_receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                            PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
                                                _channelUserVO.getUserID(), _currentDate);
                                            _receiverInterfaceInfoInDBFound = true;
                                        }
                                    }
                                }
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                                    LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                                    LoadController
                                        .decreaseTransactionInterfaceLoad(_transferID, _c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                                    listValueVO = (ListValueVO) altList.get(1);
                                    _receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
                                    _receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
                                    if (PretupsI.YES.equals(listValueVO.getType())) {
                                        _c2sTransferVO.setUnderProcessMsgReq(true);
                                    }
                                    _receiverAllServiceClassID = listValueVO.getTypeName();
                                    _externalID = listValueVO.getIDValue();
                                    _interfaceStatusType = listValueVO.getStatusType();
                                    _c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
                                    _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
                                    if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
                                        // ChangeID=LOCALEMASTER
                                        // Check which language message to be
                                        // sent from
                                        // the locale master table for the
                                        // perticuler
                                        // locale.
                                        if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                                            _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                        } else {
                                            _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                        }
                                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_UB);
                                    }
                                    checkTransactionLoad();
                                    // validate receiver limits before Interface
                                    // Validations
                                    PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
                                    requestStr = getReceiverValidateStr();
                                    // commonClient=new CommonClient();
                                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
                                    TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                                        PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 2");
                                    receiverValResponse = commonClient
                                        .process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
                                    TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                                        PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
                                    try {
                                        receiverValidateResponse(receiverValResponse, 2, altList.size());
                                        if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
                                            // Update in DB for routing
                                            // interface
                                            if (_receiverInterfaceInfoInDBFound) {
                                                PretupsBL.updateSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
                                                    _channelUserVO.getUserID(), _currentDate);
                                            } else {
                                                final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
                                                    .getRoutingControlDetails(_c2sTransferVO.getReceiverNetworkCode() + "_" + _c2sTransferVO.getServiceType() + "_" + _type);
                                                if (!_receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                                    PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _externalID, _receiverMSISDN, _type,
                                                        _channelUserVO.getUserID(), _currentDate);
                                                    _receiverInterfaceInfoInDBFound = true;
                                                }
                                            }
                                        }
                                    } catch (BTSLBaseException bex) {
                                        throw new BTSLBaseException(bex);
                                    } catch (Exception e) {
                                        throw new BTSLBaseException(e);
                                    }
                                } else {
                                    throw new BTSLBaseException(be);
                                }
                            } catch (Exception e) {
                                throw new BTSLBaseException(this, methodName, "");
                            }
                            break;
                        }
                    default:
                    	 if(_log.isDebugEnabled()){
                    		_log.debug("Default Value " , altList.size());
                    	 }
                }
            } else {
                return;
            }
        } catch (BTSLBaseException be) {
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[performAlternateRouting]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberInterfaceRouting", PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
        }
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
        // receiver language has to be taken from IN then the block below will
        // execute
        if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
            // update the receiver locale if language code returned from IN is
            // not null
            updateReceiverLocale((String) map.get("IN_LANG"));
        }
        if (_receiverNotificationMSISDN == null) {
            updateReceiverNotificationNumber((String) map.get("NOTIFICATION_MSISDN"));
        }
        _receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        _receiverTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
        _receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        _receiverTransferItemVO.setValidationStatus(status);
        _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
        _receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
        _receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
        _receiverTransferItemVO.setSubscriberType(_receiverSubscriberType);
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
            strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", PretupsErrorCodesI.C2S_RECEIVER_FAIL, strArr);
        }
        _receiverTransferItemVO.setTransferStatus(status);
        _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        _receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        // Done so that receiver check can be brough to common
        _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
        _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
        try {
            _receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        _receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
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
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            _c2sTransferVO = (C2STransferVO) p_transferVO;
            _requestVO = _c2sTransferVO.getRequestVO();
            _channelUserVO = (ChannelUserVO) _requestVO.getSenderVO();
            _type = _requestVO.getType();
            _requestID = _requestVO.getRequestID();
            _requestIDStr = _requestVO.getRequestIDStr();
            _receiverLocale = _requestVO.getReceiverLocale();
            _receiverNotificationMSISDN = _requestVO.getNotificationMSISDN();
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
                mcomCon.finalCommit();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            }
            if(mcomCon != null){mcomCon.close("C2SBillPaymentController#processFromQueue");mcomCon=null;}
            con = null;
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("_transferID=");
            	loggerValue.append(_transferID);
            	loggerValue.append(" Successfully through load");
                _log.debug("C2SBillPaymentController[processFromQueue]",  loggerValue );
            }
            _processedFromQueue = true;
            processValidationRequest();
            // Set under process message for the sender and reciever
            p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS_UB);
            final String[] messageArgArray = { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
            p_transferVO.setMessageArguments(messageArgArray);
        } catch (BTSLBaseException be) {
            try {
                if (mcomCon == null && _receiverVO != null && _receiverVO.isUnmarkRequestStatus()) { 
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                loggerValue.setLength(0);
            	loggerValue.append("Leaving Reciever Unmarked Base Exception:");
            	loggerValue.append(bex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SBillPaymentController[processFromQueue]",
                    _transferID, _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
                loggerValue.append("Leaving Reciever Unmarked Base Exception:");
                loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SBillPaymentController[processFromQueue]",
                    _transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString() );
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            }

            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

            if (be.isKey()) // checking if baseexception has key
            {
                if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                }

                _c2sTransferVO.setMessageCode(be.getMessageKey());
                _c2sTransferVO.setMessageArguments(be.getArgs());
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            }

            // checking whether need to decrease the transaction load, if it is
            // already increased
            LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            _isCounterDecreased = true;
            // making entry in the transaction log
            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
            
            _log.errorTrace(methodName, e);
            try {
                if (mcomCon ==null && _receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                loggerValue.setLength(0);
                loggerValue.append("Leaving Reciever Unmarked Base Exception:");
                loggerValue.append(bex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SBillPaymentController[processFromQueue]",
                    _transferID, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
                loggerValue.setLength(0);
                loggerValue.append("Leaving Reciever Unmarked Exception:");
                loggerValue.append( ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SBillPaymentController[processFromQueue]",
                    _transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
            }
            // checking condition whether channel receiver required the general
            // failure message
            if (_recValidationFailMessageRequired) {
                // if receivermessage is null or it is not key
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // setting receiver return message
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_UB, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_UB, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            _requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERR_EXCEPTION_UB);
            _log.errorTrace(methodName, e);
            // decreasing the transaction load count
            LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            _isCounterDecreased = true;
            // raising alarm
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[processFromQueue]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            // logging in the transaction log
            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());
        } finally {
            try {
                if (mcomCon == null ) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
                if (_transferID != null && !_transferDetailAdded) {
                    addEntryInTransfers(con);
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SBillPaymentController[processFromQueue]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            }
            if (BTSLUtil.isNullString(_c2sTransferVO.getMessageCode())) {
                _c2sTransferVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (con != null) {
                // committing transaction and closing connection
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                if(mcomCon != null){mcomCon.close("C2SBillPaymentController#processFromQueue");mcomCon=null;}
                con = null;
            }// end if
            if (_receiverMessageSendReq && BTSLUtil.isNullString(_receiverNotificationMSISDN) && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),
                _notAllowedRecSendMessGatw) && !"ALL".equals(_notAllowedRecSendMessGatw)) {
                // checking if receiver message is not null and receiver return
                // message is key
                if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // generating message and pushing it to receiver
                    final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                    (new PushMessage(_receiverNotificationMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()),
                        _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                } else {
                    (new PushMessage(_receiverNotificationMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
                        _receiverLocale)).push();
                }
            }
            // making entry in the transaction log
            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + _requestVO.getMessageCode());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    /**
     * This method implements the logic to generate the transaction id in the
     * memory.
     * 
     * @param p_transferVO
     */
    /*
     * private static synchronized void generateC2SBillPayTransferID(TransferVO
     * p_transferVO)
     * {
     * String transferID=null;
     * try
     * {
     * //ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
     * //newTransferID=IDGenerator.getNextID(PretupsI.ID_GEN_C2S_TRANSFER_NO,
     * BTSLUtil
     * .getFinancialYearLastDigits(4),receiverVO.getNetworkCode(),p_transferVO
     * .getCreatedOn());
     * long currentReqTime= System.currentTimeMillis();
     * if(currentReqTime-_prevReqTime>=(60000))
     * _transactionIDCounter=1;
     * else
     * _transactionIDCounter=_transactionIDCounter+1;
     * _prevReqTime=currentReqTime;
     * if(_transactionIDCounter==0)
     * throw new
     * BTSLBaseException("C2SBillPaymentController","generateC2SBillPayTransferID"
     * ,PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * transferID=_operatorUtil.formatC2STransferID(p_transferVO,
     * _transactionIDCounter);
     * if(transferID==null)
     * throw new
     * BTSLBaseException("C2SBillPaymentController","generateC2SBillPayTransferID"
     * ,PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * p_transferVO.setTransferID(transferID);
     * //System.out.println("generateC2STransferID transferID::"+transferID);
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * }
     */
    private static synchronized void generateC2SBillPayTransferID(TransferVO p_transferVO) {
        final String methodName = "generateC2SBillPayTransferID";
        String transferID = null;
        String minut2Compare = null;
        Date mydate = null;
        try {
            // mydate = p_transferVO.getCreatedOn();
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
                throw new BTSLBaseException("C2SBillPaymentController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = _operatorUtil.formatBillPayTransferID(p_transferVO, _transactionIDCounter);
            if (transferID == null) {
                throw new BTSLBaseException("C2SBillPaymentController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
    }
}
