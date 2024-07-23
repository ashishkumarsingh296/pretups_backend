package com.btsl.cp2p.login.businesslogic;

import java.io.Serializable;
import java.util.Date;

/**
 * @(#)CardGroupSetVO.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Mohit Goel 26/08/2005 Initial Creation
 * 
 *                         This class is used to log the user details during
 *                         login,
 *                         logout and when user session expired.
 * 
 */
public class CP2PLoginLoggerVO implements Serializable {
	private static final long serialVersionUID = 1L;
    private String _loginID = null;
    private String _userID = null;
    private String _userName = null;
    private String _networkID = null;
    private String _networkName = null;
    private String _userType = null;
    private String _domainID = null;
    private String _categoryCode = null;
    private String _logType = null;
    private Date _loginTime;
    private Date _logoutTime;
    private String _ipAddress;
    private String _browser = null;
    private String _otherInformation = null;
    private String _subscriberType;

    /**
     * @return Returns the browser.
     */
    public String getBrowser() {
        return _browser;
    }

    /**
     * @param browser
     *            The browser to set.
     */
    public void setBrowser(String browser) {
        if (browser != null)
            _browser = browser.trim();
    }

    /**
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        if (categoryCode != null)
            _categoryCode = categoryCode.trim();
    }

    /**
     * @return Returns the domainID.
     */
    public String getDomainID() {
        return _domainID;
    }

    /**
     * @param domainID
     *            The domainID to set.
     */
    public void setDomainID(String domainID) {
        if (domainID != null)
            _domainID = domainID.trim();
    }

    /**
     * @return Returns the ipAddress.
     */
    public String getIpAddress() {
        return _ipAddress;
    }

    /**
     * @param ipAddress
     *            The ipAddress to set.
     */
    public void setIpAddress(String ipAddress) {
        _ipAddress = ipAddress;
    }

    /**
     * @return Returns the loginID.
     */
    public String getLoginID() {
        return _loginID;
    }

    /**
     * @param loginID
     *            The loginID to set.
     */
    public void setLoginID(String loginID) {
        if (loginID != null)
            _loginID = loginID.trim();
    }

    /**
     * @return Returns the loginTime.
     */
    public Date getLoginTime() {
        return _loginTime;
    }

    /**
     * @param loginTime
     *            The loginTime to set.
     */
    public void setLoginTime(Date loginTime) {
        _loginTime = loginTime;
    }

    /**
     * @return Returns the logoutTime.
     */
    public Date getLogoutTime() {
        return _logoutTime;
    }

    /**
     * @param logoutTime
     *            The logoutTime to set.
     */
    public void setLogoutTime(Date logoutTime) {
        _logoutTime = logoutTime;
    }

    /**
     * @return Returns the logType.
     */
    public String getLogType() {
        return _logType;
    }

    /**
     * @param logType
     *            The logType to set.
     */
    public void setLogType(String logType) {
        if (logType != null)
            _logType = logType.trim();
    }

    /**
     * @return Returns the networkID.
     */
    public String getNetworkID() {
        return _networkID;
    }

    /**
     * @param networkID
     *            The networkID to set.
     */
    public void setNetworkID(String networkID) {
        if (networkID != null)
            _networkID = networkID.trim();
    }

    /**
     * @return Returns the networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * @param networkName
     *            The networkName to set.
     */
    public void setNetworkName(String networkName) {
        if (networkName != null)
            _networkName = networkName.trim();
    }

    /**
     * @return Returns the otherInformation.
     */
    public String getOtherInformation() {
        return _otherInformation;
    }

    /**
     * @param otherInformation
     *            The otherInformation to set.
     */
    public void setOtherInformation(String otherInformation) {
        if (otherInformation != null)
            _otherInformation = otherInformation.trim();
    }

    /**
     * @return Returns the userID.
     */
    public String getUserID() {
        return _userID;
    }

    /**
     * @param userID
     *            The userID to set.
     */
    public void setUserID(String userID) {
        if (userID != null)
            _userID = userID.trim();
    }

    /**
     * @return Returns the userName.
     */
    public String getUserName() {
        return _userName;
    }

    /**
     * @param userName
     *            The userName to set.
     */
    public void setUserName(String userName) {
        if (userName != null)
            _userName = userName.trim();
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
        if (userType != null)
            _userType = userType.trim();
    }

    public String getSubscriberType() {
        return _subscriberType;
    }

    public void setSubscriberType(String subscriberType) {
        _subscriberType = subscriberType;
    }
}
