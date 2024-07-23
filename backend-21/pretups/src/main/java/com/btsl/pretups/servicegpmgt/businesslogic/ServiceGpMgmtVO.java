package com.btsl.pretups.servicegpmgt.businesslogic;

public class ServiceGpMgmtVO {

    private String _groupId;
    private String _groupName;
    private String _groupCode;
    private String _status;
    private String _createdOn;
    private String _createdBy;
    private String _modifiedOn;
    private String _modifiedBy;
    private String _networkCode;
    private String _radioIndex = "0";
    private String _statusDescription;

    private String _serviceName;
    private String _actionType;
    private String _fileName;
    private String _recordNumber;

    private String _newGroupId;
    private String _modstatus;

    public String toString() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("\n Group Id=" + _groupId);
        strBuff.append("\n Group Name=" + _groupName);
        strBuff.append("\n Group Code=" + _groupCode);
        strBuff.append("\n Created On=" + _createdOn);
        strBuff.append("\n Status =" + _status);
        strBuff.append("\n Created By=" + _createdBy);
        strBuff.append("\n Modified On=" + _modifiedOn);
        strBuff.append("\n Modified By=" + _modifiedBy);
        strBuff.append("\n Network Code=" + _networkCode);
        strBuff.append("\n Status Description=" + _statusDescription);
        strBuff.append("\n Service Name=" + _serviceName);
        strBuff.append("\n Action Type=" + _actionType);
        strBuff.append("\n File Name=" + _fileName);
        strBuff.append("\n Record Number=" + _recordNumber);
        strBuff.append("\n New Group Id=" + _newGroupId);
        strBuff.append("\n Modstatus=" + _modstatus);
        return strBuff.toString();
    }

    public String getActionType() {
        return _actionType;
    }

    public void setActionType(String type) {
        _actionType = type;
    }

    public String getServiceName() {
        return _serviceName;
    }

    public void setServiceName(String name) {
        _serviceName = name;
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String name) {
        _fileName = name;
    }

    public String getRecordNumber() {
        return _recordNumber;
    }

    public void setRecordNumber(String number) {
        _recordNumber = number;
    }

    public String getStatusDescription() {
        return _statusDescription;
    }

    public void setStatusDescription(String description) {
        _statusDescription = description;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    public String getGroupId() {
        return _groupId;
    }

    public void setGroupId(String groupId) {
        _groupId = groupId;
    }

    public String getGroupName() {
        return _groupName;
    }

    public void setGroupName(String groupName) {
        _groupName = groupName;
    }

    public String getGroupCode() {
        return _groupCode;
    }

    public void setGroupCode(String groupCode) {
        _groupCode = groupCode;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(String createdOn) {
        _createdOn = createdOn;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public String getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public String getRadioIndex() {
        return _radioIndex;
    }

    public void setRadioIndex(String index) {
        _radioIndex = index;
    }

    public String getCombinedKey() {
        return _groupId + ":" + _groupCode;
    }

    public String getNewGroupId() {
        return _newGroupId;
    }

    public void setNewGroupId(String groupId) {
        _newGroupId = groupId;
    }

    public String getModstatus() {
        return _modstatus;
    }

    public void setModstatus(String _modstatus) {
        this._modstatus = _modstatus;
    }

}
