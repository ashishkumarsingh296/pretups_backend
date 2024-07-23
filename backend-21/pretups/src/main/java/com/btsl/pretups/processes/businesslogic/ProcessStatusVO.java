/**
 * @(#)ProcessStatusVO.java
 *                          Name Date History
 *                          ----------------------------------------------------
 *                          --------------------
 *                          Ashish Kumar 22/04/2005 Initial Creation
 *                          ----------------------------------------------------
 *                          --------------------
 *                          Copyright (c) 2006 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.processes.businesslogic;

import java.util.Date;

public class ProcessStatusVO {

    private String _processID;
    private Date _startDate;
    private String _processStatus;
    private Date _executedUpto;
    private Date _executedOn;
    private String _startDateString;
    private String _executionDateString;
    private String _executedUptoString;
    private long _expiryTime; // in minutes
    private long _beforeInterval; // in minutes
    private boolean _statusOkBool;
    private String _networkCode;
    private int _recordCount = 0;

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("_processID=" + _processID);
        sb.append("_startDate=" + _startDate);
        sb.append(",_processStatus=" + _processStatus);
        sb.append(",_executedUpto=" + _executedUpto);
        sb.append(",_executedOn=" + _executedOn);
        sb.append(",_expiryTime=" + _expiryTime);
        sb.append(",_beforeInterval=" + _beforeInterval);
        sb.append(",_statusOkBool=" + _statusOkBool);
        sb.append(",_recordCount" + _recordCount);
        return sb.toString();
    }

    /**
     * @return Returns the executedOn.
     */
    public Date getExecutedOn() {
        return _executedOn;
    }

    /**
     * @param executedOn
     *            The executedOn to set.
     */
    public void setExecutedOn(Date executedOn) {
        _executedOn = executedOn;
    }

    /**
     * @return Returns the executedUpto.
     */
    public Date getExecutedUpto() {
        return _executedUpto;
    }

    /**
     * @param executedUpto
     *            The executedUpto to set.
     */
    public void setExecutedUpto(Date executedUpto) {
        _executedUpto = executedUpto;
    }

    /**
     * @return Returns the executedUptoString.
     */
    public String getExecutedUptoString() {
        return _executedUptoString;
    }

    /**
     * @param executedUptoString
     *            The executedUptoString to set.
     */
    public void setExecutedUptoString(String executedUptoString) {
        _executedUptoString = executedUptoString;
    }

    /**
     * @return Returns the executionDateString.
     */
    public String getExecutionDateString() {
        return _executionDateString;
    }

    /**
     * @param executionDateString
     *            The executionDateString to set.
     */
    public void setExecutionDateString(String executionDateString) {
        _executionDateString = executionDateString;
    }

    /**
     * @return Returns the processID.
     */
    public String getProcessID() {
        return _processID;
    }

    /**
     * @param processID
     *            The processID to set.
     */
    public void setProcessID(String processID) {
        _processID = processID;
    }

    /**
     * @return Returns the processStatus.
     */
    public String getProcessStatus() {
        return _processStatus;
    }

    /**
     * @param processStatus
     *            The processStatus to set.
     */
    public void setProcessStatus(String processStatus) {
        _processStatus = processStatus;
    }

    /**
     * @return Returns the startDate.
     */
    public Date getStartDate() {
        return _startDate;
    }

    /**
     * @param startDate
     *            The startDate to set.
     */
    public void setStartDate(Date startDate) {
        _startDate = startDate;
    }

    /**
     * @return Returns the startDateString.
     */
    public String getStartDateString() {
        return _startDateString;
    }

    /**
     * @param startDateString
     *            The startDateString to set.
     */
    public void setStartDateString(String startDateString) {
        _startDateString = startDateString;
    }

    /**
     * @return Returns the expiryTime.
     */
    public long getExpiryTime() {
        return _expiryTime;
    }

    /**
     * @param expiryTime
     *            The expiryTime to set.
     */
    public void setExpiryTime(long expiryTime) {
        _expiryTime = expiryTime;
    }

    /**
     * @return Returns the beforeInterval.
     */
    public long getBeforeInterval() {
        return _beforeInterval;
    }

    /**
     * @param beforeInterval
     *            The beforeInterval to set.
     */
    public void setBeforeInterval(long beforeInterval) {
        _beforeInterval = beforeInterval;
    }

    /**
     * @return Returns the statusOkBool.
     */
    public boolean isStatusOkBool() {
        return _statusOkBool;
    }

    /**
     * @param statusOkBool
     *            The statusOkBool to set.
     */
    public void setStatusOkBool(boolean statusOkBool) {
        _statusOkBool = statusOkBool;
    }
    
    public int getRecordCount() {
    	return _recordCount;
    }
    
    
    public void setRecordCount(int cnt) {
    	 _recordCount = cnt;
    }
}
