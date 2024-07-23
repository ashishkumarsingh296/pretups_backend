/*
 * @(#)MVDController.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ankit Singhal 03/05/2007 Initial Creation
 * Ashish Kumar July 03, 2007 Add for the transaction id generation in the
 * memory
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 * Controller class for handling the Multiple Electronic Voucher
 * Distribution(MVD) Services
 */

package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
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
import com.btsl.pretups.cardgroup.businesslogic.CardGroupBL;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.util.EvdUtil;
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
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.btsl.user.businesslogic.UserLoanVO;

public class MVDController implements ServiceKeywordControllerI, Runnable {
    private static Log _log = LogFactory.getLog(MVDController.class.getName());
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
    private ArrayList<TransferItemVO> _itemList = null;

    private boolean _transferDetailAdded = false;
    private String _type;
    private String _serviceType;
    private boolean _finalTransferStatusUpdate = true;
    private boolean _transferEntryReqd = false;
    private UserBalancesVO _userBalancesVO = null;
    private boolean _creditBackEntryDone = false;
    private Locale _senderLocale = null;
    private Locale _receiverLocale = null;
    private RequestVO _requestVO = null;
    private boolean _processedFromQueue = false;
    private boolean _recValidationFailMessageRequired = false;
    private boolean _recTopupFailMessageRequired = false;
    private String _notAllowedSendMessGatw;
    private String _receiverSubscriberType = null;
    private static OperatorUtilI _operatorUtil = null;
    private boolean _voucherMarked = false;

    private int _quantityRequested = 0;
    private List<String> _transferIdList = null;
    private String transferListString=null;
    private List<VomsVoucherVO> _vomsVoucherList = null;
    private String _lastTransferId = null;
    private static int _transactionIDCounter = 0;
    private static int _prevMinut = 0;
   
    private boolean _receiverMessageSendReq = false;
    private boolean _oneLog = true;
    private String _receiverAllServiceClassID = PretupsI.ALL;
    private boolean _vomsInterfaceInfoInDBFound = false;
    private boolean _receiverInterfaceInfoInDBFound = false;
    private boolean _onlyForEvr = false;
    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
    private String _vomsExternalID = null;
    private String _vomsAllServiceClassID = null;
    private String _externalID = null;
    private String _interfaceStatusType = null;
    private String _intModCommunicationTypeR;
    private String _intModIPR;
    private int _intModPortR;
    private String _intModClassNameR;
    private NetworkInterfaceModuleVO _networkInterfaceModuleVO = null;
    private final String _notAllowedRecSendMessGatw;
    private String _payableAmt = null;
    private VomsVoucherVO _vomsVO = null;    
    private boolean _deliveryTrackDone = false;
    private boolean _decreaseTransactionCounts = false;
    private boolean _isCounterDecreased = false;
    private HashMap<String,String> mapOfSerialAndPIN = null;
    private static final  String controllerName = "MVDController";
    private static final String exceptionMessage = "Leaving Reciever Unmarked Base Exception:";
    private static final String messageCodeForTXNlog = "Getting Code=";
    private static final String exceptionString = "Exception:";
    private static final String processString = "process";
    private static final String enteredString = "Entered";
    private static  final String dateFormat = "ddMMyyyy";
    // Loads Operator specific class. In MVD controller it is used for
    // validating the message format.
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MVDController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /*
     * In the constructor of MVDController initialize the date variable
     * _currentDate with current date. The
     * variables MVD_REC_GEN_FAIL_MSG_REQD_V & MVD_REC_GEN_FAIL_MSG_REQD_T
     * decides whether the validation and
     * top up failed message send to receiver or not.
     */

