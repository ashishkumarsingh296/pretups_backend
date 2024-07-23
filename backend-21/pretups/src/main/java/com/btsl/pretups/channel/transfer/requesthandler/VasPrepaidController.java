package com.btsl.pretups.channel.transfer.requesthandler;

/**
 * @(#)VasPrepaidController.java
 *                               Name Date History
 *                               ----------------------------------------------
 *                               --------------------------
 *                               Rahul Dutt 3/11/2011 Initial creation
 *                               ----------------------------------------------
 *                               --------------------------
 *                               Copyright (c) 2011 Comviva Technology Ltd.
 *                               Controller class for handling the Vas Recharge
 *                               request
 */

import java.net.URLDecoder;
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
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
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
import com.btsl.pretups.logging.ChannelRequestDailyLog;
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.SelectorAmountMappingDAO;
import com.btsl.pretups.master.businesslogic.SelectorAmountMappingVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
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
import com.btsl.pretups.sos.businesslogic.SOSVO;
import com.btsl.pretups.sos.requesthandler.SOSSettlementController;
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
import com.txn.pretups.sos.businesslogic.SOSTxnDAO;

public class VasPrepaidController implements ServiceKeywordControllerI, Runnable {
    private static Log _log = LogFactory.getLog(VasPrepaidController.class.getName());
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
    private boolean _recValidationFailMessageRequired = false;
    private boolean _recTopupFailMessageRequired = false;
    private String _notAllowedSendMessGatw;
    private String _notAllowedRecSendMessGatw;
    private String _receiverSubscriberType = null;
    private static OperatorUtilI _operatorUtil = null;
    private String _interfaceStatusType = null;
    private static int _transactionIDCounter = 0;
    private static int _prevMinut = 0;
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    private boolean _receiverMessageSendReq = false;
    private String _senderPushMessageMsisdn = null;
    private static final String _vasPrefix = "V";
    // private String _vasServiceList=null; //not required as discussed with
    // gopal
    private String _receiverBundleID = null;
    private String _selectorName = "";

    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public VasPrepaidController() {
        _c2sTransferVO = new C2STransferVO();
        _currentDate = new Date();

        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("C2S_REC_GEN_FAIL_MSG_REQD_V")))) {
            _recValidationFailMessageRequired = true;
        }
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("C2S_REC_GEN_FAIL_MSG_REQD_T")))) {
            _recTopupFailMessageRequired = true;
        }
        _notAllowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2S_SEN_MSG_NOT_REQD_GW"));
        _notAllowedRecSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_NOT_REQD_GW"));
        // Initialize the time to check the current request time.
        // VAS service list load from Constants.props no required as discussed
        // with gopal
    }

    /**
     * Method to process the request of the C2S transfer
     * 
     * @param object
     *            of the RequestVO
     */
    public void process(RequestVO p_requestVO) {
        Connection con = null;MComConnectionI mcomCon = null;

        // 1. Validate the incoming Message
        // 2. Get the newtork Code for customer MSISDN
        // a) check the network and service type maapping
        // 3. Check whether same sender and reciever msisdn can be same
        // 4. Get the location URLS
        // 5. Generate the Transfer ID
        // 6. Check the min and max range stored in preferences updated
        // internally
        // 7. Format the amt in system format
        // a) Sender related checks if any
        // 8. Check the sender has balance
        // 9. Send request to IN
        // 10. Get the service class
        // 11. Get the cardgroup
        // 12. Calculate amount
        // 13. Debit the sender also check for -ve differential
        // 14. Spawn the thread and do the topup

        final String methodName = "process";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                p_requestVO.getRequestIDStr(),
                "Entered for Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " _recValidationFailMessageRequired: " + _recValidationFailMessageRequired + " _recTopupFailMessageRequired" + _recTopupFailMessageRequired + " _notAllowedSendMessGatw: " + _notAllowedSendMessGatw + " ");
        }
        // boolean receiverMessageSendReq=false;
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

            // Code added for VAS recharge when AMOUNT is not coming in API and the amount is to be fetched from SELECTOR_AMOUNT_MAPPING Table.
            String requestArray[]=p_requestVO.getRequestMessageArray();
            if(p_requestVO.getRequestMessageArray().length ==  6 && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTI_AMOUNT_ENABLED))).booleanValue() && PretupsI.VAS_BLANK_SLCTR_AMNT.equalsIgnoreCase(p_requestVO.getRequestMessageArray()[2])){
            	SelectorAmountMappingDAO selectorAmountMappingDAO=new SelectorAmountMappingDAO();
            	SelectorAmountMappingVO selectorAmountMappingVO=null;
            	selectorAmountMappingVO =selectorAmountMappingDAO.loadSelectorAmountDetails(con,p_requestVO.getServiceType(),requestArray[3]); 
            	if(selectorAmountMappingVO!=null){
            		p_requestVO.setDecryptedMessage(p_requestVO.getDecryptedMessage().replaceAll(PretupsI.VAS_BLANK_SLCTR_AMNT, selectorAmountMappingVO.getAmountStr()));
            		requestArray[2]=selectorAmountMappingVO.getAmountStr();
            		p_requestVO.setRequestMessageArray(requestArray);
            	}
            }   
            // Validating user message incomming in the request
            _operatorUtil.validateVasRechargeRequest(con, _c2sTransferVO, p_requestVO);
            // Block added to avoid decimal amount in credit transfer
            if (!BTSLUtil.isStringIn(_serviceType, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES))) {
                try {
                    final String displayAmt = PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount());
                    Long.parseLong(displayAmt);
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_AMOUNT);
                }
            }
            
            _c2sTransferVO.setTransferValue(_c2sTransferVO.getRequestedAmount());
            _receiverLocale = p_requestVO.getReceiverLocale();
            _senderLocale = p_requestVO.getSenderLocale();
            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));
            _c2sTransferVO.setSelectorCode(p_requestVO.getReqSelector());
            final ServiceSelectorMappingVO serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(
                p_requestVO.getServiceType() + "_" + p_requestVO.getReqSelector());
            if (serviceSelectorMappingVO != null) {
                _receiverBundleID = serviceSelectorMappingVO.getReceiverBundleID();
                _c2sTransferVO.setReceiverBundleID(_receiverBundleID);
                _selectorName = serviceSelectorMappingVO.getSelectorName();
            }

            if (!_receiverVO.getSubscriberType().equals(_type)) {
                // Refuse the Request
                _log.error(this, "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VasPrepaidController[process]", "", "", "",
                    "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But request initiated for the same");
                throw new BTSLBaseException("", methodName, PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { _receiverVO.getMsisdn() }, null);
            }

            _receiverVO.setModule(_c2sTransferVO.getModule());
            _receiverVO.setCreatedDate(_currentDate);
            _receiverVO.setLastTransferOn(_currentDate);
            _senderMSISDN = (_channelUserVO.getUserPhoneVO()).getMsisdn();
            _senderPushMessageMsisdn = p_requestVO.getMessageSentMsisdn();// @
            _receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
            _c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
            _c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
            _c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
            _c2sTransferVO.setSubService(p_requestVO.getReqSelector());
            _c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
            _receiverSubscriberType = _receiverVO.getSubscriberType();
            // FOR USSD
            _channelUserVO.setCellID(p_requestVO.getCellId());
            _c2sTransferVO.setCellId(p_requestVO.getCellId());
            _c2sTransferVO.setSwitchId(p_requestVO.getSwitchId());
            _c2sTransferVO.setSelectorCode(p_requestVO.getReqSelector());
            _c2sTransferVO.setSenderNetworkCode(_senderNetworkCode);
            // restricted MSISDN check
            // if
            // (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getRestrictedMsisdns()))
            // {
            // if
            // (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getTransferToListOnly()))
            RestrictedSubscriberBL.isRestrictedMsisdnExistForC2S(con, _c2sTransferVO, _channelUserVO, _receiverVO.getMsisdn(), _c2sTransferVO.getRequestedAmount());
            // }
            // check Black list restricted subscribers not allowed for recharge
            // or for CP2P services.
            /*
             * else
             * {
             * //check Black list restricted subscribers not allowed for
             * recharge or for CP2P services.
             * _operatorUtil.isRestrictedSubscriberAllowed(con,_receiverVO.getMsisdn
             * (),PretupsI.C2S_PAYEE);
             * }
             */

            // Validates the network service status
            PretupsBL.validateNetworkService(_c2sTransferVO);
            _receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW, _receiverVO.getNetworkCode(), _serviceType))
                .booleanValue();
            // checking whether self topup is allowed or not
            if (_senderMSISDN.equals(_receiverMSISDN) && !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_ALLOW_SELF_TOPUP))).booleanValue()) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SELF_TOPUP_NTALLOWD);
            }

            // Chcking senders transfer profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getTransferProfileStatus())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND);
            }

            // Chcking senders commission profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getCommissionProfileStatus())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND);
            } else if (PretupsI.YES.equalsIgnoreCase(_channelUserVO.getOutSuspened())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND);
            }

            // check if receiver barred in PreTUPS or not, user should not be
            // barred.
            try {
                PretupsBL.checkMSISDNBarred(con, _receiverMSISDN, _receiverVO.getNetworkCode(), _c2sTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
            } catch (BTSLBaseException be) {
                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
                }
                throw be;
            }

            // added by PN(25/03/08) to resolve the issude of duplicate request
            // processing
            _c2sTransferVO.setUnderProcessCheckReqd(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
            // Loading C2S receiver's controll parameters
            PretupsBL.loadRecieverControlLimits(con, p_requestVO.getRequestIDStr(), _c2sTransferVO);
            _receiverVO.setUnmarkRequestStatus(true);

            // commiting transaction after updating receiver's controll
            // parameters
            try {
                con.commit();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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

                // Change is done for ID=SUBTYPVALRECLMT
                // This chenge is done to set the receiver subscriber type in
                // transfer VO
                // This will be used in validate ReceiverLimit method of
                // PretupsBL when receiverTransferItemVO is null
                _c2sTransferVO.setReceiverSubscriberType(_receiverSubscriberType);
                // validate receiver limits before Interface Validations
                PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);

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
                    throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }
				if (mcomCon != null) {
					mcomCon.close("VasPrepaidController#process");
					mcomCon = null;
				}
                con = null;

                // Checking the Various loads and setting flag to decrease the
                // transaction count
                checkTransactionLoad();
                _decreaseTransactionCounts = true;

                if (!_channelUserVO.isStaffUser())// added by ankuj
                {
                    (_channelUserVO.getUserPhoneVO()).setLastTransferID(_transferID);
                    (_channelUserVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);
                } else {
                    (_channelUserVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferID(_transferID);
                    (_channelUserVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);
                }// ankuj addition ends here

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
                }// starting validation and topup process in thread
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
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
                    }
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + e.getMessage());
            }

            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (_recValidationFailMessageRequired) {
                // setting receiver return message
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {

                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
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
            // Populate the ChannelRequestDailyLogVo and log
            ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
            // setting success transaction status flag to false
            p_requestVO.setSuccessTxn(false);
            try {

                // getting database connection to unmark the users transaction
                // to completed
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    if (mcomCon == null) {
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
                    }
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + ex.getMessage());
            }
            // checking condition whether channel receiver required the general
            // failure message
            if (_recValidationFailMessageRequired) {
                // if receivermessage is null or it is not key
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // setting receiver return message
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[process]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            // logging in the transaction log
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            // Populate the ChannelRequestDailyLogVo and log
            ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
        }// end of catch
        finally {
            try {
                // Getting connection if it is null
                if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());

            }
            if (con != null) {
                // committing transaction and closing connection
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
				if (mcomCon != null) {
					mcomCon.close("VasPrepaidController#process");
					mcomCon = null;
				}
                con = null;
            }// end if

            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }

            // Add for MSISDn not found In IN 31/01/08
            if (_receiverTransferItemVO != null) {
                if (!BTSLUtil.isNullString(_receiverTransferItemVO.getValidationStatus())) {
                    if (_receiverTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
                        p_requestVO.setIntMsisdnNotFound(_c2sTransferVO.getErrorCode());
                    } else {
                        p_requestVO.setIntMsisdnNotFound(null);
                    }
                } else {
                    p_requestVO.setIntMsisdnNotFound(null);
                }
            }
            // End of 31/01/08

            if (_isCounterDecreased) {
                p_requestVO.setDecreaseLoadCounters(false);
            }

            if (_receiverMessageSendReq && _recValidationFailMessageRequired && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
                .equals(_notAllowedRecSendMessGatw)) {
                // checking if receiver message is not null and receiver return
                // message is key
                if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // check _receiverTransferItemVO!=null because if any
                    // exception occure before setting _receiverTransferItemVO
                    // generating message and pushing it to receiver
                    if (_receiverTransferItemVO != null && InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED.equals(_receiverTransferItemVO.getValidationStatus())) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED + "_R"));
                    }
                    final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                    (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                        _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                } else if (_c2sTransferVO.getReceiverReturnMsg() != null) {
                    (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                        .push();
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[processSKeyGen]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }// end of processSKeyGen

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
            _c2sTransferVO.setTransferDateTime(_currentDate);
            // Commented to get the Transaction id from the memory.
            // PretupsBL.generateC2STransferID(_c2sTransferVO);
            generateVASTransferId(_c2sTransferVO);
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
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                } else {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                        .getRequestedAmount()) }));
                }
            }
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[processTransfer]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
    }

    /**
     * Thread to perform IN related operations
     */
    public void run() {
        final String methodName = "run";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, _transferID, "Entered");
        }
        BTSLMessages btslMessages = null;
        _userBalancesVO = null;
        CommonClient commonClient = null;
        Connection con = null;MComConnectionI mcomCon = null;
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

            // Sending request to the common client
            final String receiverCreditResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                receiverCreditResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, _transferID, "Got the response from IN Module receiverCreditResponse=" + receiverCreditResponse);
            }
            // Getting Database connection
            mcomCon = new MComConnection();con=mcomCon.getConnection();
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
                if (((_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS)).booleanValue())) || _c2sTransferVO
                    .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    updateSenderForFailedTransaction(con);
                }

                // Validating the receiver Limits and updating it
                PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
                throw new BTSLBaseException(be);
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
                throw new BTSLBaseException(e);
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
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException("VasPrepaidController", "process", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }
                if(mcomCon != null){mcomCon.close("VasPrepaidController#run");mcomCon=null;}
                con = null;

                // Caluculate Differential if transaction successful
                try {
                    new DiffCalBL().differentialCalculations(_c2sTransferVO, PretupsI.C2S_MODULE);
                } catch (BTSLBaseException be) {
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            "VasPrepaidController",
                            "For _transferID=" + _transferID + " Diff applicable=" + _c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting BTSL Base Exception=" + be.getMessage() + " Leaving transaction status as Under process");
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VasPrepaidController[run]", _c2sTransferVO
                        .getTransferID(), _c2sTransferVO.getSenderMsisdn(), _c2sTransferVO.getNetworkCode(), "Exception:" + be.getMessage());
                    _log.errorTrace(methodName, be);
                } catch (Exception e) {
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            "VasPrepaidController",
                            "For _transferID=" + _transferID + " Diff applicable=" + _c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting Exception=" + e.getMessage() + " Leaving transaction status as Under process");
                    }
                    _log.errorTrace(methodName, e);
                }
            }// end if

            // TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"After Differential Calculation",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+_c2sTransferVO.getTransferStatus()+" Differential Appl="+_c2sTransferVO.getDifferentialApplicable()+" Diff Given="+_c2sTransferVO.getDifferentialGiven());

            // real time settlement of LMB on the basis of system preference
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ONLINE_ALLOW))).booleanValue()) {
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    final Date currentDate = new Date();
                    SOSVO sosvo = null;
                    try {
                        if (mcomCon == null) {
                        	mcomCon = new MComConnection();con=mcomCon.getConnection();
                        }
                        sosvo = new SOSTxnDAO().loadSOSDetails(con, currentDate, _receiverMSISDN);
                        if (sosvo != null) {
                            sosvo.setCreatedOn(currentDate);
                            sosvo.setInterfaceID(_receiverTransferItemVO.getInterfaceID());
                            sosvo.setInterfaceHandlerClass(_receiverTransferItemVO.getInterfaceHandlerClass());
                            sosvo.setOldExpiryInMillis(_receiverTransferItemVO.getOldExporyInMillis());
                            // sosvo.setServiceType(_serviceType);
                            sosvo.setLocale(_receiverLocale);
                            sosvo.setLmbAmountAtIN(_receiverTransferItemVO.getLmbdebitvalue());
                            final SOSSettlementController sosSettlementController = new SOSSettlementController();
                            sosSettlementController.processSOSRechargeRequest(sosvo);

                        } else {
                            _log.error(this, "VasPrepaidController", "process" + " No record found in database for this number :" + _receiverTransferItemVO.getMsisdn());
                            // throw new
                            // BTSLBaseException("VasPrepaidController","run",PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
                        }
                    } catch (BTSLBaseException be) {
                        _log.error(this, "VasPrepaidController", "run BTSLBaseException:" + "Transaction ID: " + sosvo.getTransactionID() + "Msisdn" + _receiverTransferItemVO
                            .getMsisdn() + "Getting Exception while processing LMB request :" + be.getMessage());
                        _log.errorTrace(methodName, be);
                    } catch (Exception e) {
                        _log.error(this, "VasPrepaidController", "run Exception:" + "Transaction ID: " + sosvo.getTransactionID() + "Msisdn" + _receiverTransferItemVO
                            .getMsisdn() + "Getting Exception while processing LMB request :" + e.getMessage());
                        _log.errorTrace(methodName, e);
                    } finally {
                        if (con != null) {
                            try {
                                con.commit();
                            } catch (Exception e) {
                                _log.errorTrace(methodName, e);
                            }
                            if(mcomCon != null){mcomCon.close("VasPrepaidController#run");mcomCon=null;}
                            con = null;
                        }

                    }
                }
            }// end of system preference
            if (_log.isDebugEnabled()) {
                _log.debug("VasPrepaidController",
                    "For _transferID=" + _transferID + " Diff applicable=" + _c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO
                        .getDifferentialGiven());
            }
        }// end try
        catch (BTSLBaseException be) {
            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(_c2sTransferVO.getErrorCode());
            // try{if(con!=null) con.rollback() ;}catch(Exception ex){}
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
                _log.debug(methodName, _transferID, "Error Code:" + _c2sTransferVO.getErrorCode());
            }

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
            _log.errorTrace(methodName, be);
        }// end catch BTSLBaseException
        catch (Exception e) {
            // try{if(con!=null) con.rollback() ;}catch(Exception ex){}

            _requestVO.setSuccessTxn(false);
            _requestVO.setMessageCode(_c2sTransferVO.getErrorCode());
            _log.errorTrace(methodName, e);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            _log.error(methodName, _transferID, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[run]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            btslMessages = new BTSLMessages(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

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
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }

                // Unmarking the receiver transaction status
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
                }
            }// end try
            catch (BTSLBaseException be) {
                // try{if(con!=null) con.rollback() ;}catch(Exception ex){}
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception while updating Receiver last request status in database , Exception:" + e.getMessage());
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
            }
            // if connection is not null then comitting the transaction and
            // closing the connection
            if (con != null) {
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                if(mcomCon != null){mcomCon.close("VasPrepaidController#run");mcomCon=null;}
                con = null;
            }
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
            	if(!BTSLUtil.isStringIn(_c2sTransferVO.getServiceType(),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSRBR_MSG_STOP)))){
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
                        (new PushMessage(_receiverMSISDN, getReceiverFailMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
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
            }

            if (!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedSendMessGatw)) {
                PushMessage pushMessages = null;
                PushMessage pushAddCommMessage = null;
                if (btslMessages != null) {
                    // push final error message to sender
                    pushMessages = (new PushMessage(_senderMSISDN, BTSLUtil.getMessage(_senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs()), _transferID,
                        _c2sTransferVO.getRequestGatewayCode(), _senderLocale));
                } else {
                    // push Additional Commission success message to sender and
                    // final status to sender
                    if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
                        pushAddCommMessage = (new PushMessage(_senderMSISDN, _c2sTransferVO.getSenderReturnMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
                            _senderLocale));
                    }
                    if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                        pushMessages = (new PushMessage(_senderMSISDN, getSenderSuccessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale));
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
                         if (groupTypeProfileVO != null && pushMessages != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
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
						if(pushMessages != null)
                            pushMessages.push();
                        }
                    } catch (Exception e) {
                        _log.errorTrace(methodName, e);
                    }
                } else {
				if(pushMessages != null)
                    pushMessages.push();
                }
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && pushAddCommMessage != null) {
                    pushAddCommMessage.push();
                }
                if (!BTSLUtil.isNullString(_requestVO.getParentMsisdnPOS()) && !("".equals(_requestVO.getParentMsisdnPOS())) && _channelUserVO.isStaffUser())// added
                // by
                // ankuj
                {
                    final PushMessage pushParentMessages = (new PushMessage(_requestVO.getParentMsisdnPOS(), getSenderParentSuccessMessage(), _transferID, _c2sTransferVO
                        .getRequestGatewayCode(), _senderLocale));
                    pushParentMessages.push();
                }// ankuj addition ends here
            }
            // Log the credit back entry in the balance log
            if (_creditBackEntryDone) {
                BalanceLogger.log(_userBalancesVO);
            }

            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS,
                "Trans Status=" + _c2sTransferVO.getTransferStatus() + " Error Code=" + _c2sTransferVO.getErrorCode() + " Diff Appl=" + _c2sTransferVO
                    .getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO.getDifferentialGiven() + " Message=" + _c2sTransferVO.getSenderReturnMessage());
            // Populate the ChannelRequestDailyLogVo and log
            ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));

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
        // added
        // by
        // ankuj
        _c2sTransferVO.setServiceType(_channelUserVO.getServiceTypes());
        if(p_requestVO.getRequestMap()!= null)
        {
        _c2sTransferVO.setInfo1(p_requestVO.getRequestMap().get("INFO1") != null ? (String)p_requestVO.getRequestMap().get("INFO1") : "");
        _c2sTransferVO.setInfo2(p_requestVO.getRequestMap().get("INFO2") != null ? (String)p_requestVO.getRequestMap().get("INFO2") : "");
        _c2sTransferVO.setInfo3(p_requestVO.getRequestMap().get("INFO3") != null ? (String)p_requestVO.getRequestMap().get("INFO3") : "");
        _c2sTransferVO.setInfo4(p_requestVO.getRequestMap().get("INFO4") != null ? (String)p_requestVO.getRequestMap().get("INFO4") : "");
        _c2sTransferVO.setInfo5(p_requestVO.getRequestMap().get("INFO5") != null ? (String)p_requestVO.getRequestMap().get("INFO5") : "");
        }
        
    }// end populateVOFromRequest

    /**
     * Method to process the response of the receiver validation from IN
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForReceiverValidateResponse(String str) throws BTSLBaseException {
        final String METHOD_NAME = "updateForReceiverValidateResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        ArrayList altList = null;
        boolean isRequired = false;
        // added to log the IN validation request sent and request received
        // time. Start 07/02/2008
        if (null != map.get("IN_START_TIME")) {
            _requestVO.setValidationReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            _requestVO.setValidationReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
            // end 07/02/2008
        }

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
            _receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
            try {
                _receiverTransferItemVO.setAccountStatus(URLDecoder.decode((String) map.get("ACCOUNT_STATUS")));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;

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
                // throw new
                // BTSLBaseException("VasPrepaidController","updateForReceiverValidateResponse",PretupsErrorCodesI.C2S_RECEIVER_FAIL,0,strArr,null);
                // throw new
                // BTSLBaseException("VasPrepaidController","updateForReceiverValidateResponse",_c2sTransferVO.getErrorCode(),0,strArr,null);
                if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED.equals(_receiverTransferItemVO.getValidationStatus())) {
                    throw new BTSLBaseException("VasPrepaidController", "updateForReceiverValidateResponse",
                        InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED + "_S", 0, strArr, null);
                } else {
                    throw new BTSLBaseException("VasPrepaidController", "updateForReceiverValidateResponse", _c2sTransferVO.getErrorCode(), 0, strArr, null);
                }
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
            try {
                _receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            try {
                _receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            _receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
            // Done so that receiver check can be brough to common
            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
            _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
            _receiverTransferItemVO.setOldExporyInMillis((String) map.get("CAL_OLD_EXPIRY_DATE"));

            try {
                _receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ;
            _receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
            _receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));

            _receiverTransferItemVO.setBundleTypes((String) map.get("BUNDLE_TYPES"));
            _receiverTransferItemVO.setBonusBundleValidities((String) map.get("BONUS_BUNDLE_VALIDITIES"));
            _receiverTransferItemVO.setBundleTypes((String) map.get("BUNDLE_NAMES"));
            _receiverTransferItemVO.setInAccountId((String) map.get("IN_ACCOUNT_ID"));
            _receiverTransferItemVO.setBonus1Name((String) map.get("NAME_BONUS1"));
            _receiverTransferItemVO.setBonus2Name((String) map.get("NAME_BONUS2"));
            _receiverTransferItemVO.setSelectorName((String) map.get("SELECTOR_NAME"));

            // TO DO Done for testing purpose should we use it or give exception
            // in this case
            // if(_receiverTransferItemVO.getPreviousExpiry()==null)
            // _receiverTransferItemVO.setPreviousExpiry(_currentDate);
            try {
                _receiverTransferItemVO.setLmbdebitvalue((Long.valueOf((String) map.get("LMB_ALLOWED_VALUE"))));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);

            }
            ;
            try {
                _receiverTransferItemVO.setPreviousPromoExpiry(BTSLUtil.getDateFromDateString((String) map.get("PROMO_OLD_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);

            }
            ;
            try {
                _receiverTransferItemVO.setPreviousPromoBalance(Long.parseLong((String) map.get("INTERFACE_PROMO_PREV_BALANCE")));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);

            }
            ;
            _receiverTransferItemVO.setPreviousExpiryInCal((String) map.get("CAL_OLD_EXPIRY_DATE"));
            _receiverTransferItemVO.setPreviousPromoExpiryInCal((String) map.get("PROMO_CAL_OLD_EXPIRY_DATE"));
            // set Service Provider NAme
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW))).booleanValue()) {
                _receiverTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
                _c2sTransferVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
            }
            _operatorUtil.populateBonusListAfterValidation(map, _c2sTransferVO);
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
        String vasErrorMsgReqd = PretupsI.NO;
        try {
            vasErrorMsgReqd = (String) map.get("VAS_ERROR_MSG_REQD");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        // added to log the IN validation request sent and request received
        // time. Start 07/02/2008//@@
        if (null != map.get("IN_START_TIME")) {
            _requestVO.setTopUPReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            _requestVO.setTopUPReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
            // End 07/02/2008
        }

        // For post validation request
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_POSTCREDIT_VAL_TIME"))) {
                _requestVO.setPostValidationTimeTaken(Long.parseLong((String) map.get("IN_POSTCREDIT_VAL_TIME")));
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        // for credit request
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_RECHARGE_TIME"))) {
                _requestVO.setCreditTime(Long.parseLong((String) map.get("IN_RECHARGE_TIME")));
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        // for promo request
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_PROMO_TIME"))) {
                _requestVO.setPromoTime(Long.parseLong((String) map.get("IN_PROMO_TIME")));
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        // for cos update request
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_COS_TIME"))) {
                _requestVO.setCosTime(Long.parseLong((String) map.get("IN_COS_TIME")));
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        // for ambiguous credit
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_CREDIT_VAL_TIME"))) {
                _requestVO.setCreditValTime(Long.parseLong((String) map.get("IN_CREDIT_VAL_TIME")));
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        // for ambiguous promo
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_PROMO_VAL_TIME"))) {
                _requestVO.setPromoValTime(Long.parseLong((String) map.get("IN_PROMO_VAL_TIME")));
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        // for ambiguous cos
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_COS_VAL_TIME"))) {
                _requestVO.setCosValTime(Long.parseLong((String) map.get("IN_COS_VAL_TIME")));
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;// @@ ends

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (_log.isDebugEnabled()) {
            _log.debug("updateForReceiverCreditResponse", "Mape from response=" + map + " status=" + status + " interface Status=" + interfaceStatusType);
        }
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
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
        _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
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
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID1"))) {
            try {
                _receiverTransferItemVO.setInterfaceReferenceID1((String) map.get("IN_TXN_ID1"));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _receiverTransferItemVO.setTransferType1(PretupsI.TRANSFER_TYPE_BA_ADJ_CR);
        }
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID2"))) {
            try {
                _receiverTransferItemVO.setInterfaceReferenceID2((String) map.get("IN_TXN_ID2"));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _receiverTransferItemVO.setTransferType2(PretupsI.TRANSFER_TYPE_BA_ADJ_DR);
        }
        _receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        String[] strArr = null;
        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            if (vasErrorMsgReqd != null && vasErrorMsgReqd.equals(PretupsI.YES)) {
                _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(status + "_R"));
            }
            _c2sTransferVO.setErrorCode(status + "_R");
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(status);
            strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            // throw new
            // BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.C2S_RECEIVER_FAIL,0,strArr,null);
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", _c2sTransferVO.getErrorCode(), 0, strArr, null);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
        	if(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWED_SERVICES_FOR_FAIL_WHEN_AMBIGUOUS)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWED_SERVICES_FOR_FAIL_WHEN_AMBIGUOUS)).contains(_c2sTransferVO.getServiceType()))
        	{
        		if (vasErrorMsgReqd != null && vasErrorMsgReqd.equals(PretupsI.YES)) {
        			_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(status + "_R"));
        		}
        		_c2sTransferVO.setErrorCode(status + "_R");
        		_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
        		_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
        		_receiverTransferItemVO.setTransferStatus(status);
        		strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
        		// throw new
        		// BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.C2S_RECEIVER_FAIL,0,strArr,null);
        		throw new BTSLBaseException(this, "updateForReceiverValidateResponse", _c2sTransferVO.getErrorCode(), 0, strArr, null);

        	}
        	else
        	{
	            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
	            _receiverTransferItemVO.setTransferStatus(status);
	            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
	            _receiverTransferItemVO.setUpdateStatus(status);
	            _operatorUtil.updateBonusListAfterTopup(map, _c2sTransferVO);
	            strArr = new String[] { _transferID, _receiverTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
	            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        	}
        } else {
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _receiverTransferItemVO.setUpdateStatus(status);
        }

        try {
            _receiverTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setNewGraceDate(BTSLUtil.getDateFromDateString((String) map.get("NEW_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setNewPromoBalance(Long.parseLong((String) map.get("INTERFACE_PROMO_POST_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setNewPromoExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_PROMO_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;

        try {
            final String promoStatus = (String) map.get("PROMO_STATUS");
            if (promoStatus.equals(InterfaceErrorCodesI.ERROR_RESPONSE)) {
                _receiverTransferItemVO.setPromoStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            } else {
                _receiverTransferItemVO.setPromoStatus(promoStatus);
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        _receiverTransferItemVO.setInterfacePromoStatus((String) map.get("PROMO_INTERFACE_STATUS"));
        try {
            final String cosStatus = (String) map.get("COS_STATUS");
            if (cosStatus.equals(InterfaceErrorCodesI.ERROR_RESPONSE)) {
                _receiverTransferItemVO.setCosStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            } else {
                _receiverTransferItemVO.setCosStatus(cosStatus);
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        _operatorUtil.updateBonusListAfterTopup(map, _c2sTransferVO);
        _receiverTransferItemVO.setInterfaceCosStatus((String) map.get("COS_INTERFACE_STATUS"));
        _receiverTransferItemVO.setNewServiceClssCode((String) map.get("INTERFACE_POST_COS"));
        // Lohit
        try {
            _receiverTransferItemVO.setPostCreditCoreValidity(BTSLUtil.getDateFromDateString((String) map.get("POSTCRE_NEW_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setNewGraceDate(BTSLUtil.getDateFromDateString((String) map.get("NEW_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setPostCreditCoreBalance(Long.parseLong((String) map.get("POSTCRE_INTERFACE_POST_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setPostCreditPromoBalance(Long.parseLong((String) map.get("POSTCRE_INTERFACE_PROMO_POST_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setPostCreditPromoValidity(BTSLUtil.getDateFromDateString((String) map.get("POSTCRE_NEW_PROMO_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setPostValidationStatus((String) map.get("POSTCRE_TRANSACTION_STATUS"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;

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

        if ((!_receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
            .equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
            isReceiverFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, receiverPrefixID, _receiverVO.getSubscriberType(), receiverNetworkCode, _c2sTransferVO
                .getServiceType(), _type, PretupsI.USER_TYPE_RECEIVER, action);
        } else {
            isReceiverFound = true;
        }

        if (!isReceiverFound) {
            throw new BTSLBaseException("VasPrepaidController", "populateServiceInterfaceDetails", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING);
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
                    _log.debug("VasPrepaidController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
                }
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                final String strArr[] = { _receiverMSISDN, String.valueOf(PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())) };
                throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            _log.error("VasPrepaidController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
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

            /*
             * This code was commented on 01/04/08 to eliminate fetch conversion
             * rate step.
             * Now Moldova will support single currency. Previously conversion
             * rate was required to
             * support multiple currency for moldova.
             * //Change the receiver amount for multiplication factor interface
             * wise.

             * //get the conversion value for receiver

             * //set requested amount after conversion in transferVO. This is

             */
            // End of single currency request change

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            final String receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug("run", _transferID, "Got the validation response from IN Module receiverValResponse=" + receiverValResponse);
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
				con = mcomCon.getConnection();
                // validate receiver limits after Interface Validations
                PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
                throw be;
            } catch (Exception e) {
                LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
                // validate receiver limits after Interface Validations
                PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
                throw new BTSLBaseException(e);
            }

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

            // If request is taking more time till validation of subscriber than
            // reject the request.
            InterfaceVO interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
            if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > interfaceVO.getValExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VasPrepaidController[processValidationRequest]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till validation");
                throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
            }

            // Get the service Class ID based on the code
            PretupsBL.validateServiceClassChecks(con, _receiverTransferItemVO, _c2sTransferVO, PretupsI.C2S_MODULE, _requestVO.getServiceType());
            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());

            // validate sender receiver service class,validate transfer value
            PretupsBL.validateTransferRule(con, _c2sTransferVO, PretupsI.C2S_MODULE);

            // for amount
            ArrayList cardGrpDetails = null;
            long startrange = 0;
            long prevStartrange = 0;
            long endRange = 0;
            long prevEndrange = 0;
            int index = 0;
            CardGroupDetailsVO cardGroupDetailsVO = null;
            if (_c2sTransferVO.getRequestedAmount() == 0) {
                cardGrpDetails = CardGroupBL.getCardGroupDetails(con, _c2sTransferVO);
                
                if (cardGrpDetails.size() > 1) {
                	int cardGrpDetailsSize = cardGrpDetails.size();
                    for (int i = 0; i < cardGrpDetailsSize; i++) {
                        cardGroupDetailsVO = (CardGroupDetailsVO) cardGrpDetails.get(i);
                        startrange = cardGroupDetailsVO.getStartRange() / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
                        endRange = cardGroupDetailsVO.getEndRange() / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
                        if (prevStartrange == 0 && prevEndrange == 0) {
                            prevStartrange = startrange;
                            prevEndrange = endRange;
                        }
                        if (startrange == endRange) {
                            if (prevStartrange >= startrange && prevEndrange >= endRange) {
                                startrange = prevStartrange;
                                endRange = prevEndrange;
                                index = i;
                            }
                        } else {
                            throw new BTSLBaseException("VasPrepaidController", "run", PretupsErrorCodesI.CARD_GROUP_START_AND_END_RANGE_DIFFERENT);
                        }
                    }
                    cardGroupDetailsVO = (CardGroupDetailsVO) cardGrpDetails.get(index);
                } else {
                    cardGroupDetailsVO = (CardGroupDetailsVO) cardGrpDetails.get(0);
                    startrange = cardGroupDetailsVO.getStartRange() / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
                    endRange = cardGroupDetailsVO.getEndRange() / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
                }

              
            }
            // Validate Sender Transaction profile checks and balance
            // availablility for user
            ChannelUserBL.validateSenderAvailableControls(con, _transferID, _c2sTransferVO);

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
            // End of single currency request change

            // Method to insert the record in c2s transfer table
            ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO);
            _transferDetailAdded = true;

            // Commit the transaction and relaease the locks
            try {
                con.commit();
            } catch (Exception be) {
                _log.errorTrace(methodName, be);
            }
			if (mcomCon != null) {
				mcomCon.close("VasPrepaidController#processValidationRequest");
				mcomCon = null;
			}
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
            if (_c2sTransferVO.isUnderProcessMsgReq() && _receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
                .equals(_notAllowedRecSendMessGatw)) {
                (new PushMessage(_receiverMSISDN, getReceiverUnderProcessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
            }

            // If request is taking more time till topup of subscriber than
            // reject the request.
            if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > interfaceVO.getTopUpExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VasPrepaidController[run]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till topup");
                throw new BTSLBaseException("VasPrepaidController", "run", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
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
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
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
            _log.error("VasPrepaidController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());
            if (_transferDetailAdded) {
                if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
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
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (con != null) {
            	mcomCon.finalRollback();
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                }
            }
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            if (_transferDetailAdded) {
                if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
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
            throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("VasPrepaidController#processValidationRequest");
				mcomCon = null;
			}
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
        strBuff.append("&INTERFACE_AMOUNT=" + _c2sTransferVO.getReceiverTransferValue());
        return strBuff.toString();
    }

    /**
     * Method to get the string to be sent to the interface for topup
     * 
     * @return
     */
    public String getReceiverCreditStr() {
        final String METHOD_NAME = "getReceiverCreditStr";
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getReceiverCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_CREDIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + _c2sTransferVO.getReceiverTransferValue());
        strBuff.append("&GRACE_DAYS=" + _receiverTransferItemVO.getGraceDaysStr());
        strBuff.append("&CARD_GROUP=" + _c2sTransferVO.getCardGroupCode());
        strBuff.append("&MIN_CARD_GROUP_AMT=" + _c2sTransferVO.getMinCardGroupAmount());
        strBuff.append("&SENDER_MSISDN=" + _senderMSISDN);
        strBuff.append("&SENDER_ID=" + _channelUserVO.getUserID());
        strBuff.append("&SENDER_EXTERNAL_CODE=" + _channelUserVO.getExternalCode());
        strBuff.append("&PRODUCT_CODE=" + _c2sTransferVO.getProductCode());
        strBuff.append("&VALIDITY_DAYS=" + _c2sTransferVO.getReceiverValidity());
        strBuff.append("&BONUS_VALIDITY_DAYS=" + _c2sTransferVO.getReceiverBonusValidity());
        strBuff.append("&BONUS_AMOUNT=" + _c2sTransferVO.getReceiverBonusValue());
        try {
            strBuff.append("&OLD_EXPIRY_DATE=" + BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getPreviousExpiry()));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);

        }
        strBuff.append("&INTERFACE_PREV_BALANCE=" + _receiverTransferItemVO.getPreviousBalance());
        // Avinash send the requested amount to IN. to use card group only for
        // reporting purpose.
        strBuff.append("&REQUESTED_AMOUNT=" + _c2sTransferVO.getRequestedAmount());
        strBuff.append("&SERVICE_CLASS=" + _receiverTransferItemVO.getServiceClassCode());
        strBuff.append("&SOURCE_TYPE=" + _c2sTransferVO.getSourceType());
        // Added by Zafar Abbas on 13/02/2008 after adding two new fields for
        // Bonus SMS/MMS in Card group
        strBuff.append("&BONUS1=" + _c2sTransferVO.getReceiverBonus1());
        strBuff.append("&BONUS2=" + _c2sTransferVO.getReceiverBonus2());

        strBuff.append("&BUNDLE_TYPES=" + _receiverTransferItemVO.getBundleTypes());
        strBuff.append("&BUNDLE_NAMES=" + _receiverTransferItemVO.getBundleTypes());
        strBuff.append("&BONUS_BUNDLE_VALIDITIES=" + _receiverTransferItemVO.getBonusBundleValidities());
        // added by vikas 12/12/08 for updation of card group for new entries of
        // new field
        strBuff.append("&BONUS1_VAL=" + _c2sTransferVO.getReceiverBonus1Validity());
        strBuff.append("&BONUS2_VAL=" + _c2sTransferVO.getReceiverBonus2Validity());
        strBuff.append("&CREDIT_BONUS_VAL=" + _c2sTransferVO.getReceiverCreditBonusValidity());

        // If Online flag and Both Flag are selected/checked then it would be
        // Combined Recharge
        // If only Online Flag is selected/checked then it would be Implicit
        // Recharge
        // If Online Flag and Both Flag are not selected/checked then it would
        // be called Explicit Recharge

        strBuff.append("&COMBINED_RECHARGE=" + _c2sTransferVO.getBoth());
        strBuff.append("&EXPLICIT_RECHARGE=" + _c2sTransferVO.getOnline());
        strBuff.append("&IMPLICIT_RECHARGE=" + _c2sTransferVO.getOnline());
        strBuff.append("&IN_ACCOUNT_ID=" + _receiverTransferItemVO.getInAccountId());
        // For Get Number Back Service
        if (_receiverTransferItemVO.isNumberBackAllowed()) {
            final String numbck_diff_to_in = _c2sTransferVO.getServiceType() + PreferenceI.NUMBCK_DIFF_REQ_TO_IN;
            final Boolean NBR_BK_SEP_REQ = (Boolean) PreferenceCache.getControlPreference(numbck_diff_to_in, _c2sTransferVO.getNetworkCode(), _receiverTransferItemVO
                .getInterfaceID());
            strBuff.append("&NBR_BK_DIFF_REQ=" + NBR_BK_SEP_REQ);
        }

        // For COS change and Promotion Amount
        strBuff.append("&PROMOTION_AMOUNT=" + _c2sTransferVO.getInPromo());
        strBuff.append("&COS_FLAG=" + _c2sTransferVO.getCosRequired());
        strBuff.append("&NEW_COS_SERVICE_CLASS=" + _c2sTransferVO.getNewCos());
        strBuff.append("&INTERFACE_PROMO_PREV_BALANCE=" + _receiverTransferItemVO.getPreviousPromoBalance());
        strBuff.append("&CAL_OLD_EXPIRY_DATE=" + _receiverTransferItemVO.getPreviousExpiryInCal());
        strBuff.append("&PROMO_CAL_OLD_EXPIRY_DATE=" + _receiverTransferItemVO.getPreviousPromoExpiryInCal());
        strBuff.append("&PROMO_OLD_EXPIRY_DATE=" + _receiverTransferItemVO.getPreviousPromoExpiry());
        strBuff.append("&RC_COMMENT=" + _c2sTransferVO.getRechargeComment());
        strBuff.append("&SELECTOR_BUNDLE_ID=" + _receiverBundleID);
        strBuff.append("&SELECTOR_BUNDLE_TYPE=" + _c2sTransferVO.getSelectorBundleType());
        strBuff.append("&BONUS_BUNDLE_IDS=" + _c2sTransferVO.getBonusBundleIdS());
        strBuff.append("&BONUS_BUNDLE_TYPES=" + _c2sTransferVO.getBonusBundleTypes());
        strBuff.append("&BONUS_BUNDLE_VALUES=" + _c2sTransferVO.getBonusBundleValues());
        strBuff.append("&BONUS_BUNDLE_VALIDITIES=" + _c2sTransferVO.getBonusBundleValidities());
        strBuff.append("&IN_RESP_BUNDLE_CODES=" + _receiverTransferItemVO.getBundleTypes());
        strBuff.append("&BONUS_BUNDLE_NAMES=" + _c2sTransferVO.getBonusBundleNames());
        strBuff.append("&BONUS_BUNDLE_RATES=" + _c2sTransferVO.getBonusBundleRate());
        strBuff.append("&BONUS_BUNDLE_CODES=" + _c2sTransferVO.getBonusBundleCode());
        strBuff.append("&IN_RESP_BUNDLE_PREV_BALS=" + _receiverTransferItemVO.getPrevBundleBals());
        strBuff.append("&IN_RESP_BUNDLE_PREV_VALIDITY=" + _receiverTransferItemVO.getPrevBundleExpiries());
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
        // added for CRE_INT_CR00029 by ankit Zindal
        strBuff.append("&CARD_GROUP_SELECTOR=" + _requestVO.getReqSelector());
        strBuff.append("&USER_TYPE=R");
        strBuff.append("&REQ_SERVICE=" + _serviceType);
        strBuff.append("&INT_ST_TYPE=" + _c2sTransferVO.getReceiverInterfaceStatusType());
        strBuff.append("&SELECTOR_BUNDLE_ID=" + _receiverBundleID);
        return strBuff.toString();
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
        if (!("N".equals(_c2sTransferVO.getBoth()) && "N".equals(_c2sTransferVO.getOnline()))) {
            messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO
                .getValidity()), _senderMSISDN, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_c2sTransferVO
                .getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO.getUserName(), String.valueOf(BTSLUtil.parseDoubleToLong(  _c2sTransferVO.getReceiverBonus1())), String
                .valueOf(BTSLUtil.parseDoubleToLong(  _c2sTransferVO.getReceiverBonus2())), PretupsBL.getDisplayAmount(_c2sTransferVO.getBonusTalkTimeValue()), PretupsBL
                .getDisplayAmount(_c2sTransferVO.getCalminusBonusvalue()), String.valueOf(_c2sTransferVO.getReceiverBonus1Validity()), String.valueOf(_c2sTransferVO
                .getReceiverBonus2Validity()), String.valueOf(_c2sTransferVO.getReceiverCreditBonusValidity()), _receiverTransferItemVO.getBonus1Name(), _receiverTransferItemVO
                .getBonus2Name(), _receiverTransferItemVO.getSelectorName() };
            key = PretupsErrorCodesI.IMPLICIT_MSG;
        } else {
            // For Get NUMBER BACK Service
            if (_c2sTransferVO.getReceiverTransferItemVO().isNumberBackAllowed()) {
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED))).booleanValue()) {
                    _c2sTransferVO.setCalminusBonusvalue(_c2sTransferVO.getReceiverTransferValue() - _c2sTransferVO.getBonusTalkTimeValue());
                }
                // added by vikas kumar for card group sms/mms
                messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO
                    .getValidity()), _senderMSISDN, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_c2sTransferVO
                    .getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO.getUserName(), String.valueOf(_c2sTransferVO.getReceiverBonus1()), String
                    .valueOf(_c2sTransferVO.getReceiverBonus2()), PretupsBL.getDisplayAmount(_c2sTransferVO.getBonusTalkTimeValue()), PretupsBL
                    .getDisplayAmount(_c2sTransferVO.getCalminusBonusvalue()), String.valueOf(_c2sTransferVO.getReceiverBonus1Validity()), String.valueOf(_c2sTransferVO
                    .getReceiverBonus2Validity()), String.valueOf(_c2sTransferVO.getReceiverCreditBonusValidity()) };
                if (_c2sTransferVO.getBonusTalkTimeValue() == 0) {
                    key = PretupsErrorCodesI.VAST_RECEIVER_GET_NUMBER_BACK_SUCCESS;
                } else {
                    key = PretupsErrorCodesI.VAST_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS;
                }
            }

            if (!"N".equals(_receiverPostBalanceAvailable)) {
                String dateStrGrace = null;
                String dateStrValidity = null;
                // Changed by ankit Zindal on date 2/08/06 for problem when
                // validity and grace date is null
                try {
                    dateStrGrace = (_receiverTransferItemVO.getNewGraceDate() == null) ? "0" : BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewGraceDate());
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    dateStrGrace = String.valueOf(_receiverTransferItemVO.getNewGraceDate());
                }
                try {
                    dateStrValidity = (_receiverTransferItemVO.getNewExpiry() == null) ? "0" : BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewExpiry());
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    dateStrValidity = String.valueOf(_receiverTransferItemVO.getNewExpiry());
                }
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED))).booleanValue()) {
                    _c2sTransferVO.setCalminusBonusvalue(_c2sTransferVO.getReceiverTransferValue() - _c2sTransferVO.getBonusTalkTimeValue());
                }
                // added by vikas kumar for card group sms/mms
                // / messageArgArray=new
                // String[]{_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()),_senderMSISDN,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()),_c2sTransferVO.getSubService(),_channelUserVO.getUserName(),String.valueOf(_c2sTransferVO.getReceiverBonus1()),String.valueOf(_c2sTransferVO.getReceiverBonus2()),PretupsBL.getDisplayAmount(_c2sTransferVO.getBonusTalkTimeValue()),PretupsBL.getDisplayAmount(_c2sTransferVO.getCalminusBonusvalue()),String.valueOf(_c2sTransferVO.getReceiverBonus1Validity()),String.valueOf(_c2sTransferVO.getReceiverBonus2Validity()),String.valueOf(_c2sTransferVO.getReceiverCreditBonusValidity())};
                messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), _senderMSISDN, PretupsBL
                    .getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO
                    .getUserName(), String.valueOf(_c2sTransferVO.getReceiverBonus1()), String.valueOf(_c2sTransferVO.getReceiverBonus2()), PretupsBL
                    .getDisplayAmount(_c2sTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(_c2sTransferVO.getCalminusBonusvalue()), String
                    .valueOf(_c2sTransferVO.getReceiverBonus1Validity()), String.valueOf(_c2sTransferVO.getReceiverBonus2Validity()), String.valueOf(_c2sTransferVO
                    .getReceiverCreditBonusValidity()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostCreditCoreBalance()), PretupsBL
                    .getDisplayAmount(_receiverTransferItemVO.getPostCreditPromoBalance()), String.valueOf(_receiverTransferItemVO.getPostCreditCoreValidity()), String
                    .valueOf(_receiverTransferItemVO.getPostCreditPromoValidity()) };
                if (!InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getPostValidationStatus())) {
                    if ((_c2sTransferVO.getBonusTalkTimeValue() == 0)) {
                        key = PretupsErrorCodesI.VAST_RECEIVER_SUCCESS_WITHOUT_POSTBAL;// return
                        // BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
                    } else {
                        key = PretupsErrorCodesI.VAST_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS;
                    }
                } else {
                    if ((_c2sTransferVO.getBonusTalkTimeValue() == 0)) {
                        key = PretupsErrorCodesI.VAST_RECEIVER_SUCCESS;// return
                        // BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
                    } else {
                        key = PretupsErrorCodesI.VAST_RECEIVER_SUCCESS_WITH_BONUS;
                    }
                }
            } else {
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED))).booleanValue()) {
                    _c2sTransferVO.setCalminusBonusvalue(_c2sTransferVO.getReceiverTransferValue() - _c2sTransferVO.getBonusTalkTimeValue());
                }
                // added by vikas kumar for card group sms/mms
                messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), _senderMSISDN, PretupsBL
                    .getDisplayAmount(_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService(), _channelUserVO
                    .getUserName(), String.valueOf(_c2sTransferVO.getReceiverBonus1()), String.valueOf(_c2sTransferVO.getReceiverBonus2()), PretupsBL
                    .getDisplayAmount(_c2sTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(_c2sTransferVO.getCalminusBonusvalue()), String
                    .valueOf(_c2sTransferVO.getReceiverBonus1Validity()), String.valueOf(_c2sTransferVO.getReceiverBonus2Validity()), String.valueOf(_c2sTransferVO
                    .getReceiverCreditBonusValidity()) };
                if (_c2sTransferVO.getBonusTalkTimeValue() == 0) {
                    key = PretupsErrorCodesI.VAST_RECEIVER_SUCCESS_WITHOUT_POSTBAL;// return
                    // BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.VAST_RECEIVER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
                } else {
                    key = PretupsErrorCodesI.VAST_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS;
                }
            }
        }
		String tempKey = _c2sTransferVO.getServiceType() + "_"+key;
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_C2S)).booleanValue()) {
            String message = null;
            try {
				message = BTSLUtil.getMessage(_receiverLocale, tempKey + "_" + _receiverTransferItemVO.getServiceClass(), messageArgArray, _requestVO.getRequestGatewayType());
            	if(BTSLUtil.isNullString(message))
                message = BTSLUtil.getMessage(_receiverLocale, key + "_" + _receiverTransferItemVO.getServiceClass(), messageArgArray, _requestVO.getRequestGatewayType());
                if (!BTSLUtil.isNullString(message)) {
                    return message;
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        String message = null; 
        message = BTSLUtil.getMessage(_receiverLocale, tempKey, messageArgArray, _requestVO.getRequestGatewayType());
        if(BTSLUtil.isNullString(message))
        	message = BTSLUtil.getMessage(_receiverLocale, key, messageArgArray, _requestVO.getRequestGatewayType());
        
        return message;
    }

    private String getSenderSuccessMessage() {
        final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL
            .getDisplayAmount(_senderTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL
            .getDisplayAmount(_receiverTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO
            .getReceiverAccessFee()), _c2sTransferVO.getSubService() };
        String message = null;
        message = BTSLUtil.getMessage(_senderLocale, _c2sTransferVO.getServiceType()+"_"+PretupsErrorCodesI.VAST_SENDER_SUCCESS, messageArgArray);
        if(BTSLUtil.isNullString(message))
        	message =  BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.VAST_SENDER_SUCCESS, messageArgArray);
        
        return message;
    }

    /**
     * Method to get the under process message to be sent to receiver
     * 
     * @return
     */
    private String getReceiverUnderProcessMessage() {
        final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO
            .getValidity()), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderMSISDN, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _channelUserVO
            .getUserName() };
        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.VAST_RECEIVER_UNDERPROCESS, messageArgArray, _requestVO.getRequestGatewayType());
    }

    /**
     * Method to get the success message to be sent to sender
     * 
     * @return
     */
    private String getSenderUnderProcessMessage() {
        final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), String
            .valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()) };
        String message = null;
        message = BTSLUtil.getMessage(_senderLocale, _c2sTransferVO.getServiceType()+"_"+PretupsErrorCodesI.VAST_SENDER_UNDERPROCESS, messageArgArray);
        if(BTSLUtil.isNullString(message))
        	message =  BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.VAST_SENDER_UNDERPROCESS, messageArgArray);
        
        return message;
    }

    /**
     * Method to get the under process message before validation to be sent to
     * sender
     * 
     * @return
     */
    private String getSndrUPMsgBeforeValidation() {
        final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
        String message = null;
        message = BTSLUtil.getMessage(_senderLocale, _c2sTransferVO.getServiceType()+"_"+PretupsErrorCodesI.VAST_SENDER_UNDERPROCESS_B4VAL, messageArgArray);
        if(BTSLUtil.isNullString(message))
        	message =  BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.VAST_SENDER_UNDERPROCESS_B4VAL, messageArgArray);
        
        return message;
    }

    private String getReceiverAmbigousMessage() {
        final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderMSISDN, _channelUserVO.getUserName() };
        String message = null;
        message = BTSLUtil.getMessage(_senderLocale, _c2sTransferVO.getServiceType()+"_"+PretupsErrorCodesI.VAST_RECEIVER_AMBIGOUS_KEY, messageArgArray, _requestVO.getRequestGatewayType());
        if(BTSLUtil.isNullString(message))
        	message =  BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.VAST_RECEIVER_AMBIGOUS_KEY, messageArgArray, _requestVO.getRequestGatewayType());
        
        return message;
    }

    private String getReceiverFailMessage() {
        final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderMSISDN, _channelUserVO.getUserName() };
        String message = null;
        message = BTSLUtil.getMessage(_senderLocale, _c2sTransferVO.getServiceType()+"_"+PretupsErrorCodesI.VAST_RECEIVER_FAIL_KEY, messageArgArray, _requestVO.getRequestGatewayType());
        if(BTSLUtil.isNullString(message))
        	message =  BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.VAST_RECEIVER_FAIL_KEY, messageArgArray, _requestVO.getRequestGatewayType());
        
        return message;
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

            if (_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                _requestVO.setSuccessTxn(false);
                final String[] messageArgArray = { _c2sTransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _c2sTransferVO
                    .getTransferID(), PretupsBL.getDisplayAmount(_userBalancesVO.getBalance()) };
                _requestVO.setMessageArguments(messageArgArray);
                _requestVO.setMessageCode(PretupsErrorCodesI.C2S_SENDER_CREDIT_SUCCESS);
            }
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
     * Method that will perform the validation request in thread
     * 
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequestInThread() throws BTSLBaseException, Exception {
        final String methodName = "processValidationRequestInThread";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered and performing validations for transfer ID=" + _transferID);
        }
        try {
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            processValidationRequest();
        } catch (BTSLBaseException be) {
            _log.error("VasPrepaidController[processValidationRequestInThread]", "Getting BTSL Base Exception:" + be.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL
                    .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
            }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            _log.error(this, _transferID, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "VasPrepaidController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                    if(mcomCon != null){mcomCon.close("VasPrepaidController#processValidationRequestInThread");mcomCon=null;}
                    con=null;
                    _log.errorTrace(methodName, be);
                } catch (Exception e) {
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (Exception ex) {
                            _log.errorTrace(methodName, ex);
                        }
                    }
                    if(mcomCon != null){mcomCon.close("VasPrepaidController#processValidationRequestInThread");mcomCon=null;}
                    con=null;
                    _log.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "VasPrepaidController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
                } finally {
                	if(mcomCon != null){mcomCon.close("VasPrepaidController#processValidationRequestInThread");mcomCon=null;}
                    con = null;
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[process]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
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
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                " Entered with MSISDN=" + p_msisdn + " Prefix ID=" + p_prefixID + " p_subscriberType=" + p_subscriberType + " p_networkCode=" + p_networkCode + " p_serviceType=" + p_serviceType + " p_interfaceCategory=" + p_interfaceCategory + " p_userType=" + p_userType + " p_action=" + p_action);
        }
        boolean isSuccess = false;
        /*
         * Interface loaded will always be on the basis of
         * service,selector(product) based interface mappping from the DB
         * for Vas recharge service as selector is mandatory
         */

        String interfaceID = null;
        String interfaceHandlerClass = null;
        String underProcessMsgReqd = null;
        String allServiceClassID = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "");
            }
            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache
                    .getObject(_serviceType + "_" + _c2sTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                if (interfaceMappingVO1 != null) {
                    interfaceID = interfaceMappingVO1.getInterfaceID();
                    interfaceHandlerClass = interfaceMappingVO1.getHandlerClass();
                    underProcessMsgReqd = interfaceMappingVO1.getUnderProcessMsgRequired();
                    allServiceClassID = interfaceMappingVO1.getAllServiceClassID();
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
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
                    }
                } else {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VAS_PROMOVAS_MAPPING_NOT_EXIST);
                }
            } else {
                isSuccess = false;
            }

            if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
                _receiverTransferItemVO.setInterfaceID(interfaceID);
                _receiverTransferItemVO.setInterfaceType(_type);
                _receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                if (PretupsI.YES.equals(underProcessMsgReqd)) {
                    _c2sTransferVO.setUnderProcessMsgReq(true);
                }
                _receiverAllServiceClassID = allServiceClassID;
                _c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
            }
        } catch (BTSLBaseException be) {
            if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
                _receiverTransferItemVO.setInterfaceID(interfaceID);
                _receiverTransferItemVO.setInterfaceType(_type);
                _receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                if (PretupsI.YES.equals(underProcessMsgReqd)) {
                    _c2sTransferVO.setUnderProcessMsgReq(true);
                }
                _receiverAllServiceClassID = allServiceClassID;
                _c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
            }
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[getInterfaceRoutingDetails]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            isSuccess = false;
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting with isSuccess=" + isSuccess);
        }
        return isSuccess;
    }

    /**
     * Method to perform the Interface routing for the subscriber MSISDN
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
                                throw new BTSLBaseException(this, "performAlternateRouting", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
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
                                // not required in case of VAs and Promo vas
                                // service
                                /*
                                 * if(InterfaceErrorCodesI.SUCCESS.equals(
                                 * _receiverTransferItemVO.getValidationStatus())
                                 * )
                                 * {
                                 * //Update in DB for routing interface
                                 * if(_receiverInterfaceInfoInDBFound)
                                 * PretupsBL.updateSubscriberInterfaceRouting(
                                 * _receiverTransferItemVO
                                 * .getInterfaceID(),_externalID,_receiverMSISDN
                                 * ,_type,_channelUserVO.getUserID(),_currentDate
                                 * );
                                 * else
                                 * {
                                 * SubscriberRoutingControlVO
                                 * subscriberRoutingControlVO=
                                 * SubscriberRoutingControlCache
                                 * .getRoutingControlDetails
                                 * (_c2sTransferVO.getReceiverNetworkCode
                                 * ()+"_"+_c2sTransferVO.getServiceType()+"_"+_type
                                 * );
                                 * if(!_receiverInterfaceInfoInDBFound &&
                                 * subscriberRoutingControlVO!=null &&
                                 * subscriberRoutingControlVO.isDatabaseCheckBool
                                 * ())
                                 * {
                                 * PretupsBL.insertSubscriberInterfaceRouting(
                                 * _receiverTransferItemVO
                                 * .getInterfaceID(),_externalID,_receiverMSISDN
                                 * ,_type,_channelUserVO.getUserID(),_currentDate
                                 * );
                                 * _receiverInterfaceInfoInDBFound=true;
                                 * }
                                 * }
                                 * }
                                 */
                            } catch (BTSLBaseException be) {
                                throw be;
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
                                throw new BTSLBaseException(this, "performAlternateRouting", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
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
                                        throw new BTSLBaseException(this, "performAlternateRouting", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
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
                                        throw bex;
                                    } catch (Exception e) {
                                    	throw new BTSLBaseException(this, methodName, "");
                                    }
                                } else {
                                    throw be;
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
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[performAlternateRouting]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberInterfaceRouting", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
        final String METHOD_NAME = "receiverValidateResponse";
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
            throw new BTSLBaseException(this, "receiverValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        }
        // receiver language has to be taken from IN then the block below will
        // execute
        if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
            // update the receiver locale if language code returned from IN is
            // not null
            updateReceiverLocale((String) map.get("IN_LANG"));
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
            throw new BTSLBaseException("VasPrepaidController", "updateForReceiverValidateResponse", PretupsErrorCodesI.C2S_RECEIVER_FAIL, 0, strArr, null);
        }

        _receiverTransferItemVO.setTransferStatus(status);
        _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

        try {
            _receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);

        }
        ;
        try {
            _receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        _receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        // Done so that receiver check can be brough to common
        _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
        _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());

        try {
            _receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        ;
        _receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
        _receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));

        // TO DO Done for testing purpose should we use it or give exception in
        // this case
        if (_receiverTransferItemVO.getPreviousExpiry() == null) {
            _receiverTransferItemVO.setPreviousExpiry(_currentDate);
        }
        _operatorUtil.populateBonusListAfterValidation(map, _c2sTransferVO);
    }

    /**
     * Method to process request from queue
     * 
     * @param p_transferVO
     */
    public void processFromQueue(TransferVO p_transferVO) {
        final String methodName = "processFromQueue";
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

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
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
                throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
			if (mcomCon != null) {
				mcomCon.close("VasPrepaidController#processFromQueue");
				mcomCon = null;
			}
            con = null;

            if (_log.isDebugEnabled()) {
                _log.debug("VasPrepaidController[processFromQueue]", "_transferID=" + _transferID + " Successfully through load");
            }
            _processedFromQueue = true;

            processValidationRequest();
            // Set under process message for the sender and reciever
            p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
            final String[] messageArgArray = { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
            p_transferVO.setMessageArguments(messageArgArray);
        } catch (BTSLBaseException be) {
			if (mcomCon != null) {
				mcomCon.close("VasPrepaidController#processFromQueue");
				mcomCon = null;
			}
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
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[processFromQueue]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[processFromQueue]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + e.getMessage());
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
			if (mcomCon != null) {
				mcomCon.close("VasPrepaidController#processFromQueue");
				mcomCon = null;
			}
            con = null;
            _log.errorTrace(methodName, e);
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[processFromQueue]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[processFromQueue]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + ex.getMessage());
            }
            // checking condition whether channel receiver required the general
            // failure message
            if (_recValidationFailMessageRequired) {
                // if receivermessage is null or it is not key
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // setting receiver return message
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[processFromQueue]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            // logging in the transaction log
            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());
        } finally {
            try {
                if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
                }
                // makking entry in the transfer table if transfer entry has not
                // been made and message gateway flow is common, i.e. validation
                // is not in thread
                if (_transferID != null && !_transferDetailAdded) {
                    addEntryInTransfers(con);
                }
            } catch (BTSLBaseException be) {

                _log.errorTrace(methodName, be);
            } catch (Exception e) {

                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VasPrepaidController[processFromQueue]",
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
				if (mcomCon != null) {
					mcomCon.close("VasPrepaidController#processFromQueue");
					mcomCon = null;
				}
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
            // making entry in the transaction log
            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + _requestVO.getMessageCode());
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VasPrepaidController[updateReceiverLocale]",
                        _transferID, _receiverMSISDN, "", "Exception: Notification language returned from IN is not defined in system p_languageCode: " + p_languageCode);
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
     * @param p_transferVO
     *            generateVASTransferId method to generate transfer id for VAS
     *            services.
     *            Access specifier is default
     */
    static synchronized void generateVASTransferId(TransferVO p_transferVO) {
        final String methodName = "generateC2STransferID";
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
            } else if (_transactionIDCounter >= 9999) {
                _transactionIDCounter = 1;
            } else {
                _transactionIDCounter++;
            }
            if (_transactionIDCounter == 0) {
                throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = _operatorUtil.formatVASPVASTransferID(p_transferVO, _transactionIDCounter, _vasPrefix);
            if (transferID == null) {
                throw new BTSLBaseException("VasPrepaidController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
    }

    private String getSenderParentSuccessMessage()// ankuj
    {
        final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL
            .getDisplayAmount(_senderTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL
            .getDisplayAmount(_receiverTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO
            .getReceiverAccessFee()), _c2sTransferVO.getSubService(), _requestVO.getSenderLoginID() };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_PARENT_SUCCESS, messageArgArray);
    }

    /**
     * Date : Aug 28, 2007
     * Discription :
     * Method : setReceiverConversionFactor
     * 
     * @return void
     * @author ved.sharma
     */
    /*
     * This code was commented on 01/04/08 to eliminate fetch conversion rate
     * step.
     * Now Moldova will support single currency. Previously conversion rate was
     * required to
     * support multiple currency for moldova.
     * 
     * private void setReceiverConversionFactor()
     * {
     * _receiverTransferItemVO.setTransferValue(Math.round(_receiverTransferItemVO
     * .getTransferValue()/_receiverConversionFactor));
     * _c2sTransferVO.setReceiverBonusValue(Math.round(_c2sTransferVO.
     * getReceiverBonusValue()/_receiverConversionFactor));
     * _c2sTransferVO.setReceiverTax1Value(Math.round(_c2sTransferVO.
     * getReceiverTax1Value()/_receiverConversionFactor));
     * _c2sTransferVO.setReceiverTax2Value(Math.round(_c2sTransferVO.
     * getReceiverTax2Value()/_receiverConversionFactor));
     * _c2sTransferVO.setReceiverAccessFee(Math.round(_c2sTransferVO.
     * getReceiverAccessFee()/_receiverConversionFactor));
     * _c2sTransferVO.setReceiverTransferValue(Math.round(_c2sTransferVO.
     * getReceiverTransferValue()/_receiverConversionFactor));
     * }
     */

}
