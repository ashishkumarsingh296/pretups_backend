package com.btsl.pretups.master.businesslogic;

import java.util.Date;

public class ServiceTypeSelectorMappingVO {
    private String _sno;
    private String _serviceType;
    private String _senderSubscriberType;
    private String _receiverSubscriberType;
    private String _selectorCode;
    private String _selectorName;
    private String _status;
    private String _description;
    private String _createdBy;
    private String _modifiedBy;
    private Date _createdOn;
    private Date _modifiedOn;
    private String _type;
    private String _mappingType;
    private String _mappingStatus;
    private String _serviceName;
    private String _isDefaultCodeStr;
    private boolean _isDefaultCode;
    private String _senderBundleID;
    private String _receiverBundleID;
    private int _radioIndex;
    private String _statusDesc;

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

    public String getReceiverSubscriberType() {
        return _receiverSubscriberType;
    }

    public void setReceiverSubscriberType(String receiverSubscriberType) {
        _receiverSubscriberType = receiverSubscriberType;
    }

    public String getSelectorCode() {
        return _selectorCode;
    }

    public void setSelectorCode(String seclectorCode) {
        _selectorCode = seclectorCode;
    }

    public String getSelectorName() {
        return _selectorName;
    }

    public void setSelectorName(String seclectorName) {
        _selectorName = seclectorName;
    }

    public String getSenderSubscriberType() {
        return _senderSubscriberType;
    }

    public void setSenderSubscriberType(String senderSubscriberType) {
        _senderSubscriberType = senderSubscriberType;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public String getSno() {
        return _sno;
    }

    public void setSno(String sno) {
        _sno = sno;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getMappingType() {
        return _mappingType;
    }

    public void setMappingType(String mappingType) {
        _mappingType = mappingType;
    }

    public String getServiceName() {
        return _serviceName;
    }

    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public String getMappingStatus() {
        return _mappingStatus;
    }

    public void setMappingStatus(String mappingStatus) {
        _mappingStatus = mappingStatus;
    }

    public boolean isDefaultCode() {
        return _isDefaultCode;
    }

    public void setDefaultCode(boolean isDefaultCode) {
        _isDefaultCode = isDefaultCode;
    }

    public String getIsDefaultCodeStr() {
        return _isDefaultCodeStr;
    }

    public void setIsDefaultCodeStr(String isDefaultCodeStr) {
        _isDefaultCodeStr = isDefaultCodeStr;
    }

    /**
     * @return Returns the receiverBundleID.
     */
    public String getReceiverBundleID() {
        return _receiverBundleID;
    }

    /**
     * @param receiverBundleID
     *            The receiverBundleID to set.
     */
    public void setReceiverBundleID(String receiverBundleID) {
        _receiverBundleID = receiverBundleID;
    }

    /**
     * @return Returns the senderBundleID.
     */
    public String getSenderBundleID() {
        return _senderBundleID;
    }

    /**
     * @param senderBundleID
     *            The senderBundleID to set.
     */
    public void setSenderBundleID(String senderBundleID) {
        _senderBundleID = senderBundleID;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("");
        sb.append("_sno=" + _sno + ", ");
        sb.append("_serviceType=" + _serviceType + ", ");
        sb.append("_selectorCode=" + _selectorCode + ", ");
        sb.append("_selectorName=" + _selectorName + ", ");
        sb.append("_status=" + _status + ", ");
        sb.append("_type=" + _type + ", ");
        sb.append("_isDefaultCode=" + _isDefaultCode + ", ");
        sb.append("_senderSubscriberType=" + _senderSubscriberType + ", ");
        sb.append("_receiverSubscriberType=" + _receiverSubscriberType + "");

        return sb.toString();
    }

    public int getRadioIndex() {
        return _radioIndex;
    }

    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    public String getStatusDesc() {
        return _statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
    }

}
