/**
 * @(#)OperatorUtil.java
 *                       Copyright(c) 2005, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 * 
 *                       <description>
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       avinash.kamthan Aug 5, 2005 Initital Creation
 *                       Abhijit Jul 21 2006 Modified By
 *                       Ankit Zindal Nov 20,2006 ChangeID=LOCALEMASTER
 *                       Sourabh Gupta Dec 14,2006 ChangeId=TATASKYRCHG
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 * 
 */

package com.selftopup.pretups.util;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.p2p.subscriber.requesthandler.ModifyCardDetailsController;
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsDAO;
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsVO;
import com.selftopup.pretups.payment.businesslogic.PaymentMethodCache;
import com.selftopup.pretups.payment.businesslogic.PaymentMethodKeywordVO;
import com.selftopup.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.selftopup.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.selftopup.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.selftopup.pretups.routing.subscribermgmt.businesslogic.RoutingDAO;
import com.selftopup.pretups.subscriber.businesslogic.ReceiverVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.transfer.businesslogic.TransferItemVO;
import com.selftopup.pretups.transfer.businesslogic.TransferVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.UtilValidate;

/**
 * @author avinash.kamthan This class must be extended if Operator specific
 *         implementation would be required Tax 1 Rate (Service Tax)= x % Tax2
 *         Rate (Withholding tax) = y % Distributor Margin Rate= z%
 * 
 * 
 *         Tax1 Value=(x/(100+x))*MRP (tax in inclusive in MRP) Distributor
 *         Margin Value =
 *         (z/1000)*Transfer MRP Tax 2 Value = (y/100)*Distributor Margin Value
 *         Distributor Amount Payable = MRP � Distributor Margin Value �Tax2
 * 
 */
public class OperatorUtil implements OperatorUtilI {
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
     * Field FOC_BATCH_MASTER_ID_PAD_LENGTH. for the TXN_ID of the FOC BATCH
     * MASTER
     */
    public int FOC_BATCH_MASTER_ID_PAD_LENGTH = 3;

    /**
     * Field FOC_BATCH_DETAIL_ID_PAD_LENGTH. for the TXN_ID of the FOC BATCH
     * DETAILS
     */

    public int FOC_BATCH_DETAIL_ID_PAD_LENGTH = 5;
    /**
     * Field ADJUSTMENT_ID_PAD_LENGTH.
     */
    public int ADJUSTMENT_ID_PAD_LENGTH = 4;

    public int BATCH_ID_LENGTH = Integer.parseInt(Constants.getProperty("BATCH_PADDING_LENGTH"));

    public static NumberPortDAO _numberPortDAO = new NumberPortDAO();

    /**
     * Field C2C_BATCH_DETAIL_ID_PAD_LENGTH. for the TXN_ID of the C2C BATCH
     * DETAILS
     */

    public int C2C_BATCH_DETAIL_ID_PAD_LENGTH = 5;

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private static Random rn = new Random();

    // added by Lohit For Direct Payout
    public int DP_BATCH_MASTER_ID_PAD_LENGTH = 3;
    public int DP_BATCH_DETAIL_ID_PAD_LENGTH = 5;
    // end
    public int SALE_BATCH_NUMBER_PAD_LENGTH = 5;

    public int CRBT_REG__TRANSFER_ID_PAD_LENGTH = 4;

    /**
     * Field O2C_BATCH_MASTER_ID_PAD_LENGTH. for the TXN_ID of the FOC BATCH
     * MASTER
     */
    public int O2C_BATCH_MASTER_ID_PAD_LENGTH = 3;
    // vastrix added by hitesh
    public int VAS_TRANSFER_ID_PAD_LENGTH = 4;

    /**
     * Method calculateAccessFee.
     * 
     * @param p_accessFeeValue
     *            double
     * @param p_accessFeeType
     *            String
     * @param p_requestedValue
     *            long
     * @param p_minAccessFee
     *            long
     * @param p_maxAccessFee
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.selftopup.pretups.util.OperatorUtilI#calculateAccessFee(double,
     *      String, long, long, long)
     */
    public long calculateAccessFee(double p_accessFeeValue, String p_accessFeeType, long p_requestedValue, long p_minAccessFee, long p_maxAccessFee) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("calculateAccessFee", "Entered with p_accessFeeValue=" + p_accessFeeValue + " p_accessFeeType=" + p_accessFeeType + " p_minAccessFee=" + p_minAccessFee + " p_minAccessFee=" + p_minAccessFee);
        long calculatedAccessFee = 0;
        try {
            if (p_accessFeeType.equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT))
                calculatedAccessFee = (long) p_accessFeeValue;
            else if (p_accessFeeType.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
                // calculatedAccessFee=(long)((p_accessFeeValue*p_requestedValue)/(100+p_accessFeeValue));
                calculatedAccessFee = (long) ((p_accessFeeValue * p_requestedValue) / (100));
                if (calculatedAccessFee < p_minAccessFee)
                    calculatedAccessFee = p_minAccessFee;
                else if (calculatedAccessFee > p_maxAccessFee)
                    calculatedAccessFee = p_maxAccessFee;
            } else {
                if (_log.isDebugEnabled())
                    _log.debug("calculateAccessFee()", "Exception p_accessFeeType is not define in the system p_accessFeeType=" + p_accessFeeType);
                throw new BTSLBaseException(this, "calculateAccessFee", "error.invalid.ratetype");
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[calculateAccessFee]", "", "", " ", "Not able to calculate the access fee applicable getting Exception=" + e.getMessage());
            throw new BTSLBaseException("OperatorUtil", "calculateAccessFee", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("calculateAccessFee", "Exiting with calculatedAccessFee=" + calculatedAccessFee);
        return calculatedAccessFee;
    }

    /**
     * Method calculateCardGroupTax1.
     * 
     * @param p_type
     *            String
     * @param p_rate
     *            double
     * @param p_requestValue
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.selftopup.pretups.util.OperatorUtilI#calculateCardGroupTax1(String,
     *      double, long)
     */
    public long calculateCardGroupTax1(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException {
        long calculatedTax1Value = 0;
        if (p_type.equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT))
            calculatedTax1Value = (long) p_rate;
        else if (p_type.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE))
            calculatedTax1Value = (long) ((p_rate * p_requestValue) / (100 + p_rate));
        else {
            if (_log.isDebugEnabled())
                _log.debug("calculateCardGroupTax1()", "Exception p_type is not define in the system p_type=" + p_type);
            throw new BTSLBaseException(this, "calculateCardGroupTax1", "error.invalid.ratetype");
        }
        return calculatedTax1Value;
    }

