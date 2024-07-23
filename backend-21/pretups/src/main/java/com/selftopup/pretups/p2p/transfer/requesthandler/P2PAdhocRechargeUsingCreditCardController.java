package com.selftopup.pretups.p2p.transfer.requesthandler;

/**
 * @(#)P2PAdhocRechargeUsingCreditCardController.java
 *                                                    Copyright(c) 2005, Bharti
 *                                                    Telesoft Int. Public Ltd.
 *                                                    All Rights Reserved
 *                                                    --------------------------
 *                                                    --------------------------
 *                                                    --------------------------
 *                                                    -------------------
 *                                                    Author Date History
 *                                                    --------------------------
 *                                                    --------------------------
 *                                                    --------------------------
 *                                                    -------------------
 *                                                    Abhijit Chauhan June
 *                                                    18,2005 Initial Creation
 *                                                    Gurjeet Singh Bedi
 *                                                    15/09/05 Modified
 *                                                    Abhijit Aug 10,2006
 *                                                    Modified for
 *                                                    ID=SUBTYPVALRECLMT
 *                                                    Ankit Zindal Nov 20,2006
 *                                                    ChangeID=LOCALEMASTER
 *                                                    Ashish Kumar July 03, 2007
 *                                                    Add for the transaction id
 *                                                    generation in the memory
 *                                                    Divyakant Verma Feb 12
 *                                                    2008 P2PRequestDailyLog
 *                                                    introduced to log time
 *                                                    taken by IN for validation
 *                                                    & topup.
 *                                                    --------------------------
 *                                                    --------------------------
 *                                                    --------------------------
 *                                                    ------------------
 */
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.BTSLMessages;
import com.selftopup.common.CommonClient;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.loadcontroller.LoadController;
import com.selftopup.loadcontroller.LoadControllerI;
import com.selftopup.loadcontroller.ReqNetworkServiceLoadController;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.cardgroup.businesslogic.CardGroupBL;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.pretups.inter.module.InterfaceErrorCodesI;
import com.selftopup.pretups.inter.util.InterfaceCloserI;
import com.selftopup.pretups.interfaces.businesslogic.InterfaceVO;
import com.selftopup.pretups.logging.P2PRequestDailyLog;
import com.selftopup.pretups.logging.TransactionLog;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.LookupsCache;
import com.selftopup.pretups.master.businesslogic.LookupsVO;
import com.selftopup.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.selftopup.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.selftopup.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.selftopup.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.selftopup.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.selftopup.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.processes.ResumeSuspendProcess;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.selftopup.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.selftopup.pretups.routing.master.businesslogic.ServiceInterfaceRoutingVO;
import com.selftopup.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.selftopup.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.ReceiverVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.subscriber.businesslogic.SubscriberVO;
import com.selftopup.pretups.transfer.businesslogic.TransferItemVO;
import com.selftopup.pretups.transfer.businesslogic.TransferVO;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.selftopup.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.selftopup.pretups.whitelist.businesslogic.WhiteListVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;

public class P2PAdhocRechargeUsingCreditCardController implements ServiceKeywordControllerI, Runnable {

    private Log _log = LogFactory.getLog(P2PAdhocRechargeUsingCreditCardController.class.getName());
    private P2PTransferVO _p2pTransferVO = null;
    private TransferItemVO _senderTransferItemVO = null;
    private TransferItemVO _receiverTransferItemVO = null;
    private TransferItemVO _senderCreditBackStatusVO = null;
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
    private String _intModCommunicationTypeR;
    private String _intModIPR;
    private int _intModPortR;
    private String _intModClassNameR;
    private String _transferID;
    private long _requestID;
    private String _requestIDStr;
    Locale _senderLocale = null;
    Locale _receiverLocale = null;
    private boolean _isCounterDecreased = false;
    private String _type;
    private String _serviceType;
    private boolean _finalTransferStatusUpdate = true;
    private boolean _decreaseTransactionCounts = false;
    private boolean _transferDetailAdded = false;
    private boolean _senderInterfaceInfoInDBFound = false;
    private boolean _receiverInterfaceInfoInDBFound = false;
    private String _senderAllServiceClassID = PretupsI.ALL;
    private String _receiverAllServiceClassID = PretupsI.ALL;
    private String _senderPostBalanceAvailable;
    private String _receiverPostBalanceAvailable;
    private String _senderCreditPostBalanceAvailable;
    private String _receiverExternalID = null;
    private String _senderExternalID = null;
    private RequestVO _requestVO = null;
    private boolean _processedFromQueue = false; // Flag to indicate that
                                                 // request has been processed
                                                 // from Queue
    private boolean _recValidationFailMessageRequired = false; // Whether
                                                               // Receiver Fail
                                                               // Message is
                                                               // required
                                                               // before
                                                               // validation
    private boolean _recTopupFailMessageRequired = false;// Whether Receiver
                                                         // Fail Message is
                                                         // required before
                                                         // topup
    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
    private boolean _useAlternateCategory = false; // Whether to use alternate
                                                   // interface category
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
    private String _oldInterfaceCategory = null; // The initial interface
                                                 // category that has to be used
    private String _newInterfaceCategory = null; // The alternate interface
                                                 // category that has to be used
    private boolean _senderDeletionReqFromSubRouting = false; // Whether to
                                                              // update in
                                                              // Subscriber
                                                              // Routing for
                                                              // sender MSISDN
    private boolean _receiverDeletionReqFromSubRouting = false; // Whether to
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
    private String _receiverIMSI = null;
    private String _senderIMSI = null;
    private NetworkPrefixVO _networkPrefixVO = null;
    private String _oldDefaultSelector = null;
    private String _newDefaultSelector = null;
    public static OperatorUtilI _operatorUtil = null;
    private String _senderInterfaceStatusType = null;
    private String _receiverInterfaceStatusType = null;
    private static int _transactionIDCounter = 0;
    // private static long _prevReqTime=0;
    private static int _prevMinut = 0;
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    // to update the P2P_Subscriber if subscriber found on Alternate Interface.
    private boolean _isUpdateRequired = false;
    private boolean isRoutingSecond = false;
    private boolean _isSenderRoutingUpdate = false;

    public P2PAdhocRechargeUsingCreditCardController() {
        _p2pTransferVO = new P2PTransferVO();
        _currentDate = new Date();
        if (BTSLUtil.NullToString(Constants.getProperty("P2P_REC_GEN_FAIL_MSG_REQD_V")).equals("Y")) {
            _recValidationFailMessageRequired = true;
        }
        if (BTSLUtil.NullToString(Constants.getProperty("P2P_REC_GEN_FAIL_MSG_REQD_T")).equals("Y")) {
            _recTopupFailMessageRequired = true;
        }
    }

