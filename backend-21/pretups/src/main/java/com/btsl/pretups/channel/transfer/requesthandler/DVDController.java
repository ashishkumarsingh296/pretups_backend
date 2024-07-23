/*
 * @(#)DVDController.java
 * Controller class for handling the Digital Voucher Distribution(DVD)
 */

package com.btsl.pretups.channel.transfer.requesthandler;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.ChannelRequestDailyLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.ResumeSuspendProcess;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberBL;
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.RoutingControlDAO;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingVO;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;


public class DVDController implements ServiceKeywordControllerI, Runnable {
    private static Log _log = LogFactory.getLog(DVDController.class.getName());
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
    private String _requestIDStr;
    private String _transferID;
    private ArrayList _itemList = null;
    private String _intModCommunicationTypeR;
    private String _intModIPR;
    private int _intModPortR;
    private String _intModClassNameR;
    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
    private boolean _transferDetailAdded = false;
    private boolean _isCounterDecreased = false;
    private String _type;
    private String _serviceType;
    private boolean _finalTransferStatusUpdate = true;
    private boolean _transferEntryReqd = false;
    private boolean _decreaseTransactionCounts = false;
    private boolean _receiverInterfaceInfoInDBFound = false;
    private String _receiverAllServiceClassID = PretupsI.ALL;
    private String _receiverPostBalanceAvailable;
    private Locale _senderLocale = null;
    private Locale _receiverLocale = null;
    private String _externalID = null;
    private RequestVO _requestVO = null;
    private boolean _processedFromQueue = false;
    private boolean _recValidationFailMessageRequired = false;
    private final String _notAllowedSendMessGatw;
    private String _receiverSubscriberType = null;
    private static OperatorUtilI _operatorUtil = null;
    private VomsVoucherVO _vomsVO = null;
    private String _interfaceStatusType = null;
    private static int _transactionIDCounter = 0;
    private String _payableAmt = null;
    private static int _prevMinut = 0;
    private boolean _onlyForEvr = false;
    private boolean _receiverMessageSendReq=true;
    private final String _notAllowedRecSendMessGatw;
    private boolean _oneLog = true;
    private String extraPrefixOtherInfo;
    private String _sid=null;
    private String _receiverBundleID = null;
    private boolean _subValRequired=true;
    private List<String> transferIdList = null;
    private String transferListString=null;
    private String lastTransferId = null;
    private List<VomsVoucherVO> vomsVoucherList = null;
    private int quantityRequested = 0;
    private String serialNoAsString = null;
    private String vomsPinAsString = null;
    private String saleBatchNumber = null;
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /*
     * In the constructor of DVDController initialize the date variable
     * _currentDate with current date. The
     * variables EVD_REC_GEN_FAIL_MSG_REQD_V & EVD_REC_GEN_FAIL_MSG_REQD_T
     * decides whether the validation and
     * top up failed message send to receiver or not.
     */

