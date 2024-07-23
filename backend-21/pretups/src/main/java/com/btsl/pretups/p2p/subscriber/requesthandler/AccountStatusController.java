/**
 * @(#)AccountStatusController.java
 *                                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                  All Rights Reserved
 * 
 *                                  <description>
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  avinash.kamthan June 29, 2005 Initital
 *                                  Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 * 
 */

package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;

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
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.PostPaidControlParametersVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.txn.pretups.preference.businesslogic.PreferenceTxnDAO;

/**
 * @author avinash.kamthan
 */
public class AccountStatusController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(AccountStatusController.class.getName());

    /**
     * 
     */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
        try {
            final SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            // <Key Word> <PIN>
            final String[] args = p_requestVO.getRequestMessageArray();
            boolean transferStatusFlag = false;
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_REQUIRED_CODE))).booleanValue()) {
                if (args.length == PretupsI.MESSAGE_LENGTH_ACCOUNTSTATUS) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, p_requestVO.getRequestMessageArray()[1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                        	mcomCon.finalCommit();
                        }
                        throw be;
                    }
                    transferStatusFlag = true;
                } else {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
                    return;
                }
            } else {
                // if pin is not reuired at system level
                if (args.length == (PretupsI.MESSAGE_LENGTH_ACCOUNTSTATUS - 1)) {
                    transferStatusFlag = true;
                } else {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
                    return;
                }
            }

            // to check whether user made any transaction or not
            if (transferStatusFlag) {
                if (senderVO.getLastTransferAmount() == 0) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.NO_TRANSACTION);
                    return;
                }
            }

            // send the last transfer status
            if (transferStatusFlag) {

                String monthlyPreferenceAmount = null, monthlyPreferenceCount = null;
                if (PretupsI.REGISTERATION_REQUEST_PRE.equals(senderVO.getSubscriberType())) {
                    final PreferenceTxnDAO preferencetxnDAO = new PreferenceTxnDAO();
                    final ArrayList arrayList = preferencetxnDAO.loadServicePreferencesList(con, senderVO.getServiceClassCode());
                    PreferenceCacheVO preferenceCacheVO = null;
                    for (int i = 0, k = arrayList.size(); i < k; i++) {
                        preferenceCacheVO = (PreferenceCacheVO) arrayList.get(i);
                        if (preferenceCacheVO.getPreferenceCode().equals(PreferenceI.MONTHLY_MAX_TRFR_NUM_CODE)) {
                            monthlyPreferenceCount = preferenceCacheVO.getValue();
                        }
                        if (preferenceCacheVO.getPreferenceCode().equals(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE)) {
                            monthlyPreferenceAmount = preferenceCacheVO.getValue();
                        }
                    }
                } else if (PretupsI.REGISTERATION_REQUEST_POST.equals(senderVO.getSubscriberType())) {
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_PPAID_USER_DEFINED_CONTROLS))).booleanValue()) {
                        // set the arguments for message
                        final SubscriberDAO subscriberDAO = new SubscriberDAO();
                        final PostPaidControlParametersVO postPaidControlParametersVO = subscriberDAO.loadPostPaidControlParameters(con, senderVO.getMsisdn());
                        monthlyPreferenceCount = postPaidControlParametersVO.getMonthlyTransferAllowed() + "";
                        monthlyPreferenceAmount = PretupsBL.getDisplayAmount(postPaidControlParametersVO.getMonthlyTransferAmountAllowed());
                    } else {
                        final PreferenceTxnDAO preferencetxnDAO = new PreferenceTxnDAO();
                        final ArrayList arrayList = preferencetxnDAO.loadServicePreferencesList(con, senderVO.getServiceClassCode());
                        PreferenceCacheVO preferenceCacheVO = null;

                        for (int i = 0, k = arrayList.size(); i < k; i++) {
                            preferenceCacheVO = (PreferenceCacheVO) arrayList.get(i);
                            if (preferenceCacheVO.getPreferenceCode().equals(PreferenceI.MONTHLY_MAX_TRFR_NUM_CODE)) {
                                monthlyPreferenceCount = preferenceCacheVO.getValue();
                            }
                            if (preferenceCacheVO.getPreferenceCode().equals(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE)) {
                                monthlyPreferenceAmount = preferenceCacheVO.getValue();
                            }
                        }
                    }
                }
                final String arr[] = { senderVO.getMonthlyTransferCountStr(), PretupsBL.getDisplayAmount(senderVO.getMonthlyTransferAmount()), monthlyPreferenceCount, monthlyPreferenceAmount };
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(PretupsErrorCodesI.ACCOUNT_STATUS_SUCCESS);
                return;
            }
        } catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setMessageCode(PretupsErrorCodesI.ACCOUNT_STATUS_FAILED);
            return;
        } catch (Exception e) {
            _log.error("process", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccountStatusController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.ACCOUNT_STATUS_FAILED);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("AccountStatusController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }
}
