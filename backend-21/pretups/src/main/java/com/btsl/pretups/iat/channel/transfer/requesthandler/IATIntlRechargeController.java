/*
 * Created on Jul 9, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.iat.channel.transfer.requesthandler;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.iat.businesslogic.IATNWServiceCache;
import com.btsl.pretups.iat.businesslogic.IATNetworkServiceMappingVO;
import com.btsl.pretups.iat.transfer.businesslogic.IATInterfaceVO;
import com.btsl.pretups.iat.transfer.businesslogic.IATTransferItemVO;
import com.btsl.pretups.inter.module.IATInterfaceHandlerI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.ChannelRequestDailyLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberBL;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.iat.businesslogic.IATTxnDAO;

public class IATIntlRechargeController implements ServiceKeywordControllerI, Runnable {
    private static Log _log = LogFactory.getLog(IATIntlRechargeController.class.getName());
    private static OperatorUtilI _operatorUtil = null;
    private C2STransferVO _c2sTransferVO = null;
    private Date _currentDate = null;
    private String _notAllowedSendMessGatw;
    private RequestVO _requestVO = null;
    private String _receiverAllServiceClassID = PretupsI.ALL;
    private ReceiverVO _receiverVO;
    private ChannelUserVO _channelUserVO;
    private IATTransferItemVO _iatTransferItemVO;
    private Locale _senderLocale = null;
    private String _senderNetworkCode = null;
    private long _requestID;
    private String _requestIDStr;
    private String _transferID;
    private String _type;
    private String _serviceType;
    private String _senderMSISDN;
    private String _receiverMSISDN;
    private String _notifyMSISDN;
    private Locale _iatNotifyMSISDNLocale = null;
    private String _receiverSubscriberType = null;
    private TransferItemVO _senderTransferItemVO = null;
    private TransferItemVO _receiverTransferItemVO = null;
    private UserBalancesVO _userBalancesVO = null;
    private ArrayList _itemList = null;
    private String _senderSubscriberType;
    private boolean _transferDetailAdded = false;
    private boolean _isCounterDecreased = false;
    private boolean _finalTransferStatusUpdate = true;
    private boolean _transferEntryReqd = false;
    private boolean _decreaseTransactionCounts = false;
    private boolean _processedFromQueue = false;
    private boolean _creditBackEntryDone = false;
    private Connection con = null;
    private MComConnectionI mcomCon = null;
    private static int _transactionIDCounter = 0;
    private static int _prevMinut = 0;
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    private boolean _closeConInFinally = false;
    private String _senderPushMessageMsisdn = null;
    // added by nilesh:consolidated for logger
    private boolean _oneLog = true;

    /**
     * //Receiver network code is sender network code for IAT
     */
    public IATIntlRechargeController() {
        _c2sTransferVO = new C2STransferVO();
        // Initialize the time to check the current request time.
        _currentDate = new Date();
        _notAllowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("IAT_C2S_SEN_MSG_NOT_REQD_GW"));
    }

    // Loads operator specific class
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder();
        	sb.append("Entered for Request ID=");
        	sb.append(p_requestVO.getRequestIDStr());
        	sb.append(p_requestVO.getRequestID());
        	sb.append(" MSISDN=");
        	sb.append(p_requestVO.getFilteredMSISDN());
        	sb.append(" _notAllowedSendMessGatw: ");
        	sb.append(_notAllowedSendMessGatw);
        	sb.append(" ");
        	
            _log.debug("process",sb.toString());
        }
        final String METHOD_NAME = "process";
        try {
            _requestVO = p_requestVO;
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            TransactionLog.log("", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "Received Request From Receiver", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            _senderLocale = p_requestVO.getSenderLocale();
            _senderNetworkCode = _channelUserVO.getNetworkID();
            // Populating C2STransferVO from the request VO
            populateVOFromRequest(p_requestVO);
            _requestID = p_requestVO.getRequestID();
            _requestIDStr = p_requestVO.getRequestIDStr();
            // _type is picked from service type table (TYPE field). For IAT
            // transactions value of this parameter will be 'IAT'.
            _type = p_requestVO.getType();
            _c2sTransferVO.setExtCreditIntfceType(_type);
            // Value of _service will be 'RR'.
            _serviceType = p_requestVO.getServiceType();

            // Getting oracle connection. This will be single connection used in
            // entire flow.
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Validating user message incomming in the request (for service
            // type 'RR')
            _operatorUtil.validateIRServiceRequest(con, _c2sTransferVO, p_requestVO);

            // Block added to avoid decimal amount in credit transfer. If
            // decimal for RC service is enabled then
            // IAT (RR) service will also support decimal.
            if (!BTSLUtil.isStringIn(_serviceType, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES))) {
                try {
                    String displayAmt = PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount());
                    Long.parseLong(displayAmt);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INVALID_AMOUNT);
                }
            }
            _senderLocale = p_requestVO.getSenderLocale();
            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
            _receiverVO.setModule(_c2sTransferVO.getModule());
            _receiverVO.setCreatedDate(_currentDate);
            _receiverVO.setLastTransferOn(_currentDate);
            // For IAT sender and receiver network will always be same.
            _receiverVO.setNetworkCode(_senderNetworkCode);

            // for IAT transactions receiver subscriber type will be IAT.
            // _receiverVO.setSubscriberType(_type);

            // For IAT transactions subscriber type will be set according to the
            // service type.
            // Since international recharge service is for PRE type of
            // subscriber, so subscriber type is hard coded as 'PRE'.
            // for post type of service, SubscriberType will be hard-coded as
            // 'POST' (e.g. postpaid iat controller)
            _receiverVO.setSubscriberType("PRE");
            _senderMSISDN = (_channelUserVO.getUserPhoneVO()).getMsisdn();
            _senderPushMessageMsisdn = p_requestVO.getMessageSentMsisdn();
            // receiver MSISDN will not include country code.
            _receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
            _c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
            // For IAT sender and receiver network will always be same.
            _c2sTransferVO.setReceiverNetworkCode(_senderNetworkCode);
            _c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
            // For IAT transactions sub service will always be '1'.
            _c2sTransferVO.setSubService(p_requestVO.getReqSelector());
            _c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
            _receiverSubscriberType = _receiverVO.getSubscriberType();
            // check if User maintains his corporate list or not. (corporate
            // user will not be able to do IAT transaction (RR service).)
            RestrictedSubscriberBL.isRestrictedMsisdnExistForC2S(con, _c2sTransferVO, _channelUserVO, _receiverVO.getMsisdn(), _c2sTransferVO.getRequestedAmount());

            // Chcking senders transfer profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getTransferProfileStatus())) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND);
            }

            // Chcking senders commission profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getCommissionProfileStatus())) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND);
            } else if (PretupsI.YES.equalsIgnoreCase(_channelUserVO.getOutSuspened())) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND);
            }
            // set the flag to check if any request from same user (who has sent
            // request) is under process or not (on the same message gateway).
            // Note1:- In case of IAT transactions, there will be a dummy user
            // at receiver zebra. This dummy user will always send request
            // through a EXTGW.
            // So For this Gateway flag will be 'N'. (Otherwise at a time only
            // one IAT request will be processed at receiver Zebra)
            // Note2:- For the gateways, through which requests are initiated,
            // value of this flaf will be 'Y' (Practically this will be applied
            // for sender Zebra.).
            _c2sTransferVO.setUnderProcessCheckReqd(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());

            // should be checked whether it is really required???????
            try {
                con.commit();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("IATIntlRechargeController", "process", PretupsErrorCodesI.IAT_COMMIT_ERR_EXCEPTION);
            }

            // checking if SKey is required for the C2S transfers
            /*
             * if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED)).booleanValue())
             * {
             * //It is the case of SKey forwarding request to generate the SKEY
             * processSKeyGen(_con);
             * //Set Sender Message and throw Exception
             * }
             * else
             */
            {
                // forwarding request to process the transfer request
                processTransfer();
                p_requestVO.setTransactionID(_transferID);
                _receiverVO.setLastTransferID(_transferID);
                // get IATtransactionItemVO from c2s transferVO. This was set
                // during validation of RR service request format.
                // This VO would be used to make entries in
                // C2S_IAT_TRANSFER_ITEMS table.
                _iatTransferItemVO = _c2sTransferVO.getIatTransferItemVO();
                // set sender System transaction id in IATtransactionItemVO
                _iatTransferItemVO.setIatSenderTxnId(_transferID);
                _iatTransferItemVO.setSendingNWTimestamp(_currentDate);
                _notifyMSISDN = _iatTransferItemVO.getIatNotifyMsisdn();
                _iatNotifyMSISDNLocale = _iatTransferItemVO.getIatNotifyMsisdnLocale();
                // making entry in the transaction log
                TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Generated Transfer ID", PretupsI.TXN_LOG_STATUS_SUCCESS, "Source Type=" + _c2sTransferVO.getSourceType() + " Gateway Code=" + _c2sTransferVO.getRequestGatewayCode());
                // fetch receiver network code and the IAT Code where request
                // would be routed for validation and credit.
                populateIATServiceDetails();
                // Set receiver service class in c2sTransferVO. For IAT
                // transactions, at sender side receiver's service class would
                // be ALL.
                // According to this service class only, transfer rule and card
                // group will be picked.
                _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
                _c2sTransferVO.setReceiverSubscriberType(_receiverSubscriberType); // /////////do
                                                                                   // i
                                                                                   // need
                                                                                   // this
                // Validate Sender Transaction profile checks and balance
                // availablility for user
                ChannelUserBL.validateSenderAvailableControls(con, _transferID, _c2sTransferVO);
                // setting validation status
                _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

                // //Dhiraj....should be checked whether it is really
                // required???????
                // commiting transaction and closing the transaction as it is
                // not requred////// is it needed????
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("IATIntlRechargeController", "process", PretupsErrorCodesI.IAT_COMMIT_ERR_EXCEPTION);
                }

                // Checking the Various loads and setting flag to decrease the
                // transaction count.
                // For IAT transactions (sender side), receiver network would be
                // same as sender network.
                // So interface load would be checked on sender network only.
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
                    // Process validation requests in main thread and start new
                    // thread for the topup
                    processValidationRequest();
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    // set flag to check if sender under process msg is reqd or
                    // not. This flag picked from INTERFACE_TYPES table.
                    p_requestVO.setSenderMessageRequired(_c2sTransferVO.isUnderProcessMsgReq());
                    p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());
                    // Parameter set to indicate that instance counters will not
                    // be decreased in receiver for this transaction
                    p_requestVO.setDecreaseLoadCounters(false);
                }// starting validation and topup process in thread
                else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    // Process validation and topup requests in sequential
                    // manner in new thread. main thread will not send
                    // validation or topup.
                    // Check if message needs to be sent in case of Thread
                    // implmentation
                    p_requestVO.setSenderReturnMessage(getSndrUPMsgBeforeValidation());
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    Thread _controllerThread = new Thread(this);
                    // starting thread
                    _controllerThread.start();
                    _oneLog = false;
                    // Parameter set to indicate that instance counters will not
                    // be decreased in receiver for this transaction
                    p_requestVO.setDecreaseLoadCounters(false);
                } else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                    // Process validation and topup requests in sequential
                    // manner in main thread.
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    processValidationRequest();
                    run();
                    String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPreviousBalance()), String.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService() };
                    p_requestVO.setMessageArguments(messageArgArray);
                }
            }
        } catch (BTSLBaseException be) {
            // On the basis of this flag, connection will be closed in finally
            // block. If true then only instance level single db connection will
            // be closed other wise connection will be closed in run method.
            // This flag is made true when exception occurs -
            // a) before starting new thread for credit in 'C' mode.
            // b) before starting new thread for credit in 'T' mode.
            // c) before calling run method in 'R' mode. (In 'R' mode this flag
            // is not more significance because in R case we have to close
            // connection in finally block.)
            _closeConInFinally = true;

            p_requestVO.setSuccessTxn(false);
            // to be discussed with sanjay k and Avinash
            if (PretupsErrorCodesI.IAT_COMMIT_ERR_EXCEPTION.equals(be.getMessage())) {
                try {
                    con.rollback();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }

            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            // transaction status set in iat item VO for reporting purpose.
            // (This vo finally updates the iat item table)
            if (_iatTransferItemVO != null) {
                _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
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
                // setting default error code if message and key is not found
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
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            // Populate the ChannelRequestDailyLogVo and log
            ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            // On the basis of this flag, connection will be closed in finally
            // block. If true then only instance level single db connection will
            // be closed other wise connection will be closed in run method.
            // This flag is made true when exception occurs -
            // a) before starting new thread for credit in 'C' mode.
            // b) before starting new thread for credit in 'T' mode.
            // c) before calling run method in 'R' mode. (In 'R' mode this flag
            // is not more significance because in R case we have to close
            // connection in finally block.)
            _closeConInFinally = true;

            // setting success transaction status flag to false
            p_requestVO.setSuccessTxn(false);

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

            // transaction status set in iat item VO for reporting purpose.
            // (This vo finally updates the iat item table)
            if (_iatTransferItemVO != null) {
                _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            }
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _log.error("process", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);

            // decreasing the transaction load count
            if (_transferID != null && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            // raising alarm
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            // logging in the transaction log
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            // Populate the ChannelRequestDailyLogVo and log
            ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
        } finally {
            try {
                // Getting connection if it is null
                if (mcomCon == null) {
                    mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
                // makking entry in the transfer table if transfer entry has not
                // been made and message gateway flow is common, i.e. validation
                // is not in thread
                if (_transferID != null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS)))) {
                    // added by nilesh: consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        addEntryInTransfers();
                    }
                } else if (_transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    _log.info("process", p_requestVO.getRequestIDStr(), "Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + _transferID + " But not added entry in Transfers yet");
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                _log.error("process", "BTSLBaseException:" + be.getMessage());
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("process", "Exception:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());

            }
            // Close connection if 'R' mode OR _closeConInFinally is true.
            if (con != null && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST) || _closeConInFinally)) {
                // committing transaction and closing connection
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
				}
				if (mcomCon != null) {
					mcomCon.close("IATIntlRechargeController#process");
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

            // added by nilesh : consolidated for logger
            if (_oneLog) {
                OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
            }
            // making entry in the transaction log
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
            }
        }
    }

    /**
     * Method to populate C2S Transfer VO from request VO for further use
     * 
     * @param p_requestVO
     */
    private void populateVOFromRequest(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("populateVOFromRequest", "Entered");
        }
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
        if (_log.isDebugEnabled()) {
            _log.debug("populateVOFromRequest", "Exited");
        }
    }// end populateVOFromRequest

    /**
     * @throws BTSLBaseException
     */
    private void populateIATServiceDetails() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("populateIATServiceDetails", "Entered");
        }
        final String METHOD_NAME = "populateIATServiceDetails";
        try {
            // get IAT code handler class and other values from cache and set in
            // iat item vo.
            IATNetworkServiceMappingVO iatNetworkServiceMappingVO = (IATNetworkServiceMappingVO) IATNWServiceCache.getNetworkServiceObject(_iatTransferItemVO.getIatRecCountryShortName() + "_" + _iatTransferItemVO.getIatRecNWCode() + "_" + _c2sTransferVO.getServiceType());
            /*
             * if(!PretupsI.YES.equals(iatNetworkServiceMappingVO.getServiceStatus
             * ()))
             * {
             * //set message in particular language.
             * String[] strArr=new
             * String[]{_iatTransferItemVO.getIatRcvrCountryCode
             * ()+_iatTransferItemVO
             * .getIatRecMsisdn(),_c2sTransferVO.getServiceName
             * (),_iatTransferItemVO
             * .getIatRecCountryShortName(),_iatTransferItemVO
             * .getIatRcvrCountryName(),_iatTransferItemVO.getIatRecNWCode()};
             * throw new
             * BTSLBaseException("PretupsBL","populateIATServiceDetails"
             * ,PretupsErrorCodesI.IAT_NW_CNTRY_SERVICE_SUSPEND,0,strArr,null);
             * }
             */
            /*
             * if(iatNetworkServiceMappingVO==null)
             * {
             * //set message in particular language.
             * String[] strArr=new
             * String[]{_iatTransferItemVO.getIatRcvrCountryCode
             * ()+_iatTransferItemVO
             * .getIatRecMsisdn(),_c2sTransferVO.getServiceName
             * (),_iatTransferItemVO
             * .getIatRecCountryShortName(),_iatTransferItemVO
             * .getIatRcvrCountryName(),_iatTransferItemVO.getIatRecNWCode()};
             * throw new
             * BTSLBaseException("PretupsBL","populateIATServiceDetails"
             * ,PretupsErrorCodesI.IAT_NW_CNTRY_SERVICE_SUSPEND,0,strArr,null);
             * }
             */
            if (iatNetworkServiceMappingVO == null) {
                // set message in particular language.
                IATTxnDAO iatTxnDAO = new IATTxnDAO();
                iatNetworkServiceMappingVO = iatTxnDAO.loadIATNetworkServiceSuspendedVO(_iatTransferItemVO.getIatRecCountryShortName(), _iatTransferItemVO.getIatRecNWCode(), _c2sTransferVO.getServiceType());
                if (iatNetworkServiceMappingVO != null) {
                    if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                        _c2sTransferVO.setSenderReturnMessage(iatNetworkServiceMappingVO.getLanguage1Message());
                    } else {
                        _c2sTransferVO.setSenderReturnMessage(iatNetworkServiceMappingVO.getLanguage2Message());
                    }
                    String[] strArr = new String[] { _iatTransferItemVO.getIatRcvrCountryCode() + _iatTransferItemVO.getIatRecMsisdn(), _c2sTransferVO.getServiceName(), _iatTransferItemVO.getIatRecCountryShortName(), _iatTransferItemVO.getIatRcvrCountryName(), _iatTransferItemVO.getIatRecNWCode() };
                    throw new BTSLBaseException("IATRoamReachargeController", "populateIATServiceDetails", PretupsErrorCodesI.IAT_NW_CNTRY_SERVICE_SUSPEND, 0, strArr, null);
                }
                String[] strArr = new String[] { _iatTransferItemVO.getIatRcvrCountryCode() + _iatTransferItemVO.getIatRecMsisdn(), _c2sTransferVO.getServiceName(), _iatTransferItemVO.getIatRecCountryShortName(), _iatTransferItemVO.getIatRcvrCountryName(), _iatTransferItemVO.getIatRecNWCode() };
                throw new BTSLBaseException("IATRoamReachargeController", "populateIATServiceDetails", PretupsErrorCodesI.IAT_NW_CNTRY_SERVICE_SUSPEND, 0, strArr, null);
            }
            _iatTransferItemVO.setIatHandlerClass(iatNetworkServiceMappingVO.getHandlerClass());
            _iatTransferItemVO.setIatCode(iatNetworkServiceMappingVO.getIatCode());

            InterfaceVO interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(iatNetworkServiceMappingVO.getIatCode());
            if (!PretupsI.YES.equals(interfaceVO.getStatusCode())) {
                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                    _c2sTransferVO.setSenderReturnMessage(interfaceVO.getLanguage1Message());
                } else {
                    _c2sTransferVO.setSenderReturnMessage(interfaceVO.getLanguage2Message());
                }
                throw new BTSLBaseException(this, "populateIATServiceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
            }

            _iatTransferItemVO.setIatInterfaceType(iatNetworkServiceMappingVO.getInterfaceTypeID());
            // sender id and service type are set in iat item vo for reporting
            // purpose. (iat item vo is used to make entries in IAT item table)

            _iatTransferItemVO.setServiceType(_serviceType);
            _iatTransferItemVO.setSenderId(_channelUserVO.getUserID());
            _receiverTransferItemVO.setInterfaceID(iatNetworkServiceMappingVO.getIatCode());
            _receiverTransferItemVO.setInterfaceType(iatNetworkServiceMappingVO.getInterfaceTypeID());
            // This is to set if under process message is required or not for
            // sender.
            if ("Y".equals(iatNetworkServiceMappingVO.getUnderProcessMsgReq())) {
                _c2sTransferVO.setUnderProcessMsgReq(true);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("populateIATServiceDetails", "Exception while populating iat code and handler. Exception: " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATIntlRechargeController[populateIATServiceDetails]", "", _receiverMSISDN, "", "Exception while populating iat code and handler. Exception: " + e.getMessage());
            throw new BTSLBaseException(PretupsErrorCodesI.IAT_C2S_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("populateIATServiceDetails", "Exited _iatTransferItemVO: " + _iatTransferItemVO);
            }
        }
    }

    /**
     * Method to process the request and perform the validation of the request
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    public void processTransfer() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("processTransfer", "Entered");
        }
        final String METHOD_NAME = "processTransfer";
        try {
            // Generating the Sender PreTUPS transfer ID
            _c2sTransferVO.setTransferDate(_currentDate);
            _c2sTransferVO.setTransferDateTime(_currentDate);
            if (_log.isDebugEnabled()) {
                _log.debug("generateIATRoamRechargeTransferID", "Entering");
            }
            generateIATRoamRechargeTransferID(_c2sTransferVO);
            _transferID = _c2sTransferVO.getTransferID();
            if (_log.isDebugEnabled()) {
                _log.debug("generateIATRoamRechargeTransferID", "Exited. _transferID: " + _transferID);
            }
            _receiverVO.setLastTransferID(_transferID);
            // Set sender transfer item details
            setSenderTransferItemVO();
            // set receiver transfer item details
            setReceiverTransferItemVO();
            // Get the product Info based on the service type
            PretupsBL.getProductFromServiceType(con, _c2sTransferVO, _serviceType, PretupsI.C2S_MODULE);
            // This flag shows that if a request passes this stage transaction
            // entries would be done in the database.
            _transferEntryReqd = true;

            _senderTransferItemVO.setTransferValue(_c2sTransferVO.getTransferValue());
        } catch (BTSLBaseException be) {
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            // transaction status set in iat item VO for reporting purpose.
            // (This vo finally updates the iat item table)
            if (_iatTransferItemVO != null) {
                _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            }
            if (be.isKey()) {
                _c2sTransferVO.setErrorCode(be.getMessageKey());
            } else {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            throw be;
        } catch (Exception e) {
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            // transaction status set in iat item VO for reporting purpose.
            // (This vo finally updates the iat item table)
            if (_iatTransferItemVO != null) {
                _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            }
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[processTransfer]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("C2SPrepaidController", "processTransfer", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
    }

    /**
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    private static synchronized void generateIATRoamRechargeTransferID(TransferVO p_transferVO) throws BTSLBaseException {
        final String METHOD_NAME = "generateIATRoamRechargeTransferID";
        String transferID = null;
        String minut2Compare = null;
        Date mydate = null;
        try {
            // mydate = p_transferVO.getCreatedOn();
            mydate = new Date();
            p_transferVO.setCreatedOn(mydate);
            minut2Compare = _sdfCompare.format(mydate);
            int currentMinut = Integer.parseInt(minut2Compare);

            if (currentMinut != _prevMinut) {
                _transactionIDCounter = 1;
                _prevMinut = currentMinut;
            } else if (_transactionIDCounter >= 9999) {
                _transactionIDCounter = 1;
            } else {
                _transactionIDCounter++;
            }
            if (_transactionIDCounter == 0) {
                throw new BTSLBaseException("IATIntlRechargeController", "generateIATRoamRechargeTransferID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = _operatorUtil.formatIntlRechargeTransferID(p_transferVO, _transactionIDCounter);
            if (transferID == null) {
                throw new BTSLBaseException("IATIntlRechargeController", "generateIATRoamRechargeTransferID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("C2SPrepaidController", "generateIATRoamRechargeTransferID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
        }
    }

    /**
     * Sets the sender transfer Items VO for the channel user
     * 
     */
    private void setSenderTransferItemVO() {
        if (_log.isDebugEnabled()) {
            _log.debug("setSenderTransferItemVO", "Entered");
        }
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
        if (_log.isDebugEnabled()) {
            _log.debug("setSenderTransferItemVO", "Exited");
        }
    }

    /**
     * Sets the receiever transfer Items VO for the subscriber
     * 
     */
    private void setReceiverTransferItemVO() {
        if (_log.isDebugEnabled()) {
            _log.debug("setReceiverTransferItemVO", "Entered");
        }
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
        if (_log.isDebugEnabled()) {
            _log.debug("setReceiverTransferItemVO", "Exited");
        }
    }

    /**
     * Check the transaction load
     * 
     * @throws BTSLBaseException
     */
    private void checkTransactionLoad() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("checkTransactionLoad", "Checking load for transfer ID=" + _transferID);
        }
        final String METHOD_NAME = "checkTransactionLoad";
        int recieverLoadStatus = 0;
        try {
            _c2sTransferVO.setRequestVO(_requestVO);
            _c2sTransferVO.setSenderTransferItemVO(_senderTransferItemVO);
            _c2sTransferVO.setReceiverTransferItemVO(_receiverTransferItemVO);

            recieverLoadStatus = LoadController.checkInterfaceLoad(_c2sTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _transferID, _c2sTransferVO, true);
            if (recieverLoadStatus == 0) {
                LoadController.checkTransactionLoad(((ReceiverVO) _c2sTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
                if (_log.isDebugEnabled()) {
                    _log.debug("IATIntlRechargeController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
                }
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                String strArr[] = { _receiverMSISDN, String.valueOf(PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())) };
                throw new BTSLBaseException("IATIntlRechargeController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("IATIntlRechargeController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            _log.error("IATIntlRechargeController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("IATIntlRechargeController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
            throw new BTSLBaseException("IATIntlRechargeController", "checkTransactionLoad", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder();
        	sb.append("Entered and performing validations for transfer ID=");
        	sb.append(_transferID);
        	sb.append(" ");
        	sb.append(_c2sTransferVO.getModule());
        	sb.append(" ");
        	sb.append(_c2sTransferVO.getReceiverNetworkCode());
        	sb.append(" ");
        	sb.append(_type);
            _log.debug("processValidationRequest", sb.toString());
        }
        final String METHOD_NAME = "processValidationRequest";
        try {
            IATInterfaceVO reqIATInterfaceVO = new IATInterfaceVO();
            setReceiverValidateParams(reqIATInterfaceVO);
            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
            IATInterfaceHandlerI handleObj = (IATInterfaceHandlerI) PretupsBL.getIATHandlerObj(_iatTransferItemVO.getIatHandlerClass());
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, reqIATInterfaceVO.toString(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            handleObj.validate(reqIATInterfaceVO);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, reqIATInterfaceVO.toString(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            if (_log.isDebugEnabled()) {
                _log.debug("processValidationRequest", _transferID, "Got the validation response from IAT Module " + reqIATInterfaceVO.toString());
            }

            _itemList = new ArrayList();
            _itemList.add(_senderTransferItemVO);
            _itemList.add(_receiverTransferItemVO);
            _itemList.add(_iatTransferItemVO);
            _c2sTransferVO.setTransferItemList(_itemList);

            try {
                updateForReceiverValidateResponse(reqIATInterfaceVO);
            } catch (BTSLBaseException be) {
                LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                // TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+_receiverVO.getInterfaceResponseCode());
                if (_log.isDebugEnabled()) {
                    _log.debug("processValidationRequest", "inside catch of BTSL Base Exception: " + be.getMessage());
                }
                throw be;
            } catch (Exception e) {
                LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                // TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,_receiverVO.getInterfaceResponseCode());
                throw new BTSLBaseException(this, METHOD_NAME, "Exception in validation of the receiver and perform the steps before.");
            }

            LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

            // Get the service Class ID based on the code
            PretupsBL.validateServiceClassChecks(con, _receiverTransferItemVO, _c2sTransferVO, PretupsI.C2S_MODULE, _requestVO.getServiceType());
            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());

            // validate sender receiver service class,validate transfer value
            PretupsBL.validateTransferRule(con, _c2sTransferVO, PretupsI.C2S_MODULE);

            // calculate card group details
            CardGroupBL.calculateCardGroupDetails(con, _c2sTransferVO, PretupsI.C2S_MODULE, true);

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "After Card Group Set Id=" + _c2sTransferVO.getCardGroupSetID() + " Code" + _c2sTransferVO.getCardGroupCode() + " Card ID=" + _c2sTransferVO.getCardGroupID() + " Access fee=" + _c2sTransferVO.getReceiverAccessFee() + " Tax1 =" + _c2sTransferVO.getReceiverTax1Value() + " Tax2=" + _c2sTransferVO.getReceiverTax1Value() + " Bonus=" + _c2sTransferVO.getReceiverBonusValue() + " Val Type=" + _c2sTransferVO.getReceiverValPeriodType() + " Validity=" + _c2sTransferVO.getReceiverValidity() + " Talk Time=" + _c2sTransferVO.getReceiverTransferValue(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            // Here the code for debiting the user account will come
            _userBalancesVO = ChannelUserBL.debitUserBalanceForProduct(con, _transferID, _c2sTransferVO);

            // Update Transfer Out Counts for the sender
            ChannelTransferBL.increaseC2STransferOutCounts(con, _c2sTransferVO, true);

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            // transaction status set in iat item VO for reporting purpose.
            // (This vo finally updates the iat item table)
            _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

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
                _log.errorTrace(METHOD_NAME, be);
            }// ///HANDLE ERROE AND GET NEW CONNECTION}

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Marked Under process", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");

            // Log the details if the transfer Details were added i.e. if User
            // was debitted
            if (_transferDetailAdded) {
                BalanceLogger.log(_userBalancesVO);
            }

            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue) {
                // create new Thread
                Thread _controllerThread = new Thread(this);
                _controllerThread.start();
                _oneLog = false;
            }
        } catch (BTSLBaseException be) {
            if (con != null) {
                con.rollback();
            }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            // transaction status set in iat item VO for reporting purpose.
            // (This vo finally updates the iat item table)
            _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }
            }
            _log.error("IATIntlRechargeController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());
            if (_transferDetailAdded) {
                _userBalancesVO = null;
                // Update the sender back for fail transaction
                updateSenderForFailedTransaction();

                // So that we can update with final status here
                // added by nilesh: consolidated for logger
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    addEntryInTransfers();
                }

                con.commit();
                // Log the details if the transfer Details were added i.e. if
                // User was creditted
                if (_creditBackEntryDone) {
                    BalanceLogger.log(_userBalancesVO);
                }
            }
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            if (con != null) {
                con.rollback();
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            // transaction status set in iat item VO for reporting purpose.
            // (This vo finally updates the iat item table)
            _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            if (_transferDetailAdded) {
                _userBalancesVO = null;
                // Update the sender back for fail transaction
                updateSenderForFailedTransaction();

                // So that we can update with final status here
                // added by nilesh: consolidated for logger
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    addEntryInTransfers();
                }

                con.commit();
                // Log the details if the transfer Details were added i.e. if
                // User was creditted
                if (_creditBackEntryDone) {
                    BalanceLogger.log(_userBalancesVO);
                }
            }
            throw new BTSLBaseException("IATIntlRechargeController", "processValidationRequest", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
        	_log.debug(METHOD_NAME, "inside finally block");
        }
    }

    private void setReceiverValidateParams(IATInterfaceVO p_iatInterfaceVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("setReceiverValidateParams", "Entered");
        }
        setReceiverCommonParams(p_iatInterfaceVO);
        p_iatInterfaceVO.setIatAction(PretupsI.INTERFACE_VALIDATE_ACTION);
        if (_log.isDebugEnabled()) {
            _log.debug("setReceiverValidateParams", "Exited");
        }
    }

    private void setReceiverCommonParams(IATInterfaceVO p_iatInterfaceVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("setReceiverCommonParams", "Entered");
        }
        p_iatInterfaceVO.setIatReceiverMSISDN(_receiverMSISDN);
        p_iatInterfaceVO.setIatSenderNWTRXID(_transferID);
        p_iatInterfaceVO.setIatSenderNWID(_c2sTransferVO.getNetworkCode());
        p_iatInterfaceVO.setIatInterfaceId(_iatTransferItemVO.getIatCode());
        p_iatInterfaceVO.setIatInterfaceHandlerClass(_iatTransferItemVO.getIatHandlerClass());
        p_iatInterfaceVO.setIatModule(PretupsI.C2S_MODULE);
        p_iatInterfaceVO.setIatCardGrpSelector(_requestVO.getReqSelector());
        p_iatInterfaceVO.setIatINAccessType(PretupsI.CONTROLLER);
        p_iatInterfaceVO.setIatRetailerMsisdn(_senderMSISDN);
        p_iatInterfaceVO.setIatUserType("R");
        p_iatInterfaceVO.setIatServiceType(_serviceType);
        p_iatInterfaceVO.setIatReceiverCountryShortName(_iatTransferItemVO.getIatRecCountryShortName());
        p_iatInterfaceVO.setIatType(_iatTransferItemVO.getIatType());
        p_iatInterfaceVO.setIatSourceType(_c2sTransferVO.getSourceType());
        p_iatInterfaceVO.setIatReceiverCountryCode(_iatTransferItemVO.getIatRcvrCountryCode());
        p_iatInterfaceVO.setIatSenderCountryCode(Integer.parseInt(_iatTransferItemVO.getSenderCountryCode()));
        p_iatInterfaceVO.setSenderId(_channelUserVO.getUserID());
        p_iatInterfaceVO.setIatRetailerID(_senderMSISDN);
        p_iatInterfaceVO.setIatRcvrNWID(_iatTransferItemVO.getIatRecNWCode());
        p_iatInterfaceVO.setIatNotifyMSISDN(_iatTransferItemVO.getIatNotifyMsisdn());
        if (_log.isDebugEnabled()) {
            _log.debug("setReceiverCommonParams", "Exited");
        }
    }

    /**
     * Method to process the response of the receiver validation from IN
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForReceiverValidateResponse(IATInterfaceVO p_iatInterfaceVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateForReceiverValidateResponse", "Entered");
        }
        String[] strArr = null;
        String status = p_iatInterfaceVO.getIatINTransactionStatus();
        if (null != p_iatInterfaceVO.getIatStartTime()) {
            _requestVO.setValidationReceiverRequestSent(((Long.valueOf(p_iatInterfaceVO.getIatStartTime()).longValue())));
        }
        if (null != p_iatInterfaceVO.getIatEndTime()) {
            _requestVO.setValidationReceiverResponseReceived(((Long.valueOf(p_iatInterfaceVO.getIatEndTime()).longValue())));
        }

        /*
         * //Start: Update the Interface table for the interface ID based on
         * Handler status and update the Cache
         * String interfaceStatusType=(String)map.get("INT_SET_STATUS");
         * if(!BTSLUtil.isNullString(interfaceStatusType) &&
         * (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) ||
         * InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType)))
         * new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES,
         * _receiverTransferItemVO
         * .getInterfaceID(),interfaceStatusType,PretupsErrorCodesI
         * .PROCESS_RESUMESUSPEND_INT_MSG
         * ,PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
         * //:End
         */

        if (!InterfaceErrorCodesI.SUCCESS.equals(status)) {
            _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            throw new BTSLBaseException("IATIntlRechargeController", "updateForReceiverValidateResponse", _c2sTransferVO.getErrorCode(), 0, strArr, null);
        }
        _receiverTransferItemVO.setValidationStatus(status);
        _receiverTransferItemVO.setTransferStatus(status);
        _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

        _receiverTransferItemVO.setServiceClassCode(PretupsI.ALL);
        _receiverVO.setServiceClassCode(PretupsI.ALL);

        _receiverVO.setInterfaceResponseCode(p_iatInterfaceVO.getIatResponseCodeVal());
        _receiverTransferItemVO.setPreviousExpiry(_currentDate);
        if (_log.isDebugEnabled()) {
            _log.debug("updateForReceiverValidateResponse", "Exited");
        }
    }

    /**
     * Method to update the channel user back in case of failed transaction
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    private void updateSenderForFailedTransaction() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateSenderForFailedTransaction", "Entered");
        }
        final String METHOD_NAME = "updateSenderForFailedTransaction";
        try {
            _userBalancesVO = ChannelUserBL.creditUserBalanceForProduct(con, _c2sTransferVO.getTransferID(), _c2sTransferVO);
            ChannelTransferBL.decreaseC2STransferOutCounts(con, _c2sTransferVO);
            _creditBackEntryDone = true;

            if (_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                _requestVO.setSuccessTxn(false);

                if (!PretupsI.IAT_TRANSACTION_TYPE.equals(_c2sTransferVO.getExtCreditIntfceType())) {
                    String[] messageArgArray = { _c2sTransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(_userBalancesVO.getBalance()) };
                    _requestVO.setMessageArguments(messageArgArray);
                    _requestVO.setMessageCode(PretupsErrorCodesI.C2S_SENDER_CREDIT_SUCCESS);
                } else {
                    String[] messageArgArray = { _c2sTransferVO.getIatTransferItemVO().getIatRcvrCountryCode() + _c2sTransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(_userBalancesVO.getBalance()) };
                    _requestVO.setMessageArguments(messageArgArray);
                    _requestVO.setMessageCode(PretupsErrorCodesI.IAT_C2S_SENDER_CREDIT_SUCCESS);
                }
            }
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Done", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (BTSLBaseException be) {
            _finalTransferStatusUpdate = false;
            _c2sTransferVO.setSenderReturnMessage(null);
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage() + " Getting Code=" + be.getMessageKey());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("updateSenderForFailedTransaction", "Exited");
        }
    }

    /**
     * Method that will add entry in Transfer Table if not added else update the
     * records
     * 
     * @param p_con
     */
    private void addEntryInTransfers() {
        if (_log.isDebugEnabled()) {
            _log.debug("addEntryInTransfers", "Entered");
        }
        final String METHOD_NAME = "addEntryInTransfers";
        try {
            // METHOD FOR INSERTING AND UPDATION IN C2S Transfer Table
            if (!_transferDetailAdded && _transferEntryReqd) {
                ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO);// add
                                                                              // transfer
                                                                              // details
                                                                              // in
                                                                              // database
            } else if (_transferDetailAdded) {
                _c2sTransferVO.setModifiedOn(new Date());
                _c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                // added by nilesh: consolidated for logger
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    ChannelTransferBL.updateC2STransferDetails(con, _c2sTransferVO);// add
                                                                                     // transfer
                                                                                     // details
                                                                                     // in
                                                                                     // database
                }
            }
            con.commit();
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            if (!_isCounterDecreased && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            _log.error("addEntryInTransfers", _transferID, "BTSLBaseException while adding transfer details in database:" + be.getMessage());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            if (!_isCounterDecreased && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            _log.error("addEntryInTransfers", _transferID, "Exception while adding transfer details in database:" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug("addEntryInTransfers", "Exited");
        }
    }

    /**
     * Thread to perform IN related operations
     */
    public void run() {
        if (_log.isDebugEnabled()) {
            _log.debug("run", _transferID, "Entered");
        }
        final String METHOD_NAME = "run";
        BTSLMessages btslMessages = null;
        _userBalancesVO = null;
        PushMessage pushMessagesNotify = null;

        try {
            if (mcomCon == null) {
                mcomCon = new MComConnection();con=mcomCon.getConnection();
            }
            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !_processedFromQueue) {
                // Processing validation request in Thread
                processValidationRequestInThread();
            }

            // send validation request for sender
            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_TOP);
            IATInterfaceVO reqIATInterfaceVO = new IATInterfaceVO();
            setReceiverCreditParams(reqIATInterfaceVO);

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP, reqIATInterfaceVO.toString(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            IATInterfaceHandlerI handleObj = (IATInterfaceHandlerI) PretupsBL.getIATHandlerObj(_iatTransferItemVO.getIatHandlerClass());

            handleObj.credit(reqIATInterfaceVO);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP, reqIATInterfaceVO.toString(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug("run", _transferID, "Got the response from IN Module receiverCreditResponse=" + reqIATInterfaceVO.toString());
            }
            // Getting Database connection
            try {
                // updating receiver credit response
                updateForReceiverCreditResponse(reqIATInterfaceVO);
                // decreasing response counters
                LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
            } catch (BTSLBaseException be) {
            	StringBuilder sb = new StringBuilder();
            	sb.append("Transfer Status=");
            	sb.append(_c2sTransferVO.getTransferStatus());
            	sb.append(" Getting Code=");
            	sb.append(_receiverVO.getInterfaceResponseCode());
                TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL,sb.toString());
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                }

                // Update the sender back for fail transaction
                // Check Status if Ambigous then credit back preference wise
                if (((_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS)).booleanValue())) || _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    updateSenderForFailedTransaction();
                }
                throw be;
            }// end catch BTSLBaseException
            catch (Exception e) {

                TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _c2sTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO.getInterfaceResponseCode());

                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);
                }

                // Update the sender back for fail transaction
                // Check Status if Ambigous then credit back preference wise
                if (((_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_SNDR_CREDIT_BK_AMB_STATUS)).booleanValue())) || _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    updateSenderForFailedTransaction();
                }
                throw new BTSLBaseException(this, METHOD_NAME, "Exception in performing IN related operations.");
            }// end of catch Exception

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            // transaction status set in iat item VO for reporting purpose.
            // (This vo finally updates the iat item table)
            _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _c2sTransferVO.setErrorCode(null);

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, _receiverVO.getNetworkCode());

            // TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Success",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+_c2sTransferVO.getTransferStatus());

            _c2sTransferVO.setSenderReturnMessage(null);

            // checking whether differential commission is applicable or not
            if (PretupsI.YES.equals(_c2sTransferVO.getDifferentialAllowedForService())) {
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("C2SPrepaidController", "process", PretupsErrorCodesI.IAT_COMMIT_ERR_EXCEPTION);
                }
                try {
                    new DiffCalBL().differentialCalculations(_c2sTransferVO, PretupsI.C2S_MODULE);
                } catch (BTSLBaseException be) {
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                        _log.debug("C2SPrepaidController", "For _transferID=" + _transferID + " Diff applicable=" + _c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO.getDifferentialGiven() + " Not able to give Diff commission getting BTSL Base Exception=" + be.getMessage() + " Leaving transaction status as Under process");
                    }
                    _log.errorTrace(METHOD_NAME, be);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPrepaidController[run]", _c2sTransferVO.getTransferID(), _c2sTransferVO.getSenderMsisdn(), _c2sTransferVO.getNetworkCode(), "Exception:" + be.getMessage());
                } catch (Exception e) {
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                        _log.debug("C2SPrepaidController", "For _transferID=" + _transferID + " Diff applicable=" + _c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO.getDifferentialGiven() + " Not able to give Diff commission getting Exception=" + e.getMessage() + " Leaving transaction status as Under process");
                    }
                    _log.errorTrace(METHOD_NAME, e);
                }
            }// end if

            // TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"After Differential Calculation",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+_c2sTransferVO.getTransferStatus()+" Differential Appl="+_c2sTransferVO.getDifferentialApplicable()+" Diff Given="+_c2sTransferVO.getDifferentialGiven());

            if (_log.isDebugEnabled()) {
                _log.debug("C2SPrepaidController", "For _transferID=" + _transferID + " Diff applicable=" + _c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO.getDifferentialGiven());
            }
        }// end try
        catch (BTSLBaseException be) {
            _requestVO.setSuccessTxn(false);
            // try{if(_con!=null) _con.rollback() ;}catch(Exception ex){}
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
                _log.debug("run", _transferID, "Error Code:" + _c2sTransferVO.getErrorCode());
            }

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
            _log.errorTrace(METHOD_NAME, be);
        }// end catch BTSLBaseException
        catch (Exception e) {
            // try{if(_con!=null) _con.rollback() ;}catch(Exception ex){}
            _requestVO.setSuccessTxn(false);
            _log.errorTrace(METHOD_NAME, e);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            _log.error("run", _transferID, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            btslMessages = new BTSLMessages(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());

        }// end catch Exception
        finally {
            try {
                // decreasing transaction load count
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);

                // Unmarking the receiver transaction status
                /*
                 * if(_receiverVO!=null && _receiverVO.isUnmarkRequestStatus())
                 * PretupsBL.unmarkReceiverLastRequest(_con,_transferID,_receiverVO
                 * );
                 */
            }// end try
            /*
             * catch(BTSLBaseException be)
             * {
             * //try{if(con!=null) con.rollback() ;}catch(Exception ex){}
             * be.printStackTrace();
             * _log.error("run",_transferID,
             * "BTSLBaseException while updating Receiver last request status in database:"
             * +be.getMessage());
             * }
             */
            catch (Exception e) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
                _log.errorTrace(METHOD_NAME, e);
                _log.error("run", _transferID, "Exception while updating Receiver last request status in database:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPrepaidController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception while updating Receiver last request status in database , Exception:" + e.getMessage());
            }// end catch

            try {
                if (_finalTransferStatusUpdate) {
                    // Setting modified on and by
                    _c2sTransferVO.setModifiedOn(new Date());
                    _c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    // Updating C2S Transfer details in database
                    // added by nilesh: consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        ChannelTransferBL.updateC2STransferDetails(con, _c2sTransferVO);
                    }
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                _log.error("run", _transferID, "BTSLBaseException while updating transfer details in database:" + be.getMessage());
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("run", _transferID, "Exception while updating transfer details in database:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SPrepaidController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
            }
            // if connection is not null then comitting the transaction and
            // closing the connection
            if (con != null) {
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
				if (mcomCon != null) {
					mcomCon.close("IATIntlRechargeController#run");
					mcomCon = null;
				}
                con = null;
            }
            // If transaction is fail and grouptype counters need to be decrease
            // then decrease the counters
            // This change has been done by ankit on date 14/07/06 for SMS
            // charging
            if (!_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && _requestVO.isDecreaseGroupTypeCounter() && ((ChannelUserVO) _requestVO.getSenderVO()).getUserControlGrouptypeCounters() != null) {
                PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO) _requestVO.getSenderVO()).getUserControlGrouptypeCounters());
            }

            if (!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedSendMessGatw)) {
                PushMessage pushMessages = null;
                if (btslMessages != null) {
                    // push final error message to sender
                    pushMessages = (new PushMessage(_senderPushMessageMsisdn, BTSLUtil.getMessage(_senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs()), _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale));
                } else {
                    // push Additional Commission success message to sender and
                    // final status to sender
                    if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
                        pushMessages = (new PushMessage(_senderPushMessageMsisdn, _c2sTransferVO.getSenderReturnMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale));
                    } else if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                        pushMessages = (new PushMessage(_senderPushMessageMsisdn, getSenderSuccessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale));
                    }
                }// end if
                 // If transaction is successfull then if group type counters
                 // reach limit then send message using gateway that is
                 // associated with group type profile
                 // This change has been done by ankit on date 14/07/06 for SMS
                 // charging
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)).indexOf(_requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE.equals(_requestVO.getGroupType())) {
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
                            SMSChargingLog.log(((ChannelUserVO) _requestVO.getSenderVO()).getUserID(), (((ChannelUserVO) _requestVO.getSenderVO()).getUserChargeGrouptypeCounters()).getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(), groupTypeProfileVO.getResGatewayType(), groupTypeProfileVO.getNetworkCode(), _requestVO.getGroupType(), _requestVO.getServiceType(), _requestVO.getModule());
                        } else {
                            pushMessages.push();
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                } else {
                    pushMessages.push();
                }
            }

            String[] messageArgArray = { _iatTransferItemVO.getIatRcvrCountryCode() + _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()) };
            if (_notifyMSISDN != null) {
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    pushMessagesNotify = (new PushMessage(_notifyMSISDN, BTSLUtil.getMessage(_iatNotifyMSISDNLocale, PretupsErrorCodesI.IAT_NOTIFY_SUCCESS_KEY, messageArgArray), _transferID, _c2sTransferVO.getRequestGatewayCode(), _iatNotifyMSISDNLocale));
                } else if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    pushMessagesNotify = (new PushMessage(_notifyMSISDN, BTSLUtil.getMessage(_iatNotifyMSISDNLocale, PretupsErrorCodesI.IAT_NOTIFY_FAIL_KEY, messageArgArray), _transferID, _c2sTransferVO.getRequestGatewayCode(), _iatNotifyMSISDNLocale));
                } else if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    pushMessagesNotify = (new PushMessage(_notifyMSISDN, BTSLUtil.getMessage(_iatNotifyMSISDNLocale, PretupsErrorCodesI.IAT_NOTIFY_AMB_KEY, messageArgArray), _transferID, _c2sTransferVO.getRequestGatewayCode(), _iatNotifyMSISDNLocale));
                }
                pushMessagesNotify.push();
            }
            // Log the credit back entry in the balance log
            if (_creditBackEntryDone) {
                BalanceLogger.log(_userBalancesVO);
            }

            // added by nilesh : consolidated for logger
            if (!_oneLog) {
                OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
            }
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS, "Trans Status=" + _c2sTransferVO.getTransferStatus() + " Error Code=" + _c2sTransferVO.getErrorCode() + " Diff Appl=" + _c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO.getDifferentialGiven() + " Message=" + _c2sTransferVO.getSenderReturnMessage());
            // Populate the ChannelRequestDailyLogVo and log
            ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));

            btslMessages = null;
            _userBalancesVO = null;

            if (_log.isDebugEnabled()) {
                _log.debug("run", _transferID, "Exiting");
            }
        }// end of finally
    }

    /**
     * Method that will perform the validation request in thread
     * 
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequestInThread() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("processValidationRequestInThread", "Entered and performing validations for transfer ID=" + _transferID);
        }
        final String METHOD_NAME = "processValidationRequestInThread";
        try {
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            processValidationRequest();
        } catch (BTSLBaseException be) {
            _log.error("C2SPrepaidController[processValidationRequestInThread]", "Getting BTSL Base Exception:" + be.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            // transaction status set in iat item VO for reporting purpose.
            // (This vo finally updates the iat item table)
            _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            _log.error(this, _transferID, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
            throw new BTSLBaseException(this, "processValidationRequestInThread", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            // !_transferDetailAdded Condition Added as we think its not require
            // as already done
            if (_transferID != null && !_transferDetailAdded) {
                try {
                    // added by nilesh: consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        addEntryInTransfers();
                    }
                    if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        _finalTransferStatusUpdate = false; // No need to update
                                                            // the status of
                                                            // transaction in
                                                            // run method
                    }
                }
                catch (Exception e) {
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (Exception ex) {
                            _log.errorTrace(METHOD_NAME, ex);
                        }
                    }
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error("process", "Exception:" + e.getMessage());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
                } finally {
                	_log.debug(METHOD_NAME, "inside finally block");
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
            }
        }
    }

    private void setReceiverCreditParams(IATInterfaceVO p_iatInterfaceVO) {
        setReceiverCommonParams(p_iatInterfaceVO);
        p_iatInterfaceVO.setIatAction(PretupsI.INTERFACE_CREDIT_ACTION);
        p_iatInterfaceVO.setIatGraceDays(_receiverTransferItemVO.getGraceDaysStr());
        p_iatInterfaceVO.setIatCardGroupCode(_c2sTransferVO.getCardGroupCode());
        p_iatInterfaceVO.setIatValidityDays(_c2sTransferVO.getReceiverValidity());
        p_iatInterfaceVO.setIatBonusValidityDays(_c2sTransferVO.getReceiverBonusValidity());
        p_iatInterfaceVO.setIatRequestedAmount(_c2sTransferVO.getRequestedAmount());
        p_iatInterfaceVO.setIatSendingNWTimestamp(_iatTransferItemVO.getSendingNWTimestamp());
        p_iatInterfaceVO.setIatInterfaceAmt(_c2sTransferVO.getReceiverTransferValue());
    }

    /**
     * Method to get the success message to be sent to sender
     * 
     * @return
     */
    private String getSenderUnderProcessMessage() {
        String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), String.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()) };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.IAT_C2S_SENDER_UNDERPROCESS, messageArgArray);
    }

    /**
     * Method to process the response of the receiver top up from IN
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForReceiverCreditResponse(IATInterfaceVO p_iatInterfaceVO) throws BTSLBaseException {
        String status = p_iatInterfaceVO.getIatINTransactionStatus();
        final String METHOD_NAME = "updateForReceiverCreditResponse";
        if (null != p_iatInterfaceVO.getIatStartTime()) {
            _requestVO.setTopUPReceiverRequestSent(((Long.valueOf(p_iatInterfaceVO.getIatStartTime()).longValue())));
        }
        if (null != p_iatInterfaceVO.getIatEndTime()) {
            _requestVO.setTopUPReceiverResponseReceived(((Long.valueOf(p_iatInterfaceVO.getIatEndTime()).longValue())));
        }

        /*
         * //Start: Update the Interface table for the interface ID based on
         * Handler status and update the Cache
         * String interfaceStatusType=(String)map.get("INT_SET_STATUS");
         * if(_log.isDebugEnabled())
         * _log.debug("updateForReceiverCreditResponse"
         * ,"Mape from response="+map
         * +" status="+status+" interface Status="+interfaceStatusType);
         * if(!BTSLUtil.isNullString(interfaceStatusType) &&
         * (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) ||
         * InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType)))
         * new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES,
         * _receiverTransferItemVO
         * .getInterfaceID(),interfaceStatusType,PretupsErrorCodesI
         * .PROCESS_RESUMESUSPEND_INT_MSG
         * ,PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
         * //:End
         */

        _receiverTransferItemVO.setProtocolStatus(p_iatInterfaceVO.getIatProtocolStatus());
        _receiverTransferItemVO.setInterfaceResponseCode(p_iatInterfaceVO.getIatResponseCodeChkStatus());
        _iatTransferItemVO.setIatTimestamp(p_iatInterfaceVO.getIatTimeStamp());
        _iatTransferItemVO.setIatTxnId(p_iatInterfaceVO.getIatTRXID());
        // _iatTransferItemVO.setIatSenderTxnId(p_iatInterfaceVO.getIatSenderNWTRXID());
        // _iatTransferItemVO.setIatRcvrCountryCode(p_iatInterfaceVO.getIatReceiverCountryCode());
        // _iatTransferItemVO.setIatRecNWCode(p_iatInterfaceVO.getIatRcvrNWID());
        // _iatTransferItemVO.setIatRecMsisdn(p_iatInterfaceVO.getIatReceiverMSISDN());
        // _iatTransferItemVO.setIatNotifyMsisdn(p_iatInterfaceVO.getIatNotifyMSISDN());
        _iatTransferItemVO.setIatExchangeRate(p_iatInterfaceVO.getIatExchangeRate());
        _iatTransferItemVO.setIatProvRatio(p_iatInterfaceVO.getIatProvRatio());
        _iatTransferItemVO.setIatReceiverSystemBonus(p_iatInterfaceVO.getIatReceiverZebraBonus());

        _iatTransferItemVO.setIatFees(p_iatInterfaceVO.getIatFees());
        _iatTransferItemVO.setIatCreditMessage(p_iatInterfaceVO.getIatResponseMsgCredit());
        _iatTransferItemVO.setIatCreditRespCode(p_iatInterfaceVO.getIatResponseCodeCredit());
        _iatTransferItemVO.setIatCheckStatusRespCode(p_iatInterfaceVO.getIatResponseCodeChkStatus());

        _iatTransferItemVO.setIatSentAmtByIAT(p_iatInterfaceVO.getIatSentAmtByIAT());
        _iatTransferItemVO.setIatRcvrRcvdAmt(p_iatInterfaceVO.getIatRcvrRcvdAmount());
        _iatTransferItemVO.setIatReceivedAmount(p_iatInterfaceVO.getIatReceivedAmount());

        if (!PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(status)) {
            _iatTransferItemVO.setIatErrorCode(p_iatInterfaceVO.getIatReasonCode());
            _iatTransferItemVO.setIatErrorMessage(p_iatInterfaceVO.getIatReasonMessage());
            _iatTransferItemVO.setIatFailedAt(p_iatInterfaceVO.getIatFailedAt());
            _iatTransferItemVO.setIatRcvrNWErrorCode(p_iatInterfaceVO.getReceiverNWReasonCode());
            _iatTransferItemVO.setIatRcvrNWErrorMessage(p_iatInterfaceVO.getReceiverNWReasonMessage());
        }

        String updateStatus = p_iatInterfaceVO.getIatUpdateStatus();

        if (BTSLUtil.isNullString(updateStatus)) {
            updateStatus = status;
        }
        _receiverTransferItemVO.setUpdateStatus(updateStatus);

        // set from IN Module
        if (!BTSLUtil.isNullString(p_iatInterfaceVO.getIatTRXID())) {
            try {
                _receiverTransferItemVO.setInterfaceReferenceID(p_iatInterfaceVO.getIatTRXID());
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        }
        _receiverTransferItemVO.setReferenceID(p_iatInterfaceVO.getReconId());

        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        String[] strArr = null;
        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            _c2sTransferVO.setErrorCode(status + "_R");
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(status);
            strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            // throw new
            // BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.C2S_RECEIVER_FAIL,0,strArr,null);
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", _c2sTransferVO.getErrorCode(), 0, strArr, null);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
            _c2sTransferVO.setErrorCode(status + "_R");
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            // transaction status set in iat item VO for reporting purpose.
            // (This vo finally updates the iat item table)
            _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _iatTransferItemVO.setTransferValue(_c2sTransferVO.getReceiverTransferValue());
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _receiverTransferItemVO.setUpdateStatus(status);
            strArr = new String[] { _transferID, _receiverTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        } else {
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _receiverTransferItemVO.setUpdateStatus(status);
            _iatTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _iatTransferItemVO.setTransferValue(_c2sTransferVO.getReceiverTransferValue());
        }
    }

    private String getSenderSuccessMessage() {
        String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), String.valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO.getSubService() };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.IAT_C2S_SENDER_SUCCESS, messageArgArray);
    }

    /**
     * Method to get the under process message before validation to be sent to
     * sender
     * 
     * @return
     */
    private String getSndrUPMsgBeforeValidation() {
        String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.IAT_C2S_SENDER_UNDERPROCESS_B4VAL, messageArgArray);
    }
}