    public DVDController() {
        _c2sTransferVO = new C2STransferVO();
        _currentDate = new Date();
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("EVD_REC_GEN_FAIL_MSG_REQD_V")))) {
            _recValidationFailMessageRequired = true;
        }
        _notAllowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("EVD_SEN_MSG_NOT_REQD_GW"));
        _notAllowedRecSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("EVD_REC_MSG_NOT_REQD_GW"));
		extraPrefixOtherInfo=BTSLUtil.NullToString(Constants.getProperty("EVD_EXTRA_PREFIX_OTHER_INFO"));
    }

    /**
     * Method to process the request of the Digital Voucher Distribution 
     * 
     * @param p_requestVO
     *            RequestVO
     * @return void
     */

    @Override
    public void process(RequestVO p_requestVO) {
        Connection con = null;
        MComConnectionI mcomCon = null;
        final String methodName = "process";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                p_requestVO.getRequestIDStr(),
                "Entered for Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " _recValidationFailMessageRequired: " + _recValidationFailMessageRequired  + " ");
        }
        try {
            _requestVO = p_requestVO;
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            TransactionLog.log("", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_REQ,
                PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "Received Request From Receiver", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            _senderLocale = p_requestVO.getSenderLocale();
            _senderNetworkCode = _channelUserVO.getNetworkID();
            populateVOFromRequest(p_requestVO);
            _requestIDStr = p_requestVO.getRequestIDStr();
            _type = p_requestVO.getType();
            _serviceType = p_requestVO.getServiceType();

            // Checking senders out transfer status, it should not be suspended
            if (PretupsI.YES.equalsIgnoreCase(_channelUserVO.getOutSuspened())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND_DVD);
            }

            // Checking senders transfer profile status, it should not be suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getTransferProfileStatus())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND_DVD);
            }

            // Checking senders commission profile status, it should not be suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getCommissionProfileStatus())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_DVD);
            }

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            
            _c2sTransferVO.setTxnBatchId(p_requestVO.getTxnBatchId()); //added for multiple-DVD-process
            _c2sTransferVO.setCellId(p_requestVO.getCellId());
            _c2sTransferVO.setSwitchId(p_requestVO.getSwitchId());
            // Validating user message incoming in the request
            quantityRequested= _operatorUtil.validateDVDRequestFormat(con, _c2sTransferVO, p_requestVO);

        	if(!BTSLUtil.isNullString(_requestVO.getSid())){
				_c2sTransferVO.setSID(_requestVO.getSid());
			}
			if(_log.isDebugEnabled()) {
				_log.debug(methodName, "_c2sTransferVO.getSID()=" + _c2sTransferVO.getSID());
			}
			
			//validate voucher combination
			PretupsBL.validateVoucher(con, _c2sTransferVO);
            
			_receiverLocale = p_requestVO.getReceiverLocale();
            _senderLocale = p_requestVO.getSenderLocale();
            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
            if(_log.isDebugEnabled()) 
    		{_log.debug("process","Prefixes :: Receiver prefix : ",PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()) +"  Sender prefix : "+PretupsBL.getMSISDNPrefix(_channelUserVO.getMsisdn()) );}
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SUBSCRIBER_PREFIX_ROUTING_ALLOWED))).booleanValue()){		
				if(!_operatorUtil.isSubscriberPrefixMappingExist(con, _receiverVO.getMsisdn(), _channelUserVO.getMsisdn(), PretupsI.SERVICE_TYPE_PRE))	
					throw new BTSLBaseException(this,"process",PretupsErrorCodesI.RECHARGE_ERROR_DIFFERENT_NETWORK);
			}
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));
            _c2sTransferVO.setSelectorCode(p_requestVO.getReqSelector());
            final ServiceSelectorMappingVO serviceSelectorMappingVO = (ServiceSelectorMappingVO) ServiceSelectorMappingCache.getServiceSelectorMap().get(
					p_requestVO.getServiceType() + "_" + p_requestVO.getReqSelector());
			if (serviceSelectorMappingVO != null) {
				_receiverBundleID = serviceSelectorMappingVO.getReceiverBundleID();
				_c2sTransferVO.setReceiverBundleID(_receiverBundleID);
			}
			else{
				throw new BTSLBaseException("", methodName, PretupsErrorCodesI.ERROR_INVALID_SELECTOR_VALUE,0, null,null);
			}

            if (!_receiverVO.getSubscriberType().equals(_type)) {
                // Refuse the Request
                _log.error(this, "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "DVDController[process]", "", "", "",
                    "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But request initiated for the same");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { _receiverVO.getMsisdn() }, null);
            }
            _receiverVO.setModule(_c2sTransferVO.getModule());
            _receiverVO.setCreatedDate(_currentDate);
            _receiverVO.setLastTransferOn(_currentDate);
            _senderMSISDN = (_channelUserVO.getUserPhoneVO()).getMsisdn();
            _receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
            _c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
            _c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
			_c2sTransferVO.setSenderNetworkCode(_channelUserVO.getNetworkID());
            _c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
            _c2sTransferVO.setSubService(p_requestVO.getReqSelector());
            _c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
            _receiverSubscriberType = _receiverVO.getSubscriberType();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_senderMSISDN));
            _c2sTransferVO.setInfo6(p_requestVO.getInfo6());
            _c2sTransferVO.setInfo7(p_requestVO.getInfo7());
            _c2sTransferVO.setInfo8(p_requestVO.getInfo8());
            _c2sTransferVO.setInfo9(p_requestVO.getInfo9());
            _c2sTransferVO.setInfo10(p_requestVO.getInfo10());
            // checking whether self voucher distribution is allowed  //
            if (_senderMSISDN.equals(_receiverMSISDN)) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SELF_VOUCHER_DIST_NOTALLOWED);
            }

             RestrictedSubscriberBL.isRestrictedMsisdnExistForC2S(con, _c2sTransferVO, _channelUserVO, _receiverVO.getMsisdn(), _c2sTransferVO.getRequestedAmount()* quantityRequested);

            // Validates the network service status
            PretupsBL.validateNetworkService(_c2sTransferVO);
            _receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW, _receiverVO.getNetworkCode(), _serviceType))
                .booleanValue();
            // check if receiver barred in PreTUPS or not, user should not be
            // barred.
            isReceiverBarred(con); 

            try {
				if(p_requestVO.getRequestGatewayCode().equals(PretupsI.GATEWAY_TYPE_EXTGW))
				PretupsBL.checkMSISDNBarred(con, _senderMSISDN  , _receiverVO.getNetworkCode(), _c2sTransferVO.getModule(), PretupsI.USER_TYPE_SENDER);
				PretupsBL.checkMSISDNBarred(con, _receiverMSISDN, _receiverVO.getNetworkCode(), _c2sTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
			} catch (BTSLBaseException be) {
				if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
				}
				throw be;
			}
            _c2sTransferVO.setUnderProcessCheckReqd(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
            PretupsBL.loadRecieverControlLimits(con, p_requestVO.getRequestIDStr(), _c2sTransferVO);
            _receiverVO.setUnmarkRequestStatus(true);

            // commiting transaction after updating receiver's control parameters
            try {
            	mcomCon.partialCommit();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_DVD_FAIL);
            }

            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED)).booleanValue()) {
                processSKeyGen(con);
            } else {
                processTransfer(con);
                p_requestVO.setTransactionID(_transferID);
                _receiverVO.setLastTransferID(lastTransferId);

                TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_INT,
                    PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Generated Transfer ID", PretupsI.TXN_LOG_STATUS_SUCCESS,
                    "Source Type=" + _c2sTransferVO.getSourceType() + " Gateway Code=" + _c2sTransferVO.getRequestGatewayCode());

                // Populate VOMS and IN interface details
                populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
                _c2sTransferVO.setReceiverSubscriberType(_receiverSubscriberType);

                // validate receiver limits before Interface Validations
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                	PretupsBL.validateRecieverLimits(_c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE, quantityRequested);
                }

                // Validate Sender Transaction profile checks 
                ChannelUserBL.validateSenderTransferProfile(con, _transferID, _c2sTransferVO);
                _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_DVD_FAIL);
                }
               if (mcomCon != null)
					mcomCon.close("DVDController#process");
				mcomCon = null;
				con = null;

                // Checking the Various loads and setting flag to decrease the transaction count
                checkTransactionLoad();
                _decreaseTransactionCounts = true;

                (_channelUserVO.getUserPhoneVO()).setLastTransferID(lastTransferId);
                (_channelUserVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);

                // Checking the flow type of the transfer request, whether it is  common or thread
                if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
                    processValidationRequest();
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    p_requestVO.setSenderMessageRequired(_c2sTransferVO.isUnderProcessMsgReq());
                    p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());
                    p_requestVO.setDecreaseLoadCounters(false);
                }
                else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    p_requestVO.setSenderReturnMessage(getSndrUPMsgBeforeValidation());
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    final Thread _controllerThread = new Thread(this);
                    _controllerThread.start();
                    _oneLog = false;
                    p_requestVO.setDecreaseLoadCounters(false);
                } else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    processValidationRequest();
                    run();
                  
                    if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))
	                {
                    	p_requestVO.setMessageCode(PretupsErrorCodesI.DVD_SUCCESS);
                    	final String[] messageArgArray = { saleBatchNumber, _receiverMSISDN,serialNoAsString,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_c2sTransferVO.getVoucherQuantity() };
					    p_requestVO.setMessageArguments(messageArgArray);
					 }
                }
                p_requestVO.setDecreaseLoadCounters(false);
                p_requestVO.setTxnBatchId(saleBatchNumber);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {  
                    if (mcomCon == null) {
                    	mcomCon = new MComConnection();
                    	con=mcomCon.getConnection();
                    }
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DVDController[process]",  _transferID + "-" + lastTransferId,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_DVD_FAIL);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DVDController[process]",  _transferID + "-" + lastTransferId,
                        _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + e.getMessage());
            }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.DVD_RECEIVER_FAIL, new String[] { _transferID, String.valueOf(quantityRequested),PretupsBL
                				.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* quantityRequested),lastTransferId }));
                    } else {
                		_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_DVD, new String[] { String.valueOf(quantityRequested), PretupsBL
                				.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* quantityRequested) }));
                	
                    }
                }
            }
            if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
                p_requestVO.setSenderReturnMessage(_c2sTransferVO.getSenderReturnMessage());
            }
            if (be.isKey()) 
            {
                if (_c2sTransferVO.getErrorCode() == null) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                }

                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_DVD_FAIL);
            }

            // checking whether need to decrease the transaction load, if it is
            // already increased
            if (_transferID != null && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
			ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    if (mcomCon == null) {
                    	mcomCon = new MComConnection();
                    	con=mcomCon.getConnection();
                    }
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DVDController[process]", _transferID + "-" + lastTransferId,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_DVD_FAIL);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DVDController[process]", _transferID + "-" + lastTransferId,
                        _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + ex.getMessage());
            }
            // checking condition whether channel receiver required the general failure message
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.DVD_RECEIVER_FAIL, new String[] { _transferID, String.valueOf(quantityRequested),PretupsBL
                				.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* quantityRequested),lastTransferId }));
                    } else {
                		_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_DVD, new String[] { String.valueOf(quantityRequested), PretupsBL
                				.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* quantityRequested) }));
                	
                    }
                }
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_DVD_FAIL);
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_DVD_FAIL);
            _log.errorTrace(methodName, e);
            

            if (_transferID != null && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController[process]", _transferID, _senderMSISDN,
                _senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
			ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
        }
        finally {
            try {
                if (mcomCon == null) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                }
                if(_transferID!=null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) ||p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS))))
		        {
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        addEntryInTransfers(con);
                    }
                } else if (_transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    _log.info(methodName, p_requestVO.getRequestIDStr(),
                    		"Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + _transferID + " But not added entry in Transfers yet");
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController[process]", _transferID + "-" + lastTransferId,
                    _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            }
            if (con != null) {
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                if (mcomCon != null)
					mcomCon.close("DVDController#process");
				mcomCon = null;
				con = null;
            }
            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (_isCounterDecreased) {
                p_requestVO.setDecreaseLoadCounters(false);
            }
            if (_receiverMessageSendReq && _recValidationFailMessageRequired && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), 
            		_notAllowedRecSendMessGatw) && !"ALL".equals(_notAllowedRecSendMessGatw)) {
                if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
					if(_receiverLocale == null){
						_receiverLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
					}
                    (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                        _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                } else if (_c2sTransferVO.getReceiverReturnMsg() != null) {
					if(_receiverLocale == null){
						_receiverLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
					}
                    (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                        .push();
                }
            }
            if (_oneLog) {
                OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
            }
            TransactionLog.log(_transferID + "-" + lastTransferId, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
			ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
            if (!(p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) && (((p_requestVO.getMessageGatewayVO().getGatewayType().equalsIgnoreCase(PretupsI.GATEWAY_TYPE_SMSC)) || (p_requestVO.getMessageGatewayVO().getGatewayType()
                    .equalsIgnoreCase(PretupsI.GATEWAY_TYPE_USSD)) || (p_requestVO.getMessageGatewayVO().getGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_STK))) && (_finalTransferStatusUpdate))) {
                    //sendMVDThroughSMS(_requestVO, _quantityRequested);
                }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
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
        _c2sTransferVO.setReferenceID(p_requestVO.getExternalReferenceNum());
        _c2sTransferVO.setActiveUserId(_channelUserVO.getActiveUserID());
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
        	ArrayList<ChannelSoSVO>  chnlSoSVOList = new ArrayList<ChannelSoSVO> ();
        	chnlSoSVOList.add(new ChannelSoSVO(_channelUserVO.getUserID(),_channelUserVO.getMsisdn(),_channelUserVO.getSosAllowed(),_channelUserVO.getSosAllowedAmount(),_channelUserVO.getSosThresholdLimit()));
        	_c2sTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }
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
     * Method that will add entry in Transfer Table if not added else update the
     * records
     * 
     * @param Connection
     *            p_con
     */

    private void addEntryInTransfers(Connection p_con) {
        final String methodName = "addEntryInTransfers";
        StringBuilder loggerValue= new StringBuilder(); 
        try {
            // METHOD FOR INSERTING AND UPDATION IN C2S Transfer Table
            if (!_transferDetailAdded && _transferEntryReqd) {
            	 checkvomsVoucherList(p_con);
               // ChannelTransferBL.addC2STransferDetails(p_con, _c2sTransferVO);// add
                // transfer
                // details
                // in
                // database
            } else if (_transferDetailAdded) {
                _c2sTransferVO.setModifiedOn(new Date());
                _c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                // added by nilesh: consolidated for logger
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    ChannelTransferBL.updateC2STransferDetails(p_con, _c2sTransferVO, transferIdList);// add
                    // transfer
                    // details
                    // in
                    // database
                }
            }
            p_con.commit();
            _transferDetailAdded = true;
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, _transferID, "BTSLBaseException while adding transfer details in database:" + be.getMessage());
            _log.errorTrace(methodName, be);
	        loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController["+methodName+"]",
                    _transferID + "-" + lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
            checkCounter();
        } catch (Exception e) {
        	_log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController["+methodName+"]",
                _transferID + "-" + lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            checkCounter();
        }
    }

	private void checkCounter() {
		if (!_isCounterDecreased && _decreaseTransactionCounts) {
		    LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
		    _isCounterDecreased = true;
		}
	}

    /**
     * Method to process the request if SKEY is required for this transaction
     * 
     * @param p_con
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processSKeyGen(Connection p_con) throws BTSLBaseException {
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController[processSKeyGen]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }// end of processSKeyGen

    /**
     * Method processTransfer
     * This method is used to generate the transfer id & get the product Info
     * based on the service type.
     * 
     * @param Connection
     *            p_con
     * @return void
     * @throws BTSLBaseException
     */

    public void processTransfer(Connection p_con) throws BTSLBaseException {
        final String methodName = "processTransfer";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        try {
            // Generating the DVD transfer ID
            _c2sTransferVO.setTransferDate(_currentDate);
            _c2sTransferVO.setTransferDateTime(_currentDate);
            // Transaction id would be generated in the memory.
            //generateDVDTransferID(_c2sTransferVO);                                           //here add condition to generate single id
            transferIdList = generateDVDTransferID(_c2sTransferVO, quantityRequested);
            transferListString = transferIdList.stream().collect(Collectors.joining(","));
            _transferID = _c2sTransferVO.getTransferID();
            _requestVO.setValueObject(transferIdList);
            lastTransferId = (String) transferIdList.get(quantityRequested - 1);
            _c2sTransferVO.setLastTransferId(lastTransferId);
            _receiverVO.setLastTransferID(_transferID);

            setSenderTransferItemVO();
            setReceiverTransferItemVO();
            _c2sTransferVO.setReceiverTransferItemVO(_receiverTransferItemVO);
            PretupsBL.getProductFromServiceType(p_con, _c2sTransferVO, _serviceType, PretupsI.C2S_MODULE);
            _transferEntryReqd = true;

            if ((_channelUserVO.getCategoryVO()).getDomainTypeCode().equals(PretupsI.DOMAIN_TYPE_SALECENTER)) {
                _senderTransferItemVO.setTransferValue(_c2sTransferVO.getRequestedAmount() * quantityRequested);
            } else {
                _senderTransferItemVO.setTransferValue(_c2sTransferVO.getTransferValue() * quantityRequested);
            }

        } catch (BTSLBaseException be) {
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            throw be;
        } catch (Exception e) {
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                if (_transferID != null) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.DVD_RECEIVER_FAIL, new String[] { _transferID, String.valueOf(quantityRequested),PretupsBL
            				.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* quantityRequested),lastTransferId }));
                } else {
            		_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_DVD, new String[] { String.valueOf(quantityRequested), PretupsBL
            				.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* quantityRequested) }));
            	
                }
            }
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController[processTransfer]", _transferID + "-" + lastTransferId,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_DVD_FAIL);
        }
    }

    /**
     * Thread to perform IN related operations
     */
    @Override
    public void run() {
        final String methodName = "run";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, _transferID, "Entered");
        }
        BTSLMessages btslMessages = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !_processedFromQueue) {
                // Processing validation request in Thread
                processValidationRequestInThread();
            }
            //sendSMS();
            _receiverPostBalanceAvailable = "N";
        }
        catch (BTSLBaseException be) {
        	_requestVO.setSuccessTxn(false);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_DVD_FAIL);
                }
            }
            if (be.isKey() && _c2sTransferVO.getSenderReturnMessage() == null) {
                btslMessages = be.getBtslMessages();
            } else if (_c2sTransferVO.getSenderReturnMessage() == null) {
                _c2sTransferVO.setSenderReturnMessage(PretupsErrorCodesI.ERROR_DVD_FAIL);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, _transferID, "Error Code:" + _c2sTransferVO.getErrorCode());
            }

            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
            _log.errorTrace(methodName, be);
        }
        catch (Exception e) {
        	_requestVO.setSuccessTxn(false);
            _log.errorTrace(methodName, e);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_DVD_FAIL);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController[run]", _transferID, _senderMSISDN,
                _senderNetworkCode, "Exception:" + e.getMessage());
            btslMessages = new BTSLMessages(PretupsErrorCodesI.ERROR_DVD_FAIL);
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());

        }
        finally {
            try {
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO
                    .getReceiverReturnMsg()).isKey())) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_DVD), new String[] { _transferID, _c2sTransferVO.getVoucherQuantity() }));
                }
            	LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            	if (mcomCon == null) {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
				}
            	if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    try{
                    	PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
                    }catch (BTSLBaseException be) {
                        _log.errorTrace(methodName, be);
                    } catch (Exception e) {
                        _log.errorTrace(methodName, e);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DVDController[run]", _transferID, _senderMSISDN,
                            _senderNetworkCode, "Exception while updating Receiver last request status in database , Exception:" + e.getMessage());
                    }
                }
            if (_finalTransferStatusUpdate) {
				_c2sTransferVO.setModifiedOn(new Date());
				_c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
				if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
					try {
	                    ChannelTransferBL.updateC2STransferDetails(con, _c2sTransferVO, transferIdList);

					} catch (BTSLBaseException be) {
						_log.errorTrace(methodName, be);
					} catch (Exception e) {
						_log.errorTrace(methodName, e);
		                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DVDController[run]", _transferID, _senderMSISDN,
		                        _senderNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
					}
				}
			}
    		mcomCon.finalCommit();
			} catch (BTSLBaseException be) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, be);
			} catch (Exception e) {
				try {
					mcomCon.finalRollback();
				} catch (SQLException sqle) {
					_log.errorTrace(methodName, sqle);
				}
				_log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DVDController[run]", _transferID, _senderMSISDN,
                        _senderNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
			
				}
            finally{
				if(mcomCon != null )mcomCon.close("DVDController#run");
				mcomCon = null;
				con = null;
			}
            
            if (!_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && _requestVO.isDecreaseGroupTypeCounter() && ((ChannelUserVO) _requestVO
                    .getSenderVO()).getUserControlGrouptypeCounters() != null) {
                    PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO) _requestVO.getSenderVO()).getUserControlGrouptypeCounters());
                }
            
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue() ) {
                _receiverMessageSendReq = false;
            }
            if (_receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
                .equals(_notAllowedRecSendMessGatw)) {
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    if (_receiverMSISDN.equals(_c2sTransferVO.getPinSentToMsisdn())) {
                        if (_c2sTransferVO.getReceiverReturnMsg() == null) {
                        	if(!PretupsI.NO.equalsIgnoreCase(_requestVO.getSendSms())) {
                        		(new PushMessage(_receiverMSISDN, getReceiverSuccessMessage(), saleBatchNumber, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();                        		
                        	}
                        } else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                            final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                            (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                                _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                        } else {
                            (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
                                _receiverLocale)).push();
                        }
                    }
                }else if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
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
                else if (_c2sTransferVO.getReceiverReturnMsg() == null) {
                    (new PushMessage(_receiverMSISDN, getReceiverAmbigousMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                } else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                    (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                        _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                } else {
                    (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                        .push();
                }
            
            }

            // Message to sender will be send only when request gateway code is
            // allowed to send message
            if (!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedSendMessGatw)) {
                PushMessage pushMessages = null;
                if (btslMessages != null) {
                    pushMessages = (new PushMessage(_senderMSISDN, BTSLUtil.getMessage(_senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs()), _transferID,
                        _c2sTransferVO.getRequestGatewayCode(), _senderLocale));
                }
				else
				{
					//push Additional Commission success message to sender and final status to sender
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ADD_COMM_SEPARATE_MSG))).booleanValue()) {
						if(!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
							(new PushMessage(_senderMSISDN,_c2sTransferVO.getSenderReturnMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_senderLocale)).push();
						}
						if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
							pushMessages=(new PushMessage(_senderMSISDN,getSenderSuccessMessage(),_transferID,_c2sTransferVO.getRequestGatewayCode(),_senderLocale));
						} else if(_c2sTransferVO.getErrorCode()!=null) {
							pushMessages=new PushMessage(_senderMSISDN,BTSLUtil.getMessage(_senderLocale,_c2sTransferVO.getErrorCode(),null),_transferID,_c2sTransferVO.getRequestGatewayCode(),_senderLocale);
						}
					}
				 else {
                    if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
                        pushMessages = (new PushMessage(_senderMSISDN, _c2sTransferVO.getSenderReturnMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
                            _senderLocale));
                    } else if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                        pushMessages = new PushMessage(_senderMSISDN, getSenderSuccessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _senderLocale);
                    } else if (_c2sTransferVO.getErrorCode() != null) {
                        pushMessages = (new PushMessage(_senderMSISDN, BTSLUtil.getMessage(_senderLocale, _c2sTransferVO.getErrorCode(), null), _transferID, _c2sTransferVO
                            .getRequestGatewayCode(), _senderLocale));
                    }
                }
				}
                if (!_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) || _receiverMSISDN
                        .equals(_c2sTransferVO.getPinSentToMsisdn())) {
                	pushMessages.push();
                }
            }
            if (!_oneLog) {
                OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
            }
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS, "Message=" + _c2sTransferVO.getSenderReturnMessage());
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue() && _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                try {
                    if (!BTSLUtil.isNullString(_channelUserVO.getLmsProfile())) {
                        // String profileType= null;
                        final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                        final LoyaltyVO loyaltyVO = new LoyaltyVO();
                        // LoyaltyDAO loyaltyDAO = new LoyaltyDAO();
                        loyaltyVO.setServiceType(_c2sTransferVO.getServiceType());
                        loyaltyVO.setModuleType(PretupsI.C2S_MODULE);
                        loyaltyVO.setTransferamt(_c2sTransferVO.getTransferValue());
                        loyaltyVO.setCategory(_c2sTransferVO.getCategoryCode());
                        loyaltyVO.setUserid(_c2sTransferVO.getActiveUserId());
                        loyaltyVO.setNetworkCode(_c2sTransferVO.getNetworkCode());
                        loyaltyVO.setSenderMsisdn(_c2sTransferVO.getSenderMsisdn());
                        loyaltyVO.setTxnId(_c2sTransferVO.getTransferID());
                        loyaltyVO.setCreatedOn(_c2sTransferVO.getCreatedOn());
                        loyaltyVO.setProductCode(_c2sTransferVO.getProductCode());
                        loyaltyVO.setSetId(_channelUserVO.getLmsProfile());
                        _loyaltyBL.distributeLoyaltyPoints(PretupsI.C2S_MODULE, _c2sTransferVO.getTransferID(), loyaltyVO);
                    } else {
                        _log.error("process", "Exception during LMS Module.SetId not found");
                    }
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);

                }
            } 
            btslMessages = null;
            ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, _transferID, "Exiting");
            }
        }
    }

    /**
     * Method that will perform the validation request in thread
     * 
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequestInThread() throws BTSLBaseException {
        final String methodName = "processValidationRequestInThread";
        StringBuilder loggerValue= new StringBuilder(); 
		        loggerValue.setLength(0);
            	loggerValue.append("Entered and performing validations for transfer ID=");
            	loggerValue.append(_transferID);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,  loggerValue);
        }
        try {
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            processValidationRequest();
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("Getting BTSL Base Exception:" );
        	loggerValue.append(be.getMessage());
            _log.error("DVDController[processValidationRequestInThread]", loggerValue );
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { _transferID, PretupsBL
                    .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
            }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController[processValidationRequestInThread]",
                _transferID, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_transferID != null && !_transferDetailAdded) {
                Connection con = null;
                MComConnectionI mcomCon = null;
                try {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        addEntryInTransfers(con);
                    }
                    if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        _finalTransferStatusUpdate = false; 
                    }
                } catch (BTSLBaseException be) {
                    if (con != null) {
                        try {
                        	mcomCon.finalRollback();
                        } catch (Exception ex) {
                            _log.errorTrace(methodName, ex);
                        }
                    }
                    if (mcomCon != null)
						mcomCon.close("DVDController#process");
					mcomCon = null;
					con = null;
                    _log.errorTrace(methodName, be);
                } catch (Exception e) {
                    if (con != null) {
                        try {
                        	mcomCon.finalRollback();
                        } catch (Exception ex) {
                            _log.errorTrace(methodName, ex);
                        }
                    }
                    if (mcomCon != null)
						mcomCon.close("DVDController#process");
					mcomCon = null;
					con = null;
                    _log.errorTrace(methodName, e);
                    loggerValue.setLength(0);
                	loggerValue.append("Exception:");
                	loggerValue.append(e.getMessage());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "DVDController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
                } finally {
                    if (mcomCon != null)
						mcomCon.close("DVDController#process");
					mcomCon = null;
					con = null;
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
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
        _senderTransferItemVO.setRequestValue(_c2sTransferVO.getRequestedAmount()*quantityRequested);
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
        _senderTransferItemVO.setLanguage(_senderLocale.getLanguage());
        _senderTransferItemVO.setCountry(_senderLocale.getCountry());
    }

    /**
     * Sets the receiever transfer Items VO for the subscriber
     * 
     */
    private void setReceiverTransferItemVO() {
        _receiverTransferItemVO = new C2STransferItemVO();
        _receiverTransferItemVO.setSNo(2);
        _receiverTransferItemVO.setMsisdn(_receiverMSISDN);
        _receiverTransferItemVO.setRequestValue(_c2sTransferVO.getRequestedAmount()*quantityRequested);
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
        _receiverTransferItemVO.setLanguage(_receiverLocale.getLanguage());
        _receiverTransferItemVO.setCountry(_receiverLocale.getCountry());

    }

    /**
     * Method getInterfaceRoutingDetails
     * This method is used to get the interface details based on the parameters
     * @param p_con
     * @param p_msisdn
     * @param p_prefixID
     * @param p_subscriberType
     * @param p_networkCode
     * @param p_serviceType
     * @param p_interfaceCategory
     * @param p_userType
     * @param p_action
     * @return
     * @throws BTSLBaseException
     */
    private boolean getInterfaceRoutingDetails(Connection p_con, String p_msisdn, long p_prefixID, String p_subscriberType, String p_networkCode, String p_serviceType, 
    		String p_interfaceCategory, String p_userType, String p_action) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "getInterfaceRoutingDetails";
        if (_log.isDebugEnabled()) {
        	 loggerValue.setLength(0);
          	loggerValue.append( " Entered with MSISDN=");
          	loggerValue.append(p_msisdn);
        	loggerValue.append(" Prefix ID=" );
          	loggerValue.append(p_prefixID);
        	loggerValue.append(" p_subscriberType=" );
          	loggerValue.append(p_subscriberType);
        	loggerValue.append(" p_networkCode=");
          	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_serviceType=");
          	loggerValue.append(p_serviceType);
        	loggerValue.append(" p_interfaceCategory=");
          	loggerValue.append(p_interfaceCategory);
        	loggerValue.append(" p_userType=");
          	loggerValue.append(p_userType);
          	loggerValue.append(" p_action=" );
          	loggerValue.append(p_action);
            _log.debug( methodName,loggerValue );
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
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
        try {
            if (!_onlyForEvr) {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                    .getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType() + "_" + _requestVO.getType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            "process",
                            _requestIDStr,
                            "For =" + _receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO
                                .getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO
                                .getAlternateInterfaceType());
                    }

                    p_interfaceCategory = _serviceInterfaceRoutingVO.getInterfaceType();

                    if (!_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool()) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_TO_BE_SENT_INTERFACE_NOT_DEFINED);
                    }

                    final RoutingControlDAO routingControlDAO = new RoutingControlDAO();
                    final ArrayList routingControlList = routingControlDAO.loadRoutingControlDetailsList(p_con);

                    // alternate interface type should be defined in
                    // routing_control table
                    boolean inerfaceFound = false;
                    final Iterator iterator = routingControlList.iterator();
                    while (iterator.hasNext()) {
                        subscriberRoutingControlVO = (SubscriberRoutingControlVO) iterator.next();
                        if (subscriberRoutingControlVO.getInterfaceCategory().equals(_serviceInterfaceRoutingVO.getAlternateInterfaceType()) && PretupsI.SERVICE_TYPE_DVD
                            .equals(subscriberRoutingControlVO.getServiceType())) {
                            inerfaceFound = true;
                            break;
                        }
                    }

                    if (!inerfaceFound) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_TO_BE_SENT_INTERFACE_NOT_DEFINED);
                    }

                } else {
                    p_interfaceCategory = PretupsI.INTERFACE_CATEGORY_VOMS;
                    _log.info("process", _requestVO.getRequestIDStr(), "Service Interface Routing control Not defined");
                }

            } else {
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            "process",
                            _requestIDStr,
                            "For =" + _receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO
                                .getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO
                                .getAlternateInterfaceType());
                    }

                    p_interfaceCategory = _serviceInterfaceRoutingVO.getAlternateInterfaceType();
                } else {
                    _log.info("process", _requestVO.getRequestIDStr(),
                        "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SEnquiryHandler[process]", "", "", "",
                        "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                }

                
            }
            subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
            //
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " subscriberRoutingControlVO=" + subscriberRoutingControlVO);
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
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                            interfaceMappingVO1 = ServiceSelectorInterfaceMappingCache
                                .getObject(_serviceType + "_" + _c2sTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
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
                    isSuccess = checkInterfaceMappingv01(p_prefixID,
							p_networkCode, p_interfaceCategory, p_action);
                } else {
                    isSuccess = false;
                }
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DVDController[getInterfaceRoutingDetails]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Routing control information not defined so performing series based routing");
                isSuccess = checkInterfaceMappingv01(p_prefixID, p_networkCode,
						p_interfaceCategory, p_action);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController[getInterfaceRoutingDetails]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            isSuccess = false;
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting with isSuccess=" + isSuccess);
        }
        return isSuccess;
    }

	private boolean checkInterfaceMappingv01(long p_prefixID,
			String p_networkCode, String p_interfaceCategory, String p_action)
			throws BTSLBaseException {
		boolean isSuccess;
		ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
		MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
		    interfaceMappingVO1 = ServiceSelectorInterfaceMappingCache
		        .getObject(_serviceType + "_" + _c2sTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
		}
		if (interfaceMappingVO1 == null) {
		    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_interfaceCategory, p_action);
		    isSuccess = true;
		    setInterfaceDetails(p_prefixID, p_interfaceCategory, p_action, null, true, interfaceMappingVO, null);
		} else {
		    isSuccess = true;
		    setInterfaceDetails(p_prefixID, p_interfaceCategory, p_action, null, true, null, interfaceMappingVO1);
		}
		return isSuccess;
	}

    /**
     * Method to do the validation of the receiver and perform the steps before
     * the topup stage
     * 
     * @param p_con
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequest() throws BTSLBaseException, Exception {
        Connection con = null;
        MComConnectionI mcomCon = null;
        final String methodName = "processValidationRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered and performing validations for transfer ID=" + _transferID + "-" + lastTransferId + " " + _c2sTransferVO.getModule() + " " + _c2sTransferVO
                .getReceiverNetworkCode() + " " + _type);
        }

        try {
            final CommonClient commonClient = new CommonClient();
            _itemList = new ArrayList();
            _itemList.add(_senderTransferItemVO);
            _itemList.add(_receiverTransferItemVO);
            _c2sTransferVO.setTransferItemList(_itemList);

           // validate the receiver on IN
        	 final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_c2sTransferVO.getModule(),
                     _c2sTransferVO.getReceiverNetworkCode(), _type);
             _intModCommunicationTypeR = networkInterfaceModuleVOS.getCommunicationType();
             _intModIPR = networkInterfaceModuleVOS.getIP();
             _intModPortR = networkInterfaceModuleVOS.getPort();
             _intModClassNameR = networkInterfaceModuleVOS.getClassName();
             _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
             _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        	_receiverInterfaceInfoInDBFound = true;
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            
        	if(_requestVO.getRequestMap()!=null && _requestVO.getRequestMap().get("SERVICECLASS") != null){
				_subValRequired=false;
				_receiverTransferItemVO.setServiceClassCode((String)_requestVO.getRequestMap().get("SERVICECLASS")); 
				_receiverTransferItemVO.setServiceClass(_receiverTransferItemVO.getServiceClassCode());
			}
			String requestStr = getReceiverValidateStr();
			LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			final String receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeR, _intModIPR, _intModPortR, _intModClassNameR);
			TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
			try {
				updateForReceiverValidateResponse(receiverValResponse);
			} catch (BTSLBaseException be) {
				LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
				if (_log.isDebugEnabled()) {
					StringBuilder msg = new StringBuilder();
					msg.append("inside catch of BTSL Base Exception: ");
					msg.append(be.getMessage() );
					msg.append(" _receiverInterfaceInfoInDBFound: ");
					msg.append(_receiverInterfaceInfoInDBFound);
					_log.debug(methodName, msg);
				}
				if (_receiverInterfaceInfoInDBFound && _receiverTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
					PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _type);
				}
				 PretupsBL.validateRecieverLimits(_c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE, quantityRequested);
				throw be;
			} catch (Exception e) {
				LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
				 PretupsBL.validateRecieverLimits(_c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE, quantityRequested);
				throw new BTSLBaseException(this, methodName, "");
			}
			
			LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
			InterfaceVO interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
			if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > interfaceVO.getValExpiryTime()) {
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "DVDController[processValidationRequest]",
						_transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till validation");
				throw new BTSLBaseException("DVDController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
			}
			
            PretupsBL.validateServiceClassChecks(con, _receiverTransferItemVO, _c2sTransferVO, PretupsI.C2S_MODULE, _requestVO.getServiceType());
            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
            PretupsBL.validateTransferRule(con, _c2sTransferVO, PretupsI.C2S_MODULE);
            CardGroupBL.calculateCardGroupDetails(con, _c2sTransferVO, PretupsI.C2S_MODULE, true);
            if (_receiverTransferItemVO.getPreviousExpiry() == null) {
                    _receiverTransferItemVO.setPreviousExpiry(_currentDate);
                }
            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
            	  PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
            	}

            vomsVoucherList = getVoucherList(con);
            _requestVO.setVomsVoucherList(vomsVoucherList);
            try {
            	mcomCon.finalCommit();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_DVD_FAIL);
            }
            if (mcomCon != null)
				mcomCon.close("DVDController#process");
			mcomCon = null;
			con = null;
			
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            ChannelTransferBL.increaseC2STransferOutCounts(con, _c2sTransferVO, true, quantityRequested);
            
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

		   _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
           _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        	populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            
        	_c2sTransferVO.setPinSentToMsisdn(_receiverMSISDN);
            // Method to insert the record in c2s transfer table
            if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
            	if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "_vomsVoucherList Size=" + vomsVoucherList.size());
                }
                ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO, vomsVoucherList);
            }
            _transferDetailAdded = true;
            // Commit the transaction and release the locks
            try {
            	mcomCon.finalCommit();
            } catch (Exception be) {
                _log.errorTrace(methodName, be);
            }
            if (mcomCon != null)
				mcomCon.close("DVDController#process");
			mcomCon = null;
			con = null;

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Marked Under process", PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");

            try
            {
                _finalTransferStatusUpdate=updateVouchersInDb(_receiverVO, _c2sTransferVO, quantityRequested, new ArrayList<VomsVoucherVO>(vomsVoucherList));
            }
            catch(BTSLBaseException be)
            {
                throw be;
            }
            catch(Exception e)
            {
                throw (BTSLBaseException)e;
            }
        	_requestVO.setValueObject((ArrayList)vomsVoucherList);
        	
        	
            // Push Under Process Message to Sender and Receiver , this might
            // have to be implemented on flag basis whether to send message or
            // not
            if (_c2sTransferVO.isUnderProcessMsgReq() && _receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
                .equals(_notAllowedRecSendMessGatw)) {
                (new PushMessage(_receiverMSISDN, getReceiverUnderProcessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
            }

            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue) {
                final Thread _controllerThread = new Thread(this);
                _controllerThread.start();
                _oneLog = false;
            }
        } catch (BTSLBaseException be) {
        	 if (con != null) {
             	mcomCon.finalRollback();
             }
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_DVD), new String[] { _transferID, _c2sTransferVO.getVoucherQuantity() }));
                }
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.ERROR_DVD_FAIL);
                }
            }
            _log.error("DVDController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());

            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (con != null) {
            	mcomCon.finalRollback();
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_DVD), new String[] { _transferID, _c2sTransferVO.getVoucherQuantity() }));
                }
            } 
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_DVD_FAIL);
        } finally {
            if (mcomCon != null)
				mcomCon.close("DVDController#process");
			mcomCon = null;
			con = null;
        }
    }

	
	/**
	 * Method to get the reciever validate String
	 * @return
	 */
	public String getReceiverValidateStr()
	{
		StringBuffer strBuff;
		strBuff=new StringBuffer(getReceiverCommonString());
		strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_VALIDATE_ACTION);
		return strBuff.toString();
	}
	
	/**
	 *  Method to get the string to be sent to the interface for topup
	 * @return
	 */
	private String getReceiverCommonString()
	{
		StringBuffer strBuff;
		strBuff=new StringBuffer("MSISDN="+_receiverMSISDN);
		strBuff.append("&TRANSACTION_ID="+_transferID);
		strBuff.append("&NETWORK_CODE="+_receiverVO.getNetworkCode());
		strBuff.append("&INTERFACE_ID="+_receiverTransferItemVO.getInterfaceID());
		strBuff.append("&INTERFACE_HANDLER="+_receiverTransferItemVO.getInterfaceHandlerClass());
		strBuff.append("&INT_MOD_COMM_TYPE="+_intModCommunicationTypeR);
		strBuff.append("&INT_MOD_IP="+_intModIPR);
		strBuff.append("&INT_MOD_PORT="+_intModPortR);
		strBuff.append("&INT_MOD_CLASSNAME="+_intModClassNameR);
		strBuff.append("&MODULE="+PretupsI.C2S_MODULE);
		strBuff.append("&CARD_GROUP_SELECTOR="+_requestVO.getReqSelector());
		strBuff.append("&USER_TYPE=R");
		strBuff.append("&REQ_SERVICE="+_serviceType);
		strBuff.append("&INT_ST_TYPE="+_c2sTransferVO.getReceiverInterfaceStatusType());
		return strBuff.toString();
	}
	
	
	/**
	 * Method to process the response of the receiver validation from IN
	 * @param str
	 * @throws BTSLBaseException
	 */
	public void updateForReceiverValidateResponse(String str) throws BTSLBaseException
	{
		final String methodName = "updateForReceiverValidateResponse";
		final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
		StringBuilder loggerValue= new StringBuilder(); 
		final String status = (String) map.get("TRANSACTION_STATUS");
		ArrayList altList = null;
		boolean isRequired = false;

		_receiverTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
		try{_requestVO.setInValidateURL((String)map.get("IP")); _c2sTransferVO.setInfo10(_requestVO.getInValidateURL());}catch(Exception e){_log.errorTrace(methodName, e);}
		try{_requestVO.setValINRespCode(_receiverVO.getInterfaceResponseCode());_c2sTransferVO.setInfo6(_requestVO.getValINRespCode());}catch(Exception ex){_log.errorTrace(methodName, ex);}
		try{_c2sTransferVO.setInfo9((String)map.get("SERVICE_CLASS"));}catch(Exception ex1){_log.errorTrace(methodName, ex1);}
		if (null != map.get("IN_START_TIME")) {
			_requestVO.setValidationReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
		}
		if (null != map.get("IN_END_TIME")) {
			_requestVO.setValidationReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
		}

		final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
		if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
			new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
		}
		if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
			_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			altList = InterfaceRoutingControlCache.getRoutingControlDetails(_receiverTransferItemVO.getInterfaceID());
			if (altList != null && !altList.isEmpty()) {
				performAlternateRouting(altList); // Performing alternate
			} else {
				isRequired = true;
			}
		}
		if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
			if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
				updateReceiverLocale((String) map.get("IN_LANG"));
			}
			_receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
			try {
				_receiverTransferItemVO.setAccountStatus(URLDecoder.decode((String) map.get("ACCOUNT_STATUS")));
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (BTSLUtil.isNullString(_receiverTransferItemVO.getAccountStatus())) {
				_receiverTransferItemVO.setAccountStatus(" ");
			}
			_receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
			_receiverTransferItemVO.setValidationStatus(status);
			_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
			_receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
			_receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
			_receiverTransferItemVO.setSubscriberType(_receiverSubscriberType);
			String[] strArr ;
			if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
				_c2sTransferVO.setErrorCode(status + "_R");
				_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				_receiverTransferItemVO.setTransferStatus(status);
				_senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
				if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED.equals(_receiverTransferItemVO.getValidationStatus())) {
					throw new BTSLBaseException("DVDController", methodName, InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED + "_S", 0, strArr, null);
				} else {
					throw new BTSLBaseException("DVDController", methodName, _c2sTransferVO.getErrorCode(), 0, strArr, null);
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
				_receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.error(methodName, loggerValue );
				_log.errorTrace(methodName, e);
			}
			try {
				_receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.errorTrace(methodName, e);
				
			}
			if(_subValRequired){  
				_receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
				_receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
			}
			_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
			_receiverTransferItemVO.setOldExporyInMillis((String) map.get("CAL_OLD_EXPIRY_DATE"));
			if(!BTSLUtil.isNullString((String) map.get("INTERFACE_PREV_BALANCE")))
			            {
						try {
							_receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
						} catch (Exception e) {
							loggerValue.setLength(0);
			            	loggerValue.append("Exception ");
			            	loggerValue.append(e);
							_log.error(methodName, loggerValue);
							_log.errorTrace(methodName, e);
						}
			 }
			_receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
			_receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));
			_receiverTransferItemVO.setBundleTypes((String) map.get("IN_RESP_BUNDLE_CODES"));
			_receiverTransferItemVO.setInAccountId((String) map.get("IN_ACCOUNT_ID"));
			if (_receiverTransferItemVO.getPreviousExpiry() == null) {
				_receiverTransferItemVO.setPreviousExpiry(_currentDate);
			}
			_operatorUtil.populateBonusListAfterValidation(map, _c2sTransferVO);
			try {
				final String lmbAllowedValue = (String) map.get("LMB_ALLOWED_VALUE");
				if (!BTSLUtil.isNullString(lmbAllowedValue)) {
					_receiverTransferItemVO.setLmbdebitvalue((Long.valueOf(lmbAllowedValue)));
				}
			} catch (Exception e) {
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.errorTrace(methodName, e);
			}
			_c2sTransferVO.setPreviousPromoExpiry((String) map.get("PROMO_OLD_EXPIRY_DATE"));
			_c2sTransferVO.setPreviousPromoBalance((String) map.get("INTERFACE_PROMO_PREV_BALANCE"));
			_receiverTransferItemVO.setPreviousExpiryInCal((String) map.get("CAL_OLD_EXPIRY_DATE"));
			_receiverTransferItemVO.setPreviousPromoExpiryInCal((String) map.get("PROMO_CAL_OLD_EXPIRY_DATE"));
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW))).booleanValue()) {
				_receiverTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
				_c2sTransferVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
			}
			try{_requestVO.setInValidateURL((String)map.get("IP"));	_c2sTransferVO.setInfo10(_requestVO.getInValidateURL());}catch(Exception e){
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, e);}
			try{_requestVO.setValINRespCode(_receiverVO.getInterfaceResponseCode());_c2sTransferVO.setInfo6(_requestVO.getValINRespCode());}catch(Exception ex){
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(ex);
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, ex);}
			try{_c2sTransferVO.setInfo9((String)map.get("SERVICE_CLASS"));}catch(Exception ex1){
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(ex1);
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, ex1);}
		}
	}

    /**
     * Method to perform the Interface routing for the subscriber MSISDN
     * 
     * @throws BTSLBaseException
     */
    private void performAlternateRouting(ArrayList altList) throws BTSLBaseException {
        final String methodName = "performAlternateRouting";
        StringBuilder loggerValue= new StringBuilder(); 
		      
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
                // Also store in global variables
                // If Not Found repeat the iteration for alt 2
                ListValueVO listValueVO = null;
                String requestStr ;
                CommonClient commonClient = null;
                String receiverValResponse ;
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
                            _c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
                            _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);

                            if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
                                if (PretupsI.LOCALE_LANGAUGE_EN.equals(_senderLocale.getLanguage())) {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                } else {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
                            }

                            checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

                            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                            	PretupsBL.validateRecieverLimits(_c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE, quantityRequested);
                            }

                            requestStr = getReceiverValidateStr();
                            commonClient = new CommonClient();

                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

                            receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeR, _intModIPR, _intModPortR, _intModClassNameR);

                            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                receiverValidateResponse(receiverValResponse, 1, altList.size());
                                if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
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
                                if (PretupsI.LOCALE_LANGAUGE_EN.equals(_senderLocale.getLanguage())) {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                } else {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
                            }

                            checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

                            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                            	PretupsBL.validateRecieverLimits(_c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE, quantityRequested);
                            }

                            requestStr = getReceiverValidateStr();
                            commonClient = new CommonClient();

                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

                            receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeR, _intModIPR, _intModPortR, _intModClassNameR);

                            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                receiverValidateResponse(receiverValResponse, 1, altList.size());
                                if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
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
                                        if (PretupsI.LOCALE_LANGAUGE_EN.equals(_senderLocale.getLanguage())) {
                                            _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                        } else {
                                            _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                        }
                                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
                                    }

                                    checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

                                    if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                                    	PretupsBL.validateRecieverLimits(_c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE, quantityRequested);
                                    }

                                    requestStr = getReceiverValidateStr();

                                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                                    TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                                        PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 2");

                                    receiverValResponse = commonClient
                                        .process(requestStr, _transferID, _intModCommunicationTypeR, _intModIPR, _intModPortR, _intModClassNameR);

                                    TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                                        PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                                    try {
                                        receiverValidateResponse(receiverValResponse, 2, altList.size());
                                        if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
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
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DVDController[performAlternateRouting]", _transferID,
                _senderMSISDN, _senderNetworkCode,loggerValue.toString());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
            _log.debug(methodName,  loggerValue );
        }
        int recieverLoadStatus = 0;
        int senderLoadStatus = 0;
        try {
            _c2sTransferVO.setRequestVO(_requestVO);
            _c2sTransferVO.setSenderTransferItemVO(_senderTransferItemVO);
            _c2sTransferVO.setReceiverTransferItemVO(_receiverTransferItemVO);
            senderLoadStatus = LoadController.checkInterfaceLoad(_c2sTransferVO.getReceiverNetworkCode(), _senderTransferItemVO.getInterfaceID(), _transferID, _c2sTransferVO,
                true);
            if (senderLoadStatus == 0) {
                if (_serviceType.equals(PretupsI.SERVICE_TYPE_DVD)) {
                    recieverLoadStatus = LoadController.checkInterfaceLoad(_c2sTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _transferID,
                        _c2sTransferVO, true);
                    if (recieverLoadStatus == 0) {
                    	checkRecieverTransactionLoad();
                    	checkSenderTransactionLoad();
                        debugcheckTransactionLoad();
                    }
                    // Request in Queue
                    else if (recieverLoadStatus == 1) {
                        // Decrease the interface counter of the sender that was
                        // increased
                        LoadController.decreaseCurrentInterfaceLoad(_transferID, _c2sTransferVO.getReceiverNetworkCode(), _senderTransferItemVO.getInterfaceID(),
                            LoadControllerI.DEC_LAST_TRANS_COUNT);

                        final String[] strArr = { _receiverMSISDN, String.valueOf(PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())) };
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
                    }
                    // Refuse the request
                    else {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                    }
                } else {
                    LoadController.checkTransactionLoad(_c2sTransferVO.getReceiverNetworkCode(), _senderTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE, _transferID,
                        true, LoadControllerI.USERTYPE_SENDER);
                }
            } else if (senderLoadStatus == 1) {
                final String[] strArr = { _receiverMSISDN, String.valueOf(PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())) };
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("Refusing request getting Exception:" );
        	loggerValue.append(be.getMessage());
            _log.error("DVDController[checkTransactionLoad]", loggerValue );
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_DVD_FAIL);
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
        final String methodName = "checkTransactionLoad";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Checking load for transfer ID=" + _transferID + " on interface=" + p_interfaceID);
        }
        int recieverLoadStatus = 0;

        try {
            // Do not enter the request in Queue
            recieverLoadStatus = LoadController.checkInterfaceLoad(_c2sTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _transferID,
                _c2sTransferVO, false);
            if (recieverLoadStatus == 0) {
                LoadController.checkTransactionLoad(_c2sTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE, _transferID, true,
                    LoadControllerI.USERTYPE_RECEIVER);
                debugcheckTransactionLoad();
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            _log.error("DVDController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_DVD_FAIL);
        }
    }

	private void debugcheckTransactionLoad() {
		if (_log.isDebugEnabled()) {
		    _log.debug("DVDController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
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
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
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
        String[] strArr ;

        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            _c2sTransferVO.setErrorCode(status + "_R");
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(status);
            _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { _transferID, _c2sTransferVO.getVoucherQuantity() };
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", PretupsErrorCodesI.DVD_RECEIVER_FAIL, 0, strArr, null);
        }

        _receiverTransferItemVO.setTransferStatus(status);
        _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

        try {
            _receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
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
        _receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));

        // TO DO Done for testing purpose should we use it or give exception in
        // this case
        if (_receiverTransferItemVO.getPreviousExpiry() == null) {
            _receiverTransferItemVO.setPreviousExpiry(_currentDate);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited");
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DVDController[updateReceiverLocale]",
                        _transferID, _receiverMSISDN, "", "Exception: Notification language returned from IN is not defined in system p_languageCode: " + p_languageCode);
                } else {
                    _receiverLocale = LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode);
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
     * Method to get the under process message to be sent to receiver
     * 
     * @return
     */
    private String getReceiverUnderProcessMessage() {
        final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO
            .getValidity()), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _senderMSISDN, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _channelUserVO
            .getUserName(), _payableAmt };
        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_UNDERPROCESS_VMS, messageArgArray, _requestVO.getRequestGatewayType());
    }

	/**
	 * Method to get the success message to be sent to sender
	 * @return
	 */
	private String getSenderUnderProcessMessage()
	{   
		String 	key = PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS+"_"+PretupsI.SERVICE_TYPE_DVD;
		String[] messageArgArray;		
		if (BTSLUtil.isNullString(_sid)) {
			messageArgArray= new String[]{_receiverMSISDN,saleBatchNumber,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_c2sTransferVO.getVoucherQuantity(),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),_c2sTransferVO.getProductName()};
		} else {
			messageArgArray= new String[]{_sid,saleBatchNumber,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_c2sTransferVO.getVoucherQuantity(),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),_c2sTransferVO.getProductName()};
		}
		return BTSLUtil.getMessage(_senderLocale,key,messageArgArray);
	}
	
	/**
	 *  Method to get the success message to be sent to receiver
	 * Method updated for notification message using service class date 15/05/06
	 *
	 * @return
	 */
	private String getReceiverSuccessMessage()
	{
		final String methodName = "getReceiverSuccessMessage";
		String[] messageArgArray=null;
		String key;
		messageArgArray= new String[]{PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),serialNoAsString,vomsPinAsString,saleBatchNumber};
		key=PretupsErrorCodesI.DVD_RECEIVER_SUCCESS;
		return BTSLUtil.getMessage(_receiverLocale,key,messageArgArray);
	}
	
	private String getSenderSuccessMessage()
	{
		String[] messageArgArray;
		if (BTSLUtil.isNullString(_sid))
		{
			messageArgArray= new String[]{PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),serialNoAsString,vomsPinAsString,saleBatchNumber,_receiverMSISDN};
		}
		else
		{
			messageArgArray= new String[]{PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),serialNoAsString,vomsPinAsString,saleBatchNumber,_sid};
		}
		String key;
		key=PretupsErrorCodesI.DVD_SENDER_SUCCESS;
		return BTSLUtil.getMessage(((ChannelUserVO) _c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),key,messageArgArray);
	}
	
	private String getReceiverFailMessage()
	{
		 final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* quantityRequested), _senderMSISDN, _channelUserVO.getUserName(), lastTransferId, String
		            .valueOf(quantityRequested) };
		return BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY_DVD,messageArgArray);
	}	
	/**
	 *  Method to get the under process message before validation to be sent to sender
	 * @return
	 */
	private String getSndrUPMsgBeforeValidation()
	{
		String[] messageArgArray;		
		if (BTSLUtil.isNullString(_sid)) {
			messageArgArray= new String[]{_receiverMSISDN,_transferID,_c2sTransferVO.getVoucherQuantity()};
		} else {
			messageArgArray= new String[]{_sid,_transferID,_c2sTransferVO.getVoucherQuantity()};
		}
		return BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS_B4VAL_DVD,messageArgArray);
	}
	
	/**
	 * Method to populate the service interface details based on the action and service type
	 * @param action
	 * @throws BTSLBaseException
	 */
	public void populateServiceInterfaceDetails(Connection p_con,String action) throws BTSLBaseException
	{
		final String methodName="populateServiceInterfaceDetails";
		String receiverNetworkCode=_receiverVO.getNetworkCode();
		long receiverPrefixID=_receiverVO.getPrefixID();
		boolean isReceiverFound=false;
        
		_onlyForEvr=true;
		if ((!_receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
				.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
			isReceiverFound=getInterfaceRoutingDetails(p_con,_receiverMSISDN,receiverPrefixID,_receiverVO.getSubscriberType(),receiverNetworkCode,_c2sTransferVO.getServiceType(),_type,PretupsI.USER_TYPE_RECEIVER,action);
		} else {
			isReceiverFound = true;
		}
		if (!isReceiverFound) {
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING);
		}
		
	}
	
	/**
	 * Method to set the interface Details
	 * @param p_prefixID
	 * @param p_interfaceCategory
	 * @param p_action
	 * @param p_listValueVO
	 * @param p_useInterfacePrefixVO
	 * @param p_MSISDNPrefixInterfaceMappingVO
	 * @throws BTSLBaseException
	 */
	private void setInterfaceDetails(long p_prefixID,String p_interfaceCategory,String p_action,ListValueVO p_listValueVO,boolean p_useInterfacePrefixVO,MSISDNPrefixInterfaceMappingVO p_MSISDNPrefixInterfaceMappingVO,ServiceSelectorInterfaceMappingVO p_serviceSelectorInterfaceMappingVO) throws BTSLBaseException
	{
		final String methodName = "setInterfaceDetails";
		StringBuilder loggerValue= new StringBuilder(); 
		if(_log.isDebugEnabled()) {
			        loggerValue.setLength(0);
	            	loggerValue.append(" Entered p_prefixID=");
	            	loggerValue.append(p_prefixID);
	            	loggerValue.append(" p_action=");
	            	loggerValue.append(p_action);
	            	loggerValue.append(" p_interfaceCategory=");
	            	loggerValue.append(p_interfaceCategory);
	            	loggerValue.append(" p_listValueVO=");
	            	loggerValue.append(p_listValueVO);
	            	loggerValue.append(" p_useInterfacePrefixVO=");
	            	loggerValue.append(p_useInterfacePrefixVO);
	            	loggerValue.append(" p_MSISDNPrefixInterfaceMappingVO=");
	            	loggerValue.append(p_MSISDNPrefixInterfaceMappingVO);
			_log.debug(methodName,_requestIDStr,loggerValue);
		}
		try
		{
			String interfaceID=null;
			String interfaceHandlerClass=null;
			String underProcessMsgReqd=null;
			String allServiceClassID=null;
			String externalID=null;
			String status=null;
			String message1=null;
			String message2=null;
			String interfaceStatusTy=null;
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()&&p_serviceSelectorInterfaceMappingVO!=null)
			{
				interfaceID = p_serviceSelectorInterfaceMappingVO.getInterfaceID();
				interfaceHandlerClass= p_serviceSelectorInterfaceMappingVO.getHandlerClass();
				underProcessMsgReqd=p_serviceSelectorInterfaceMappingVO.getUnderProcessMsgRequired();
				allServiceClassID=p_serviceSelectorInterfaceMappingVO.getAllServiceClassID();
				externalID=p_serviceSelectorInterfaceMappingVO.getExternalID();
				status=p_serviceSelectorInterfaceMappingVO.getInterfaceStatus();
				message1=p_serviceSelectorInterfaceMappingVO.getLanguage1Message();
				message2=p_serviceSelectorInterfaceMappingVO.getLanguage2Message();
				interfaceStatusTy=p_serviceSelectorInterfaceMappingVO.getStatusType();
			}
			if(p_useInterfacePrefixVO&&p_serviceSelectorInterfaceMappingVO==null)
			{
				interfaceID = p_MSISDNPrefixInterfaceMappingVO.getInterfaceID();
				interfaceHandlerClass= p_MSISDNPrefixInterfaceMappingVO.getHandlerClass();
				underProcessMsgReqd=p_MSISDNPrefixInterfaceMappingVO.getUnderProcessMsgRequired();
				allServiceClassID=p_MSISDNPrefixInterfaceMappingVO.getAllServiceClassID();
				externalID=p_MSISDNPrefixInterfaceMappingVO.getExternalID();
				status=p_MSISDNPrefixInterfaceMappingVO.getInterfaceStatus();
				message1=p_MSISDNPrefixInterfaceMappingVO.getLanguage1Message();
				message2=p_MSISDNPrefixInterfaceMappingVO.getLanguage2Message();
				interfaceStatusTy=p_MSISDNPrefixInterfaceMappingVO.getStatusType();
			}
			else if(p_serviceSelectorInterfaceMappingVO==null)
			{
				interfaceID=p_listValueVO.getValue();
				interfaceHandlerClass=p_listValueVO.getLabel();
				allServiceClassID=p_listValueVO.getTypeName();
				externalID=p_listValueVO.getIDValue();
				underProcessMsgReqd=p_listValueVO.getType();
				status=p_listValueVO.getStatus();
				message1=p_listValueVO.getOtherInfo();
				message2=p_listValueVO.getOtherInfo2();
				interfaceStatusTy=p_listValueVO.getStatusType();
			}
			

			_receiverTransferItemVO.setPrefixID(p_prefixID);
			_receiverTransferItemVO.setInterfaceID(interfaceID);
			_receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
			_receiverTransferItemVO.setInterfaceType(p_interfaceCategory);
			_senderTransferItemVO.setPrefixID(p_prefixID);
			_senderTransferItemVO.setInterfaceID(interfaceID);
			_senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
			_senderTransferItemVO.setInterfaceType(p_interfaceCategory);
			if(!p_useInterfacePrefixVO && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
				_receiverInterfaceInfoInDBFound=true;
			}
			_externalID=externalID;
			_interfaceStatusType=interfaceStatusTy;
			if(PretupsI.YES.equals(underProcessMsgReqd)) {
				_c2sTransferVO.setUnderProcessMsgReq(true);
			}
			_receiverAllServiceClassID=allServiceClassID;
			_c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
			_c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
		
			//Check if interface status is Active or not.
			if(!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceStatusTy))
			{
				if(PretupsI.LOCALE_LANGAUGE_EN.equals(_senderLocale.getLanguage())) {
					_c2sTransferVO.setSenderReturnMessage(message1);
				} else {
					_c2sTransferVO.setSenderReturnMessage(message2);
				}
				throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_DVD);
			}
		}
		catch(BTSLBaseException be)
		{     loggerValue.setLength(0);
    	       loggerValue.append("Getting Base Exception =");
    	      loggerValue.append(be.getMessage());
			_log.error(methodName,loggerValue);
			_log.errorTrace(methodName, be);
			throw be;
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PrepaidController[setInterfaceDetails]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.ERROR_DVD_FAIL);
		}
		finally
		{
			if(_log.isDebugEnabled()) {
				loggerValue.setLength(0);
            	loggerValue.append(" Exiting with Sender Interface ID=");
            	loggerValue.append(_senderTransferItemVO.getInterfaceID());
            	loggerValue.append(" Receiver Interface=");
            	loggerValue.append(_receiverTransferItemVO.getInterfaceID());
				_log.debug(methodName,_requestIDStr,loggerValue);
			}
		}
	}


    private void checkRecieverTransactionLoad() throws BTSLBaseException{
    	try {
    		LoadController.checkTransactionLoad(_c2sTransferVO.getReceiverNetworkCode(), _senderTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE,
    				_transferID, true, LoadControllerI.USERTYPE_SENDER);
    	} catch (BTSLBaseException e) {
    		// Decreasing interface load of receiver which we
    		// had incremented before 27/09/06, sender was
    		// decreased in the method
    		LoadController.decreaseCurrentInterfaceLoad(_transferID, _c2sTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(),
    				LoadControllerI.DEC_LAST_TRANS_COUNT);
    		throw e;
    	}
    }
  
    private void checkSenderTransactionLoad() throws BTSLBaseException{
    	try {
    		LoadController.checkTransactionLoad(_c2sTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE,
    				_transferID, true, LoadControllerI.USERTYPE_RECEIVER);
    	} catch (BTSLBaseException e) {
    		LoadController.decreaseTransactionInterfaceLoad(_transferID, _c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
    		throw e;
    	}
    }
    private void isReceiverBarred(Connection p_con) throws BTSLBaseException{
    	try {
            PretupsBL.checkMSISDNBarred(p_con, _receiverMSISDN, _receiverVO.getNetworkCode(), _c2sTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
        } catch (BTSLBaseException be) {
            if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
            }
            throw be;
        }
    }

	public String prepareOtherInfo(String p_extraPrefixOtherInfo, String p_otherInfo)
	{
		final String methodName="prepareOtherInfo";
		if(_log.isDebugEnabled()){
			_log.debug(methodName, "Entered: _extraPrefixOtherInfo="+extraPrefixOtherInfo+",_vomsVO.getOtherInfo()="+_vomsVO.getOtherInfo());
			
		}
		if(BTSLUtil.isNullString(p_otherInfo)){
			return p_otherInfo;
		} else {
			return p_extraPrefixOtherInfo+p_otherInfo;
		}		
		
	}
	
	 /**
	 *  This method is responsible to generate the transaction ids in the memory.
	 * @param p_transferVO
	 * @param p_quantityRequested
	 * @return
	 * @throws BTSLBaseException
	 */
	public static synchronized ArrayList<String> generateDVDTransferID(TransferVO p_transferVO, int p_quantityRequested) throws BTSLBaseException {
	        final String methodName = "generateDVDTransferID";
	        // if(_log.isDebugEnabled())
	        // _log.debug("generateMVDTransferID","Entered p_quantityRequested:"+p_quantityRequested);
	        // long newTransferID=0;
	         SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
	        ArrayList<String> transferIDList = null;
	        String transferID = null;
	        String minut2Compare = null;
	        Date mydate = null;
	        int currentMinut = 0;
	        try {

	            transferIDList = new ArrayList<String>();

	            for (int i = 0; i < p_quantityRequested; i++) {
	                mydate = new Date();
	                p_transferVO.setCreatedOn(mydate);
	                minut2Compare = _sdfCompare.format(mydate);
	                currentMinut = Integer.parseInt(minut2Compare);

	                if (currentMinut != _prevMinut) {
	                    _transactionIDCounter = 1;
	                    _prevMinut = currentMinut;

	                } else {
	                    _transactionIDCounter++;

	                }
	                if (_transactionIDCounter == 0) {
	                    throw new BTSLBaseException("DVDController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
	                }
	                transferID = _operatorUtil.formatDVDTransferID(p_transferVO, _transactionIDCounter);
	                transferIDList.add(transferID);
	            }
	            // setting the last transfer id
	            p_transferVO.setTransferID((String) transferIDList.get(0));
	            return transferIDList;
	        }
	        catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            throw new BTSLBaseException("DVDController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
	        } finally {
	             if(_log.isDebugEnabled() )
	             _log.debug(methodName,"Exiting ");
	        }
	    }
	 
		/**
		 * @param p_con
		 * @throws BTSLBaseException
		 */
		private void checkvomsVoucherList(Connection p_con)
				throws BTSLBaseException {
			VomsVoucherVO voucherVO;
			if (vomsVoucherList == null || vomsVoucherList.isEmpty()) {
			    vomsVoucherList = new ArrayList<VomsVoucherVO>();
			    for (int i = 0; i < quantityRequested; i++) {
			        voucherVO = new VomsVoucherVO();
			        voucherVO.setTransactionID((String) transferIdList.get(i));
			        vomsVoucherList.add(voucherVO);
			    }
			    _itemList = new ArrayList<TransferItemVO>();
			    _itemList.add(_senderTransferItemVO);
			    _itemList.add(_receiverTransferItemVO);
			    _c2sTransferVO.setTransferItemList(_itemList);
			}
			if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
			    ChannelTransferBL.addC2STransferDetails(p_con, _c2sTransferVO, vomsVoucherList);// add transfer details in database
			}
		}
		
		
		  /**
		 * @param p_receiverVO
		 * @param p_transferVO
		 * @param p_quantityRequired
		 * @param p_voucherList
		 * @return
		 * @throws BTSLBaseException
		 */
		public boolean updateVouchersInDb(ReceiverVO p_receiverVO, C2STransferVO p_transferVO, int p_quantityRequired, ArrayList<VomsVoucherVO> p_voucherList) throws BTSLBaseException {
		        if (_log.isDebugEnabled()) {
		            _log.debug("updateVouchersInDb",
		                " Eentered for p_quantityRequired=" + p_quantityRequired + "p_voucherList size" + p_voucherList.size());
		        }
		        boolean finalTransferStatusUpdate = true;
		        String receiverMsisdn = null;
		        VOMSVoucherDAO vomsDAO = null;
		        Connection con = null;MComConnectionI mcomCon = null;
		        String methodName = "updateVouchersInDb";
		        try {
		        	mcomCon = new MComConnection();
		            con=mcomCon.getConnection();
		            receiverMsisdn = p_transferVO.getReceiverMsisdn();
		            
		            //added 18-08-20, to check saleBatchNumber already exist for multiple DVD operation
		            saleBatchNumber = (p_transferVO.getTxnBatchId() != null) ? p_transferVO.getTxnBatchId() : ChannelTransferBL.generateSaleBatchNumberForDVD(p_transferVO);
		            //update voms_voucher and associate subscriber id
		            for (int i = 0; i < p_quantityRequired; i++) {
		                ((VomsVoucherVO) p_voucherList.get(i)).SetSaleBatchNo(saleBatchNumber);
		            }
		            VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
		            vomsDAO = new VOMSVoucherDAO();
		            if(vomsVoucherDAO.updateVoucherSubsriberId(con, p_voucherList,_c2sTransferVO) == p_quantityRequired){
		                if (vomsDAO.insertDetailsInVoucherAudit(con, p_voucherList) == p_quantityRequired) {
		                	mcomCon.finalCommit();
		                } else {
		                	mcomCon.finalRollback();
		                    // If vouchers status cannot be updated successfully, no
		                    // exception will be thrown
		                    EventHandler
		                        .handle(
		                            EventIDI.SYSTEM_ERROR,
		                            EventComponentI.SYSTEM,
		                            EventStatusI.RAISED,
		                            EventLevelI.MAJOR,
		                            "DVDController[updateVouchersInDb]",
		                            null,
		                            null,
		                            null,
		                            "Voucher can not be updated successfully from Serial number=" + ((VomsVoucherVO) p_voucherList.get(0)).getSerialNo() + " to " + ((VomsVoucherVO) p_voucherList
		                                .get(p_quantityRequired - 1)).getSerialNo());

		                }
		            } else {
		                // If vouchers status cannot be updated successfully, no
		                // exception will be thrown
		                EventHandler
		                    .handle(
		                        EventIDI.SYSTEM_ERROR,
		                        EventComponentI.SYSTEM,
		                        EventStatusI.RAISED,
		                        EventLevelI.MAJOR,
		                        "DVDController[updateVouchersInDb]",
		                        null,
		                        null,
		                        null,
		                        "Voucher can not be updated successfully from Serial number=" + ((VomsVoucherVO) p_voucherList.get(0)).getSerialNo() + " to " + ((VomsVoucherVO) p_voucherList
		                            .get(p_quantityRequired - 1)).getSerialNo());

		            }

		            VomsVoucherChangeStatusLog.log(p_transferVO.getTransferID(), p_transferVO.getSerialNumber(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_ENABLE, p_transferVO
		                .getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getUserID(), BTSLUtil.getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));

		            p_transferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
		            p_transferVO.setErrorCode(null);
		            p_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
		            // For increasing the counters in network and service type
		            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), p_transferVO.getRequestGatewayType(), p_transferVO.getNetworkCode(), p_transferVO
		                .getServiceType(), p_transferVO.getTransferID(), LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, p_transferVO.getReceiverNetworkCode());
		            TransactionLog.log(p_transferVO.getTransferID() + "-" + p_transferVO.getLastTransferId(), null, receiverMsisdn, p_receiverVO.getNetworkCode(),
		                PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Success", PretupsI.TXN_LOG_STATUS_SUCCESS, "Transfer Status=" + p_transferVO
		                    .getTransferStatus() + " voucher serial number=" + ((VomsVoucherVO) p_voucherList.get(0)).getSerialNo() + "-" + ((VomsVoucherVO) p_voucherList
		                    .get(p_quantityRequired - 1)).getSerialNo());
		            // validate receiver limits after Interface update
		            PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
		            // checking whether differential commission is applicable or not
		            if (PretupsI.YES.equals(p_transferVO.getDifferentialAllowedForService())) {
		                // Calculate Differential if transaction successful
		                try {
		                    new DiffCalBL().differentialCalculations(p_transferVO, PretupsI.C2S_MODULE, p_quantityRequired, p_voucherList);
		                } catch (BTSLBaseException be) {
		                    finalTransferStatusUpdate = false;
		                    _log
		                        .error(
		                            this,
		                            "For p_transactionID=" + p_transferVO.getTransferID() + " Diff applicable=" + p_transferVO.getDifferentialApplicable() + " Diff Given=" + p_transferVO
		                                .getDifferentialGiven() + " Not able to give Diff commission getting BTSL Base Exception=" + be.getMessage() + " Leaving transaction status as Under process");
		                    _log.errorTrace("METHOD_NAME", be);
		                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDUtil[updateVoucherAndGiveDifferentials]",
		                        p_transferVO.getTransferID(), p_transferVO.getSenderMsisdn(), p_transferVO.getNetworkCode(), "BTSLBaseException:" + be.getMessage());
		                } catch (Exception e) {
		                    finalTransferStatusUpdate = false;
		                    _log.errorTrace("METHOD_NAME", e);
		                    _log
		                        .error(
		                            this,
		                            "For p_transactionID=" + p_transferVO.getTransferID() + " Diff applicable=" + p_transferVO.getDifferentialApplicable() + " Diff Given=" + p_transferVO
		                                .getDifferentialGiven() + " Not able to give Diff commission getting Exception=" + e.getMessage() + " Leaving transaction status as Under process");
		                }
		            }
		        } catch (BTSLBaseException be) {
		            if (con != null) {
		                try {
		                	mcomCon.finalRollback();
		                } catch (Exception e) {
		                    _log.errorTrace("METHOD_NAME", e);
		                }
		            }
		            throw be;
		        } catch (Exception e) {
		            if (con != null) {
		                try {
		                	mcomCon.finalRollback();
		                } catch (Exception be) {
		                    _log.errorTrace("METHOD_NAME", be);
		                }
		            }
		            throw new BTSLBaseException(this, "updateVoucherAndGiveDifferentials", "");
		        } finally {
					if (mcomCon != null) {
						mcomCon.close("EvdUtil#updateVoucherAndGiveDifferentials");
						mcomCon = null;
					}
		            if (_log.isDebugEnabled()) {
		                _log.debug("updateVoucherAndGiveDifferentials", " Exited for p_transferVO= " + p_transferVO);
		            }
		        }
		        return finalTransferStatusUpdate;
		    }


		/**
		 * @param con
		 * @return
		 * @throws BTSLBaseException
		 */
		public ArrayList<VomsVoucherVO>getVoucherList(Connection con) throws BTSLBaseException{
			  String methodName = "getVoucherList";
			  VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
			  String  systemLimit =  ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DVD_ORDER_BY_PARAMETERS));
	            ArrayList<VomsVoucherVO> vomsVoucherVOList = vomsVoucherDAO.loadVomsVoucherVObyUserId(con, _c2sTransferVO,systemLimit);
	            if(BTSLUtil.isNullOrEmptyList(vomsVoucherVOList)){
	            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_NOT_ASSOSIATED);
	            }
	          
	            if(vomsVoucherVOList.size() <  Long.parseLong(_c2sTransferVO.getVoucherQuantity())){
                  throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_DVD_QTY_NOT_AVAILABLE );
                }
	            int i =0;
	            StringBuilder serialString = new StringBuilder();
	            StringBuilder vomsString = new StringBuilder();
	            for(VomsVoucherVO vomsVO :vomsVoucherVOList){
		            	vomsVO.setTransactionID((String)transferIdList.get(i));//Setting TXN ID
		            	i++;
		            	if(BTSLUtil.isNullString(vomsString.toString())){
		            		serialString.append(vomsVO.getSerialNo());
		            		vomsString.append(VomsUtil.decryptText(vomsVO.getPinNo()));
		            	}
		            	else{
		            		serialString.append(",").append(vomsVO.getSerialNo());
		            		vomsString.append(",").append(VomsUtil.decryptText(vomsVO.getPinNo()));
		            	}
		           
		            } 
	            vomsPinAsString = vomsString.toString();
	            serialNoAsString = serialString.toString();
			  return vomsVoucherVOList;
		  }
		
		
		  private String getReceiverAmbigousMessage() {
		        final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* quantityRequested), _senderMSISDN, _channelUserVO.getUserName(), lastTransferId, String
		            .valueOf(quantityRequested) };
		        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_AMBIGOUS_KEY_DVD, messageArgArray, _requestVO.getRequestGatewayType());
		    }
		  
}
