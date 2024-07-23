package com.client.pretups.channel.transfer.requesthandler.clienthandler;

/*
 * @(#)VchrConsChnlRcvrController.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Zeeshan Aleem 16.12.2014 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * Controller class for handling the Electronic Voucher Distribution(EVD) &
 * Electronic Voucher Recharge(EVR) Services
 */

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

public class VchrConsChnlRcvrController implements ServiceKeywordControllerI, Runnable {
    private static final Log LOG = LogFactory.getLog(VchrConsChnlRcvrController.class.getName());
    private C2STransferVO c2sTransferVO = null;
    private TransferItemVO senderTransferItemVO = null;
    private TransferItemVO receiverTransferItemVO = null;
    private String senderMSISDN;
    private String receiverMSISDN;
    private ChannelUserVO channelUserVO;
    private ReceiverVO receiverVO;
    private String senderSubscriberType;
    private String senderNetworkCode;
    private Date currentDate = null;
    private long requestID;
    private String requestIDStr;
    private String transferID;
    private ArrayList itemList = null;
    private String intModCommunicationTypeR;
    private String intModIPR;
    private int intModPortR;
    private String intModClassNameR;
    private NetworkInterfaceModuleVO networkInterfaceModuleVO = null;
    private ServiceInterfaceRoutingVO serviceInterfaceRoutingVO = null;
    private boolean transferDetailAdded = false;
    private boolean isCounterDecreased = false;
    private String type;
    private String serviceType;
    private boolean finalTransferStatusUpdate = true;
    private boolean transferEntryReqd = false;
    private boolean decreaseTransactionCounts = false;
    UserBalancesVO userBalancesVO = null;
    private boolean creditBackEntryDone = false;
    private boolean receiverInterfaceInfoInDBFound = false;
    private String receiverAllServiceClassID = PretupsI.ALL;
    private String receiverPostBalanceAvailable;
    Locale senderLocale = null;
    Locale receiverLocale = null;
    private String externalID = null;
    private RequestVO requestVO = null;
    private boolean processedFromQueue = false;
    private boolean recValidationFailMessageRequired = false;
    private boolean recTopupFailMessageRequired = false;
    private String notAllowedSendMessGatw;
    private String receiverSubscriberType = null;
    public static OperatorUtilI operatorUtil = null;
    private boolean vomsInterfaceInfoInDBFound = false;
    private String vomsExternalID = null;
    VomsVoucherVO vomsVO = null;
    private boolean voucherMarked = false;
    private boolean deliveryTrackDone = false;
    private String vomsAllServiceClassID = null;
    private String interfaceStatusType = null;
    private static int transactionIDCounter = 0;
    private static long prevReqTime = 0;
    private String payableAmt = null;
    private static int prevMinut = 0;
    private static SimpleDateFormat sdfCompare = new SimpleDateFormat("mm");
    private boolean onlyForEvr = false;
    // Loads Operator specific class. In EVD controller it is used for
    // validating the message format.
    private boolean receiverMessageSendReq = false;
    private String notAllowedRecSendMessGatw;
    private boolean oneLog = true;
    private String receiverBundleID = null;

