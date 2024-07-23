package com.selftopup.cp2p.login.web;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import jakarta.servlet.http.HttpSession;

import com.selftopup.common.BTSLActionSupport;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;
import com.opensymphony.xwork2.interceptor.ScopedModelDriven;

public class CP2PChangePasswordAction extends BTSLActionSupport implements ScopedModelDriven<CP2PSubscriberVO> {

    private static final long serialVersionUID = 1L;

    private CP2PSubscriberVO cp2pSubscriberVO;
    private String MODEL_SESSION_KEY;

    public CP2PSubscriberVO getModel() {
        // TODO Auto-generated method stub
        return cp2pSubscriberVO;
    }

    public String getScopeKey() {
        // TODO Auto-generated method stub
        return MODEL_SESSION_KEY;
    }

    public void setModel(CP2PSubscriberVO arg0) {
        // TODO Auto-generated method stub
        this.cp2pSubscriberVO = (CP2PSubscriberVO) arg0;
    }

    public void setScopeKey(String arg0) {
        // TODO Auto-generated method stub
        MODEL_SESSION_KEY = arg0;
    }

    public CP2PSubscriberVO getCP2PSubscriberVO() {
        return cp2pSubscriberVO;
    }

    public void setCP2PSubscriberVO(CP2PSubscriberVO cp2pSubscriberVO) {
        this.cp2pSubscriberVO = cp2pSubscriberVO;
    }

    private Log _log = LogFactory.getLog(this.getClass().getName());
    public static OperatorUtilI _operatorUtil = null;
    public CP2PSubscriberVO _cp2pSubscriberVO = null;
    public SubscriberDAO _subscriberDAO = null;
    // Loads operator specific class
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangePasswordController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public String input() {
        cp2pSubscriberVO.setOldPassword(null);
        return INPUT;
    }

