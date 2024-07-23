/**
 * @(#)OmaUtil.java
 *                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                  All Rights Reserved
 * 
 *                  <description>
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  avinash.kamthan Aug 5, 2005 Initital Creation
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 * 
 */

package com.client.pretups.util.clientutils;

import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class OmaUtil extends OperatorUtil {
    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method will convert operator specific msisdn to system specific
     * msisdn.
     * 
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     */
    public String getSystemFilteredMSISDN(String p_msisdn) throws BTSLBaseException {
        String msisdn = super.getSystemFilteredMSISDN(p_msisdn);
        if (msisdn.length() < 10) {
            msisdn = "0" + msisdn;
        }
        return msisdn;
    }

    /**
     * This method will convert system specific msisdn to operater specific
     * msisdn
     * 
     * @param p_msisdn
     * @return
     */
    public String getOperatorFilteredMSISDN(String p_msisdn) {
        if (p_msisdn.length() == 10 && p_msisdn.startsWith("0")) {
            p_msisdn = p_msisdn.substring(1);
        }
        return Constants.getProperty("COUNTRY_CODE") + p_msisdn;
    }

    /**
     * Method validatePassword.
     * 
     * @author sanjeew.kumar
     * @created on 12/07/07
     * @param p_loginID
     *            String
     * @param p_password
     *            String
     * @return String
     */
    public HashMap validatePassword(String p_loginID, String p_password) {
        _log.debug("validatePassword", "Entered, p_userID= ", new String(p_loginID + ", Password= " + p_password));
        final HashMap messageMap = new HashMap();
        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_password);
        if (defaultPin.equals(p_password)) {
            return messageMap;
        }
        defaultPin = BTSLUtil.getDefaultPasswordText(p_password);
        if (defaultPin.equals(p_password)) {
            return messageMap;
        }
        if (p_password.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue() || p_password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) {
            final String[] args = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) };
            messageMap.put("operatorutil.validatepassword.error.passwordlenerr", args);
        }
        final int result = BTSLUtil.isSMSPinValid(p_password);// for consecutive
        // and
        // same characters
        if (result == -1) {
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit", null);
        } else if (result == 1) {
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutive", null);
        }
        if (!BTSLUtil.containsChar(p_password)) {
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontainschar", null);
        }
        // for special character
        final String specialChar = Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
        if (!BTSLUtil.isNullString(specialChar)) {
            final String[] specialCharArray = { specialChar };
            final String[] passwordCharArray = specialChar.split(",");
            boolean specialCharFlag = false;
            for (int i = 0, j = passwordCharArray.length; i < j; i++) {
                if (p_password.contains(passwordCharArray[i])) {
                    specialCharFlag = true;
                    break;
                }
            }
            if (!specialCharFlag) {
                messageMap.put("operatorutil.validatepassword.error.passwordspecialchar", specialCharArray);
            }
        }
        // for number
        final String[] passwordNumberStrArray = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        boolean numberStrFlag = false;
        for (int i = 0, j = passwordNumberStrArray.length; i < j; i++) {
            if (p_password.contains(passwordNumberStrArray[i])) {
                numberStrFlag = true;
                break;
            }
        }
        if (!numberStrFlag) {
            messageMap.put("operatorutil.validatepassword.error.passwordnumberchar", null);
        }
        if (p_loginID.equals(p_password)) {
            messageMap.put("operatorutil.validatepassword.error.sameusernamepassword", null);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validatePassword", "Exiting ");
        }
        return messageMap;
    }

    public void setCalculatedCardGroupValues(String p_subService, CardGroupDetailsVO p_cardGroupDetailVO, TransferVO p_transferVO) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("setCalculatedCardGroupValues()",
                "Entered  p_subService: " + p_subService + " p_cardGroupDetailVO: " + p_cardGroupDetailVO + " p_transferVO: " + p_transferVO);
        }

        try {
            /**
             * In case of CVG all values are set as calculated.
             * In case of VG transfer value is set to 0.
             * In case of C, validity and grace will be set to 0.
             * 
             */
            TransferItemVO transferItemVO = null;
            final int bonusValidityValue = Integer.parseInt(String.valueOf(p_cardGroupDetailVO.getBonusValidityValue()));
            final int validityPeriodValue = p_cardGroupDetailVO.getValidityPeriod();
            final long transferValue = p_cardGroupDetailVO.getTransferValue();
            final long bonusValue = p_cardGroupDetailVO.getBonusTalkTimeValue();
            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);

            // This feature is specific to the operator
            // if operator wants, amount is needed to be deducted for Get number
            // back service
            // so transfer the value to user after amount deducted for number
            // back feature
            // so net transfer value is
            // transferValue=transferValue-amountDeducted
            // and accessFee is normalAccessFee+amountDeducted

            /*
             * int amountDeducted;
             * if(transferItemVO.isNumberBackAllowed())
             * {
             * amountDeducted= transferItemVO.getAmountDeducted();
             * transferValue=transferValue-amountDeducted;
             * if(!(transferValue>0))
             * throw new BTSLBaseException(this,"setCalculatedCardGroupValues",
             * PretupsErrorCodesI.TRANSFER_VALUE_IS_NOT_VALID);
             * p_cardGroupDetailVO.setTransferValue(transferValue);
             * p_transferVO.setReceiverAccessFee(p_transferVO.getReceiverAccessFee
             * () + amountDeducted);
             * }
             * }
             */

            p_transferVO.setReceiverBonusValidity(bonusValidityValue);
            p_transferVO.setReceiverGracePeriod(p_cardGroupDetailVO.getGracePeriod());
            p_transferVO.setReceiverValidity(validityPeriodValue);
            // Is Bonus Validity on Requested Value ??
            calculateValidity(p_transferVO, transferItemVO.getTransferDateTime(), transferItemVO.getPreviousExpiry(), p_cardGroupDetailVO.getValidityPeriodType(),
                validityPeriodValue, bonusValidityValue);
            p_transferVO.setReceiverTransferValue(transferValue);
            transferItemVO.setTransferValue(transferValue);
            transferItemVO.setGraceDaysStr(String.valueOf(p_cardGroupDetailVO.getGracePeriod()));
            transferItemVO.setValidity(validityPeriodValue);
            p_transferVO.setReceiverBonusValue(bonusValue);

            if ((String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE)).equals(p_subService) && "P2P".equals(p_transferVO.getModule()))// C
            {
                p_transferVO.setReceiverBonusValidity(0);
                p_transferVO.setReceiverGracePeriod(0);
                p_transferVO.setReceiverValidity(0);
                p_transferVO.setReceiverTransferValue(transferValue);
                transferItemVO.setTransferValue(transferValue);
                transferItemVO.setGraceDaysStr("0");
                transferItemVO.setValidity(0);
                p_transferVO.setReceiverBonusValue(bonusValue);
            }
            if ((String.valueOf(PretupsI.CHNL_SELECTOR_VG_VALUE)).equals(p_subService) && "P2P".equals(p_transferVO.getModule()))// VG
            {
                p_transferVO.setReceiverBonusValidity(bonusValidityValue);
                p_transferVO.setReceiverGracePeriod(p_cardGroupDetailVO.getGracePeriod());
                p_transferVO.setReceiverValidity(validityPeriodValue);
                // Is Bonus Validity on Requested Value ??
                calculateValidity(p_transferVO, transferItemVO.getTransferDateTime(), transferItemVO.getPreviousExpiry(), p_cardGroupDetailVO.getValidityPeriodType(),
                    validityPeriodValue, bonusValidityValue);
                p_transferVO.setReceiverTransferValue(0);
                transferItemVO.setTransferValue(0);
                transferItemVO.setGraceDaysStr(String.valueOf(p_cardGroupDetailVO.getGracePeriod()));
                transferItemVO.setValidity(validityPeriodValue);
                p_transferVO.setReceiverBonusValue(0);
            }

        } catch (Exception e) {
            _log.errorTrace("setCalculatedCardGroupValues", e);
            throw e;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("setCalculatedCardGroupValues()", "Exiting  p_transferVO: " + p_transferVO.toString());
            }
        }
    }

    // for c2s table merging
    public boolean getNewDataAftrTbleMerging(Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "getNewDataAftrTbleMerging";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:: p_fromDate=" + p_fromDate + " , p_toDate=" + p_toDate);
        }
        boolean newData = false;
        final String migrationDate = Constants.getProperty("MIGRATION_DATE");
        try {
            final int no_of_days_aftr_migration = Integer.parseInt(Constants.getProperty("AFTER_MIG_NO_OF_DAYS"));
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "migrationDate=" + migrationDate + " , no_of_days_aftr_migration=" + no_of_days_aftr_migration);
            }
            final Date lastDate = BTSLUtil.addDaysInUtilDate(BTSLUtil.getDateFromDateString(migrationDate), no_of_days_aftr_migration);
            final Date currentdate = new Date();
            if (p_toDate == null) {
                p_toDate = p_fromDate;
            }
            if (BTSLUtil.isNullString(Constants.getProperty("OLD_DATA_REQ_AFTR_TBLE_MERGE")) || PretupsI.NO.equalsIgnoreCase(Constants
                .getProperty("OLD_DATA_REQ_AFTR_TBLE_MERGE"))) {
                newData = true;
            } else {
                // Back date Enquiry Handling
                if (BTSLUtil.getDifferenceInUtilDates(p_toDate, new Date()) <= 0) // IF
                // CURRENT
                {
                    p_toDate = BTSLUtil.addDaysInUtilDate(BTSLUtil.getDateFromDateString(migrationDate), -1);
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "p_toDate=" + p_toDate);
                    // Ended Here
                }

                if (BTSLUtil.getDateFromDateString(migrationDate).after(p_fromDate) && BTSLUtil.getDateFromDateString(migrationDate).before(p_toDate)) {
                    throw new BTSLBaseException(this, "getNewDataAftrTbleMerging", "operatorUtil.date.range.error");
                } else if (p_fromDate.compareTo(BTSLUtil.getDateFromDateString(migrationDate)) >= 0) {
                    newData = true;
                } else if ((currentdate.after(lastDate)) && (p_fromDate.before(BTSLUtil.getDateFromDateString(migrationDate)))) {
                    newData = true;
                }
            }
        } catch (Exception e) {
            _log.error(methodName, e);
            throw new BTSLBaseException(this, "getNewDataAftrTbleMerging", "operatorUtil.date.range.error");
        }
        return newData;
    }

}
