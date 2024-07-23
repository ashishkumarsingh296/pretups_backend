package com.btsl.pretups.scheduletopup.businesslogic;

import java.util.Date;

import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;

/**
 * @# ScheduleBatchDetailVO.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Sandeep Goel Mar 31, 2006 Initial creation
 *    Ashish Apr 22, 2006 Modified
 *    Babu Kunwar 10-OCT-2011 Modified(FOR IAT RCHRG)
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2006 Bharti Telesoft Ltd.
 */
public class ScheduleBatchDetailVO extends RestrictedSubscriberVO {
    private String _batchID = null;
    private Date _processedOn = null;
    private String _processedOnAsString = null;
    private String _transactionID = null;
    private String _checkBoxVal;

    // schedule detail schedule status.
    private String _scheduleStatus = null;
    private String _prevScheduleStatus = null;
    // for logging purpose
    private boolean _error;

    private String _scheduleDateStr = null;

    private String _subService = null; // for the subService used in the
                                       // restrictedTopUp module
    private String _subServiceDesc = null;
    private String _transferErrorCode = null;
    // schedule top up for normal batch type
    private String _batchType = null;
    private String _donorLanguage = null;
    private String _donorCountry = null;
    private String _donorMsisdn = null;
    private String _donorName = null;
    private String _donorLanguageCode = null;
    private String _senderLanguageCode = null;
    private String _activeUserName = null;
    // Added By Babu Kunwar For Corporate IAT Recharge
    private String _restrictedType = null;
    
    private Integer rowNumber;
    private String frequency;
    private Integer iterations;
    private int executedIterations;

    public ScheduleBatchDetailVO() {
        super();
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(super.toString());
        sbf.append("_batchID = " + _batchID);
        sbf.append(",_processedOn = " + _processedOn);
        sbf.append(",_processedOnAsString = " + _processedOnAsString);
        sbf.append(",_transactionID = " + _transactionID);
        sbf.append(",_scheduleDateStr=" + _scheduleDateStr);
        sbf.append(",_scheduleStatus=" + _scheduleStatus);
        sbf.append(",_prevScheduleStatus=" + _prevScheduleStatus);
        sbf.append(",_subService=" + _subService);
        sbf.append(",_transferErrorCode=" + _transferErrorCode);
        sbf.append(",_donorName=" + _donorName);
        sbf.append(",_donorMsisdn=" + _donorMsisdn);
        sbf.append(",_donorLanguage=" + _donorLanguage);
        sbf.append(",_donorCountry=" + _donorCountry);
        sbf.append(",_donorLanguageCode=" + _donorLanguageCode);
        sbf.append(",_senderLanguageCode=" + _senderLanguageCode);
        sbf.append(",_activeUserName=" + _activeUserName);
        sbf.append(",executedIterations=" + executedIterations);
        return sbf.toString();
    }

    
    
