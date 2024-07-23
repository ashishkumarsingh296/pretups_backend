package com.btsl.pretups.vastrix.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;

public class ServiceSelectorInterfaceMappingVO extends MSISDNPrefixInterfaceMappingVO implements Serializable {

    private String _serviceType;
    private String _serviceName;
    private String _selectorCode;
    private String _selectorName;
    private String _interfaceTypeId;
    private String _interfaceName;
    private int _prefixID;
    private String _methodType;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _rowID;
    private String _validateSeries;
    private String _updateSeries;
    private Long _validatePrefixID;
    private Long _updatePrefixID;
    private String _multiBox;
    private String _serviceInterfaceMappngID;
    private long _lastModifiedTime = 0;
    /* new added */
    private String _interfaceCategory;
    private String _validatePrepaidSeries = null;
    private String _validatePostpaidSeries = null;
    private String _updatePrepaidSeries = null;
    private String _updatePostpaidSeries = null;
    private String _interfaceCategoryID;
    private String _interfaceCategoryIDDesc;
    private String _interfaceIDDesc;
    private String _prefixSeries;

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

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long modifiedTime) {
        _lastModifiedTime = modifiedTime;
    }

    public String getServiceInterfaceMappngID() {
        return _serviceInterfaceMappngID;
    }

    public void setServiceInterfaceMappngID(String interfaceMappngID) {
        _serviceInterfaceMappngID = interfaceMappngID;
    }

    public String getMultiBox() {
        return _multiBox;
    }

    public void setMultiBox(String box) {
        _multiBox = box;
    }

    public Long getUpdatePrefixID() {
        return _updatePrefixID;
    }

    public void setUpdatePrefixID(Long prefixID) {
        _updatePrefixID = prefixID;
    }

    public Long getValidatePrefixID() {
        return _validatePrefixID;
    }

    public void setValidatePrefixID(Long prefixID) {
        _validatePrefixID = prefixID;
    }

    public String getUpdateSeries() {
        return _updateSeries;
    }

    public void setUpdateSeries(String series) {
        _updateSeries = series;
    }

    public String getValidateSeries() {
        return _validateSeries;
    }

    public void setValidateSeries(String series) {
        _validateSeries = series;
    }

    public String getRowID() {
        return _rowID;
    }

    public void setRowID(String _rowid) {
        _rowID = _rowid;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String by) {
        _createdBy = by;
    }

    public String getMethodType() {
        return _methodType;
    }

    public void setMethodType(String type) {
        _methodType = type;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String by) {
        _modifiedBy = by;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date on) {
        _createdOn = on;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date on) {
        _modifiedOn = on;
    }

    public long getPrefixId() {
        return _prefixID;
    }

    public void setPrefixID(int _prefixid) {
        _prefixID = _prefixid;
    }

    public String getSelectorCode() {
        return _selectorCode;
    }

    public void setSelectorCode(String _selector_code) {
        this._selectorCode = _selector_code;
    }

    public String getServiceName() {
        return _serviceName;
    }

    public void setServiceName(String name) {
        _serviceName = name;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String type) {
        _serviceType = type;
    }

    public String getInterfaceName() {
        return _interfaceName;
    }

    public void setInterfaceName(String name) {
        _interfaceName = name;
    }

    public String getSelectorName() {
        return _selectorName;
    }

    public void setSelectorName(String name) {
        _selectorName = name;
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
        if (updatePostpaidSeries != null) {
            _updatePostpaidSeries = updatePostpaidSeries.trim();
        }
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
        if (updatePrepaidSeries != null) {
            _updatePrepaidSeries = updatePrepaidSeries.trim();
        }
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
        if (validatePostpaidSeries != null) {
            _validatePostpaidSeries = validatePostpaidSeries.trim();
        }
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
        if (validatePrepaidSeries != null) {
            _validatePrepaidSeries = validatePrepaidSeries.trim();
        }
    }

    public String getInterfaceCategory() {
        return _interfaceCategory;
    }

    public void setInterfaceCategory(String category) {
        _interfaceCategory = category;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("ServiceSelectorInterfaceMappingVO Data ");
        sb.append("_validatePrepaidSeries=" + _validatePrepaidSeries + ",");
        sb.append("_validatePostpaidSeries=" + _validatePostpaidSeries + ",");
        sb.append("_updatePrepaidSeries=" + _updatePrepaidSeries + ",");
        sb.append("_updatePostpaidSeries=" + _updatePostpaidSeries + ",");

        return sb.toString();
    }

    public String getPrefixSeries() {
        return _prefixSeries;
    }

    public void setPrefixSeries(String series) {
        _prefixSeries = series;
    }

}
