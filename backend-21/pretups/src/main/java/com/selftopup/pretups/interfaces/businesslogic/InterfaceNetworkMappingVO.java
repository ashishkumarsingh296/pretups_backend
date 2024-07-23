package com.selftopup.pretups.interfaces.businesslogic;

import java.io.Serializable;
import java.util.Date;

/**
 * @(#)InterfaceNetworkMappingVO.java
 *                                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                    All Rights Reserved
 * 
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Mohit Goel 21/09/2005 Initial Creation
 * 
 *                                    This class is used for Interface Network
 *                                    Mapping
 * 
 */
public class InterfaceNetworkMappingVO implements Serializable {

    private String _networkCode;
    private String _interfaceName;
    private String _interfaceCategoryID;
    private String _interfaceCategoryIDDesc;
    private String _interfaceID;
    private String _interfaceIDDesc;
    private long _queueSize;
    private long _queueTimeOut;
    private long _requestTimeOut;
    private long _nextCheckQueueReqSec;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;

    private long _lastModifiedOn;

    // defined for associateInterfacePrefix.jsp
    private String _validatePrepaidSeries;
    private String _validatePostpaidSeries;
    private String _updatePrepaidSeries;
    private String _updatePostpaidSeries;

    public String toString() {
        StringBuffer sb = new StringBuffer("InterfaceNetworkMappingVO Data ");
        sb.append("_networkCode=" + _networkCode + ",");
        sb.append("_interfaceCategoryID=" + _interfaceCategoryID + ",");
        sb.append("_interfaceID=" + _interfaceID + ",");
        sb.append("_queueSize=" + _queueSize + ",");
        sb.append("_queueTimeOut=" + _queueTimeOut + ",");
        sb.append("_requestTimeOut=" + _requestTimeOut + ",");
        sb.append("nextCheckQueueReqSec=" + _nextCheckQueueReqSec + ",");
        sb.append("_createdOn=" + _createdOn + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_modifiedOn=" + _modifiedOn + ",");
        sb.append("_modifiedBy=" + _modifiedBy + ",");
        sb.append("_lastModifiedOn=" + _lastModifiedOn + ",");

        sb.append("_validatePrepaidSeries=" + _validatePrepaidSeries + ",");
        sb.append("_validatePostpaidSeries=" + _validatePostpaidSeries + ",");
        sb.append("_updatePrepaidSeries=" + _updatePrepaidSeries + ",");
        sb.append("_updatePostpaidSeries=" + _updatePostpaidSeries + ",");

        return sb.toString();
    }

    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the interfaceCategoryID.
     */
    public String getInterfaceCategoryID() {
        return _interfaceCategoryID;
    }

    /**
     * @param interfaceCategoryID
     *            The interfaceCategoryID to set.
     */
    public void setInterfaceCategoryID(String interfaceCategoryID) {
        _interfaceCategoryID = interfaceCategoryID;
    }

    /**
     * @return Returns the interfaceCategoryIDDesc.
     */
    public String getInterfaceCategoryIDDesc() {
        return _interfaceCategoryIDDesc;
    }

    /**
     * @param interfaceCategoryIDDesc
     *            The interfaceCategoryIDDesc to set.
     */
    public void setInterfaceCategoryIDDesc(String interfaceCategoryIDDesc) {
        _interfaceCategoryIDDesc = interfaceCategoryIDDesc;
    }

    /**
     * @return Returns the interfaceID.
     */
    public String getInterfaceID() {
        return _interfaceID;
    }

    /**
     * @param interfaceID
     *            The interfaceID to set.
     */
    public void setInterfaceID(String interfaceID) {
        _interfaceID = interfaceID;
    }

    /**
     * @return Returns the interfaceIDDesc.
     */
    public String getInterfaceIDDesc() {
        return _interfaceIDDesc;
    }

    /**
     * @param interfaceIDDesc
     *            The interfaceIDDesc to set.
     */
    public void setInterfaceIDDesc(String interfaceIDDesc) {
        _interfaceIDDesc = interfaceIDDesc;
    }

    /**
     * @return Returns the modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param modifiedBy
     *            The modifiedBy to set.
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * @return Returns the modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOn
     *            The modifiedOn to set.
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the networkName.
     */
    public String getInterfaceName() {
        return _interfaceName;
    }

    /**
     * @param networkName
     *            The networkName to set.
     */
    public void setInterfaceName(String networkName) {
        _interfaceName = networkName;
    }

    /**
     * @return Returns the queueSize.
     */
    public long getQueueSize() {
        return _queueSize;
    }

    /**
     * @param queueSize
     *            The queueSize to set.
     */
    public void setQueueSize(long queueSize) {
        _queueSize = queueSize;
    }

    /**
     * @return Returns the queueTimeOut.
     */
    public long getQueueTimeOut() {
        return _queueTimeOut;
    }

    /**
     * @param queueTimeOut
     *            The queueTimeOut to set.
     */
    public void setQueueTimeOut(long queueTimeOut) {
        _queueTimeOut = queueTimeOut;
    }

    /**
     * @return Returns the requestTimeOut.
     */
    public long getRequestTimeOut() {
        return _requestTimeOut;
    }

    /**
     * @param requestTimeOut
     *            The requestTimeOut to set.
     */
    public void setRequestTimeOut(long requestTimeOut) {
        _requestTimeOut = requestTimeOut;
    }

    /**
     * @return Returns the nextCheckQueueReqSec.
     */
    public long getNextCheckQueueReqSec() {
        return _nextCheckQueueReqSec;
    }

    /**
     * @param nextCheckQueueReqSec
     *            The nextCheckQueueReqSec to set.
     */
    public void setNextCheckQueueReqSec(long nextCheckQueueReqSec) {
        this._nextCheckQueueReqSec = nextCheckQueueReqSec;
    }

    /**
     * @return Returns the lastModifiedOn.
     */
    public long getLastModifiedOn() {
        return _lastModifiedOn;
    }

    /**
     * @param lastModifiedOn
     *            The lastModifiedOn to set.
     */
    public void setLastModifiedOn(long lastModifiedOn) {
        _lastModifiedOn = lastModifiedOn;
    }

    /**
     * @return Returns the updatePostpaidSeries.
     */
    public String getUpdatePostpaidSeries() {
        return _updatePostpaidSeries;
    }

    /**
     * @param updatePostpaidSeries
     *            The updatePostpaidSeries to set.
     */
    public void setUpdatePostpaidSeries(String updatePostpaidSeries) {
        if (updatePostpaidSeries != null)
            _updatePostpaidSeries = updatePostpaidSeries.trim();
        else
            _updatePostpaidSeries = updatePostpaidSeries;
    }

    /**
     * @return Returns the updatePrepaidSeries.
     */
    public String getUpdatePrepaidSeries() {
        return _updatePrepaidSeries;
    }

    /**
     * @param updatePrepaidSeries
     *            The updatePrepaidSeries to set.
     */
    public void setUpdatePrepaidSeries(String updatePrepaidSeries) {
        if (updatePrepaidSeries != null)
            _updatePrepaidSeries = updatePrepaidSeries.trim();
        else
            _updatePrepaidSeries = updatePrepaidSeries;
    }

    /**
     * @return Returns the validatePostpaidSeries.
     */
    public String getValidatePostpaidSeries() {
        return _validatePostpaidSeries;
    }

    /**
     * @param validatePostpaidSeries
     *            The validatePostpaidSeries to set.
     */
    public void setValidatePostpaidSeries(String validatePostpaidSeries) {
        if (validatePostpaidSeries != null)
            _validatePostpaidSeries = validatePostpaidSeries.trim();
        else
            _validatePostpaidSeries = validatePostpaidSeries;
    }

    /**
     * @return Returns the validatePrepaidSeries.
     */
    public String getValidatePrepaidSeries() {
        return _validatePrepaidSeries;
    }

    /**
     * @param validatePrepaidSeries
     *            The validatePrepaidSeries to set.
     */
    public void setValidatePrepaidSeries(String validatePrepaidSeries) {
        if (validatePrepaidSeries != null)
            _validatePrepaidSeries = validatePrepaidSeries.trim();
        else
            _validatePrepaidSeries = validatePrepaidSeries;
    }
}
