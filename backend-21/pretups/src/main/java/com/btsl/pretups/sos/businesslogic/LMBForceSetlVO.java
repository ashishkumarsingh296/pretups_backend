package com.btsl.pretups.sos.businesslogic;

import java.util.Date;

public class LMBForceSetlVO {
    private String _lmbMsisdn;
    private String _lmbRechargeDateStr;
    private String _lineNumber;
    private long _failCount;
    private String _records;
    private String _errorCode;
    private String _errorMessage;
    private String _forceSettleStatus;
    private Date _date;
    private boolean isDefaultSuccStatus = false;
    private String _prevStatus;
    private long _amount;

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(" _sosMsisdn:" + _lmbMsisdn);
        sbf.append(" _lmbRechargeDateStr:" + _lmbRechargeDateStr);
        sbf.append(" _lineNumber:" + _lineNumber);
        sbf.append(" _failCount:" + _failCount);
        sbf.append(" _records:" + _records);
        sbf.append(" _errorCode:" + _errorCode);
        sbf.append(" _errorMessage" + _errorMessage);
        sbf.append(" _settleStatus" + _forceSettleStatus);
        sbf.append(" _date" + _date);
        sbf.append(" isDefault" + isDefaultSuccStatus);
        sbf.append(" _prevStatus" + _prevStatus);
        sbf.append(" _amount" + _amount);
        return sbf.toString();
    }

    /**
     * @return the failCount
     */
    public long getFailCount() {
        return _failCount;
    }

    /**
     * @param failCount
     *            the failCount to set
     */
    public void setFailCount(long failCount) {
        _failCount = failCount;
    }

    /**
     * @return the lineNumber
     */
    public String getLineNumber() {
        return _lineNumber;
    }

    /**
     * @param lineNumber
     *            the lineNumber to set
     */
    public void setLineNumber(String lineNumber) {
        _lineNumber = lineNumber;
    }

    /**
     * @return the records
     */
    public String getRecords() {
        return _records;
    }

    /**
     * @param records
     *            the records to set
     */
    public void setRecords(String records) {
        _records = records;
    }

    /**
     * @return the sosMsisdn
     */
    public String getLmbMsisdn() {
        return _lmbMsisdn;
    }

    /**
     * @param sosMsisdn
     *            the sosMsisdn to set
     */
    public void setLmbMsisdn(String sosMsisdn) {
        _lmbMsisdn = sosMsisdn;
    }

    /**
     * @return the sosRechargeDateStr
     */
    public String getLmbRechargeDateStr() {
        return _lmbRechargeDateStr;
    }

    /**
     * @param sosRechargeDateStr
     *            the sosRechargeDateStr to set
     */
    public void setLmbRechargeDateStr(String sosRechargeDateStr) {
        _lmbRechargeDateStr = sosRechargeDateStr;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return _errorCode;
    }

    /**
     * @param errorCode
     *            the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        _errorCode = errorCode;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return _errorMessage;
    }

    /**
     * @param errorMessage
     *            the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        _errorMessage = errorMessage;
    }

    /**
     * @return the rechargeStatus
     */
    public String getForceSettleStatus() {
        return _forceSettleStatus;
    }

    /**
     * @param rechargeStatus
     *            the rechargeStatus to set
     */
    public void setForceSettleStatus(String rechargeStatus) {
        _forceSettleStatus = rechargeStatus;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return _date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
        _date = date;
    }

    /**
     * @return the prevStatus
     */
    public String getPrevStatus() {
        return _prevStatus;
    }

    /**
     * @param prevStatus
     *            the prevStatus to set
     */
    public void setPrevStatus(String prevStatus) {
        _prevStatus = prevStatus;
    }

    /**
     * @return the isDefault
     */
    public boolean isDefaultSuccStatus() {
        return isDefaultSuccStatus;
    }

    /**
     * @param isDefault
     *            the isDefault to set
     */
    public void setDefaultSuccStatus(boolean isDefault) {
        this.isDefaultSuccStatus = isDefault;
    }

    public long getAmount() {
        return _amount;
    }

    /**
     * @param failCount
     *            the failCount to set
     */
    public void setAmount(long amount) {
        _amount = amount;
    }
}
