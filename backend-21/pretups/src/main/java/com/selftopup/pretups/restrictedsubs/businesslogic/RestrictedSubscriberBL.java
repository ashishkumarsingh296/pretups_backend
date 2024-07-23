package com.selftopup.pretups.restrictedsubs.businesslogic;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.subscriber.businesslogic.ReceiverVO;
import com.selftopup.pretups.transfer.businesslogic.TransferVO;
import com.selftopup.pretups.util.OperatorUtilI;

/**
 * @(#)RestrictedSubscriberBL.java
 *                                 Copyright(c) 2005, Bharti Telesoft Int.
 *                                 Public Ltd.
 *                                 All Rights Reserved
 *                                 This class holds the Business Logic for
 *                                 restricted MSISDNs in Pretups system.
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Ankit Singhal March 24, 2006 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 */

public class RestrictedSubscriberBL {
    private static Log _log = LogFactory.getLog(RestrictedSubscriberBL.class.getName());
    /**
     * To genrate the operator to channel transfer id
     */
    public static OperatorUtilI calculatorI = null;
    // calculate the tax
    static {
        String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferBL[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * This method checks the receiver's limits in case of restricted MSISDN
     * 
     * @param p_transferVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public static boolean validateRestrictedSubscriberLimits(TransferVO p_transferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateRestrictedSubscriberLimits", p_transferVO);
        boolean isValidated = false;
        try {
            long requestedAmount = p_transferVO.getRequestedAmount();
            RestrictedSubscriberVO restrictedSubscriberVO = (RestrictedSubscriberVO) ((ReceiverVO) p_transferVO.getReceiverVO()).getRestrictedSubscriberVO();
            if (restrictedSubscriberVO == null)
                throw new BTSLBaseException("RestrictedSubscriberBL", "validateRestrictedSubscriberLimits", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION, 0, null, null);

            if (requestedAmount < restrictedSubscriberVO.getMinTxnAmount())
                throw new BTSLBaseException("RestrictedSubscriberBL", "validateRestrictedSubscriberLimits", SelfTopUpErrorCodesI.RM_ERROR_AMOUNT_LESSTHANMINIMUM, 0, null, null);
            if (requestedAmount > restrictedSubscriberVO.getMaxTxnAmount())
                throw new BTSLBaseException("RestrictedSubscriberBL", "validateRestrictedSubscriberLimits", SelfTopUpErrorCodesI.RM_ERROR_AMOUNT_MORETHANMAXIMUM, 0, null, null);
            if (requestedAmount + restrictedSubscriberVO.getMonthlyTransferAmount() > restrictedSubscriberVO.getMonthlyLimit())
                throw new BTSLBaseException("RestrictedSubscriberBL", "validateRestrictedSubscriberLimits", SelfTopUpErrorCodesI.RM_ERROR_AMOUNT_MONTHLYLIMIT_CROSSED, 0, null, null);
            isValidated = true;
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateRestrictedSubscriberLimits", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberBL[validateRestrictedSubscriberLimits]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RestrictedSubscriberBL", "validateRestrictedSubscriberLimits", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validateRestrictedSubscriberLimits", "returning isValidated " + isValidated);
        }

        return isValidated;
    }
}