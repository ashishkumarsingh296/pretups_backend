package com.selftopup.pretups.master.businesslogic;

/*
 * ServiceSelectorMappingVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/05/2007 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 * Service Type Selector Mapping Transfer Object
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ServiceSelectorMappingVO implements Serializable {
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
    private String _amountStr = null;
    private ArrayList selectorCount;
    private boolean _modifiedAllowed = true;
    private int _displayOrderList;
    private String _displayOrder;
    private ArrayList _newOrderList = null;
    private String _newOrder;
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

    /**
     * @return the amountStr
     */
    public String getAmountStr() {
        return _amountStr;
    }

    /**
     * @param amountStr
     *            the amountStr to set
     */
    public void setAmountStr(String amountStr) {
        _amountStr = amountStr;
    }

    /**
     * @return the modifiedAllowed
     */
    public boolean isModifiedAllowed() {
        return _modifiedAllowed;
    }

    /**
     * @param modifiedAllowed
     *            the modifiedAllowed to set
     */
    public void setModifiedAllowed(boolean modifiedAllowed) {
        _modifiedAllowed = modifiedAllowed;
    }

    // vastrix

    /**
     * @return the selectorCount
     */
    public ArrayList getSelectorCount() {
        return selectorCount;
    }

    /**
     * @param selectorCount
     *            the selectorCount to set
     */
    public void setSelectorCount(ArrayList selectorCount) {
        this.selectorCount = selectorCount;
    }

    public int getDisplayOrderList() {
        return _displayOrderList;
    }

    public void setDisplayOrderList(int orderList) {
        _displayOrderList = orderList;
    }

    public String getDisplayOrder() {
        return _displayOrder;
    }

    public void setDisplayOrder(String order) {
        _displayOrder = order;
    }

    public ArrayList getNewOrderList() {
        return _newOrderList;
    }

    public void setNewOrderList(ArrayList orderList) {
        _newOrderList = orderList;
    }

    public String getNewOrder() {
        return _newOrder;
    }

    public void setNewOrder(String order) {
        _newOrder = order;
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

    public String toString() {
        StringBuffer sb = new StringBuffer("");
        sb.append("_serviceType=" + _serviceType + ",");
        sb.append("_selectorCode=" + _selectorCode + ",");
        sb.append("_status=" + _status + ",");
        sb.append("_type=" + _type + ",");
        sb.append("_isDefaultCode=" + _isDefaultCode + ",");
        sb.append("_senderBundleID=" + _senderBundleID + ",");
        sb.append("_receiverBundleID=" + _receiverBundleID + ",");
        sb.append("_amountStr=" + _amountStr + ",");
        sb.append("_modifiedAllowed=" + _modifiedAllowed + ",");
        sb.append("_displayOrder=" + _displayOrder + "");
        sb.append("_statusDesc=" + _statusDesc + "");
        return sb.toString();
    }
}
