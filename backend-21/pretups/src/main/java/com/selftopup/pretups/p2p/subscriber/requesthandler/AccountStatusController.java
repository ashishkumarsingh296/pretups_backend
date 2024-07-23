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
import com.selftopup.pretups.preference.businesslogic.PreferenceCacheVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceDAO;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.PostPaidControlParametersVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.OracleUtil;

/**
 * @author avinash.kamthan
 */
public class AccountStatusController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(AccountStatusController.class.getName());

    /**
	 * 
	 */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("process", " Entered " + p_requestVO);

        Connection con = null;
        try {
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            // <Key Word> <PIN>
            String[] args = p_requestVO.getRequestMessageArray();
            boolean transferStatusFlag = false;
            con = OracleUtil.getConnection();
            if (SystemPreferences.PIN_REQUIRED) {
                if (args.length == PretupsI.MESSAGE_LENGTH_ACCOUNTSTATUS) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, p_requestVO.getRequestMessageArray()[1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            con.commit();
                        throw be;
                    }
                    transferStatusFlag = true;
                } else {
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
                    return;
                }
            } else {
                // if pin is not reuired at system level
                if (args.length == (PretupsI.MESSAGE_LENGTH_ACCOUNTSTATUS - 1)) {
                    transferStatusFlag = true;
                } else {
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
                    return;
                }
            }

            // to check whether user made any transaction or not
            if (transferStatusFlag) {
                if (senderVO.getLastTransferAmount() == 0) {
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.NO_TRANSACTION);
                    return;
                }
            }

            // send the last transfer status
            if (transferStatusFlag) {

                String monthlyPreferenceAmount = null, monthlyPreferenceCount = null;
                if (PretupsI.REGISTERATION_REQUEST_PRE.equals(senderVO.getSubscriberType())) {
                    PreferenceDAO preferenceDAO = new PreferenceDAO();
                    ArrayList arrayList = preferenceDAO.loadServicePreferencesList(con, senderVO.getServiceClassCode());
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
                    if (SystemPreferences.USE_PPAID_USER_DEFINED_CONTROLS) {
                        // set the arguments for message
                        SubscriberDAO subscriberDAO = new SubscriberDAO();
                        PostPaidControlParametersVO postPaidControlParametersVO = subscriberDAO.loadPostPaidControlParameters(con, senderVO.getMsisdn());
                        monthlyPreferenceCount = postPaidControlParametersVO.getMonthlyTransferAllowed() + "";
                        monthlyPreferenceAmount = PretupsBL.getDisplayAmount(postPaidControlParametersVO.getMonthlyTransferAmountAllowed());
                    } else {
                        PreferenceDAO preferenceDAO = new PreferenceDAO();
                        ArrayList arrayList = preferenceDAO.loadServicePreferencesList(con, senderVO.getServiceClassCode());
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
                String arr[] = { senderVO.getMonthlyTransferCountStr(), PretupsBL.getDisplayAmount(senderVO.getMonthlyTransferAmount()), monthlyPreferenceCount, monthlyPreferenceAmount };
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.ACCOUNT_STATUS_SUCCESS);
                return;
            }
        } catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException " + be.getMessage());
            be.printStackTrace();
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"AccountStatusController[process]","","","","BTSL Exception:"+be.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.ACCOUNT_STATUS_FAILED);
            return;
        } catch (Exception e) {
            _log.error("process", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccountStatusController[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.ACCOUNT_STATUS_FAILED);
            return;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("process", " Exited ");
        }
    }
}
