package com.client.pretups.channel.transfer.requesthandler.clienthandler;

/**
 * @(#)VchrConsController.java
 *                             Copyright(c) 2005, Bharti Telesoft Int. Public
 *                             Ltd.
 *                             All Rights Reserved
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Zeeshan Aleem December 03,2014 Initial Creation
 *                             ------------------------------------------------
 *                             ------------------------------------------------
 */
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
import com.btsl.pretups.logging.P2PRequestDailyLog;
import com.btsl.pretups.logging.SMSChargingLog;
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
import com.btsl.pretups.sos.requesthandler.SOSSettlementController;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.subscriber.businesslogic.SubscriberVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.txn.pretups.sos.businesslogic.SOSTxnDAO;

public class VchrConsController implements ServiceKeywordControllerI, Runnable {

    private static final Log LOG = LogFactory.getLog(VchrConsController.class.getName());
    private P2PTransferVO p2pTransferVO = null;
    private TransferItemVO senderTransferItemVO = null;
    private TransferItemVO receiverTransferItemVO = null;
    private TransferItemVO senderCreditBackStatusVO = null;
    private String senderMSISDN;
    private String receiverMSISDN;
    private SenderVO senderVO;
    private ReceiverVO receiverVO;
    private String senderSubscriberType;
    private String senderNetworkCode;
    private Date currentDate = null;
    private ArrayList itemList = null;
    private String intModCommunicationTypeS;
    private String intModIPS;
    private int intModPortS;
    private String intModClassNameS;
    private String intModCommunicationTypeR;
    private String intModIPR;
    private int intModPortR;
    private String intModClassNameR;
    private String transferID;
    private long requestID;
    private String requestIDStr;
    Locale senderLocale = null;
    Locale receiverLocale = null;
    private boolean isCounterDecreased = false;
    private String type;
    private String serviceType;
    private boolean finalTransferStatusUpdate = true;
    private boolean decreaseTransactionCounts = false;
    private boolean transferDetailAdded = false;
    private boolean senderInterfaceInfoInDBFound = false;
    private boolean receiverInterfaceInfoInDBFound = false;
    private String senderAllServiceClassID = PretupsI.ALL;
    private String receiverAllServiceClassID = PretupsI.ALL;
    private String senderPostBalanceAvailable;
    private String receiverPostBalanceAvailable;
    private String senderCreditPostBalanceAvailable;
    private String receiverExternalID = null;
    private String senderExternalID = null;
    private RequestVO requestVO = null;
    private boolean processedFromQueue = false; // Flag to indicate that request
    // has been processed from Queue
    private boolean recValidationFailMessageRequired = false; // Whether
    // Receiver Fail
    // Message is
    // required before
    // validation
    private boolean recTopupFailMessageRequired = false;// Whether Receiver Fail
    // Message is required
    // before topup
    private ServiceInterfaceRoutingVO serviceInterfaceRoutingVO = null;
    private boolean useAlternateCategory = false; // Whether to use alternate
    // interface category
    private boolean performIntfceCatRoutingBeforeVal = false; // Whether we need
    // to perform
    // alternate
    // interface
    // category
    // routing before
    // sending
    // Receiver
    // Validation
    // Request
    private boolean interfaceCatRoutingDone = false; // To indicate that
    // interface category
    // routing has been done
    // for the process
    private String oldInterfaceCategory = null; // The initial interface
    // category that has to be used
    private String newInterfaceCategory = null; // The alternate interface
    // category that has to be used
    private boolean senderDeletionReqFromSubRouting = false; // Whether to
    // update in
    // Subscriber
    // Routing for
    // sender MSISDN
    private boolean receiverDeletionReqFromSubRouting = false; // Whether to
    // update in
    // Subscriber
    // Routing for
    // Reciever
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
    private String receiverIMSI = null;
    private String senderIMSI = null;
    private NetworkPrefixVO networkPrefixVO = null;
    private String oldDefaultSelector = null;
    private String newDefaultSelector = null;
    private static OperatorUtilI operatorUtil = null;
    private String senderInterfaceStatusType = null;
    private String receiverInterfaceStatusType = null;
    private static int transactionIDCounter = 0;
    // private static long _prevReqTime=0;
    private static int prevMinut = 0;
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    // to update the P2P_Subscriber if subscriber found on Alternate Interface.
    private boolean isUpdateRequired = false;
    private boolean isRoutingSecond = false;
    private boolean isSenderRoutingUpdate = false;
    private boolean oneLog = true;
    private NetworkInterfaceModuleVO networkInterfaceModuleVO = null;
    VomsVoucherVO vomsVO = null;
    private boolean vomsInterfaceInfoInDBFound = false;
    private String payableAmt = null;
    private final String vomsExternalID = null;
    private boolean voucherMarked = false;
    UserBalancesVO userBalancesVO = null;
    private final String vomsAllServiceClassID = null;

