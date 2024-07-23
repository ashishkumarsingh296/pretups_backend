package com.btsl.pretups.processes.businesslogic;

/**
 * @(#)DailyTransferDetailsVO.java
 * 
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Manisha Jain 19/02/2008 Initial Creation
 *                                 --------------------------------------------
 *                                 ----------------------------
 *                                 Copyright (c) 2008 Bharti Telesoft Ltd.
 *                                 All Rights Reserved
 */
import java.util.Date;
import java.util.Locale;

public class DailyTransferDetailsVO {

    private String _type;
    private String _transferCategory;
    private String _transferType;
    private String _transferSubType;
    private String _fromUserId;
    private String _toUserId;
    private String _userId;
    private String _productCode;
    private String _productName;
    private long _sumPayableAmount = 0;
    private long _sumNetPayableAmount = 0;
    private long _transferMrp = 0;
    private String _senderId;
    private String _userName;
    private String _serviceType;
    private String _name;
    private String _transferStatus;
    private long _transferValue;
    private long _balance = 0;
    private Date _balanceDate;
    private long _previousBalance = 0;
    private long _noOfTxn = 0;
    private String _msisdn;
    private String _country;
    private Locale _locale;

    /**
     * @return Returns the balance.
     */
    public long getBalance() {
        return _balance;
    }

    /**
     * @param balance
     *            The balance to set.
     */
    public void setBalance(long balance) {
        _balance = balance;
    }

    /**
     * @return Returns the balanceDate.
     */
    public Date getBalanceDate() {
        return _balanceDate;
    }

    /**
     * @param balanceDate
     *            The balanceDate to set.
     */
    public void setBalanceDate(Date balanceDate) {
        _balanceDate = balanceDate;
    }

    /**
     * @return Returns the fromUserId.
     */
    public String getFromUserId() {
        return _fromUserId;
    }

    /**
     * @param fromUserId
     *            The fromUserId to set.
     */
    public void setFromUserId(String fromUserId) {
        _fromUserId = fromUserId;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return _name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * @return Returns the previousBalance.
     */
    public long getPreviousBalance() {
        return _previousBalance;
    }

    /**
     * @param previousBalance
     *            The previousBalance to set.
     */
    public void setPreviousBalance(long previousBalance) {
        _previousBalance = previousBalance;
    }

    /**
     * @return Returns the productCode.
     */
    public String getProductCode() {
        return _productCode;
    }

    /**
     * @param productCode
     *            The productCode to set.
     */
    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    /**
     * @return Returns the productName.
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * @param productName
     *            The productName to set.
     */
    public void setProductName(String productName) {
        _productName = productName;
    }

    /**
     * @return Returns the senderId.
     */
    public String getSenderId() {
        return _senderId;
    }

    /**
     * @param senderId
     *            The senderId to set.
     */
    public void setSenderId(String senderId) {
        _senderId = senderId;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the sumNetPayableAmount.
     */
    public long getSumNetPayableAmount() {
        return _sumNetPayableAmount;
    }

    /**
     * @param sumNetPayableAmount
     *            The sumNetPayableAmount to set.
     */
    public void setSumNetPayableAmount(long sumNetPayableAmount) {
        _sumNetPayableAmount = sumNetPayableAmount;
    }

    /**
     * @return Returns the sumPayableAmount.
     */
    public long getSumPayableAmount() {
        return _sumPayableAmount;
    }

    /**
     * @param sumPayableAmount
     *            The sumPayableAmount to set.
     */
    public void setSumPayableAmount(long sumPayableAmount) {
        _sumPayableAmount = sumPayableAmount;
    }

    /**
     * @return Returns the toUserId.
     */
    public String getToUserId() {
        return _toUserId;
    }

    /**
     * @param toUserId
     *            The toUserId to set.
     */
    public void setToUserId(String toUserId) {
        _toUserId = toUserId;
    }

    /**
     * @return Returns the transferCategory.
     */
    public String getTransferCategory() {
        return _transferCategory;
    }

    /**
     * @param transferCategory
     *            The transferCategory to set.
     */
    public void setTransferCategory(String transferCategory) {
        _transferCategory = transferCategory;
    }

    /**
     * @return Returns the transferMrp.
     */
    public long getTransferMrp() {
        return _transferMrp;
    }

    /**
     * @param transferMrp
     *            The transferMrp to set.
     */
    public void setTransferMrp(long transferMrp) {
        _transferMrp = transferMrp;
    }

    /**
     * @return Returns the transferStatus.
     */
    public String getTransferStatus() {
        return _transferStatus;
    }

    /**
     * @param transferStatus
     *            The transferStatus to set.
     */
    public void setTransferStatus(String transferStatus) {
        _transferStatus = transferStatus;
    }

    /**
     * @return Returns the transferSubType.
     */
    public String getTransferSubType() {
        return _transferSubType;
    }

    /**
     * @param transferSubType
     *            The transferSubType to set.
     */
    public void setTransferSubType(String transferSubType) {
        _transferSubType = transferSubType;
    }

    /**
     * @return Returns the transferType.
     */
    public String getTransferType() {
        return _transferType;
    }

    /**
     * @param transferType
     *            The transferType to set.
     */
    public void setTransferType(String transferType) {
        _transferType = transferType;
    }

    /**
     * @return Returns the transferValue.
     */
    public long getTransferValue() {
        return _transferValue;
    }

    /**
     * @param transferValue
     *            The transferValue to set.
     */
    public void setTransferValue(long transferValue) {
        _transferValue = transferValue;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        _type = type;
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

    /**
     * @return Returns the noOfTxn.
     */
    public long getNoOfTxn() {
        return _noOfTxn;
    }

    /**
     * @param noOfTxn
     *            The noOfTxn to set.
     */
    public void setNoOfTxn(long noOfTxn) {
        _noOfTxn = noOfTxn;
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
     * @return Returns the country.
     */
    public String getCountry() {
        return _country;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCountry(String country) {
        _country = country;
    }

    /**
     * @return Returns the locale.
     */
    public Locale getLocale() {
        return _locale;
    }

    /**
     * @param locale
     *            The locale to set.
     */
    public void setLocale(Locale locale) {
        _locale = locale;
    }
}
