/**
 * @(#)DeleteBuddyController.java
 *                                Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 *                                Controller class for deleting a buddy from
 *                                subscriber list
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                avinash.kamthan Mar 23, 2005 Initital Creation
 *                                Gurjeet Singh Bedi 26/06/06 Modified
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 * 
 */

package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;

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
import com.selftopup.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

public class DeleteBuddyController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(DeleteBuddyController.class.getName());
    private static OperatorUtilI _operatorUtil = null;
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " DeleteBuddyController [initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
	 * 
	 */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());

        Connection con = null;
        try {

            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            // <Key Word> < Name / MSISDN> <PIN>
            // <Key Word> < Name / MSISDN>
            con = OracleUtil.getConnection();
            String actualPin = senderVO.getPin();
            String[] args = p_requestVO.getRequestMessageArray();
            int messageLength = args.length;
            String buddyNameMobileNo = null;
            switch (messageLength) {
            case (PretupsI.MESSAGE_LENGTH_DELETE_BUDDY - 1): {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_DELBUDDY_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                }
                buddyNameMobileNo = args[1];
                break;
            }
            case (PretupsI.MESSAGE_LENGTH_DELETE_BUDDY): {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[2]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            con.commit();
                        throw be;
                    }
                }
                buddyNameMobileNo = args[1];
                break;
            }
            default:
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_DELBUDDY_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }

            // to check the name and number which sends by user is proper or not
            if (BTSLUtil.isNullString(buddyNameMobileNo))
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.DELETE_BUDDYNAME_INVALID);

            buddyNameMobileNo = buddyNameMobileNo.trim();
            buddyNameMobileNo = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(buddyNameMobileNo));
            SubscriberDAO subscriberDAO = new SubscriberDAO();

            // load the buddy details
            BuddyVO buddyVO = subscriberDAO.loadBuddyDetails(con, senderVO.getUserID(), buddyNameMobileNo);
            if (buddyVO == null) {
                String arr[] = { buddyNameMobileNo };
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.BUDDY_DOESNOT_EXIST, 0, arr, null);
            }

            // delete the buddy
            int deleteCount = subscriberDAO.deleteBuddy(con, buddyVO);
            if (deleteCount > 0) {
                con.commit();
                String arr[] = { buddyNameMobileNo };
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.DELETE_BUDDY_SUCCESS);
            } else {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.DELETE_BUDDY_FAILED);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
            }
            _log.error("process", "BTSLBaseException while deleting Buddy" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.DELETE_BUDDY_FAILED);
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            _log.error("process", "Exception while deleting Buddy" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DeleteBuddyController[process]", "", ((SenderVO) p_requestVO.getSenderVO()).getMsisdn(), "", "Exception while deleting Buddy:" + e.getMessage());
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
