package com.btsl.pretups.processes.businesslogic;

/**
 * @(#)HourlyCountTransactionDetailsVO.java
 * 
 *                                          ------------------------------------
 *                                          ------------------------------------
 *                                          -------------------------
 *                                          Author Date History
 *                                          ------------------------------------
 *                                          ------------------------------------
 *                                          -------------------------
 *                                          Shishupal Singh 24/04/2008 Initial
 *                                          Creation
 *                                          ------------------------------------
 *                                          ------------------------------------
 *                                          Copyright (c) 2008 Bharti Telesoft
 *                                          Ltd.
 *                                          All Rights Reserved
 */

public class HourlyCountTransactionDetailsVO {

    private String _serviceType;
    private String _networkCode;
    private String _module;
    private String _msisdn;
    private long _totalAmount;
    private long _successCount;
    private long _failureCount;
    private long _totalCount;

    private long _totalDayAmount;
    private long _successDayCount;
    private long _failureDayCount;
    private long _totalDayCount;
    private String _message;

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

    public long getTotalAmount() {
        return _totalAmount;
    }

    /**
     * @param transferValue
     *            The transferValue to set.
     */
    public void setTotalAmount(long totalAmount) {
        _totalAmount = totalAmount;
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
    public String getMessage() {
        return _message;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setMessage(String country) {
        _message = country;
    }

    /**
     * @return Returns the _networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param code
     *            The _networkCode to set.
     */
    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    /**
     * @return Returns the _failureCount.
     */
    public long getFailureCount() {
        return _failureCount;
    }

    /**
     * @param count
     *            The _failureCount to set.
     */
    public void setFailureCount(long count) {
        _failureCount = count;
    }

    /**
     * @return Returns the _successCount.
     */
    public long getSuccessCount() {
        return _successCount;
    }

    /**
     * @param count
     *            The _successCount to set.
     */
    public void setSuccessCount(long count) {
        _successCount = count;
    }

    /**
     * @return Returns the _totalCount.
     */
    public long getTotalCount() {
        return _totalCount;
    }

    /**
     * @param count
     *            The _totalCount to set.
     */
    public void setTotalCount(long count) {
        _totalCount = count;
    }

    /**
     * @return Returns the _module.
     */
    public String getModule() {
        return _module;
    }

    /**
     * @param _module
     *            The _module to set.
     */
    public void setModule(String _module) {
        this._module = _module;
    }

    /**
     * @return Returns the _failureDayCount.
     */
    public long getFailureDayCount() {
        return _failureDayCount;
    }

    /**
     * @param dayCount
     *            The _failureDayCount to set.
     */
    public void setFailureDayCount(long dayCount) {
        _failureDayCount = dayCount;
    }

    /**
     * @return Returns the _successDayCount.
     */
    public long getSuccessDayCount() {
        return _successDayCount;
    }

    /**
     * @param dayCount
     *            The _successDayCount to set.
     */
    public void setSuccessDayCount(long dayCount) {
        _successDayCount = dayCount;
    }

    /**
     * @return Returns the _totalDayAmount.
     */
    public long getTotalDayAmount() {
        return _totalDayAmount;
    }

    /**
     * @param dayAmount
     *            The _totalDayAmount to set.
     */
    public void setTotalDayAmount(long dayAmount) {
        _totalDayAmount = dayAmount;
    }

    /**
     * @return Returns the _totalDayCount.
     */
    public long getTotalDayCount() {
        return _totalDayCount;
    }

    /**
     * @param dayCount
     *            The _totalDayCount to set.
     */
    public void setTotalDayCount(long dayCount) {
        _totalDayCount = dayCount;
    }

    public String toString() {
        String temp = "Network Code= " + _networkCode + ", Module =" + _module + ", Admin MSISDN=" + _msisdn + ", service type =" + _serviceType + ", total count =" + _totalCount + ", success count =" + _successCount + ", failure count =" + _failureCount + ", total amount =" + _totalAmount + ", total day count =" + _totalDayCount + ", success day count =" + _successDayCount + ", failure day count =" + _failureDayCount + ", total day amount =" + _totalDayAmount;
        temp = temp + " " + super.toString();
        return temp;
    }

}
