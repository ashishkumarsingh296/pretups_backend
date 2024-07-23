package com.btsl.session.businesslogic;

import java.io.Serializable;

/**
 * @(#)ActiveSessionVO.java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Mohit Goel 20/07/2005 Initial Creation
 * 
 */
public class ActiveSessionVO implements Serializable {

    private String _sessionKey;
    private String _userId;
    private String _userLoginId;
    private String _userType;
    private String _userRemoteAddress;
    private String _userRemoteHost;
    private String _loginTime;
    private String _lastAccessedTime;
    private String _parentCode;

    /**
     * @return Returns the lastAccessedTime.
     */
    public String getLastAccessedTime() {
        return _lastAccessedTime;
    }

    /**
     * @param lastAccessedTime
     *            The lastAccessedTime to set.
     */
    public void setLastAccessedTime(String lastAccessedTime) {
        this._lastAccessedTime = lastAccessedTime;
    }

    /**
     * @return Returns the loginDate.
     */
    public String getLoginTime() {
        return _loginTime;
    }

    /**
     * @param loginDate
     *            The loginDate to set.
     */
    public void setLoginTime(String loginTime) {
        this._loginTime = loginTime;
    }

    /**
     * @return Returns the parentCode.
     */
    public String getParentCode() {
        return _parentCode;
    }

    /**
     * @param parentCode
     *            The parentCode to set.
     */
    public void setParentCode(String parentCode) {
        this._parentCode = parentCode;
    }

    /**
     * @return Returns the sessionKey.
     */
    public String getSessionKey() {
        return _sessionKey;
    }

    /**
     * @param sessionKey
     *            The sessionKey to set.
     */
    public void setSessionKey(String sessionKey) {
        this._sessionKey = sessionKey;
    }

    /**
     * @return Returns the userLoginId.
     */
    public String getUserLoginId() {
        return _userLoginId;
    }

    /**
     * @param userLoginId
     *            The userLoginId to set.
     */
    public void setUserLoginId(String userLoginId) {
        this._userLoginId = userLoginId;
    }

    /**
     * @return Returns the userRemoteAddress.
     */
    public String getUserRemoteAddress() {
        return _userRemoteAddress;
    }

    /**
     * @param userRemoteAddress
     *            The userRemoteAddress to set.
     */
    public void setUserRemoteAddress(String userRemoteAddress) {
        this._userRemoteAddress = userRemoteAddress;
    }

    /**
     * @return Returns the userRemoteHost.
     */
    public String getUserRemoteHost() {
        return _userRemoteHost;
    }

    /**
     * @param userRemoteHost
     *            The userRemoteHost to set.
     */
    public void setUserRemoteHost(String userRemoteHost) {
        this._userRemoteHost = userRemoteHost;
    }

    /**
     * @return Returns the userType.
     */
    public String getUserType() {
        return _userType;
    }

    /**
     * @param userType
     *            The userType to set.
     */
    public void setUserType(String userType) {
        this._userType = userType;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(String userId) {
        _userId = userId;
    }
}