    public String change() {
        if (_log.isDebugEnabled())
            _log.debug("change", "Entered");
        HttpSession session = _request.getSession();
        Connection con = null;
        int count = 0;
        String returnStr = null;
        HashMap messageMap = null;
        try {

            if (BTSLUtil.isNullString(cp2pSubscriberVO.getOldPassword()) || BTSLUtil.isNullString(cp2pSubscriberVO.getNewPassword()) || BTSLUtil.isNullString(cp2pSubscriberVO.getConfirmPassword())) {
                this.addActionError(this.getText("cp2p.change.password.null"));
                return ERROR;
            }
            _cp2pSubscriberVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");

            String newPassword = cp2pSubscriberVO.getNewPassword();
            String userLoginID = _cp2pSubscriberVO.getLoginId();
            String confirmPassword = cp2pSubscriberVO.getConfirmPassword();

            _subscriberDAO = new SubscriberDAO();
            con = OracleUtil.getConnection();
            String[] userDetails = new String[4];
            userDetails[0] = _cp2pSubscriberVO.getUserId();
            String oldPassword = BTSLUtil.decryptText(_cp2pSubscriberVO.getPassword());
            userDetails[1] = oldPassword;
            userDetails[2] = _cp2pSubscriberVO.getMsisdn();
            if (userDetails != null && !BTSLUtil.isNullArray(userDetails)) {
                checkOldPasswordEqualDbPassword(userDetails[1], cp2pSubscriberVO.getOldPassword());
                checkNewPasswordEqualOldPassword(userDetails[1], newPassword);
                checkNewPasswordEqualConfirmPassword(newPassword, confirmPassword);
                boolean passwordExist = _subscriberDAO.checkPasswordHistory(con, PretupsI.USER_PASSWORD_MANAGEMENT, userDetails[0], userDetails[2], BTSLUtil.encryptText(newPassword));

                if (passwordExist) {
                    String arrmsg[] = { userLoginID, newPassword };
                    throw new BTSLBaseException("cp2p.changepwd.error.passwordexists", "change");
                }
                messageMap = _operatorUtil.validatePassword(userLoginID, newPassword);
                if (!messageMap.isEmpty()) {
                    Iterator itr = messageMap.keySet().iterator();
                    while (itr.hasNext()) {
                        String errorKey = (String) itr.next();
                        if (errorKey.equals("operatorutil.validatepassword.error.passwordlenerr")) {
                            String[] args = { String.valueOf(SystemPreferences.MIN_LOGIN_PWD_LENGTH), String.valueOf(SystemPreferences.MAX_LOGIN_PWD_LENGTH) };
                            this.addActionError(this.getText("operatorutil.validatepassword.error.passwordlenerr", args));
                            return ERROR;
                        }

                        else if (errorKey.equals("operatorutil.validatepassword.error.passwordsamedigit")) {
                            throw new BTSLBaseException("operatorutil.validatepassword.error.passwordsamedigit", "change");
                        }

                        else if (errorKey.equals("operatorutil.validatepassword.error.passwordconsecutive")) {
                            throw new BTSLBaseException("operatorutil.validatepassword.error.passwordconsecutive", "change");
                        } else if (errorKey.equals("operatorutil.validatepassword.error.passwordnotcontainschar")) {
                            throw new BTSLBaseException("operatorutil.validatepassword.error.passwordnotcontainschar", "change");
                        } else if (errorKey.equals("operatorutil.validatepassword.error.passwordspecialchar")) {
                            throw new BTSLBaseException("operatorutil.validatepassword.error.passwordspecialchar", "change");
                        } else if (errorKey.equals("operatorutil.validatepassword.error.passwordnumberchar")) {
                            throw new BTSLBaseException("operatorutil.validatepassword.error.passwordnumberchar", "change");
                        }

                        else if (errorKey.equals("operatorutil.validatepassword.error.sameusernamepassword")) {
                            throw new BTSLBaseException("operatorutil.validatepassword.error.sameusernamepassword", "change");
                        } else if (errorKey.equals("operatorutil.validatepassword.error.passwordnotcontaincapschar")) {
                            throw new BTSLBaseException("operatorutil.validatepassword.error.passwordnotcontaincapschar", "change");
                        }

                    }
                } else {
                    // Change the password for the user with new password based
                    // on either login_id or msisdn
                    Date currentDate = new Date();
                    count = _subscriberDAO.changePassword(con, userDetails[0], BTSLUtil.encryptText(newPassword), currentDate, _cp2pSubscriberVO.getUserId());
                }

                if (count > 0) {
                    con.commit();
                    _request.getSession().setAttribute("dispMenu", "SELFTOPUP");
                    _cp2pSubscriberVO.setPassword(BTSLUtil.encryptText(newPassword));
                    session.setAttribute("cp2pSubscriberVO", _cp2pSubscriberVO);
                    return SUCCESS;
                } else {
                    con.rollback();
                    throw new BTSLBaseException("cp2p.changepwd.error.failed", "change");
                }
            } else {
                throw new BTSLBaseException("cp2p.changepwd.error.failed", "change");
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                // ignored
            }
            if (messageMap != null) {
                String args[] = (String[]) messageMap.get((String) be.getMessage());
                this.addActionError(this.getText(be.getMessage(), args));
            } else {
                this.addActionError(this.getText(be.getMessage()));
            }

            _log.error("change", "Exiting", "BTSLBaseException " + be.getMessage());
            return ERROR;

        } catch (Exception e) {
            _log.errorTrace("change: Exception print stack trace: ", e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e1) {
            }
            this.addActionError(this.getText("error.general.processing"));
            _log.error("change", "Exiting", "BTSLBaseException " + e.getMessage());
            return ERROR;

        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("change", " Exited ");
        }

    }

    /**
     * @description : Method to check whether the New password is same as Old
     *              password
     * @param p_oldPassword
     * @param p_password
     * @throws BTSLBaseException
     */
    private void checkNewPasswordEqualOldPassword(String p_oldPassword, String p_password) throws BTSLBaseException {
        if (p_password.equals(p_oldPassword))
            throw new BTSLBaseException("cp2p.changepwd.error.newoldsame", "checkNewPasswordEqualOldPassword");
    }

    /**
     * @description : Method to check whether the Old is same as that in DB.
     * @param p_oldPassword
     * @param p_password
     * @throws BTSLBaseException
     */
    private void checkOldPasswordEqualDbPassword(String p_oldPassword, String p_dbPassword) throws BTSLBaseException {
        if (!p_oldPassword.equals(p_dbPassword))
            throw new BTSLBaseException("cp2p.changepwd.error.olddbnotsame", "checkNewPasswordEqualOldPassword");
    }

    private void checkNewPasswordEqualConfirmPassword(String p_newPassword, String p_confirmPassword) throws BTSLBaseException {
        if (!p_newPassword.equals(p_confirmPassword))
            throw new BTSLBaseException("cp2p.changepwd.error.newconfirmnotsame", "checkNewPasswordEqualOldPassword");
    }

    public void validate() {
        if (_request.getServletPath().equals("/cp2plogin/changePwd_change.action")) {
            if (BTSLUtil.isNullString(cp2pSubscriberVO.getOldPassword()) || BTSLUtil.isNullString(cp2pSubscriberVO.getNewPassword()) || BTSLUtil.isNullString(cp2pSubscriberVO.getConfirmPassword())) {
                this.addActionError(this.getText("cp2p.change.password.null"));
            }
        }
    }

}