    // Loads operator specific class
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
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
    public void process(RequestVO p_requestVO) {
        Connection con = null;
        _requestIDStr = p_requestVO.getRequestIDStr();
        boolean receiverMessageSendReq = false;
        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestIDStr, "Entered");
        }
        try {
            _requestVO = p_requestVO;
            _senderVO = (SenderVO) p_requestVO.getSenderVO();

            // If user is not already registered then register the user with
            // status as NEW and Default PIN
            // sonali changes
            if (_senderVO == null) {
            }

            _senderLocale = p_requestVO.getSenderLocale();
            _receiverLocale = p_requestVO.getReceiverLocale();

            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestIDStr, "_senderLocale=" + _senderLocale + " _receiverLocale=" + _receiverLocale);
            }

            TransactionLog.log("", p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_RECIVED, "Received Request From Receiver", PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            _requestID = p_requestVO.getRequestID();
            _type = p_requestVO.getType();

            _serviceType = p_requestVO.getServiceType();

            populateVOFromRequest(p_requestVO); // step 1. we're setting the
                                                // "_p2pTransferVO" here, that
                                                // we shall pass in
                                                // "validateP2PAdhocRechargeMessageFormat()"

            con = OracleUtil.getConnection();
            _senderVO.setModifiedBy(_senderVO.getUserID());
            _senderVO.setModifiedOn(_currentDate);

            // validate message format "Vikas Singh"
            // if(_log.isDebugEnabled())_log.debug("process",_requestIDStr,"VIKAS _p2pTransferVO="+_p2pTransferVO);
            _operatorUtil.validateP2PAdhocRechargeMessageFormat(con, p_requestVO, _p2pTransferVO, _senderVO);// step
                                                                                                             // 2.
            _senderVO.setSubscriberType("PG");
            // Block added to avoid decimal amount in credit transfer
            if (!BTSLUtil.isStringIn(_serviceType, SystemPreferences.DECIMAL_ALLOWED_IN_SERVICES)) {
                try {
                    String displayAmt = PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount());
                    Long.parseLong(displayAmt);
                } catch (Exception e) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_AMOUNT);
                }
            }
            _receiverLocale = p_requestVO.getReceiverLocale();

            _receiverVO = (ReceiverVO) _p2pTransferVO.getReceiverVO();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));
            // for ussd
            _p2pTransferVO.setCellId(_requestVO.getCellId());
            _p2pTransferVO.setSwitchId(_requestVO.getSwitchId());

            if (!SystemPreferences.P2P_ALLOW_SELF_TOPUP && _senderVO.getMsisdn().equals(_receiverVO.getMsisdn())) {
                _log.error("process", _requestIDStr, "Sender and receiver MSISDN are same, Sender MSISDN=" + _senderVO.getMsisdn() + " Receiver MSISDN=" + _receiverVO.getMsisdn());
                throw new BTSLBaseException("", "process", SelfTopUpErrorCodesI.ERROR_P2P_SAME_MSISDN_TRANSFER_NOTALLWD, 0, new String[] { _receiverVO.getMsisdn() }, null);
            }

            PretupsBL.getSelectorValueFromCode(p_requestVO); // step 3.

            // Get the Interface Category routing details based on the receiver
            // Network Code and Service type
            if (_type.equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + _senderVO.getSubscriberType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", _requestIDStr, "For =" + _receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO.getAlternateInterfaceType() + " _oldDefaultSelector=" + _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "_newDefaultSelector= " + _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode());
                    }

                    _type = _serviceInterfaceRoutingVO.getInterfaceType();
                    _oldInterfaceCategory = _type;
                    _oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                    _useAlternateCategory = _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    _newInterfaceCategory = _serviceInterfaceRoutingVO.getAlternateInterfaceType();
                    _newDefaultSelector = _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
                } else {
                    _log.info("process", _requestIDStr, "Service Interface Routing control Not defined, thus using default type=" + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSFER_DEFAULT_SERVICETYPE));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[process]", "", _senderMSISDN, _senderNetworkCode, "Service Interface Routing control Not defined, thus using default type=" + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSFER_DEFAULT_SERVICETYPE));
                    _type = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSFER_DEFAULT_SERVICETYPE);
                    // _oldDefaultSelector=String.valueOf(SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
                    // Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_p2pTransferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        _oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                }
            } else {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + _senderVO.getSubscriberType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", _requestIDStr, "For =" + _receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO.getAlternateInterfaceType() + " _oldDefaultSelector=" + _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() + "_newDefaultSelector= " + _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode());
                    }
                    _oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                } else {
                    // _oldDefaultSelector=String.valueOf(SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
                    // Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(_p2pTransferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        _oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                    _log.info("process", _requestIDStr, "Service Interface Routing control Not defined, thus using default Selector=" + _oldDefaultSelector);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[process]", "", _senderMSISDN, _senderNetworkCode, "Service Interface Routing control Not defined, thus using default selector=" + _oldDefaultSelector);
                }
            }

            // If the interface category does not match with the Receiver
            // subscriber type got from validation from
            // network prefixes then load the new prefix ID against the
            // interface category
            // If not found then check whether Alternate has to be used or not ,
            // if yes then use the old prefix ID
            // already loaded and set the _useAlternateCategory=false denoting
            // that do not perform alternate interface
            // category routing again, If Not required then give the error
            if (!_receiverVO.getSubscriberType().equals(_type)) {
                _networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _type);
                if (_networkPrefixVO != null) {
                    PretupsBL.checkNumberPortability(con, _requestIDStr, _receiverVO.getMsisdn(), _networkPrefixVO);
                    _receiverVO.setNetworkCode(_networkPrefixVO.getNetworkCode());
                    _receiverVO.setPrefixID(_networkPrefixVO.getPrefixID());
                    _receiverVO.setSubscriberType(_networkPrefixVO.getSeriesType());
                } else if (_useAlternateCategory) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", _requestIDStr, "Network Prefix Not Found For Series=" + _receiverVO.getMsisdnPrefix() + " and Type=" + _type + " and thus using Type as =" + _newInterfaceCategory + " _useAlternateCategory was true");
                    }
                    _useAlternateCategory = false;
                    _type = _newInterfaceCategory;
                    _oldDefaultSelector = _newDefaultSelector;
                    _interfaceCatRoutingDone = true;
                } else {
                    // Refuse the Request
                    _log.error(this, "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[process]", "", "", "", "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But alternate Category Routing was required on interface");
                    throw new BTSLBaseException("", "process", SelfTopUpErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { _receiverVO.getMsisdn() }, null);
                }
            } else {
                _networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _type);
                PretupsBL.checkNumberPortability(con, _requestIDStr, _receiverVO.getMsisdn(), _networkPrefixVO);
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

            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestIDStr, "_receiverVO:" + _receiverVO);
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
            _receiverVO.setModule(_p2pTransferVO.getModule());
            _receiverVO.setCreatedDate(_currentDate);
            _receiverVO.setLastTransferOn(_currentDate);
            _p2pTransferVO.setReceiverMsisdn(_receiverMSISDN);
            _p2pTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
            _p2pTransferVO.setSubService(p_requestVO.getReqSelector());
            _p2pTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());

            // Validates the network service status
            PretupsBL.validateNetworkService(_p2pTransferVO);// Step 4.
            receiverMessageSendReq = true;

            // Check Network Load : If true then pass the request else refuse
            // the request
            // LoadController.checkNetworkLoad(_requestID,_senderNetworkCode,LoadControllerI.NETWORK_NEW_REQUEST);
            // The following check is commented because if default payment
            // method is not used then specific payment method will be loaded
            // according to service type.
            /*
             * if((!PretupsI.YES.equals(_p2pTransferVO.getDefaultPaymentMethod())
             * )&&!ServicePaymentMappingCache.isMappingExist(_p2pTransferVO.
             * getServiceType
             * (),_senderSubscriberType,_p2pTransferVO.getPaymentMethodType()))
             * {
             * throw new
             * BTSLBaseException("P2PAdhocRechargeUsingCreditCardController"
             * ,"process"
             * ,PretupsErrorCodesI.ERROR_NOTFOUND_SERVICEPAYMENTMETHOD);
             * }
             */

            // check receiver barred or not
            try {
                PretupsBL.checkMSISDNBarred(con, _receiverVO.getMsisdn(), _receiverVO.getNetworkCode(), _p2pTransferVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
                // check Black list restricted subscribers not allowed for
                // recharge or for CP2P services.
                if (SystemPreferences.CHK_BLK_LST_STAT) {
                    _operatorUtil.isRestrictedSubscriberAllowed(con, _receiverVO.getMsisdn(), _senderMSISDN);
                }

            } catch (BTSLBaseException be) {
                if (be.isKey() && (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                    _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
                }
                throw be;
            }

            PretupsBL.loadRecieverControlLimits(con, p_requestVO.getRequestIDStr(), _p2pTransferVO);// Step
                                                                                                    // 5.
            _receiverVO.setUnmarkRequestStatus(true);
            try {
                con.commit();
            } catch (Exception e) {
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "process", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            // check subscriber details for skey requirement
            if (SystemPreferences.SKEY_REQUIRED && _senderVO.getSkeyRequired().equals(PretupsI.YES)) {
                // Call the method to handle SKey related transfers
                processSKeyGen(con);
            } else {
                processTransfer(con);// Step 6. in here we set the final amount
                                     // to be transferred in transferVO
                p_requestVO.setTransactionID(_transferID);
                _receiverVO.setLastTransferID(_transferID);
                TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), _senderVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Generated Transfer ID", PretupsI.TXN_LOG_STATUS_SUCCESS, "Source Type=" + _p2pTransferVO.getSourceType() + " Gateway Code=" + _p2pTransferVO.getRequestGatewayCode());

                populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION); // step
                                                                                                                  // 7.

                _p2pTransferVO.setTransferCategory(_senderSubscriberType + "-" + _type);

                _p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
                _p2pTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);

                SubscriberBL.validateSenderLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);

                _p2pTransferVO.setReceiverSubscriberType(_receiverTransferItemVO.getInterfaceType());

                PretupsBL.validateRecieverLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

                try {
                    con.commit();
                } catch (Exception e) {
                    throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "process", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                }
                try {
                    con.close();
                } catch (Exception e) {
                    throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "process", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                }
                con = null;

                checkTransactionLoad();
                _decreaseTransactionCounts = true;

                if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
                    processValidationRequest();
                    p_requestVO.setSenderMessageRequired(_p2pTransferVO.isUnderProcessMsgReq());
                    p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());
                    // p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    p_requestVO.setDecreaseLoadCounters(false);
                } else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    p_requestVO.setSenderReturnMessage(getSndrUPMsgBeforeValidation());
                    // p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    Thread _controllerThread = new Thread(this);
                    _controllerThread.start();
                    p_requestVO.setDecreaseLoadCounters(false);
                } else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                    // p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                    processValidationRequest();
                    run();
                    p_requestVO.setDecreaseLoadCounters(false);
                }

                // Parameter set to indicate that instance counters will not be
                // decreased in receiver for this transaction
                p_requestVO.setDecreaseLoadCounters(false);
            }
        } catch (BTSLBaseException be) {
            _log.error("process", "Exception be:" + be.getMessage());
            // be.printStackTrace();
            p_requestVO.setSuccessTxn(false);
            if (_senderVO != null) {
                try {
                    if (con == null) {
                        con = OracleUtil.getConnection();
                    }
                    SubscriberBL.updateSubscriberLastDetails(con, _p2pTransferVO, _senderVO, _currentDate, SelfTopUpErrorCodesI.TXN_STATUS_FAIL);

                } catch (BTSLBaseException bex) {
                    bex.printStackTrace();
                    // _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Base Exception while updating Subscriber Last Details:" + bex.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception while updating Subscriber Last Details:" + e.getMessage());
                    _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                }

                // Unmarking Receiver last request status
                try {
                    if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                        PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
                    }
                } catch (BTSLBaseException bex) {
                    bex.printStackTrace();
                    // _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Base Exception:" + bex.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + e.getMessage());
                    _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            }
            /*
             * try killed by sanjay and moved in the above if condition 15/08/06
             * {
             * if(_receiverVO!=null && _receiverVO.isUnmarkRequestStatus())
             * {
             * PretupsBL.unmarkReceiverLastRequest(con,p_requestVO.getRequestIDStr
             * (),_receiverVO);
             * }
             * }
             * catch(BTSLBaseException bex)
             * {
             * bex.printStackTrace();
             * //_p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION
             * );
             * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
             * EventStatusI.RAISED,EventLevelI.MAJOR,
             * "P2PAdhocRechargeUsingCreditCardController[process]"
             * ,_transferID,_senderMSISDN
             * ,_senderNetworkCode,"Leaving Reciever Unmarked Base Exception:"
             * +bex.getMessage());
             * }
             * catch(Exception e)
             * {
             * e.printStackTrace();
             * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
             * EventStatusI.RAISED,EventLevelI.MAJOR,
             * "P2PAdhocRechargeUsingCreditCardController[process]"
             * ,_transferID,_senderMSISDN
             * ,_senderNetworkCode,"Leaving Reciever Unmarked Exception:"
             * +e.getMessage());
             * _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION
             * );
             * }
             */

            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            if (_recValidationFailMessageRequired) {
                if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (_transferID != null) {
                        _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                    } else {
                        _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                    }
                }
            }

            if (!BTSLUtil.isNullString(_p2pTransferVO.getSenderReturnMessage())) {
                p_requestVO.setSenderReturnMessage(_p2pTransferVO.getSenderReturnMessage());
            }

            if (be.isKey()) {
                if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                    _p2pTransferVO.setErrorCode(be.getMessageKey());
                }
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            if (_transferID != null && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            TransactionLog.log(_transferID, _requestIDStr, p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _p2pTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            // Populate the P2PRequestDailyLog and log
            P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));
        } catch (Exception e) {
            _log.error("process", "Exception e:" + e.getMessage());
            e.printStackTrace();
            p_requestVO.setSuccessTxn(false);
            try {
                if (con == null) {
                    con = OracleUtil.getConnection();
                }
                SubscriberBL.updateSubscriberLastDetails(con, _p2pTransferVO, _senderVO, _currentDate, SelfTopUpErrorCodesI.TXN_STATUS_FAIL);

            } catch (BTSLBaseException bex) {
                bex.printStackTrace();
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            } catch (Exception ex) {
                ex.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    PretupsBL.unmarkReceiverLastRequest(con, p_requestVO.getRequestIDStr(), _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                bex.printStackTrace();
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            } catch (Exception ex1) {
                ex1.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + ex1.getMessage());
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            if (_recValidationFailMessageRequired) {
                if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (_transferID != null) {
                        _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                    } else {
                        _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                    }
                }
            }

            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            _log.error("process", "Exception:" + e.getMessage());
            e.printStackTrace();
            if (_transferID != null && _decreaseTransactionCounts) {
                _isCounterDecreased = true;
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _p2pTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            // Populate the P2PRequestDailyLog and log
            P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));
        } finally {
            try {
                if (con == null) {
                    con = OracleUtil.getConnection();
                }
                if (_transferID != null && !_transferDetailAdded && (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) || (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) && !p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_UNDER_PROCESS)))) {
                    addEntryInTransfers(con);
                } else if (_transferID != null && p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                    _log.info("process", _requestIDStr, "Send the message to MSISDN=" + p_requestVO.getFilteredMSISDN() + " Transfer ID=" + _transferID + " But not added entry in Transfers yet");
                }
            } catch (BTSLBaseException be) {
                be.printStackTrace();
                _log.error("process", "BTSL Base Exception:" + be.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("process", "Exception:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            }
            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.REQ_NOT_PROCESS);
            }
            if (_isCounterDecreased) {
                p_requestVO.setDecreaseLoadCounters(false);
            }
            if (con != null) {
                try {
                    con.commit();
                } catch (Exception e) {
                }
                ;
                try {
                    con.close();
                } catch (Exception e) {
                }
                con = null;
            }
            if (receiverMessageSendReq) {
                if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P.equals(_receiverTransferItemVO.getValidationStatus())) {
                        _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P + "_R"));
                    }
                    BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
                    (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                } else if (_p2pTransferVO.getReceiverReturnMsg() != null) {
                    (new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                }
            }
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
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
        if (_log.isDebugEnabled()) {
            _log.debug("processSKeyGen", "Entered");
        }
        try {
            // validate skey details for generation
            // generate skey
            PretupsBL.generateSKey(p_con, _p2pTransferVO);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("processSKeyGen", "Exception e:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[processSKeyGen]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "processSKeyGen", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
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
        if (_log.isDebugEnabled()) {
            _log.debug("processValidationRequest", "Entered and performing validations for transfer ID=" + _transferID);
        }
        Connection con = null;
        try {
            CommonClient commonClient = new CommonClient();
            InterfaceVO interfaceVO;
            _itemList = new ArrayList();

            _itemList.add(_senderTransferItemVO);
            _itemList.add(_receiverTransferItemVO);
            _p2pTransferVO.setTransferItemList(_itemList);

            NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_p2pTransferVO.getModule(), _senderNetworkCode, _p2pTransferVO.getPaymentMethodType());
            _intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
            _intModIPS = networkInterfaceModuleVOS.getIP();
            _intModPortS = networkInterfaceModuleVOS.getPort();
            _intModClassNameS = networkInterfaceModuleVOS.getClassName();
            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

            String requestStr = getSenderValidateStr();

            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            // if self topup is not allowed only then check sender at IN else
            // only receiver's validations are enough.
            if (!(SystemPreferences.P2P_ALLOW_SELF_TOPUP && _senderVO.getMsisdn().equals(_receiverVO.getMsisdn()))) {
                String senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                if (_log.isDebugEnabled()) {
                    _log.debug("processValidationRequest", "senderValResponse From IN Module=" + senderValResponse);
                }

                try {
                    // Get the Sender validate response and processes the same

                    updateForSenderValidateResponse(senderValResponse);
                } catch (BTSLBaseException be) {
                    // TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+_senderTransferItemVO.getInterfaceResponseCode());
                    LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    if (_senderDeletionReqFromSubRouting && _senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
                        PretupsBL.deleteSubscriberInterfaceRouting(_senderMSISDN, PretupsI.INTERFACE_CATEGORY_PRE);
                    }
                    con = OracleUtil.getConnection();
                    // validate sender limits after Interface Validations
                    SubscriberBL.validateSenderLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL);

                    // This block will send different error code if the user is
                    // already registered at a particular interface
                    // Category but is not found on that interface while
                    // validation request
                    if (!_senderVO.isDefUserRegistration() && _senderTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
                        throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "processValidationRequest", SelfTopUpErrorCodesI.P2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL, 0, new String[] { ((LookupsVO) LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE, _senderVO.getSubscriberType())).getLookupName() }, null);
                    }
                    throw be;
                }

                con = OracleUtil.getConnection();

                LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);

                // If request is taking more time till validation of sender than
                // reject the request.
                interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_senderTransferItemVO.getInterfaceID());
                if ((System.currentTimeMillis() - _p2pTransferVO.getRequestStartTime()) > interfaceVO.getValExpiryTime()) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till validation of sender");
                    throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "processValidationRequest", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION_TKING_TIME_TILL_VAL_S);
                }
                interfaceVO = null;

                // Get the service Class ID based on the code
                PretupsBL.validateServiceClassChecks(con, _senderTransferItemVO, _p2pTransferVO, PretupsI.P2P_MODULE, _requestVO.getServiceType());
                _senderVO.setServiceClassCode(_senderTransferItemVO.getServiceClassCode());
                _senderVO.setUsingAllServiceClass(_senderTransferItemVO.isUsingAllServiceClass());
                // update P2P_SUBSCRIBERS if ldcc and found on ailternate
                // Interface type.
                if (_isUpdateRequired) {
                    PretupsBL.updateP2PSubscriberDetail(_senderVO);
                }
                if (_isSenderRoutingUpdate && !_isUpdateRequired) {
                    updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, _p2pTransferVO.getNetworkCode(), _senderTransferItemVO.getInterfaceID(), _senderExternalID, _senderMSISDN, _p2pTransferVO.getPaymentMethodType(), _senderVO.getUserID(), _currentDate);
                } else if (_isSenderRoutingUpdate && _isUpdateRequired) {
                    updateSubscriberAilternateRouting(PretupsI.USER_TYPE_SENDER, _p2pTransferVO.getNetworkCode(), _senderTransferItemVO.getInterfaceID(), _senderExternalID, _senderMSISDN, _p2pTransferVO.getPaymentMethodType(), _senderVO.getUserID(), _currentDate);
                }
                // validate sender limits after Interface Validations
                SubscriberBL.validateSenderLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL);

                if (con != null) {
                    try {
                        con.rollback();
                    } catch (Exception be) {
                    }
                    try {
                        con.close();
                    } catch (Exception be) {
                    }
                    con = null;
                }
                // send validation request for receiver
                LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_VAL);
            }

            NetworkInterfaceModuleVO networkInterfaceModuleVOR = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_p2pTransferVO.getModule(), _receiverVO.getNetworkCode(), _type);
            _intModCommunicationTypeR = networkInterfaceModuleVOR.getCommunicationType();
            _intModIPR = networkInterfaceModuleVOR.getIP();
            _intModPortR = networkInterfaceModuleVOR.getPort();
            _intModClassNameR = networkInterfaceModuleVOR.getClassName();

            String receiverValStr = getReceiverValidateStr();
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            String receiverValResponse = commonClient.process(getReceiverValidateStr(), _transferID, _intModCommunicationTypeR, _intModIPR, _intModPortR, _intModClassNameR);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug("processValidationRequest", "receiverValResponse From IN Module=" + receiverValResponse);
            }

            try {
                // Get the Receiver validate response and processes the same
                updateForReceiverValidateResponse(receiverValResponse);
            } catch (BTSLBaseException be) {
                // TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Failed",PretupsI.TXN_LOG_STATUS_FAIL,"Getting Code="+_receiverVO.getInterfaceResponseCode());
                LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
                if (_receiverDeletionReqFromSubRouting && _receiverTransferItemVO.getValidationStatus().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
                    PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, PretupsI.INTERFACE_CATEGORY_PRE);
                }
                con = OracleUtil.getConnection();
                // validate receiver limits after Interface Validations
                PretupsBL.validateRecieverLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.P2P_MODULE);

                // No need to check for sender limits as the receiver created
                // the problem
                throw be;
            }

            con = OracleUtil.getConnection();

            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.RECEIVER_VAL_RESPONSE);

            // If request is taking more time till validation of receiver than
            // reject the request.
            interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_receiverTransferItemVO.getInterfaceID());
            if ((System.currentTimeMillis() - _p2pTransferVO.getRequestStartTime()) > interfaceVO.getValExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till validation of reciever");
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "processValidationRequest", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION_TKING_TIME_TILL_VAL_R);
            }
            interfaceVO = null;

            // Get the service Class ID based on the code
            PretupsBL.validateServiceClassChecks(con, _receiverTransferItemVO, _p2pTransferVO, PretupsI.P2P_MODULE, _requestVO.getServiceType());
            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());
            _receiverVO.setUsingAllServiceClass(_receiverTransferItemVO.isUsingAllServiceClass());

            if (SystemPreferences.P2P_ALLOW_SELF_TOPUP && _senderVO.getMsisdn().equals(_receiverVO.getMsisdn())) {
                _senderVO.setServiceClassCode(_receiverVO.getServiceClassCode());
            }
            // validate sender receiver service class,validate transfer value
            PretupsBL.validateTransferRule(con, _p2pTransferVO, PretupsI.P2P_MODULE);
            // validate receiver limits after Interface Validations
            PretupsBL.validateRecieverLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INVAL, PretupsI.P2P_MODULE);

            // calculate card group details
            CardGroupBL.calculateCardGroupDetails(con, _p2pTransferVO, PretupsI.P2P_MODULE, true);

            /*
             * //validate sender limits after Card Group Calculations
             * SubscriberBL.validateSenderLimits(con,_p2pTransferVO,PretupsI.
             * TRANS_STAGE_AFTER_FIND_CGROUP);
             */

            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "After Card Group Set Id=" + _p2pTransferVO.getCardGroupSetID() + " Code" + _p2pTransferVO.getCardGroupCode() + " Card ID=" + _p2pTransferVO.getCardGroupID() + " Sender Access fee=" + _p2pTransferVO.getSenderAccessFee() + " Tax1 =" + _p2pTransferVO.getSenderTax1Value() + " Tax2=" + _p2pTransferVO.getSenderTax1Value() + " Talk Time=" + _p2pTransferVO.getSenderTransferValue() + " Receiver Access fee=" + _p2pTransferVO.getReceiverAccessFee() + " Tax1 =" + _p2pTransferVO.getReceiverTax1Value() + " Tax2=" + _p2pTransferVO.getReceiverTax1Value() + " Bonus=" + _p2pTransferVO.getReceiverBonusValue() + " Val Type=" + _p2pTransferVO.getReceiverValPeriodType() + " Validity=" + _p2pTransferVO.getReceiverValidity() + " Talk Time=" + _p2pTransferVO.getReceiverTransferValue(), PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            // TransactionLog.log(_transferID,_requestIDStr,_receiverMSISDN,_receiverVO.getNetworkCode(),PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"After Card Group Access fee="+_p2pTransferVO.getReceiverAccessFee()+" Tax1 ="+_p2pTransferVO.getReceiverTax1Value()+" Tax2="+_p2pTransferVO.getReceiverTax1Value()+" Bonus="+_p2pTransferVO.getReceiverBonusValue()+" Val Type="+_p2pTransferVO.getReceiverValPeriodType()+" Validity="+_p2pTransferVO.getReceiverValidity()+" Talk Time="+_p2pTransferVO.getReceiverTransferValue(),PretupsI.TXN_LOG_STATUS_SUCCESS,"");

            // Update Daily Counters for the sender and Buddy if there
            SubscriberBL.increaseTransferOutCounts(con, _senderTransferItemVO.getServiceClass(), _p2pTransferVO);

            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_UNDER_PROCESS);

            // populate payment and service interface details
            populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);

            // update transfer details in database
            PretupsBL.addTransferDetails(con, _p2pTransferVO);
            _transferDetailAdded = true;

            con.commit();
            try {
                con.close();
            } catch (Exception be) {
            }
            con = null;

            // Push Under Process Message to Reciever , this might have to be
            // implemented on flag basis whether to send message or not
            if (_p2pTransferVO.isUnderProcessMsgReq()) {
                // In case of Self TopUp,Underprocessrequest message will be
                // given to sender only.
                if (!_p2pTransferVO.getSenderMsisdn().equals(_p2pTransferVO.getReceiverMsisdn())) {
                    (new PushMessage(_receiverMSISDN, getReceiverUnderProcessMessage(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
                }
            }

            // Thread.sleep(5000);
            // If request is taking more time till credit transfer of subscriber
            // than reject the request.
            interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(_senderTransferItemVO.getInterfaceID());
            if ((System.currentTimeMillis() - _p2pTransferVO.getRequestStartTime()) > interfaceVO.getTopUpExpiryTime()) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception: System is taking more time till credit transfer");
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "run", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION_TKING_TIME_TILL_TOPUP);
            }
            interfaceVO = null;

            if (_p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON) || _processedFromQueue) {
                // create new Thread
                Thread _controllerThread = new Thread(this);
                _controllerThread.start();
            }
        } catch (BTSLBaseException be) {

            if (con != null) {
                con.rollback();
            }
            if (_recValidationFailMessageRequired) {
                if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((SelfTopUpErrorCodesI.P2P_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                }
            }
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _p2pTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            }

            if (BTSLUtil.isNullString(_receiverTransferItemVO.getTransferStatus()) || _receiverTransferItemVO.getTransferStatus().equals(InterfaceErrorCodesI.SUCCESS)) {
                _receiverTransferItemVO.setTransferStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                _receiverTransferItemVO.setUpdateStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                _senderTransferItemVO.setTransferStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                _senderTransferItemVO.setUpdateStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
            }
            _log.error("P2PAdhocRechargeUsingCreditCardController[processValidationRequest]", "Getting BTSL Base Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            if (con != null) {
                con.rollback();
            }
            if (_recValidationFailMessageRequired) {
                if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((SelfTopUpErrorCodesI.P2P_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                }
            }
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            _log.error("P2PAdhocRechargeUsingCreditCardController[processValidationRequest]", "Getting Exception:" + e.getMessage());
            if (BTSLUtil.isNullString(_receiverTransferItemVO.getTransferStatus()) || _receiverTransferItemVO.getTransferStatus().equals(InterfaceErrorCodesI.SUCCESS)) {
                _receiverTransferItemVO.setTransferStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                _receiverTransferItemVO.setUpdateStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                _senderTransferItemVO.setTransferStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                _senderTransferItemVO.setUpdateStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
            }
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception be) {
                }
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
        if (_log.isDebugEnabled()) {
            _log.debug("processTransfer", _p2pTransferVO.getRequestID(), "Entered");
        }
        try {
            _p2pTransferVO.setTransferDate(_currentDate);
            _p2pTransferVO.setTransferDateTime(_currentDate);
            // PretupsBL.generateTransferID(_p2pTransferVO);
            generateTransferID(_p2pTransferVO);
            _transferID = _p2pTransferVO.getTransferID();
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
             * if(_p2pTransferVO.getDefaultPaymentMethod().equals(PretupsI.YES)&&
             * _senderMSISDN.equals(_receiverMSISDN))
             * {
             * throw new
             * BTSLBaseException(this,"processTransfer",PretupsErrorCodesI
             * .ERROR_NOTALLOWED_SELFTOPUPDEFAULTPMT);
             * }
             */

            // Get the product Info based on the service type
            PretupsBL.getProductFromServiceType(p_con, _p2pTransferVO, _serviceType, PretupsI.P2P_MODULE);

        } catch (BTSLBaseException be) {
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            if (be.isKey()) {
                _p2pTransferVO.setErrorCode(be.getMessageKey());
            } else {
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            throw be;
        } catch (Exception e) {
            if (_recValidationFailMessageRequired) {
                if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (_transferID != null) {
                        _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                    } else {
                        _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                    }
                }
            }
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            e.printStackTrace();

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[processTransfer]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "processTransfer", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * This method will perform either topup in thread or both validation and
     * topup on thread based on Flow Type
     * 
     * @throws BTSLBaseException
     */
    public void run() {
        if (_log.isDebugEnabled()) {
            _log.debug("run", _transferID, "Entered");
        }
        BTSLMessages btslMessages = null;
        boolean onlyDecreaseCounters = false;
        Connection con = null;
        try {
            // Perform the validation of parties if Flow type is thread
            if (_p2pTransferVO.getMsgGatewayFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD) && !_processedFromQueue) {
                processValidationRequestInThread();
            }

            // send validation request for sender
            CommonClient commonClient = new CommonClient();
            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_TOP);

            String requestStr = getSenderDebitAdjustStr();
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            String senderDebitResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP, senderDebitResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug("run", _transferID, "senderDebitResponse From IN Module=" + senderDebitResponse);
            }
            try {
                // Get the Sender Debit response and processes the same
                updateForSenderDebitResponse(senderDebitResponse);
            } catch (BTSLBaseException be) {
                TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Getting Code=" + _senderVO.getInterfaceResponseCode());

                LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_TOP_RESPONSE);

                // con=OracleUtil.getConnection();

                // If transaction is Ambigous and Preference flag is Set to true
                // (Whether credit back is true in ambigous case)
                // Then credit back the sender
                if (_p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS) && SystemPreferences.P2P_SNDR_CREDIT_BK_AMB_STATUS || _p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_FAIL)) {
                    onlyDecreaseCounters = true;
                    creditBackSenderForFailedTrans(commonClient, onlyDecreaseCounters);
                }

                /*
                 * //validate sender limits after Interface Updation
                 * SubscriberBL.validateSenderLimits(null,_p2pTransferVO,PretupsI
                 * .TRANS_STAGE_AFTER_INTOP);
                 */

                // PretupsBL.unmarkReceiverLastRequest(con,_transferID,_receiverVO);
                throw be;
            }
            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_TOP_RESPONSE);

            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_TOP);

            String receiverStr = getReceiverCreditStr();
            // send validation request for receiver
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INTOP, receiverStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            String receiverCreditResponse = commonClient.process(receiverStr, _transferID, _intModCommunicationTypeR, _intModIPR, _intModPortR, _intModClassNameR);
            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INTOP, receiverCreditResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug("run", _transferID, "receiverCreditResponse From IN Module=" + receiverCreditResponse);
            }

            try {
                // Get the Receiver Credit response and processes the same
                updateForReceiverCreditResponse(receiverCreditResponse);
            } catch (BTSLBaseException be) {
                TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Getting Code=" + _receiverVO.getInterfaceResponseCode());
                // No need to check for sender limits as the receiver created
                // the problem
                LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_TOP_RESPONSE);

                // con=OracleUtil.getConnection();

                // If transaction is Ambigous and Preference flag is Set to true
                // (Whether credit back is true in ambigous case)
                // Then credit back the sender
                if (_p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS) && SystemPreferences.P2P_SNDR_CREDIT_BK_AMB_STATUS || _p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_FAIL)) {
                    creditBackSenderForFailedTrans(commonClient, onlyDecreaseCounters);
                }

                // validate receiver limits after Interface Updation
                PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.P2P_MODULE);

                throw be;
            } catch (Exception e) {
                LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_TOP_RESPONSE);

                // con=OracleUtil.getConnection();
                // If transaction is Ambigous and Preference flag is Set to true
                // (Whether credit back is true in ambigous case)
                // Then credit back the sender

                if (_p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS) && SystemPreferences.P2P_SNDR_CREDIT_BK_AMB_STATUS || _p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_FAIL)) {
                    creditBackSenderForFailedTrans(commonClient, onlyDecreaseCounters);
                }

                // validate receiver limits after Interface Updation
                PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.P2P_MODULE);
                // No need to check for sender limits as the receiver created
                // the problem
                TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Failed", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Getting exception=" + e.getMessage());

                throw e;
            }

            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.RECEIVER_TOP_RESPONSE);

            _senderVO.setTotalConsecutiveFailCount(0);
            _senderVO.setTotalTransfers(_senderVO.getTotalTransfers() + 1);
            _senderVO.setTotalTransferAmount(_senderVO.getTotalTransferAmount() + _senderTransferItemVO.getRequestValue());
            _senderVO.setLastSuccessTransferDate(_currentDate);
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS);
            _p2pTransferVO.setErrorCode(null);
            // TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,_senderNetworkCode,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS,"Transaction Success",PretupsI.TXN_LOG_STATUS_SUCCESS,"Transfer Status="+_p2pTransferVO.getTransferStatus()+" Transfer Category="+_p2pTransferVO.getTransferCategory());

            // con=OracleUtil.getConnection();

            // validate receiver limits after Interface Updation
            PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.P2P_MODULE);

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_SUCCESS_REQUEST, 0, true, _receiverVO.getNetworkCode());
            // real time settlement of LMB on the basis of system preference
            // //@nu

        } catch (BTSLBaseException be) {
            if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _p2pTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            }
            if (be.isKey() && _p2pTransferVO.getSenderReturnMessage() == null) {
                btslMessages = be.getBtslMessages();
            } else if (_p2pTransferVO.getSenderReturnMessage() == null) {
                _p2pTransferVO.setSenderReturnMessage(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("run", _transferID, "Error Code:" + btslMessages.print());
            }

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
            _requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            _requestVO.setSuccessTxn(false);
            // throw new
            // BTSLBaseException(this,"run",PretupsErrorCodesI.P2P_ERROR_EXCEPTION);

        } catch (Exception e) {
            e.printStackTrace();
            if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            _log.error("run", _transferID, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            btslMessages = new BTSLMessages(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);

            // For increaseing the counters in network and service type
            ReqNetworkServiceLoadController.increaseRechargeCounters(_requestVO.getInstanceID(), _requestVO.getMessageGatewayVO().getGatewayType(), _senderNetworkCode, _serviceType, _transferID, LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, _receiverVO.getNetworkCode());
            _requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            _requestVO.setSuccessTxn(false);
        } finally {
            try {
                if (_p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_FAIL) && (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey())) {
                    _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((SelfTopUpErrorCodesI.P2P_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                }

                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);

                if (con == null) {
                    con = OracleUtil.getConnection();
                }
                try {
                    SubscriberBL.updateSubscriberLastDetails(con, _p2pTransferVO, _senderVO, _currentDate, _p2pTransferVO.getTransferStatus());
                } catch (BTSLBaseException bex) {
                    bex.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Not able to update Subscriber Last Details Exception:" + e.getMessage());
                }

                try {
                    if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                        PretupsBL.unmarkReceiverLastRequest(con, _transferID, _receiverVO);
                    }
                } catch (BTSLBaseException bex) {
                    bex.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Not able to unmark Receiver Last Request, Exception:" + e.getMessage());
                }

                if (_finalTransferStatusUpdate) {
                    // update transfer details in database
                    // update transfer details in database
                    _p2pTransferVO.setModifiedOn(_currentDate);
                    _p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                    PretupsBL.updateTransferDetails(con, _p2pTransferVO);
                }
            } catch (BTSLBaseException bex) {
                bex.printStackTrace();
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception ex) {
                }
                _log.error("run", _transferID, "BTSL Base Exception while updating transfer details in database:" + bex.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception ex) {
                }
                _log.error("run", _transferID, "Exception while updating transfer details in database:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception while updating transfer details in database , Exception:" + e.getMessage());
            }
            if (con != null) {
                try {
                    con.commit();
                } catch (Exception e) {
                }
                try {
                    con.close();
                } catch (Exception e) {
                }
                con = null;
            }

            String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("P2P_REC_MSG_REQD_BY_ALT_GW"));
            String reqruestGW = _p2pTransferVO.getRequestGatewayCode();
            if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
                if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
                    reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
                    if (_log.isDebugEnabled()) {
                        _log.debug("run: Reciver Message push through alternate GW", reqruestGW, "Requested GW was:" + _p2pTransferVO.getRequestGatewayCode());
                    }
                }
            }

            if (_p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS)) {
                if (_p2pTransferVO.getReceiverReturnMsg() == null) {
                    // (new
                    // PushMessage(_receiverMSISDN,getReceiverSuccessMessage(),_transferID,reqruestGW,_receiverLocale)).push();
                } else if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
                    (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID, reqruestGW, _receiverLocale)).push();
                } else {
                    (new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, reqruestGW, _receiverLocale)).push();
                }
            } else if (_recTopupFailMessageRequired && _p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                if (_p2pTransferVO.getReceiverReturnMsg() == null) {
                    (new PushMessage(_receiverMSISDN, getReceiverAmbigousMessage(), _transferID, reqruestGW, _receiverLocale)).push();
                } else if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
                    (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID, reqruestGW, _receiverLocale)).push();
                } else {
                    (new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, reqruestGW, _receiverLocale)).push();
                }
            } else if (_recTopupFailMessageRequired && _p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_FAIL)) {
                if (_p2pTransferVO.getReceiverReturnMsg() == null) {
                    (new PushMessage(_receiverMSISDN, getReceiverFailMessage(), _transferID, reqruestGW, _receiverLocale)).push();
                } else if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
                    (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID, reqruestGW, _receiverLocale)).push();
                } else {
                    (new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, reqruestGW, _receiverLocale)).push();
                }
            }

            /*
             * if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.
             * TXN_STATUS_SUCCESS)
             * &&_p2pTransferVO.getReceiverReturnMsg()!=null&&
             * ((BTSLMessages)_p2pTransferVO.getReceiverReturnMsg()).isKey())
             * {
             * BTSLMessages
             * btslRecMessages=(BTSLMessages)_p2pTransferVO.getReceiverReturnMsg
             * ();
             * (new
             * PushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale
             * ,btslRecMessages
             * .getMessageKey(),btslRecMessages.getArgs()),_transferID
             * ,_p2pTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
             * }
             * else
             * if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI
             * .TXN_STATUS_SUCCESS)
             * &&_p2pTransferVO.getReceiverReturnMsg()!=null)
             * (new PushMessage(_receiverMSISDN,(String)_p2pTransferVO.
             * getReceiverReturnMsg
             * (),_transferID,_p2pTransferVO.getRequestGatewayCode
             * (),_receiverLocale)).push();
             * 
             * if(!_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.
             * TXN_STATUS_SUCCESS)
             * &&!BTSLUtil.isNullString(Constants.getProperty
             * ("P2P_REC_GEN_FAIL_MSG_REQD")) &&
             * "Y".equalsIgnoreCase(Constants.getProperty
             * ("P2P_REC_GEN_FAIL_MSG_REQD")))
             * {
             * if(_p2pTransferVO.getReceiverReturnMsg()!=null&&((BTSLMessages)
             * _p2pTransferVO.getReceiverReturnMsg()).isKey())
             * {
             * BTSLMessages
             * btslRecMessages=(BTSLMessages)_p2pTransferVO.getReceiverReturnMsg
             * ();
             * (new
             * PushMessage(_receiverMSISDN,BTSLUtil.getMessage(_receiverLocale
             * ,btslRecMessages
             * .getMessageKey(),btslRecMessages.getArgs()),_transferID
             * ,_p2pTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
             * }
             * else if(_p2pTransferVO.getReceiverReturnMsg()!=null)
             * (new PushMessage(_receiverMSISDN,(String)_p2pTransferVO.
             * getReceiverReturnMsg
             * (),_transferID,_p2pTransferVO.getRequestGatewayCode
             * (),_receiverLocale)).push();
             * else
             * if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI
             * .TXN_STATUS_AMBIGUOUS))
             * (new
             * PushMessage(_receiverMSISDN,getReceiverAmbigousMessage(),_transferID
             * ,_p2pTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
             * else
             * if(_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI
             * .TXN_STATUS_FAIL))
             * (new
             * PushMessage(_receiverMSISDN,getReceiverFailMessage(),_transferID
             * ,_p2pTransferVO.getRequestGatewayCode(),_receiverLocale)).push();
             * }
             */
            PushMessage pushMessages = null;
            // In case of self TopUp,sender and receiver will be same so only
            // one final response message will be given to receiver.
            // Otherwise two final response message.
            /*
             * if(!_p2pTransferVO.getSenderMsisdn().equals(_p2pTransferVO.
             * getReceiverMsisdn()))
             * {
             */
            if (!BTSLUtil.isNullString(_p2pTransferVO.getSenderReturnMessage())) {
                pushMessages = (new PushMessage(_senderMSISDN, _p2pTransferVO.getSenderReturnMessage(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _senderLocale));
            } else if (btslMessages != null) {
                // push error message to sender
                pushMessages = (new PushMessage(_senderMSISDN, BTSLUtil.getMessage(_senderLocale, btslMessages.getMessageKey(), btslMessages.getArgs()), _transferID, _p2pTransferVO.getRequestGatewayCode(), _senderLocale));
            } else if (_p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS)) {
                // push success message to sender and receiver
                // pushMessages=(new
                // PushMessage(_p2pTransferVO.getReceiverMsisdn(),getSenderSuccessMessage(),_transferID,_p2pTransferVO.getRequestGatewayCode(),_senderLocale));
                pushMessages = (new PushMessage(_p2pTransferVO.getReceiverMsisdn(), getReceiverSuccessMessage(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale));
            }
            /*
             * }
             * else
             * {
             * if(btslMessages!=null)
             * {
             * //push error message to sender
             * pushMessages=(new
             * PushMessage(_p2pTransferVO.getReceiverMsisdn(),BTSLUtil
             * .getMessage
             * (_senderLocale,btslMessages.getMessageKey(),btslMessages
             * .getArgs()
             * ),_transferID,_p2pTransferVO.getRequestGatewayCode(),_senderLocale
             * ));
             * System.out.println("shishu 4"+pushMessages);
             * }
             * System.out.println("shishu 5"+pushMessages);
             * }
             */
            pushMessages.push();

            int messageLength = 0;
            String message = getSenderSuccessMessage();
            String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
            if (!BTSLUtil.isNullString(messLength)) {
                messageLength = (new Integer(messLength)).intValue();
            }
            // System.out.println("@@@@@@@@@@@@@@@@  before SENDER"+
            // " REQ GATE : reqruestGW="+reqruestGW +
            // " _p2pTransferVO.getRequestGatewayCode()="+_p2pTransferVO.getRequestGatewayCode()+" (message.length()<messageLength) is true or not=>"+(message.length()<messageLength)+" _p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)="+_p2pTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS));
            if (((message.length() < messageLength)) && ((_p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS)) && (!reqruestGW.equalsIgnoreCase(_p2pTransferVO.getRequestGatewayCode())))) {
                System.out.println("@@@@@@@@@@@@@@@@ ENTERED in SENDER");
                // push success message to sender and receiver
                PushMessage pushMessages1 = (new PushMessage(_senderMSISDN, message, _transferID, reqruestGW, _senderLocale));
                pushMessages1.push();
            }
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Ending", PretupsI.TXN_LOG_STATUS_SUCCESS, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Transfer Category=" + _p2pTransferVO.getTransferCategory() + " Error Code=" + _p2pTransferVO.getErrorCode() + " Message=" + _p2pTransferVO.getSenderReturnMessage());
            // Populate the P2PRequestDailyLog and log
            P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));
            if (_log.isDebugEnabled()) {
                _log.debug("run", _transferID, "Exiting");
            }
        }
    }

    /**
     * Method to get the sender regsitration message
     * 
     * @return
     */
    private String getSenderRegistrationMessage() {
        if (_senderVO.isPinUpdateReqd()) {
            String[] messageArgArray = { BTSLUtil.decryptText(_senderVO.getPin()) };
            return BTSLUtil.getMessage(_senderLocale, SelfTopUpErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS_WITHPIN, messageArgArray);
        }
        return BTSLUtil.getMessage(_senderLocale, SelfTopUpErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS, null);
    }

    /***
     * 
     * Method updated for notification message using service class date 15/05/06
     */
    private String getSenderSuccessMessage() {
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
        remainingMonAmount = (_senderVO.getMonthlyMaxTransAmtThreshold() - (_senderVO.getMonthlyTransferAmount() + _p2pTransferVO.getRequestedAmount()));
        remainingMonCount = (_senderVO.getMonthlyMaxTransCountThreshold() - (_senderVO.getMonthlyTransferCount() + 1));
        remainingDailyAmount = (_senderVO.getDailyMaxTransAmtThreshold() - (_senderVO.getDailyTransferAmount() + _p2pTransferVO.getRequestedAmount()));
        remainingDailyCount = (_senderVO.getDailyMaxTransCountThreshold() - (_senderVO.getDailyTransferCount() + 1));
        remainingWeekAmount = (_senderVO.getWeeklyMaxTransAmtThreshold() - (_senderVO.getWeeklyTransferAmount() + _p2pTransferVO.getRequestedAmount()));
        remainingWeekCount = (_senderVO.getWeeklyMaxTransCountThreshold() - (_senderVO.getWeeklyTransferCount() + 1));
        if (!"N".equals(_senderPostBalanceAvailable)) {
            messageArgArray = new String[] { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getTransferValue()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getTransferValue()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()), PretupsBL.getDisplayAmount(_p2pTransferVO.getSenderAccessFee()), _p2pTransferVO.getSubService(), PretupsBL.getDisplayAmount(remainingDailyAmount), Long.toString(remainingDailyCount), PretupsBL.getDisplayAmount(remainingMonAmount), Long.toString(remainingMonCount), PretupsBL.getDisplayAmount(remainingWeekAmount), Long.toString(remainingWeekCount) };
            if (_p2pTransferVO.getSenderAccessFee() == 0) {
                key = SelfTopUpErrorCodesI.P2P_SENDER_SUCCESS_WITHOUT_ACCESSFEE;
                // return
                // BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.P2P_SENDER_SUCCESS_WITHOUT_ACCESSFEE,messageArgArray);
                // _requestVO.setMessageCode(PretupsErrorCodesI.P2P_SENDER_SUCCESS_WITHOUT_ACCESSFEE);
                // _requestVO.setMessageArguments(messageArgArray);
            } else {
                key = SelfTopUpErrorCodesI.P2P_SENDER_SUCCESS;
                // return
                // BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.P2P_SENDER_SUCCESS,messageArgArray);
                // _requestVO.setMessageCode(PretupsErrorCodesI.P2P_SENDER_SUCCESS);
                // _requestVO.setMessageArguments(messageArgArray);
            }
        } else {
            messageArgArray = new String[] { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getTransferValue()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getTransferValue()), PretupsBL.getDisplayAmount(_p2pTransferVO.getSenderAccessFee()), _p2pTransferVO.getSubService(), PretupsBL.getDisplayAmount(remainingDailyAmount), Long.toString(remainingDailyCount), PretupsBL.getDisplayAmount(remainingMonAmount), Long.toString(remainingMonCount), PretupsBL.getDisplayAmount(remainingWeekAmount), Long.toString(remainingWeekCount) };
            key = SelfTopUpErrorCodesI.P2P_SENDER_SUCCESS_WITHOUT_POSTBAL;
            // _requestVO.setMessageCode(PretupsErrorCodesI.P2P_SENDER_SUCCESS_WITHOUT_POSTBAL);
            // _requestVO.setMessageArguments(messageArgArray);
            // return
            // BTSLUtil.getMessage(_senderLocale,PretupsErrorCodesI.P2P_SENDER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
        }
        if (SystemPreferences.NOTIFICATION_SERVICECLASSWISE_SEN) {
            String message = null;
            try {
                message = BTSLUtil.getMessage(_senderLocale, key + "_" + _senderTransferItemVO.getServiceClass(), messageArgArray);
                if (BTSLUtil.isNullString(message)) {
                    message = BTSLUtil.getMessage(_senderLocale, key, messageArgArray);
                }
                return message;
            } catch (Exception e) {
                return BTSLUtil.getMessage(_senderLocale, key, messageArgArray);
            }
        }
        return BTSLUtil.getMessage(_senderLocale, key, messageArgArray);
    }

    /**
     * Method to get the Receiver Ambigous Message
     * 
     * @return
     */
    private String getReceiverAmbigousMessage() {
        String[] messageArgArray = { _senderMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(_receiverLocale, SelfTopUpErrorCodesI.P2P_RECEIVER_AMBIGOUS_MESSAGE_KEY, messageArgArray);
    }

    /**
     * Method to get the Receiver Fail Message
     * 
     * @return
     */
    private String getReceiverFailMessage() {
        String[] messageArgArray = { _senderMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(_receiverLocale, SelfTopUpErrorCodesI.P2P_RECEIVER_FAIL_MESSAGE_KEY, messageArgArray);
    }

    /***
     * 
     * Method updated for notification message using service class date 15/05/06
     */
    private String getReceiverSuccessMessage() {
        String[] messageArgArray = null;
        String key = null;

        // For Get NUMBER BACK Service
        if (_p2pTransferVO.getReceiverTransferItemVO().isNumberBackAllowed()) {
            if (SystemPreferences.IS_SEPARATE_BONUS_REQUIRED) {
                _p2pTransferVO.setCalminusBonusvalue(_p2pTransferVO.getReceiverTransferValue() - _p2pTransferVO.getBonusTalkTimeValue());
            }
            // added by vikas kumar for card group updation
            messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverTransferValue()), String.valueOf(_receiverTransferItemVO.getValidity()), _senderMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverAccessFee()), _p2pTransferVO.getSubService(), String.valueOf(_p2pTransferVO.getReceiverBonus1()), String.valueOf(_p2pTransferVO.getReceiverBonus2()), PretupsBL.getDisplayAmount(_p2pTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(_p2pTransferVO.getCalminusBonusvalue()), String.valueOf(_p2pTransferVO.getReceiverBonus1Validity()), String.valueOf(_p2pTransferVO.getReceiverBonus2Validity()), String.valueOf(_p2pTransferVO.getReceiverCreditBonusValidity()) };
            if (_p2pTransferVO.getBonusTalkTimeValue() == 0) {
                key = SelfTopUpErrorCodesI.P2P_RECEIVER_GET_NUMBER_BACK_SUCCESS;
            } else {
                key = SelfTopUpErrorCodesI.P2P_RECEIVER_GET_NUMBER_BACK_SUCCESS_WITH_BONUS;
            }
        }
        if (!"N".equals(_receiverPostBalanceAvailable)) {
            String dateStr = null;
            try {
                dateStr = BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getNewExpiry());
            } catch (Exception e) {
                dateStr = String.valueOf(_receiverTransferItemVO.getNewExpiry());
            }
            if (SystemPreferences.IS_SEPARATE_BONUS_REQUIRED) {
                _p2pTransferVO.setCalminusBonusvalue(_receiverTransferItemVO.getTransferValue() - _p2pTransferVO.getBonusTalkTimeValue());
            }
            // added by vikas kumar for card group updation sms/mms
            messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getTransferValue()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getPostBalance()), dateStr, _senderMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverAccessFee()), _p2pTransferVO.getSubService(), String.valueOf(_p2pTransferVO.getReceiverBonus1()), String.valueOf(_p2pTransferVO.getReceiverBonus2()), PretupsBL.getDisplayAmount(_p2pTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(_p2pTransferVO.getCalminusBonusvalue()), String.valueOf(_p2pTransferVO.getReceiverBonus1Validity()), String.valueOf(_p2pTransferVO.getReceiverBonus2Validity()), String.valueOf(_p2pTransferVO.getReceiverCreditBonusValidity()) };
            if (_p2pTransferVO.getBonusTalkTimeValue() == 0) {
                key = SelfTopUpErrorCodesI.P2P_RECEIVER_SUCCESS;// return
                                                                // BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.P2P_RECEIVER_SUCCESS,messageArgArray);
                _requestVO.setMessageCode(key);
                _requestVO.setMessageArguments(messageArgArray);
            } else {
                key = SelfTopUpErrorCodesI.P2P_RECEIVER_SUCCESS_WITH_BONUS;
                _requestVO.setMessageCode(key);
                _requestVO.setMessageArguments(messageArgArray);

            }

        } else {
            // 601:Transaction number {0} to transfer {1} INR from {3} is
            // successful. Transferred value is {2} & access fee is {4} INR.
            // Please check your balance.
            if (SystemPreferences.IS_SEPARATE_BONUS_REQUIRED) {
                _p2pTransferVO.setCalminusBonusvalue(_receiverTransferItemVO.getTransferValue() - _p2pTransferVO.getBonusTalkTimeValue());
            }
            // added by vikas kumar fro card group updation
            messageArgArray = new String[] { _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_receiverTransferItemVO.getTransferValue()), _senderMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverAccessFee()), _p2pTransferVO.getSubService(), String.valueOf(_p2pTransferVO.getReceiverBonus1()), String.valueOf(_p2pTransferVO.getReceiverBonus2()), PretupsBL.getDisplayAmount(_p2pTransferVO.getBonusTalkTimeValue()), PretupsBL.getDisplayAmount(_p2pTransferVO.getCalminusBonusvalue()), String.valueOf(_p2pTransferVO.getReceiverBonus1Validity()), String.valueOf(_p2pTransferVO.getReceiverBonus2Validity()), String.valueOf(_p2pTransferVO.getReceiverCreditBonusValidity()) };
            if (_p2pTransferVO.getBonusTalkTimeValue() == 0) {
                key = SelfTopUpErrorCodesI.P2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL;// return
                                                                                // BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.P2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
                _requestVO.setMessageCode(key);
                _requestVO.setMessageArguments(messageArgArray);
            } else {
                key = SelfTopUpErrorCodesI.P2P_RECEIVER_SUCCESS_WITHOUT_POSTBAL_WITH_BONUS;
                _requestVO.setMessageCode(key);
                _requestVO.setMessageArguments(messageArgArray);
            }
        }
        if (SystemPreferences.NOTIFICATION_SERVICECLASSWISE_REC) {
            String message = null;
            try {
                message = BTSLUtil.getMessage(_receiverLocale, key + "_" + _receiverTransferItemVO.getServiceClass(), messageArgArray);
                if (BTSLUtil.isNullString(message)) {
                    message = BTSLUtil.getMessage(_receiverLocale, key, messageArgArray);
                }
                return message;
            } catch (Exception e) {
                return BTSLUtil.getMessage(_receiverLocale, key, messageArgArray);
            }
        }
        return BTSLUtil.getMessage(_receiverLocale, key, messageArgArray);
    }

    /**
     * Populates the Sender Transfer Items VO
     * 
     */
    private void setSenderTransferItemVO() {
        _senderTransferItemVO = new TransferItemVO();
        // set sender transfer item details
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
        _senderTransferItemVO.setEntryType(PretupsI.DEBIT);
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
        String senderNetworkCode = _senderVO.getNetworkCode();
        String receiverNetworkCode = _receiverVO.getNetworkCode();
        long senderPrefixID = _senderVO.getPrefixID();
        long receiverPrefixID = _receiverVO.getPrefixID();
        boolean isSenderFound = false;
        boolean isReceiverFound = false;
        if (_log.isDebugEnabled()) {
            _log.debug(this, "Getting interface details For Action=" + action + " _senderInterfaceInfoInDBFound=" + _senderInterfaceInfoInDBFound + " _receiverInterfaceInfoInDBFound=" + _receiverInterfaceInfoInDBFound);
            // Avoid searching in the loop again if in validation details was
            // found in database
            // This condition has been changed so that if payment method is not
            // the dafult one then there may be case that default interface will
            // be used for that.
        }

        if (((!_senderInterfaceInfoInDBFound && (_p2pTransferVO.getPaymentMethodKeywordVO() == null || !PretupsI.YES.equals(_p2pTransferVO.getPaymentMethodKeywordVO().getUseDefaultInterface()))) && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
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
                    throw new BTSLBaseException(this, "populateServicePaymentInterfaceDetails", SelfTopUpErrorCodesI.INTERFACE_NOT_ACTIVE);
                }
                isSenderFound = true;
            } else {
                isSenderFound = getInterfaceRoutingDetails(p_con, _senderMSISDN, senderPrefixID, _senderVO.getSubscriberType(), senderNetworkCode, _p2pTransferVO.getServiceType(), _p2pTransferVO.getPaymentMethodType(), PretupsI.USER_TYPE_SENDER, action);
            }
        } else {
            isSenderFound = true;
        }
        if (!isSenderFound) {
            if (!_senderVO.isDefUserRegistration()) {
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "populateServicePaymentInterfaceDetails", SelfTopUpErrorCodesI.P2P_SENDER_ALREADY_REG_NOT_FOUND_IN_VAL, 0, new String[] { ((LookupsVO) LookupsCache.getObject(PretupsI.SUBSRICBER_TYPE, _senderVO.getSubscriberType())).getLookupName() }, null);
            }
            throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "populateServicePaymentInterfaceDetails", SelfTopUpErrorCodesI.P2P_NOTFOUND_PAYMENTINTERFACEMAPPING);
        }

        // Avoid searching in the loop again if in validation details was found
        // in database
        if ((!_receiverInterfaceInfoInDBFound && action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION)) || action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION)) {
            isReceiverFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, receiverPrefixID, _type, receiverNetworkCode, _p2pTransferVO.getServiceType(), _type, PretupsI.USER_TYPE_RECEIVER, action);
            // If receiver Not found and we need to perform the alternate
            // category routing before IN Validation and it has not been
            // performed before then do Category Routing
            if (action.equals(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION) && !isReceiverFound && _performIntfceCatRoutingBeforeVal && _useAlternateCategory && !_interfaceCatRoutingDone) {
                // Get the alternate interface category and check whether it is
                // valid in that category.
                _log.info(this, "********* Performing ALTERNATE INTERFACE CATEGORY routing for receiver before IN Validations on Interface=" + _newInterfaceCategory + " *********");

                _type = _newInterfaceCategory;
                _interfaceCatRoutingDone = true;

                _requestVO.setReqSelector(_newDefaultSelector);
                _p2pTransferVO.setSubService(_newDefaultSelector);

                // Load the new prefix ID against the interface category , If
                // Not required then give the error

                _networkPrefixVO = null;
                _networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _type);
                if (_networkPrefixVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(this, "Getting Reeciver Prefix ID for MSISDN=" + _receiverMSISDN + " as " + _networkPrefixVO.getPrefixID());
                    }
                    _receiverVO.setNetworkCode(_networkPrefixVO.getNetworkCode());
                    _receiverVO.setPrefixID(_networkPrefixVO.getPrefixID());
                    _receiverVO.setSubscriberType(_networkPrefixVO.getSeriesType());
                    receiverNetworkCode = _receiverVO.getNetworkCode();
                    receiverPrefixID = _receiverVO.getPrefixID();
                    isReceiverFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, receiverPrefixID, _type, receiverNetworkCode, _p2pTransferVO.getServiceType(), _type, PretupsI.USER_TYPE_RECEIVER, action);
                } else {
                    _log.error(this, "Series Not Defined for Alternate Interface =" + _type + " For Series=" + _receiverVO.getMsisdnPrefix());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[populateServicePaymentInterfaceDetails]", "", "", "", "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But alternate Category Routing was required on interface");
                    isReceiverFound = false;
                }
            }
        } else {
            isReceiverFound = true;
        }
        if (!isReceiverFound) {
            throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "populateServicePaymentInterfaceDetails", SelfTopUpErrorCodesI.P2P_NOTFOUND_SERVICEINTERFACEMAPPING);
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
        // added for CRE_INT_CR00029 by ankit Zindal
        strBuff.append("&CARD_GROUP_SELECTOR=" + _requestVO.getReqSelector());
        strBuff.append("&REQ_SERVICE=" + _serviceType);
        strBuff.append("&INT_ST_TYPE=" + _p2pTransferVO.getSenderInterfaceStatusType());
        strBuff.append("&RECEIVER_MSISDN=" + _receiverMSISDN);
        strBuff.append("&REQ_AMOUNT=" + _p2pTransferVO.getRequestedAmount());
        // Added By Babu Kunwar
        strBuff.append("&SELECTOR_BUNDLE_ID=" + _p2pTransferVO.getSelectorBundleId());
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
     * Get the sender Debit Request String
     * 
     * @return
     */
    public String getSenderDebitAdjustStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getSenderCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_DEBIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + _senderTransferItemVO.getTransferValue());
        // strBuff.append("&GRACE_DAYS="+_senderTransferItemVO.getGraceDaysStr());
        // strBuff.append("&CARD_GROUP="+_p2pTransferVO.getCardGroupCode());
        // strBuff.append("&SERVICE_CLASS="+_senderTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + _senderTransferItemVO.getReferenceID());
        // strBuff.append("&ACCOUNT_STATUS="+_senderTransferItemVO.getAccountStatus());
        strBuff.append("&SOURCE_TYPE=" + _p2pTransferVO.getSourceType());
        strBuff.append("&PRODUCT_CODE=" + _p2pTransferVO.getProductCode());
        strBuff.append("&TAX_AMOUNT=" + (_p2pTransferVO.getSenderTax1Value() + _p2pTransferVO.getSenderTax2Value()));
        strBuff.append("&ACCESS_FEE=" + _p2pTransferVO.getSenderAccessFee());
        strBuff.append("&SENDER_MSISDN=" + _senderMSISDN);
        // strBuff.append("&RECEIVER_MSISDN="+_receiverMSISDN);
        strBuff.append("&EXTERNAL_ID=" + _senderExternalID);
        strBuff.append("&GATEWAY_CODE=" + _requestVO.getRequestGatewayCode());
        strBuff.append("&GATEWAY_TYPE=" + _requestVO.getRequestGatewayType());
        // strBuff.append("&IMSI="+BTSLUtil.NullToString(_senderIMSI));
        // strBuff.append("&SENDER_ID="+((SenderVO)_requestVO.getSenderVO()).getUserID());
        strBuff.append("&SERVICE_TYPE=" + _senderSubscriberType + "-" + _type);
        // strBuff.append("&ADJUST=Y");
        // strBuff.append("&INTERFACE_PREV_BALANCE="+_senderTransferItemVO.getPreviousBalance());
        // Avinash send the requested amount to IN. to use card group only for
        // reporting purpose.
        strBuff.append("&REQUESTED_AMOUNT=" + _p2pTransferVO.getRequestedAmount());

        strBuff.append("&BANK_PIN=" + ((SenderVO) _requestVO.getSenderVO()).getPin());
        strBuff.append("&TAS_ORIGIN_ST_CODE=" + _p2pTransferVO.getPaymentMethodType());
        strBuff.append("&CAL_OLD_EXPIRY_DATE=" + _senderTransferItemVO.getOldExporyInMillis());
        strBuff.append("&CARD_NUMBER=" + _senderVO.getCardDetailsVO().getCardNumber());
        strBuff.append("&CARD_NAME=" + _senderVO.getCardDetailsVO().getNameOfEmbossing());
        strBuff.append("&CARD_EXPIRY=" + _senderVO.getCardDetailsVO().getExpiryDate());
        strBuff.append("&CVV=" + _senderVO.getCardDetailsVO().getCvv());

        return strBuff.toString();
    }

    /**
     * Get the Receiver Request String to be send to common Client
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
        strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeR);
        strBuff.append("&INT_MOD_IP=" + _intModIPR);
        strBuff.append("&INT_MOD_PORT=" + _intModPortR);
        strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameR);
        strBuff.append("&MODULE=" + PretupsI.P2P_MODULE);
        strBuff.append("&USER_TYPE=R");
        // added for CRE_INT_CR00029 by ankit Zindal
        strBuff.append("&CARD_GROUP_SELECTOR=" + _requestVO.getReqSelector());
        strBuff.append("&REQ_SERVICE=" + _serviceType);
        strBuff.append("&INT_ST_TYPE=" + _p2pTransferVO.getReceiverInterfaceStatusType());
        // Added By Babu Kunwar
        strBuff.append("&SELECTOR_BUNDLE_ID=" + _p2pTransferVO.getSelectorBundleId());
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
        strBuff.append("&SERVICE_CLASS=" + _receiverTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + _receiverTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + _receiverTransferItemVO.getAccountStatus());
        strBuff.append("&CREDIT_LIMIT=" + _receiverTransferItemVO.getPreviousBalance());
        strBuff.append("&SERVICE_TYPE=" + _senderSubscriberType + "-" + _type);
        return strBuff.toString();
    }

    /**
     * Gets the sender Credit Request String
     * 
     * @return
     */
    public String getReceiverCreditStr() {
        StringBuffer strBuff = null;
        strBuff = new StringBuffer(getReceiverCommonString());
        strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_CREDIT_ACTION);
        strBuff.append("&INTERFACE_AMOUNT=" + _receiverTransferItemVO.getTransferValue());
        strBuff.append("&SERVICE_CLASS=" + _receiverTransferItemVO.getServiceClassCode());
        strBuff.append("&ACCOUNT_ID=" + _receiverTransferItemVO.getReferenceID());
        strBuff.append("&ACCOUNT_STATUS=" + _receiverTransferItemVO.getAccountStatus());
        strBuff.append("&GRACE_DAYS=" + _receiverTransferItemVO.getGraceDaysStr());
        strBuff.append("&CARD_GROUP=" + _p2pTransferVO.getCardGroupCode());
        strBuff.append("&MIN_CARD_GROUP_AMT=" + _p2pTransferVO.getMinCardGroupAmount());
        strBuff.append("&VALIDITY_DAYS=" + _receiverTransferItemVO.getValidity());
        strBuff.append("&BONUS_VALIDITY_DAYS=" + _p2pTransferVO.getReceiverBonusValidity());
        strBuff.append("&BONUS_AMOUNT=" + _p2pTransferVO.getReceiverBonusValue());
        strBuff.append("&SOURCE_TYPE=" + _p2pTransferVO.getSourceType());
        strBuff.append("&PRODUCT_CODE=" + _p2pTransferVO.getProductCode());
        strBuff.append("&TAX_AMOUNT=" + (_p2pTransferVO.getReceiverTax1Value() + _p2pTransferVO.getReceiverTax2Value()));
        strBuff.append("&ACCESS_FEE=" + _p2pTransferVO.getReceiverAccessFee());
        strBuff.append("&SENDER_MSISDN=" + _senderMSISDN);
        strBuff.append("&RECEIVER_MSISDN=" + _receiverMSISDN);
        strBuff.append("&EXTERNAL_ID=" + _receiverExternalID);
        strBuff.append("&GATEWAY_CODE=" + _requestVO.getRequestGatewayCode());
        strBuff.append("&GATEWAY_TYPE=" + _requestVO.getRequestGatewayType());
        strBuff.append("&IMSI=" + BTSLUtil.NullToString(_receiverIMSI));
        strBuff.append("&SENDER_ID=" + ((SenderVO) _requestVO.getSenderVO()).getUserID());
        strBuff.append("&SERVICE_TYPE=" + _senderSubscriberType + "-" + _type);
        if (String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE).equals(_requestVO.getReqSelector())) {
            strBuff.append("&ADJUST=Y");
            strBuff.append("&CAL_OLD_EXPIRY_DATE=" + _receiverTransferItemVO.getOldExporyInMillis());// /@nu
        }
        try {
            strBuff.append("&OLD_EXPIRY_DATE=" + BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getPreviousExpiry()));
        } catch (Exception e) {
        }
        try {
            strBuff.append("&OLD_GRACE_DATE=" + BTSLUtil.getDateStringFromDate(_receiverTransferItemVO.getPreviousGraceDate()));
        } catch (Exception e) {
        }
        strBuff.append("&INTERFACE_PREV_BALANCE=" + _receiverTransferItemVO.getPreviousBalance());
        // Avinash send the requested amount to IN. to use card group only for
        // reporting purpose.
        strBuff.append("&REQUESTED_AMOUNT=" + _p2pTransferVO.getRequestedAmount());
        // For Get NUMBER BACK Service
        if (_receiverTransferItemVO.isNumberBackAllowed()) {
            String numbck_diff_to_in = _p2pTransferVO.getServiceType() + PreferenceI.NUMBCK_DIFF_REQ_TO_IN;
            Boolean NBR_BK_SEP_REQ = (Boolean) PreferenceCache.getControlPreference(numbck_diff_to_in, _p2pTransferVO.getNetworkCode(), _receiverTransferItemVO.getInterfaceID());
            strBuff.append("&NBR_BK_DIFF_REQ=" + NBR_BK_SEP_REQ);
        }
        // Added by Zafar Abbas on 13/02/2008 after adding two new fields for
        // Bonus SMS/MMS in Card group
        strBuff.append("&BONUS1=" + _p2pTransferVO.getReceiverBonus1());
        strBuff.append("&BONUS2=" + _p2pTransferVO.getReceiverBonus2());
        strBuff.append("&BUNDLE_TYPES=" + _receiverTransferItemVO.getBundleTypes());
        strBuff.append("&BONUS_BUNDLE_VALIDITIES=" + _receiverTransferItemVO.getBonusBundleValidities());

        // added by vikask for card group updation field

        strBuff.append("&BONUS1_VAL=" + _p2pTransferVO.getReceiverBonus1Validity());
        strBuff.append("&BONUS2_VAL=" + _p2pTransferVO.getReceiverBonus2Validity());
        strBuff.append("&CREDIT_BONUS_VAL=" + _p2pTransferVO.getReceiverCreditBonusValidity());

        // added by amit for card group offline field
        strBuff.append("&COMBINED_RECHARGE=" + _p2pTransferVO.getBoth());
        strBuff.append("&EXPLICIT_RECHARGE=" + _p2pTransferVO.getOnline());
        if (SystemPreferences.ENQ_POSTBAL_IN) {
            strBuff.append("&ENQ_POSTBAL_IN=" + PretupsI.YES);
        } else {
            strBuff.append("&ENQ_POSTBAL_IN=" + PretupsI.NO);
        }
        if (SystemPreferences.ENQ_POSTBAL_ALLOW) {
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
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");
        ArrayList altList = null;
        boolean isRequired = false;

        // added to log the IN validation request sent and request received
        // time. Start 12/02/2008
        if (null != map.get("IN_START_TIME")) {
            _requestVO.setValidationSenderRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            _requestVO.setValidationSenderResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
            // end 12/02/2008
        }

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _senderTransferItemVO.getInterfaceID(), interfaceStatusType, SelfTopUpErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
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
        boolean ldccHandle = _operatorUtil.handleLDCCRequest();
        // If we get the MSISDN not found on interface error then perform
        // interface routing
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
            if (!ldccHandle) {
                isRoutingSecond = true;
            }
            _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(_senderTransferItemVO.getInterfaceID());
            try {
                if (map.get("SERVICE_CLASS").equals(PretupsI.SERVICE_CLASS_LDCC)) {
                    throw new BTSLBaseException(this, "updateForSenderValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                } else {
                    if (altList != null && altList.size() > 0) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("updateForSenderValidateResponse", "Got Status=" + status + " After validation Request For MSISDN=" + _senderMSISDN + " Performing Alternate Routing");
                        }
                        performSenderAlternateRouting(altList); // Method to
                                                                // perform the
                                                                // sender
                                                                // interface
                                                                // routing for
                                                                // validation
                    } else {
                        isRequired = true;
                        throw new BTSLBaseException(this, "updateForSenderValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    }
                }
            } catch (BTSLBaseException e) {
                // TODO Auto-generated catch block
                if (!ldccHandle) {
                    throw e;
                } else {
                    status = e.getMessage();
                    if (ldccHandle && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
                        try {
                            handleLDCCRequest();
                        } catch (BTSLBaseException be) {
                            status = be.getMessage();
                        }
                        if (!BTSLUtil.isNullString(_senderTransferItemVO.getValidationStatus())) {
                            status = _senderTransferItemVO.getValidationStatus();
                        }
                        isRequired = false;
                        isLDCCTest = false;
                        isRoutingSecond = true;
                        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
                            _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
                            altList = InterfaceRoutingControlCache.getRoutingControlDetails(_senderTransferItemVO.getInterfaceID());
                            // routing of Ailternate type IN .
                            if (altList != null && altList.size() > 0) {
                                if (_log.isDebugEnabled()) {
                                    _log.debug("updateForSenderValidateResponse", "Got Status=" + status + " After validation Request For MSISDN=" + _senderMSISDN + " Performing Alternate Routing");
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
                                if (InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
                                    _senderVO.setSubscriberType(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
                                    _p2pTransferVO.setTransferCategory(_serviceInterfaceRoutingVO.getAlternateInterfaceType() + "-" + _type);
                                    _p2pTransferVO.setPaymentMethodType(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
                                    _isUpdateRequired = true;
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
                        _isUpdateRequired = false;
                        _isSenderRoutingUpdate = false;
                        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(_senderTransferItemVO.getValidationStatus())) {
                            // if not found on other IN but subscriber is
                            // basically INACTIVE or balance is not enough.
                            _senderTransferItemVO.setValidationStatus(InterfaceErrorCodesI.SUCCESS);
                            status = InterfaceErrorCodesI.SUCCESS;
                            isRequired = true;
                        }
                    }
                }
            }

        }
        if ((!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && isLDCCTest) || isRequired) {
            _senderTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
            _senderTransferItemVO.setAccountStatus((String) map.get("ACCOUNT_STATUS"));
            _senderTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
            _senderTransferItemVO.setValidationStatus(status);
            _senderVO.setInterfaceResponseCode(_senderTransferItemVO.getInterfaceResponseCode());

            if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
                try {
                    _senderTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            _senderTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

            String[] strArr = null;
            if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                _p2pTransferVO.setErrorCode(status + "_S");
                _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
                _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                _senderTransferItemVO.setTransferStatus(status);
                _receiverTransferItemVO.setValidationStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                _receiverTransferItemVO.setTransferStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                strArr = new String[] { _receiverMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
                // throw new
                // BTSLBaseException("PretupsBL","updateForReceiverValidateResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "updateForSenderValidateResponse", _p2pTransferVO.getErrorCode(), 0, strArr, null);
            }
            _senderTransferItemVO.setTransferStatus(status);
            _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS);
            SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _p2pTransferVO.getPaymentMethodType());
            if ((PretupsI.INTERFACE_CATEGORY_PRE.equals(_senderVO.getSubscriberType()) || ldccHandle) && !_senderDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                PretupsBL.insertSubscriberInterfaceRouting(_senderTransferItemVO.getInterfaceID(), _senderExternalID, _senderMSISDN, _p2pTransferVO.getPaymentMethodType(), _senderVO.getUserID(), _currentDate);
                _senderInterfaceInfoInDBFound = true;
                _senderDeletionReqFromSubRouting = true;
            }

            try {
                _senderTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
            }
            try {
                _senderTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
            }
            _senderTransferItemVO.setOldExporyInMillis((String) map.get("CAL_OLD_EXPIRY_DATE"));// @nu
            _senderTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));

            try {
                _senderTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
                _senderTransferItemVO.setBalanceCheckReq(false);
            }
            _senderVO.setCreditLimit(_senderTransferItemVO.getPreviousBalance());

            // Update the Previous Balance in case of Post Paid Offline
            // interface with Credit Limit - Monthly Transfer Amount
            if (_senderVO.isPostOfflineInterface()) {
                boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(_senderVO.getLastSuccessTransferDate(), _currentDate, BTSLUtil.PERIOD_MONTH);
                if (!isPeriodChange) {
                    _senderTransferItemVO.setPreviousBalance(_senderTransferItemVO.getPreviousBalance() - _senderVO.getMonthlyTransferAmount());
                }
            }
            if (PretupsI.INTERFACE_CATEGORY_POST.equals(_senderVO.getSubscriberType())) {

                long balance = Long.parseLong((String) map.get("BILL_AMOUNT_BAL"));
                long credit_limit = Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE"));
                // System.out.println("balance="+balance+",credit_limit="+credit_limit+",_p2pTransferVO.getRequestedAmount()="+_p2pTransferVO.getRequestedAmount());
                _senderVO.setCreditLimit(credit_limit - balance);
                _senderTransferItemVO.setPreviousBalance(_senderTransferItemVO.getPreviousBalance() - _senderVO.getMonthlyTransferAmount());
            }
            _senderTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
            _senderTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));
            // added for service provider information:

            _senderTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
        }
    }

    /**
     * Method to handle Sender Debit Response
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForSenderDebitResponse(String str) throws BTSLBaseException {
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");

        // added to log the IN validation request sent and request received
        // time. Start 12/02/2008
        if (null != map.get("IN_START_TIME")) {
            _requestVO.setTopUPSenderRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            _requestVO.setTopUPSenderResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
            // end 12/02/2008
        }

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType, SelfTopUpErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
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
                e.printStackTrace();
            }
        }

        _senderTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

        String[] strArr = null;

        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            _p2pTransferVO.setErrorCode(status + "_S");
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            _senderTransferItemVO.setTransferStatus(status);
            _receiverTransferItemVO.setUpdateStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
            _receiverTransferItemVO.setTransferStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { _receiverMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
            // throw new
            // BTSLBaseException(this,"updateForReceiverValidateResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
            throw new BTSLBaseException(this, "updateForSenderDebitResponse", _p2pTransferVO.getErrorCode(), 0, strArr, null);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
            _p2pTransferVO.setErrorCode(status + "_S");
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _senderTransferItemVO.setTransferStatus(status);
            _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _senderTransferItemVO.setUpdateStatus(status);
            _receiverTransferItemVO.setUpdateStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
            _receiverTransferItemVO.setTransferStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { _transferID, _receiverTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForSenderDebitResponse", SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        } else {
            _senderTransferItemVO.setTransferStatus(status);
            _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS);
            _senderTransferItemVO.setUpdateStatus(status);
        }

        /*
         * try{_senderTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString(
         * (String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy"));}catch(Exception
         * e){};
         * try{_senderTransferItemVO.setNewGraceDate(BTSLUtil.getDateFromDateString
         * ((String)map.get("NEW_GRACE_DATE"),"ddMMyyyy"));}catch(Exception
         * e){};
         * try{_senderTransferItemVO.setPostBalance(Long.parseLong((String)map.get
         * ("INTERFACE_POST_BALANCE")));}catch(Exception e){};
         */
        // @nu
        if (SystemPreferences.ENQ_POSTBAL_ALLOW) {
            try {
                _senderTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
            }
            ;
            // try{_senderTransferItemVO.setNewGraceDate(BTSLUtil.getDateFromDateString((String)map.get("NEW_GRACE_DATE"),"ddMMyyyy"));}catch(Exception
            // e){};
            try {
                _senderTransferItemVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
            } catch (Exception e) {
            }
            ;
            try {
                _senderTransferItemVO.setPostValidationStatus((String) map.get("POSTCRE_TRANSACTION_STATUS"));
            } catch (Exception e) {
            }
            ;
        }
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_POSTCREDIT_VAL_TIME"))) {
                _requestVO.setPostValidationTimeTaken(Long.parseLong((String) map.get("IN_POSTCREDIT_VAL_TIME")));
            } else {
                _requestVO.setPostValidationTimeTaken(0L);
            }
        } catch (Exception e) {
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
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");
        ArrayList altList = null;
        boolean isRequired = false;

        // added to log the IN validation request sent and request received
        // time. Start 12/02/2008
        if (null != map.get("IN_START_TIME")) {
            _requestVO.setValidationReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            _requestVO.setValidationReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
        }
        // end 12/02/2008
        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType, SelfTopUpErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        // If we get the MSISDN not found on interface error then perform
        // interface routing
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
            _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(_receiverTransferItemVO.getInterfaceID());
            if (altList != null && altList.size() > 0) {
                performReceiverAlternateRouting(altList, SRC_BEFORE_INRESP_CAT_ROUTING);
            } else {
                if (_useAlternateCategory && !_performIntfceCatRoutingBeforeVal && !_interfaceCatRoutingDone) {
                    performAlternateCategoryRouting();
                } else {
                    isRequired = true;
                }
            }
        }
        if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
            populateReceiverItemsDetails(map);
            // For Service Provider Information
            _receiverTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
        }
    }

    /**
     * Method to handle receiver credit response
     * 
     * @param str
     * @throws BTSLBaseException
     */
    public void updateForReceiverCreditResponse(String str) throws BTSLBaseException {
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");

        // added to log the IN validation request sent and request received
        // time. Start 12/02/2008
        if (null != map.get("IN_START_TIME")) {
            _requestVO.setTopUPReceiverRequestSent(((Long.valueOf((String) map.get("IN_START_TIME"))).longValue()));
        }
        if (null != map.get("IN_END_TIME")) {
            _requestVO.setTopUPReceiverResponseReceived(((Long.valueOf((String) map.get("IN_END_TIME"))).longValue()));
            // end 12/02/2008
        }

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        // Done so that in Credit Back IN module does not activate the IN as
        // else it would receive M from here
        if (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) && _receiverTransferItemVO.getInterfaceID().equals(_senderTransferItemVO.getInterfaceID())) {
            _p2pTransferVO.setSenderInterfaceStatusType(InterfaceCloserI.INTERFACE_AUTO_ACTIVE);
        }
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType, SelfTopUpErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        _receiverTransferItemVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        _receiverTransferItemVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        String updateStatus = (String) map.get("UPDATE_STATUS");

        if (BTSLUtil.isNullString(updateStatus)) {
            updateStatus = status;
        }

        _receiverTransferItemVO.setUpdateStatus(status);
        _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());

        _receiverTransferItemVO.setUpdateStatus1((String) map.get("UPDATE_STATUS1"));
        _receiverTransferItemVO.setUpdateStatus2((String) map.get("UPDATE_STATUS2"));

        if (!BTSLUtil.isNullString((String) map.get("ADJUST_AMOUNT"))) {
            _receiverTransferItemVO.setAdjustValue(Long.parseLong((String) map.get("ADJUST_AMOUNT")));
        }

        _receiverPostBalanceAvailable = ((String) map.get("POST_BALANCE_ENQ_SUCCESS"));

        // set from IN Module
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
            try {
                _receiverTransferItemVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID1"))) {
            try {
                _receiverTransferItemVO.setInterfaceReferenceID1((String) map.get("IN_TXN_ID1"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            _receiverTransferItemVO.setTransferType1(PretupsI.TRANSFER_TYPE_BA_ADJ_CR);
        }
        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID2"))) {
            try {
                _receiverTransferItemVO.setInterfaceReferenceID2((String) map.get("IN_TXN_ID2"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            _receiverTransferItemVO.setTransferType2(PretupsI.TRANSFER_TYPE_BA_ADJ_DR);
        }
        _receiverTransferItemVO.setReferenceID((String) map.get("IN_RECON_ID"));

        String[] strArr = null;
        if (BTSLUtil.isNullString(status) || (!status.equals(InterfaceErrorCodesI.SUCCESS) && !status.equals(InterfaceErrorCodesI.AMBIGOUS))) {
            _p2pTransferVO.setErrorCode(status + "_R");
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(status);
            strArr = new String[] { _receiverMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
            // throw new
            // BTSLBaseException(this,"updateForReceiverCreditResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
            throw new BTSLBaseException(this, "updateForReceiverCreditResponse", _p2pTransferVO.getErrorCode(), 0, strArr, null);
        } else if (status.equals(InterfaceErrorCodesI.AMBIGOUS)) {
            _p2pTransferVO.setErrorCode(status + "_R");
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _receiverTransferItemVO.setUpdateStatus(status);
            strArr = new String[] { _transferID, _receiverTransferItemVO.getMsisdn(), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
            throw new BTSLBaseException(this, "updateForReceiverCreditResponse", SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS, 0, strArr, null);
        } else {
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS);
            _receiverTransferItemVO.setUpdateStatus(status);
        }

        /*
         * try{_receiverTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString
         * ((String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy"));}catch(Exception
         * e){};
         * try{_receiverTransferItemVO.setNewGraceDate(BTSLUtil.
         * getDateFromDateString
         * ((String)map.get("NEW_GRACE_DATE"),"ddMMyyyy"));}catch(Exception
         * e){};
         * try{_receiverTransferItemVO.setPostBalance(Long.parseLong((String)map.
         * get("INTERFACE_POST_BALANCE")));}catch(Exception e){};
         */
        if (SystemPreferences.ENQ_POSTBAL_ALLOW) {
            try {
                _receiverTransferItemVO.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
            }
            ;
            // try{_receiverTransferItemVO.setNewGraceDate(BTSLUtil.getDateFromDateString((String)map.get("NEW_GRACE_DATE"),"ddMMyyyy"));}catch(Exception
            // e){};
            try {
                _receiverTransferItemVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
            } catch (Exception e) {
            }
            ;
            try {
                _receiverTransferItemVO.setPostValidationStatus((String) map.get("POSTCRE_TRANSACTION_STATUS"));
            } catch (Exception e) {
            }
            ;
        }
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_POSTCREDIT_VAL_TIME"))) {
                _requestVO.setPostValidationTimeTaken(Long.parseLong((String) map.get("IN_POSTCREDIT_VAL_TIME")));
            } else {
                _requestVO.setPostValidationTimeTaken(0L);
            }
        } catch (Exception e) {
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
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        _senderCreditBackStatusVO = new TransferItemVO();
        _senderCreditBackStatusVO.setMsisdn(_senderTransferItemVO.getMsisdn());
        _senderCreditBackStatusVO.setRequestValue(_senderTransferItemVO.getRequestValue());
        _senderCreditBackStatusVO.setSubscriberType(_senderTransferItemVO.getSubscriberType());
        _senderCreditBackStatusVO.setTransferDate(_senderTransferItemVO.getTransferDate());
        _senderCreditBackStatusVO.setTransferDateTime(_senderTransferItemVO.getTransferDateTime());
        _senderCreditBackStatusVO.setTransferID(_senderTransferItemVO.getTransferID());
        _senderCreditBackStatusVO.setUserType(_senderTransferItemVO.getUserType());
        _senderCreditBackStatusVO.setEntryDate(_senderTransferItemVO.getEntryDate());
        _senderCreditBackStatusVO.setEntryDateTime(_senderTransferItemVO.getEntryDateTime());
        _senderCreditBackStatusVO.setPrefixID(_senderTransferItemVO.getPrefixID());
        _senderCreditBackStatusVO.setTransferValue(_senderTransferItemVO.getTransferValue());
        _senderCreditBackStatusVO.setInterfaceID(_senderTransferItemVO.getInterfaceID());
        _senderCreditBackStatusVO.setInterfaceType(_senderTransferItemVO.getInterfaceType());
        _senderCreditBackStatusVO.setServiceClass(_senderTransferItemVO.getServiceClass());
        _senderCreditBackStatusVO.setServiceClassCode(_senderTransferItemVO.getServiceClassCode());
        _senderCreditBackStatusVO.setInterfaceHandlerClass(_senderTransferItemVO.getInterfaceHandlerClass());

        _senderCreditBackStatusVO.setSNo(3);
        _senderCreditBackStatusVO.setEntryType(PretupsI.CREDIT);
        _senderCreditBackStatusVO.setTransferType(PretupsI.TRANSFER_TYPE_P2P_CREDITBACK);
        _senderCreditPostBalanceAvailable = (String) map.get("POST_BALANCE_ENQ_SUCCESS");

        String status = (String) map.get("TRANSACTION_STATUS");
        _senderCreditBackStatusVO.setProtocolStatus((String) map.get("PROTOCOL_STATUS"));
        _senderCreditBackStatusVO.setInterfaceResponseCode((String) map.get("INTERFACE_STATUS"));
        _senderCreditBackStatusVO.setTransferStatus(status);
        _senderCreditBackStatusVO.setUpdateStatus(status);
        _senderCreditBackStatusVO.setValidationStatus(status);
        _p2pTransferVO.setCreditBackStatus(status);

        if (!BTSLUtil.isNullString((String) map.get("IN_TXN_ID"))) {
            _senderCreditBackStatusVO.setInterfaceReferenceID((String) map.get("IN_TXN_ID"));
        }

        _senderCreditBackStatusVO.setReferenceID((String) map.get("IN_RECON_ID"));

        if (BTSLUtil.isNullString(status) || !status.equals(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS)) {
            // Mark the request as Ambigous if not able to credit back the
            // sender
            _p2pTransferVO.setErrorCode(status + "_S");
            _p2pTransferVO.setCreditBackStatus(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS);
            _senderCreditBackStatusVO.setTransferStatus(InterfaceErrorCodesI.AMBIGOUS);
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS);
            throw new BTSLBaseException(status);
        }
        if (!SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS.equals(_p2pTransferVO.getTransferStatus())) {
            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
        }

        try {
            _senderCreditBackStatusVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
        }
        ;
        // try{_senderCreditBackStatusVO.setPostBalance(Long.parseLong((String)map.get("INTERFACE_POST_BALANCE")));}catch(Exception
        // e){};
        try {
            _senderCreditBackStatusVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
        }
        ;
        // try{_senderCreditBackStatusVO.setNewExpiry(BTSLUtil.getDateFromDateString((String)map.get("NEW_EXPIRY_DATE"),"ddMMyyyy"));}catch(Exception
        // e){};
        // @nu
        if (SystemPreferences.ENQ_POSTBAL_ALLOW) {
            try {
                _senderCreditBackStatusVO.setPostBalance(Long.parseLong((String) map.get("INTERFACE_POST_BALANCE")));
            } catch (Exception e) {
            }
            ;
            try {
                _senderCreditBackStatusVO.setNewExpiry(BTSLUtil.getDateFromDateString((String) map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
            }
            ;
            try {
                _senderCreditBackStatusVO.setPostValidationStatus((String) map.get("POSTCRE_TRANSACTION_STATUS"));
            } catch (Exception e) {
            }
            ;
        }
        try {
            if (!BTSLUtil.isNullString((String) map.get("IN_POSTCREDIT_VAL_TIME"))) {
                _requestVO.setPostValidationTimeTaken(Long.parseLong((String) map.get("IN_POSTCREDIT_VAL_TIME")));
            } else {
                _requestVO.setPostValidationTimeTaken(0L);
            }
        } catch (Exception e) {
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
        if (_log.isDebugEnabled()) {
            _log.debug("checkTransactionLoad", "Checking load for transfer ID=" + _transferID);
        }
        try {
            _requestVO.setPerformIntfceCatRoutingBeforeVal(_performIntfceCatRoutingBeforeVal);
            _p2pTransferVO.setRequestVO(_requestVO);
            _p2pTransferVO.setSenderTransferItemVO(_senderTransferItemVO);
            _p2pTransferVO.setReceiverTransferItemVO(_receiverTransferItemVO);
            _requestVO.setReceiverDeletionReqFromSubRouting(_receiverDeletionReqFromSubRouting);
            _requestVO.setReceiverInterfaceInfoInDBFound(_receiverInterfaceInfoInDBFound);
            _requestVO.setSenderDeletionReqFromSubRouting(_senderDeletionReqFromSubRouting);
            _requestVO.setSenderInterfaceInfoInDBFound(_senderInterfaceInfoInDBFound);
            _requestVO.setInterfaceCatRoutingDone(_interfaceCatRoutingDone);

            int senderLoadStatus = LoadController.checkInterfaceLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), _senderTransferItemVO.getInterfaceID(), _transferID, _p2pTransferVO, true);
            int recieverLoadStatus = 0;
            // Further process the request
            if (senderLoadStatus == 0) {
                recieverLoadStatus = LoadController.checkInterfaceLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _transferID, _p2pTransferVO, true);
                if (recieverLoadStatus == 0) {
                    try {
                        LoadController.checkTransactionLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), _senderTransferItemVO.getInterfaceID(), PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
                    } catch (BTSLBaseException e) {
                        // Decreasing interface load of receiver which we had
                        // incremented before 27/09/06, sender was decreased in
                        // the method
                        LoadController.decreaseCurrentInterfaceLoad(_transferID, ((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                        throw e;
                    }
                    try {
                        LoadController.checkTransactionLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_RECEIVER);
                    } catch (BTSLBaseException e) {
                        // Decreasing interface load of sender which we had
                        // incremented before 27/09/06, receiver was decreased
                        // in the method
                        LoadController.decreaseTransactionInterfaceLoad(_transferID, ((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                        throw e;
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("P2PAdhocRechargeUsingCreditCardController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
                    }
                }
                // Request in Queue
                else if (recieverLoadStatus == 1) {
                    // Decrease the interface counter of the sender that was
                    // increased
                    LoadController.decreaseCurrentInterfaceLoad(_transferID, ((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), _senderTransferItemVO.getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    String strArr[] = { _receiverMSISDN, String.valueOf(_p2pTransferVO.getRequestedAmount()) };
                    throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
                }
                // Refuse the request
                else {
                    throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                }
            }
            // Request in Queue
            else if (senderLoadStatus == 1) {
                String strArr[] = { _receiverMSISDN, String.valueOf(_p2pTransferVO.getRequestedAmount()) };
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
            _log.error("P2PAdhocRechargeUsingCreditCardController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("P2PAdhocRechargeUsingCreditCardController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "checkTransactionLoad", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
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
        if (_log.isDebugEnabled()) {
            _log.debug("checkTransactionLoad", "Checking load for transfer ID=" + _transferID + " on interface=" + p_interfaceID);
        }
        int recieverLoadStatus = 0;

        try {
            if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
                int senderLoadStatus = LoadController.checkInterfaceLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), p_interfaceID, _transferID, _p2pTransferVO, true);
                // Further process the request
                if (senderLoadStatus == 0) {
                    recieverLoadStatus = LoadController.checkInterfaceLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _transferID, _p2pTransferVO, false);
                    if (recieverLoadStatus == 0) {
                        try {
                            LoadController.checkTransactionLoad(((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), p_interfaceID, PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
                        } catch (BTSLBaseException e) {
                            // Decreasing interface load of receiver which we
                            // had incremented before 27/09/06, sender was
                            // decreased in the method
                            LoadController.decreaseCurrentInterfaceLoad(_transferID, ((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                            throw e;
                        }
                        try {
                            LoadController.checkTransactionLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_RECEIVER);
                        } catch (BTSLBaseException e) {
                            // Decreasing interface load of sender which we had
                            // incremented before 27/09/06, receiver was
                            // decreased in the method
                            LoadController.decreaseTransactionInterfaceLoad(_transferID, ((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                            throw e;
                        }

                        if (_log.isDebugEnabled()) {
                            _log.debug("P2PAdhocRechargeUsingCreditCardController[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
                        }
                    }
                    // Refuse the request
                    else {
                        throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                    }
                }
                // Request in Queue
                else if (senderLoadStatus == 1) {
                    String strArr[] = { _receiverMSISDN, String.valueOf(_p2pTransferVO.getRequestedAmount()) };
                    throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_IN_QUEUE, 0, strArr, null);
                }
                // Refuse the request
                else {
                    throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                }
            } else {
                // Do not enter the request in Queue
                recieverLoadStatus = LoadController.checkInterfaceLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), p_interfaceID, _transferID, _p2pTransferVO, false);
                if (recieverLoadStatus == 0) {
                    LoadController.checkTransactionLoad(((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), p_interfaceID, PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_RECEIVER);
                    if (_log.isDebugEnabled()) {
                        _log.debug("checkTransactionLoad[checkTransactionLoad]", "_transferID=" + _transferID + " Successfully through load");
                    }
                }
                // Request in Queue
                else if (recieverLoadStatus == 1) {
                    throw new BTSLBaseException("checkTransactionLoad", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                }
                // Refuse the request
                else {
                    throw new BTSLBaseException("checkTransactionLoad", "checkTransactionLoad", SelfTopUpErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
                }
            }
        } catch (BTSLBaseException be) {
            _log.error("P2PAdhocRechargeUsingCreditCardController[checkTransactionLoad]", "Refusing request getting Exception:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("P2PAdhocRechargeUsingCreditCardController[checkTransactionLoad]", "Refusing request getting Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "checkTransactionLoad", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    /**
     * This method will be called to process the request from the queue
     * 
     * @param p_transferVO
     */
    public void processFromQueue(TransferVO p_transferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("processFromQueue", "Entered");
        }
        Connection con = null;
        try {
            _p2pTransferVO = (P2PTransferVO) p_transferVO;
            _requestVO = _p2pTransferVO.getRequestVO();
            _senderVO = (SenderVO) _requestVO.getSenderVO();
            _receiverVO = (ReceiverVO) _p2pTransferVO.getReceiverVO();
            _type = _requestVO.getType();
            if (_type.equals(PretupsI.INTERFACE_CATEGORY_BOTH)) {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", _requestIDStr, "For =" + _receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO.getAlternateInterfaceType() + " _oldDefaultSelector=" + _oldDefaultSelector + "_newDefaultSelector= " + _newDefaultSelector);
                    }

                    _type = _serviceInterfaceRoutingVO.getInterfaceType();
                    _oldInterfaceCategory = _type;
                    _oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                    _useAlternateCategory = _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool();
                    _newInterfaceCategory = _serviceInterfaceRoutingVO.getAlternateInterfaceType();
                    _newDefaultSelector = _serviceInterfaceRoutingVO.getAlternateDefaultSelectortCode();
                } else {
                    _log.info("process", _requestIDStr, "Service Interface Routing control Not defined, thus using default type=" + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSFER_DEFAULT_SERVICETYPE));
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[process]", "", _senderMSISDN, _senderNetworkCode, "Service Interface Routing control Not defined, thus using default type=" + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSFER_DEFAULT_SERVICETYPE));
                    _type = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSFER_DEFAULT_SERVICETYPE);
                    // _oldDefaultSelector=String.valueOf(SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
                    // Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_transferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        _oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }

                }
            } else {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType() + "_" + _senderVO.getSubscriberType());
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", _requestIDStr, "For =" + _receiverVO.getNetworkCode() + "_" + _requestVO.getServiceType() + " Got Interface Category=" + _serviceInterfaceRoutingVO.getInterfaceType() + " Alternate Check Required=" + _serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() + " Alternate Interface=" + _serviceInterfaceRoutingVO.getAlternateInterfaceType() + " _oldDefaultSelector=" + _oldDefaultSelector + "_newDefaultSelector= " + _newDefaultSelector);
                    }
                    _oldDefaultSelector = _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode();
                } else {
                    // _oldDefaultSelector=String.valueOf(SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
                    // Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_transferVO.getServiceType());
                    if (serviceSelectorMappingVO != null) {
                        _oldDefaultSelector = serviceSelectorMappingVO.getSelectorCode();
                    }
                    _log.info("process", _requestIDStr, "Service Interface Routing control Not defined, thus using default Selector=" + _oldDefaultSelector);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[process]", "", _senderMSISDN, _senderNetworkCode, "Service Interface Routing control Not defined, thus using default selector=" + _oldDefaultSelector);
                }
            }

            if (BTSLUtil.isNullString(_requestVO.getReqSelector())) {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", _requestIDStr, "Selector Not found in Incoming Message Thus using Selector as  " + _oldDefaultSelector);
                }
                _requestVO.setReqSelector(_oldDefaultSelector);
            } else {
                _newDefaultSelector = _requestVO.getReqSelector();
            }

            _requestID = _requestVO.getRequestID();
            _requestIDStr = _requestVO.getRequestIDStr();
            _receiverLocale = _requestVO.getReceiverLocale();
            _transferID = _p2pTransferVO.getTransferID();
            _senderSubscriberType = _senderVO.getSubscriberType();
            _senderNetworkCode = _senderVO.getNetworkCode();
            _senderMSISDN = ((SubscriberVO) _p2pTransferVO.getSenderVO()).getMsisdn();
            _receiverMSISDN = ((SubscriberVO) _p2pTransferVO.getReceiverVO()).getMsisdn();
            _senderLocale = _requestVO.getSenderLocale();
            _receiverLocale = _requestVO.getReceiverLocale();
            _serviceType = _requestVO.getServiceType();
            _senderTransferItemVO = _p2pTransferVO.getSenderTransferItemVO();
            _receiverTransferItemVO = _p2pTransferVO.getReceiverTransferItemVO();
            _performIntfceCatRoutingBeforeVal = _requestVO.isPerformIntfceCatRoutingBeforeVal();
            _receiverDeletionReqFromSubRouting = _requestVO.isReceiverDeletionReqFromSubRouting();
            _receiverInterfaceInfoInDBFound = _requestVO.isReceiverInterfaceInfoInDBFound();
            _senderDeletionReqFromSubRouting = _requestVO.isSenderDeletionReqFromSubRouting();
            _senderInterfaceInfoInDBFound = _requestVO.isSenderInterfaceInfoInDBFound();
            _interfaceCatRoutingDone = _requestVO.isInterfaceCatRoutingDone();

            try {
                LoadController.checkTransactionLoad(((SubscriberVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), _senderTransferItemVO.getInterfaceID(), PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_SENDER);
            } catch (BTSLBaseException e) {
                // Decreasing interface load of receiver which we had
                // incremented before 27/09/06, sender was decreased in the
                // method
                LoadController.decreaseCurrentInterfaceLoad(_transferID, ((ReceiverVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                throw e;
            }

            try {
                LoadController.checkTransactionLoad(((SubscriberVO) _p2pTransferVO.getReceiverVO()).getNetworkCode(), _receiverTransferItemVO.getInterfaceID(), PretupsI.P2P_MODULE, _transferID, true, LoadControllerI.USERTYPE_RECEIVER);
            } catch (BTSLBaseException e) {
                // Decreasing interface load of sender which we had incremented
                // before 27/09/06, receiver was decreased in the method
                LoadController.decreaseTransactionInterfaceLoad(_transferID, ((SenderVO) _p2pTransferVO.getSenderVO()).getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                throw e;
            }

            con = OracleUtil.getConnection();
            // Loading receiver's controll parameters
            PretupsBL.loadRecieverControlLimits(con, _requestIDStr, _p2pTransferVO);
            _receiverVO.setUnmarkRequestStatus(true);
            try {
                con.commit();
            } catch (Exception e) {
            }
            try {
                con.close();
            } catch (Exception e) {
            }
            con = null;

            _processedFromQueue = true;

            if (_log.isDebugEnabled()) {
                _log.debug("P2PAdhocRechargeUsingCreditCardController[processFromQueue]", "_transferID=" + _transferID + " Successfully through load");
            }
            processValidationRequest();
            // Set under process message for the sender and reciever
            p_transferVO.setMessageCode(SelfTopUpErrorCodesI.SENDER_UNDERPROCESS_SUCCESS);
            String[] messageArgArray = { p_transferVO.getTransferID(), PretupsBL.getDisplayAmount(p_transferVO.getRequestedAmount()) };
            p_transferVO.setMessageArguments(messageArgArray);
        } catch (BTSLBaseException be) {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            con = null;
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    con = OracleUtil.getConnection();
                    PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                bex.printStackTrace();
                // _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[processFromQueue]", _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + bex.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[processFromQueue]", _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + e.getMessage());
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);

            if (be.isKey()) {
                if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                    _p2pTransferVO.setErrorCode(be.getMessageKey());
                }
                _p2pTransferVO.setMessageCode(be.getMessageKey());
                _p2pTransferVO.setMessageArguments(be.getArgs());
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
            TransactionLog.log(_transferID, _requestIDStr, _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _p2pTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());

        } catch (Exception e) {
            _log.error("processFromQueue", "Exception:" + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            con = null;
            try {
                if (_receiverVO != null && _receiverVO.isUnmarkRequestStatus()) {
                    con = OracleUtil.getConnection();
                    PretupsBL.unmarkReceiverLastRequest(con, _requestIDStr, _receiverVO);
                }
            } catch (BTSLBaseException bex) {
                bex.printStackTrace();
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[processFromQueue]", _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + bex.getMessage());
            } catch (Exception ex1) {
                ex1.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[processFromQueue]", _transferID, _senderMSISDN, _senderNetworkCode, "Leaving Reciever Unmarked Exception:" + ex1.getMessage());
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            if (_recValidationFailMessageRequired) {
                if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    if (_transferID != null) {
                        _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.P2P_RECEIVER_FAIL, new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                    } else {
                        _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(SelfTopUpErrorCodesI.P2P_FAIL_R, new String[] { PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                    }
                }
            }

            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            _requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            _p2pTransferVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);

            LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);

            TransactionLog.log(_transferID, _requestIDStr, _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, _p2pTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + _requestVO.getMessageCode());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[processFromQueue]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
        } finally {
            try {
                if (con == null) {
                    con = OracleUtil.getConnection();
                }
                if (_transferID != null && !_transferDetailAdded) {
                    addEntryInTransfers(con);
                }
            } catch (BTSLBaseException be) {
                be.printStackTrace();
                _log.error("processFromQueue", "BTSL Base Exception:" + be.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("processFromQueue", "Exception:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[processFromQueue]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            }
            if (BTSLUtil.isNullString(_p2pTransferVO.getMessageCode())) {
                _p2pTransferVO.setMessageCode(SelfTopUpErrorCodesI.REQ_NOT_PROCESS);
            }
            if (con != null) {
                try {
                    con.commit();
                } catch (Exception e) {
                }
                ;
                try {
                    con.close();
                } catch (Exception e) {
                }
                con = null;
            }
            if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
                (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
            } else if (_p2pTransferVO.getReceiverReturnMsg() != null) {
                (new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale)).push();
            }

            TransactionLog.log(_transferID, _requestVO.getRequestIDStr(), _requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller after Queue Processing", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + _requestVO.getMessageCode());
            if (_log.isDebugEnabled()) {
                _log.debug("processFromQueue", "Exiting");
            }
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
        _p2pTransferVO.setCreatedBy(_senderVO.getUserID());
        _p2pTransferVO.setModifiedOn(_currentDate);
        _p2pTransferVO.setModifiedBy(_senderVO.getUserID());
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
    }

    /**
     * Method to credit back the sender for failed or ambigous transaction
     * 
     * @param p_commonClient
     * @param p_onlyDecreaseOnly
     * @throws BTSLBaseException
     */
    private void creditBackSenderForFailedTrans(CommonClient p_commonClient, boolean p_onlyDecreaseOnly) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("creditBackSenderForFailedTrans", _transferID, "Entered with p_onlyDecreaseOnly=" + p_onlyDecreaseOnly);
        }
        Connection con = null;
        try {
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Sender", PretupsI.TXN_LOG_STATUS_SUCCESS, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Credit Back Allowed=" + SystemPreferences.P2P_SNDR_CREDIT_BACK_ALLOWED + " Credit in Ambigous =" + SystemPreferences.P2P_SNDR_CREDIT_BK_AMB_STATUS);

            if (!p_onlyDecreaseOnly) {
                if (SystemPreferences.P2P_SNDR_CREDIT_BACK_ALLOWED) {
                    if ((SystemPreferences.P2P_SNDR_CREDIT_BK_AMB_STATUS && _p2pTransferVO.getTransferStatus().equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS)) || _p2pTransferVO.getTransferStatus().equalsIgnoreCase(SelfTopUpErrorCodesI.TXN_STATUS_FAIL)) {
                        String requestStr = getSenderCreditAdjustStr();

                        TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_CREDITBACK, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");
                        String senderCreditBackResponse = p_commonClient.process(getSenderCreditAdjustStr(), _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);
                        TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_CREDITBACK, senderCreditBackResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                        if (_log.isDebugEnabled()) {
                            _log.debug("creditBackSenderForFailedTrans", _transferID, "senderCreditBackResponse From IN Module=" + senderCreditBackResponse);
                        }

                        boolean isCounterToBeDecreased = true;
                        try {
                            // update the transfer_item details table before
                            // credit back of sender
                            /*
                             * _p2pTransferVO.setModifiedOn(_currentDate);
                             * _p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER)
                             * ;
                             * PretupsBL.updateTransferDetails(con,_p2pTransferVO
                             * );
                             */
                            updateForSenderCreditBackResponse(senderCreditBackResponse);
                            /*
                             * if(!PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS.equals
                             * (_p2pTransferVO.getTransferStatus()))
                             * {
                             * _p2pTransferVO.setCreditBackStatus(PretupsErrorCodesI
                             * .TXN_STATUS_SUCCESS);
                             * _p2pTransferVO.setTransferStatus(PretupsErrorCodesI
                             * .TXN_STATUS_FAIL);
                             * }
                             */
                        } catch (BTSLBaseException be) {
                            isCounterToBeDecreased = false;
                            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Transaction Not Success", PretupsI.TXN_LOG_STATUS_FAIL, "Transfer Status=" + _p2pTransferVO.getTransferStatus() + " Getting Code=" + _senderVO.getInterfaceResponseCode());
                        }
                        TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Success", PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                        con = OracleUtil.getConnection();

                        if (isCounterToBeDecreased) {
                            SubscriberBL.decreaseTransferOutCounts(con, _p2pTransferVO);
                        }

                        _p2pTransferVO.setModifiedOn(_currentDate);
                        _p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                        PretupsBL.updateTransferDetails(con, _p2pTransferVO);

                        PretupsBL.addTransferCreditBackDetails(con, _p2pTransferVO.getTransferID(), _senderCreditBackStatusVO);

                        con.commit();
                        _finalTransferStatusUpdate = false;

                        if (SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS.equals(_p2pTransferVO.getCreditBackStatus())) {
                            _p2pTransferVO.setSenderReturnMessage(getSenderCreditBackMessage());
                        }

                    } else {
                        // SubscriberBL.updateSubscriberLastDetails(con,_p2pTransferVO,_senderVO,_currentDate,PretupsErrorCodesI.TXN_STATUS_FAIL);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[processFromQueue]", _transferID, _senderMSISDN, _senderNetworkCode, "Credit back not required in case of Ambigous cases");
                    }
                } else {
                    // SubscriberBL.updateSubscriberLastDetails(con,_p2pTransferVO,_senderVO,_currentDate,PretupsErrorCodesI.TXN_STATUS_FAIL);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[creditBackSenderForFailedTrans]", _transferID, _senderMSISDN, _senderNetworkCode, "Credit back Not required in case of failed transactions");
                }
                TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Credit Back Done", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            }
            // When Sender Debit fails the decrease the counters only
            else {
                con = OracleUtil.getConnection();
                SubscriberBL.decreaseTransferOutCounts(con, _p2pTransferVO);
                _p2pTransferVO.setModifiedOn(_currentDate);
                _p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                PretupsBL.updateTransferDetails(con, _p2pTransferVO);
                con.commit();
                _finalTransferStatusUpdate = false;
            }
            /*
             * else if(SystemPreferences.P2P_SNDR_CREDIT_BACK_ALLOWED)
             * {
             * if((SystemPreferences.P2P_SNDR_CREDIT_BK_AMB_STATUS &&
             * _p2pTransferVO
             * .getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI
             * .TXN_STATUS_AMBIGUOUS)) ||
             * _p2pTransferVO.getTransferStatus().equalsIgnoreCase
             * (PretupsErrorCodesI.TXN_STATUS_FAIL))
             * {
             * String requestStr=getSenderCreditAdjustStr();
             * 
             * TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,
             * _senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_CREDITBACK
             * ,requestStr,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
             * String senderCreditBackResponse=p_commonClient.process(
             * getSenderCreditAdjustStr
             * (),_transferID,_intModCommunicationTypeS,_intModIPS
             * ,_intModPortS,_intModClassNameS);
             * TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,
             * _senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_REQ,PretupsI.TXN_LOG_TXNSTAGE_CREDITBACK
             * ,senderCreditBackResponse,PretupsI.TXN_LOG_STATUS_SUCCESS,"");
             * 
             * if(_log.isDebugEnabled())_log.debug("creditBackSenderForFailedTrans"
             * ,_transferID,"senderCreditBackResponse From IN Module="+
             * senderCreditBackResponse);
             * 
             * boolean isCounterToBeDecreased=true;
             * try
             * {
             * //update the transfer_item details table before credit back of
             * sender
             * _p2pTransferVO.setModifiedOn(_currentDate);
             * _p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
             * PretupsBL.updateTransferDetails(p_con,_p2pTransferVO);
             * updateForSenderCreditBackResponse(senderCreditBackResponse);
             * if(!PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS.equals(_p2pTransferVO.
             * getTransferStatus()))
             * _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL
             * );
             * }
             * catch(BTSLBaseException be)
             * {
             * isCounterToBeDecreased=false;
             * TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,
             * _senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS
             * ,"Transaction Not Success"
             * ,PretupsI.TXN_LOG_STATUS_FAIL,"Transfer Status="
             * +_p2pTransferVO.getTransferStatus
             * ()+" Getting Code="+_senderVO.getInterfaceResponseCode());
             * }
             * TransactionLog.log(_transferID,_requestIDStr,_senderMSISDN,
             * _senderNetworkCode
             * ,PretupsI.TXN_LOG_REQTYPE_INT,PretupsI.TXN_LOG_TXNSTAGE_PROCESS
             * ,"Credit Back Success",PretupsI.TXN_LOG_STATUS_SUCCESS,"");
             * 
             * PretupsBL.addTransferCreditBackDetails(p_con,_p2pTransferVO.
             * getTransferID(),_senderCreditBackStatusVO);
             * _p2pTransferVO.setSenderReturnMessage(getSenderCreditBackMessage()
             * );
             * 
             * if(isCounterToBeDecreased)
             * SubscriberBL.decreaseTransferOutCounts(p_con,_p2pTransferVO);
             * }
             * else
             * {
             * SubscriberBL.updateSubscriberLastDetails(p_con,_p2pTransferVO,
             * _senderVO,_currentDate,PretupsErrorCodesI.TXN_STATUS_FAIL);
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
             * EventStatusI.RAISED,EventLevelI.INFO,
             * "P2PAdhocRechargeUsingCreditCardController[processFromQueue]"
             * ,_transferID,_senderMSISDN,_senderNetworkCode,
             * "Credit back not required in case of Ambigous cases");
             * }
             * 
             * }
             * else
             * {
             * SubscriberBL.updateSubscriberLastDetails(p_con,_p2pTransferVO,
             * _senderVO,_currentDate,PretupsErrorCodesI.TXN_STATUS_FAIL);
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
             * EventStatusI.RAISED,EventLevelI.INFO,
             * "P2PAdhocRechargeUsingCreditCardController[creditBackSenderForFailedTrans]"
             * ,_transferID,_senderMSISDN,_senderNetworkCode,
             * "Credit back Not required in case of failed transactions");
             * }
             */
        } catch (BTSLBaseException be) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception e) {
                }
            }
            _finalTransferStatusUpdate = false;
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"P2PAdhocRechargeUsingCreditCardController[creditBackSenderForFailedTrans]",_transferID,_senderMSISDN,_senderNetworkCode,"Exception:"+be.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage() + " Getting Code=" + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                }
            }
            _finalTransferStatusUpdate = false;
            _log.error("creditBackSenderForFailedTrans", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[creditBackSenderForFailedTrans]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "creditBackSenderForFailedTrans", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        if (_log.isDebugEnabled()) {
            _log.debug("processValidationRequestInThread", "Entered and performing validations for transfer ID=" + _transferID);
        }
        try {
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Performing Validation in thread", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
            processValidationRequest();
        } catch (BTSLBaseException be) {
            _log.error("P2PAdhocRechargeUsingCreditCardController[processValidationRequestInThread]", "Getting BTSL Base Exception:" + be.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Base Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + be.getMessageKey());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            if (_recValidationFailMessageRequired) {
                if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                    _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((SelfTopUpErrorCodesI.P2P_RECEIVER_FAIL), new String[] { String.valueOf(_transferID), PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) }));
                }
            }

            _p2pTransferVO.setTransferStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            if (BTSLUtil.isNullString(_p2pTransferVO.getErrorCode())) {
                _p2pTransferVO.setErrorCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            _log.error(this, _transferID, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[run]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Exception while performing Validation in thread", PretupsI.TXN_LOG_STATUS_FAIL, "Getting exception =" + e.getMessage());
            throw new BTSLBaseException(this, "processValidationRequestInThread", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            // !_transferDetailAdded Condition Added as we think its not require
            // as already done
            if (_transferID != null && !_transferDetailAdded) {
                Connection con = null;
                try {
                    con = OracleUtil.getConnection();
                    addEntryInTransfers(con);
                    if (_p2pTransferVO.getTransferStatus().equals(SelfTopUpErrorCodesI.TXN_STATUS_FAIL)) {
                        _finalTransferStatusUpdate = false; // No need to update
                                                            // the status of
                                                            // transaction in
                                                            // run method
                    }

                } catch (BTSLBaseException be) {
                    be.printStackTrace();
                    _log.error("processValidationRequestInThread", "BTSLBaseException:" + be.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    _log.error("processValidationRequestInThread", "Exception:" + e.getMessage());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
                } finally {
                    if (con != null) {
                        try {
                            con.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
        strBuff.append("&RECEIVER_MSISDN=" + _receiverMSISDN);
        strBuff.append("&EXTERNAL_ID=" + _senderExternalID);
        strBuff.append("&GATEWAY_CODE=" + _requestVO.getRequestGatewayCode());
        strBuff.append("&GATEWAY_TYPE=" + _requestVO.getRequestGatewayType());
        strBuff.append("&IMSI=" + BTSLUtil.NullToString(_senderIMSI));
        strBuff.append("&SENDER_ID=" + ((SenderVO) _requestVO.getSenderVO()).getUserID());
        strBuff.append("&SERVICE_TYPE=" + _senderSubscriberType + "-" + _type);
        strBuff.append("&ADJUST=Y");
        strBuff.append("&INTERFACE_PREV_BALANCE=" + _senderTransferItemVO.getPostBalance());
        // Avinash send the requested amount to IN. to use card group only for
        // reporting purpose.
        strBuff.append("&REQUESTED_AMOUNT=" + _p2pTransferVO.getRequestedAmount());
        // Added for closing the sender credit back issue.as below parameter was
        // not set
        strBuff.append("&CAL_OLD_EXPIRY_DATE=" + _senderTransferItemVO.getOldExporyInMillis());// @nu
        strBuff.append("&VALIDITY_DAYS=" + _senderTransferItemVO.getValidity());

        if (SystemPreferences.ENQ_POSTBAL_IN) {
            strBuff.append("&ENQ_POSTBAL_IN=" + PretupsI.YES);
        } else {
            strBuff.append("&ENQ_POSTBAL_IN=" + PretupsI.NO);
        }
        if (SystemPreferences.ENQ_POSTBAL_ALLOW) {
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
        String[] messageArgArray = { _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverTransferValue()), _senderMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getReceiverAccessFee()) };
        return BTSLUtil.getMessage(_receiverLocale, SelfTopUpErrorCodesI.P2P_RECEIVER_UNDERPROCESS, messageArgArray);
    }

    /**
     * Method to get the under process message before validation to be sent to
     * sender
     * 
     * @return
     */
    private String getSndrUPMsgBeforeValidation() {
        String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(_senderLocale, SelfTopUpErrorCodesI.P2P_SENDER_UNDERPROCESS_B4VAL, messageArgArray);
    }

    /**
     * Method to get the success message to be sent to sender
     * 
     * @return
     */
    private String getSenderUnderProcessMessage() {
        String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_p2pTransferVO.getSenderTransferValue()), PretupsBL.getDisplayAmount(_p2pTransferVO.getSenderAccessFee()) };
        return BTSLUtil.getMessage(_senderLocale, SelfTopUpErrorCodesI.P2P_SENDER_UNDERPROCESS, messageArgArray);
    }

    /**
     * Method to get the credit back message
     * 
     * @return
     */
    private String getSenderCreditBackMessage() {
        if (BTSLUtil.isNullString(_senderCreditPostBalanceAvailable) || "Y".equals(_senderCreditPostBalanceAvailable)) {
            String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(_senderTransferItemVO.getPostBalance()) };
            return BTSLUtil.getMessage(_senderLocale, SelfTopUpErrorCodesI.P2P_SENDER_CREDIT_BACK, messageArgArray);
        }
        String[] messageArgArray = { _receiverMSISDN, _transferID, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()) };
        return BTSLUtil.getMessage(_senderLocale, SelfTopUpErrorCodesI.P2P_SENDER_CREDIT_BACK_WITHOUT_POSTBAL, messageArgArray);

    }

    /**
     * Method that will add entry in Transfer Table if not added else update the
     * records
     * 
     * @param p_con
     */
    private void addEntryInTransfers(Connection p_con) {
        try {
            // METHOD FOR INSERTING AND UPDATION IN P2P Transfer Table
            if (!_transferDetailAdded) {
                PretupsBL.addTransferDetails(p_con, _p2pTransferVO);// add
                                                                    // transfer
                                                                    // details
                                                                    // in
                                                                    // database
            } else if (_transferDetailAdded) {
                _p2pTransferVO.setModifiedOn(new Date());
                _p2pTransferVO.setModifiedBy(_p2pTransferVO.getSenderID());
                PretupsBL.updateTransferDetails(p_con, _p2pTransferVO);// add
                                                                       // transfer
                                                                       // details
                                                                       // in
                                                                       // database
            }
            p_con.commit();
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            if (!_isCounterDecreased && _decreaseTransactionCounts) {
                LoadController.decreaseTransactionLoad(_transferID, _senderNetworkCode, LoadControllerI.DEC_LAST_TRANS_COUNT);
                _isCounterDecreased = true;
            }
            _log.error("addEntryInTransfers", _transferID, "BTSLBaseException while adding transfer details in database:" + be.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[process]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
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
        if (_log.isDebugEnabled()) {
            _log.debug("getInterfaceRoutingDetails", _requestIDStr, " Entered with MSISDN=" + p_msisdn + " Prefix ID=" + p_prefixID + " p_subscriberType=" + p_subscriberType + " p_networkCode=" + p_networkCode + " p_serviceType=" + p_serviceType + " p_interfaceCategory=" + p_interfaceCategory + " p_userType=" + p_userType + " p_action=" + p_action);
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
        _performIntfceCatRoutingBeforeVal = false; // Set so that receiver flag
                                                   // is not overridden by
                                                   // sender flag
        SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + p_serviceType + "_" + p_interfaceCategory);
        try {
            if (subscriberRoutingControlVO != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("getInterfaceRoutingDetails", _transferID, " p_userType=" + p_userType + " Database Check Required=" + subscriberRoutingControlVO.isDatabaseCheckBool() + " Series Check Required=" + subscriberRoutingControlVO.isSeriesCheckBool());
                }

                if (subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)) {
                        ListValueVO listValueVO = PretupsBL.validateNumberInRoutingDatabase(p_con, p_msisdn, p_interfaceCategory);
                        if (listValueVO != null) {
                            isSuccess = true;
                            setInterfaceDetails(p_prefixID, p_userType, listValueVO, false, null, null);

                            if (p_userType.equals(PretupsI.USER_TYPE_SENDER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                _senderInterfaceInfoInDBFound = true;
                                _senderDeletionReqFromSubRouting = true;
                            } else if (p_userType.equals(PretupsI.USER_TYPE_RECEIVER) && p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                _receiverInterfaceInfoInDBFound = true;
                                _receiverDeletionReqFromSubRouting = true;
                            }
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("getInterfaceRoutingDetails", _transferID, " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " not found in Database , performing Series Check for Prefix ID=" + p_prefixID);
                            }
                            // service selector based checks added
                            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            if (SystemPreferences.SELECTOR_INTERFACE_MAPPING) {
                                interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _p2pTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                            }
                            if (interfaceMappingVO1 == null) {
                                try {
                                    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                                    isSuccess = true;
                                    setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
                                } catch (BTSLBaseException be) {
                                    if (be.isKey() && (be.getMessageKey().equals(SelfTopUpErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                        _performIntfceCatRoutingBeforeVal = true;
                                    } else {
                                        throw be;
                                    }
                                }
                            } else {
                                isSuccess = true;
                                setInterfaceDetails(p_prefixID, p_userType, null, true, null, interfaceMappingVO1);
                            }
                        } else {
                            _performIntfceCatRoutingBeforeVal = true;
                            isSuccess = false;
                        }
                    } else if (p_interfaceCategory.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)) {
                        WhiteListVO whiteListVO = PretupsBL.validateNumberInWhiteList(p_con, p_msisdn);
                        if (whiteListVO != null) {
                            isSuccess = true;
                            ListValueVO listValueVO = whiteListVO.getListValueVO();
                            interfaceID = listValueVO.getValue();
                            interfaceHandlerClass = listValueVO.getLabel();
                            underProcessMsgReqd = listValueVO.getType();
                            allServiceClassID = listValueVO.getTypeName();
                            externalID = listValueVO.getIDValue();

                            if (p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                                _senderTransferItemVO.setInterfaceID(interfaceID);
                                _senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                                _senderAllServiceClassID = allServiceClassID;
                                _senderExternalID = externalID;
                                // Mark the Post Paid Interface as Online
                                _senderVO.setPostOfflineInterface(true);

                                _senderTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
                                _senderVO.setCreditLimit(whiteListVO.getCreditLimit());
                                _senderTransferItemVO.setReferenceID(whiteListVO.getAccountID());
                                _senderTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
                                _senderIMSI = whiteListVO.getImsi();
                                _senderTransferItemVO.setPrefixID(p_prefixID);
                                _senderTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
                                if (p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                    _senderInterfaceInfoInDBFound = true;
                                }
                            } else {
                                _receiverTransferItemVO.setPrefixID(p_prefixID);
                                _receiverTransferItemVO.setInterfaceID(interfaceID);
                                _receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                                if (PretupsI.YES.equals(underProcessMsgReqd)) {
                                    _p2pTransferVO.setUnderProcessMsgReq(true);
                                }
                                _receiverAllServiceClassID = allServiceClassID;
                                _receiverExternalID = externalID;
                                _receiverVO.setPostOfflineInterface(true);

                                _receiverTransferItemVO.setPreviousBalance(whiteListVO.getCreditLimit());
                                _receiverTransferItemVO.setServiceClassCode(whiteListVO.getServiceClassCode());
                                _receiverTransferItemVO.setReferenceID(whiteListVO.getAccountID());
                                _receiverIMSI = whiteListVO.getImsi();
                                _receiverTransferItemVO.setAccountStatus(whiteListVO.getAccountStatus());
                                if (p_action.equals(PretupsI.INTERFACE_VALIDATE_ACTION)) {
                                    _receiverInterfaceInfoInDBFound = true;
                                }
                            }
                            if (!PretupsI.YES.equals(listValueVO.getStatus())) {
                                // ChangeID=LOCALEMASTER
                                // which language message to be set is
                                // determined from the locale master table for
                                // the requested locale
                                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(_senderLocale)).getMessage())) {
                                    _p2pTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo());
                                } else {
                                    _p2pTransferVO.setSenderReturnMessage(listValueVO.getOtherInfo2());
                                }
                                throw new BTSLBaseException(this, "getInterfaceRoutingDetails", SelfTopUpErrorCodesI.INTERFACE_NOT_ACTIVE);
                            }
                        } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("getInterfaceRoutingDetails", _transferID, " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " not found in Database , performing Series Check for Prefix ID=" + p_prefixID);
                            }

                            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                            // check service selector based check loading of
                            // interface
                            ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                            if (SystemPreferences.SELECTOR_INTERFACE_MAPPING) {
                                interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _p2pTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                            }
                            if (interfaceMappingVO1 == null) {
                                try {
                                    interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                                    isSuccess = true;
                                    setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
                                } catch (BTSLBaseException be) {
                                    if (be.isKey() && (be.getMessageKey().equals(SelfTopUpErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                        _performIntfceCatRoutingBeforeVal = true;
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
                            _performIntfceCatRoutingBeforeVal = true;
                        }
                    }
                } else if (subscriberRoutingControlVO.isSeriesCheckBool()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("getInterfaceRoutingDetails", _transferID, " p_userType=" + p_userType + " MSISDN =" + p_msisdn + " performing Series Check for Prefix ID=" + p_prefixID);
                    }

                    MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                    // check service selector based check loading of interface
                    ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                    if (SystemPreferences.SELECTOR_INTERFACE_MAPPING) {
                        interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _p2pTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                    }
                    if (interfaceMappingVO1 == null) {
                        try {
                            interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                            isSuccess = true;
                            setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && (be.getMessageKey().equals(SelfTopUpErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                                _performIntfceCatRoutingBeforeVal = true;
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
                if (_log.isDebugEnabled()) {
                    _log.debug("getInterfaceRoutingDetails", _transferID, " By default carrying out series check as routing control not defined for p_userType=" + p_userType + " MSISDN =" + p_msisdn + " performing Series Check for Prefix ID=" + p_prefixID);
                }
                // This event is raised by ankit Z on date 3/8/06 for case when
                // entry not found in routing control and considering series
                // based routing
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[getInterfaceRoutingDetails]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:Routing control information not defined so performing series based routing");

                MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
                // check service selector based check loading of interface
                ServiceSelectorInterfaceMappingVO interfaceMappingVO1 = null;
                if (SystemPreferences.SELECTOR_INTERFACE_MAPPING) {
                    interfaceMappingVO1 = (ServiceSelectorInterfaceMappingVO) ServiceSelectorInterfaceMappingCache.getObject(_serviceType + "_" + _p2pTransferVO.getSubService() + "_" + p_action + "_" + p_networkCode + "_" + p_prefixID);
                }
                if (interfaceMappingVO1 == null) {
                    try {
                        interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(p_prefixID, p_subscriberType, p_action);
                        isSuccess = true;
                        setInterfaceDetails(p_prefixID, p_userType, null, true, interfaceMappingVO, null);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && (be.getMessageKey().equals(SelfTopUpErrorCodesI.MSISDN_PREFIX_INTERFACE_MAPPING_NOTFOUND))) {
                            _performIntfceCatRoutingBeforeVal = true;
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
                _senderTransferItemVO.setInterfaceType(p_interfaceCategory);
            } else if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
                _receiverTransferItemVO.setInterfaceType(_type);
                _receiverTransferItemVO.setSubscriberType(_type);
            }
        } catch (BTSLBaseException be) {
            if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_SENDER)) {
                _senderTransferItemVO.setInterfaceType(p_interfaceCategory);
            } else if (isSuccess && p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
                _receiverTransferItemVO.setInterfaceType(_type);
                _receiverTransferItemVO.setSubscriberType(_type);
            }
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[getInterfaceRoutingDetails]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            isSuccess = false;
            throw new BTSLBaseException(this, "getInterfaceRoutingDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getInterfaceRoutingDetails", _requestIDStr, " Exiting with isSuccess=" + isSuccess + "_senderAllServiceClassID=" + _senderAllServiceClassID + " _receiverAllServiceClassID=" + _receiverAllServiceClassID);
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
        if (_log.isDebugEnabled()) {
            _log.debug("performSenderAlternateRouting", _requestIDStr, " Entered ");
        }
        Connection con = null;
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
                    LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(_transferID, _p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(_senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null, null);

                    checkTransactionLoad(PretupsI.USER_TYPE_SENDER, _senderTransferItemVO.getInterfaceID());

                    // validate sender limits after Interface Validations
                    con = OracleUtil.getConnection();
                    SubscriberBL.validateSenderLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);

                    if (con != null) {
                        con.close();
                        con = null;
                    }
                    requestStr = getSenderValidateStr();
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + _senderMSISDN + " on ALternate Routing 1 to =" + _senderTransferItemVO.getInterfaceID());
                    }

                    senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(senderValResponse, 1, altList.size());
                        // if(PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) &&
                        // InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus()))
                        if (InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
                            // Update in DB for routing interface
                            // updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER,_p2pTransferVO.getNetworkCode(),_senderTransferItemVO.getInterfaceID(),_senderExternalID,_senderMSISDN,_p2pTransferVO.getPaymentMethodType(),_senderVO.getUserID(),_currentDate);
                            _isSenderRoutingUpdate = true;
                        }
                    } catch (BTSLBaseException be) {
                        throw be;
                    } catch (Exception e) {
                        throw e;
                    }

                    break;
                }
                case 2: {
                    LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                    LoadController.decreaseTransactionInterfaceLoad(_transferID, _p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(_senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null, null);

                    checkTransactionLoad(PretupsI.USER_TYPE_SENDER, _senderTransferItemVO.getInterfaceID());

                    con = OracleUtil.getConnection();
                    SubscriberBL.validateSenderLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);

                    if (con != null) {
                        con.close();
                        con = null;
                    }

                    requestStr = getSenderValidateStr();

                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                    TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + _senderMSISDN + " on ALternate Routing 1 to =" + _senderTransferItemVO.getInterfaceID());
                    }

                    senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        senderValidateResponse(senderValResponse, 1, altList.size());
                        if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
                            // Update in DB for routing interface
                            updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, _p2pTransferVO.getNetworkCode(), _senderTransferItemVO.getInterfaceID(), _senderExternalID, _senderMSISDN, _p2pTransferVO.getPaymentMethodType(), _senderVO.getUserID(), _currentDate);

                        }
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("performSenderAlternateRouting", "Got Status=" + InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND + " After validation Request For MSISDN=" + _senderMSISDN + " Performing Alternate Routing to 2");
                            }

                            if (con != null) {
                                con.close();
                                con = null;
                            }

                            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(_transferID, _p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(1);

                            setInterfaceDetails(_senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, false, null, null);

                            checkTransactionLoad(PretupsI.USER_TYPE_SENDER, _senderTransferItemVO.getInterfaceID());

                            // validate sender limits after Interface
                            // Validations
                            con = OracleUtil.getConnection();
                            SubscriberBL.validateSenderLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);

                            if (con != null) {
                                con.close();
                                con = null;
                            }

                            requestStr = getSenderValidateStr();

                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            if (_log.isDebugEnabled()) {
                                _log.debug("performSenderAlternateRouting", "Sending Request For MSISDN=" + _senderMSISDN + " on ALternate Routing 2 to =" + _senderTransferItemVO.getInterfaceID());
                            }

                            senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                            try {
                                senderValidateResponse(senderValResponse, 2, altList.size());
                                if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
                                    // Update in DB for routing interface
                                    updateSubscriberRoutingDetails(PretupsI.USER_TYPE_SENDER, _p2pTransferVO.getNetworkCode(), _senderTransferItemVO.getInterfaceID(), _senderExternalID, _senderMSISDN, _p2pTransferVO.getPaymentMethodType(), _senderVO.getUserID(), _currentDate);

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
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[performSenderAlternateRouting]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performSenderAlternateRouting", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                con = null;
            }// end if
        }
        if (_log.isDebugEnabled()) {
            _log.debug("performSenderAlternateRouting", _requestIDStr, " Exiting ");
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
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _senderTransferItemVO.getInterfaceID(), interfaceStatusType, SelfTopUpErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        // This has been done so that when Alternate routing has to be performed
        // and when we have to get out and throw error
        if ((InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) || (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && !isRoutingSecond)) {
            _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
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
            _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
            _senderTransferItemVO.setTransferStatus(status);
            _receiverTransferItemVO.setValidationStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
            _receiverTransferItemVO.setTransferStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { _receiverMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
            throw new BTSLBaseException(this, "senderValidateResponse", SelfTopUpErrorCodesI.P2P_SENDER_FAIL, 0, strArr, null);
        }

        _senderTransferItemVO.setTransferStatus(status);
        _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS);

        try {
            _senderTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
        }
        ;
        try {
            _senderTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
        }
        ;

        _senderTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));
        try {
            _senderTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
            _senderTransferItemVO.setBalanceCheckReq(false);
        }
        _senderVO.setCreditLimit(_senderTransferItemVO.getPreviousBalance());

        // Update the Previous Balance in case of Post Paid Offline interface
        // with Credit Limit - Monthly Transfer Amount
        if (_senderVO.isPostOfflineInterface()) {
            boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(_senderVO.getLastSuccessTransferDate(), _currentDate, BTSLUtil.PERIOD_MONTH);
            if (!isPeriodChange) {
                _senderTransferItemVO.setPreviousBalance(_senderTransferItemVO.getPreviousBalance() - _senderVO.getMonthlyTransferAmount());
            }
        }

        _senderTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
        _senderTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));
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
        if (_log.isDebugEnabled()) {
            _log.debug("updateReceiverLocale", "Entered p_languageCode=" + p_languageCode);
        }
        // check if language is returned fron IN or not.
        // If not then send alarm and not set the locale
        // otherwise set the local corresponding to the code returned from the
        // IN.
        if (!BTSLUtil.isNullString(p_languageCode)) {
            try {
                if (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode) == null) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[updateReceiverLocale]", _transferID, _receiverMSISDN, "", "Exception: Notification language returned from IN is not defined in system p_languageCode: " + p_languageCode);
                } else {
                    _receiverLocale = (LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("updateReceiverLocale", "Exited _receiverLocale=" + _receiverLocale);
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
        if (_log.isDebugEnabled()) {
            _log.debug("performAlternateCategoryRouting", "Performing ALternate interface category routing Entered");
        }
        Connection con = null;
        try {
            String requestStr = null;
            CommonClient commonClient = null;
            String receiverValResponse = null;

            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
            LoadController.decreaseReceiverTransactionInterfaceLoad(_transferID, _p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

            con = OracleUtil.getConnection();

            // populates the alternate interface category details
            populateAlternateInterfaceDetails(con);

            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception be) {
                }
                try {
                    con.close();
                } catch (Exception be) {
                }
                con = null;
            }
            _p2pTransferVO.setTransferCategory(_senderSubscriberType + "-" + _type);
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestIDStr, "Overriding transfer Category as :" + _p2pTransferVO.getTransferCategory());
            }

            _p2pTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);

            checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

            // validate receiver limits before Interface Validations
            PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

            requestStr = getReceiverValidateStr();
            commonClient = new CommonClient();

            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_VAL);

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Alternate Category Routing");

            receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            handleReceiverValidateResponse(receiverValResponse, SRC_AFTER_INRESP_CAT_ROUTING);
            if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
                // If mobile number found on Post but previously was defined in
                // PRE then delete the number
                if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                    if (_receiverDeletionReqFromSubRouting) {
                        PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _oldInterfaceCategory);
                    }
                } else {
                    // Update in DB for routing interface
                    SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _newInterfaceCategory);
                    if (!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("performAlternateCategoryRouting", "Inserting the MSISDN=" + _receiverMSISDN + " in Subscriber routing database for further usage");
                        }

                        PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _newInterfaceCategory, _senderVO.getUserID(), _currentDate);
                        _receiverInterfaceInfoInDBFound = true;
                        _receiverDeletionReqFromSubRouting = true;
                    }
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[performAlternateCategoryRouting]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performAlternateCategoryRouting", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception be) {
                }
                try {
                    con.close();
                } catch (Exception be) {
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
        if (_log.isDebugEnabled()) {
            _log.debug("populateAlternateInterfaceDetails", "Entered to get the alternate category");
        }

        boolean isReceiverFound = false;

        if (!_interfaceCatRoutingDone) {
            _interfaceCatRoutingDone = true;
            _type = _newInterfaceCategory;
            _networkPrefixVO = null;

            _requestVO.setReqSelector(_newDefaultSelector);
            _p2pTransferVO.setSubService(_newDefaultSelector);

            // Load the new prefix ID against the interface category , If Not
            // required then give the error

            if (_log.isDebugEnabled()) {
                _log.debug("populateAlternateInterfaceDetails", "Got the alternate category as =" + _type);
            }

            _networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _type);
            if (_networkPrefixVO != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("populateAlternateInterfaceDetails", "Got the Prefix ID for MSISDN=" + _receiverMSISDN + "Prefix ID=" + _networkPrefixVO.getPrefixID());
                }

                _receiverVO.setNetworkCode(_networkPrefixVO.getNetworkCode());
                _receiverVO.setPrefixID(_networkPrefixVO.getPrefixID());
                _receiverVO.setSubscriberType(_networkPrefixVO.getSeriesType());
                isReceiverFound = getInterfaceRoutingDetails(p_con, _receiverMSISDN, _receiverVO.getPrefixID(), _receiverVO.getSubscriberType(), _receiverVO.getNetworkCode(), _p2pTransferVO.getServiceType(), _type, PretupsI.USER_TYPE_RECEIVER, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
            } else {
                _log.error(this, "Series Not Defined for Alternate Interface =" + _type + " For Series=" + _receiverVO.getMsisdnPrefix());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "P2PAdhocRechargeUsingCreditCardController[populateAlternateInterfaceDetails]", "", "", "", "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But required for validation");
                isReceiverFound = false;
            }

            if (!isReceiverFound) {
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "populateServicePaymentInterfaceDetails", SelfTopUpErrorCodesI.P2P_NOTFOUND_SERVICEINTERFACEMAPPING);
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
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");
        ArrayList altList = null;
        boolean isRequired = false;

        // If we get the MSISDN not found on interface error then perform
        // interface routing
        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
            _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            altList = InterfaceRoutingControlCache.getRoutingControlDetails(_receiverTransferItemVO.getInterfaceID());
            if (altList != null && altList.size() > 0) {
                performReceiverAlternateRouting(altList, p_source);
            } else {
                isRequired = true;
            }
        }
        if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
            populateReceiverItemsDetails(map);
            // For Service Provider Information
            _receiverTransferItemVO.setServiceProviderName(BTSLUtil.NullToString((String) map.get("SPNAME")));
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
        if (_log.isDebugEnabled()) {
            _log.debug("performReceiverAlternateRouting", _requestIDStr, " Entered p_source=" + p_source);
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
                    LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
                    LoadController.decreaseReceiverTransactionInterfaceLoad(_transferID, _p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(_receiverTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_RECEIVER, listValueVO, false, null, null);

                    checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

                    // validate receiver limits before Interface Validations
                    PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

                    requestStr = getReceiverValidateStr();
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_VAL);

                    TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performReceiverAlternateRouting", "Sending Request For MSISDN=" + _receiverMSISDN + " on ALternate Routing 1 to =" + _receiverTransferItemVO.getInterfaceID());
                    }

                    receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        receiverValidateResponse(receiverValResponse, 1, altList.size(), p_source);
                        // If source is before IN validation then if interface
                        // is pre then we need to update in subscriber
                        // Routing but after alternate routing if number is
                        // found on another interface
                        // Then we need to delete the number from subscriber
                        // Routing or Vice versa
                        if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
                                // Update in DB for routing interface
                                updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER, _p2pTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _type, _senderVO.getUserID(), _currentDate);
                            }
                        } else {
                            if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
                                if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                                    if (_receiverDeletionReqFromSubRouting) {
                                        PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _oldInterfaceCategory);
                                    }
                                } else {
                                    // Update in DB for routing interface
                                    SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _newInterfaceCategory);
                                    if (!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                        PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _newInterfaceCategory, _senderVO.getUserID(), _currentDate);
                                        _receiverInterfaceInfoInDBFound = true;
                                        _receiverDeletionReqFromSubRouting = true;
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
                    LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
                    LoadController.decreaseReceiverTransactionInterfaceLoad(_transferID, _p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                    listValueVO = (ListValueVO) altList.get(0);

                    setInterfaceDetails(_receiverTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_RECEIVER, listValueVO, false, null, null);

                    checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

                    // validate receiver limits before Interface Validations
                    PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

                    requestStr = getReceiverValidateStr();
                    commonClient = new CommonClient();

                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_VAL);

                    TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 1");

                    if (_log.isDebugEnabled()) {
                        _log.debug("performReceiverAlternateRouting", "Sending Request For MSISDN=" + _receiverMSISDN + " on ALternate Routing 1 to =" + _receiverTransferItemVO.getInterfaceID());
                    }

                    receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                    TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

                    try {
                        receiverValidateResponse(receiverValResponse, 1, altList.size(), p_source);
                        // If source is before IN validation then if interface
                        // is pre then we need to update in subscriber
                        // Routing but after alternate routing if number is
                        // found on another interface
                        // Then we need to delete the number from subscriber
                        // Routing or Vice versa

                        if (p_source == SRC_BEFORE_INRESP_CAT_ROUTING) {
                            if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
                                // Update in DB for routing interface
                                updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER, _p2pTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _type, _senderVO.getUserID(), _currentDate);
                            }
                        } else {
                            if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
                                if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                                    if (_receiverDeletionReqFromSubRouting) {
                                        PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _oldInterfaceCategory);
                                    }
                                } else {
                                    // Update in DB for routing interface
                                    SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _newInterfaceCategory);
                                    if (!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                        PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _newInterfaceCategory, _senderVO.getUserID(), _currentDate);
                                        _receiverInterfaceInfoInDBFound = true;
                                        _receiverDeletionReqFromSubRouting = true;
                                    }
                                }
                            }
                        }
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("performReceiverAlternateRouting", "Got Status=" + InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND + " After validation Request For MSISDN=" + _receiverMSISDN + " Performing Alternate Routing to 2");
                            }

                            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.RECEIVER_VAL_RESPONSE);
                            LoadController.decreaseReceiverTransactionInterfaceLoad(_transferID, _p2pTransferVO.getReceiverNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(1);

                            setInterfaceDetails(_receiverTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_RECEIVER, listValueVO, false, null, null);

                            checkTransactionLoad(PretupsI.USER_TYPE_RECEIVER, _receiverTransferItemVO.getInterfaceID());

                            // validate receiver limits before Interface
                            // Validations
                            PretupsBL.validateRecieverLimits(null, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL, PretupsI.P2P_MODULE);

                            requestStr = getReceiverValidateStr();

                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.RECEIVER_UNDER_VAL);

                            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "Performing Interface Routing 2");

                            if (_log.isDebugEnabled()) {
                                _log.debug("performReceiverAlternateRouting", "Sending Request For MSISDN=" + _receiverMSISDN + " on ALternate Routing 2 to =" + _receiverTransferItemVO.getInterfaceID());
                            }

                            receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                            TransactionLog.log(_transferID, _requestIDStr, _receiverMSISDN, _receiverVO.getNetworkCode(), PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, receiverValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

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
                                    if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
                                        // Update in DB for routing interface
                                        updateSubscriberRoutingDetails(PretupsI.USER_TYPE_RECEIVER, _p2pTransferVO.getReceiverNetworkCode(), _receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _type, _senderVO.getUserID(), _currentDate);
                                    }
                                } else {
                                    if (InterfaceErrorCodesI.SUCCESS.equals(_receiverTransferItemVO.getValidationStatus())) {
                                        if (_newInterfaceCategory.equals(PretupsI.INTERFACE_CATEGORY_POST)) {
                                            if (_receiverDeletionReqFromSubRouting) {
                                                PretupsBL.deleteSubscriberInterfaceRouting(_receiverMSISDN, _oldInterfaceCategory);
                                            }
                                        } else {
                                            // Update in DB for routing
                                            // interface
                                            SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _newInterfaceCategory);
                                            if (!_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                                                PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _newInterfaceCategory, _senderVO.getUserID(), _currentDate);
                                                _receiverInterfaceInfoInDBFound = true;
                                                _receiverDeletionReqFromSubRouting = true;
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
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[performAlternateRouting]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performReceiverAlternateRouting", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("performReceiverAlternateRouting", _requestIDStr, " Exiting ");
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
        HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        String status = (String) map.get("TRANSACTION_STATUS");

        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME.equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, _receiverTransferItemVO.getInterfaceID(), interfaceStatusType, SelfTopUpErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) {
            _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            throw new BTSLBaseException(this, "receiverValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        } else if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == p_altSize && p_source == SRC_BEFORE_INRESP_CAT_ROUTING && _useAlternateCategory && !_interfaceCatRoutingDone) {
            if (_log.isDebugEnabled()) {
                _log.debug(this, " Performing Alternate category routing as MSISDN not found on any interfaces after routing for " + _receiverMSISDN);
            }
            performAlternateCategoryRouting();
        } else {
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

            // If status is other than Success in validation stage mark sender
            // request as Not applicable and
            // Make transaction Fail
            String[] strArr = null;

            if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                _p2pTransferVO.setErrorCode(status + "_R");
                _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
                _receiverTransferItemVO.setTransferStatus(status);
                _senderTransferItemVO.setValidationStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                _senderTransferItemVO.setTransferStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
                strArr = new String[] { _receiverMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "receiverValidateResponse", SelfTopUpErrorCodesI.P2P_SENDER_FAIL, 0, strArr, null);
            }
            _receiverTransferItemVO.setTransferStatus(status);
            _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS);
            _receiverTransferItemVO.setSubscriberType(_type);
            _receiverVO.setSubscriberType(_type);

            try {
                _receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
            }
            ;
            try {
                _receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
            } catch (Exception e) {
            }
            ;
            _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());

            _receiverTransferItemVO.setFirstCall((String) map.get("FIRST_CALL"));
            _receiverTransferItemVO.setGraceDaysStr((String) map.get("GRACE_DAYS"));

            _receiverTransferItemVO.setServiceClassCode((String) map.get("SERVICE_CLASS"));

            // Done so that receiver check can be brough to common
            _receiverVO.setServiceClassCode(_receiverTransferItemVO.getServiceClass());

            try {
                _receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) map.get("INTERFACE_PREV_BALANCE")));
            } catch (Exception e) {
            }
            ;

            // Update the Previous Balance in case of Post Paid Offline
            // interface with Credit Limit - Monthly Transfer Amount
            if (_receiverVO.isPostOfflineInterface()) {
                boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(_receiverVO.getLastSuccessOn(), _currentDate, BTSLUtil.PERIOD_MONTH);
                if (!isPeriodChange) {
                    _receiverTransferItemVO.setPreviousBalance(_receiverTransferItemVO.getPreviousBalance() - _receiverVO.getMonthlyTransferAmount());
                }
            }

            // TO DO Done for testing purpose should we use it or give exception
            // in this case
            if (_receiverTransferItemVO.getPreviousExpiry() == null) {
                _receiverTransferItemVO.setPreviousExpiry(_currentDate);
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
        String status = (String) p_map.get("TRANSACTION_STATUS");
        // receiver language has to be taken from IN then the block below will
        // execute
        if ("Y".equals(_requestVO.getUseInterfaceLanguage())) {
            // update the receiver locale if language code returned from IN is
            // not null
            updateReceiverLocale((String) p_map.get("IN_LANG"));
        }
        _receiverTransferItemVO.setProtocolStatus((String) p_map.get("PROTOCOL_STATUS"));
        _receiverTransferItemVO.setAccountStatus((String) p_map.get("ACCOUNT_STATUS"));
        _receiverTransferItemVO.setInterfaceResponseCode((String) p_map.get("INTERFACE_STATUS"));
        _receiverTransferItemVO.setValidationStatus(status);
        _receiverVO.setInterfaceResponseCode(_receiverTransferItemVO.getInterfaceResponseCode());

        if (!BTSLUtil.isNullString((String) p_map.get("IN_TXN_ID"))) {
            try {
                _receiverTransferItemVO.setInterfaceReferenceID((String) p_map.get("IN_TXN_ID"));
            } catch (Exception e) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PAdhocRechargeUsingCreditCardController[updateForReceiverValidateResponse]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception while parsing for interface txn ID , Exception:" + e.getMessage());
            }
        }
        _receiverTransferItemVO.setReferenceID((String) p_map.get("IN_RECON_ID"));

        String[] strArr = null;

        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            _p2pTransferVO.setErrorCode(status + "_R");
            _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
            _receiverTransferItemVO.setTransferStatus(status);

            _senderVO.setTransactionStatus(SelfTopUpErrorCodesI.REQUEST_NOT_APPLICABLE);
            strArr = new String[] { _receiverMSISDN, PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount()), _transferID };
            // throw new
            // BTSLBaseException("P2PAdhocRechargeUsingCreditCardController","updateForReceiverValidateResponse",PretupsErrorCodesI.P2P_SENDER_FAIL,0,strArr,null);
            // throw new
            // BTSLBaseException("P2PAdhocRechargeUsingCreditCardController","populateReceiverItemsDetails",_p2pTransferVO.getErrorCode(),0,strArr,null);
            if (InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P.equals(_receiverTransferItemVO.getValidationStatus())) {
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "populateReceiverItemsDetails", InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P + "_S", 0, strArr, null);
            } else {
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "populateReceiverItemsDetails", _p2pTransferVO.getErrorCode(), 0, strArr, null);
            }
        }
        _receiverTransferItemVO.setTransferStatus(status);
        _receiverTransferItemVO.setSubscriberType(_type);
        _receiverVO.setSubscriberType(_type);

        _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_SUCCESS);
        SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(_p2pTransferVO.getReceiverNetworkCode() + "_" + _p2pTransferVO.getServiceType() + "_" + _type);
        if (PretupsI.INTERFACE_CATEGORY_PRE.equals(_type) && !_receiverDeletionReqFromSubRouting && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
            PretupsBL.insertSubscriberInterfaceRouting(_receiverTransferItemVO.getInterfaceID(), _receiverExternalID, _receiverMSISDN, _type, _senderVO.getUserID(), _currentDate);
            _receiverInterfaceInfoInDBFound = true;
            _receiverDeletionReqFromSubRouting = true;
        }

        try {
            _receiverTransferItemVO.setPreviousExpiry(BTSLUtil.getDateFromDateString((String) p_map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
        }
        ;
        try {
            _receiverTransferItemVO.setPreviousGraceDate(BTSLUtil.getDateFromDateString((String) p_map.get("OLD_GRACE_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
        }
        ;
        _receiverTransferItemVO.setFirstCall((String) p_map.get("FIRST_CALL"));
        _receiverTransferItemVO.setGraceDaysStr((String) p_map.get("GRACE_DAYS"));

        _receiverTransferItemVO.setServiceClassCode((String) p_map.get("SERVICE_CLASS"));
        _receiverTransferItemVO.setOldExporyInMillis((String) p_map.get("CAL_OLD_EXPIRY_DATE"));// @nu

        try {
            _receiverTransferItemVO.setPreviousBalance(Long.parseLong((String) p_map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
        }
        ;

        _receiverTransferItemVO.setBundleTypes((String) p_map.get("BUNDLE_TYPES"));
        _receiverTransferItemVO.setBonusBundleValidities((String) p_map.get("BONUS_BUNDLE_VALIDITIES"));

        // Update the Previous Balance in case of Post Paid Offline interface
        // with Credit Limit - Monthly Transfer Amount
        if (_receiverVO.isPostOfflineInterface()) {
            boolean isPeriodChange = BTSLUtil.isPeriodChangeBetweenDates(_receiverVO.getLastSuccessOn(), _currentDate, BTSLUtil.PERIOD_MONTH);
            if (!isPeriodChange) {
                _receiverTransferItemVO.setPreviousBalance(_receiverTransferItemVO.getPreviousBalance() - _receiverVO.getMonthlyTransferAmount());
            }
        }
        // TO DO Done for testing purpose should we use it or give exception in
        // this case
        if (_receiverTransferItemVO.getPreviousExpiry() == null) {
            _receiverTransferItemVO.setPreviousExpiry(_currentDate);
        }

        try {
            _receiverTransferItemVO.setLmbdebitvalue((Long.valueOf((String) p_map.get("LMB_ALLOWED_VALUE"))));
        } catch (Exception e) {
            _log.error("populateReceiverItemsDetails", "Exception e" + e);
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
        if (_log.isDebugEnabled()) {
            _log.debug("setInterfaceDetails", _requestIDStr, " Entered p_prefixID=" + p_prefixID + " p_listValueVO=" + p_listValueVO + " p_useInterfacePrefixVO=" + p_useInterfacePrefixVO + " p_MSISDNPrefixInterfaceMappingVO=" + p_MSISDNPrefixInterfaceMappingVO + "p_serviceSelectorInterfaceMappingVO" + p_serviceSelectorInterfaceMappingVO);
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
            if (SystemPreferences.SELECTOR_INTERFACE_MAPPING && p_serviceSelectorInterfaceMappingVO != null) {
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
                _senderTransferItemVO.setPrefixID(p_prefixID);
                _senderTransferItemVO.setInterfaceID(interfaceID);
                _senderTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                _senderAllServiceClassID = allServiceClassID;
                _senderExternalID = externalID;
                _senderInterfaceStatusType = interfaceStatusTy;
                _p2pTransferVO.setSenderAllServiceClassID(_senderAllServiceClassID);
                _p2pTransferVO.setSenderInterfaceStatusType(_senderInterfaceStatusType);

            } else if (p_userType.equals(PretupsI.USER_TYPE_RECEIVER)) {
                _receiverTransferItemVO.setPrefixID(p_prefixID);
                _receiverTransferItemVO.setInterfaceID(interfaceID);
                _receiverTransferItemVO.setInterfaceHandlerClass(interfaceHandlerClass);
                if (PretupsI.YES.equals(underProcessMsgReqd)) {
                    _p2pTransferVO.setUnderProcessMsgReq(true);
                }
                _receiverAllServiceClassID = allServiceClassID;
                _receiverExternalID = externalID;
                _receiverInterfaceStatusType = interfaceStatusTy;
                _p2pTransferVO.setReceiverAllServiceClassID(_receiverAllServiceClassID);
                _p2pTransferVO.setReceiverInterfaceStatusType(_receiverInterfaceStatusType);
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
                throw new BTSLBaseException(this, "setInterfaceDetails", SelfTopUpErrorCodesI.INTERFACE_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
            _log.error("setInterfaceDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[setInterfaceDetails]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "setInterfaceDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("setInterfaceDetails", _requestIDStr, " Exiting with Sender Interface ID=" + _senderTransferItemVO.getInterfaceID() + " Receiver Interface=" + _receiverTransferItemVO.getInterfaceID());
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
        if (_log.isDebugEnabled()) {
            _log.debug("updateSubscriberRoutingDetails", _requestIDStr, " Entered p_userType=" + p_userType + " p_networkCode=" + p_networkCode + " p_interfaceID=" + p_interfaceID + " p_externalID=" + p_externalID + " p_msisdn=" + p_msisdn + " p_interfaceCategory=" + p_interfaceCategory + " p_userID=" + p_userID + " p_currentDate=" + p_currentDate);
        }
        try {
            boolean updationReqd = false;
            if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
                updationReqd = _senderDeletionReqFromSubRouting;
            } else {
                updationReqd = _receiverDeletionReqFromSubRouting;
            }

            if (updationReqd) {
                PretupsBL.updateSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
            } else {
                SubscriberRoutingControlVO subscriberRoutingControlVO = SubscriberRoutingControlCache.getRoutingControlDetails(p_networkCode + "_" + _p2pTransferVO.getServiceType() + "_" + p_interfaceCategory);
                if (!updationReqd && subscriberRoutingControlVO != null && subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
                    if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
                        _senderInterfaceInfoInDBFound = true;
                        _senderDeletionReqFromSubRouting = true;
                    } else {
                        _receiverInterfaceInfoInDBFound = true;
                        _receiverDeletionReqFromSubRouting = true;
                    }
                }
            }

        } catch (BTSLBaseException be) {
            _log.error("updateSubscriberRoutingDetails", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[updateSubscriberRoutingDetails]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberRoutingDetails", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateSubscriberRoutingDetails", _requestIDStr, " Exiting ");
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
     * //if(_log.isDebugEnabled()) _log.debug("generateTransferID","Entered ");
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
     * _transactionIDCounter=1;
     * else
     * _transactionIDCounter=_transactionIDCounter+1;
     * _prevReqTime=currentReqTime;
     * 
     * if(_transactionIDCounter==0)
     * throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController",
     * "generateTransferID",PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * transferID=_operatorUtil.formatP2PTransferID(p_transferVO,
     * _transactionIDCounter);
     * if(transferID==null)
     * throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController",
     * "generateC2STransferID",PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
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
     * throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController",
     * "generateTransferID",PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * }
     * }
     */
    public static synchronized void generateTransferID(TransferVO p_transferVO) throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("generateTransferID","Entered ");
        String transferID = null;
        Date mydate = null;
        String minut2Compare = null;

        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // newTransferID=IDGenerator.getNextID(PretupsI.ID_GEN_P2P_TRANSFER_NO,BTSLUtil.getFinancialYearLastDigits(4),receiverVO.getNetworkCode(),p_transferVO.getCreatedOn());
            mydate = new Date();
            p_transferVO.setCreatedOn(mydate);
            minut2Compare = _sdfCompare.format(mydate);
            int currentMinut = Integer.parseInt(minut2Compare);

            if (currentMinut != _prevMinut) {
                _transactionIDCounter = 1;
                _prevMinut = currentMinut;

            } else {
                _transactionIDCounter++;

            }

            if (_transactionIDCounter == 0) {
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "generateTransferID", SelfTopUpErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = _operatorUtil.formatP2PTransferID(p_transferVO, _transactionIDCounter);
            if (transferID == null) {
                throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "generateTransferID", SelfTopUpErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException("P2PAdhocRechargeUsingCreditCardController", "generateTransferID", SelfTopUpErrorCodesI.NOT_GENERATE_TRASNFERID);
        }
    }

    public void handleLDCCRequest() throws BTSLBaseException {
        Connection con = null;
        if (_log.isDebugEnabled()) {
            _log.debug("handleLDCCRequest", "Entered");
        }
        try {
            ListValueVO listValueVO = null;
            String requestStr = null;
            CommonClient commonClient = null;
            String senderValResponse = null;
            con = OracleUtil.getConnection();
            // populate payment and service interface details for validate
            // action
            populateServicePaymentInterfaceDetails(con, PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
            _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache.getServiceInterfaceRoutingDetails(_senderVO.getNetworkCode() + "_" + _serviceType + "_" + _senderVO.getSubscriberType());
            _senderVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_senderVO.getMsisdn()));
            NetworkPrefixVO netPreVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_senderVO.getMsisdnPrefix(), _serviceInterfaceRoutingVO.getAlternateInterfaceType());
            NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(_p2pTransferVO.getModule(), _senderNetworkCode, _p2pTransferVO.getPaymentMethodType());
            LoadController.decreaseResponseCounters(_transferID, SelfTopUpErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
            LoadController.decreaseTransactionInterfaceLoad(_transferID, _p2pTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
            _senderTransferItemVO.setPrefixID(netPreVO.getPrefixID());
            _senderVO.setPrefixID(netPreVO.getPrefixID());
            MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
            try {
                interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(_senderTransferItemVO.getPrefixID(), _serviceInterfaceRoutingVO.getAlternateInterfaceType(), PretupsI.INTERFACE_VALIDATE_ACTION);
                setInterfaceDetails(_senderTransferItemVO.getPrefixID(), PretupsI.USER_TYPE_SENDER, listValueVO, true, interfaceMappingVO, null);
            } catch (BTSLBaseException be) {
                throw be;
            }
            checkTransactionLoad(PretupsI.USER_TYPE_SENDER, _senderTransferItemVO.getInterfaceID());

            // validate sender limits after Interface Validations
            SubscriberBL.validateSenderLimits(con, _p2pTransferVO, PretupsI.TRANS_STAGE_BEFORE_INVAL);

            if (con != null) {
                con.close();
                con = null;
            }
            requestStr = getSenderValidateStr();
            commonClient = new CommonClient();

            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_REQ, PretupsI.TXN_LOG_TXNSTAGE_INVAL, requestStr, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            if (_log.isDebugEnabled()) {
                _log.debug("handleLDCCRequest", "Sending Request For MSISDN=" + _senderMSISDN + " on ALternate Routing 1 to =" + _senderTransferItemVO.getInterfaceID());
            }

            senderValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_INVAL, senderValResponse, PretupsI.TXN_LOG_STATUS_SUCCESS, "");

            try {
                senderValidateResponse(senderValResponse, 1, 0);
                if (InterfaceErrorCodesI.SUCCESS.equals(_senderTransferItemVO.getValidationStatus())) {
                    _senderVO.setSubscriberType(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
                    _p2pTransferVO.setTransferCategory(_serviceInterfaceRoutingVO.getAlternateInterfaceType() + "-" + _type);
                    _p2pTransferVO.setPaymentMethodType(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
                    _isUpdateRequired = true;
                    _isSenderRoutingUpdate = true;
                    _senderSubscriberType = _serviceInterfaceRoutingVO.getAlternateInterfaceType();
                }
                if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(_senderTransferItemVO.getValidationStatus())) {
                    _receiverVO.setTransactionStatus(SelfTopUpErrorCodesI.TXN_STATUS_FAIL);
                    throw new BTSLBaseException(this, "handleLDCCRequest", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                }

            } catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                throw e;
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[performSenderAlternateRouting]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "performSenderAlternateRouting", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                con = null;
            }// end if
            if (_log.isDebugEnabled()) {
                _log.debug("handleLDCCRequest", "Exit");
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
    private void updateSubscriberAilternateRouting(String p_userType, String p_networkCode, String p_interfaceID, String p_externalID, String p_msisdn, String p_interfaceCategory, String p_userID, Date p_currentDate) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateSubscriberAilternateRouting", _requestIDStr, " Entered p_userType=" + p_userType + " p_networkCode=" + p_networkCode + " p_interfaceID=" + p_interfaceID + " p_externalID=" + p_externalID + " p_msisdn=" + p_msisdn + " p_interfaceCategory=" + p_interfaceCategory + " p_userID=" + p_userID + " p_currentDate=" + p_currentDate);
        }
        try {
            boolean updationReqd = false;
            if (PretupsI.USER_TYPE_SENDER.equals(p_userType)) {
                updationReqd = _senderDeletionReqFromSubRouting;
            } else {
                updationReqd = _receiverDeletionReqFromSubRouting;
            }

            // if(updationReqd)
            try {
                PretupsBL.updateSubscriberInterfaceAilternateRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
            } catch (BTSLBaseException e) {
                if (SelfTopUpErrorCodesI.ERROR_EXCEPTION.equals(e.getMessage())) {
                    PretupsBL.insertSubscriberInterfaceRouting(p_interfaceID, p_externalID, p_msisdn, p_interfaceCategory, p_userID, p_currentDate);
                }
            }
            _senderInterfaceInfoInDBFound = true;
            _senderDeletionReqFromSubRouting = true;

        } catch (BTSLBaseException be) {
            _log.error("updateSubscriberAilternateRouting", "Getting Base Exception =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PAdhocRechargeUsingCreditCardController[updateSubscriberAilternateRouting]", _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateSubscriberAilternateRouting", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateSubscriberAilternateRouting", _requestIDStr, " Exiting ");
            }
        }
    }

}