    public VchrConsController() {
        p2pTransferVO = new P2PTransferVO();
        currentDate = new Date();
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("P2P_REC_GEN_FAIL_MSG_REQD_V")))) {
            recValidationFailMessageRequired = true;
        }
        if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("P2P_REC_GEN_FAIL_MSG_REQD_T")))) {
            recTopupFailMessageRequired = true;
        }
    }

    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace("VchrConsController", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * This is the main entry method for P2P transactions
     * It calls all other methods based on the process flow.
     * 1. If any authorised user is sending the transfer request then register
     * the user on the request with status NEW.
     * 2. Parse the message send in the request.
     * 3. If the service type used has its Type as BOTH then based on the
     * receiver network code and service type,
     * Get the First interface on which the request will be processed.
     * 4. Validate whether the Service is launched at the Network
     * 5. Check whether the Payment method is allowed for the Service against
     * the Sender Subscriber Type
     * 6. Check whether Receiver MSISDN is barred or not.
     * 7. Load the Receiver Controlling Limits and mark the request as under
     * process.
     * 8. Generate the Transfer ID
     * 9. Populate the interface details for the Series Type and interface
     * Category for VALIDATE action.
     * 10. Based on the Routing Control, Database Check and Series Check are
     * performed to get the Interface ID.
     * 11. Validate the Sender Controlling Limits
     * 12. Validate the Receiver Controlling Limits
     * 13. Check the transaction Load Counters.
     * 14. Based on the Flow Type decide whether Validation needs to be done in
     * Thread along with topup or before that
     * 15. Perform the Validation and Send Request for Sender on the Interface,
     * If Number was not found on the interface
     * Then perform the alternate routing of the interfaces to validate the
     * same.
     * If Found then check whether if Database Check was Y and Number was
     * initailly not found in DB then insert the
     * same. If not found even after routing then delete the number from routing
     * database if initially had been found.
     * 16. Perform the Validation and Send Request for Receiver on the
     * Interface, If Number was not found on the interface
     * Then perform the alternate routing of the interfaces to validate the
     * same.
     * If Found then check whether if Database Check was Y and Number was
     * initailly not found in DB then insert the
     * same. If not found even after routing then Check whether alternate
     * Category Routing was required or not.
     * If Yes then get the new category and perform the validation process again
     * on the interface and on alternate interfaces
     * as well if not found on previous ones. If still not found then Delete the
     * number from routing database if initially had been found.
     * Alternate category routing will be performed only if it has not been
     * performed initially.
     * 17. Calculate the Card group based on the service class IDs
     * 18. Increase the sender controlling limits.
     * 19. Insert the record in transaction table with status as Under process.
     * 20. Populate the interface details for the Series Type and interface
     * Category for TOPUP action.
     * If Database check was Y then do not fire query for search again in DB,
     * use the earlier loaded interface ID.
     * 21. Send the Sender Debit request
     * 22. If Failed then increase the Sender controlling Limits and fail the
     * transaction.
     * 23. If Success then Send credit Request for receiver at interface.
     * 24. If Fail then send credit back request of Sender and increase the
     * controlling limits.
     * 25. If ambigous then check whether credit back request for sender needs
     * to be send or not.
     * 26. If Success make the transaction status as success and send message
     * accordingly.
     */
    @Override
    public void process(RequestVO p_requestVO) {
        Connection con = null;
        MComConnectionI mcomCon = null;
        requestIDStr = p_requestVO.getRequestIDStr();
        boolean receiverMessageSendReq = false;
        final String methodName = "process";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, requestIDStr, "Entered");
        }
        try {
            requestVO = p_requestVO;
            senderVO = (SenderVO) p_requestVO.getSenderVO();
            // If user is not already registered then register the user with
            // status as NEW and Default PIN
            if (senderVO == null) {
                new RegisterationController().regsiterNewUser(p_requestVO);
                senderVO = (SenderVO) p_requestVO.getSenderVO();
                senderVO.setDefUserRegistration(true);
                p_requestVO.setSenderLocale(new Locale(senderVO.getLanguage(), senderVO.getCountry()));
                // If group type counters are allowed to check for controlling
                // for the request gateway then check them
                // This change has been done by ankit on date 14/07/06 for SMS
                // charging
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
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_D, arr);
                        }
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_M, arr);
                    }
                }
            }

            senderLocale = p_requestVO.getSenderLocale();
            receiverLocale = p_requestVO.getReceiverLocale();

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, requestIDStr, "senderLocale=" + senderLocale + " receiverLocale=" + receiverLocale);
            }

            TransactionLog.log("", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "Received Request From Receiver", PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            requestID = p_requestVO.getRequestID();
            type = p_requestVO.getType();

            serviceType = p_requestVO.getServiceType();

            populateVOFromRequest(p_requestVO);

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            senderVO.setModifiedBy(senderVO.getUserID());
            senderVO.setModifiedOn(currentDate);

            /*
             * validate message format
             * if(p_requestVO.getServiceType().equals(PretupsI.REQUEST_TYPE_ACCEPT
             * )) {
             * new MessageFormater().handleAcceptMessageFormat(con,p_requestVO,
             * p2pTransferVO);
             * } else {
             * operatorUtil.validateVoucherPin(con,p_requestVO,p2pTransferVO);
             * }
             */
            operatorUtil.validateVoucherPin(con, p_requestVO, p2pTransferVO);
            // Block added to avoid decimal amount in credit transfer
            if (!BTSLUtil.isStringIn(serviceType, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES))) {
                try {
                    final String displayAmt = PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount());
                    Long.parseLong(displayAmt);
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_AMOUNT);
                }
            }
            receiverLocale = p_requestVO.getReceiverLocale();

            receiverVO = (ReceiverVO) p2pTransferVO.getReceiverVO();
            receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(receiverVO.getMsisdn()));
            // for ussd
            p2pTransferVO.setCellId(requestVO.getCellId());
            p2pTransferVO.setSwitchId(requestVO.getSwitchId());
            p2pTransferVO.setVoucherCode(requestVO.getVoucherCode());
            p2pTransferVO.setSerialNumber(requestVO.getSerialnumber());
            try {p2pTransferVO.setRequestedAmount(Long.parseLong(p_requestVO.getReqAmount()));}catch(Exception e) {}
            
            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_ALLOW_SELF_TOPUP))).booleanValue() && senderVO.getMsisdn().equals(receiverVO.getMsisdn())) {
                LOG.error(methodName, requestIDStr, "Sender and receiver MSISDN are same, Sender MSISDN=" + senderVO.getMsisdn() + " Receiver MSISDN=" + receiverVO
                    .getMsisdn());
                throw new BTSLBaseException("", methodName, PretupsErrorCodesI.ERROR_P2P_SAME_MSISDN_TRANSFER_NOTALLWD, 0, new String[] { receiverVO.getMsisdn() }, null);
            }

            PretupsBL.getSelectorValueFromCode(p_requestVO);

            // Get the Interface Category routing details based on the receiver
            // Network Code and Service type
            if (type.equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
                serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                    .getServiceInterfaceRoutingDetails(receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + senderVO.getSubscriberType());
                if (serviceInterfaceRoutingVO != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            methodName,
                            requestIDStr,
                            "For =" + receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + " Got Interface Category=" + serviceInterfaceRoutingVO
                                .getInterfaceType() + " Alternate Check Required=" + serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + serviceInterfaceRoutingVO
                                .getAlternateInterfaceType() + " oldDefaultSelector=" + serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "newDefaultSelector= " + serviceInterfaceRoutingVO
                                .getAlternateDefaultSelectortCode());
                    }

                    type = serviceInterfaceRoutingVO.getInterfaceType();
                    oldInterfaceCategory = type;
                    oldDefaultSelector = serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                    useAlternateCategory = serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    newInterfaceCategory = serviceInterfaceRoutingVO.getAlternateInterfaceType();
                    newDefaultSelector = serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
                } else {
                    LOG.info(methodName, requestIDStr,
                        "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[process]", "", senderMSISDN,
                        senderNetworkCode, "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    type = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                    // oldDefaultSelector=String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_TRANSFER_DEF_SELECTOR_CODE)));
                    // Changed on 27/05/07 for Service Type selector Mapping
                    final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p2pTransferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                }
            } else {
                serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                    .getServiceInterfaceRoutingDetails(receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + senderVO.getSubscriberType());
                if (serviceInterfaceRoutingVO != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            methodName,
                            requestIDStr,
                            "For =" + receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + " Got Interface Category=" + serviceInterfaceRoutingVO
                                .getInterfaceType() + " Alternate Check Required=" + serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + serviceInterfaceRoutingVO
                                .getAlternateInterfaceType() + " oldDefaultSelector=" + serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "newDefaultSelector= " + serviceInterfaceRoutingVO
                                .getAlternateDefaultSelectortCode());
                    }
                    oldDefaultSelector = serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                } else {
                    // oldDefaultSelector=String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_TRANSFER_DEF_SELECTOR_CODE)));
                    // Changed on 27/05/07 for Service Type selector Mapping
                    final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p2pTransferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                    LOG.info(methodName, requestIDStr, "Service Interface Routing control Not defined, thus using default Selector=" + oldDefaultSelector);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VchrConsController[process]", "", senderMSISDN,
                        senderNetworkCode, "Service Interface Routing control Not defined, thus using default selector=" + oldDefaultSelector);
                }
            }

            // If the interface category does not match with the Receiver
            // subscriber type got from validation from
            // network prefixes then load the new prefix ID against the
            // interface category
            // If not found then check whether Alternate has to be used or not ,
            // if yes then use the old prefix ID
            // already loaded and set the useAlternateCategory=false denoting
            // that do not perform alternate interface
            // category routing again, If Not required then give the error
            if (!receiverVO.getSubscriberType().equals(type)) {
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(receiverVO.getMsisdnPrefix(), type);
                if (networkPrefixVO != null) {
                    PretupsBL.checkNumberPortability(con, requestIDStr, receiverVO.getMsisdn(), networkPrefixVO);
                    receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                    receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
                    receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
                    // In P2P both sender and receiver are from the same network
                    senderVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                } else if (useAlternateCategory) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            methodName,
                            requestIDStr,
                            "Network Prefix Not Found For Series=" + receiverVO.getMsisdnPrefix() + " and Type=" + type + " and thus using Type as =" + newInterfaceCategory + " useAlternateCategory was true");
                    }
                    useAlternateCategory = false;
                    type = newInterfaceCategory;
                    oldDefaultSelector = newDefaultSelector;
                    interfaceCatRoutingDone = true;
                } else {
                    // Refuse the Request
                    LOG.error(this, "Series =" + receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + type);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VchrConsController[process]", "", "", "",
                        "Series =" + receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + type + " But alternate Category Routing was required on interface");
                    throw new BTSLBaseException("", methodName, PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { receiverVO.getMsisdn() }, null);
                }
            } else {
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(receiverVO.getMsisdnPrefix(), type);
                PretupsBL.checkNumberPortability(con, requestIDStr, receiverVO.getMsisdn(), networkPrefixVO);
            }

            // changed for CRE_INT_CR00029 by ankit Zindal
            if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, requestIDStr, "Selector Not found in Incoming Message Thus using Selector as  " + oldDefaultSelector);
                }
                p_requestVO.setReqSelector(oldDefaultSelector);
            } else {
                newDefaultSelector = p_requestVO.getReqSelector();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, requestIDStr, "receiverVO:" + receiverVO);
            }

            // check service payment mapping
            senderSubscriberType = senderVO.getSubscriberType();
            // By Default Entry, will be overridden later in the file
            p2pTransferVO.setTransferCategory(senderSubscriberType + "-" + type);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, requestIDStr, "Starting with transfer Category as :" + p2pTransferVO.getTransferCategory());
            }

            senderNetworkCode = senderVO.getNetworkCode();
            senderMSISDN = ((SubscriberVO) p2pTransferVO.getSenderVO()).getMsisdn();
            receiverMSISDN = ((SubscriberVO) p2pTransferVO.getReceiverVO()).getMsisdn();
            receiverVO.setModule(p2pTransferVO.getModule());
            receiverVO.setCreatedDate(currentDate);
            receiverVO.setLastTransferOn(currentDate);
            p2pTransferVO.setReceiverMsisdn(receiverMSISDN);
            p2pTransferVO.setReceiverNetworkCode(receiverVO.getNetworkCode());
            p2pTransferVO.setSubService(p_requestVO.getReqSelector());
            p2pTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
            // Validates the network service status
            PretupsBL.validateNetworkService(p2pTransferVO);
            receiverMessageSendReq = true;
            // Check Network Load : If true then pass the request else refuse
            // the request
            // LoadController.checkNetworkLoad(requestID,senderNetworkCode,LoadControllerI.NETWORK_NEW_REQUEST);
            // The following check is commented because if default payment
            // method is not used then specific payment method will be loaded
            // according to service type.
            /*
             * if((!PretupsI.YES.equals(p2pTransferVO.getDefaultPaymentMethod()))
             * &&!ServicePaymentMappingCache.isMappingExist(p2pTransferVO.
             * getServiceType
             * (),senderSubscriberType,p2pTransferVO.getPaymentMethodType()))
             * {
             * throw new
             * BTSLBaseException("VchrConsController","process",PretupsErrorCodesI
             * .ERROR_NOTFOUND_SERVICEPAYMENTMETHOD);
             * }
             */

            // chect receiver barred
            try {
                PretupsBL.checkMSISDNBarred(con, receiverVO.getMsisdn(), receiverVO.getNetworkCode(), p2pTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
                // check Black list restricted subscribers not allowed for
                // recharge or for CP2P services.
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHK_BLK_LST_STAT))).booleanValue()) {
                    operatorUtil.isRestrictedSubscriberAllowed(con, receiverVO.getMsisdn(), senderMSISDN);
                }

            } catch (BTSLBaseException be) {
                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                    p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
                }
                throw be;
            }

            PretupsBL.loadRecieverControlLimits(con, p_requestVO.getRequestIDStr(), p2pTransferVO);
            receiverVO.setUnmarkRequestStatus(true);
            try {
                mcomCon.partialCommit();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                throw new BTSLBaseException("VchrConsController", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            // check subscriber details for skey requirement
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SKEY_REQUIRED)).booleanValue() && senderVO.getSkeyRequired().equals(PretupsI.YES)) {
                // Call the method to handle SKey related transfers
                processSKeyGen(con);
            } else {
                processTransfer(con);
                p_requestVO.setTransactionID(transferID);
                receiverVO.setLastTransferID(transferID);
                TransactionLog.log(transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT,
                    PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Generated Transfer ID", PretupsI.TXN_LOG_STATUS_SUCCESS,
                    "Source Type=" + p2pTransferVO.getSourceType() + " Gateway Code=" + p2pTransferVO.getRequestGatewayCode());

                // populate payment and service interface details for validate
                // action
                populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

                p2pTransferVO.setTransferCategory(senderSubscriberType + "-" + type);

                p2pTransferVO.setSenderAllServiceClassID(senderAllServiceClassID);
                p2pTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);

                // validate sender limits before Interface Validations
                SubscriberBL.validateSenderLimits(con, p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);
                // Change is done for ID=SUBTYPVALRECLMT
                // This chenge is done to set the receiver subscriber type in
                // transfer VO
                // This will be used in validate ReceiverLimit method of
                // PretupsBL when receiverTransferItemVO is null
                p2pTransferVO.setReceiverSubscriberType(receiverTransferItemVO.getInterfaceType());

                // validate receiver limits before Interface Validations
                PretupsBL.validateRecieverLimits(con, p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException("VchrConsController", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                }
				if (mcomCon != null) {
					mcomCon.close("VchrConsController#process");
					mcomCon = null;
				}
                con = null;

                // Checks the Various loads
                checkTransactionLoad();
                decreaseTransactionCounts = true;

                // Checks If flow type is common then validation will be
                // performed before sending the
                // response to user and if it is thread based then validation
                // will also be performed in thread
                // along with topup
                if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
                    // Process validation requests
                    processValidationRequest();
                    p_requestVO.setSenderMessageRequired(p2pTransferVO.isUnderProcessMsgReq());
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
                    oneLog = false;
                    p_requestVO.setDecreaseLoadCounters(false);
                } else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                    p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    processValidationRequest();
                    processThread();
                    final String[] messageArgArray = { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getTransferValue()), p2pTransferVO
                        .getVoucherCode() };
                    p_requestVO.setMessageArguments(messageArgArray);
                }

                // Parameter set to indicate that instance counters will not be
                // decreased in receiver for this transaction
                p_requestVO.setDecreaseLoadCounters(false);
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            LOG.error(methodName, "Exception be:" + be.getMessage());
            // be.printStackTrace();
            p_requestVO.setSuccessTxn(false);
            if (senderVO != null) {
                try {
                    if (mcomCon == null) {
                        mcomCon = new MComConnection();
                        con=mcomCon.getConnection();
                    }
                    SubscriberBL.updateSubscriberLastDetails(con, p2pTransferVO, senderVO, currentDate, PretupsErrorCodesI.TXN_STATUS_FAIL);

                } catch (BTSLBaseException bex) {
                    LOG.errorTrace(methodName, bex);
                    // p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[process]", transferID,
                        senderMSISDN, senderNetworkCode, "Base Exception while updating Subscriber Last Details:" + bex.getMessage());
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[process]", transferID,
                        senderMSISDN, senderNetworkCode, "Exception while updating Subscriber Last Details:" + e.getMessage());
                    p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                }

                // Unmarking Receiver last request status
                try {
                    if (receiverVO != null && receiverVO.isUnmarkRequestStatus()) {
                        PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), receiverVO);
                    }
                } catch (BTSLBaseException bex) {
                    LOG.errorTrace(methodName, bex);
                    // p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[process]", transferID,
                        senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[process]", transferID,
                        senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Exception:" + e.getMessage());
                    p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            }
            /*
             * try killed by sanjay and moved in the above if condition 15/08/06
             * {
             * if(receiverVO!=null && receiverVO.isUnmarkRequestStatus())
             * {
             * PretupsBL.unmarkReceiverLastRequest(con,p_requestVO.getRequestIDStr
             * (),receiverVO);
             * }
             * }
             * catch(BTSLBaseException bex)
             * {
             * bex.printStackTrace();
             * //p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION
             * );
             * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.MAJOR,"VchrConsController[process]"
             * ,transferID,senderMSISDN,senderNetworkCode,
             * "Leaving Reciever Unmarked Base Exception:"+bex.getMessage());
             * }
             * catch(Exception e)
             * {
             * e.printStackTrace();
             * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.MAJOR,"VchrConsController[process]"
             * ,transferID,senderMSISDN,senderNetworkCode,
             * "Leaving Reciever Unmarked Exception:"+e.getMessage());
             * p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION)
             * ;
             * }
             */

            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (recValidationFailMessageRequired) {
                if (p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (transferID != null) {
                        p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(transferID), PretupsBL
                            .getDisplayAmount(p2pTransferVO.getRequestedAmount()) }));
                    } else {
                        p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(p2pTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }

            if (!BTSLUtil.isNullString(p2pTransferVO.getSenderReturnMessage())) {
                p_requestVO.setSenderReturnMessage(p2pTransferVO.getSenderReturnMessage());
            }

            if (be.isKey()) {
                if (BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
                    p2pTransferVO.setErrorCode(be.getMessageKey());
                }
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            if (transferID != null && decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                isCounterDecreased = true;
            }
            TransactionLog.log(transferID, requestIDStr, p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, p2pTransferVO
                .getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            // Populate the P2PRequestDailyLog and log
            P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(requestVO, p2pTransferVO));
        } catch (Exception e) {
            LOG.error(methodName, "Exception e:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            p_requestVO.setSuccessTxn(false);
            try {
                if (mcomCon == null) {
                    mcomCon = new MComConnection();
                    con=mcomCon.getConnection();
                }
                SubscriberBL.updateSubscriberLastDetails(con, p2pTransferVO, senderVO, currentDate, PretupsErrorCodesI.TXN_STATUS_FAIL);

            } catch (BTSLBaseException bex) {
                LOG.errorTrace(methodName, bex);
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[process]", transferID,
                    senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            try {
                if (receiverVO != null && receiverVO.isUnmarkRequestStatus()) {
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), receiverVO);
                }
            } catch (BTSLBaseException bex) {
                LOG.errorTrace(methodName, bex);
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            } catch (Exception ex1) {
                LOG.errorTrace(methodName, ex1);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[process]", transferID,
                    senderMSISDN, senderNetworkCode, "Exception:" + ex1.getMessage());
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            if (recValidationFailMessageRequired) {
                if (p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (transferID != null) {
                        p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(transferID), PretupsBL
                            .getDisplayAmount(p2pTransferVO.getRequestedAmount()) }));
                    } else {
                        p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(p2pTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }

            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            LOG.error(methodName, "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            if (transferID != null && decreaseTransactionCounts) {
                isCounterDecreased = true;
                LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[process]", transferID,
                senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(transferID, requestIDStr, p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, p2pTransferVO
                .getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            // Populate the P2PRequestDailyLog and log
            P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(requestVO, p2pTransferVO));
        } finally {
            try {
                if (mcomCon == null) {
                    mcomCon = new MComConnection();
                    con=mcomCon.getConnection();
                }
                if (transferID != null && !transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || (p_requestVO
                    .getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !p_requestVO.getMessageCode().equals(
                    PretupsI.TXN_STATUS_UNDER_PROCESS)))) {
                    addEntryInTransfers(con);
                } else if (transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    LOG.info(methodName, requestIDStr,
                        "Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + transferID + " But not added entry in Transfers yet");
                }
            } catch (BTSLBaseException be) {
                LOG.errorTrace(methodName, be);
                LOG.error(methodName, "BTSL Base Exception:" + be.getMessage());
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                LOG.error(methodName, "Exception:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[process]", transferID,
                    senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            }
            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (isCounterDecreased) {
                p_requestVO.setDecreaseLoadCounters(false);
            }
            if (con != null) {
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
				if (mcomCon != null) {
					mcomCon.close("VchrConsController#process");
					mcomCon = null;
				}
                con = null;
            }
            if (receiverMessageSendReq) {
                if (p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P.equals(receiverTransferItemVO.getValidationStatus())) {
                        p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P + "_R"));
                    }
                    final BTSLMessages btslRecMessages = (BTSLMessages) p2pTransferVO.getReceiverReturnMsg();
                    (new PushMessage(receiverMSISDN, BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), transferID,
                        p2pTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                } else if (p2pTransferVO.getReceiverReturnMsg() != null) {
                    (new PushMessage(receiverMSISDN, (String) p2pTransferVO.getReceiverReturnMsg(), transferID, p2pTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                }
            }
            if (oneLog) {
                // /
                // OneLineTXNLog.log(p2pTransferVO,senderTransferItemVO,receiverTransferItemVO);
            }
            TransactionLog.log(transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
    }

    /**
     * This method process the S Key based transactions
     * 
     * @param p_con
     * @throws BTSLBaseException
     * @throws Exception
     */
    private void processSKeyGen(Connection p_con) throws BTSLBaseException, Exception {
        final String methodName = "processSKeyGen";
        if (LOG.isDebugEnabled()) {
            LOG.debug("processSKeyGen", "Entered");
        }
        try {
            // validate skey details for generation
            // generate skey
            PretupsBL.generateSKey(p_con, p2pTransferVO);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LOG.error(methodName, "Exception e:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[processSKeyGen]", transferID,
                senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("VchrConsController", "processSKeyGen", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
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
        Connection con = null;MComConnectionI mcomCon = null;
        InterfaceVO interfaceVO = null;
        final String methodName = "processValidationRequest";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered and performing validations for transfer ID=" + transferID + " " + p2pTransferVO.getModule() + " " + p2pTransferVO
                .getReceiverNetworkCode() + " " + type);
        }

        try {
            final CommonClient commonClient = new CommonClient();
            InterfaceVO recInterfaceVO = null;
            itemList = new ArrayList();
            itemList.add(senderTransferItemVO);
            itemList.add(receiverTransferItemVO);
            p2pTransferVO.setTransferItemList(itemList);
            final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(p2pTransferVO.getModule(),
                p2pTransferVO.getReceiverNetworkCode(), type);
            intModCommunicationTypeR = networkInterfaceModuleVOS.getCommunicationType();
            intModIPR = networkInterfaceModuleVOS.getIP();
            intModPortR = networkInterfaceModuleVOS.getPort();
            intModClassNameR = networkInterfaceModuleVOS.getClassName();
            // Till here we get the IN interface validation response.. if the
            // service is EVR
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            /*
             * //Get the service Class ID based on the code
             * PretupsBL.validateServiceClassChecks(con,receiverTransferItemVO,
             * p2pTransferVO,PretupsI.P2P_MODULE,requestVO.getServiceType());
             * 
             * receiverVO.setServiceClassCode(receiverTransferItemVO.getServiceClass
             * ());
             * 
             * //validate sender receiver service class,validate transfer value
             * PretupsBL.validateTransferRule(con,p2pTransferVO,PretupsI.P2P_MODULE
             * );
             * 
             * if(!p2pTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR
             * ))
             * {
             * if(receiverTransferItemVO.getPreviousExpiry()==null) {
             * receiverTransferItemVO.setPreviousExpiry(currentDate);
             * }
             * }
             * 
             * //calculate card group details
             * CardGroupBL.calculateCardGroupDetails(con,p2pTransferVO,PretupsI.
             * P2P_MODULE,true);
             * 
             * try {con.commit();} catch(Exception e){ throw new
             * BTSLBaseException("EVDController",methodName,PretupsErrorCodesI.
             * C2S_ERROR_EXCEPTION_EVD);}
             * try {con.close();} catch(Exception e) { throw new
             * BTSLBaseException("EVDController",methodName,PretupsErrorCodesI.
             * C2S_ERROR_EXCEPTION_EVD);}
             * con=null;
             */
            // ***Construct & validate VOMS validation request using common
            // client*************
            networkInterfaceModuleVO = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(p2pTransferVO.getModule(), p2pTransferVO.getReceiverNetworkCode(),
                PretupsI.INTERFACE_CATEGORY_VOMS);
            final EvdUtil evdUtil = new EvdUtil();
            interfaceVO = new InterfaceVO();
            interfaceVO.setInterfaceId(senderTransferItemVO.getInterfaceID());
            interfaceVO.setHandlerClass(senderTransferItemVO.getInterfaceHandlerClass());
            final String requestStr = evdUtil.getVOMSUpdateRequestStr(PretupsI.INTERFACE_VALIDATE_ACTION, p2pTransferVO, networkInterfaceModuleVO, interfaceVO,
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
            p2pTransferVO.setTransferItemList(itemList);

            try {
                updateForVOMSValidationResponse(receiverValResponse);
                VomsVoucherChangeStatusLog.log(transferID, vomsVO.getSerialNo(), VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_UNPROCESS, p2pTransferVO.getReceiverNetworkCode(),
                    senderVO.getUserID(), BTSLUtil.getDateTimeStringFromDate(currentDate));
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
                    PretupsBL.validateRecieverLimits(null, p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.P2P_MODULE);
                }
                throw be;
            } catch (Exception e) {
                LoadController.decreaseResponseCounters(transferID, receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

                // validate receiver limits after Interface Validations
                if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                    PretupsBL.validateRecieverLimits(null, p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.P2P_MODULE);
                }
                throw e;
            }
            voucherMarked = true;

            LoadController.decreaseResponseCounters(transferID, receiverVO.getTransactionStatus(), LoadControllerI.SENDER_VAL_RESPONSE);

            // If request is taking more time till validation of subscriber than
            // reject the request.
            InterfaceVO vomsInterfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(interfaceVO.getInterfaceId());
            if ((System.currentTimeMillis() - p2pTransferVO.getRequestStartTime()) > vomsInterfaceVO.getValExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDController[processValidationRequest]",
                    transferID, senderMSISDN, senderNetworkCode, "Exception: System is taking more time till validation of voucher");
                throw new BTSLBaseException("EVDController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_VAL);
            }
            vomsInterfaceVO = null;

            // This method will set various values into items and transferVO
            evdUtil.calulateTransferValue(p2pTransferVO, vomsVO);

            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Validity=" + p2pTransferVO.getReceiverValidity() + " Talk Time=" + p2pTransferVO.getReceiverTransferValue() + " Serial number=" + vomsVO.getSerialNo(),
                PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                PretupsBL.validateRecieverLimits(null, p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.P2P_MODULE);
            }

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Here the code for debiting the user account will come and Update
            // Transfer Out Counts for the sender
            // userBalancesVO=ChannelUserBL.debitUserBalanceForProduct(con,transferID,p2pTransferVO);
            // SubscriberBL.increaseTransferOutCounts(con,senderTransferItemVO.getServiceClass(),p2pTransferVO);
            // SubscriberBL.increaseC2STransferOutCounts(con,p2pTransferVO,true);
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

            populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
            if (PretupsI.SERVICE_TYPE_EVD.equals(p2pTransferVO.getServiceType())) {
                receiverTransferItemVO.setServiceClass(vomsAllServiceClassID);
                final String pinSendTo = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_SEND_TO));
                // Construct the PIN message for sender or receiver as the case
                // is
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "PIN sent to in preference=" + pinSendTo);
                }
                // changed for EVD private recharge (as subservice =1 )
                if (PretupsI.PIN_SENT_RET.equals(pinSendTo) || (p2pTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVD) && p2pTransferVO.getSubService().equals("1"))) {
                    p2pTransferVO.setPinSentToMsisdn(senderMSISDN);
                } else {
                    p2pTransferVO.setPinSentToMsisdn(receiverMSISDN);
                }
            }
            senderTransferItemVO.setServiceClass(vomsAllServiceClassID);
            // Method to insert the record in c2s transfer table
            // added by nilesh: consolidated for logger
            if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                // ChannelTransferBL.addC2STransferDetails(con,p2pTransferVO);
                PretupsBL.addTransferDetails(con, p2pTransferVO);
            }
            transferDetailAdded = true;
            // Commit the transaction and relaease the locks
            try {
                mcomCon.finalCommit();
            } catch (Exception be) {
                LOG.errorTrace(methodName, be);
            }
			if (mcomCon != null) {
				mcomCon.close("VchrConsController#processValidationRequest");
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
             */

            // Push Under Process Message to Sender and Reciever , this might
            // have to be implemented on flag basis whether to send message or
            // not
            /*
             * if(p2pTransferVO.isUnderProcessMsgReq() &&
             * _receiverMessageSendReq
             * &&!BTSLUtil.isStringIn(p2pTransferVO.getRequestGatewayCode
             * (),_notAllowedRecSendMessGatw
             * )&&!"ALL".equals(_notAllowedRecSendMessGatw)) {
             * (new PushMessage(receiverMSISDN,getReceiverUnderProcessMessage(),
             * transferID
             * ,p2pTransferVO.getRequestGatewayCode(),receiverLocale)).push();
             * }
             */
            // If request is taking more time till validation of subscriber than
            // reject the request.
            // intrfaceVO=(InterfaceVO)NetworkInterfaceModuleCache.getObject(interfaceVO.getInterfaceId());
            if (p2pTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
                if ((System.currentTimeMillis() - p2pTransferVO.getRequestStartTime()) > recInterfaceVO.getTopUpExpiryTime()) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "EVDController[processValidationRequest]",
                        transferID, senderMSISDN, senderNetworkCode, "Exception: System is taking more time till topup");
                    throw new BTSLBaseException("EVDController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
                }
                recInterfaceVO = null;
            }

            if (p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || processedFromQueue) {
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
                if (p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL), new String[] { String.valueOf(transferID), PretupsBL
                        .getDisplayAmount(p2pTransferVO.getRequestedAmount()) }));
                }
            }
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    p2pTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    p2pTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
            }
            LOG.error("EVDController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());

            voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            if (con != null) {
                mcomCon.finalRollback();
            }
            con = null;
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (recValidationFailMessageRequired) {
                if (p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD), new String[] { String.valueOf(transferID), PretupsBL
                        .getDisplayAmount(p2pTransferVO.getRequestedAmount()) }));
                }
            }
            if (BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            voucherUpdateSenderCreditBack(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);

            throw new BTSLBaseException("EVDController", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("VchrConsController#processValidationRequest");
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("processTransfer", p2pTransferVO.getRequestID(), "Entered");
        }
        try {
            p2pTransferVO.setTransferDate(currentDate);
            p2pTransferVO.setTransferDateTime(currentDate);
            // PretupsBL.generateTransferID(p2pTransferVO);
            generateTransferID(p2pTransferVO);
            transferID = p2pTransferVO.getTransferID();
            // set sender transfer item details
            setSenderTransferItemVO();
            // set receiver transfer item details
            setReceiverTransferItemVO();

            // validate self transfer
            // The code below is commented as self topup allowed is checked from
            // system preferences before this method is called.
            // This is commented by ankit zindal 0n date 2/8/06 as discussed
            // with AC/GB
            /*
             * if(p2pTransferVO.getDefaultPaymentMethod().equals(PretupsI.YES)&&
             * senderMSISDN.equals(receiverMSISDN))
             * {
             * throw new
             * BTSLBaseException(this,"processTransfer",PretupsErrorCodesI
             * .ERROR_NOTALLOWED_SELFTOPUPDEFAULTPMT);
             * }
             */

            // Get the product Info based on the service type
            PretupsBL.getProductFromServiceType(p_con, p2pTransferVO, serviceType, PretupsI.P2P_MODULE);

        } catch (BTSLBaseException be) {
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (be.isKey()) {
                p2pTransferVO.setErrorCode(be.getMessageKey());
            } else {
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            throw be;
        } catch (Exception e) {
            if (recValidationFailMessageRequired) {
                if (p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (transferID != null) {
                        p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(transferID), PretupsBL
                            .getDisplayAmount(p2pTransferVO.getRequestedAmount()) }));
                    } else {
                        p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(p2pTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            final String methodName = "processValidationRequest";
            LOG.errorTrace(methodName, e);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[processTransfer]", transferID,
                senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("VchrConsController", "processTransfer", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    @Override
    public void run() {
        processThread();
    }

    /**
     * This method will perform either topup in thread or both validation and
     * topup on thread based on Flow Type
     */
    public void processThread() {
        final String methodName = "processThread";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, transferID, "Entered");
        }
        BTSLMessages btslMessages = null;
        final boolean onlyDecreaseCounters = false;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            // Perform the validation of parties if Flow type is thread

            if (p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !processedFromQueue) {
                processValidationRequestInThread();
            }

            // send validation request for sender
            final CommonClient commonClient = new CommonClient();
            LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.SENDER_UNDER_TOP);
            /*
             * String requestStr=getSenderDebitAdjustStr();
             * TransactionLog.log(transferID,requestIDStr,senderMSISDN,
             * senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_INTOP
             * ,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
             * String
             * senderDebitResponse=commonClient.process(requestStr,transferID
             * ,intModCommunicationTypeS
             * ,intModIPS,intModPortS,intModClassNameS);
             * TransactionLog.log(transferID,requestIDStr,senderMSISDN,
             * senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_RES,PretupsI.TXN_LOG_TXNSTAGE_INTOP
             * ,senderDebitResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
             * 
             * if(LOG.isDebugEnabled()) {
             * LOG.debug(methodName,transferID,"senderDebitResponse From IN Module="
             * +senderDebitResponse);
             * }
             * try
             * {
             * //Get the Sender Debit response and processes the same
             * updateForSenderDebitResponse(senderDebitResponse);
             * }
             * catch(BTSLBaseException be)
             * {
             * TransactionLog.log(transferID,requestIDStr,senderMSISDN,
             * senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS
             * ,"Transaction Failed"
             * ,PretupsI.TXN_LOG_STATUS_FAIL,"Transfer Status="
             * +p2pTransferVO.getTransferStatus
             * ()+" Getting Code="+senderVO.getInterfaceResponseCode());
             * 
             * LoadController.decreaseResponseCounters(transferID,PretupsErrorCodesI
             * .TXN_STATUS_FAIL,LoadControllerI.SENDER_TOP_RESPONSE);
             * 
             * //con=OracleUtil.getConnection();
             * 
             * //If transaction is Ambigous and Preference flag is Set to true
             * (Whether credit back is true in ambigous case)
             * //Then credit back the sender
             * if(p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.
             * TXN_STATUS_AMBIGUOUS) &&
             * ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue() ||
             * p2pTransferVO.getTransferStatus
             * ().equals(PretupsErrorCodesI.TXN_STATUS_FAIL))
             * {
             * onlyDecreaseCounters=true;
             * creditBackSenderForFailedTrans(commonClient,onlyDecreaseCounters);
             * }
             * 
             * //validate sender limits after Interface Updation
             * SubscriberBL.validateSenderLimits(null,p2pTransferVO,PretupsI.
             * TRANS_STAGE_AFTER_INTOP);
             * 
             * //PretupsBL.unmarkReceiverLastRequest(con,transferID,receiverVO);
             * throw be;
             * }
             * LoadController.decreaseResponseCounters(transferID,PretupsErrorCodesI
             * .TXN_STATUS_SUCCESS,LoadControllerI.SENDER_TOP_RESPONSE);
             * 
             * LoadController.incrementTransactionInterCounts(transferID,
             * LoadControllerI.RECEIVER_UNDER_TOP);
             */
            final String receiverStr = getReceiverCreditStr();
            // send validation request for receiver
            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                receiverStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            final String receiverCreditResponse = commonClient.process(receiverStr, transferID, intModCommunicationTypeR, intModIPR, intModPortR, intModClassNameR);
            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP,
                receiverCreditResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, transferID, "receiverCreditResponse From IN Module=" + receiverCreditResponse);
            }

            try {
                // Get the Receiver Credit response and processes the same
                updateForReceiverCreditResponse(receiverCreditResponse);
            } catch (BTSLBaseException be) {
                TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + p2pTransferVO.getTransferStatus() + " Getting Code=" + receiverVO
                        .getInterfaceResponseCode());
                // No need to check for sender limits as the receiver created
                // the problem
                LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_TOP_RESPONSE);

                // con=OracleUtil.getConnection();

                // If transaction is Ambigous and Preference flag is Set to true
                // (Whether credit back is true in ambigous case)
                // Then credit back the sender
                if (p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue() || p2pTransferVO
                    .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    creditBackSenderForFailedTrans(commonClient, onlyDecreaseCounters);
                }

                // validate receiver limits after Interface Updation
                PretupsBL.validateRecieverLimits(null, p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.P2P_MODULE);

                throw be;
            } catch (Exception e) {
                LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_TOP_RESPONSE);

                // con=OracleUtil.getConnection();
                // If transaction is Ambigous and Preference flag is Set to true
                // (Whether credit back is true in ambigous case)
                // Then credit back the sender

                if (p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue() || p2pTransferVO
                    .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    creditBackSenderForFailedTrans(commonClient, onlyDecreaseCounters);
                }

                // validate receiver limits after Interface Updation
                PretupsBL.validateRecieverLimits(null, p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.P2P_MODULE);
                // No need to check for sender limits as the receiver created
                // the problem
                TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + p2pTransferVO.getTransferStatus() + " Getting exception=" + e.getMessage());

                throw e;
            }

            LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.RECEIVER_TOP_RESPONSE);

            senderVO.setTotalConsecutiveFailCount(0);
            senderVO.setTotalTransfers(senderVO.getTotalTransfers() + 1);
            senderVO.setTotalTransferAmount(senderVO.getTotalTransferAmount() + senderTransferItemVO.getRequestValue());
            senderVO.setLastSuccessTransferDate(currentDate);
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            p2pTransferVO.setErrorCode(null);
            // TransactionLog.log(transferID,requestIDStr,senderMSISDN,senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Success",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+p2pTransferVO.getTransferStatus()+" Transfer Category="+p2pTransferVO.getTransferCategory());

            // con=OracleUtil.getConnection();

            // validate receiver limits after Interface Updation
            PretupsBL.validateRecieverLimits(null, p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.P2P_MODULE);

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(), requestVO.getMessageGatewayVO().getGatewayType(), senderNetworkCode,
                serviceType, transferID, LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, receiverVO.getNetworkCode());
            // real time settlement of LMB on the basis of system preference
            // //@nu
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ONLINE_ALLOW))).booleanValue()) {
                if (p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    final Date currentDate = new Date();
                    SOSVO sosvo = null;
                    try {
                        if (mcomCon == null) {
                            mcomCon = new MComConnection();con=mcomCon.getConnection();
                        }
                        sosvo = new SOSTxnDAO().loadSOSDetails(con, currentDate, receiverMSISDN);
                        if (sosvo != null) {
                            sosvo.setCreatedOn(currentDate);
                            sosvo.setInterfaceID(receiverTransferItemVO.getInterfaceID());
                            sosvo.setInterfaceHandlerClass(receiverTransferItemVO.getInterfaceHandlerClass());
                            sosvo.setOldExpiryInMillis(receiverTransferItemVO.getOldExporyInMillis());
                            sosvo.setLmbAmountAtIN(receiverTransferItemVO.getLmbdebitvalue());
                            // /sosvo.setServiceType(serviceType);
                            sosvo.setSettlmntServiceType(requestVO.getServiceType()); // samna
                            // soin
                            sosvo.setLocale(receiverLocale);
                            final SOSSettlementController sosSettlementController = new SOSSettlementController();
                            sosSettlementController.processSOSRechargeRequest(sosvo);
                        } else {
                            LOG.error(this, "VchrConsController", methodName + " No record found in database for this number :" + receiverMSISDN);
                            // throw new
                            // BTSLBaseException("VchrConsController","run",PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
                        }
                    } catch (BTSLBaseException be) {
                        LOG.errorTrace(methodName, be);
                        LOG.error(this, "VchrConsController",
                            methodName + "Transaction ID: " + sosvo.getTransactionID() + "Msisdn" + receiverMSISDN + "Getting Exception while processing LMB request :" + be);
                    } finally {
                        if (con != null) {
                            try {
                                mcomCon.finalCommit();
                            } catch (Exception e) {
                                LOG.errorTrace(methodName, e);
                            }
							if (mcomCon != null) {
								mcomCon.close("VchrConsController#processThread");
								mcomCon = null;
							}
                            con = null;
                        }
                    }
                }
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            if (BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    p2pTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            }
            if (be.isKey() && p2pTransferVO.getSenderReturnMessage() == null) {
                btslMessages = be.getBtslMessages();
            } else if (p2pTransferVO.getSenderReturnMessage() == null) {
                p2pTransferVO.setSenderReturnMessage(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, transferID, "Error Code:" + btslMessages.print());
            }

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(), requestVO.getMessageGatewayVO().getGatewayType(), senderNetworkCode,
                serviceType, transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, receiverVO.getNetworkCode());
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            if (BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            LOG.error(methodName, transferID, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[run]", transferID, senderMSISDN,
                senderNetworkCode, "Exception:" + e.getMessage());
            btslMessages = new BTSLMessages(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(requestVO.getInstanceID(), requestVO.getMessageGatewayVO().getGatewayType(), senderNetworkCode,
                serviceType, transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, receiverVO.getNetworkCode());
        } finally {
            try {
                if (p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) p2pTransferVO
                    .getReceiverReturnMsg()).isKey())) {
                    p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.P2P_RECEIVER_FAIL), new String[] { String.valueOf(transferID), PretupsBL
                        .getDisplayAmount(p2pTransferVO.getRequestedAmount()) }));
                }

                LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);

                if (mcomCon == null) {
                    mcomCon = new MComConnection();
                    con=mcomCon.getConnection();
                }
                try {
                    SubscriberBL.updateSubscriberLastDetails(con, p2pTransferVO, senderVO, currentDate, p2pTransferVO.getTransferStatus());
                } catch (BTSLBaseException bex) {
                    LOG.errorTrace(methodName, bex);
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[run]", transferID,
                        senderMSISDN, senderNetworkCode, "Not able to update Subscriber Last Details Exception:" + e.getMessage());
                }

                try {
                    if (receiverVO != null && receiverVO.isUnmarkRequestStatus()) {
                        PretupsBL.unmarkReceiverLastRequest(con, transferID, receiverVO);
                    }
                } catch (BTSLBaseException bex) {
                    LOG.errorTrace(methodName, bex);
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[run]", transferID,
                        senderMSISDN, senderNetworkCode, "Not able to unmark Receiver Last Request, Exception:" + e.getMessage());
                }

                if (finalTransferStatusUpdate) {
                    // update transfer details in database
                    // update transfer details in database
                    p2pTransferVO.setModifiedOn(currentDate);
                    p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    PretupsBL.updateTransferDetails(con, p2pTransferVO);
                }
            } catch (BTSLBaseException bex) {
                LOG.errorTrace(methodName, bex);
                try {
                    if (con != null) {
                       mcomCon.finalRollback();
                    }
                } catch (Exception ex) {
                    LOG.errorTrace(methodName, ex);
                }
                LOG.error(methodName, transferID, "BTSL Base Exception while updating transfer details in database:" + bex.getMessage());
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                try {
                    if (con != null) {
                        mcomCon.finalRollback();
                    }
                } catch (Exception ex) {
                    LOG.errorTrace(methodName, ex);
                }
                LOG.error(methodName, transferID, "Exception while updating transfer details in database:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[run]", transferID,
                    senderMSISDN, senderNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
            }
            if (con != null) {
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
				if (mcomCon != null) {
					mcomCon.close("VchrConsController#processThread");
					mcomCon = null;
				}
                con = null;
            }
            // If transaction is fail and grouptype counters need to be decrease
            // then decrease the counters
            // This change has been done by ankit on date 14/07/06 for SMS
            // charging
            if (requestVO.getSenderVO() != null && !p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && requestVO.isDecreaseGroupTypeCounter() && ((SenderVO) requestVO
                .getSenderVO()).getUserControlGrouptypeCounters() != null) {
                PretupsBL.decreaseGroupTypeCounters(((SenderVO) requestVO.getSenderVO()).getUserControlGrouptypeCounters());
            }

            final String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("P2P_REC_MSG_REQD_BY_ALT_GW"));
            String reqruestGW = p2pTransferVO.getRequestGatewayCode();
            if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
                if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
                    reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("run: Reciver Message push through alternate GW", reqruestGW, "Requested GW was:" + p2pTransferVO.getRequestGatewayCode());
                    }
                }
            }

            if (p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                if (p2pTransferVO.getReceiverReturnMsg() == null) {
                    (new PushMessage(receiverMSISDN, getReceiverSuccessMessage(), transferID, reqruestGW, receiverLocale)).push();
                } else if (p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    final BTSLMessages btslRecMessages = (BTSLMessages) p2pTransferVO.getReceiverReturnMsg();
                    (new PushMessage(receiverMSISDN, BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), transferID, reqruestGW,
                        receiverLocale)).push();
                } else {
                    (new PushMessage(receiverMSISDN, (String) p2pTransferVO.getReceiverReturnMsg(), transferID, reqruestGW, receiverLocale)).push();
                }
            } else if (recTopupFailMessageRequired && p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                if (p2pTransferVO.getReceiverReturnMsg() == null) {
                    (new PushMessage(receiverMSISDN, getReceiverAmbigousMessage(), transferID, reqruestGW, receiverLocale)).push();
                } else if (p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    final BTSLMessages btslRecMessages = (BTSLMessages) p2pTransferVO.getReceiverReturnMsg();
                    (new PushMessage(receiverMSISDN, BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), transferID, reqruestGW,
                        receiverLocale)).push();
                } else {
                    (new PushMessage(receiverMSISDN, (String) p2pTransferVO.getReceiverReturnMsg(), transferID, reqruestGW, receiverLocale)).push();
                }
            } else if (recTopupFailMessageRequired && p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                if (p2pTransferVO.getReceiverReturnMsg() == null) {
                    (new PushMessage(receiverMSISDN, getReceiverFailMessage(), transferID, reqruestGW, receiverLocale)).push();
                } else if (p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    final BTSLMessages btslRecMessages = (BTSLMessages) p2pTransferVO.getReceiverReturnMsg();
                    (new PushMessage(receiverMSISDN, BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), transferID, reqruestGW,
                        receiverLocale)).push();
                } else {
                    (new PushMessage(receiverMSISDN, (String) p2pTransferVO.getReceiverReturnMsg(), transferID, reqruestGW, receiverLocale)).push();
                }
            }

            /*
             * if(p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.
             * TXN_STATUS_SUCCESS)
             * &&p2pTransferVO.getReceiverReturnMsg()!=null&&
             * ((BTSLMessages)p2pTransferVO.getReceiverReturnMsg()).isKey())
             * {
             * BTSLMessages
             * btslRecMessages=(BTSLMessages)p2pTransferVO.getReceiverReturnMsg
             * ();
             * (new
             * PushMessage(receiverMSISDN,BTSLUtil.getMessage(receiverLocale
             * ,btslRecMessages
             * .getMessageKey(),btslRecMessages.getArgs()),transferID
             * ,p2pTransferVO.getRequestGatewayCode(),receiverLocale)).push();
             * }
             * else
             * if(p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI
             * .TXN_STATUS_SUCCESS)
             * &&p2pTransferVO.getReceiverReturnMsg()!=null)
             * (new
             * PushMessage(receiverMSISDN,(String)p2pTransferVO.getReceiverReturnMsg
             * (
             * ),transferID,p2pTransferVO.getRequestGatewayCode(),receiverLocale
             * )).push();
             * 
             * if(!p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.
             * TXN_STATUS_SUCCESS)
             * &&!BTSLUtil.isNullString(Constants.getProperty
             * ("P2P_REC_GEN_FAIL_MSG_REQD")) &&
             * "Y".equalsIgnoreCase(Constants.getProperty
             * ("P2P_REC_GEN_FAIL_MSG_REQD")))
             * {
             * if(p2pTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)
             * p2pTransferVO.getReceiverReturnMsg()).isKey())
             * {
             * BTSLMessages
             * btslRecMessages=(BTSLMessages)p2pTransferVO.getReceiverReturnMsg
             * ();
             * (new
             * PushMessage(receiverMSISDN,BTSLUtil.getMessage(receiverLocale
             * ,btslRecMessages
             * .getMessageKey(),btslRecMessages.getArgs()),transferID
             * ,p2pTransferVO.getRequestGatewayCode(),receiverLocale)).push();
             * }
             * else if(p2pTransferVO.getReceiverReturnMsg()!=null)
             * (new
             * PushMessage(receiverMSISDN,(String)p2pTransferVO.getReceiverReturnMsg
             * (
             * ),transferID,p2pTransferVO.getRequestGatewayCode(),receiverLocale
             * )).push();
             * else
             * if(p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI
             * .TXN_STATUS_AMBIGUOUS))
             * (new
             * PushMessage(receiverMSISDN,getReceiverAmbigousMessage(),transferID
             * ,p2pTransferVO.getRequestGatewayCode(),receiverLocale)).push();
             * else
             * if(p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI
             * .TXN_STATUS_FAIL))
             * (new
             * PushMessage(receiverMSISDN,getReceiverFailMessage(),transferID
             * ,p2pTransferVO.getRequestGatewayCode(),receiverLocale)).push();
             * }
             */PushMessage pushMessages = null;
            // In case of self TopUp,sender and receiver will be same so only
            // one final response message will be given to receiver.
            // Otherwise two final response message.
            if (!p2pTransferVO.getSenderMsisdn().equals(p2pTransferVO.getReceiverMsisdn())) {
                if (!BTSLUtil.isNullString(p2pTransferVO.getSenderReturnMessage())) {
                    pushMessages = (new PushMessage(senderMSISDN, p2pTransferVO.getSenderReturnMessage(), transferID, p2pTransferVO.getRequestGatewayCode(), senderLocale));
                } else if (btslMessages != null) {
                    // push error message to sender
                    pushMessages = (new PushMessage(senderMSISDN, BTSLUtil.getMessage(senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs()), transferID,
                        p2pTransferVO.getRequestGatewayCode(), senderLocale));
                } else if (p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    // push success message to sender and receiver
                    pushMessages = (new PushMessage(senderMSISDN, getSenderSuccessMessage(), transferID, p2pTransferVO.getRequestGatewayCode(), senderLocale));
                    // (new
                    // PushMessage(receiverMSISDN,getReceiverSuccessMessage(),transferID,p2pTransferVO.getRequestGatewayCode(),receiverLocale)).push();
                    if (senderVO.isActivateStatusReqd()) {
                        // TO DO Also update is required if PIN is other then
                        // Default PIN
                        (new PushMessage(senderMSISDN, getSenderRegistrationMessage(), transferID, reqruestGW, senderLocale)).push();
                    }
                }
            } else {
                if (btslMessages != null) {
                    // push error message to sender
                    pushMessages = (new PushMessage(senderMSISDN, BTSLUtil.getMessage(senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs()), transferID,
                        p2pTransferVO.getRequestGatewayCode(), senderLocale));
                }
            }
            // If transaction is successfull then if group type counters reach
            // limit then send message using gateway that is associated with
            // group type profile
            // This change has been done by ankit on date 14/07/06 for SMS
            // charging
            if (p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED))
                .indexOf(requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE.equals(requestVO.getGroupType())) {
                try {
                    GroupTypeProfileVO groupTypeProfileVO = null;
                    // load the user running and profile counters
                    // Check the counters
                    // update the counters
                    groupTypeProfileVO = PretupsBL.loadAndCheckP2PGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CHARGING);
                    // if group type counters reach limit then send message
                    // using gateway that is associated with group type profile
                    if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
                        pushMessages.push(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());// new
                        // method
                        // will
                        // be
                        // called
                        // here
                        SMSChargingLog.log(((SenderVO) requestVO.getSenderVO()).getUserID(), (((SenderVO) requestVO.getSenderVO()).getUserChargeGrouptypeCounters())
                            .getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(), groupTypeProfileVO.getResGatewayType(),
                            groupTypeProfileVO.getNetworkCode(), requestVO.getGroupType(), requestVO.getServiceType(), requestVO.getModule());
                    } else {
                        pushMessages.push();
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
            } else {
                // pushMessages.push();
            }
            int messageLength = 0;
            final String message = getSenderSuccessMessage();
            final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
            if (!BTSLUtil.isNullString(messLength)) {
                messageLength = Integer.valueOf(messLength);
            }
            if (((message.length() < messageLength)) && ((p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) && (!reqruestGW
                .equalsIgnoreCase(p2pTransferVO.getRequestGatewayCode())))) {
                // push success message to sender and receiver
                final PushMessage pushMessages1 = (new PushMessage(senderMSISDN, message, transferID, reqruestGW, senderLocale));
                pushMessages1.push();
            }
            if (!oneLog) {
                OneLineTXNLog.log(p2pTransferVO, senderTransferItemVO, receiverTransferItemVO);
            }
            TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS, "Transfer Status=" + p2pTransferVO.getTransferStatus() + " Transfer Category=" + p2pTransferVO
                    .getTransferCategory() + " Error Code=" + p2pTransferVO.getErrorCode() + " Message=" + p2pTransferVO.getSenderReturnMessage());
            // Populate the P2PRequestDailyLog and log
            P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(requestVO, p2pTransferVO));
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, transferID, "Exiting");
            }
        }
    }

    /**
     * Method to get the sender regsitration message
     * 
     * @return
     */
    private String getSenderRegistrationMessage() {
        if (senderVO.isPinUpdateReqd()) {
            final String[] messageArgArray = { BTSLUtil.decryptText(senderVO.getPin()) };
            return BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS_WITHPIN, messageArgArray);
        }
        return BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS, null);
    }

    /***
     * 
     * Method updated for notification message using service class date 15/05/06
     */
    private String getSenderSuccessMessage() {
        final String methodName = "getSenderSuccessMessage";
        String key = null;

        String[] messageArgArray = null;
        long remainingMonAmount = 0;
        long remainingMonCount = 0;
        long remainingDailyAmount = 0;
        long remainingDailyCount = 0;
        long remainingWeekCount = 0;
        long remainingWeekAmount = 0;
        // added for updating sender message with remaining threshold amount and
        // count Manisha(01/02/08)
        // messages would be changed in messages.properties per operator's
        // requirement (Monthly/daily/weekly/not applicable). So there would be
        // 4 messages against one key, but only one would be active at a time.
        // Same would be applicable for messages with service class.
        remainingMonAmount = (senderVO.getMonthlyMaxTransAmtThreshold() - (senderVO.getMonthlyTransferAmount() + p2pTransferVO.getRequestedAmount()));
        remainingMonCount = (senderVO.getMonthlyMaxTransCountThreshold() - (senderVO.getMonthlyTransferCount() + 1));
        remainingDailyAmount = (senderVO.getDailyMaxTransAmtThreshold() - (senderVO.getDailyTransferAmount() + p2pTransferVO.getRequestedAmount()));
        remainingDailyCount = (senderVO.getDailyMaxTransCountThreshold() - (senderVO.getDailyTransferCount() + 1));
        remainingWeekAmount = (senderVO.getWeeklyMaxTransAmtThreshold() - (senderVO.getWeeklyTransferAmount() + p2pTransferVO.getRequestedAmount()));
        remainingWeekCount = (senderVO.getWeeklyMaxTransCountThreshold() - (senderVO.getWeeklyTransferCount() + 1));
        if (!"N".equals(senderPostBalanceAvailable)) {
            messageArgArray = new String[] { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), PretupsBL
                .getDisplayAmount(senderTransferItemVO.getTransferValue()), PretupsBL.getDisplayAmount(receiverTransferItemVO.getTransferValue()), PretupsBL
                .getDisplayAmount(senderTransferItemVO.getPostBalance()), PretupsBL.getDisplayAmount(p2pTransferVO.getSenderAccessFee()), p2pTransferVO.getSubService(), PretupsBL
                .getDisplayAmount(remainingDailyAmount), Long.toString(remainingDailyCount), PretupsBL.getDisplayAmount(remainingMonAmount), Long.toString(remainingMonCount), PretupsBL
                .getDisplayAmount(remainingWeekAmount), Long.toString(remainingWeekCount) };
            if (p2pTransferVO.getSenderAccessFee() == 0) {
                key = PretupsErrorCodesI.P2P_SENDER_SUCCESS_WITHOUT_ACCESSFEE;
                // return
                // BTSLUtil.getMessage(senderLocale,PretupsErrorCodesI.P2P_SENDER_SUCCESS_WITHOUT_ACCESSFEE,messageArgArray);
            } else {
                key = PretupsErrorCodesI.P2P_SENDER_SUCCESS;
                // return
                // BTSLUtil.getMessage(senderLocale,PretupsErrorCodesI.P2P_SENDER_SUCCESS,messageArgArray);
            }
        } else {
            messageArgArray = new String[] { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), PretupsBL
                .getDisplayAmount(senderTransferItemVO.getTransferValue()), PretupsBL.getDisplayAmount(receiverTransferItemVO.getTransferValue()), PretupsBL
                .getDisplayAmount(p2pTransferVO.getSenderAccessFee()), p2pTransferVO.getSubService(), PretupsBL.getDisplayAmount(remainingDailyAmount), Long
                .toString(remainingDailyCount), PretupsBL.getDisplayAmount(remainingMonAmount), Long.toString(remainingMonCount), PretupsBL
                .getDisplayAmount(remainingWeekAmount), Long.toString(remainingWeekCount) };
            key = PretupsErrorCodesI.P2P_SENDER_SUCCESS_WITHOUT_POSTBAL;
            // return
            // BTSLUtil.getMessage(senderLocale,PretupsErrorCodesI.P2P_SENDER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
        }
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_SEN))).booleanValue()) {
            String message = null;
            try {
                message = BTSLUtil.getMessage(senderLocale, key + "_" + senderTransferItemVO.getServiceClass(), messageArgArray);
                if (BTSLUtil.isNullString(message)) {
                    message = BTSLUtil.getMessage(senderLocale, key, messageArgArray);
                }
                return message;
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                return BTSLUtil.getMessage(senderLocale, key, messageArgArray);
            }
        }
        return BTSLUtil.getMessage(senderLocale, key, messageArgArray);
    }

    /**
     * Method to get the Receiver Ambigous Message
     * 
     * @return
     */
    private String getReceiverAmbigousMessage() {
        final String[] messageArgArray = { senderMSISDN, transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(receiverLocale, PretupsErrorCodesI.P2P_RECEIVER_AMBIGOUS_MESSAGE_KEY, messageArgArray);
    }

    /**
     * Method to get the Receiver Fail Message
     * 
     * @return
     */
    private String getReceiverFailMessage() {
        final String[] messageArgArray = { senderMSISDN, transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(receiverLocale, PretupsErrorCodesI.P2P_RECEIVER_FAIL_MESSAGE_KEY, messageArgArray);
    }

    /***
     * 
     * Method updated for notification message using service class date 15/05/06
     */
    private String getReceiverSuccessMessage() {
        final String methodName = "getReceiverSuccessMessage";
        String[] messageArgArray = null;
        String key = null;

        // For Get NUMBER BACK Service
        if (p2pTransferVO.getReceiverTransferItemVO().isNumberBackAllowed()) {
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED))).booleanValue()) {
                p2pTransferVO.setCalminusBonusvalue(p2pTransferVO.getReceiverTransferValue() - p2pTransferVO.getBonusTalkTimeValue());
            }
            // added by vikas kumar for card group updation
            messageArgArray = new String[] { transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getReceiverTransferValue()), String.valueOf(receiverTransferItemVO
                .getValidity()), senderMSISDN, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), PretupsBL
                .getDisplayAmount(p2pTransferVO.getReceiverAccessFee()), p2pTransferVO.getSubService(), String.valueOf(p2pTransferVO.getReceiverBonus1()), String
                .valueOf(p2pTransferVO.getReceiverBonus2()), PretupsBL.getDisplayAmount(p2pTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(p2pTransferVO
                .getCalminusBonusvalue()), String.valueOf(p2pTransferVO.getReceiverBonus1Validity()), String.valueOf(p2pTransferVO.getReceiverBonus2Validity()), String
                .valueOf(p2pTransferVO.getReceiverCreditBonusValidity()) };
            if (p2pTransferVO.getBonusTalkTimeValue() == 0) {
                key = PretupsErrorCodesI.P2P_RECEIVER_GET_NUMBER_BACK_SUCCESS;
            } else {
                key = PretupsErrorCodesI.P2P_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS;
            }
        }
        if (!"N".equals(receiverPostBalanceAvailable)) {
            String dateStr = null;
            try {
                dateStr = BTSLUtil.getDateStringFromDate(receiverTransferItemVO.getNewExpiry());
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                dateStr = String.valueOf(receiverTransferItemVO.getNewExpiry());
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED))).booleanValue()) {
                p2pTransferVO.setCalminusBonusvalue(receiverTransferItemVO.getTransferValue() - p2pTransferVO.getBonusTalkTimeValue());
            }
            // added by vikas kumar for card group updation sms/mms
            messageArgArray = new String[] { transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(receiverTransferItemVO
                .getTransferValue()), PretupsBL.getDisplayAmount(receiverTransferItemVO.getPostBalance()), dateStr, senderMSISDN, PretupsBL.getDisplayAmount(p2pTransferVO
                .getReceiverAccessFee()), p2pTransferVO.getSubService(), String.valueOf(p2pTransferVO.getReceiverBonus1()), String.valueOf(p2pTransferVO.getReceiverBonus2()), PretupsBL
                .getDisplayAmount(p2pTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(p2pTransferVO.getCalminusBonusvalue()), String.valueOf(p2pTransferVO
                .getReceiverBonus1Validity()), String.valueOf(p2pTransferVO.getReceiverBonus2Validity()), String.valueOf(p2pTransferVO.getReceiverCreditBonusValidity()) };
            if (p2pTransferVO.getBonusTalkTimeValue() == 0) {
                key = PretupsErrorCodesI.P2P_RECEIVER_SUCCESS;// return
                // BTSLUtil.getMessage(receiverLocale,PretupsErrorCodesI.P2P_RECEIVER_SUCCESS,messageArgArray);
            } else {
                key = PretupsErrorCodesI.P2P_RECEIVER_SUCCESS_WITH_BONUS;
            }

        } else {
            // 601:Transaction number {0} to transfer {1} INR from {3} is
            // successful. Transferred value is {2} & access fee is {4} INR.
            // Please check your balance.
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_BONUS_REQUIRED))).booleanValue()) {
                p2pTransferVO.setCalminusBonusvalue(receiverTransferItemVO.getTransferValue() - p2pTransferVO.getBonusTalkTimeValue());
            }
            // added by vikas kumar fro card group updation
            messageArgArray = new String[] { transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(receiverTransferItemVO
                .getTransferValue()), senderMSISDN, PretupsBL.getDisplayAmount(p2pTransferVO.getReceiverAccessFee()), p2pTransferVO.getSubService(), String
                .valueOf(p2pTransferVO.getReceiverBonus1()), String.valueOf(p2pTransferVO.getReceiverBonus2()), PretupsBL.getDisplayAmount(p2pTransferVO
                .getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(p2pTransferVO.getCalminusBonusvalue()), String.valueOf(p2pTransferVO.getReceiverBonus1Validity()), String
                .valueOf(p2pTransferVO.getReceiverBonus2Validity()), String.valueOf(p2pTransferVO.getReceiverCreditBonusValidity()) };
            if (p2pTransferVO.getBonusTalkTimeValue() == 0) {
                key = PretupsErrorCodesI.P2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL;// return
                // BTSLUtil.getMessage(receiverLocale,PretupsErrorCodesI.P2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
            } else {
                key = PretupsErrorCodesI.P2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS;
            }
        }
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC))).booleanValue()) {
            String message = null;
            try {
                message = BTSLUtil.getMessage(receiverLocale, key + "_" + receiverTransferItemVO.getServiceClass(), messageArgArray);
                if (BTSLUtil.isNullString(message)) {
                    message = BTSLUtil.getMessage(receiverLocale, key, messageArgArray);
                }
                return message;
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                return BTSLUtil.getMessage(receiverLocale, key, messageArgArray);
            }
        }
        return BTSLUtil.getMessage(receiverLocale, key, messageArgArray);
    }

    /**
     * Populates the Sender Transfer Items VO
     * 
     */
    private void setSenderTransferItemVO() {
        senderTransferItemVO = new TransferItemVO();
        // set sender transfer item details
        senderTransferItemVO.setSNo(1);
        senderTransferItemVO.setMsisdn(senderMSISDN);
        senderTransferItemVO.setRequestValue(p2pTransferVO.getTransferValue());
        senderTransferItemVO.setSubscriberType(senderSubscriberType);
        senderTransferItemVO.setTransferDate(currentDate);
        senderTransferItemVO.setTransferDateTime(currentDate);
        senderTransferItemVO.setTransferID(p2pTransferVO.getTransferID());
        senderTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
        senderTransferItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
        senderTransferItemVO.setEntryDate(currentDate);
        senderTransferItemVO.setEntryDateTime(currentDate);
        senderTransferItemVO.setEntryType(PretupsI.DEBIT);
        senderTransferItemVO.setPrefixID(senderVO.getPrefixID());
    }

    /**
     * Populates the Receiver Transfer Items VO
     * 
     */
    private void setReceiverTransferItemVO() {
        receiverTransferItemVO = new TransferItemVO();
        receiverTransferItemVO.setSNo(2);
        receiverTransferItemVO.setMsisdn(receiverMSISDN);
        receiverTransferItemVO.setRequestValue(p2pTransferVO.getTransferValue());
        receiverTransferItemVO.setSubscriberType(type);
        receiverTransferItemVO.setTransferDate(currentDate);
        receiverTransferItemVO.setTransferDateTime(currentDate);
        receiverTransferItemVO.setTransferID(p2pTransferVO.getTransferID());
        receiverTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
        receiverTransferItemVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
        receiverTransferItemVO.setEntryDate(currentDate);
        receiverTransferItemVO.setEntryType(PretupsI.CREDIT);
        receiverTransferItemVO.setPrefixID(receiverVO.getPrefixID());
        receiverTransferItemVO.setEntryDateTime(currentDate);
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
        final String senderNetworkCode = senderVO.getNetworkCode();
        String receiverNetworkCode = receiverVO.getNetworkCode();
        final long senderPrefixID = senderVO.getPrefixID();
        long receiverPrefixID = receiverVO.getPrefixID();
        boolean isSenderFound = false;
        boolean isReceiverFound = false;
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                this,
                "Getting interface details For Action=" + action + " senderInterfaceInfoInDBFound=" + senderInterfaceInfoInDBFound + " receiverInterfaceInfoInDBFound=" + receiverInterfaceInfoInDBFound);
            // Avoid searching in the loop again if in validation details was
            // found in database
            // This condition has been changed so that if payment method is not
            // the dafult one then there may be case that default interface will
            // be used for that.
        }

        if (((!senderInterfaceInfoInDBFound && (p2pTransferVO.getPaymentMethodKeywordVO() == null || !PretupsI.YES.equals(p2pTransferVO.getPaymentMethodKeywordVO()
            .getUseDefaultInterface()))) && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
            .equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
            if (p2pTransferVO.getPaymentMethodKeywordVO() != null && PretupsI.YES.equals(p2pTransferVO.getPaymentMethodKeywordVO().getUseDefaultInterface())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(this, "For Sender using the Payment Method Default Interface as=" + p2pTransferVO.getPaymentMethodKeywordVO().getDefaultInterfaceID());
                }
                senderTransferItemVO.setPrefixID(senderPrefixID);
                senderTransferItemVO.setInterfaceID(p2pTransferVO.getPaymentMethodKeywordVO().getDefaultInterfaceID());
                senderTransferItemVO.setInterfaceHandlerClass(p2pTransferVO.getPaymentMethodKeywordVO().getHandlerClass());
                senderAllServiceClassID = p2pTransferVO.getPaymentMethodKeywordVO().getAllServiceClassId();
                senderExternalID = p2pTransferVO.getPaymentMethodKeywordVO().getExternalID();
                senderInterfaceStatusType = p2pTransferVO.getPaymentMethodKeywordVO().getStatusType();
                p2pTransferVO.setSenderAllServiceClassID(senderAllServiceClassID);
                senderTransferItemVO.setInterfaceType(p2pTransferVO.getPaymentMethodType());
                p2pTransferVO.setSenderInterfaceStatusType(senderInterfaceStatusType);
                if (!PretupsI.YES.equals(p2pTransferVO.getPaymentMethodKeywordVO().getStatus()) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(senderInterfaceStatusType)) {
                    if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(senderLocale)).getMessage())) {
                        p2pTransferVO.setSenderReturnMessage(p2pTransferVO.getPaymentMethodKeywordVO().getLang1Message());
                    } else {
                        p2pTransferVO.setSenderReturnMessage(p2pTransferVO.getPaymentMethodKeywordVO().getLang2Message());
                    }
                    throw new BTSLBaseException(this, "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
                }
                isSenderFound = true;
            } else {
                isSenderFound = getInterfaceRoutingDetails(p_con, senderMSISDN, senderPrefixID, senderVO.getSubscriberType(), senderNetworkCode, p2pTransferVO
                    .getServiceType(), p2pTransferVO.getPaymentMethodType(), PretupsI.USER_TYPE_SENDER, action);
            }
        } else {
            isSenderFound = true;
        }
        if (!isSenderFound) {
            if (!senderVO.isDefUserRegistration()) {
                throw new BTSLBaseException("VchrConsController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL, 0,
                    new String[] { ((LookupsVO) LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE, senderVO.getSubscriberType())).getLookupName() }, null);
            }
            throw new BTSLBaseException("VchrConsController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_NOTFOUND_PAYMENTINTERFACEMAPPING);
        }

        // Avoid searching in the loop again if in validation details was found
        // in database
        if ((!receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action
            .equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
            isReceiverFound = getInterfaceRoutingDetails(p_con, receiverMSISDN, receiverPrefixID, type, receiverNetworkCode, p2pTransferVO.getServiceType(), type,
                PretupsI.USER_TYPE_RECEIVER, action);
            // If receiver Not found and we need to perform the alternate
            // category routing before IN Validation and it has not been
            // performed before then do Category Routing
            if (action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION) && !isReceiverFound && performIntfceCatRoutingBeforeVal && useAlternateCategory && !interfaceCatRoutingDone) {
                // Get the alternate interface category and check whether it is
                // valid in that category.
                LOG.info(this,
                    "********* Performing ALTERNATE INTERFACE CATEGORY routing for receiver before IN Validations on Interface=" + newInterfaceCategory + " *********");

                type = newInterfaceCategory;
                interfaceCatRoutingDone = true;

                requestVO.setReqSelector(newDefaultSelector);
                p2pTransferVO.setSubService(newDefaultSelector);

                // Load the new prefix ID against the interface category , If
                // Not required then give the error

                networkPrefixVO = null;
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(receiverVO.getMsisdnPrefix(), type);
                if (networkPrefixVO != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(this, "Getting Reeciver Prefix ID for MSISDN=" + receiverMSISDN + " as " + networkPrefixVO.getPrefixID());
                    }
                    receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                    receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
                    receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
                    receiverNetworkCode = receiverVO.getNetworkCode();
                    receiverPrefixID = receiverVO.getPrefixID();
                    isReceiverFound = getInterfaceRoutingDetails(p_con, receiverMSISDN, receiverPrefixID, type, receiverNetworkCode, p2pTransferVO.getServiceType(), type,
                        PretupsI.USER_TYPE_RECEIVER, action);
                } else {
                    LOG.error(this, "Series Not Defined for Alternate Interface =" + type + " For Series=" + receiverVO.getMsisdnPrefix());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                        "VchrConsController[populateServicePaymentInterfaceDetails]", "", "", "",
                        "Series =" + receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + type + " But alternate Category Routing was required on interface");
                    isReceiverFound = false;
                }
            }
        } else {
            isReceiverFound = true;
        }
        if (!isReceiverFound) {
            throw new BTSLBaseException("VchrConsController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_NOTFOUND_SERVICEINTERFACEMAPPING);
        }
    }

    /**
     * Get the sender String to be send to common Client
     * 
     * @return
     */
    private String getSenderCommonString() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer("MSISDN=" + senderMSISDN);
        strBuff.append("&TRANSACTION_ID=" + transferID);
        strBuff.append("&NETWORK_CODE=" + senderVO.getNetworkCode());
        strBuff.append("&INTERFACE_ID=" + senderTransferItemVO.getInterfaceID());
        strBuff.append("&INTERFACE_HANDLER=" + senderTransferItemVO.getInterfaceHandlerClass());
        strBuff.append("&INT_MOD_COMM_TYPE=" + intModCommunicationTypeS);
        strBuff.append("&INT_MOD_IP=" + intModIPS);
        strBuff.append("&INT_MOD_PORT=" + intModPortS);
        strBuff.append("&INT_MOD_CLASSNAME=" + intModClassNameS);
        strBuff.append("&MODULE=" + PretupsI.P2P_MODULE);
        strBuff.append("&USER_TYPE=S");
        // added for CRE_INT_CR00029 by ankit Zindal
        strBuff.append("&CARD_GROUP_SELECTOR=" + requestVO.getReqSelector());
        strBuff.append("&REQ_SERVICE=" + serviceType);
        strBuff.append("&INT_ST_TYPE=" + p2pTransferVO.getSenderInterfaceStatusType());
        strBuff.append("&RECEIVER_MSISDN=" + receiverMSISDN);
        strBuff.append("&REQ_AMOUNT=" + p2pTransferVO.getRequestedAmount());
        // Added By Babu Kunwar
        strBuff.append("&SELECTOR_BUNDLE_ID=" + p2pTransferVO.getSelectorBundleId());
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
        strBuff.append("&SERVICE_CLASS=" + senderTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + senderTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + senderTransferItemVO.getAccountStatus());
        strBuff.append("&CREDIT_LIMIT=" + senderTransferItemVO.getPreviousBalance());
        strBuff.append("&SERVICE_TYPE=" + senderSubscriberType + "-" + type);
        return strBuff.toString();
    }

    /**
     * Get the sender Debit Request String
     * 
     * @return
     */
    public String getSenderDebitAdjustStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getSenderCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_DEBIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + senderTransferItemVO.getTransferValue());
        strBuff.append("&GRACE_DAYS=" + senderTransferItemVO.getGraceDaysStr());
        strBuff.append("&CARD_GROUP=" + p2pTransferVO.getCardGroupCode());
        strBuff.append("&SERVICE_CLASS=" + senderTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + senderTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + senderTransferItemVO.getAccountStatus());
        strBuff.append("&SOURCE_TYPE=" + p2pTransferVO.getSourceType());
        strBuff.append("&PRODUCT_CODE=" + p2pTransferVO.getProductCode());
        strBuff.append("&TAX_AMOUNT=" + (p2pTransferVO.getSenderTax1Value() + p2pTransferVO.getSenderTax2Value()));
        strBuff.append("&ACCESS_FEE=" + p2pTransferVO.getSenderAccessFee());
        strBuff.append("&SENDER_MSISDN=" + senderMSISDN);
        strBuff.append("&RECEIVER_MSISDN=" + receiverMSISDN);
        strBuff.append("&EXTERNAL_ID=" + senderExternalID);
        strBuff.append("&GATEWAY_CODE=" + requestVO.getRequestGatewayCode());
        strBuff.append("&GATEWAY_TYPE=" + requestVO.getRequestGatewayType());
        strBuff.append("&IMSI=" + BTSLUtil.NullToString(senderIMSI));
        strBuff.append("&SENDER_ID=" + ((SenderVO) requestVO.getSenderVO()).getUserID());
        strBuff.append("&SERVICE_TYPE=" + senderSubscriberType + "-" + type);
        strBuff.append("&ADJUST=Y");
        strBuff.append("&INTERFACE_PREV_BALANCE=" + senderTransferItemVO.getPreviousBalance());
        // Avinash send the requested amount to IN. to use card group only for
        // reporting purpose.
        strBuff.append("&REQUESTED_AMOUNT=" + p2pTransferVO.getRequestedAmount());
        // Aircel Chennai::SelfTopUp:ASHISH S
        strBuff.append("&BANK_PIN=" + ((SenderVO) requestVO.getSenderVO()).getPin());
        strBuff.append("&TAS_ORIGIN_ST_CODE=" + p2pTransferVO.getPaymentMethodType());
        strBuff.append("&CAL_OLD_EXPIRY_DATE=" + senderTransferItemVO.getOldExporyInMillis());// @nu
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
        return strBuff.toString();
    }

    /**
     * Get the Receiver Request String to be send to common Client
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
        strBuff.append("&MODULE=" + PretupsI.P2P_MODULE);
        strBuff.append("&USER_TYPE=R");
        // added for CRE_INT_CR00029 by ankit Zindal
        strBuff.append("&CARD_GROUP_SELECTOR=" + requestVO.getReqSelector());
        strBuff.append("&REQ_SERVICE=" + serviceType);
        strBuff.append("&INT_ST_TYPE=" + p2pTransferVO.getReceiverInterfaceStatusType());
        // Added By Babu Kunwar
        strBuff.append("&SELECTOR_BUNDLE_ID=" + p2pTransferVO.getSelectorBundleId());
        try {
            strBuff.append("&TRANSFER_DATE=" + BTSLUtil.getDateTimeStringFromDate(p2pTransferVO.getTransferDate(), PretupsI.TIMESTAMP_DATESPACEHHMM));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        // strBuff.append("&TRANSFER_DATE="+
        // BTSLUtil.getTimestampFromUtilDate(new Date()));
        strBuff.append("&VOUCHER_CODE=" + p2pTransferVO.getVoucherCode());
        return strBuff.toString();
    }

    /**
     * Gets the receiver validate Request String
     * 
     * @return
     */
    public String getReceiverValidateStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getReceiverCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
        strBuff.append("&SERVICE_CLASS=" + receiverTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + receiverTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + receiverTransferItemVO.getAccountStatus());
        strBuff.append("&CREDIT_LIMIT=" + receiverTransferItemVO.getPreviousBalance());
        strBuff.append("&SERVICE_TYPE=" + senderSubscriberType + "-" + type);
        return strBuff.toString();
    }

    /**
     * Gets the sender Credit Request String
     * 
     * @return
     */
    public String getReceiverCreditStr() {
        final String methodName = "getReceiverCreditStr";
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getReceiverCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_CREDIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + receiverTransferItemVO.getTransferValue());
        strBuff.append("&SERVICE_CLASS=" + receiverTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + receiverTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + receiverTransferItemVO.getAccountStatus());
        strBuff.append("&GRACE_DAYS=" + receiverTransferItemVO.getGraceDaysStr());
        strBuff.append("&CARD_GROUP=" + p2pTransferVO.getCardGroupCode());
        strBuff.append("&MIN_CARD_GROUP_AMT=" + p2pTransferVO.getMinCardGroupAmount());
        strBuff.append("&VALIDITY_DAYS=" + receiverTransferItemVO.getValidity());
        strBuff.append("&BONUS_VALIDITY_DAYS=" + p2pTransferVO.getReceiverBonusValidity());
        strBuff.append("&BONUS_AMOUNT=" + p2pTransferVO.getReceiverBonusValue());
        strBuff.append("&SOURCE_TYPE=" + p2pTransferVO.getSourceType());
        strBuff.append("&SERIAL_NUMBER=" + vomsVO.getSerialNo());
        strBuff.append("&UPDATE_STATUS=" + VOMSI.VOUCHER_USED);
        strBuff.append("&PREVIOUS_STATUS=" + vomsVO.getCurrentStatus());
        strBuff.append("&PRODUCT_CODE=" + p2pTransferVO.getProductCode());
        strBuff.append("&TAX_AMOUNT=" + (p2pTransferVO.getReceiverTax1Value() + p2pTransferVO.getReceiverTax2Value()));
        strBuff.append("&ACCESS_FEE=" + p2pTransferVO.getReceiverAccessFee());
        strBuff.append("&SENDER_MSISDN=" + senderMSISDN);
        strBuff.append("&RECEIVER_MSISDN=" + receiverMSISDN);
        strBuff.append("&EXTERNAL_ID=" + receiverExternalID);
        strBuff.append("&GATEWAY_CODE=" + requestVO.getRequestGatewayCode());
        strBuff.append("&GATEWAY_TYPE=" + requestVO.getRequestGatewayType());
        strBuff.append("&IMSI=" + BTSLUtil.NullToString(receiverIMSI));
        strBuff.append("&SENDER_ID=" + ((SenderVO) requestVO.getSenderVO()).getUserID());
        strBuff.append("&SERVICE_TYPE=" + senderSubscriberType + "-" + type);
        if (String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE).equals(requestVO.getReqSelector())) {
            strBuff.append("&ADJUST=Y");
            strBuff.append("&CAL_OLD_EXPIRY_DATE=" + receiverTransferItemVO.getOldExporyInMillis());// /@nu
        }
        try {
            strBuff.append("&OLD_EXPIRY_DATE=" + BTSLUtil.getDateStringFromDate(receiverTransferItemVO.getPreviousExpiry()));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        try {
            strBuff.append("&OLD_GRACE_DATE=" + BTSLUtil.getDateStringFromDate(receiverTransferItemVO.getPreviousGraceDate()));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        strBuff.append("&INTERFACE_PREV_BALANCE=" + receiverTransferItemVO.getPreviousBalance());
        // Avinash send the requested amount to IN. to use card group only for
        // reporting purpose.
        strBuff.append("&REQUESTED_AMOUNT=" + p2pTransferVO.getRequestedAmount());
        // For Get NUMBER BACK Service
        if (receiverTransferItemVO.isNumberBackAllowed()) {
            final String numbck_diff_to_in = p2pTransferVO.getServiceType() + PreferenceI.NUMBCK_DIFF_REQ_TO_IN;
            final Boolean NBR_BK_SEP_REQ = (Boolean) PreferenceCache.getControlPreference(numbck_diff_to_in, p2pTransferVO.getNetworkCode(), receiverTransferItemVO
                .getInterfaceID());
            strBuff.append("&NBR_BK_DIFF_REQ=" + NBR_BK_SEP_REQ);
        }
        // Added by Zafar Abbas on 13/02/2008 after adding two new fields for
        // Bonus SMS/MMS in Card group
        strBuff.append("&BONUS1=" + p2pTransferVO.getReceiverBonus1());
        strBuff.append("&BONUS2=" + p2pTransferVO.getReceiverBonus2());
        strBuff.append("&BUNDLE_TYPES=" + receiverTransferItemVO.getBundleTypes());
        strBuff.append("&BONUS_BUNDLE_VALIDITIES=" + receiverTransferItemVO.getBonusBundleValidities());

        // added by vikask for card group updation field

        strBuff.append("&BONUS1_VAL=" + p2pTransferVO.getReceiverBonus1Validity());
        strBuff.append("&BONUS2_VAL=" + p2pTransferVO.getReceiverBonus2Validity());
        strBuff.append("&CREDIT_BONUS_VAL=" + p2pTransferVO.getReceiverCreditBonusValidity());

        // added by amit for card group offline field
        strBuff.append("&COMBINED_RECHARGE=" + p2pTransferVO.getBoth());
        strBuff.append("&EXPLICIT_RECHARGE=" + p2pTransferVO.getOnline());
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
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");
        ArrayList altList = null;
        boolean isRequired = false;

        // added to log the IN validation request sent and request received
        // time. Start 12/02/2008
        if (null != map.get("IN_START_TIME")) {
            requestVO.setValidationSenderRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            requestVO.setValidationSenderResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
            // end 12/02/2008
        }

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, senderTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        /*
         * 
         * LDCC handling (Case 1 : Subscriber not found)
         * 1. first making isLDCCTest to true .
         * 2. if it is true then it will run without any effect ,means no LDCC
         * feature.
         * 3. once we started handling for ldcc request then we make it false as
         * if request got any other response
         * than subscriber not found then in handleldcc method all values will
         * be set so no need to go to below
         * if
         * statement.{if((!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals
         * (status) && isLDCCTest)|| isRequired)}
         * 4. if MSISDN not found after ldcchandle then we will do routing for
         * ailternate TYPE IN and in that also
         * if MSISDN not found then we will mark isRequired=true so that it will
         * go to the below if statement
         * {if((!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)
         * && isLDCCTest)|| isRequired)}
         * LDCC Handling (Case 2 : Subscriber INACTIVE or ReqAmount <
         * InterfacePrevAmount and subscriber is LDCC subscriber)
         * 1.In this case we test the subscriber for LDCC in IN Handler if it is
         * LDCC then we set Transaction status
         * as MSISDN not found and Service class to LDCC .
         * 2.Then it will be handled in controller by the same below handling .
         * 3.After all validation and routing if it still gives MSISDN not found
         * means subscriber is not LDCC
         * so we make transactionj status as success and the flow will be usual
         * in which it will say that
         * subscriber is inactive or requested amount should be more.
         */
        boolean isLDCCTest = true;
        final boolean ldccHandle = operatorUtil.handleLDCCRequest();
        // If we get the MSISDN not found on interface error then perform
        // interface routing
        final String methodName = "updateForSenderValidateResponse";
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
            if (!ldccHandle) {
                isRoutingSecond = true;
            }
            senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(senderTransferItemVO.getInterfaceID());
            try {
                if (map.get("SERVICE_CLASS").equals(PretupsI.SERVICE_CLASS_LDCC)) {
                    throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                } else {
                    if (altList != null && altList.size() > 0) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, "Got Status=" + status + " After validation Request For MSISDN=" + senderMSISDN + " Performing Alternate Routing");
                        }
                        performSenderAlternateRouting(altList); // Method to
                        // perform the
                        // sender
                        // interface
                        // routing for
                        // validation
                    } else {
                        isRequired = true;
                        throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    }
                }
            } catch (BTSLBaseException e) {
                // TODO Auto-generated catch block
                LOG.errorTrace(methodName, e);
                if (!ldccHandle) {
                    throw e;
                } else {
                    status = e.getMessage();
                    if (ldccHandle && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
                        try {
                            handleLDCCRequest();
                        } catch (BTSLBaseException be) {
                            LOG.errorTrace(methodName, be);
                            status = be.getMessage();
                        }
                        if (!BTSLUtil.isNullString(senderTransferItemVO.getValidationStatus())) {
                            status = senderTransferItemVO.getValidationStatus();
                        }
                        isRequired = false;
                        isLDCCTest = false;
                        isRoutingSecond = true;
                        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
                            senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                            altList = InterfaceRoutingControlCache.getRoutingControlDetails(senderTransferItemVO.getInterfaceID());
                            // routing of Ailternate type IN .
                            if (altList != null && altList.size() > 0) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(methodName, "Got Status=" + status + " After validation Request For MSISDN=" + senderMSISDN + " Performing Alternate Routing");
                                }
                                performSenderAlternateRouting(altList); // Method
                                // to
                                // perform
                                // the
                                // sender
                                // interface
                                // routing
                                // for
                                // validation
                                if (InterfaceErrorCodesI.SUCCESS.equals(senderTransferItemVO.getValidationStatus())) {
                                    senderVO.setSubscriberType(serviceInterfaceRoutingVO.getAlternateInterfaceType());
                                    p2pTransferVO.setTransferCategory(serviceInterfaceRoutingVO.getAlternateInterfaceType() + "-" + type);
                                    p2pTransferVO.setPaymentMethodType(serviceInterfaceRoutingVO.getAlternateInterfaceType());
                                    isUpdateRequired = true;
                                }
                            } else {
                                isLDCCTest = true;
                                isRequired = true;
                            }
                        }
                    }

                    if (map.get("SERVICE_CLASS").equals(PretupsI.SERVICE_CLASS_LDCC)) {
                        // if service class is ldcc then subscriber type will
                        // not be updated . it will remain as PRE type.
                        isUpdateRequired = false;
                        isSenderRoutingUpdate = false;
                        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(senderTransferItemVO.getValidationStatus())) {
                            // if not found on other IN but subscriber is
                            // basically INACTIVE or balance is not enough.
                            senderTransferItemVO.setValidationStatus(InterfaceErrorCodesI.SUCCESS);
                            status = InterfaceErrorCodesI.SUCCESS;
                            isRequired = true;
                        }
                    }
                }
            }

        }
        if ((!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && isLDCCTest) || isRequired) {
            senderTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
            senderTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
            senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
            senderTransferItemVO.setValidationStatus(status);
            senderVO.setInterfaceResponseCode(senderTransferItemVO.getInterfaceResponseCode());

            if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
                try {
                    senderTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
            }
            senderTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

            String[] strArr = null;
            if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                p2pTransferVO.setErrorCode(status + "_S");
                senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                receiverVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                senderTransferItemVO.setTransferStatus(status);
                receiverTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                strArr = new String[] { receiverMSISDN, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), transferID };
                // throw new
                // BTSLBaseException("PretupsBL","updateForReceiverValidateResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
                throw new BTSLBaseException("VchrConsController", methodName, p2pTransferVO.getErrorCode(), 0, strArr, null);
            }
            senderTransferItemVO.setTransferStatus(status);
            senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
                .getRoutingControlDetails(p2pTransferVO.getNetworkCode() + "_" + p2pTransferVO.getServiceType() + "_" + p2pTransferVO.getPaymentMethodType());
            if ((PretupsI.INTERFACE_CATEGORY_PRE.equals(senderVO.getSubscriberType()) || ldccHandle) && !senderDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO
                .isDatabaseCheckBool()) {
                PretupsBL.insertSubscriberInterfaceRouting(senderTransferItemVO.getInterfaceID(), senderExternalID, senderMSISDN, p2pTransferVO.getPaymentMethodType(),
                    senderVO.getUserID(), currentDate);
                senderInterfaceInfoInDBFound = true;
                senderDeletionReqFromSubRouting = true;
            }
            try {
                senderTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                senderTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            senderTransferItemVO.setOldExporyInMillis((String) map.get("CAL_OLD_EXPIRY_DATE"));// @nu
            senderTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));

            try {
                senderTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                senderTransferItemVO.setBalanceCheckReq(false);
            }
            senderVO.setCreditLimit(senderTransferItemVO.getPreviousBalance());

            // Update the Previous Balance in case of Post Paid Offline
            // interface with Credit Limit - Monthly Transfer Amount
            if (senderVO.isPostOfflineInterface()) {
                final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(senderVO.getLastSuccessTransferDate(), currentDate, BTSLUtil.PERIOD_MONTH);
                if (!isPeriodChange) {
                    senderTransferItemVO.setPreviousBalance(senderTransferItemVO.getPreviousBalance() - senderVO.getMonthlyTransferAmount());
                }
            }
            if (PretupsI.INTERFACE_CATEGORY_POST.equals(senderVO.getSubscriberType())) {

                final long balance = Long.parseLong((String) map.get("BILL_AMOUNT_BAL"));
                final long credit_limit = Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE"));
                senderVO.setCreditLimit(credit_limit - balance);
                senderTransferItemVO.setPreviousBalance(senderTransferItemVO.getPreviousBalance() - senderVO.getMonthlyTransferAmount());
            }
            senderTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
            senderTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));
            // added for service provider information:

            senderTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
        }
    }

    /**
     * Method to handle Sender Debit Response
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForSenderDebitResponse(String str) throws BTSLBaseException {
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");

        // added to log the IN validation request sent and request received
        // time. Start 12/02/2008
        if (null != map.get("IN_START_TIME")) {
            requestVO.setTopUPSenderRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            requestVO.setTopUPSenderResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
            // end 12/02/2008
        }

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        senderTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        senderTransferItemVO.setUpdateStatus(status);
        senderVO.setInterfaceResponseCode(senderTransferItemVO.getInterfaceResponseCode());
        senderPostBalanceAvailable = ((String) map.get("POST_BALANCE_ENQ_SUCCESS"));

        final String methodName = "updateForSenderDebitResponse";
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
            try {
                senderTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }

        senderTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

        String[] strArr = null;

        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            p2pTransferVO.setErrorCode(status + "_S");
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            senderTransferItemVO.setTransferStatus(status);
            receiverTransferItemVO.setUpdateStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { receiverMSISDN, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), transferID };
            // throw new
            // BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
            throw new BTSLBaseException(this, methodName, p2pTransferVO.getErrorCode(), 0, strArr, null);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
            p2pTransferVO.setErrorCode(status + "_S");
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            senderTransferItemVO.setTransferStatus(status);
            senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            senderTransferItemVO.setUpdateStatus(status);
            receiverTransferItemVO.setUpdateStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { transferID, receiverTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        } else {
            senderTransferItemVO.setTransferStatus(status);
            senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            senderTransferItemVO.setUpdateStatus(status);
        }

        /*
         * try{senderTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString((
         * String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy"));}catch(Exception e){};
         * try{senderTransferItemVO.setNewGraceDate(BTSLUtil.getDateFromDateString
         * ((String)map.get("NEW_GRACE_DATE"),"ddMMyyyy"));}catch(Exception
         * e){};
         * try{senderTransferItemVO.setPostBalance(Long.parseLong((String)map.get
         * ("INTERFACE_POST_BALANCE")));}catch(Exception e){};
         */
        // @nu
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
            try {
                senderTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            ;
            // try{senderTransferItemVO.setNewGraceDate(BTSLUtil.getDateFromDateString((String)map.get("NEW_GRACE_DATE"),"ddMMyyyy"));}catch(Exception
            // e){LOG.errorTrace(methodName,e);};
            try {
                senderTransferItemVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            ;
            try {
                senderTransferItemVO.setPostValidationStatus((String) map.get("POSTCRE_TRANSACTION_STATUS"));
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
    }

    /**
     * Method to handle receiver validation response
     * This method will perform the Alternate interface routing is mobile is not
     * found on the interface
     * If not found on any interface then perform the alternate category routing
     * if that is not done
     * Earlier.
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForReceiverValidateResponse(String str) throws BTSLBaseException {
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        ArrayList altList = null;
        boolean isRequired = false;

        // added to log the IN validation request sent and request received
        // time. Start 12/02/2008
        if (null != map.get("IN_START_TIME")) {
            requestVO.setValidationReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            requestVO.setValidationReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
        }
        // end 12/02/2008
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
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(receiverTransferItemVO.getInterfaceID());
            if (altList != null && altList.size() > 0) {
                performReceiverAlternateRouting(altList, SRC_BEFORE_INRESP_CAT_ROUTING);
            } else {
                if (useAlternateCategory && !performIntfceCatRoutingBeforeVal && !interfaceCatRoutingDone) {
                    performAlternateCategoryRouting();
                } else {
                    isRequired = true;
                }
            }
        }
        if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
            populateReceiverItemsDetails(map);
            // For Service Provider Information
            receiverTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
        }
    }

    /**
     * Method to handle receiver credit response
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForReceiverCreditResponse(String str) throws BTSLBaseException {
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");

        // added to log the IN validation request sent and request received
        // time. Start 12/02/2008
        if (null != map.get("IN_START_TIME")) {
            requestVO.setTopUPReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            requestVO.setTopUPReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
            // end 12/02/2008
        }

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        // Done so that in Credit Back IN module does not activate the IN as
        // else it would receive M from here
        if (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) && receiverTransferItemVO.getInterfaceID().equals(senderTransferItemVO.getInterfaceID())) {
            p2pTransferVO.setSenderInterfaceStatusType(InterfaceCloserI.INTERFACE_AUTO_ACTIVE);
        }
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, receiverTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        String updateStatus = (String) map.get("UPDATE_STATUS");

        if (BTSLUtil.isNullString(updateStatus)) {
            updateStatus = status;
        }

        receiverTransferItemVO.setUpdateStatus(status);
        receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());

        receiverTransferItemVO.setUpdateStatus1((String) map.get("UPDATE_STATUS1"));
        receiverTransferItemVO.setUpdateStatus2((String) map.get("UPDATE_STATUS2"));

        if (!BTSLUtil.isNullString((String) map.get("ADJUST_AMOUNT"))) {
            receiverTransferItemVO.setAdjustValue(Long.parseLong((String) map.get("ADJUST_AMOUNT")));
        }

        receiverPostBalanceAvailable = ((String) map.get("POST_BALANCE_ENQ_SUCCESS"));

        // set from IN Module
        final String methodName = "updateForReceiverCreditResponse";
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

        String[] strArr = null;
        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            p2pTransferVO.setErrorCode(status + "_R");
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            receiverTransferItemVO.setTransferStatus(status);
            strArr = new String[] { receiverMSISDN, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), transferID };
            // throw new
            // BTSLBaseException(this,"updateForReceiverCreditResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
            throw new BTSLBaseException(this, methodName, p2pTransferVO.getErrorCode(), 0, strArr, null);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
            p2pTransferVO.setErrorCode(status + "_R");
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            receiverTransferItemVO.setTransferStatus(status);
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            receiverTransferItemVO.setUpdateStatus(status);
            strArr = new String[] { transferID, receiverTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        } else {
            receiverTransferItemVO.setTransferStatus(status);
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            receiverTransferItemVO.setUpdateStatus(status);
        }

        /*
         * try{receiverTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString
         * ((String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy"));}catch(Exception
         * e){};
         * try{receiverTransferItemVO.setNewGraceDate(BTSLUtil.getDateFromDateString
         * ((String)map.get("NEW_GRACE_DATE"),"ddMMyyyy"));}catch(Exception
         * e){};
         * try{receiverTransferItemVO.setPostBalance(Long.parseLong((String)map.get
         * ("INTERFACE_POST_BALANCE")));}catch(Exception e){};
         */
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
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
                receiverTransferItemVO.setPostValidationStatus((String) map.get("POSTCRE_TRANSACTION_STATUS"));
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

    }

    /**
     * Method to handle sender credit back response
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForSenderCreditBackResponse(String str) throws BTSLBaseException {
        final String methodName = "updateForSenderCreditBackResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        senderCreditBackStatusVO = new TransferItemVO();
        senderCreditBackStatusVO.setMsisdn(senderTransferItemVO.getMsisdn());
        senderCreditBackStatusVO.setRequestValue(senderTransferItemVO.getRequestValue());
        senderCreditBackStatusVO.setSubscriberType(senderTransferItemVO.getSubscriberType());
        senderCreditBackStatusVO.setTransferDate(senderTransferItemVO.getTransferDate());
        senderCreditBackStatusVO.setTransferDateTime(senderTransferItemVO.getTransferDateTime());
        senderCreditBackStatusVO.setTransferID(senderTransferItemVO.getTransferID());
        senderCreditBackStatusVO.setUserType(senderTransferItemVO.getUserType());
        senderCreditBackStatusVO.setEntryDate(senderTransferItemVO.getEntryDate());
        senderCreditBackStatusVO.setEntryDateTime(senderTransferItemVO.getEntryDateTime());
        senderCreditBackStatusVO.setPrefixID(senderTransferItemVO.getPrefixID());
        senderCreditBackStatusVO.setTransferValue(senderTransferItemVO.getTransferValue());
        senderCreditBackStatusVO.setInterfaceID(senderTransferItemVO.getInterfaceID());
        senderCreditBackStatusVO.setInterfaceType(senderTransferItemVO.getInterfaceType());
        senderCreditBackStatusVO.setServiceClass(senderTransferItemVO.getServiceClass());
        senderCreditBackStatusVO.setServiceClassCode(senderTransferItemVO.getServiceClassCode());
        senderCreditBackStatusVO.setInterfaceHandlerClass(senderTransferItemVO.getInterfaceHandlerClass());

        senderCreditBackStatusVO.setSNo(3);
        senderCreditBackStatusVO.setEntryType(PretupsI.CREDIT);
        senderCreditBackStatusVO.setTransferType(PretupsI.TRANSFER_TYPE_P2P_CREDITBACK);
        senderCreditPostBalanceAvailable = (String) map.get("POST_BALANCE_ENQ_SUCCESS");

        final String status = (String) map.get("TRANSACTION_STATUS");
        senderCreditBackStatusVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        senderCreditBackStatusVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        senderCreditBackStatusVO.setTransferStatus(status);
        senderCreditBackStatusVO.setUpdateStatus(status);
        senderCreditBackStatusVO.setValidationStatus(status);
        p2pTransferVO.setCreditBackStatus(status);

        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
            senderCreditBackStatusVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
        }

        senderCreditBackStatusVO.setReferenceID((String) map.get("IN_RECON_ID"));

        if (BTSLUtil.isNullString(status) || !status.equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
            // Mark the request as Ambigous if not able to credit back the
            // sender
            p2pTransferVO.setErrorCode(status + "_S");
            p2pTransferVO.setCreditBackStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            senderCreditBackStatusVO.setTransferStatus(InterfaceErrorCodesI.AMBIGOUS);
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            throw new BTSLBaseException(status);
        }
        if (!PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS.equals(p2pTransferVO.getTransferStatus())) {
            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
        }

        try {
            senderCreditBackStatusVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        // try{senderCreditBackStatusVO.setPostBalance(Long.parseLong((String)map.get("INTERFACE_POST_BALANCE")));}catch(Exception
        // e){LOG.errorTrace(methodName,e);};
        try {
            senderCreditBackStatusVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        // try{senderCreditBackStatusVO.setNewExpiry(BTSLUtil.getDateFromDateString((String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy"));}catch(Exception
        // e){LOG.errorTrace(methodName,e);};
        // @nu
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ENQ_POSTBAL_ALLOW))).booleanValue()) {
            try {
                senderCreditBackStatusVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            ;
            try {
                senderCreditBackStatusVO.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            ;
            try {
                senderCreditBackStatusVO.setPostValidationStatus((String) map.get("POSTCRE_TRANSACTION_STATUS"));
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
    }

    /**
     * Method to check the various level of loads whether request can be passed
     * or not
     * 
     * @throws BTSLBaseException
     */
    private void checkTransactionLoad() throws BTSLBaseException {
        final String methodName = "checkTransactionLoad";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Checking load for transfer ID=" + transferID);
        }
        try {
            requestVO.setPerformIntfceCatRoutingBeforeVal(performIntfceCatRoutingBeforeVal);
            p2pTransferVO.setRequestVO(requestVO);
            p2pTransferVO.setSenderTransferItemVO(senderTransferItemVO);
            p2pTransferVO.setReceiverTransferItemVO(receiverTransferItemVO);
            requestVO.setReceiverDeletionReqFromSubRouting(receiverDeletionReqFromSubRouting);
            requestVO.setReceiverInterfaceInfoInDBFound(receiverInterfaceInfoInDBFound);
            requestVO.setSenderDeletionReqFromSubRouting(senderDeletionReqFromSubRouting);
            requestVO.setSenderInterfaceInfoInDBFound(senderInterfaceInfoInDBFound);
            requestVO.setInterfaceCatRoutingDone(interfaceCatRoutingDone);

            final int senderLoadStatus = LoadController.checkInterfaceLoad(((SenderVO) p2pTransferVO.getSenderVO()).getNetworkCode(), senderTransferItemVO.getInterfaceID(),
                transferID, p2pTransferVO, true);
            int recieverLoadStatus = 0;
            // Further process the request
            if (senderLoadStatus == 0) {
                recieverLoadStatus = LoadController.checkInterfaceLoad(((ReceiverVO) p2pTransferVO.getReceiverVO()).getNetworkCode(), receiverTransferItemVO.getInterfaceID(),
                    transferID, p2pTransferVO, true);
                if (recieverLoadStatus == 0) {
                    try {
                        LoadController.checkTransactionLoad(((SenderVO) p2pTransferVO.getSenderVO()).getNetworkCode(), senderTransferItemVO.getInterfaceID(),
                            PretupsI.P2P_MODULE, transferID, true, LoadControllerI.USERTYPE_SENDER);
                    } catch (BTSLBaseException e) {
                        // Decreasing interface load of receiver which we had
                        // incremented before 27/09/06, sender was decreased in
                        // the method
                        LoadController.decreaseCurrentInterfaceLoad(transferID, ((ReceiverVO) p2pTransferVO.getReceiverVO()).getNetworkCode(), receiverTransferItemVO
                            .getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                        throw e;
                    }
                    try {
                        LoadController.checkTransactionLoad(((ReceiverVO) p2pTransferVO.getReceiverVO()).getNetworkCode(), receiverTransferItemVO.getInterfaceID(),
                            PretupsI.P2P_MODULE, transferID, true, LoadControllerI.USERTYPE_RECEIVER);
                    } catch (BTSLBaseException e) {
                        // Decreasing interface load of sender which we had
                        // incremented before 27/09/06, receiver was decreased
                        // in the method
                        LoadController.decreaseTransactionInterfaceLoad(transferID, ((SenderVO) p2pTransferVO.getSenderVO()).getNetworkCode(),
                            LoadControllerI.DEC_LAST_TRANS_COUNT);
                        throw e;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("VchrConsController[checkTransactionLoad]", "transferID=" + transferID + " Successfully through load");
                    }
                }
                // Request in Queue
                else if (recieverLoadStatus == 1) {
                    // Decrease the interface counter of the sender that was
                    // increased
                    LoadController.decreaseCurrentInterfaceLoad(transferID, ((SenderVO) p2pTransferVO.getSenderVO()).getNetworkCode(), senderTransferItemVO.getInterfaceID(),
                        LoadControllerI.DEC_LAST_TRANS_COUNT);

                    final String strArr[] = { receiverMSISDN, String.valueOf(p2pTransferVO.getRequestedAmount()) };
                    throw new BTSLBaseException("VchrConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
                }
                // Refuse the request
                else {
                    throw new BTSLBaseException("VchrConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                }
            }
            // Request in Queue
            else if (senderLoadStatus == 1) {
                final String strArr[] = { receiverMSISDN, String.valueOf(p2pTransferVO.getRequestedAmount()) };
                throw new BTSLBaseException("VchrConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("EVDP2PController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            LOG.error("VchrConsController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            LOG.error("VchrConsController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
            throw new BTSLBaseException("VchrConsController", "checkTransactionLoad", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
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
            if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
                final int senderLoadStatus = LoadController.checkInterfaceLoad(((SenderVO) p2pTransferVO.getSenderVO()).getNetworkCode(), p_interfaceID, transferID,
                    p2pTransferVO, true);
                // Further process the request
                if (senderLoadStatus == 0) {
                    recieverLoadStatus = LoadController.checkInterfaceLoad(((ReceiverVO) p2pTransferVO.getReceiverVO()).getNetworkCode(), receiverTransferItemVO
                        .getInterfaceID(), transferID, p2pTransferVO, false);
                    if (recieverLoadStatus == 0) {
                        try {
                            LoadController.checkTransactionLoad(((SenderVO) p2pTransferVO.getSenderVO()).getNetworkCode(), p_interfaceID, PretupsI.P2P_MODULE, transferID,
                                true, LoadControllerI.USERTYPE_SENDER);
                        } catch (BTSLBaseException e) {
                            // Decreasing interface load of receiver which we
                            // had incremented before 27/09/06, sender was
                            // decreased in the method
                            LoadController.decreaseCurrentInterfaceLoad(transferID, ((ReceiverVO) p2pTransferVO.getReceiverVO()).getNetworkCode(), receiverTransferItemVO
                                .getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                            throw e;
                        }
                        try {
                            LoadController.checkTransactionLoad(((ReceiverVO) p2pTransferVO.getReceiverVO()).getNetworkCode(), receiverTransferItemVO.getInterfaceID(),
                                PretupsI.P2P_MODULE, transferID, true, LoadControllerI.USERTYPE_RECEIVER);
                        } catch (BTSLBaseException e) {
                            // Decreasing interface load of sender which we had
                            // incremented before 27/09/06, receiver was
                            // decreased in the method
                            LoadController.decreaseTransactionInterfaceLoad(transferID, ((SenderVO) p2pTransferVO.getSenderVO()).getNetworkCode(),
                                LoadControllerI.DEC_LAST_TRANS_COUNT);
                            throw e;
                        }

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("VchrConsController[checkTransactionLoad]", "transferID=" + transferID + " Successfully through load");
                        }
                    }
                    // Refuse the request
                    else {
                        throw new BTSLBaseException("VchrConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                    }
                }
                // Request in Queue
                else if (senderLoadStatus == 1) {
                    final String strArr[] = { receiverMSISDN, String.valueOf(p2pTransferVO.getRequestedAmount()) };
                    throw new BTSLBaseException("VchrConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
                }
                // Refuse the request
                else {
                    throw new BTSLBaseException("VchrConsController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                }
            } else {
                // Do not enter the request in Queue
                recieverLoadStatus = LoadController.checkInterfaceLoad(((ReceiverVO) p2pTransferVO.getReceiverVO()).getNetworkCode(), p_interfaceID, transferID,
                    p2pTransferVO, false);
                if (recieverLoadStatus == 0) {
                    LoadController.checkTransactionLoad(((ReceiverVO) p2pTransferVO.getReceiverVO()).getNetworkCode(), p_interfaceID, PretupsI.P2P_MODULE, transferID, true,
                        LoadControllerI.USERTYPE_RECEIVER);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("checkTransactionLoad[checkTransactionLoad]", "transferID=" + transferID + " Successfully through load");
                    }
                }
                // Request in Queue
                else if (recieverLoadStatus == 1) {
                    throw new BTSLBaseException("checkTransactionLoad", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                }
                // Refuse the request
                else {
                    throw new BTSLBaseException("checkTransactionLoad", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                }
            }
        } catch (BTSLBaseException be) {
            LOG.error("VchrConsController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            LOG.error("VchrConsController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
            throw new BTSLBaseException("VchrConsController", "checkTransactionLoad", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * This method will be called to process the request from the queue
     * 
     * @param p_transferVO
     */
    public void processFromQueue(TransferVO p_transferVO) {
        final String methodName = "processFromQueue";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            p2pTransferVO = (P2PTransferVO) p_transferVO;
            requestVO = p2pTransferVO.getRequestVO();
            senderVO = (SenderVO) requestVO.getSenderVO();
            receiverVO = (ReceiverVO) p2pTransferVO.getReceiverVO();
            type = requestVO.getType();
            if (type.equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
                serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(receiverVO.getNetworkCode() + "_" + requestVO.getServiceType());
                if (serviceInterfaceRoutingVO != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "process",
                            requestIDStr,
                            "For =" + receiverVO.getNetworkCode() + "_" + requestVO.getServiceType() + " Got Interface Category=" + serviceInterfaceRoutingVO
                                .getInterfaceType() + " Alternate Check Required=" + serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + serviceInterfaceRoutingVO
                                .getAlternateInterfaceType() + " oldDefaultSelector=" + oldDefaultSelector + "newDefaultSelector= " + newDefaultSelector);
                    }

                    type = serviceInterfaceRoutingVO.getInterfaceType();
                    oldInterfaceCategory = type;
                    oldDefaultSelector = serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                    useAlternateCategory = serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    newInterfaceCategory = serviceInterfaceRoutingVO.getAlternateInterfaceType();
                    newDefaultSelector = serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
                } else {
                    LOG.info("process", requestIDStr,
                        "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[process]", "", senderMSISDN,
                        senderNetworkCode, "Service Interface Routing control Not defined, thus using default type=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE)));
                    type = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRSFR_DEF_SRVCTYPE));
                    // oldDefaultSelector=String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_TRANSFER_DEF_SELECTOR_CODE)));
                    // Changed on 27/05/07 for Service Type selector Mapping
                    final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_transferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }

                }
            } else {
                serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                    .getServiceInterfaceRoutingDetails(receiverVO.getNetworkCode() + "_" + requestVO.getServiceType() + "_" + senderVO.getSubscriberType());
                if (serviceInterfaceRoutingVO != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "process",
                            requestIDStr,
                            "For =" + receiverVO.getNetworkCode() + "_" + requestVO.getServiceType() + " Got Interface Category=" + serviceInterfaceRoutingVO
                                .getInterfaceType() + " Alternate Check Required=" + serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + serviceInterfaceRoutingVO
                                .getAlternateInterfaceType() + " oldDefaultSelector=" + oldDefaultSelector + "newDefaultSelector= " + newDefaultSelector);
                    }
                    oldDefaultSelector = serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                } else {
                    // oldDefaultSelector=String.valueOf(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_TRANSFER_DEF_SELECTOR_CODE)));
                    // Changed on 27/05/07 for Service Type selector Mapping
                    final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_transferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                    LOG.info("process", requestIDStr, "Service Interface Routing control Not defined, thus using default Selector=" + oldDefaultSelector);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VchrConsController[process]", "", senderMSISDN,
                        senderNetworkCode, "Service Interface Routing control Not defined, thus using default selector=" + oldDefaultSelector);
                }
            }

            if (BTSLUtil.isNullString(requestVO.getReqSelector())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("process", requestIDStr, "Selector Not found in Incoming Message Thus using Selector as  " + oldDefaultSelector);
                }
                requestVO.setReqSelector(oldDefaultSelector);
            } else {
                newDefaultSelector = requestVO.getReqSelector();
            }

            requestID = requestVO.getRequestID();
            requestIDStr = requestVO.getRequestIDStr();
            receiverLocale = requestVO.getReceiverLocale();
            transferID = p2pTransferVO.getTransferID();
            senderSubscriberType = senderVO.getSubscriberType();
            senderNetworkCode = senderVO.getNetworkCode();
            senderMSISDN = ((SubscriberVO) p2pTransferVO.getSenderVO()).getMsisdn();
            receiverMSISDN = ((SubscriberVO) p2pTransferVO.getReceiverVO()).getMsisdn();
            senderLocale = requestVO.getSenderLocale();
            receiverLocale = requestVO.getReceiverLocale();
            serviceType = requestVO.getServiceType();
            senderTransferItemVO = p2pTransferVO.getSenderTransferItemVO();
            receiverTransferItemVO = p2pTransferVO.getReceiverTransferItemVO();
            performIntfceCatRoutingBeforeVal = requestVO.isPerformIntfceCatRoutingBeforeVal();
            receiverDeletionReqFromSubRouting = requestVO.isReceiverDeletionReqFromSubRouting();
            receiverInterfaceInfoInDBFound = requestVO.isReceiverInterfaceInfoInDBFound();
            senderDeletionReqFromSubRouting = requestVO.isSenderDeletionReqFromSubRouting();
            senderInterfaceInfoInDBFound = requestVO.isSenderInterfaceInfoInDBFound();
            interfaceCatRoutingDone = requestVO.isInterfaceCatRoutingDone();

            try {
                LoadController.checkTransactionLoad(((SubscriberVO) p2pTransferVO.getSenderVO()).getNetworkCode(), senderTransferItemVO.getInterfaceID(), PretupsI.P2P_MODULE,
                    transferID, true, LoadControllerI.USERTYPE_SENDER);
            } catch (BTSLBaseException e) {
                // Decreasing interface load of receiver which we had
                // incremented before 27/09/06, sender was decreased in the
                // method
                LoadController.decreaseCurrentInterfaceLoad(transferID, ((ReceiverVO) p2pTransferVO.getReceiverVO()).getNetworkCode(),
                    receiverTransferItemVO.getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                throw e;
            }

            try {
                LoadController.checkTransactionLoad(((SubscriberVO) p2pTransferVO.getReceiverVO()).getNetworkCode(), receiverTransferItemVO.getInterfaceID(),
                    PretupsI.P2P_MODULE, transferID, true, LoadControllerI.USERTYPE_RECEIVER);
            } catch (BTSLBaseException e) {
                // Decreasing interface load of sender which we had incremented
                // before 27/09/06, receiver was decreased in the method
                LoadController.decreaseTransactionInterfaceLoad(transferID, ((SenderVO) p2pTransferVO.getSenderVO()).getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                throw e;
            }

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // Loading receiver's controll parameters
            PretupsBL.loadRecieverControlLimits(con, requestIDStr, p2pTransferVO);
            receiverVO.setUnmarkRequestStatus(true);
            try {
               mcomCon.finalCommit();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
			if (mcomCon != null) {
				mcomCon.close("VchrConsController#processFromQueue");
				mcomCon = null;
			}
			con = null;

            processedFromQueue = true;

            if (LOG.isDebugEnabled()) {
                LOG.debug("VchrConsController[processFromQueue]", "transferID=" + transferID + " Successfully through load");
            }
            processValidationRequest();
            // Set under process message for the sender and reciever
            p_transferVO.setMessageCode(PretupsErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
            final String[] messageArgArray = { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
            p_transferVO.setMessageArguments(messageArgArray);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
			if (mcomCon != null) {
				mcomCon.close("VchrConsController#processFromQueue");
				mcomCon = null;
			}
            con = null;
            try {
                if (receiverVO != null && receiverVO.isUnmarkRequestStatus()) {
                    mcomCon = new MComConnection();
                    con=mcomCon.getConnection();
                    PretupsBL.unmarkReceiverLastRequest(con, requestIDStr, receiverVO);
                }
            } catch (BTSLBaseException bex) {
                LOG.errorTrace(methodName, bex);
                // p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[processFromQueue]", transferID,
                    senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Exception:" + bex.getMessage());
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[processFromQueue]", transferID,
                    senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Exception:" + e.getMessage());
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

            if (be.isKey()) {
                if (BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
                    p2pTransferVO.setErrorCode(be.getMessageKey());
                }
                p2pTransferVO.setMessageCode(be.getMessageKey());
                p2pTransferVO.setMessageArguments(be.getArgs());
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            TransactionLog.log(transferID, requestIDStr, requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, p2pTransferVO
                .getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + requestVO.getMessageCode());

        } catch (Exception e) {
            LOG.error(methodName, "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
			if (mcomCon != null) {
				mcomCon.close("VchrConsController#processFromQueue");
				mcomCon = null;
			}
            con = null;
            try {
                if (receiverVO != null && receiverVO.isUnmarkRequestStatus()) {
                    mcomCon = new MComConnection();
                    con=mcomCon.getConnection();
                    PretupsBL.unmarkReceiverLastRequest(con, requestIDStr, receiverVO);
                }
            } catch (BTSLBaseException bex) {
                LOG.errorTrace(methodName, bex);
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[processFromQueue]", transferID,
                    senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Exception:" + bex.getMessage());
            } catch (Exception ex1) {
                LOG.errorTrace(methodName, ex1);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[processFromQueue]", transferID,
                    senderMSISDN, senderNetworkCode, "Leaving Reciever Unmarked Exception:" + ex1.getMessage());
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            if (recValidationFailMessageRequired) {
                if (p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (transferID != null) {
                        p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(transferID), PretupsBL
                            .getDisplayAmount(p2pTransferVO.getRequestedAmount()) }));
                    } else {
                        p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(p2pTransferVO
                            .getRequestedAmount()) }));
                    }
                }
            }

            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            p2pTransferVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);

            LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);

            TransactionLog.log(transferID, requestIDStr, requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, p2pTransferVO
                .getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + requestVO.getMessageCode());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[processFromQueue]", transferID,
                senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
        } finally {
            try {
                if (mcomCon == null) {
                    mcomCon = new MComConnection();con=mcomCon.getConnection();
                }
                if (transferID != null && !transferDetailAdded) {
                    addEntryInTransfers(con);
                }
            } catch (BTSLBaseException be) {
                LOG.errorTrace(methodName, be);
                LOG.error(methodName, "BTSL Base Exception:" + be.getMessage());
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                LOG.error(methodName, "Exception:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[processFromQueue]", transferID,
                    senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            }
            if (BTSLUtil.isNullString(p2pTransferVO.getMessageCode())) {
                p2pTransferVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            if (con != null) {
                try {
                    mcomCon.finalCommit();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
				if (mcomCon != null) {
					mcomCon.close("VchrConsController#processFromQueue");
					mcomCon = null;
				}
                con = null;
            }
            if (p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                final BTSLMessages btslRecMessages = (BTSLMessages) p2pTransferVO.getReceiverReturnMsg();
                (new PushMessage(receiverMSISDN, BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), transferID, p2pTransferVO
                    .getRequestGatewayCode(), receiverLocale)).push();
            } else if (p2pTransferVO.getReceiverReturnMsg() != null) {
                (new PushMessage(receiverMSISDN, (String) p2pTransferVO.getReceiverReturnMsg(), transferID, p2pTransferVO.getRequestGatewayCode(), receiverLocale)).push();
            }

            TransactionLog.log(transferID, requestVO.getRequestIDStr(), requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Leaving the controller after Queue Processing", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + requestVO.getMessageCode());
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
    }

    /**
     * Method to populate transfer VO from request VO
     * 
     * @param p_requestVO
     */
    private void populateVOFromRequest(RequestVO p_requestVO) {
        p2pTransferVO.setSenderVO(senderVO);
        p2pTransferVO.setRequestID(p_requestVO.getRequestIDStr());
        p2pTransferVO.setModule(p_requestVO.getModule());
        p2pTransferVO.setInstanceID(p_requestVO.getInstanceID());
        p2pTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        p2pTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        p2pTransferVO.setServiceType(p_requestVO.getServiceType());
        p2pTransferVO.setSourceType(p_requestVO.getSourceType());
        p2pTransferVO.setCreatedOn(currentDate);
        p2pTransferVO.setCreatedBy(senderVO.getUserID());
        p2pTransferVO.setModifiedOn(currentDate);
        p2pTransferVO.setModifiedBy(senderVO.getUserID());
        p2pTransferVO.setTransferDate(currentDate);
        p2pTransferVO.setTransferDateTime(currentDate);
        p2pTransferVO.setSenderMsisdn(senderVO.getMsisdn());
        p2pTransferVO.setSenderID(senderVO.getUserID());
        p2pTransferVO.setNetworkCode(senderVO.getNetworkCode());
        p2pTransferVO.setLocale(senderLocale);
        p2pTransferVO.setLanguage(p2pTransferVO.getLocale().getLanguage());
        p2pTransferVO.setCountry(p2pTransferVO.getLocale().getCountry());
        p2pTransferVO.setMsgGatewayFlowType(p_requestVO.getMessageGatewayVO().getFlowType());
        p2pTransferVO.setMsgGatewayResponseType(p_requestVO.getMessageGatewayVO().getResponseType());
        p2pTransferVO.setMsgGatewayTimeOutValue(p_requestVO.getMessageGatewayVO().getTimeoutValue());
    }

    /**
     * Method to credit back the sender for failed or ambigous transaction
     * 
     * @param p_commonClient
     * @param p_onlyDecreaseOnly
     * @throws BTSLBaseException
     */
    private void creditBackSenderForFailedTrans(CommonClient p_commonClient, boolean p_onlyDecreaseOnly) throws BTSLBaseException {
        final String methodName = "creditBackSenderForFailedTrans";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, transferID, "Entered with p_onlyDecreaseOnly=" + p_onlyDecreaseOnly);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            TransactionLog
                .log(
                    transferID,
                    requestIDStr,
                    senderMSISDN,
                    senderNetworkCode,
                    PretupsI.TXN_LOG_REQTYPE_INT,
                    PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Credit Back Sender",
                    PretupsI.TXN_LOG_STATUS_SUCCESS,
                    "Transfer Status=" + p2pTransferVO.getTransferStatus() + " Credit Back Allowed=" + ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BACK_ALLOWED))).booleanValue() + " Credit in Ambigous =" + ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue());

            if (!p_onlyDecreaseOnly) {
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BACK_ALLOWED))).booleanValue()) {
                    if ((((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue() && p2pTransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) || p2pTransferVO
                        .getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        final String requestStr = getSenderCreditAdjustStr();

                        TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_CREDITBACK,
                            requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
                        final String senderCreditBackResponse = p_commonClient.process(getSenderCreditAdjustStr(), transferID, intModCommunicationTypeS, intModIPS,
                            intModPortS, intModClassNameS);
                        TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_CREDITBACK,
                            senderCreditBackResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, transferID, "senderCreditBackResponse From IN Module=" + senderCreditBackResponse);
                        }

                        boolean isCounterToBeDecreased = true;
                        try {
                            // update the transfer_item details table before
                            // credit back of sender
                            /*
                             * p2pTransferVO.setModifiedOn(currentDate);
                             * p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                             * PretupsBL.updateTransferDetails(con,p2pTransferVO)
                             * ;
                             */
                            updateForSenderCreditBackResponse(senderCreditBackResponse);
                            /*
                             * if(!PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS.equals
                             * (p2pTransferVO.getTransferStatus()))
                             * {
                             * p2pTransferVO.setCreditBackStatus(PretupsErrorCodesI
                             * .TXN_STATUS_SUCCESS);
                             * p2pTransferVO.setTransferStatus(PretupsErrorCodesI
                             * .TXN_STATUS_FAIL);
                             * }
                             */
                        } catch (BTSLBaseException be) {
                            LOG.errorTrace(methodName, be);
                            isCounterToBeDecreased = false;
                            TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                                "Transaction Not Success", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + p2pTransferVO.getTransferStatus() + " Getting Code=" + senderVO
                                    .getInterfaceResponseCode());
                        }
                        TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                            "Credit Back Success", PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                        mcomCon = new MComConnection();con=mcomCon.getConnection();

                        if (isCounterToBeDecreased) {
                            SubscriberBL.decreaseTransferOutCounts(con, p2pTransferVO,PretupsI.SERVICE_TYPE_VOUCHER_CONSUMPTION);
                        }

                        p2pTransferVO.setModifiedOn(currentDate);
                        p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                        PretupsBL.updateTransferDetails(con, p2pTransferVO);

                        PretupsBL.addTransferCreditBackDetails(con, p2pTransferVO.getTransferID(), senderCreditBackStatusVO);

                        mcomCon.finalCommit();
                        finalTransferStatusUpdate = false;

                        if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(p2pTransferVO.getCreditBackStatus())) {
                            p2pTransferVO.setSenderReturnMessage(getSenderCreditBackMessage());
                        }

                    } else {
                        // SubscriberBL.updateSubscriberLastDetails(con,p2pTransferVO,senderVO,currentDate,PretupsErrorCodesI.TXN_STATUS_FAIL);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VchrConsController[processFromQueue]",
                            transferID, senderMSISDN, senderNetworkCode, "Credit back not required in case of Ambigous cases");
                    }
                } else {
                    // SubscriberBL.updateSubscriberLastDetails(con,p2pTransferVO,senderVO,currentDate,PretupsErrorCodesI.TXN_STATUS_FAIL);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                        "VchrConsController[creditBackSenderForFailedTrans]", transferID, senderMSISDN, senderNetworkCode,
                        "Credit back Not required in case of failed transactions");
                }
                TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                    "Credit Back Done", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            }
            // When Sender Debit fails the decrease the counters only
            else {
                mcomCon = new MComConnection();con=mcomCon.getConnection();
                SubscriberBL.decreaseTransferOutCounts(con, p2pTransferVO,PretupsI.SERVICE_TYPE_VOUCHER_CONSUMPTION);
                p2pTransferVO.setModifiedOn(currentDate);
                p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                PretupsBL.updateTransferDetails(con, p2pTransferVO);
                mcomCon.finalCommit();
                finalTransferStatusUpdate = false;
            }
            /*
             * else if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BACK_ALLOWED))).booleanValue())
             * {
             * if((((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SNDR_CREDIT_BK_AMB_STATUS))).booleanValue() &&
             * p2pTransferVO
             * .getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI
             * .TXN_STATUS_AMBIGUOUS)) ||
             * p2pTransferVO.getTransferStatus().equalsIgnoreCase
             * (PretupsErrorCodesI.TXN_STATUS_FAIL))
             * {
             * String requestStr=getSenderCreditAdjustStr();
             * 
             * TransactionLog.log(transferID,requestIDStr,senderMSISDN,
             * senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_CREDITBACK
             * ,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
             * String senderCreditBackResponse=p_commonClient.process(
             * getSenderCreditAdjustStr
             * (),transferID,intModCommunicationTypeS,intModIPS
             * ,intModPortS,intModClassNameS);
             * TransactionLog.log(transferID,requestIDStr,senderMSISDN,
             * senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_CREDITBACK
             * ,senderCreditBackResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
             * 
             * if(LOG.isDebugEnabled())LOG.debug("creditBackSenderForFailedTrans"
             * ,transferID,"senderCreditBackResponse From IN Module="+
             * senderCreditBackResponse);
             * 
             * boolean isCounterToBeDecreased=true;
             * try
             * {
             * //update the transfer_item details table before credit back of
             * sender
             * p2pTransferVO.setModifiedOn(currentDate);
             * p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
             * PretupsBL.updateTransferDetails(p_con,p2pTransferVO);
             * updateForSenderCreditBackResponse(senderCreditBackResponse);
             * if(!PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS.equals(p2pTransferVO.
             * getTransferStatus()))
             * p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL
             * );
             * }
             * catch(BTSLBaseException be)
             * {
             * isCounterToBeDecreased=false;
             * TransactionLog.log(transferID,requestIDStr,senderMSISDN,
             * senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS
             * ,"Transaction Not Success"
             * ,PretupsI.TXN_LOG_STATUS_FAIL,"Transfer Status="
             * +p2pTransferVO.getTransferStatus
             * ()+" Getting Code="+senderVO.getInterfaceResponseCode());
             * }
             * TransactionLog.log(transferID,requestIDStr,senderMSISDN,
             * senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS
             * ,"Credit Back Success",PretupsI.TXN_LOG_STATUS_SUCCESS,"");
             * 
             * PretupsBL.addTransferCreditBackDetails(p_con,p2pTransferVO.
             * getTransferID(),senderCreditBackStatusVO);
             * p2pTransferVO.setSenderReturnMessage(getSenderCreditBackMessage())
             * ;
             * 
             * if(isCounterToBeDecreased)
             * SubscriberBL.decreaseTransferOutCounts(p_con,p2pTransferVO);
             * }
             * else
             * {
             * SubscriberBL.updateSubscriberLastDetails(p_con,p2pTransferVO,senderVO
             * ,currentDate,PretupsErrorCodesI.TXN_STATUS_FAIL);
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.INFO,"VchrConsController[processFromQueue]"
             * ,transferID,senderMSISDN,senderNetworkCode,
             * "Credit back not required in case of Ambigous cases");
             * }
             * 
             * }
             * else
             * {
             * SubscriberBL.updateSubscriberLastDetails(p_con,p2pTransferVO,senderVO
             * ,currentDate,PretupsErrorCodesI.TXN_STATUS_FAIL);
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
             * EventStatusI.RAISED,EventLevelI.INFO,
             * "VchrConsController[creditBackSenderForFailedTrans]"
             * ,transferID,senderMSISDN,senderNetworkCode,
             * "Credit back Not required in case of failed transactions");
             * }
             */
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            if (con != null) {
                try {
                   mcomCon.finalRollback();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
            }
            finalTransferStatusUpdate = false;
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VchrConsController[creditBackSenderForFailedTrans]",transferID,senderMSISDN,senderNetworkCode,"Exception:"+be.getMessage());
            TransactionLog.log(transferID, requestIDStr, senderMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage() + " Getting Code=" + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            if (con != null) {
                try {
                    mcomCon.finalRollback();
                } catch (Exception ex) {
                    LOG.errorTrace(methodName, ex);
                }
            }
            finalTransferStatusUpdate = false;
            LOG.error(methodName, "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[creditBackSenderForFailedTrans]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(transferID, requestIDStr, senderMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("VchrConsController#creditBackSenderForFailedTrans");
				mcomCon = null;
			}
		}

    }

    /**
     * Method to perform validation in thread
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
            TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            processValidationRequest();
        } catch (BTSLBaseException be) {
            LOG.error("VchrConsController[processValidationRequestInThread]", "Getting BTSL Base Exception:" + be.getMessage());
            TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            if (recValidationFailMessageRequired) {
                if (p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.P2P_RECEIVER_FAIL), new String[] { String.valueOf(transferID), PretupsBL
                        .getDisplayAmount(p2pTransferVO.getRequestedAmount()) }));
                }
            }

            p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(p2pTransferVO.getErrorCode())) {
                p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            LOG.error(this, transferID, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[run]", transferID, senderMSISDN,
                senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            // !transferDetailAdded Condition Added as we think its not require
            // as already done
            if (transferID != null && !transferDetailAdded) {
                Connection con = null;MComConnectionI mcomCon = null;
                try {
                    mcomCon = new MComConnection();con=mcomCon.getConnection();
                    addEntryInTransfers(con);
                    if (p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        finalTransferStatusUpdate = false; // No need to update
                        // the status of
                        // transaction in run
                        // method
                    }

                } catch (BTSLBaseException be) {
                    LOG.errorTrace(methodName, be);
                    LOG.error(methodName, "BTSLBaseException:" + be.getMessage());
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    LOG.error(methodName, "Exception:" + e.getMessage());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[process]",
                        transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
                } finally {
					if (mcomCon != null) {
						mcomCon.close("VchrConsController#processValidationRequestInThread");
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
     * Get the sender Credit Back Adjust String
     * 
     * @return
     */
    public String getSenderCreditAdjustStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getSenderCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_CREDIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + senderTransferItemVO.getTransferValue());
        strBuff.append("&CARD_GROUP=" + p2pTransferVO.getCardGroupCode());
        strBuff.append("&SERVICE_CLASS=" + senderTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + senderTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + senderTransferItemVO.getAccountStatus());
        strBuff.append("&SOURCE_TYPE=" + p2pTransferVO.getSourceType());
        strBuff.append("&PRODUCT_CODE=" + p2pTransferVO.getProductCode());
        strBuff.append("&TAX_AMOUNT=" + (p2pTransferVO.getSenderTax1Value() + p2pTransferVO.getSenderTax2Value()));
        strBuff.append("&ACCESS_FEE=" + p2pTransferVO.getSenderAccessFee());
        strBuff.append("&SENDER_MSISDN=" + senderMSISDN);
        strBuff.append("&RECEIVER_MSISDN=" + receiverMSISDN);
        strBuff.append("&EXTERNAL_ID=" + senderExternalID);
        strBuff.append("&GATEWAY_CODE=" + requestVO.getRequestGatewayCode());
        strBuff.append("&GATEWAY_TYPE=" + requestVO.getRequestGatewayType());
        strBuff.append("&IMSI=" + BTSLUtil.NullToString(senderIMSI));
        strBuff.append("&SENDER_ID=" + ((SenderVO) requestVO.getSenderVO()).getUserID());
        strBuff.append("&SERVICE_TYPE=" + senderSubscriberType + "-" + type);
        strBuff.append("&ADJUST=Y");
        strBuff.append("&INTERFACE_PREV_BALANCE=" + senderTransferItemVO.getPostBalance());
        // Avinash send the requested amount to IN. to use card group only for
        // reporting purpose.
        strBuff.append("&REQUESTED_AMOUNT=" + p2pTransferVO.getRequestedAmount());
        // Added for closing the sender credit back issue.as below parameter was
        // not set
        strBuff.append("&CAL_OLD_EXPIRY_DATE=" + senderTransferItemVO.getOldExporyInMillis());// @nu
        strBuff.append("&VALIDITY_DAYS=" + senderTransferItemVO.getValidity());

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

        return strBuff.toString();
    }

    /**
     * Get the receiver Under process message
     * 
     * @return
     */
    private String getReceiverUnderProcessMessage() {
        final String[] messageArgArray = { transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(p2pTransferVO
            .getReceiverTransferValue()), senderMSISDN, PretupsBL.getDisplayAmount(p2pTransferVO.getReceiverAccessFee()) };
        return BTSLUtil.getMessage(receiverLocale, PretupsErrorCodesI.P2P_RECEIVER_UNDERPROCESS, messageArgArray);
    }

    /**
     * Method to get the under process message before validation to be sent to
     * sender
     * 
     * @return
     */
    private String getSndrUPMsgBeforeValidation() {
        final String[] messageArgArray = { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.P2P_SENDER_UNDERPROCESS_B4VAL, messageArgArray);
    }

    /**
     * Method to get the success message to be sent to sender
     * 
     * @return
     */
    private String getSenderUnderProcessMessage() {
        final String[] messageArgArray = { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), PretupsBL
            .getDisplayAmount(p2pTransferVO.getSenderTransferValue()), PretupsBL.getDisplayAmount(p2pTransferVO.getSenderAccessFee()) };
        return BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.P2P_SENDER_UNDERPROCESS, messageArgArray);
    }

    /**
     * Method to get the credit back message
     * 
     * @return
     */
    private String getSenderCreditBackMessage() {
        if (BTSLUtil.isNullString(senderCreditPostBalanceAvailable) || "Y".equals(senderCreditPostBalanceAvailable)) {
            final String[] messageArgArray = { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), PretupsBL
                .getDisplayAmount(senderTransferItemVO.getPostBalance()) };
            return BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.P2P_SENDER_CREDIT_BACK, messageArgArray);
        }
        final String[] messageArgArray = { receiverMSISDN, transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.P2P_SENDER_CREDIT_BACK_WITHOUT_POSTBAL, messageArgArray);

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
            // METHOD FOR INSERTING AND UPDATION IN P2P Transfer Table
            if (!transferDetailAdded) {
                PretupsBL.addTransferDetails(p_con, p2pTransferVO);// add
                // transfer
                // details in
                // database
            } else if (transferDetailAdded) {
                p2pTransferVO.setModifiedOn(new Date());
                p2pTransferVO.setModifiedBy(p2pTransferVO.getSenderID());
                PretupsBL.updateTransferDetails(p_con, p2pTransferVO);// add
                // transfer
                // details
                // in
                // database
            }
            p_con.commit();
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            if (!isCounterDecreased && decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                isCounterDecreased = true;
            }
            LOG.error("addEntryInTransfers", transferID, "BTSLBaseException while adding transfer details in database:" + be.getMessage());
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[process]", transferID,
                senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            if (!isCounterDecreased && decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(transferID, senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                isCounterDecreased = true;
            }
            LOG.error(methodName, transferID, "Exception while adding transfer details in database:" + e.getMessage());
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
        final String methodName = "getInterfaceRoutingDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                requestIDStr,
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
        String interfaceID = null;
        String interfaceHandlerClass = null;
        String underProcessMsgReqd = null;
        String allServiceClassID = null;
        String externalID = null;
        performIntfceCatRoutingBeforeVal = false; // Set so that receiver flag
        // is not overridden by sender
        // flag
        final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
            .getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
        try {
            if (subscriberRoutingControlVO != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        methodName,
                        transferID,
                        " p_userType=" + p_userType + " Database Check Required=" + subscriberRoutingControlVO.isDatabaseCheckBool() + " Series Check Required=" + subscriberRoutingControlVO
                            .isSeriesCheckBool());
                }

                if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)) {
                        final ListValueVO listValueVO = PretupsBL.validateNumberInRoutingDatabase(p_con, p_msisdn, p_interfaceCategory);
                        if (listValueVO != null) {
                            isSuccess = true;
                            setInterfaceDetails(p_prefixID, p_userType, listValueVO, false, null, null);

                            if (p_userType.equals(PretupsI.USER_TYPE_SENDER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                senderInterfaceInfoInDBFound = true;
                                senderDeletionReqFromSubRouting = true;
                            } else if (p_userType.equals(PretupsI.USER_TYPE_RECEIVER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                receiverInterfaceInfoInDBFound = true;
                                receiverDeletionReqFromSubRouting = true;
                            }
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(methodName, transferID,
                                    " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " not found in Database , performing Series Check for Prefix ID=" + p_prefixID);
                            }
                            // service selector based checks added
                            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                                interfaceMappingVO1 = ServiceSelectorInterfaceMappingCache
                                    .getObject(serviceType + "_" + p2pTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                            }
                            if (interfaceMappingVO1 == null) {
                                try {
                                    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                                    isSuccess = true;
                                    setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
                                } catch (BTSLBaseException be) {
                                    if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                        performIntfceCatRoutingBeforeVal = true;
                                    } else {
                                        throw be;
                                    }
                                }
                            } else {
                                isSuccess = true;
                                setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
                            }
                        } else {
                            performIntfceCatRoutingBeforeVal = true;
                            isSuccess = false;
                        }
                    } else if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)) {
                        final WhiteListVO whiteListVO = PretupsBL.validateNumberInWhiteList(p_con, p_msisdn);
                        if (whiteListVO != null) {
                            isSuccess = true;
                            final ListValueVO listValueVO = whiteListVO.getListValueVO();
                            interfaceID = listValueVO.getValue();
                            interfaceHandlerClass = listValueVO.getLabel();
                            underProcessMsgReqd = listValueVO.getType();
                            allServiceClassID = listValueVO.getTypeName();
                            externalID = listValueVO.getIDValue();

                            if (p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                                senderTransferItemVO.setInterfaceID(interfaceID);
                                senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                                senderAllServiceClassID = allServiceClassID;
                                senderExternalID = externalID;
                                // Mark the Post Paid Interface as Online
                                senderVO.setPostOfflineInterface(true);

                                senderTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
                                senderVO.setCreditLimit(whiteListVO.getCreditLimit());
                                senderTransferItemVO.setReferenceID(whiteListVO.getAccountID());
                                senderTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
                                senderIMSI = whiteListVO.getImsi();
                                senderTransferItemVO.setPrefixID(p_prefixID);
                                senderTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
                                if (p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                    senderInterfaceInfoInDBFound = true;
                                }
                            } else {
                                receiverTransferItemVO.setPrefixID(p_prefixID);
                                receiverTransferItemVO.setInterfaceID(interfaceID);
                                receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                                if (PretupsI.YES.equals(underProcessMsgReqd)) {
                                    p2pTransferVO.setUnderProcessMsgReq(true);
                                }
                                receiverAllServiceClassID = allServiceClassID;
                                receiverExternalID = externalID;
                                receiverVO.setPostOfflineInterface(true);

                                receiverTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
                                receiverTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
                                receiverTransferItemVO.setReferenceID(whiteListVO.getAccountID());
                                receiverIMSI = whiteListVO.getImsi();
                                receiverTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
                                if (p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                    receiverInterfaceInfoInDBFound = true;
                                }
                            }
                            if (!PretupsI.YES.equals(listValueVO.getStatus())) {
                                // ChangeID=LOCALEMASTER
                                // which language message to be set is
                                // determined from the locale master table for
                                // the requested locale
                                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(senderLocale)).getMessage())) {
                                    p2pTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                } else {
                                    p2pTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
                            }
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(methodName, transferID,
                                    " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " not found in Database , performing Series Check for Prefix ID=" + p_prefixID);
                            }

                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            // check service selector based check loading of
                            // interface
                            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                                interfaceMappingVO1 = ServiceSelectorInterfaceMappingCache
                                    .getObject(serviceType + "_" + p2pTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                            }
                            if (interfaceMappingVO1 == null) {
                                try {
                                    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                                    isSuccess = true;
                                    setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
                                } catch (BTSLBaseException be) {
                                    if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                        performIntfceCatRoutingBeforeVal = true;
                                    } else {
                                        throw be;
                                    }
                                }
                            } else {
                                isSuccess = true;
                                setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
                            }
                        } else {
                            isSuccess = false;
                            performIntfceCatRoutingBeforeVal = true;
                        }
                    }
                } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, transferID, " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " performing Series Check for Prefix ID=" + p_prefixID);
                    }

                    MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                    // check service selector based check loading of interface
                    ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                        interfaceMappingVO1 = ServiceSelectorInterfaceMappingCache
                            .getObject(serviceType + "_" + p2pTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                    }
                    if (interfaceMappingVO1 == null) {
                        try {
                            interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                            isSuccess = true;
                            setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                performIntfceCatRoutingBeforeVal = true;
                            } else {
                                throw be;
                            }
                        }
                    } else {
                        isSuccess = true;
                        setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
                    }
                } else {
                    isSuccess = false;
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        methodName,
                        transferID,
                        " By default carrying out series check as routing control not defined for p_userType=" + p_userType + " MSISDN =" + p_msisdn + " performing Series Check for Prefix ID=" + p_prefixID);
                }
                // This event is raised by ankit Z on date 3/8/06 for case when
                // entry not found in routing control and considering series
                // based routing
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VchrConsController[getInterfaceRoutingDetails]",
                    transferID, senderMSISDN, senderNetworkCode, "Exception:Routing control information not defined so performing series based routing");

                MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                // check service selector based check loading of interface
                ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue()) {
                    interfaceMappingVO1 = ServiceSelectorInterfaceMappingCache
                        .getObject(serviceType + "_" + p2pTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                }
                if (interfaceMappingVO1 == null) {
                    try {
                        interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                        isSuccess = true;
                        setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                            performIntfceCatRoutingBeforeVal = true;
                        } else {
                            throw be;
                        }
                    }
                } else {
                    isSuccess = true;
                    setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
                }
            }

            if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                senderTransferItemVO.setInterfaceType(p_interfaceCategory);
            } else if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
                receiverTransferItemVO.setInterfaceType(type);
                receiverTransferItemVO.setSubscriberType(type);
            }
        } catch (BTSLBaseException be) {
            if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                senderTransferItemVO.setInterfaceType(p_interfaceCategory);
            } else if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
                receiverTransferItemVO.setInterfaceType(type);
                receiverTransferItemVO.setSubscriberType(type);
            }
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[getInterfaceRoutingDetails]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            isSuccess = false;
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, requestIDStr,
                " Exiting with isSuccess=" + isSuccess + "senderAllServiceClassID=" + senderAllServiceClassID + " receiverAllServiceClassID=" + receiverAllServiceClassID);
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
        final String methodName = "performSenderAlternateRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, requestIDStr, " Entered ");
        }
        Connection con = null;MComConnectionI mcomCon = null;
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
                String senderValResponse = null;
                switch (altList.size()) {
                case 1: {
                    LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(transferID, p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null, null);

                    checkTransactionLoad(PretupsI.USER_TYPE_SENDER, senderTransferItemVO.getInterfaceID());

                    // validate sender limits after Interface Validations
                    mcomCon = new MComConnection();con=mcomCon.getConnection();
                    SubscriberBL.validateSenderLimits(con, p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);

					if (mcomCon != null) {
						mcomCon.close("VchrConsController#performSenderAlternateRouting");
						mcomCon = null;
					}
                    con=null;
                    requestStr = getSenderValidateStr();
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr,
                        PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "Sending Request For MSISDN=" + senderMSISDN + " on ALternate Routing 1 to =" + senderTransferItemVO.getInterfaceID());
                    }

                    senderValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeS, intModIPS, intModPortS, intModClassNameS);

                    TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(senderValResponse, 1, altList.size());
                        // if(PretupsI.INTERFACE_CATEGORY_PRE.equals(type) &&
                        // InterfaceErrorCodesI.SUCCESS.equals(senderTransferItemVO.getValidationStatus()))
                        if (InterfaceErrorCodesI.SUCCESS.equals(senderTransferItemVO.getValidationStatus())) {
                            // Update in DB for routing interface
                            // updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER,p2pTransferVO.getNetworkCode(),senderTransferItemVO.getInterfaceID(),senderExternalID,senderMSISDN,p2pTransferVO.getPaymentMethodType(),senderVO.getUserID(),currentDate);
                            isSenderRoutingUpdate = true;
                        }
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        throw e;
                    }

                    break;
                }
                case 2: {
                    LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(transferID, p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null, null);

                    checkTransactionLoad(PretupsI.USER_TYPE_SENDER, senderTransferItemVO.getInterfaceID());

                    mcomCon = new MComConnection();con=mcomCon.getConnection();
                    SubscriberBL.validateSenderLimits(con, p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);

					if (mcomCon != null) {
						mcomCon.close("VchrConsController#performSenderAlternateRouting");
						mcomCon = null;
					}
                    con=null;

                    requestStr = getSenderValidateStr();

                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr,
                        PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "Sending Request For MSISDN=" + senderMSISDN + " on ALternate Routing 1 to =" + senderTransferItemVO.getInterfaceID());
                    }

                    senderValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeS, intModIPS, intModPortS, intModClassNameS);

                    TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(senderValResponse, 1, altList.size());
                        if (PretupsI.INTERFACE_CATEGORY_PRE.equals(type) && InterfaceErrorCodesI.SUCCESS.equals(senderTransferItemVO.getValidationStatus())) {
                            // Update in DB for routing interface
                            updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, p2pTransferVO.getNetworkCode(), senderTransferItemVO.getInterfaceID(), senderExternalID,
                                senderMSISDN, p2pTransferVO.getPaymentMethodType(), senderVO.getUserID(), currentDate);

                        }
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    methodName,
                                    "Got Status=" + InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND + " After validation Request For MSISDN=" + senderMSISDN + " Performing Alternate Routing to 2");
                            }

                            LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(transferID, p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(1);

                            setInterfaceDetails(senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null, null);

                            checkTransactionLoad(PretupsI.USER_TYPE_SENDER, senderTransferItemVO.getInterfaceID());

                            // validate sender limits after Interface
                            // Validations
                            mcomCon = new MComConnection();con=mcomCon.getConnection();
                            SubscriberBL.validateSenderLimits(con, p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);

							if (mcomCon != null) {
								mcomCon.close("VchrConsController#performSenderAlternateRouting");
								mcomCon = null;
							}
							  con=null;

                            requestStr = getSenderValidateStr();

                            LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.SENDER_UNDER_VAL);

                            TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            if (LOG.isDebugEnabled()) {
                                LOG.debug(methodName, "Sending Request For MSISDN=" + senderMSISDN + " on ALternate Routing 2 to =" + senderTransferItemVO.getInterfaceID());
                            }

                            senderValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeS, intModIPS, intModPortS, intModClassNameS);

                            TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                                senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                senderValidateResponse(senderValResponse, 2, altList.size());
                                if (PretupsI.INTERFACE_CATEGORY_PRE.equals(type) && InterfaceErrorCodesI.SUCCESS.equals(senderTransferItemVO.getValidationStatus())) {
                                    // Update in DB for routing interface
                                    updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, p2pTransferVO.getNetworkCode(), senderTransferItemVO.getInterfaceID(),
                                        senderExternalID, senderMSISDN, p2pTransferVO.getPaymentMethodType(), senderVO.getUserID(), currentDate);

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[performSenderAlternateRouting]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("VchrConsController#performSenderAlternateRouting");
				mcomCon = null;
			}
			con=null;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, requestIDStr, " Exiting ");
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
        final String methodName = "senderValidateResponse";
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, senderTransferItemVO.getInterfaceID(), interfaceStatusType,
                PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        // This has been done so that when Alternate routing has to be performed
        // and when we have to get out and throw error
        if ((InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) || (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND
            .equals(status) && !isRoutingSecond)) {
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            throw new BTSLBaseException(this, "senderValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        }
        senderTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        senderTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
        senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        senderTransferItemVO.setValidationStatus(status);
        senderVO.setInterfaceResponseCode(senderTransferItemVO.getInterfaceResponseCode());
        senderTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
        senderTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

        // If status is other than Success in validation stage mark sender
        // request as Not applicable and
        // Make transaction Fail
        String[] strArr = null;

        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            p2pTransferVO.setErrorCode(status + "_S");
            senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            receiverVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            senderTransferItemVO.setTransferStatus(status);
            receiverTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { receiverMSISDN, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), transferID };
            throw new BTSLBaseException(this, "senderValidateResponse", PretupsErrorCodesI.P2P_SENDER_FAIL, 0, strArr, null);
        }

        senderTransferItemVO.setTransferStatus(status);
        senderVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

        try {
            senderTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            senderTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;

        senderTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        try {
            senderTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            senderTransferItemVO.setBalanceCheckReq(false);
        }
        senderVO.setCreditLimit(senderTransferItemVO.getPreviousBalance());

        // Update the Previous Balance in case of Post Paid Offline interface
        // with Credit Limit - Monthly Transfer Amount
        if (senderVO.isPostOfflineInterface()) {
            final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(senderVO.getLastSuccessTransferDate(), currentDate, BTSLUtil.PERIOD_MONTH);
            if (!isPeriodChange) {
                senderTransferItemVO.setPreviousBalance(senderTransferItemVO.getPreviousBalance() - senderVO.getMonthlyTransferAmount());
            }
        }

        senderTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
        senderTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VchrConsController[updateReceiverLocale]",
                        transferID, receiverMSISDN, "", "Exception: Notification language returned from IN is not defined in system p_languageCode: " + p_languageCode);
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
     * This method will perform the alternate interface category routing if
     * there
     * This method will be called either after validation or after performing
     * interface routing
     * 
     * @throws BTSLBaseException
     */
    public void performAlternateCategoryRouting() throws BTSLBaseException {
        final String methodName = "performAlternateCategoryRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Performing ALternate interface category routing Entered");
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            String requestStr = null;
            CommonClient commonClient = null;
            String receiverValResponse = null;

            LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
            LoadController.decreaseReceiverTransactionInterfaceLoad(transferID, p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // populates the alternate interface category details
            populateAlternateInterfaceDetails(con);

            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception be) {
                    LOG.errorTrace(methodName, be);
                }
				if (mcomCon != null) {
					mcomCon.close("VchrConsController#performAlternateCategoryRouting");
					mcomCon = null;
				}
                con = null;
            }
            p2pTransferVO.setTransferCategory(senderSubscriberType + "-" + type);
            if (LOG.isDebugEnabled()) {
                LOG.debug("process", requestIDStr, "Overriding transfer Category as :" + p2pTransferVO.getTransferCategory());
            }

            p2pTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);

            checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, receiverTransferItemVO.getInterfaceID());

            // validate receiver limits before Interface Validations
            PretupsBL.validateRecieverLimits(null, p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

            requestStr = getReceiverValidateStr();
            commonClient = new CommonClient();

            LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.RECEIVER_UNDER_VAL);

            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Alternate Category Routing");

            receiverValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeS, intModIPS, intModPortS, intModClassNameS);

            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            handleReceiverValidateResponse(receiverValResponse, SRC_AFTER_INRESP_CAT_ROUTING);
            if (InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus())) {
                // If mobile number found on Post but previously was defined in
                // PRE then delete the number
                if (newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                    if (receiverDeletionReqFromSubRouting) {
                        PretupsBL.deleteSubscriberInterfaceRouting(receiverMSISDN, oldInterfaceCategory);
                    }
                } else {
                    // Update in DB for routing interface
                    final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p2pTransferVO
                        .getReceiverNetworkCode() + "_" + p2pTransferVO.getServiceType() + "_" + newInterfaceCategory);
                    if (!receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, "Inserting the MSISDN=" + receiverMSISDN + " in Subscriber routing database for further usage");
                        }

                        PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), receiverExternalID, receiverMSISDN, newInterfaceCategory, senderVO
                            .getUserID(), currentDate);
                        receiverInterfaceInfoInDBFound = true;
                        receiverDeletionReqFromSubRouting = true;
                    }
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[performAlternateCategoryRouting]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception be) {
                    LOG.errorTrace(methodName, be);
                }
				if (mcomCon != null) {
					mcomCon.close("VchrConsController#performAlternateCategoryRouting");
					mcomCon = null;
				}
				con = null;
            }
        }
    }

    /**
     * Method to populate the Alternate Interface Details for the Receiver
     * against the new interface category
     * 
     * @throws BTSLBaseException
     */
    public void populateAlternateInterfaceDetails(Connection p_con) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("populateAlternateInterfaceDetails", "Entered to get the alternate category");
        }

        boolean isReceiverFound = false;

        if (!interfaceCatRoutingDone) {
            interfaceCatRoutingDone = true;
            type = newInterfaceCategory;
            networkPrefixVO = null;

            requestVO.setReqSelector(newDefaultSelector);
            p2pTransferVO.setSubService(newDefaultSelector);

            // Load the new prefix ID against the interface category , If Not
            // required then give the error

            if (LOG.isDebugEnabled()) {
                LOG.debug("populateAlternateInterfaceDetails", "Got the alternate category as =" + type);
            }

            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(receiverVO.getMsisdnPrefix(), type);
            if (networkPrefixVO != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("populateAlternateInterfaceDetails", "Got the Prefix ID for MSISDN=" + receiverMSISDN + "Prefix ID=" + networkPrefixVO.getPrefixID());
                }

                receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
                receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
                receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
                isReceiverFound = getInterfaceRoutingDetails(p_con, receiverMSISDN, receiverVO.getPrefixID(), receiverVO.getSubscriberType(), receiverVO.getNetworkCode(),
                    p2pTransferVO.getServiceType(), type, PretupsI.USER_TYPE_RECEIVER, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
            } else {
                LOG.error(this, "Series Not Defined for Alternate Interface =" + type + " For Series=" + receiverVO.getMsisdnPrefix());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                    "VchrConsController[populateAlternateInterfaceDetails]", "", "", "",
                    "Series =" + receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + type + " But required for validation");
                isReceiverFound = false;
            }

            if (!isReceiverFound) {
                throw new BTSLBaseException("VchrConsController", "populateServicePaymentInterfaceDetails", PretupsErrorCodesI.P2P_NOTFOUND_SERVICEINTERFACEMAPPING);
            }
        }
    }

    /**
     * This method handles the receiver validate response after sending request
     * to IN
     * 
     * @param str
     * @param p_source
     * @throws BTSLBaseException
     */
    public void handleReceiverValidateResponse(String str, int p_source) throws BTSLBaseException {
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        ArrayList altList = null;
        boolean isRequired = false;

        // If we get the MSISDN not found on interface error then perform
        // interface routing
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(receiverTransferItemVO.getInterfaceID());
            if (altList != null && altList.size() > 0) {
                performReceiverAlternateRouting(altList, p_source);
            } else {
                isRequired = true;
            }
        }
        if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
            populateReceiverItemsDetails(map);
            // For Service Provider Information
            receiverTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
        }
    }

    /**
     * Method to perform the Interface routing for the subscriber MSISDN
     * 
     * @param altList
     * @param p_source
     *            : Determines whether Alternate category needs to be performed
     *            after this or not
     * @throws BTSLBaseException
     */
    private void performReceiverAlternateRouting(ArrayList altList, int p_source) throws BTSLBaseException {
        final String methodName = "performReceiverAlternateRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, requestIDStr, " Entered p_source=" + p_source);
        }
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
                    LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
                    LoadController.decreaseReceiverTransactionInterfaceLoad(transferID, p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(receiverTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_RECEIVER, listValueVO, false, null, null);

                    checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, receiverTransferItemVO.getInterfaceID());

                    // validate receiver limits before Interface Validations
                    PretupsBL.validateRecieverLimits(null, p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

                    requestStr = getReceiverValidateStr();
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.RECEIVER_UNDER_VAL);

                    TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "Sending Request For MSISDN=" + receiverMSISDN + " on ALternate Routing 1 to =" + receiverTransferItemVO.getInterfaceID());
                    }

                    receiverValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeS, intModIPS, intModPortS, intModClassNameS);

                    TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        receiverValidateResponse(receiverValResponse, 1, altList.size(), p_source);
                        // If source is before IN validation then if interface
                        // is pre then we need to update in subscriber
                        // Routing but after alternate routing if number is
                        // found on another interface
                        // Then we need to delete the number from subscriber
                        // Routing or Vice versa
                        if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(type) && InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus())) {
                                // Update in DB for routing interface
                                updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER, p2pTransferVO.getReceiverNetworkCode(), receiverTransferItemVO.getInterfaceID(),
                                    receiverExternalID, receiverMSISDN, type, senderVO.getUserID(), currentDate);
                            }
                        } else {
                            if (InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus())) {
                                if (newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                                    if (receiverDeletionReqFromSubRouting) {
                                        PretupsBL.deleteSubscriberInterfaceRouting(receiverMSISDN, oldInterfaceCategory);
                                    }
                                } else {
                                    // Update in DB for routing interface
                                    final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p2pTransferVO
                                        .getReceiverNetworkCode() + "_" + p2pTransferVO.getServiceType() + "_" + newInterfaceCategory);
                                    if (!receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                        PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), receiverExternalID, receiverMSISDN,
                                            newInterfaceCategory, senderVO.getUserID(), currentDate);
                                        receiverInterfaceInfoInDBFound = true;
                                        receiverDeletionReqFromSubRouting = true;
                                    }
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
                    LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
                    LoadController.decreaseReceiverTransactionInterfaceLoad(transferID, p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(receiverTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_RECEIVER, listValueVO, false, null, null);

                    checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, receiverTransferItemVO.getInterfaceID());

                    // validate receiver limits before Interface Validations
                    PretupsBL.validateRecieverLimits(null, p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

                    requestStr = getReceiverValidateStr();
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.RECEIVER_UNDER_VAL);

                    TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "Sending Request For MSISDN=" + receiverMSISDN + " on ALternate Routing 1 to =" + receiverTransferItemVO.getInterfaceID());
                    }

                    receiverValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeS, intModIPS, intModPortS, intModClassNameS);

                    TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL,
                        receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        receiverValidateResponse(receiverValResponse, 1, altList.size(), p_source);
                        // If source is before IN validation then if interface
                        // is pre then we need to update in subscriber
                        // Routing but after alternate routing if number is
                        // found on another interface
                        // Then we need to delete the number from subscriber
                        // Routing or Vice versa

                        if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(type) && InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus())) {
                                // Update in DB for routing interface
                                updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER, p2pTransferVO.getReceiverNetworkCode(), receiverTransferItemVO.getInterfaceID(),
                                    receiverExternalID, receiverMSISDN, type, senderVO.getUserID(), currentDate);
                            }
                        } else {
                            if (InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus())) {
                                if (newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                                    if (receiverDeletionReqFromSubRouting) {
                                        PretupsBL.deleteSubscriberInterfaceRouting(receiverMSISDN, oldInterfaceCategory);
                                    }
                                } else {
                                    // Update in DB for routing interface
                                    final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p2pTransferVO
                                        .getReceiverNetworkCode() + "_" + p2pTransferVO.getServiceType() + "_" + newInterfaceCategory);
                                    if (!receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                        PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), receiverExternalID, receiverMSISDN,
                                            newInterfaceCategory, senderVO.getUserID(), currentDate);
                                        receiverInterfaceInfoInDBFound = true;
                                        receiverDeletionReqFromSubRouting = true;
                                    }
                                }
                            }
                        }
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    methodName,
                                    "Got Status=" + InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND + " After validation Request For MSISDN=" + receiverMSISDN + " Performing Alternate Routing to 2");
                            }

                            LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
                            LoadController.decreaseReceiverTransactionInterfaceLoad(transferID, p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(1);

                            setInterfaceDetails(receiverTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_RECEIVER, listValueVO, false, null, null);

                            checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, receiverTransferItemVO.getInterfaceID());

                            // validate receiver limits before Interface
                            // Validations
                            PretupsBL.validateRecieverLimits(null, p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

                            requestStr = getReceiverValidateStr();

                            LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.RECEIVER_UNDER_VAL);

                            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 2");

                            if (LOG.isDebugEnabled()) {
                                LOG.debug(methodName, "Sending Request For MSISDN=" + receiverMSISDN + " on ALternate Routing 2 to =" + receiverTransferItemVO
                                    .getInterfaceID());
                            }

                            receiverValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeS, intModIPS, intModPortS, intModClassNameS);

                            TransactionLog.log(transferID, requestIDStr, receiverMSISDN, receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES,
                                PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                receiverValidateResponse(receiverValResponse, 2, altList.size(), p_source);
                                // If source is before IN validation then if
                                // interface is pre then we need to update in
                                // subscriber
                                // Routing but after alternate routing if number
                                // is found on another interface
                                // Then we need to delete the number from
                                // subscriber Routing or Vice versa

                                if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
                                    if (PretupsI.INTERFACE_CATEGORY_PRE.equals(type) && InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus())) {
                                        // Update in DB for routing interface
                                        updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER, p2pTransferVO.getReceiverNetworkCode(), receiverTransferItemVO
                                            .getInterfaceID(), receiverExternalID, receiverMSISDN, type, senderVO.getUserID(), currentDate);
                                    }
                                } else {
                                    if (InterfaceErrorCodesI.SUCCESS.equals(receiverTransferItemVO.getValidationStatus())) {
                                        if (newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                                            if (receiverDeletionReqFromSubRouting) {
                                                PretupsBL.deleteSubscriberInterfaceRouting(receiverMSISDN, oldInterfaceCategory);
                                            }
                                        } else {
                                            // Update in DB for routing
                                            // interface
                                            final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p2pTransferVO
                                                .getReceiverNetworkCode() + "_" + p2pTransferVO.getServiceType() + "_" + newInterfaceCategory);
                                            if (!receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                                PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), receiverExternalID, receiverMSISDN,
                                                    newInterfaceCategory, senderVO.getUserID(), currentDate);
                                                receiverInterfaceInfoInDBFound = true;
                                                receiverDeletionReqFromSubRouting = true;
                                            }
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[performAlternateRouting]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, requestIDStr, " Exiting ");
        }
    }

    /**
     * This method validates the response from Interfaces in interface routing
     * 
     * @param str
     * @param p_attempt
     * @param p_altSize
     * @param p_source
     * @throws BTSLBaseException
     */
    public void receiverValidateResponse(String str, int p_attempt, int p_altSize, int p_source) throws BTSLBaseException {
        final String methodName = "receiverValidateResponse";
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
            throw new BTSLBaseException(this, "receiverValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        } else if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == p_altSize && p_source == SRC_BEFORE_INRESP_CAT_ROUTING && useAlternateCategory && !interfaceCatRoutingDone) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(this, " Performing Alternate category routing as MSISDN not found on any interfaces after routing for " + receiverMSISDN);
            }
            performAlternateCategoryRouting();
        } else {
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

            // If status is other than Success in validation stage mark sender
            // request as Not applicable and
            // Make transaction Fail
            String[] strArr = null;

            if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                p2pTransferVO.setErrorCode(status + "_R");
                receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                receiverTransferItemVO.setTransferStatus(status);
                senderTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                senderVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                strArr = new String[] { receiverMSISDN, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), transferID };
                throw new BTSLBaseException("VchrConsController", "receiverValidateResponse", PretupsErrorCodesI.P2P_SENDER_FAIL, 0, strArr, null);
            }
            receiverTransferItemVO.setTransferStatus(status);
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            receiverTransferItemVO.setSubscriberType(type);
            receiverVO.setSubscriberType(type);

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
            receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());

            receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
            receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));

            receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));

            // Done so that receiver check can be brough to common
            receiverVO.setServiceClassCode(receiverTransferItemVO.getServiceClass());

            try {
                receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            ;

            // Update the Previous Balance in case of Post Paid Offline
            // interface with Credit Limit - Monthly Transfer Amount
            if (receiverVO.isPostOfflineInterface()) {
                final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(receiverVO.getLastSuccessOn(), currentDate, BTSLUtil.PERIOD_MONTH);
                if (!isPeriodChange) {
                    receiverTransferItemVO.setPreviousBalance(receiverTransferItemVO.getPreviousBalance() - receiverVO.getMonthlyTransferAmount());
                }
            }

            // TO DO Done for testing purpose should we use it or give exception
            // in this case
            if (receiverTransferItemVO.getPreviousExpiry() == null) {
                receiverTransferItemVO.setPreviousExpiry(currentDate);
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
    public void populateReceiverItemsDetails(HashMap p_map) throws BTSLBaseException {
        final String methodName = "populateReceiverItemsDetails";
        final String status = (String) p_map.get("TRANSACTION_STATUS");
        // receiver language has to be taken from IN then the block below will
        // execute
        if ("Y".equals(requestVO.getUseInterfaceLanguage())) {
            // update the receiver locale if language code returned from IN is
            // not null
            updateReceiverLocale((String) p_map.get("IN_LANG"));
        }
        receiverTransferItemVO.setProtocolStatus((String) p_map.get("PROTOCOL_STATUS"));
        receiverTransferItemVO.setAccountStatus((String) p_map.get("ACCOUNT_STATUS"));
        receiverTransferItemVO.setInterfaceResponseCode((String) p_map.get("INTERFACE_STATUS"));
        receiverTransferItemVO.setValidationStatus(status);
        receiverVO.setInterfaceResponseCode(receiverTransferItemVO.getInterfaceResponseCode());

        if (!BTSLUtil.isNullString((String) p_map.get("IN_TXN_ID"))) {
            try {
                receiverTransferItemVO.setInterfaceReferenceID((String) p_map.get("IN_TXN_ID"));
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "VchrConsController[updateForReceiverValidateResponse]", transferID, senderMSISDN, senderNetworkCode,
                    "Exception while parsing for interface txn ID , Exception:" + e.getMessage());
            }
        }
        receiverTransferItemVO.setReferenceID((String) p_map.get("IN_RECON_ID"));

        String[] strArr = null;

        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            p2pTransferVO.setErrorCode(status + "_R");
            receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            receiverTransferItemVO.setTransferStatus(status);

            senderVO.setTransactionStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { receiverMSISDN, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()), transferID };
            // throw new
            // BTSLBaseException("VchrConsController","updateForReceiverValidateResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
            // throw new
            // BTSLBaseException("VchrConsController","populateReceiverItemsDetails",p2pTransferVO.getErrorCode(),0,strArr,null);
            if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P.equals(receiverTransferItemVO.getValidationStatus())) {
                throw new BTSLBaseException("VchrConsController", "populateReceiverItemsDetails", InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P + "_S", 0,
                    strArr, null);
            } else {
                throw new BTSLBaseException("VchrConsController", "populateReceiverItemsDetails", p2pTransferVO.getErrorCode(), 0, strArr, null);
            }
        }
        receiverTransferItemVO.setTransferStatus(status);
        receiverTransferItemVO.setSubscriberType(type);
        receiverVO.setSubscriberType(type);
        p2pTransferVO.setRequestedAmount(Long.parseLong(String.valueOf(p_map.get("TALK_TIME"))));
        receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
            .getRoutingControlDetails(p2pTransferVO.getReceiverNetworkCode() + "_" + p2pTransferVO.getServiceType() + "_" + type);
        if (PretupsI.INTERFACE_CATEGORY_PRE.equals(type) && !receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO
            .isDatabaseCheckBool()) {
            PretupsBL.insertSubscriberInterfaceRouting(receiverTransferItemVO.getInterfaceID(), receiverExternalID, receiverMSISDN, type, senderVO.getUserID(), currentDate);
            receiverInterfaceInfoInDBFound = true;
            receiverDeletionReqFromSubRouting = true;
        }

        try {
            receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) p_map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        try {
            receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) p_map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;
        receiverTransferItemVO.setFirstCall((String) p_map.get("FIRST_CALL"));
        receiverTransferItemVO.setGraceDaysStr((String) p_map.get("GRACE_DAYS"));

        receiverTransferItemVO.setServiceClassCode((String) p_map.get("SERVICE_CLASS"));
        receiverTransferItemVO.setOldExporyInMillis((String) p_map.get("CAL_OLD_EXPIRY_DATE"));// @nu

        try {
            receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) p_map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        ;

        receiverTransferItemVO.setBundleTypes((String) p_map.get("BUNDLE_TYPES"));
        receiverTransferItemVO.setBonusBundleValidities((String) p_map.get("BONUS_BUNDLE_VALIDITIES"));

        // Update the Previous Balance in case of Post Paid Offline interface
        // with Credit Limit - Monthly Transfer Amount
        if (receiverVO.isPostOfflineInterface()) {
            final boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(receiverVO.getLastSuccessOn(), currentDate, BTSLUtil.PERIOD_MONTH);
            if (!isPeriodChange) {
                receiverTransferItemVO.setPreviousBalance(receiverTransferItemVO.getPreviousBalance() - receiverVO.getMonthlyTransferAmount());
            }
        }
        // TO DO Done for testing purpose should we use it or give exception in
        // this case
        if (receiverTransferItemVO.getPreviousExpiry() == null) {
            receiverTransferItemVO.setPreviousExpiry(currentDate);
        }

        try {
            receiverTransferItemVO.setLmbdebitvalue((Long.valueOf((String) p_map.get("LMB_ALLOWED_VALUE"))));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            LOG.error("populateReceiverItemsDetails", "Exception e" + e);
        }
        ;// @nu
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "setInterfaceDetails",
                requestIDStr,
                " Entered p_prefixID=" + p_prefixID + " p_listValueVO=" + p_listValueVO + " p_useInterfacePrefixVO=" + p_useInterfacePrefixVO + " p_MSISDNPrefixInterfaceMappingVO=" + p_MSISDNPrefixInterfaceMappingVO + "p_serviceSelectorInterfaceMappingVO" + p_serviceSelectorInterfaceMappingVO);
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

            if (p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                senderTransferItemVO.setPrefixID(p_prefixID);
                senderTransferItemVO.setInterfaceID(interfaceID);
                senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                senderAllServiceClassID = allServiceClassID;
                senderExternalID = externalID;
                senderInterfaceStatusType = interfaceStatusTy;
                p2pTransferVO.setSenderAllServiceClassID(senderAllServiceClassID);
                p2pTransferVO.setSenderInterfaceStatusType(senderInterfaceStatusType);

            } else if (p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
                receiverTransferItemVO.setPrefixID(p_prefixID);
                receiverTransferItemVO.setInterfaceID(interfaceID);
                receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                if (PretupsI.YES.equals(underProcessMsgReqd)) {
                    p2pTransferVO.setUnderProcessMsgReq(true);
                }
                receiverAllServiceClassID = allServiceClassID;
                receiverExternalID = externalID;
                receiverInterfaceStatusType = interfaceStatusTy;
                p2pTransferVO.setReceiverAllServiceClassID(receiverAllServiceClassID);
                p2pTransferVO.setReceiverInterfaceStatusType(receiverInterfaceStatusType);
            }
            // Check if interface status is Active or not.

            if (!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(interfaceStatusTy)) {
                // ChangeID=LOCALEMASTER
                // which language message to be set is determined from the
                // locale master table for the requested locale

                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(senderLocale)).getMessage())) {
                    p2pTransferVO.setSenderReturnMessage(message1);
                } else {
                    p2pTransferVO.setSenderReturnMessage(message2);
                }
                throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
            LOG.error("setInterfaceDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            final String methodName = "performReceiverAlternateRouting";
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[setInterfaceDetails]", transferID,
                senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("setInterfaceDetails", requestIDStr,
                    " Exiting with Sender Interface ID=" + senderTransferItemVO.getInterfaceID() + " Receiver Interface=" + receiverTransferItemVO.getInterfaceID());
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
     * @param pcurrentDate
     * @throws BTSLBaseException
     */
    private void updateSubscriberRoutingDetails(String p_userType, String p_networkCode, String p_interfaceID, String p_externalID, String p_msisdn, String p_interfaceCategory, String p_userID, Date pcurrentDate) throws BTSLBaseException {
        final String methodName = "updateSubscriberRoutingDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                requestIDStr,
                " Entered p_userType=" + p_userType + " p_networkCode=" + p_networkCode + " p_interfaceID=" + p_interfaceID + " p_externalID=" + p_externalID + " p_msisdn=" + p_msisdn + " p_interfaceCategory=" + p_interfaceCategory + " p_userID=" + p_userID + " pcurrentDate=" + pcurrentDate);
        }
        try {
            boolean updationReqd = false;
            if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
                updationReqd = senderDeletionReqFromSubRouting;
            } else {
                updationReqd = receiverDeletionReqFromSubRouting;
            }

            if (updationReqd) {
                PretupsBL.updateSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, pcurrentDate);
            } else {
                final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + p2pTransferVO
                    .getServiceType() + "_" + p_interfaceCategory);
                if (!updationReqd && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, pcurrentDate);
                    if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
                        senderInterfaceInfoInDBFound = true;
                        senderDeletionReqFromSubRouting = true;
                    } else {
                        receiverInterfaceInfoInDBFound = true;
                        receiverDeletionReqFromSubRouting = true;
                    }
                }
            }

        } catch (BTSLBaseException be) {
            LOG.error(methodName, "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[updateSubscriberRoutingDetails]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, requestIDStr, " Exiting ");
            }
        }
    }

    /**
     * Method to generate the Transfer ID
     * 
     * @param p_transferVO
     * @throws BTSLBaseException
     */
    /*
     * public static synchronized void generateTransferID(TransferVO
     * p_transferVO) throws BTSLBaseException
     * {
     * //if(LOG.isDebugEnabled()) LOG.debug("generateTransferID","Entered ");
     * String transferID=null;
     * try
     * {
     * //ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
     * //newTransferID=IDGenerator.getNextID(PretupsI.ID_GEN_P2P_TRANSFER_NO,
     * BTSLUtil
     * .getFinancialYearLastDigits(4),receiverVO.getNetworkCode(),p_transferVO
     * .getCreatedOn());
     * long currentReqTime= System.currentTimeMillis();
     * if(currentReqTime-_prevReqTime>=(60000))
     * transactionIDCounter=1;
     * else
     * transactionIDCounter=transactionIDCounter+1;
     * _prevReqTime=currentReqTime;
     * 
     * if(transactionIDCounter==0)
     * throw new BTSLBaseException("VchrConsController","generateTransferID",
     * PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * transferID=operatorUtil.formatP2PTransferID(p_transferVO,transactionIDCounter
     * );
     * if(transferID==null)
     * throw new BTSLBaseException("VchrConsController","generateC2STransferID",
     * PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * p_transferVO.setTransferID(transferID);
     * }
     * catch(BTSLBaseException be)
     * {
     * be.printStackTrace();
     * throw be;
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * throw new BTSLBaseException("VchrConsController","generateTransferID",
     * PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * }
     * }
     */
    public static synchronized void generateTransferID(TransferVO p_transferVO) throws BTSLBaseException {
        // if(LOG.isDebugEnabled()) LOG.debug("generateTransferID","Entered ");
        String transferID = null;
        Date mydate = null;
        String minut2Compare = null;

        final String methodName = "generateTransferID";
        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // newTransferID=IDGenerator.getNextID(PretupsI.ID_GEN_P2P_TRANSFER_NO,BTSLUtil.getFinancialYearLastDigits(4),receiverVO.getNetworkCode(),p_transferVO.getCreatedOn());
            mydate = new Date();
            p_transferVO.setCreatedOn(mydate);
            minut2Compare = _sdfCompare.format(mydate);
            final int currentMinut = Integer.parseInt(minut2Compare);

            if (currentMinut != prevMinut) {
                transactionIDCounter = 1;
                prevMinut = currentMinut;

            } else {
                transactionIDCounter++;

            }

            if (transactionIDCounter == 0) {
                throw new BTSLBaseException("VchrConsController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = operatorUtil.formatP2PTransferID(p_transferVO, transactionIDCounter);
            if (transferID == null) {
                throw new BTSLBaseException("VchrConsController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("VchrConsController", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
        }
    }

    public void handleLDCCRequest() throws BTSLBaseException {
        Connection con = null;MComConnectionI mcomCon = null;
        final String methodName = "handleLDCCRequest";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        try {
            final ListValueVO listValueVO = null;
            String requestStr = null;
            CommonClient commonClient = null;
            String senderValResponse = null;
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            // populate payment and service interface details for validate
            // action
            populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
            serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(senderVO.getNetworkCode() + "_" + serviceType + "_" + senderVO
                .getSubscriberType());
            senderVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(senderVO.getMsisdn()));
            final NetworkPrefixVO netPreVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix(), serviceInterfaceRoutingVO.getAlternateInterfaceType());
            final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(p2pTransferVO.getModule(),
                senderNetworkCode, p2pTransferVO.getPaymentMethodType());
            LoadController.decreaseResponseCounters(transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
            LoadController.decreaseTransactionInterfaceLoad(transferID, p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
            senderTransferItemVO.setPrefixID(netPreVO.getPrefixID());
            senderVO.setPrefixID(netPreVO.getPrefixID());
            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
            try {
                interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(senderTransferItemVO.getPrefixID(),
                    serviceInterfaceRoutingVO.getAlternateInterfaceType(), PretupsI.INTERFACE_VALIDATE_ACTION);
                setInterfaceDetails(senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, true, interfaceMappingVO, null);
            } catch (BTSLBaseException be) {
                throw be;
            }
            checkTransactionLoad(PretupsI.USER_TYPE_SENDER, senderTransferItemVO.getInterfaceID());

            // validate sender limits after Interface Validations
            SubscriberBL.validateSenderLimits(con, p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);

			if (mcomCon != null) {
				mcomCon.close("VchrConsController#handleLDCCRequest");
				mcomCon = null;
			}
			con = null;
            requestStr = getSenderValidateStr();
            commonClient = new CommonClient();

            LoadController.incrementTransactionInterCounts(transferID, LoadControllerI.SENDER_UNDER_VAL);

            TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr,
                PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Sending Request For MSISDN=" + senderMSISDN + " on ALternate Routing 1 to =" + senderTransferItemVO.getInterfaceID());
            }

            senderValResponse = commonClient.process(requestStr, transferID, intModCommunicationTypeS, intModIPS, intModPortS, intModClassNameS);

            TransactionLog.log(transferID, requestIDStr, senderMSISDN, senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse,
                PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            try {
                senderValidateResponse(senderValResponse, 1, 0);
                if (InterfaceErrorCodesI.SUCCESS.equals(senderTransferItemVO.getValidationStatus())) {
                    senderVO.setSubscriberType(serviceInterfaceRoutingVO.getAlternateInterfaceType());
                    p2pTransferVO.setTransferCategory(serviceInterfaceRoutingVO.getAlternateInterfaceType() + "-" + type);
                    p2pTransferVO.setPaymentMethodType(serviceInterfaceRoutingVO.getAlternateInterfaceType());
                    isUpdateRequired = true;
                    isSenderRoutingUpdate = true;
                    senderSubscriberType = serviceInterfaceRoutingVO.getAlternateInterfaceType();
                }
                if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(senderTransferItemVO.getValidationStatus())) {
                    receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                }

            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                throw e;
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VchrConsController[performSenderAlternateRouting]",
                transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performSenderAlternateRouting", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("VchrConsController#handleLDCCRequest");
				mcomCon = null;
			}
        	con=null;
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exit");
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
     * @param pcurrentDate
     * @throws BTSLBaseException
     */
    private void updateSubscriberAilternateRouting(String p_userType, String p_networkCode, String p_interfaceID, String p_externalID, String p_msisdn, String p_interfaceCategory, String p_userID, Date pcurrentDate) throws BTSLBaseException {
        final String methodName = "updateSubscriberAilternateRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                requestIDStr,
                " Entered p_userType=" + p_userType + " p_networkCode=" + p_networkCode + " p_interfaceID=" + p_interfaceID + " p_externalID=" + p_externalID + " p_msisdn=" + p_msisdn + " p_interfaceCategory=" + p_interfaceCategory + " p_userID=" + p_userID + " pcurrentDate=" + pcurrentDate);
        }
        try {
            // if(updationReqd)
            try {
                PretupsBL.updateSubscriberInterfaceAilternateRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, pcurrentDate);
            } catch (BTSLBaseException e) {
                LOG.errorTrace(methodName, e);
                if (PretupsErrorCodesI.ERROR_EXCEPTION.equals(e.getMessage())) {
                    PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, pcurrentDate);
                }
            }
            senderInterfaceInfoInDBFound = true;
            senderDeletionReqFromSubRouting = true;

        } catch (BTSLBaseException be) {
            LOG.error(methodName, "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "VchrConsController[updateSubscriberAilternateRouting]", transferID, senderMSISDN, senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, requestIDStr, " Exiting ");
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
            p2pTransferVO.setErrorCode(status + "_S");
            senderTransferItemVO.setTransferStatus(status);
            if (PretupsI.SERVICE_TYPE_EVD.equals(serviceType)) {
                receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                receiverTransferItemVO.setValidationStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
                receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.REQUEST_NOT_APPLICABLE);
            }
            strArr = new String[] { transferID, PretupsBL.getDisplayAmount(p2pTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException("EVDController", methodName, p2pTransferVO.getErrorCode(), 0, strArr, null);
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

        if ("null".equals(map.get("SERIAL_NUMBER"))) {
            throw new BTSLBaseException("EVDController", methodName, PretupsErrorCodesI.VOUCHER_NOT_FOUND);
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
        p2pTransferVO.setSerialNumber((String) map.get("SERIAL_NUMBER"));
        senderTransferItemVO.setTransferValue(Long.parseLong((String) map.get("PAYABLE_AMT")));
        p2pTransferVO.setTransferValue(Long.parseLong((String) map.get("PAYABLE_AMT")));
        p2pTransferVO.setRequestedAmount(Long.parseLong((String) map.get("PAYABLE_AMT")));
        final SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache
            .getRoutingControlDetails(p2pTransferVO.getReceiverNetworkCode() + "_" + p2pTransferVO.getServiceType() + "_" + PretupsI.INTERFACE_CATEGORY_VOMS);
        if (!vomsInterfaceInfoInDBFound && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
            PretupsBL.insertSubscriberInterfaceRouting(senderTransferItemVO.getInterfaceID(), vomsExternalID, receiverMSISDN, PretupsI.INTERFACE_CATEGORY_VOMS, senderVO
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
                    if (!p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                        final EvdUtil evdUtil = new EvdUtil();
                        final InterfaceVO interfaceVO = new InterfaceVO();
                        interfaceVO.setInterfaceId(senderTransferItemVO.getInterfaceID());
                        interfaceVO.setHandlerClass(senderTransferItemVO.getInterfaceHandlerClass());
                        evdUtil.updateVoucherForFailedTransaction(p2pTransferVO, networkInterfaceModuleVO, interfaceVO);
                    }

                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    // Event Handle to show that voucher could not be updated
                    // and is still Under process
                    LOG.error(
                        methodName,
                        " For transfer ID=" + transferID + " Error while updating voucher status for =" + p2pTransferVO.getSerialNumber() + " So leaving the voucher marked as under process. Exception: " + e
                            .getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EvdController[voucherUpdateSenderCreditBack]",
                        transferID, "", "",
                        "Error while updating voucher status for =" + p2pTransferVO.getSerialNumber() + " So leaving the voucher marked as under process. Exception: " + e
                            .getMessage());
                }
                mcomCon = new MComConnection();con=mcomCon.getConnection();
                if (transferDetailAdded) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "transferID=" + transferID + " Doing Sender Credit back ");
                    }
                    // updateSenderForFailedTransaction(con,p2pTransferVO);
                    // C2STransferItemVO
                    // senderCreditBackItemVO=(C2STransferItemVO)p2pTransferVO.getTransferItemList().get(2);
                    // senderCreditBackItemVO.setUpdateStatus(senderTransferItemVO.getUpdateStatus1());
                }

                // added by nilesh: consolidated for logger
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    addEntryInTransfers(con);
                }
                // Log the details if the transfer Details were added i.e. if
                // User was creditted
                /*
                 * if(_creditBackEntryDone) {
                 * BalanceLogger.log(userBalancesVO);
                 * }
                 */

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EvdController[voucherUpdateSenderCreditBack]",
                transferID, "", "", "Error while credit back sender, getting exception: " + e.getMessage());
        } finally {
			if (mcomCon != null) {
				mcomCon.close("VchrConsController#voucherUpdateSenderCreditBack");
				mcomCon = null;
			}
			con=null;
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting for transferID=" + transferID + " p_action=" + p_action);
            }
        }
    }

}