    static {
        final String methodName = "static";
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsChnlRcvrController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /*
     * In the constructor of VchrConsChnlRcvrController initialize the date
     * variable currentDate with current date. The
     * variables EVD_REC_GEN_FAIL_MSG_REQD_V & EVD_REC_GEN_FAIL_MSG_REQD_T
     * decides whether the validation and
     * top up failed message send to receiver or not.
     */

    public VchrConsChnlRcvrController() {
        c2sTransferVO = new C2STransferVO();
        currentDate = new Date();
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("EVD_REC_GEN_FAIL_MSG_REQD_V")))) {
            recValidationFailMessageRequired = true;
        }
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("EVD_REC_GEN_FAIL_MSG_REQD_T")))) {
            recTopupFailMessageRequired = true;
        }
        notAllowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("EVD_SEN_MSG_NOT_REQD_GW"));
        notAllowedRecSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("EVD_REC_MSG_NOT_REQD_GW"));
    }

    /**
     * Method to process the request of the Electornic Voucher Distribution as
     * well as Electornic Voucher Recharge
     * 
     * @param p_requestVO
     *            RequestVO
     * @return void
     */

    public void process(RequestVO p_requestVO) {
        Connection con = null;
        MComConnectionI mcomCon = null;
        final String methodName = "process";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                p_requestVO.getRequestIDStr(),
                "Entered for Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + " recValidationFailMessageRequired: " + recValidationFailMessageRequired + " recTopupFailMessageRequired" + recTopupFailMessageRequired + " notAllowedSendMessGatw: " + notAllowedSendMessGatw + " ");
        }
        // boolean receiverMessageSendReq=false;
        try {
            requestVO = p_requestVO;
            channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            TransactionLog.log("", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_REQ,
                PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "Received Request From Receiver", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            senderLocale = p_requestVO.getSenderLocale();
            senderNetworkCode = channelUserVO.getNetworkID();

            // Populatig C2STransferVO from the request VO
            populateVOFromRequest(p_requestVO);
            requestID = p_requestVO.getRequestID();
            requestIDStr = p_requestVO.getRequestIDStr();
            type = p_requestVO.getType();
            serviceType = p_requestVO.getServiceType();

            // Checking senders out transfer status, it should not be suspended
            if (PretupsI.YES.equalsIgnoreCase(channelUserVO.getOutSuspened())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SENDER_OUT_SUSPEND_EVD);
            }

            // Checking senders transfer profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(channelUserVO.getTransferProfileStatus())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_TRANPROFILE_SUSPEND_EVD);
            }

            // Checking senders commission profile status, it should not be
            // suspended
            if (PretupsI.SUSPEND.equals(channelUserVO.getCommissionProfileStatus())) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SNDR_COMMPROFILE_SUSPEND_EVD);
            }

            // Getting oracle connection
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            // Validating user message incoming in the request [Keyword is
            // either EVD/EVR]
            operatorUtil.validateEVDO2CRequestFormat(con, c2sTransferVO, p_requestVO);

            // Block added to avoid decimal amount in credit transfer
            if (!BTSLUtil.isStringIn(serviceType, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES))) {
                try {
                    final String displayAmt = PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount());
                    Long.parseLong(displayAmt);
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_AMOUNT_EVD);
                }
            }
            receiverLocale = p_requestVO.getReceiverLocale();
            senderLocale = p_requestVO.getSenderLocale();
            receiverVO = (ReceiverVO) c2sTransferVO.getReceiverVO();
            receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(receiverVO.getMsisdn()));

            // The condition below will be checked for EVR only because in EVd
            // any postpaid number can also request PIN
            if (!receiverVO.getSubscriberType().equals(type) && c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
                // Refuse the Request
                LOG.error(this, "Series =" + receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + type);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VchrConsChnlRcvrController[process]", "", "", "",
                    "Series =" + receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + type + " But request initiated for the same");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE_EVD, 0, new String[] { receiverVO.getMsisdn() }, null);
            }
            receiverVO.setModule(c2sTransferVO.getModule());
            receiverVO.setCreatedDate(currentDate);
            receiverVO.setLastTransferOn(currentDate);
            senderMSISDN = (channelUserVO.getUserPhoneVO()).getMsisdn();
            receiverMSISDN = ((ReceiverVO) c2sTransferVO.getReceiverVO()).getMsisdn();
            c2sTransferVO.setReceiverMsisdn(receiverMSISDN);
            c2sTransferVO.setReceiverNetworkCode(receiverVO.getNetworkCode());
            c2sTransferVO.setGrphDomainCode(channelUserVO.getGeographicalCode());
            c2sTransferVO.setSubService(p_requestVO.getReqSelector());
            c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
            receiverSubscriberType = receiverVO.getSubscriberType();
            receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(senderMSISDN));
            // VFE 6 CR
            c2sTransferVO.setInfo1(p_requestVO.getInfo1());
            c2sTransferVO.setInfo2(p_requestVO.getInfo2());
            c2sTransferVO.setInfo3(p_requestVO.getInfo3());
            c2sTransferVO.setInfo4(p_requestVO.getInfo4());
            c2sTransferVO.setInfo5(p_requestVO.getInfo5());
            c2sTransferVO.setInfo6(p_requestVO.getInfo6());
            c2sTransferVO.setInfo7(p_requestVO.getInfo7());
            c2sTransferVO.setInfo8(p_requestVO.getInfo8());
            c2sTransferVO.setInfo9(p_requestVO.getInfo9());
            c2sTransferVO.setInfo10(p_requestVO.getInfo10());
            c2sTransferVO.setCellId(requestVO.getCellId());
            c2sTransferVO.setSwitchId(requestVO.getSwitchId());
            c2sTransferVO.setVoucherCode(requestVO.getVoucherCode());
            c2sTransferVO.setSerialNumber(requestVO.getSerialnumber());
            // checking whether self voucher distribution is allowed or not //
            // in case of EVD private recharge also it is allowed.
            if (senderMSISDN.equals(receiverMSISDN) && (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SELF_VOUCHER_DISTRIBUTION_ALLOWED))).booleanValue() && (!(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue() && c2sTransferVO
                .getServiceType().equals(PretupsI.SERVICE_TYPE_EVD) && "1".equals(c2sTransferVO.getSubService()))))) {
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.CHNL_ERROR_SELF_VOUCHER_DIST_NOTALLOWED);
            }

            // Restricted MSISDN check
            // if
            // (PretupsI.STATUS_ACTIVE.equals((channelUserVO.getCategoryVO()).getRestrictedMsisdns()))
            // RestrictedSubscriberBL.isRestrictedMsisdnExist(con,c2sTransferVO,channelUserVO,receiverVO.getMsisdn(),c2sTransferVO.getRequestedAmount());
            RestrictedSubscriberBL.isRestrictedMsisdnExistForC2S(con, c2sTransferVO, channelUserVO, receiverVO.getMsisdn(), c2sTransferVO.getRequestedAmount());

            // Validates the network service status
            PretupsBL.validateNetworkService(c2sTransferVO);
            // receiverMessageSendReq=true;
            receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REC_MSG_SEND_ALLOW, receiverVO.getNetworkCode(), serviceType)).booleanValue();
            // receiver message send should be false if it is for private
            // recharge as in this case there will be no reciever MSISDN
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue() && c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVD) && "1".equals(c2sTransferVO.getSubService())) {
                receiverMessageSendReq = false;
            }
            // check if receiver barred in PreTUPS or not, user should not be
            // barred.
            try {
                PretupsBL.checkMSISDNBarred(con, receiverMSISDN, receiverVO.getNetworkCode(), c2sTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
            } catch (BTSLBaseException be) {
                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                    c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
                }
                throw be;
            }

            /*
             * Loading C2S receiver's control parameters from subscriber_control
             * table
             * If the last trf status of receiver is underprocess then throw
             * error else add
             * the details of receiver in subscriber_control table
             */
            // added by PN(25/03/08) to resolve the issude of duplicate request
            // processing
            c2sTransferVO.setUnderProcessCheckReqd(p_requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
            PretupsBL.loadRecieverControlLimits(con, p_requestVO.getRequestIDStr(), c2sTransferVO);
            receiverVO.setUnmarkRequestStatus(true);

            // commiting transaction after updating receiver's control
            // parameters
            try {
                mcomCon.partialCommit();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
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
                p_requestVO.setTransactionID(transferID);
                receiverVO.setLastTransferID(transferID);

                // making entry in the transaction log
                TransactionLog.log(transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), channelUserVO.getNetworkID(), PretupsI.TXN_LOG_REQTYPE_INT,
                    PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Generated Transfer ID", PretupsI.TXN_LOG_STATUS_SUCCESS,
                    "Source Type=" + c2sTransferVO.getSourceType() + " Gateway Code=" + c2sTransferVO.getRequestGatewayCode());

                // Populate VOMS and IN interface details(IN interface will be
                // loaded for EVr only)
                populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                c2sTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);
                // This will be used in validate ReceiverLimit method of
                // PretupsBL when receiverTransferItemVO is null
                c2sTransferVO.setReceiverSubscriberType(receiverSubscriberType);

                // validate receiver limits before Interface Validations
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                    PretupsBL.validateRecieverLimits(con, c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
                }

                // Validate Sender Transaction profile checks and balance
                // availablility for user
                // ChannelUserBL.validateSenderAvailableControls(con,transferID,c2sTransferVO);

                // setting validation status
                senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

                // commiting transaction and closing the transaction as it is
                // not requred
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
				if (mcomCon != null) {
					mcomCon.close("VchrConsChnlRcvrController#process");
					mcomCon = null;
				}
                con = null;

                // Checking the Various loads and setting flag to decrease the
                // transaction count
                checkTransactionLoad();
                decreaseTransactionCounts = true;

                (channelUserVO.getUserPhoneVO()).setLastTransferID(transferID);
                (channelUserVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2S);

                // Checking the flow type of the transfer request, whether it is
                // common or thread
                if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
                    // Process validation requests and start thread for the
                    // topup
                    processValidationRequest();
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    p_requestVO.setSenderMessageRequired(c2sTransferVO.isUnderProcessMsgReq());
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
                    oneLog = false;
                    // Parameter set to indicate that instance counters will not
                    // be decreased in receiver for this transaction
                    p_requestVO.setDecreaseLoadCounters(false);
                } else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    processValidationRequest();
                    processThread();
                    final String[] messageArgArray = { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()), c2sTransferVO
                        .getVoucherCode() };
                    p_requestVO.setMessageArguments(messageArgArray);
                }
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            p_requestVO.setSuccessTxn(false);
            try {
                if (receiverVO != null && receiverVO.isUnmarkRequestStatus()) {
                    // getting database connection if it is not already there
                    if (mcomCon == null) {
                        mcomCon = new MComConnection();
                        con=mcomCon.getConnection();
                    }
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), receiverVO);
                }
            } catch (BTSLBaseException bex) {
                LOG.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsChnlRcvrController[process]", transferID,
                    senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsChnlRcvrController[process]", transferID,
                    senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + e.getMessage());
            }

            // setting transaction status to Fail
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (recValidationFailMessageRequired) {
                // setting receiver return message
                if (c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {

                    if (transferID != null) {
                        c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD, new String[] { String.valueOf(transferID), PretupsBL
                            .getDisplayAmount(c2sTransferVO.getRequestedAmount()) }));
                    } else {
                        c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_EVD, new String[] { PretupsBL.getDisplayAmount(c2sTransferVO
                            .getRequestedAmount()) }));
                    }

                }
            }
            // getting return message from the C2StransferVO and setting it to
            // the requestVO
            if (!BTSLUtil.isNullString(c2sTransferVO.getSenderReturnMessage())) {
                p_requestVO.setSenderReturnMessage(c2sTransferVO.getSenderReturnMessage());
            }

            if (be.isKey()) // checking if baseexception has key
            {
                if (c2sTransferVO.getErrorCode() == null) {
                    c2sTransferVO.setErrorCode(be.getMessageKey());
                }

                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }

            // checking whether need to decrease the transaction load, if it is
            // already increased
            if (transferID != null && decreaseTransactionCounts) {
                // decreasing transaction load
                LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                isCounterDecreased = true;
            }
            // making entry in the transaction log
            TransactionLog.log(transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());

        } catch (Exception e) {
            // setting success transaction status flag to false
            p_requestVO.setSuccessTxn(false);
            try {
                // getting database connection to unmark the users transaction
                // to completed
                if (receiverVO != null && receiverVO.isUnmarkRequestStatus()) {
                    if (mcomCon == null) {
                        mcomCon = new MComConnection();
                        con=mcomCon.getConnection();
                    }
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), receiverVO);
                }
            } catch (BTSLBaseException bex) {
                LOG.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsChnlRcvrController[process]", transferID,
                    senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsChnlRcvrController[process]", transferID,
                    senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + ex.getMessage());
            }
            // checking condition whether channel receiver required the general
            // failure message
            if (recValidationFailMessageRequired) {
                // if receivermessage is null or it is not key
                if (c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // setting receiver return message
                    if (transferID != null) {
                        c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD, new String[] { String.valueOf(transferID), PretupsBL
                            .getDisplayAmount(c2sTransferVO.getRequestedAmount()) }));
                    } else {
                        c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_EVD, new String[] { PretupsBL.getDisplayAmount(c2sTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }

            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            LOG.errorTrace(methodName, e);

            // decreasing the transaction load count
            if (transferID != null && decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                isCounterDecreased = true;
            }
            // raising alarm
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsChnlRcvrController[process]", transferID,
                senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            // logging in the transaction log
            TransactionLog.log(transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
        }// end of catch
        finally {
            try {
                // Getting connection if it is null
                if (mcomCon == null) {
                    mcomCon = new MComConnection();
                    con=mcomCon.getConnection();
                }
                // makking entry in the transfer table if transfer entry has not
                // been made and message gateway flow is common, i.e. validation
                // is not in thread
                if (transferID != null && !transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || (p_requestVO
                    .getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(
                    PretupsI.TXN_STATUS_UNDER_PROCESS)))) {
                    // added by nilesh: consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        // addEntryInTransfers(con);
                    }
                } else if (transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    LOG.info(methodName, p_requestVO.getRequestIDStr(),
                        "Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + transferID + " But not added entry in Transfers yet");
                }
            } catch (BTSLBaseException be) {
                LOG.errorTrace(methodName, be);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsChnlRcvrController[process]", transferID,
                    senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            }
            if (con != null) {
                // committing transaction and closing connection
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
				if (mcomCon != null) {
					mcomCon.close("VchrConsChnlRcvrController#process");
					mcomCon = null;
				}
                con = null;
            }// end if

            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (isCounterDecreased) {
                p_requestVO.setDecreaseLoadCounters(false);
            }

            if (receiverMessageSendReq && recValidationFailMessageRequired && !BTSLUtil.isStringIn(c2sTransferVO.getRequestGatewayCode(), notAllowedRecSendMessGatw) && !"ALL"
                .equals(notAllowedRecSendMessGatw)) {
                // checking if receiver message is not null and receiver return
                // message is key
                if (c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // generating message and pushing it to receiver
                    final BTSLMessages btslRecMessages = (BTSLMessages) c2sTransferVO.getReceiverReturnMsg();
                    (new PushMessage(receiverMSISDN, BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), transferID,
                        c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                } else if (c2sTransferVO.getReceiverReturnMsg() != null) {
                    (new PushMessage(receiverMSISDN, (String) c2sTransferVO.getReceiverReturnMsg(), transferID, c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                }
            }
            // added by nilesh : consolidated for logger
            if (oneLog) {
                OneLineTXNLog.log(c2sTransferVO, senderTransferItemVO, receiverTransferItemVO);
            }
            // making entry in the transaction log
            TransactionLog.log(transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }// end of finally
    }

    /**
     * Method to populate C2S Transfer VO from request VO for further use
     * 
     * @param p_requestVO
     */
    private void populateVOFromRequest(RequestVO p_requestVO) {
        c2sTransferVO.setSenderVO(channelUserVO);
        c2sTransferVO.setRequestID(p_requestVO.getRequestIDStr());
        c2sTransferVO.setModule(p_requestVO.getModule());
        c2sTransferVO.setInstanceID(p_requestVO.getInstanceID());
        c2sTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        c2sTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        c2sTransferVO.setServiceType(p_requestVO.getServiceType());
        c2sTransferVO.setSourceType(p_requestVO.getSourceType());
        c2sTransferVO.setCreatedOn(currentDate);
        c2sTransferVO.setCreatedBy(channelUserVO.getUserID());
        c2sTransferVO.setModifiedOn(currentDate);
        c2sTransferVO.setModifiedBy(channelUserVO.getUserID());
        c2sTransferVO.setTransferDate(currentDate);
        c2sTransferVO.setTransferDateTime(currentDate);
        c2sTransferVO.setSenderMsisdn((channelUserVO.getUserPhoneVO()).getMsisdn());
        c2sTransferVO.setSenderID(channelUserVO.getUserID());
        c2sTransferVO.setNetworkCode(channelUserVO.getNetworkID());
        c2sTransferVO.setLocale(senderLocale);
        c2sTransferVO.setLanguage(c2sTransferVO.getLocale().getLanguage());
        c2sTransferVO.setCountry(c2sTransferVO.getLocale().getCountry());
        c2sTransferVO.setMsgGatewayFlowType(p_requestVO.getMessageGatewayVO().getFlowType());
        c2sTransferVO.setMsgGatewayResponseType(p_requestVO.getMessageGatewayVO().getResponseType());
        c2sTransferVO.setMsgGatewayTimeOutValue(p_requestVO.getMessageGatewayVO().getTimeoutValue());
        (channelUserVO.getUserPhoneVO()).setLocale(senderLocale);
        c2sTransferVO.setReferenceID(p_requestVO.getExternalReferenceNum());
        c2sTransferVO.setActiveUserId(channelUserVO.getActiveUserID());
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
            if (!transferDetailAdded && transferEntryReqd) {
                ChannelTransferBL.addC2STransferDetails(p_con, c2sTransferVO);// add
                // transfer
                // details
                // in
                // database
            } else if (transferDetailAdded) {
                c2sTransferVO.setModifiedOn(new Date());
                c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                // added by nilesh: consolidated for logger
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    ChannelTransferBL.updateC2STransferDetails(p_con, c2sTransferVO);// add
                    // transfer
                    // details
                    // in
                    // database
                }
            }
            p_con.commit();
            transferDetailAdded = true;
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            if (!isCounterDecreased && decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                isCounterDecreased = true;
            }
            LOG.error("processTransfer", transferID, "BTSLBaseException while adding transfer details in database:" + be.getMessage());
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsChnlRcvrController[process]", transferID,
                senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            if (!isCounterDecreased && decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                isCounterDecreased = true;
            }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        try {
            // validate skey details for generation and generate skey
            PretupsBL.generateSKey(p_con, c2sTransferVO);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsChnlRcvrController[processSKeyGen]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        try {
            // Generating the EVD transfer ID
            c2sTransferVO.setTransferDate(currentDate);
            c2sTransferVO.setTransferDateTime(currentDate);
            // PretupsBL.generateEVDTransferID(c2sTransferVO);
            // Transaction id would be generated in the memory.
            generateEVDTransferID(c2sTransferVO);
            transferID = c2sTransferVO.getTransferID();
            receiverVO.setLastTransferID(transferID);

            // Set sender transfer item details
            setSenderTransferItemVO();

            // set receiver transfer item details
            setReceiverTransferItemVO();

            // Get the product Info based on the service type
            PretupsBL.getProductFromServiceType(p_con, c2sTransferVO, serviceType, PretupsI.C2S_MODULE);
            transferEntryReqd = true;

            // Here logic will come for Commission profile for sale center
            if ((channelUserVO.getCategoryVO()).getDomainTypeCode().equals(PretupsI.DOMAIN_TYPE_SALECENTER)) {
                senderTransferItemVO.setTransferValue(c2sTransferVO.getRequestedAmount());
            } else {
                senderTransferItemVO.setTransferValue(c2sTransferVO.getTransferValue());
            }

        } catch (BTSLBaseException be) {
            // setting transfer status to FAIL
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            throw be;
        } catch (Exception e) {
            if (c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                if (transferID != null) {
                    c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD, new String[] { String.valueOf(transferID), PretupsBL
                        .getDisplayAmount(c2sTransferVO.getRequestedAmount()) }));
                } else {
                    c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_EVD, new String[] { PretupsBL.getDisplayAmount(c2sTransferVO
                        .getRequestedAmount()) }));
                }
            }
            // setting transfer status to FAIL
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsChnlRcvrController[processTransfer]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        }
    }

    public void run() {
        processThread();
    }

    /**
     * Thread to perform IN related operations
     */
    public void processThread() {
        final String methodName = "processThread";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, transferID, "Entered");
        }
        BTSLMessages btslMessages = null;
        userBalancesVO = null;
        CommonClient commonClient = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        InterfaceVO interfaceVO = null;
        try {
            if (c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !processedFromQueue) {
                // Processing validation request in Thread
                processValidationRequestInThread();
            }
            LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.SENDER_UNDER_TOP);

            // Getting the receiver credit string from C2S transfer VO to be
            // sent to the Interface Module
            commonClient = new CommonClient();
            final String requestStr = getReceiverCreditStr();
            // Sending request to the common client
            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            final String receiverCreditResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeR, intModIPR, intModPortR, intModClassNameR);
            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                receiverCreditResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, transferID, "Got the response from IN Module receiverCreditResponse=" + receiverCreditResponse);
            }

            try {
                // updating receiver credit response
                updateForReceiverCreditResponse(receiverCreditResponse);
                LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.RECEIVER_TOP_RESPONSE);
            } catch (BTSLBaseException be) {

                TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + c2sTransferVO.getTransferStatus() + " Getting Code=" + receiverVO
                        .getInterfaceResponseCode());
                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.RECEIVER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_TOP_RESPONSE);
                }

                // Check Status if Ambigous then credit back preference wise and
                // Update the sender back for fail transaction
                if (((c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CREDIT_BACK_ALWD_EVD_AMB))).booleanValue())) || c2sTransferVO
                    .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                }
                // Validating the receiver Limits and updating it
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                    PretupsBL.validateRecieverLimits(null, c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
                }
                throw be;
            }// end catch BTSLBaseException
            catch (Exception e) {

                TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + c2sTransferVO.getTransferStatus() + " Getting Code=" + receiverVO
                        .getInterfaceResponseCode());

                // decreaseing the resposne counters and making it success in
                // case of Ambiguous and Fail in case of fail
                if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.RECEIVER_TOP_RESPONSE);
                } else {
                    LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_TOP_RESPONSE);
                }

                // Update the sender back for fail transaction
                // Check Status if Ambigous then credit back preference wise
                if (((c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CREDIT_BACK_ALWD_EVD_AMB))).booleanValue())) || c2sTransferVO
                    .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                }

                // Validating the receiver Limits and updating it
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                    PretupsBL.validateRecieverLimits(null, c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
                }
                throw e;
            }// end of catch Exception
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            c2sTransferVO.setErrorCode(null);
            try {
                // Consume status and mark status to consume and give
                // diffrentials
                final EvdUtil evdUtil = new EvdUtil();
                interfaceVO = new InterfaceVO();
                interfaceVO.setInterfaceId(senderTransferItemVO.getInterfaceID());
                interfaceVO.setHandlerClass(senderTransferItemVO.getInterfaceHandlerClass());
                finalTransferStatusUpdate = evdUtil.updateVoucherAndGiveDifferentials(receiverVO, c2sTransferVO, networkInterfaceModuleVO, interfaceVO, requestVO
                    .getInstanceID(), false);
            } catch (BTSLBaseException be) {
                LOG.errorTrace(methodName, be);
                finalTransferStatusUpdate = false;
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "VchrConsChnlRcvrController",
                        "For transferID=" + transferID + " Diff applicable=" + c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + c2sTransferVO
                            .getDifferentialGiven() + " Not able to give Diff commission getting BTSL Base Exception=" + be.getMessage() + " Leaving transaction status as Under process");
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                finalTransferStatusUpdate = false;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("VchrConsChnlRcvrController",
                        "For transferID=" + transferID + " Diff applicable=" + c2sTransferVO.getDifferentialApplicable() + " Diff Given=" + c2sTransferVO
                            .getDifferentialGiven() + " Not able to give Diff commission getting Exception=" + e.getMessage() + " Leaving transaction status as Under process");
                }
            }

            receiverPostBalanceAvailable = "N";
            // perform step 2 of comments above
            // requestVO.setEvdPin(BTSLUtil.decryptText(vomsVO.getPinNo()));
            requestVO.setEvdPin(VomsUtil.decryptText(vomsVO.getPinNo()));
            // sendSMS(vomsVO);
        }// end try
        catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
            }// end if
            if (be.isKey() && c2sTransferVO.getSenderReturnMessage() == null) {
                btslMessages = be.getBtslMessages();
            } else if (c2sTransferVO.getSenderReturnMessage() == null) {
                c2sTransferVO.setSenderReturnMessage(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, transferID, "Error Code:" + c2sTransferVO.getErrorCode());
            }

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(), requestVO.getMessageGatewayVO().getGatewayType(), senderNetworkCode,
                serviceType, transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, receiverVO.getNetworkCode());

        }// end catch BTSLBaseException
        catch (Exception e) {
            LOG.errorTrace(methodName, e);
            if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsChnlRcvrController[run]", transferID,
                senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            btslMessages = new BTSLMessages(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(), requestVO.getMessageGatewayVO().getGatewayType(), senderNetworkCode,
                serviceType, transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, receiverVO.getNetworkCode());

        }// end catch Exception
        finally {
            try {
                if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) c2sTransferVO
                    .getReceiverReturnMsg()).isKey())) {
                    c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD), new String[] { String.valueOf(transferID), PretupsBL
                        .getDisplayAmount(c2sTransferVO.getRequestedAmount()) }));
                }
                // decreasing transaction load count
                if (!deliveryTrackDone) {
                    LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                }

                mcomCon = new MComConnection();con=mcomCon.getConnection();
                // Unmarking the receiver transaction status
                // In case of delivery tracking receiver is unmarked in delivery
                // receipt servlet
                if (receiverVO != null && receiverVO.isUnmarkRequestStatus() && !deliveryTrackDone) {
                    PretupsBL.unmarkReceiverLastRequest(con, transferID, receiverVO);
                }
            }// end try
            catch (BTSLBaseException be) {
                LOG.errorTrace(methodName, be);
            } catch (Exception e) {
                try {
                    if (con != null) {
                        mcomCon.finalRollback();
                    }
                } catch (Exception ex) {
                    LOG.errorTrace(methodName, ex);
                }
                LOG.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsChnlRcvrController[run]", transferID,
                    senderMSISDN, senderNetworkCode, "Exception while updating Receiver last request status in database , Exception:" + e.getMessage());
            }// end catch

            try {
                if (finalTransferStatusUpdate) {
                    // Setting modified on and by
                    c2sTransferVO.setModifiedOn(new Date());
                    c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    // Updating C2S Transfer details in database
                    // added by nilesh: consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        ChannelTransferBL.updateC2STransferDetails(con, c2sTransferVO);
                    }
                }
            } catch (BTSLBaseException be) {
                LOG.errorTrace(methodName, be);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsChnlRcvrController[run]", transferID,
                    senderMSISDN, senderNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
            }
            // if connection is not null then comitting the transaction and
            // closing the connection
            if (con != null) {
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
				if (mcomCon != null) {
					mcomCon.close("VchrConsChnlRcvrController#processThread");
					mcomCon = null;
				}
                con = null;
            }
            // If transaction is fail and grouptype counters need to be decrease
            // then decrease the counters
            // This change has been done by ankit on date 14/07/06 for SMS
            // charging
            if (!deliveryTrackDone && !c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && requestVO.isDecreaseGroupTypeCounter() && ((ChannelUserVO) requestVO
                .getSenderVO()).getUserControlGrouptypeCounters() != null) {
                PretupsBL.decreaseGroupTypeCounters(((ChannelUserVO) requestVO.getSenderVO()).getUserControlGrouptypeCounters());
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue() && c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVD)) {
                receiverMessageSendReq = false;
            }
            if (receiverMessageSendReq && !BTSLUtil.isStringIn(c2sTransferVO.getRequestGatewayCode(), notAllowedRecSendMessGatw) && !"ALL".equals(notAllowedRecSendMessGatw)) {
                if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    // Success message to receiver will be send only when the
                    // following condition is true:
                    // condition means either serviceType is EVR or (delivery
                    // receipt is not tracked and PIN is send to sender)
                    if (c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR) || (!deliveryTrackDone && senderMSISDN.equals(c2sTransferVO.getPinSentToMsisdn()))) {
                        if (c2sTransferVO.getReceiverReturnMsg() == null) {
                            (new PushMessage(receiverMSISDN, getReceiverSuccessMessage(), transferID, c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                        } else if (c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                            final BTSLMessages btslRecMessages = (BTSLMessages) c2sTransferVO.getReceiverReturnMsg();
                            (new PushMessage(receiverMSISDN, BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), transferID,
                                c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                        } else {
                            (new PushMessage(receiverMSISDN, (String) c2sTransferVO.getReceiverReturnMsg(), transferID, c2sTransferVO.getRequestGatewayCode(), receiverLocale))
                                .push();
                        }
                    }
                } else if (recTopupFailMessageRequired && c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    if (c2sTransferVO.getReceiverReturnMsg() == null) {
                        (new PushMessage(receiverMSISDN, getReceiverAmbigousMessage(), transferID, c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                    } else if (c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                        final BTSLMessages btslRecMessages = (BTSLMessages) c2sTransferVO.getReceiverReturnMsg();
                        (new PushMessage(receiverMSISDN, BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), transferID,
                            c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                    } else {
                        (new PushMessage(receiverMSISDN, (String) c2sTransferVO.getReceiverReturnMsg(), transferID, c2sTransferVO.getRequestGatewayCode(), receiverLocale))
                            .push();
                    }
                } else if (recTopupFailMessageRequired && c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    if (c2sTransferVO.getReceiverReturnMsg() == null) {
                        (new PushMessage(receiverMSISDN, getReceiverFailMessage(), transferID, c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                    } else if (c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                        final BTSLMessages btslRecMessages = (BTSLMessages) c2sTransferVO.getReceiverReturnMsg();
                        (new PushMessage(receiverMSISDN, BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), transferID,
                            c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                    } else {
                        (new PushMessage(receiverMSISDN, (String) c2sTransferVO.getReceiverReturnMsg(), transferID, c2sTransferVO.getRequestGatewayCode(), receiverLocale))
                            .push();
                    }
                }
            }

            // Message to sender will be send only when request gateway code is
            // allowed to send message
            if (!BTSLUtil.isStringIn(c2sTransferVO.getRequestGatewayCode(), notAllowedSendMessGatw)) {
                PushMessage pushMessages = null;
                if (btslMessages != null) {
                    // push final error message to sender
                    pushMessages = (new PushMessage(senderMSISDN, BTSLUtil.getMessage(senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs()), transferID,
                        c2sTransferVO.getRequestGatewayCode(), senderLocale));
                } else {
                    // push Additional Commission success message to sender and
                    // final status to sender
                    if (!BTSLUtil.isNullString(c2sTransferVO.getSenderReturnMessage())) {
                        pushMessages = (new PushMessage(senderMSISDN, c2sTransferVO.getSenderReturnMessage(), transferID, c2sTransferVO.getRequestGatewayCode(), senderLocale));
                    } else if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                        pushMessages = (new PushMessage(senderMSISDN, getSenderSuccessMessage(), transferID, c2sTransferVO.getRequestGatewayCode(), senderLocale));
                    } else if (c2sTransferVO.getErrorCode() != null) {
                        pushMessages = (new PushMessage(senderMSISDN, BTSLUtil.getMessage(senderLocale, c2sTransferVO.getErrorCode(), null), transferID, c2sTransferVO
                            .getRequestGatewayCode(), senderLocale));
                    }
                }// end if
                 // Message to sender will send only when
                 // 1. Either service type is EVR
                 // 2. OR transaction is not success
                 // 3. OR delivery receipt is not tracked and PIN is send to
                 // receiver

                // if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)||!c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)||(!_deliveryTracktDone
                // &&
                // receiverMSISDN.equals(c2sTransferVO.getPinSentToMsisdn())))
                // vfe
                // ...if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)||!deliveryTrackDone
                // && receiverMSISDN.equals(c2sTransferVO.getPinSentToMsisdn()))
                if (c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR) || !c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) || (!deliveryTrackDone && receiverMSISDN
                    .equals(c2sTransferVO.getPinSentToMsisdn()))) {
                    // If transaction is successfull then if group type counters
                    // reach limit then send message using gateway that is
                    // associated with group type profile
                    if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED))
                        .indexOf(requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE.equals(requestVO.getGroupType())) {
                        try {
                            GroupTypeProfileVO groupTypeProfileVO = null;
                            // load the user running and profile counters
                            // Check the counters
                            // update the counters
                            groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CHARGING);
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
                                SMSChargingLog.log(((ChannelUserVO) requestVO.getSenderVO()).getUserID(), (((ChannelUserVO) requestVO.getSenderVO())
                                    .getUserChargeGrouptypeCounters()).getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(),
                                    groupTypeProfileVO.getResGatewayType(), groupTypeProfileVO.getNetworkCode(), requestVO.getGroupType(), requestVO.getServiceType(),
                                    requestVO.getModule());
                            } else {
                                pushMessages.push();
                            }
                        } catch (Exception e) {
                            LOG.errorTrace(methodName, e);
                        }
                    } else {
                        pushMessages.push();
                    }
                }
            }

            // Log the credit back entry in the balance log
            if (creditBackEntryDone) {
                BalanceLogger.log(userBalancesVO);
            }

            // added by nilesh: consolidated for logger
            if (!oneLog) {
                OneLineTXNLog.log(c2sTransferVO, senderTransferItemVO, receiverTransferItemVO);
            }
            TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS, "Message=" + c2sTransferVO.getSenderReturnMessage());

            btslMessages = null;
            userBalancesVO = null;
            commonClient = null;
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, transferID, "Exiting");
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
        final String status = (String) map.get("TRANSACTION_STATUS");

        // added to log the IN validation request sent and request received
        // time. Start 07/02/2008
        if (null != map.get("IN_START_TIME")) {
            requestVO.setTopUPReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            requestVO.setTopUPReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
        }
        // End 07/02/2008
        // added for promo and COS by gaurav
        // For post validation request
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_POSTCREDIT_VAL_TIME"))) {
                requestVO.setPostValidationTimeTaken(Long.parseLong((String) map.get("IN_POSTCREDIT_VAL_TIME")));
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        // for credit request
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_RECHARGE_TIME"))) {
                requestVO.setCreditTime(Long.parseLong((String) map.get("IN_RECHARGE_TIME")));
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        // for promo request
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_PROMO_TIME"))) {
                requestVO.setPromoTime(Long.parseLong((String) map.get("IN_PROMO_TIME")));
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        // for cos update request
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_COS_TIME"))) {
                requestVO.setCosTime(Long.parseLong((String) map.get("IN_COS_TIME")));
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        // for ambiguous credit
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_CREDIT_VAL_TIME"))) {
                requestVO.setCreditValTime(Long.parseLong((String) map.get("IN_CREDIT_VAL_TIME")));
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        // for ambiguous promo
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_PROMO_VAL_TIME"))) {
                requestVO.setPromoValTime(Long.parseLong((String) map.get("IN_PROMO_VAL_TIME")));
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        // for ambiguous cos
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_COS_VAL_TIME"))) {
                requestVO.setCosValTime(Long.parseLong((String) map.get("IN_COS_VAL_TIME")));
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;// @@ ends
         // Start: Update the Interface table for the interface ID based on
         // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (LOG.isDebugEnabled()) {
            LOG.debug("updateForReceiverCreditResponse", "Mape from response=" + map + " status=" + status + " interface Status=" + interfaceStatusType);
        }
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        // setting transaction status for restricted subscriber feature
        if (PretupsI.STATUS_ACTIVE.equals((channelUserVO.getCategoryVO()).getRestrictedMsisdns())) {
            if (PretupsI.STATUS_ACTIVE.equals((channelUserVO.getCategoryVO()).getTransferToListOnly())) {
                ((RestrictedSubscriberVO) ((ReceiverVO) c2sTransferVO.getReceiverVO()).getRestrictedSubscriberVO()).setTempStatus(status);
            }
        }

        receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        String updateStatus = (String) map.get("UPDATE_STATUS");

        if (BTSLUtil.isNullString(updateStatus)) {
            updateStatus = status;
        }
        receiverTransferItemVO.setUpdateStatus(updateStatus);
        receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());
        receiverTransferItemVO.setUpdateStatus1((String) map.get("UPDATE_STATUS1"));
        receiverTransferItemVO.setUpdateStatus2((String) map.get("UPDATE_STATUS2"));

        if (!BTSLUtil.isNullString((String) map.get("ADJUST_AMOUNT"))) {
            receiverTransferItemVO.setAdjustValue(Long.parseLong((String) map.get("ADJUST_AMOUNT")));
        }

        receiverPostBalanceAvailable = (String) map.get("POST_BALANCE_ENQ_SUCCESS");
        // set from IN Module
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
            try {
                receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID1"))) {
            try {
                receiverTransferItemVO.setInterfaceReferenceID1((String) map.get("IN_TXN_ID1"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            receiverTransferItemVO.setTransferType1(PretupsI.TRANSFER_TYPE_BA_ADJ_CR);
        }
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID2"))) {
            try {
                receiverTransferItemVO.setInterfaceReferenceID2((String) map.get("IN_TXN_ID2"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            receiverTransferItemVO.setTransferType2(PretupsI.TRANSFER_TYPE_BA_ADJ_DR);
        }
        receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        String[] strArr = null;
        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            c2sTransferVO.setErrorCode(status + "_R");
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            receiverTransferItemVO.setTransferStatus(status);
            strArr = new String[] { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()) };
            // throw new
            // BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.C2S_RECEIVER_FAIL,0,strArr,null);
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", c2sTransferVO.getErrorCode(), 0, strArr, null);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
            c2sTransferVO.setErrorCode(status + "_R");
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            receiverTransferItemVO.setTransferStatus(status);
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            receiverTransferItemVO.setUpdateStatus(status);

            // Method call to update the list of bonuses
            operatorUtil.updateBonusListAfterTopup(map, c2sTransferVO);
            strArr = new String[] { transferID, receiverTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForReceiverValidateResponse", PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        } else {
            receiverTransferItemVO.setTransferStatus(status);
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            receiverTransferItemVO.setUpdateStatus(status);
            c2sTransferVO.setTransferValue(Long.parseLong(map.get("REQUESTED_AMOUNT").toString()));
            // Tunisia specific
            if (!BTSLUtil.isNullString((String) map.get("activationBonusProvided"))) {
                c2sTransferVO.setActiveBonusProvided((String) map.get("activationBonusProvided"));
            } else {
                c2sTransferVO.setActiveBonusProvided(PretupsI.YES);
            }
            /*
             * try{receiverTransferItemVO.setNewExpiry(BTSLUtil.
             * getDateFromDateString
             * ((String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy"));}catch(Exception
             * e){};
             * try{receiverTransferItemVO.setNewGraceDate(BTSLUtil.
             * getDateFromDateString
             * ((String)map.get("NEW_GRACE_DATE"),"ddMMyyyy"));}catch(Exception
             * e){};
             * try{receiverTransferItemVO.setPostBalance(Long.parseLong((String)map
             * .get("INTERFACE_POST_BALANCE")));}catch(Exception e){};
             */
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue())// @nu
            {
                try {
                    receiverTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                ;
                // try{receiverTransferItemVO.setNewGraceDate(BTSLUtil.getDateFromDateString((String)map.get("NEW_GRACE_DATE"),"ddMMyyyy"));}catch(Exception
                // e){LOG.errorTrace(methodName,e);};
                try {
                    receiverTransferItemVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                ;
                try {
                    receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PRE_BALANCE")));
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                ;
                try {
                    receiverTransferItemVO.setPostValidationStatus((String) map.get("POST_BALANCE_ENQ_SUCCESS"));
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                ;
            }
            try {
                if (!BTSLUtil.isNullString((String) map.get("IN_POSTCREDIT_VAL_TIME"))) {
                    requestVO.setPostValidationTimeTaken(Long.parseLong((String) map.get("IN_POSTCREDIT_VAL_TIME")));
                } else {
                    requestVO.setPostValidationTimeTaken(0L);
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            ;
            // Method call to update the list of bonuses
            operatorUtil.updateBonusListAfterTopup(map, c2sTransferVO);
        }

        try {
            c2sTransferVO.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            c2sTransferVO.setNewGraceDate(BTSLUtil.getDateFromDateString((String) map.get("NEW_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            c2sTransferVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;

        String new_promoBalance = null;
        String new_promoExpiry = null;
        if (BTSLUtil.isNullString(c2sTransferVO.getNewPromoBalance())) {
            new_promoBalance = (String) map.get("INTERFACE_PROMO_POST_BALANCE");
            if (!BTSLUtil.isNullString(new_promoBalance)) {
                new_promoBalance = PretupsI.PROMO_BALANCE_PREFIX + ":" + new_promoBalance;
            }

            // try{receiverTransferItemVO.setNewPromoBalance(Long.parseLong((String)map.get("INTERFACE_PROMO_POST_BALANCE")));}catch(Exception
            // e){};
        } else {
            new_promoBalance = (String) map.get("INTERFACE_PROMO_POST_BALANCE");
            if (!BTSLUtil.isNullString(new_promoBalance)) {
                new_promoBalance = c2sTransferVO.getNewPromoBalance() + "|" + PretupsI.PROMO_BALANCE_PREFIX + ":" + new_promoBalance;
            } else {
                new_promoBalance = c2sTransferVO.getNewPromoBalance();
            }

        }
        c2sTransferVO.setNewPromoBalance(new_promoBalance);
        try {
            if (BTSLUtil.isNullString(c2sTransferVO.getNewPromoExpiry())) {

                new_promoExpiry = (String) map.get("NEW_PROMO_EXPIRY_DATE");
                if (!BTSLUtil.isNullString(new_promoExpiry)) {

                    new_promoExpiry = PretupsI.PROMO_BALANCE_PREFIX + ":" + new_promoExpiry;
                }
                // new_promoExpiry=PretupsI.PROMO_BALANCE_PREFIX+":"+date;
            } else {
                new_promoExpiry = (String) map.get("NEW_PROMO_EXPIRY_DATE");
                if (!BTSLUtil.isNullString(new_promoExpiry)) {

                    new_promoExpiry = c2sTransferVO.getNewPromoExpiry() + "|" + PretupsI.PROMO_BALANCE_PREFIX + ":" + new_promoExpiry;

                } else {
                    new_promoExpiry = c2sTransferVO.getNewPromoExpiry();
                }

            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        c2sTransferVO.setNewPromoExpiry(new_promoExpiry);
        // try{receiverTransferItemVO.setNewPromoExpiry(BTSLUtil.getDateFromDateString((String)map.get("NEW_PROMO_EXPIRY_DATE"),"ddMMyyyy"));}catch(Exception
        // e){};
        try {
            final String promoStatus = (String) map.get("PROMO_STATUS");
            if (promoStatus.equals(InterfaceErrorCodesI.ERROR_RESPONSE)) {
                c2sTransferVO.setPromoStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            } else {
                c2sTransferVO.setPromoStatus(promoStatus);
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        c2sTransferVO.setInterfacePromoStatus((String) map.get("PROMO_INTERFACE_STATUS"));
        try {
            final String cosStatus = (String) map.get("COS_STATUS");
            if (cosStatus.equals(InterfaceErrorCodesI.ERROR_RESPONSE)) {
                c2sTransferVO.setCosStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            } else {
                c2sTransferVO.setCosStatus(cosStatus);
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;

        c2sTransferVO.setInterfaceCosStatus((String) map.get("COS_INTERFACE_STATUS"));
        c2sTransferVO.setNewServiceClssCode((String) map.get("INTERFACE_POST_COS"));
        try {
            c2sTransferVO.setPostCreditCoreValidity(BTSLUtil.getDateFromDateString((String) map.get("POSTCRE_NEW_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            c2sTransferVO.setNewGraceDate(BTSLUtil.getDateFromDateString((String) map.get("NEW_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            c2sTransferVO.setPostCreditCoreBalance(Long.parseLong((String) map.get("POSTCRE_INTERFACE_POST_BALANCE")));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            c2sTransferVO.setPostCreditPromoBalance(Long.parseLong((String) map.get("POSTCRE_INTERFACE_PROMO_POST_BALANCE")));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            c2sTransferVO.setPostCreditPromoValidity(BTSLUtil.getDateFromDateString((String) map.get("POSTCRE_NEW_PROMO_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            c2sTransferVO.setPostValidationStatus((String) map.get("POSTCRE_TRANSACTION_STATUS"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;

    }

    /**
     * Method that will perform the validation request in thread
     * 
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processValidationRequestInThread() throws BTSLBaseException, Exception {
        final String methodName = "processValidationRequestInThread";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered and performing validations for transfer ID=" + transferID);
        }
        try {
            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            processValidationRequest();
        } catch (BTSLBaseException be) {
            LOG.error("VchrConsChnlRcvrController[processValidationRequestInThread]", "Getting BTSL Base Exception:" + be.getMessage());
            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            if (c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(transferID), PretupsBL
                    .getDisplayAmount(c2sTransferVO.getRequestedAmount()) }));
            }

            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "VchrConsChnlRcvrController[processValidationRequestInThread]", transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            // !transferDetailAdded Condition Added as we think its not require
            // as already done
            if (transferID != null && !transferDetailAdded) {
                Connection con = null;MComConnectionI mcomCon = null;
                try {
                    mcomCon = new MComConnection();con=mcomCon.getConnection();
                    // added by nilesh: consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        // addEntryInTransfers(con);
                    }
                    if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        finalTransferStatusUpdate = false; // No need to update
                        // the status of
                        // transaction in run
                        // method
                    }
                } catch (Exception e) {
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (Exception ex) {
                            LOG.errorTrace(methodName, ex);
                        }
                    }
					if (mcomCon != null) {
						mcomCon.close("VchrConsChnlRcvrController#processValidationRequestInThread");
						mcomCon = null;
					}
                    con=null;
                    LOG.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "VchrConsChnlRcvrController[processValidationRequestInThread]", transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
                } finally {
					if (mcomCon != null) {
						mcomCon.close("VchrConsChnlRcvrController#processValidationRequestInThread");
						mcomCon = null;
					}
                    con = null;
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
    }

    /**
     * Sets the sender transfer Items VO for the channel user
     * 
     */
    private void setSenderTransferItemVO() {
        senderTransferItemVO = new C2STransferItemVO();
        // set sender transfer item details
        senderTransferItemVO.setSNo(1);
        senderTransferItemVO.setMsisdn(senderMSISDN);
        senderTransferItemVO.setRequestValue(c2sTransferVO.getRequestedAmount());
        senderTransferItemVO.setSubscriberType(senderSubscriberType);
        senderTransferItemVO.setTransferDate(currentDate);
        senderTransferItemVO.setTransferDateTime(currentDate);
        senderTransferItemVO.setTransferID(c2sTransferVO.getTransferID());
        senderTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
        senderTransferItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
        senderTransferItemVO.setEntryDate(currentDate);
        senderTransferItemVO.setEntryDateTime(currentDate);
        senderTransferItemVO.setEntryType(PretupsI.DEBIT);
        senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
        senderTransferItemVO.setPrefixID((channelUserVO.getUserPhoneVO()).getPrefixID());
        senderTransferItemVO.setLanguage(senderLocale.getLanguage());
        senderTransferItemVO.setCountry(senderLocale.getCountry());
    }

    /**
     * Sets the receiever transfer Items VO for the subscriber
     * 
     */
    private void setReceiverTransferItemVO() {
        receiverTransferItemVO = new C2STransferItemVO();
        receiverTransferItemVO.setSNo(2);
        receiverTransferItemVO.setMsisdn(receiverMSISDN);
        receiverTransferItemVO.setRequestValue(c2sTransferVO.getRequestedAmount());
        receiverTransferItemVO.setSubscriberType(receiverVO.getSubscriberType());
        receiverTransferItemVO.setTransferDate(currentDate);
        receiverTransferItemVO.setTransferDateTime(currentDate);
        receiverTransferItemVO.setTransferID(c2sTransferVO.getTransferID());
        receiverTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
        receiverTransferItemVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
        receiverTransferItemVO.setEntryDate(currentDate);
        receiverTransferItemVO.setEntryDateTime(currentDate);
        receiverTransferItemVO.setEntryType(PretupsI.CREDIT);
        receiverTransferItemVO.setPrefixID(receiverVO.getPrefixID());
        receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.RECEIVER_UNDERPROCESS_SUCCESS);
        receiverTransferItemVO.setLanguage(receiverLocale.getLanguage());
        receiverTransferItemVO.setCountry(receiverLocale.getCountry());

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

    private boolean getInterfaceRoutingDetails(Connection p_con, String p_msisdn, long p_prefixID, String p_subscriberType, String p_networkCode, String p_serviceType, String p_interfaceCategory, String p_userType, String p_action) throws BTSLBaseException {
        final String methodName = "getInterfaceRoutingDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
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
        SubscriberRoutingControlVO subscriberRoutingControlVO = null;
        try {
            if (!onlyForEvr) {
                serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                    .getServiceInterfaceRoutingDetails(receiverVO.getNetworkCode() + "_" + requestVO.getServiceType() + "_" + requestVO.getType());
                if (serviceInterfaceRoutingVO != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "process",
                            requestIDStr,
                            "For =" + receiverVO.getNetworkCode() + "_" + requestVO.getServiceType() + " Got Interface Category=" + serviceInterfaceRoutingVO
                                .getInterfaceType() + " Alternate Check Required=" + serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + serviceInterfaceRoutingVO
                                .getAlternateInterfaceType());
                    }

                    p_interfaceCategory = serviceInterfaceRoutingVO.getInterfaceType();
                    // useAlternateCategory=serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    // _defaultSelector=serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();

                    if (!serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool()) {
                        throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.VOUCHER_TO_BE_SENT_INTERFACE_NOT_DEFINED);
                    }

                    final RoutingControlDAO routingControlDAO = new RoutingControlDAO();
                    final ArrayList routingControlList = routingControlDAO.loadRoutingControlDetailsList();

                    // alternate interface type should be defined in
                    // routing_control table
                    boolean inerfaceFound = false;
                    final Iterator iterator = routingControlList.iterator();
                    while (iterator.hasNext()) {
                        subscriberRoutingControlVO = (SubscriberRoutingControlVO) iterator.next();
                        if (subscriberRoutingControlVO.getInterfaceCategory().equals(serviceInterfaceRoutingVO.getAlternateInterfaceType()) && PretupsI.SERVICE_TYPE_VCNO2C
                            .equals(subscriberRoutingControlVO.getServiceType())) {
                            inerfaceFound = true;
                            break;
                        }
                    }

                    if (!inerfaceFound) {
                        throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.VOUCHER_TO_BE_SENT_INTERFACE_NOT_DEFINED);
                    }

                } else {
                    p_interfaceCategory = PretupsI.INTERFACE_CATEGORY_VOMS;
                    LOG.info("process", requestVO.getRequestIDStr(), "Service Interface Routing control Not defined");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SEnquiryHandler[process]", "", "", "",
                        "Service Interface Routing control Not defined");
                    // p_interfaceCategory=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                }

                subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
            } else {
                if (serviceInterfaceRoutingVO != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "process",
                            requestIDStr,
                            "For =" + receiverVO.getNetworkCode() + "_" + requestVO.getServiceType() + " Got Interface Category=" + serviceInterfaceRoutingVO
                                .getInterfaceType() + " Alternate Check Required=" + serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + serviceInterfaceRoutingVO
                                .getAlternateInterfaceType());
                    }

                    p_interfaceCategory = serviceInterfaceRoutingVO.getAlternateInterfaceType();
                    // useAlternateCategory=serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    // _defaultSelector=serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                } else {
                    LOG.info("process", requestVO.getRequestIDStr(),
                        "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2SEnquiryHandler[process]", "", "", "",
                        "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    // p_interfaceCategory=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                }

                subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
            }

            //
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " subscriberRoutingControlVO=" + subscriberRoutingControlVO);
            }
            receiverSubscriberType = p_subscriberType;

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
                            interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(serviceType + "_" + c2sTransferVO
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
                    // added by rahul.d to check service selector based check
                    // loading of interface
                    ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                    MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                        interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(serviceType + "_" + c2sTransferVO
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
                // This event is raised by ankit Z on date 3/8/06 for case when
                // entry not found in routing control and considering series
                // based routing
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "VchrConsChnlRcvrController[getInterfaceRoutingDetails]", transferID, senderMSISDN, senderNetworkCode,
                    "Routing control information not defined so performing series based routing");
                // added by rahul.d to check service selector based check
                // loading of interface
                ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                // if preference is true load service slector based mapping
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                    interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache
                        .getObject(serviceType + "_" + c2sTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                }
                if (interfaceMappingVO1 == null) {
                    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
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
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "VchrConsChnlRcvrController[getInterfaceRoutingDetails]", transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            isSuccess = false;
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Exiting with isSuccess=" + isSuccess);
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
        Connection con = null;MComConnectionI mcomCon = null;
        InterfaceVO interfaceVO = null;
        final String methodName = "processValidationRequest";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered and performing validations for transfer ID=" + transferID + " " + c2sTransferVO.getModule() + " " + c2sTransferVO
                .getReceiverNetworkCode() + " " + type);
        }

        try {
            final CommonClient commonClient = new CommonClient();
            final InterfaceVO recInterfaceVO = null;
            itemList = new ArrayList();
            itemList.add(senderTransferItemVO);
            itemList.add(receiverTransferItemVO);
            c2sTransferVO.setTransferItemList(itemList);
            final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(c2sTransferVO.getModule(),
                c2sTransferVO.getReceiverNetworkCode(), type);
            intModCommunicationTypeR = networkInterfaceModuleVOS.getCommunicationType();
            intModIPR = networkInterfaceModuleVOS.getIP();
            intModPortR = networkInterfaceModuleVOS.getPort();
            intModClassNameR = networkInterfaceModuleVOS.getClassName();
            // Till here we get the IN interface validation response.. if the
            // service is EVR
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            receiverTransferItemVO.setPreviousExpiry(currentDate);
            // ***Construct & validate VOMS validation request using common
            // client*************
            networkInterfaceModuleVO = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(c2sTransferVO.getModule(), c2sTransferVO.getReceiverNetworkCode(),
                PretupsI.INTERFACE_CATEGORY_VOMS);
            final EvdUtil evdUtil = new EvdUtil();
            interfaceVO = new InterfaceVO();
            interfaceVO.setInterfaceId(senderTransferItemVO.getInterfaceID());
            interfaceVO.setHandlerClass(senderTransferItemVO.getInterfaceHandlerClass());
            final String requestStr = evdUtil.getVCNO2CUpdateRequestStr(PretupsI.INTERFACE_VALIDATE_ACTION, c2sTransferVO, networkInterfaceModuleVO, interfaceVO,
                VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_ENABLE);
            LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.SENDER_UNDER_VAL);

            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            final String receiverValResponse = commonClient.process(requestStr, transferID, networkInterfaceModuleVO.getCommunicationType(), networkInterfaceModuleVO.getIP(),
                networkInterfaceModuleVO.getPort(), networkInterfaceModuleVO.getClassName());
            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, transferID, "Got the validation response from VOMS Handler receiverValResponse=" + receiverValResponse);
            }
            itemList = new ArrayList();
            itemList.add(senderTransferItemVO);
            itemList.add(receiverTransferItemVO);
            c2sTransferVO.setTransferItemList(itemList);

            try {
                updateForVOMSValidationResponse(receiverValResponse);
                VomsVoucherChangeStatusLog.log(transferID, vomsVO.getSerialNo(), VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_UNPROCESS, c2sTransferVO.getReceiverNetworkCode(),
                    channelUserVO.getUserID(), BTSLUtil.getDateTimeStringFromDate(currentDate));
            } catch (BTSLBaseException be) {
                LoadController.decreaseResponseCounters(transferID, receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "inside catch of BTSL Base Exception: " + be.getMessage() + " vomsInterfaceInfoInDBFound: " + vomsInterfaceInfoInDBFound);
                }
                if (vomsInterfaceInfoInDBFound && senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
                    PretupsBL.deleteSubscriberInterfaceRouting(receiverMSISDN, PretupsI.INTERFACE_CATEGORY_VOMS);
                }

                // validate receiver limits after Interface Validations
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                    PretupsBL.validateRecieverLimits(null, c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
                }
                throw be;
            } catch (Exception e) {
                LoadController.decreaseResponseCounters(transferID, receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

                // validate receiver limits after Interface Validations
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                    PretupsBL.validateRecieverLimits(null, c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
                }
                throw e;
            }
            voucherMarked = true;

            LoadController.decreaseResponseCounters(transferID, receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

            // If request is taking more time till validation of subscriber than
            // reject the request.
            InterfaceVO vomsInterfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(interfaceVO.getInterfaceId());
            if ((System.currentTimeMillis() - c2sTransferVO.getRequestStartTime()) > vomsInterfaceVO.getValExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                    "VchrConsChnlRcvrController[processValidationRequest]", transferID, senderMSISDN, senderNetworkCode,
                    "Exception: System is taking more time till validation of voucher");
                throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
            }

            // Get the service Class ID based on the code
            PretupsBL.validateServiceClassChecks(con, receiverTransferItemVO, c2sTransferVO, PretupsI.C2S_MODULE, requestVO.getServiceType());
            receiverVO.setServiceClassCode(receiverTransferItemVO.getServiceClass());

            // validate sender receiver service class,validate transfer value
            PretupsBL.validateTransferRule(con, c2sTransferVO, PretupsI.C2S_MODULE);

            // validate receiver limits after Interface Validations
            PretupsBL.validateRecieverLimits(con, c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);

            // calculate card group details
            CardGroupBL.calculateCardGroupDetails(con, c2sTransferVO, PretupsI.C2S_MODULE, true);

            vomsInterfaceVO = null;

            // This method will set various values into items and transferVO
            evdUtil.calulateTransferValue(c2sTransferVO, vomsVO);

            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Validity=" + c2sTransferVO.getReceiverValidity() + " Talk Time=" + c2sTransferVO.getReceiverTransferValue() + " Serial number=" + vomsVO.getSerialNo(),
                PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                PretupsBL.validateRecieverLimits(null, c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.C2S_MODULE);
            }

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Here the code for debiting the user account will come and Update
            // Transfer Out Counts for the sender
            /*
             * userBalancesVO=ChannelUserBL.debitUserBalanceForProduct(con,
             * transferID,c2sTransferVO);
             * ChannelTransferBL.increaseC2STransferOutCounts(con,c2sTransferVO,true
             * );
             */
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

            populateServiceInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            if (PretupsI.SERVICE_TYPE_EVD.equals(c2sTransferVO.getServiceType())) {
                receiverTransferItemVO.setServiceClass(vomsAllServiceClassID);
                final String pinSendTo = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_SEND_TO));
                // Construct the PIN message for sender or receiver as the case
                // is
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "PIN sent to in preference=" + pinSendTo);
                }
                // changed for EVD private recharge (as subservice =1 )
                if (PretupsI.PIN_SENT_RET.equals(pinSendTo) || (c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVD) && "1".equals(c2sTransferVO.getSubService()))) {
                    c2sTransferVO.setPinSentToMsisdn(senderMSISDN);
                } else {
                    c2sTransferVO.setPinSentToMsisdn(receiverMSISDN);
                }
            }
            senderTransferItemVO.setServiceClass(vomsAllServiceClassID);
            // Method to insert the record in c2s transfer table
            // added by nilesh: consolidated for logger
            if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                ChannelTransferBL.addC2STransferDetails(con, c2sTransferVO);
            }
            transferDetailAdded = true;
            // Commit the transaction and relaease the locks
            try {
                mcomCon.finalCommit();
            } catch (Exception be) {
                LOG.errorTrace(methodName, be);
            }
			if (mcomCon != null) {
				mcomCon.close("VchrConsChnlRcvrController#processValidationRequest");
				mcomCon = null;
			}
            con = null;

            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Marked Under process, voucher Serial number=" + vomsVO.getSerialNo(), PretupsI.TXN_LOG_STATUS_UNDERPROCESS, "");

            // Log the details if the transfer Details were added i.e. if User
            // was debitted
            /*
             * if(transferDetailAdded) {
             * BalanceLogger.log(userBalancesVO);
             * }
             * 
             * //Push Under Process Message to Sender and Reciever , this might
             * have to be implemented on flag basis whether to send message or
             * not
             * if(c2sTransferVO.isUnderProcessMsgReq() &&
             * receiverMessageSendReq&&
             * !BTSLUtil.isStringIn(c2sTransferVO.getRequestGatewayCode
             * (),notAllowedRecSendMessGatw
             * )&&!"ALL".equals(notAllowedRecSendMessGatw)) {
             * (new PushMessage(receiverMSISDN,getReceiverUnderProcessMessage(),
             * transferID
             * ,c2sTransferVO.getRequestGatewayCode(),receiverLocale)).push();
             * }
             */
            // If request is taking more time till validation of subscriber than
            // reject the request.
            // intrfaceVO=(InterfaceVO)NetworkInterfaceModuleCache.getObject(interfaceVO.getInterfaceId());
            /*
             * if(c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR
             * ))
             * {
             * if((System.currentTimeMillis()-c2sTransferVO.getRequestStartTime()
             * )>recInterfaceVO.getTopUpExpiryTime())
             * {
             * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
             * EventStatusI.RAISED,EventLevelI.INFO,
             * "VchrConsChnlRcvrController[processValidationRequest]"
             * ,transferID,senderMSISDN,senderNetworkCode,
             * "Exception: System is taking more time till topup");
             * throw new
             * BTSLBaseException("VchrConsChnlRcvrController",methodName
             * ,PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
             * }
             * recInterfaceVO=null;
             * }
             */
            if (c2sTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || processedFromQueue) {
                // create new Thread
                final Thread _controllerThread = new Thread(this);
                _controllerThread.start();
                oneLog = false;
            }
        } catch (BTSLBaseException be) {
            if (con != null) {
                mcomCon.finalRollback();
            }
            con = null;
            if (recValidationFailMessageRequired) {
                if (c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(transferID), PretupsBL
                        .getDisplayAmount(c2sTransferVO.getRequestedAmount()) }));
                }
            }
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
            }
            LOG.error("VchrConsChnlRcvrController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());

            voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            if (con != null) {
                mcomCon.finalRollback();
            }
            con = null;
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (recValidationFailMessageRequired) {
                if (c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD), new String[] { String.valueOf(transferID), PretupsBL
                        .getDisplayAmount(c2sTransferVO.getRequestedAmount()) }));
                }
            }
            if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

            throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("VchrConsChnlRcvrController#processValidationRequest");
				mcomCon = null;
			}
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

    private void sendSMS(VomsVoucherVO p_vomsVO) throws BTSLBaseException, Exception {
        final String methodName = "sendSMS";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered transferID=" + transferID + " p_vomsVO=" + p_vomsVO);
        }
        String pinMessage = null;
        String binaryPinMessage = null;
        Locale locale = null;
        InterfaceVO interfaceVO = null;
        interfaceVO = new InterfaceVO();
        interfaceVO.setInterfaceId(senderTransferItemVO.getInterfaceID());
        interfaceVO.setHandlerClass(senderTransferItemVO.getInterfaceHandlerClass());
        final EvdUtil evdUtil = new EvdUtil();
        boolean creditbackdone = false;
        boolean smsChargingRequired = false;
        try {

            GroupTypeProfileVO groupTypeProfileVO = null;
            if (senderMSISDN.equals(c2sTransferVO.getPinSentToMsisdn())) {
                // binaryPinMessage=BTSLUtil.getMessage(senderLocale,PretupsErrorCodesI.BIN_PIN_MESSAGE_FOR_R,new
                // String[]{transferID,PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()),receiverMSISDN,BTSLUtil.decryptText(p_vomsVO.getPinNo()),p_vomsVO.getSerialNo(),p_vomsVO.getExpiryDateStr()});
                binaryPinMessage = BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.BIN_PIN_MESSAGE_FOR_R, new String[] { transferID, PretupsBL
                    .getDisplayAmount(c2sTransferVO.getRequestedAmount()), receiverMSISDN, VomsUtil.decryptText(p_vomsVO.getPinNo()), p_vomsVO.getSerialNo(), p_vomsVO
                    .getExpiryDateStr() });
                binaryPinMessage = operatorUtil.DES3Encryption(binaryPinMessage, requestVO);
                // pinMessage=BTSLUtil.getMessage(senderLocale,PretupsErrorCodesI.PIN_MESSAGE_FOR_R,new
                // String[]{transferID,PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()),receiverMSISDN,BTSLUtil.decryptText(p_vomsVO.getPinNo()),p_vomsVO.getSerialNo()});
                pinMessage = BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.PIN_MESSAGE_FOR_R, new String[] { transferID, PretupsBL.getDisplayAmount(c2sTransferVO
                    .getRequestedAmount()), receiverMSISDN, VomsUtil.decryptText(p_vomsVO.getPinNo()), p_vomsVO.getSerialNo() });
                locale = senderLocale;
                // always send in english
                // binaryPinMessage=BTSLUtil.getMessage(new
                // Locale("en","US"),PretupsErrorCodesI.BIN_PIN_MESSAGE_FOR_R,new
                // String[]{transferID,PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()),receiverMSISDN,BTSLUtil.decryptText(p_vomsVO.getPinNo()),p_vomsVO.getSerialNo(),p_vomsVO.getExpiryDateStr()});
                // binaryPinMessage=operatorUtil.DES3Encryption(binaryPinMessage,requestVO);

                if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)).indexOf(requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE
                    .equals(requestVO.getGroupType())) {
                    try {
                        // load the user running and profile counters
                        // Check the counters
                        // update the counters
                        groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CHARGING);
                        // if group type counters reach limit then send message
                        // using gateway that is associated with group type
                        // profile
                        if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
                            smsChargingRequired = true;
                            SMSChargingLog.log(((ChannelUserVO) requestVO.getSenderVO()).getUserID(), (((ChannelUserVO) requestVO.getSenderVO())
                                .getUserChargeGrouptypeCounters()).getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(),
                                groupTypeProfileVO.getResGatewayType(), groupTypeProfileVO.getNetworkCode(), requestVO.getGroupType(), requestVO.getServiceType(), requestVO
                                    .getModule());
                        }
                    } catch (Exception e) {
                        LOG.errorTrace(methodName, e);
                    }
                }
            } else {
                // pinMessage=BTSLUtil.getMessage(receiverLocale,PretupsErrorCodesI.PIN_MESSAGE_FOR_C,new
                // String[]{transferID,PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()),senderMSISDN,BTSLUtil.decryptText(p_vomsVO.getPinNo()),p_vomsVO.getSerialNo()});
                pinMessage = BTSLUtil.getMessage(receiverLocale, PretupsErrorCodesI.PIN_MESSAGE_FOR_C, new String[] { transferID, PretupsBL.getDisplayAmount(c2sTransferVO
                    .getRequestedAmount()), senderMSISDN, VomsUtil.decryptText(p_vomsVO.getPinNo()), p_vomsVO.getSerialNo() });
                locale = receiverLocale;
            }
            final PushMessage pushMessage = new PushMessage(c2sTransferVO.getPinSentToMsisdn(), pinMessage, transferID, c2sTransferVO.getRequestGatewayCode(), locale);
            PushMessage pushMessage1 = null;
            if (requestVO.getPrivateRechBinMsgAllowed()) {
                pushMessage1 = new PushMessage(c2sTransferVO.getPinSentToMsisdn(), binaryPinMessage, transferID, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECH_MESSGATEWAY)), locale);
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
                updateMySqlCt = EvdUtil.checkMySqlConnUp(c2sTransferVO);
                // updateMySqlCt=1;
                if (updateMySqlCt > 0) {
                    // Push underprocess message with receipt
                    if (smsChargingRequired) {
                        retKannstatus = pushMessage.pushSmsUrlWithReceipt(false, c2sTransferVO.getTransferID(), groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO
                            .getAltGatewayCode());
                        if (requestVO.getPrivateRechBinMsgAllowed()) {
                            retBinKannstatus = pushMessage1.pushSmsUrlWithReceipt(false, c2sTransferVO.getTransferID(), groupTypeProfileVO.getGatewayCode(),
                                groupTypeProfileVO.getAltGatewayCode());
                        }
                    } else {
                        retKannstatus = pushMessage.pushSmsUrlWithReceipt(false, c2sTransferVO.getTransferID(), null, null);
                        if (requestVO.getPrivateRechBinMsgAllowed()) {
                            retBinKannstatus = pushMessage1.pushSmsUrlWithReceipt(false, c2sTransferVO.getTransferID(), null, null);
                        }
                    }
                    if (!retKannstatus.equalsIgnoreCase(PretupsI.GATEWAY_MESSAGE_SUCCESS)) // &&
                    // !retBinKannstatus.equalsIgnoreCase(PretupsI.GATEWAY_MESSAGE_SUCCESS)
                    {
                        // credit back the sender and voucher
                        creditbackdone = true;
                        c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                        c2sTransferVO.setErrorCode(PretupsErrorCodesI.VMS_PIN_SENT_FAIL);
                        voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                        // Added so that Failed Counters can be increased
                        ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(), requestVO.getMessageGatewayVO().getGatewayType(),
                            senderNetworkCode, serviceType, transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, receiverVO.getNetworkCode());
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                methodName,
                                "Transfer ID=" + transferID + " Message Received by kannel Got Status=" + retKannstatus + " leave the controller now and wait for Delivery Receipt from kannel");
                        }
                        finalTransferStatusUpdate = false;
                        deliveryTrackDone = true;
                    }
                } else {
                    creditbackdone = true;
                    c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    c2sTransferVO.setErrorCode(PretupsErrorCodesI.VMS_PIN_SENT_FAIL);
                    voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                    // Added so that Failed Counters can be increased
                    ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(), requestVO.getMessageGatewayVO().getGatewayType(), senderNetworkCode,
                        serviceType, transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, receiverVO.getNetworkCode());
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
                    if (requestVO.getPrivateRechBinMsgAllowed()) {
                        retBinKannstatus = pushMessage1.pushMessageWithStatus(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());
                    }
                } else {
                    retKannstatus = pushMessage.pushMessageWithStatus(null, null);
                    if (requestVO.getPrivateRechBinMsgAllowed()) {
                        retBinKannstatus = pushMessage1.pushMessageWithStatus(null, null);
                    }
                }
                if (retKannstatus.equals(PretupsI.GATEWAY_MESSAGE_SUCCESS)) // &&
                // retBinKannstatus.equals(PretupsI.GATEWAY_MESSAGE_SUCCESS)
                {
                    try {
                        finalTransferStatusUpdate = evdUtil.updateVoucherAndGiveDifferentials(receiverVO, c2sTransferVO, networkInterfaceModuleVO, interfaceVO, requestVO
                            .getInstanceID(), false);
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        throw e;
                    }

                } else// message sending failed i.e. message is not accepted by
                      // kannel
                {
                    creditbackdone = true;

                    c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    c2sTransferVO.setErrorCode(PretupsErrorCodesI.VMS_PIN_SENT_FAIL);
                    voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                    // Added so that Failed Counters can be increased
                    ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(), requestVO.getMessageGatewayVO().getGatewayType(), senderNetworkCode,
                        serviceType, transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, receiverVO.getNetworkCode());
                }
            }
        } catch (BTSLBaseException be) {
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
            }// end if
            if (!creditbackdone) {
                voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            }
            throw be;
        } catch (Exception e) {
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }

            if (!creditbackdone) {
                voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            }
            throw e;
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited transferID=" + transferID + " c2sTransferVO.getTransferStatus()=" + c2sTransferVO.getTransferStatus());
            }

        }
    }

    /**
     * updateForVOMSValidationResponse
     * Method to process the response of the receiver validation from VOMS
     * 
     * @param str
     * @throws BTSLBaseException
     */

    public void updateForVOMSValidationResponse(String str) throws BTSLBaseException {
        final String methodName = "updateForVOMSValidationResponse";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        receiverVO.setInterfaceResponseCode(senderTransferItemVO.getInterfaceResponseCode());
        senderTransferItemVO.setValidationStatus(status);
        senderTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));

        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        String[] strArr = null;
        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            c2sTransferVO.setErrorCode(status + "_S");
            senderTransferItemVO.setTransferStatus(status);
            if (PretupsI.SERVICE_TYPE_EVD.equals(serviceType)) {
                receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                receiverTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            }
            strArr = new String[] { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, c2sTransferVO.getErrorCode(), 0, strArr, null);
        }
        senderTransferItemVO.setTransferStatus(status);
        receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        // Set the service class received from the IN.
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "SERVICE_CLASS=" + (String) map.get("SERVICE_CLASS"));
        }
        receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));

        vomsVO = new VomsVoucherVO();
        vomsVO.setProductID((String) map.get("PRODUCT_ID"));
        vomsVO.setSerialNo((String) map.get("SERIAL_NUMBER"));
        vomsVO.setCurrentStatus((String) map.get("UPDATE_STATUS"));
        vomsVO.setPreviousStatus((String) map.get("PREVIOUS_STATUS"));
        vomsVO.setTransactionID((String) map.get("TRANSACTION_ID"));
        vomsVO.setCategoryType((String) map.get("CATEGORY_TYPE"));

        if ("null".equals((String) map.get("SERIAL_NUMBER"))) {
            throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.VOUCHER_NOT_FOUND);
        }

        vomsVO.setTalkTime(Long.parseLong((String) map.get("TALK_TIME")));
        vomsVO.setValidity(Integer.parseInt((String) map.get("VALIDITY")));
        vomsVO.setPinNo((String) map.get("PIN"));
        try {
            final Date expDate = BTSLUtil.getDateFromDateString((String) map.get("VOUCHER_EXPIRY_DATE"), "yyyyMMdd");
            vomsVO.setExpiryDateStr(BTSLUtil.getDateStringFromDate(expDate));
            vomsVO.setExpiryDate(expDate);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        c2sTransferVO.setSerialNumber((String) map.get("SERIAL_NUMBER"));
        senderTransferItemVO.setTransferValue(Long.parseLong((String) map.get("PAYABLE_AMT")));
        c2sTransferVO.setRequestedAmount(Long.parseLong((String) map.get("PAYABLE_AMT")));
        final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
            .getRoutingControlDetails(c2sTransferVO.getReceiverNetworkCode() + "_" + c2sTransferVO.getServiceType() + "_" + PretupsI.INTERFACE_CATEGORY_VOMS);
        if (!vomsInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
            PretupsBL.insertSubscriberInterfaceRouting(senderTransferItemVO.getInterfaceID(), vomsExternalID, receiverMSISDN, PretupsI.INTERFACE_CATEGORY_VOMS, channelUserVO
                .getUserID(), currentDate);
            vomsInterfaceInfoInDBFound = true;
        }

        if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue()) {
            final String payAmt = (String) map.get("RECEIVER_PAYABLE_AMT");
            if (!BTSLUtil.isNullString(payAmt) && BTSLUtil.isNumeric(payAmt)) {
                payableAmt = PretupsBL.getDisplayAmount(Long.parseLong(payAmt));
            }
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
     * Method to get the string to be sent to the interface for topup
     * 
     * @return
     */
    private String getReceiverCommonString() {
        final String methodName = "getReceiverCommonString";
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + receiverMSISDN);
        strBuff.append("&TRANSACTION_ID=" + transferID);
        strBuff.append("&NETWORK_CODE=" + receiverVO.getNetworkCode());
        strBuff.append("&INTERFACE_ID=" + receiverTransferItemVO.getInterfaceID());
        strBuff.append("&INTERFACE_HANDLER=" + receiverTransferItemVO.getInterfaceHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + intModCommunicationTypeR);
        strBuff.append("&INT_MOD_IP=" + intModIPR);
        strBuff.append("&INT_MOD_PORT=" + intModPortR);
        strBuff.append("&INT_MOD_CLASSNAME=" + intModClassNameR);
        strBuff.append("&MODULE=" + PretupsI.C2S_MODULE);
        // added for CRE_INT_CR00029 by ankit Zindal
        strBuff.append("&CARD_GROUP_SELECTOR=" + requestVO.getReqSelector());
        strBuff.append("&USER_TYPE=R");
        strBuff.append("&REQ_SERVICE=" + serviceType);
        strBuff.append("&INT_ST_TYPE=" + c2sTransferVO.getReceiverInterfaceStatusType());
        try {
            strBuff.append("&TRANSFER_DATE=" + BTSLUtil.getDateTimeStringFromDate(c2sTransferVO.getTransferDate(), PretupsI.TIMESTAMP_DATESPACEHHMM));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        return strBuff.toString();
    }

    /**
     * Method to get the string to be sent to the interface for topup
     * 
     * @return
     */
    public String getReceiverCreditStr() {
        final String methodName = "getReceiverCreditStr";
        Long previous_balance = 0L;
        Date _previousPromoExpiry = null;
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getReceiverCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_CREDIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + c2sTransferVO.getReceiverTransferValue());
        strBuff.append("&GRACE_DAYS=" + receiverTransferItemVO.getGraceDaysStr());
        strBuff.append("&CARD_GROUP=" + c2sTransferVO.getCardGroupCode());
        strBuff.append("&MIN_CARD_GROUP_AMT=" + c2sTransferVO.getMinCardGroupAmount());
        strBuff.append("&SENDER_MSISDN=" + senderMSISDN);
        strBuff.append("&SENDER_ID=" + channelUserVO.getUserID());
        strBuff.append("&SENDER_EXTERNAL_CODE=" + channelUserVO.getExternalCode());
        strBuff.append("&PRODUCT_CODE=" + c2sTransferVO.getProductCode());
        strBuff.append("&VALIDITY_DAYS=" + c2sTransferVO.getReceiverValidity());
        strBuff.append("&BONUS_VALIDITY_DAYS=" + c2sTransferVO.getReceiverBonusValidity());
        strBuff.append("&BONUS_AMOUNT=" + c2sTransferVO.getReceiverBonusValue());
        try {
            strBuff.append("&OLD_EXPIRY_DATE=" + BTSLUtil.getDateStringFromDate(receiverTransferItemVO.getPreviousExpiry(), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        try {
            strBuff.append("&OLD_GRACE_DATE=" + BTSLUtil.getDateStringFromDate(receiverTransferItemVO.getPreviousGraceDate(), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        strBuff.append("&INTERFACE_PREV_BALANCE=" + receiverTransferItemVO.getPreviousBalance());
        // Avinash send the requested amount to IN. to use card group only for
        // reporting purpose.
        strBuff.append("&REQUESTED_AMOUNT=" + c2sTransferVO.getRequestedAmount());
        strBuff.append("&O2C_REQUESTED_AMOUNT=" + PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()));
        strBuff.append("&SERVICE_CLASS=" + receiverTransferItemVO.getServiceClassCode());
        strBuff.append("&SOURCE_TYPE=" + c2sTransferVO.getSourceType());
        strBuff.append("&SERIAL_NUMBER=" + vomsVO.getSerialNo());
        strBuff.append("&CATEGORY_TYPE=" + vomsVO.getCategoryType());
        strBuff.append("&UPDATE_STATUS=" + VOMSI.VOUCHER_USED);
        strBuff.append("&PREVIOUS_STATUS=" + vomsVO.getCurrentStatus());
        strBuff.append("&CREDIT_BONUS_VAL=" + c2sTransferVO.getReceiverCreditBonusValidity());
        strBuff.append("&COMBINED_RECHARGE=" + c2sTransferVO.getBoth());
        strBuff.append("&IMPLICIT_RECHARGE=" + c2sTransferVO.getOnline());
        strBuff.append("&IN_ACCOUNT_ID=" + receiverTransferItemVO.getInAccountId());
        strBuff.append("&CAL_OLD_EXPIRY_DATE=" + receiverTransferItemVO.getOldExporyInMillis());// @nu

        // For Get Number Back Service
        if (receiverTransferItemVO.isNumberBackAllowed()) {
            final String numbck_diff_to_in = c2sTransferVO.getServiceType() + PreferenceI.NUMBCK_DIFF_REQ_TO_IN;
            final Boolean NBR_BK_SEP_REQ = (Boolean) PreferenceCache.getControlPreference(numbck_diff_to_in, c2sTransferVO.getNetworkCode(), receiverTransferItemVO
                .getInterfaceID());
            strBuff.append("&NBR_BK_DIFF_REQ=" + NBR_BK_SEP_REQ);
        }
        // @nu
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_IN))).booleanValue()) {
            strBuff.append("&ENQ_POSTBAL_IN=" + PretupsI.YES);
        } else {
            strBuff.append("&ENQ_POSTBAL_IN=" + PretupsI.NO);
        }
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
            strBuff.append("&ENQ_POSTBAL_ALLOW=" + PretupsI.YES);
        } else {
            strBuff.append("&ENQ_POSTBAL_ALLOW=" + PretupsI.NO);
        }
        // end @nu
        strBuff.append("&SELECTOR_BUNDLE_ID=" + receiverBundleID);
        strBuff.append("&SELECTOR_BUNDLE_TYPE=" + c2sTransferVO.getSelectorBundleType());
        strBuff.append("&BONUS_BUNDLE_IDS=" + c2sTransferVO.getBonusBundleIdS());
        strBuff.append("&BONUS_BUNDLE_TYPES=" + c2sTransferVO.getBonusBundleTypes());
        strBuff.append("&BONUS_BUNDLE_VALUES=" + c2sTransferVO.getBonusBundleValues());
        strBuff.append("&BONUS_BUNDLE_VALIDITIES=" + c2sTransferVO.getBonusBundleValidities());
        strBuff.append("&IN_RESP_BUNDLE_CODES=" + receiverTransferItemVO.getBundleTypes());
        strBuff.append("&BONUS_BUNDLE_NAMES=" + c2sTransferVO.getBonusBundleNames());
        strBuff.append("&BONUS_BUNDLE_RATES=" + c2sTransferVO.getBonusBundleRate());
        strBuff.append("&BONUS_BUNDLE_CODES=" + c2sTransferVO.getBonusBundleCode());

        strBuff.append("&IN_RESP_BUNDLE_PREV_BALS=" + receiverTransferItemVO.getPrevBundleBals());
        strBuff.append("&IN_RESP_BUNDLE_PREV_VALIDITY=" + receiverTransferItemVO.getPrevBundleExpiries());
        // For COS change and Promotion Amount by gaurav
        strBuff.append("&PROMOTION_AMOUNT=" + c2sTransferVO.getInPromo());
        strBuff.append("&COS_FLAG=" + c2sTransferVO.getCosRequired());
        strBuff.append("&NEW_COS_SERVICE_CLASS=" + c2sTransferVO.getNewCos());
        try {
            previous_balance = (Long.parseLong(c2sTransferVO.getPreviousPromoBalance()));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            _previousPromoExpiry = (BTSLUtil.getDateFromDateString(c2sTransferVO.getPreviousPromoExpiry(), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        strBuff.append("&INTERFACE_PROMO_PREV_BALANCE=" + previous_balance);
        strBuff.append("&CAL_OLD_EXPIRY_DATE=" + c2sTransferVO.getPreviousExpiryInCal());
        strBuff.append("&PROMO_CAL_OLD_EXPIRY_DATE=" + c2sTransferVO.getPreviousPromoExpiryInCal());
        strBuff.append("&PROMO_OLD_EXPIRY_DATE=" + _previousPromoExpiry);
        strBuff.append("&RC_COMMENT=" + c2sTransferVO.getRechargeComment());
        return strBuff.toString();

        /*
         * final String methodName = "getReceiverCreditStr";
         * StringBuffer strBuff=null;
         * strBuff=new StringBuffer(getReceiverCommonString());
         * strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_CREDIT_ACTION);
         * strBuff.append("&INTERFACE_AMOUNT="+receiverTransferItemVO.
         * getTransferValue());
         * strBuff.append("&SERVICE_CLASS="+receiverTransferItemVO.
         * getServiceClassCode());
         * strBuff.append("&ACCOUNT_ID="+receiverTransferItemVO.getReferenceID())
         * ;
         * strBuff.append("&ACCOUNT_STATUS="+receiverTransferItemVO.getAccountStatus
         * ());
         * strBuff.append("&GRACE_DAYS="+receiverTransferItemVO.getGraceDaysStr()
         * );
         * strBuff.append("&CARD_GROUP="+c2sTransferVO.getCardGroupCode());
         * strBuff.append("&MIN_CARD_GROUP_AMT="+c2sTransferVO.getMinCardGroupAmount
         * ());
         * strBuff.append("&VALIDITY_DAYS="+receiverTransferItemVO.getValidity())
         * ;
         * strBuff.append("&BONUS_VALIDITY_DAYS="+c2sTransferVO.
         * getReceiverBonusValidity());
         * strBuff.append("&BONUS_AMOUNT="+c2sTransferVO.getReceiverBonusValue())
         * ;
         * strBuff.append("&SOURCE_TYPE="+c2sTransferVO.getSourceType());
         * strBuff.append("&SERIAL_NUMBER="+vomsVO.getSerialNo());
         * strBuff.append("&UPDATE_STATUS="+VOMSI.VOUCHER_USED);
         * strBuff.append("&PREVIOUS_STATUS="+vomsVO.getCurrentStatus());
         * strBuff.append("&PRODUCT_CODE="+c2sTransferVO.getProductCode());
         * strBuff.append("&TAX_AMOUNT="+(c2sTransferVO.getReceiverTax1Value()+
         * c2sTransferVO.getReceiverTax2Value()));
         * strBuff.append("&ACCESS_FEE="+c2sTransferVO.getReceiverAccessFee());
         * strBuff.append("&SENDER_MSISDN="+senderMSISDN);
         * strBuff.append("&RECEIVER_MSISDN="+receiverMSISDN);
         * strBuff.append("&EXTERNAL_ID="+_receiverExternalID);
         * strBuff.append("&GATEWAY_CODE="+requestVO.getRequestGatewayCode());
         * strBuff.append("&GATEWAY_TYPE="+requestVO.getRequestGatewayType());
         * strBuff.append("&IMSI="+BTSLUtil.NullToString(_receiverIMSI));
         * strBuff.append("&SENDER_ID="+((SenderVO)requestVO.getSenderVO()).
         * getUserID());
         * strBuff.append("&SERVICE_TYPE="+senderSubscriberType+"-"+type);
         * if(String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE).equals(requestVO.
         * getReqSelector()))
         * {
         * strBuff.append("&ADJUST=Y");
         * strBuff.append("&CAL_OLD_EXPIRY_DATE="+receiverTransferItemVO.
         * getOldExporyInMillis());///@nu
         * }
         * try{strBuff.append("&OLD_EXPIRY_DATE="+BTSLUtil.getDateStringFromDate(
         * receiverTransferItemVO.getPreviousExpiry()));}catch(Exception
         * e){LOG.errorTrace(methodName, e);}
         * try{strBuff.append("&OLD_GRACE_DATE="+BTSLUtil.getDateStringFromDate(
         * receiverTransferItemVO.getPreviousGraceDate()));}catch(Exception
         * e){LOG.errorTrace(methodName, e);}
         * strBuff.append("&INTERFACE_PREV_BALANCE="+receiverTransferItemVO.
         * getPreviousBalance());
         * // Avinash send the requested amount to IN. to use card group only
         * for reporting purpose.
         * strBuff.append("&REQUESTED_AMOUNT="+c2sTransferVO.getRequestedAmount()
         * );
         * //For Get NUMBER BACK Service
         * if(receiverTransferItemVO.isNumberBackAllowed())
         * {
         * String numbck_diff_to_in=c2sTransferVO.getServiceType()+PreferenceI.
         * NUMBCK_DIFF_REQ_TO_IN;
         * Boolean NBR_BK_SEP_REQ=(Boolean)PreferenceCache.getControlPreference(
         * numbck_diff_to_in
         * ,c2sTransferVO.getNetworkCode(),receiverTransferItemVO
         * .getInterfaceID());
         * strBuff.append("&NBR_BK_DIFF_REQ="+NBR_BK_SEP_REQ);
         * }
         * //Added by Zafar Abbas on 13/02/2008 after adding two new fields for
         * Bonus SMS/MMS in Card group
         * strBuff.append("&BONUS1="+c2sTransferVO.getReceiverBonus1());
         * strBuff.append("&BONUS2="+c2sTransferVO.getReceiverBonus2());
         * strBuff.append("&BUNDLE_TYPES="+receiverTransferItemVO.getBundleTypes(
         * ));
         * strBuff.append("&BONUS_BUNDLE_VALIDITIES="+receiverTransferItemVO.
         * getBonusBundleValidities());
         * 
         * //added by vikask for card group updation field
         * 
         * strBuff.append("&BONUS1_VAL="+c2sTransferVO.getReceiverBonus1Validity(
         * ));
         * strBuff.append("&BONUS2_VAL="+c2sTransferVO.getReceiverBonus2Validity(
         * ));
         * strBuff.append("&CREDIT_BONUS_VAL="+c2sTransferVO.
         * getReceiverCreditBonusValidity());
         * 
         * //added by amit for card group offline field
         * strBuff.append("&COMBINED_RECHARGE="+c2sTransferVO.getBoth());
         * strBuff.append("&EXPLICIT_RECHARGE="+c2sTransferVO.getOnline());
         * if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_IN))).booleanValue()) {
         * strBuff.append("&ENQ_POSTBAL_IN="+PretupsI.YES);
         * } else {
         * strBuff.append("&ENQ_POSTBAL_IN="+PretupsI.NO);
         * }
         * if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
         * strBuff.append("&ENQ_POSTBAL_ALLOW="+PretupsI.YES);
         * } else {
         * strBuff.append("&ENQ_POSTBAL_ALLOW="+PretupsI.NO);
         * }
         * 
         * return strBuff.toString();
         */

        /*
         * final String methodName = "getReceiverCreditStr";
         * StringBuffer strBuff=null;
         * strBuff=new StringBuffer(getReceiverCommonString());
         * strBuff.append("&INTERFACE_ACTION="+PretupsI.INTERFACE_CREDIT_ACTION);
         * strBuff.append("&INTERFACE_AMOUNT="+c2sTransferVO.
         * getReceiverTransferValue());
         * strBuff.append("&GRACE_DAYS="+receiverTransferItemVO.getGraceDaysStr()
         * );
         * strBuff.append("&SENDER_MSISDN="+senderMSISDN);
         * strBuff.append("&SENDER_ID="+channelUserVO.getUserID());
         * strBuff.append("&SENDER_EXTERNAL_CODE="+channelUserVO.getExternalCode(
         * ));
         * strBuff.append("&PRODUCT_CODE="+c2sTransferVO.getProductCode());
         * strBuff.append("&VALIDITY_DAYS="+c2sTransferVO.getReceiverValidity());
         * strBuff.append("&BONUS_VALIDITY_DAYS="+c2sTransferVO.
         * getReceiverBonusValidity());
         * strBuff.append("&BONUS_AMOUNT="+c2sTransferVO.getReceiverBonusValue())
         * ;
         * try{strBuff.append("&OLD_EXPIRY_DATE="+BTSLUtil.getDateStringFromDate(
         * receiverTransferItemVO.getPreviousExpiry()));}catch(Exception
         * e){LOG.errorTrace(methodName, e);}
         * strBuff.append("&INTERFACE_PREV_BALANCE="+receiverTransferItemVO.
         * getPreviousBalance());
         * strBuff.append("&SERIAL_NUMBER="+vomsVO.getSerialNo());
         * strBuff.append("&PIN="+BTSLUtil.decryptText(vomsVO.getPinNo()));
         * strBuff.append("&INTERFACE_PREV_BALANCE="+receiverTransferItemVO.
         * getPreviousBalance());
         * // Avinash send the requested amount to IN. to use card group only
         * for reporting purpose.
         * strBuff.append("&REQUESTED_AMOUNT="+c2sTransferVO.getRequestedAmount()
         * );
         * strBuff.append("&SERIAL_NUMBER="+vomsVO.getSerialNo());
         * strBuff.append("&UPDATE_STATUS="+VOMSI.VOUCHER_USED);
         * strBuff.append("&PREVIOUS_STATUS="+vomsVO.getCurrentStatus());
         * return strBuff.toString();
         */}

    /**
     * Method to process the response of the receiver validation from IN
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForReceiverValidateResponse(String str) throws BTSLBaseException {
        final String methodName = "updateForReceiverValidateResponse";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
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
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        // If we get the MSISDN not found on interface error then perform
        // interface routing
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            // Getting routing controll details
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(receiverTransferItemVO.getInterfaceID());
            if (altList != null && altList.size() > 0) {
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
            if ("Y".equals(requestVO.getUseInterfaceLanguage())) {
                // update the receiver locale if language code returned from IN
                // is not null
                updateReceiverLocale((String) map.get("IN_LANG"));
            }
            receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
            receiverTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
            receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
            receiverTransferItemVO.setValidationStatus(status);
            receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());
            receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
            receiverTransferItemVO.setSubscriberType(receiverSubscriberType);
            // If status is other than Success in validation stage mark sender
            // request as Not applicable and
            // Make transaction Fail
            String[] strArr = null;

            if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                c2sTransferVO.setErrorCode(status + "_R");
                receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                receiverTransferItemVO.setTransferStatus(status);
                senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                strArr = new String[] { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()) };
                throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, c2sTransferVO.getErrorCode(), 0, strArr, null);
            }
            receiverTransferItemVO.setTransferStatus(status);
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
                .getRoutingControlDetails(c2sTransferVO.getReceiverNetworkCode() + "_" + c2sTransferVO.getServiceType() + "_" + type);
            if (!receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), externalID, receiverMSISDN, type, channelUserVO.getUserID(), currentDate);
                receiverInterfaceInfoInDBFound = true;
            }
            try {
                receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
            // Done so that receiver check can be brough to common
            receiverVO.setServiceClassCode(receiverTransferItemVO.getServiceClass());
            receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());

            try {
                receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
            receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));

            // TO DO Done for testing purpose should we use it or give exception
            // in this case
            if (receiverTransferItemVO.getPreviousExpiry() == null) {
                receiverTransferItemVO.setPreviousExpiry(currentDate);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }
    }

    /**
     * Method to perform the Interface routing for the subscriber MSISDN
     * 
     * @throws BTSLBaseException
     */
    private void performAlternateRouting(ArrayList altList) throws BTSLBaseException {
        final String methodName = "performAlternateRouting";
        try {
            if (altList != null && altList.size() > 0) {
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
                    LoadController.decreaseResponseCounters(transferID, receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(transferID, c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);
                    receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
                    receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
                    if (PretupsI.YES.equals(listValueVO.getType())) {
                        c2sTransferVO.setUnderProcessMsgReq(true);
                    }
                    receiverAllServiceClassID = listValueVO.getTypeName();
                    externalID = listValueVO.getIDValue();
                    interfaceStatusType = listValueVO.getStatusType();
                    c2sTransferVO.setReceiverInterfaceStatusType(interfaceStatusType);
                    c2sTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);

                    if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
                        // if default language is english then pick language 1
                        // message else language 2
                        // Changed on 15/05/06 for CR00020 (Gurjeet Singh Bedi)
                        // if((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)).equals("en"))
                        if (PretupsI.LOCALE_LANGAUGE_EN.equals(senderLocale.getLanguage())) {
                            c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                        } else {
                            c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                        }
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
                    }

                    checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, receiverTransferItemVO.getInterfaceID());

                    // validate receiver limits before Interface Validations
                    if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                        PretupsBL.validateRecieverLimits(null, c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
                    }

                    requestStr = getReceiverValidateStr();
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

                    receiverValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeR, intModIPR, intModPortR, intModClassNameR);

                    TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        receiverValidateResponse(receiverValResponse, 1, altList.size());
                        if (InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus())) {
                            // Update in DB for routing interface
                            if (receiverInterfaceInfoInDBFound) {
                                PretupsBL.updateSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), externalID, receiverMSISDN, type, channelUserVO
                                    .getUserID(), currentDate);
                            } else {
                                final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(c2sTransferVO
                                    .getReceiverNetworkCode() + "_" + c2sTransferVO.getServiceType() + "_" + type);
                                if (!receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                    PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), externalID, receiverMSISDN, type, channelUserVO
                                        .getUserID(), currentDate);
                                    receiverInterfaceInfoInDBFound = true;
                                }
                            }
                        }
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        throw e;
                    }

                    break;
                }
                case 2: {
                    LoadController.decreaseResponseCounters(transferID, receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(transferID, c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);
                    receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
                    receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
                    if (PretupsI.YES.equals(listValueVO.getType())) {
                        c2sTransferVO.setUnderProcessMsgReq(true);
                    }
                    receiverAllServiceClassID = listValueVO.getTypeName();
                    externalID = listValueVO.getIDValue();
                    interfaceStatusType = listValueVO.getStatusType();
                    c2sTransferVO.setReceiverInterfaceStatusType(interfaceStatusType);
                    c2sTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);

                    if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
                        // if default language is english then pick language 1
                        // message else language 2
                        // Changed on 15/05/06 for CR00020 (Gurjeet Singh Bedi)
                        // if((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)).equals("en"))
                        if (PretupsI.LOCALE_LANGAUGE_EN.equals(senderLocale.getLanguage())) {
                            c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                        } else {
                            c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                        }
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
                    }

                    checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, receiverTransferItemVO.getInterfaceID());

                    // validate receiver limits before Interface Validations
                    if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                        PretupsBL.validateRecieverLimits(null, c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
                    }

                    requestStr = getReceiverValidateStr();
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

                    receiverValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeR, intModIPR, intModPortR, intModClassNameR);

                    TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        receiverValidateResponse(receiverValResponse, 1, altList.size());
                        if (InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus())) {
                            // Update in DB for routing interface
                            if (receiverInterfaceInfoInDBFound) {
                                PretupsBL.updateSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), externalID, receiverMSISDN, type, channelUserVO
                                    .getUserID(), currentDate);
                            } else {
                                final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(c2sTransferVO
                                    .getReceiverNetworkCode() + "_" + c2sTransferVO.getServiceType() + "_" + type);
                                if (!receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                    PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), externalID, receiverMSISDN, type, channelUserVO
                                        .getUserID(), currentDate);
                                    receiverInterfaceInfoInDBFound = true;
                                }
                            }
                        }
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                            LoadController.decreaseResponseCounters(transferID, receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(transferID, c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(1);
                            receiverTransferItemVO.setInterfaceID(listValueVO.getValue());
                            receiverTransferItemVO.setInterfaceHandlerClass(listValueVO.getLabel());
                            if (PretupsI.YES.equals(listValueVO.getType())) {
                                c2sTransferVO.setUnderProcessMsgReq(true);
                            }
                            receiverAllServiceClassID = listValueVO.getTypeName();
                            externalID = listValueVO.getIDValue();
                            interfaceStatusType = listValueVO.getStatusType();
                            c2sTransferVO.setReceiverInterfaceStatusType(interfaceStatusType);
                            c2sTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);

                            if (!PretupsI.YES.equals(listValueVO.getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(listValueVO.getStatusType())) {
                                // if default language is english then pick
                                // language 1 message else language 2
                                // Changed on 15/05/06 for CR00020 (Gurjeet
                                // Singh Bedi)
                                // if((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)).equals("en"))
                                if (PretupsI.LOCALE_LANGAUGE_EN.equals(senderLocale.getLanguage())) {
                                    c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                } else {
                                    c2sTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
                            }

                            checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, receiverTransferItemVO.getInterfaceID());

                            // validate receiver limits before Interface
                            // Validations
                            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                                PretupsBL.validateRecieverLimits(null, c2sTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.C2S_MODULE);
                            }

                            requestStr = getReceiverValidateStr();
                            // commonClient=new CommonClient();

                            LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.SENDER_UNDER_VAL);

                            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 2");

                            receiverValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeR, intModIPR, intModPortR, intModClassNameR);

                            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                receiverValidateResponse(receiverValResponse, 2, altList.size());
                                if (InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus())) {
                                    // Update in DB for routing interface
                                    if (receiverInterfaceInfoInDBFound) {
                                        PretupsBL.updateSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), externalID, receiverMSISDN, type, channelUserVO
                                            .getUserID(), currentDate);
                                    } else {
                                        final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(c2sTransferVO
                                            .getReceiverNetworkCode() + "_" + c2sTransferVO.getServiceType() + "_" + type);
                                        if (!receiverInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                            PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), externalID, receiverMSISDN, type,
                                                channelUserVO.getUserID(), currentDate);
                                            receiverInterfaceInfoInDBFound = true;
                                        }
                                    }
                                }
                            } catch (BTSLBaseException bex) {
                                throw bex;
                            } catch (Exception e) {
                                throw e;
                            }
                        } else {
                            throw be;
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                    break;
                }
                }

            } else {
                return;
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsChnlRcvrController[performAlternateRouting]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Checking load for transfer ID=" + transferID);
        }
        int recieverLoadStatus = 0;
        int senderLoadStatus = 0;
        try {
            c2sTransferVO.setRequestVO(requestVO);
            c2sTransferVO.setSenderTransferItemVO(senderTransferItemVO);
            c2sTransferVO.setReceiverTransferItemVO(receiverTransferItemVO);
            senderLoadStatus = LoadController.checkInterfaceLoad(c2sTransferVO.getReceiverNetworkCode(), senderTransferItemVO.getInterfaceID(), transferID, c2sTransferVO,
                true);
            if (senderLoadStatus == 0) {
                if (serviceType.equals(PretupsI.SERVICE_TYPE_EVR)) {
                    recieverLoadStatus = LoadController.checkInterfaceLoad(c2sTransferVO.getReceiverNetworkCode(), receiverTransferItemVO.getInterfaceID(), transferID,
                        c2sTransferVO, true);
                    if (recieverLoadStatus == 0) {
                        try {
                            LoadController.checkTransactionLoad(c2sTransferVO.getReceiverNetworkCode(), senderTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE,
                                transferID, true, LoadControllerI.USERTYPE_SENDER);
                        } catch (BTSLBaseException e) {
                            // Decreasing interface load of receiver which we
                            // had incremented before 27/09/06, sender was
                            // decreased in the method
                            LoadController.decreaseCurrentInterfaceLoad(transferID, c2sTransferVO.getReceiverNetworkCode(), receiverTransferItemVO.getInterfaceID(),
                                LoadControllerI.DEC_LAST_TRANS_COUNT);
                            throw e;
                        }

                        try {
                            LoadController.checkTransactionLoad(c2sTransferVO.getReceiverNetworkCode(), receiverTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE,
                                transferID, true, LoadControllerI.USERTYPE_RECEIVER);
                        } catch (BTSLBaseException e) {
                            // Decreasing interface load of sender which we had
                            // incremented before 27/09/06, receiver was
                            // decreased in the method
                            LoadController.decreaseTransactionInterfaceLoad(transferID, c2sTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                            throw e;
                        }

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("VchrConsChnlRcvrController[checkTransactionLoad]", "transferID=" + transferID + " Successfully through load");
                        }
                    }
                    // Request in Queue
                    else if (recieverLoadStatus == 1) {
                        // Decrease the interface counter of the sender that was
                        // increased
                        LoadController.decreaseCurrentInterfaceLoad(transferID, c2sTransferVO.getReceiverNetworkCode(), senderTransferItemVO.getInterfaceID(),
                            LoadControllerI.DEC_LAST_TRANS_COUNT);

                        final String strArr[] = { receiverMSISDN, String.valueOf(PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount())) };
                        throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
                    }
                    // Refuse the request
                    else {
                        throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                    }
                } else {
                    LoadController.checkTransactionLoad(c2sTransferVO.getReceiverNetworkCode(), senderTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE, transferID, true,
                        LoadControllerI.USERTYPE_SENDER);
                }
            } else if (senderLoadStatus == 1) {
                final String strArr[] = { receiverMSISDN, String.valueOf(PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount())) };
                throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            LOG.error("VchrConsChnlRcvrController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Checking load for transfer ID=" + transferID + " on interface=" + p_interfaceID);
        }
        int recieverLoadStatus = 0;

        try {
            // Do not enter the request in Queue
            recieverLoadStatus = LoadController.checkInterfaceLoad(c2sTransferVO.getReceiverNetworkCode(), receiverTransferItemVO.getInterfaceID(), transferID, c2sTransferVO,
                false);
            if (recieverLoadStatus == 0) {
                LoadController.checkTransactionLoad(c2sTransferVO.getReceiverNetworkCode(), receiverTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE, transferID, true,
                    LoadControllerI.USERTYPE_RECEIVER);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("VchrConsChnlRcvrController[checkTransactionLoad]", "transferID=" + transferID + " Successfully through load");
                }
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            LOG.error("VchrConsChnlRcvrController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) {
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        }
        // receiver language has to be taken from IN then the block below will
        // execute
        if ("Y".equals(requestVO.getUseInterfaceLanguage())) {
            // update the receiver locale if language code returned from IN is
            // not null
            updateReceiverLocale((String) map.get("IN_LANG"));
        }
        receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        receiverTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
        receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        receiverTransferItemVO.setValidationStatus(status);
        receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());
        receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
        receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));
        receiverTransferItemVO.setSubscriberType(receiverSubscriberType);

        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        String[] strArr = null;

        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            c2sTransferVO.setErrorCode(status + "_R");
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            receiverTransferItemVO.setTransferStatus(status);
            senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException("VchrConsChnlRcvrController", "updateForReceiverValidateResponse", PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD, 0, strArr, null);
        }

        receiverTransferItemVO.setTransferStatus(status);
        receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

        try {
            receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        // Done so that receiver check can be brough to common
        receiverVO.setServiceClassCode(receiverTransferItemVO.getServiceClass());
        receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());

        try {
            receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
        receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));

        // TO DO Done for testing purpose should we use it or give exception in
        // this case
        if (receiverTransferItemVO.getPreviousExpiry() == null) {
            receiverTransferItemVO.setPreviousExpiry(currentDate);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_languageCode=" + p_languageCode);
        }
        // check if language is returned fron IN or not.
        // If not then send alarm and not set the locale
        // otherwise set the local corresponding to the code returned from the
        // IN.
        if (!BTSLUtil.isNullString(p_languageCode)) {
            try {
                if (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode) == null) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                        "VchrConsChnlRcvrController[updateReceiverLocale]", transferID, receiverMSISDN, "",
                        "Exception: Notification language returned from IN is not defined in system p_languageCode: " + p_languageCode);
                } else {
                    receiverLocale = (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode));
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited receiverLocale=" + receiverLocale);
        }
    }

    /**
     * Method to get the under process message to be sent to receiver
     * 
     * @return
     */
    private String getReceiverUnderProcessMessage() {
        final String[] messageArgArray = { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverTransferValue()), String.valueOf(receiverTransferItemVO
            .getValidity()), PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), senderMSISDN, PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverAccessFee()), channelUserVO
            .getUserName(), payableAmt };
        return BTSLUtil.getMessage(receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_UNDERPROCESS_VMS, messageArgArray, requestVO.getRequestGatewayType());
    }

    /**
     * Method to get the success message to be sent to sender
     * 
     * @return
     */
    private String getSenderUnderProcessMessage() {
        final String[] messageArgArray = { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), String.valueOf(receiverTransferItemVO
            .getValidity()), PretupsBL.getDisplayAmount(senderTransferItemVO.getPostBalance()) };
        return BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS, messageArgArray);
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
        if (!"N".equals(receiverPostBalanceAvailable)) {
            String dateStrGrace = null;
            String dateStrValidity = null;
            // Changed by ankit Zindal on date 2/08/06 for problem when validity
            // and grace date is null
            try {
                dateStrGrace = (receiverTransferItemVO.getNewGraceDate() == null) ? "0" : BTSLUtil.getDateStringFromDate(receiverTransferItemVO.getNewGraceDate());
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                dateStrGrace = String.valueOf(receiverTransferItemVO.getNewGraceDate());
            }
            try {
                dateStrValidity = (receiverTransferItemVO.getNewExpiry() == null) ? "0" : BTSLUtil.getDateStringFromDate(receiverTransferItemVO.getNewExpiry());
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                dateStrValidity = String.valueOf(receiverTransferItemVO.getNewExpiry());
            }
            if (!BTSLUtil.isNullString(payableAmt)) {
                messageArgArray = new String[] { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverTransferValue()), String.valueOf(receiverTransferItemVO
                    .getValidity()), PretupsBL.getDisplayAmount(receiverTransferItemVO.getPostBalance()), senderMSISDN, dateStrGrace, dateStrValidity, payableAmt, PretupsBL
                    .getDisplayAmount(c2sTransferVO.getReceiverAccessFee()), c2sTransferVO.getSubService(), channelUserVO.getUserName(), PretupsBL
                    .getDisplayAmount(c2sTransferVO.getRequestedAmount()) };
            } else {
                messageArgArray = new String[] { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverTransferValue()), String.valueOf(receiverTransferItemVO
                    .getValidity()), PretupsBL.getDisplayAmount(receiverTransferItemVO.getPostBalance()), senderMSISDN, dateStrGrace, dateStrValidity, PretupsBL
                    .getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverAccessFee()), c2sTransferVO.getSubService(), channelUserVO
                    .getUserName(), payableAmt };
            }
            key = PretupsErrorCodesI.EVD_RECEIVER_SUCCESS;// return
            // BTSLUtil.getMessage(receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_SUCCESS,messageArgArray);
        } else {
            try {
                if (!BTSLUtil.isNullString(payableAmt)) {
                    messageArgArray = new String[] { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverTransferValue()), senderMSISDN, payableAmt, PretupsBL
                        .getDisplayAmount(c2sTransferVO.getReceiverAccessFee()), c2sTransferVO.getSubService(), channelUserVO.getUserName(), PretupsBL
                        .getDisplayAmount(c2sTransferVO.getRequestedAmount()) };
                } else {
                    messageArgArray = new String[] { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverTransferValue()), senderMSISDN, PretupsBL
                        .getDisplayAmount(c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverAccessFee()), c2sTransferVO.getSubService(), channelUserVO
                        .getUserName(), payableAmt };
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            key = PretupsErrorCodesI.EVD_RECEIVER_SUCCESS_WITHOUT_POSTBAL;// return
            // BTSLUtil.getMessage(receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
        }
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_EVD))).booleanValue()) {
            String message = null;
            try {
                message = BTSLUtil.getMessage(receiverLocale, key + "_" + receiverTransferItemVO.getServiceClass(), messageArgArray, requestVO.getRequestGatewayType());
                if (!BTSLUtil.isNullString(message)) {
                    return message;
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
        return BTSLUtil.getMessage(receiverLocale, key, messageArgArray, requestVO.getRequestGatewayType());
    }

    private String getSenderSuccessMessage() {
        final String[] messageArgArray = { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()), c2sTransferVO.getVoucherCode() };
        String key = null;
        if (PretupsI.SERVICE_TYPE_EVD.equals(c2sTransferVO.getServiceType())) {
            key = PretupsErrorCodesI.EVD_SENDER_SUCCESS;
        } else {
            key = PretupsErrorCodesI.EVR_SENDER_SUCCESS;
        }
        return BTSLUtil.getMessage(senderLocale, key, messageArgArray);
    }

    private String getReceiverAmbigousMessage() {
        final String[] messageArgArray = { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), senderMSISDN, channelUserVO.getUserName(), payableAmt };
        return BTSLUtil.getMessage(receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_AMBIGOUS_KEY_EVD, messageArgArray, requestVO.getRequestGatewayType());
    }

    private String getReceiverFailMessage() {
        final String[] messageArgArray = { transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), senderMSISDN, channelUserVO.getUserName(), payableAmt };
        return BTSLUtil.getMessage(receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY_EVD, messageArgArray, requestVO.getRequestGatewayType());
    }

    /**
     * Method to get the under process message before validation to be sent to
     * sender
     * 
     * @return
     */
    private String getSndrUPMsgBeforeValidation() {
        final String[] messageArgArray = { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.C2S_SENDER_UNDERPROCESS_B4VAL_EVD, messageArgArray);
    }

    /**
     * Method to populate the service interface details based on the action and
     * service type
     * 
     * @param action
     * @throws BTSLBaseException
     */
    public void populateServiceInterfaceDetails(Connection p_con, String action) throws BTSLBaseException {
        final String receiverNetworkCode = receiverVO.getNetworkCode();
        final long receiverPrefixID = receiverVO.getPrefixID();
        boolean isReceiverFound = false;
        boolean isVOMSFound = false;
        final String interfaceCategory = null;

        if ((!vomsInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
            .equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
            isVOMSFound = getInterfaceRoutingDetails(p_con, receiverMSISDN, receiverPrefixID, receiverVO.getSubscriberType(), receiverNetworkCode, c2sTransferVO
                .getServiceType(), type, PretupsI.USER_TYPE_RECEIVER, action);
        } else {
            isVOMSFound = true;
        }
        if (!isVOMSFound) {
            throw new BTSLBaseException("VchrConsChnlRcvrController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.VOMS_INTERFACE_NOT_FOUND);
        }

        if (c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
            onlyForEvr = true;
            if ((!receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
                .equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
                isReceiverFound = getInterfaceRoutingDetails(p_con, receiverMSISDN, receiverPrefixID, receiverVO.getSubscriberType(), receiverNetworkCode, c2sTransferVO
                    .getServiceType(), type, PretupsI.USER_TYPE_RECEIVER, action);
            } else {
                isReceiverFound = true;
            }

            if (!isReceiverFound) {
                throw new BTSLBaseException("VchrConsChnlRcvrController", "populateServiceInterfaceDetails", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING_EVD);
            }
        }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                requestIDStr,
                " Entered p_prefixID=" + p_prefixID + " p_action=" + p_action + " p_interfaceCategory=" + p_interfaceCategory + " p_listValueVO=" + p_listValueVO + " p_useInterfacePrefixVO=" + p_useInterfacePrefixVO + " p_MSISDNPrefixInterfaceMappingVO=" + p_MSISDNPrefixInterfaceMappingVO);
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

            if (p_interfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_VOMS) || serviceType.equals(PretupsI.SERVICE_TYPE_VCNO2C)) {
                receiverTransferItemVO.setInterfaceID(interfaceID);
                receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                receiverTransferItemVO.setInterfaceType(p_interfaceCategory);
                senderTransferItemVO.setPrefixID(p_prefixID);
                senderTransferItemVO.setInterfaceID(interfaceID);
                senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                senderTransferItemVO.setInterfaceType(p_interfaceCategory);
                if (!p_useInterfacePrefixVO && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                    vomsInterfaceInfoInDBFound = true;
                }
                vomsExternalID = externalID;
                vomsAllServiceClassID = allServiceClassID;
            } else {
                receiverTransferItemVO.setPrefixID(p_prefixID);
                receiverTransferItemVO.setInterfaceID(interfaceID);
                receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                receiverTransferItemVO.setInterfaceType(p_interfaceCategory);
                if (!p_useInterfacePrefixVO && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                    receiverInterfaceInfoInDBFound = true;
                }
                externalID = externalID;
                interfaceStatusType = interfaceStatusTy;
                if (PretupsI.YES.equals(underProcessMsgReqd)) {
                    c2sTransferVO.setUnderProcessMsgReq(true);
                }
                receiverAllServiceClassID = allServiceClassID;
                c2sTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);
                c2sTransferVO.setReceiverInterfaceStatusType(interfaceStatusType);
            }
            // Check if interface status is Active or not.
            if (!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceStatusTy)) {
                if (PretupsI.LOCALE_LANGAUGE_EN.equals(senderLocale.getLanguage())) {
                    c2sTransferVO.setSenderReturnMessage(message1);
                } else {
                    c2sTransferVO.setSenderReturnMessage(message2);
                }
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE_VMS);
            }
        } catch (BTSLBaseException be) {
            LOG.error(methodName, "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrepaidController[setInterfaceDetails]", transferID,
                senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, requestIDStr,
                    " Exiting with Sender Interface ID=" + senderTransferItemVO.getInterfaceID() + " Receiver Interface=" + receiverTransferItemVO.getInterfaceID());
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            c2sTransferVO = (C2STransferVO) p_transferVO;
            requestVO = c2sTransferVO.getRequestVO();
            channelUserVO = (ChannelUserVO) requestVO.getSenderVO();
            type = requestVO.getType();
            requestID = requestVO.getRequestID();
            requestIDStr = requestVO.getRequestIDStr();
            receiverLocale = requestVO.getReceiverLocale();
            transferID = c2sTransferVO.getTransferID();
            receiverVO = (ReceiverVO) c2sTransferVO.getReceiverVO();
            senderMSISDN = (channelUserVO.getUserPhoneVO()).getMsisdn();
            receiverMSISDN = ((ReceiverVO) c2sTransferVO.getReceiverVO()).getMsisdn();
            senderLocale = requestVO.getSenderLocale();
            senderNetworkCode = channelUserVO.getNetworkID();
            serviceType = requestVO.getServiceType();
            senderTransferItemVO = c2sTransferVO.getSenderTransferItemVO();
            receiverTransferItemVO = c2sTransferVO.getReceiverTransferItemVO();
            transferEntryReqd = true;
            receiverSubscriberType = c2sTransferVO.getReceiverSubscriberType();

            LoadController.checkTransactionLoad(((ReceiverVO) c2sTransferVO.getReceiverVO()).getNetworkCode(), senderTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE,
                transferID, true, LoadControllerI.USERTYPE_SENDER);
            LoadController.checkTransactionLoad(((ReceiverVO) c2sTransferVO.getReceiverVO()).getNetworkCode(), receiverTransferItemVO.getInterfaceID(), PretupsI.C2S_MODULE,
                transferID, true, LoadControllerI.USERTYPE_RECEIVER);

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            // Loading C2S receiver's controll parameters
            // added by PN(25/03/08) to resolve the issude of duplicate request
            // processing
            c2sTransferVO.setUnderProcessCheckReqd(requestVO.getMessageGatewayVO().getRequestGatewayVO().getUnderProcessCheckReqd());
            PretupsBL.loadRecieverControlLimits(con, requestIDStr, c2sTransferVO);
            receiverVO.setUnmarkRequestStatus(true);
            try {
                mcomCon.finalCommit();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }
			if (mcomCon != null) {
				mcomCon.close("VchrConsChnlRcvrController#processFromQueue");
				mcomCon = null;
			}
			con = null;

            if (LOG.isDebugEnabled()) {
                LOG.debug("C2SPrepaidController[processFromQueue]", "transferID=" + transferID + " Successfully through load");
            }
            processedFromQueue = true;

            processValidationRequest();
            // Set under process message for the sender and reciever
            p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
            final String[] messageArgArray = { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
            p_transferVO.setMessageArguments(messageArgArray);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
			if (mcomCon != null) {
				mcomCon.close("VchrConsChnlRcvrController#processFromQueue");
				mcomCon = null;
			}
            con = null;
            try {
                if (receiverVO != null && receiverVO.isUnmarkRequestStatus()) { // getting
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
                    PretupsBL.unmarkReceiverLastRequest(con, requestIDStr, receiverVO);
                }
            } catch (BTSLBaseException bex) {
                LOG.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsChnlRcvrController[processFromQueue]",
                    transferID, senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsChnlRcvrController[processFromQueue]",
                    transferID, senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + e.getMessage());
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            // setting transaction status to Fail
            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

            if (be.isKey()) // checking if baseexception has key
            {
                if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                    c2sTransferVO.setErrorCode(be.getMessageKey());
                }

                c2sTransferVO.setMessageCode(be.getMessageKey());
                c2sTransferVO.setMessageArguments(be.getArgs());
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            }

            // checking whether need to decrease the transaction load, if it is
            // already increased
            LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            isCounterDecreased = true;
            // making entry in the transaction log
            TransactionLog.log(transferID, requestVO.getRequestIDStr(), requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + requestVO.getMessageCode());

        } catch (Exception e) {
			if (mcomCon != null) {
				mcomCon.close("VchrConsChnlRcvrController#processFromQueue");
				mcomCon = null;
			}
            con = null;
            LOG.errorTrace(methodName, e);
            try {
                if (receiverVO != null && receiverVO.isUnmarkRequestStatus()) {
                    mcomCon = new MComConnection();con=mcomCon.getConnection();
                    // Setting users transaction status to completed at the
                    // start it was marked underprocess
                    PretupsBL.unmarkReceiverLastRequest(con, requestIDStr, receiverVO);
                }
            } catch (BTSLBaseException bex) {
                LOG.errorTrace(methodName, bex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsChnlRcvrController[processFromQueue]",
                    transferID, senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsChnlRcvrController[processFromQueue]",
                    transferID, senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Exception:" + ex.getMessage());
            }
            // checking condition whether channel receiver required the general
            // failure message
            if (recValidationFailMessageRequired) {
                // if receivermessage is null or it is not key
                if (c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // setting receiver return message
                    if (transferID != null) {
                        c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD, new String[] { String.valueOf(transferID), PretupsBL
                            .getDisplayAmount(c2sTransferVO.getRequestedAmount()) }));
                    } else {
                        c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R_EVD, new String[] { PretupsBL.getDisplayAmount(c2sTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }

            c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
            c2sTransferVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);

            LOG.errorTrace(methodName, e);

            // decreasing the transaction load count
            LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            isCounterDecreased = true;

            // raising alarm
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsChnlRcvrController[processFromQueue]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            // logging in the transaction log
            TransactionLog.log(transferID, requestVO.getRequestIDStr(), requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                c2sTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + requestVO.getMessageCode());
        } finally {
            try {
                if (mcomCon == null) {
                    mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
                // makking entry in the transfer table if transfer entry has not
                // been made and message gateway flow is common, i.e. validation
                // is not in thread
                if (transferID != null && !transferDetailAdded) {
                    // added by nilesh: consolidated for logger
                    if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                        // addEntryInTransfers(con);
                    }
                }
            } catch (BTSLBaseException be) {
                // try{if(con!=null) con.rollback() ;}catch(Exception ex){}
                LOG.errorTrace(methodName, be);
            } catch (Exception e) {
                // try{if(con!=null) con.rollback() ;}catch(Exception ex){}
                LOG.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsChnlRcvrController[processFromQueue]",
                    transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            }

            if (BTSLUtil.isNullString(c2sTransferVO.getMessageCode())) {
                c2sTransferVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }

            if (con != null) {
                // committing transaction and closing connection
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
				if (mcomCon != null) {
					mcomCon.close("VchrConsChnlRcvrController#processFromQueue");
					mcomCon = null;
				}
				con = null;
			} // end if

            if (receiverMessageSendReq && !BTSLUtil.isStringIn(c2sTransferVO.getRequestGatewayCode(), notAllowedRecSendMessGatw) && !"ALL".equals(notAllowedRecSendMessGatw)) {
                // checking if receiver message is not null and receiver return
                // message is key
                if (c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                    // generating message and pushing it to receiver
                    final BTSLMessages btslRecMessages = (BTSLMessages) c2sTransferVO.getReceiverReturnMsg();
                    (new PushMessage(receiverMSISDN, BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), transferID,
                        c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                } else if (c2sTransferVO.getReceiverReturnMsg() != null) {
                    (new PushMessage(receiverMSISDN, (String) c2sTransferVO.getReceiverReturnMsg(), transferID, c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                }
            }
            // making entry in the transaction log
            TransactionLog.log(transferID, requestVO.getRequestIDStr(), requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + requestVO.getMessageCode());
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered for transferID=" + transferID + " p_action=" + p_action + " voucherMarked=" + voucherMarked);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            if (voucherMarked) {
                userBalancesVO = null;
                try {
                    if (!c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                        final EvdUtil evdUtil = new EvdUtil();
                        final InterfaceVO interfaceVO = new InterfaceVO();
                        interfaceVO.setInterfaceId(senderTransferItemVO.getInterfaceID());
                        interfaceVO.setHandlerClass(senderTransferItemVO.getInterfaceHandlerClass());
                        evdUtil.updateVoucherForFailedTransaction(c2sTransferVO, networkInterfaceModuleVO, interfaceVO);
                    }

                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    // Event Handle to show that voucher could not be updated
                    // and is still Under process
                    LOG.error(
                        methodName,
                        " For transfer ID=" + transferID + " Error while updating voucher status for =" + c2sTransferVO.getSerialNumber() + " So leaving the voucher marked as under process. Exception: " + e
                            .getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                        "VchrConsChnlRcvrController[voucherUpdateSenderCreditBack]", transferID, "", "", "Error while updating voucher status for =" + c2sTransferVO
                            .getSerialNumber() + " So leaving the voucher marked as under process. Exception: " + e.getMessage());
                }
                mcomCon = new MComConnection();con=mcomCon.getConnection();
                if (transferDetailAdded) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "transferID=" + transferID + " Doing Sender Credit back ");
                    }
                    updateSenderForFailedTransaction(con, c2sTransferVO);
                    final C2STransferItemVO senderCreditBackItemVO = (C2STransferItemVO) c2sTransferVO.getTransferItemList().get(2);
                    senderCreditBackItemVO.setUpdateStatus(senderTransferItemVO.getUpdateStatus1());
                }

                // added by nilesh: consolidated for logger
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    // addEntryInTransfers(con);
                }
                // Log the details if the transfer Details were added i.e. if
                // User was creditted
                if (creditBackEntryDone) {
                    BalanceLogger.log(userBalancesVO);
                }

                if (PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION.equals(p_action)) {
                    finalTransferStatusUpdate = false;
                }
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            if (PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION.equals(p_action)) {
                finalTransferStatusUpdate = false;
            }
            LOG.error(methodName, " For transfer ID=" + transferID + " Getting BTSL Base Exception: " + be.getMessage());
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    LOG.errorTrace(methodName, ex);
                }
            }
            LOG.errorTrace(methodName, e);
            if (PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION.equals(p_action)) {
                finalTransferStatusUpdate = false;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "VchrConsChnlRcvrController[voucherUpdateSenderCreditBack]", transferID, "", "", "Error while credit back sender, getting exception: " + e.getMessage());
        } finally {
			if (mcomCon != null) {
				mcomCon.close("VchrConsChnlRcvrController#voucherUpdateSenderCreditBack");
				mcomCon = null;
			}
        	con=null;
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting for transferID=" + transferID + " p_action=" + p_action);
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
    public void updateSenderForFailedTransaction(Connection p_con, C2STransferVO p_transferVO) throws BTSLBaseException, Exception {
        final String methodName = "updateSenderForFailedTransaction";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered for p_transferVO= " + p_transferVO);
        }
        try {
            userBalancesVO = ChannelUserBL.creditUserBalanceForProduct(p_con, p_transferVO.getTransferID(), p_transferVO);
            ChannelTransferBL.decreaseC2STransferOutCounts(p_con, p_transferVO);
            creditBackEntryDone = true;
            if (requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                requestVO.setSuccessTxn(false);
                final String[] messageArgArray = { c2sTransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), c2sTransferVO
                    .getTransferID(), PretupsBL.getDisplayAmount(userBalancesVO.getBalance()) };
                requestVO.setMessageArguments(messageArgArray);
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_SENDER_CREDIT_SUCCESS);
            }
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), p_transferVO.getSenderNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Done to sender", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (Exception be) {
            LOG.errorTrace(methodName, be);
            finalTransferStatusUpdate = false;
            // PretupsBL.validateRecieverLimits(null,p_transferVO,PretupsI.TRANS_STAGE_AFTER_INTOP,PretupsI.C2S_MODULE);
            TransactionLog.log(p_transferVO.getTransferID(), null, p_transferVO.getSenderMsisdn(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EvdUtil[updateSenderForFailedTransaction]", "", "",
                "", "Error while credit back the retailer Exception: " + be.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " Exited for finalTransferStatusUpdate= " + finalTransferStatusUpdate);
            }
        }
    }

    /**
     * This method is responsible to generate the transaction id in the memory.
     * 
     * @param p_transferVO
     * @return
     */
    /*
     * private static synchronized void generateEVDTransferID(TransferVO
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
     * if(currentReqTime-prevReqTime>=(60000))
     * transactionIDCounter=1;
     * else
     * transactionIDCounter=transactionIDCounter+1;
     * prevReqTime=currentReqTime;
     * if(transactionIDCounter==0)
     * throw new
     * BTSLBaseException("VchrConsChnlRcvrController","generateEVDTransferID"
     * ,PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * transferID=operatorUtil.formatEVDTransferID(p_transferVO,transactionIDCounter
     * );
     * if(transferID==null)
     * throw new
     * BTSLBaseException("VchrConsChnlRcvrController","generateEVDTransferID"
     * ,PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * p_transferVO.setTransferID(transferID);
     * 
     * }
     * catch(Exception e)
     * {
     * LOG.errorTrace(methodName,e);
     * }
     * }
     */

    private static synchronized void generateEVDTransferID(TransferVO p_transferVO) {
        final String methodName = "generateEVDTransferID";

        String transferID = null;
        String minut2Compare = null;
        Date mydate = null;
        try {
            // mydate = p_transferVO.getCreatedOn();
            mydate = new Date();
            p_transferVO.setCreatedOn(mydate);
            minut2Compare = sdfCompare.format(mydate);
            final int currentMinut = Integer.parseInt(minut2Compare);

            if (currentMinut != prevMinut) {
                transactionIDCounter = 1;
                prevMinut = currentMinut;

            } else {
                transactionIDCounter++;

            }
            if (transactionIDCounter == 0) {
                throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = operatorUtil.formatEVDTransferID(p_transferVO, transactionIDCounter);
            if (transferID == null) {
                throw new BTSLBaseException("VchrConsChnlRcvrController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
    }

}
