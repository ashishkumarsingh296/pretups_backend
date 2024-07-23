package com.selftopup.pretups.subscriber.businesslogic;

/*
 * SenderVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 14/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.selftopup.util.BTSLUtil;

public class SenderVO extends SubscriberVO implements Serializable {

    private String _userID;
    private String _userName;
    private String _status;
    private String _pin;
    private long _consecutiveFailures;
    private int _pinBlockCount;
    private int _buddySeqNumber;
    private String _billingType;
    private Date _billingCycleDate;
    private long _creditLimit;
    private Date _activatedOn;
    private Date _registeredOn;
    private String _oldPin;

    private String _category; // User Category, Not there in P2P
    private ArrayList _voList = null;// to store VOs
    private long _totalConsecutiveFailCount = 0; // Total Consecutive Failed
                                                 // Count
    private String _billingDateStr;// for the validation at the jsp
    private String _creditLimitStr;// for the validation at jsp
    private String _registeredOnAsString;
    private String _activatedOnAsString;

    private String _multiBox;// this is a flag variable used in the jsp to
                             // select a record of the SenderVO which is drawn
                             // form the ArrayList of VOs.

    private String _skeyRequired;

    private long _dailyTransferCount;
    private long _dailyTransferAmount;
    private long _weeklyTransferCount;
    private long _weeklyTransferAmount;

    private long _prevDailyTransferCount;
    private long _prevDailyTransferAmount;
    private long _prevMonthlyTransferCount;
    private long _prevMonthlyTransferAmount;
    private long _prevWeeklyTransferCount;
    private long _prevWeeklyTransferAmount;

    private Date _prevTransferDate;
    private Date _prevTransferWeekDate;
    private Date _prevTransferMonthDate;

    private String _remarks;
    private Date _pinModifiedOn;
    private Date _firstInvalidPinTime;
    /**
     * Field _lastModifiedTime. This field is used to check that the record is
     * modified during the transaction.
     */
    private long _lastModifiedTime;

    // fields used in the jsp.
    private String _dailyTransferCountStr;
    private String _dailyTransferAmountStr;
    private String _weeklyTransferCountStr;
    private String _weeklyTransferAmountStr;

    // Added by Amit
    private String _language;
    private String _country;
    private String _serviceClassID;

    private boolean _forcePinCheckReqd = true;
    private boolean _barUserForInvalidPin = false;
    private Locale _locale = null;
    private boolean _isDefUserRegistration = false;
    private boolean _isPinUpdateReqd = false;
    private boolean _isActivateStatusReqd = false;
    private String _registered;
    private boolean _minTransferAmountCheckDone = false;
    private boolean _maxTransferAmountCheckDone = false;
    private boolean _dailyTotalTransAmtCheckDone = false;
    private boolean _dailyTotalTransCountCheckDone = false;
    private boolean _weeklyTotalTransAmtCheckDone = false;
    private boolean _weeklyTotalTransCountCheckDone = false;
    private boolean _monthlyTotalTransAmtCheckDone = false;
    private boolean _monthlyTotalTransCountCheckDone = false;

    private String _accountStatus;
    private String _lastTransferStatusDesc;

    // added for adding remaining threshold amount and count in sender essage
    // (Manisha 29/01/08)
    private long _monthlyMaxTransCountThreshold;
    private long _monthlyMaxTransAmtThreshold;
    private long _dailyMaxTransCountThreshold;
    private long _dailyMaxTransAmtThreshold;
    private long _weeklyMaxTransCountThreshold;
    private long _weeklyMaxTransAmtThreshold;
    private Date _creationDateString;
    // /Added by sonali for self Topup
    private String _login;
    private String _password;
    private String _imei;
    private String _emailId;
    // Sonali Garg changes end

    private String _encryptionKey;

    /**
     * @return Returns the dailyMaxTransAmtThreshold.
     */
    public long getDailyMaxTransAmtThreshold() {
        return _dailyMaxTransAmtThreshold;
    }

    /**
     * @param dailyMaxTransAmtThreshold
     *            The dailyMaxTransAmtThreshold to set.
     */
    public void setDailyMaxTransAmtThreshold(long dailyMaxTransAmtThreshold) {
        _dailyMaxTransAmtThreshold = dailyMaxTransAmtThreshold;
    }

    /**
     * @return Returns the dailyMaxTransCountThreshold.
     */
    public long getDailyMaxTransCountThreshold() {
        return _dailyMaxTransCountThreshold;
    }

    /**
     * @param dailyMaxTransCountThreshold
     *            The dailyMaxTransCountThreshold to set.
     */
    public void setDailyMaxTransCountThreshold(long dailyMaxTransCountThreshold) {
        _dailyMaxTransCountThreshold = dailyMaxTransCountThreshold;
    }

    /**
     * @return Returns the monthlyMaxTransAmtThreshold.
     */
    public long getMonthlyMaxTransAmtThreshold() {
        return _monthlyMaxTransAmtThreshold;
    }

    /**
     * @param monthlyMaxTransAmtThreshold
     *            The monthlyMaxTransAmtThreshold to set.
     */
    public void setMonthlyMaxTransAmtThreshold(long monthlyMaxTransAmtThreshold) {
        _monthlyMaxTransAmtThreshold = monthlyMaxTransAmtThreshold;
    }

    /**
     * @return Returns the monthlyMaxTransCountThreshold.
     */
    public long getMonthlyMaxTransCountThreshold() {
        return _monthlyMaxTransCountThreshold;
    }

    /**
     * @param monthlyMaxTransCountThreshold
     *            The monthlyMaxTransCountThreshold to set.
     */
    public void setMonthlyMaxTransCountThreshold(long monthlyMaxTransCountThreshold) {
        _monthlyMaxTransCountThreshold = monthlyMaxTransCountThreshold;
    }

    /**
     * @return Returns the weeklyMaxTransAmtThreshold.
     */
    public long getWeeklyMaxTransAmtThreshold() {
        return _weeklyMaxTransAmtThreshold;
    }

    /**
     * @param weeklyMaxTransAmtThreshold
     *            The weeklyMaxTransAmtThreshold to set.
     */
    public void setWeeklyMaxTransAmtThreshold(long weeklyMaxTransAmtThreshold) {
        _weeklyMaxTransAmtThreshold = weeklyMaxTransAmtThreshold;
    }

    /**
     * @return Returns the weeklyMaxTransCountThreshold.
     */
    public long getWeeklyMaxTransCountThreshold() {
        return _weeklyMaxTransCountThreshold;
    }

    /**
     * @param weeklyMaxTransCountThreshold
     *            The weeklyMaxTransCountThreshold to set.
     */
    public void setWeeklyMaxTransCountThreshold(long weeklyMaxTransCountThreshold) {
        _weeklyMaxTransCountThreshold = weeklyMaxTransCountThreshold;
    }

    /**
     * To get the value of country field
     * 
     * @return country.
     */
    public String getCountry() {
        return _country;
    }

    /**
     * To set the value of country field
     */
    public void setCountry(String country) {
        _country = country;
    }

    /**
     * To get the value of language field
     * 
     * @return language.
     */
    public String getLanguage() {
        return _language;
    }

    /**
     * To set the value of language field
     */
    public void setLanguage(String language) {
        _language = language;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedOn) {
        _lastModifiedTime = lastModifiedOn;
    }

    /**
     * @return Returns the activatedOn.
     */
    public Date getActivatedOn() {
        return _activatedOn;
    }

    /**
     * @param activatedOn
     *            The activatedOn to set.
     */
    public void setActivatedOn(Date activatedOn) {
        _activatedOn = activatedOn;
    }

    /**
     * @return Returns the billingCycleDate.
     */
    public Date getBillingCycleDate() {
        return _billingCycleDate;
    }

    /**
     * @param billingCycleDate
     *            The billingCycleDate to set.
     */
    public void setBillingCycleDate(Date billingCycleDate) {
        _billingCycleDate = billingCycleDate;
    }

    /**
     * @return Returns the billingType.
     */
    public String getBillingType() {
        return _billingType;
    }

    /**
     * @param billingType
     *            The billingType to set.
     */
    public void setBillingType(String billingType) {
        _billingType = billingType;
    }

    /**
     * @return Returns the buddySeqNumber.
     */
    public int getBuddySeqNumber() {
        return _buddySeqNumber;
    }

    /**
     * @param buddySeqNumber
     *            The buddySeqNumber to set.
     */
    public void setBuddySeqNumber(int buddySeqNumber) {
        _buddySeqNumber = buddySeqNumber;
    }

    /**
     * @return Returns the creditLimit.
     */
    public long getCreditLimit() {
        return _creditLimit;
    }

    /**
     * @param creditLimit
     *            The creditLimit to set.
     */
    public void setCreditLimit(long creditLimit) {
        _creditLimit = creditLimit;
    }

    /**
     * @return Returns the pin.
     */
    public String getPin() {
        return _pin;
    }

    /**
     * @param pin
     *            The pin to set.
     */
    public void setPin(String pin) {
        _pin = pin;
    }

    /**
     * @return Returns the registeredOn.
     */
    public Date getRegisteredOn() {
        return _registeredOn;
    }

    /**
     * @param registeredOn
     *            The registeredOn to set.
     */
    public void setRegisteredOn(Date registeredOn) {
        _registeredOn = registeredOn;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
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
        _userID = userID;
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
        _userName = userName;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(",_dailyTransferAmount=" + _dailyTransferAmount);
        sbf.append(",_dailyTransferCount=" + _dailyTransferCount);
        sbf.append(",_weeklyTransferAmount=" + _weeklyTransferAmount);
        sbf.append(",_weeklyTransferCount=" + _weeklyTransferCount);
        sbf.append(",MonthlyTransferAmount=" + this.getMonthlyTransferAmount());
        sbf.append(",MonthlyTransferCount=" + this.getMonthlyTransferCount());
        sbf.append(",IMEI=" + this.getImei());
        return ("_userID=" + _userID + "  _userName:" + _userName + "	_status:" + _status + "_billingType" + _billingType + "_billingCycleDate" + _billingCycleDate + "_creditLimit" + _creditLimit + super.toString() + sbf);
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String category) {
        _category = category;
    }

    public String getOldPin() {
        return _oldPin;
    }

    public void setOldPin(String oldPin) {
        _oldPin = oldPin;
    }

    public ArrayList getVoList() {
        return _voList;
    }

    public void setVoList(ArrayList voList) {
        _voList = voList;
    }

    public long getTotalConsecutiveFailCount() {
        return _totalConsecutiveFailCount;
    }

    public void setTotalConsecutiveFailCount(long totalConsecutiveFailCount) {
        _totalConsecutiveFailCount = totalConsecutiveFailCount;
    }

    public String getBillingDateStr() {
        return _billingDateStr;
    }

    public void setBillingDateStr(String billingDateStr) {
        _billingDateStr = billingDateStr;
    }

    public String getMultiBox() {
        return _multiBox;
    }

    public void setMultiBox(String multiBox) {
        _multiBox = multiBox;
    }

    public String getCreditLimitStr() {
        return _creditLimitStr;
    }

    public void setCreditLimitStr(String creditLimitStr) {
        _creditLimitStr = creditLimitStr;
    }

    /**
     * @return Returns the skeyRequired.
     */
    public String getSkeyRequired() {
        return _skeyRequired;
    }

    /**
     * @param skeyRequired
     *            The skeyRequired to set.
     */
    public void setSkeyRequired(String skeyRequired) {
        _skeyRequired = skeyRequired;
    }

    public String getRegisteredOnAsString() {
        return _registeredOnAsString;
    }

    public void setRegisteredOnAsString(String registeredOnAsString) {
        _registeredOnAsString = registeredOnAsString;
    }

    public String getActivatedOnAsString() {
        return _activatedOnAsString;
    }

    public void setActivatedOnAsString(String activatedOnAsString) {
        _activatedOnAsString = activatedOnAsString;
    }

    public long getConsecutiveFailures() {
        return _consecutiveFailures;
    }

    public void setConsecutiveFailures(long consecutiveFailures) {
        _consecutiveFailures = consecutiveFailures;
    }

    public long getDailyTransferAmount() {
        return _dailyTransferAmount;
    }

    public void setDailyTransferAmount(long dailyTransferAmount) {
        _dailyTransferAmount = dailyTransferAmount;
    }

    public long getDailyTransferCount() {
        return _dailyTransferCount;
    }

    public void setDailyTransferCount(long dailyTransferCount) {
        _dailyTransferCount = dailyTransferCount;
        _dailyTransferCountStr = String.valueOf(_dailyTransferCount);
    }

    public long getPrevDailyTransferAmount() {
        return _prevDailyTransferAmount;
    }

    public void setPrevDailyTransferAmount(long prevDailyTransferAmount) {
        _prevDailyTransferAmount = prevDailyTransferAmount;
    }

    public long getPrevDailyTransferCount() {
        return _prevDailyTransferCount;
    }

    public void setPrevDailyTransferCount(long prevDailyTransferCount) {
        _prevDailyTransferCount = prevDailyTransferCount;
    }

    public long getPrevMonthlyTransferAmount() {
        return _prevMonthlyTransferAmount;
    }

    public void setPrevMonthlyTransferAmount(long prevMonthlyTransferAmount) {
        _prevMonthlyTransferAmount = prevMonthlyTransferAmount;
    }

    public long getPrevMonthlyTransferCount() {
        return _prevMonthlyTransferCount;
    }

    public void setPrevMonthlyTransferCount(long prevMonthlyTransferCount) {
        _prevMonthlyTransferCount = prevMonthlyTransferCount;
    }

    public Date getPrevTransferDate() {
        return _prevTransferDate;
    }

    public void setPrevTransferDate(Date prevTransferDate) {
        _prevTransferDate = prevTransferDate;
    }

    public Date getPrevTransferMonthDate() {
        return _prevTransferMonthDate;
    }

    public void setPrevTransferMonthDate(Date prevTransferMonthDate) {
        _prevTransferMonthDate = prevTransferMonthDate;
    }

    public Date getPrevTransferWeekDate() {
        return _prevTransferWeekDate;
    }

    public void setPrevTransferWeekDate(Date prevTransferWeekDate) {
        _prevTransferWeekDate = prevTransferWeekDate;
    }

    public long getPrevWeeklyTransferAmount() {
        return _prevWeeklyTransferAmount;
    }

    public void setPrevWeeklyTransferAmount(long prevWeeklyTransferAmount) {
        _prevWeeklyTransferAmount = prevWeeklyTransferAmount;
    }

    public long getPrevWeeklyTransferCount() {
        return _prevWeeklyTransferCount;
    }

    public void setPrevWeeklyTransferCount(long prevWeeklyTransferCount) {
        _prevWeeklyTransferCount = prevWeeklyTransferCount;
    }

    public long getWeeklyTransferAmount() {
        return _weeklyTransferAmount;
    }

    public void setWeeklyTransferAmount(long weeklyTransferAmount) {
        _weeklyTransferAmount = weeklyTransferAmount;
    }

    public long getWeeklyTransferCount() {
        return _weeklyTransferCount;
    }

    public void setWeeklyTransferCount(long weeklyTransferCount) {
        _weeklyTransferCount = weeklyTransferCount;
        _weeklyTransferCountStr = String.valueOf(_weeklyTransferCount);
    }

    public int getPinBlockCount() {
        return _pinBlockCount;
    }

    public void setPinBlockCount(int pinBlockCount) {
        _pinBlockCount = pinBlockCount;
    }

    public String getBillingCycleDateStr() {
        if (_billingCycleDate != null)
            try {
                return BTSLUtil.getDateStringFromDate(_billingCycleDate);
            } catch (ParseException e) {
                return "";
            }
        else
            return "";
    }

    public String getRemarks() {
        return _remarks;
    }

    public void setRemarks(String remarks) {
        _remarks = remarks;
    }

    public String getDailyTransferAmountStr() {
        return _dailyTransferAmountStr;
    }

    public void setDailyTransferAmountStr(String dailyTransferAmountStr) {
        _dailyTransferAmountStr = dailyTransferAmountStr;
    }

    public String getDailyTransferCountStr() {
        return _dailyTransferCountStr;
    }

    public void setDailyTransferCountStr(String dailyTransferCountStr) {
        _dailyTransferCountStr = dailyTransferCountStr;
    }

    public String getWeeklyTransferAmountStr() {
        return _weeklyTransferAmountStr;
    }

    public void setWeeklyTransferAmountStr(String weeklyTransferAmountStr) {
        _weeklyTransferAmountStr = weeklyTransferAmountStr;
    }

    public String getWeeklyTransferCountStr() {
        return _weeklyTransferCountStr;
    }

    public void setWeeklyTransferCountStr(String weeklyTransferCountStr) {
        _weeklyTransferCountStr = weeklyTransferCountStr;
    }

    public Date getFirstInvalidPinTime() {
        return _firstInvalidPinTime;
    }

    public void setFirstInvalidPinTime(Date firstInvalidPinTime) {
        _firstInvalidPinTime = firstInvalidPinTime;
    }

    public Date getPinModifiedOn() {
        return _pinModifiedOn;
    }

    public void setPinModifiedOn(Date pinModifiedOn) {
        _pinModifiedOn = pinModifiedOn;
    }

    public String getServiceClassID() {
        return _serviceClassID;
    }

    public void setServiceClassID(String serviceClassID) {
        _serviceClassID = serviceClassID;
    }

    public boolean isForcePinCheckReqd() {
        return _forcePinCheckReqd;
    }

    public void setForcePinCheckReqd(boolean forcePinCheckReqd) {
        _forcePinCheckReqd = forcePinCheckReqd;
    }

    public boolean isBarUserForInvalidPin() {
        return _barUserForInvalidPin;
    }

    public void setBarUserForInvalidPin(boolean barUserForInvalidPin) {
        _barUserForInvalidPin = barUserForInvalidPin;
    }

    public Locale getLocale() {
        return _locale;
    }

    public void setLocale(Locale locale) {
        _locale = locale;
    }

    public boolean isDefUserRegistration() {
        return _isDefUserRegistration;
    }

    public void setDefUserRegistration(boolean isDefUserRegistration) {
        _isDefUserRegistration = isDefUserRegistration;
    }

    public boolean isPinUpdateReqd() {
        return _isPinUpdateReqd;
    }

    public void setPinUpdateReqd(boolean isPinUpdateReqd) {
        _isPinUpdateReqd = isPinUpdateReqd;
    }

    public boolean isActivateStatusReqd() {
        return _isActivateStatusReqd;
    }

    public void setActivateStatusReqd(boolean isActivateStatusReqd) {
        _isActivateStatusReqd = isActivateStatusReqd;
    }

    public String getAccountStatus() {
        return _accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        _accountStatus = accountStatus;
    }

    public String getRegistered() {
        return _registered;
    }

    public void setRegistered(String registered) {
        _registered = registered;
    }

    public boolean isDailyTotalTransAmtCheckDone() {
        return _dailyTotalTransAmtCheckDone;
    }

    public void setDailyTotalTransAmtCheckDone(boolean dailyTotalTransAmtCheckDone) {
        _dailyTotalTransAmtCheckDone = dailyTotalTransAmtCheckDone;
    }

    public boolean isDailyTotalTransCountCheckDone() {
        return _dailyTotalTransCountCheckDone;
    }

    public void setDailyTotalTransCountCheckDone(boolean dailyTotalTransCountCheckDone) {
        _dailyTotalTransCountCheckDone = dailyTotalTransCountCheckDone;
    }

    public boolean isMaxTransferAmountCheckDone() {
        return _maxTransferAmountCheckDone;
    }

    public void setMaxTransferAmountCheckDone(boolean maxTransferAmountCheckDone) {
        _maxTransferAmountCheckDone = maxTransferAmountCheckDone;
    }

    public boolean isMinTransferAmountCheckDone() {
        return _minTransferAmountCheckDone;
    }

    public void setMinTransferAmountCheckDone(boolean minTransferAmountCheckDone) {
        _minTransferAmountCheckDone = minTransferAmountCheckDone;
    }

    public boolean isMonthlyTotalTransCountCheckDone() {
        return _monthlyTotalTransCountCheckDone;
    }

    public void setMonthlyTotalTransCountCheckDone(boolean monthlyTotalTransCountCheckDone) {
        _monthlyTotalTransCountCheckDone = monthlyTotalTransCountCheckDone;
    }

    public boolean isMonthlyTotalTransAmtCheckDone() {
        return _monthlyTotalTransAmtCheckDone;
    }

    public void setMonthlyTotalTransAmtCheckDone(boolean monthlyTotalTransAmtCheckDone) {
        _monthlyTotalTransAmtCheckDone = monthlyTotalTransAmtCheckDone;
    }

    public boolean isWeeklyTotalTransAmtCheckDone() {
        return _weeklyTotalTransAmtCheckDone;
    }

    public void setWeeklyTotalTransAmtCheckDone(boolean weeklyTotalTransAmtCheckDone) {
        _weeklyTotalTransAmtCheckDone = weeklyTotalTransAmtCheckDone;
    }

    public boolean isWeeklyTotalTransCountCheckDone() {
        return _weeklyTotalTransCountCheckDone;
    }

    public void setWeeklyTotalTransCountCheckDone(boolean weeklyTotalTransCountCheckDone) {
        _weeklyTotalTransCountCheckDone = weeklyTotalTransCountCheckDone;
    }

    /**
     * @return Returns the lastTransferStatusDesc.
     */
    public String getLastTransferStatusDesc() {
        return _lastTransferStatusDesc;
    }

    /**
     * @param lastTransferStatusDesc
     *            The lastTransferStatusDesc to set.
     */
    public void setLastTransferStatusDesc(String lastTransferStatusDesc) {
        _lastTransferStatusDesc = lastTransferStatusDesc;
    }

    public Date getCreationDateString() {
        return _creationDateString;
    }

    public void setCreationDateString(Date creationDateString) {
        _creationDateString = creationDateString;
    }

    public String getLogin() {
        return _login;
    }

    public void setLogin(String id) {
        _login = id;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String _password) {
        this._password = _password;
    }

    public String getImei() {
        return _imei;
    }

    public void setImei(String _imei) {
        this._imei = _imei;
    }

    public String getEmailId() {
        return _emailId;
    }

    public void setEmailId(String id) {
        _emailId = id;
    }

    public String getEncryptionKey() {
        return _encryptionKey;
    }

    public void setEncryptionKey(String key) {
        _encryptionKey = key;
    }

}
