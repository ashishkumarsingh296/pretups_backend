package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyDAO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBuddiesDAO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.payment.businesslogic.PaymentMethodCache;
import com.btsl.pretups.payment.businesslogic.PaymentMethodKeywordVO;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

import java.util.Base64;

/**
 * @(#)MobinilUtil.java
 *                      Copyright(c) 2007, Bharti Telesoft Ltd.
 *                      All Rights Reserved
 * 
 *                      <description>
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Ashish Kumar Jul 2, 2007 Initital Creation
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 * 
 **/
public class MobinilUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method is responsible to format the transaction id for the C2S
     * Transfer.
     * Transaction id also contains the instance code.
     * 
     * @param TransferVO
     *            TransferVO
     * @param long p_tempTransferID
     */
    public String formatC2STransferID(TransferVO p_transferVO, long p_tempTransferID) {
        if (_log.isDebugEnabled()) {
            _log.debug("formatC2STransferID", " Entered p_transferVO:" + p_transferVO + " p_tempTransferID:" + p_tempTransferID);
        }
        final String METHOD_NAME = "formatC2STransferID";
        String returnStr = null;
        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // String currentYear=BTSLUtil.getFinancialYearLastDigits(2);
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), C2S_TRANSFER_ID_PAD_LENGTH);
            // returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr;
            final String instanceId = p_transferVO.getInstanceID();
            // As per discussed with Sanjay sir if instance id is null Handle
            // the event with level FATAL.
            if (BTSLUtil.isNullString(instanceId)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatC2STransferID]", "", "", "",
                    "Instance id is NULL");
                returnStr = null;
            }
            returnStr = "R" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants
                .getProperty("INSTANCE_ID") + paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatC2STransferID]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatC2STransferID", "Exiting returnStr:" + returnStr);
            }
        }
        return returnStr;
    }

    /**
     * Method formatPostpaidBillPayTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.MobinilUtilI#formatPostpaidBillPayTransferID(TransferVO,
     *      long)
     * 
     */
    public String formatPostpaidBillPayTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        if (_log.isDebugEnabled()) {
            _log.debug("formatC2STransferID", " Entered p_transferVO:" + p_transferVO + " p_tempTransferID:" + p_tempTransferID);
        }
        final String METHOD_NAME = "formatPostpaidBillPayTransferID";
        String returnStr = null;
        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // String currentYear=BTSLUtil.getFinancialYearLastDigits(2);
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), C2S_TRANSFER_ID_PAD_LENGTH);
            // returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr;
            String instanceId = p_transferVO.getInstanceID();
            //Taking separate INSTANCE_ID for each SMSP's
            instanceId = Constants.getProperty("INSTANCE_ID");
            // As per discussed with Sanjay sir if instance id is null Handle
            // the event with level FATAL.
            if (BTSLUtil.isNullString(instanceId)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatPostpaidBillPayTransferID]", "",
                    "", "", "Instance id is NULL");
                returnStr = null;
            }
            returnStr = "P" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + instanceId + paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatPostpaidBillPayTransferID]", "", "",
                "", "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatC2STransferID", "Exiting returnStr:" + returnStr);
            }
        }
        return returnStr;
    }

    /**
     * Method formatBillPayTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.MobinilUtilI#formatBillPayTransferID(TransferVO,
     *      long)
     * 
     */
    public String formatBillPayTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        if (_log.isDebugEnabled()) {
            _log.debug("formatC2STransferID", " Entered p_transferVO:" + p_transferVO + " p_tempTransferID:" + p_tempTransferID);
        }
        final String METHOD_NAME = "formatBillPayTransferID";
        String returnStr = null;
        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // String currentYear=BTSLUtil.getFinancialYearLastDigits(2);
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), C2S_TRANSFER_ID_PAD_LENGTH);
            // returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr;
            final String instanceId = p_transferVO.getInstanceID();
            // As per discussed with Sanjay sir if instance id is null Handle
            // the event with level FATAL.
            if (BTSLUtil.isNullString(instanceId)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatBillPayTransferID]", "", "", "",
                    "Instance id is NULL");
                returnStr = null;
            }
            returnStr = "B" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + instanceId + paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatBillPayTransferID]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatC2STransferID", "Exiting returnStr:" + returnStr);
            }
        }
        return returnStr;
    }

    /**
     * Method formatEVDTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatEVDTransferID(TransferVO,
     *      long)
     */
    public String formatEVDTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatEVDTransferID";
        try {
            // ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
            // String currentYear=BTSLUtil.getFinancialYearLastDigits(2);
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), C2S_TRANSFER_ID_PAD_LENGTH);
            // returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr;
            final String instanceId = p_transferVO.getInstanceID();
            // As per discussed with Sanjay sir if instance id is null Handle
            // the event with level FATAL.
            if (BTSLUtil.isNullString(instanceId)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatEVDTransferID]", "", "", "",
                    "Instance id is NULL");
                returnStr = null;
            }
            returnStr = "E" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + instanceId + paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatEVDTransferID]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatEVDTransferID", "Exiting returnStr:" + returnStr);
            }
        }
        return returnStr;
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
            String instanceId = p_transferVO.getInstanceID();
            //Taking separate INSTANCE_ID for each SMSP's
            instanceId = Constants.getProperty("INSTANCE_ID");
            // As per discussed with Sanjay sir if instance id is null Handle
            // the event with level FATAL.
            if (BTSLUtil.isNullString(instanceId)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatP2PTransferID]", "", "", "",
                    "Instance id is NULL");
                returnStr = null;
            }
            // returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr;
            if (p_transferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER)) {
                final String sch_type = p_transferVO.getRequestVO().getMcdScheduleType();
                if (sch_type.equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
                    returnStr = "W" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + instanceId + paddedTransferIDStr;
                }
                if (sch_type.equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
                    returnStr = "Q" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + instanceId + paddedTransferIDStr;
                }
            } else {
                returnStr = "C" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + instanceId + paddedTransferIDStr;
            }
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatP2PTransferID]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatP2PTransferID", "Exiting returnStr:" + returnStr);
            }
        }
        return returnStr;
    }

    public String removeZeroFromMSISDN(String msisdn) {
        // this block is for Operator specific
        if ((msisdn.substring(0, 1)).equals("0")) {
            msisdn = msisdn.substring(1, msisdn.length());
        }
        return msisdn;
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
        final String METHOD_NAME = "generateRandomPassword";
        // These variable will be used in generateRandomPassword()
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
     * To check the period after which the created password will be expired.
     */
    public boolean checkPasswordPeriodToResetAfterCreation(Date p_modifiedOn, ChannelUserVO p_channelUserVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("checkPasswordPeriodToResetAfterCreation", "Exited with passwordResetFlag :" + true);
        }

        return false;
    }

    public boolean handleLDCCRequest() {
        // customized according to mobinil requirement for all other it will
        // return false.
        return true;
    }

    // Added by Amit Raheja for NNP
    public String getSystemFilteredMSISDN(String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getSystemFilteredMSISDN", "Entered p_msisdn:" + p_msisdn);
        }
        final String METHOD_NAME = "getSystemFilteredMSISDN";
        String msisdn = null;
        boolean prefixFound = false;
        String prefix = null;
        String old_prefix = null;
        String new_prefix = null;
        String new_old_mapping = null;
        try {
            if (p_msisdn.length() >= ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("getSystemFilteredMSISDN", "((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)):" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)));
                }

                final StringTokenizer strTok = new StringTokenizer(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)), ",");
                while (strTok.hasMoreTokens()) {
                    prefix = strTok.nextToken();
                    if (p_msisdn.startsWith(prefix, 0)) {
                        prefixFound = true;
                        break;
                    } else {
                        continue;
                    }
                }
                if (prefixFound) {
                    msisdn = p_msisdn.substring(prefix.length());
                } else {
                    msisdn = p_msisdn;
                }

                final StringTokenizer strToken = new StringTokenizer(SystemPreferences.MSISDN_MIGRATION_LIST, ",");
                while (strToken.hasMoreTokens()) {
                    new_old_mapping = strToken.nextToken();
                    old_prefix = new_old_mapping.substring(0, new_old_mapping.indexOf(':'));
                    new_prefix = new_old_mapping.substring(new_old_mapping.indexOf(':') + 1);
                    // if(msisdn.startsWith(old_prefix,0)&&
                    // (old_prefix.length()==new_prefix.length() ||
                    // msisdn.length()<((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue() ))
                    if (msisdn.startsWith(old_prefix, 0) && ((old_prefix.length() == new_prefix.length() && msisdn.length() == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) || (old_prefix
                        .length() < new_prefix.length() && msisdn.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()))) {
                        msisdn = msisdn.replaceFirst(old_prefix, new_prefix);
                        break;
                    }
                }

            } else {
                msisdn = p_msisdn;
            }
        } catch (Exception e) {
            _log.error("getSystemFilteredMSISDN", "Exception while getting the mobile no from passed no=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[getSystemFilteredMSISDN]", "", p_msisdn,
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getSystemFilteredMSISDN", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getSystemFilteredMSISDN", "Exiting Filtered msisdn=" + msisdn);
            }
        }
        return msisdn;
    }

    // Addition ends
    /**
     * Method formatMultP2PTransferID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatMultP2PTransferID(TransferVO,
     *      long)
     */
    public String formatMultP2PTransferID(TransferVO p_transferVO, long p_tempTransferID) {
        String returnStr = null;
        final String METHOD_NAME = "formatMultP2PTransferID";
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), P2P_TRANSFER_ID_PAD_LENGTH);
            String instanceId = p_transferVO.getInstanceID();
            //Taking separate INSTANCE_ID for each SMSP's
            instanceId = Constants.getProperty("INSTANCE_ID");
            if (BTSLUtil.isNullString(instanceId)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatMultP2PTransferID]", "", "", "",
                    "Instance id is NULL");
                returnStr = null;
            }
            returnStr = "M" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + instanceId + paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilUtil[formatMultP2PTransferID]", "", "", "",
                "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatMultP2PTransferID", "Exiting returnStr:" + returnStr);
            }
        }
        return returnStr;
    }

    /**
     * Method that will validate the user message sent
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#validateEVDRequestFormat(Connection,
     *      C2STransferVO, RequestVO)
     */
    public void validateEVDRequestFormat(Connection p_con, C2STransferVO p_c2sTransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validateEVDRequestFormat";
        try {
            final String[] p_requestArr = p_requestVO.getRequestMessageArray();
            String custMsisdn = null;
            String requestAmtStr = null;
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_c2sTransferVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = (UserPhoneVO) channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = (UserPhoneVO) channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            final int messageLen = p_requestArr.length;
            if (_log.isDebugEnabled()) {
                _log.debug("validateEVDRequestFormat", "messageLen: " + messageLen);
            }
            for (int i = 0; i < messageLen; i++) {
                if (_log.isDebugEnabled()) {
                    _log.debug("validateEVDRequestFormat", "i: " + i + " value: " + p_requestArr[i]);
                }
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue()) {
                p_requestVO.setSenderMessageRequired(false);
            }
            switch (messageLen) {
                case 4:
                    {
                        // Do the 000 check Default PIN
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[3]);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }
                        final ReceiverVO receiverVO = new ReceiverVO();
                        // Customer MSISDN Validation
                        custMsisdn = p_requestArr[1];

                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                        // Changed on 27/05/07 for Service Type selector Mapping
                        final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                            .getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                        p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        break;
                    }

                case 5:
                    {
                        // Do the 000 check Default PIN
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[4]);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }
                        final ReceiverVO receiverVO = new ReceiverVO();
                        // Customer MSISDN Validation
                        custMsisdn = p_requestArr[1];

                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                                // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                                // Changed on 27/05/07 for Service Type selector
                                // Mapping
                                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                    .getServiceType());
                                if (serviceSelectorMappingVO != null) {
                                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                                }
                            }
                        } else {
                            p_requestVO.setReqSelector(p_requestArr[3]);
                        }
                        p_requestVO.setReceiverLocale(userPhoneVO.getLocale());
                        // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                        // Changed on 27/05/07 for Service Type selector Mapping
                        final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                            .getServiceType());
                        if (serviceSelectorMappingVO != null) {
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }
                        break;
                    }

                case 6:
                    {
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[5]);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }

                        final ReceiverVO receiverVO = new ReceiverVO();
                        // Customer MSISDN Validation
                        custMsisdn = p_requestArr[1];

                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                                // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                                // Changed on 27/05/07 for Service Type selector
                                // Mapping
                                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                    .getServiceType());
                                if (serviceSelectorMappingVO != null) {
                                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                                }
                            }
                        } else {
                            p_requestVO.setReqSelector(p_requestArr[3]);
                        }

                        PretupsBL.getSelectorValueFromCode(p_requestVO);
                        if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                            // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                            // Changed on 27/05/07 for Service Type selector
                            // Mapping
                            final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
                        }
                        if (BTSLUtil.isNullString(p_requestArr[4])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[4]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                throw new BTSLBaseException(this, "validateEVDRequestFormat", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }
                case 7:
                    {
                        if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                            try {
                                ChannelUserBL.validatePIN(p_con, channelUserVO, p_requestArr[6]);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                        }

                        final ReceiverVO receiverVO = new ReceiverVO();
                        // Customer MSISDN Validation
                        custMsisdn = p_requestArr[1];

                        PretupsBL.validateMsisdn(p_con, receiverVO, p_c2sTransferVO.getRequestID(), custMsisdn);

                        // Recharge amount Validation
                        requestAmtStr = p_requestArr[2];
                        PretupsBL.validateAmount(p_c2sTransferVO, requestAmtStr);
                        p_c2sTransferVO.setReceiverVO(receiverVO);
                        if (BTSLUtil.isNullString(p_requestArr[3])) {
                            if (PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage())) {
                                // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                                // Changed on 27/05/07 for Service Type selector
                                // Mapping
                                final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                    .getServiceType());
                                if (serviceSelectorMappingVO != null) {
                                    p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                                }
                            }
                        } else {
                            p_requestVO.setReqSelector(p_requestArr[3]);
                        }

                        PretupsBL.getSelectorValueFromCode(p_requestVO);
                        if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                            // p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                            // Changed on 27/05/07 for Service Type selector
                            // Mapping
                            final ServiceSelectorMappingVO serviceSelectorMappingVO = ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO
                                .getServiceType());
                            if (serviceSelectorMappingVO != null) {
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }
                        }
                        // For handling of sender locale
                        if (BTSLUtil.isNullString(p_requestArr[4])) {
                            p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[4]);
                            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        if (_log.isDebugEnabled()) {
                            _log.debug(this, "sender locale: =" + p_requestVO.getSenderLocale());
                        }

                        if (BTSLUtil.isNullString(p_requestArr[5])) {
                            p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                        } else {
                            final int langCode = PretupsBL.getLocaleValueFromCode(p_requestVO, p_requestArr[5]);
                            if (LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)) == null) {
                                throw new BTSLBaseException(this, "validateEVDRequestFormat", PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                            }
                            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        }
                        break;
                    }
                default:
                    throw new BTSLBaseException(this, "validateEVDRequestFormat", PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
            }
            // Self EVR Allowed Check
            final String senderMSISDN = (channelUserVO.getUserPhoneVO()).getMsisdn();
            final String receiverMSISDN = ((ReceiverVO) p_c2sTransferVO.getReceiverVO()).getMsisdn();
            if (p_c2sTransferVO.getServiceType().equals(PretupsI.SERVICE_TYPE_EVR)) {
                if (receiverMSISDN.equals(senderMSISDN) && !SystemPreferences.ALLOW_SELF_EVR) {
                    throw new BTSLBaseException(this, "validateEVDRequestFormat", PretupsErrorCodesI.CHNL_ERROR_SELF_TOPUP_NTALLOWD);
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validateEVDRequestFormat", "  Exception while validating user message :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validateEVDRequestFormat]", "", "", "",
                "Exception while validating user message" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "validateEVDRequestFormat", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validateEVDRequestFormat", "Exiting ");
        }

    }

    /**
     * 
     */
    public String DES3Encryption(String p_message, RequestVO p_requestvo) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("DES3Encryption", "Entered p_message=" + p_message);
        }
        final String METHOD_NAME = "DES3Encryption";
        byte[] byteMi = null;
        byte[] byteMing = null;
        String strMi = "";
        BASE64Encoder base64en = new BASE64Encoder();
        p_requestvo.setPrivateRechBinMsgAllowed(true);
        final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestvo.getSenderVO();
        String encrytKey = channelUserVO.getUserPhoneVO().getEncryptDecryptKey();
        if (encrytKey == null || encrytKey.length() == 0) {
            Connection con = null;MComConnectionI mcomCon = null;
            try {
                mcomCon = new MComConnection();con=mcomCon.getConnection();
                // Load the pos key of the msisdn
                final PosKeyVO posKeyVO = new PosKeyDAO().loadPosKeyByMsisdn(con, p_requestvo.getFilteredMSISDN());
                if (posKeyVO == null) {
                    _log.error("getEncryptionKeyForUser", p_requestvo.getRequestIDStr(),
                        " MSISDN=" + p_requestvo.getFilteredMSISDN() + " User Encryption Not found in Database");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "PretupsBL[isPlainMessageAndAllowed]",
                        p_requestvo.getRequestIDStr(), p_requestvo.getFilteredMSISDN(), "", "User Encryption Not found in Database for MSISDN=" + p_requestvo
                            .getFilteredMSISDN());
                    throw new BTSLBaseException("PretupsBL", "getEncryptionKeyForUser", PretupsErrorCodesI.CHNL_ERROR_SNDR_ENCR_KEY_NOTFOUND);
                }
                if (posKeyVO.isRegistered()) {
                    encrytKey = posKeyVO.getKey();
                }
                if (encrytKey == null || encrytKey.length() == 0) {
                    throw new BTSLBaseException("Encryption key not defined for MSISND=" + p_requestvo.getFilteredMSISDN());
                }
            } catch (BTSLBaseException bse) {
                _log.error("DES3Encryption", "Encryption key not defined for MSISND=" + p_requestvo.getFilteredMSISDN());
                throw bse;
            } catch (Exception e) {
                _log.error("DES3Encryption", "Encryption key not defined for MSISND=" + p_requestvo.getFilteredMSISDN());
                throw e;
            } finally {
				if (mcomCon != null) {
					mcomCon.close("MobinilUtil#DES3Encryption");
					mcomCon = null;
				}
			}
        }
        // String encrytKey="2F091607C0268AC9400A444ADFAC6E48";
        try {
            String sArithmeticname = "";
            if (encrytKey.length() == 16) {
                sArithmeticname = "DES";

            } else {
                sArithmeticname = "DESede";

                int m = 0;
                while (encrytKey.length() < 48) {
                    encrytKey = encrytKey + encrytKey.substring(m, m + 2);
                    m = m + 2;
                }
            }

            byteMing = p_message.getBytes("UTF8");

            byteMi = this.getEncCode(byteMing, sArithmeticname, encrytKey);

            strMi = base64en.encode(byteMi);
        } catch (Exception e) {
            throw new BTSLBaseException("DES3Encryption Error initializing SqlMap class. Cause:" + e);
        } finally {
            base64en = null;
            byteMing = null;
            byteMi = null;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("DES3Encryption", "Exiting strMi=" + strMi);
        }
        return strMi;
    }

    /**
     * Get Encryption byte[]
     * 
     * @param byteS
     * @return
     */
    private byte[] getEncCode(byte[] byteS, String p_sArithmeticname, String p_encrytKey) {
        byte[] byteFina = null;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(p_sArithmeticname);

            final SecretKey DesKey = new SecretKeySpec(hexStr2Bytes(p_encrytKey), p_sArithmeticname);

            cipher.init(Cipher.ENCRYPT_MODE, DesKey);
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            throw new RuntimeException("getEncCode Error initializing SqlMap class. Cause:" + e);
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    private static byte[] hexStr2Bytes(String src) {

        if (src == null || src.length() == 0) {
            return new byte[0];
        }

        int m = 0;
        int n = 0;
        final int l = src.length() / 2;

        final byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }

    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        final byte b1 = Byte.decode("0x" + src1).byteValue();
        final byte ret = (byte) (b0 | b1);
        return ret;
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
        final String[] requestMessageArray = p_requestVO.getRequestMessageArray();
        if (_log.isDebugEnabled()) {
            _log.debug("checkIfBuddy", " requestMessageArray length:" + requestMessageArray.length);
        }
        final String serviceKeyword = requestMessageArray[0];
        final String senderSubscriberType = ((SenderVO) p_transferVO.getSenderVO()).getSubscriberType();
        boolean cBuddy = false;
        final StringBuffer incomingSmsStr = new StringBuffer(serviceKeyword + " ");
        if (requestMessageArray.length < 2 || requestMessageArray.length > 8) {
            throw new BTSLBaseException(this, "checkIfBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
        }

        // if receiver buddy
        // Validate 2nd Argument for Payment Method Keyword.
        final String paymentMethodKeyword = requestMessageArray[1];

        // if paymentMethod invalid , Validate 2nd Argument for Receiver
        // No(MSISDN).
        final PaymentMethodKeywordVO paymentMethodKeywordVO = PaymentMethodCache.getObject(paymentMethodKeyword, p_transferVO.getServiceType(), p_transferVO.getNetworkCode());
        String paymentMethodType = null;
        if (paymentMethodKeywordVO == null) {
            paymentMethodType = ServicePaymentMappingCache.getDefaultPaymentMethod(p_transferVO.getServiceType(), senderSubscriberType);
            if (paymentMethodType == null) {
                // return with error message, no default payment method defined
                throw new BTSLBaseException(this, "checkIfBuddy", PretupsErrorCodesI.ERROR_NOTFOUND_DEFAULTPAYMENTMETHOD);
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
            if (requestMessageArray.length < 3) {
                throw new BTSLBaseException(this, "checkIfBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }

            cBuddy = checkAfterPaymentMethodForBuddy(p_con, 2, requestMessageArray, incomingSmsStr, p_transferVO, p_requestVO);
        }
        p_transferVO.setIncomingSmsStr(incomingSmsStr.toString());
        if (_log.isDebugEnabled()) {
            _log.debug("checkIfBuddy", " return value:" + cBuddy);
        }
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
     **/
    public boolean checkAfterPaymentMethodForBuddy(Connection p_con, int i, String[] p_requestMessageArray, StringBuffer incomingSmsStr, TransferVO p_transferVO, RequestVO p_requestVO) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("checkAfterPaymentMethodForBuddy", " i=" + i + " requestMessageArray length:" + p_requestMessageArray.length + " i=" + i);
        }
        final String METHOD_NAME = "checkAfterPaymentMethodForBuddy";
        //System.out.println("Inside checkAfterPaymentMethodForBuddy MobinilUtil version::");
        int incReq = 0;
        if (i == 2) {
            incReq = 1;
        }
        final String receiverMSISDN_NAME = p_requestMessageArray[1 + incReq];
        final BuddyVO buddyVO = new P2PBuddiesDAO().loadBuddyDetails(p_con, ((SenderVO) p_transferVO.getSenderVO()).getUserID(), receiverMSISDN_NAME);
        if (buddyVO == null) {
            return false;
        }
        final String receiverMSISDN = buddyVO.getMsisdn();
        final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
        if (networkPrefixVO == null) {
            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
        }
        buddyVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        buddyVO.setPrefixID(networkPrefixVO.getPrefixID());
        buddyVO.setSubscriberType(networkPrefixVO.getSeriesType());
        p_transferVO.setReceiverVO(buddyVO);
        incomingSmsStr.append(receiverMSISDN_NAME + " ");
        final int messageLength = p_requestMessageArray.length;
        String pin = null;
        long amount = 0;
        final SenderVO senderVO = (SenderVO) p_transferVO.getSenderVO();
        final String actualPin = BTSLUtil.decryptText(senderVO.getPin());

        /*
         * Message format that are supported are as:
         * Message length 2: PRC Name
         * If pin required
         * the PIN should be default
         * Amount will always be default
         * Message length 3:
         * PIN required and pin is not default
         * PRC Name PIN
         * (PIN required and actual pin=default pin) OR PIN not required
         * PRC HDFC Name
         * PRC Name Amount
         * Message length 4:
         * PIN Required
         * actual pin!=default pin
         * PRC Name Amount PIN
         * PRC HDFC NAme PIN
         * actual=default
         * PRC HDFC Name Amount
         * PRC Name Amount langCode
         * PIN not required
         * PRC HDFC name Amount
         * PRC Name Amount langCode
         * Message length 5:
         * PIN Required
         * actual!=default
         * PRC HDFC Name Amount PIN
         * PRC Name Amount langCode PIN
         * Actual=default
         * PRC HDFC Name Amount langCode
         * PRC Name Amount selector lang
         * PIN not required
         * PRC HDFC Name Amount LangCode
         * PRC Name Amount selector LangCode
         * Message length 6:
         * PIN Required
         * Actual!=default
         * PRC HDFC Name Amount langCode PIN
         * PRC Name Amount selector langCode PIN
         * Actual=default
         * PRC HDFC Name Amount selector langCode
         * PRC Name Amount selector langCode PIN(Update with new PIN)
         * PIN not required
         * PRC HDFC Name Amount selector langCode
         * PRC Name Amount Selector langCode PIN( Update with new PIN)
         * Message length 7:
         * PIN Required
         * Actual!=default
         * PRC HDFC Name Amount selector langCode PIN
         * Actual=default
         * PRC HDFC Name Amount selector langCode PIN(Update with new PIN)
         * PIN not required
         * PRC HDFC Name Amount selector langCode PIN (Update with new PIN)
         */
        switch (messageLength) {
            case 2:
                {
                    if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                        // whether PIN validation is required or not.
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                            if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0,
                                    new String[] { p_requestVO.getActualMessageFormat() }, null);
                            }
                        }
                    }
                    amount = buddyVO.getPreferredAmount();
                    if (amount < 0) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                    }
                    p_transferVO.setTransferValue(amount);
                    p_transferVO.setRequestedAmount(amount);
                    incomingSmsStr.append(amount + " ");
                    break;
                }
            case 3:
                {
                    // if((((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())&&!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                    if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && ((((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED,
                        networkPrefixVO.getNetworkCode())).booleanValue()) && !actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                        if (i == 2) {
                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
                        } else {
                            pin = p_requestMessageArray[2];
                            incomingSmsStr.append("****" + " ");
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                            amount = buddyVO.getPreferredAmount();
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            p_transferVO.setTransferValue(amount);
                            p_transferVO.setRequestedAmount(amount);
                            incomingSmsStr.append(amount + " ");
                        }
                    } else {
                        if (i == 2) {
                            amount = buddyVO.getPreferredAmount();
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                        } else {
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    }
                    break;
                }
            case 4:
                {
                    if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                        // if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                        if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                            pin = p_requestMessageArray[3];
                            incomingSmsStr.append("****" + " ");
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                            if (i == 2) {
                                amount = buddyVO.getPreferredAmount();
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }

                            }
                        } else {
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[3]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    } else {
                        if (i == 2) {
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                        } else {
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[3]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    }
                    break;
                }
            case 5:
                {
                    if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                        // if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                        if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                            pin = p_requestMessageArray[4];
                            incomingSmsStr.append("****" + " ");
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[3]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[3]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                            }
                            p_transferVO.setTransferValue(amount);
                            p_transferVO.setRequestedAmount(amount);
                            incomingSmsStr.append(amount + " ");
                        } else {
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }

                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
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
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                        } else {
                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                            if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    }
                    break;
                }
            case 6:
                {
                    if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                        // if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                        if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                            pin = p_requestMessageArray[5];
                            incomingSmsStr.append("****" + " ");
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            }
                        } else {
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            } else {
                                pin = p_requestMessageArray[5];
                                incomingSmsStr.append("****" + " ");
                                validatePIN(pin);
                                senderVO.setPin(BTSLUtil.encryptText(pin));
                                senderVO.setPinUpdateReqd(true);

                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
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
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }
                        } else {
                            // To check whether PIN validation is required or
                            // not.
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                                if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                    pin = p_requestMessageArray[5];
                                    incomingSmsStr.append("****" + " ");
                                    validatePIN(pin);
                                    senderVO.setPin(BTSLUtil.encryptText(pin));
                                    senderVO.setPinUpdateReqd(true);
                                }
                            }

                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                            if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        incomingSmsStr.append(amount + " ");
                    }
                    break;
                }
            case 7:
                {
                    /*
                     * Actual!=default
                     * PRC HDFC Name Amount selector langCode PIN
                     * Actual=default
                     * PRC HDFC Name Amount selector langCode PIN(Update with
                     * new
                     * PIN)
                     */
                    if (i == 1) {
                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO
                            .getActualMessageFormat() }, null);
                    } else {
                        // if(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED,networkPrefixVO.getNetworkCode())).booleanValue())
                        if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED,
                            networkPrefixVO.getNetworkCode())).booleanValue()) {
                            if (!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                pin = p_requestMessageArray[6];
                                incomingSmsStr.append("****" + " ");
                                try {
                                    SubscriberBL.validatePIN(p_con, senderVO, pin);
                                } catch (BTSLBaseException be) {
                                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                        p_con.commit();
                                    }
                                    throw be;
                                }

                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            } else {
                                // To check whether PIN validation is required
                                // or
                                // not.
                                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                                    pin = p_requestMessageArray[6];
                                    incomingSmsStr.append("****" + " ");
                                    validatePIN(pin);
                                    senderVO.setPin(BTSLUtil.encryptText(pin));
                                    senderVO.setPinUpdateReqd(true);
                                }

                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            }
                            p_transferVO.setTransferValue(amount);
                            p_transferVO.setRequestedAmount(amount);
                            incomingSmsStr.append(amount + " ");
                        } else {
                            // To check whether PIN validation is required or
                            // not.
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                                if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                    pin = p_requestMessageArray[6];
                                    incomingSmsStr.append("****" + " ");
                                    validatePIN(pin);
                                    senderVO.setPin(BTSLUtil.encryptText(pin));
                                    senderVO.setPinUpdateReqd(true);
                                }
                            }

                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }

                            p_transferVO.setTransferValue(amount);
                            p_transferVO.setRequestedAmount(amount);
                            incomingSmsStr.append(amount + " ");
                        }
                        break;
                    }
                }

            case 8:
                {
                    //System.out.println("Inside case 8 checkAfterPaymentMethodForBuddy ::");
                    if (((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.BUDDY_PIN_REQUIRED, networkPrefixVO.getNetworkCode())).booleanValue()) {
                        // if(!actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))
                        if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                            pin = p_requestMessageArray[5];
                            incomingSmsStr.append("****" + " ");
                            try {
                                SubscriberBL.validatePIN(p_con, senderVO, pin);
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                    .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                                    p_con.commit();
                                }
                                throw be;
                            }
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                            } else {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            }
                        } else {
                            if (i == 2) {
                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[3]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReqSelector("" + selectorValue);
                                }
                            } else {
                                pin = p_requestMessageArray[5];
                                incomingSmsStr.append("****" + " ");
                                validatePIN(pin);
                                senderVO.setPin(BTSLUtil.encryptText(pin));
                                senderVO.setPinUpdateReqd(true);

                                amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                                if (amount < 0) {
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                                }
                                try {
                                    if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                        final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                        if (p_requestVO.getReceiverLocale() == null) {
                                            throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                        }
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                    throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                }
                                if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                    final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
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
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[5])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[5]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[5]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                            if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                final int selectorValue = Integer.parseInt(p_requestMessageArray[4]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }
                        } else {
                            // To check whether PIN validation is required or
                            // not.
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                                if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                                    pin = p_requestMessageArray[5];
                                    incomingSmsStr.append("****" + " ");
                                    validatePIN(pin);
                                    senderVO.setPin(BTSLUtil.encryptText(pin));
                                    senderVO.setPinUpdateReqd(true);
                                }
                            }

                            amount = PretupsBL.getSystemAmount(p_requestMessageArray[2]);
                            if (amount < 0) {
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
                            }
                            try {
                                if (!BTSLUtil.isNullString(p_requestMessageArray[4])) {
                                    final int localeValue = Integer.parseInt(p_requestMessageArray[4]);
                                    p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(p_requestMessageArray[4]));
                                    if (p_requestVO.getReceiverLocale() == null) {
                                        throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                                    }
                                }
                            } catch (Exception e) {
                                _log.errorTrace(METHOD_NAME, e);
                                throw new BTSLBaseException(this, "checkAfterPaymentMethodForBuddy", PretupsErrorCodesI.ERROR_INVALID_LANGUAGE_SEL_VALUE);
                            }
                            if (!BTSLUtil.isNullString(p_requestMessageArray[3])) {
                                final int selectorValue = Integer.parseInt(p_requestMessageArray[3]);
                                p_requestVO.setReqSelector("" + selectorValue);
                            }
                        }
                        p_transferVO.setTransferValue(amount);
                        p_transferVO.setRequestedAmount(amount);
                        p_requestVO.setMcdScheduleType(p_requestMessageArray[6]);
                        p_requestVO.setMcdNextScheduleDate((p_requestMessageArray[7]));
                        //System.out.println("sch_type::" + p_requestVO.getMcdScheduleType());
                        //System.out.println("sch_date::" + p_requestVO.getMcdNextScheduleDate());
                        p_transferVO.setRequestVO(p_requestVO);
                        //System.out.println("TrnsferVO::" + p_transferVO.getRequestVO().getMcdScheduleType());
                        incomingSmsStr.append(amount + " ");
                    }
                    break;
                }
            default:
                {
                    throw new BTSLBaseException(this, "checkIfBuddy", PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() },
                        null);
                }

        }
        return true;
    }

}
