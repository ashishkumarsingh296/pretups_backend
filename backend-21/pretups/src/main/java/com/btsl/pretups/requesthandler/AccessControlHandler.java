/**
 * @(#)AccessControlHandler.java
 *                               This controller is used for unblock blocked
 *                               user passwords, reset passwords to default
 *                               passwords.
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               vikas yadav Dec 5, 2006 Initital Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Copyright(c) 2006, Bharti Telesoft Ltd.
 *                               All Rights Reserved
 */
package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class AccessControlHandler implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(AccessControlHandler.class.getName());
    private HashMap requestMap = null;
    private final String XML_TAG_USERLOGINID = "USERLOGINID";
    private final String XML_TAG_ACTION = "ACTION";
    private RequestVO requestVO = null;
    private static OperatorUtilI _operatorUtil = null;
    static {

        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            requestMap = p_requestVO.getRequestMap();
            requestVO = p_requestVO;
            String loginID = (String) requestMap.get(XML_TAG_USERLOGINID);
            String action = (String) requestMap.get(XML_TAG_ACTION);

            validate(p_requestVO);

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Default locale fom system preferences
            Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            LoginDAO loginDAO = new LoginDAO();
            // Load user details by passing the login id and default password
            // from system preferences.
            ChannelUserVO channelUserVO = loginDAO.loadUserDetails(con, loginID.trim(), ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)), locale);

            if (channelUserVO != null && !channelUserVO.getNetworkID().equals(((UserVO) p_requestVO.getSenderVO()).getNetworkID())) {
                throw new BTSLBaseException("AccessControlHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
            }

            if (channelUserVO != null) {
                if ("SPREQ".equals(action)) {
                    sendPassword(con, channelUserVO);// if request is for
                                                     // sending the password
                                                     // only
                } else if ("RPREQ".equals(action)) {
                    resetPassword(con, channelUserVO);// For reset password to
                                                      // default password
                } else if ("UBPREQ".equals(action)) {
                    unblockPassword(con, channelUserVO, ((UserVO) p_requestVO.getSenderVO()).getUserID());// For
                                                                                                          // unblock
                                                                                                          // password
                } else if ("UBSPREQ".equals(action)) {
                    unblockAndSendPassword(con, channelUserVO);// for unblock
                                                               // and send
                                                               // password
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_ACTION_VALUE);
                }
            } else {
                p_requestVO.setSuccessTxn(false);
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CCE_ERROR_USER_DETAIL_NOT_FOUND);

            }
            p_requestVO.setSuccessTxn(true);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + be.getMessage());
            p_requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccessControlHandler[process]", "", "", "", "Exception:" + e.getMessage());
        } finally {
            p_requestVO.setRequestMap(requestMap);
			if (mcomCon != null) {
				mcomCon.close("AccessControlHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", "", " Exited ");
            }
        }
    }

    /**
     * check the mondatroy field, that is Login id and action is comming in the
     * request or not
     * 
     * @param p_requestVO
     * @return
     * @throws BTSLBaseException
     */
    private void validate(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validate", " Entered " + p_requestVO);
        }
        String loginID = (String) requestMap.get(XML_TAG_USERLOGINID);
        String action = (String) requestMap.get(XML_TAG_ACTION);

        if (BTSLUtil.isNullString(loginID)) {
            requestMap.put("RES_ERR_KEY", XML_TAG_USERLOGINID);
            throw new BTSLBaseException("AccessControlHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
        } else if (BTSLUtil.isNullString(action)) {
            requestMap.put("RES_ERR_KEY", XML_TAG_ACTION);
            throw new BTSLBaseException("AccessControlHandler", "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Exit ");
        }
    }

    /**
     * method is used for Send password to user
     * 
     * @param p_con
     * @param p_channelUserVO
     * @return
     * @throws BTSLBaseException
     */
    private void sendPassword(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "sendPassword";
        if (_log.isDebugEnabled()) {
            _log.debug("sendPassword", " Entered with ChannelUserVO:" + p_channelUserVO.toString());
        }
        try {
            String action = (String) requestMap.get(XML_TAG_ACTION);
            String[] arr = new String[1];// Having the new password
            arr[0] = BTSLUtil.decryptText(p_channelUserVO.getPassword());
            UserDAO userDAO = new UserDAO();
            UserPhoneVO userPhoneVO = userDAO.loadUserPhoneVO(p_con, p_channelUserVO.getUserID());
            Locale locale = null;
            if (userPhoneVO != null) {
                locale = new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry());
            } else {
                throw new BTSLBaseException(this, "sendPassword", PretupsErrorCodesI.CCE_ERROR_USER_DETAIL_NOT_FOUND);
            }

            BTSLMessages btslMessage = null;
            if ("RPREQ".equals(action)) {
                // arr[0]= ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD));
                btslMessage = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_RESETPSWD_MSG, arr);
            } else if ("UBPREQ".equals(action)) {
                btslMessage = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_UNBLOCKPSWD_MSG);
            } else if ("UBSPREQ".equals(action)) {
                arr[0] = BTSLUtil.decryptText(p_channelUserVO.getPassword());
                btslMessage = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_UNBLOCKSENDPSWD_MSG, arr);
            } else if ("SPREQ".equals(action)) {
                arr[0] = BTSLUtil.decryptText(p_channelUserVO.getPassword());
                btslMessage = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_SENDPSWD_MSG, arr);

            }
            // PushMessage pushMessage=new
            // PushMessage(userPhoneVO.getMsisdn(),btslMessage,null,null,locale,p_channelUserVO.getNetworkID());
            PushMessage pushMessage = new PushMessage(userPhoneVO.getMsisdn(), btslMessage, null, null, locale, p_channelUserVO.getNetworkID(), "SMS will be delivered shortly please wait");
            pushMessage.push();
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("sendPassword", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("sendPassword", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccessControlHandler[sendPassword]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AccessControlHandler", "sendPassword", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("sendPassword", " Exit:");
        }
    }

    /**
     * method is used for resetting the password to default value
     * 
     * @param p_con
     * @param p_channelUserVO
     * @return
     * @throws BTSLBaseException
     */
    private void resetPassword(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "resetPassword";
        if (_log.isDebugEnabled()) {
            _log.debug("resetPassword", " Entered with ChannelUserVO:" + p_channelUserVO);
        }
        try {
            UserDAO userDAO = new UserDAO();
            Date currentDate = new Date();
            int updateCount = 0;
            int resetCount = 0;
            String pwd = null;
            p_channelUserVO.setInvalidPasswordCount(0);
            LoginDAO loginDAO = new LoginDAO();
            p_channelUserVO.setModifiedBy(((UserVO) requestVO.getSenderVO()).getUserID());
            p_channelUserVO.setModifiedOn(currentDate);
            resetCount = loginDAO.updatePasswordCounter(p_con, p_channelUserVO);
            if (resetCount > 0) {
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.WEB_RANDOM_PWD_GENERATE))).booleanValue()) {
                    pwd = _operatorUtil.randomPwdGenerate();
                } else {
                    pwd = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD));
                }

                updateCount = userDAO.changePassword(p_con, p_channelUserVO.getUserID(), BTSLUtil.encryptText(pwd), currentDate, ((UserVO) requestVO.getSenderVO()).getUserID(), null);
            }
            if (resetCount > 0 && updateCount > 0) {
                p_con.commit();
                p_channelUserVO.setPassword(BTSLUtil.encryptText(pwd));
                sendPassword(p_con, p_channelUserVO);
            } else {
                throw new BTSLBaseException(this, "resetPassword", PretupsErrorCodesI.CCE_ERROR_ACC_CTRL_NOT_UPDATED);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("resetPassword", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("resetPassword", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccessControlHandler[resetPassword]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AccessControlHandler", "resetPassword", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
    }

    /**
     * method is used for unloacking the password
     * 
     * @param p_con
     * @param p_channelUserVO
     * @return
     * @throws BTSLBaseException
     */
    private void unblockPassword(Connection p_con, ChannelUserVO p_channelUserVO, String p_modifiedBy) throws BTSLBaseException {
        final String METHOD_NAME = "unblockPassword";
        if (_log.isDebugEnabled()) {
            _log.debug("unblockPassword", " Entered with ChannelUserVO:" + p_channelUserVO);
        }

        try {
            LoginDAO loginDAO = new LoginDAO();
            Date currentDate = new Date();
            // update password count
            int updateCount = 0;

            p_channelUserVO.setModifiedBy(p_modifiedBy);
            p_channelUserVO.setModifiedOn(currentDate);
            // updated by shishupal on 15/03/2007
            // if(p_channelUserVO.getInvalidPasswordCount()<
            // ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_PASSWORD_BLOCK_COUNT))).intValue())
            if (p_channelUserVO.getInvalidPasswordCount() < ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, p_channelUserVO.getNetworkID(), p_channelUserVO.getCategoryCode())).intValue()) {
                throw new BTSLBaseException(this, "unblockPassword", PretupsErrorCodesI.CCE_ACCE_ALREADY_UNBLOCK);
            }
            p_channelUserVO.setInvalidPasswordCount(0);
            updateCount = loginDAO.updatePasswordCounter(p_con, p_channelUserVO);

            if (updateCount > 0) {
                p_con.commit();
                this.sendPassword(p_con, p_channelUserVO);

            } else {
                throw new BTSLBaseException(this, "unblockPassword", PretupsErrorCodesI.CCE_ERROR_ACC_CTRL_NOT_UPDATED);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("unblockPassword", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("unblockPassword", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccessControlHandler[unblockPassword]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AccessControlHandler", "unblockPassword", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
    }

    /**
     * method is used for unloacking and sendthe password
     * 
     * @param p_con
     * @param p_channelUserVO
     * @return
     * @throws BTSLBaseException
     */
    private void unblockAndSendPassword(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "unblockAndSendPassword";
        if (_log.isDebugEnabled()) {
            _log.debug("unblockAndSendPassword", " Entered with ChannelUserVO:" + p_channelUserVO);
        }
        try {
            LoginDAO loginDAO = new LoginDAO();
            // update password count
            int updateCount = 0;
            // updated by shishupal on 15/03/2007
            // if(p_channelUserVO.getInvalidPasswordCount()<
            // ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_PASSWORD_BLOCK_COUNT))).intValue())
            if (p_channelUserVO.getInvalidPasswordCount() < ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, p_channelUserVO.getNetworkID(), p_channelUserVO.getCategoryCode())).intValue()) {
                throw new BTSLBaseException(this, "unblockAndSendPassword", PretupsErrorCodesI.CCE_ACCE_ALREADY_UNBLOCK);
            }

            p_channelUserVO.setInvalidPasswordCount(0);
            updateCount = loginDAO.updatePasswordCounter(p_con, p_channelUserVO);

            if (updateCount > 0) {
                p_con.commit();
                this.sendPassword(p_con, p_channelUserVO);
            } else {
                throw new BTSLBaseException(this, "unblockAndSendPassword", PretupsErrorCodesI.CCE_ERROR_ACC_CTRL_NOT_UPDATED);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("unblockAndSendPassword", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("unblockAndSendPassword", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AccessControlHandler[unblockAndSendPassword]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("AccessControlHandler", "unblockAndSendPassword", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
    }
}
