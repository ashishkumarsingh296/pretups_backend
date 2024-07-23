/**
 * @(#)SonatelUtil.java
 *                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                      All Rights Reserved
 * 
 *                      <description>
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      avinash.kamthan Aug 5, 2005 Initital Creation
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 * 
 */

package com.client.pretups.util.clientutils;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;

/**
 * @author avinash.kamthan
 *         Tax 1 Rate (Service Tax)= x %
 *         Tax2 Rate (Withholding tax) = y %
 *         Distributor Margin Rate= z%
 * 
 * 
 *         Tax1 Value=(x/(100+x))*MRP (tax in inclusive in MRP)
 *         Distributor Margin Value = (z/1000)*Transfer MRP
 *         Tax 2 Value = (y/100)*Distributor Margin Value
 *         Distributor Amount Payable = MRP – Distributor Margin Value –Tax2
 * 
 */
public class SonatelUtil extends OperatorUtil {
    /**
     * Field C2S_TRANSFER_ID_PAD_LENGTH. for the TXN_ID of the C2S TXN
     */
    public int C2S_TRANSFER_ID_PAD_LENGTH = 4;
    /**
     * Field P2P_TRANSFER_ID_PAD_LENGTH. for the TXN_ID of the P2P TXN
     */
    public int P2P_TRANSFER_ID_PAD_LENGTH = 4;
    /**
     * Field CHANEL_TRANSFER_ID_LENGTH. for the TXN_ID of the
     * Channel(O2C/FOC/C2C) TXN
     */
    public int CHANEL_TRANSFER_ID_PAD_LENGTH = 4;
    /**
     * Field NETWORK_STOCK_TXN_ID_LENGTH. for the TXN_ID of the NetworkStock TXN
     */
    public int NETWORK_STOCK_TXN_ID_PAD_LENGTH = 4;
    /**
     * Field SCHEDULE_BATCH_ID_PAD_LENGTH.
     */
    public int SCHEDULE_BATCH_ID_PAD_LENGTH = 4;

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method calculateTax1.
     * 
     * @param p_type
     *            String
     * @param p_rate
     *            double
     * @param p_productCost
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#calculateTax1(String, double,
     *      long)
     */
    public long calculateTax1(String p_type, double p_rate, long p_productCost) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("calculateTax1()", "Entered  p_type: " + p_type + " p_rate: " + p_rate + " p_productCost: " + p_productCost);
        }
        double taxCalculatedValue = 0;
        if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(p_type)) {
            taxCalculatedValue = (p_rate / (100 + p_rate)) * (p_productCost);
        } else if (PretupsI.SYSTEM_AMOUNT.equals(p_type)) {
            taxCalculatedValue = (long) (p_rate);
        } else {
            if (_log.isDebugEnabled()) {
                _log.debug("calculateTax1()", "Exception p_type is not define in the system p_type=" + p_type);
            }
            throw new BTSLBaseException(this, "calculateTax1", "error.invalid.ratetype");
        }

        if (_log.isDebugEnabled()) {
            _log.debug("calculateTax1()", "Exited  Tax1 Calculated Value " + taxCalculatedValue);
        }
        return (long) taxCalculatedValue;
    }

    /**
     * Method calculateTax2.
     * 
     * @param p_type
     *            String
     * @param p_rate
     *            double
     * @param p_value
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#calculateTax2(String, double,
     *      long)
     */
    public long calculateTax2(String p_type, double p_rate, long p_value) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("calculateTax2()", "Entered  p_type: " + p_type + " p_rate: " + p_rate + " p_productCost: " + p_value);
        }

        double taxCalculatedValue = 0;
        if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(p_type)) {
            taxCalculatedValue = (p_rate / 100) * p_value;
        } else if (PretupsI.SYSTEM_AMOUNT.equals(p_type)) {
            taxCalculatedValue = (long) (p_rate);
        } else {
            if (_log.isDebugEnabled()) {
                _log.debug("calculateTax2()", "Exception p_type is not define in the system p_type=" + p_type);
            }
            throw new BTSLBaseException(this, "calculateTax2", "error.invalid.ratetype");
        }

        if (_log.isDebugEnabled()) {
            _log.debug("calculateTax2()", "Exited  Tax2 Calculated Value " + taxCalculatedValue);
        }
        return (long) taxCalculatedValue;
    }

    /**
     * Method calculateTax3.
     * 
     * @param p_type
     *            String
     * @param p_rate
     *            double
     * @param p_value
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#calculateTax3(String, double,
     *      long)
     */
    public long calculateTax3(String p_type, double p_rate, long p_value) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("calculateTax3()", "Entered  p_type: " + p_type + " p_rate: " + p_rate + " p_cost: " + p_value);
        }
        double taxCalculatedValue = 0;
        if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(p_type)) {
            taxCalculatedValue = (p_rate / 100) * (p_value);
        } else if (PretupsI.SYSTEM_AMOUNT.equals(p_type)) {
            taxCalculatedValue = (long) (p_rate);
        } else {
            if (_log.isDebugEnabled()) {
                _log.debug("calculateTax3()", "Exception p_type is not define in the system p_type=" + p_type);
            }
            throw new BTSLBaseException(this, "calculateTax3", "error.invalid.ratetype");
        }

        if (_log.isDebugEnabled()) {
            _log.debug("calculateTax3()", "Exited  Tax3 Calculated Value " + taxCalculatedValue);
        }
        return (long) taxCalculatedValue;
    }

    /**
     * Method calculateCommission.
     * 
     * @param p_commissionType
     *            String
     * @param p_commissionRate
     *            double
     * @param p_productCost
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#calculateCommission(String,
     *      double, long)
     */
    public long calculateCommission(String p_commissionType, double p_commissionRate, long p_productCost) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("calculateCommission()", "Entered  p_type: " + p_commissionType + " p_rate: " + p_commissionRate + " p_cost: " + p_productCost);
        }

        double commValue = 0;
        if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(p_commissionType)) {
            commValue = (p_commissionRate / 100) * (p_productCost);
        } else if (PretupsI.SYSTEM_AMOUNT.equals(p_commissionType)) {
            commValue = (long) (p_commissionRate);
        } else {
            if (_log.isDebugEnabled()) {
                _log.debug("calculateCommission()", "Exception p_commissionType is not define in the system p_commissionType=" + p_commissionType);
            }
            throw new BTSLBaseException(this, "calculateCommission", "error.invalid.ratetype");
        }

        if (_log.isDebugEnabled()) {
            _log.debug("calculateCommission()", "Exited Commission Clculated value " + commValue);
        }

        return (long) commValue;
    }

    /**
     * Method calculateValidity.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_transferDateTime
     *            Date
     * @param p_previousExpiry
     *            Date
     * @param p_valPeriodType
     *            String
     * @param p_validityPeriod
     *            int
     * @param p_bonusValidity
     *            int
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#calculateValidity(TransferVO,
     *      Date, Date, String, int, int)
     */
    public void calculateValidity(TransferVO p_transferVO, Date p_transferDateTime, Date p_previousExpiry, String p_valPeriodType, int p_validityPeriod, int p_bonusValidity) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug(
                "calculateValidity",
                "Entered with p_transferDateTime=" + p_transferDateTime + " p_previousExpiry=" + p_previousExpiry + " p_valPeriodType=" + p_valPeriodType + " p_validityPeriod=" + p_validityPeriod + " p_bonusValidity=" + p_bonusValidity);
        }
        final String METHOD_NAME = "calculateValidity";
        try {
            java.util.Date newDate = null;
            if (p_previousExpiry == null) {
                p_previousExpiry = new Date();
            }

            // set the receiver Validity by adding the bonus validity.
            p_transferVO.setReceiverValidity(p_validityPeriod + p_bonusValidity);

            // Today + no of days or Previous Expiry which ever is higher
            if (p_valPeriodType.equals(PretupsI.VALPERIOD_HIGHEST_TYPE)) {
                newDate = BTSLUtil.addDaysInUtilDate(p_transferDateTime, p_validityPeriod + p_bonusValidity);
                if (newDate.after(p_previousExpiry)) {
                    p_transferVO.setValidityDateToBeSet(newDate);
                } else {
                    p_transferVO.setValidityDateToBeSet(p_previousExpiry);
                }
            }
            // Today + no of days or Previous Expiry + no of days which ever is
            // higher
            else if (p_valPeriodType.equals(PretupsI.VALPERIOD_CUMMULATIVE_TYPE)) {
                newDate = BTSLUtil.addDaysInUtilDate(p_transferDateTime, p_validityPeriod + p_bonusValidity);
                java.util.Date newDate2 = null;
                newDate2 = BTSLUtil.addDaysInUtilDate(p_previousExpiry, p_validityPeriod + p_bonusValidity);
                if (newDate2.after(newDate)) {
                    p_transferVO.setValidityDateToBeSet(newDate2);
                } else {
                    p_transferVO.setValidityDateToBeSet(newDate);
                }
            }
            // Today + no of days
            else if (p_valPeriodType.equals(PretupsI.VALPERIOD_LOWEST_TYPE)) {
                newDate = BTSLUtil.addDaysInUtilDate(p_transferDateTime, p_validityPeriod + p_bonusValidity);
                p_transferVO.setValidityDateToBeSet(newDate);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[calculateValidity]", p_transferVO
                .getTransferID(), p_transferVO.getReceiverMsisdn(), " ", "Not able to calculate the new validity period getting Exception=" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "calculateValidity", PretupsErrorCodesI.ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("calculateValidity", "Exiting");
        }
    }

    /**
     * Method formatP2PTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatP2PTransferID(TransferVO,
     *      long)
     */
    public String formatP2PTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatP2PTransferID";
        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // String currentYear=BTSLUtil.getFinancialYearLastDigits(2);
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), P2P_TRANSFER_ID_PAD_LENGTH);
            // returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr;
            returnStr = "C" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants
                .getProperty("INSTANCE_ID") + paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TaxCalculator[]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method to perform validation of start and end range for card group.
     * cuerrently this method do no validation.
     * But if any operator wants to run the system on CVG concept
     * then we will check start range and end range in case of VG should be
     * same.
     * added for CRE_INT_CR00029 by ankit Zindal
     * 
     * @param p_startRange
     *            String
     * @param p_endRange
     *            String
     * @param p_subService
     *            String
     * 
     * @return void
     */
    public void validateCardGroupDetails(String p_startRange, String p_endRange, String p_subService) throws Exception {
        try {
            // This implementation is comented because not needed at this time.
            // Will be implemented for any operator.
            /*
             * if(p_subService.equals(PretupsI.SUB_SERVICE_TYPE_VG))
             * {
             * if(Double.parseDouble(p_startRange)!=Double.parseDouble(
             * p_endRange))
             * throw new BTSLBaseException(this,"validateCardGroupDetails",
             * "cardgroup.addc2scardgroup.error.invalidstartandendrange");
             * 
             * }
             */
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Method to set the value for transfer.
     * This methos is called from CardGroupBL. At this time this method set
     * various values in the transferVO
     * For any other operator who wants to run system on CVG mode, we can change
     * values setting based on subservice.
     * added for CRE_INT_CR00029 by ankit Zindal
     * 
     * @param p_subService
     *            String
     * @param p_cardGroupDetailVO
     *            CardGroupDetailsVO
     * @param p_transferVO
     *            TransferVO
     * 
     * @return void
     */
    public void setCalculatedCardGroupValues(String p_subService, CardGroupDetailsVO p_cardGroupDetailVO, TransferVO p_transferVO) throws Exception {

        try {
            TransferItemVO transferItemVO = null;
            final int bonusValidityValue = Integer.parseInt(String.valueOf(p_cardGroupDetailVO.getBonusValidityValue()));
            final int validityPeriodValue = p_cardGroupDetailVO.getValidityPeriod();
            final long transferValue = p_cardGroupDetailVO.getTransferValue();
            final long bonusValue = p_cardGroupDetailVO.getBonusTalkTimeValue();

            p_transferVO.setReceiverBonusValidity(bonusValidityValue);
            p_transferVO.setReceiverGracePeriod(p_cardGroupDetailVO.getGracePeriod());
            p_transferVO.setReceiverValidity(validityPeriodValue);
            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
            // Is Bonus Validity on Requested Value ??
            calculateValidity(p_transferVO, transferItemVO.getTransferDateTime(), transferItemVO.getPreviousExpiry(), p_cardGroupDetailVO.getValidityPeriodType(),
                validityPeriodValue, bonusValidityValue);
            p_transferVO.setReceiverTransferValue(transferValue);
            transferItemVO.setTransferValue(transferValue);
            transferItemVO.setGraceDaysStr(String.valueOf(p_cardGroupDetailVO.getGracePeriod()));
            transferItemVO.setValidity(validityPeriodValue);
            p_transferVO.setReceiverBonusValue(bonusValue);
            /**
             * If we wants to run system for CVG then set the transfer value to
             * 0 for VG
             * and validity and grace to 0 in case of C type selector.
             * 
             */
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Date 04/10/07
     * Method to Add 77 string at starting position of message
     * if message lengths is less than the fixed length
     * 
     * @param message
     *            ,addString,fixedLength
     * @return String
     */
    public String addRemoveDigitsFromMSISDN(String p_msisdn) {
        if (_log.isDebugEnabled()) {
            _log.debug("addRemoveDigitsFromMSISDN", "Entered with p_msisdn=" + p_msisdn);
        }

        String newMsisdn = null;

        final int msisdnLength = p_msisdn.length();
        final int systemMSISDNLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue();
        if (msisdnLength == (systemMSISDNLength - 2)) {
            newMsisdn = "77" + p_msisdn;
        } else if (msisdnLength == (systemMSISDNLength - 1)) {
            newMsisdn = "7" + p_msisdn;
        } else {
            newMsisdn = p_msisdn;
        }

        if (_log.isDebugEnabled()) {
            _log.debug("addRemoveDigitsFromMSISDN", "Exiting p_msisdn=" + newMsisdn);
        }

        return newMsisdn;
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
        // if(BTSLUtil.isNullString(p_password))
        // {
        // previously used when default password used to be 00000000
        // String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_password);
        // if(defaultPin.equals(p_password))
        // return messageMap;
        final String defaultPin = BTSLUtil.getDefaultPasswordText(p_password);
        if (defaultPin.equals(p_password)) {
            return messageMap;
        }
        // }
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

    /**
     * Method generateRandomPassword.
     * 
     * @return String
     * @author santanu.mohanty
     */
    public String generateRandomPassword() {
        if (_log.isDebugEnabled()) {
            _log.debug("generateRandomPassword", "Entered in to VFQatarUtil");
        }

        // These variable will be used in generateRandomPassword()
        final String METHOD_NAME = "generateRandomPassword";
        String returnStr = null;
        String specialStr = "";
        String numberStr = null;
        String alphaStr = null;
        String finalStr = null;
        String SPECIAL_CHARACTERS = null;

        try {
            int decreseCounter = 0;
            String specialChar = Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
            if (!BTSLUtil.isNullString(specialChar)) {
                decreseCounter = 1;
                specialChar = specialChar.replace(",", "");
                SPECIAL_CHARACTERS = specialChar;// "~!@#$%^&";
                specialStr = BTSLUtil.generateRandomPIN(SPECIAL_CHARACTERS, decreseCounter);
            }
            final String DIGITS = "0123456789";
            numberStr = BTSLUtil.generateRandomPIN(DIGITS, 1);
            decreseCounter++;
            final String LOCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
            final String UPCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            final String PRINTABLE_CHARACTERS = LOCASE_CHARACTERS + UPCASE_CHARACTERS;
            final int minLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue();
            final int maxLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue();
            final int passwordLength = rand(minLength, maxLength);
            while (true) {
                alphaStr = BTSLUtil.generateRandomPIN(PRINTABLE_CHARACTERS, passwordLength - decreseCounter);
                final int result = BTSLUtil.isSMSPinValid(alphaStr);
                if (result == -1) {
                    continue;
                } else if (result == 1) {
                    continue;
                } else {
                    break;
                }
            }
            finalStr = specialStr + alphaStr + numberStr;
            returnStr = BTSLUtil.generateRandomPIN(finalStr, passwordLength);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[generateRandomPassword]", "", "", "",
                "Exception generate Random Password=" + e.getMessage());
            returnStr = null;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("generateRandomPassword", "Exiting from SafaricomUtil = " + returnStr);
        }
        return returnStr;
    }

    private static Random rn = new Random();

    public static int rand(int lo, int hi) {
        final int n = hi - lo + 1;
        int i = rn.nextInt() % n;
        if (i < 0) {
            i = -i;
        }
        return lo + i;
    }
	
	/**
	 * Method formatChannelTransferID.
	 * @param p_channelTransferVO ChannelTransferVO
	 * @param p_tempTransferStr String
	 * @param p_tempTransferID long
	 * @return String
	 * @see com.btsl.pretups.util.OperatorUtilI#formatChannelTransferID(ChannelTransferVO, String, long)
	 */
	public String formatChannelTransferID(ChannelTransferVO p_channelTransferVO,String p_tempTransferStr,long p_tempTransferID)
	{
		String returnStr=null;
		final String METHOD_NAME = "formatChannelTransferID";
		try
		{
			String paddedTransferIDStr=BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID),CHANEL_TRANSFER_ID_PAD_LENGTH);
			
			if("PM".equalsIgnoreCase(p_channelTransferVO.getNetworkCode()))
				paddedTransferIDStr = "2"+paddedTransferIDStr;
			//returnStr=p_tempTransferStr+currentDateTimeFormatString(p_channelTransferVO.getCreatedOn())+"."+currentTimeFormatString(p_channelTransferVO.getCreatedOn())+"."+paddedTransferIDStr;
			returnStr=p_tempTransferStr+currentDateTimeFormatString(p_channelTransferVO.getDBDateTime())+"."+currentTimeFormatString(p_channelTransferVO.getDBDateTime())+"."+paddedTransferIDStr;
			p_channelTransferVO.setTransferID(returnStr);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TaxCalculator[formatChannelTransferID]","","","","Not able to generate Transaction ID:"+e.getMessage());
			returnStr=null;
		}
		return returnStr;
	}

}
