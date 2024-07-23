package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;

/**
 * @description : This controller class will be used to process the change
 *              password request for user through external system via operator
 *              receiver.
 * @author : diwakar
 * @date : 20-JAN-2014
 */
public class ChangePasswordController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(ChangePasswordController.class.getName());
    private String _requestID = null;
    private static OperatorUtilI _operatorUtil = null;
    private ChannelUserVO _channelUserVO = null;
    private UserDAO _userDAO = null;
    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangePasswordController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        _requestID = p_requestVO.getRequestIDStr();
        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestID, " Entered " + p_requestVO);
        }
		Connection con = null;
		MComConnectionI mcomCon = null;
        int count = 0;
        try {
            final String newPassword = (String) p_requestVO.getRequestMap().get("NEWPASSWD");
            final String userMsisdn = (String) p_requestVO.getRequestMap().get("USERMSISDN");
            final String userLoginID = (String) p_requestVO.getRequestMap().get("USERLOGINID");
            _channelUserVO = (ChannelUserVO) p_requestVO.getRequestMap().get("CHNUSERVO");

            _userDAO = new UserDAO();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            final String[] userDetails = fetchOldPasswordOfUser(con, userLoginID, userMsisdn);
            if (userDetails != null && !BTSLUtil.isNullArray(userDetails)) {
                checkNewPasswordEqualOldPassword(userDetails[1], newPassword);
                // Check last 'X' password from pin password history table.
                // change by santanu
                final boolean passwordExist = _userDAO.checkPasswordHistory(con, PretupsI.USER_PASSWORD_MANAGEMENT, userDetails[0], userDetails[2], BTSLUtil
                    .encryptText(newPassword));

                if (passwordExist) {
                    final String arrmsg[] = { userLoginID, newPassword };
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHANGE_PASSWORD_NEWPASSWORD_EXIST_HIST, new String[] { String
                        .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue()) });
                }
                final HashMap messageMap = _operatorUtil.validatePassword(userLoginID, newPassword);
                if (!messageMap.isEmpty()) {
                    final Iterator itr = messageMap.keySet().iterator();
                    while (itr.hasNext()) {
                        final String errorKey = (String) itr.next();
                        if ("operatorutil.validatepassword.error.passwordlenerr".equals(errorKey)) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHANGE_PASSWORD_NEWPASSWORD_LENGTH, new String[] { String.valueOf(messageMap
                                .get(errorKey)) });
                        } else if ("operatorutil.validatepassword.error.passwordsamedigit".equals(errorKey)) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHANGE_PASSWORD_NEWPASSWORD_SAME_DIGIT);
                        } else if ("operatorutil.validatepassword.error.passwordconsecutive".equals(errorKey)) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHANGE_PASSWORD_NEWPASSWORD_CONSSECUTIVE);
                        } else if ("operatorutil.validatepassword.error.passwordnotcontainschar".equals(errorKey)) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL_ATLEAST);
                        } else if ("operatorutil.validatepassword.error.passwordmusthaverequiredchar".equals(errorKey)) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL_ATLEAST);
                        } else if ("operatorutil.validatepassword.error.sameusernamepassword".equals(errorKey)) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHANGE_PASSWORD_SAME_LOGIID_PASSWORD);
                        } else if ("user.modifypwd.error.newpasswordexistcheck".equals(errorKey)) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHANGE_PASSWORD_OLD_NEW_SAME);
                            // Ended
                        }
                    }
                } else {
                    // Change the password for the user with new password based
                    // on either login_id or msisdn
                    final Date currentDate = new Date();
                    count = _userDAO.changePassword(con, userDetails[0], BTSLUtil.encryptText(newPassword), currentDate, _channelUserVO.getUserID(), null);
                }

                if (count > 0) {
                    con.commit();
                    final String arrmsg[] = { userLoginID, newPassword };
                    final String smsMessage = BTSLUtil.getMessage(new Locale("en", "US"), PretupsErrorCodesI.CHANGE_PASSWORD_SUCCESS, arrmsg);
                    new PushMessage(userDetails[2], smsMessage, null, null, new Locale("en", "US")).push();
                    p_requestVO.setMessageArguments(arrmsg);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.CHANGE_PASSWORD_SUCCESS);
                } else {
                    con.rollback();
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHANGE_PASSWORD_FAILED);
                }
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER); // 03-MAR-2014
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }

            p_requestVO.setMessageArguments(be.getArgs());
            _log.error("process", _requestID, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);

        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.errorTrace(METHOD_NAME, e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }

            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            _log.error("process", _requestID, "BTSLBaseException " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangePinController[process]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
        	if(mcomCon != null){mcomCon.close("ChangePasswordController#process");mcomCon=null;}
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestID, " Exited ");
            }
        }
    }

    /**
     * @descrption : This method to fetch the old password of the user.
     * @author diwakar
     * @param p_con
     * @param requestLoginId
     * @param requestMSISDN
     * @return
     * @throws BTSLBaseException
     */
    private String[] fetchOldPasswordOfUser(Connection p_con, String requestLoginId, String requestMSISDN) throws BTSLBaseException {
        final String METHOD_NAME = "fetchOldPasswordOfUser";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered requestLoginId : ");
        	loggerValue.append(requestLoginId);
        	loggerValue.append(" | requestMSISDN = ");
        	loggerValue.append(requestMSISDN);
            _log.debug("fetchOldPasswordOfUser",  loggerValue );
        }
        PreparedStatement psmt = null;
        ResultSet rs = null;
        String userId = null;
        String oldPassword = null;
        String msisdn = null;
        final String[] userDetails = new String[3];
        try {
            StringBuffer strBuff = null;
            String query = null;
            final boolean isLoginId = BTSLUtil.isNullString(requestLoginId);
            final boolean isMsisdn = BTSLUtil.isNullString(requestMSISDN);
            int i = 1;

            if (!isLoginId && !isMsisdn) {
                strBuff = new StringBuffer(" SELECT DISTINCT U.USER_ID , U.PASSWORD , U.MSISDN FROM USERS U WHERE U.LOGIN_ID = ? AND U.MSISDN = ? ");
                query = strBuff.toString();
                psmt = p_con.prepareStatement(query);
                psmt.setString(i++, requestLoginId);
                psmt.setString(i++, requestMSISDN);
            } else if (!isMsisdn) {
                strBuff = new StringBuffer(" SELECT DISTINCT U.USER_ID , U.PASSWORD , U.MSISDN FROM USERS U  WHERE  U.MSISDN = ? ");
                query = strBuff.toString();
                psmt = p_con.prepareStatement(query);
                psmt.setString(i++, requestMSISDN);
            } else if (!isLoginId) {
                strBuff = new StringBuffer(" SELECT DISTINCT U.USER_ID , U.PASSWORD , U.MSISDN FROM USERS U WHERE U.LOGIN_ID = ? ");
                query = strBuff.toString();
                psmt = p_con.prepareStatement(query);
                psmt.setString(i++, requestLoginId);
            }

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(query);
                _log.debug("fetchOldPasswordOfUser", loggerValue);
            }
            if (psmt != null) {
                rs = psmt.executeQuery();
            }
            while (rs != null && rs.next()) {
                userId = rs.getString("USER_ID");
                userDetails[0] = userId;
                oldPassword = rs.getString("PASSWORD");
                oldPassword = BTSLUtil.decryptText(oldPassword);
                userDetails[1] = oldPassword;
                msisdn = rs.getString("MSISDN");
                userDetails[2] = msisdn;
            }

        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error("fetchOldPasswordOfUser",  loggerValue );
            _log.errorTrace(METHOD_NAME, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append( sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[fetchOldPasswordOfUser]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "fetchOldPasswordOfUser", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception " );
        	loggerValue.append(e.getMessage());
            _log.error("fetchOldPasswordOfUser",  loggerValue );
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[fetchOldPasswordOfUser]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "fetchOldPasswordOfUser", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("fetchOldPasswordOfUser", "Exiting");
            }
        }// end of finally
        return userDetails;
    }

    /**
     * @description : Method to check whether the New password is same as Old
     *              password
     * @author diwakar
     * @param p_oldPassword
     * @param p_password
     * @throws BTSLBaseException
     */
    private void checkNewPasswordEqualOldPassword(String p_oldPassword, String p_password) throws BTSLBaseException {
        if (p_password.equals(p_oldPassword)) {
            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHANGE_PASSWORD_OLD_NEW_SAME);
        }
    }

}
