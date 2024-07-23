package com.btsl.pretups.p2p.transfer.requesthandler;

/**
 * @(#)SmsReqRechargeHandler.java
 *                                Copyright(c) 2009, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Kapil Mehta 30/05/09 Initial Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Controller for Request for Recharge from
 *                                Retailer, This will send a message to
 *                                Retailer.
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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberBL;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class SmsReqRechargeHandler implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(SmsReqRechargeHandler.class.getName());
    private C2STransferVO _c2sTransferVO = null;
    private String _senderMSISDN;
    private String _receiverMSISDN;
    private ReceiverVO _receiverVO;
    private Date _currentDate = null;
    private String _transferID = "";
    private Locale _senderLocale = null;
    private Locale _receiverLocale = null;
    private String _notAllowedSendMessGatw;
    private static OperatorUtilI _operatorUtil = null;
    private TransferItemVO _receiverTransferItemVO = null;
    private static ChannelUserDAO _channelUserDAO = new ChannelUserDAO();

    // Loads operator specific class
    static {
        final String METHOD_NAME = "static";
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public SmsReqRechargeHandler() {
        _c2sTransferVO = new C2STransferVO();
        _currentDate = new Date();
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
        ChannelUserVO channelUserVO = null;
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
            _operatorUtil.validateC2SRechargeRequestSms(con, _c2sTransferVO, p_requestVO);

            _receiverLocale = p_requestVO.getReceiverLocale();
            _senderLocale = p_requestVO.getSenderLocale();
            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));

            _receiverVO.setModule(p_requestVO.getModule());
            _receiverVO.setCreatedDate(_currentDate);
            _receiverVO.setLastTransferOn(_currentDate);
            _senderMSISDN = p_requestVO.getRequestMSISDN();
            _receiverMSISDN = ((ReceiverVO) _c2sTransferVO.getReceiverVO()).getMsisdn();
            _c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
            _c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
            _c2sTransferVO.setSubService(p_requestVO.getReqSelector());
            _c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());

            // check if Sender barred in PreTUPS or not
            try {
                PretupsBL.checkMSISDNBarred(con, _senderMSISDN, _receiverVO.getNetworkCode(), p_requestVO.getModule(), PretupsI.USER_TYPE_SENDER);
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                if (be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED))) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R, new String[] {}));
                }
                throw be;
            }

            // check if Reciever barred in PreTUPS or not
            try {
                PretupsBL.checkMSISDNBarred(con, _receiverMSISDN, _receiverVO.getNetworkCode(), "C2S", PretupsI.USER_TYPE_SENDER);
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CREDITREQVIASMS_CHANNELUSER_BARRED);
            }
            // check Channel User Exists or not check
            RestrictedSubscriberBL.isChannelUserExistForC2SViaSms(con, _receiverMSISDN, _senderMSISDN);

            // Populate channelUserVO
            channelUserVO = _channelUserDAO.loadChannelUserDetails(con, _receiverMSISDN);

            // restricted MSISDN check
            RestrictedSubscriberBL.isRestrictedMsisdnExistForC2S(con, _c2sTransferVO, channelUserVO, _receiverVO.getMsisdn(), _c2sTransferVO.getRequestedAmount());

            // checking whether self topup is allowed or not
            if (_senderMSISDN.equals(_receiverMSISDN) && !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_ALLOW_SELF_TOPUP))).booleanValue()) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_SELF_TOPUP_NTALLOWD);
            }

            // Forwarding request to process the transfer request
            formatSMSforReciever(p_requestVO);

            // setting transaction status to Success
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.REQVIASMS_RECEIVER_SUCCESS), new String[] { p_requestVO.getReqAmount(), _senderMSISDN }));

            // setting receiver return message
            if (_c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.FAIL_R, new String[] { p_requestVO.getReqAmount() }));

            }

            // commiting transaction
            try {
            	mcomCon.finalCommit();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("SmsReqRechargeHandler", "process", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
			if (mcomCon != null) {
				mcomCon.close("SmsReqRechargeHandler#process");
				mcomCon = null;
			}
            con = null;
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setSuccessTxn(false);

            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

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
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + e.getMessage());

            // setting transaction status to Fail
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {

            // checking if receiver message is not null and receiver return
            // message is key
            if (_c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) _c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                // generating message and pushing it to receiver
                if (_receiverTransferItemVO != null && InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED.equals(_receiverTransferItemVO.getValidationStatus())) {
                    _c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED + "_R"));
                }
                final BTSLMessages btslRecMessages = (BTSLMessages) _c2sTransferVO.getReceiverReturnMsg();
                (new PushMessage(_receiverMSISDN, BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), _transferID,
                    _c2sTransferVO.getRequestGatewayCode(), _receiverLocale)).push();

            } else if (_c2sTransferVO.getReceiverReturnMsg() != null) {
                // message
                // to
                // receiver
                (new PushMessage(_receiverMSISDN, (String) _c2sTransferVO.getReceiverReturnMsg(), _transferID, _c2sTransferVO.getRequestGatewayCode(), _receiverLocale))
                    .push();
            }

			if (mcomCon != null) {
				mcomCon.close("SmsReqRechargeHandler#process");
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
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQVIASMS_SENDER_SUCCESS);
        } catch (Exception e) {
            _log.error("formatSMSforReciever", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SmsReqRechargeHandler[formatSMSforReciever]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SmsReqRechargeHandler", "formatSMSforReciever", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatSMSforReciever", "Exited");
            }
        }
    }

}
