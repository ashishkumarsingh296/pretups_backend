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

package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;

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
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBuddiesDAO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class DeleteBuddyController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(DeleteBuddyController.class.getName());
    public static OperatorUtilI _operatorUtil = null;
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " DeleteBuddyController [initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * 
     */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }

        final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
        try {

            final SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            // <Key Word> < Name / MSISDN> <PIN>
            // <Key Word> < Name / MSISDN>
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            final String actualPin = senderVO.getPin();
            final String[] args = p_requestVO.getRequestMessageArray();
            final int messageLength = args.length;
            String buddyNameMobileNo = null;
            switch (messageLength) {
            case (PretupsI.MESSAGE_LENGTH_DELETE_BUDDY - 1): {
                if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin))) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_DELBUDDY_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
                }
                buddyNameMobileNo = args[1];
                break;
            }
            case (PretupsI.MESSAGE_LENGTH_DELETE_BUDDY): {
                if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[2]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                        	mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }
                buddyNameMobileNo = args[1];
                break;
            }
            default:
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_DELBUDDY_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                    .getActualMessageFormat() }, null);
            }

            // to check the name and number which sends by user is proper or not
            if (BTSLUtil.isNullString(buddyNameMobileNo)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DELETE_BUDDYNAME_INVALID);
            }

            buddyNameMobileNo = buddyNameMobileNo.trim();
            buddyNameMobileNo = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(buddyNameMobileNo));
            final P2PBuddiesDAO p2PBuddiesDAO = new P2PBuddiesDAO();

            // load the buddy details
            final BuddyVO buddyVO = p2PBuddiesDAO.loadBuddyDetails(con, senderVO.getUserID(), buddyNameMobileNo);
            if (buddyVO == null) {
                final String arr[] = { buddyNameMobileNo };
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BUDDY_DOESNOT_EXIST, 0, arr, null);
            }

            // delete the buddy
            final int deleteCount = p2PBuddiesDAO.deleteBuddy(con, buddyVO);
            if (deleteCount > 0) {
            	mcomCon.finalCommit();
                final String arr[] = { buddyNameMobileNo };
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(PretupsErrorCodesI.DELETE_BUDDY_SUCCESS);
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DELETE_BUDDY_FAILED);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("process", "BTSLBaseException while deleting Buddy" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.DELETE_BUDDY_FAILED);
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception while deleting Buddy" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DeleteBuddyController[process]", "",
                ((SenderVO) p_requestVO.getSenderVO()).getMsisdn(), "", "Exception while deleting Buddy:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("DeleteBuddyController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }
}
