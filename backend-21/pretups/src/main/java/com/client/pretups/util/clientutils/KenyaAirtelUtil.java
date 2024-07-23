/*
 * @(#)KenyaAirtelUtil.java
 * Copyright(c) 2010, Comviva Technologies Ltd.
 * All Rights Reserved
 * Description :-
 * --------------------------------------------------------------------
 * Author Date History
 * --------------------------------------------------------------------
 * shishupal.singh Jun 10, 2011 Initial creation
 * --------------------------------------------------------------------
 */
package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
import com.btsl.login.LoginDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class KenyaAirtelUtil extends OperatorUtil {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public String[] getP2PChangePinMessageArray(String message[]) {
        if (message.length == 3) {
            final String message1[] = new String[message.length + 1];
            message1[0] = message[0];
            message1[1] = message[1];
            message1[2] = message[2];
            message1[3] = message[2];
            return message1;
        } else {
            return message;
        }

    }

    public String[] getC2SChangePinMessageArray(String message[]) {
        if (message.length == 3) {
            final String message1[] = new String[message.length + 1];
            message1[0] = message[0];
            message1[1] = message[1];
            message1[2] = message[2];
            message1[3] = message[2];
            return message1;
        } else {
            return message;
        }

    }

    /**
     * This method used for Password validation.
     * While creating or modifying the user Password This method will be used.
     * Method validatePassword.
     * 
     * @author
     * @created on
     * @param p_loginID
     *            String
     * @param p_password
     *            String
     * @return HashMap
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

        // check for small and capital letters
        final String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final char[] ch = p_password.toCharArray();
        boolean isAlphabet = false;
        for (int i = 0, j = ch.length; i < j; i++) {
            if (alphabets.contains(Character.toString(ch[i]))) {
                isAlphabet = true;
                break;
            }
        }
        if (!isAlphabet) {
            messageMap.put("nigeriautil.validatepassword.error.passwordalphabetchar", null);
        } else {

            final char[] ch1 = p_password.toCharArray();
            boolean isAlphabet1 = false;
            for (int i = 0, j = ch.length; i < j; i++) {
                if (alphabets.contains(Character.toString(ch[i]))) {
                    isAlphabet1 = true;
                    break;
                }
            }
            if (!isAlphabet1) {
                messageMap.put("nigeriautil.validatepassword.error.passwordalphabetchar", null);
            }
        }

        // check for atleast two non-alphabet characters
        final String specialChar = Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
        isAlphabet = false;
        if (!BTSLUtil.isNullString(specialChar)) {
            for (int i = 0; i < ch.length; i++) {
                if (specialChar.contains(Character.toString(ch[i]))) {
                    isAlphabet = true;
                    break;
                }
            }
        }
        if (!isAlphabet) {
            messageMap.put("nigeriautil.validatepassword.error.passwordnonalphabetchar", null);
        } else {
            final String passwordNumberStrArray = "0123456789";
            boolean isAlphabet1 = false;
            for (int i = 0; i < ch.length; i++) {
                if (passwordNumberStrArray.contains(Character.toString(ch[i]))) {
                    isAlphabet1 = true;
                    break;
                }
            }
            if (!isAlphabet1) {
                messageMap.put("nigeriautil.validatepassword.error.passwordnonalphabetchar", null);
            }
        }
        // check for same consecutive 3 characters of password with LoginId
        String compare = null;
        String tempPassword = p_password;
        boolean match = false;
        for (int i = 0; i < (p_loginID.length() - 2); i++) {
            compare = p_loginID.substring(i, i + 3);
            tempPassword = tempPassword.replace(compare, "");
            if (tempPassword.length() < p_password.length()) {
                match = true;
                break;
            }
        }
        if (match) {
            messageMap.put("nigeriautil.validatepassword.error.passwordloginidmatch", null);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("validatePassword", "Exiting messageMap.size()=" + messageMap.size());
        }
        return messageMap;
    }

    public boolean validateTransactionPassword(ChannelUserVO p_channelUserVO, String p_password) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateTransactionPassword", " Entered p_channelUserVO=:" + p_channelUserVO + " p_password=" + p_password);
        }
        final String methodName = "validateTransactionPassword";
        boolean passwordValidation = true;

        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            if (p_channelUserVO != null) {
                /*
                 * change done by ashishT for hashing implementation
                 * comparing the password hashvlue from db to the password sent
                 * by user.
                 */
                // if(!BTSLUtil.isNullString(p_channelUserVO.getPassword()) &&
                // (PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_channelUserVO.getPassword(),
                // p_password))))
                if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                    boolean checkpassword;
                    if (p_password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) {
                        checkpassword = BTSLUtil.decryptText(p_channelUserVO.getPassword()).equals(p_password);
                    } else {
                        checkpassword = (!PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_channelUserVO.getPassword(), p_password)));
                    }
                    if (!BTSLUtil.isNullString(p_channelUserVO.getPassword()) && (!checkpassword)) {
                        passwordValidation = false;
                    }
                } else {
                    if (!BTSLUtil.isNullString(p_channelUserVO.getPassword()) && (PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_channelUserVO.getPassword(),
                        p_password)))) {
                        passwordValidation = false;
                    }

                    // ///////// updated by shishu
                    // Check if PASSWORD block or not ,if block check expiry
                    // status of that password.
                    /*
                     * if (p_channelUserVO.getInvalidPasswordCount() ==
                     * ((Integer
                     * )PreferenceCache.getControlPreference(PreferenceI
                     * .MAX_PASSWORD_BLOCK_COUNT
                     * ,p_channelUserVO.getNetworkID(),p_channelUserVO
                     * .getCategoryCode())).intValue())
                     * {
                     * //got expiry period from SystemPreference table
                     * long
                     * expiryTime=((Long)PreferenceCache.getControlPreference
                     * (PreferenceI
                     * .C2S_PWD_BLK_EXP_DURATION,p_channelUserVO.getNetworkID
                     * (),p_channelUserVO.getCategoryCode())).longValue();
                     * //check expiry status
                     * if(BTSLUtil.isTimeExpired(p_channelUserVO.
                     * getPasswordCountUpdatedOn(),expiryTime)){
                     * //set invalid count to 1 as per password
                     * updatePasswordInvalidCount method logic
                     * p_channelUserVO.setInvalidPasswordCount(1);
                     * isPWDBlocedkExpired=true;
                     * 
                     * }else{
                     * //if expiry period not over shows error message
                     * throw new BTSLBaseException(this,
                     * "updatePasswordInvalidCount",
                     * PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                     * }
                     * }
                     */
                    if (p_channelUserVO.getPasswordModifiedOn() == null) {
                        throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
                    } else if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)).equals(p_channelUserVO.getPassword()))// if
                    // password
                    // value
                    // ==
                    // default
                    // Password
                    // Value
                    // force
                    // the
                    // user
                    // to
                    // change
                    // the
                    // password
                    {
                        throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
                    } else {
                        final java.util.Date date1 = p_channelUserVO.getPasswordModifiedOn();
                        final java.util.Date date2 = new java.util.Date();

                        final long dt1 = date1.getTime();
                        final long dt2 = date2.getTime();
                        final long nodays = (long) ((dt2 - dt1) / (1000 * 60 * 60 * 24));
                        long noPasswordTimeOutDays = 0;
                        try {
                            noPasswordTimeOutDays = ((Integer) PreferenceCache.getControlPreference(PreferenceI.DAYS_AFTER_CHANGE_PASSWORD, p_channelUserVO.getNetworkID(),
                                p_channelUserVO.getCategoryCode())).intValue();

                        } catch (Exception e) {
                            _log.error("validateTransactionPassword", "Exception " + e.getMessage());
                            _log.errorTrace(methodName, e);
                        }
                        /*
                         * Here we are checking whether the password change is
                         * required or not
                         * a)category check(In constants file we define those
                         * categories whom password change not required)
                         * b)No of days check
                         */
                        if (nodays > noPasswordTimeOutDays) {
                            throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
                        }
                    }
                    mcomCon = new MComConnection();
                    con=mcomCon.getConnection();

                    if (p_channelUserVO.getInvalidPasswordCount() == ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, p_channelUserVO
                        .getNetworkID(), p_channelUserVO.getCategoryCode())).intValue()) {
                        // If password is blocked throw an exception
                        throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.CHNL_ERROR_SENDER_BLOCKED);
                    } else if (updatePasswordInvalidCount(con, p_channelUserVO, passwordValidation)) {
                        if (p_channelUserVO.getInvalidPasswordCount() == ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, p_channelUserVO
                            .getNetworkID(), p_channelUserVO.getCategoryCode())).intValue()) {
                            // If password is blocked throw an exception
                            throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.CHNL_ERROR_SENDER_BLOCKED);
                        }
                        throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                    }
                    /*
                     * if(p_channelUserVO.getInvalidPasswordCount() ==
                     * ((Integer)
                     * PreferenceCache.getControlPreference(PreferenceI
                     * .MAX_PASSWORD_BLOCK_COUNT
                     * ,p_channelUserVO.getNetworkID(),p_channelUserVO
                     * .getCategoryCode())).intValue() ||
                     * updatePasswordInvalidCount
                     * (p_con,p_channelUserVO,p_loginForm
                     * ,request,p_loginLoggerVO))
                     * {
                     * 
                     * if (p_channelUserVO.getInvalidPasswordCount() ==
                     * ((Integer
                     * )PreferenceCache.getControlPreference(PreferenceI
                     * .MAX_PASSWORD_BLOCK_COUNT
                     * ,p_channelUserVO.getNetworkID(),p_channelUserVO
                     * .getCategoryCode())).intValue())
                     * {
                     * //If password is blocked throw an exception
                     * p_loginLoggerVO.setOtherInformation(this.getResources(request
                     * ).getMessage(BTSLUtil.getBTSLLocale(request),
                     * "login.index.error.invalidpwd.passwordblocked"));
                     * throw new BTSLBaseException(this,
                     * "updatePasswordInvalidCount",
                     * "login.index.error.invalidpwd.passwordblocked","index");
                     * }
                     * p_loginForm.setFocusPassword(true);
                     * p_loginLoggerVO.setOtherInformation(this.getResources(request
                     * ).getMessage(BTSLUtil.getBTSLLocale(request),
                     * "login.index.error.invalidpassword"));
                     * throw new
                     * BTSLBaseException("login.index.error.invalidpassword"
                     * ,"index");
                     * }
                     */

                    // updated by shishupal on 14/03/2007
                    // else if (p_channelUserVO.getInvalidPasswordCount() ==
                    // SystemPreferences.MAX_PASSWORD_BLOCK_COUNT)

                    // ///////// end by shishu
                }

                // if(!BTSLUtil.isNullString(p_password) &&
                // ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)).equals(p_password))
                // throw new BTSLBaseException(this, "loadValidateUserDetails",
                // PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD);
            } else {
                throw new BTSLBaseException("KenyaAirtelUtil", "validateTransactionPassword", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
            }

        } catch (BTSLBaseException bex) {
            throw bex;
        } catch (Exception e) {
            _log.error("validateTransactionPassword", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "KenyaAirtelUtil[validateTransactionPassword]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("KenyaAirtelUtil", "validateTransactionPassword", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("KenyaAirtelUtil#validateTransactionPassword");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("validateTransactionPassword", " Exiting passwordValidation=" + passwordValidation);
            }
        }
        return passwordValidation;
    }

    /**
     * Method to update the Password Invalid Count in the data base
     * 
     * @param p_channelUserVO
     *            ChannelUserVO
     * @param p_password
     *            String
     * @param isPWDBlocedkExpired
     *            boolean
     * @throws Exception
     */
    private boolean updatePasswordInvalidCount(Connection p_con, ChannelUserVO p_channelUserVO, boolean isPWDBlocedkExpired) throws Exception {
        boolean passwordStatus = false;
        int updateStatus = 0;

        try {
            // con = OracleUtil.getConnection(); killed by sanjay
            final LoginDAO _loginDAO = new LoginDAO();

            final String decryptedPassword = BTSLUtil.decryptText(p_channelUserVO.getPassword());
            final Date currentDate = new Date();
            p_channelUserVO.setModifiedOn(currentDate);

            if (_log.isDebugEnabled()) {
                _log.debug("updatePasswordInvalidCount",
                    "User Login Id:" + p_channelUserVO.getLoginID() + " decrypted Password=" + decryptedPassword + " entered Password=" + p_channelUserVO.getPassword());
            }

            // done by ashishT , for controling the both case hashing and
            // AES/DES mode.
            // boolean passwordFlag=isPWDBlocedkExpired;
            if (!isPWDBlocedkExpired) {
                final long mintInDay = 24 * 60;
                if (p_channelUserVO.getPasswordCountUpdatedOn() != null) {
                    // Check if Password counters needs to be reset after the
                    // reset duration
                    final Calendar cal = Calendar.getInstance();
                    cal.setTime(p_channelUserVO.getModifiedOn());
                    final int d1 = cal.get(Calendar.DAY_OF_YEAR);
                    cal.setTime(p_channelUserVO.getPasswordCountUpdatedOn());
                    final int d2 = cal.get(Calendar.DAY_OF_YEAR);
                    if (_log.isDebugEnabled()) {
                        _log.debug("updatePasswordInvalidCount", "Day Of year of Modified On=" + d1 + " Day Of year of PasswordCountUpdatedOn=" + d2);
                    }
                    // updated by shishupal on 15/03/2007
                    // if(d1!=d2 &&
                    // SystemPreferences.PASSWORD_BLK_RST_DURATION<=mintInDay)
                    if (d1 != d2 && ((Long) PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION, p_channelUserVO.getNetworkID(), p_channelUserVO
                        .getCategoryCode())).longValue() <= mintInDay) {

                        // reset
                        p_channelUserVO.setInvalidPasswordCount(1);
                        p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
                    } else if (d1 != d2 && ((Long) PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION, p_channelUserVO.getNetworkID(), p_channelUserVO
                        .getCategoryCode())).longValue() >= mintInDay && (d1 - d2) >= (((Long) PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION,
                        p_channelUserVO.getNetworkID(), p_channelUserVO.getCategoryCode())).longValue() / mintInDay)) {

                        // Reset
                        p_channelUserVO.setInvalidPasswordCount(1);
                        p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
                    } else if (((p_channelUserVO.getModifiedOn().getTime() - p_channelUserVO.getPasswordCountUpdatedOn().getTime()) / (60 * 1000)) < ((Long) PreferenceCache
                        .getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION, p_channelUserVO.getNetworkID(), p_channelUserVO.getCategoryCode())).longValue()) {

                        // updated by shishupal on 14/03/2007
                        // if (p_userVO.getInvalidPasswordCount() -
                        // SystemPreferences.MAX_PASSWORD_BLOCK_COUNT== 0)
                        if (p_channelUserVO.getInvalidPasswordCount() - ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, p_channelUserVO
                            .getNetworkID(), p_channelUserVO.getCategoryCode())).intValue() == 0) {

                            // password block message
                            throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.CHNL_ERROR_SENDER_BLOCKED);
                        }
                        p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
                        if (!isPWDBlocedkExpired) {
                            p_channelUserVO.setInvalidPasswordCount(p_channelUserVO.getInvalidPasswordCount() + 1);

                        } else {
                            p_channelUserVO.setInvalidPasswordCount(p_channelUserVO.getInvalidPasswordCount());

                        }
                    } else {
                        p_channelUserVO.setInvalidPasswordCount(1);
                        p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
                    }
                } else {

                    p_channelUserVO.setInvalidPasswordCount(1);
                    p_channelUserVO.setPasswordCountUpdatedOn(p_channelUserVO.getModifiedOn());
                }

                updateStatus = _loginDAO.updatePasswordCounter(p_con, p_channelUserVO);
                if (updateStatus > 0) {
                    p_con.commit();
                } else {
                    p_con.rollback();
                    throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                }
                passwordStatus = true;
            } else {

                // initilize Password Counters if ifPinCount>0
                if (p_channelUserVO.getInvalidPasswordCount() > 0) {

                    p_channelUserVO.setInvalidPasswordCount(0);
                    p_channelUserVO.setPasswordCountUpdatedOn(null);
                    updateStatus = _loginDAO.updatePasswordCounter(p_con, p_channelUserVO);
                    if (updateStatus > 0) {
                        p_con.commit();
                    } else {
                        p_con.rollback();
                        throw new BTSLBaseException(this, "updatePasswordInvalidCount", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                    }
                }

            }
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updatePasswordInvalidCount", "Exiting  ::: passwordStatus:" + passwordStatus);
            }
        }
        return passwordStatus;
    }// end of updatePasswordInvalidCount

}