    public Integer getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}

	/**
     * @return Returns the restrictedType
     */
    public String getRestrictedType() {
        return _restrictedType;
    }

    /**
     * @return Returns the restricted type to set
     */
    public void setRestrictedType(String restrictedType) {
        _restrictedType = restrictedType;
    }

    /**
     * @return Returns the transferErrorCode.
     */
    public String getTransferErrorCode() {
        return _transferErrorCode;
    }

    /**
     * @param transferErrorCode
     *            The transferErrorCode to set.
     */
    public void setTransferErrorCode(String transferErrorCode) {
        _transferErrorCode = transferErrorCode;
    }

    /**
     * @return Returns the prevScheduleStatus.
     */
    public String getPrevScheduleStatus() {
        return _prevScheduleStatus;
    }

    /**
     * @param prevScheduleStatus
     *            The prevScheduleStatus to set.
     */
    public void setPrevScheduleStatus(String prevScheduleStatus) {
        _prevScheduleStatus = prevScheduleStatus;
    }

    /**
     * @return Returns the scheduleStatus.
     */
    public String getScheduleStatus() {
        return _scheduleStatus;
    }

    /**
     * @param scheduleStatus
     *            The scheduleStatus to set.
     */
    public void setScheduleStatus(String scheduleStatus) {
        _scheduleStatus = scheduleStatus;
    }

    public String getBatchID() {
        return _batchID;
    }

    public void setBatchID(String batchID) {
        _batchID = batchID;
    }

    public Date getProcessedOn() {
        return _processedOn;
    }

    public void setProcessedOn(Date processedOn) {
        _processedOn = processedOn;
    }

    public String getTransactionID() {
        return _transactionID;
    }

    public void setTransactionID(String transactionID) {
        _transactionID = transactionID;
    }

    /**
     * @return Returns the processedOnAsString.
     */
    public String getProcessedOnAsString() {
        return _processedOnAsString;
    }

    /**
     * @param processedOnAsString
     *            The processedOnAsString to set.
     */
    public void setProcessedOnAsString(String processedOnAsString) {
        _processedOnAsString = processedOnAsString;
    }

    public String getCheckBoxVal() {
        return _checkBoxVal;
    }

    public void setCheckBoxVal(String checkBoxVal) {
        _checkBoxVal = checkBoxVal;
    }

    public String getScheduleDateStr() {
        return _scheduleDateStr;
    }

    public void setScheduleDateStr(String scheduleDateStr) {
        _scheduleDateStr = scheduleDateStr;
    }

    /**
     * @return Returns the error.
     */
    public boolean isError() {
        return _error;
    }

    /**
     * @param error
     *            The error to set.
     */
    public void setError(boolean error) {
        _error = error;
    }

    public String getSubService() {
        return _subService;
    }

    public void setSubService(String subService) {
        _subService = subService;
    }

    public String getSubServiceDesc() {
        return _subServiceDesc;
    }

    public void setSubServiceDesc(String subServiceDesc) {
        _subServiceDesc = subServiceDesc;
    }

    /**
     * @return Returns the batchType.
     */
    public String getBatchType() {
        return _batchType;
    }

    /**
     * @param batchType
     *            The batchType to set.
     */
    public void setBatchType(String batchType) {
        _batchType = batchType;
    }

    /**
     * @return Returns the donorCountry.
     */
    public String getDonorCountry() {
        return _donorCountry;
    }

    /**
     * @param donorCountry
     *            The donorCountry to set.
     */
    public void setDonorCountry(String donorCountry) {
        _donorCountry = donorCountry;
    }

    /**
     * @return Returns the donorLanguage.
     */
    public String getDonorLanguage() {
        return _donorLanguage;
    }

    /**
     * @param donorLanguage
     *            The donorLanguage to set.
     */
    public void setDonorLanguage(String donorLanguage) {
        _donorLanguage = donorLanguage;
    }

    /**
     * @return Returns the donorMsisdn.
     */
    public String getDonorMsisdn() {
        return _donorMsisdn;
    }

    /**
     * @param donorMsisdn
     *            The donorMsisdn to set.
     */
    public void setDonorMsisdn(String donorMsisdn) {
        _donorMsisdn = donorMsisdn;
    }

    /**
     * @return Returns the donorName.
     */
    public String getDonorName() {
        return _donorName;
    }

    /**
     * @param donorName
     *            The donorName to set.
     */
    public void setDonorName(String donorName) {
        _donorName = donorName;
    }

    /**
     * @return Returns the donorLanguageCode.
     */
    public String getDonorLanguageCode() {
        return _donorLanguageCode;
    }

    /**
     * @param donorLanguageCode
     *            The donorLanguageCode to set.
     */
    public void setDonorLanguageCode(String donorLanguageCode) {
        _donorLanguageCode = donorLanguageCode;
    }

    /**
     * @return Returns the senderLanguageCode.
     */
    public String getSenderLanguageCode() {
        return _senderLanguageCode;
    }

    /**
     * @param senderLanguageCode
     *            The senderLanguageCode to set.
     */
    public void setSenderLanguageCode(String senderLanguageCode) {
        _senderLanguageCode = senderLanguageCode;
    }

    /**
     * @return Returns the activeUserName.
     */
    public String getActiveUserName() {
        return _activeUserName;
    }

    /**
     * @param activeUserName
     *            The activeUserName to set.
     */
    public void setActiveUserName(String activeUserName) {
        _activeUserName = activeUserName;
    }
    
	
	public int getExecutedIterations() {
		return executedIterations;
	}

	public void setExecutedIterations(int executedIterations) {
		this.executedIterations = executedIterations;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public Integer getIterations() {
		return iterations;
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}
}
