package com.btsl.pretups.logging;

/*
 * ProfileBonus.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Manisha Jain 19/02/09 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Bharti Telesoft Ltd.
 * Class for logging activation bonus calculation for channel user
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.processes.businesslogic.ActivationBonusVO;

public class ProfileBonusLog {

    private static Log _log = LogFactory.getFactory().getInstance(ProfileBonusLog.class.getName());

    /**
   	 * ensures no instantiation
   	 */
    private ProfileBonusLog(){
    	
    }
    /**
     * @param p_bonusVO
     *            BonusVO
     *            Log information for redemption process.
     */

    public static void log(ActivationBonusVO p_bonusVO) {
        StringBuffer str = new StringBuffer();
        if (p_bonusVO != null) {
            str.append("[PROFILE_TYPE: " + p_bonusVO.getProfileType() + "] ");
            str.append("[USER_ID_OR_MSISDN: " + p_bonusVO.getUserId() + "] ");
            str.append("[PREVIOUS POINTS: " + p_bonusVO.getPoints() + "] ");
            str.append("[REMAINING POINTS: " + p_bonusVO.getRemainingPoints() + "] ");
            str.append("[POINTS REDEMED: " + p_bonusVO.getPointsToRedeem() + "] ");
            str.append("[BUCKET_CODE: " + p_bonusVO.getBucketCode() + "] ");
            str.append("[PRODUCT_CODE: " + p_bonusVO.getProductCode() + "] ");
            str.append("[POINTS_DATE: " + p_bonusVO.getPointsDate() + "] ");
            str.append("[LAST_REDEMPTION_ID: " + p_bonusVO.getLastRedemptionId() + "] ");
            str.append("[LAST_REDEMPTION_ON: " + p_bonusVO.getLastRedemptionDate() + "] ");
            str.append("[LAST_ALLOCATION_TYPE: " + p_bonusVO.getLastAllocationType() + "] ");
            str.append("[LAST_ALLOCATED_ON: " + p_bonusVO.getLastAllocationdate() + "] ");

            _log.info(" ", str.toString());
        }
    }

    /**
     * @param p_bonusVO
     *            BonusVO
     *            Log information of activation bonus calculation.
     */
    public static void log(String p_message, ActivationBonusVO p_bonusVO, String p_userID, String p_receiverMsisdn, double p_points, String p_transferAmt, double p_previousPoints) {
        StringBuffer str = new StringBuffer();
        if (p_bonusVO != null) {
            str.append("[MESSAGE: " + p_message + "] ");
            str.append("[PROFILE_TYPE: " + p_bonusVO.getProfileType() + "] ");
            str.append("[USER_ID_OR_MSISDN: " + p_bonusVO.getUserId() + "] ");
            str.append("[SUBSCRIBER_MSISDN: " + p_receiverMsisdn + "] ");
            str.append("[AMOUNT: " + p_transferAmt + "] ");
            str.append("[POINTS: " + p_points + "] ");
            str.append("[PREVIOUS POINTS: " + p_previousPoints + "] ");
            str.append("[BUCKET_CODE: " + p_bonusVO.getBucketCode() + "] ");
            str.append("[PRODUCT_CODE: " + p_bonusVO.getProductCode() + "] ");
            str.append("[POINTS_DATE: " + p_bonusVO.getPointsDate() + "] ");
            str.append("[ALLOCATION_TYPE: " + p_bonusVO.getLastAllocationType() + "] ");
        } else {
            str.append("[MESSAGE: " + p_message + "] ");
            str.append("[USER ID: " + p_userID + "] ");
            str.append("[RECEIVER MSISDN: " + p_receiverMsisdn + "] ");
            str.append("[AMOUNT: " + p_transferAmt + "] ");
        }
        _log.info(" ", str.toString());
    }
}