    /**
     * Method calculateCardGroupTax2.
     * 
     * @param p_type
     *            String
     * @param p_rate
     *            double
     * @param p_requestValue
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.selftopup.pretups.util.OperatorUtilI#calculateCardGroupTax2(String,
     *      double, long)
     */
    public long calculateCardGroupTax2(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException {
        long calculatedTax2Value = 0;
        if (p_type.equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT))
            calculatedTax2Value = (long) p_rate;
        else if (p_type.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE))
            calculatedTax2Value = (long) ((p_rate * p_requestValue) / (100));
        else {
            if (_log.isDebugEnabled())
                _log.debug("calculateCardGroupTax2()", "Exception p_type is not define in the system p_type=" + p_type);
            throw new BTSLBaseException(this, "calculateCardGroupTax2", "error.invalid.ratetype");
        }
        return calculatedTax2Value;
    }

    /**
     * Method calculateCardGroupBonus.
     * 
     * @param p_type
     *            String
     * @param p_rate
     *            double
     * @param p_requestValue
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.selftopup.pretups.util.OperatorUtilI#calculateCardGroupBonus(String,
     *      double, long)
     */
    public long calculateCardGroupBonus(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException {
        long calculatedBonusValue = 0;
        if (p_type.equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT))
            calculatedBonusValue = (long) p_rate;
        else if (p_type.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE))
            calculatedBonusValue = (long) ((p_requestValue * ((double) (p_rate) / (double) 100)));
        else {
            if (_log.isDebugEnabled())
                _log.debug("calculateCardGroupBonus()", "Exception p_type is not define in the system p_type=" + p_type);
            throw new BTSLBaseException(this, "calculateCardGroupBonus", "error.invalid.ratetype");
        }
        return calculatedBonusValue;
    }

    /**
     * Method calculateSenderTransferValue.
     * 
     * @param p_requestedValue
     *            long
     * @param p_calculatedTax1Value
     *            long
     * @param p_calculatedTax2Value
     *            long
     * @param p_calculatedAccessFee
     *            long
     * @return long
     * @see com.selftopup.pretups.util.OperatorUtilI#calculateSenderTransferValue(long,
     *      long, long, long)
     */
    public long calculateSenderTransferValue(long p_requestedValue, long p_calculatedTax1Value, long p_calculatedTax2Value, long p_calculatedAccessFee) {
        long transferValue = p_requestedValue + p_calculatedTax1Value + p_calculatedTax2Value + p_calculatedAccessFee;
        return transferValue;
    }

    /**
     * Method calculateReceiverTransferValue.
     * 
     * @param p_requestedValue
     *            long
     * @param p_calculatedAccessFee
     *            long
     * @param p_calculatedTax1Value
     *            long
     * @param p_calculatedTax2Value
     *            long
     * @param p_calculatedBonusTalkTimeValue
     *            long
     * @return long
     * @see com.selftopup.pretups.util.OperatorUtilI#calculateReceiverTransferValue(long,
     *      long, long, long, long)
     */
    public long calculateReceiverTransferValue(long p_requestedValue, long p_calculatedAccessFee, long p_calculatedTax1Value, long p_calculatedTax2Value, long p_calculatedBonusTalkTimeValue) {
        long transferValue = p_requestedValue - p_calculatedAccessFee - p_calculatedTax1Value - p_calculatedTax2Value + p_calculatedBonusTalkTimeValue;
        return transferValue;
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
     * @see com.selftopup.pretups.util.OperatorUtilI#calculateValidity(TransferVO,
     *      Date, Date, String, int, int)
     */
    public void calculateValidity(TransferVO p_transferVO, Date p_transferDateTime, Date p_previousExpiry, String p_valPeriodType, int p_validityPeriod, int p_bonusValidity) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("calculateValidity", "Entered with p_transferDateTime=" + p_transferDateTime + " p_previousExpiry=" + p_previousExpiry + " p_valPeriodType=" + p_valPeriodType + " p_validityPeriod=" + p_validityPeriod + " p_bonusValidity=" + p_bonusValidity);
        try {
            java.util.Date newDate = null;
            // set the receiver Validity by adding the bonus validity.
            p_transferVO.setReceiverValidity(p_validityPeriod + p_bonusValidity);

            // Today + no of days or Previous Expiry which ever is higher
            if (p_previousExpiry != null && p_valPeriodType.equals(PretupsI.VALPERIOD_HIGHEST_TYPE)) {
                newDate = BTSLUtil.addDaysInUtilDate(p_transferDateTime, p_validityPeriod + p_bonusValidity);
                if (newDate.after(p_previousExpiry))
                    p_transferVO.setValidityDateToBeSet(newDate);
                else
                    p_transferVO.setValidityDateToBeSet(p_previousExpiry);
            }
            // Today + no of days or Previous Expiry + no of days which ever is
            // higher
            else if (p_previousExpiry != null && p_valPeriodType.equals(PretupsI.VALPERIOD_CUMMULATIVE_TYPE)) {
                newDate = BTSLUtil.addDaysInUtilDate(p_transferDateTime, p_validityPeriod + p_bonusValidity);
                java.util.Date newDate2 = null;
                newDate2 = BTSLUtil.addDaysInUtilDate(p_previousExpiry, p_validityPeriod + p_bonusValidity);
                if (newDate2.after(newDate))
                    p_transferVO.setValidityDateToBeSet(newDate2);
                else
                    p_transferVO.setValidityDateToBeSet(newDate);
            }
            // Today + no of days
            else if (p_valPeriodType.equals(PretupsI.VALPERIOD_LOWEST_TYPE)) {
                newDate = BTSLUtil.addDaysInUtilDate(p_transferDateTime, p_validityPeriod + p_bonusValidity);
                p_transferVO.setValidityDateToBeSet(newDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CardGroupBL[calculateValidity]", p_transferVO.getTransferID(), p_transferVO.getReceiverMsisdn(), " ", "Not able to calculate the new validity period getting Exception=" + e.getMessage());
            throw new BTSLBaseException("CardGroupBL", "calculateValidity", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("calculateValidity", "Exiting");
    }

    /**
     * Method formatP2PTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.selftopup.pretups.util.OperatorUtilI#formatP2PTransferID(TransferVO,
     *      long)
     */
    public String formatP2PTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        String returnStr = null;
        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // String currentYear=BTSLUtil.getFinancialYearLastDigits(2);
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), P2P_TRANSFER_ID_PAD_LENGTH);
            // returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr;
            // returnStr="C"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+paddedTransferIDStr;
            returnStr = "C" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants.getProperty("INSTANCE_ID") + paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[]", "", "", "", "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method currentDateTimeFormatString.
     * 
     * @param p_date
     *            Date
     * @return String
     * @throws ParseException
     */
    public String currentDateTimeFormatString(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String dateString = sdf.format(p_date);
        return dateString;
    }

    /**
     * Method currentTimeFormatString.
     * 
     * @param p_date
     *            Date
     * @return String
     * @throws ParseException
     */
    public String currentTimeFormatString(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        String dateString = sdf.format(p_date);
        return dateString;
    }

    /**
     * Method to set the value for transfer. This methos is called from
     * CardGroupBL. At this time this method set various values in the
     * transferVO For any other operator who wants to run system on CVG mode, we
     * can change values setting based on subservice. added for CRE_INT_CR00029
     * by ankit Zindal
     * 
     * @param p_subService
     *            String
     * @param p_cardGroupDetailVO
     *            CardGroupDetailsVO
     * @param p_transferVO
     *            TransferVO
     * 
     * @throws Exception
     * @see com.selftopup.pretups.util.OperatorUtilI#setCalculatedCardGroupValues(String,
     *      CardGroupDetailsVO, TransferVO)
     */
    public void setCalculatedCardGroupValues(String p_subService, CardGroupDetailsVO p_cardGroupDetailVO, TransferVO p_transferVO) throws Exception {

        try {
            /**
             * In case of CVG all values are set as calculated. In case of VG
             * transfer value is set to 0. In case of C, validity and grace will
             * be set to 0.
             * 
             */
            TransferItemVO transferItemVO = null;
            int bonusValidityValue = Integer.parseInt(String.valueOf(p_cardGroupDetailVO.getBonusValidityValue()));
            int validityPeriodValue = p_cardGroupDetailVO.getValidityPeriod();
            long transferValue = p_cardGroupDetailVO.getTransferValue();
            long bonusValue = p_cardGroupDetailVO.getBonusTalkTimeValue();
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
             * int amountDeducted; if(transferItemVO.isNumberBackAllowed()) {
             * amountDeducted= transferItemVO.getAmountDeducted();
             * transferValue=transferValue-amountDeducted;
             * if(!(transferValue>0)) throw new
             * BTSLBaseException(this,"setCalculatedCardGroupValues",
             * PretupsErrorCodesI.TRANSFER_VALUE_IS_NOT_VALID);
             * p_cardGroupDetailVO.setTransferValue(transferValue);
             * p_transferVO.setReceiverAccessFee(p_transferVO.getReceiverAccessFee
             * () +
             * amountDeducted); } }
             */

            if ((String.valueOf(PretupsI.CHNL_SELECTOR_CVG_VALUE)).equals(p_subService))// CVG
            {
                p_transferVO.setReceiverBonusValidity(bonusValidityValue);
                p_transferVO.setReceiverGracePeriod(p_cardGroupDetailVO.getGracePeriod());
                p_transferVO.setReceiverValidity(validityPeriodValue);
                // Is Bonus Validity on Requested Value ??
                calculateValidity(p_transferVO, transferItemVO.getTransferDateTime(), transferItemVO.getPreviousExpiry(), p_cardGroupDetailVO.getValidityPeriodType(), validityPeriodValue, bonusValidityValue);
                p_transferVO.setReceiverTransferValue(transferValue);
                transferItemVO.setTransferValue(transferValue);
                transferItemVO.setGraceDaysStr(String.valueOf(p_cardGroupDetailVO.getGracePeriod()));
                transferItemVO.setValidity(validityPeriodValue);
                p_transferVO.setReceiverBonusValue(bonusValue);
            }
            if ((String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE)).equals(p_subService))// C
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
            if ((String.valueOf(PretupsI.CHNL_SELECTOR_VG_VALUE)).equals(p_subService))// VG
            {
                p_transferVO.setReceiverBonusValidity(bonusValidityValue);
                p_transferVO.setReceiverGracePeriod(p_cardGroupDetailVO.getGracePeriod());
                p_transferVO.setReceiverValidity(validityPeriodValue);
                // Is Bonus Validity on Requested Value ??
                calculateValidity(p_transferVO, transferItemVO.getTransferDateTime(), transferItemVO.getPreviousExpiry(), p_cardGroupDetailVO.getValidityPeriodType(), validityPeriodValue, bonusValidityValue);
                p_transferVO.setReceiverTransferValue(0);
                transferItemVO.setTransferValue(0);
                transferItemVO.setGraceDaysStr(String.valueOf(p_cardGroupDetailVO.getGracePeriod()));
                transferItemVO.setValidity(validityPeriodValue);
                p_transferVO.setReceiverBonusValue(0);
            } else // PRCMDA
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

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * This method will convert operator specific msisdn to system specific
     * msisdn.
     * 
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     */
    public String getSystemFilteredMSISDN(String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getSystemFilteredMSISDN", "Entered p_msisdn:" + p_msisdn);
        String msisdn = null;
        boolean prefixFound = false;
        String prefix = null;
        try {

            if (p_msisdn.length() > SystemPreferences.MIN_MSISDN_LENGTH) {
                if (_log.isDebugEnabled())
                    _log.debug("getSystemFilteredMSISDN", "(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST):" + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST));

                StringTokenizer strTok = new StringTokenizer((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST), ",");
                while (strTok.hasMoreTokens()) {
                    prefix = strTok.nextToken();
                    if (p_msisdn.startsWith(prefix, 0)) {
                        prefixFound = true;
                        break;
                    } else
                        continue;
                }
                if (prefixFound)
                    msisdn = p_msisdn.substring(prefix.length());
                else
                    msisdn = p_msisdn;
            } else
                msisdn = p_msisdn;
        } catch (Exception e) {
            _log.error("getSystemFilteredMSISDN", "Exception while getting the mobile no from passed no=" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[getSystemFilteredMSISDN]", "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getSystemFilteredMSISDN", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getSystemFilteredMSISDN", "Exiting Filtered msisdn=" + msisdn);
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
        return Constants.getProperty("COUNTRY_CODE") + p_msisdn;
    }

    /**
     * Check After Payment Method
     * 
     * @param p_con
     * @param i
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void checkAfterPaymentMethod(Connection p_con, int i, String[] p_requestMessageArray, StringBuffer incomingSmsStr, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("checkAfterPaymentMethod", " i=" + i + " requestMessageArray length:" + p_requestMessageArray.length);
        String receiverMSISDN = p_requestMessageArray[i];
        receiverMSISDN = addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverMSISDN));
        if (!BTSLUtil.isValidMSISDN(receiverMSISDN)) {
            throw new BTSLBaseException(this, "checkAfterPaymentMethod", SelfTopUpErrorCodesI.ERROR_INVALID_MSISDN, 0, new String[] { receiverMSISDN }, null);
        }
        // This block will check if the user is sending the PIN but is also a
        // buddy then that request should go through
        /*
         * BuddyVO buddyVO=new
         * SubscriberDAO().loadBuddyDetails(p_con,((SenderVO)p_transferVO.
         * getSenderVO()).getUserID(),receiverMSISDN);
         * if(buddyVO!=null) { receiverMSISDN=buddyVO.getMsisdn();
         * incomingSmsStr.append(receiverMSISDN+" "); NetworkPrefixVO
         * networkPrefixVO=PretupsBL.getNetworkDetails(receiverMSISDN,PretupsI.
         * USER_TYPE_RECEIVER);
         * if(networkPrefixVO==null) throw new
         * BTSLBaseException("","parseRequest",PretupsErrorCodesI.
         * ERROR_NOTFOUND_RECEIVERNETWORK,0,new
         * String[]{receiverMSISDN},null);
         * buddyVO.setNetworkCode(networkPrefixVO.getNetworkCode());
         * buddyVO.setPrefixID(networkPrefixVO.getPrefixID());
         * buddyVO.setSubscriberType(networkPrefixVO.getSeriesType());
         * p_transferVO.setReceiverVO(buddyVO); long amount=0;
         * amount=PretupsBL.getSystemAmount(p_requestMessageArray[i+1]);
         * if(amount<0) throw new
         * BTSLBaseException("","parseRequest",PretupsErrorCodesI.
         * P2P_ERROR_AMOUNT_LESSZERO);
         * p_transferVO.setTransferValue(amount);
         * p_transferVO.setRequestedAmount(amount);
         * incomingSmsStr.append(amount+" ");
         * } else {
         */
        incomingSmsStr.append(receiverMSISDN + " ");
        ReceiverVO _receiverVO = new ReceiverVO();
        _receiverVO.setMsisdn(receiverMSISDN);
        NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
        if (networkPrefixVO == null)
            throw new BTSLBaseException(this, "checkAfterPaymentMethod", SelfTopUpErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
        _receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
        _receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        p_transferVO.setReceiverVO(_receiverVO);
        long amount = 0;
        amount = PretupsBL.getSystemAmount(p_requestMessageArray[i + 1]);
        if (amount < 0)
            throw new BTSLBaseException(this, "checkAfterPaymentMethod", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
        p_transferVO.setTransferValue(amount);
        p_transferVO.setRequestedAmount(amount);
        incomingSmsStr.append(amount + " ");
        // }
    }

    /**
     * Check If Buddy
     * 
     * @param p_con
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean checkIfBuddy(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        String[] requestMessageArray = p_requestVO.getRequestMessageArray();
        if (_log.isDebugEnabled())
            _log.debug("checkIfBuddy", " requestMessageArray length:" + requestMessageArray.length);
        String serviceKeyword = requestMessageArray[0];
        String senderSubscriberType = ((SenderVO) p_transferVO.getSenderVO()).getSubscriberType();
        boolean cBuddy = false;
        StringBuffer incomingSmsStr = new StringBuffer(serviceKeyword + " ");
        if (requestMessageArray.length < 2 || requestMessageArray.length > 7)
            throw new BTSLBaseException(this, "checkIfBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);

        // if receiver buddy
        // Validate 2nd Argument for Payment Method Keyword.
        String paymentMethodKeyword = requestMessageArray[1];

        // if paymentMethod invalid , Validate 2nd Argument for Receiver
        // No(MSISDN).
        PaymentMethodKeywordVO paymentMethodKeywordVO = PaymentMethodCache.getObject(paymentMethodKeyword, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());
        String paymentMethodType = null;
        if (paymentMethodKeywordVO == null) {
            paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
            if (paymentMethodType == null) {
                // return with error message, no default payment method defined
                throw new BTSLBaseException(this, "checkIfBuddy", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            }
            p_transferVO.setPaymentMethodType(paymentMethodType);
            p_transferVO.setDefaultPaymentMethod("Y");
            incomingSmsStr.append(paymentMethodType + " ");
            cBuddy = checkAfterPaymentMethodForBuddy(p_con, 1, requestMessageArray, incomingSmsStr, p_transferVO, p_requestVO);
        } else {
            paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
            p_transferVO.setPaymentMethodType(paymentMethodType);
            p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
            p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
            incomingSmsStr.append(paymentMethodType + " ");
            // if paymentMethod valid , Validate 3rd Argument for Receiver
            // No(MSISDN).
            if (requestMessageArray.length < 3)
                throw new BTSLBaseException(this, "checkIfBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);

            cBuddy = checkAfterPaymentMethodForBuddy(p_con, 2, requestMessageArray, incomingSmsStr, p_transferVO, p_requestVO);
        }
        p_transferVO.setIncomingSmsStr(incomingSmsStr.toString());
        if (_log.isDebugEnabled())
            _log.debug("checkIfBuddy", " return value:" + cBuddy);
        return cBuddy;
    }

    /**
     * Check After Payment Method For Buddy
     * 
     * @param p_con
     * @param i
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    private boolean checkAfterPaymentMethodForBuddy(Connection p_con, int i, String[] p_requestMessageArray, StringBuffer incomingSmsStr, TransferVO p_transferVO, RequestVO p_requestVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("checkAfterPaymentMethodForBuddy", " i=" + i + " requestMessageArray length:" + p_requestMessageArray.length + " i=" + i);
        int incReq = 0;
        if (i == 2)
            incReq = 1;
        String receiverMSISDN_NAME = p_requestMessageArray[1 + incReq];
        BuddyVO buddyVO = new SubscriberDAO().loadBuddyDetails(p_con, ((SenderVO) p_transferVO.getSenderVO()).getUserID(), receiverMSISDN_NAME);
        if (buddyVO == null) {
            return false;
        }
        String receiverMSISDN = buddyVO.getMsisdn();
        NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
        if (networkPrefixVO == null)
            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
        buddyVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        buddyVO.setPrefixID(networkPrefixVO.getPrefixID());
        buddyVO.setSubscriberType(networkPrefixVO.getSeriesType());
        p_transferVO.setReceiverVO(buddyVO);
        incomingSmsStr.append(receiverMSISDN_NAME + " ");
        int messageLength = p_requestMessageArray.length;
        String pin = null;
        long amount = 0;
        SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
        String actualPin = BTSLUtil.decryptText(senderVO.getPin());

        /*
         * Message format that are supported are as: Message length 2: PRC Name
         * If pin required the PIN should be default Amount will always be
         * default Message length 3: PIN required and pin is not default PRC
         * Name PIN (PIN required and actual pin=default pin) OR PIN not
         * required PRC HDFC Name PRC Name Amount Message length 4: PIN Required
         * actual pin!=default pin PRC Name Amount PIN PRC HDFC NAme PIN
         * actual=default PRC HDFC Name Amount PRC Name Amount langCode PIN not
         * required PRC HDFC name Amount PRC Name Amount langCode Message length
         * 5: PIN Required actual!=default PRC HDFC Name Amount PIN PRC Name
         * Amount langCode PIN Actual=default PRC HDFC Name Amount langCode PRC
         * Name Amount selector lang PIN not required PRC HDFC Name Amount
         * LangCode PRC Name Amount selector LangCode Message length 6: PIN
         * Required Actual!=default PRC HDFC Name Amount langCode PIN PRC Name
         * Amount selector langCode PIN Actual=default PRC HDFC Name Amount
         * selector langCode PRC Name Amount selector langCode PIN(Update with
         * new PIN) PIN not required PRC HDFC Name Amount selector langCode PRC
         * Name Amount Selector langCode PIN( Update with new PIN) Message
         * length 7: PIN Required Actual!=default PRC HDFC Name Amount selector
         * langCode PIN Actual=default PRC HDFC Name Amount selector langCode
         * PIN(Update with new PIN) PIN not required PRC HDFC Name Amount
         * selector langCode PIN (Update with new PIN)
         */
        switch (messageLength) {
        case 2: {
            if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                // whether PIN validation is required or not.
                if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                    if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                }
            }
            amount = buddyVO.getPreferredAmount();
            if (amount < 0)
                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
            p_transferVO.setTransferValue(amount);
            p_transferVO.setRequestedAmount(amount);
            incomingSmsStr.append(amount + " ");
            break;
        }
        case 3: {
            // if((((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())&&!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
            if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && ((((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) && !actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                if (i == 2)
                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                else {
                    pin = p_requestMessageArray[2];
                    incomingSmsStr.append("****" + " ");
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                    amount = buddyVO.getPreferredAmount();
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                }
            } else {
                if (i == 2) {
                    amount = buddyVO.getPreferredAmount();
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                } else {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            }
            break;
        }
        case 4: {
            if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                // if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                    pin = p_requestMessageArray[3];
                    incomingSmsStr.append("****" + " ");
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                    if (i == 2) {
                        amount = buddyVO.getPreferredAmount();
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    } else {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);

                    }
                } else {
                    if (i == 2) {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    } else {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[3]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    }
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            } else {
                if (i == 2) {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                } else {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {

                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            }
            break;
        }
        case 5: {
            if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                // if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                    pin = p_requestMessageArray[4];
                    incomingSmsStr.append("****" + " ");
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                    if (i == 2) {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    } else {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[3]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    }
                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                } else {
                    if (i == 2) {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } else {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);

                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    }
                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                }
            } else {
                if (i == 2) {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                } else {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                    if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                        int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                        p_requestVO.setReqSelector("" + selectorValue);
                    }
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            }
            break;
        }
        case 6: {
            if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                // if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                    pin = p_requestMessageArray[5];
                    incomingSmsStr.append("****" + " ");
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                    if (i == 2) {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } else {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    }
                } else {
                    if (i == 2) {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    } else {
                        pin = p_requestMessageArray[5];
                        incomingSmsStr.append("****" + " ");
                        validatePIN(pin);
                        senderVO.setPin(BTSLUtil.encryptText(pin));
                        senderVO.setPinUpdateReqd(true);

                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    }
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            } else {
                if (i == 2) {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                        int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                        p_requestVO.setReqSelector("" + selectorValue);
                    }
                } else {
                    // To check whether PIN validation is required or not.
                    if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                        if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            pin = p_requestMessageArray[5];
                            incomingSmsStr.append("****" + " ");
                            validatePIN(pin);
                            senderVO.setPin(BTSLUtil.encryptText(pin));
                            senderVO.setPinUpdateReqd(true);
                        }
                    }

                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                    if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                        int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                        p_requestVO.setReqSelector("" + selectorValue);
                    }
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            }
            break;
        }
        case 7: {
            /*
             * Actual!=default PRC HDFC Name Amount selector langCode PIN
             * Actual=default PRC HDFC Name Amount selector langCode PIN(Update
             * with new PIN)
             */
            if (i == 1)
                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            else {
                // if(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                    if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        pin = p_requestMessageArray[6];
                        incomingSmsStr.append("****" + " ");
                        try {
                            SubscriberBL.validatePIN(p_con, senderVO, pin);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }

                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    } else {
                        // To check whether PIN validation is required or not.
                        if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                            pin = p_requestMessageArray[6];
                            incomingSmsStr.append("****" + " ");
                            validatePIN(pin);
                            senderVO.setPin(BTSLUtil.encryptText(pin));
                            senderVO.setPinUpdateReqd(true);
                        }

                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    }
                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                } else {
                    // To check whether PIN validation is required or not.
                    if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                        if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            pin = p_requestMessageArray[6];
                            incomingSmsStr.append("****" + " ");
                            validatePIN(pin);
                            senderVO.setPin(BTSLUtil.encryptText(pin));
                            senderVO.setPinUpdateReqd(true);
                        }
                    }

                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                        int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                        p_requestVO.setReqSelector("" + selectorValue);
                    }

                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                }
                break;
            }
        }
        default: {
            throw new BTSLBaseException(this, "checkIfBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
        }

        }
        return true;
    }

    /**
     * This method used for Password validation. While creating or modifying the
     * user Password This method will be used. Method validatePassword.
     * 
     * @author sanjeew.kumar
     * @created on 12/07/07
     * @param p_loginID
     *            String
     * @param p_password
     *            String
     * @return HashMap
     */
    public HashMap validatePassword(String p_loginID, String p_password) {
        _log.debug("validatePassword", "Entered, p_userID= ", new String(p_loginID + ", Password= " + p_password));
        HashMap messageMap = new HashMap();

        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_password);

        if (defaultPin.equals(p_password))
            return messageMap;
        defaultPin = BTSLUtil.getDefaultPasswordText(p_password);

        if (defaultPin.equals(p_password))
            return messageMap;
        if (p_password.length() < SystemPreferences.MIN_LOGIN_PWD_LENGTH || p_password.length() > SystemPreferences.MAX_LOGIN_PWD_LENGTH) {
            String[] args = { String.valueOf(SystemPreferences.MIN_LOGIN_PWD_LENGTH), String.valueOf(SystemPreferences.MAX_LOGIN_PWD_LENGTH) };
            messageMap.put("operatorutil.validatepassword.error.passwordlenerr", args);
        }
        int result = BTSLUtil.isSMSPinValid(p_password);// for consecutive and
        // same characters
        if (result == -1)
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit", null);
        else if (result == 1)
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutive", null);

        // For OCI Password Should contains atleast one character
        if (!BTSLUtil.containsChar(p_password))
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontainschar", null);
        // for special character String
        String specialChar = Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
        if (!BTSLUtil.isNullString(specialChar)) {
            String[] specialCharArray = { specialChar };
            String[] passwordCharArray = specialChar.split(",");
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

        String[] passwordNumberStrArray = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
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
        if (!BTSLUtil.containsCapChar(p_password)) {
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontaincapschar", null);
        }

        if (_log.isDebugEnabled())
            _log.debug("validatePassword", "Exiting messageMap.size()=" + messageMap.size());
        return messageMap;
    }

    /**
     * This method used for pin validation. While creating or modifying the user
     * PIN This method will be used. Method validatePIN.
     * 
     * @author sanjeew.kumar
     * @created on 19/07/07
     * @param p_pin
     *            String
     * @return HashMap
     */
    public HashMap pinValidate(String p_pin) {
        _log.debug("validatePIN", "Entered, PIN= " + p_pin);
        HashMap messageMap = new HashMap();

        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_pin);
        if (defaultPin.equals(p_pin))
            return messageMap;

        defaultPin = BTSLUtil.getDefaultPasswordText(p_pin);
        if (defaultPin.equals(p_pin))
            return messageMap;

        if (!BTSLUtil.isNumeric(p_pin))
            messageMap.put("operatorutil.validatepin.error.pinnotnumeric", null);
        if (p_pin.length() < SystemPreferences.MIN_SMS_PIN_LENGTH || p_pin.length() > SystemPreferences.MAX_SMS_PIN_LENGTH) {
            String[] args = { String.valueOf(SystemPreferences.MIN_SMS_PIN_LENGTH), String.valueOf(SystemPreferences.MAX_SMS_PIN_LENGTH) };
            messageMap.put("operatorutil.validatepin.error.smspinlenerr", args);
        }
        int result = BTSLUtil.isSMSPinValid(p_pin);
        if (result == -1)
            messageMap.put("operatorutil.validatepin.error.pinsamedigit", null);
        else if (result == 1)
            messageMap.put("operatorutil.validatepin.error.pinconsecutive", null);
        if (_log.isDebugEnabled())
            _log.debug("validatePIN", "Exiting messageMap.size()=" + messageMap.size());
        return messageMap;
    }

    public void validatePIN(Connection p_con, SenderVO p_senderVO, String p_requestPin) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validatePIN", "Entered with p_senderVO:" + p_senderVO.toString() + " p_requestPin=" + p_requestPin);
        int updateStatus = 0;
        boolean updatePinCount = false;
        boolean isUserBarred = false;
        try {
            // added changes regarding to change PIN on 1st request
            // if(p_senderVO.isForcePinCheckReqd() &&
            // (p_senderVO.getPinModifiedOn()==null ||
            // (p_senderVO.getPinModifiedOn().getTime())==(p_senderVO.getCreatedOn().getTime())))
            // throw new BTSLBaseException("OperatorUtil", "validatePIN",
            // PretupsErrorCodesI.CHNL_FIRST_REQUEST_PIN_CHANGE);

            if (_log.isDebugEnabled())
                _log.debug("validatePIN", "Modified Time=:" + p_senderVO.getModifiedOn() + " p_senderVO.getPinModifiedOn()=" + p_senderVO.getPinModifiedOn());
            if (p_senderVO.isForcePinCheckReqd() && p_senderVO.getPinModifiedOn() != null && ((p_senderVO.getModifiedOn().getTime() - p_senderVO.getPinModifiedOn().getTime()) / (24 * 60 * 60 * 1000)) > SystemPreferences.P2P_DAYS_AFTER_CHANGE_PIN) {
                if (_log.isDebugEnabled())
                    _log.debug("validatePIN", "Modified Time=:" + p_senderVO.getModifiedOn() + " p_senderVO.getPinModifiedOn()=" + p_senderVO.getPinModifiedOn() + " Difference=" + ((p_senderVO.getModifiedOn().getTime() - p_senderVO.getPinModifiedOn().getTime()) / (24 * 60 * 60 * 1000)));
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SubscriberBL[validatePIN]", "", p_senderVO.getMsisdn(), "", "Force User to change PIN after " + SystemPreferences.P2P_DAYS_AFTER_CHANGE_PIN + " days as last changed on " + p_senderVO.getPinModifiedOn());
                String strArr[] = { String.valueOf(SystemPreferences.P2P_DAYS_AFTER_CHANGE_PIN) };
                throw new BTSLBaseException("OperatorUtil", "validatePIN", SelfTopUpErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGEPIN, 0, strArr, null);
            } else {
                SubscriberDAO subscriberDAO = new SubscriberDAO();
                String decryptedPin = BTSLUtil.decryptText(p_senderVO.getPin());

                if (_log.isDebugEnabled())
                    _log.debug("validatePIN", "Sender MSISDN:" + p_senderVO.getMsisdn() + " decrypted PIN=" + decryptedPin + " p_requestPin=" + p_requestPin);

                // added for Change the default PIN
                if (p_senderVO.isForcePinCheckReqd() && (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(decryptedPin))
                    throw new BTSLBaseException("OperatorUtil", "validatePIN", SelfTopUpErrorCodesI.CHNLUSR_CHANGE_DEFAULT_PIN);
                // if (!decryptedPin.equalsIgnoreCase(p_requestPin))
                /*
                 * done by ashishT for checking the value in p_requestPin is
                 * hashvalue or actual value.
                 */
                boolean checkpin;
                if ("SHA".equalsIgnoreCase(SystemPreferences.PINPAS_EN_DE_CRYPTION_TYPE)) {
                    if (p_requestPin.length() > SystemPreferences.C2S_PIN_MAX_LENGTH)
                        checkpin = decryptedPin.equals(p_requestPin);
                    else
                        checkpin = (!PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(decryptedPin, p_requestPin)));
                } else {
                    checkpin = decryptedPin.equals(p_requestPin);
                }
                if (!checkpin) {
                    updatePinCount = true;
                    int mintInDay = 24 * 60;
                    if (p_senderVO.getFirstInvalidPinTime() != null) {
                        // Check if PIN counters needs to be reset after the
                        // reset duration
                        if (_log.isDebugEnabled())
                            _log.debug("validatePIN", "p_senderVO.getModifiedOn().getTime()=" + p_senderVO.getModifiedOn().getTime() + " p_senderVO.getFirstInvalidPinTime().getTime()=" + p_senderVO.getFirstInvalidPinTime().getTime() + " Diff=" + ((p_senderVO.getModifiedOn().getTime() - p_senderVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) + " Allowed=" + SystemPreferences.P2P_PIN_BLK_RST_DURATION);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(p_senderVO.getModifiedOn());
                        int d1 = cal.get(Calendar.DAY_OF_YEAR);
                        cal.setTime(p_senderVO.getFirstInvalidPinTime());
                        int d2 = cal.get(Calendar.DAY_OF_YEAR);
                        if (_log.isDebugEnabled())
                            _log.debug("validatePIN", "Day Of year of Modified On=" + d1 + " Day Of year of FirstInvalidPinTime=" + d2);
                        if (d1 != d2 && SystemPreferences.P2P_PIN_BLK_RST_DURATION <= mintInDay) {
                            // reset
                            p_senderVO.setPinBlockCount(1);
                            p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
                        } else if (d1 != d2 && SystemPreferences.P2P_PIN_BLK_RST_DURATION >= mintInDay && (d1 - d2) >= (SystemPreferences.P2P_PIN_BLK_RST_DURATION / mintInDay)) {
                            // Reset
                            p_senderVO.setPinBlockCount(1);
                            p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
                        } else if (((p_senderVO.getModifiedOn().getTime() - p_senderVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) < SystemPreferences.P2P_PIN_BLK_RST_DURATION) {
                            if (p_senderVO.getPinBlockCount() - SystemPreferences.P2P_MAX_PIN_BLOCK_COUNT == 0) {
                                // isStatusUpdate = true;
                                // p_senderVO.setStatus(PretupsI.USER_STATUS_BLOCK);
                                // Set The flag that indicates that we need to
                                // bar the user because of PIN Change
                                p_senderVO.setPinBlockCount(0);
                                // p_senderVO.setFirstInvalidPinTime(null);
                                isUserBarred = true;
                            } else
                                p_senderVO.setPinBlockCount(p_senderVO.getPinBlockCount() + 1);

                            if (p_senderVO.getPinBlockCount() == 0)
                                p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
                        } else {
                            p_senderVO.setPinBlockCount(1);
                            p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
                        }
                    } else {
                        p_senderVO.setPinBlockCount(1);
                        p_senderVO.setFirstInvalidPinTime(p_senderVO.getModifiedOn());
                    }
                } else {
                    // initilize PIN Counters if ifPinCount>0
                    if (p_senderVO.getPinBlockCount() > 0) {
                        p_senderVO.setPinBlockCount(0);
                        p_senderVO.setFirstInvalidPinTime(null);
                        updateStatus = subscriberDAO.updatePinStatus(p_con, p_senderVO, false);
                        if (updateStatus < 0) {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "", p_senderVO.getMsisdn(), "", "Not able to update invalid PIN count for users");
                            throw new BTSLBaseException("OperatorUtil", "validatePIN", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                        }
                    }
                }
                if (updatePinCount) {
                    updateStatus = subscriberDAO.updatePinStatus(p_con, p_senderVO, false);
                    if (updateStatus > 0 && !isUserBarred)
                        throw new BTSLBaseException("OperatorUtil", "validatePIN", SelfTopUpErrorCodesI.ERROR_INVALID_PIN);
                    else if (updateStatus > 0 && isUserBarred) {
                        p_senderVO.setBarUserForInvalidPin(true);
                        throw new BTSLBaseException("OperatorUtil", "validatePIN", SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK, 0, new String[] { String.valueOf(SystemPreferences.P2P_MAX_PIN_BLOCK_COUNT), String.valueOf(SystemPreferences.P2P_PIN_BLK_RST_DURATION) }, null);
                    } else if (updateStatus < 0) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "", p_senderVO.getMsisdn(), "", "Not able to update invalid PIN count for users");
                        throw new BTSLBaseException("OperatorUtil", "validatePIN", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
                    }
                }
            }
        } catch (BTSLBaseException bex) {
            throw bex;
        } catch (Exception e) {
            _log.error("validatePIN", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("OperatorUtil", "validatePIN", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validatePIN", "Exiting with increase Pin Count flag=" + updatePinCount + " Barred Update Flag:" + isUserBarred);
        }
    }

    /**
     * Method checkDisallowedServiceClass.
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_serviceType
     * @param p_serviceClass
     * @param p_module
     * @param p_userType
     * @throws BTSLBaseException
     * @return boolean
     */
    public boolean checkMsisdnServiceClassMapping(Connection p_con, String p_msisdn, String p_serviceType, String p_serviceClass, String p_module, String p_userType) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("checkMsisdndServiceClassMapping", "Entered with p_msisdn=" + p_msisdn + " p_serviceType=" + p_serviceType + " p_serviceClass=" + p_serviceClass + " p_module=" + p_module + " p_userType=" + p_userType);
        RoutingDAO routingDAO = new RoutingDAO();
        boolean isExist = false;
        // If the value of status is 'Y', it means database is searched for
        // allowed users of specified service class and service type.
        // If the value of status is 'N', it means database is searched for
        // disallowed users of specified service class and service type.
        String status = PretupsI.YES;
        try {
            isExist = routingDAO.isMsisdnServiceClassMapped(p_con, p_msisdn, p_serviceType, p_serviceClass, status, p_module, p_userType);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[checkMsisdnServiceClassMapping]", "", "", " ", "Not able to check whether msisdn is mapped with service type and service class Exception=" + e.getMessage());
            throw new BTSLBaseException("OperatorUtil", "checkMsisdnServiceClassMapping", SelfTopUpErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled())
            _log.debug("checkMsisdnServiceClassMapping", "Exiting with isExist=" + isExist);
        return isExist;
    }

    /**
     * This method is used to check whether the restricted MSISDN is Black
     * listed as a CP2P Pair or CP2P Payee or C2S Payee. Method
     * :isRestrictedSubscriberAllowed
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_msisdn
     *            String
     * @param p_blacklist_type
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isRestrictedSubscriberAllowed(Connection p_con, String p_msisdn, String p_senderMsisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isRestrictedSubscriberAllowed", "Entered with p_msisdn=" + p_msisdn + " p_senderMsisdn=" + p_senderMsisdn);
        HashMap map = new HashMap();
        RestrictedSubscriberVO restrictedSenderSubscriberVO = null;
        RestrictedSubscriberVO restrictedReceiverSubscriberVO = null;
        String errorcode = null;
        boolean isAllow = false;
        /*
         * if(PretupsI.CP2P_PAYER.equals(p_blacklist_type))
         * errorcode=PretupsErrorCodesI.
         * RM_ERROR_RESTRICTED_SUB_NOT_ALLOWED_CP2P_PAYER;
         * else if(PretupsI.CP2P_PAYEE.equals(p_blacklist_type))
         * errorcode=PretupsErrorCodesI.
         * RM_ERROR_RESTRICTED_SUB_NOT_ALLOWED_CP2P_PAYEE;
         * else
         * errorcode=PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUB_RECHARGE_NOT_ALLOWED
         * ;
         */
        errorcode = SelfTopUpErrorCodesI.RM_ERROR_RESTRICTED_SUB_RECHARGE_NOT_ALLOWED_P2P;
        try {
            RestrictedSubscriberDAO restrictedSubscriberDao = new RestrictedSubscriberDAO();
            map = restrictedSubscriberDao.loadRestrictedMsisdnDetail(p_con, p_msisdn, p_senderMsisdn);
            if (map != null && map.size() == 2) {
                if (_log.isDebugEnabled())
                    _log.debug("isRestrictedSubscriberAllowed", "Entered with map size 2=");
                restrictedSenderSubscriberVO = (RestrictedSubscriberVO) map.get(p_senderMsisdn);
                restrictedReceiverSubscriberVO = (RestrictedSubscriberVO) map.get(p_msisdn);
                // If CP2P_WITHIN_LIST is �N� for sender AND CP2P_PAYER is �N�
                // for sender AND CP2P_WITHIN_LIST is �N� AND CP2P_Payee is �N�
                // for receiver, allow credit transfer.
                if (PretupsI.STATUS_DELETE.equals(restrictedSenderSubscriberVO.getCp2pWithInList()) && PretupsI.STATUS_DELETE.equals(restrictedSenderSubscriberVO.getCp2pPayerStatus()) && PretupsI.STATUS_DELETE.equals(restrictedReceiverSubscriberVO.getCp2pWithInList()) && PretupsI.STATUS_DELETE.equals(restrictedReceiverSubscriberVO.getCp2pPayeeStatus())) {
                    if (_log.isDebugEnabled())
                        _log.debug("isRestrictedSubscriberAllowed", "If CP2P_WITHIN_LIST is �N� for sender AND CP2P_PAYER is �N� for sender AND CP2P_WITHIN_LIST is �N� AND CP2P_Payee is �N� for receiver");
                    isAllow = true;
                }

                // Else If both receiver and sender are in the same domain.
                else if (restrictedSenderSubscriberVO.getSubscriberDomainCode().equals(restrictedReceiverSubscriberVO.getSubscriberDomainCode())) {
                    if (_log.isDebugEnabled())
                        _log.debug("isRestrictedSubscriberAllowed", "both receiver and sender are in the same domain");
                    // a. If sender and receiver have the same owner.
                    if (restrictedSenderSubscriberVO.getOwnerID().equals(restrictedReceiverSubscriberVO.getOwnerID())) {
                        if (_log.isDebugEnabled())
                            _log.debug("isRestrictedSubscriberAllowed", "If sender and receiver have the same owner.");
                        /*
                         * i. Is CP2P-WITHIN_LIST is Y for both sender and
                         * receiver. 1. If Sender and receiver belong to the
                         * same parent allow Credit Transfer. 2. ELSE IF sender
                         * and receiver belong to different parent, check if
                         * CP2P_WITHIN_LIST_LEVEL is �O� for both sender and
                         * receiver, allow credit transfer between Sender and
                         * receiver. 3. ELSE reject the request
                         */

                        if (PretupsI.STATUS_ACTIVE.equals(restrictedSenderSubscriberVO.getCp2pWithInList()) && PretupsI.STATUS_ACTIVE.equals(restrictedReceiverSubscriberVO.getCp2pWithInList())) {
                            if (_log.isDebugEnabled())
                                _log.debug("isRestrictedSubscriberAllowed", "Is CP2P-WITHIN_LIST is Y for both sender and receiver.");
                            if (restrictedSenderSubscriberVO.getChannelUserID().equals(restrictedReceiverSubscriberVO.getChannelUserID())) {
                                isAllow = true;
                            } else if (!(restrictedSenderSubscriberVO.getChannelUserID().equals(restrictedReceiverSubscriberVO.getChannelUserID())) && (PretupsI.CP2P_WITHIN_LIST_LEVEL_OWNER.equals(restrictedReceiverSubscriberVO.getCp2pListLevel()) || PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN.equals(restrictedReceiverSubscriberVO.getCp2pListLevel())) && (PretupsI.CP2P_WITHIN_LIST_LEVEL_OWNER.equals(restrictedSenderSubscriberVO.getCp2pListLevel()) || PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN.equals(restrictedSenderSubscriberVO.getCp2pListLevel()))) {
                                isAllow = true;
                            }
                        }
                        // ii. If CP2P_WITHIN_LIST is �Y� for only sender AND
                        // CP2P_WITHIN_LIST_LEVEL is �O� for sender AND
                        // CP2P_WITHIN_LIST is �N� for receiver AND CP2P_Payee
                        // is N for receiver allow credit transfer.
                        else if (PretupsI.STATUS_ACTIVE.equals(restrictedSenderSubscriberVO.getCp2pWithInList()) && (PretupsI.CP2P_WITHIN_LIST_LEVEL_OWNER.equals(restrictedSenderSubscriberVO.getCp2pListLevel()) || PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN.equals(restrictedSenderSubscriberVO.getCp2pListLevel())) && PretupsI.STATUS_DELETE.equals(restrictedReceiverSubscriberVO.getCp2pWithInList()) && PretupsI.STATUS_DELETE.equals(restrictedReceiverSubscriberVO.getCp2pPayeeStatus())) {
                            isAllow = true;
                        }
                        // iii. If CP2P_WITHIN_LIST is �N� for sender AND
                        // CP2P_PAYER is �Y� for sender reject the request
                        // already done default value false
                        // iv. ELSE IF CP2P_WITHIN_LIST is �N� for sender AND
                        // CP2P_PAYER is �N� AND CP2P_WITHIN_LIST is �Y� AND
                        // CP2P_WITHIN_LIST_LEVEL is �O� for receiver, allow
                        // credit transfer.
                        else if (PretupsI.STATUS_DELETE.equals(restrictedSenderSubscriberVO.getCp2pWithInList()) && PretupsI.STATUS_DELETE.equals(restrictedSenderSubscriberVO.getCp2pPayerStatus()) && PretupsI.STATUS_ACTIVE.equals(restrictedReceiverSubscriberVO.getCp2pWithInList()) && (PretupsI.CP2P_WITHIN_LIST_LEVEL_OWNER.equals(restrictedReceiverSubscriberVO.getCp2pListLevel()) || PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN.equals(restrictedReceiverSubscriberVO.getCp2pListLevel()))) {
                            isAllow = true;
                        }
                    } else {
                        /*
                         * i. Is CP2P-WITHIN_LIST is Y for both sender and
                         * receiver. 1. IF sender and receiver belong to
                         * different parent, check if CP2P_WITHIN_LIST_LEVEL is
                         * �D� for both sender and receiver, allow credit
                         * transfer between Sender and receiver. 2. ELSE reject
                         * the request
                         */
                        if (PretupsI.STATUS_ACTIVE.equals(restrictedSenderSubscriberVO.getCp2pWithInList()) && PretupsI.STATUS_ACTIVE.equals(restrictedReceiverSubscriberVO.getCp2pWithInList())) {
                            if (_log.isDebugEnabled())
                                _log.debug("isRestrictedSubscriberAllowed", "Is CP2P-WITHIN_LIST is Y for both sender and receiver.");

                            if (!(restrictedSenderSubscriberVO.getChannelUserID().equals(restrictedReceiverSubscriberVO.getChannelUserID())) && PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN.equals(restrictedReceiverSubscriberVO.getCp2pListLevel()) && PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN.equals(restrictedSenderSubscriberVO.getCp2pListLevel())) {
                                isAllow = true;
                            }
                        }
                        // If CP2P_WITHIN_LIST is �Y� for only sender AND
                        // CP2P_WITHIN_LIST_LEVEL is �D� for sender AND
                        // CP2P_WITHIN_LIST is �N� for receiver AND CP2P_Payee
                        // is N for receiver allow credit transfer.
                        else if (PretupsI.STATUS_ACTIVE.equals(restrictedSenderSubscriberVO.getCp2pWithInList()) && PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN.equals(restrictedSenderSubscriberVO.getCp2pListLevel()) && PretupsI.STATUS_DELETE.equals(restrictedReceiverSubscriberVO.getCp2pWithInList()) && PretupsI.STATUS_DELETE.equals(restrictedReceiverSubscriberVO.getCp2pPayeeStatus())) {
                            isAllow = true;
                        }
                        // ELSE IF CP2P_WITHIN_LIST is �N� for sender AND
                        // CP2P_PAYER is �N� AND CP2P_WITHIN_LIST is �Y� for
                        // receiver AND CP2P_WITHIN_LIST_LEVEL is �D� for
                        // receiver, allow credit transfer.
                        else if (PretupsI.STATUS_DELETE.equals(restrictedSenderSubscriberVO.getCp2pWithInList()) && PretupsI.STATUS_DELETE.equals(restrictedSenderSubscriberVO.getCp2pPayerStatus()) && PretupsI.STATUS_ACTIVE.equals(restrictedReceiverSubscriberVO.getCp2pWithInList()) && PretupsI.CP2P_WITHIN_LIST_LEVEL_DOMAIN.equals(restrictedReceiverSubscriberVO.getCp2pListLevel())) {
                            isAllow = true;
                        }
                    }
                } else {
                    // b. If CP2P_Payer is �N� for sender AND CP2P_PAYEE is �N�
                    // for receiver allowthe request.
                    if (PretupsI.STATUS_DELETE.equals(restrictedSenderSubscriberVO.getCp2pPayerStatus()) && PretupsI.STATUS_DELETE.equals(restrictedReceiverSubscriberVO.getCp2pPayeeStatus())) {
                        if (_log.isDebugEnabled())
                            _log.debug("isRestrictedSubscriberAllowed", "If CP2P_Payer is �N� for sender AND CP2P_PAYEE is �N� for receiver allowthe request.");
                        isAllow = true;
                    }
                }
            }
            if (map != null && map.size() == 1) {
                restrictedSenderSubscriberVO = (RestrictedSubscriberVO) map.get(p_senderMsisdn);
                restrictedReceiverSubscriberVO = (RestrictedSubscriberVO) map.get(p_msisdn);
                if (restrictedSenderSubscriberVO != null && PretupsI.STATUS_DELETE.equals(restrictedSenderSubscriberVO.getCp2pPayerStatus())) {
                    isAllow = true;
                }
                if (restrictedReceiverSubscriberVO != null && PretupsI.STATUS_DELETE.equals(restrictedReceiverSubscriberVO.getCp2pPayeeStatus())) {
                    isAllow = true;
                }

            }
            if (map != null && map.size() == 0) {
                isAllow = true;
            }
            if (!isAllow) {
                throw new BTSLBaseException(this, "isRestrictedSubscriberAllowed", errorcode);
            }
        } catch (BTSLBaseException be) {
            _log.error("isRestrictedSubscriberAllowed", "BTSLBaseException : " + be);
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            _log.error("isRestrictedSubscriberAllowed", "Exception : " + e);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[isRestrictedSubscriberAllowed]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("OperatorUtil", "isRestrictedSubscriberAllowed", SelfTopUpErrorCodesI.RM_ERROR_RESTRICTED_SUB_EXCEPTION_UB);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("isRestrictedSubscriberAllowed", "returning isAllow=" + isAllow + map.size());
        }
        return isAllow;
    }

    /**
     * 
     * This method validates the requested PIN business rules
     * 
     * @param p_requestPin
     * @throws BTSLBaseException
     * @author santanu.sharma
     */
    public void validatePINRules(String p_requestPin) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validatePINRules", "Entered with p_requestPin=" + p_requestPin);
        validatePIN(p_requestPin);
        if (_log.isDebugEnabled())
            _log.debug("validatePINRules", "Exiting from OperatorUtil ");
    }

    /**
     * Method to generate randomPin.
     * 
     * @return String
     * @see com.selftopup.pretups.util.OperatorUtil#randomPinGenerate()
     */

    public String randomPinGenerate() {
        return (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN);
    }

    /**
     * Method to generate random Password.
     * 
     * @return String
     * @see com.selftopup.pretups.util.OperatorUtil#randomPwdGenerate()
     */

    private static int rand(int lo, int hi) {
        int n = hi - lo + 1;
        int i = rn.nextInt() % n;
        if (i < 0)
            i = -i;
        return lo + i;
    }

    private static String randomCapString() {
        int n = rand(1, 1);
        byte b[] = new byte[n];
        for (int i = 0; i < n; i++)
            b[i] = (byte) rand('A', 'Z');
        return new String(b);
    }

    private static String randomstring() {
        int maxlength = SystemPreferences.MAX_LOGIN_PWD_LENGTH;
        int n = rand(4, maxlength);
        byte b[] = new byte[n];
        for (int i = 0; i < n; i++)
            b[i] = (byte) rand('a', 'z');
        return new String(b);
    }

    public String randomPwdGenerate() {
        String randomString = randomstring();
        String pwdOtherPart = randomString.substring(randomString.length() / 2);
        String randomCapitalString = randomCapString();
        int maxlength = SystemPreferences.MAX_LOGIN_PWD_LENGTH;
        String finalPwd = randomString.substring(0, randomString.length() / 2) + randomCapitalString + pwdOtherPart.substring(0, pwdOtherPart.length() / 2) + rn.nextInt(100) + pwdOtherPart.substring(pwdOtherPart.length() / 2);
        if (finalPwd.length() > maxlength) {
            finalPwd = finalPwd.substring(finalPwd.length() - maxlength);
        }
        return finalPwd;
    }

    /**
     * Method calculateCommissionQuantity.
     * 
     * @param p_commissionType
     *            String
     * @param p_commissionRate
     *            double
     * @param p_productCost
     *            long
     * @return long
     * @throws BTSLBaseException
     * @see com.selftopup.pretups.util.OperatorUtilI#calculateCommission(long,
     *      long)
     */
    public long calculateCommissionQuantity(long p_commisionValue, long p_unitValue, long p_tax3Value) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("calculateCommissionQuantity()", "Entered   p_commissionValue: " + p_commisionValue + " p_unitValue: " + p_unitValue + ", p_tax3Value=" + p_tax3Value);

        double commissionQuantity = 0;
        commissionQuantity = p_commisionValue - p_tax3Value;
        commissionQuantity = PretupsBL.getSystemAmount((commissionQuantity / p_unitValue));

        if (_log.isDebugEnabled())
            _log.debug("calculateCommissionQuantity()", "Exited Commission Calculated in terms of quantity " + commissionQuantity);

        return (long) commissionQuantity;
    }

    /**
     * Method handleLDCCRequest
     * 
     * @author chetan.kothari
     * @created on 21/01/2010
     */
    public boolean handleLDCCRequest() {
        // customized according to mobinil requirement for all other it will
        // return false.
        return false;
    }

    /**
     * Method to validate the SMS PIn sent in the request
     * 
     * @param p_pin
     * @throws BTSLBaseException
     */
    public void validatePIN(String p_pin) throws BTSLBaseException {
        if (BTSLUtil.isNullString(p_pin))
            throw new BTSLBaseException("BTSLUtil", "validatePIN", SelfTopUpErrorCodesI.PIN_INVALID);
        else if (!BTSLUtil.isNumeric(p_pin))
            throw new BTSLBaseException("BTSLUtil", "validatePIN", SelfTopUpErrorCodesI.NEWPIN_NOTNUMERIC);
        else if (p_pin.length() < SystemPreferences.MIN_SMS_PIN_LENGTH || p_pin.length() > SystemPreferences.MAX_SMS_PIN_LENGTH) {
            String msg[] = { String.valueOf(SystemPreferences.MIN_SMS_PIN_LENGTH), String.valueOf(SystemPreferences.MAX_SMS_PIN_LENGTH) };
            throw new BTSLBaseException("BTSLUtil", "validatePIN", SelfTopUpErrorCodesI.PIN_LENGTHINVALID, 0, msg, null);
        } else {
            int result = BTSLUtil.isSMSPinValid(p_pin);
            if (result == -1)
                throw new BTSLBaseException("BTSLUtil", "validatePIN", SelfTopUpErrorCodesI.PIN_SAMEDIGIT);
            else if (result == 1)
                throw new BTSLBaseException("BTSLUtil", "validatePIN", SelfTopUpErrorCodesI.PIN_CONSECUTIVE);
        }
    }

    public void validateP2PMeassgeFormat(Connection p_con, RequestVO p_requestVO, SenderVO p_senderVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validateP2PMeassgeFormat", "Entered");
        String[] requestMessageArray = null;
        String actualPin = null;
        String cvv = null;
        String pin = null;
        String paymentMethodType = null;
        String senderSubscriberType = null;
        String amount = null;
        try {
            requestMessageArray = p_requestVO.getRequestMessageArray();
            actualPin = p_senderVO.getPin();
            senderSubscriberType = ((SenderVO) p_transferVO.getSenderVO()).getSubscriberType();
            switch (requestMessageArray.length) {
            // service_keyword cvv amt pin
            // use default card details
            case 4: {
                pin = requestMessageArray[3];
                // incomingSmsStr.append("****"+" ");
                // if pin Invalid return with error(PIN is Mandatory)
                // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
                // whether PIN validation is required or not.
                if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                        if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            BTSLUtil.validatePIN(pin);
                            p_senderVO.setPin(BTSLUtil.encryptText(pin));
                            p_senderVO.setPinUpdateReqd(true);
                            p_senderVO.setActivateStatusReqd(true);
                        }
                    } else {
                        try {
                            SubscriberBL.validatePIN(p_con, p_senderVO, pin);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }
                    }
                }

                PaymentMethodKeywordVO paymentMethodKeywordVO = null;
                CardDetailsVO cardDetailsVO = new CardDetailsDAO().loadDefaultCredtCardDetails(p_con, p_senderVO.getUserID());
                if (cardDetailsVO == null)
                    throw new BTSLBaseException(this, "validateP2PMeassgeFormat", SelfTopUpErrorCodesI.DEFAULT_CARD_NOT_FOUND);

                p_senderVO.setCardDetailsVO(cardDetailsVO);
                paymentMethodKeywordVO = PaymentMethodCache.getObject(cardDetailsVO.getBankName(), p_transferVO.getServiceType(), p_transferVO.getNetworkCode());
                // p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                amount = requestMessageArray[2];
                if (paymentMethodKeywordVO == null) {
                    paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                    if (paymentMethodType == null) {
                        // return with error message, no default payment method
                        // defined
                        throw new BTSLBaseException(this, "validateP2PMeassgeFormat", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                    }
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setDefaultPaymentMethod("Y");
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, p_senderVO.getMsisdn());
                    // incomingSmsStr.append(paymentMethodType+" ");
                    // _incomingSmsStr+=amount+" ";
                } else {
                    paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                    p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                    // incomingSmsStr.append(paymentMethodType+" ");
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, p_senderVO.getMsisdn());

                }
                cvv = requestMessageArray[1];
                if (!BTSLUtil.isNullString(cvv) && BTSLUtil.isNumeric(cvv))
                    p_senderVO.setCvv(cvv);
                else
                    throw new BTSLBaseException("", "validateP2PMeassgeFormat", SelfTopUpErrorCodesI.INVALID_CVV_NUMBER);
                p_senderVO.setNickName(cardDetailsVO.getCardNickName());
                break;
            }
            // service_keyword Nick_name cvv amt pin
            case 5: {
                pin = requestMessageArray[4];
                // incomingSmsStr.append("****"+" ");
                // if pin Invalid return with error(PIN is Mandatory)
                // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
                // whether PIN validation is required or not.
                if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                        if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            BTSLUtil.validatePIN(pin);
                            p_senderVO.setPin(BTSLUtil.encryptText(pin));
                            p_senderVO.setPinUpdateReqd(true);
                            p_senderVO.setActivateStatusReqd(true);
                        }
                    } else {
                        try {
                            SubscriberBL.validatePIN(p_con, p_senderVO, pin);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }
                    }
                }

                PaymentMethodKeywordVO paymentMethodKeywordVO = null;
                CardDetailsVO cardDetailsVO = new CardDetailsDAO().loadCredtCardDetails(p_con, p_senderVO.getUserID(), requestMessageArray[1]);
                if (cardDetailsVO == null)
                    throw new BTSLBaseException(this, "validateP2PMeassgeFormat", SelfTopUpErrorCodesI.WRONG_NICK_NAME);

                p_senderVO.setCardDetailsVO(cardDetailsVO);
                // paymentMethodKeyword=requestMessageArray[1];
                // if paymentMethod invalid , Validate next Argument for
                // Receiver No(MSISDN).
                paymentMethodKeywordVO = PaymentMethodCache.getObject(cardDetailsVO.getBankName(), p_transferVO.getServiceType(), p_transferVO.getNetworkCode());
                // p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                amount = requestMessageArray[3];
                if (paymentMethodKeywordVO == null) {
                    paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                    if (paymentMethodType == null) {
                        // return with error message, no default payment method
                        // defined
                        throw new BTSLBaseException(this, "validateP2PMeassgeFormat", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                    }
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setDefaultPaymentMethod("Y");
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, p_senderVO.getMsisdn());
                    // incomingSmsStr.append(paymentMethodType+" ");
                    // _incomingSmsStr+=amount+" ";
                } else {
                    paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                    p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                    // incomingSmsStr.append(paymentMethodType+" ");
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, p_senderVO.getMsisdn());

                }
                cvv = requestMessageArray[2];
                if (!BTSLUtil.isNullString(cvv) && BTSLUtil.isNumeric(cvv))
                    p_senderVO.setCvv(cvv);
                else
                    throw new BTSLBaseException("", "validateP2PMeassgeFormat", SelfTopUpErrorCodesI.INVALID_CVV_NUMBER);
                p_senderVO.setNickName(cardDetailsVO.getCardNickName());
                break;
            }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateP2PMeassgeFormat", "  Exception while validation message :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateP2PMeassgeFormat]", "", "", "", "Exception while validating message" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "validateP2PMeassgeFormat", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validateP2PMeassgeFormat", "Exiting");
        }
    }

    /**
     * Check After Payment Method
     * 
     * @param p_con
     * @param i
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void checkAfterPaymentMethod(Connection p_con, String p_amount, TransferVO p_transferVO, String p_msisdn) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("checkAfterPaymentMethod", " p_amount:" + p_amount + " p_msisdn: " + p_msisdn);
        String receiverMSISDN = p_msisdn;
        ReceiverVO _receiverVO = new ReceiverVO();
        _receiverVO.setMsisdn(receiverMSISDN);
        NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
        if (networkPrefixVO == null)
            throw new BTSLBaseException(this, "checkAfterPaymentMethod", SelfTopUpErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
        _receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
        _receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        p_transferVO.setReceiverVO(_receiverVO);
        long amount = 0;
        amount = PretupsBL.getSystemAmount(p_amount);
        if (amount < 0)
            throw new BTSLBaseException(this, "checkAfterPaymentMethod", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
        p_transferVO.setTransferValue(amount);
        p_transferVO.setRequestedAmount(amount);
    }

    public boolean prefixServiceMappingExist(String p_serviceType, String p_prxfService) {
        return true;
    }

    private boolean checkAfterPaymentMethodForMCDBuddy(Connection p_con, int i, String[] p_requestMessageArray, StringBuffer incomingSmsStr, TransferVO p_transferVO, RequestVO p_requestVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("checkAfterPaymentMethodForBuddy", " i=" + i + " requestMessageArray length:" + p_requestMessageArray.length + " i=" + i);
        int incReq = 0;
        if (i == 2)
            incReq = 1;
        String receiverMSISDN_NAME = p_requestMessageArray[1 + incReq];
        BuddyVO buddyVO = new SubscriberDAO().loadBuddyDetails(p_con, ((SenderVO) p_transferVO.getSenderVO()).getUserID(), receiverMSISDN_NAME);
        if (buddyVO == null) {
            return false;
        }
        String receiverMSISDN = buddyVO.getMsisdn();
        NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
        if (networkPrefixVO == null)
            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
        buddyVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        buddyVO.setPrefixID(networkPrefixVO.getPrefixID());
        buddyVO.setSubscriberType(networkPrefixVO.getSeriesType());
        p_transferVO.setReceiverVO(buddyVO);
        incomingSmsStr.append(receiverMSISDN_NAME + " ");
        int messageLength = p_requestMessageArray.length;
        String pin = null;
        long amount = 0;
        SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
        String actualPin = BTSLUtil.decryptText(senderVO.getPin());

        /*
         * Message format that are supported are as: Message length 2: PRC Name
         * If pin required the PIN should be default Amount will always be
         * default Message length 3: PIN required and pin is not default PRC
         * Name PIN (PIN required and actual pin=default pin) OR PIN not
         * required PRC HDFC Name PRC Name Amount Message length 4: PIN Required
         * actual pin!=default pin PRC Name Amount PIN PRC HDFC NAme PIN
         * actual=default PRC HDFC Name Amount PRC Name Amount langCode PIN not
         * required PRC HDFC name Amount PRC Name Amount langCode Message length
         * 5: PIN Required actual!=default PRC HDFC Name Amount PIN PRC Name
         * Amount langCode PIN Actual=default PRC HDFC Name Amount langCode PRC
         * Name Amount selector lang PIN not required PRC HDFC Name Amount
         * LangCode PRC Name Amount selector LangCode Message length 6: PIN
         * Required Actual!=default PRC HDFC Name Amount langCode PIN PRC Name
         * Amount selector langCode PIN Actual=default PRC HDFC Name Amount
         * selector langCode PRC Name Amount selector langCode PIN(Update with
         * new PIN) PIN not required PRC HDFC Name Amount selector langCode PRC
         * Name Amount Selector langCode PIN( Update with new PIN) Message
         * length 7: PIN Required Actual!=default PRC HDFC Name Amount selector
         * langCode PIN Actual=default PRC HDFC Name Amount selector langCode
         * PIN(Update with new PIN) PIN not required PRC HDFC Name Amount
         * selector langCode PIN (Update with new PIN)
         */
        switch (messageLength) {
        case 2: {
            if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                // whether PIN validation is required or not.
                if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                    if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                }
            }
            amount = buddyVO.getPreferredAmount();
            if (amount < 0)
                throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
            p_transferVO.setTransferValue(amount);
            p_transferVO.setRequestedAmount(amount);
            incomingSmsStr.append(amount + " ");
            break;
        }
        case 3: {
            // if((((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())&&!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
            if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && ((((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) && !actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                if (i == 2)
                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                else {
                    pin = p_requestMessageArray[2];
                    incomingSmsStr.append("****" + " ");
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                    amount = buddyVO.getPreferredAmount();
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                }
            } else {
                if (i == 2) {
                    amount = buddyVO.getPreferredAmount();
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                } else {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            }
            break;
        }
        case 4: {
            if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                // if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                    pin = p_requestMessageArray[3];
                    incomingSmsStr.append("****" + " ");
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                    if (i == 2) {
                        amount = buddyVO.getPreferredAmount();
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    } else {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);

                    }
                } else {
                    if (i == 2) {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    } else {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[3]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    }
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            } else {
                if (i == 2) {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                } else {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[3]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            }
            break;
        }
        case 5: {
            if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                // if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                    pin = p_requestMessageArray[4];
                    incomingSmsStr.append("****" + " ");
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                    if (i == 2) {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    } else {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[3]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    }
                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                } else {
                    if (i == 2) {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } else {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);

                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    }
                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                }
            } else {
                if (i == 2) {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                } else {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                    if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                        int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                        p_requestVO.setReqSelector("" + selectorValue);
                    }
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            }
            break;
        }
        case 6: {
            if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                // if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                    pin = p_requestMessageArray[5];
                    incomingSmsStr.append("****" + " ");
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                    if (i == 2) {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } else {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);

                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    }
                } else {
                    if (i == 2) {
                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);

                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    } else {
                        pin = p_requestMessageArray[5];
                        incomingSmsStr.append("****" + " ");
                        validatePIN(pin);
                        senderVO.setPin(BTSLUtil.encryptText(pin));
                        senderVO.setPinUpdateReqd(true);

                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);

                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    }
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            } else {
                if (i == 2) {
                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);

                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                        int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                        p_requestVO.setReqSelector("" + selectorValue);
                    }
                } else {
                    // To check whether PIN validation is required or not.
                    if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                        if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            pin = p_requestMessageArray[5];
                            incomingSmsStr.append("****" + " ");
                            validatePIN(pin);
                            senderVO.setPin(BTSLUtil.encryptText(pin));
                            senderVO.setPinUpdateReqd(true);
                        }
                    }

                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);

                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                    if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                        int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                        p_requestVO.setReqSelector("" + selectorValue);
                    }
                }
                p_transferVO.setTransferValue(amount);
                p_transferVO.setRequestedAmount(amount);
                incomingSmsStr.append(amount + " ");
            }
            break;
        }
        case 7: {
            /*
             * Actual!=default PRC HDFC Name Amount selector langCode PIN
             * Actual=default PRC HDFC Name Amount selector langCode PIN(Update
             * with new PIN)
             */
            if (i == 1)
                throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            else {
                // if(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                    if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        pin = p_requestMessageArray[6];
                        incomingSmsStr.append("****" + " ");
                        try {
                            SubscriberBL.validatePIN(p_con, senderVO, pin);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }

                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    } else {
                        // To check whether PIN validation is required or not.
                        if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                            pin = p_requestMessageArray[6];
                            incomingSmsStr.append("****" + " ");
                            validatePIN(pin);
                            senderVO.setPin(BTSLUtil.encryptText(pin));
                            senderVO.setPinUpdateReqd(true);
                        }

                        amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                        if (amount < 0)
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                        try {
                            if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                if (p_requestVO.getReceiverLocale() == null)// changed
                                    // by
                                    // ankit
                                    // zindal
                                    // 01/08/06
                                    // discussed
                                    // by
                                    // AC/GB
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } catch (Exception e) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                        if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                            int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                            p_requestVO.setReqSelector("" + selectorValue);
                        }
                    }
                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                } else {
                    // To check whether PIN validation is required or not.
                    if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                        if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            pin = p_requestMessageArray[6];
                            incomingSmsStr.append("****" + " ");
                            validatePIN(pin);
                            senderVO.setPin(BTSLUtil.encryptText(pin));
                            senderVO.setPinUpdateReqd(true);
                        }
                    }

                    amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                    if (amount < 0)
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    try {
                        if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                            int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                            if (p_requestVO.getReceiverLocale() == null)// changed
                                // by
                                // ankit
                                // zindal
                                // 01/08/06
                                // discussed
                                // by
                                // AC/GB
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        }
                    } catch (Exception e) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                        int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                        p_requestVO.setReqSelector("" + selectorValue);
                    }

                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                }
                break;
            }
        }
        default: {
            throw new BTSLBaseException(this, "checkIfBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
        }

        }
        return true;
    }

    private void validateIfNotMCDBuddy(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        String[] requestMessageArray = p_requestVO.getRequestMessageArray();
        if (_log.isDebugEnabled())
            _log.debug("validateIfNotMCDBuddy", " requestMessageArray length:" + requestMessageArray);
        if (requestMessageArray.length < 3 || requestMessageArray.length > 7)
            throw new BTSLBaseException(this, "validateIfNotBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
        String serviceKeyword = requestMessageArray[0];
        String senderSubscriberType = ((SenderVO) p_transferVO.getSenderVO()).getSubscriberType();
        StringBuffer incomingSmsStr = new StringBuffer(serviceKeyword + " ");
        int messageLength = requestMessageArray.length;
        SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
        // if pin Invalid return with error(PIN is Mandatory)
        String actualPin = BTSLUtil.decryptText(senderVO.getPin());
        if (_log.isDebugEnabled())
            _log.debug("validateIfNotMCDBuddy", " actualPin:" + actualPin);

        String paymentMethodType = null;
        String pin = null;
        String paymentMethodKeyword = null;
        switch (messageLength) {
        case 3: {
            // whether PIN validation is required or not.
            if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                    throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }
            paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
            if (paymentMethodType == null) {
                // return with error message, no default payment method defined
                throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            }
            p_transferVO.setPaymentMethodType(paymentMethodType);
            incomingSmsStr.append(paymentMethodType + " ");
            checkMCDAfterPaymentMethod(p_con, 1, requestMessageArray, incomingSmsStr, p_transferVO);
            break;
        }
        case 4: {
            // Validate 2nd Argument for PIN.
            pin = requestMessageArray[3];

            incomingSmsStr.append("****" + " ");
            // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
            // whether PIN validation is required or not.
            if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                    if (!BTSLUtil.isNullString(requestMessageArray[3])) {
                        if (BTSLUtil.isNumeric(requestMessageArray[3]) && requestMessageArray[3].length() == 1) {
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[3]));
                            if (p_requestVO.getReceiverLocale() == null)
                                p_requestVO.setReceiverLocale(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                        } else {
                            if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                validatePIN(pin);
                                senderVO.setPin(BTSLUtil.encryptText(pin));
                                senderVO.setPinUpdateReqd(true);
                                senderVO.setActivateStatusReqd(true);
                            }

                        }

                    }
                } else {
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                }
            }

            paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
            if (paymentMethodType == null) {
                // return with error message, no default payment method defined
                throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            }
            p_transferVO.setPaymentMethodType(paymentMethodType);
            incomingSmsStr.append(paymentMethodType + " ");
            checkMCDAfterPaymentMethod(p_con, 1, requestMessageArray, incomingSmsStr, p_transferVO);
            break;
        }
        case 5: {

            // Validate 2nd Argument for PIN.
            pin = requestMessageArray[4];
            incomingSmsStr.append("****" + " ");
            // if pin Invalid return with error(PIN is Mandatory)
            // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
            // whether PIN validation is required or not.
            if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                    // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                    if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        validatePIN(pin);
                        senderVO.setPin(BTSLUtil.encryptText(pin));
                        senderVO.setPinUpdateReqd(true);
                        senderVO.setActivateStatusReqd(true);
                    }
                } else {
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                }
            }

            // if PIN valid
            // Validate next Argument for Payment Method.

            // killed by sanjay as payemnt method table does not exists
            PaymentMethodKeywordVO paymentMethodKeywordVO = null;
            paymentMethodKeyword = requestMessageArray[1];
            // if paymentMethod invalid , Validate next Argument for Receiver
            // No(MSISDN).
            paymentMethodKeywordVO = PaymentMethodCache.getObject(paymentMethodKeyword, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());

            if (paymentMethodKeywordVO == null) {
                paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                if (paymentMethodType == null) {
                    // return with error message, no default payment method
                    // defined
                    throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                }
                p_transferVO.setPaymentMethodType(paymentMethodType);
                p_transferVO.setDefaultPaymentMethod("Y");
                incomingSmsStr.append(paymentMethodType + " ");
                checkMCDAfterPaymentMethod(p_con, 1, requestMessageArray, incomingSmsStr, p_transferVO);
                try {
                    // _requestVO.setReqSelector(""+SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
                    if (!BTSLUtil.isNullString(requestMessageArray[3])) {
                        int localeValue = Integer.parseInt(requestMessageArray[3]);
                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[3]));
                        if (p_requestVO.getReceiverLocale() == null)// changed
                            // by ankit
                            // zindal
                            // 01/08/06
                            // discussed
                            // by AC/GB
                            throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                        // _requestVO.setReceiverLocale(new
                        // Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

                    }
                } catch (Exception e) {
                    throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                }
            } else {
                paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                p_transferVO.setPaymentMethodType(paymentMethodType);
                p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                incomingSmsStr.append(paymentMethodType + " ");
                checkMCDAfterPaymentMethod(p_con, 2, requestMessageArray, incomingSmsStr, p_transferVO);
                // _requestVO.setReqSelector(""+SystemPreferences.P2P_TRANSFER_DEF_SELECTOR_CODE);
            }

            break;
        }
        case 6: {
            // Validate 2nd Argument for PIN.
            pin = requestMessageArray[5];
            incomingSmsStr.append("****" + " ");
            // if pin Invalid return with error(PIN is Mandatory)
            // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
            // whether PIN validation is required or not.
            if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                    // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                    if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        validatePIN(pin);
                        senderVO.setPin(BTSLUtil.encryptText(pin));
                        senderVO.setPinUpdateReqd(true);
                        senderVO.setActivateStatusReqd(true);
                    }
                } else {
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                }
            }
            // if PIN valid as
            // Validate next Argument for Payment Method.
            paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
            if (paymentMethodType == null)
                // return with error message, no default payment method defined
                throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
            incomingSmsStr.append(paymentMethodType + " ");

            p_transferVO.setPaymentMethodType(paymentMethodType);
            p_transferVO.setDefaultPaymentMethod("Y");

            // if paymentMethod valid , Validate next Argument for Receiver
            // No(MSISDN).
            checkMCDAfterPaymentMethod(p_con, 1, requestMessageArray, incomingSmsStr, p_transferVO);
            try {
                if (!BTSLUtil.isNullString(requestMessageArray[3])) {
                    int selectorValue = Integer.parseInt(requestMessageArray[3]);
                    p_requestVO.setReqSelector("" + selectorValue);
                }
            } catch (Exception e) {
                throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
            }
            try {
                if (!BTSLUtil.isNullString(requestMessageArray[4])) {
                    int localeValue = Integer.parseInt(requestMessageArray[4]);
                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[4]));
                    if (p_requestVO.getReceiverLocale() == null)
                        throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                }
            } catch (Exception e) {
                throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
            }
            break;
        }
        case 7: {
            // Validate 2nd Argument for PIN.
            pin = requestMessageArray[6];
            incomingSmsStr.append("****" + " ");
            // if pin Invalid return with error(PIN is Mandatory)
            // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
            // whether PIN validation is required or not.
            if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                    // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                    if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        validatePIN(pin);
                        senderVO.setPin(BTSLUtil.encryptText(pin));
                        senderVO.setPinUpdateReqd(true);
                        senderVO.setActivateStatusReqd(true);
                    }
                } else {
                    try {
                        SubscriberBL.validatePIN(p_con, senderVO, pin);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            p_con.commit();
                        throw be;
                    }
                }
            }
            // if PIN valid as
            // Validate next Argument for Payment Method.

            PaymentMethodKeywordVO paymentMethodKeywordVO = null;
            paymentMethodKeyword = requestMessageArray[1];
            // if paymentMethod invalid , Validate next Argument for Receiver
            // No(MSISDN).
            paymentMethodKeywordVO = PaymentMethodCache.getObject(paymentMethodKeyword, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());

            if (paymentMethodKeywordVO == null) {
                throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_NOTFOUND_SERVICEPAYMENTMETHOD);
            } else {
                paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                p_transferVO.setPaymentMethodType(paymentMethodType);
                p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                incomingSmsStr.append(paymentMethodType + " ");
                checkMCDAfterPaymentMethod(p_con, 2, requestMessageArray, incomingSmsStr, p_transferVO);
                try {
                    if (!BTSLUtil.isNullString(requestMessageArray[4])) {
                        int selectorValue = Integer.parseInt(requestMessageArray[4]);
                        p_requestVO.setReqSelector("" + selectorValue);
                    }
                } catch (Exception e) {
                    throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
                }
                try {
                    if (!BTSLUtil.isNullString(requestMessageArray[5])) {
                        int localeValue = Integer.parseInt(requestMessageArray[5]);
                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(requestMessageArray[5]));
                        if (p_requestVO.getReceiverLocale() == null)
                            throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                    }
                } catch (Exception e) {
                    throw new BTSLBaseException(this, "validateIfNotMCDBuddy", SelfTopUpErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                }
            }
            break;
        }
        }
        p_transferVO.setIncomingSmsStr(incomingSmsStr.toString());
    }

    public void checkMCDAfterPaymentMethod(Connection p_con, int i, String[] p_requestMessageArray, StringBuffer incomingSmsStr, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("checkMCDAfterPaymentMethod", " i=" + i + " requestMessageArray length:" + p_requestMessageArray.length);
        String receiverMSISDN = p_requestMessageArray[i];
        receiverMSISDN = addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverMSISDN));
        if (!BTSLUtil.isValidMSISDN(receiverMSISDN)) {
            throw new BTSLBaseException(this, "checkMCDAfterPaymentMethod", SelfTopUpErrorCodesI.ERROR_INVALID_MSISDN, 0, new String[] { receiverMSISDN }, null);
        }
        // This block will check if the user is sending the PIN but is also a
        // buddy then that request should go through
        /*
         * BuddyVO buddyVO=new
         * SubscriberDAO().loadBuddyDetails(p_con,((SenderVO)p_transferVO.
         * getSenderVO()).getUserID(),receiverMSISDN);
         * if(buddyVO!=null) { receiverMSISDN=buddyVO.getMsisdn();
         * incomingSmsStr.append(receiverMSISDN+" "); NetworkPrefixVO
         * networkPrefixVO=PretupsBL.getNetworkDetails(receiverMSISDN,PretupsI.
         * USER_TYPE_RECEIVER);
         * if(networkPrefixVO==null) throw new
         * BTSLBaseException("","parseRequest",PretupsErrorCodesI.
         * ERROR_NOTFOUND_RECEIVERNETWORK,0,new
         * String[]{receiverMSISDN},null);
         * buddyVO.setNetworkCode(networkPrefixVO.getNetworkCode());
         * buddyVO.setPrefixID(networkPrefixVO.getPrefixID());
         * buddyVO.setSubscriberType(networkPrefixVO.getSeriesType());
         * p_transferVO.setReceiverVO(buddyVO); long amount=0;
         * amount=PretupsBL.getSystemAmount(p_requestMessageArray[i+1]);
         * if(amount<0) throw new
         * BTSLBaseException("","parseRequest",PretupsErrorCodesI.
         * P2P_ERROR_AMOUNT_LESSZERO);
         * p_transferVO.setTransferValue(amount);
         * p_transferVO.setRequestedAmount(amount);
         * incomingSmsStr.append(amount+" ");
         * } else {
         */
        incomingSmsStr.append(receiverMSISDN + " ");
        ReceiverVO _receiverVO = new ReceiverVO();
        _receiverVO.setMsisdn(receiverMSISDN);
        NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
        if (networkPrefixVO == null)
            throw new BTSLBaseException(this, "checkMCDAfterPaymentMethod", SelfTopUpErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
        _receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
        _receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        p_transferVO.setReceiverVO(_receiverVO);
        long amount = 0;
        amount = PretupsBL.getSystemAmount(p_requestMessageArray[i + 1]);
        if (amount < 0)
            throw new BTSLBaseException(this, "checkMCDAfterPaymentMethod", SelfTopUpErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
        p_transferVO.setTransferValue(amount);
        p_transferVO.setRequestedAmount(amount);
        incomingSmsStr.append(amount + " ");
        // }
    }

    // VASTRIX changes ends.

    /**
     * Method validateP2PAdhocRechargeMessageFormat.
     * 
     * @author sonali.garg
     * @author Vikas Singh
     */
    public void validateP2PAdhocRechargeMessageFormat(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO, SenderVO p_senderVO) throws BTSLBaseException, Exception {

        if (_log.isDebugEnabled())
            _log.debug("validateP2PAdhocRechargeMessageFormat", "Entered");
        String[] requestMessageArray = null;
        String actualPin = null;
        String cvv = null;
        String pin = null;
        String paymentMethodType = null;
        String senderSubscriberType = null;
        String amount = null;
        String bankName = null;
        String cardNumber = null;
        String holderName = null;
        String expiryDate = null;
        String senderMsisdn = null;// p_requestVO.getRequestMSISDN();
        String receiverMsisdn = null;
        String imei = null;
        String nickName = null;
        String userID = null;// p_senderVO.getUserID();
        String selector = null;
        Double tempAmt = null;
        String filteredMSISDN = null;
        try {
            requestMessageArray = p_requestVO.getRequestMessageArray();
            actualPin = p_senderVO.getPin();
            senderSubscriberType = ((SenderVO) p_transferVO.getSenderVO()).getSubscriberType();

            switch (requestMessageArray.length) {
            // Vikas Singh
            case 7: {
                // System.out.println("CASE 7: validateP2PAdhocRechargeMessageFormat:"
                // );
                pin = requestMessageArray[6];
                // incomingSmsStr.append("****"+" ");
                // if pin Invalid return with error(PIN is Mandatory)
                // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
                // whether PIN validation is required or not.
                if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                        if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            BTSLUtil.validatePIN(pin);
                            p_senderVO.setPin(BTSLUtil.encryptText(pin));
                            p_senderVO.setPinUpdateReqd(true);
                            p_senderVO.setActivateStatusReqd(true);
                        }
                    } else {
                        try {
                            SubscriberBL.validatePIN(p_con, p_senderVO, pin);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }
                    }
                }
                senderMsisdn = p_requestVO.getRequestMSISDN();
                userID = p_senderVO.getUserID();
                amount = requestMessageArray[1];
                try {
                    tempAmt = Double.parseDouble(amount);
                    if (!BTSLUtil.isNullString(amount) && BTSLUtil.isDecimalValue(amount) && !(tempAmt <= 0)) {
                        // _finalAmount=PretupsBL.getSystemAmount(Double.parseDouble(_amount));
                        amount = tempAmt.toString();
                    }
                } catch (NumberFormatException nfe) {
                    _log.error("process", "Number Format Exception: amount can't be parse to an Int");
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_INVALID_AMOUNT);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.AUTO_TOPUP_INVALID_AMOUNT);
                }

                nickName = requestMessageArray[2].toUpperCase();
                new ModifyCardDetailsController().validateNickName(nickName, PretupsI.NAME_ALLOWED_LENGTH, p_requestVO);

                imei = requestMessageArray[3];
                boolean imeiExist = new SubscriberDAO().checkImei(p_con, senderMsisdn, imei, userID);
                if (!imeiExist) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_IMEI);
                }

                if (BTSLUtil.isNumeric(requestMessageArray[4]) && requestMessageArray[4].length() == PretupsI.CVV_LENGTH) {
                    cvv = requestMessageArray[4];
                } else
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.INVALID_CVV_NUMBER);

                selector = requestMessageArray[5];
                if (BTSLUtil.isNumeric(requestMessageArray[5])) {
                    selector = requestMessageArray[5];
                } else
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);

                CardDetailsVO cardDetailsVO = new CardDetailsDAO().loadCredtCardDetails(p_con, userID, nickName);
                if (cardDetailsVO != null) {
                    cardNumber = BTSLUtil.decryptText(cardDetailsVO.getCardNumber());
                    cardDetailsVO.setCardNumber(cardNumber);
                    p_transferVO.setCardReferenceNo(cardNumber.substring(cardNumber.length() - 4, cardNumber.length()));
                    holderName = BTSLUtil.decryptText(cardDetailsVO.getNameOfEmbossing());
                    cardDetailsVO.setNameOfEmbossing(holderName);
                    expiryDate = BTSLUtil.decryptText(cardDetailsVO.getExpiryDate());
                    cardDetailsVO.setExpiryDate(expiryDate);
                    bankName = cardDetailsVO.getBankName();
                    if (BTSLUtil.isNullString(bankName))
                        bankName = PretupsI.DEFAULT_PAYMENT_GATEWAY;
                    cardDetailsVO.setCvv(cvv);
                } else {
                    _log.error("process", "PROVIDED NICK NAME DOESN'T EXIST ");
                    String msgArr1[] = { nickName };
                    p_requestVO.setMessageArguments(msgArr1);
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.INVALID_OLD_NICK);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.INVALID_OLD_NICK, msgArr1);
                }
                PaymentMethodKeywordVO paymentMethodKeywordVO = null;
                paymentMethodKeywordVO = PaymentMethodCache.getObject(bankName, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());
                if (paymentMethodKeywordVO == null) {
                    paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                    if (paymentMethodType == null) {
                        // return with error message, no default payment method
                        // defined
                        throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                    }
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setDefaultPaymentMethod("Y");
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, senderMsisdn);
                } else {
                    paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                    p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, senderMsisdn);
                }
                p_requestVO.setReqSelector(selector);
                p_senderVO.setCardDetailsVO(cardDetailsVO);
                break;
            }
            case 8: {

                pin = requestMessageArray[7];
                if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                        if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            BTSLUtil.validatePIN(pin);
                            p_senderVO.setPin(BTSLUtil.encryptText(pin));
                            p_senderVO.setPinUpdateReqd(true);
                            p_senderVO.setActivateStatusReqd(true);
                        }
                    } else {
                        try {
                            SubscriberBL.validatePIN(p_con, p_senderVO, pin);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }
                    }
                }
                senderMsisdn = p_requestVO.getRequestMSISDN();
                userID = p_senderVO.getUserID();
                receiverMsisdn = requestMessageArray[1];
                if (BTSLUtil.isNullString(receiverMsisdn))
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_MSISDN_NULL);
                filteredMSISDN = PretupsBL.getFilteredMSISDN(receiverMsisdn.trim());
                if (!BTSLUtil.isValidMSISDN(filteredMSISDN))// &&
                                                            // filteredMSISDN.equals(p_requestVO.getRequestMSISDN())
                                                            // )
                {
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.ERROR_INVALID_MSISDN);
                }
                if (filteredMSISDN.equals(p_requestVO.getRequestMSISDN()))
                    receiverMsisdn = p_requestVO.getRequestMSISDN();
                // receiver msisdn validation begin
                receiverMsisdn = getSystemFilteredMSISDN(receiverMsisdn);
                if (!BTSLUtil.isValidMSISDN(receiverMsisdn)) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OperatorUtil[validateP2PAdhocRechargeMessageFormat]", "", receiverMsisdn, "", "Receiver MSISDN Not valid");
                    p_requestVO.setSenderMessageRequired(false);
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN);
                }
                // receiver msisdn validation ends
                amount = requestMessageArray[2];
                try {
                    tempAmt = Double.parseDouble(amount);
                    if (!BTSLUtil.isNullString(amount) && BTSLUtil.isDecimalValue(amount) && !(tempAmt <= 0)) {
                        amount = tempAmt.toString();
                    }
                } catch (NumberFormatException nfe) {
                    _log.error("process", "Number Format Exception: amount can't be parse to an Int");
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_INVALID_AMOUNT);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.AUTO_TOPUP_INVALID_AMOUNT);
                }
                nickName = requestMessageArray[3].toUpperCase();
                new ModifyCardDetailsController().validateNickName(nickName, PretupsI.NAME_ALLOWED_LENGTH, p_requestVO);

                imei = requestMessageArray[4];

                if (BTSLUtil.isNumeric(requestMessageArray[5]) && requestMessageArray[5].length() == PretupsI.CVV_LENGTH) {
                    cvv = requestMessageArray[5];
                } else
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.INVALID_CVV_NUMBER);

                if (BTSLUtil.isNumeric(requestMessageArray[6])) {
                    selector = requestMessageArray[6];
                } else
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);

                // IMEI verification
                boolean imeiExist = new SubscriberDAO().checkImei(p_con, senderMsisdn, imei, userID);
                if (!imeiExist) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_IMEI);
                }
                // IMEI verification ends
                CardDetailsVO cardDetailsVO = new CardDetailsDAO().loadCredtCardDetails(p_con, userID, nickName);
                if (cardDetailsVO != null) {
                    cardNumber = BTSLUtil.decryptText(cardDetailsVO.getCardNumber());
                    cardDetailsVO.setCardNumber(cardNumber);
                    p_transferVO.setCardReferenceNo(cardNumber.substring(cardNumber.length() - 4, cardNumber.length()));
                    holderName = BTSLUtil.decryptText(cardDetailsVO.getNameOfEmbossing());
                    cardDetailsVO.setNameOfEmbossing(holderName);
                    expiryDate = BTSLUtil.decryptText(cardDetailsVO.getExpiryDate());
                    cardDetailsVO.setExpiryDate(expiryDate);
                    bankName = cardDetailsVO.getBankName();
                    if (BTSLUtil.isNullString(bankName))
                        bankName = PretupsI.DEFAULT_PAYMENT_GATEWAY;
                    cardDetailsVO.setCvv(cvv);
                } else {
                    _log.error("process", "PROVIDED NICK NAME DOESN'T EXIST ");
                    String msgArr1[] = { nickName };
                    p_requestVO.setMessageArguments(msgArr1);
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.INVALID_OLD_NICK);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.INVALID_OLD_NICK, msgArr1);
                }

                PaymentMethodKeywordVO paymentMethodKeywordVO = null;
                paymentMethodKeywordVO = PaymentMethodCache.getObject(bankName, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());
                if (paymentMethodKeywordVO == null) {
                    paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                    if (paymentMethodType == null) {
                        // return with error message, no default payment method
                        // defined
                        throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                    }
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setDefaultPaymentMethod("Y");
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, receiverMsisdn);
                } else {
                    paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                    p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                    // incomingSmsStr.append(paymentMethodType+" ");
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, receiverMsisdn);
                }
                p_requestVO.setReqSelector(selector);
                p_senderVO.setCardDetailsVO(cardDetailsVO);
                break;
            } // end Vikas Singh
              // service_keyword receiver_msisdn amount bank name card number
              // holder name expiry date cvv pin
              // ADHOCRC 7211000014 100 CITI 4500000000000007 Joey 01/22 567
              // 1357
              // Recharge through web

            case 9: {
                pin = requestMessageArray[8];
                // incomingSmsStr.append("****"+" ");
                // if pin Invalid return with error(PIN is Mandatory)
                // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
                // whether PIN validation is required or not.
                if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                        if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            BTSLUtil.validatePIN(pin);
                            p_senderVO.setPin(BTSLUtil.encryptText(pin));
                            p_senderVO.setPinUpdateReqd(true);
                            p_senderVO.setActivateStatusReqd(true);
                        }
                    } else {
                        try {
                            SubscriberBL.validatePIN(p_con, p_senderVO, pin);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }
                    }
                }

                receiverMsisdn = requestMessageArray[1];
                if (BTSLUtil.isNullString(receiverMsisdn))
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_MSISDN_NULL);
                filteredMSISDN = PretupsBL.getFilteredMSISDN(receiverMsisdn.trim());
                if (!BTSLUtil.isValidMSISDN(filteredMSISDN))// &&
                                                            // filteredMSISDN.equals(p_requestVO.getRequestMSISDN())
                                                            // )
                {
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.ERROR_INVALID_MSISDN);
                }
                if (filteredMSISDN.equals(p_requestVO.getRequestMSISDN()))
                    receiverMsisdn = p_requestVO.getRequestMSISDN();

                // receiver msisdn validation begin
                receiverMsisdn = getSystemFilteredMSISDN(receiverMsisdn);
                if (!BTSLUtil.isValidMSISDN(receiverMsisdn)) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OperatorUtil[validateP2PAdhocRechargeMessageFormat]", "", receiverMsisdn, "", "Receiver MSISDN Not valid");
                    p_requestVO.setSenderMessageRequired(false);
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN);
                }
                bankName = requestMessageArray[3];
                PaymentMethodKeywordVO paymentMethodKeywordVO = null;
                paymentMethodKeywordVO = PaymentMethodCache.getObject(bankName, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());
                amount = requestMessageArray[2];
                if (paymentMethodKeywordVO == null) {
                    paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                    if (paymentMethodType == null) {
                        // return with error message, no default payment method
                        // defined
                        throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                    }
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setDefaultPaymentMethod("Y");
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, receiverMsisdn);
                } else {
                    paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                    p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, receiverMsisdn);

                }
                cvv = requestMessageArray[7];
                if (!BTSLUtil.isNullString(cvv) && BTSLUtil.isNumeric(cvv))
                    p_senderVO.setCvv(cvv);
                else
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_CVV_NUMBER);

                cardNumber = requestMessageArray[4];
                p_transferVO.setCardReferenceNo(cardNumber.substring(cardNumber.length() - 4, cardNumber.length()));
                holderName = requestMessageArray[5];
                expiryDate = requestMessageArray[6];
                CardDetailsVO cardDetailsVO = new CardDetailsVO();

                if (!BTSLUtil.isNullString(cardNumber) && BTSLUtil.isNumeric(cardNumber))
                    cardDetailsVO.setCardNumber(cardNumber);
                else
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_CREDITCARD_NUMBER);
                if (holderName.length() > PretupsI.VALID_LENGTH_CARD_HOLDER_NAME) {
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_LENGTH_HOLDER_NAME);
                }
                if (!BTSLUtil.isAlphaNumericIncludingSpace(holderName)) {
                    throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.INVALID_HOLDER_NAME);
                }
                cardDetailsVO.setNameOfEmbossing(holderName);
                cardDetailsVO.setExpiryDate(expiryDate);
                cardDetailsVO.setCvv(cvv);
                p_senderVO.setCardDetailsVO(cardDetailsVO);
                break;
            }
            // ADHOC recharge i.e. on the fly recharge.
            // 0 1 2 3 4 5 6 7 8 9
            // TYPE MSISDN2 AMOUNT SELECTOR HOLDERNAME CARDNO EDATE IMEI CVV PIN
            case 10: {
                imei = requestMessageArray[7];
                userID = p_senderVO.getUserID();
                senderMsisdn = p_senderVO.getMsisdn();
                boolean imeiExist = new SubscriberDAO().checkImei(p_con, senderMsisdn, imei, userID);
                if (!imeiExist) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_IMEI);
                }
                pin = requestMessageArray[9];
                // incomingSmsStr.append("****"+" ");
                // if pin Invalid return with error(PIN is Mandatory)
                // if(actualPin.equalsIgnoreCase(PretupsI.DEFAULT_P2P_PIN))
                // whether PIN validation is required or not.
                if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                    if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                        // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                        if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                            BTSLUtil.validatePIN(pin);
                            p_senderVO.setPin(BTSLUtil.encryptText(pin));
                            p_senderVO.setPinUpdateReqd(true);
                            p_senderVO.setActivateStatusReqd(true);
                        }
                    } else {
                        try {
                            SubscriberBL.validatePIN(p_con, p_senderVO, pin);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }
                    }
                }
                amount = requestMessageArray[2];
                /*
                 * if(!BTSLUtil.isNumeric(amount))
                 * {
                 * throw new
                 * BTSLBaseException(this,"validateP2PAdhocRechargeMessageFormat"
                 * ,SelfTopUpErrorCodesI.INVALID_AMOUNT_NOTNUMERIC);
                 * }
                 */
                try {
                    tempAmt = Double.parseDouble(amount);
                    if (!BTSLUtil.isNullString(amount) && BTSLUtil.isDecimalValue(amount) && !(tempAmt <= 0)) {
                        // _finalAmount=PretupsBL.getSystemAmount(Double.parseDouble(_amount));
                        amount = tempAmt.toString();
                    }
                } catch (NumberFormatException nfe) {
                    _log.error("process", "Number Format Exception: amount can't be parse to an Int");
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_INVALID_AMOUNT);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.AUTO_TOPUP_INVALID_AMOUNT);
                }
                receiverMsisdn = requestMessageArray[1];
                if (BTSLUtil.isNullString(receiverMsisdn))
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_MSISDN_NULL);
                filteredMSISDN = PretupsBL.getFilteredMSISDN(receiverMsisdn.trim());
                if (!BTSLUtil.isValidMSISDN(filteredMSISDN))// &&
                                                            // filteredMSISDN.equals(p_requestVO.getRequestMSISDN())
                                                            // )
                {
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.ERROR_INVALID_MSISDN);
                }
                if (filteredMSISDN.equals(p_requestVO.getRequestMSISDN()))
                    receiverMsisdn = p_requestVO.getRequestMSISDN();

                // userID=p_senderVO.getUserID();
                // receiver msisdn validation begin
                receiverMsisdn = getSystemFilteredMSISDN(receiverMsisdn);
                if (!BTSLUtil.isValidMSISDN(receiverMsisdn)) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OperatorUtil[validateP2PAdhocRechargeMessageFormat]", "", receiverMsisdn, "", "Receiver MSISDN Not valid");
                    p_requestVO.setSenderMessageRequired(false);
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.P2P_ERROR_INVALID_RECEIVER_MSISDN);
                }

                bankName = PretupsI.DEFAULT_PAYMENT_GATEWAY;
                PaymentMethodKeywordVO paymentMethodKeywordVO = null;
                paymentMethodKeywordVO = PaymentMethodCache.getObject(bankName, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());
                if (paymentMethodKeywordVO == null) {
                    paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
                    if (paymentMethodType == null) {
                        // return with error message, no default payment method
                        // defined
                        throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
                    }
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setDefaultPaymentMethod("Y");
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, receiverMsisdn);
                } else {
                    paymentMethodType = paymentMethodKeywordVO.getPaymentMethodType();
                    p_transferVO.setPaymentMethodType(paymentMethodType);
                    p_transferVO.setPaymentMethodKeywordVO(paymentMethodKeywordVO);
                    p_transferVO.setDefaultPaymentMethod(PretupsI.NO);
                    // incomingSmsStr.append(paymentMethodType+" ");
                    checkAfterPaymentMethod(p_con, amount, p_transferVO, receiverMsisdn);
                }
                cardNumber = requestMessageArray[5];
                p_transferVO.setCardReferenceNo(cardNumber.substring(cardNumber.length() - 4, cardNumber.length()));
                holderName = requestMessageArray[4];
                expiryDate = requestMessageArray[6];
                cvv = requestMessageArray[8];
                CardDetailsVO cardDetailsVO = new CardDetailsVO();

                if (!BTSLUtil.isNullString(cardNumber) && BTSLUtil.isNumeric(cardNumber) && UtilValidate.isCreditCard(cardNumber)) {
                    String cardType = UtilValidate.getCardType(cardNumber);
                    // we're having only those card registered with the system
                    // which are allowed to do the transaction from the cards
                    // like VISA, MASTER, AM-EX
                    if (cardDetailsVO.getCardNumber().length() != 16) {
                        if (cardDetailsVO.getCardNumber().length() != 15)
                            throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_CREDITCARD_NUMBER);
                    }
                    if ("Unknown".equalsIgnoreCase(cardType)) {
                        throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_CREDITCARD_NUMBER);
                    } else
                        cardDetailsVO.setCardNumber(cardNumber);
                } else
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_CREDITCARD_NUMBER);
                if (!BTSLUtil.isNullString(cvv) && BTSLUtil.isNumeric(cvv) && requestMessageArray[8].length() == PretupsI.CVV_LENGTH)
                    cardDetailsVO.setCvv(cvv);
                else
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_CVV_NUMBER);
                if (holderName.length() > PretupsI.VALID_LENGTH_CARD_HOLDER_NAME) {
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_LENGTH_HOLDER_NAME);
                }
                if (BTSLUtil.isNullString(holderName)) {
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_HOLDER_NAME);
                }
                if (!BTSLUtil.isAlphaNumericIncludingSpace(holderName)) {
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_HOLDER_NAME);
                } else {
                    cardDetailsVO.setNameOfEmbossing(holderName);
                }
                Date date = new Date();
                if (BTSLUtil.isNullString(expiryDate)) {
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_EXPIRY_DATE);
                } else if (BTSLUtil.isValidDatePattern(expiryDate)) {
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_EXPIRY_DATE);
                } else if (BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.getDateFromDateString(expiryDate, "MM/yy")) <= 0) {
                    throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.INVALID_EXPIRY_DATE_BEFORE);
                } else {
                    cardDetailsVO.setExpiryDate(expiryDate);
                }
                p_senderVO.setCardDetailsVO(cardDetailsVO);
                selector = requestMessageArray[3];
                if (!BTSLUtil.isNumeric(selector)) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_SELECTOR_VALUE);
                } else {
                    p_requestVO.setReqSelector(selector);
                }

                break;

            }

            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateP2PAdhocRechargeMessageFormat", "  Exception while validation message :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsBL[validateP2PAdhocRechargeMessageFormat]", "", "", "", "Exception while validating message" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "validateP2PAdhocRechargeMessageFormat", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validateP2PAdhocRechargeMessageFormat", "Exiting");
        }

    }

    public String addRemoveDigitsFromMSISDN(String msisdn) {
        // this block is for Operator specific
        /*
         * if((msisdn.substring(0,1)).equals("0"))
         * msisdn=msisdn.substring(1,msisdn.length());
         */
        return msisdn;
    }

    public String[] getP2PChangePinMessageArray(String message[]) {
        return message;
    }

}