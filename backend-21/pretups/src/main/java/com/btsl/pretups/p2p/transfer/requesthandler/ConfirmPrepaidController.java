package com.btsl.pretups.p2p.transfer.requesthandler;

/**
 * @(#)ConfirmPrepaidController.java
 *                                   Copyright(c) 2005, Bharti Telesoft Int.
 *                                   Public Ltd.
 *                                   All Rights Reserved
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Abhijit Chauhan June 18,2005 Initial
 *                                   Creation
 *                                   Gurjeet Singh Bedi 15/09/05 Modified
 *                                   Abhijit Aug 10,2006 Modified for
 *                                   ID=SUBTYPVALRECLMT
 *                                   Ankit Zindal Nov 20,2006
 *                                   ChangeID=LOCALEMASTER
 *                                   Ashish Kumar July 03, 2007 Add for the
 *                                   transaction id generation in the memory
 *                                   Divyakant Verma Feb 12 2008
 *                                   P2PRequestDailyLog introduced to log time
 *                                   taken by IN for validation & topup.
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   ----------
 */

import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.P2PRequestDailyLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.subscriber.businesslogic.SubscriberVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

// public class ConfirmPrepaidController implements
// ServiceKeywordControllerI,Runnable{
public class ConfirmPrepaidController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(ConfirmPrepaidController.class.getName());
    private P2PTransferVO _p2pTransferVO = null;

    private String _senderMSISDN;
    private String _receiverMSISDN;
    private SenderVO _senderVO;
    private ReceiverVO _receiverVO;
    private String _senderNetworkCode;
    private Date _currentDate = null;
    private String _transferID;
    private long _requestID;
    private String _requestIDStr;
    private Locale _senderLocale = null;
    private String _type;
    private String _serviceType;
    private RequestVO _requestVO = null;
    private static OperatorUtilI _operatorUtil = null;

    public ConfirmPrepaidController() {
        _p2pTransferVO = new P2PTransferVO();
        _currentDate = new Date();

    }

    // Loads operator specific class
    static {
        final String METHOD_NAME = "static";
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ConfirmPrepaidController[initialize]", "", "", "",
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
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        _requestIDStr = p_requestVO.getRequestIDStr();

        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestIDStr, "Entered");
        }
        try {
            _requestVO = p_requestVO;
            _senderVO = (SenderVO) p_requestVO.getSenderVO();
            // If user is not already registered then register the user with
            // status as NEW and Default PIN
            /*
             * if(_senderVO==null)
             * {
             * new RegisterationController().regsiterNewUser(p_requestVO);
             * _senderVO=(SenderVO)p_requestVO.getSenderVO();
             * _senderVO.setDefUserRegistration(true);
             * p_requestVO.setSenderLocale(new
             * Locale(_senderVO.getLanguage(),_senderVO.getCountry()));
             * //If group type counters are allowed to check for controlling for
             * the request gateway then check them
             * //This change has been done by ankit on date 14/07/06 for SMS
             * charging
             * if(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED))!=null &&
             * SystemPreferences.
             * GRPT_CTRL_ALLOWED.indexOf(p_requestVO.getRequestGatewayType
             * ())!=-1 &&
             * !PretupsI.NOT_APPLICABLE.equals(p_requestVO.getGroupType()))
             * {
             * //load the user running and profile counters
             * //Check the counters
             * //update the counters
             * GroupTypeProfileVO
             * groupTypeProfileVO=PretupsBL.loadAndCheckP2PGroupTypeCounters
             * (p_requestVO,PretupsI.GRPT_TYPE_CONTROLLING);
             * //If counters reach the profile limit them throw exception
             * if(groupTypeProfileVO!=null &&
             * groupTypeProfileVO.isGroupTypeCounterReach())
             * {
             * p_requestVO.setDecreaseGroupTypeCounter(false);
             * String
             * arr[]={String.valueOf(groupTypeProfileVO.getThresholdValue())};
             * if(PretupsI.GRPT_TYPE_FREQUENCY_DAILY.equals(groupTypeProfileVO.
             * getFrequency()))
             * throw new BTSLBaseException(this,"process",PretupsErrorCodesI.
             * P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_D,arr);
             * throw new BTSLBaseException(this,"process",PretupsErrorCodesI.
             * P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_M,arr);
             * }
             * }
             * }
             */
            _senderLocale = p_requestVO.getSenderLocale();
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestIDStr, "_senderLocale=" + _senderLocale);
            }
            _requestID = p_requestVO.getRequestID();
            _type = p_requestVO.getType();
            _serviceType = p_requestVO.getServiceType();
            populateVOFromRequest(p_requestVO);
            _operatorUtil.handleConfirmTransferMessageFormat(p_requestVO, _p2pTransferVO);
            // Block added to avoid decimal amount in credit transfer
            if (!BTSLUtil.isStringIn(_serviceType, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DECIMAL_ALLOW_SERVICES))) {
                try {
                    final String displayAmt = PretupsBL.getDisplayAmount(_p2pTransferVO.getRequestedAmount());
                    Long.parseLong(displayAmt);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INVALID_AMOUNT);
                }
            }
            _receiverVO = (ReceiverVO) _p2pTransferVO.getReceiverVO();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));
            // sender number = _requestVO.getFilteredMSISDN()
            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_ALLOW_SELF_TOPUP))).booleanValue() && _requestVO.getFilteredMSISDN().equals(_receiverVO.getMsisdn())) {
                _log.error("process", _requestIDStr, "Sender and receiver MSISDN are same, Sender MSISDN=" + _senderVO.getMsisdn() + " Receiver MSISDN=" + _receiverVO
                    .getMsisdn());
                throw new BTSLBaseException("", "process", PretupsErrorCodesI.ERROR_P2P_SAME_MSISDN_TRANSFER_NOTALLWD, 0, new String[] { _receiverVO.getMsisdn() }, null);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestIDStr, "Starting with transfer Category as :" + _p2pTransferVO.getTransferCategory());
            }

            if (_senderVO != null) {
                _senderNetworkCode = _senderVO.getNetworkCode();
                _senderMSISDN = _senderVO.getMsisdn();
            } else {
                _senderNetworkCode = p_requestVO.getRequestNetworkCode();
                _senderMSISDN = p_requestVO.getRequestMSISDN();
            }
            _receiverMSISDN = ((SubscriberVO) _p2pTransferVO.getReceiverVO()).getMsisdn();
            _receiverVO.setModule(_p2pTransferVO.getModule());
            _p2pTransferVO.setReceiverMsisdn(_receiverMSISDN);
            _p2pTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
            _p2pTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
            // Validates the network service status
            PretupsBL.validateNetworkService(_p2pTransferVO);
            // =====================CP2P SMS Confirmation
            // Start=============================================
            // _p2pTransferVO.setSenderReturnMessage(getSenderConfirmMessage(p_requestVO));
            p_requestVO.setSenderReturnMessage(getSenderConfirmMessage(p_requestVO));
            // =====================CP2P SMS Confirmation
            // End=============================================
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("process", "Exception be:" + be.getMessage());
            // be.printStackTrace();
            p_requestVO.setSuccessTxn(false);
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
                _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }

            TransactionLog.log(_transferID, _requestIDStr, p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                _p2pTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            // Populate the P2PRequestDailyLog and log
            P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));
        } catch (Exception e) {
            _log.error("process", "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _p2pTransferVO.setErrorCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ConfirmPrepaidController[process]", _transferID,
                _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            TransactionLog.log(_transferID, _requestIDStr, p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                _p2pTransferVO.getSenderReturnMessage(), PretupsI.TXN_LOG_STATUS_FAIL, "Getting Code=" + p_requestVO.getMessageCode());
            // Populate the P2PRequestDailyLog and log
            P2PRequestDailyLog.log(P2PRequestDailyLog.populateP2PRequestDailyLogVO(_requestVO, _p2pTransferVO));
        } finally {

            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            TransactionLog.log(_transferID, p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", PretupsI.TXN_LOG_REQTYPE_RES,
                PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller", PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
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
        // _p2pTransferVO.setCreatedBy(_senderVO.getUserID());
        _p2pTransferVO.setModifiedOn(_currentDate);
        // _p2pTransferVO.setModifiedBy(_senderVO.getUserID());
        _p2pTransferVO.setTransferDate(_currentDate);
        _p2pTransferVO.setTransferDateTime(_currentDate);
        // _p2pTransferVO.setSenderMsisdn(_senderVO.getMsisdn());
        _p2pTransferVO.setSenderMsisdn(p_requestVO.getRequestMSISDN());
        // _p2pTransferVO.setSenderID(_senderVO.getUserID());
        // _p2pTransferVO.setNetworkCode(_senderVO.getNetworkCode());
        _p2pTransferVO.setNetworkCode(p_requestVO.getRequestNetworkCode());
        _p2pTransferVO.setLocale(_senderLocale);
        _p2pTransferVO.setLanguage(_p2pTransferVO.getLocale().getLanguage());
        _p2pTransferVO.setCountry(_p2pTransferVO.getLocale().getCountry());
        _p2pTransferVO.setMsgGatewayFlowType(p_requestVO.getMessageGatewayVO().getFlowType());
        _p2pTransferVO.setMsgGatewayResponseType(p_requestVO.getMessageGatewayVO().getResponseType());
        _p2pTransferVO.setMsgGatewayTimeOutValue(p_requestVO.getMessageGatewayVO().getTimeoutValue());
    }

    private String getSenderConfirmMessage(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "getSenderConfirmMessage";
        if (_log.isDebugEnabled()) {
            _log.debug("getSenderConfirmMessage", "Entered " + p_requestVO.getDecryptedMessage());
        }
        String finalRetMsg = null;
        String retMsg = null;
        String MESSAGE_SEP = null;
        String keyword = null;
        String otherThanKeyword = null;
        String keywordMapped = null;
        String[] messageArgArray = null;
        String key = null;

        try {
            retMsg = p_requestVO.getDecryptedMessage();
            MESSAGE_SEP = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
            if (BTSLUtil.isNullString(MESSAGE_SEP)) {
                MESSAGE_SEP = " ";
            }
            keyword = retMsg.substring(0, retMsg.indexOf(MESSAGE_SEP));
            otherThanKeyword = retMsg.substring(retMsg.indexOf(keyword) + keyword.length(), retMsg.length());
            keywordMapped = Constants.getProperty(keyword);
            finalRetMsg = keywordMapped + otherThanKeyword;
            messageArgArray = new String[] { finalRetMsg };
            key = PretupsErrorCodesI.P2P_SENDER_CONFIRM;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getSenderConfirmMessage", "Exception :" + e.getMessage() + " occured in generating Confirmation message for sender");
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ConfirmPrepaidController[getSenderConfirmMessage]",
                _transferID, _senderMSISDN, _senderNetworkCode, "Exception:" + e.getMessage());
            throw new BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getSenderConfirmMessage", "Exited. finalRetMsg: " + finalRetMsg);
            }
        }
        return BTSLUtil.getMessage(_senderLocale, key, messageArgArray);
    }

    public static void main(String[] args) {
        final String retMsg = "jkh#klsd#7899";
        final String MESSAGE_SEP = "#";
        final String keyword = retMsg.substring(0, retMsg.indexOf(MESSAGE_SEP));
        final String otherThanKeyword = retMsg.substring(retMsg.indexOf(keyword) + keyword.length(), retMsg.length());

    }
}
