package com.btsl.ota.bulkpush.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;

import com.btsl.ota.services.businesslogic.SimProfileVO;

/**
 * @(#)BulkPushVO
 *                Copyright(c) 2003, Bharti Telesoft Ltd.
 *                All Rights Reserved
 *                Travelling objcet class for Bulk Pushing of the messages
 *                --------------------------------------------------------------
 *                -----------------------------------
 *                Author Date History
 *                --------------------------------------------------------------
 *                -----------------------------------
 *                Gurjeet 18/12/2003 Initial Creation
 *                --------------------------------------------------------------
 *                -----------------------------------
 */

public class BulkPushVO implements Serializable {
    private java.lang.String _userType;
    private java.lang.String _profile;
    private java.lang.String _serviceSetID;
    private java.lang.String _jobId;
    private java.lang.String _jobName;
    private ArrayList _jobList;
    private java.lang.String _batchId;
    private java.lang.String _msisdn;
    private java.lang.String _key;
    private java.lang.String _transactionId;
    private long _length = 0;
    private long _offset = 0;
    private int _jobSize = 0;
    private int _batchSize = 0;
    private int _position = 0;
    private java.lang.String _byteCode;
    private java.lang.String _serviceID;
    private java.lang.String _majorVersion;
    private java.lang.String _minorVersion;
    private java.lang.String _newServiceID;
    private java.lang.String _newMajorVersion;
    private java.lang.String _newMinorVersion;
    private java.lang.String _status;
    private java.lang.String _locationCode;
    private java.lang.String _locationName;
    private java.lang.String _description;
    private java.lang.String _label1;
    private java.lang.String _label2;
    private java.lang.String _operation;
    private java.lang.String _operationType;
    private java.lang.String _createdBy;
    private java.lang.String _newBatchId = null;
    private java.lang.String _newJobId = null;
    private java.lang.String _userTypeName = null;
    private java.lang.String _profileName = null;
    private java.lang.String _compareHexString = null;
    private java.util.Date _createdOn;
    private ArrayList _batchList = null;
    private java.lang.String _operationsHexCode;
    private ArrayList _compareStringList = null;
    private ArrayList _oldJobList = null;
    private ArrayList _newOtaJobList = null;
    private SimProfileVO _simProfileVO = null;

    private int _numberOfMobiles;
    private int _mobileCount;

