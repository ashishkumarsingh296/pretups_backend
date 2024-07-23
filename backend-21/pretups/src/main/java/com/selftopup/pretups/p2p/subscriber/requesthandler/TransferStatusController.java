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

package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Locale;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.MessagesCache;
import com.selftopup.util.MessagesCaches;
import com.selftopup.util.OracleUtil;

/**
 * @author avinash.kamthan
 */
public class TransferStatusController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(TransferStatusController.class.getName());

    /**
	 * 
	 */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("process", " Entered " + p_requestVO);

        SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
        // <Key Word> <PIN>
        String[] args = p_requestVO.getRequestMessageArray();
        boolean transferStatusFlag = false;
        Connection con = null;

        try {

            String actualPin = senderVO.getPin();

            int messageLength = args.length;
            if (messageLength > PretupsI.MESSAGE_LENGTH_TRANSFERSTATUS)
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_INVALID_TR_STATUS_REPORTREQUESTFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                switch (messageLength) {
                case 1: {
                    if (!(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin))) {
                        throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_INVALID_TR_STATUS_REPORTREQUESTFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                    }
                    transferStatusFlag = true;
                    break;
                }
                case 2: {
                    // if(!PretupsI.DEFAULT_P2P_PIN.equals(BTSLUtil.decryptText(actualPin)))
                    if (!(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin))) {
                        try {
                            // Getting database connection
                            con = OracleUtil.getConnection();
                            SubscriberBL.validatePIN(con, senderVO, p_requestVO.getRequestMessageArray()[1]);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                                con.commit();
                            throw be;
                        }
                    }
                    transferStatusFlag = true;
                    break;
                }
                }
            } else
                transferStatusFlag = true;

            if (senderVO.getLastTransferAmount() == 0) {
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.NO_TRANSACTION);
                return;
            } else {
                // String txnStatus=null;
                // txnStatus=BTSLUtil.getMessage(senderVO.getLocale(),"TXN_STATUS_"+senderVO.getLastTransferStatus(),new
                // String[]{});
                MessagesCache messagesCache = MessagesCaches.get(senderVO.getLocale());
                if (messagesCache == null) {
                    _log.error("process", "Messages cache not available for locale " + senderVO.getLocale().getDisplayName() + "    key: " + "TXN_STATUS_" + senderVO.getLastTransferStatus());
                    Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
                    messagesCache = MessagesCaches.get(locale);
                    if (messagesCache == null)
                        return;
                }
                String arr[] = { PretupsBL.getDisplayAmount(senderVO.getLastTransferAmount()), senderVO.getLastTransferMSISDN(), messagesCache.getProperty("TXN_STATUS_" + senderVO.getLastTransferStatus()) };
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.TRANSFER_STATUS_SUCCESS);
                return;
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e) {
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.TRANSFER_REPORT_FAILED);
            return;
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception ee) {
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransfersReportController[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.TRANSFER_REPORT_FAILED);
            return;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("process", " Exited ");
        }// end of finally
    }// end of process
}
