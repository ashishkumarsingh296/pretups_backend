/*
 * @# GeographicalDomainVO.java
 * This is VO class for the Geographical Domain Module
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 26, 2005 Sandeep Goel Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.businesslogic;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 */

public class GeographicalDomainVO implements Serializable {

    private String _grphDomainCode;
    private String _networkCode;
    private String _grphDomainName;
    private String _parentDomainCode;
    private String _grphDomainShortName;
    private String _description;
    private String _status;
    private String _grphDomainType;
    private String _createdBy;
    private Date _createdOn = null;
    private String _modifiedBy;
    private Date _modifiedOn = null;

    private long _lastModifiedTime = 0;
    private String _parentDomainName;
    private String _statusDescription;
    private String _isDefault = "N";
    private String _isDefaultDesc;

    private String _categoryCode;
    private String _serviceAllowed;

    public GeographicalDomainVO() {

    }

    public String getCombinedKey() {
        return _grphDomainCode + ":" + _grphDomainType + ":";
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

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getGrphDomainCode() {
        return _grphDomainCode;
    }

    public void setGrphDomainCode(String grphDomainCode) {
        _grphDomainCode = grphDomainCode;
    }

    public String getGrphDomainName() {
        return _grphDomainName;
    }

    public void setGrphDomainName(String grphDomainName) {
        _grphDomainName = grphDomainName;
    }

    public String getGrphDomainShortName() {
        return _grphDomainShortName;
    }

    public void setGrphDomainShortName(String grphDomainShortName) {
        _grphDomainShortName = grphDomainShortName;
    }

    public String getGrphDomainType() {
        return _grphDomainType;
    }

    public void setGrphDomainType(String grphDomainType) {
        _grphDomainType = grphDomainType;
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

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedOn) {
        _lastModifiedTime = lastModifiedOn;
    }

    public String getParentDomainName() {
        return _parentDomainName;
    }

    public void setParentDomainName(String parentDomainName) {
        _parentDomainName = parentDomainName;
    }

    public String getParentDomainCode() {
        return _parentDomainCode;
    }

    public void setParentDomainCode(String parentDomainCode) {
        _parentDomainCode = parentDomainCode;
    }

    public String getStatusDescription() {
        return _statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        _statusDescription = statusDescription;
    }

    public String getParentDomainNameWithCode() {
        return this._parentDomainName + "(" + this._parentDomainCode + ")";
    }

    public void setcategoryCode(String catCode) {
        _categoryCode = catCode;
    }

    public String getcategoryCode() {
        return _categoryCode;
    }

   /*Removed getgrphDomainName and setgrphDomainName by jasho */
  /*  public String getgrphDomainName() {
        return _grphDomainName;
    }

    public void setgrphDomainName(String geographyName) {
        _grphDomainName = geographyName;
    }
 */
    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("createdBy=" + _createdBy);
        sbf.append(",createdOn=" + _createdOn);
        sbf.append(",description=" + _description);
        sbf.append(",_grphDomainCode=" + _grphDomainCode);
        sbf.append(",_grphDomainName=" + _grphDomainName);
        sbf.append(",_grphDomainShortName=" + _grphDomainShortName);
        sbf.append(",_grphDomainType=" + _grphDomainType);
        sbf.append(",_lastModifiedTime=" + _lastModifiedTime);
        sbf.append(",_modifiedBy=" + _modifiedBy);
        sbf.append(",_modifiedOn=" + _modifiedOn);
        sbf.append(",_networkCode=" + _networkCode);
        sbf.append(",_parentDomainCode=" + _parentDomainCode);
        sbf.append(",_parentDomainName=" + _parentDomainName);
        sbf.append(",_status=" + _status);
        sbf.append(",_statusDescription=" + _statusDescription);
        return sbf.toString();
    }

    /**
     * @return the isDefault
     */
    public String getIsDefault() {
        return _isDefault;
    }

    /**
     * @param isDefault
     *            the isDefault to set
     */
    public void setIsDefault(String isDefault) {
        _isDefault = isDefault;
    }

    /**
     * @return the isDefaultDesc
     */
    public String getIsDefaultDesc() {
        return _isDefaultDesc;
    }

    /**
     * @param isDefaultDesc
     *            the isDefaultDesc to set
     */
    public void setIsDefaultDesc(String isDefaultDesc) {
        _isDefaultDesc = isDefaultDesc;
    }

    public String getCombinedCategoryKey() {
        return _grphDomainCode + ":" + _categoryCode + ":" + _grphDomainName;
    }

    public String getServiceAllowed() {
        return _serviceAllowed;
    }

    public void setServiceAllowed(String serviceAllowed) {
        _serviceAllowed = serviceAllowed;
    }

    public static GeographicalDomainVO getInstance(){
    	return new GeographicalDomainVO();
    }
}
