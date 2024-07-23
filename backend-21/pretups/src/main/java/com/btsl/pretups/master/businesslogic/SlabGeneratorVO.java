/*
 * #SlabGeneratorVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Dec 26, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.master.businesslogic;

import java.util.Date;

public class SlabGeneratorVO {
private String _slabid;
private double _toRange;
private double _fromRange;
private String _toRangeAsString;
    private String _fromRangeAsString;
    private String _serviceType;
    private String _slabDate;
    private Date _slabDateAsDate;
    private int _radioIndex;
    private String _networkCode;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;
    private String _radioRequired;
    private String _slabDateCode;

    /**
     * @return Returns the slabDateCode.
     */
    public String getSlabDateCode() {
        return _slabDateCode;
    }

    /**
     * @param slabDateCode
     *            The slabDateCode to set.
     */
    public void setSlabDateCode(String slabDateCode) {
        _slabDateCode = slabDateCode;
    }

    /**
     * @return Returns the slabDateAsDate.
     */
    public Date getSlabDateAsDate() {
        return _slabDateAsDate;
    }

    /**
     * @param slabDateAsDate
     *            The slabDateAsDate to set.
     */
    public void setSlabDateAsDate(Date slabDateAsDate) {
        _slabDateAsDate = slabDateAsDate;
    }

    /**
     * @return Returns the fromRangeAsString.
     */
    public String getFromRangeAsString() {
        return _fromRangeAsString;
    }

    /**
     * @param fromRangeAsString
     *            The fromRangeAsString to set.
     */
    public void setFromRangeAsString(String fromRangeAsString) {
        _fromRangeAsString = fromRangeAsString;
    }

    /**
     * @return Returns the toRangeAsString.
     */
    public String getToRangeAsString() {
        return _toRangeAsString;
    }

    /**
     * @param toRangeAsString
     *            The toRangeAsString to set.
     */
    public void setToRangeAsString(String toRangeAsString) {
        _toRangeAsString = toRangeAsString;
    }

    /**
     * @return Returns the radioRequired.
     */
    public String getRadioRequired() {
        return _radioRequired;
    }

    /**
     * @param radioRequired
     *            The radioRequired to set.
     */
    public void setRadioRequired(String radioRequired) {
        _radioRequired = radioRequired;
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
     * @return Returns the radioIndex.
     */
    public int getRadioIndex() {
        return _radioIndex;
    }

    /**
     * @param radioIndex
     *            The radioIndex to set.
     */
    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    /**
     * @return Returns the fromRange.
     */
    public double getFromRange() {
        return _fromRange;
    }

    /**
     * @param fromRange
     *            The fromRange to set.
     */
    public void setFromRange(double fromRange) {
        _fromRange = fromRange;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the slabDate.
     */
    public String getSlabDate() {
        return _slabDate;
    }

    /**
     * @param slabDate
     *            The slabDate to set.
     */
    public void setSlabDate(String slabDate) {
        _slabDate = slabDate;
    }

    /**
     * @return Returns the slabid.
     */
    public String getSlabid() {
        return _slabid;
    }

    /**
     * @param slabid
     *            The slabid to set.
     */
    public void setSlabid(String slabid) {
        _slabid = slabid;
    }

    /**
     * @return Returns the toRange.
     */
    public double getToRange() {
        return _toRange;
    }

    /**
     * @param toRange
     *            The toRange to set.
     */
    public void setToRange(double toRange) {
        _toRange = toRange;
    }
}
