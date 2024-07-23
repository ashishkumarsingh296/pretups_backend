/**
 * @(#)SuspendServiceController.java
 *                                   Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 *                                   Controller class for suspending self P2P
 *                                   Services
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   avinash.kamthan June 22, 2005 Initital
 *                                   Creation
 *                                   Gurjeet Singh Bedi 26/06/06 Modified
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 * 
 */

package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Date;
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

public class SuspendServiceController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(SuspendServiceController.class.getName());

    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());

        Connection con = null;
        try {

            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            String actualPin = senderVO.getPin();
            String[] args = p_requestVO.getRequestMessageArray();
            int messageLength = args.length;

            // call the dao method to change the user status
            con = OracleUtil.getConnection();

            switch (messageLength) {
            case (PretupsI.MESSAGE_LENGTH_SUSPEND - 1): {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_SUSPEND_SERVCE_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                }
                break;
            }
            case (PretupsI.MESSAGE_LENGTH_SUSPEND): {
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
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_SUSPEND_SERVCE_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }

            Date currentDate = new Date();
            senderVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
            senderVO.setModifiedOn(currentDate);
            senderVO.setModifiedBy(PretupsI.SYSTEM_USER);

            SubscriberDAO subscriberDAO = new SubscriberDAO();
            int count = subscriberDAO.updateSubscriberStatus(con, senderVO);
            if (count > 0) {
                con.commit();
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.USER_STATUS_SUSPEND_SUCCESS);
            } else {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.USER_STATUS_SUSPEND_FAILED);
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException while suspending services for =" + p_requestVO.getFilteredMSISDN() + " getting exception=" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.USER_STATUS_SUSPEND_FAILED);
        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception ee) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception while suspending services for =" + p_requestVO.getFilteredMSISDN() + " getting exception=" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SuspendServiceController[process]", p_requestVO.getFilteredMSISDN(), "", "", "Exception while suspending services:" + e.getMessage());
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
