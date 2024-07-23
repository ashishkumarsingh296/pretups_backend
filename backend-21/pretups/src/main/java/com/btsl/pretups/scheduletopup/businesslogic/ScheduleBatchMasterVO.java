package com.btsl.pretups.scheduletopup.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @# ScheduleBatchMasterVO.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Sandeep Goel Mar 31, 2006 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2006 Bharti Telesoft Ltd.
 */
public class ScheduleBatchMasterVO implements Serializable {
    
	private static final long serialVersionUID = 1L;
	private String _batchID = null;
    private String _batchIDDisp = null;
    private String _status = null;
    private String _networkCode = null;
    private long _totalCount = 0;
    private long _noOfRecords = 0;
    private long _successfulCount = 0;
    private long _uploadFailedCount = 0;
    private long _processFailedCount = 0;
    private long _cancelledCount = 0;
    private Date _scheduledDate = null;
    private String _parentID = null;
    private String _ownerID = null;
    private String _parentCategory = null;
    private String _parentDomain = null;
    private String _serviceType = null;
    private Date _createdOn = null;
    private String createdOnStr = null;
    private String _createdBy = null;
    private Date _modifiedOn = null;
    private String _modifiedBy = null;
    private String _initiatedBy = null;
    private long _lastModifiedTime;
    private String _refBatchID = null;
    private Integer iterations;
    private String frequency;
    private String userGeo;

    
    
    public String getCreatedOnStr() {
		return createdOnStr;
	}

	public void setCreatedOnStr(String _createdOnStr) {
		this.createdOnStr = _createdOnStr;
	}

	public String getUserGeo() {
		return userGeo;
	}

	public void setUserGeo(String userGeo) {
		this.userGeo = userGeo;
	}

