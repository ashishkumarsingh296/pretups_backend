package com.btsl.pretups.processes.businesslogic;

/*
 * UserTransactionVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Manisha Jain 23/02/09 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Bharti Telesoft Ltd.
 * This class is used for USER_TXN table
 */
import java.io.Serializable;
import java.util.Date;

public class UserTransactionVO implements Serializable {
    private String _profileType;
    private String _userIdOrMsisdn;
    private String _periodicity;
    private String _userCategory;
    private String _productCode;
    private long _count;
    private long _amount;
    private Date _txnDate;
    private String _serviceType;
    private String _subscriberType;

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("_profileType=" + _profileType + ",");
        str.append("_userIdOrMsisdn=" + _userIdOrMsisdn + ",");
        str.append("_periodicity=" + _periodicity + ",");
        str.append("_userCategory=" + _userCategory + ",");
        str.append("_serviceType=" + _serviceType + ",");
        str.append("_count=" + _count + ",");
        str.append("_amount=" + _amount + ",");
        str.append("_txnDate=" + _txnDate + ",");
        str.append("_subscriberType=" + _subscriberType + ",");
        return str.toString();
    }

    /**
     * @return Returns the amount.
     */
    public long getAmount() {
        return _amount;
    }

    /**
     * @param amount
     *            The amount to set.
     */
    public void setAmount(long amount) {
        _amount = amount;
    }

    /**
     * @return Returns the count.
     */
    public long getCount() {
        return _count;
    }

    /**
     * @param count
     *            The count to set.
     */
    public void setCount(long count) {
        _count = count;
    }

    /**
     * @return Returns the periodicity.
     */
    public String getPeriodicity() {
        return _periodicity;
    }

    /**
     * @param periodicity
     *            The periodicity to set.
     */
    public void setPeriodicity(String periodicity) {
        _periodicity = periodicity;
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
     * @return Returns the profileType.
     */
    public String getProfileType() {
        return _profileType;
    }

    /**
     * @param profileType
     *            The profileType to set.
     */
    public void setProfileType(String profileType) {
        _profileType = profileType;
    }

    /**
     * @return Returns the txnDat.
     */
    public Date getTxnDate() {
        return _txnDate;
    }

    /**
     * @param txnDat
     *            The txnDat to set.
     */
    public void setTxnDate(Date txnDate) {
        _txnDate = txnDate;
    }

    /**
     * @return Returns the userCategory.
     */
    public String getUserCategory() {
        return _userCategory;
    }

    /**
     * @param userCategory
     *            The userCategory to set.
     */
    public void setUserCategory(String userCategory) {
        _userCategory = userCategory;
    }

    /**
     * @return Returns the userIdOrMsisdn.
     */
    public String getUserIdOrMsisdn() {
        return _userIdOrMsisdn;
    }

    /**
     * @param userIdOrMsisdn
     *            The userIdOrMsisdn to set.
     */
    public void setUserIdOrMsisdn(String userIdOrMsisdn) {
        _userIdOrMsisdn = userIdOrMsisdn;
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
}