/**
 * @(#)BuddyListController.java
 *                              Copyright(c) 2005, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 *                              Controller class for Buddy list display
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              avinash.kamthan June 23, 2005 Initital Creation
 *                              Gurjeet Singh Bedi 26/06/06 Modified
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
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

public class BuddyListController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(BuddyListController.class.getName());

    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());

        Connection con = null;
        try {
            // <Key Word> <PIN>
            String[] args = p_requestVO.getRequestMessageArray();
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            String actualPin = senderVO.getPin();
            int messageLength = args.length;
            con = OracleUtil.getConnection();
            switch (messageLength) {
            case (PretupsI.MESSAGE_LENGTH_BUDDYLIST - 1): {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_BUDDYLIST_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                }
                break;
            }
            case (PretupsI.MESSAGE_LENGTH_BUDDYLIST): {
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
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_BUDDYLIST_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }

            // set the buddy list as message part
            // if buddy list not found then send error message buddy list not
            // found
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            ArrayList list = subscriberDAO.loadBuddyList(con, senderVO.getUserID());
            if (_log.isDebugEnabled())
                _log.debug("process", " Buddy LIST for User MSISDN=" + senderVO.getMsisdn() + " getting size=" + list.size());
            if (list.size() > 0) {
                // call teh BL to fomr the array according to message format
                String argsArr[] = { SubscriberBL.loadBuddyListForSMS(list) };
                p_requestVO.setMessageArguments(argsArr);
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.BUDDY_LIST_SUCCESS);
            } else {
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.BUDDY_LIST_NOTFOUND);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            _log.error("process", "BTSLBaseException while getting buddy List" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.BUDDY_LIST_ERROR);
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            _log.error("process", "Exception while getting buddy List" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyListController[process]", ((SenderVO) p_requestVO.getSenderVO()).getMsisdn(), "", "", "Exception while getting buddy List" + e.getMessage());
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
