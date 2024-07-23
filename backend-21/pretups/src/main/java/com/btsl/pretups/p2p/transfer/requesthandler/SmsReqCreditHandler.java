package com.btsl.pretups.p2p.transfer.requesthandler;

/**
 * @(#)SmsReqCreditHandler.java
 *                              Copyright(c) 2009, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Kapil Mehta 30/05/09 Initial Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Controller for Request for credit transfer from
 *                              subscriber to subscriber, This will send a
 *                              message to subscriber.
 */

import java.sql.Connection;
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
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBuddiesDAO;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class SmsReqCreditHandler implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(SmsReqRechargeHandler.class.getName());
    private P2PTransferVO _p2pTransferVO = null;
    private String _senderMSISDN;
    private String _receiverMSISDN;
    private String _receiverMSISDN_NAME;
    private ReceiverVO _receiverVO;
    private Date _currentDate = null;
    private String _transferID = "";
    private Locale _senderLocale = null;
    private Locale _receiverLocale = null;
    private String _notAllowedSendMessGatw;
    private static OperatorUtilI _operatorUtil = null;
    private boolean _restrictedTypeSender = false;
    private boolean _restrictedTypeReciever = false;
    private SenderVO _senderVO;
    // Loads operator specific class
    static {
        final String METHOD_NAME = "static";
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SmsReqCreditHandler[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public SmsReqCreditHandler() {
        _p2pTransferVO = new P2PTransferVO();
        _currentDate = new Date();
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;

        if (_log.isDebugEnabled()) {
            _log.debug(
                "process",
                p_requestVO.getRequestIDStr(),
                "Entered for Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN() + "_notAllowedSendMessGatw: " + _notAllowedSendMessGatw + " ");
        }
        try {
            // Getting oracle connection
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

            // Validating user message incomming in the request
            _operatorUtil.validateCreditRequestSms(con, _p2pTransferVO, p_requestVO);

            _receiverLocale = p_requestVO.getReceiverLocale();
            _senderLocale = p_requestVO.getSenderLocale();
            _receiverVO = (ReceiverVO) _p2pTransferVO.getReceiverVO();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));

            _receiverVO.setModule(p_requestVO.getModule());
            _receiverVO.setCreatedDate(_currentDate);
            _receiverVO.setLastTransferOn(_currentDate);
            _senderMSISDN = p_requestVO.getRequestMSISDN();
            _receiverMSISDN_NAME = _p2pTransferVO.getBuddy();
            final BuddyVO buddyVO = new P2PBuddiesDAO().loadReciverMSISDN(con, ((SenderVO) p_requestVO.getSenderVO()).getUserID(), _receiverMSISDN_NAME);
            if (buddyVO == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_CHLAN_INVALIDMESSAGEFORMAT, 0,
                    new String[] { p_requestVO.getActualMessageFormat() }, null);
            }
            _receiverMSISDN = buddyVO.getMsisdn();
            _p2pTransferVO.setReceiverMsisdn(_receiverMSISDN);
            _p2pTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
            _p2pTransferVO.setSubService(p_requestVO.getReqSelector());
            _p2pTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
            _senderVO = (SenderVO) p_requestVO.getSenderVO();
            _p2pTransferVO.setSenderVO(_senderVO);
            _p2pTransferVO.setSenderID(_senderVO.getUserID());

            // restricted MSISDN check for Sender
            _operatorUtil.isRestrictedSubscriberAllowed(con, _receiverMSISDN, _senderMSISDN);

            // checking whether self topup is allowed or not
            if (_senderMSISDN.equals(_receiverMSISDN) && !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_ALLOW_SELF_TOPUP))).booleanValue()) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_SELF_TOPUP_NTALLOWD);
            }

            // check if Sender barred in PreTUPS or not,
            try {
                PretupsBL.checkMSISDNBarred(con, _senderMSISDN, _receiverVO.getNetworkCode(), p_requestVO.getModule(), PretupsI.USER_TYPE_SENDER);
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                    _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
                }
                throw be;
            }
            // check if Reciever barred in PreTUPS or not,
            try {
                PretupsBL.checkMSISDNBarred(con, _receiverMSISDN, _receiverVO.getNetworkCode(), p_requestVO.getModule(), PretupsI.USER_TYPE_RECEIVER);
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                    _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
                }
                throw be;
            }

            // Forwarding request to process the transfer request
            formatSMSforReciever(p_requestVO);

            // setting transaction status to Success
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.CREDITREQVIASMS_RECEIVER_SUCCESS),
                new String[] { p_requestVO.getReqAmount(), _senderMSISDN }));

            // setting receiver return message
            if (_p2pTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                _p2pTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { p_requestVO.getReqAmount() }));

            }

            // commiting transaction
            try {
            	mcomCon.finalCommit();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("SmsReqCreditHandler", "process", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
			if (mcomCon != null) {
				mcomCon.close("SmsReqCreditHandler#process");
				mcomCon = null;
			}
            con = null;
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setSuccessTxn(false);

            // setting transaction status to Fail
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

            if (be.isKey()) // checking if baseexception has key
            {
                if (_p2pTransferVO.getErrorCode() == null) {
                    _p2pTransferVO.setErrorCode(be.getMessageKey());
                }

                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                // setting default error code if message and key is not found
                _p2pTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + e.getMessage());

            // setting transaction status to Fail
            _p2pTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {

            // checking if receiver message is not null and receiver return
            // message is key
            if (_p2pTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _p2pTransferVO.getReceiverReturnMsg()).isKey()) {
                // generating message and pushing it to receiver
                final BTSLMessages btslRecMessages = (BTSLMessages) _p2pTransferVO.getReceiverReturnMsg();
                (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                    _p2pTransferVO.getRequestGatewayCode(), _receiverLocale)).push();

            } else if (_p2pTransferVO.getReceiverReturnMsg() != null) {
                // message
                // to
                // receiver
                (new PushMessage(_receiverMSISDN, (String) _p2pTransferVO.getReceiverReturnMsg(), _transferID, _p2pTransferVO.getRequestGatewayCode(), _receiverLocale))
                    .push();
            }

			if (mcomCon != null) {
				mcomCon.close("SmsReqCreditHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
            }
        }
    }

    /**
     * This method is used for generating SMS Message for Sender
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */

    private void formatSMSforReciever(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatSMSforReciever";
        if (_log.isDebugEnabled()) {
            _log.debug("formatSMSforReciever", " p_requestVO " + p_requestVO.toString());
        }

        try {

            final String[] arr = new String[2];
            arr[0] = p_requestVO.getReqAmount();
            arr[1] = _receiverMSISDN;
            p_requestVO.setMessageArguments(arr);
            p_requestVO.setMessageCode(PretupsErrorCodesI.CREDITREQVIASMS_SENDER_SUCCESS);
        } catch (Exception e) {
            _log.error("formatSMSforReciever", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SmsReqCreditHandler[formatSMSforReciever]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SmsReqCreditHandler", "formatSMSforReciever", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatSMSforReciever", "Exited");
            }
        }
    }

}
