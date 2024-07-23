package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author akanksha.gupta
 *
 */
public class DigitelUtil extends OperatorUtil {
	 /** LOG object for writing log*/
	private Log _log = LogFactory.getLog(this.getClass().getName());

   
	    /**
	     * 
	     * This method validatePINRules the requested PIN business rules
	     * 
	     * @param p_requestPin
	     * @throws BTSLBaseException
	     * @author akanksha.gupta
	     */
	    public void validatePINRules(String p_requestPin) throws BTSLBaseException {
	         String methodName ="validatePINRules";
	    	if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "  Entered p_transferVO:" + p_requestPin);
	        }

	        if (BTSLUtil.isNullString(p_requestPin)) {
	            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PIN_INVALID);
	        } else if (!BTSLUtil.isNumeric(p_requestPin)) {
	            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NEWPIN_NOTNUMERIC);
	        } else if (p_requestPin.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue() || p_requestPin.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) {
	            final String msg[] = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) };
	            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PIN_LENGTHINVALID, 0, msg, null);
	        }

	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, PretupsI.EXITED);
	        }
	    }

	    public void handleConfirmTransferMessageFormat(RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
	    	String methodName ="handleConfirmTransferMessageFormat";
	    	if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered p_requestVO=" + p_requestVO + " p_transferVO=" + p_transferVO);// requestMessageArray
	        }

	        try {

	            final String[] requestMessageArray = p_requestVO.getRequestMessageArray();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, " requestMessageArray length:" + requestMessageArray);
	            }
	            if (requestMessageArray.length < 3 || requestMessageArray.length > 7) {
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_INVALID_MESSAGEFORMAT, 0, new String[] { p_requestVO
	                    .getActualMessageFormat() }, null);
	            }

	            final int messageLength = requestMessageArray.length;
	            String pin = null;
	            long amount = 0;
	            String receiverMSISDN = null;

	            switch (messageLength) {
	                case 3:
	                    {
	                        receiverMSISDN = requestMessageArray[1];
	                        amount = PretupsBL.getSystemAmount(requestMessageArray[2]);
	                        break;
	                    }
	                case 4:
	                    {
	                        pin = requestMessageArray[3];
	                        receiverMSISDN = requestMessageArray[1];
	                        amount = PretupsBL.getSystemAmount(requestMessageArray[2]);
	                        break;
	                    }
	                case 5:
	                    {
	                        // Validate 2nd Argument for PIN.
	                        pin = requestMessageArray[4];
	                        receiverMSISDN = requestMessageArray[2];
	                        amount = PretupsBL.getSystemAmount(requestMessageArray[3]);
	                        break;
	                    }
	                case 6:
	                    {
	                        // Validate 2nd Argument for PIN.
	                        pin = requestMessageArray[5];
	                        receiverMSISDN = requestMessageArray[2];
	                        amount = PretupsBL.getSystemAmount(requestMessageArray[3]);
	                        break;
	                    }
	                case 7:
	                    {
	                        // Validate 2nd Argument for PIN.
	                        pin = requestMessageArray[6];
	                        receiverMSISDN = requestMessageArray[2];
	                        amount = PretupsBL.getSystemAmount(requestMessageArray[3]);
	                        break;
	                    }
	            }

	            if (messageLength != 3) {

	               
	                this.validatePINRules(pin);
	            }
	            receiverMSISDN = PretupsBL.getFilteredMSISDN(receiverMSISDN);
	            if (!BTSLUtil.isValidMSISDN(receiverMSISDN)) {
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_MSISDN, 0, new String[] { receiverMSISDN }, null);
	            }
	            final ReceiverVO _receiverVO = new ReceiverVO();
	            _receiverVO.setMsisdn(receiverMSISDN);
	            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
	            if (networkPrefixVO == null) {
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN },
	                    null);
	            }
	            _receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
	            _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
	            _receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
	            p_transferVO.setReceiverVO(_receiverVO);

	            if (amount < 0) {
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.P2P_ERROR_AMOUNT_LESSZERO);
	            }
	            p_transferVO.setTransferValue(amount);
	            p_transferVO.setRequestedAmount(amount);

	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (Exception e) {
	            throw e;
	        } finally {
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, PretupsI.EXITED);
	            }
	        }
	    }



	 


	    /**
	     * Method to set the value for transfer.
	     * This methos is called from CardGroupBL. At this time this method set
	     * various values in the transferVO
	     * For any other operator who wants to run system on CVG mode, we can change
	     * values setting based on subservice.
	     * 
	     * @param p_subService
	     *            String
	     * @param p_cardGroupDetailVO
	     *            CardGroupDetailsVO
	     * @param p_transferVO
	     *            TransferVO
	     * @throws Exception
	     * @see com.btsl.pretups.util.OperatorUtilI#setCalculatedCardGroupValues(String,
	     *      CardGroupDetailsVO, TransferVO)
	     */
	    public void setCalculatedCardGroupValues(String p_subService, CardGroupDetailsVO p_cardGroupDetailVO, TransferVO p_transferVO) throws Exception {
	       String methodName="setCalculatedCardGroupValues";
	    	_log.debug(methodName, PretupsI.ENTERED);
	        try {
	            TransferItemVO transferItemVO = null;
	            final int bonusValidityValue = Integer.parseInt(String.valueOf(p_cardGroupDetailVO.getBonusValidityValue()));
	            final int validityPeriodValue = p_cardGroupDetailVO.getValidityPeriod();
	            final long transferValue = p_cardGroupDetailVO.getTransferValue();
	            final long bonusValue = p_cardGroupDetailVO.getBonusTalkTimeValue();
	            transferItemVO = (TransferItemVO) p_transferVO.getTransferItemList().get(1);
	            // Calculate the validities when CARD_GROUP_SELECTOR=1 i.e. for Main
	            // account.
	            if ((String.valueOf(PretupsI.CHNL_SELECTOR_CVG_VALUE)).equals(p_subService))// CVG
	            {
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
	            }
	            // Calculate the validities when CARD_GROUP_SELECTOR=2 i.e. for SMS
	            // account.
	            if ((String.valueOf(PretupsI.CHNL_SELECTOR_C_VALUE)).equals(p_subService))// C
	            {
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
	            }
	            // Calculate the validities when CARD_GROUP_SELECTOR=3 i.e. for MMS
	            // account.
	            if ((String.valueOf(PretupsI.CHNL_SELECTOR_VG_VALUE)).equals(p_subService))// VG
	            {
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
	            }
	        } catch (Exception e) {
	            throw e;
	        }
	        _log.debug(methodName, PretupsI.EXITED);
	    }

	    /**
	     * Method for checking Pasword or already exist in Pin_Password_history
	     * table or not.
	     * 
	     * @param p_con
	     *            java.sql.Connection
	     * @param p_modificationType
	     *            String
	     * @param p_loginId
	     *            String
	     * @param p_newPassword
	     *            String
	     * @return flag boolean
	     * @throws BTSLBaseException
	     */
	    private boolean checkPasswordHistory(Connection p_con, String p_modificationType, String p_loginId, String p_newPassword) throws BTSLBaseException {
	     
	        final String methodName = "checkPasswordHistory";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered: p_modification_type=" + p_modificationType + "p_loginId=" + p_loginId + " p_newPassword= " + p_newPassword);
	        }
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        boolean existFlag = false;
	        final StringBuffer strBuff = new StringBuffer();
	        strBuff.append(" SELECT pin_or_password,modified_on FROM (SELECT pin_or_password,modified_on,  row_number()  over (ORDER BY modified_on DESC) rn  ");
	        strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND msisdn_or_loginid=? )x  WHERE rn <= ? ");
	        strBuff.append(" ORDER BY modified_on DESC ");
	        final String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        }
	        try {

	            pstmt = p_con.prepareStatement(sqlSelect);
	            pstmt.setString(1, p_modificationType);
	            pstmt.setString(2, p_loginId);
	            pstmt.setInt(3, ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PASS_NOT_ALLOW))).intValue());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                if (rs.getString("Pin_or_Password").equals(p_newPassword)) {
	                    existFlag = true;
	                    break;
	                }
	            }
	            return existFlag;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException : " + sqe);
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "",
	                "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	            _log.error(methodName, "Exception : " + ex);
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[checkPasswordHistory]", "", "", "",
	                "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "QUERY pstmt=   " + pstmt);
	            }
	            try {
	                if (pstmt != null) {
	                    pstmt.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
	            }
	        }
	    }

	    /**
	     * @author diwakar
	     * @date: 09-JUNE-2014
	     */
	    public HashMap validatePassword(String p_loginID, String p_password) {
	        final String methodName = "validatePassword";
	        _log.debug(methodName, "Entered, p_userID= ", new String(p_loginID + ", Password= " + p_password));
		       
	        boolean passwordExist = false;
	        final HashMap messageMap = new HashMap();
	        boolean specialCharFlag = false;
	        boolean numberStrFlag = false;
	        boolean capitalLettersFlag = false;
	        boolean smallLettersFlag = false;
	        boolean validPassword = false;
	        boolean firstcapitalLettersFlag = false;

	        String defaultPasswd = BTSLUtil.getDefaultPasswordNumeric(p_password);
	        if (defaultPasswd.equals(p_password)) {
	            return messageMap;
	        }
	        defaultPasswd = BTSLUtil.getDefaultPasswordText(p_password);
	        if (defaultPasswd.equals(p_password)) {
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

	        // Should contains Special character
	        final String specialChar = Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
	        if (!BTSLUtil.isNullString(specialChar)) {
	            final String[] specialCharArray = { specialChar };
	            final String[] passwordCharArray = specialChar.split(",");

	            for (int i = 0, j = passwordCharArray.length; i < j; i++) {
	                if (p_password.contains(passwordCharArray[i])) {
	                    specialCharFlag = true;
	                    break;
	                }
	            }
	        }

	        // Should contains Number
	        final String[] passwordNumberStrArray = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

	        for (int i = 0, j = passwordNumberStrArray.length; i < j; i++) {
	            if (p_password.contains(passwordNumberStrArray[i])) {
	                numberStrFlag = true;
	                break;
	            }
	        }
	        // Should not contains 1'st character Capital
	        final char[] firstLetter = p_password.toCharArray();
	        if (!String.valueOf(firstLetter[0]).matches("[A-Z]")) {
	            firstcapitalLettersFlag = true;
	        }
	        _log.debug(methodName, "firstcapitalLettersFlag::" + firstcapitalLettersFlag);

	        // Should contains Capital Letters
	        if (p_password.matches(".*[A-Z].*")) {
	            capitalLettersFlag = true;
	        }
	        _log.debug(methodName, "capitalLettersFlag::" + capitalLettersFlag);

	        // Should contains Small Letters
	        if (p_password.matches(".*[a-z].*")) {
	            smallLettersFlag = true;
	        }
	        _log.debug(methodName, "smallLettersFlag::" + smallLettersFlag);

	        // D+C+S
	        if (specialCharFlag && firstcapitalLettersFlag && numberStrFlag && capitalLettersFlag && smallLettersFlag) {
	            validPassword = true;
	        } else if (specialCharFlag && firstcapitalLettersFlag && numberStrFlag && capitalLettersFlag) {
	            validPassword = true;
	        } else if (specialCharFlag && firstcapitalLettersFlag && numberStrFlag && smallLettersFlag) {
	            validPassword = true;
	        } else if (specialCharFlag && firstcapitalLettersFlag && capitalLettersFlag && smallLettersFlag) {
	            validPassword = true;
	        } else if (specialCharFlag && firstcapitalLettersFlag && numberStrFlag) {
	            validPassword = true;
	        } else if (specialCharFlag && firstcapitalLettersFlag && capitalLettersFlag) {
	            validPassword = true;
	        } else if (specialCharFlag && firstcapitalLettersFlag && smallLettersFlag) {
	            validPassword = true;
	        }

	        // extra validation as per MALI requirement

	        // Check Sequential
	        final boolean isSequential = findSequential(p_password);
	        if (isSequential) {
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Sequential Found");
	            }
	            if (validPassword) {
	                validPassword = false;
	            }
	        } else {
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Sequential Not Found");
	            }

	            // Check Repetation
	            final boolean isRepetation = findRepetation(p_password, specialChar);
	            if (isRepetation) {
	                if (_log.isDebugEnabled()) {
	                    _log.debug(methodName, "Repetation Found");
	                }
	                if (validPassword) {
	                    validPassword = false;
	                }
	            } else {
	                if (_log.isDebugEnabled()) {
	                    _log.debug(methodName, "Repetation Not Found");
	                }

	            }
	        }
	        // Ended Here

	        if (!validPassword) {
	            messageMap.put("operatorutil.validatepassword.error.passwordmusthaverequiredchar4m", null);
	        }

	        Connection con = null;
	        MComConnectionI mcomCon = null;
	        if (!BTSLUtil.isNullString(p_loginID)) {
	            if (p_password.contains(p_loginID)) {
	                messageMap.put("operatorutil.validatepassword.error.sameusernamepassword", null);
	            }

	            try {
	                mcomCon = new MComConnection();
	                con=mcomCon.getConnection();
	                passwordExist = this.checkPasswordHistory(con, PretupsI.USER_PASSWORD_MANAGEMENT, p_loginID, p_password);
	                if (passwordExist) {
	                    messageMap.put("user.modifypwd.error.newpasswordexistcheck", ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PASS_NOT_ALLOW))).intValue());
	                }

	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }

	            finally {
	            	if(mcomCon != null)
	            	{
	            		mcomCon.close("MaliUtil#validatePassword");
	            		mcomCon=null;
	            		}
	            }
	        }
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Exiting ");
	        }
	        return messageMap;
	    }

	    public HashMap pinValidate(String p_pin) {
	        final String methodName = "pinValidate";
	        _log.debug(methodName, "Entered, PIN= " + p_pin);
	        final HashMap messageMap = new HashMap();

	        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_pin);
	        if (defaultPin.equals(p_pin)) {
	            return messageMap;
	        }

	        defaultPin = BTSLUtil.getDefaultPasswordText(p_pin);
	        if (defaultPin.equals(p_pin)) {
	            return messageMap;
	        }

	        if (!BTSLUtil.isNumeric(p_pin)) {
	            messageMap.put("operatorutil.validatepin.error.pinnotnumeric", null);
	        }
	        if (p_pin.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue() || p_pin.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) {
	            final String[] args = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) };
	            messageMap.put("operatorutil.validatepin.error.smspinlenerr", args);
	        }
	        final int result = BTSLUtil.isSMSPinValid(p_pin);
	        if (result == -1) {
	            messageMap.put("operatorutil.validatepin.error.pinsamedigit", null);
	        } else if (result == 1) {
	            messageMap.put("operatorutil.validatepin.error.pinconsecutive", null);
	        }

	        // Should contains Maximum 2 repetation
	        if (!isValidRepetation(p_pin)) {
	            String msg[] = { "2" };
	            try {
	                msg = new String[] { Constants.getProperty("NO_OF_REPETATION_ALLOWDED_IN_PIN_VALIDATION") };
	            } catch (RuntimeException e) {
	                msg = new String[] { "2" };
	                _log.errorTrace(methodName, e);
	            }
	            messageMap.put("operatorutil.validatepin.error.pinrepetative", msg);

	        }

	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Exiting messageMap.size()=" + messageMap.size());
	        }
	        return messageMap;
	    }

	    public void validatePIN(String p_pin) throws BTSLBaseException {
	       
	        final String methodName = "validatePIN";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered, p_pin= " + p_pin);
	        }
	        if (BTSLUtil.isNullString(p_pin)) {
	            throw new BTSLBaseException("BTSLUtil", methodName, PretupsErrorCodesI.PIN_INVALID);
	        } else if (!BTSLUtil.isNumeric(p_pin)) {
	            throw new BTSLBaseException("BTSLUtil", methodName, PretupsErrorCodesI.NEWPIN_NOTNUMERIC);
	        } else if (p_pin.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue() || p_pin.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) {
	            final String msg[] = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) };
	            throw new BTSLBaseException("BTSLUtil", methodName, PretupsErrorCodesI.PIN_LENGTHINVALID, 0, msg, null);
	        }
	        // Should contains Maximum 2 repetation
	        else if (!isValidRepetation(p_pin)) {
	            String msg[] = { "2" };
	            try {
	                msg = new String[] { Constants.getProperty("NO_OF_REPETATION_ALLOWDED_IN_PIN_VALIDATION") };
	            } catch (RuntimeException e) {
	                msg = new String[] { "2" };
	                _log.errorTrace(methodName, e);
	            }
	            throw new BTSLBaseException("BTSLUtil", methodName, PretupsErrorCodesI.PIN_REPETATION_INVALID, 0, msg, null);
	        } else {
	            // Commented the below line as Mali is maintaing the old PIN policy

	            final int result = isSMSPinValid(p_pin);
	            if (result == -1) {
	                throw new BTSLBaseException("BTSLUtil", methodName, PretupsErrorCodesI.PIN_SAMEDIGIT);
	            } else if (result == 1) {
	                throw new BTSLBaseException("BTSLUtil", methodName, PretupsErrorCodesI.PIN_CONSECUTIVE);
	            }
	        }
	    }

	    /**
	     * Validate the string of repetation not more than configured
	     * 
	     * @param p_string
	     * @author diwakar
	     * @date : 09-06-2014
	     * @return
	     */
	    public boolean isValidRepetation(String p_string) {
	        final String methodName = "isValidRepetation";
		    
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "p_string= " + p_string);
	        }
	        boolean result = true;
	        int noOfConfiguredRepetation = 2;
	        final HashMap<String, Integer> hashMap = new HashMap<String, Integer>();

	        try {
	            noOfConfiguredRepetation = Integer.parseInt(Constants.getProperty("NO_OF_REPETATION_ALLOWDED_IN_PIN_VALIDATION"));

	        } catch (RuntimeException e) {
	            noOfConfiguredRepetation = 2;
	            _log.errorTrace(methodName, e);
	        }

	        for (int i = 0; i < p_string.length(); i++) {

	            if (hashMap.containsKey(p_string.substring(i, i + 1))) {
	                hashMap.put(p_string.substring(i, i + 1), hashMap.get(p_string.substring(i, i + 1)) + 1);
	            } else {
	                hashMap.put(p_string.substring(i, i + 1), 1);
	            }
	        }

	        final Set<String> keySet = hashMap.keySet();
	        final Iterator i = keySet.iterator();
	        int repetaionCount = 0;
	        int foundCount = 0;
	        while (i.hasNext()) {
	            final String repetaionKey = (String) i.next();
	            repetaionCount = hashMap.get(repetaionKey);
	            if (repetaionCount > noOfConfiguredRepetation) {
	                if (_log.isDebugEnabled()) {
	                    _log.debug(methodName, "repetaionKey= " + repetaionKey + " Ocurred " + repetaionCount + " times");
	                }
	                result = false;
	                break;
	            } else if (repetaionCount == noOfConfiguredRepetation) {
	                foundCount++;
	            }
	            if (foundCount > noOfConfiguredRepetation) {
	                if (_log.isDebugEnabled()) {
	                    _log.debug(methodName, " Repetation Ocurred " + foundCount + " times");
	                }
	                result = false;
	            }

	        }
	        return result;
	    }

	    /**
	     * Validate the sequential of the string
	     * 
	     * @param p_string
	     * @author diwakar
	     * @date : 12-06-2014
	     * @return
	     */
	    private boolean findSequential(String p_password) {

	        if (_log.isDebugEnabled()) {
	            _log.debug("findSequential", "Entered p_password = " + p_password);
	        }
	        final char[] password = p_password.toCharArray();
	        final int passwordLength = password.length;
	        int currentCounter = 0;
	        int matchFoundAlpha = 1;
	        int matchFoundNumber = 1;
	        boolean isSequential = false;

	        while ((currentCounter + 1) < passwordLength) {
	            final String str = String.valueOf(password[currentCounter]);

	            
	            if (str.matches("[a-z]$")) {
	                
	                final int ascciValFirst = (int) password[currentCounter];
	                final int ascciValSecond = (int) password[currentCounter + 1];
	                if (((ascciValFirst + 1) == ascciValSecond) || ((ascciValFirst + 1) == (ascciValSecond + 32))) {
	                    matchFoundAlpha++;
	                    isSequential = true;
	                    return isSequential;
	                }
	            } else if (str.matches("[A-Z]$")) {

	                
	                final int ascciValFirst = (int) password[currentCounter];
	                final int ascciValSecond = (int) password[currentCounter + 1];
	                if (((ascciValFirst + 1) == ascciValSecond) || ((ascciValFirst + 1) == (ascciValSecond - 32))) {
	                    matchFoundAlpha++;
	                    isSequential = true;
	                    return isSequential;
	                }
	            } else if (str.matches("[0-9]$")) {

	                final int ascciValFirst = (int) password[currentCounter];
	                final int ascciValSecond = (int) password[currentCounter + 1];
	                if ((ascciValFirst + 1) == ascciValSecond) {
	                    matchFoundNumber++;
	                    isSequential = true;
	                    return isSequential;
	                }
	            }
	            currentCounter++;

	        }
	        if (matchFoundAlpha > 2 || matchFoundNumber > 2) {
	            isSequential = true;
	        } else {
	            isSequential = false;
	        }

	        return isSequential;
	    }

	    /**
	     * Validate the repetation of the string
	     * 
	     * @param p_string
	     * @author diwakar
	     * @date : 12-06-2014
	     * @return
	     */
	    private boolean findRepetation(String p_password, String allowdedSpecialChars) {

	        if (_log.isDebugEnabled()) {
	            _log.debug("findRepetation", "Entered p_password = " + p_password + " allowdedSpecialChars = " + allowdedSpecialChars);
	        }
	        final char[] password = p_password.toCharArray();
	        final int passwordLength = password.length;
	        int currentCounter = 0;
	        boolean isRepetation = false;

	        final String[] allowdedSpecialCharArray = allowdedSpecialChars.split(",");

	        while ((currentCounter + 1) < passwordLength) {
	            final String str = String.valueOf(password[currentCounter]);

	           
	            if (str.matches("[a-z]$")) {
	                
	                final int ascciValFirst = (int) password[currentCounter];
	                final int ascciValSecond = (int) password[currentCounter + 1];
	                if ((ascciValFirst == ascciValSecond) || ((ascciValFirst) == (ascciValSecond + 32))) {
	                    isRepetation = true;
	                }
	            } else if (str.matches("[A-Z]$")) {
	                
	                final int ascciValFirst = (int) password[currentCounter];
	                final int ascciValSecond = (int) password[currentCounter + 1];
	                if ((ascciValFirst == ascciValSecond) || (ascciValFirst == (ascciValSecond - 32))) {
	                    isRepetation = true;
	                }
	            } else if (str.matches("[0-9]$")) {
	                
	                final int ascciValFirst = (int) password[currentCounter];
	                final int ascciValSecond = (int) password[currentCounter + 1];
	                if (ascciValFirst == ascciValSecond) {
	                    isRepetation = true;
	                }
	            } else {
	                
	                for (int i = 0, j = allowdedSpecialCharArray.length; i < j; i++) {
	                    if (str.contains(allowdedSpecialCharArray[i])) {
	                        final int ascciValFirst = (int) password[currentCounter];
	                        final int ascciValSecond = (int) password[currentCounter + 1];
	                        if (ascciValFirst == ascciValSecond) {
	                            isRepetation = true;
	                        }
	                        break;
	                    }
	                }

	            }
	            currentCounter++;
	            if (isRepetation) {
	                return isRepetation;
	            }

	        }
	        return isRepetation;
	    }

	    /**
	     * Validate the isSMSPinValid of the string
	     * 
	     * @param p_string
	     * @author diwakar
	     * @date : 20-10-2014
	     * @return
	     */
	    public static int isSMSPinValid(String s) {
	        int i = 0;
	        int j = 0;
	        final boolean flag = false;
	        char c1 = '\0';
	        final boolean flag1 = false;
	        byte byte0 = 0;
	        for (int k = 0; k < s.length(); k++) {
	            final char c2 = s.charAt(k);
	            if (k < s.length() - 1) {
	                c1 = s.charAt(k + 1);
	            }
	            final char c = c1;
	            if (c2 == c1) {
	                i++;
	                continue;
	            }
	            if (c == c2 + 1 || c == c2 - 1) {
	                j++;
	            }
	        }

	        if (i == s.length()) {
	            return byte0 = -1;
	        }
	        if (j == s.length() - 1) {
	            return byte0 = 1;
	        } else {
	            return byte0;
	        }
	    }
	    
	

	
}
