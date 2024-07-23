package com.selftopup.pretups.p2p.subscriber.businesslogic;

/*
 * BuddyVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 21/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.Locale;

import com.selftopup.pretups.subscriber.businesslogic.ReceiverVO;

public class P2PBatchesVO implements Serializable {

    private String _listName; // added by harsh 23 Aug12
    private String _batchID;
    private String _parentID;
    private String _status;
    private String _scheduleType;
    private long _noOfSchedule;
    private long _batchTotalRecords;
    private Date _batchDate;
    private Date _createdOn;
    private Date _modifiedOn;
    private long _executionCount;
    private long _successiveFailCount;
    private String _senderMSISDN;
    private ArrayList _buddyList;
    public String _senderLocale;
    public String _createdBy;
    public String _modifiedBy;
    public String _senderPin;
    public String _senderLangCode;
    public String _senderCountry;

    // added by harsh
    private Date _scheduleDate;
    private String _networkCode;

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getSenderLangCode() {
        return _senderLangCode;
    }

    public void setSenderLangCode(String langCode) {
        _senderLangCode = langCode;
    }

    public String getSenderPin() {
        return _senderPin;
    }

    public void setSenderPin(String senderPin) {
        _senderPin = senderPin;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_batchID=" + _batchID);
        sbf.append(",_parentID=" + _parentID);
        sbf.append(",_scheduleType=" + _scheduleType);
        sbf.append(",_noOfSchedule=" + _noOfSchedule);
        sbf.append(",_batchDate=" + _batchDate);
        sbf.append(",_executionCount=" + _executionCount);
        sbf.append("," + super.toString());
        return sbf.toString();
    }

    public String getListName() {
        return _listName;
    }

    /**
     * @param name
     *            The _listName to set.
     */
    public void setListName(String name) {
        _listName = name;
    }

    public String getBatchID() {
        return _batchID;
    }

    public void setBatchID(String batchID) {
        _batchID = batchID;
    }

    public String getParentID() {
        return _parentID;
    }

    public void setParentID(String parentID) {
        _parentID = parentID;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getScheduleType() {
        return _scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        _scheduleType = scheduleType;
    }

    public long getBatchTotalRecords() {
        return _batchTotalRecords;
    }

    public void setBatchTotalRecords(long batchTotalRecords) {
        _batchTotalRecords = batchTotalRecords;
    }

    public Date getBatchDate() {
        return _batchDate;
    }

    public void setBatchDate(Date batchDate) {
        _batchDate = batchDate;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public long getExecutionCount() {
        return _executionCount;
    }

    public void setExecutionCount(long executionCount) {
        _executionCount = executionCount;
    }

    public long getNoOfSchedule() {
        return _noOfSchedule;
    }

    public void setNoOfSchedule(long noOfSchedule) {
        _noOfSchedule = noOfSchedule;
    }

    public long getSuccessiveFailCount() {
        return _successiveFailCount;
    }

    public void setSuccessiveFailCount(long successiveFailCount) {
        _successiveFailCount = successiveFailCount;
    }

    public String getSenderMSISDN() {
        return _senderMSISDN;
    }

    public void setSenderMSISDN(String senderMSISDN) {
        _senderMSISDN = senderMSISDN;
    }

    public ArrayList getBuddyList() {
        return _buddyList;
    }

    public void setBuddyList(ArrayList buddyList) {
        _buddyList = buddyList;
    }

    public String getSenderLocale() {
        return _senderLocale;
    }

    public void setSenderLocale(String senderLocale) {
        _senderLocale = senderLocale;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public String getSenderCountry() {
        return _senderCountry;
    }

    public void setSenderCountry(String senderCountry) {
        _senderCountry = senderCountry;
    }

    public Date getScheduleDate() {
        return _scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        _scheduleDate = scheduleDate;
    }
}
