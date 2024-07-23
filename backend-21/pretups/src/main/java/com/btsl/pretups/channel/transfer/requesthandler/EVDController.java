/*
 * @(#)EVDController.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Ruwali 01/09/2006 Initial Creation
 * Ankit Zindal 10/09/06 Modified
 * Gurjeet Singh Bedi 29/09/06 Modified for code restructuring
 * Ashish Kumar July 02, 2007 Add for the transaction id generation in the
 * memory
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * Controller class for handling the Electronic Voucher Distribution(EVD) &
 * Electronic Voucher Recharge(EVR) Services
 */

package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.logging.ChannelGatewayRequestLog;
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
import com.btsl.pretups.logging.ChannelRequestDailyLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
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
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class EVDController implements ServiceKeywordControllerI, Runnable {
    private static Log _log = LogFactory.getLog(EVDController.class.getName());
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
    private String _intModCommunicationTypeR;
    private String _intModIPR;
    private int _intModPortR;
    private String _intModClassNameR;
    private NetworkInterfaceModuleVO _networkInterfaceModuleVO = null;
    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
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
    private final String _notAllowedSendMessGatw;
    private String _receiverSubscriberType = null;
    private static OperatorUtilI _operatorUtil = null;
    private boolean _vomsInterfaceInfoInDBFound = false;
    private String _vomsExternalID = null;
    private VomsVoucherVO _vomsVO = null;
    private boolean _voucherMarked = false;
    private boolean _deliveryTrackDone = false;
    private String _vomsAllServiceClassID = null;
    private String _interfaceStatusType = null;
    private static int _transactionIDCounter = 0;
    private static long _prevReqTime = 0;
    private String _payableAmt = null;
    private static int _prevMinut = 0;

    private boolean _onlyForEvr = false;
    // Loads Operator specific class. In EVD controller it is used for
    // validating the message format.
    private boolean _receiverMessageSendReq=true;
    private final String _notAllowedRecSendMessGatw;
    private boolean _oneLog = true;
    private boolean _bypassSenderMessageDeliveryStatus= false;
    private String extraPrefixSerialNumber;
    private String extraPrefixOtherInfo;
  //private recharge sid in case of EVD
    private String _sid=null;
    private String _successMessage=null;
    private String _status;
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /*
     * In the constructor of EVDController initialize the date variable
     * _currentDate with current date. The
     * variables EVD_REC_GEN_FAIL_MSG_REQD_V & EVD_REC_GEN_FAIL_MSG_REQD_T
     * decides whether the validation and
     * top up failed message send to receiver or not.
     */

    public EVDController() {
        _c2sTransferVO = new C2STransferVO();
        _currentDate = new Date();
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("EVD_REC_GEN_FAIL_MSG_REQD_V")))) {
            _recValidationFailMessageRequired = true;
        }
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("EVD_REC_GEN_FAIL_MSG_REQD_T")))) {
            _recTopupFailMessageRequired = true;
        }
        _notAllowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("EVD_SEN_MSG_NOT_REQD_GW"));
        _notAllowedRecSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("EVD_REC_MSG_NOT_REQD_GW"));
		extraPrefixSerialNumber=BTSLUtil.NullToString(Constants.getProperty("EVD_EXTRA_PREFIX_SERIAL_NUMBER"));
		extraPrefixOtherInfo=BTSLUtil.NullToString(Constants.getProperty("EVD_EXTRA_PREFIX_OTHER_INFO"));
		_bypassSenderMessageDeliveryStatus = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BYPASS_EVD_KANNEL_MESSAGE_STATUS);
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
        Connection con = null;
        MComConnectionI mcomCon = null;

        /*
         * 1. Check sender out transfer status if the sender is out suspend the
         * request is failed.
         * 2. Check sender transfer profile status if it is suspended request is
         * failed.
         * 3. Check sender commission profile status, it should not be
         * suspended.
         * 4. Check whether self voucher distribution is allowed or not.
         * 5. Validate the request format received from the ChannelReceiver
         * servlet.
         * 6. Check whether the restricted msisdn is allowed for the sender
         * category, if yes check the
         * receiver number in the restricted list of sender ( restricted_msisdns
         * table).
         * 7. Validates the Service [EVD/EVR] status for the network, check in
         * NETWORK_SERVICES.
         * 8. Check if receiver barred in PreTUPS or not,if yes request is
         * failed.
         * 9. Load receiver control limits.
         * 9.1) Search the subscriber in subscriber_control table.If subscriber
         * details not found then mark
         * the transaction as underprocess and insert the subscriber info in
         * subscriber_control table.
         * If subscriber details found and the last transaction status is
         * underprocess throw error else
         * update the subscriber_control table.
         * 10. In processTransfer method generate evd transfer id & load the
         * product corresponding to the service
         * type.
         * 11. Get the Interface routing details.
         * 11.1) Load the VOMS Interface.
         * 11.2) In case of EVR load the IN Interface.
         * 11.3) Validate the receiver limits
         * 12. Validate sender avaliable controls [ Check user
         * balances,thresholds ]
         * 13. Check transaction load.
         * 14. Check if message gateway type flow is common or thread.
         * 14.1) If flow type is thread then spawn a thread and perform the
         * voucher loading.
         * 15. Construct VOMS validation request & if service type is EVR
         * construct receiver validation
         * request.
         * 15.1) Validate receiver on IN using common client.
         * 15.2) Validate the service class returned from IN Interface.
         * 15.3) Validate transfer rule & receiver limits.
         * 15.4) Validate VOMS interface using common client.
         * 15.5) If VOMS is not validate throw error else construct productVO &
         * set the serial number
         * in _c2sTransferVO.
         * 15.6) Debit the sender balance & increase sender transfer outcounts.
         * 16. If service type is EVD find from the preferences if PIN is send
         * to retailer or customer.
         * 16.1) If PIN is send to retailer then from preferences find whether
         * delivery receipt has to be
         * tracked or not.
         * 16.2) If delivery report is not tracked then
         * 16.2.1) Make entry in transaction table.
         * 16.2.2) Generate SMS to be sending to retailer & send.
         * 16.2.2.1) If status is sent then call VOMS credit method & mark
         * voucher consumed.
         * 16.2.2.2) If voucher status is updated successfully then give
         * diffrential commision &
         * send success message to retailer.
         * 16.2.2.3) If voucher status is not updated credit back the sender.
         * 16.2.2.4) If status is not sent credit back the sender & call voms
         * debit method also
         * send failure message to retailer & customer.
         * 16.3) If delivery report has to be tracked.
         * 16.3.1) Mark entry 200 in _c2sTransfers, check mySQL is up or not. If
         * down throw error & update
         * the voucher status to enable using debit method of VoucherHandler
         * also credit back the
         * sender.
         * 16.4) If pin is send to customer then same as 16.2)
         * 
         * 17. If service type is EVR
         * 17.1) Construct the receiver credit request & send to IN. Check the
         * status if credit is successful
         * then use VOMS interface credit method to mark the voucher status to
         * consumed. If voucher status
         * is successfully updated then give differential commision & mark entry
         * in transaction table.
         * 17.2) If Credit to IN is fail credit back the sender & use VOMS
         * interface debit method to mark voucher
         * status enable.Also send failure message to both retailer and
         * customer.
         * 17.3) If credit to IN is ambigious then credit back the sender & use
         * voms interface debit method.
         */

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

            // Checking senders out transfer status, it should not be suspended
            if (PretupsI.YES.equalsIgnoreCase(_channelUserVO.getOutSuspened())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND_EVD);
            }

            // Checking senders transfer profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getTransferProfileStatus())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND_EVD);
            }

            // Checking senders commission profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(_channelUserVO.getCommissionProfileStatus())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_EVD);
            }

            // Getting oracle connection
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            _c2sTransferVO.setCellId(p_requestVO.getCellId());
            _c2sTransferVO.setSwitchId(p_requestVO.getSwitchId());

            // Validating user message incoming in the request [Keyword is
            // either EVD/EVR]
            _operatorUtil.validateEVDRequestFormat(con, _c2sTransferVO, p_requestVO);

            // Block added to avoid decimal amount in credit transfer
            if (!BTSLUtil.isStringIn(_serviceType, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES))) {
                try {
                    final String displayAmt = PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount());
                    Long.parseLong(displayAmt);
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_AMOUNT_EVD);
                }
            }
            _receiverLocale = p_requestVO.getReceiverLocale();
            _senderLocale = p_requestVO.getSenderLocale();
            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));

            // The condition below will be checked for EVR only because in EVd
            // any postpaid number can also request PIN
            if (!_receiverVO.getSubscriberType().equals(_type) && _c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
                // Refuse the Request
                _log.error(this, "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDController[process]", "", "", "",
                    "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But request initiated for the same");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE_EVD, 0, new String[] { _receiverVO.getMsisdn() }, null);
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
            // VFE 6 CR
            /*_c2sTransferVO.setInfo1(p_requestVO.getInfo1());
            _c2sTransferVO.setInfo2(p_requestVO.getInfo2());
            _c2sTransferVO.setInfo3(p_requestVO.getInfo3());
            _c2sTransferVO.setInfo4(p_requestVO.getInfo4());
            _c2sTransferVO.setInfo5(p_requestVO.getInfo5());*/
            _c2sTransferVO.setInfo6(p_requestVO.getInfo6());
            _c2sTransferVO.setInfo7(p_requestVO.getInfo7());
            _c2sTransferVO.setInfo8(p_requestVO.getInfo8());
            _c2sTransferVO.setInfo9(p_requestVO.getInfo9());
            _c2sTransferVO.setInfo10(p_requestVO.getInfo10());
            // checking whether self voucher distribution is allowed or not //
            // in case of EVD private recharge also it is allowed.
            if (_senderMSISDN.equals(_receiverMSISDN) && (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SELF_VOUCHER_DISTRIBUTION_ALLOWED))).booleanValue() && (!(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue() && (_c2sTransferVO.getServiceType().contains(PretupsI.SERVICE_TYPE_EVD)) && "1".equals(_c2sTransferVO.getSubService()))))) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SELF_VOUCHER_DIST_NOTALLOWED);
            }

            // Restricted MSISDN check
            // if
            // (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getRestrictedMsisdns()))
            // RestrictedSubscriberBL.isRestrictedMsisdnExist(con,_c2sTransferVO,_channelUserVO,_receiverVO.getMsisdn(),_c2sTransferVO.getRequestedAmount());
            RestrictedSubscriberBL.isRestrictedMsisdnExistForC2S(con, _c2sTransferVO, _channelUserVO, _receiverVO.getMsisdn(), _c2sTransferVO.getRequestedAmount());

            // Validates the network service status
            PretupsBL.validateNetworkService(_c2sTransferVO);
            // receiverMessageSendReq=true;
            _receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW, _receiverVO.getNetworkCode(), _serviceType))
                .booleanValue();
            // receiver message send should be false if it is for private
            // recharge as in this case there will be no reciever MSISDN
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue() && (_c2sTransferVO.getServiceType().contains(PretupsI.SERVICE_TYPE_EVD)) && "1".equals(_c2sTransferVO.getSubService())) {
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
            if(!(boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED)) {
            	PretupsBL.loadRecieverControlLimits(con, p_requestVO.getRequestIDStr(), _c2sTransferVO);
			}
            _receiverVO.setUnmarkRequestStatus(true);

            // commiting transaction after updating receiver's control
            // parameters
            try {
            	mcomCon.partialCommit();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
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

                // Populate VOMS and IN interface details(IN interface will be
                // loaded for EVr only)
                populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                _c2sTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
                // This will be used in validate ReceiverLimit method of
                // PretupsBL when receiverTransferItemVO is null
                _c2sTransferVO.setReceiverSubscriberType(_receiverSubscriberType);

                // validate receiver limits before Interface Validations
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                    PretupsBL.validateRecieverLimits(con, _c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
                }

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
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
               if (mcomCon != null)
					mcomCon.close("EVDController#process");
				mcomCon = null;
				con = null;

                // Checking the Various loads and setting flag to decrease the
                // transaction count
                checkTransactionLoad();
                _decreaseTransactionCounts = true;

                (_channelUserVO.getUserPhoneVO()).setLastTransferID(_transferID);
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
                  
                    if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))
	                {
                    	  if(_c2sTransferVO.getServiceType().equalsIgnoreCase(PretupsI.SERVICE_TYPE_EVR))
                          {
                          	p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_SENDER_SUCCESS);
                          }else{
//                          	p_requestVO.setMessageCode(PretupsErrorCodesI.EVD_SENDER_SUCCESS);//priyank
                          	p_requestVO.setMessageCode(PretupsErrorCodesI.EVD_SENDER_MESSAGE_FOR_SUCCESS);
                          }
                    	  
	                	if(PretupsI.SERVICE_TYPE_EVD.equals(_c2sTransferVO.getServiceType())||PretupsI.SERVICE_TYPE_EVD101.equals(_c2sTransferVO.getServiceType())||PretupsI.SERVICE_TYPE_EVD102.equals(_c2sTransferVO.getServiceType())||PretupsI.SERVICE_TYPE_EVD104.equals(_c2sTransferVO.getServiceType())||PretupsI.SERVICE_TYPE_EVD105.equals(_c2sTransferVO.getServiceType())||PretupsI.SERVICE_TYPE_EVD106.equals(_c2sTransferVO.getServiceType())) {
	                    	if(_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS))
	                    	{
	                    		final String[] messageArgArray = { 
	                    				_receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL
	                    				.getDisplayAmount(_senderTransferItemVO.getPostBalance()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPreviousBalance()), String
	                    				.valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), String
	                    				.valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), extraPrefixSerialNumber+_vomsVO.getSerialNo(),_vomsVO.getProductID(),_vomsVO.getProductName(),_vomsVO.getExpiryDateStr(),prepareOtherInfo(extraPrefixOtherInfo, _vomsVO.getOtherInfo()),_c2sTransferVO.getProductName(),VomsUtil.decryptText(_vomsVO.getPinNo()) };
	                    		p_requestVO.setMessageArguments(messageArgArray);
	                    	}
	                    } else {
	                    	final String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()), PretupsBL
	                            .getDisplayAmount(_senderTransferItemVO.getPostBalance()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPreviousBalance()), String
	                            .valueOf(_receiverTransferItemVO.getValidity()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()),  String
	                            .valueOf(_receiverTransferItemVO.getNewGraceDate()), PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()), _c2sTransferVO
	                            .getSubService(),extraPrefixSerialNumber+_vomsVO.getSerialNo(),_vomsVO.getProductID(),_vomsVO.getProductName(),_vomsVO.getExpiryDateStr(),prepareOtherInfo(extraPrefixOtherInfo, _vomsVO.getOtherInfo()),_c2sTransferVO.getProductName(),VomsUtil.decryptText(_vomsVO.getPinNo()) };
	                        p_requestVO.setMessageArguments(messageArgArray);
	                    
	                    }
	                }
                }
                p_requestVO.setDecreaseLoadCounters(false);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.errorTrace(methodName, be);
            _log.error(methodName, be.getMessage());
            if(null==p_requestVO.getMessageCode()){
            	p_requestVO.setMessageCode(be.getMessage());
            }

			if(p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS))
            	p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);//priyank
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {  // getting database connection if it  is not already there
                    if (mcomCon == null) {
                    	mcomCon = new MComConnection();
                    	con=mcomCon.getConnection();
                    }
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + e.getMessage());
            }

            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            if (_recValidationFailMessageRequired) {
                // setting receiver return message
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_EVD, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                            .getRequestedAmount()) }));
                    }
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
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
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
			ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
            // setting success transaction status flag to false
            p_requestVO.setSuccessTxn(false);
            if(p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS))
            	p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);//priyank
            try {
                // getting database connection to unmark the users transaction
                // to completed
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    if (mcomCon == null) {
                    	mcomCon = new MComConnection();
                    	con=mcomCon.getConnection();
                    }
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + ex.getMessage());
            }
            // checking condition whether channel receiver required the general
            // failure message
            if (_recValidationFailMessageRequired) {
                // if receivermessage is null or it is not key
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // setting receiver return message
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_EVD, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            _log.errorTrace(methodName, e);
            
            

            // decreasing the transaction load count
            if (_transferID != null && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            // raising alarm
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[process]", _transferID, _senderMSISDN,
                _senderNetworkCode, "Exception:" + e.getMessage());
            // logging in the transaction log
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
			//ChannelRequestDailyLog
			ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
        }// end of catch
        finally {
            try {
                // Getting connection if it is null
                if (mcomCon == null) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                }
                p_requestVO.setStatus(_status);//setting transaction status
                
                // makking entry in the transfer table if transfer entry has not
                // been made and message gateway flow is common, i.e. validation
                // is not in thread
                //if (_transferID != null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)|| p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS)))) 
                if(_transferID!=null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) ||p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS))))
		        {
                    // added by nilesh: consolidated for logger
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[process]", _transferID,
                    _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            }
            if (con != null) {
                // committing transaction and closing connection
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                if (mcomCon != null)
					mcomCon.close("EVDController#process");
				mcomCon = null;
				con = null;
            }// end if

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
            // added by nilesh : consolidated for logger
            if (_oneLog) {
                OneLineTXNLog.log(_c2sTransferVO, _senderTransferItemVO, _receiverTransferItemVO);
            }
            // making entry in the transaction log
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
			//ChannelRequestDailyLog
			ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
			p_requestVO.setTransactionStatus(_c2sTransferVO.getTransferStatus());            
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
                // added by nilesh: consolidated for logger
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    ChannelTransferBL.updateC2STransferDetails(p_con, _c2sTransferVO);// add
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
            checkCounter();
            _log.error("processTransfer", _transferID, "BTSLBaseException while adding transfer details in database:" + be.getMessage());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[process]", _transferID, _senderMSISDN,
                _senderNetworkCode, "Exception:" + e.getMessage());
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[processSKeyGen]", _transferID,
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
            // Generating the EVD transfer ID
            _c2sTransferVO.setTransferDate(_currentDate);
            _c2sTransferVO.setTransferDateTime(_currentDate);
            // PretupsBL.generateEVDTransferID(_c2sTransferVO);
            // Transaction id would be generated in the memory.
            generateEVDTransferID(_c2sTransferVO);
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
            _requestVO.setValueObject(_c2sTransferVO);

        } catch (BTSLBaseException be) {
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            throw be;
        } catch (Exception e) {
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                if (_transferID != null) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD, new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                } else {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_EVD, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                        .getRequestedAmount()) }));
                }
            }
            // setting transfer status to FAIL
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[processTransfer]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
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
        _userBalancesVO = null;
        CommonClient commonClient = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        InterfaceVO interfaceVO = null;
        try {
            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !_processedFromQueue) {
                // Processing validation request in Thread
                processValidationRequestInThread();
            }
            /*
             * From here processing will be divided into two parts
             * 1.EVR
             * 1.1 Credit receiver on IN
             * 1.2 If credit is success then update the voucher to consume and
             * give differentials to sender
             * 1.3 If credit is fail then update the voucher to enable and
             * credit back the sender
             * 2.EVD
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
            if (_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
                LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_TOP);

                // Getting the receiver credit string from C2S transfer VO to be
                // sent to the Interface Module
                commonClient = new CommonClient();
                final String requestStr = getReceiverCreditStr();

                // Sending request to the common client
                TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                    requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
                final String receiverCreditResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeR, _intModIPR, _intModPortR, _intModClassNameR);
                TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                    receiverCreditResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, _transferID, "Got the response from IN Module receiverCreditResponse=" + receiverCreditResponse);
                }

                try {
                    // updating receiver credit response
                    updateForReceiverCreditResponse(receiverCreditResponse);
                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.RECEIVER_TOP_RESPONSE);
                } catch (BTSLBaseException be) {

                    TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                        PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL,
                        "Transfer Status=" + _c2sTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO.getInterfaceResponseCode());
                    // decreaseing the resposne counters and making it success
                    // in case of Ambiguous and Fail in case of fail
                    if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                        LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.RECEIVER_TOP_RESPONSE);
                    } else {
                        LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_TOP_RESPONSE);
                    }

                    // Check Status if Ambigous then credit back preference wise
                    // and Update the sender back for fail transaction
                    if ((_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CREDIT_BACK_ALWD_EVD_AMB))).booleanValue()) || _c2sTransferVO
                        .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                    }
                    // Validating the receiver Limits and updating it
                    if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                        PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
                    }
                    throw be;
                }// end catch BTSLBaseException
                catch (Exception e) {

                    TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                        PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL,
                        "Transfer Status=" + _c2sTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO.getInterfaceResponseCode());

                    // decreaseing the resposne counters and making it success
                    // in case of Ambiguous and Fail in case of fail
                    if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                        LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.RECEIVER_TOP_RESPONSE);
                    } else {
                        LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_TOP_RESPONSE);
                    }

                    // Update the sender back for fail transaction
                    // Check Status if Ambigous then credit back preference wise
                    if ((_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CREDIT_BACK_ALWD_EVD_AMB))).booleanValue()) || _c2sTransferVO
                        .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                    }

                    // Validating the receiver Limits and updating it
                    if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                        PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
                    }
                    throw new BTSLBaseException(this, methodName, "");
                }// end of catch Exception

                try {
                    // Consume status and mark status to consume and give
                    // diffrentials
                    final EvdUtil evdUtil = new EvdUtil();
                    interfaceVO = new InterfaceVO();
                    interfaceVO.setInterfaceId(_senderTransferItemVO.getInterfaceID());
                    interfaceVO.setHandlerClass(_senderTransferItemVO.getInterfaceHandlerClass());
                    _finalTransferStatusUpdate = evdUtil.updateVoucherAndGiveDifferentials(_receiverVO, _c2sTransferVO, _networkInterfaceModuleVO, interfaceVO, _requestVO
                        .getInstanceID(), false);
                } catch (BTSLBaseException be) {
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            methodName,
                            "For _transferID=" + _transferID + " Diff applicable=" + _c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting BTSL Base Exception=" + be.getMessage() + " Leaving transaction status as Under process");
                    }
                    _log.errorTrace(methodName, be);
                } catch (Exception e) {
                    _finalTransferStatusUpdate = false;
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            methodName,
                            "For _transferID=" + _transferID + " Diff applicable=" + _c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + _c2sTransferVO
                                .getDifferentialGiven() + " Not able to give Diff commission getting Exception=" + e.getMessage() + " Leaving transaction status as Under process");
                    }
                    _log.errorTrace(methodName, e);
                }

                // Meditel changes by Ashutosh
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    try {
                        if (mcomCon == null) {
                        	mcomCon = new MComConnection();
                        	con=mcomCon.getConnection();
                        }
                        boolean statusAllowed = false;
                        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(_channelUserVO.getNetworkID(), _channelUserVO.getCategoryCode(),
                            _channelUserVO.getUserType(), _requestVO.getRequestGatewayType());
                        if (userStatusVO == null) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
                        } else {
                            final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
                            final String status[] = userStatusAllowed.split(",");
                            for (int i = 0; i < status.length; i++) {
                                if (status[i].equals(_channelUserVO.getStatus())) {
                                    statusAllowed = true;
                                }
                            }

                          
                            PretupsBL
                                .chkAllwdStatusToBecomeActive(con, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG), _channelUserVO.getUserID(), _channelUserVO.getStatus());
                        }

                    } catch (Exception ex) {
                        _log.error("process", "Exception while changing user state to active  " + ex.getMessage());
                        _log.errorTrace(methodName, ex);
                    } finally {
                        if (con != null) {
                            try {
                            	mcomCon.finalCommit();
                            } catch (Exception e) {
                                _log.errorTrace(methodName, e);
                            }
                            if (mcomCon != null)
								mcomCon.close("EVDController#process");
							mcomCon = null;
							con = null;
                        }

                    }
                }
                // end of changes
            } else {
                _receiverPostBalanceAvailable = "N";
                // _vomsVO.setPinNo(BTSLUtil.encryptText(_vomsVO.getPinNo()));
                //    _vomsVO.setPinNo(VomsUtil.encryptText(_vomsVO.getPinNo())); 
                // perform step 2 of comments above
                // _requestVO.setEvdPin(BTSLUtil.decryptText(_vomsVO.getPinNo()));
               
                _vomsVO.setPinNo(VomsUtil.encryptText(_vomsVO.getPinNo()));
                _requestVO.setEvdPin(VomsUtil.decryptText(_vomsVO.getPinNo()));
               if (_log.isDebugEnabled()) {
                   _log.debug(methodName, _transferID, "_vomsVO.getPinNo()="+_vomsVO.getPinNo()+" , _requestVO.getEvdPin()="+_requestVO.getEvdPin());
               }
                sendSMS(_vomsVO);
            }
        }// end try
        catch (BTSLBaseException be) {
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
            }// end if
            if (be.isKey() && _c2sTransferVO.getSenderReturnMessage() == null) {
                btslMessages = be.getBtslMessages();
            } else if (_c2sTransferVO.getSenderReturnMessage() == null) {
                _c2sTransferVO.setSenderReturnMessage(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
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

            _log.errorTrace(methodName, e);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[run]", _transferID, _senderMSISDN,
                _senderNetworkCode, "Exception:" + e.getMessage());
            btslMessages = new BTSLMessages(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode,
                _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());

        }// end catch Exception
        finally {
            try {
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO
                    .getReceiverReturnMsg()).isKey())) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD), new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                }
                // decreasing transaction load count
                if (!_deliveryTrackDone) {
                    LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                }

                mcomCon = new MComConnection();
                con=mcomCon.getConnection();
                // Unmarking the receiver transaction status
                // In case of delivery tracking receiver is unmarked in delivery
                // receipt servlet
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus() && !_deliveryTrackDone) {
                    PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
                }
            }// end try
            catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                try {
                    if (con != null) {
                    	mcomCon.finalRollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[run]", _transferID, _senderMSISDN,
                    _senderNetworkCode, "Exception while updating Receiver last request status in database , Exception:" + e.getMessage());
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
                _log.errorTrace(methodName, be);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[run]", _transferID, _senderMSISDN,
                    _senderNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
            }
            // if connection is not null then comitting the transaction and
            // closing the connection
            if (con != null) {
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                if (mcomCon != null)
					mcomCon.close("EVDController#process");
				mcomCon = null;
				con = null;
            }
            // If transaction is fail and grouptype counters need to be decrease
            // then decrease the counters
            // This change has been done by ankit on date 14/07/06 for SMS
            // charging
            if (!_deliveryTrackDone && !_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && _requestVO.isDecreaseGroupTypeCounter() && ((ChannelUserVO) _requestVO
                .getSenderVO()).getUserControlGrouptypeCounters() != null) {
                PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO) _requestVO.getSenderVO()).getUserControlGrouptypeCounters());
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue() && (_c2sTransferVO.getServiceType().contains(PretupsI.SERVICE_TYPE_EVD))) {
                _receiverMessageSendReq = false;
            }
            
            if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
            	_requestVO.setSenderReturnMessage(getSenderSuccessMessage());
            }

            
            if (_receiverMessageSendReq && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedRecSendMessGatw) && !"ALL"
                .equals(_notAllowedRecSendMessGatw)) {
                if (_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    // Success message to receiver will be send only when the
                    // following condition is true:
                    // condition means either serviceType is EVR or (delivery
                    // receipt is not tracked and PIN is send to sender)
                    if (_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR) || (!_deliveryTrackDone && _senderMSISDN.equals(_c2sTransferVO.getPinSentToMsisdn()))) {
                        if (_c2sTransferVO.getReceiverReturnMsg() == null) {
                            (new PushMessage(_receiverMSISDN, getReceiverSuccessMessage(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                        } else if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                            final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                            (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                                _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                        } else {
                            (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(),
                                _receiverLocale)).push();
                        }
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

            // Message to sender will be send only when request gateway code is
            // allowed to send message
            if (!BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(), _notAllowedSendMessGatw)) {
                PushMessage pushMessages = null;
                if (btslMessages != null) {
                    // push final error message to sender
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
                    // push Additional Commission success message to sender and
                    // final status to sender
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
				}// end if
                 // Message to sender will send only when
                 // 1. Either service type is EVR
                 // 2. OR transaction is not success
                 // 3. OR delivery receipt is not tracked and PIN is send to
                 // receiver

                // if(_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)||!_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)||(!_deliveryTracktDone
                // &&
                // _receiverMSISDN.equals(_c2sTransferVO.getPinSentToMsisdn())))
                // vfe
                // ...if(_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)||!_deliveryTrackDone
                // &&
                // _receiverMSISDN.equals(_c2sTransferVO.getPinSentToMsisdn()))
                if (_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR) || !_c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) || (!_deliveryTrackDone && _receiverMSISDN
                    .equals(_c2sTransferVO.getPinSentToMsisdn()))) {
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
                            // if group type counters reach limit then send
                            // message using gateway that is associated with
                            // group type profile
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
            }

            // Log the credit back entry in the balance log
            if (_creditBackEntryDone) {
                BalanceLogger.log(_userBalancesVO);
            }

            // added by nilesh: consolidated for logger
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
            } // / ends here
            btslMessages = null;
            _userBalancesVO = null;
            commonClient = null;
            ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(_requestVO, _c2sTransferVO));
            ChannelGatewayRequestLog.outLog(_requestVO);

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, _transferID, "Exiting");
            }
        }// end of finally
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
        StringBuilder loggerValue= new StringBuilder(); 
        final String status = (String) map.get("TRANSACTION_STATUS");

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (_log.isDebugEnabled()) {
           	loggerValue.setLength(0);
        	loggerValue.append( "Mape from response=" );
        	loggerValue.append(map);
        	loggerValue.append(" status=");
        	loggerValue.append(status);
        	loggerValue.append(" interface Status=");
        	loggerValue.append(interfaceStatusType);
            _log.debug(methodName,loggerValue);
        }
       
        if (null != map.get("IN_START_TIME")) {
            _requestVO.setTopUPReceiverRequestSent((Long.valueOf((String) map.get("IN_START_TIME"))).longValue());
        }
        if (null != map.get("IN_END_TIME")) {
            _requestVO.setTopUPReceiverResponseReceived((Long.valueOf((String) map.get("IN_END_TIME"))).longValue());
        }
        
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        // setting transaction status for restricted subscriber feature
        if (PretupsI.STATUS_ACTIVE.equals((_channelUserVO.getCategoryVO()).getRestrictedMsisdns())) {
            ((RestrictedSubscriberVO) ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getRestrictedSubscriberVO()).setTempStatus(status);
        }

        _receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        _receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        
        try{_requestVO.setInCreditURL((String)map.get("IP"));_c2sTransferVO.setInfo7(_requestVO.getInCreditURL());      }catch(Exception e){_log.errorTrace(methodName, e);}
        try{_requestVO.setCreditINRespCode(_receiverVO.getInterfaceResponseCode()); _c2sTransferVO.setInfo8(_requestVO.getCreditINRespCode());}catch(Exception ex){_log.errorTrace(methodName, ex);}
        
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
        String[] strArr ;
        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            _c2sTransferVO.setErrorCode(status + "_R");
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(status);
            strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            // throw new
            // BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.C2S_RECEIVER_FAIL,0,strArr,null);
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", _c2sTransferVO.getErrorCode(), 0, strArr, null);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
            _c2sTransferVO.setErrorCode(status + "_R");
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _status = PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS;
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _receiverTransferItemVO.setUpdateStatus(status);
            strArr = new String[] { _transferID, _receiverMSISDN, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        } else {
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _status = PretupsErrorCodesI.TXN_STATUS_SUCCESS;
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
            _log.error("EVDController[processValidationRequestInThread]", loggerValue );
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
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[processValidationRequestInThread]",
                _transferID, _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            // !_transferDetailAdded Condition Added as we think its not require
            // as already done
            if (_transferID != null && !_transferDetailAdded) {
                Connection con = null;
                MComConnectionI mcomCon = null;
                try {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                    // added by nilesh: consolidated for logger
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
                        	mcomCon.finalRollback();
                        } catch (Exception ex) {
                            _log.errorTrace(methodName, ex);
                        }
                    }
                    if (mcomCon != null)
						mcomCon.close("EVDController#process");
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
						mcomCon.close("EVDController#process");
					mcomCon = null;
					con = null;
                    _log.errorTrace(methodName, e);
                    loggerValue.setLength(0);
                	loggerValue.append("Exception:");
                	loggerValue.append(e.getMessage());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "EVDController[processValidationRequestInThread]", _transferID, _senderMSISDN, _senderNetworkCode,  loggerValue.toString());
                } finally {
                    if (mcomCon != null)
						mcomCon.close("EVDController#process");
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
        _receiverTransferItemVO.setLanguage(_receiverLocale.getLanguage());
        _receiverTransferItemVO.setCountry(_receiverLocale.getCountry());

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
     *            p_serviceType : EVD/EVR
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
                    // useAlternateCategory=_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    // _defaultSelector=_serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();

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
                        if (subscriberRoutingControlVO.getInterfaceCategory().equals(_serviceInterfaceRoutingVO.getAlternateInterfaceType()) && PretupsI.SERVICE_TYPE_EVR
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
                    // EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"C2SEnquiryHandler[process]","","","","Service Interface Routing control Not defined");
                    // p_interfaceCategory=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                }

                subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
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
                    // useAlternateCategory=_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    // _defaultSelector=_serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                } else {
                    _log.info("process", _requestVO.getRequestIDStr(),
                        "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SEnquiryHandler[process]", "", "", "",
                        "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    // p_interfaceCategory=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                }

                subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
            }

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
                    isSuccess = checkInterfaceMappingv01(p_prefixID,
							p_networkCode, p_interfaceCategory, p_action);
                } else {
                    isSuccess = false;
                }
            } else {
                // This event is raised by ankit Z on date 3/8/06 for case when
                // entry not found in routing control and considering series
                // based routing
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[getInterfaceRoutingDetails]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Routing control information not defined so performing series based routing");
                isSuccess = checkInterfaceMappingv01(p_prefixID, p_networkCode,
						p_interfaceCategory, p_action);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[getInterfaceRoutingDetails]",
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
        InterfaceVO interfaceVO = null;
        final String methodName = "processValidationRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered and performing validations for transfer ID=" + _transferID + " " + _c2sTransferVO.getModule() + " " + _c2sTransferVO
                .getReceiverNetworkCode() + " " + _type);
        }

        try {
            final CommonClient commonClient = new CommonClient();
            InterfaceVO recInterfaceVO = null;
            _itemList = new ArrayList();
            _itemList.add(_senderTransferItemVO);
            _itemList.add(_receiverTransferItemVO);
            _c2sTransferVO.setTransferItemList(_itemList);

            // If service is EVR then validate the receiver on IN
            if (_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
            	
            	 final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_c2sTransferVO.getModule(),
                         _c2sTransferVO.getReceiverNetworkCode(), _type);
                     _intModCommunicationTypeR = networkInterfaceModuleVOS.getCommunicationType();
                     _intModIPR = networkInterfaceModuleVOS.getIP();
                     _intModPortR = networkInterfaceModuleVOS.getPort();
                     _intModClassNameR = networkInterfaceModuleVOS.getClassName();
                     _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                     _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                     
            	_receiverInterfaceInfoInDBFound = true;
            }

            // Till here we get the IN interface validation response.. if the
            // service is EVR
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // Get the service Class ID based on the code
            PretupsBL.validateServiceClassChecks(con, _receiverTransferItemVO, _c2sTransferVO, PretupsI.C2S_MODULE, _requestVO.getServiceType());

            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());

            // validate sender receiver service class,validate transfer value
            PretupsBL.validateTransferRule(con, _c2sTransferVO, PretupsI.C2S_MODULE);

            if (!_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
                if (_receiverTransferItemVO.getPreviousExpiry() == null) {
                    _receiverTransferItemVO.setPreviousExpiry(_currentDate);
                }
            }

            // calculate card group details
            CardGroupBL.calculateCardGroupDetails(con, _c2sTransferVO, PretupsI.C2S_MODULE, true);

            try {
            	mcomCon.finalCommit();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }
            if (mcomCon != null)
				mcomCon.close("EVDController#process");
			mcomCon = null;
			con = null;

            // ***Construct & validate VOMS validation request using common
            // client*************
            _networkInterfaceModuleVO = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_c2sTransferVO.getModule(), _c2sTransferVO.getReceiverNetworkCode(),
                PretupsI.INTERFACE_CATEGORY_VOMS);
            final EvdUtil evdUtil = new EvdUtil();
            interfaceVO = new InterfaceVO();
            interfaceVO.setInterfaceId(_senderTransferItemVO.getInterfaceID());
            recInterfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
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
                VomsVoucherChangeStatusLog.log(_transferID, extraPrefixSerialNumber+_vomsVO.getSerialNo(), VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_UNPROCESS, _c2sTransferVO.getReceiverNetworkCode(),
                    _channelUserVO.getUserID(), BTSLUtil.getDateTimeStringFromDate(_currentDate));
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
                    PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
                }
                throw be;
            } catch (Exception e) {
                LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

                // validate receiver limits after Interface Validations
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                    PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
                }
                throw e;
            }
            _voucherMarked = true;

            LoadController.decreaseResponseCounters(_transferID, _receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

            // If request is taking more time till validation of subscriber than
            // reject the request.
            InterfaceVO vomsInterfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(interfaceVO.getInterfaceId());
            if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > vomsInterfaceVO.getValExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDController[processValidationRequest]",
                    _transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till validation of voucher");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
            }
            vomsInterfaceVO = null;

            // This method will set various values into items and transferVO
            evdUtil.calulateTransferValue(_c2sTransferVO, _vomsVO);

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Validity=" + _c2sTransferVO.getReceiverValidity() + " Talk Time=" + _c2sTransferVO.getReceiverTransferValue() + " Serial number=" +extraPrefixSerialNumber+ _vomsVO.getSerialNo(),
                PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                PretupsBL.validateRecieverLimits(null, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
            }

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            // Here the code for debiting the user account will come and Update
            // Transfer Out Counts for the sender
            _userBalancesVO = ChannelUserBL.debitUserBalanceForProduct(con, _transferID, _c2sTransferVO);
			_c2sTransferVO.setSenderPostBalance(_userBalancesVO.getBalance());
            ChannelTransferBL.increaseC2STransferOutCounts(con, _c2sTransferVO, true);

        	/*if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,_c2sTransferVO.getNetworkCode()))
			{
				  ChannelTransferBL.increaseUserOTFCounts(con, _c2sTransferVO, _channelUserVO);
			}*/
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _status = PretupsErrorCodesI.TXN_STATUS_SUCCESS;
            _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

            populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
			if(PretupsI.SERVICE_TYPE_EVD.equals(_c2sTransferVO.getServiceType())||PretupsI.SERVICE_TYPE_EVD101.equals(_c2sTransferVO.getServiceType())||PretupsI.SERVICE_TYPE_EVD102.equals(_c2sTransferVO.getServiceType())||PretupsI.SERVICE_TYPE_EVD104.equals(_c2sTransferVO.getServiceType())||PretupsI.SERVICE_TYPE_EVD105.equals(_c2sTransferVO.getServiceType()))
			{
                _receiverTransferItemVO.setServiceClass(_vomsAllServiceClassID);
                final String pinSendTo = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_SEND_TO));
                // Construct the PIN message for sender or receiver as the case
                // is
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "PIN sent to in preference=" + pinSendTo);
                }
                // changed for EVD private recharge (as subservice =1 )
				if(PretupsI.PIN_SENT_RET.equals(pinSendTo)) {
                    _c2sTransferVO.setPinSentToMsisdn(_senderMSISDN);
                } else {
                    _c2sTransferVO.setPinSentToMsisdn(_receiverMSISDN);
                }
            }
            _senderTransferItemVO.setServiceClass(_vomsAllServiceClassID);
            // Method to insert the record in c2s transfer table
            // added by nilesh: consolidated for logger
            if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO);
            }
            _transferDetailAdded = true;
            // Commit the transaction and relaease the locks
            try {
            	mcomCon.finalCommit();
            } catch (Exception be) {
                _log.errorTrace(methodName, be);
            }
            if (mcomCon != null)
				mcomCon.close("EVDController#process");
			mcomCon = null;
			con = null;

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Marked Under process, voucher Serial number=" + _vomsVO.getSerialNo(), PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");

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

            // If request is taking more time till validation of subscriber than
            // reject the request.
            // intrfaceVO=(InterfaceVO)NetworkInterfaceModuleCache.getObject(interfaceVO.getInterfaceId());
            if (_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
                if ((System.currentTimeMillis() - _c2sTransferVO.getRequestStartTime()) > recInterfaceVO.getTopUpExpiryTime()) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDController[processValidationRequest]",
                        _transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till topup");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
                }
                recInterfaceVO = null;
            }

            if (_c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue) {
                // create new Thread
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
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                }
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
            }
            _log.error("EVDController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());

            voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            if (con != null) {
            	mcomCon.finalRollback();
            }
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            if (_recValidationFailMessageRequired) {
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD), new String[] { String.valueOf(_transferID), PretupsBL
                        .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                }
            } 
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        } finally {
            if (mcomCon != null)
				mcomCon.close("EVDController#process");
			mcomCon = null;
			con = null;
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
         	loggerValue.append("Entered _transferID=" );
         	loggerValue.append(_transferID);
         	loggerValue.append(" p_vomsVO=");
         	loggerValue.append(p_vomsVO);
            _log.debug(methodName, loggerValue );
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
                // binaryPinMessage=BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.BIN_PIN_MESSAGE_FOR_R,new
                // String[]{_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_receiverMSISDN,BTSLUtil.decryptText(p_vomsVO.getPinNo()),p_vomsVO.getSerialNo(),p_vomsVO.getExpiryDateStr()});
                binaryPinMessage = BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.BIN_PIN_MESSAGE_FOR_R,
                    new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _receiverMSISDN, VomsUtil.decryptText(p_vomsVO
                        .getPinNo()), p_vomsVO.getSerialNo(), p_vomsVO.getExpiryDateStr() });
                //binaryPinMessage = _operatorUtil.DES3Encryption(binaryPinMessage, _requestVO);
                // pinMessage=BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.PIN_MESSAGE_FOR_R,new
                // String[]{_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_receiverMSISDN,BTSLUtil.decryptText(p_vomsVO.getPinNo()),p_vomsVO.getSerialNo()});
                pinMessage = BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.PIN_MESSAGE_FOR_R, new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO
                    .getRequestedAmount()), _receiverMSISDN, VomsUtil.decryptText(p_vomsVO.getPinNo()), p_vomsVO.getSerialNo() });
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
                    .getRequestedAmount()), _senderMSISDN, VomsUtil.decryptText(p_vomsVO.getPinNo()), p_vomsVO.getSerialNo() });
                locale = _receiverLocale;
            }
            final PushMessage pushMessage = new PushMessage(_c2sTransferVO.getPinSentToMsisdn(), pinMessage, _transferID, _c2sTransferVO.getRequestGatewayCode(), locale);
            PushMessage pushMessage1 = null;
            if (_requestVO.getPrivateRechBinMsgAllowed()) {
                pushMessage1 = new PushMessage(_c2sTransferVO.getPinSentToMsisdn(), binaryPinMessage, _transferID, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECH_MESSGATEWAY)), locale);
            }
            String retKannstatus = null ;
            String retBinKannstatus = null ;

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
                // updateMySqlCt=1;
                if (updateMySqlCt > 0) {
                    // Push underprocess message with receipt
                    if (smsChargingRequired) {
                    	if (pushMessage!=null && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)) {
                        retKannstatus = pushMessage.pushSmsUrlWithReceipt(false, _c2sTransferVO.getTransferID(), groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO
                            .getAltGatewayCode());
                    	}
                        if (_requestVO.getPrivateRechBinMsgAllowed()&&pushMessage1!=null && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)) {
                            retBinKannstatus = pushMessage1.pushSmsUrlWithReceipt(false, _c2sTransferVO.getTransferID(), groupTypeProfileVO.getGatewayCode(),
                                groupTypeProfileVO.getAltGatewayCode());
                        }
                    } else {
                    	if (pushMessage!=null && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)) {
                        retKannstatus = pushMessage.pushSmsUrlWithReceipt(false, _c2sTransferVO.getTransferID(), null, null);
                    	}
                        if (_requestVO.getPrivateRechBinMsgAllowed()&&pushMessage1!=null && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)) {
                            retBinKannstatus = pushMessage1.pushSmsUrlWithReceipt(false, _c2sTransferVO.getTransferID(), null, null);
                        }
                    }
                    if(_log.isDebugEnabled()) {
	        	 		_log.debug("sendSMS","_bypassSenderMessageDeliveryStatus="+_bypassSenderMessageDeliveryStatus+" , _transferID="+_transferID+", retKannstatus="+retKannstatus);
	        	 	}
	        	 	if((_bypassSenderMessageDeliveryStatus||BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedSendMessGatw)) && (BTSLUtil.isNullString(retKannstatus)||!retKannstatus.equals(PretupsI.GATEWAY_MESSAGE_SUCCESS))){
	        	 		retKannstatus=PretupsI.GATEWAY_MESSAGE_SUCCESS;
	        	 	}	        	 	
                    if (!PretupsI.GATEWAY_MESSAGE_SUCCESS.equalsIgnoreCase(retKannstatus)) // &&
                    // !retBinKannstatus.equalsIgnoreCase(PretupsI.GATEWAY_MESSAGE_SUCCESS)
                    {
                        // credit back the sender and voucher
                    	if(_bypassSenderMessageDeliveryStatus){
	                		 creditbackdone=false;
	                	} else {
	                        creditbackdone = true;
	                        _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							_status = PretupsErrorCodesI.TXN_STATUS_FAIL;
	                        _c2sTransferVO.setErrorCode(PretupsErrorCodesI.VMS_PIN_SENT_FAIL);
	                        voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
	                        // Added so that Failed Counters can be increased
	                        ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),
	                            _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
	                	}
                    } else {
                        if (_log.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append( "Transfer ID=" );
                        	loggerValue.append(_transferID);
                        	loggerValue.append(" Message Received by kannel Got Status=" );
                        	loggerValue.append(retKannstatus);
                        	loggerValue.append(" leave the controller now and wait for Delivery Receipt from kannel");
                            _log.debug( methodName,loggerValue);
                        }
                        _finalTransferStatusUpdate = false;
                        _deliveryTrackDone = true;
                    }
                } else {
                	 if(_bypassSenderMessageDeliveryStatus){
                		 creditbackdone=false;
                	 } else { 
	                    creditbackdone = true;
	                    _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						_status = PretupsErrorCodesI.TXN_STATUS_FAIL;
	                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.VMS_PIN_SENT_FAIL);
	                    voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
	                    // Added so that Failed Counters can be increased
	                    ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),
	                        _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
                	 }
                }
            } else // Delivery receipt not required.
            {
                // The condition below will check if Kannel has accepted the
                // message or not.
                // If kannel accepted the message then it return the success and
                // we will give differentials to sender and also mark the
                // voucher to consume
                if (smsChargingRequired) {
                	if (pushMessage!=null && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)) {
                    retKannstatus = pushMessage.pushMessageWithStatus(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());
                	}
                    if (_requestVO.getPrivateRechBinMsgAllowed()&&pushMessage1!=null && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)) {
                        retBinKannstatus = pushMessage1.pushMessageWithStatus(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());
                    }
                } else {
                	if (pushMessage!=null && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)) {
                    retKannstatus = pushMessage.pushMessageWithStatus(null, null);
                	}
                    if (_requestVO.getPrivateRechBinMsgAllowed()&&pushMessage1!=null && !BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedRecSendMessGatw)) {
                        retBinKannstatus = pushMessage1.pushMessageWithStatus(null, null);
                    }
                }
                if(_log.isDebugEnabled()) {
        	 		_log.debug("sendSMS","_bypassSenderMessageDeliveryStatus="+_bypassSenderMessageDeliveryStatus+" , _transferID="+_transferID+", retKannstatus="+retKannstatus);
        	 	}
        	 	if((_bypassSenderMessageDeliveryStatus||BTSLUtil.isStringIn(_c2sTransferVO.getRequestGatewayCode(),_notAllowedSendMessGatw)) && (BTSLUtil.isNullString(retKannstatus)||!retKannstatus.equals(PretupsI.GATEWAY_MESSAGE_SUCCESS))){
        	 		retKannstatus=PretupsI.GATEWAY_MESSAGE_SUCCESS;
        	 	}
                if (PretupsI.GATEWAY_MESSAGE_SUCCESS.equalsIgnoreCase(retKannstatus)) // &&
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
                	if(_bypassSenderMessageDeliveryStatus){
               		 creditbackdone=false;
               	 } else {
               		 creditbackdone=true;
                    _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.VMS_PIN_SENT_FAIL);
                    voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                    // Added so that Failed Counters can be increased
                    ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(),
                        _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
               	 }
                }
            }
        } catch (BTSLBaseException be) {
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
            }// end if
            if (!creditbackdone) {
                voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            }
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }

            if (!creditbackdone) {
                voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            }
            throw new BTSLBaseException(this, methodName, "");
        } finally {
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exited _transferID=");
            	loggerValue.append(_transferID);
            	loggerValue.append(" _c2sTransferVO.getTransferStatus()=");
            	loggerValue.append(_c2sTransferVO.getTransferStatus());
                _log.debug(methodName,  loggerValue );
            }

		}
	}
	
	
	/**updateForVOMSValidationResponse
	 * Method to process the response of the receiver validation from VOMS
	 * @param str
	 * @throws BTSLBaseException
	 */
	
	public void updateForVOMSValidationResponse(String str) throws BTSLBaseException
	{
		final String methodName = "updateForVOMSValidationResponse";
		StringBuilder loggerValue= new StringBuilder(); 
		if(_log.isDebugEnabled()) {
			 loggerValue.setLength(0);
         	loggerValue.append("Entered=");
         	loggerValue.append(_receiverSubscriberType); 
			_log.debug(methodName,loggerValue);
		}
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
		_status = status;
		_senderTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		_receiverVO.setInterfaceResponseCode(_senderTransferItemVO.getInterfaceResponseCode());		
		_senderTransferItemVO.setValidationStatus(status);
		_senderTransferItemVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));
		try{
		_receiverTransferItemVO.setProtocolStatus((String)map.get("PROTOCOL_STATUS"));
		_receiverTransferItemVO.setSubscriberType(_receiverSubscriberType);
		_receiverTransferItemVO.setReferenceID((String)map.get("IN_RECON_ID"));
		_receiverTransferItemVO.setInAccountId((String)map.get("IN_ACCOUNT_ID"));
		_receiverTransferItemVO.setAccountStatus((String)map.get("ACCOUNT_STATUS"));
		_receiverTransferItemVO.setValidationStatus(status);
		_receiverTransferItemVO.setUpdateStatus(status);	
		_receiverTransferItemVO.setInterfaceResponseCode((String)map.get("INTERFACE_STATUS"));
		_receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());	
		_receiverTransferItemVO.setInterfaceReferenceID((String)map.get("IN_TXN_ID"));
		_c2sTransferVO.setReceiverSubscriberType(_receiverSubscriberType);
		_c2sTransferVO.setSubscriberType(_receiverSubscriberType);
		if (null != map.get("IN_START_TIME")) {
			_requestVO.setValidationReceiverRequestSent(((Long.valueOf((String)map.get("IN_START_TIME"))).longValue()));
		}
		if (null != map.get("IN_END_TIME")) {
			_requestVO.setValidationReceiverResponseReceived(((Long.valueOf((String)map.get("IN_END_TIME"))).longValue()));
		//end 07/02/2008
		}
		try{_requestVO.setInValidateURL((String)map.get("IP"));	_c2sTransferVO.setInfo7(_requestVO.getInValidateURL());}catch(Exception e){_log.errorTrace(methodName, e);}
		try{_requestVO.setValINRespCode(_receiverVO.getInterfaceResponseCode());_c2sTransferVO.setInfo6(_requestVO.getValINRespCode());}catch(Exception ex){_log.errorTrace(methodName, ex);}
		try{_c2sTransferVO.setInfo9((String)map.get("SERVICE_CLASS"));}catch(Exception ex1){_log.errorTrace(methodName, ex1);}

		}catch(Exception ex){
			_log.errorTrace(methodName, ex);
		}
		
		//If status is other than Success in validation stage mark sender request as Not applicable and
		//Make transaction Fail
		String [] strArr;
		if(BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS))
		{
			_c2sTransferVO.setErrorCode(status+"_S");
			_senderTransferItemVO.setTransferStatus(status);
			if(PretupsI.SERVICE_TYPE_EVD.equals(_serviceType)||PretupsI.SERVICE_TYPE_EVD101.equals(_serviceType)||PretupsI.SERVICE_TYPE_EVD102.equals(_serviceType)||PretupsI.SERVICE_TYPE_EVD104.equals(_serviceType)||PretupsI.SERVICE_TYPE_EVD105.equals(_serviceType)||PretupsI.SERVICE_TYPE_EVD106.equals(_serviceType))
			{
				_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				_receiverTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
				_receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
			}
			strArr=new String[]{_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount())};
			throw new BTSLBaseException(this,"updateForReceiverValidateResponse",_c2sTransferVO.getErrorCode(),0,strArr,null);
		}
		_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
		_status = PretupsErrorCodesI.TXN_STATUS_SUCCESS;
		_senderTransferItemVO.setTransferStatus(status);
		_receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
			//Set the service class received from the IN.
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"SERVICE_CLASS="+(String)map.get("SERVICE_CLASS"));
		}
		_receiverTransferItemVO.setServiceClassCode((String)map.get("SERVICE_CLASS"));
		
 		 _vomsVO=new VomsVoucherVO();
		 _vomsVO.setProductID((String)map.get("PRODUCT_ID"));
		 _vomsVO.setSerialNo((String)map.get("SERIAL_NUMBER"));
		 _vomsVO.setProductName((String)map.get("PRODUCT_NAME"));		 
		 if("null".equals((String)map.get("SERIAL_NUMBER"))) {
			throw new BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.VOUCHER_NOT_FOUND);
		}
		
		 _vomsVO.setTalkTime(Long.parseLong((String)map.get("TALK_TIME")));
		 _vomsVO.setValidity(Integer.parseInt((String)map.get("VALIDITY")));
		 _vomsVO.setPinNo((String)map.get("PIN"));
		 try
		 {
			 if(_log.isDebugEnabled()) {
					_log.debug(methodName,(String)map.get("VOUCHER_EXPIRY_DATE")+"  " +(String)map.get("EXPIRY_DATE"));
			}
			 
			 Date expDate = null;
			 try {expDate=BTSLUtil.getDateFromDateString((String)map.get("VOUCHER_EXPIRY_DATE"),"yyyyMMdd");}catch(Exception e) {
				 expDate=BTSLUtil.getDateFromDateString((String)map.get("VOUCHER_EXPIRY_DATE"),"yyyy-MM-dd");
			 }
			 
			 _vomsVO.setExpiryDateStr(BTSLUtil.getDateStringFromDate(expDate));
			 _vomsVO.setExpiryDate(expDate);
			 if(BTSLUtil.isNullString(_vomsVO.getExpiryDateStr())){
				 _vomsVO.setExpiryDateStr((String)map.get("EXPIRY_DATE"));
			 }
		 }
		 catch(Exception e){
			 _log.errorTrace(methodName,e);
			 _vomsVO.setExpiryDateStr((String)map.get("VOUCHER_EXPIRY_DATE"));
			 if(BTSLUtil.isNullString(_vomsVO.getExpiryDateStr())){
				 _vomsVO.setExpiryDateStr((String)map.get("EXPIRY_DATE"));
			 }
		 }
		 if(_log.isDebugEnabled()) {
			 loggerValue.setLength(0);
         	 loggerValue.append(_vomsVO.getProductID());
         	 loggerValue.append(_vomsVO.getProductName());
         	loggerValue.append(_vomsVO.getExpiryDateStr());
         	loggerValue.append(", ");
         	loggerValue.append(prepareOtherInfo(extraPrefixOtherInfo, _vomsVO.getOtherInfo()));
				_log.debug(methodName, loggerValue);
		 }
		 _c2sTransferVO.setSerialNumber((String)map.get("SERIAL_NUMBER"));
		 _senderTransferItemVO.setTransferValue(Long.parseLong((String)map.get("PAYABLE_AMT")));
		 SubscriberRoutingControlVO subscriberRoutingControlVO=SubscriberRoutingControlCache.getRoutingControlDetails(_c2sTransferVO.getReceiverNetworkCode()+"_"+_c2sTransferVO.getServiceType()+"_"+PretupsI.INTERFACE_CATEGORY_VOMS);
		 if(!_vomsInterfaceInfoInDBFound && subscriberRoutingControlVO!=null && subscriberRoutingControlVO.isDatabaseCheckBool())
		 {
			PretupsBL.insertSubscriberInterfaceRouting(_senderTransferItemVO.getInterfaceID(),_vomsExternalID,_receiverMSISDN,PretupsI.INTERFACE_CATEGORY_VOMS,_channelUserVO.getUserID(),_currentDate);
			_vomsInterfaceInfoInDBFound=true;
		 }
         
         if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
         {
             String payAmt=(String)map.get("RECEIVER_PAYABLE_AMT");
             if(!BTSLUtil.isNullString(payAmt)&& BTSLUtil.isNumeric(payAmt)) {
				_payableAmt=PretupsBL.getDisplayAmount(Long.parseLong(payAmt));
			}
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
		//added for CRE_INT_CR00029 by ankit Zindal
		strBuff.append("&CARD_GROUP_SELECTOR="+_requestVO.getReqSelector());
		strBuff.append("&USER_TYPE=R");
		strBuff.append("&REQ_SERVICE="+_serviceType);
		strBuff.append("&INT_ST_TYPE="+_c2sTransferVO.getReceiverInterfaceStatusType());
		return strBuff.toString();
	}
	
	/**
	 * Method to get the string to be sent to the interface for topup
	 * @return
	 */
	public String getReceiverCreditStr()
	{
		final String methodName = "getReceiverCreditStr";
		StringBuffer strBuff;
		strBuff=new StringBuffer(getReceiverCommonString());
		strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_CREDIT_ACTION);
		strBuff.append("&INTERFACE_AMOUNT="+_c2sTransferVO.getReceiverTransferValue());
		strBuff.append("&GRACE_DAYS="+_receiverTransferItemVO.getGraceDaysStr());
		strBuff.append("&SENDER_MSISDN="+_senderMSISDN);
		strBuff.append("&SENDER_ID="+_channelUserVO.getUserID());
		strBuff.append("&SENDER_EXTERNAL_CODE="+_channelUserVO.getExternalCode());
		strBuff.append("&PRODUCT_CODE="+_c2sTransferVO.getProductCode());
		strBuff.append("&VALIDITY_DAYS="+_c2sTransferVO.getReceiverValidity());
		strBuff.append("&BONUS_VALIDITY_DAYS="+_c2sTransferVO.getReceiverBonusValidity());
		strBuff.append("&BONUS_AMOUNT="+_c2sTransferVO.getReceiverBonusValue());
		try{strBuff.append("&OLD_EXPIRY_DATE="+BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getPreviousExpiry()));}catch(Exception e){_log.errorTrace(methodName, e);}
		strBuff.append("&INTERFACE_PREV_BALANCE="+_receiverTransferItemVO.getPreviousBalance());
		strBuff.append("&SERIAL_NUMBER="+_vomsVO.getSerialNo());
		//strBuff.append("&PIN="+BTSLUtil.decryptText(_vomsVO.getPinNo()));
		strBuff.append("&PIN="+VomsUtil.decryptText(_vomsVO.getPinNo()));
		// Avinash send the requested amount to IN. to use card group only for reporting purpose.
		strBuff.append("&REQUESTED_AMOUNT="+_c2sTransferVO.getRequestedAmount());
		if(!BTSLUtil.isNullString(_channelUserVO.getLongitude()))
			strBuff.append("&CELL_ID="+_channelUserVO.getLongitude());
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
			if(_log.isDebugEnabled()) {
				_log.debug(methodName,"Entered");
			}
		HashMap map=BTSLUtil.getStringToHash(str,"&","=");
		String status=(String)map.get("TRANSACTION_STATUS");
		ArrayList altList=null;
		boolean isRequired=false;
		
		if (null != map.get("IN_START_TIME")) {
			_requestVO.setValidationReceiverRequestSent(((Long.valueOf((String)map.get("IN_START_TIME"))).longValue()));
		}
		if (null != map.get("IN_END_TIME")) {
			_requestVO.setValidationReceiverResponseReceived(((Long.valueOf((String)map.get("IN_END_TIME"))).longValue()));
		//end 07/02/2008
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
            _receiverTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
            _receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
            _receiverTransferItemVO.setValidationStatus(status);
            _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());
            _receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            _receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
            _receiverTransferItemVO.setSubscriberType(_receiverSubscriberType);
            
            try{_requestVO.setInValidateURL((String)map.get("IP"));	_c2sTransferVO.setInfo5(_requestVO.getInValidateURL());}catch(Exception e){_log.errorTrace(methodName, e);}
    		try{_requestVO.setValINRespCode(_receiverVO.getInterfaceResponseCode());_c2sTransferVO.setInfo6(_requestVO.getValINRespCode());}catch(Exception ex){_log.errorTrace(methodName, ex);}
    		try{_c2sTransferVO.setInfo9((String)map.get("SERVICE_CLASS"));}catch(Exception ex1){_log.errorTrace(methodName, ex1);}
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
                strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
                throw new BTSLBaseException(this, methodName, _c2sTransferVO.getErrorCode(), 0, strArr, null);
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
                                // if((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)).equals("en"))
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
                                // if((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)).equals("en"))
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
                                        // if((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)).equals("en"))
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
                                    // commonClient=new CommonClient();

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
        	loggerValue.append( "Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[performAlternateRouting]", _transferID,
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
                if (_serviceType.equals(PretupsI.SERVICE_TYPE_EVR)) {
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
            _log.error("EVDController[checkTransactionLoad]", loggerValue );
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
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
            _log.error("EVDController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        }
    }

	private void debugcheckTransactionLoad() {
		if (_log.isDebugEnabled()) {
		    _log.debug("EVDController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
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
            strArr = new String[] { _transferID, PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD, 0, strArr, null);
        }

        _receiverTransferItemVO.setTransferStatus(status);
        _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

        try {
            _receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        ;
        try {
            _receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[updateReceiverLocale]",
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
		String key;
		if(PretupsI.SERVICE_TYPE_EVD.equals(_c2sTransferVO.getServiceType())) {
			key=PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS+"_"+PretupsI.SERVICE_TYPE_EVD;
		} else {
			key=PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS;
		}
		String[] messageArgArray;		
		if (BTSLUtil.isNullString(_sid)) {
			messageArgArray= new String[]{_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),String.valueOf(_receiverTransferItemVO.getValidity()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),_c2sTransferVO.getProductName()};
		} else {
			messageArgArray= new String[]{_sid,_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),String.valueOf(_receiverTransferItemVO.getValidity()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),_c2sTransferVO.getProductName()};
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
		if(!"N".equals(_receiverPostBalanceAvailable))
		{
			String dateStrGrace=null;
			String dateStrValidity;
			//Changed by ankit Zindal on date 2/08/06 for problem when validity and grace date is null 
			try{dateStrGrace=(_receiverTransferItemVO.getNewGraceDate()==null)?"0":BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewGraceDate());}catch(Exception e){
				_log.errorTrace(methodName, e);
				dateStrGrace=String.valueOf(_receiverTransferItemVO.getNewGraceDate());}
			try{dateStrValidity=(_receiverTransferItemVO.getNewExpiry()==null)?"0":BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewExpiry());}catch(Exception e){
				_log.errorTrace(methodName, e);
				dateStrValidity=String.valueOf(_receiverTransferItemVO.getNewExpiry());}
			if(!BTSLUtil.isNullString(_payableAmt)) {
				messageArgArray=new String[]{_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()),String.valueOf(_receiverTransferItemVO.getValidity()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()),_senderMSISDN,dateStrGrace,dateStrValidity,_payableAmt,PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()),_c2sTransferVO.getSubService(),_channelUserVO.getUserName(),PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),prepareOtherInfo(extraPrefixOtherInfo, _vomsVO.getOtherInfo()),_c2sTransferVO.getProductName()};
			} else {
				messageArgArray=new String[]{_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()),String.valueOf(_receiverTransferItemVO.getValidity()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()),_senderMSISDN,dateStrGrace,dateStrValidity,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()),_c2sTransferVO.getSubService(),_channelUserVO.getUserName(),_payableAmt,prepareOtherInfo(extraPrefixOtherInfo, _vomsVO.getOtherInfo()),_c2sTransferVO.getProductName()};
			}
			key=PretupsErrorCodesI.EVD_RECEIVER_SUCCESS;//return BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_SUCCESS,messageArgArray);
		}
		else
		{
			try
            {
                if(!BTSLUtil.isNullString(_payableAmt)) {
					messageArgArray=new String[]{_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()),_senderMSISDN,_payableAmt,PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()),_c2sTransferVO.getSubService(),_channelUserVO.getUserName(),PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),prepareOtherInfo(extraPrefixOtherInfo, _vomsVO.getOtherInfo()),_c2sTransferVO.getProductName()};
				} else {
					messageArgArray=new String[]{_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverTransferValue()),_senderMSISDN,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()),_c2sTransferVO.getSubService(),_channelUserVO.getUserName(),_payableAmt,prepareOtherInfo(extraPrefixOtherInfo, _vomsVO.getOtherInfo()),_c2sTransferVO.getProductName()};
				}
            }catch(Exception e){_log.errorTrace(methodName, e);}
			key=PretupsErrorCodesI.EVD_RECEIVER_SUCCESS_WITHOUT_POSTBAL;//return BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
		}
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_EVD))).booleanValue())
		{
			String message=null;
			try
			{
				message= BTSLUtil.getMessage(_receiverLocale,key+"_"+_receiverTransferItemVO.getServiceClass(),messageArgArray,_requestVO.getRequestGatewayType());
				if(!BTSLUtil.isNullString(message)) {
					return message;
				}
			}
			catch(Exception e){_log.errorTrace(methodName, e);	}
		}
		return BTSLUtil.getMessage(_receiverLocale,key,messageArgArray,_requestVO.getRequestGatewayType());
	}
	
	private String getSenderSuccessMessage()
	{
		String[] messageArgArray;
		if (_log.isDebugEnabled()) {
            _log.debug("getSenderSuccessMessage", _transferID, "_vomsVO.getPinNo()="+_vomsVO.getPinNo()+" , _requestVO.getEvdPin()="+_requestVO.getEvdPin());
        }
		if (BTSLUtil.isNullString(_sid))
		{
			messageArgArray= new String[]{_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),String.valueOf(_receiverTransferItemVO.getValidity()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()),String.valueOf(_receiverTransferItemVO.getNewGraceDate()),PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()),_c2sTransferVO.getSubService(),extraPrefixSerialNumber+_vomsVO.getSerialNo(),_vomsVO.getProductID(),_vomsVO.getProductName(),_vomsVO.getExpiryDateStr(),prepareOtherInfo(extraPrefixOtherInfo, _vomsVO.getOtherInfo()),_c2sTransferVO.getProductName(),_requestVO.getEvdPin()};
		}
		else
		{
			messageArgArray= new String[]{_sid,_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getTransferValue()),PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()),String.valueOf(_receiverTransferItemVO.getValidity()),PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()),String.valueOf(_receiverTransferItemVO.getNewGraceDate()),PretupsBL.getDisplayAmount(_c2sTransferVO.getReceiverAccessFee()),_c2sTransferVO.getSubService(),extraPrefixSerialNumber+_vomsVO.getSerialNo(),_vomsVO.getProductID(),_vomsVO.getProductName(),_vomsVO.getExpiryDateStr(),prepareOtherInfo(extraPrefixOtherInfo, _vomsVO.getOtherInfo()),_c2sTransferVO.getProductName(),_requestVO.getEvdPin()};
		}
		String key;
		if(PretupsI.SERVICE_TYPE_EVD.equals(_serviceType)||PretupsI.SERVICE_TYPE_EVD101.equals(_serviceType)||PretupsI.SERVICE_TYPE_EVD102.equals(_serviceType)||PretupsI.SERVICE_TYPE_EVD104.equals(_serviceType)||PretupsI.SERVICE_TYPE_EVD105.equals(_serviceType)||PretupsI.SERVICE_TYPE_EVD106.equals(_serviceType)) {
			key=PretupsErrorCodesI.EVD_SENDER_MESSAGE_FOR_SUCCESS;
		} else {
			key=PretupsErrorCodesI.EVD_SENDER_MESSAGE_FOR_SUCCESS;
		}
		_successMessage = BTSLUtil.getMessage(((ChannelUserVO) _c2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),key,messageArgArray);
		return _successMessage;
	}
	
	private String getReceiverAmbigousMessage()
	{
		String[] messageArgArray={_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_senderMSISDN,_channelUserVO.getUserName(),_payableAmt};
		return BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_AMBIGOUS_KEY_EVD,messageArgArray,_requestVO.getRequestGatewayType());
	}	
	private String getReceiverFailMessage()
	{
		String[] messageArgArray={_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_senderMSISDN,_channelUserVO.getUserName(),_payableAmt};
		return BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY_EVD,messageArgArray,_requestVO.getRequestGatewayType());
	}	
	/**
	 *  Method to get the under process message before validation to be sent to sender
	 * @return
	 */
	private String getSndrUPMsgBeforeValidation()
	{
		String[] messageArgArray;		
		if (BTSLUtil.isNullString(_sid)) {
			messageArgArray= new String[]{_receiverMSISDN,_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_c2sTransferVO.getProductName()};
		} else {
			messageArgArray= new String[]{_sid,_transferID,PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()),_c2sTransferVO.getProductName()};
		}
		return BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS_B4VAL_EVD,messageArgArray);
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
		boolean isVOMSFound=false;
		String interfaceCategory;
		
		if((!_vomsInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
			isVOMSFound=getInterfaceRoutingDetails(p_con,_receiverMSISDN,receiverPrefixID,_receiverVO.getSubscriberType(),receiverNetworkCode,_c2sTransferVO.getServiceType(),_type,PretupsI.USER_TYPE_RECEIVER,action);
		} else {
			isVOMSFound=true;
		}
		if(!isVOMSFound) {
			throw new BTSLBaseException(this,"populateServicePaymentInterfaceDetails",PretupsErrorCodesI.VOMS_INTERFACE_NOT_FOUND);
		}
        
		if(_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR))
		{
			_onlyForEvr=true;
			isReceiverFound=getInterfaceRoutingDetails(p_con,_receiverMSISDN,receiverPrefixID,_receiverVO.getSubscriberType(),receiverNetworkCode,_c2sTransferVO.getServiceType(),_type,PretupsI.USER_TYPE_RECEIVER,action);
			if(!isReceiverFound) {
				throw new BTSLBaseException(this,"populateServiceInterfaceDetails",PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING_EVD);
			}
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
			
			if(p_interfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_VOMS))
			{
				if(_c2sTransferVO.getServiceType().contains(PretupsI.SERVICE_TYPE_EVD) || _c2sTransferVO.getServiceType().contains(PretupsI.SERVICE_TYPE_EVR))
				{
					_receiverTransferItemVO.setInterfaceID(interfaceID);
					_receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
					_receiverTransferItemVO.setInterfaceType(p_interfaceCategory);
				}
				_senderTransferItemVO.setPrefixID(p_prefixID);
				_senderTransferItemVO.setInterfaceID(interfaceID);
				_senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
				_senderTransferItemVO.setInterfaceType(p_interfaceCategory);
				if(!p_useInterfacePrefixVO && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
					_vomsInterfaceInfoInDBFound=true;
				}
				_vomsExternalID=externalID;
				_vomsAllServiceClassID=allServiceClassID;
			}
			else
			{
				_receiverTransferItemVO.setPrefixID(p_prefixID);
				_receiverTransferItemVO.setInterfaceID(interfaceID);
				_receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
				_receiverTransferItemVO.setInterfaceType(p_interfaceCategory);
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
			}
			//Check if interface status is Active or not.
			if(!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceStatusTy))
			{
				if(PretupsI.LOCALE_LANGAUGE_EN.equals(_senderLocale.getLanguage())) {
					_c2sTransferVO.setSenderReturnMessage(message1);
				} else {
					_c2sTransferVO.setSenderReturnMessage(message2);
				}
				throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_EVD);
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
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
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
	/**
	 * Method to process request from queue
	 * @param p_transferVO
	 */
	public void processFromQueue(TransferVO p_transferVO)
	{   StringBuilder loggerValue= new StringBuilder(); 
		final String methodName = "processFromQueue";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName,"Entered");
		}
		Connection con=null;
		MComConnectionI mcomCon = null;
		try
		{
			_c2sTransferVO=(C2STransferVO)p_transferVO;
			_requestVO=_c2sTransferVO.getRequestVO();
			_channelUserVO=(ChannelUserVO)_requestVO.getSenderVO();
			_type=_requestVO.getType();
			_requestID=_requestVO.getRequestID();
			_requestIDStr=_requestVO.getRequestIDStr();
			_receiverLocale=_requestVO.getReceiverLocale();
			_transferID=_c2sTransferVO.getTransferID();			
			_receiverVO=(ReceiverVO)_c2sTransferVO.getReceiverVO();
			_senderMSISDN=(_channelUserVO.getUserPhoneVO()).getMsisdn();
			_receiverMSISDN=((ReceiverVO)_c2sTransferVO.getReceiverVO()).getMsisdn();			
			_senderLocale=_requestVO.getSenderLocale();
			_senderNetworkCode=_channelUserVO.getNetworkID();
			_serviceType=_requestVO.getServiceType();
			_senderTransferItemVO=_c2sTransferVO.getSenderTransferItemVO();
			_receiverTransferItemVO=_c2sTransferVO.getReceiverTransferItemVO();
			_transferEntryReqd=true;
			_receiverSubscriberType=_c2sTransferVO.getReceiverSubscriberType();
			
			LoadController.checkTransactionLoad(((ReceiverVO)_c2sTransferVO.getReceiverVO()).getNetworkCode(),_senderTransferItemVO.getInterfaceID(),PretupsI.C2S_MODULE,_transferID,true,LoadControllerI.USERTYPE_SENDER);
			LoadController.checkTransactionLoad(((ReceiverVO)_c2sTransferVO.getReceiverVO()).getNetworkCode(),_receiverTransferItemVO.getInterfaceID(),PretupsI.C2S_MODULE,_transferID,true,LoadControllerI.USERTYPE_RECEIVER);

			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
            // Loading C2S receiver's controll parameters
            // added by PN(25/03/08) to resolve the issude of duplicate request
            // processing
            _c2sTransferVO.setUnderProcessCheckReqd(_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
            if(!(boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED)){
            	PretupsBL.loadRecieverControlLimits(con, _requestIDStr, _c2sTransferVO);
			}
            _receiverVO.setUnmarkRequestStatus(true);
            try {
            	mcomCon.finalCommit();
           } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }
            if (mcomCon != null)
				mcomCon.close("EVDController#process");
			mcomCon = null;
			con = null;

            if (_log.isDebugEnabled()) {

     	       loggerValue.setLength(0);
     	       loggerValue.append("_transferID=" );
     	       loggerValue.append(_transferID);
     	       loggerValue.append( " Successfully through load");
                _log.debug("C2SPrepaidController[processFromQueue]",loggerValue);
            }
            _processedFromQueue = true;

            processValidationRequest();
            // Set under process message for the sender and reciever
            p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
            final String[] messageArgArray = { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
            p_transferVO.setMessageArguments(messageArgArray);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            if (mcomCon != null)
				mcomCon.close("EVDController#process");
			mcomCon = null;
			con = null;
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
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                loggerValue.setLength(0);
            	loggerValue.append("Leaving Reciever Unmarked Base Exception:" );
            	loggerValue.append( bex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[processFromQueue]", _transferID,
                    _senderMSISDN, _senderNetworkCode, loggerValue.toString());
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                loggerValue.setLength(0);
            	loggerValue.append("Leaving Reciever Unmarked Base Exception:"  );
            	loggerValue.append( e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[processFromQueue]", _transferID,
                    _senderMSISDN, _senderNetworkCode, loggerValue.toString());
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
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
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }

            // checking whether need to decrease the transaction load, if it is
            // already increased
            LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            _isCounterDecreased = true;
            // making entry in the transaction log
            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());

        } catch (Exception e) {
            if (mcomCon != null)
				mcomCon.close("EVDController#process");
			mcomCon = null;
			con = null;
            _log.errorTrace(methodName, e);
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                _log.errorTrace(methodName, bex);
                loggerValue.setLength(0);
            	loggerValue.append("Leaving Reciever Unmarked Base Exception:" );
            	loggerValue.append(bex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[processFromQueue]", _transferID,
                    _senderMSISDN, _senderNetworkCode, loggerValue.toString() );
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                loggerValue.setLength(0);
            	loggerValue.append("Leaving Reciever Unmarked Exception:");
            	loggerValue.append(ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[processFromQueue]", _transferID,
                    _senderMSISDN, _senderNetworkCode,  loggerValue.toString() );
            }
            // checking condition whether channel receiver required the general
            // failure message
            if (_recValidationFailMessageRequired) {
                // if receivermessage is null or it is not key
                if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // setting receiver return message
                    if (_transferID != null) {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD, new String[] { String.valueOf(_transferID), PretupsBL
                            .getDisplayAmount(_c2sTransferVO.getRequestedAmount()) }));
                    } else {
                        _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_EVD, new String[] { PretupsBL.getDisplayAmount(_c2sTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }

            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _status = PretupsErrorCodesI.TXN_STATUS_FAIL;
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            _requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            _c2sTransferVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);

            _log.errorTrace(methodName, e);

            // decreasing the transaction load count
            LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            _isCounterDecreased = true;

            // raising alarm
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[processFromQueue]", _transferID,
                _senderMSISDN, _senderNetworkCode,loggerValue.toString());
            // logging in the transaction log
            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                _c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());
        } finally {
            try {
                if (mcomCon == null) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                }
                // makking entry in the transfer table if transfer entry has not
                // been made and message gateway flow is common, i.e. validation
                // is not in thread
                if (_transferID != null && !_transferDetailAdded) {
                    // added by nilesh: consolidated for logger
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
            	loggerValue.append(e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EVDController[processFromQueue]", _transferID,
                    _senderMSISDN, _senderNetworkCode, loggerValue.toString());
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
                if (mcomCon != null)
					mcomCon.close("EVDController#process");
				mcomCon = null;
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
        	loggerValue.append("Entered for _transferID=");
        	loggerValue.append(_transferID);
        	loggerValue.append(" p_action=");
        	loggerValue.append(p_action);
        	loggerValue.append( " _voucherMarked=");
        	loggerValue.append(_voucherMarked);
            _log.debug(methodName, loggerValue);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
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
                    _log.error( methodName,loggerValue );
                    _log.errorTrace(methodName, e);
                    loggerValue.setLength(0);
                	loggerValue.append("Error while updating voucher status for =");
                	loggerValue.append(_c2sTransferVO.getSerialNumber());
                	loggerValue.append(" So leaving the voucher marked as under process. Exception: ");
                	loggerValue.append(e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EvdController[voucherUpdateSenderCreditBack]",
                        _transferID, "", "",loggerValue.toString());
                }
                mcomCon = new MComConnection();
                con=mcomCon.getConnection();
                if (_transferDetailAdded) {
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("transferID=");
                    	loggerValue.append(_transferID);
                    	loggerValue.append(" Doing Sender Credit back ");
                        _log.debug(methodName,  loggerValue);
                    }
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
        	loggerValue.append(" For transfer ID=" );
        	loggerValue.append(_transferID);
        	loggerValue.append( " Getting BTSL Base Exception: ");
        	loggerValue.append( be.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
            if (con != null) {
                try {
                	mcomCon.finalRollback();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            _log.errorTrace(methodName, e);
            if (PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION.equals(p_action)) {
                _finalTransferStatusUpdate = false;
            }
            loggerValue.setLength(0);
        	loggerValue.append("Error while credit back sender, getting exception: ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EvdController[voucherUpdateSenderCreditBack]",
                _transferID, "", "",  loggerValue.toString());
        } finally {
            if (mcomCon != null)
				mcomCon.close("EVDController#process");
			mcomCon = null;
			con = null;
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting for _transferID=");
            	loggerValue.append(_transferID);
            	loggerValue.append(" p_action=" );
            	loggerValue.append(p_action);
                _log.debug(methodName,  loggerValue );
            }
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
        final String methodName = "updateVoucherForFailedTransaction";
        boolean finalTransferStatusUpdate = true;
        C2STransferItemVO senderTransferItemVO = (C2STransferItemVO) p_transferVO.getTransferItemList().get(0);
        try {
            final CommonClient commonClient = new CommonClient();
            final String vomsCreditBackResponse = commonClient.process(getVOMSUpdateRequestStr(PretupsI.INTERFACE_DEBIT_ACTION, p_transferVO, p_networkInterfaceModuleVO,
                p_interfaceVO, VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_UNPROCESS), p_transferVO.getTransferID(), p_networkInterfaceModuleVO.getCommunicationType(),
                p_networkInterfaceModuleVO.getIP(), p_networkInterfaceModuleVO.getPort(), p_networkInterfaceModuleVO.getClassName());
            // getting the update status from the Response and set in
            // appropriate VO: senderTransferItemVO update Status 1 can be used
            try {
                final HashMap map = BTSLUtil.getStringToHash(vomsCreditBackResponse, "&", "=");
                senderTransferItemVO.setUpdateStatus1((String) map.get("TRANSACTION_STATUS"));
                if (!InterfaceErrorCodesI.SUCCESS.equals(senderTransferItemVO.getUpdateStatus1())) {
                    // p_transferVO.setErrorCode(senderTransferItemVO.getUpdateStatus1()+"_S");
                    throw new BTSLBaseException(this, "updateVoucherForFailedTransaction", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
                VomsVoucherChangeStatusLog.log(p_transferVO.getTransferID(), p_transferVO.getSerialNumber(), VOMSI.VOUCHER_USED, VOMSI.VOUCHER_ENABLE, p_transferVO
                    .getReceiverNetworkCode(), ((ChannelUserVO) p_transferVO.getSenderVO()).getUserID(), BTSLUtil
                    .getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
            } catch (Exception e) {
                _log.error("updateVoucherForFailedTransaction", " Exception while updating voucher status= " + e.getMessage());
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, "");
            }
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), p_transferVO.getSenderNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Done to voucher for serial number=" + p_transferVO.getSerialNumber(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (Exception be) {
            _log.errorTrace(methodName, be);
            finalTransferStatusUpdate = false;
            senderTransferItemVO.setUpdateStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            PretupsBL.validateRecieverLimits(null, p_transferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back voucher", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage());
            _log.errorTrace(methodName, be);
            throw new BTSLBaseException(this, "updateVoucherForFailedTransaction", PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateVoucherForFailedTransaction", " Exited for finalTransferStatusUpdate= " + finalTransferStatusUpdate);
            }
        }
        return finalTransferStatusUpdate;
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
    public void updateSenderForFailedTransaction(Connection p_con, C2STransferVO p_transferVO) throws BTSLBaseException, Exception {
        final String methodName = "updateSenderForFailedTransaction";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered for p_transferVO= " + p_transferVO);
        }
        try {
            _userBalancesVO = ChannelUserBL.creditUserBalanceForProduct(p_con, p_transferVO.getTransferID(), p_transferVO);
            ChannelTransferBL.decreaseC2STransferOutCounts(p_con, p_transferVO);
        	/*if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,_c2sTransferVO.getNetworkCode()) && _c2sTransferVO.isOtfCountsIncreased() )
			{
				  ChannelTransferBL.decreaseUserOTFCounts(p_con, _c2sTransferVO, _channelUserVO);
			} */
            _creditBackEntryDone = true;
            if (_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                _requestVO.setSuccessTxn(false);
                final String[] messageArgArray = { _c2sTransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _c2sTransferVO
                    .getTransferID(), PretupsBL.getDisplayAmount(_userBalancesVO.getBalance()) };
                _requestVO.setMessageArguments(messageArgArray);
                _requestVO.setMessageCode(PretupsErrorCodesI.C2S_SENDER_CREDIT_SUCCESS);
            }
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), p_transferVO.getSenderNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Done to sender", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (Exception be) {
            _log.errorTrace(methodName, be);
            _finalTransferStatusUpdate = false;
            // PretupsBL.validateRecieverLimits(null,p_transferVO,PretupsI.TRANS_STAGE_AFTER_INTOP,PretupsI.C2S_MODULE);
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EvdUtil[updateSenderForFailedTransaction]", "", "",
                "", "Error while credit back the retailer Exception: " + be.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exited for _finalTransferStatusUpdate= " + _finalTransferStatusUpdate);
            }
        }
    }

    /**
     * This method is responsible to generate the transaction id in the memory.
     * 
     * @param p_transferVO
     * @return
     */
    
    private static synchronized void generateEVDTransferID(TransferVO p_transferVO) {
        final String methodName = "generateEVDTransferID";
        SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
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
                throw new BTSLBaseException("EVDController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = _operatorUtil.formatEVDTransferID(p_transferVO, _transactionIDCounter);
            if (transferID == null) {
                throw new BTSLBaseException("EVDController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
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
        final String methodName = "getVOMSUpdateRequestStr";
        StringBuffer strBuff ;
        strBuff = new StringBuffer(getVOMSCommonString(p_transferVO, p_networkInterfaceModuleVO, p_interfaceVO));
        strBuff.append("&INTERFACE_ACTION=" + p_interfaceAction);
        try {
            strBuff.append("&TRANSFER_DATE=" + BTSLUtil.getDateTimeStringFromDate(p_transferVO.getTransferDateTime()));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        strBuff.append("&INTERFACE_AMOUNT=" + p_transferVO.getRequestedAmount());
        strBuff.append("&UPDATE_STATUS=" + p_updateStatus);
        strBuff.append("&PREVIOUS_STATUS=" + p_previousStatus);
        strBuff.append("&SOURCE=" + p_transferVO.getSourceType());
        strBuff.append("&SENDER_MSISDN=" + p_transferVO.getSenderMsisdn());
        strBuff.append("&SERIAL_NUMBER=" + p_transferVO.getSerialNumber());
        strBuff.append("&SENDER_USER_ID=" + p_transferVO.getSenderID());
        return strBuff.toString();
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
    		// Decreasing interface load of sender which we had
    		// incremented before 27/09/06, receiver was
    		// decreased in the method
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
	
}
