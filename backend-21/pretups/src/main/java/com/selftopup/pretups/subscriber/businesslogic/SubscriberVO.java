package com.selftopup.pretups.subscriber.businesslogic;

/*
 * SubscriberVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 14/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsVO;
import com.selftopup.util.BTSLUtil;

public class SubscriberVO implements Serializable {

    private String _msisdn;
    private String _msisdnPrefix;
    private String _subscriberType;
    private String _subscriberTypeDescription;
    private String _serviceClassCode;
    private long _prefixID;
    private String _networkCode;
    private int _lastTxnCount; // NOT NEEDED ????
    private String _lastTransferID;
    private long _lastTransferAmount;
    private Date _lastTransferOn;
    private String _lastTransferType;
    private String _lastTransferStatus;
    private String _lastTransferMSISDN; // to whom last transfer occur
    private String _requestStatus;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;

    private long _monthlyTransferCount;
    private long _monthlyTransferAmount;
    private long _totalTransfers;
    private long _totalTransferAmount;

    private String _module;
    private String _status;
    private String _category; // User Category, Not there in P2P
    private String _createdOnAsString; // to display the date on the jsp page
    private String _lastTxnOnAsString;// to display the date on the jsp page

    private Date _lastSuccessTransferDate;
    // fields used in the jsp.
    private String _monthlyTransferCountStr;
    private String _monthlyTransferAmountStr;
    private String _transactionStatus;
    private String _interfaceResponseCode;
    private boolean _usingAllServiceClass;
    private long _currentBalance;
    private double _minResidualBalanceAllowed;
    private double _minTxnAmountAllowed;
    private double _maxTxnAmountAllowed;
    private double _maxPerTransferAllowed;
    private boolean _postOfflineInterface = false;
    private String _cvv = null;
    private String _nickName = null;
    private CardDetailsVO _cardDetailsVO = null;
    private String _sid = null;
    private String _userID = null;
    private double _scheduleAmount = 0;
    private String _scheduleType = null;
    private Date _scheduleDate = null;
    private String _pin = null;
    private int _failRetryCount = 0;
    private String _imei = null;

    public String getSid() {
        return _sid;
    }

    public void setSid(String sid) {
        _sid = sid;
    }

    public String getLastTxnOnAsString() {
        return _lastTxnOnAsString;
    }

    public void setLastTxnOnAsString(String lastTxnOnAsString) {
        _lastTxnOnAsString = lastTxnOnAsString;
    }

    public String getCreatedOnAsString() {
        return _createdOnAsString;
    }

    public void setCreatedOnAsString(String createdOnAsString) {
        _createdOnAsString = createdOnAsString;
    }

    /**
     * @return Returns the activatedOn.
     */
    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the lastTxnCount.
     */
    public int getLastTxnCount() {
        return _lastTxnCount;
    }

    /**
     * @param lastTxnCount
     *            The lastTxnCount to set.
     */
    public void setLastTxnCount(int lastTxnCount) {
        _lastTxnCount = lastTxnCount;
    }

    /**
     * @return Returns the modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param modifiedBy
     *            The modifiedBy to set.
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * @return Returns the modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOn
     *            The modifiedOn to set.
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the subscriberType.
     */
    public String getSubscriberType() {
        return _subscriberType;
    }

    /**
     * @param subscriberType
     *            The subscriberType to set.
     */
    public void setSubscriberType(String subscriberType) {
        _subscriberType = subscriberType;
    }

   
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
    	StringBuilder sbf = new StringBuilder();
        sbf.append(" _msisdn :").append(_msisdn);
        sbf.append(" _subscriberType:").append(_subscriberType);
        sbf.append(" _networkCode:").append(_networkCode);
        sbf.append(",_subscriberTypeDescription:").append(_subscriberTypeDescription);
       
    	
    	return sbf.toString();
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String category) {
        _category = category;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getMsisdnPrefix() {
        return _msisdnPrefix;
    }

    public void setMsisdnPrefix(String msisdnPrefix) {
        _msisdnPrefix = msisdnPrefix;
    }

    public String getModule() {
        return _module;
    }

    public void setModule(String module) {
        _module = module;
    }

    public long getPrefixID() {
        return _prefixID;
    }

    public void setPrefixID(long prefixID) {
        _prefixID = prefixID;
    }

    public String getSubscriberTypeDescription() {
        return _subscriberTypeDescription;
    }

    public void setSubscriberTypeDescription(String subscriberTypeDescription) {
        _subscriberTypeDescription = subscriberTypeDescription;
    }

    public Date getLastSuccessTransferDate() {
        return _lastSuccessTransferDate;
    }

    public void setLastSuccessTransferDate(Date lastSuccessTransferDate) {
        _lastSuccessTransferDate = lastSuccessTransferDate;
    }

    public long getLastTransferAmount() {
        return _lastTransferAmount;
    }

    public void setLastTransferAmount(long lastTransferAmount) {
        _lastTransferAmount = lastTransferAmount;
    }

    public String getLastTransferMSISDN() {
        return _lastTransferMSISDN;
    }

    public void setLastTransferMSISDN(String lastTransferMSISDN) {
        _lastTransferMSISDN = lastTransferMSISDN;
    }

    public String getLastTransferID() {
        return _lastTransferID;
    }

    public void setLastTransferID(String lastTransferID) {
        _lastTransferID = lastTransferID;
    }

    public Date getLastTransferOn() {
        return _lastTransferOn;
    }

    public void setLastTransferOn(Date lastTransferOn) {
        _lastTransferOn = lastTransferOn;
    }

    public String getLastTransferStatus() {
        return _lastTransferStatus;
    }

    public void setLastTransferStatus(String lastTransferStatus) {
        _lastTransferStatus = lastTransferStatus;
    }

    public String getLastTransferType() {
        return _lastTransferType;
    }

    public void setLastTransferType(String lastTransferType) {
        _lastTransferType = lastTransferType;
    }

    public String getRequestStatus() {
        return _requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        _requestStatus = requestStatus;
    }

    public String getServiceClassCode() {
        return _serviceClassCode;
    }

    public void setServiceClassCode(String serviceClassCode) {
        _serviceClassCode = serviceClassCode;
    }

    public long getMonthlyTransferAmount() {
        return _monthlyTransferAmount;
    }

    public void setMonthlyTransferAmount(long monthlyTransferAmount) {
        _monthlyTransferAmount = monthlyTransferAmount;
    }

    public long getMonthlyTransferCount() {
        return _monthlyTransferCount;
    }

    public void setMonthlyTransferCount(long monthlyTransferCount) {
        _monthlyTransferCount = monthlyTransferCount;
        _monthlyTransferCountStr = String.valueOf(_monthlyTransferCount);
    }

    public long getTotalTransferAmount() {
        return _totalTransferAmount;
    }

    public void setTotalTransferAmount(long totalTransferAmount) {
        _totalTransferAmount = totalTransferAmount;
    }

    public long getTotalTransfers() {
        return _totalTransfers;
    }

    public void setTotalTransfers(long totalTransfers) {
        _totalTransfers = totalTransfers;
    }

    // added by sandeep goel to get/set data on the jsp
    public String getLastSuccessTransferDateStr() {
        if (_lastSuccessTransferDate != null)
            try {
                return BTSLUtil.getDateStringFromDate(_lastSuccessTransferDate);
            } catch (ParseException e) {
                return "";
            }
        else
            return "";
    }

    public String getMonthlyTransferAmountStr() {
        return _monthlyTransferAmountStr;
    }

    public void setMonthlyTransferAmountStr(String monthlyTransferAmountStr) {
        _monthlyTransferAmountStr = monthlyTransferAmountStr;
    }

    public String getMonthlyTransferCountStr() {
        return _monthlyTransferCountStr;
    }

    public void setMonthlyTransferCountStr(String monthlyTransferCountStr) {
        _monthlyTransferCountStr = monthlyTransferCountStr;
    }

    public String getTransactionStatus() {
        return _transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        _transactionStatus = transactionStatus;
    }

    /**
     * @return Returns the interfaceResponseCode.
     */
    public String getInterfaceResponseCode() {
        return _interfaceResponseCode;
    }

    /**
     * @param interfaceResponseCode
     *            The interfaceResponseCode to set.
     */
    public void setInterfaceResponseCode(String interfaceResponseCode) {
        _interfaceResponseCode = interfaceResponseCode;
    }

    public boolean isUsingAllServiceClass() {
        return _usingAllServiceClass;
    }

    public void setUsingAllServiceClass(boolean usingAllServiceClass) {
        _usingAllServiceClass = usingAllServiceClass;
    }

    public long getCurrentBalance() {
        return _currentBalance;
    }

    public void setCurrentBalance(long currentBalance) {
        _currentBalance = currentBalance;
    }

    public double getMaxTxnAmountAllowed() {
        return _maxTxnAmountAllowed;
    }

    public void setMaxTxnAmountAllowed(double maxTxnAmountAllowed) {
        _maxTxnAmountAllowed = maxTxnAmountAllowed;
    }

    public double getMinResidualBalanceAllowed() {
        return _minResidualBalanceAllowed;
    }

    public void setMinResidualBalanceAllowed(double minResidualBalanceAllowed) {
        _minResidualBalanceAllowed = minResidualBalanceAllowed;
    }

    public double getMinTxnAmountAllowed() {
        return _minTxnAmountAllowed;
    }

    public void setMinTxnAmountAllowed(double minTxnAmountAllowed) {
        _minTxnAmountAllowed = minTxnAmountAllowed;
    }

    public double getMaxPerTransferAllowed() {
        return _maxPerTransferAllowed;
    }

    public void setMaxPerTransferAllowed(double maxPerTransferAllowed) {
        _maxPerTransferAllowed = maxPerTransferAllowed;
    }

    public boolean isPostOfflineInterface() {
        return _postOfflineInterface;
    }

    public void setPostOfflineInterface(boolean postOfflineInterface) {
        _postOfflineInterface = postOfflineInterface;
    }

    /**
     * @return the cvv
     */
    public String getCvv() {
        return _cvv;
    }

    /**
     * @param cvv
     *            the cvv to set
     */
    public void setCvv(String cvv) {
        _cvv = cvv;
    }

    /**
     * @return the nickName
     */
    public String getNickName() {
        return _nickName;
    }

    /**
     * @param nickName
     *            the nickName to set
     */
    public void setNickName(String nickName) {
        _nickName = nickName;
    }

    /**
     * @return the cardDetailsVO
     */
    public CardDetailsVO getCardDetailsVO() {
        return _cardDetailsVO;
    }

    /**
     * @param cardDetailsVO
     *            the cardDetailsVO to set
     */
    public void setCardDetailsVO(CardDetailsVO cardDetailsVO) {
        _cardDetailsVO = cardDetailsVO;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String _userid) {
        _userID = _userid;
    }

    public double getScheduleAmount() {
        return _scheduleAmount;
    }

    public void setScheduleAmount(Double amount) {
        _scheduleAmount = amount;
    }

    public String getScheduleType() {
        return _scheduleType;
    }

    public void setScheduleType(String type) {
        _scheduleType = type;
    }

    public Date getScheduleDate() {
        return _scheduleDate;
    }

    public void setScheduleDate(Date date) {
        _scheduleDate = date;
    }

    public String getPin() {
        return _pin;
    }

    public void setPin(String _pin) {
        this._pin = _pin;
    }

    public int getFailRetryCount() {
        return _failRetryCount;
    }

    public void setFailRetryCount(int retryCount) {
        _failRetryCount = retryCount;
    }

    public String getImei() {
        return _imei;
    }

    public void setImei(String imei) {
        _imei = imei;
    }
}