    public MVDController() {
		
		_log.debug(processString, enteredString);
        _c2sTransferVO = new C2STransferVO();
        _currentDate = new Date();
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("MVD_REC_GEN_FAIL_MSG_REQD_V")))) {
            _recValidationFailMessageRequired = true;
        }
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("MVD_REC_GEN_FAIL_MSG_REQD_T")))) {
            _recTopupFailMessageRequired = true;
        }
        _notAllowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("MVD_SEN_MSG_NOT_REQD_GW"));
        _notAllowedRecSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("MVD_REC_MSG_NOT_REQD_GW"));
        _log.debug(
            processString,
            "Exiting with _recValidationFailMessageRequired=" + _recValidationFailMessageRequired + " _recTopupFailMessageRequired=" + _recTopupFailMessageRequired + " _notAllowedSendMessGatw: " + _notAllowedSendMessGatw);
    }

    /**
     * Method to process the request of the Electornic Voucher Distribution as
     * well as Electornic Voucher Recharge
     * 
     * @param p_requestVO
     *            RequestVO
     * @return void
     */
    @Override
    public void process(RequestVO p_requestVO) {
        Connection con = null;MComConnectionI mcomCon = null;

        /*
         * 1. Check sender out transfer status if the sender is out suspend the
         * request is failed.
         * 2. Check sender transfer profile status if it is suspended request is
         * failed.
         * 3. Check sender commission profile status, it should not be
         * suspended.
         * 4. Validate the request format received from the ChannelReceiver
         * servlet.
         * 5. Check whether the restricted msisdn is allowed for the sender
         * category, if yes check the
         * receiver number in the restricted list of sender ( restricted_msisdns
         * table).
         * 6. Validates the Service [MVD] status for the network, check in
         * NETWORK_SERVICES.
         * 7. Load receiver control limits.
         * 7.1) Search the subscriber in subscriber_control table.If subscriber
         * details not found then mark
         * the transaction as underprocess and insert the subscriber info in
         * subscriber_control table.
         * If subscriber details found and the last transaction status is
         * underprocess throw error else
         * update the subscriber_control table.
         * 8. In processTransfer method generate mvd transfer id & load the
         * product corresponding to the service
         * type.
         * 9. Mark the vouchers as under process.
         * 10.Validate sender avaliable controls [ Check user
         * balances,thresholds ]
         * 11.Check if message gateway type flow is common or thread.
         * 14.1) If flow type is thread then spawn a thread.
         * 12.Construct VOMS validation request & if service type is EVR
         * construct receiver validation
         * request.
         * 12.1) Validate transfer rule & receiver limits.
         * 12.2) If VOMS is not validate throw error else construct productVO &
         * set the serial number
         * in _c2sTransferVO.
         * 12.3) Debit the sender balance & increase sender transfer outcounts.
         * 13. If service type is MVD find from the preferences if PIN is send
         * to retailer or customer.
         * 13.1) Make entry in transaction table.
         * 13.2) Generate SMS to be sending to receiver & send.
         * 13.1.1) Mark voucher consumed.
         * 13.1.2) If voucher status is updated successfully then give
         * diffrential commision.
         * 13.1.3) If voucher status is not updated credit back the sender.
         * 14.4 If pin is send to customer then same as 16.2)
         */

        final String methodName = "process";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Entered for Request ID=" );
         	loggerValue.append(p_requestVO.getRequestID());
         	loggerValue.append(" MSISDN=" );
         	loggerValue.append(p_requestVO.getFilteredMSISDN() );
         	loggerValue.append(" _recValidationFailMessageRequired: ");
         	loggerValue.append(_recValidationFailMessageRequired);
         	loggerValue.append(" _recTopupFailMessageRequired" );
         	loggerValue.append(_recTopupFailMessageRequired);
         	loggerValue.append(" _notAllowedSendMessGatw: ");
         	loggerValue.append(_notAllowedSendMessGatw );
         	loggerValue.append(" ");
            _log.debug(
                methodName, p_requestVO.getRequestIDStr(),loggerValue);
        }

		
		try {
            _requestVO = p_requestVO;
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            TransactionLog.log("", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_REQ,
                PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "Received Request From Receiver", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            _senderLocale = p_requestVO.getSenderLocale();
            _senderNetworkCode = _channelUserVO.getNetworkID();

            // Populatig C2STransferVO from the request VO
            populateVOFromRequest(p_requestVO);
            _requestIDStr = p_requestVO.getRequestIDStr();
            _type = p_requestVO.getType();
            _serviceType = p_requestVO.getServiceType();

            // Checking senders out transfer status, it should not be suspended
            if (PretupsI.YES.equalsIgnoreCase(_channelUserVO.getOutSuspened())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND_MVD);
            }

            // Checking senders transfer profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getTransferProfileStatus())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND_MVD);
            }

            // Checking senders commission profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getCommissionProfileStatus())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_MVD);
            }

            // Getting oracle connection
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            _c2sTransferVO.setCellId(p_requestVO.getCellId());
            _c2sTransferVO.setSwitchId(p_requestVO.getSwitchId());
            
            // Validating user message incoming in the request [Keyword is
            // either MVD]
            _quantityRequested = _operatorUtil.validateMVDRequestFormat(con, _c2sTransferVO, p_requestVO);

            // Block added to avoid decimal amount in credit transfer
            if (!BTSLUtil.isStringIn(_serviceType, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES))) {
            	isDecimalAllowed();
            }
            _receiverLocale = p_requestVO.getReceiverLocale();
            _senderLocale = p_requestVO.getSenderLocale();
            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));

            _receiverVO.setModule(_c2sTransferVO.getModule());
            _receiverVO.setCreatedDate(_currentDate);
            _receiverVO.setLastTransferOn(_currentDate);
            _senderMSISDN = (_channelUserVO.getUserPhoneVO()).getMsisdn();
            _receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
            _c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
            _c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
            _c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
            _c2sTransferVO.setSubService(p_requestVO.getReqSelector());
            _c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
            _receiverSubscriberType = _receiverVO.getSubscriberType();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_senderMSISDN));
            // Added because the same is present in the EVD for VFE, so included in MVD as well
            _c2sTransferVO.setInfo1(p_requestVO.getInfo1());
            _c2sTransferVO.setInfo2(p_requestVO.getInfo2());
            _c2sTransferVO.setInfo3(p_requestVO.getInfo3());
            _c2sTransferVO.setInfo4(p_requestVO.getInfo4());
            _c2sTransferVO.setInfo5(p_requestVO.getInfo5());
            _c2sTransferVO.setInfo6(p_requestVO.getInfo6());
            _c2sTransferVO.setInfo7(p_requestVO.getInfo7());
            _c2sTransferVO.setInfo8(p_requestVO.getInfo8());
            _c2sTransferVO.setInfo9(p_requestVO.getInfo9());
            _c2sTransferVO.setInfo10(p_requestVO.getInfo10());
            // checking whether self voucher distribution is allowed or not 
            // in case of EVD private recharge also it is allowed. So putting into MVD as well.
            if (_senderMSISDN.equals(_receiverMSISDN) && (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SELF_VOUCHER_DISTRIBUTION_ALLOWED))).booleanValue()) && (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) && "1".equals(_c2sTransferVO.getSubService())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SELF_VOUCHER_DIST_NOTALLOWED);
            }
            // Restricted MSISDN check
            // if
            // (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getRestrictedMsisdns()))
            RestrictedSubscriberBL.isRestrictedMsisdnExistForC2S(con, _c2sTransferVO, _channelUserVO, _receiverVO.getMsisdn(),
                _c2sTransferVO.getRequestedAmount() * _quantityRequested);

            // Validates the network service status
            PretupsBL.validateNetworkService(_c2sTransferVO);
            _receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW, _receiverVO.getNetworkCode(), _serviceType))
                .booleanValue();
            // receiver message send should be false if it is for private
            // recharge as in this case there will be no reciever MSISDN
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()  && "1".equals(_c2sTransferVO.getSubService())) {
                _receiverMessageSendReq = false;
            }
            // check if receiver barred in PreTUPS or not, user should not be
            // barred.
            isReceiverBarred(con);            
            /*
             * Loading C2S receiver's control parameters from subscriber_control
             * table
             * If the last trf status of receiver is underprocess then throw
             * error else add
             * the details of receiver in subscriber_control table
             */
            // added by PN(25/03/08) to resolve the issude of duplicate request
            // processing
            _c2sTransferVO.setUnderProcessCheckReqd(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
            PretupsBL.loadRecieverControlLimits(con, p_requestVO.getRequestIDStr(), _c2sTransferVO);
            _receiverVO.setUnmarkRequestStatus(true);

            // commiting transaction after updating receiver's control
            // parameters
            commitConnection(con);

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
                _receiverVO.setLastTransferID(_lastTransferId);

                // making entry in the transaction log
                TransactionLog.log(_transferID + "-" + _lastTransferId, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _channelUserVO.getNetworkID(),
                    PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Generated Transfer ID", PretupsI.TXN_LOG_STATUS_SUCCESS, "Source Type=" + _c2sTransferVO
                        .getSourceType() + " Gateway Code=" + _c2sTransferVO.getRequestGatewayCode());

                //Populate VOMS and IN interface details
                populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION); //done
                
                _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
                
                // This will be used in validate ReceiverLimit method of
                // PretupsBL when receiverTransferItemVO is null
                _c2sTransferVO.setReceiverSubscriberType(_receiverSubscriberType);

                // validate receiver limits before Interface Validations
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                	PretupsBL.validateRecieverLimits(_c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE, _quantityRequested);
                }
               
                // Validate Sender Transaction profile checks and balance
                // availablility for user
                ChannelUserBL.validateSenderAvailableControls(con, _transferID, _c2sTransferVO, _quantityRequested);

                // setting validation status
                _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

                // commiting transaction and closing the transaction as it is
                // not requred
                commitConnection(con);
				if (mcomCon != null) {
					mcomCon.close("MVDController#process");
					mcomCon = null;
				}
				con = null;

                // Checking the Various loads and setting flag to decrease the
                // transaction count
                checkTransactionLoad();
                _decreaseTransactionCounts = true;
                
                (_channelUserVO.getUserPhoneVO()).setLastTransferID(_lastTransferId);
                (_channelUserVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);

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
                    // implmentartion
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
                     final String[] messageArgArray = {
                    		 _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue() * _quantityRequested), PretupsBL
                            .getDisplayAmount(_senderTransferItemVO.getPostBalance()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPreviousBalance()), String
                            .valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), String
                            .valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO
                            .getSubService(),String.valueOf(_quantityRequested) };
                         p_requestVO.setMessageArguments(messageArgArray);
                }
                p_requestVO.setDecreaseLoadCounters(false);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) { // getting database connection if it  is not already there
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
            	loggerValue.append(exceptionMessage);
            	loggerValue.append(bex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
            }

            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            // setting receiver return message
            if (_recValidationFailMessageRequired && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) ) {
            	if (_transferID != null) {
            		_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_MVD, new String[] { String.valueOf(_transferID), PretupsBL
            				.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), String.valueOf(_quantityRequested), _lastTransferId }));
            	} else {
            		_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_MVD, new String[] { String.valueOf(_quantityRequested), PretupsBL
            				.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested) }));
            	}
            }
            // getting return message from the C2StransferVO and setting it to
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
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
            }

            // checking whether need to decrease the transaction load, if it is
            // already increased
            if (_transferID != null && _decreaseTransactionCounts) {
                // decreasing transaction load
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            // making entry in the transaction log
            TransactionLog.log(_transferID + "-" + _lastTransferId, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, messageCodeForTXNlog + p_requestVO.getMessageCode());
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
                loggerValue.setLength(0);
            	loggerValue.append(exceptionMessage);
            	loggerValue.append(bex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode,  loggerValue.toString() );
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
                loggerValue.setLength(0);
            	loggerValue.append(exceptionMessage);
            	loggerValue.append(ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
            }
            // checking condition whether channel receiver required the general
            // failure message
                if (_recValidationFailMessageRequired && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey())) {
                    // setting receiver return message
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_MVD, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), String.valueOf(_quantityRequested), _lastTransferId }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_MVD, new String[] { String.valueOf(_quantityRequested), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested) }));
                    }
                }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
            _log.errorTrace(methodName, e);
            // decreasing transaction load
            if (_transferID != null && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            // raising alarm
            loggerValue.setLength(0);
        	loggerValue.append(exceptionString);
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"["+methodName+"]", _transferID, _senderMSISDN,
                _senderNetworkCode, loggerValue.toString());
            // logging in the transaction log
            TransactionLog.log(_transferID + "-" + _lastTransferId, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, messageCodeForTXNlog + p_requestVO.getMessageCode());
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
                    // added by nilesh: consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        addEntryInTransfers(con);
                    }
                } else if (_transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    _log.info(methodName,p_requestVO.getRequestIDStr(),
                    		"Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + _transferID + "-" + _lastTransferId + " But not added entry in Transfers yet");
                }
               if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
            	loggerValue.append(exceptionString);
            	loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            }
            if (con != null) {// committing transaction and closing connection
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
				if (mcomCon != null) {
					mcomCon.close("MVDController#process");
					mcomCon = null;
				}
                con = null;
            }/// end if

            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (_isCounterDecreased) {
                p_requestVO.setDecreaseLoadCounters(false);
            }
            if (_receiverMessageSendReq && _recValidationFailMessageRequired && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), 
            		_notAllowedRecSendMessGatw) && !"ALL".equals(_notAllowedRecSendMessGatw)) {
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
            // added by nilesh: consolidated for logger
            if (_oneLog) {
                OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
                // making entry in the transaction log
            }

            TransactionLog.log(_transferID + "-" + _lastTransferId, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, messageCodeForTXNlog + p_requestVO.getMessageCode());

            // Changed to Push MVD through SMS if request gateway is SMSC done
            // by ashishT.
                if (!(p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) && (((p_requestVO.getMessageGatewayVO().getGatewayType().equalsIgnoreCase(PretupsI.GATEWAY_TYPE_SMSC)) || (p_requestVO.getMessageGatewayVO().getGatewayType()
                    .equalsIgnoreCase(PretupsI.GATEWAY_TYPE_USSD)) || (p_requestVO.getMessageGatewayVO().getGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_STK))) && (_finalTransferStatusUpdate))) {
                    sendMVDThroughSMS(_requestVO, _quantityRequested);
                }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
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
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue()  ) {
        	
				_c2sTransferVO.setUserLoanVOList(_channelUserVO.getUserLoanVOList());
		
	}
        else if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
        	ArrayList<ChannelSoSVO>  chnlSoSVOList = new ArrayList<> ();
        	chnlSoSVOList.add(new ChannelSoSVO(_channelUserVO.getUserID(),_channelUserVO.getMsisdn(),_channelUserVO.getSosAllowed(),_channelUserVO.getSosAllowedAmount(),_channelUserVO.getSosThresholdLimit()));
        	_c2sTransferVO.setChannelSoSVOList(chnlSoSVOList);
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
        VomsVoucherVO voucherVO = null;
        try {
            // METHOD FOR INSERTING AND UPDATION IN C2S Transfer Table
            // if error comes before loading vouchers then voucher list need to
            // be set.
            if (!_transferDetailAdded && _transferEntryReqd) {
                checkvomsVoucherList(p_con);
            } else if (_transferDetailAdded) {
                _c2sTransferVO.setModifiedOn(new Date());
                _c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                // added by nilesh: consolidated for logger
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    ChannelTransferBL.updateC2STransferDetails(p_con, _c2sTransferVO, _transferIdList);// add
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

	        loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
            counterDecreased();
        
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"["+methodName+"]",
                _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            counterDecreased();
        }
    }

	private void counterDecreased() {
		if (!_isCounterDecreased && _decreaseTransactionCounts) {
		    LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
		    _isCounterDecreased = true;
		}
	}

	private void checkvomsVoucherList(Connection p_con)
			throws BTSLBaseException {
		VomsVoucherVO voucherVO;
		if (_vomsVoucherList == null || _vomsVoucherList.isEmpty()) {
		    _vomsVoucherList = new ArrayList<VomsVoucherVO>();
		    for (int i = 0; i < _quantityRequested; i++) {
		        voucherVO = new VomsVoucherVO();
		        voucherVO.setTransactionID((String) _transferIdList.get(i));
		        _vomsVoucherList.add(voucherVO);
		    }
		    _itemList = new ArrayList<TransferItemVO>();
		    _itemList.add(_senderTransferItemVO);
		    _itemList.add(_receiverTransferItemVO);
		    _c2sTransferVO.setTransferItemList(_itemList);
		}
		// added by nilesh: consolidated for logger
		if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
		    ChannelTransferBL.addC2STransferDetails(p_con, _c2sTransferVO, _vomsVoucherList);// add transfer details in database
		}
	}

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"[processSKeyGen]",
                _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        try {
            // Generating the MVD transfer ID
            _c2sTransferVO.setTransferDate(_currentDate);
            _c2sTransferVO.setTransferDateTime(_currentDate);
            // _transferIdList=PretupsBL.generateMVDTransferID(_c2sTransferVO,_quantityRequested);
            // Forming the transaction id's in the memory
            _transferIdList = generateMVDTransferID(_c2sTransferVO, _quantityRequested);
            transferListString = _transferIdList.stream().collect(Collectors.joining(","));
            _requestVO.setValueObject(_transferIdList);
            _transferID = _c2sTransferVO.getTransferID();
            _lastTransferId = (String) _transferIdList.get(_quantityRequested - 1);
            _c2sTransferVO.setLastTransferId(_lastTransferId);
            _receiverVO.setLastTransferID(_transferID);// vikas: here it might be the _lastTransferId instead of _transferID

            // Set sender transfer item details
            setSenderTransferItemVO();

            // set receiver transfer item details
            setReceiverTransferItemVO();
            _c2sTransferVO.setReceiverTransferItemVO(_receiverTransferItemVO);
            // Get the product Info based on the service type
            PretupsBL.getProductFromServiceType(p_con, _c2sTransferVO, _serviceType, PretupsI.C2S_MODULE);
            _transferEntryReqd = true;

            // Here logic will come for Commission profile for sale center
            if ((_channelUserVO.getCategoryVO()).getDomainTypeCode().equals(PretupsI.DOMAIN_TYPE_SALECENTER)) {
                _senderTransferItemVO.setTransferValue(_c2sTransferVO.getRequestedAmount()* _quantityRequested);
            } else {
                _senderTransferItemVO.setTransferValue(_c2sTransferVO.getTransferValue() * _quantityRequested);
            }

        } catch (BTSLBaseException be) {
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            throw be;
        } catch (Exception e) {
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                if (_transferID != null) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_MVD, new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), String.valueOf(_quantityRequested), _lastTransferId }));
                } else {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_MVD, new String[] { String.valueOf(_quantityRequested), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested) }));
                }
            }
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append( e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"["+methodName+"]",
                _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
            throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
        }
    }

    /**
     * Thread to perform IN related operations
     */
    @Override
    public void run() {
        final String methodName = "run";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
         	loggerValue.append(_transferID);
         	loggerValue.append("-");
         	loggerValue.append(_lastTransferId);
            _log.debug(methodName,  loggerValue , "Entered");
        }
        BTSLMessages btslMessages = null;
        _userBalancesVO = null;
        CommonClient commonClient = null;
        Connection con = null;MComConnectionI mcomCon = null;
        InterfaceVO interfaceVO = null;
        try {
            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !_processedFromQueue) {
                // Processing validation request in Thread
                processValidationRequestInThread();
            }
            /*
             * From here processing will be divided into two parts
             * 2.MVD
             * 2.1 Check to whome PIN is to send
             * 2.2 check delivery receipt is tracked or not
             * 2.3 If delivery is not tracked
             * 2.3.1 Send PIN
             * 2.3.2 If status from kannel is sent then update voucher status to
             * consume and give differentils
             * 2.3.3 If voucher is not updated to consume properly then credit
             * back sender and mark voucher to enable
             * 2.3.4 If status is not sent then update voucher status to enable
             * 2.4 If delivery is tracked
             * 2.4.1 Send PIN
             * 2.4.2 If delivery is received then mark status of voucher to
             * consume and make transaction as success
             * 2.4.3 If voucher is not properly updated to consume then credit
             * back the sender and mark voucher to enable
             * 2.4.4 If delivery is not received then credit back the sender and
             * mark voucher to enable
             */
            // perform step 2 of comments above
            _vomsVO.setPinNo(VomsUtil.encryptText(_vomsVO.getPinNo()));
            // perform step 2 of comments above
            _requestVO.setEvdPin(VomsUtil.decryptText(_vomsVO.getPinNo()));
            sendSMS(_vomsVO);
        }// end try
        catch (BTSLBaseException be) {
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
                }
            }// end if
            if (be.isKey() && _c2sTransferVO.getSenderReturnMessage() == null) {
                btslMessages = be.getBtslMessages();
            } else if (_c2sTransferVO.getSenderReturnMessage() == null) {
                _c2sTransferVO.setSenderReturnMessage(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Error Code:");
            	loggerValue.append(_c2sTransferVO.getErrorCode());
                _log.debug(methodName, _transferID + "-" + _lastTransferId,  loggerValue);
            }
            _log.errorTrace(methodName, be);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
        }// end catch BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
            }
            loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"[run]",
                _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            btslMessages = new BTSLMessages(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
        }// end catch Exception
        finally {
            try {
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO
                    .getReceiverReturnMsg()).isKey())) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_MVD, new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), String.valueOf(_quantityRequested), _lastTransferId }));
                    // decreasing transaction load count
                }

                mcomCon = new MComConnection();con=mcomCon.getConnection();
                // Unmarking the receiver transaction status
                // In case of delivery tracking receiver is unmarked in delivery
                // receipt servlet
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
                }
            }// end try
            catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
                loggerValue.setLength(0);
            	loggerValue.append("Exception while updating Receiver last request status in database , Exception:");
            	loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"[run]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode,
                    loggerValue.toString() );
            }// end catch

            try {
                if (_finalTransferStatusUpdate) {
                    // Setting modified on and by
                    _c2sTransferVO.setModifiedOn(new Date());
                    _c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    // Updating C2S Transfer details in database
                    // added by nilesh: consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        ChannelTransferBL.updateC2STransferDetails(con, _c2sTransferVO, _transferIdList);
                    }
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
            	loggerValue.append("Exception while updating transfer details in database , Exception:");
            	loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MVDController[run]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
            }
            // if connection is not null then comitting the transaction and
            // closing the connection
            if (con != null) {
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
				}
				if (mcomCon != null) {
					mcomCon.close("MVDController#run");
					mcomCon = null;
				}
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

            if (_receiverMessageSendReq) {
                if (_recTopupFailMessageRequired && _c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
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

            // Message to sender will be send only when request gateway code is
            // allowed to send message

            // Log the credit back entry in the balance log
            if (_creditBackEntryDone) {
                BalanceLogger.log(_userBalancesVO);
            }
            // added by nilesh : consolidated for logger
            if (!_oneLog) {
                OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
            }
            TransactionLog.log(_transferID + "-" + _lastTransferId, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS, "Message=" + _c2sTransferVO.getSenderReturnMessage());

            btslMessages = null;
            _userBalancesVO = null;
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(_transferID);
            	loggerValue.append("-");
            	loggerValue.append(_lastTransferId);
                _log.debug(methodName,  loggerValue, "Exiting");
            }
        }// end of finally
    }

    /**
     * Method that will perform the validation request in thread
     * 
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequestInThread() throws Exception {
        final String methodName = "processValidationRequestInThread";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {

	        loggerValue.setLength(0);
        	loggerValue.append("Entered and performing validations for transfer ID=" );
        	loggerValue.append(_transferID);
        	loggerValue.append("-" );
        	loggerValue.append(_lastTransferId);
            _log.debug(methodName,loggerValue);
        }
        try {
            TransactionLog.log(_transferID + "-" + _lastTransferId, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            processValidationRequest();
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("Getting BTSL Base Exception:");
        	loggerValue.append( be.getMessage());
            _log.error(controllerName+"["+methodName+"]",  loggerValue);
            TransactionLog.log(_transferID + "-" + _lastTransferId, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, messageCodeForTXNlog + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
                    .getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested) }));
            }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"[processValidationRequestInThread]",
                _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
            TransactionLog.log(_transferID + "-" + _lastTransferId, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            // !_transferDetailAdded Condition Added as we think its not require
            // as already done
            if (_transferID != null && !_transferDetailAdded) {
				Connection con = null;
				MComConnectionI mcomCon = null;
                try {
					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
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
                } catch (Exception e) {
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (Exception ex) {
                            _log.errorTrace(methodName, ex);
                        }
					}
					if (mcomCon != null) {
						mcomCon.close("MVDController#processValidationRequestInThread");
						mcomCon = null;
					}
                    _log.errorTrace(methodName, e);
                    loggerValue.setLength(0);
                	loggerValue.append("Exception:" );
                	loggerValue.append(e.getMessage());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    		controllerName+"[processValidationRequestInThread]", _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
                } finally {
					if (mcomCon != null) {
						mcomCon.close("MVDController#processValidationRequestInThread");
						mcomCon = null;
					}
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
        _senderTransferItemVO.setRequestValue(_c2sTransferVO.getRequestedAmount()* _quantityRequested);
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
        _receiverTransferItemVO.setRequestValue(_c2sTransferVO.getRequestedAmount()* _quantityRequested);
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
     * Method to do the validation of the receiver and perform the steps before
     * the topup stage
     * 
     * @param p_con
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequest() throws Exception {
        Connection con = null;MComConnectionI mcomCon = null;
        InterfaceVO interfaceVO = null;
        EvdUtil evdUtil = null;
        final String methodName = "processValidationRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                "Entered and performing validations for transfer ID=" + _transferID + "-" + _lastTransferId + " " + _c2sTransferVO.getModule() + " " + _c2sTransferVO
                    .getReceiverNetworkCode() + " " + _type);
        }

        try {
            final CommonClient commonClient = new CommonClient();
            InterfaceVO recInterfaceVO = null;
            _itemList = new ArrayList();
            _itemList.add(_senderTransferItemVO);
            _itemList.add(_receiverTransferItemVO);
            _c2sTransferVO.setTransferItemList(_itemList);
            
         // Till here we get the IN interface validation response.. if the
            // service is EVR
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Get the service Class ID based on the code
            PretupsBL.validateServiceClassChecks(con, _receiverTransferItemVO, _c2sTransferVO, PretupsI.C2S_MODULE, _requestVO.getServiceType());

            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());

            // validate sender receiver service class,validate transfer value
            PretupsBL.validateTransferRule(con, _c2sTransferVO, PretupsI.C2S_MODULE);

                if (_receiverTransferItemVO.getPreviousExpiry() == null) {
                    _receiverTransferItemVO.setPreviousExpiry(_currentDate);
                }

            // calculate card group details
            CardGroupBL.calculateCardGroupDetails(con, _c2sTransferVO, PretupsI.C2S_MODULE, true);

            commitConnection(con);
			if (mcomCon != null) {
				mcomCon.close("MVDController#processValidationRequest");
				mcomCon = null;
			}
            con = null;

            // ***Construct & validate VOMS validation request using common
            // client*************
            _networkInterfaceModuleVO = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_c2sTransferVO.getModule(), _c2sTransferVO.getReceiverNetworkCode(),
                PretupsI.INTERFACE_CATEGORY_VOMS);
            evdUtil = new EvdUtil();
            interfaceVO = new InterfaceVO();
            interfaceVO.setInterfaceId(_senderTransferItemVO.getInterfaceID());
            interfaceVO.setHandlerClass(_senderTransferItemVO.getInterfaceHandlerClass());
            final String requestStr = getVOMSUpdateRequestStr(PretupsI.INTERFACE_VALIDATE_ACTION, _c2sTransferVO, _networkInterfaceModuleVO, interfaceVO,
                VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_ENABLE);
            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            final String receiverValResponse = commonClient.process(requestStr, _transferID, _networkInterfaceModuleVO.getCommunicationType(), _networkInterfaceModuleVO
                .getIP(), _networkInterfaceModuleVO.getPort(), _networkInterfaceModuleVO.getClassName());
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, _transferID, "Got the validation response from VOMS Handler receiverValResponse=" + receiverValResponse);
            }
            _itemList = new ArrayList();
            _itemList.add(_senderTransferItemVO);
            _itemList.add(_receiverTransferItemVO);
            _c2sTransferVO.setTransferItemList(_itemList);

            try {
                updateForVOMSValidationResponse(receiverValResponse);
                VomsVoucherChangeStatusLog.log(_transferID + "-" + _lastTransferId, _vomsVO.getSerialNo(),
                VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_UNPROCESS, _c2sTransferVO.getReceiverNetworkCode(), _channelUserVO.getUserID(), BTSLUtil
                            .getDateTimeStringFromDate(_currentDate));
                
                
            } catch (BTSLBaseException be) {
                LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "inside catch of BTSL Base Exception: " + be.getMessage() + " _vomsInterfaceInfoInDBFound: " + _vomsInterfaceInfoInDBFound);
                }
                if (_vomsInterfaceInfoInDBFound && _senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
                    PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, PretupsI.INTERFACE_CATEGORY_VOMS);
                }

                // validate receiver limits after Interface Validations
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                	PretupsBL.validateRecieverLimits(_c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE, _quantityRequested);
                }
                throw new BTSLBaseException(controllerName, "updateForReceiverValidateResponse", be.getMessageKey(), 0, be.getArgs(), null);
                //throw new BTSLBaseException(be);
            } catch (Exception e) {
                LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

                // validate receiver limits after Interface Validations
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                	PretupsBL.validateRecieverLimits(_c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE, _quantityRequested);
                }
                throw new BTSLBaseException(e);
            }
            _voucherMarked = true;

            LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

            // If request is taking more time till validation of subscriber than
            // reject the request.
            InterfaceVO vomsInterfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(interfaceVO.getInterfaceId());
            if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > vomsInterfaceVO.getValExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, controllerName+"[processValidationRequest]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till validation of voucher");
                throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
            }
            vomsInterfaceVO = null;

            // This method will set various values into items and transferVO
            evdUtil.calculateTransferValue(_c2sTransferVO, (VomsVoucherVO) _vomsVoucherList.get(0), _quantityRequested);
            
            TransactionLog.log(_transferID + "-" + _lastTransferId,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,                PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Validity=" + _c2sTransferVO.getReceiverValidity() + " Talk Time=" + _c2sTransferVO.getReceiverTransferValue() + " Serial number=" + 
                		_vomsVO.getSerialNo(), 
            PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
            }

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            
            // setting the total amount to be deducted from the sender's account
            final long amt = ((C2STransferItemVO) _c2sTransferVO.getTransferItemList().get(0)).getTransferValue() * _quantityRequested;
            ((C2STransferItemVO) _c2sTransferVO.getTransferItemList().get(0)).setTransferValue(amt);
            // Here the code for debiting the user account will come and Update
            // Transfer Out Counts for the sender
            _userBalancesVO = ChannelUserBL.debitUserBalanceForProduct(con, _transferID, _c2sTransferVO);
            
            // revreting back the amount to that of one transaction
            ((C2STransferItemVO) _c2sTransferVO.getTransferItemList().get(0)).setTransferValue(amt / _quantityRequested);
            
            ChannelTransferBL.increaseC2STransferOutCounts(con, _c2sTransferVO, true, _quantityRequested);

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

            populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            if (PretupsI.SERVICE_TYPE_MVD.equals(_c2sTransferVO.getServiceType())) {
                _receiverTransferItemVO.setServiceClass(_vomsAllServiceClassID);
                final String pinSendTo = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_SEND_TO));
                // Construct the PIN message for sender or receiver as the case
                // is
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "PIN sent to in preference=" + pinSendTo);
                }
                // changed for EVD private recharge (as subservice =1 )
                if (PretupsI.PIN_SENT_RET.equals(pinSendTo)) {
                    _c2sTransferVO.setPinSentToMsisdn(_senderMSISDN);
                } else {
                    _c2sTransferVO.setPinSentToMsisdn(_receiverMSISDN);
                }
            }
            _senderTransferItemVO.setServiceClass(_vomsAllServiceClassID);
            // Method to insert the record in c2s transfer table
            // added by nilesh: consolidated for logger
            if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
            	 if (_log.isDebugEnabled()) {
                     _log.debug(methodName, "_vomsVoucherList Size=" + _vomsVoucherList.size());
                 }
                ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO, _vomsVoucherList);
            }
            _transferDetailAdded = true;
            // Commit the transaction and relaease the locks
			commitConnection(con);
			if (mcomCon != null) {
				mcomCon.close("MVDController#processValidationRequest");
				mcomCon = null;
			}
			con = null;

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Marked Under process, voucher Serial number=" + _vomsVO.getSerialNo()
                , PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");

            // Log the details if the transfer Details were added i.e. if User
            // was debitted
            if (_transferDetailAdded) {
                BalanceLogger.log(_userBalancesVO);
            }

            if(PretupsI.SERVICE_TYPE_NMVD.equals(_requestVO.getServiceType()) || PretupsI.SERVICE_TYPE_MVD.equals(_requestVO.getServiceType()))
            {
            	try
                {
                    _finalTransferStatusUpdate=evdUtil.updateVoucherAndGiveDifferentials(_receiverVO,_c2sTransferVO,_requestVO.getInstanceID(),_quantityRequested,new ArrayList<VomsVoucherVO>(_vomsVoucherList));
                }
                catch(BTSLBaseException be)
                {
                    throw be;
                }
                catch(Exception e)
                {
                    throw (BTSLBaseException)e;
                }
            	_requestVO.setValueObject((ArrayList)_vomsVoucherList);
            }
            // Push Under Process Message to Sender and Reciever , this might
            // have to be implemented on flag basis whether to send message or
            // not
            if (_c2sTransferVO.isUnderProcessMsgReq() && _receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
                .equals(_notAllowedRecSendMessGatw)) {
                (new PushMessage(_receiverMSISDN, getReceiverUnderProcessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
            }

            // If request is taking more time till validation of subscriber than
            // reject the request.
            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue) {
                // create new Thread
                final Thread _controllerThread = new Thread(this);
                _controllerThread.start();
                _oneLog = false;
            }
            
        } catch (BTSLBaseException be) {
            if (con != null) {
                con.rollback();
            }
            con = null;
                if (_recValidationFailMessageRequired && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey())) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested) }));
                }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
                }
            }
            _log.error(controllerName+"[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());

            voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
            throw new BTSLBaseException(controllerName, "processValidationRequest", be.getMessageKey(), 0, be.getArgs(), null);
            //throw new BTSLBaseException(be);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (con != null) {
                con.rollback();
            }
            con = null;
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                if (_recValidationFailMessageRequired && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey())) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_MVD, new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested) }));
                }
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

            throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("MVDController#processValidationRequest");
				mcomCon = null;
			}
			con = null;
        }
            

    }

    /** updateForVOMSValidationResponse
     * Method to process the response of the receiver validation from VOMS
     * 
     * @param str
     * @throws BTSLBaseException
     */

    public void updateForVOMSValidationResponse(String str) throws BTSLBaseException {
        final String METHOD_NAME = "updateForVOMSValidationResponse";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered");
        }
        final HashMap<String,String> map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        _senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        _receiverVO.setInterfaceResponseCode(_senderTransferItemVO.getInterfaceResponseCode());
        _senderTransferItemVO.setValidationStatus(status);
        _senderTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));

        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
       
        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            _c2sTransferVO.setErrorCode(status + "_S");
            _senderTransferItemVO.setTransferStatus(status);
            if (PretupsI.SERVICE_TYPE_MVD.equals(_serviceType)) {
                _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                _receiverTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            }
            String[] strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested) };
            throw new BTSLBaseException(controllerName, "updateForReceiverValidateResponse", _c2sTransferVO.getErrorCode(), 0, strArr, null);
        }
        _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        _senderTransferItemVO.setTransferStatus(status);
        _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        // Set the service class received from the IN.
        if (_log.isDebugEnabled()) {loggerValue.setLength(0);
        	loggerValue.append("SERVICE_CLASS=");
        	loggerValue.append((String) map.get("SERVICE_CLASS"));
            _log.debug(METHOD_NAME, loggerValue );
        }
        _receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        
        if ("null".equals(map.get("SERIAL_NUMBER"))) {
            throw new BTSLBaseException(controllerName, "updateForReceiverValidateResponse", PretupsErrorCodesI.VOUCHER_NOT_FOUND);
        }
        mapOfSerialAndPIN = new HashMap();
