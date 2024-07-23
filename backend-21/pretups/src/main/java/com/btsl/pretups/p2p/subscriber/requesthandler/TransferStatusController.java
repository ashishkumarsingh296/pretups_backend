/**
 * @(#)TransferStatusController.java
 *                                   Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 * 
 *                                   <description>
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   avinash.kamthan june 29, 2005 Initital
 *                                   Creation
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 * 
 */

package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.MessagesCache;
import com.btsl.util.MessagesCaches;

/**
 * @author avinash.kamthan
 */
public class TransferStatusController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(TransferStatusController.class.getName());

    /**
     * 
     */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }

        final String methodName = "process";
        final SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
        // <Key Word> <PIN>
        final String[] args = p_requestVO.getRequestMessageArray();
        boolean transferStatusFlag = false;
		Connection con = null;
		MComConnectionI mcomCon = null;

        try {

            final String actualPin = senderVO.getPin();

            final int messageLength = args.length;
            if (messageLength > PretupsI.MESSAGE_LENGTH_TRANSFERSTATUS) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_INVALID_TR_STATUS_REPORTREQUESTFORMAT, 0, new String[] { p_requestVO
                    .getActualMessageFormat() }, null);
            }
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                switch (messageLength) {
                case 1: {
                    if (!BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin)) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_INVALID_TR_STATUS_REPORTREQUESTFORMAT, 0, new String[] { p_requestVO
                            .getActualMessageFormat() }, null);
                    }
                    transferStatusFlag = true;
                    break;
                }
                case 2: {
                    if (!BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin)) {
                        try {
                            // Getting database connection
							mcomCon = new MComConnection();
							con = mcomCon.getConnection();
                            SubscriberBL.validatePIN(con, senderVO, p_requestVO.getRequestMessageArray()[1]);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            	mcomCon.finalCommit();
                            }
                            throw be;
                        }
                    }
                    transferStatusFlag = true;
                    break;
                }
                }
            } else {
                transferStatusFlag = true;
            }

            if (senderVO.getLastTransferAmount() == 0) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.NO_TRANSACTION);
                return;
            } else {

                MessagesCache messagesCache = MessagesCaches.get(senderVO.getLocale());
                if (messagesCache == null) {
                    _log.error("process", "Messages cache not available for locale " + senderVO.getLocale().getDisplayName() + "    key: " + "TXN_STATUS_" + senderVO
                        .getLastTransferStatus());
                    final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    messagesCache = MessagesCaches.get(locale);
                    if (messagesCache == null) {
                        return;
                    }
                }
                final String arr[] = { PretupsBL.getDisplayAmount(senderVO.getLastTransferAmount()), senderVO.getLastTransferMSISDN(), messagesCache
                    .getProperty("TXN_STATUS_" + senderVO.getLastTransferStatus()) };
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(PretupsErrorCodesI.TRANSFER_STATUS_SUCCESS);
                return;
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(methodName, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.TRANSFER_REPORT_FAILED);
            }
            return;
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransfersReportController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.TRANSFER_REPORT_FAILED);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("TransferStatusController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }// end of finally
    }// end of process
}
