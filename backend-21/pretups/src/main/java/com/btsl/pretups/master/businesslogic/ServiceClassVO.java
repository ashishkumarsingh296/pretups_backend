/*
 * #ServiceClassVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 13, 2005 amit.ruwali Initial creation
 * Nov 18,2005 Sandeep Goel Customization
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class ServiceClassVO implements Serializable {
    private String _serviceClassId;
    private String _interfaceCode;
    private String _interfaceName;
    private String _serviceClassCode;
    private String _serviceClassName;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private int _radioIndex;
    private long _lastModified;
    private String _networkCode;
    private String _interfaceCategory;
    private String _status;
    private String _statusName;
    private String _p2pSenderSuspend;
    private String _p2pReceiverSuspend;
    private String _c2sReceiverSuspend;
    private String _p2pSenderAllowedStatus;
    private String _p2pSenderDeniedStatus;
    private String _p2pReceiverAllowedStatus;
    private String _p2pReceiverDeniedStatus;
    private String _c2sReceiverAllowedStatus;
    private String _c2sReceiverDeniedStatus;

    public String toString() {
        StringBuilder strBuild = new StringBuilder("\nService Class Id=" + _serviceClassId);
        strBuild.append("Service Class Code=").append(_serviceClassCode);
        strBuild.append("Service Class Name=").append(_serviceClassName);
        strBuild.append("Interface Code=").append(_interfaceCode);
        strBuild.append("Interface Name=").append(_interfaceName);
        strBuild.append("Created On=").append(_createdOn);
        strBuild.append("Created By=").append(_createdBy);
        strBuild.append("Modified On=").append(_modifiedOn);
        strBuild.append("Modified By=").append(_modifiedBy);
        strBuild.append("Created On=").append(_createdOn);
        strBuild.append("Radio Index=").append(_radioIndex);
        strBuild.append("Created On=").append(_createdOn);
        strBuild.append("Last Modified=").append(_lastModified);
        strBuild.append("networkCode=").append(_networkCode);
        strBuild.append("interfaceCategory=").append(_interfaceCategory);
        strBuild.append("Status=").append(_status);
        strBuild.append(" _p2pReceiverAllowedStatus=").append(_p2pReceiverAllowedStatus);
        strBuild.append(" _p2pReceiverDeniedStatus=").append(_p2pReceiverDeniedStatus);
        strBuild.append(" _p2pReceiverSuspend=").append(_p2pReceiverSuspend);
        strBuild.append(" _p2pSenderAllowedStatus=").append(_p2pSenderAllowedStatus);
        strBuild.append(" _p2pSenderDeniedStatus=").append(_p2pSenderDeniedStatus);
        strBuild.append(" _p2pSenderSuspend=").append(_p2pSenderSuspend);
        strBuild.append(" _c2sReceiverAllowedStatus=").append(_c2sReceiverAllowedStatus);
        strBuild.append(" _c2sReceiverDeniedStatus=").append(_c2sReceiverDeniedStatus);
        strBuild.append(" _c2sReceiverSuspend=").append(_c2sReceiverSuspend);
        return strBuild.toString();
    }

    /**
     * @return Returns the statusName.
     */
    public String getStatusName() {
        return _statusName;
    }

    /**
     * @param statusName
     *            The statusName to set.
     */
    public void setStatusName(String statusName) {
        _statusName = statusName;
    }

    public String getInterfaceCategory() {
        return _interfaceCategory;
    }

    public void setInterfaceCategory(String interfaceCategory) {
        _interfaceCategory = interfaceCategory;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * To get the value of serviceClassId field
     * 
     * @return serviceClassId.
     */
    public String getServiceClassId() {
        return _serviceClassId;
    }

    /**
     * To set the value of serviceClassId field
     */
    public void setServiceClassId(String serviceClassId) {
        _serviceClassId = serviceClassId;
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
     * To get the value of interfaceCode field
     * 
     * @return interfaceCode.
     */
    public String getInterfaceCode() {
        return _interfaceCode;
    }

    /**
     * To set the value of interfaceCode field
     */
    public void setInterfaceCode(String interfaceCode) {
        _interfaceCode = interfaceCode;
    }

    /**
     * To get the value of interfaceName field
     * 
     * @return interfaceName.
     */
    public String getInterfaceName() {
        return _interfaceName;
    }

    /**
     * To set the value of interfaceName field
     */
    public void setInterfaceName(String interfaceName) {
        _interfaceName = interfaceName;
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
     * To get the value of serviceClassCode field
     * 
     * @return serviceClassCode.
     */
    public String getServiceClassCode() {
        return _serviceClassCode;
    }

    /**
     * To set the value of serviceClassCode field
     */
    public void setServiceClassCode(String serviceClassCode) {
        _serviceClassCode = serviceClassCode;
    }

    /**
     * To get the value of serviceClassName field
     * 
     * @return serviceClassName.
     */
    public String getServiceClassName() {
        return _serviceClassName;
    }

    /**
     * To set the value of serviceClassName field
     */
    public void setServiceClassName(String serviceClassName) {
        _serviceClassName = serviceClassName;
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

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getC2sReceiverAllowedStatus() {
        return _c2sReceiverAllowedStatus;
    }

    public void setC2sReceiverAllowedStatus(String receiverAllowedStatus) {
        _c2sReceiverAllowedStatus = receiverAllowedStatus;
    }

    public String getC2sReceiverDeniedStatus() {
        return _c2sReceiverDeniedStatus;
    }

    public void setC2sReceiverDeniedStatus(String receiverDeniedStatus) {
        _c2sReceiverDeniedStatus = receiverDeniedStatus;
    }

    public String getC2sReceiverSuspend() {
        return _c2sReceiverSuspend;
    }

    public void setC2sReceiverSuspend(String receiverSuspend) {
        _c2sReceiverSuspend = receiverSuspend;
    }

    public String getP2pReceiverAllowedStatus() {
        return _p2pReceiverAllowedStatus;
    }

    public void setP2pReceiverAllowedStatus(String receiverAllowedStatus) {
        _p2pReceiverAllowedStatus = receiverAllowedStatus;
    }

    public String getP2pReceiverDeniedStatus() {
        return _p2pReceiverDeniedStatus;
    }

    public void setP2pReceiverDeniedStatus(String receiverDeinedStatus) {
        _p2pReceiverDeniedStatus = receiverDeinedStatus;
    }

    public String getP2pReceiverSuspend() {
        return _p2pReceiverSuspend;
    }

    public void setP2pReceiverSuspend(String receiverSuspend) {
        _p2pReceiverSuspend = receiverSuspend;
    }

    public String getP2pSenderAllowedStatus() {
        return _p2pSenderAllowedStatus;
    }

    public void setP2pSenderAllowedStatus(String senderAllowedStatus) {
        _p2pSenderAllowedStatus = senderAllowedStatus;
    }

    public String getP2pSenderDeniedStatus() {
        return _p2pSenderDeniedStatus;
    }

    public void setP2pSenderDeniedStatus(String senderDeniedStatus) {
        _p2pSenderDeniedStatus = senderDeniedStatus;
    }

    public String getP2pSenderSuspend() {
        return _p2pSenderSuspend;
    }

    public void setP2pSenderSuspend(String senderSuspend) {
        _p2pSenderSuspend = senderSuspend;
    }

}
