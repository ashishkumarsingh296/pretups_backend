package com.selftopup.cp2p.login.web;

/**
 * @(#)CP2PLoginAction.java
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Sonali Garg may/2014 Migration to Struts2
 *                          ----------------------------------------------------
 *                          --------------------------------------------
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.spring.custom.action.Globals;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.interceptor.ScopedModelDriven;
import com.selftopup.common.BTSLActionSupport;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.BTSLMessages;
import com.selftopup.common.TypesI;
import com.selftopup.cp2p.login.businesslogic.CP2PLoginDAO;
import com.selftopup.cp2p.login.businesslogic.CP2PLoginLoggerVO;
import com.selftopup.cp2p.subscriber.businesslogic.CP2PSubscriberVO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.menu.MenuBL;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.gateway.businesslogic.MessageGatewayCache;
import com.selftopup.pretups.gateway.businesslogic.MessageGatewayVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.user.businesslogic.SessionInfoVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;
import com.selftopup.util.UtilValidate;

public class CP2PLoginAction extends BTSLActionSupport implements ScopedModelDriven<CP2PSubscriberVO> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    public Log _log = LogFactory.getLog(this.getClass().getName());
    private boolean _isFirstTimeLogin = false;

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

    private void recreateSession(HttpServletRequest _request) {

        if (_log.isDebugEnabled())
            _log.debug("recreateSession", "Got Login ID in Login Action as =" + cp2pSubscriberVO.getLoginId());
        HttpSession session = _request.getSession();
        Locale loc = (Locale) session.getAttribute("WW_TRANS_I18N_LOCALE");
        try {
            session.invalidate();
            ServletContext servletContext = ServletActionContext.getServletContext();
        } catch (Exception e) {
        }
        HttpSession session3 = _request.getSession(true);
        session3.setAttribute("WW_TRANS_I18N_LOCALE", loc);
        if (_log.isDebugEnabled())
            _log.debug("recreateSession", "Invalidating for as =" + cp2pSubscriberVO.getLoginId() + " ID=" + session.getId());

        if (_log.isDebugEnabled())
            _log.debug("recreateSession", "Got For =" + cp2pSubscriberVO.getLoginId() + " Session ID as=" + _request.getSession().getId());
    }

    public String loadCp2pUserDetails() {

        if (_log.isDebugEnabled())
            _log.debug("loadCp2pUserDetails", "Entered");
        Connection con = null;
        String returnStr = null;
        CP2PLoginLoggerVO cp2ploginLoggerVO = new CP2PLoginLoggerVO();
        boolean passwordChangeFlag = false;

        try {
            recreateSession(_request);
            con = OracleUtil.getConnection();

            CP2PLoginDAO _cp2pLoginDAO = new CP2PLoginDAO();

            String decryptedPassword = cp2pSubscriberVO.getPassword();
            cp2pSubscriberVO = _cp2pLoginDAO.loadCP2PSubscriberDetails(con, cp2pSubscriberVO.getLoginId(), cp2pSubscriberVO.getPassword(), BTSLUtil.getBTSLLocale(_request));
            if (cp2pSubscriberVO == null) {
                cp2ploginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.invalidlogin"));
                throw new BTSLBaseException("cp2p.login.error.invalidlogin", "cp2pLogin");
            }
            cp2ploginLoggerVO.setLoginID(cp2pSubscriberVO.getLoginId());
            cp2ploginLoggerVO.setLogType(TypesI.LOG_TYPE_LOGIN);
            cp2ploginLoggerVO.setLoginTime(new Date());
            cp2ploginLoggerVO.setLogoutTime(null);
            String xForwardedFor = _request.getHeader("x-forwarded-for");
            SessionInfoVO sessionInfoVO = new SessionInfoVO();
            sessionInfoVO.setSessionID(_request.getSession().getId());
            if (_log.isDebugEnabled())
                _log.debug("loadCp2pUserDetails", "Login ID=" + cp2pSubscriberVO.getLoginId() + " xForwardedFor: " + xForwardedFor + " Session ID=" + sessionInfoVO.getSessionID());
            if (BTSLUtil.isNullString(xForwardedFor)) {
                cp2ploginLoggerVO.setIpAddress(_request.getRemoteAddr());
                sessionInfoVO.setRemoteAddr(_request.getRemoteAddr());
                sessionInfoVO.setRemoteHost(_request.getRemoteHost());
            } else {
                cp2ploginLoggerVO.setIpAddress(xForwardedFor);
                sessionInfoVO.setRemoteAddr(xForwardedFor);
                sessionInfoVO.setRemoteHost(xForwardedFor);
            }
            cp2ploginLoggerVO.setBrowser(_request.getHeader("User-Agent"));
            MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(SystemPreferences.DEFAULT_WEB_GATEWAY_CODE);
            if (messageGatewayVO != null) {
                if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus()))
                    throw new BTSLBaseException(this, "loadCp2pUserDetails", "cp2p.login.error.messagegatewaynotactive", "cp2pLogin");
                else if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().getStatus()))
                    throw new BTSLBaseException(this, "loadCp2pUserDetails", "cp2p.login.error.reqmessagegatewaynotactive", "cp2pLogin");
            }
            sessionInfoVO.setMessageGatewayVO(messageGatewayVO);
            if (cp2pSubscriberVO != null) {
                cp2pSubscriberVO.setActiveUserId(cp2pSubscriberVO.getUserId());
                cp2pSubscriberVO.setRemoteAddress(cp2ploginLoggerVO.getIpAddress());
                cp2pSubscriberVO.setBrowserType(cp2ploginLoggerVO.getBrowser());
                cp2pSubscriberVO.setLoginTime(cp2ploginLoggerVO.getLoginTime());

                // set the login details for logging
                cp2ploginLoggerVO.setUserID(cp2pSubscriberVO.getActiveUserId());
                cp2ploginLoggerVO.setNetworkID(cp2pSubscriberVO.getNetworkId());
                cp2ploginLoggerVO.setNetworkName(cp2pSubscriberVO.getNetworkName());
                cp2ploginLoggerVO.setUserName(cp2pSubscriberVO.getUserName());
                cp2ploginLoggerVO.setSubscriberType(cp2pSubscriberVO.getSubscriberType());
                cp2ploginLoggerVO.setDomainID(cp2pSubscriberVO.getDomainId());
                cp2ploginLoggerVO.setCategoryCode(cp2pSubscriberVO.getCategory());
                cp2pSubscriberVO.setSessionInfoVO(sessionInfoVO);
            }

            cp2pSubscriberVO = validateUserLoginDetails(con, _request, cp2ploginLoggerVO, cp2pSubscriberVO, decryptedPassword);
            boolean msisdnbarred = _cp2pLoginDAO.isSubscriberBarred(con, cp2pSubscriberVO.getMsisdn());

            if (msisdnbarred) {
                throw new BTSLBaseException(this, "loadCp2pUserDetails", "cp2p.login.error.msisdnbarred", "cp2pLogin");
            }

            if (cp2pSubscriberVO.isDuplicateLogin()) {
                cp2ploginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.doublelogin"));
                String arr[] = { cp2pSubscriberVO.getDuplicateHost() };
                BTSLMessages btslMessage = new BTSLMessages("cp2p.login.error.doublelogin", arr, "validateDuplicateLogin");
                return ERROR;
            }

            cp2ploginLoggerVO.setOtherInformation(this.getText("cp2p.login.label.successlogin"));
            cp2ploginLoggerVO.setOtherInformation(cp2ploginLoggerVO.getOtherInformation() + "		" + _request.getSession().getId());

            if (cp2pSubscriberVO != null) {
                // Roles set in UserVO
                String roleAssignment = MenuBL.FIXED;

                ArrayList menuItemList = MenuBL.getMenuItemList(con, cp2pSubscriberVO.getActiveUserId(), cp2pSubscriberVO.getCategory(), roleAssignment, Constants.getProperty("ROLE_TYPE"), cp2pSubscriberVO.getDomainId());

                cp2pSubscriberVO.setMenuItemList(menuItemList);

                // load the services info from the user_services table that are
                // assigned to the user

                cp2pSubscriberVO.setCP2PServiceList(_cp2pLoginDAO.loadSubscriberServicesList(con, cp2pSubscriberVO.getCategory()));
                if (menuItemList == null || menuItemList.size() == 0) {
                    this.addActionError(getText("login.index.nomenu"));
                    return ERROR;
                }
                if (menuItemList == null || menuItemList.size() == 0) {
                    this.addActionError(getText("login.index.nomenu"));
                    return ERROR;
                }
                HttpSession session = _request.getSession();
                session.setAttribute("cp2pSubscriberVO", cp2pSubscriberVO);
                Date resetPasswordExpiredTime = null;
                if (cp2pSubscriberVO.getPasswordModifiedOn() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(cp2pSubscriberVO.getPasswordModifiedOn());
                    int resetPasswordExpiredInHours = ((Integer) PreferenceCache.getControlPreference(PreferenceI.RESET_PASSWORD_EXPIRED_TIME_IN_HOURS, cp2pSubscriberVO.getNetworkId(), cp2pSubscriberVO.getCategory())).intValue();
                    cal.add(Calendar.HOUR, resetPasswordExpiredInHours);
                    resetPasswordExpiredTime = cal.getTime();
                }

                if (cp2pSubscriberVO.getLastLoginOn() == null || _isFirstTimeLogin || cp2pSubscriberVO.getPasswordModifiedOn() == null) {
                    /*
                     * This attribute is used on the change password screen
                     * 
                     * if this attribute is exist in the session left menu will
                     * not be displayed
                     * this force the user to change the password when password
                     * is changed
                     * successfully this attribute is remove from the session in
                     * UserAction Class(method changePassword)
                     */
                    // logic is applicable at the first time when user wants to
                    // login in to the system,after user creation only.
                    Date passwordExpiredTime = null;
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(cp2pSubscriberVO.getModifiedOn());
                    // cal.setTime(modifiedOn);
                    int passwordExpiredInHours = ((Integer) PreferenceCache.getControlPreference(PreferenceI.RESET_PASSWORD_EXPIRED_TIME_IN_HOURS, cp2pSubscriberVO.getNetworkId(), cp2pSubscriberVO.getCategory())).intValue();
                    cal.add(Calendar.HOUR, passwordExpiredInHours);
                    passwordExpiredTime = cal.getTime();

                    if (cp2pSubscriberVO.getPasswordReset().equals("Y") && cp2pSubscriberVO.getLoginTime().after(passwordExpiredTime)) {
                        cp2ploginLoggerVO.setOtherInformation(this.getText("login.index.error.passwordexpired"));
                        String arr[] = { BTSLUtil.getDateTimeStringFromDate(passwordExpiredTime) };
                        _request.getSession().invalidate();
                        throw new BTSLBaseException("cp2p.login.error.passwordexpired", arr, "logout");
                    }
                    session.setAttribute("leftMenu", "N");
                    _request.getSession().setAttribute("isForceChanged", "Y");
                    _request.getSession().setAttribute("cp2pSubscriberVO", cp2pSubscriberVO);
                    _request.getSession().setAttribute("menuItemList", menuItemList);
                    _request.getSession().setAttribute("dispMenu", "");
                    _request.getSession().setAttribute("PWDMOD", PretupsI.YES);
                    returnStr = SUCCESS;
                    passwordChangeFlag = true;

                } else if (cp2pSubscriberVO.getPasswordReset().equals("Y") && cp2pSubscriberVO.getLoginTime().before(resetPasswordExpiredTime)) {
                    session.setAttribute("leftMenu", "N");
                    _request.getSession().setAttribute("isForceChanged", "Y");
                    _request.getSession().setAttribute("cp2pSubscriberVO", cp2pSubscriberVO);
                    _request.getSession().setAttribute("menuItemList", menuItemList);
                    _request.getSession().setAttribute("dispMenu", "");
                    _request.getSession().setAttribute("PWDMOD", PretupsI.YES);
                    returnStr = SUCCESS;
                    passwordChangeFlag = true;
                } else if (cp2pSubscriberVO.getPasswordReset().equals("Y") && cp2pSubscriberVO.getLoginTime().after(resetPasswordExpiredTime)) {
                    cp2ploginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.resetpasswordexpired"));
                    String arr[] = { BTSLUtil.getDateTimeStringFromDate(resetPasswordExpiredTime) };
                    _request.getSession().invalidate();
                    throw new BTSLBaseException("cp2p.login.error.resetpasswordexpired", arr, "logout");
                } else if ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD).equals(cp2pSubscriberVO.getPassword()))// if
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
                    /*
                     * This attribute is used on the change password screen
                     * 
                     * if this attribute is exist in the session left menu will
                     * not be displayed
                     * this force the user to change the password when password
                     * is changed
                     * successfully this attribute is remove from the session in
                     * UserAction Class(method changePassword)
                     */
                    session.setAttribute("leftMenu", "N");
                    _request.getSession().setAttribute("isForceChanged", "Y");
                    _request.getSession().setAttribute("cp2pSubscriberVO", cp2pSubscriberVO);
                    _request.getSession().setAttribute("menuItemList", menuItemList);
                    _request.getSession().setAttribute("dispMenu", "");
                    _request.getSession().setAttribute("PWDMOD", PretupsI.YES);
                    returnStr = SUCCESS;
                    passwordChangeFlag = true;
                } else {
                    java.util.Date date1 = cp2pSubscriberVO.getPasswordModifiedOn();
                    java.util.Date date2 = new java.util.Date();

                    long dt1 = date1.getTime();
                    long dt2 = date2.getTime();
                    long nodays = (long) ((dt2 - dt1) / (1000 * 60 * 60 * 24));
                    long noPasswordTimeOutDays = 0;
                    try {
                        noPasswordTimeOutDays = ((Integer) PreferenceCache.getControlPreference(PreferenceI.DAYS_AFTER_CHANGE_PASSWORD, cp2pSubscriberVO.getNetworkId(), cp2pSubscriberVO.getCategory())).intValue();
                    } catch (Exception e) {
                        _log.error("loadCp2pUserDetails", "Exceptin:e=" + e);
                    }
                    // System.out.println("loginController.jsp nodays="+nodays+"    noPasswordTimeOutDays="+noPasswordTimeOutDays);
                    /*
                     * Here we are checking whether the password change is
                     * required or not
                     * a)category check(In constants file we define those
                     * categories whom password change not required)
                     * b)No of days check
                     */
                    if (!BTSLUtil.isStringIn(cp2pSubscriberVO.getCategory(), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANGE_PASSWORD_NOT_REQUIRED_CATEGORY)) && nodays > noPasswordTimeOutDays) {
                        CP2PSubscriberVO tempSubscriberVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
                        tempSubscriberVO.setPasswordModifiedOn(null);
                        session.setAttribute("cp2pSubscriberVO", tempSubscriberVO);
                        /*
                         * This attribute is used on the change password screen
                         * 
                         * if this attribute is exust in the session left menu
                         * will not be displayed
                         * this force the user to change the password when
                         * password is changed
                         * successfully this attribute is remove from the
                         * session in UserAction Class(method changePassword)
                         */
                        session.setAttribute("leftMenu", "N");
                        _request.getSession().setAttribute("isForceChanged", "Y");
                        _request.getSession().setAttribute("cp2pSubscriberVO", cp2pSubscriberVO);
                        _request.getSession().setAttribute("menuItemList", menuItemList);
                        _request.getSession().setAttribute("dispMenu", "");
                        _request.getSession().setAttribute("PWDMOD", PretupsI.YES);
                        returnStr = SUCCESS;
                        passwordChangeFlag = true;
                    }

                    if (passwordChangeFlag) {
                        returnStr = "chpasswd";
                    } else {
                        returnStr = SUCCESS;
                    }

                    _request.getSession().setAttribute("cp2pSubscriberVO", cp2pSubscriberVO);
                    _request.getSession().setAttribute("menuItemList", menuItemList);
                    _request.getSession().setAttribute("dispMenu", "SELFTOPUP");
                    _request.getSession().setAttribute("HOMEPAGE", PretupsI.YES);
                    return returnStr;
                }
            } else {
                cp2ploginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.invalidlogin"));
                this.addActionError(getText("login.index.invalidLoginID"));
                return ERROR;
            }

        } catch (BTSLBaseException be) {
            /*
             * If exception comes during login we are forwarding to the
             * cp2pLogin.jsp
             * so before fowarding to the index.jsp we need to set the default
             * locale
             */

            _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
            BTSLBaseException beException = new BTSLBaseException(this, "loadCp2pUserDetails", be.getMessage(), 0, be.getArgs(), "cp2pLogin");
            _log.error("loadCp2pUserDetails", "Exceptin:e=" + be);
            _log.errorTrace("loadCp2pUserDetails: Exception print stack trace: ", be);
            this.addActionError(this.getText(be.getMessage()));
            returnStr = ERROR;
        }

        catch (Exception e) {
            /*
             * If exception comes during login we are forwarding to the
             * index.jsp
             * so before fowarding to the index.jsp we need to set the default
             * locale
             */

            _request.getSession().setAttribute(Globals.LOCALE_KEY, _request.getLocale());
            _log.error("loadCp2pUserDetails", "Exceptin:e=" + e);
            _log.errorTrace("loadCp2pUserDetails: Exception print stack trace: ", e);
            this.addActionError(this.getText("error.general.processing"));
            returnStr = ERROR;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
            // write the user information in the Log file
            if (_log.isDebugEnabled())
                _log.debug("loadCp2pUserDetails", "Exiting" + returnStr);
        }
        return returnStr;

    }

    private CP2PSubscriberVO validateUserLoginDetails(Connection p_con, HttpServletRequest _request, CP2PLoginLoggerVO p_cp2pLoginLoggerVO, CP2PSubscriberVO p_cp2pSubscriberVO, String p_decryptedPassword) throws Exception {

        int validStatus = 0;
        _isFirstTimeLogin = false;
        if (_log.isDebugEnabled())
            _log.debug("validateUserLoginDetails", "Entered");

        if (cp2pSubscriberVO == null) {
            p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.invalidlogin"));
            throw new BTSLBaseException("cp2p.login.error.invalidlogin", "cp2pLogin");
        }

        if (cp2pSubscriberVO.getInvalidPasswordCount() == ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, cp2pSubscriberVO.getNetworkId(), cp2pSubscriberVO.getCategory())).intValue()) {
            // got expiry period from SystemPreference table
            long expiryTime = ((Long) PreferenceCache.getControlPreference(PreferenceI.C2S_PWD_BLK_EXP_DURATION, cp2pSubscriberVO.getNetworkId(), cp2pSubscriberVO.getCategory())).longValue();
            // check expiry status
            if (BTSLUtil.isTimeExpired(cp2pSubscriberVO.getPswdCountUpdatedOn(), expiryTime)) {
                // set invalid count to 1 as per password
                // updatePasswordInvalidCount method logic
                cp2pSubscriberVO.setInvalidPasswordCount(1);

            } else {
                // if expiry period not over shows error message
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.invalidpwd.passwordblocked"));
                throw new BTSLBaseException(this, "updatePasswordInvalidCount", "cp2p.login.error.invalidpwd.passwordblocked", "cp2pLogin");
            }
        }
        if (updatePasswordInvalidCount(p_con, p_cp2pSubscriberVO, _request, p_cp2pLoginLoggerVO, p_decryptedPassword)) {
            if (cp2pSubscriberVO.getInvalidPasswordCount() == ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, cp2pSubscriberVO.getNetworkId(), cp2pSubscriberVO.getCategory())).intValue()) {
                // If password is blocked throw an exception
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.invalidpwd.passwordblocked"));
                throw new BTSLBaseException(this, "updatePasswordInvalidCount", "cp2p.login.error.invalidpwd.passwordblocked", "cp2pLogin");
            }
            p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.invalidpassword"));
            throw new BTSLBaseException("cp2p.login.error.invalidpassword", "cp2pLogin");
        } else if (cp2pSubscriberVO.getInvalidPasswordCount() == ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, cp2pSubscriberVO.getNetworkId(), cp2pSubscriberVO.getCategory())).intValue()) {
            // If password is blocked throw an exception
            p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.passwordblocked"));
            throw new BTSLBaseException(this, "updatePasswordInvalidCount", "cp2p.login.error.passwordblocked", "cp2pLogin");
        }
        // else
        // if((PretupsI.STATUS_SUSPEND.equals(cp2pSubscriberVO.getNetworkStatus()))
        // &&
        // (TypesI.NO).equalsIgnoreCase(cp2pSubscriberVO.getCategoryVO().getViewOnNetworkBlock()))
        else if ((PretupsI.STATUS_SUSPEND.equals(cp2pSubscriberVO.getNetworkStatus()))) {
            p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.networksuspend"));
            /*
             * If netwrok is temporally suspended and also the view_on_network
             * flag of that category
             * is N throw a exception which show the langguage 1 or language 2
             * message why netwrok is
             * suspended
             */
            String[] str = { cp2pSubscriberVO.getMessage() };
            throw new BTSLBaseException("cp2p.login.error.networksuspend", str, "cp2pLogin");
        }

        else {
            _log.info("validateUserLoginDetails", "_loginID=" + cp2pSubscriberVO.getLoginId() + "     url=" + _request.getRemoteAddr() + "  time=" + new Date());

            if (PretupsI.USER_STATUS_NEW.equals(cp2pSubscriberVO.getStatus()) || PretupsI.USER_STATUS_APPROVED.equals(cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.userapprovalpending"));
                throw new BTSLBaseException("cp2p.login.error.userapprovalpending", "cp2pLogin");
            } else if (PretupsI.USER_STATUS_SUSPEND.equals(cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.usersuspended"));
                throw new BTSLBaseException("cp2p.login.error.usersuspended", "cp2pLogin");
            } else if (PretupsI.USER_STATUS_BLOCK.equals(cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.userblocked"));
                throw new BTSLBaseException("cp2p.login.error.userblocked", "cp2pLogin");
            } else if (PretupsI.USER_STATUS_DEREGISTERED.equals(cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.userderegistered"));
                throw new BTSLBaseException("cp2p.login.error.userderegistered", "cp2pLogin");
            } else if (PretupsI.USER_STATUS_SUSPEND_REQUEST.equals(cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.usersuspendedrequest"));
                throw new BTSLBaseException("cp2p.login.error.usersuspendedrequest", "cp2pLogin");
            } else if (PretupsI.USER_STATUS_DELETE_REQUEST.equals(cp2pSubscriberVO.getStatus())) {
                p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.userdeletedrequest"));
                throw new BTSLBaseException("cp2p.login.error.userdeletedrequest", "cp2pLogin");
            }

            else {
                if (_log.isDebugEnabled()) {
                    _log.debug("validateUserLoginDetails()", " Calling CP2PSessionCounter to checkMaxLocationTypeUsers");
                }
                if ((cp2pSubscriberVO.getLastLoginOn() == null || "Y".equals(cp2pSubscriberVO.getPasswordReset())) && !_isFirstTimeLogin)
                    _isFirstTimeLogin = true;
                cp2pSubscriberVO.setLastLoginOn(new Date());
                CP2PLoginDAO cp2pLoginDAO = new CP2PLoginDAO();
                int count = cp2pLoginDAO.updateUserLoginDetails(p_con, cp2pSubscriberVO);
                if (count > 0) {
                    validStatus = 2;
                    p_con.commit();
                } else {
                    validStatus = 0;
                    p_con.rollback();
                }
                cp2pSubscriberVO.setValidStatus(validStatus);
            }
        }

        return p_cp2pSubscriberVO;

    }

    /**
     * Method to update the Password Invalid Count in the data base
     * 
     * @param p_decryptedPassword
     *            TODO
     * @param p_userVO
     *            UserVO
     * @param p_loginForm
     *            LoginForm
     * @param p_loginLoggerVO
     * @throws Exception
     */
    private boolean updatePasswordInvalidCount(Connection p_con, CP2PSubscriberVO p_cp2pSubscriberVO, HttpServletRequest request, CP2PLoginLoggerVO p_cp2pLoginLoggerVO, String p_decryptedPassword) throws Exception {
        boolean passwordStatus = false;
        int updateStatus = 0;

        try {
            CP2PLoginDAO _cp2ploginDAO = new CP2PLoginDAO();

            String decryptedPassword = BTSLUtil.decryptText(p_cp2pSubscriberVO.getPassword());
            Date currentDate = new Date();
            p_cp2pSubscriberVO.setModifiedOn(currentDate);

            if (_log.isDebugEnabled())
                _log.debug("updatePasswordInvalidCount", "User Login Id:" + p_cp2pSubscriberVO.getLoginId() + " decrypted Password=" + decryptedPassword + " entered Password=" + p_cp2pSubscriberVO.getPassword());

            /**
             * changes done by ashishT for implementing hashing
             * encrypting the password from form and then comparing it with
             * hashvalue from cp2psubVo.
             */
            boolean b;
            if ("SHA".equalsIgnoreCase(SystemPreferences.PINPAS_EN_DE_CRYPTION_TYPE))
                b = PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_cp2pSubscriberVO.getPassword(), p_cp2pSubscriberVO.getPassword()));
            else
                b = (!decryptedPassword.equals(p_decryptedPassword));
            if (b) {
                long mintInDay = 24 * 60;
                if (p_cp2pSubscriberVO.getPswdCountUpdatedOn() != null) {
                    // Check if Password counters needs to be reset after the
                    // reset duration
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(p_cp2pSubscriberVO.getModifiedOn());
                    int d1 = cal.get(Calendar.DAY_OF_YEAR);
                    cal.setTime(p_cp2pSubscriberVO.getPswdCountUpdatedOn());
                    int d2 = cal.get(Calendar.DAY_OF_YEAR);
                    if (_log.isDebugEnabled())
                        _log.debug("updatePasswordInvalidCount", "Day Of year of Modified On=" + d1 + " Day Of year of PasswordCountUpdatedOn=" + d2);
                    if (d1 != d2 && ((Long) PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION, p_cp2pSubscriberVO.getNetworkId(), p_cp2pSubscriberVO.getCategory())).longValue() <= mintInDay) {
                        // reset
                        p_cp2pSubscriberVO.setInvalidPasswordCount(1);
                        p_cp2pSubscriberVO.setPswdCountUpdatedOn(p_cp2pSubscriberVO.getModifiedOn());
                    } else if (d1 != d2 && ((Long) PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION, p_cp2pSubscriberVO.getNetworkId(), p_cp2pSubscriberVO.getCategory())).longValue() >= mintInDay && (d1 - d2) >= (((Long) PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION, p_cp2pSubscriberVO.getNetworkId(), p_cp2pSubscriberVO.getCategory())).longValue() / mintInDay)) {
                        // Reset
                        p_cp2pSubscriberVO.setInvalidPasswordCount(1);
                        p_cp2pSubscriberVO.setPswdCountUpdatedOn(p_cp2pSubscriberVO.getModifiedOn());
                    } else if (((p_cp2pSubscriberVO.getModifiedOn().getTime() - p_cp2pSubscriberVO.getPswdCountUpdatedOn().getTime()) / (60 * 1000)) < ((Long) PreferenceCache.getControlPreference(PreferenceI.PASSWORD_BLK_RST_DURATION, p_cp2pSubscriberVO.getNetworkId(), p_cp2pSubscriberVO.getCategory())).longValue()) {

                        if (p_cp2pSubscriberVO.getInvalidPasswordCount() - ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, p_cp2pSubscriberVO.getNetworkId(), p_cp2pSubscriberVO.getCategory())).intValue() == 0) {
                            throw new BTSLBaseException(this, "updatePasswordInvalidCount", "cp2p.login.error.passwordblocked", "cp2pLogin");
                        }
                        p_cp2pSubscriberVO.setInvalidPasswordCount(p_cp2pSubscriberVO.getInvalidPasswordCount() + 1);
                        p_cp2pSubscriberVO.setPswdCountUpdatedOn(p_cp2pSubscriberVO.getModifiedOn());

                    } else {
                        p_cp2pSubscriberVO.setInvalidPasswordCount(1);
                        p_cp2pSubscriberVO.setPswdCountUpdatedOn(p_cp2pSubscriberVO.getModifiedOn());
                    }
                } else {
                    p_cp2pSubscriberVO.setInvalidPasswordCount(1);
                    p_cp2pSubscriberVO.setPswdCountUpdatedOn(p_cp2pSubscriberVO.getModifiedOn());
                }

                updateStatus = _cp2ploginDAO.updatePasswordCounter(p_con, p_cp2pSubscriberVO);
                if (updateStatus > 0)
                    p_con.commit();
                else {
                    p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.invalidpassword"));
                    p_con.rollback();
                    throw new BTSLBaseException("cp2p.login.error.invalidpassword", "cp2pLogin");
                }
                passwordStatus = true;
            } else {
                // initilize Password Counters if ifPinCount>0
                if (p_cp2pSubscriberVO.getInvalidPasswordCount() > 0) {
                    p_cp2pSubscriberVO.setInvalidPasswordCount(0);
                    p_cp2pSubscriberVO.setPswdCountUpdatedOn(null);
                    updateStatus = _cp2ploginDAO.updatePasswordCounter(p_con, p_cp2pSubscriberVO);
                    if (updateStatus > 0)
                        p_con.commit();
                    else {
                        p_cp2pLoginLoggerVO.setOtherInformation(this.getText("cp2p.login.error.invalidpassword"));
                        p_con.rollback();
                        throw new BTSLBaseException("cp2p.login.error.invalidpassword", "cp2pLogin");
                    }
                }
            }
        } catch (Exception e) {
            this.addActionError(this.getText("error.general.processing"));
            throw e;

        } finally {
            if (_log.isDebugEnabled())
                _log.debug("updatePasswordInvalidCount", "Exiting  ::: passwordStatus:" + passwordStatus);
        }
        return passwordStatus;
    }// end of updatePasswordInvalidCount

    public String gethomepage() throws Exception {
        return "homepage";
    }

    public String homepage() throws Exception {
        if (_request.getSession() != null && PretupsI.YES.equals(_request.getSession().getAttribute("PWDMOD"))) {
            return "chpasswd";
        }
        if (_request.getSession() != null && PretupsI.YES.equals(_request.getSession().getAttribute("HOMEPAGE"))) {
            return "homepage";
        }
        _request.getSession().invalidate();
        _request.setAttribute("keepLogin", "keepLogin");
        return "securityLogout";
    }

    public String logout() {
        HttpSession session = null;
        String returnStr = "logout";
        try {
            session = _request.getSession();
            return returnStr;
        } catch (Exception e) {
            _log.errorTrace("logout: Exception print stack trace: ", e);
            this.addActionError(this.getText("error.general.processing"));
            returnStr = "logout";
            return returnStr;
        } finally {
            session.invalidate();
            this.addActionMessage(this.getText("login.index.label.successlogout"));
            _request.setAttribute("keepLogin", "keepLogin");
        }
    }

    public String logedout() {
        String returnStr = "logout";
        HttpSession session = null;
        try {
            session = _request.getSession();
            return returnStr;
        } catch (Exception e) {
            _log.error("logedout", "logedout Exceptin e:" + e);
            this.addActionError(this.getText("error.general.processing"));
            returnStr = "logout";
            return returnStr;
        } finally {
            session.invalidate();
            this.addActionError(this.getText("login.index.label.sessionSuccesslogout"));
            _request.setAttribute("keepLogin", "keepLogin");
        }
    }

    public void validate() {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered");
        CP2PLoginLoggerVO cp2ploginLoggerVO = new CP2PLoginLoggerVO();
        CP2PSubscriberVO cp2pSubscriberVO = this.cp2pSubscriberVO;
        if (_request.getServletPath().equals("/cp2plogin/cp2plogin_loadCp2pUserDetails.action")) {
            if (UtilValidate.isEmpty(cp2pSubscriberVO.getLoginId())) {
                cp2ploginLoggerVO.setOtherInformation(this.getText("login.error.loginIdEmpty"));
                this.addActionError(getText("login.error.loginIdEmpty"));
            }
            if (UtilValidate.isEmpty(cp2pSubscriberVO.getPassword())) {
                cp2ploginLoggerVO.setOtherInformation(this.getText("login.error.passwordEmpty"));
                this.addActionError(getText("login.error.passwordEmpty"));
            }
            if (UtilValidate.isEmpty(cp2pSubscriberVO.getLanguage())) {
                cp2ploginLoggerVO.setOtherInformation(this.getText("login.error.languageEmpty"));
                this.addActionError(getText("login.error.languageEmpty"));
            }
        }

        if (_log.isDebugEnabled())
            _log.debug("validate", "Exiting");
    }

}
