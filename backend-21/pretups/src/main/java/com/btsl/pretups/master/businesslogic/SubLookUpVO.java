/*
 * #SubLookUpVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jun 21, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class SubLookUpVO implements Serializable {

    private String _lookupName;

    private String _subLookupCode;
    private String _subCode;
    private String _lookupCode;
    private String _subLookupName;
    private String _lookupType;
    private String _status;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private long _lastModified;
    private String _deleteAlowed;

    public SubLookUpVO() {
    }

    public SubLookUpVO(String code, String value) {
        _subLookupCode = code;
        _subLookupName = value;
    }

    public String toString() {

        StringBuffer strBuff = new StringBuffer("\nSub Lookup Code=" + _subLookupCode);
        strBuff.append("\nLookup Code=" + _lookupCode);
        strBuff.append("\nSub Lookup Name=" + _subLookupName);
        strBuff.append("\nLookup Type=" + _lookupType);
        strBuff.append("\nStatus=" + _status);
        strBuff.append("\nCreated On=" + _createdOn);
        strBuff.append("\nCreated By=" + _createdBy);
        strBuff.append("\nModified On" + _modifiedOn);
        strBuff.append("\nModified By=" + _modifiedBy);
        strBuff.append("\nLast Modified=" + _lastModified);
        strBuff.append("\nDelete Allowed=" + _deleteAlowed);
        return strBuff.toString();
    }

    /**
     * To get the value of createdBy field
     * 
     * @return createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * To set the value of createdBy field
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * To get the value of createdOn field
     * 
     * @return createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * To set the value of createdOn field
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * To get the value of lookupCode field
     * 
     * @return lookupCode.
     */
    public String getLookupCode() {
        return _lookupCode;
    }

    /**
     * To set the value of lookupCode field
     */
    public void setLookupCode(String lookupCode) {
        _lookupCode = lookupCode;
    }

    /**
     * To get the value of subCode field
     * 
     * @return subCode.
     */
    public String getSubCode() {
        return _subCode;
    }

    /**
     * To set the value of subCode field
     */
    public void setSubCode(String subCode) {
        _subCode = subCode;
    }

    /**
     * To get the value of lookupName field
     * 
     * @return lookupName.
     */
    public String getLookupName() {
        return _lookupName;
    }

    /**
     * To set the value of lookupName field
     */
    public void setLookupName(String lookupName) {
        _lookupName = lookupName;
    }

    /**
     * To get the value of lookupType field
     * 
     * @return lookupType.
     */
    public String getLookupType() {
        return _lookupType;
    }

    /**
     * To set the value of lookupType field
     */
    public void setLookupType(String lookupType) {
        _lookupType = lookupType;
    }

    /**
     * To get the value of modifiedBy field
     * 
     * @return modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * To set the value of modifiedBy field
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * To get the value of modifiedOn field
     * 
     * @return modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * To set the value of modifiedOn field
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * To get the value of status field
     * 
     * @return status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * To set the value of status field
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * To get the value of subLookupCode field
     * 
     * @return subLookupCode.
     */
    public String getSubLookupCode() {
        return _subLookupCode;
    }

    /**
     * To set the value of subLookupCode field
     */
    public void setSubLookupCode(String subLookupCode) {
        _subLookupCode = subLookupCode;
    }

    /**
     * To get the value of subLookupName field
     * 
     * @return subLookupName.
     */
    public String getSubLookupName() {
        return _subLookupName;
    }

    /**
     * To set the value of subLookupName field
     */
    public void setSubLookupName(String subLookupName) {
        _subLookupName = subLookupName;
    }

    /**
     * To get the value of lastModified field
     * 
     * @return lastModified.
     */
    public long getLastModified() {
        return _lastModified;
    }

    /**
     * To set the value of lastModified field
     */
    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
    }

    /**
     * @return Returns the deleteAlowed.
     */
    public String getDeleteAlowed() {
        return _deleteAlowed;
    }

    /**
     * @param deleteAlowed
     *            The deleteAlowed to set.
     */
    public void setDeleteAlowed(String deleteAlowed) {
        _deleteAlowed = deleteAlowed;
    }
    
    
    public boolean equalssubLookup(SubLookUpVO subLookUpVO) {
        boolean flag = false;
        if (subLookUpVO.getModifiedOn().equals(this.getModifiedOn())) {
            flag = true;
        }
        return flag;
    }
    
    public String logInfo() {

        StringBuffer sbf = new StringBuffer(100);

        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        sbf.append(startSeperator);
        sbf.append("Sub Lookup Code");
        sbf.append(middleSeperator);
        sbf.append(this.getSubLookupCode());

        sbf.append(startSeperator);
        sbf.append("Sub Lookup Name");
        sbf.append(middleSeperator);
        sbf.append(this.getSubLookupName());

        sbf.append(startSeperator);
        sbf.append("Status");
        sbf.append(middleSeperator);
        sbf.append(this.getStatus());

        return sbf.toString();
    }

    public String differences(SubLookUpVO subLookUpVO) {

        StringBuffer sbf = new StringBuffer(100);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        if (!BTSLUtil.isNullString(this.getSubLookupName()) && !BTSLUtil.isNullString(subLookUpVO.getSubLookupName()) && !BTSLUtil.compareLocaleString(this.getSubLookupName(), subLookUpVO.getSubLookupName())) {
            sbf.append(startSeperator);
            sbf.append("Name");
            sbf.append(middleSeperator);
            sbf.append(subLookUpVO.getSubLookupName());
            sbf.append(middleSeperator);
            sbf.append(this.getSubLookupName());
        }

        if (!BTSLUtil.isNullString(this.getStatus()) && !this.getStatus().equals(subLookUpVO.getStatus())) {
            sbf.append(startSeperator);
            sbf.append("Status");
            sbf.append(middleSeperator);
            sbf.append(subLookUpVO.getStatus());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());
        }

        return sbf.toString();
    }

}
