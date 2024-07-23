package com.btsl.pretups.subscriber.businesslogic;

import java.io.Serializable;
import java.util.Date;

/*
 * PostPaidControlParametersVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 02/07/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

public class PostPaidControlParametersVO implements Serializable {

    
	private static final long serialVersionUID = 1L;
	private String _msisdn;
    private long _dailyTransferAllowed;
    private long _dailyTransferAmountAllowed;
    private long _weeklyTransferAllowed;
    private long _weeklyTransferAmountAllowed;
    private long _monthlyTransferAllowed;
    private long _monthlyTransferAmountAllowed;
    // Added on 07/02/08 for addition of new date_time column in the table
    // POSTPAID_CONTROL_PARAMETERS.
    private Date _dateTime = null;
@Override
    public String toString() {
        StringBuilder sbf = new StringBuilder();
        sbf.append("_msisdn =" + _msisdn);
        sbf.append(",_dailyTransferAllowed =" + _dailyTransferAllowed);
        sbf.append(",_dailyTransferAmountAllowed =" + _dailyTransferAmountAllowed);
        sbf.append(",_weeklyTransferAllowed =" + _weeklyTransferAllowed);
        sbf.append(",_weeklyTransferAmountAllowed =" + _weeklyTransferAmountAllowed);
        sbf.append(",_monthlyTransferAllowed =" + _monthlyTransferAllowed);
        sbf.append(",_monthlyTransferAmountAllowed =" + _monthlyTransferAmountAllowed);
        sbf.append(",_dateTime =" + _dateTime);
        return sbf.toString();
    }

    public long getDailyTransferAllowed() {
        return _dailyTransferAllowed;
    }

    public void setDailyTransferAllowed(long dailyTransferAllowed) {
        _dailyTransferAllowed = dailyTransferAllowed;
    }

    public long getDailyTransferAmountAllowed() {
        return _dailyTransferAmountAllowed;
    }

    public void setDailyTransferAmountAllowed(long dailyTransferAmountAllowed) {
        _dailyTransferAmountAllowed = dailyTransferAmountAllowed;
    }

    public long getMonthlyTransferAllowed() {
        return _monthlyTransferAllowed;
    }

    public void setMonthlyTransferAllowed(long monthlyTransferAllowed) {
        _monthlyTransferAllowed = monthlyTransferAllowed;
    }

    public long getMonthlyTransferAmountAllowed() {
        return _monthlyTransferAmountAllowed;
    }

    public void setMonthlyTransferAmountAllowed(long monthlyTransferAmountAllowed) {
        _monthlyTransferAmountAllowed = monthlyTransferAmountAllowed;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public long getWeeklyTransferAllowed() {
        return _weeklyTransferAllowed;
    }

    public void setWeeklyTransferAllowed(long weeklyTransferAllowed) {
        _weeklyTransferAllowed = weeklyTransferAllowed;
    }

    public long getWeeklyTransferAmountAllowed() {
        return _weeklyTransferAmountAllowed;
    }

    public void setWeeklyTransferAmountAllowed(long weeklyTransferAmountAllowed) {
        _weeklyTransferAmountAllowed = weeklyTransferAmountAllowed;
    }

    /**
     * @return Returns the dateTime.
     */
    public Date getDateTime() {
        return _dateTime;
    }

    /**
     * @param dateTime
     *            The dateTime to set.
     */
    public void setDateTime(Date dateTime) {
        this._dateTime = dateTime;
    }
}