	public Integer getIterations() {
		return iterations;
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/*
     * for display purpose
     */
    private String _initiatedByName = null;
    private String _scheduledDateStr = null;
    private String _statusDesc = null;

    private ArrayList _list = null;
    private Locale _senderLocale = null;
    private String _senderMsisdn = null;
    private String _senderlanguageCode = null;

    // for logging
    private Date _startDateOfBatch = null;
    private Date _endDateOfBatch = null;
    private String _otherInfo1 = null;
    private String _othterInfo2 = null;
    private String _otherInfo3 = null;
    private int _totalRecords;
    private int _proccessedRecords;
    private int _unproccessedRecords;
    // Used for counting number of connection refused.
    private int _connectionRefuseCounter;
    private String _prevStatus = null;
    // schedule filoe for normal users also
    private String _batchType = null;
    private String _serviceName = null;
    private String _activeUserId = null;
    private String _activeUserName = null;
    
	/**
     * @return Returns the batchIDDisp.
     */
    public String getBatchIDDisp() {
        return _batchIDDisp;
    }

    /**
     * @param batchIDDisp
     *            The batchIDDisp to set.
     */
    public void setBatchIDDisp(String batchIDDisp) {
        _batchIDDisp = batchIDDisp;
    }

    /**
     * @return Returns the prevStatus.
     */
    public String getPrevStatus() {
        return _prevStatus;
    }

    /**
     * @param prevStatus
     *            The prevStatus to set.
     */
    public void setPrevStatus(String prevStatus) {
        _prevStatus = prevStatus;
    }

    /**
     * @return Returns the connectionRefuseCounter.
     */
    public int getConnectionRefuseCounter() {
        return _connectionRefuseCounter;
    }

    /**
     * @param connectionRefuseCounter
     *            The connectionRefuseCounter to set.
     */
    public void setConnectionRefuseCounter(int connectionRefuseCounter) {
        _connectionRefuseCounter = connectionRefuseCounter;
    }

    public void initializeConRefCounter() {
        _connectionRefuseCounter = 0;
    }

    /**
     * @return Returns the endDateOfBatch.
     */
    public Date getEndDateOfBatch() {
        return _endDateOfBatch;
    }

    /**
     * @param endDateOfBatch
     *            The endDateOfBatch to set.
     */
    public void setEndDateOfBatch(Date endDateOfBatch) {
        _endDateOfBatch = endDateOfBatch;
    }

    /**
     * @return Returns the proccessedRecords.
     */
    public int getProccessedRecords() {
        return _proccessedRecords;
    }

    /**
     * @param proccessedRecords
     *            The proccessedRecords to set.
     */
    public void setProccessedRecords(int proccessedRecords) {
        _proccessedRecords = proccessedRecords;
    }

    /**
     * @return Returns the totalRecords.
     */
    public int getTotalRecords() {
        return _totalRecords;
    }

    /**
     * @param totalRecords
     *            The totalRecords to set.
     */
    public void setTotalRecords(int totalRecords) {
        _totalRecords = totalRecords;
    }

    /**
     * @return Returns the unproccessedRecords.
     */
    public int getUnproccessedRecords() {
        return _unproccessedRecords;
    }

    /**
     * @param unproccessedRecords
     *            The unproccessedRecords to set.
     */
    public void setUnproccessedRecords(int unproccessedRecords) {
        _unproccessedRecords = unproccessedRecords;
    }

    /**
     * @return Returns the startDateOfBatch.
     */
    public Date getStartDateOfBatch() {
        return _startDateOfBatch;
    }

    /**
     * @param startDateOfBatch
     *            The startDateOfBatch to set.
     */
    public void setStartDateOfBatch(Date startDateOfBatch) {
        _startDateOfBatch = startDateOfBatch;
    }

    /**
     * @return Returns the otherInfo1.
     */
    public String getOtherInfo1() {
        return _otherInfo1;
    }

    /**
     * @param otherInfo1
     *            The otherInfo1 to set.
     */
    public void setOtherInfo1(String otherInfo1) {
        _otherInfo1 = otherInfo1;
    }

    /**
     * @return Returns the otherInfo3.
     */
    public String getOtherInfo3() {
        return _otherInfo3;
    }

    /**
     * @param otherInfo3
     *            The otherInfo3 to set.
     */
    public void setOtherInfo3(String otherInfo3) {
        _otherInfo3 = otherInfo3;
    }

    /**
     * @return Returns the othterInfo2.
     */
    public String getOthterInfo2() {
        return _othterInfo2;
    }

    /**
     * @param othterInfo2
     *            The othterInfo2 to set.
     */
    public void setOthterInfo2(String othterInfo2) {
        _othterInfo2 = othterInfo2;
    }

    /**
     * @return Returns the senderlanguageCode.
     */
    public String getSenderlanguageCode() {
        return _senderlanguageCode;
    }

    /**
     * @param senderlanguageCode
     *            The senderlanguageCode to set.
     */
    public void setSenderlanguageCode(String senderlanguageCode) {
        _senderlanguageCode = senderlanguageCode;
    }

    public ScheduleBatchMasterVO() {
        super();
    }

    @Override
	public String toString() {
        StringBuilder sbf = new StringBuilder();
        sbf.append("_batchID = " + _batchID);
        sbf.append(",_scheduledDate = " + _scheduledDate);
        sbf.append(",_initiatedBy = " + _initiatedBy);
        sbf.append(",_networkCode = " + _networkCode);
        sbf.append(",_ownerID = " + _ownerID);
        sbf.append(",_parentCategory = " + _parentCategory);
        sbf.append(",_parentDomain = " + _parentDomain);
        sbf.append(",_parentID = " + _parentID);
        sbf.append(",_processFailedCount = " + _processFailedCount);
        sbf.append(",_serviceType = " + _serviceType);
        sbf.append(",_status = " + _status);
        sbf.append(",_successfulCount = " + _successfulCount);
        sbf.append(",_totalCount = " + _totalCount);
        sbf.append(",_uploadFailedCount = " + _uploadFailedCount);
        sbf.append(",_cancelledCount = " + _cancelledCount);
        sbf.append(",_createdOn = " + _createdOn);
        sbf.append(",_createdBy = " + _createdBy);
        sbf.append(",_modifiedOn = " + _modifiedOn);
        sbf.append(",_modifiedBy = " + _modifiedBy);
        sbf.append(",_refBatchID = " + _refBatchID);
        sbf.append(",_batchType = " + _batchType);
        sbf.append(",_serviceName = " + _serviceName);
        sbf.append(",_activeUserId = " + _activeUserId);
        sbf.append(",_activeUserName = " + _activeUserName);
        sbf.append(",frequency = " + frequency);
        sbf.append(",iterations = " + iterations);
        sbf.append(",processedOn = " + processedOn);
        sbf.append(",executedIterations = " + executedIterations);
        return sbf.toString();
    }

    /**
     * @return Returns the noOfRecords.
     */
    public long getNoOfRecords() {
        return _noOfRecords;
    }

    /**
     * @param noOfRecords
     *            The noOfRecords to set.
     */
    public void setNoOfRecords(long noOfRecords) {
        _noOfRecords = noOfRecords;
    }

    public String getBatchID() {
        return _batchID;
    }

    public void setBatchID(String batchID) {
        _batchID = batchID;
    }

    public long getCancelledCount() {
        return _cancelledCount;
    }

    public void setCancelledCount(long cancelledCount) {
        _cancelledCount = cancelledCount;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public String getInitiatedBy() {
        return _initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        _initiatedBy = initiatedBy;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getOwnerID() {
        return _ownerID;
    }

    public void setOwnerID(String ownerID) {
        _ownerID = ownerID;
    }

    public String getParentCategory() {
        return _parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        _parentCategory = parentCategory;
    }

    public String getParentDomain() {
        return _parentDomain;
    }

    public void setParentDomain(String parentDomain) {
        _parentDomain = parentDomain;
    }

    public String getParentID() {
        return _parentID;
    }

    public void setParentID(String parentID) {
        _parentID = parentID;
    }

    public long getProcessFailedCount() {
        return _processFailedCount;
    }

    public void setProcessFailedCount(long processFailedCount) {
        _processFailedCount = processFailedCount;
    }

    public Date getScheduledDate() {
        return _scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        _scheduledDate = scheduledDate;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public long getSuccessfulCount() {
        return _successfulCount;
    }

    public void setSuccessfulCount(long successfulCount) {
        _successfulCount = successfulCount;
    }

    public long getTotalCount() {
        return _totalCount;
    }

    public void setTotalCount(long totalCount) {
        _totalCount = totalCount;
    }

    public long getUploadFailedCount() {
        return _uploadFailedCount;
    }

    public void setUploadFailedCount(long uploadFailedCount) {
        _uploadFailedCount = uploadFailedCount;
    }

    public String getInitiatedByName() {
        return _initiatedByName;
    }

    public void setInitiatedByName(String initiatedByName) {
        _initiatedByName = initiatedByName;
    }

    public String getScheduledDateStr() {
        return _scheduledDateStr;
    }

    public void setScheduledDateStr(String scheduledDateStr) {
        _scheduledDateStr = scheduledDateStr;
    }

    public String getStatusDesc() {
        return _statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        _lastModifiedTime = lastModifiedTime;
    }

    public String getRefBatchID() {
        return _refBatchID;
    }

    public void setRefBatchID(String refBatchID) {
        _refBatchID = refBatchID;
    }

    public ArrayList getList() {
        return _list;
    }

    public void setList(ArrayList list) {
        _list = list;
    }

    /**
     * @return Returns the senderLocale.
     */
    public Locale getSenderLocale() {
        return _senderLocale;
    }

    /**
     * @param senderLocale
     *            The senderLocale to set.
     */
    public void setSenderLocale(Locale senderLocale) {
        _senderLocale = senderLocale;
    }

    /**
     * @return Returns the senderMsisdn.
     */
    public String getSenderMsisdn() {
        return _senderMsisdn;
    }

    /**
     * @param senderMsisdn
     *            The senderMsisdn to set.
     */
    public void setSenderMsisdn(String senderMsisdn) {
        _senderMsisdn = senderMsisdn;
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

    public String getDisplayBatchID() {
        return _batchID + " (" + _scheduledDateStr + ")";
    }

    /**
     * @return Returns the serviceName.
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * @param serviceName
     *            The serviceName to set.
     */
    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
    }

    /**
     * @return Returns the activeUserId.
     */
    public String getActiveUserId() {
        return _activeUserId;
    }

    /**
     * @param activeUserId
     *            The activeUserId to set.
     */
    public void setActiveUserId(String activeUserId) {
        _activeUserId = activeUserId;
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
	private int executedIterations;

	public int getExecutedIterations() {
		return executedIterations;
	}

	public void setExecutedIterations(int executedIterations) {
		this.executedIterations = executedIterations;
	}

	private Date processedOn = null;
	private String processedOnStr=null;
	public Date getProcessedOn() {
		return processedOn;
	}

	public void setProcessedOn(Date processedOn) {
		this.processedOn = processedOn;
	}
	
	public String getProcessedOnStr() {
	        return processedOnStr;
	    }

	public void setProcessedOnStr(String processedOnStr) {
		this.processedOnStr = processedOnStr;
	    }
}
