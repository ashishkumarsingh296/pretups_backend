package com.selftopup.cp2p.subscriber.businesslogic;

/*
 * CP2PSubscriberVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gopal 22/06/2009 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Comvivia Technologies Ltd.
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.selftopup.menu.MenuItem;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.LocaleMasterVO;
import com.selftopup.pretups.subscriber.businesslogic.SubscriberVO;
import com.selftopup.user.businesslogic.SessionInfoVO;

public class CP2PSubscriberVO extends SubscriberVO {

    private String _loginId;
    private String _password;
    private String _userId;
    private String _userName;
    private String _billingCycleDate;
    private String _billingType;
    private String _buddySeqNumber;
    private String _consecutiveFailures;
    private String _country;
    private String _creditLimit;
    private String _dailyTxrAmount;
    private String _dailyTxrCount;
    private String _pinBlockCount;
    private String _networkName;
    private String _reportHeaderName;
    private String _networkId;
    private String _networkStatus;
    private String _message;
    private String _activeUserId;
    private String _domainId;
    private String _remoteAddress;
    private String _browserType;
    private String _passwordReset;
    private String _loggerMessage;
    private String _otherInformation;
    private String _currentRoleCode;
    private String _smsPin;
    private String _pinRequired;
    private String _serviceClassId;

    private Date _activatedOn;
    private Date _lastLoginOn;
    private Date _firstInvalidPinTime;
    private Date _pinModifiedOn;
    private Date _previousTxrDate;
    private Date _previousTxrMonthDate;
    private Date _previousTxrWeekDate;
    private Date _passwordModifiedOn;
    private Date _registeredOn;
    private Date _pswdCountUpdatedOn;
    private Date _loginTime;

    private int _invalidPasswordCount;
    private int _validStatus;

    private long _previousDailyTxrAmount;
    private long _previousDailyTxrCount;
    private long _previousMonthlyTxrAmount;
    private long _previousMonthlyTxrCount;
    private long _previousWeeklyTxrAmount;
    private long _previousWeeklyTxrCount;
    private long _weeklyTxrAmount;
    private long _weeklyTxrCount;

    private ArrayList _menuItemList;
    private ArrayList _associatdServiceTypeList;

    private SessionInfoVO _sessionInfoVO = null;

    private String _language;
    private Locale _locale = null;
    private String _messageCode;
    private String[] _messageArguments; // arguments send with the message
    private HashMap _localeList = null;
    private boolean _isDuplicateLogin;
    private String _duplicateHost;
    private String _subscriberPassword;
    private String _emailId;

    // added by sonali for self topup
    private String _oldPassword = null;
    private String _newPassword = null;
    private String _confirmPassword = null;
    private String _oldPin = null;
    private String _newPin = null;
    private String _confirmPin = null;

    public void flush() {

        _loginId = null;
        _password = null;
        _userId = null;
        _userName = null;
        _billingCycleDate = null;
        _billingType = null;
        _buddySeqNumber = null;
        _consecutiveFailures = null;
        _country = null;
        _creditLimit = null;
        _dailyTxrAmount = null;
        _dailyTxrCount = null;
        _pinBlockCount = null;
        _networkName = null;
        _reportHeaderName = null;
        _networkId = null;
        _networkStatus = null;
        _message = null;
        _activeUserId = null;
        _domainId = null;
        _remoteAddress = null;
        _browserType = null;
        _passwordReset = null;
        _loggerMessage = null;
        _otherInformation = null;
        _currentRoleCode = null;
        _smsPin = null;
        _pinRequired = null;
        _serviceClassId = null;
        _activatedOn = null;
        _lastLoginOn = null;
        _firstInvalidPinTime = null;
        _pinModifiedOn = null;
        _previousTxrDate = null;
        _previousTxrMonthDate = null;
        _previousTxrWeekDate = null;
        _passwordModifiedOn = null;
        _registeredOn = null;
        _pswdCountUpdatedOn = null;
        _loginTime = null;
        _menuItemList = null;
        _associatdServiceTypeList = null;
        _sessionInfoVO = null;
        _language = null;
        _locale = null;
        _messageCode = null;
        _messageArguments = null;
        _emailId = null;
    }

    /**
     * @return Returns the _loginId.
     */
    public String getLoginId() {
        return _loginId;
    }

    /**
     * @param id
     *            The _loginId to set.
     */
    public void setLoginId(String id) {
        _loginId = id;
    }

    /**
     * @return Returns the _password.
     */
    public String getPassword() {
        return _password;
    }

    /**
     * @param _password
     *            The _password to set.
     */
    public void setPassword(String _password) {
        this._password = _password;
    }

    public SessionInfoVO getSessionInfoVO() {
        return _sessionInfoVO;
    }

    public void setSessionInfoVO(SessionInfoVO sessionInfoVO) {
        _sessionInfoVO = sessionInfoVO;
    }

    public String getUserId() {
        return _userId;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserId(String userId) {
        _userId = userId;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public String getBillingCycleDate() {
        return _billingCycleDate;
    }

    public void setBillingCycleDate(String billingCycleDate) {
        _billingCycleDate = billingCycleDate;
    }

    public String getBillingType() {
        return _billingType;
    }

    public void setBillingType(String billingType) {
        _billingType = billingType;
    }

    public String getBuddySeqNumber() {
        return _buddySeqNumber;
    }

    public void setBuddySeqNumber(String buddySeqNumber) {
        _buddySeqNumber = buddySeqNumber;
    }

    public String getConsecutiveFailures() {
        return _consecutiveFailures;
    }

    public void setConsecutiveFailures(String consecutiveFailures) {
        _consecutiveFailures = consecutiveFailures;
    }

    public String getCountry() {
        return _country;
    }

    public void setCountry(String country) {
        _country = country;
    }

    public Date getActivatedOn() {
        return _activatedOn;
    }

    public Date getLastLoginOn() {
        return _lastLoginOn;
    }

    public void setActivatedOn(Date activatedOn) {
        _activatedOn = activatedOn;
    }

    public void setLastLoginOn(Date lastLoginOn) {
        _lastLoginOn = lastLoginOn;
    }

    public String getCreditLimit() {
        return _creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        _creditLimit = creditLimit;
    }

    public String getDailyTxrAmount() {
        return _dailyTxrAmount;
    }

    public void setDailyTxrAmount(String dailyTxrAmount) {
        _dailyTxrAmount = dailyTxrAmount;
    }

    public String getDailyTxrCount() {
        return _dailyTxrCount;
    }

    public void setDailyTxrCount(String dailyTxrCount) {
        _dailyTxrCount = dailyTxrCount;
    }

    public Date getFirstInvalidPinTime() {
        return _firstInvalidPinTime;
    }

    public void setFirstInvalidPinTime(Date firstInvalidPinTime) {
        _firstInvalidPinTime = firstInvalidPinTime;
    }

    public String getPinBlockCount() {
        return _pinBlockCount;
    }

    public void setPinBlockCount(String pinBlockCount) {
        _pinBlockCount = pinBlockCount;
    }

    public Date getPinModifiedOn() {
        return _pinModifiedOn;
    }

    public void setPinModifiedOn(Date pinModifiedOn) {
        _pinModifiedOn = pinModifiedOn;
    }

    public long getPreviousDailyTxrAmount() {
        return _previousDailyTxrAmount;
    }

    public long getPreviousDailyTxrCount() {
        return _previousDailyTxrCount;
    }

    public long getPreviousMonthlyTxrAmount() {
        return _previousMonthlyTxrAmount;
    }

    public long getPreviousMonthlyTxrCount() {
        return _previousMonthlyTxrCount;
    }

    public void setPreviousDailyTxrAmount(long previousDailyTxrAmount) {
        _previousDailyTxrAmount = previousDailyTxrAmount;
    }

    public void setPreviousDailyTxrCount(long previousDailyTxrCount) {
        _previousDailyTxrCount = previousDailyTxrCount;
    }

    public void setPreviousMonthlyTxrAmount(long previousMonthlyTxrAmount) {
        _previousMonthlyTxrAmount = previousMonthlyTxrAmount;
    }

    public void setPreviousMonthlyTxrCount(long previousMonthlyTxrCount) {
        _previousMonthlyTxrCount = previousMonthlyTxrCount;
    }

    public Date getPreviousTxrDate() {
        return _previousTxrDate;
    }

    public void setPreviousTxrDate(Date previousTxrDate) {
        _previousTxrDate = previousTxrDate;
    }

    public Date getPreviousTxrMonthDate() {
        return _previousTxrMonthDate;
    }

    public Date getPreviousTxrWeekDate() {
        return _previousTxrWeekDate;
    }

    public void setPreviousTxrMonthDate(Date previousTxrMonthDate) {
        _previousTxrMonthDate = previousTxrMonthDate;
    }

    public void setPreviousTxrWeekDate(Date previousTxrWeekDate) {
        _previousTxrWeekDate = previousTxrWeekDate;
    }

    public long getPreviousWeeklyTxrAmount() {
        return _previousWeeklyTxrAmount;
    }

    public long getPreviousWeeklyTxrCount() {
        return _previousWeeklyTxrCount;
    }

    public void setPreviousWeeklyTxrAmount(long previousWeeklyTxrAmount) {
        _previousWeeklyTxrAmount = previousWeeklyTxrAmount;
    }

    public void setPreviousWeeklyTxrCount(long previousWeeklyTxrCount) {
        _previousWeeklyTxrCount = previousWeeklyTxrCount;
    }

    public Date getPasswordModifiedOn() {
        return _passwordModifiedOn;
    }

    public void setPasswordModifiedOn(Date passwordModifiedOn) {
        _passwordModifiedOn = passwordModifiedOn;
    }

    public Date getRegisteredOn() {
        return _registeredOn;
    }

    public void setRegisteredOn(Date registeredOn) {
        _registeredOn = registeredOn;
    }

    public String getServiceClassId() {
        return _serviceClassId;
    }

    public void setServiceClassId(String serviceClassId) {
        _serviceClassId = serviceClassId;
    }

    public long getWeeklyTxrAmount() {
        return _weeklyTxrAmount;
    }

    public long getWeeklyTxrCount() {
        return _weeklyTxrCount;
    }

    public void setWeeklyTxrAmount(long weeklyTxrAmount) {
        _weeklyTxrAmount = weeklyTxrAmount;
    }

    public void setWeeklyTxrCount(long weeklyTxrCount) {
        _weeklyTxrCount = weeklyTxrCount;
    }

    public Date getPswdCountUpdatedOn() {
        return _pswdCountUpdatedOn;
    }

    public void setPswdCountUpdatedOn(Date pswdCountUpdatedOn) {
        _pswdCountUpdatedOn = pswdCountUpdatedOn;
    }

    public String getNetworkName() {
        return _networkName;
    }

    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    public String getMessage() {
        return _message;
    }

    public String getNetworkStatus() {
        return _networkStatus;
    }

    public String getReportHeaderName() {
        return _reportHeaderName;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public void setNetworkStatus(String networkStatus) {
        _networkStatus = networkStatus;
    }

    public void setReportHeaderName(String reportHeaderName) {
        _reportHeaderName = reportHeaderName;
    }

    public String getActiveUserId() {
        return _activeUserId;
    }

    public void setActiveUserId(String activeUserId) {
        _activeUserId = activeUserId;
    }

    public String getNetworkId() {
        return _networkId;
    }

    public void setNetworkId(String networkId) {
        _networkId = networkId;
    }

    public String getDomainId() {
        return _domainId;
    }

    public void setDomainId(String domainId) {
        _domainId = domainId;
    }

    public String getBrowserType() {
        return _browserType;
    }

    public String getRemoteAddress() {
        return _remoteAddress;
    }

    public void setBrowserType(String browserType) {
        _browserType = browserType;
    }

    public void setRemoteAddress(String remoteAddress) {
        _remoteAddress = remoteAddress;
    }

    public Date getLoginTime() {
        return _loginTime;
    }

    public void setLoginTime(Date loginTime) {
        _loginTime = loginTime;
    }

    public int getValidStatus() {
        return _validStatus;
    }

    public void setValidStatus(int validStatus) {
        _validStatus = validStatus;
    }

    public ArrayList getMenuItemList() {
        return _menuItemList;
    }

    public void setMenuItemList(ArrayList menuItemList) {
        _menuItemList = menuItemList;
    }

    public String getPasswordReset() {
        return _passwordReset;
    }

    public void setPasswordReset(String passwordReset) {
        _passwordReset = passwordReset;
    }

    public int getInvalidPasswordCount() {
        return _invalidPasswordCount;
    }

    public void setInvalidPasswordCount(int invalidPasswordCount) {
        _invalidPasswordCount = invalidPasswordCount;
    }

    public String getLoggerMessage() {
        return _loggerMessage;
    }

    public void setLoggerMessage(String loggerMessage) {
        _loggerMessage = loggerMessage;
    }

    public String getOtherInformation() {
        return _otherInformation;
    }

    public void setOtherInformation(String otherInformation) {
        _otherInformation = otherInformation;
    }

    public String getCurrentRoleCode() {
        return _currentRoleCode;
    }

    public void setCurrentRoleCode(String currentRoleCode) {
        _currentRoleCode = currentRoleCode;
    }

    public boolean isAccessAllowed(String p_accessCode) {
        if (p_accessCode == null)
            return false;
        for (int loop = 0; loop < _menuItemList.size(); loop++) {
            MenuItem menuItem = (MenuItem) _menuItemList.get(loop);
            if (p_accessCode.equals(menuItem.getPageCode())) {
                _currentRoleCode = menuItem.getRoleCode();
                // _currentModule=menuItem.getModuleCode();
                return true;
            }
        }
        return false;
    }

    public ArrayList getCP2PServiceList() {
        return _associatdServiceTypeList;
    }

    public void setCP2PServiceList(ArrayList serviceList) {
        _associatdServiceTypeList = serviceList;
    }

    /**
     * @return Returns the _smsPin.
     */
    public String getSmsPin() {
        return _smsPin;
    }

    /**
     * @param pin
     *            The _smsPin to set.
     */
    public void setSmsPin(String pin) {
        _smsPin = pin;
    }

    /**
     * @return Returns the _pinRequired.
     */
    public String getPinRequired() {
        return _pinRequired;
    }

    /**
     * @param required
     *            The _pinRequired to set.
     */
    public void setPinRequired(String required) {
        _pinRequired = required;
    }

    public ArrayList getCP2PServiceTypeList() {
        return _associatdServiceTypeList;
    }

    public String getLanguage() {
        return _language;
    }

    public Locale getLocale() {
        return _locale;
    }

    public String[] getMessageArguments() {
        return _messageArguments;
    }

    public String getMessageCode() {
        return _messageCode;
    }

    public void setCP2PServiceTypeList(ArrayList associatdServiceTypeList) {
        _associatdServiceTypeList = associatdServiceTypeList;
    }

    public void setLanguage(String language) {
        _language = language;
    }

    public void setLocale(Locale locale) {
        _locale = locale;
    }

    public void setMessageArguments(String[] messageArguments) {
        _messageArguments = messageArguments;
    }

    public void setMessageCode(String messageCode) {
        _messageCode = messageCode;
    }

    public HashMap getLocaleList() {
        _localeList = new HashMap();
        ArrayList arrList = new ArrayList();
        arrList = LocaleMasterCache.getLocaleListForWEB();
        LocaleMasterVO localeVO = null;
        Locale locale = null;
        for (int i = 0; i < arrList.size(); i++) {
            locale = (Locale) arrList.get(i);
            localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
            _localeList.put(arrList.get(i), localeVO.getName());
        }
        return _localeList;
    }

    public void setLocaleList(HashMap localeList) {
        _localeList = localeList;
    }

    public boolean isDuplicateLogin() {
        return _isDuplicateLogin;
    }

    public void setIsDuplicateLogin(boolean duplicateLogin) {
        _isDuplicateLogin = duplicateLogin;
    }

    public String getDuplicateHost() {
        return _duplicateHost;
    }

    public void setDuplicateHost(String host) {
        _duplicateHost = host;
    }

    public String getSubscriberPassword() {
        return _subscriberPassword;
    }

    public void setSubscriberPassword(String password) {
        _subscriberPassword = password;
    }

    public String getEmailId() {
        return _emailId;
    }

    public void setEmailId(String emailId) {
        _emailId = emailId;
    }

    public String getOldPassword() {
        return _oldPassword;
    }

    public void setOldPassword(String password) {
        _oldPassword = password;
    }

    public String getNewPassword() {
        return _newPassword;
    }

    public void setNewPassword(String password) {
        _newPassword = password;
    }

    public String getConfirmPassword() {
        return _confirmPassword;
    }

    public void setConfirmPassword(String password) {
        _confirmPassword = password;
    }

    public String getOldPin() {
        return _oldPin;
    }

    public void setOldPin(String pin) {
        _oldPin = pin;
    }

    public String getNewPin() {
        return _newPin;
    }

    public void setNewPin(String pin) {
        _newPin = pin;
    }

    public String getConfirmPin() {
        return _confirmPin;
    }

    public void setConfirmPin(String pin) {
        _confirmPin = pin;
    }
}