    private String _message;
    private String _messageType; // BIN

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return _message;
    }

    /**
     * @param message
     *            The message to set.
     */
    public void setMessage(String message) {
        _message = message;
    }

    /**
     * @return Returns the messageType.
     */
    public String getMessageType() {
        return _messageType;
    }

    /**
     * @param messageType
     *            The messageType to set.
     */
    public void setMessageType(String messageType) {
        _messageType = messageType;
    }

    /**
     * To get the value of mobileCount field
     * 
     * @return mobileCount.
     */
    public int getMobileCount() {
        return _mobileCount;
    }

    /**
     * To set the value of mobileCount field
     */
    public void setMobileCount(int mobileCount) {
        _mobileCount = mobileCount;
    }

    /**
     * To get the value of numberOfMobiles field
     * 
     * @return numberOfMobiles.
     */
    public int getNumberOfMobiles() {
        return _numberOfMobiles;
    }

    /**
     * To set the value of numberOfMobiles field
     */
    public void setNumberOfMobiles(int numberOfMobiles) {
        _numberOfMobiles = numberOfMobiles;
    }

    /**
     * @return
     */
    public java.lang.String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @return
     */
    public java.util.Date getCreatedOn() {
        return  _createdOn;
    }

    /**
     * @return
     */
    public java.lang.String getMajorVersion() {
        return _majorVersion;
    }

    /**
     * @return
     */
    public java.lang.String getMinorVersion() {
        return _minorVersion;
    }

    /**
     * @return
     */
    public int getPosition() {
        return _position;
    }

    /**
     * @return
     */
    public java.lang.String getServiceID() {
        return _serviceID;
    }

    /**
     * @return
     */
    public java.lang.String getStatus() {
        return _status;
    }

    /**
     * @return
     */
    public java.lang.String getUserType() {
        return _userType;
    }

    /**
     * @param string
     */
    public void setCreatedBy(java.lang.String string) {
        _createdBy = string;
    }

    /**
     * @param date
     */
    public void setCreatedOn(java.util.Date date) {
        this._createdOn = date;
    }

    /**
     * @param string
     */
    public void setMajorVersion(java.lang.String string) {
        _majorVersion = string;
    }

    /**
     * @param string
     */
    public void setMinorVersion(java.lang.String string) {
        _minorVersion = string;
    }

    /**
     * @param i
     */
    public void setPosition(int i) {
        _position = i;
    }

    /**
     * @param string
     */
    public void setServiceID(java.lang.String string) {
        _serviceID = string;
    }

    /**
     * @param string
     */
    public void setStatus(java.lang.String string) {
        _status = string;
    }

    /**
     * @param string
     */
    public void setUserType(java.lang.String string) {
        _userType = string;
    }

    /**
     * @return
     */
    public java.lang.String getDescription() {
        return _description;
    }

    /**
     * @return
     */
    public java.lang.String getLabel1() {
        return _label1;
    }

    /**
     * @return
     */
    public java.lang.String getLabel2() {
        return _label2;
    }

    /**
     * @param string
     */
    public void setDescription(java.lang.String string) {
        _description = string;
    }

    /**
     * @param string
     */
    public void setLabel1(java.lang.String string) {
        _label1 = string;
    }

    /**
     * @param string
     */
    public void setLabel2(java.lang.String string) {
        _label2 = string;
    }

    /**
     * @return
     */
    public java.lang.String getLocationCode() {
        return _locationCode;
    }

    /**
     * @return
     */
    public java.lang.String getLocationName() {
        return _locationName;
    }

    /**
     * @param string
     */
    public void setLocationCode(java.lang.String string) {
        _locationCode = string;
    }

    /**
     * @param string
     */
    public void setLocationName(java.lang.String string) {
        _locationName = string;
    }

    /**
     * @return
     */
    public java.lang.String getNewMajorVersion() {
        return _newMajorVersion;
    }

    /**
     * @return
     */
    public java.lang.String getNewMinorVersion() {
        return _newMinorVersion;
    }

    /**
     * @return
     */
    public java.lang.String getNewServiceID() {
        return _newServiceID;
    }

    /**
     * @param string
     */
    public void setNewMajorVersion(java.lang.String string) {
        _newMajorVersion = string;
    }

    /**
     * @param string
     */
    public void setNewMinorVersion(java.lang.String string) {
        _newMinorVersion = string;
    }

    /**
     * @param string
     */
    public void setNewServiceID(java.lang.String string) {
        _newServiceID = string;
    }

    /**
     * @return
     */
    public java.lang.String getProfile() {
        return _profile;
    }

    /**
     * @param string
     */
    public void setProfile(java.lang.String string) {
        _profile = string;
    }

    /**
     * @return
     */
    public long getLength() {
        return _length;
    }

    /**
     * @return
     */
    public long getOffset() {
        return _offset;
    }

    /**
     * @param l
     */
    public void setLength(long l) {
        _length = l;
    }

    /**
     * @param l
     */
    public void setOffset(long l) {
        _offset = l;
    }

    /**
     * @return
     */
    public java.lang.String getByteCode() {
        return _byteCode;
    }

    /**
     * @param string
     */
    public void setByteCode(java.lang.String string) {
        _byteCode = string;
    }

    /**
     * @return
     */
    public java.lang.String getOperation() {
        return _operation;
    }

    /**
     * @param string
     */
    public void setOperation(java.lang.String string) {
        _operation = string;
    }

    /**
     * @return
     */
    public java.lang.String getServiceSetID() {
        return _serviceSetID;
    }

    /**
     * @param string
     */
    public void setServiceSetID(java.lang.String string) {
        _serviceSetID = string;
    }

    /**
     * @return
     */
    public java.lang.String getProfileName() {
        return _profileName;
    }

    /**
     * @return
     */
    public java.lang.String getUserTypeName() {
        return _userTypeName;
    }

    /**
     * @param string
     */
    public void setProfileName(java.lang.String string) {
        _profileName = string;
    }

    /**
     * @param string
     */
    public void setUserTypeName(java.lang.String string) {
        _userTypeName = string;
    }

    /**
     * @return
     */
    public int getBatchSize() {
        return _batchSize;
    }

    /**
     * @return
     */
    public int getJobSize() {
        return _jobSize;
    }

    /**
     * @param i
     */
    public void setBatchSize(int i) {
        _batchSize = i;
    }

    /**
     * @param i
     */
    public void setJobSize(int i) {
        _jobSize = i;
    }

    /**
     * @return
     */
    public java.lang.String getCompareHexString() {
        return _compareHexString;
    }

    /**
     * @param string
     */
    public void setCompareHexString(java.lang.String string) {
        _compareHexString = string;
    }

    /**
     * @return
     */
    public java.lang.String getBatchId() {
        return _batchId;
    }

    /**
     * @return
     */
    public java.lang.String getJobId() {
        return _jobId;
    }

    /**
     * @param string
     */
    public void setBatchId(java.lang.String string) {
        _batchId = string;
    }

    /**
     * @param string
     */
    public void setJobId(java.lang.String string) {
        _jobId = string;
    }

    /**
     * @return
     */
    public java.lang.String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param string
     */
    public void setMsisdn(java.lang.String string) {
        _msisdn = string;
    }

    /**
     * @return
     */
    public java.lang.String getTransactionId() {
        return _transactionId;
    }

    /**
     * @param string
     */
    public void setTransactionId(java.lang.String string) {
        _transactionId = string;
    }

    /**
     * @return
     */
    public java.lang.String getOperationType() {
        return _operationType;
    }

    /**
     * @param string
     */
    public void setOperationType(java.lang.String string) {
        _operationType = string;
    }

    /**
     * @return
     */
    public java.lang.String getNewBatchId() {
        return _newBatchId;
    }

    /**
     * @param string
     */
    public void setNewBatchId(java.lang.String string) {
        _newBatchId = string;
    }

    /**
     * @return
     */
    public java.lang.String getJobName() {
        return _jobName;
    }

    /**
     * @param string
     */
    public void setJobName(java.lang.String string) {
        _jobName = string;
    }

    /**
     * @return
     */
    public ArrayList getBatchList() {
        return  _batchList;
    }

    /**
     * @param list
     */
    public void setBatchList(ArrayList list) {
        this._batchList = list;
    }

    /**
     * @return
     */
    public ArrayList getJobList() {
        return  _jobList;
    }

    /**
     * @param list
     */
    public void setJobList(ArrayList list) {
        this._jobList = list;
    }

    /**
     * @return
     */
    public java.lang.String getKey() {
        return _key;
    }

    /**
     * @param string
     */
    public void setKey(java.lang.String string) {
        _key = string;
    }

    /**
     * @return
     */
    public ArrayList getCompareStringList() {
        return  _compareStringList;
    }

    /**
     * @param list
     */
    public void setCompareStringList(ArrayList list) {
        this._compareStringList = list;
    }

    /**
     * @return
     */
    public java.lang.String getOperationsHexCode() {
        return _operationsHexCode;
    }

    /**
     * @param string
     */
    public void setOperationsHexCode(java.lang.String string) {
        _operationsHexCode = string;
    }

    /**
     * @return
     */
    public java.lang.String getNewJobId() {
        return _newJobId;
    }

    /**
     * @param string
     */
    public void setNewJobId(java.lang.String string) {
        _newJobId = string;
    }

    /**
     * @return
     */
    public ArrayList getOldJobList() {
        return _oldJobList;
    }

    /**
     * @param list
     */
    public void setOldJobList(ArrayList list) {
        this._oldJobList = list;
    }

    /**
     * @return
     */
    public ArrayList getNewOtaJobList() {
        return _newOtaJobList;
    }

    /**
     * @param list
     */
    public void setNewOtaJobList(ArrayList list) {
        this._newOtaJobList = list;
    }

    /**
     * @return
     */
    public SimProfileVO getSimProfileVO() {
        return _simProfileVO;
    }

    /**
     * @param profileVO
     */
    public void setSimProfileVO(SimProfileVO profileVO) {
        _simProfileVO = profileVO;
    }

}