//        mapOfSerialAndPIN = map;
        mapOfSerialAndPIN.put("SERIAL_NUMBER",((String)map.get("SERIAL_NUMBER")).replace("%2C", ",").toString());
        mapOfSerialAndPIN.put("PIN",((String)map.get("PIN")).replace("%2C", ",").toString());
        
        String[] serials = ((String)map.get("SERIAL_NUMBER")).split("%2C"); 
        String[] pins = ((String)map.get("PIN")).split("%2C");
        if (_log.isDebugEnabled()) {
        	
        	int i =1; 
        	for(String k : serials){
        		_log.debug(METHOD_NAME, ""
        				+ "count="+i+" serial=" + k );
        		i++;
        	}
        	i=1;
        	for(String k : pins){
        		_log.debug(METHOD_NAME, ""
        				+ "count="+i+" pin=" + k );
        		i++;
        	}
        }
        _vomsVoucherList = new ArrayList<VomsVoucherVO>();
        for(int i= 0 ;i <_quantityRequested;i++){
        	_vomsVO = new VomsVoucherVO();
        	_vomsVO.setProductID((String) map.get("PRODUCT_ID"));
        	_vomsVO.setSerialNo((String) map.get("SERIAL_NUMBER"));
        	_vomsVO.setTalkTime(Long.parseLong((String) map.get("TALK_TIME")));
        	_vomsVO.setValidity(Integer.parseInt((String) map.get("VALIDITY")));
        	_vomsVO.setPinNo((String) pins[i]);	//settting SerialNo
        	_vomsVO.setTransactionID((String)_transferIdList.get(i));//Setting TXN ID
        	_vomsVO.setSerialNo((String) serials[i]);//Setting serial no
        	_vomsVO.setTalkTime(Long.parseLong((String)map.get("TALK_TIME")));
        	_vomsVO.setValidity(Integer.parseInt((String)map.get("VALIDITY")));
        	_vomsVO.setCurrentStatus((String)map.get("State"));
        	_vomsVO.setUserLocationCode((String)map.get("NETWORK_CODE"));
        	_vomsVoucherList.add(_vomsVO);
        } 

        try {
            final Date expDate = BTSLUtil.getDateFromDateString((String) map.get("VOUCHER_EXPIRY_DATE"), "yyyy-MM-dd");
            _vomsVO.setExpiryDateStr(BTSLUtil.getDateStringFromDate(expDate));
            _vomsVO.setExpiryDate(expDate);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        _c2sTransferVO.setSerialNumber((String) map.get("SERIAL_NUMBER"));
        _senderTransferItemVO.setTransferValue(Long.parseLong((String) map.get("PAYABLE_AMT")));
        final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
            .getRoutingControlDetails(_c2sTransferVO.getReceiverNetworkCode() + "_" + _c2sTransferVO.getServiceType() + "_" + PretupsI.INTERFACE_CATEGORY_VOMS);
        if (!_vomsInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
            PretupsBL.insertSubscriberInterfaceRouting(_senderTransferItemVO.getInterfaceID(), _vomsExternalID, _receiverMSISDN, PretupsI.INTERFACE_CATEGORY_VOMS,
                _channelUserVO.getUserID(), _currentDate);
            _vomsInterfaceInfoInDBFound = true;
        }

        if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue()) {
            final String payAmt = (String) map.get("RECEIVER_PAYABLE_AMT");
            if (!BTSLUtil.isNullString(payAmt) && BTSLUtil.isNumeric(payAmt)) {
                _payableAmt = PretupsBL.getDisplayAmount(Long.parseLong(payAmt));
            }
        }
    }
    /**
     * Method to get the success message to be sent to sender
     * 
     * @return
     */
    private String getSenderUnderProcessMessage() {
        final String[] messageArgArray = { _requestVO.getSid(), _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), String
            .valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()), String.valueOf(_quantityRequested), _lastTransferId };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.MVD_C2S_SENDER_UNDERPROCESS, messageArgArray);
    }

    private String getReceiverAmbigousMessage() {
        final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), _senderMSISDN, _channelUserVO.getUserName(), _lastTransferId, String
            .valueOf(_quantityRequested) };
        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_AMBIGOUS_KEY_MVD, messageArgArray, _requestVO.getRequestGatewayType());
    }

    private String getReceiverFailMessage() {
        final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), _senderMSISDN, _channelUserVO.getUserName(), _lastTransferId, String
            .valueOf(_quantityRequested) };
        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY_MVD, messageArgArray, _requestVO.getRequestGatewayType());
    }

    /**
     * Method to get the under process message before validation to be sent to
     * sender
     * 
     * @return
     */
    private String getSndrUPMsgBeforeValidation() {
        final String[] messageArgArray = { _requestVO.getSid(), _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), String
            .valueOf(_quantityRequested), _lastTransferId };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS_B4VAL_MVD, messageArgArray);
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

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            // Loading C2S receiver's controll parameters
            // added by PN(25/03/08) to resolve the issude of duplicate request
            // processing
            _c2sTransferVO.setUnderProcessCheckReqd(_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
            PretupsBL.loadRecieverControlLimits(con, _requestIDStr, _c2sTransferVO);
            _receiverVO.setUnmarkRequestStatus(true);
			commitConnection(con);
			if (mcomCon != null) {
				mcomCon.close("MVDController#processFromQueue");
				mcomCon = null;
			}
            con = null;

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( "_transferID=");
            	loggerValue.append(_transferID); 
            	loggerValue.append("-" );
            	loggerValue.append(_lastTransferId); 
            	loggerValue.append(" Successfully through load");
                _log.debug(controllerName+"["+methodName+"]", loggerValue );
            }
            _processedFromQueue = true;

            processValidationRequest();
            // Set under process message for the sender and reciever
            p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
            final String[] messageArgArray = { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount() * _quantityRequested) };
            p_transferVO.setMessageArguments(messageArgArray);
        } catch (BTSLBaseException be) {
			if (mcomCon != null) {
				mcomCon.close("MVDController#processFromQueue");
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
                loggerValue.setLength(0);
            	loggerValue.append(exceptionMessage);
            	loggerValue.append( bex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
            	loggerValue.append(exceptionMessage);
            	loggerValue.append( e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
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
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
            }

            // checking whether need to decrease the transaction load, if it is
            // already increased
            
              LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
              _isCounterDecreased=true;
             // making entry in the transaction log
            TransactionLog.log(_transferID + "-" + _lastTransferId, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, messageCodeForTXNlog + _requestVO.getMessageCode());
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
			if (mcomCon != null) {
				mcomCon.close("MVDController#processFromQueue");
				mcomCon = null;
			}
            con = null;
            _log.errorTrace(methodName, e);
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                loggerValue.setLength(0);
            	loggerValue.append(exceptionMessage);
            	loggerValue.append(bex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
                loggerValue.setLength(0);
            	loggerValue.append("Leaving Reciever Unmarked Exception:" );
            	loggerValue.append(ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            }
            // checking condition whether channel receiver required the general
            // failure message
                // if receivermessage is null or it is not key
                if (_recValidationFailMessageRequired && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey())) {
                    // setting receiver return message
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_MVD, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), String.valueOf(_quantityRequested), _lastTransferId }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_MVD, new String[] { String.valueOf(_quantityRequested), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested) }));
                    }
                }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
            _requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
            _c2sTransferVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);

            _log.errorTrace(methodName, e);

            // decreasing the transaction load count
            
             LoadController.decreaseTransactionLoad(_transferID,_senderNetworkCode,LoadControllerI.DEC_LAST_TRANS_COUNT);
             _isCounterDecreased=true;
             
            // raising alarm
             loggerValue.setLength(0);
         	loggerValue.append("Exception:" );
         	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"["+methodName+"]",
                _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
            // logging in the transaction log
            TransactionLog.log(_transferID + "-" + _lastTransferId, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, messageCodeForTXNlog + _requestVO.getMessageCode());
        } finally {
            try {
                if (mcomCon == null) {
                	mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
                // makking entry in the transfer table if transfer entry has not
                // been made and message gateway flow is common, i.e. validation
                // is not in thread
                if (_transferID != null && !_transferDetailAdded&&!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    // added by nilesh:consolidated for logger
                   
                        addEntryInTransfers(con);
                    
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
            	loggerValue.append("Exception:");
            	loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"["+methodName+"]",
                    _transferID + "-" + _lastTransferId, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
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
				if (mcomCon != null) {
					mcomCon.close("MVDController#processFromQueue");
					mcomCon = null;
				}
                con = null;
            }// end if

            if (_receiverMessageSendReq) {
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
            TransactionLog.log(_transferID + "-" + _lastTransferId, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, messageCodeForTXNlog + _requestVO.getMessageCode());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    /**
     * Method :updateSenderForFailedTransaction
     * This method will make credit back the sender
     * 
     * @param p_con
     * @param p_transferVO
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void updateSenderForFailedTransaction(Connection p_con, C2STransferVO p_transferVO) throws  Exception {
        final String methodName = "updateSenderForFailedTransaction";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered for p_transferVO= " + p_transferVO);
        }
        try {
            ChannelTransferBL.decreaseC2STransferOutCounts(p_con, p_transferVO, _quantityRequested);
            _creditBackEntryDone = true;
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), p_transferVO.getSenderNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Done to sender", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (Exception be) {
            _log.errorTrace(methodName, be);
            _finalTransferStatusUpdate = false;
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"[updateSenderForFailedTransaction]", "", "",
                "", "Error while credit back the retailer Exception: " + be.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exited for _finalTransferStatusUpdate= " + _finalTransferStatusUpdate);
            }
        }
    }

    /**
     * Generates the Transfer ID For MVD
     * 
     * @param p_transferVO
     * @param p_quantityRequested
     * @throws BTSLBaseException
     * @returns transferIDList
     */
    public static synchronized ArrayList<String> generateMVDTransferID(TransferVO p_transferVO, int p_quantityRequested) throws BTSLBaseException {
        final String methodName = "generateMVDTransferID";
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
                transferID = _operatorUtil.formatMVDTransferID(p_transferVO, _transactionIDCounter);
                transferIDList.add(transferID);
            }
            // setting the last transfer id
            p_transferVO.setTransferID((String) transferIDList.get(0));
            return transferIDList;
        }
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException("MVDController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
        } finally {
             if(_log.isDebugEnabled() )
             _log.debug(methodName,"Exiting ");
        }
    }

    /**
     * To send the MVD through SMS
     * 
     * @param p_requestVO
     * @param con
     * @param p_quantityRequired
     * @author ashish Todia
     */
    private void sendMVDThroughSMS(RequestVO p_requestVO, int p_quantityRequired) {
        final String methodName = "sendMVDThroughSMS";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, p_requestVO.getRequestIDStr(), "Entered  _vomsVoucherList.size()" + _vomsVoucherList.size() + "p_quantityRequired =" + p_quantityRequired);
        }
        VomsVoucherVO vomsVoucherVo = null;
        String voucherlist = "";
        final String seperator = ",";
        try {
            final String[] arr = new String[4];
            if (_finalTransferStatusUpdate) {
                for (int i = 0; i < p_quantityRequired; i++) {
                    vomsVoucherVo = (VomsVoucherVO) _vomsVoucherList.get(i);
                    voucherlist = voucherlist + vomsVoucherVo.getPinNo() + seperator;
                }

                arr[0] = Integer.toString(p_quantityRequired);
                arr[1] = PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue() * _quantityRequested);
                arr[2] = p_requestVO.getTransactionID();
                arr[3] = mapOfSerialAndPIN.get("PIN");
                final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.MVD_DOWNLOAD_MSG, arr);
                final PushMessage pushMessage = new PushMessage(_senderMSISDN, btslMessage, null, null, _senderLocale, _senderNetworkCode);
                pushMessage.push();
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, p_requestVO.getRequestIDStr(), "Exited :");
            }
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
        boolean isVOMSFound = false;

        if ((!_vomsInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
            .equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
            isVOMSFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, receiverPrefixID, _receiverVO.getSubscriberType(), 
            		receiverNetworkCode, _c2sTransferVO.getServiceType(), _type, PretupsI.USER_TYPE_RECEIVER, action);
        } else {
            isVOMSFound = true;
        }
        if (!isVOMSFound) {
            throw new BTSLBaseException(controllerName, "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.VOMS_INTERFACE_NOT_FOUND);
        }

    }
    /**
     * Method getInterfaceRoutingDetails
     * This method is used to get the interface details based on the parameters
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_msisdn
     * @param String
     *            p_prefixID
     * @param String
     *            p_subscriberType
     * @param String
     *            p_networkCode
     * @param String
     *            p_serviceType : MVD/EVR
     * @param String
     *            p_interfaceCategory: VOMS
     * @param String
     *            p_userType: RECEIVER ONLY
     * @param String
     *            p_action: VALIDATE OR UPDATE
     * @return boolean isSuccess
     */

    private boolean getInterfaceRoutingDetails(Connection p_con, String p_msisdn, long p_prefixID, String p_subscriberType, String p_networkCode, String p_serviceType, 
    		String p_interfaceCategory, String p_userType, String p_action) throws BTSLBaseException {
        final String methodName = "getInterfaceRoutingDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	 loggerValue.setLength(0);
         	loggerValue.append( " Entered with MSISDN=" );
         	loggerValue.append(p_msisdn);
         	loggerValue.append(" Prefix ID=");
         	loggerValue.append(p_prefixID);
         	loggerValue.append(" p_subscriberType=");
         	loggerValue.append(p_subscriberType);
         	loggerValue.append(" p_networkCode=");
         	loggerValue.append(p_networkCode);
         	loggerValue.append( " p_serviceType=" );
         	loggerValue.append(p_serviceType);
         	loggerValue.append( " p_interfaceCategory=" );
         	loggerValue.append(p_interfaceCategory);
         	loggerValue.append(" p_userType=" );
         	loggerValue.append(p_userType);
         	loggerValue.append(" p_action=" );
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
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
        try {
            if (!_onlyForEvr) {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                    .getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType() + "_" + _requestVO.getType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("For =");
                    	loggerValue.append(_receiverVO.getNetworkCode());
                    	loggerValue.append("_");
                    	loggerValue.append(_requestVO.getServiceType());
                    	loggerValue.append(" Got Interface Category=");
                    	loggerValue.append(_serviceInterfaceRoutingVO.getInterfaceType());
                    	loggerValue.append(" Alternate Check Required=" );
                    	loggerValue.append(_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool());
                    	loggerValue.append(" Alternate Interface=");
                    	loggerValue.append( _serviceInterfaceRoutingVO.getAlternateInterfaceType());
                        _log.debug("process", _requestIDStr,loggerValue);
                    }

                    p_interfaceCategory = _serviceInterfaceRoutingVO.getInterfaceType();
                    if (!_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool()) {
                        throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.VOUCHER_TO_BE_SENT_INTERFACE_NOT_DEFINED);
                    }

                    final RoutingControlDAO routingControlDAO = new RoutingControlDAO();
                    final ArrayList routingControlList = routingControlDAO.loadRoutingControlDetailsList(p_con);

                    // alternate interface type should be defined in
                    // routing_control table
                    boolean inerfaceFound = false;
                    final Iterator iterator = routingControlList.iterator();
                    while (iterator.hasNext()) {
                        subscriberRoutingControlVO = (SubscriberRoutingControlVO) iterator.next();
                        if (subscriberRoutingControlVO.getInterfaceCategory()
                        		.equals(_serviceInterfaceRoutingVO.getAlternateInterfaceType()) && PretupsI.SERVICE_TYPE_EVR
                            .equals(subscriberRoutingControlVO.getServiceType())) {
                            inerfaceFound = true;
                            break;
                        }
                    }

                    if (!inerfaceFound) {
                        throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.VOUCHER_TO_BE_SENT_INTERFACE_NOT_DEFINED);
                    }

                } else {
                    p_interfaceCategory = PretupsI.INTERFACE_CATEGORY_VOMS;
                    _log.info("process", _requestVO.getRequestIDStr(), "Service Interface Routing control Not defined");
                }

                subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
            } else {
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append( "For =");
                    	loggerValue.append(_receiverVO.getNetworkCode() );
                    	loggerValue.append("_");
                    	loggerValue.append(_requestVO.getServiceType() );
                    	loggerValue.append(" Got Interface Category=");
                    	loggerValue.append(_serviceInterfaceRoutingVO.getInterfaceType());
                    	loggerValue.append(" Alternate Check Required=");
                    	loggerValue.append(_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() );
                    	loggerValue.append( " Alternate Interface=" );
                    	loggerValue.append(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
                        _log.debug("process",_requestIDStr,loggerValue );
                    }

                    p_interfaceCategory = _serviceInterfaceRoutingVO.getAlternateInterfaceType();
                } else {
                	loggerValue.setLength(0);
                	loggerValue.append( "Service Interface Routing control Not defined, thus using default type=");
                	loggerValue.append( ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                	
                    _log.info("process", _requestVO.getRequestIDStr(),loggerValue);
                    loggerValue.setLength(0);
                	loggerValue.append("Service Interface Routing control Not defined, thus using default type=");
                	loggerValue.append(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SEnquiryHandler[process]", "", "", "",
                    		loggerValue.toString());
                    // p_interfaceCategory=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                }

                subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
            }

            //
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(" subscriberRoutingControlVO=");
            	loggerValue.append(subscriberRoutingControlVO);
                _log.debug(methodName,  loggerValue);
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
                    // added by rahul.d to check service selector based check
                    // loading of interface
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
                } else {
                    isSuccess = false;
                }
            } else {
                // This event is raised by ankit Z on date 3/8/06 for case when
                // entry not found in routing control and considering series
                // based routing
            	
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"[getInterfaceRoutingDetails]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Routing control information not defined so performing series based routing");
                // added by rahul.d to check service selector based check
                // loading of interface
                ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                // if preference is true load service slector based mapping
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
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:");
        	loggerValue.append( e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"[getInterfaceRoutingDetails]",
                _transferID, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
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
     * Method to set the interface Details
     * 
     * @param p_prefixID
     * @param p_interfaceCategory
     * @param p_action
     * @param p_listValueVO
     * @param p_useInterfacePrefixVO
     * @param p_MSISDNPrefixInterfaceMappingVO
     * @throws BTSLBaseException
     */
    private void setInterfaceDetails(long p_prefixID, String p_interfaceCategory, String p_action, ListValueVO p_listValueVO, boolean p_useInterfacePrefixVO, MSISDNPrefixInterfaceMappingVO p_MSISDNPrefixInterfaceMappingVO, ServiceSelectorInterfaceMappingVO p_serviceSelectorInterfaceMappingVO) throws BTSLBaseException {
        final String methodName = "setInterfaceDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append( " Entered p_prefixID=" );
        	loggerValue.append(p_prefixID);
        	loggerValue.append(" p_action= ");
        	loggerValue.append(p_action);
        	loggerValue.append(" p_interfaceCategory= ");
        	loggerValue.append( p_interfaceCategory);
        	loggerValue.append(" p_listValueVO= ");
        	loggerValue.append(p_listValueVO);
        	loggerValue.append(" p_useInterfacePrefixVO= ");
        	loggerValue.append(p_useInterfacePrefixVO);
        	loggerValue.append(" p_MSISDNPrefixInterfaceMappingVO=");
        	loggerValue.append(p_MSISDNPrefixInterfaceMappingVO);
            _log.debug( methodName, _requestIDStr,loggerValue );
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
            String interfaceStatusTy = null;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue() && p_serviceSelectorInterfaceMappingVO != null) {
                interfaceID = p_serviceSelectorInterfaceMappingVO.getInterfaceID();
                interfaceHandlerClass = p_serviceSelectorInterfaceMappingVO.getHandlerClass();
                underProcessMsgReqd = p_serviceSelectorInterfaceMappingVO.getUnderProcessMsgRequired();
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
                underProcessMsgReqd = p_MSISDNPrefixInterfaceMappingVO.getUnderProcessMsgRequired();
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
                underProcessMsgReqd = p_listValueVO.getType();
                status = p_listValueVO.getStatus();
                message1 = p_listValueVO.getOtherInfo();
                message2 = p_listValueVO.getOtherInfo2();
                interfaceStatusTy = p_listValueVO.getStatusType();
            }

            if (p_interfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_VOMS)) {
                if (_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_MVD) || _c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_NMVD)) {
                    _receiverTransferItemVO.setInterfaceID(interfaceID);
                    _receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                    _receiverTransferItemVO.setInterfaceType(p_interfaceCategory);
                }
                _senderTransferItemVO.setPrefixID(p_prefixID);
                _senderTransferItemVO.setInterfaceID(interfaceID);
                _senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                _senderTransferItemVO.setInterfaceType(p_interfaceCategory);
                if (!p_useInterfacePrefixVO && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                    _vomsInterfaceInfoInDBFound = true;
                }
                _vomsExternalID = externalID;
                _vomsAllServiceClassID = allServiceClassID;
            } else {
                _receiverTransferItemVO.setPrefixID(p_prefixID);
                _receiverTransferItemVO.setInterfaceID(interfaceID);
                _receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                _receiverTransferItemVO.setInterfaceType(p_interfaceCategory);
                if (!p_useInterfacePrefixVO && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                    _receiverInterfaceInfoInDBFound = true;
                }
                _externalID = externalID;
                _interfaceStatusType = interfaceStatusTy;
                if (PretupsI.YES.equals(underProcessMsgReqd)) {
                    _c2sTransferVO.setUnderProcessMsgReq(true);
                }
                _receiverAllServiceClassID = allServiceClassID;
                _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
                _c2sTransferVO.setReceiverInterfaceStatusType(_interfaceStatusType);
            }
            // Check if interface status is Active or not.
            if (!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceStatusTy)) {
                if (PretupsI.LOCALE_LANGAUGE_EN.equals(_senderLocale.getLanguage())) {
                    _c2sTransferVO.setSenderReturnMessage(message1);
                } else {
                    _c2sTransferVO.setSenderReturnMessage(message2);
                }
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
            }
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("Getting Base Exception =");
        	loggerValue.append( be.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrepaidController[setInterfaceDetails]", _transferID,
                _senderMSISDN, _senderNetworkCode,  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
        } finally {
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append(" Exiting with Sender Interface ID=");
            	loggerValue.append( _senderTransferItemVO.getInterfaceID());
            	loggerValue.append( " Receiver Interface=");
            	loggerValue.append(_receiverTransferItemVO.getInterfaceID());
                _log.debug(methodName, _requestIDStr, loggerValue);
            }
        }
    }
    /**
     * Method to get the reciever validate String
     * 
     * @return
     */
    public String getReceiverValidateStr() {
        StringBuffer strBuff ;
        strBuff = new StringBuffer(getReceiverCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
        return strBuff.toString();
    }
    
    /**
     * Method to get the string to be sent to the interface for topup
     * 
     * @return
     */
    private String getReceiverCommonString() {
        StringBuffer strBuff ;
        strBuff = new StringBuffer("MSISDN=" + _receiverMSISDN);
        strBuff.append("&TRANSACTION_ID=" + _transferID);
        strBuff.append("&NETWORK_CODE=" + _receiverVO.getNetworkCode());
        strBuff.append("&INTERFACE_ID=" + _receiverTransferItemVO.getInterfaceID());
        strBuff.append("&INTERFACE_HANDLER=" + _receiverTransferItemVO.getInterfaceHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeR);
        strBuff.append("&INT_MOD_IP=" + _intModIPR);
        strBuff.append("&INT_MOD_PORT=" + _intModPortR);
        strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameR);
        strBuff.append("&MODULE=" + PretupsI.C2S_MODULE);
        // added for CRE_INT_CR00029 by ankit Zindal
        strBuff.append("&CARD_GROUP_SELECTOR=" + _requestVO.getReqSelector());
        strBuff.append("&USER_TYPE=R");
        strBuff.append("&REQ_SERVICE=" + _serviceType);
        strBuff.append("&INT_ST_TYPE=" + _c2sTransferVO.getReceiverInterfaceStatusType());
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
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
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

            if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                _c2sTransferVO.setErrorCode(status + "_R");
                _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                _receiverTransferItemVO.setTransferStatus(status);
                _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                String[] strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested) };
                throw new BTSLBaseException(controllerName, methodName, _c2sTransferVO.getErrorCode(), 0, strArr, null);
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
                _receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), dateFormat));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                _receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), dateFormat));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
            // Done so that receiver check can be brough to common
            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
            _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());

            try {
                _receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
            _receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));

            // TO DO Done for testing purpose should we use it or give exception
            // in this case
            if (_receiverTransferItemVO.getPreviousExpiry() == null) {
                _receiverTransferItemVO.setPreviousExpiry(_currentDate);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited");
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
                // Also store in global veriables
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
                                // if default language is english then pick
                                // language 1
                                // message else language 2
                                // Changed on 15/05/06 for CR00020 (Gurjeet
                                // Singh Bedi)
                                if (PretupsI.LOCALE_LANGAUGE_EN.equals(_senderLocale.getLanguage())) {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                } else {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
                            }

                            checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

                            // validate receiver limits before Interface
                            // Validations
                            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                                PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
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
                                // if default language is english then pick
                                // language 1
                                // message else language 2
                                // Changed on 15/05/06 for CR00020 (Gurjeet
                                // Singh Bedi)
                                if (PretupsI.LOCALE_LANGAUGE_EN.equals(_senderLocale.getLanguage())) {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                } else {
                                    _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
                            }

                            checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

                            // validate receiver limits before Interface
                            // Validations
                            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                                PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
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
                                        // if default language is english then
                                        // pick
                                        // language 1 message else language 2
                                        // Changed on 15/05/06 for CR00020
                                        // (Gurjeet
                                        // Singh Bedi)
                                        if (PretupsI.LOCALE_LANGAUGE_EN.equals(_senderLocale.getLanguage())) {
                                            _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                        } else {
                                            _c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                        }
                                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
                                    }

                                    checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

                                    // validate receiver limits before Interface
                                    // Validations
                                    if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                                        PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
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
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append( e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"[performAlternateRouting]", _transferID,
                _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"[updateReceiverLocale]",
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
     * This method will check the transaction load on the given interface
     * 
     * @param p_userType
     * @param p_interfaceID
     * @throws BTSLBaseException
     */
    private void checkTransactionLoad(String p_userType, String p_interfaceID) throws BTSLBaseException {
        final String methodName = "checkTransactionLoad";
        debugCheckTransactionLoad(p_userType, p_interfaceID, methodName);
        int recieverLoadStatus = 0;
        StringBuilder loggerValue= new StringBuilder(); 

        try {
            // Do not enter the request in Queue
            recieverLoadStatus = LoadController.checkInterfaceLoad(_c2sTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _transferID,
                _c2sTransferVO, false);
            if (recieverLoadStatus == 0) {
                LoadController.checkTransactionLoad(_c2sTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE, _transferID, true,
                    LoadControllerI.USERTYPE_RECEIVER);
                if (_log.isDebugEnabled()) {
    		        loggerValue.setLength(0);
                	loggerValue.append("_transferID=");
                	loggerValue.append(_transferID);
                	loggerValue.append(" Successfully through load");
                    _log.debug(controllerName+"["+methodName+"]",  loggerValue );
                }
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
        	loggerValue.append("Refusing request getting Exception:" );
        	loggerValue.append(be.getMessage());
            _log.error(controllerName+"["+methodName+"]", loggerValue );
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
        }
    }

	private void debugCheckTransactionLoad(String p_userType,
			String p_interfaceID, final String methodName) {
		if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Checking load for transfer ID=" + _transferID + " on interface=" + p_interfaceID + " p_userType="+p_userType);
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
        final HashMap<String,String> map = BTSLUtil.getStringToHash(str, "&", "=");
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

        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            _c2sTransferVO.setErrorCode(status + "_R");
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(status);
            _senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            String[] strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested) };
            throw new BTSLBaseException(controllerName, "updateForReceiverValidateResponse", PretupsErrorCodesI.C2S_RECEIVER_FAIL_MVD, 0, strArr, null);
        }

        _receiverTransferItemVO.setTransferStatus(status);
        _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

        try {
            _receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        try {
            _receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        _receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        // Done so that receiver check can be brough to common
        _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
        _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());

        try {
            _receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
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
     * Method:getVOMSUpdateRequestStr
     * This method will construct the detils to be sent in request to
     * VOMSHandler
     * 
     * @param p_interfaceAction
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @param p_updateStatus
     * @param p_previousStatus
     * @return
     */
    public String getVOMSUpdateRequestStr(String p_interfaceAction, C2STransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO, String p_updateStatus, String p_previousStatus) {
        final String METHOD_NAME = "getVOMSUpdateRequestStr";
        StringBuffer strBuff ;
        strBuff = new StringBuffer(getVOMSCommonString(p_transferVO, p_networkInterfaceModuleVO, p_interfaceVO));
        strBuff.append("&INTERFACE_ACTION=" + p_interfaceAction);
        try {
            strBuff.append("&TRANSFER_DATE=" + BTSLUtil.getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        strBuff.append("&INTERFACE_AMOUNT=" + p_transferVO.getRequestedAmount());
        strBuff.append("&UPDATE_STATUS=" + p_updateStatus);
        strBuff.append("&PREVIOUS_STATUS=" + p_previousStatus);
        strBuff.append("&SOURCE=" + p_transferVO.getSourceType());
        strBuff.append("&SENDER_MSISDN=" + p_transferVO.getSenderMsisdn());
        strBuff.append("&SERIAL_NUMBER=" + p_transferVO.getSerialNumber());
        strBuff.append("&SENDER_USER_ID=" + p_transferVO.getSenderID());
        strBuff.append("&QUANTITY=" + String.valueOf(_quantityRequested));
        strBuff.append("&TRANSFERLISTSTRING=" + transferListString); 
        return strBuff.toString();
    }
    /**
     * Method: getVOMSCommonString
     * This method will construct the common string that is to be send in
     * request for VOMSHandler
     * 
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @return
     */
    private String getVOMSCommonString(C2STransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO) {
        StringBuffer strBuff ;
        strBuff = new StringBuffer("MSISDN=" + p_transferVO.getReceiverMsisdn());
        strBuff.append("&TRANSACTION_ID=" + p_transferVO.getTransferID());
        strBuff.append("&NETWORK_CODE=" + p_transferVO.getReceiverNetworkCode());
        strBuff.append("&INTERFACE_ID=" + p_interfaceVO.getInterfaceId());
        strBuff.append("&INTERFACE_HANDLER=" + p_interfaceVO.getHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + p_networkInterfaceModuleVO.getCommunicationType());
        strBuff.append("&INT_MOD_IP=" + p_networkInterfaceModuleVO.getIP());
        strBuff.append("&INT_MOD_PORT=" + p_networkInterfaceModuleVO.getPort());
        strBuff.append("&INT_MOD_CLASSNAME=" + p_networkInterfaceModuleVO.getClassName());
        strBuff.append("&MODULE=" + PretupsI.C2S_MODULE);
        strBuff.append("&CARD_GROUP_SELECTOR=" + p_transferVO.getSubService());
        strBuff.append("&REQUEST_GATEWAY_CODE=" + _requestVO.getRequestGatewayCode());
        strBuff.append("&REQUEST_GATEWAY_TYPE=" + _requestVO.getRequestGatewayType());
        strBuff.append("&LOGIN=" + _requestVO.getLogin());
        strBuff.append("&PASSWORD=" + _requestVO.getPassword());
        strBuff.append("&SOURCE_TYPE=" + _requestVO.getSourceType());
        strBuff.append("&SERVICE_PORT=" + _requestVO.getServicePort());
        strBuff.append("&REQ_SERVICE=" + _serviceType);
        return strBuff.toString();
    }
    /**
     * Method to get the under process message to be sent to receiver
     * 
     * @return
     */
    private String getReceiverUnderProcessMessage() {
        final String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO
            .getValidity()), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), _senderMSISDN, PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _channelUserVO
            .getUserName(), _payableAmt };
        return BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_UNDERPROCESS_VMS, messageArgArray, _requestVO.getRequestGatewayType());
    }

    /**
     * Method to update the Voucher Status and Credit back the Sender. It also
     * update the transaction table with final status
     * 
     * @param p_action
     */
    private void voucherUpdateSenderCreditBack(String p_action) {
        final String methodName = "voucherUpdateSenderCreditBack";
       StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
	        loggerValue.setLength(0);
        	loggerValue.append( "Entered for _transferID=" );
        	loggerValue.append(_transferID);
        	loggerValue.append(" p_action=" );
        	loggerValue.append( p_action);
        	loggerValue.append(" _voucherMarked=" );
        	loggerValue.append(_voucherMarked);
            _log.debug(methodName,loggerValue );
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            if (_voucherMarked) {
                _userBalancesVO = null;
                try {
                    if (!_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                        final InterfaceVO interfaceVO = new InterfaceVO();
                        interfaceVO.setInterfaceId(_senderTransferItemVO.getInterfaceID());
                        interfaceVO.setHandlerClass(_senderTransferItemVO.getInterfaceHandlerClass());
                        updateVoucherForFailedTransaction(_c2sTransferVO, _networkInterfaceModuleVO, interfaceVO);
                    }

                } catch (Exception e) {
                    // Event Handle to show that voucher could not be updated
                    // and is still Under process
                	loggerValue.setLength(0);
                	loggerValue.append(" For transfer ID=");
                	loggerValue.append(_transferID);
                	loggerValue.append(" Error while updating voucher status for =");
                	loggerValue.append(_c2sTransferVO.getSerialNumber());
                	loggerValue.append(" So leaving the voucher marked as under process. Exception: ");
                	loggerValue.append(e.getMessage());
                    _log.error(methodName,loggerValue );
                    _log.errorTrace(methodName, e);
                    loggerValue.setLength(0);
                	loggerValue.append("Error while updating voucher status for =" );
                	loggerValue.append(_c2sTransferVO.getSerialNumber());
                	loggerValue.append(" So leaving the voucher marked as under process. Exception: ");
                	loggerValue.append( e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, controllerName+"[voucherUpdateSenderCreditBack]",
                        _transferID, "", "",loggerValue.toString());
                }
                mcomCon = new MComConnection();
                con=mcomCon.getConnection();
                if (_transferDetailAdded) {
                    debugSenderCreditBack(methodName);
                    updateSenderForFailedTransaction(con, _c2sTransferVO);
                    final C2STransferItemVO senderCreditBackItemVO = (C2STransferItemVO) _c2sTransferVO.getTransferItemList().get(2);
                    senderCreditBackItemVO.setUpdateStatus(_senderTransferItemVO.getUpdateStatus1());
                }

                // added by nilesh: consolidated for logger
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    addEntryInTransfers(con);
                }
                // Log the details if the transfer Details were added i.e. if
                // User was creditted
                if (_creditBackEntryDone) {
                    BalanceLogger.log(_userBalancesVO);
                }

                if (PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION.equals(p_action)) {
                    _finalTransferStatusUpdate = false;
                }
            }
        } catch (BTSLBaseException be) {
            if (PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION.equals(p_action)) {
                _finalTransferStatusUpdate = false;
            }
            loggerValue.setLength(0);
        	loggerValue.append(" For transfer ID=");
        	loggerValue.append(_transferID);
        	loggerValue.append(" Getting BTSL Base Exception: ");
        	loggerValue.append(be.getMessage());
            _log.error(methodName,  loggerValue );
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
            if (PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION.equals(p_action)) {
                _finalTransferStatusUpdate = false;
            }
            loggerValue.setLength(0);
        	loggerValue.append("Error while credit back sender, getting exception: " );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, controllerName+"[voucherUpdateSenderCreditBack]",
                _transferID, "", "", loggerValue.toString() );
        } finally {
			if (mcomCon != null) {
				mcomCon.close("MVDController#voucherUpdateSenderCreditBack");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting for _transferID=" + _transferID + " p_action=" + p_action);
            }
        }
    }

	private void debugSenderCreditBack(final String methodName) {

        StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
        	loggerValue.append("transferID=" );
        	loggerValue.append(_transferID);
        	loggerValue.append(" Doing Sender Credit back ");
		    _log.debug(methodName, loggerValue.toString() );
		}
	}
    /**
     * Method :updateVoucherForFailedTransaction
     * This method will mark the voucher status to enable
     * 
     * @param p_con
     * @param p_transferVO
     * @param p_networkInterfaceModuleVO
     * @param p_interfaceVO
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean updateVoucherForFailedTransaction(C2STransferVO p_transferVO, NetworkInterfaceModuleVO p_networkInterfaceModuleVO, InterfaceVO p_interfaceVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateVoucherForFailedTransaction", " Entered for p_transferVO= " + p_transferVO);
        }
        final String METHOD_NAME = "updateVoucherForFailedTransaction";
        boolean finalTransferStatusUpdate = true;
        C2STransferItemVO senderTransferItemVO = (C2STransferItemVO) p_transferVO.getTransferItemList().get(0);
        try {
            final CommonClient commonClient = new CommonClient();
            final String vomsCreditBackResponse = commonClient.process(getVOMSUpdateRequestStr(PretupsI.INTERFACE_CREDIT_ACTION, p_transferVO, p_networkInterfaceModuleVO,
                p_interfaceVO, VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_UNPROCESS), p_transferVO.getTransferID(), p_networkInterfaceModuleVO.getCommunicationType(),
                p_networkInterfaceModuleVO.getIP(), p_networkInterfaceModuleVO.getPort(), p_networkInterfaceModuleVO.getClassName());
            // getting the update status from the Response and set in
            // appropriate VO: senderTransferItemVO update Status 1 can be used
            extractVomsVoucherChangeStatusLog(p_transferVO, METHOD_NAME,
					senderTransferItemVO, vomsCreditBackResponse);
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), p_transferVO.getSenderNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Done to voucher for serial number=" + p_transferVO.getSerialNumber(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (Exception be) {
            _log.errorTrace(METHOD_NAME, be);
            finalTransferStatusUpdate = false;
            senderTransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back voucher", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw new BTSLBaseException(this, "updateVoucherForFailedTransaction", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateVoucherForFailedTransaction", " Exited for finalTransferStatusUpdate= " + finalTransferStatusUpdate);
            }
        }
        return finalTransferStatusUpdate;
    }

	private void extractVomsVoucherChangeStatusLog(C2STransferVO p_transferVO,
			final String METHOD_NAME, C2STransferItemVO senderTransferItemVO,
			final String vomsCreditBackResponse) throws BTSLBaseException {
		try {
		    final HashMap<String,String> map = BTSLUtil.getStringToHash(vomsCreditBackResponse, "&", "=");
		    senderTransferItemVO.setUpdateStatus1((String) map.get("TRANSACTION_STATUS"));
		    if (!InterfaceErrorCodesI.SUCCESS.equals(senderTransferItemVO.getUpdateStatus1())) {
		        throw new BTSLBaseException(this, "updateVoucherForFailedTransaction", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
		    }
		    VomsVoucherChangeStatusLog.log(p_transferVO.getTransferID(), p_transferVO.getSerialNumber(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_ENABLE, p_transferVO
		        .getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getUserID(), BTSLUtil
		        .getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
		} catch (Exception e) {
		    _log.error("updateVoucherForFailedTransaction", " Exception while updating voucher status= " + e.getMessage());
		    _log.errorTrace(METHOD_NAME, e);
		    throw new BTSLBaseException(e);
		}
	}
    /**
     * Method: sendSMS
     * Method to send success & failure messages to customer and retailer &
     * adjust.
     * 
     * @param Connection
     *            p_con
     * @param CommonClient
     *            p_commonClient
     * @param VomsVoucherVO
     *            p_vomsVO
     * @param String
     *            p_source
     * @throws BTSLBaseException
     *             ,Exception
     */

    private void sendSMS(VomsVoucherVO p_vomsVO) throws BTSLBaseException {
        final String methodName = "sendSMS";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered _transferID=");
        	loggerValue.append(_transferID);
        	loggerValue.append(" p_vomsVO=" );
        	loggerValue.append(p_vomsVO);
            _log.debug(methodName, loggerValue);
        }
        String pinMessage = null;
        String binaryPinMessage = null;
        Locale locale = null;
        InterfaceVO interfaceVO = null;
        interfaceVO = new InterfaceVO();
        interfaceVO.setInterfaceId(_senderTransferItemVO.getInterfaceID());
        interfaceVO.setHandlerClass(_senderTransferItemVO.getInterfaceHandlerClass());
        final EvdUtil evdUtil = new EvdUtil();
        boolean creditbackdone = false;
        boolean smsChargingRequired = false;
        try {

            GroupTypeProfileVO groupTypeProfileVO = null;
            if (_senderMSISDN.equals(_c2sTransferVO.getPinSentToMsisdn())) {
                binaryPinMessage = BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.BIN_PIN_MESSAGE_FOR_R,
                new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested), _receiverMSISDN, 
                		mapOfSerialAndPIN.get("PIN"), mapOfSerialAndPIN.get("SERIAL_NUMBER"), p_vomsVO.getExpiryDateStr() });
                binaryPinMessage = _operatorUtil.DES3Encryption(binaryPinMessage, _requestVO);
                pinMessage = BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.PIN_MESSAGE_FOR_R, new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO
                        .getRequestedAmount() * _quantityRequested ), _receiverMSISDN, mapOfSerialAndPIN.get("PIN"), mapOfSerialAndPIN.get("SERIAL_NUMBER") });
                
                locale = _senderLocale;
                // always send in english
                // binaryPinMessage=BTSLUtil.getMessage(new
                // Locale("en","US"),PretupsErrorCodesI.BIN_PIN_MESSAGE_FOR_R,new
                // String[]{_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_receiverMSISDN,BTSLUtil.decryptText(p_vomsVO.getPinNo()),p_vomsVO.getSerialNo(),p_vomsVO.getExpiryDateStr()});
                // binaryPinMessage=_operatorUtil.DES3Encryption(binaryPinMessage,_requestVO);

                if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)).indexOf(_requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE
                    .equals(_requestVO.getGroupType())) {
                    try {
                        // load the user running and profile counters
                        // Check the counters
                        // update the counters
                        groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(_requestVO, PretupsI.GRPT_TYPE_CHARGING);
                        // if group type counters reach limit then send message
                        // using gateway that is associated with group type
                        // profile
                        if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
                            smsChargingRequired = true;
                            SMSChargingLog.log(((ChannelUserVO) _requestVO.getSenderVO()).getUserID(), (((ChannelUserVO) _requestVO.getSenderVO())
                                .getUserChargeGrouptypeCounters()).getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(),
                                groupTypeProfileVO.getResGatewayType(), groupTypeProfileVO.getNetworkCode(), _requestVO.getGroupType(), _requestVO.getServiceType(),
                                _requestVO.getModule());
                        }
                    } catch (Exception e) {
                        _log.errorTrace(methodName, e);
                    }
                }
            } else {
            	  pinMessage = BTSLUtil.getMessage(_receiverLocale, PretupsErrorCodesI.PIN_MESSAGE_FOR_C, new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO
                        .getRequestedAmount()*_quantityRequested), _senderMSISDN, mapOfSerialAndPIN.get("PIN"), mapOfSerialAndPIN.get("SERIAL_NUMBER") });
                locale = _receiverLocale;
            }
            final PushMessage pushMessage = new PushMessage(_c2sTransferVO.getPinSentToMsisdn(), pinMessage, _transferID, _c2sTransferVO.getRequestGatewayCode(), locale);
            PushMessage pushMessage1 = null;
            if (_requestVO.getPrivateRechBinMsgAllowed()) {
                pushMessage1 = new PushMessage(_c2sTransferVO.getPinSentToMsisdn(), binaryPinMessage, _transferID, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECH_MESSGATEWAY)), locale);
            }
            String retKannstatus = null;
            String retBinKannstatus = null;

            // *********************************
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DELIVERY_RECEIPT_TRACKED))).booleanValue()) {
                // Check mySql db is up or not?. If down throw error and update
                // the voucher
                // status to enable using debit method of voucherHandler, also
                // credit back the
                // sender by updateSenderForFailedTransaction
                int updateMySqlCt = 0; // To check whether we are able to update
                // My SQL database
                updateMySqlCt = EvdUtil.checkMySqlConnUp(_c2sTransferVO);
                if (updateMySqlCt > 0) {
                    // Push underprocess message with receipt
                    if (smsChargingRequired) {
                        retKannstatus = pushMessage.pushSmsUrlWithReceipt(false, _c2sTransferVO.getTransferID(), groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO
                            .getAltGatewayCode());
                        if (_requestVO.getPrivateRechBinMsgAllowed()&&pushMessage1!=null) {
                            retBinKannstatus = pushMessage1.pushSmsUrlWithReceipt(false, _c2sTransferVO.getTransferID(), groupTypeProfileVO.getGatewayCode(),
                                groupTypeProfileVO.getAltGatewayCode());
                        }
                    } else {
                        retKannstatus = pushMessage.pushSmsUrlWithReceipt(false, _c2sTransferVO.getTransferID(), null, null);
                        if (_requestVO.getPrivateRechBinMsgAllowed()&&pushMessage1!=null) {
                            retBinKannstatus = pushMessage1.pushSmsUrlWithReceipt(false, _c2sTransferVO.getTransferID(), null, null);
                        }
                    }
                    if (!retKannstatus.equalsIgnoreCase(PretupsI.GATEWAY_MESSAGE_SUCCESS)) // &&
                    // !retBinKannstatus.equalsIgnoreCase(PretupsI.GATEWAY_MESSAGE_SUCCESS)
                    {
                        // credit back the sender and voucher
                        creditbackdone = true;
                        _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                        _c2sTransferVO.setErrorCode(PretupsErrorCodesI.VMS_PIN_SENT_FAIL);
                        voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                        // Added so that Failed Counters can be increased
                        ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),
                            _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
                    } else {
                        if (_log.isDebugEnabled()) {
                            _log.debug(
                                methodName,
                                "Transfer ID=" + _transferID + " Message Received by kannel Got Status=" + retKannstatus + " leave the controller now and wait for Delivery Receipt from kannel");
                        }
                        _finalTransferStatusUpdate = false;
                        _deliveryTrackDone = true;
                    }
                } else {
                    creditbackdone = true;
                    _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.VMS_PIN_SENT_FAIL);
                    voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                    // Added so that Failed Counters can be increased
                    ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),
                        _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
                }
            } else // Delivery receipt not required.
            {
                // The condition below will check if Kannel has accepted the
                // message or not.
                // If kannel accepted the message then it return the success and
                // we will give differentials to sender and also mark the
                // voucher to consume
                if (smsChargingRequired) {
                    retKannstatus = pushMessage.pushMessageWithStatus(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());
                    if (_requestVO.getPrivateRechBinMsgAllowed()&&pushMessage1!=null) {
                        retBinKannstatus = pushMessage1.pushMessageWithStatus(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());
                    }
                } else {
                    retKannstatus = pushMessage.pushMessageWithStatus(null, null);
                    if (_requestVO.getPrivateRechBinMsgAllowed()&&pushMessage1!=null) {
                        retBinKannstatus = pushMessage1.pushMessageWithStatus(null, null);
                    }
                }
                if (retKannstatus.equals(PretupsI.GATEWAY_MESSAGE_SUCCESS)) // &&
                // retBinKannstatus.equals(PretupsI.GATEWAY_MESSAGE_SUCCESS)
                {
                    try {
                        _finalTransferStatusUpdate = evdUtil.updateVoucherAndGiveDifferentials(_receiverVO, _c2sTransferVO, _networkInterfaceModuleVO, interfaceVO, _requestVO
                            .getInstanceID(), false);
                    } catch (BTSLBaseException be) {
                        throw new BTSLBaseException(be);
                    } catch (Exception e) {
                        throw new BTSLBaseException(e);
                    }

                } else// message sending failed i.e. message is not accepted by
                      // kannel
                {
                    creditbackdone = true;

                    _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.VMS_PIN_SENT_FAIL);
                    voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                    // Added so that Failed Counters can be increased
                    ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),
                        _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
                }
            }
        } catch (BTSLBaseException be) {
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
                }
            }// end if
            if (!creditbackdone) {
                voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            }
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
            }

            if (!creditbackdone) {
                voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            }
            throw new BTSLBaseException(this, methodName, "");
        } finally {
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exited _transferID=" );
            	loggerValue.append(_transferID);
            	loggerValue.append( " _c2sTransferVO.getTransferStatus()=" );
            	loggerValue.append( _c2sTransferVO.getTransferStatus());
                _log.debug(methodName, loggerValue);
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
        int senderLoadStatus = 0;
        try {
            _c2sTransferVO.setRequestVO(_requestVO);
            _c2sTransferVO.setSenderTransferItemVO(_senderTransferItemVO);
            _c2sTransferVO.setReceiverTransferItemVO(_receiverTransferItemVO);
            senderLoadStatus = LoadController.checkInterfaceLoad(_c2sTransferVO.getReceiverNetworkCode(), _senderTransferItemVO.getInterfaceID(), _transferID, _c2sTransferVO,
                true);
            if (senderLoadStatus == 0) {
                    LoadController.checkTransactionLoad(_c2sTransferVO.getReceiverNetworkCode(), _senderTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE, _transferID,
                        true, LoadControllerI.USERTYPE_SENDER);
            } else if (senderLoadStatus == 1) {
                final String[] strArr = { _receiverMSISDN, String.valueOf(PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()* _quantityRequested)) };
                throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            _log.error(controllerName+"[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
        }
    }
    
    private void isReceiverBarred(Connection p_con) throws BTSLBaseException{
    	final String methodName = "isReceiverBarred";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, "Entered");
    	}
    	try {
    		PretupsBL.checkMSISDNBarred(p_con, _receiverMSISDN, _receiverVO.getNetworkCode(), _c2sTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
    	} catch (BTSLBaseException be) {
    		if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
    			_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
    		}
    		throw be;
    	}finally {
    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, "Exited");
    		}
    	}
    }

    private void commitConnection(Connection p_con) throws BTSLBaseException{
    	final String methodName = "commitConnection";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, "Entered");
    	}
    	try {
    		p_con.commit();
    	} catch (Exception e) {
    		_log.errorTrace(methodName, e);
    		throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
    	}finally {
    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, "Exited");
    		}
    	}

    }
    
    private void closeConnection(Connection p_con) throws BTSLBaseException{
    	final String methodName = "closeConnection";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, "Entered");
    	}
    	try {
    		p_con.close();
    	} catch (Exception e) {
    		_log.errorTrace(methodName, e);
    		throw new BTSLBaseException(controllerName, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_MVD);
    	}finally {
    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, "Exited");
    		}
    	}
    }
    private void isDecimalAllowed() throws BTSLBaseException{
    	final String methodName = "isDecimalAllowed";
    	if (_log.isDebugEnabled()) {
    		_log.debug(methodName, "Entered");
    	}
    	try {
    		final String displayAmt = PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount() * _quantityRequested);
    		Long.parseLong(displayAmt);
    	} catch (Exception e) {
    		_log.errorTrace(methodName, e);
    		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_AMOUNT_MVD);
    	}
    	finally {
    		if (_log.isDebugEnabled()) {
    			_log.debug(methodName, "Exited");
    		}

    	}
    }
}
