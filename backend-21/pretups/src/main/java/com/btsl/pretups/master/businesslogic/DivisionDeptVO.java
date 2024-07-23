/*
 * #DivisionDeptVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 4, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class DivisionDeptVO implements Serializable {
    private String _divDeptId;
    private String _divDeptName;
    private String _divDeptShortCode;
    private String _status;
    private String _statusName;
    private String _divDeptType;
    private String _divDeptTypeName;
    private String _divDept;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _parentId;
    private String _networkCode;
    private String _networkName;
    private String _userId;
    private long _lastModified;
    private int _radioIndex;

    public String toString() {
        StringBuffer strBuff = new StringBuffer("\n divDeptId Id:" + _divDeptId);
        strBuff.append("\n _divDeptName :" + _divDeptName);
        strBuff.append("\n _divDeptShortCode:" + _divDeptShortCode);
        strBuff.append("\n status:" + _status);
        strBuff.append("\n status Name:" + _statusName);
        strBuff.append("\n divDeptType:" + _divDeptType);
        strBuff.append("\n divDeptTypeName:" + _divDeptTypeName);
        strBuff.append("\n _divDept :" + _divDept);
        strBuff.append("\nDivision/Department Created On:" + _createdOn);
        strBuff.append("\nDivision/Department Created By:" + _createdBy);
        strBuff.append("\nDivision/Department Modified On:" + _modifiedOn);
        strBuff.append("\nDivision/Department Modified By:" + _modifiedBy);
        strBuff.append("\nDivision/Department Parent Id:" + _parentId);
        strBuff.append("\nDivision/Department Network Code:" + _networkCode);
        strBuff.append("\nDivision/Department Network Name:" + _networkName);
        strBuff.append("\nDivision/Department User ID:" + _userId);
        strBuff.append("\nDivision/Department Last Modified:" + _lastModified);
        return strBuff.toString();
    }

    /**
     * To get the value of divDeptTypeName field
     * 
     * @return divDeptTypeName.
     */
    public String getDivDeptTypeName() {
        return _divDeptTypeName;
    }

    /**
     * To set the value of divDeptTypeName field
     */
    public void setDivDeptTypeName(String divDeptTypeName) {
        _divDeptTypeName = divDeptTypeName;
    }

    /**
     * To get the value of statusName field
     * 
     * @return statusName.
     */
    public String getStatusName() {
        return _statusName;
    }

    /**
     * To set the value of statusName field
     */
    public void setStatusName(String statusName) {
        _statusName = statusName;
    }

    /**
     * To get the value of networkName field
     * 
     * @return networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * To set the value of networkName field
     */
    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    /**
     * To get the value of radioIndex field
     * 
     * @return radioIndex.
     */
    public int getRadioIndex() {
        return _radioIndex;
    }

    /**
     * To set the value of radioIndex field
     */
    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    /**
     * To get the value of divDeptType field
     * 
     * @return divDeptType.
     */
    public String getDivDeptType() {
        return _divDeptType;
    }

    /**
     * To set the value of divDeptType field
     */
    public void setDivDeptType(String divDeptType) {
        _divDeptType = divDeptType;
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
     * To get the value of divDept field
     * 
     * @return divDept.
     */
    public String getDivDept() {
        return _divDept;
    }

    /**
     * To set the value of divDept field
     */
    public void setDivDept(String divDept) {
        _divDept = divDept;
    }

    /**
     * To get the value of divDeptId field
     * 
     * @return divDeptId.
     */
    public String getDivDeptId() {
        return _divDeptId;
    }

    /**
     * To set the value of divDeptId field
     */
    public void setDivDeptId(String divDeptId) {
        _divDeptId = divDeptId;
    }

    /**
     * To get the value of divDeptName field
     * 
     * @return divDeptName.
     */
    public String getDivDeptName() {
        return _divDeptName;
    }

    /**
     * To set the value of divDeptName field
     */
    public void setDivDeptName(String divDeptName) {
        _divDeptName = divDeptName;
    }

    /**
     * To get the value of divDeptShortCode field
     * 
     * @return divDeptShortCode.
     */
    public String getDivDeptShortCode() {
        return _divDeptShortCode;
    }

    /**
     * To set the value of divDeptShortCode field
     */
    public void setDivDeptShortCode(String divDeptShortCode) {
        _divDeptShortCode = divDeptShortCode;
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
     * To get the value of networkCode field
     * 
     * @return networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * To set the value of networkCode field
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * To get the value of parentId field
     * 
     * @return parentId.
     */
    public String getParentId() {
        return _parentId;
    }

    /**
     * To set the value of parentId field
     */
    public void setParentId(String parentId) {
        _parentId = parentId;
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
     * To get the value of userId field
     * 
     * @return userId.
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * To set the value of userId field
     */
    public void setUserId(String userId) {
        _userId = userId;
    }
}
