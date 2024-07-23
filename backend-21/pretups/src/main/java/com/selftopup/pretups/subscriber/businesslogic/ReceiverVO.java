package com.selftopup.pretups.subscriber.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.selftopup.util.BTSLUtil;

/*
 * ReceiverVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 14/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

public class ReceiverVO extends SubscriberVO implements Serializable {
    private long _totalFailCount = 0; // Total Failed Transaction Count
    private long _totalConsecutiveFailCount = 0; // Total Consecutive Failed
                                                 // Count
    private Date _lastFailedOn = null; // Last Failed date and time
    private long _totalSuccessCount = 0; // Total Success Time
    private Date _lastSuccessOn = null; // Last Success Date and Time
    private long _totalTransferAmount = 0; // Total Transaction Amout in a day
    private String _lastTransferStage = null; // Last Transaction Stage
    private Date _createdDate = null; // Date against which the records will be
                                      // checked
    private boolean _noOfSuccTransCheckDone = false; // No of successful
                                                     // transactions is a day
                                                     // check done or not
    private boolean _totalTransAmtCheckDone = false; // Total transactions
                                                     // amount is a day check
                                                     // done or not
    private boolean _noOfConsFailCheckDone = false; // Consecutive failure check
                                                    // before getting barred
                                                    // check done or not
    private boolean _lastSuccTransBlockCheckDone = false; // Last Success
                                                          // transactions block
                                                          // time check done or
                                                          // not

    // BTRC- Varibles that would describe the receiver side control
    private boolean _noOfWeeklySuccTransCheckDone = false;// No of successful
                                                          // transactions in a
                                                          // week check done or
                                                          // not
    private boolean _noOfMonthlySuccTransCheckDone = false;// No of successful
                                                           // transactions in a
                                                           // week check done or
                                                           // not
    private boolean _totalWeeklyTransAmtCheckDone = false; // Total transactions
                                                           // amount in a day
                                                           // check done or not
    private boolean _totalMonthlyTransAmtCheckDone = false; // Total
                                                            // transactions
                                                            // amount in a day
                                                            // check done or not
    private long _prevDaySuccCount = 0; // Contains the total success counts of
                                        // the previous day.
    private long _prevDayTrasferAmount = 0; // Contains the total success amount
                                            // of previous day.
    private long _weeklySuccCount = 0; // Contains success transaction in a
                                       // week.
    private long _weeklyTransferAmount = 0;// Contains the successful amount
                                           // transfer in a week.
    private long _prevWeekSuccCount = 0;// Contains the number of transaction
                                        // done in previous week.
    private long _prevWeekTransferAmount = 0;// Contains the successful amount
                                             // transfer in previous week.
    private long _monthlySuccCount = 0;// Contains the successful transaction in
                                       // month.
    private long _monthlyTransferAmount = 0;// Contains the successful amount in
                                            // a month.
    private long _prevMonthSuccCount = 0;// Contains the total number of
                                         // transaction done in previous week.
    private long _prevMonthTransferAmount = 0;// Contains the total succesful
                                              // amount transffered in previous
                                              // week.
    // Added for Corporate IAT Recharge
    private String _ntworkErrorMsg = null;
    private boolean isErrorMsgFound = false;
    private String _countryCodeMatchError = null;
    // added by nilesh:for MRP block time
    private long _lastMRP = 0;
    private String _lastServiceType = null;
    private long _requestedMRP = 0;
    private String _requestedServiceType = null;
    // end
    private boolean _unmarkRequestStatus = false;

    private Object _restrictedSubscriberVO = null;

    public Date getLastFailedOn() {
        return _lastFailedOn;
    }

    public void setLastFailedOn(Date lastFailedOn) {
        _lastFailedOn = lastFailedOn;
    }

    public Date getLastSuccessOn() {
        return _lastSuccessOn;
    }

    public void setLastSuccessOn(Date lastSuccessOn) {
        _lastSuccessOn = lastSuccessOn;
    }

    public long getTotalConsecutiveFailCount() {
        return _totalConsecutiveFailCount;
    }

    public void setTotalConsecutiveFailCount(long totalConsecutiveFailCount) {
        _totalConsecutiveFailCount = totalConsecutiveFailCount;
    }

    public long getTotalFailCount() {
        return _totalFailCount;
    }

    public void setTotalFailCount(long totalFailCount) {
        _totalFailCount = totalFailCount;
    }

    public long getTotalSuccessCount() {
        return _totalSuccessCount;
    }

    public void setTotalSuccessCount(long totalSuccessCount) {
        _totalSuccessCount = totalSuccessCount;
    }

    public Date getCreatedDate() {
        return _createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        _createdDate = createdDate;
    }

    public boolean isLastSuccTransBlockCheckDone() {
        return _lastSuccTransBlockCheckDone;
    }

    public void setLastSuccTransBlockCheckDone(boolean lastSuccTransBlockCheckDone) {
        _lastSuccTransBlockCheckDone = lastSuccTransBlockCheckDone;
    }

    public boolean isNoOfConsFailCheckDone() {
        return _noOfConsFailCheckDone;
    }

    public void setNoOfConsFailCheckDone(boolean noOfConsFailCheckDone) {
        _noOfConsFailCheckDone = noOfConsFailCheckDone;
    }

    public boolean isNoOfSuccTransCheckDone() {
        return _noOfSuccTransCheckDone;
    }

    public void setNoOfSuccTransCheckDone(boolean noOfSuccTransCheckDone) {
        _noOfSuccTransCheckDone = noOfSuccTransCheckDone;
    }

    public boolean isTotalTransAmtCheckDone() {
        return _totalTransAmtCheckDone;
    }

    public void setTotalTransAmtCheckDone(boolean totalTransAmtCheckDone) {
        _totalTransAmtCheckDone = totalTransAmtCheckDone;
    }

    public String getLastTransferStage() {
        return _lastTransferStage;
    }

    public void setLastTransferStage(String lastTransferStage) {
        _lastTransferStage = lastTransferStage;
    }

    public long getTotalTransferAmount() {
        return _totalTransferAmount;
    }

    public void setTotalTransferAmount(long totalTransferAmount) {
        _totalTransferAmount = totalTransferAmount;
    }

    public boolean isUnmarkRequestStatus() {
        return _unmarkRequestStatus;
    }

    public void setUnmarkRequestStatus(boolean unmarkRequestStatus) {
        _unmarkRequestStatus = unmarkRequestStatus;
    }

    public Object getRestrictedSubscriberVO() {
        return _restrictedSubscriberVO;
    }

    public void setRestrictedSubscriberVO(Object restrictedSubscriberVO) {
        _restrictedSubscriberVO = restrictedSubscriberVO;
    }

    /**
     * @return Returns the monthlySuccAmount.
     */
    public long getMonthlyTransferAmount() {
        return _monthlyTransferAmount;
    }

    /**
     * @param p_monthlySuccAmount
     *            The monthlySuccAmount to set.
     */
    public void setMonthlyTransferAmount(long p_monthlySuccAmount) {
        _monthlyTransferAmount = p_monthlySuccAmount;
    }

    /**
     * @return Returns the monthlySuccCount.
     */
    public long getMonthlySuccCount() {
        return _monthlySuccCount;
    }

    /**
     * @param p_monthlySuccCount
     *            The monthlySuccCount to set.
     */
    public void setMonthlySuccCount(long p_monthlySuccCount) {
        _monthlySuccCount = p_monthlySuccCount;
    }

    /**
     * @return Returns the preMonthlySuccAmount.
     */
    public long getPrevMonthTransferAmount() {
        return _prevMonthTransferAmount;
    }

    /**
     * @param p_preMonthlySuccAmount
     *            The preMonthlySuccAmount to set.
     */
    public void setPrevMonthTransferAmount(long p_preMonthlySuccAmount) {
        _prevMonthTransferAmount = p_preMonthlySuccAmount;
    }

    /**
     * @return Returns the preMonthlySuccCount.
     */
    public long getPrevMonthSuccCount() {
        return _prevMonthSuccCount;
    }

    /**
     * @param p_preMonthlySuccCount
     *            The preMonthlySuccCount to set.
     */
    public void setPrevMonthSuccCount(long p_preMonthlySuccCount) {
        _prevMonthSuccCount = p_preMonthlySuccCount;
    }

    /**
     * @return Returns the prevDaySuccCount.
     */
    public long getPrevDaySuccCount() {
        return _prevDaySuccCount;
    }

    /**
     * @param p_prevDaySuccCount
     *            The prevDaySuccCount to set.
     */
    public void setPrevDaySuccCount(long p_prevDaySuccCount) {
        _prevDaySuccCount = p_prevDaySuccCount;
    }

    /**
     * @return Returns the prevDayTrasferAmount.
     */
    public long getPrevDayTrasferAmount() {
        return _prevDayTrasferAmount;
    }

    /**
     * @param p_prevDayTrasferAmount
     *            The prevDayTrasferAmount to set.
     */
    public void setPrevDayTrasferAmount(long p_prevDayTrasferAmount) {
        _prevDayTrasferAmount = p_prevDayTrasferAmount;
    }

    /**
     * @return Returns the prevWeekSuccAmount.
     */
    public long getPrevWeekTransferAmount() {
        return _prevWeekTransferAmount;
    }

    /**
     * @param p_prevWeekSuccAmount
     *            The prevWeekSuccAmount to set.
     */
    public void setPrevWeekTransferAmount(long p_prevWeekSuccAmount) {
        _prevWeekTransferAmount = p_prevWeekSuccAmount;
    }

    /**
     * @return Returns the prevWeekSuccCount.
     */
    public long getPrevWeekSuccCount() {
        return _prevWeekSuccCount;
    }

    /**
     * @param p_prevWeekSuccCount
     *            The prevWeekSuccCount to set.
     */
    public void setPrevWeekSuccCount(long p_prevWeekSuccCount) {
        _prevWeekSuccCount = p_prevWeekSuccCount;
    }

    /**
     * @return Returns the weeklySuccCount.
     */
    public long getWeeklySuccCount() {
        return _weeklySuccCount;
    }

    /**
     * @param p_weeklySuccCount
     *            The weeklySuccCount to set.
     */
    public void setWeeklySuccCount(long p_weeklySuccCount) {
        _weeklySuccCount = p_weeklySuccCount;
    }

    /**
     * @return Returns the weeklyTransferAmount.
     */
    public long getWeeklyTransferAmount() {
        return _weeklyTransferAmount;
    }

    /**
     * @param p_weeklyTransferAmount
     *            The weeklyTransferAmount to set.
     */
    public void setWeeklyTransferAmount(long p_weeklyTransferAmount) {
        _weeklyTransferAmount = p_weeklyTransferAmount;
    }

    /**
     * @return Returns the noOfMonthlySuccTransCheckDone.
     */
    public boolean isNoOfMonthlySuccTransCheckDone() {
        return _noOfMonthlySuccTransCheckDone;
    }

    /**
     * @param p_noOfMonthlySuccTransCheckDone
     *            The noOfMonthlySuccTransCheckDone to set.
     */
    public void setNoOfMonthlySuccTransCheckDone(boolean p_noOfMonthlySuccTransCheckDone) {
        _noOfMonthlySuccTransCheckDone = p_noOfMonthlySuccTransCheckDone;
    }

    /**
     * @return Returns the noOfWeeklySuccTransCheckDone.
     */
    public boolean isNoOfWeeklySuccTransCheckDone() {
        return _noOfWeeklySuccTransCheckDone;
    }

    /**
     * @param p_noOfWeeklySuccTransCheckDone
     *            The noOfWeeklySuccTransCheckDone to set.
     */
    public void setNoOfWeeklySuccTransCheckDone(boolean p_noOfWeeklySuccTransCheckDone) {
        _noOfWeeklySuccTransCheckDone = p_noOfWeeklySuccTransCheckDone;
    }

    /**
     * @return Returns the totalMonthlyTransAmtCheckDone.
     */
    public boolean isTotalMonthlyTransAmtCheckDone() {
        return _totalMonthlyTransAmtCheckDone;
    }

    /**
     * @param p_totalMonthlyTransAmtCheckDone
     *            The totalMonthlyTransAmtCheckDone to set.
     */
    public void setTotalMonthlyTransAmtCheckDone(boolean p_totalMonthlyTransAmtCheckDone) {
        _totalMonthlyTransAmtCheckDone = p_totalMonthlyTransAmtCheckDone;
    }

    /**
     * @return Returns the totalWeeklyTransAmtCheckDone.
     */
    public boolean isTotalWeeklyTransAmtCheckDone() {
        return _totalWeeklyTransAmtCheckDone;
    }

    /**
     * @param totalWeeklyTransAmtCheckDone
     *            The totalWeeklyTransAmtCheckDone to set.
     */
    public void setTotalWeeklyTransAmtCheckDone(boolean totalWeeklyTransAmtCheckDone) {
        _totalWeeklyTransAmtCheckDone = totalWeeklyTransAmtCheckDone;
    }

    public String getNtworkErrorMsg() {
        return _ntworkErrorMsg;
    }

    public void setNtworkErrorMsg(String ntworkErrorMsg) {
        _ntworkErrorMsg = ntworkErrorMsg;
    }

    public boolean isErrorMsgFound() {
        return isErrorMsgFound;
    }

    public void setErrorMsgFound(boolean isErrorMsgFound) {
        this.isErrorMsgFound = isErrorMsgFound;
    }

    public String getCountryCodeMatchError() {
        return _countryCodeMatchError;
    }

    public void setCountryCodeMatchError(String countryCodeMatchError) {
        _countryCodeMatchError = countryCodeMatchError;
    }

    // added by nilesh: for MRP block time
    /**
     * @return Returns the lastMRP.
     */
    public long getLastMRP() {
        return _lastMRP;
    }

    /**
     * @param lastMRP
     *            The lastMRP to set.
     */
    public void setLastMRP(long lastMRP) {
        _lastMRP = lastMRP;
    }

    /**
     * @return Returns the lastServiceType.
     */
    public String getLastServiceType() {
        return _lastServiceType;
    }

    /**
     * @param lastServiceType
     *            The lastServiceType to set.
     */
    public void setLastServiceType(String lastServiceType) {
        _lastServiceType = lastServiceType;
    }

    /**
     * @return Returns the RequestedServiceType.
     */
    public String getRequestedServiceType() {
        return _requestedServiceType;
    }

    /**
     * @param requestedServiceType
     *            The requestedServiceType to set.
     */
    public void setRequestedServiceType(String requestedServiceType) {
        _requestedServiceType = requestedServiceType;
    }

    /**
     * @return Returns the requestedMRP.
     */
    public long getRequestedMRP() {
        return _requestedMRP;
    }

    /**
     * @return Returns the requestedMRP.
     */
    public void setRequestedMRP(long requestedMRP) {
        _requestedMRP = requestedMRP;
    }
}
