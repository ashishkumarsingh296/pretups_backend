/**
 * @(#)DeregisterSubscriberController.java
 *                                         Copyright(c) 2005, Bharti Telesoft
 *                                         Ltd.
 *                                         All Rights Reserved
 *                                         Controller class for deregistering
 *                                         self
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         Author Date History
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         avinash.kamthan June 30, 2005
 *                                         Initital Creation
 *                                         Gurjeet Singh Bedi 26/06/06 Modified
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 * 
 */

package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;

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
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

public class DeregisterSubscriberController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(DeregisterSubscriberController.class.getName());

    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());

        Connection con = null;
        try {
            con = OracleUtil.getConnection();
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            String actualPin = senderVO.getPin();
            String[] args = p_requestVO.getRequestMessageArray();
            int messageLength = args.length;

            switch (messageLength) {
            case (PretupsI.MESSAGE_LENGTH_DEREGISTER - 1): {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_DEREGISTER_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                }
                break;
            }
            case (PretupsI.MESSAGE_LENGTH_DEREGISTER): {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            con.commit();
                        throw be;
                    }
                }
                break;
            }
            default:
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_DEREGISTER_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }

            // check whether user's any transaction is ambiguous or not
            if (SubscriberBL.checkAmbiguousTranscationStatus(con, senderVO.getMsisdn())) {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_AMBIGOUS_CASE_PENDING);
            }
            // load the buddy list
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            ArrayList buddyList = subscriberDAO.loadBuddyList(con, senderVO.getUserID());
            senderVO.setVoList(buddyList);
            // delete the subscriber
            try {
                int status = SubscriberBL.deleteSubscriber(con, senderVO);
                if (status > 0) {
                    con.commit();
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_DEREGISTERATION_SUCCESS);
                } else {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_DEREGISTERATION_FAIL);
                }
            } catch (Exception e) {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_DEREGISTERATION_FAIL);
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException while deregistering =" + p_requestVO.getFilteredMSISDN() + " getting exception=" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_DEREGISTERATION_FAIL);
        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception ee) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception while deregistering =" + p_requestVO.getFilteredMSISDN() + " getting exception=" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DeregisterSubscriberController[process]", p_requestVO.getFilteredMSISDN(), "", "", "Exception while deregistering:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (_log.isDebugEnabled())
                _log.debug("process", " Exited ");
        }
    }
}
