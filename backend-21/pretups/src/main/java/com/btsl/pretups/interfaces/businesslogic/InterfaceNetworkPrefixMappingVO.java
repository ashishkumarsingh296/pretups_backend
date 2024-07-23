package com.btsl.pretups.interfaces.businesslogic;

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
 *                                    Prefix Mapping
 * 
 */
public class InterfaceNetworkPrefixMappingVO implements Serializable {

    private String _networkCode;
    private String _interfaceID;
    private long _prefixID;
    private String _action;
    private String _methodType;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _series;
    private String _seriesType;

    private long _lastModifiedOn;

    public String toString() {
        StringBuffer sb = new StringBuffer("InterfaceNetworkMappingVO Data ");
        sb.append("_networkCode=" + _networkCode + ",");
        sb.append("_interfaceID=" + _interfaceID + ",");
        sb.append("_prefixID=" + _prefixID + ",");
        sb.append("_action=" + _action + ",");
        sb.append("_methodType=" + _methodType + ",");
        sb.append("_series=" + _series + ",");
        sb.append("_createdOn=" + _createdOn + ",");
        sb.append("_createdBy=" + _createdBy + ",");
        sb.append("_modifiedOn=" + _modifiedOn + ",");
        sb.append("_modifiedBy=" + _modifiedBy + ",");
        sb.append("_lastModifiedOn=" + _lastModifiedOn + ",");

        return sb.toString();
    }

    /**
     * @return Returns the action.
     */
    public String getAction() {
        return _action;
    }

    /**
     * @param action
     *            The action to set.
     */
    public void setAction(String action) {
        _action = action;
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
     * @return Returns the methodType.
     */
    public String getMethodType() {
        return _methodType;
    }

    /**
     * @param methodType
     *            The methodType to set.
     */
    public void setMethodType(String methodType) {
        _methodType = methodType;
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
     * @return Returns the prefixID.
     */
    public long getPrefixID() {
        return _prefixID;
    }

    /**
     * @param prefixID
     *            The prefixID to set.
     */
    public void setPrefixID(long prefixID) {
        _prefixID = prefixID;
    }

    /**
     * @return Returns the series.
     */
    public String getSeries() {
        return _series;
    }

    /**
     * @param series
     *            The series to set.
     */
    public void setSeries(String series) {
        _series = series;
    }

    public String getSeriesType() {
        return _seriesType;
    }

    public void setSeriesType(String seriesType) {
        _seriesType = seriesType;
    }

}
