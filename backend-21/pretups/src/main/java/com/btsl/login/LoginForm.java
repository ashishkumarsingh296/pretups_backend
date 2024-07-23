package com.btsl.login;

/**
 * @(#)LoginForm.java
 *                    Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                    All Rights Reserved
 *                    Form class for login
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Gurjeet Singh Bedi 24/06/2005 Initial Creation
 *                    ----------------------------------------------------------
 *                    --------------------------------------
 */

import java.util.Enumeration;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class LoginForm  {
    private String _loginID;
    private String _password;
    private boolean _focusPassword;// this flag is by default false set to true
                                   // in updatePasswordInvalidCount method of
                                   // the action
    private String _remoteHost;
    private String _duplicateHost;
    private boolean _duplicateLogin = false;
    private UserVO _userVO = null;
    private String _language;
    private String _otherInfo;
    private boolean _pwdBlocedkExpired = false;
    private String _rsaPasscode;
    private String _otp = null;
    // Captcha Authentication at Login Page Start- by akanksha.
    private String captchaVerificationCode = null;
    private boolean _captchaEnabled;
    private int _invalidPasswordCount;

    // Captcha Authentication at Login Page Start- by akanksha.

    public void flush() {
        _loginID = null;
        _password = null;
        captchaVerificationCode = null;
        _remoteHost = null;
        _duplicateHost = null;
        _duplicateLogin = false;
        _userVO = null;
        _focusPassword = false;
        _language = null;
        _otherInfo = null;// used for password encryption
        _pwdBlocedkExpired = false;
    }

    /**
     * Utility method to validate if Captcha is present in
     * the HTTPRequest
     * 
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
    private boolean isCaptchaPresent(HttpServletRequest request) {
        Enumeration<String> requestParams = request.getParameterNames();
        boolean captchaPresent = false;
        while (requestParams.hasMoreElements()) {

            String parameterName = (String) requestParams.nextElement();

            if ("captchaVerificationCode".equals(parameterName)) {
                captchaPresent = true;
            }
        }
        return captchaPresent;
    }

    public void semiFlush() {
        _remoteHost = null;
        _duplicateHost = null;
        _duplicateLogin = false;
        _userVO = null;
    }

    public String getLoginID() {
        return _loginID;
    }

    public void setLoginID(String loginID) {
        _loginID = loginID;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public String getRemoteHost() {
        return _remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        _remoteHost = remoteHost;
    }

    public String getDuplicateHost() {
        return _duplicateHost;
    }

    public void setDuplicateHost(String duplicateHost) {
        _duplicateHost = duplicateHost;
    }

    public boolean isDuplicateLogin() {
        return _duplicateLogin;
    }

    public void setDuplicateLogin(boolean duplicateLogin) {
        _duplicateLogin = duplicateLogin;
    }

    public UserVO getUserVO() {
        return _userVO;
    }

    public void setUserVO(UserVO userVO) {
        _userVO = userVO;
    }

    /**
     * @return Returns the focusPassword.
     */
    public boolean isFocusPassword() {
        return _focusPassword;
    }

    /**
     * @param focusPassword
     *            The focusPassword to set.
     */
    public void setFocusPassword(boolean focusPassword) {
        _focusPassword = focusPassword;
    }

    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return _language;
    }

    /**
     * @param language
     *            The language to set.
     */
    public void setLanguage(String language) {
        _language = language;
    }

    /**
     * @return Returns the _otherInfo.
     */
    public String getOtherInfo() {
        return _otherInfo;
    }

    /**
     * @param info
     *            The _otherInfo to set.
     */
    public void setOtherInfo(String otherInfo) {
        _otherInfo = otherInfo;
    }

    /**
     * @return Returns the _pwdBlocedkExpired.
     */
    public boolean isPWDBlocedkExpired() {
        return _pwdBlocedkExpired;
    }

    /**
     * @param blocedkExpired
     *            The _pwdBlocedkExpired to set.
     */
    public void setPWDBlocedkExpired(boolean pwdBlocedkExpired) {
        _pwdBlocedkExpired = pwdBlocedkExpired;
    }

    /**
     * @return Returns the rsaPasscode.
     */
    public String getRsaPasscode() {
        return _rsaPasscode;
    }

    /**
     * @param p_rsaPasscode
     *            The rsaPasscode to set.
     */
    public void setRsaPasscode(String p_rsaPasscode) {
        _rsaPasscode = p_rsaPasscode;
    }

    public String getOtp() {
        return _otp;
    }

    public void setOtp(String otp) {
        _otp = otp;
    }

    public String getcaptchaVerificationCode() {
        return captchaVerificationCode;
    }

    public void setcaptchaVerificationCode(String captchaVerificationCode) {
        this.captchaVerificationCode = captchaVerificationCode;
    }

    public void setCaptchaEnabled(boolean cap) {
        this._captchaEnabled = cap;
    }

    public boolean getCaptchaEnabled() {
        return _captchaEnabled;
    }

}
