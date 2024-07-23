package com.btsl.pretups.routing.master.businesslogic;

import java.util.Date;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/*
 * @(#)ServiceRoutingVO
 * Name Date History
 * ------------------------------------------------------------------------
 * Ankit Singhal 16/05/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */

public class ServiceInterfaceRoutingVO {
    // Instanse variables
    private int _sno;
    private String _networkCode;
    private String _serviceType;
    private String _senderSubscriberType;
    private String _interfaceType;
    private String _interfaceDefaultSelectortCode;
    private String _alternateInterfaceType;
    private boolean _alternateInterfaceCheckBool;
    private String _alternateInterfaceCheck;
    private String _alternateDefaultSelectortCode;
    private String _createdBy;
    private Date _createdOn;
    private String _modifiedBy;
    private Date _modifiedOn;
    private String _createdOnString;
    private String _modifiedOnString;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" _sno=" + _sno);
        sb.append(" _networkCode=" + _networkCode);
        sb.append(", _serviceType=" + _serviceType);
        sb.append(", _senderSubscriberType=" + _senderSubscriberType);
        sb.append(",_interfaceType=" + _interfaceType);
        sb.append(",_interfaceDefaultSelectortCode=" + _interfaceDefaultSelectortCode);
        sb.append(", _alternateInterfaceType=" + _alternateInterfaceType);
        sb.append(", _alternateInterfaceCheck=" + _alternateInterfaceCheck);
        sb.append(",_alternateDefaultSelectortCode=" + _alternateDefaultSelectortCode);
        sb.append(", _createdBy=" + _createdBy);
        sb.append(", _createdOn=" + _createdOn);
        sb.append(",_modifiedBy=" + _modifiedBy);
        sb.append(",_modifiedOn=" + _modifiedOn);
        return sb.toString();
    }

    /**
     * @return Returns the alternateInterfaceCheck.
     */
    public String getAlternateInterfaceCheck() {
        return _alternateInterfaceCheck;
    }

    /**
     * @param alternateInterfaceCheck
     *            The alternateInterfaceCheck to set.
     */
    public void setAlternateInterfaceCheck(String alternateInterfaceCheck) {
        _alternateInterfaceCheck = alternateInterfaceCheck;
    }

    /**
     * @return Returns the alternateInterfaceCheckBool.
     */
    public boolean isAlternateInterfaceCheckBool() {
        return _alternateInterfaceCheckBool;
    }

    /**
     * @param alternateInterfaceCheckBool
     *            The alternateInterfaceCheckBool to set.
     */
    public void setAlternateInterfaceCheckBool(boolean alternateInterfaceCheckBool) {
        _alternateInterfaceCheckBool = alternateInterfaceCheckBool;
    }

    /**
     * @return Returns the alternateInterfaceType.
     */
    public String getAlternateInterfaceType() {
        return _alternateInterfaceType;
    }

    /**
     * @param alternateInterfaceType
     *            The alternateInterfaceType to set.
     */
    public void setAlternateInterfaceType(String alternateInterfaceType) {
        _alternateInterfaceType = alternateInterfaceType;
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
     * @return Returns the createdOnString.
     */
    public String getCreatedOnString() {
        return _createdOnString;
    }

    /**
     * @param createdOnString
     *            The createdOnString to set.
     */
    public void setCreatedOnString(String createdOnString) {
        _createdOnString = createdOnString;
    }

    /**
     * @return Returns the interfaceType.
     */
    public String getInterfaceType() {
        return _interfaceType;
    }

    /**
     * @param interfaceType
     *            The interfaceType to set.
     */
    public void setInterfaceType(String interfaceType) {
        _interfaceType = interfaceType;
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
     * @return Returns the modifiedOnString.
     */
    public String getModifiedOnString() {
        return _modifiedOnString;
    }

    /**
     * @param modifiedOnString
     *            The modifiedOnString to set.
     */
    public void setModifiedOnString(String modifiedOnString) {
        _modifiedOnString = modifiedOnString;
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

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(200);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Network Code");
        sbf.append(middleSeperator);
        sbf.append(this.getNetworkCode());

        sbf.append(startSeperator);
        sbf.append("Service Type");
        sbf.append(middleSeperator);
        sbf.append(this.getServiceType());

        sbf.append(startSeperator);
        sbf.append("Interface Type");
        sbf.append(middleSeperator);
        sbf.append(this.getInterfaceType());

        sbf.append(startSeperator);
        sbf.append("Alternate Interface Check");
        sbf.append(middleSeperator);
        sbf.append(this.isAlternateInterfaceCheckBool());

        sbf.append(startSeperator);
        sbf.append("Alternate Interface Type");
        sbf.append(middleSeperator);
        sbf.append(this.getAlternateInterfaceType());

        sbf.append("***********");
        return sbf.toString();
    }

    /**
     * 
     * @param p_subscriberRoutingControlVO
     * @return String
     */
    public String differences(ServiceInterfaceRoutingVO p_serviveRoutingVO) {
        StringBuffer sbf = new StringBuffer(10);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!BTSLUtil.compareLocaleString(this.getNetworkCode(), p_serviveRoutingVO.getNetworkCode())) {
            sbf.append(startSeperator);
            sbf.append("Network Code");
            sbf.append(middleSeperator);
            sbf.append(p_serviveRoutingVO.getNetworkCode());
            sbf.append(middleSeperator);
            sbf.append(this.getNetworkCode());
        }

        if (!BTSLUtil.compareLocaleString(this.getServiceType(), p_serviveRoutingVO.getServiceType())) {
            sbf.append(startSeperator);
            sbf.append("Service Type");
            sbf.append(middleSeperator);
            sbf.append(p_serviveRoutingVO.getServiceType());
            sbf.append(middleSeperator);
            sbf.append(this.getServiceType());
        }

        if (!BTSLUtil.compareLocaleString(this.getInterfaceType(), p_serviveRoutingVO.getInterfaceType())) {
            sbf.append(startSeperator);
            sbf.append("Database Check");
            sbf.append(middleSeperator);
            sbf.append(p_serviveRoutingVO.getInterfaceType());
            sbf.append(middleSeperator);
            sbf.append(this.getInterfaceType());
        }

        if (!BTSLUtil.compareLocaleString(this.getAlternateInterfaceCheck(), p_serviveRoutingVO.getAlternateInterfaceCheck())) {
            sbf.append(startSeperator);
            sbf.append("Interface Category");
            sbf.append(middleSeperator);
            sbf.append(p_serviveRoutingVO.getAlternateInterfaceCheck());
            sbf.append(middleSeperator);
            sbf.append(this.getAlternateInterfaceCheck());
        }

        if (!BTSLUtil.compareLocaleString(this.getAlternateInterfaceType(), p_serviveRoutingVO.getAlternateInterfaceType())) {
            sbf.append(startSeperator);
            sbf.append("Series Check");
            sbf.append(middleSeperator);
            sbf.append(p_serviveRoutingVO.getAlternateInterfaceType());
            sbf.append(middleSeperator);
            sbf.append(this.getAlternateInterfaceType());
        }
        return sbf.toString();
    }

    public String getAlternateDefaultSelectortCode() {
        return _alternateDefaultSelectortCode;
    }

    public void setAlternateDefaultSelectortCode(String alternateDefaultSelectortCode) {
        _alternateDefaultSelectortCode = alternateDefaultSelectortCode;
    }

    public String getInterfaceDefaultSelectortCode() {
        return _interfaceDefaultSelectortCode;
    }

    public void setInterfaceDefaultSelectortCode(String interfaceDefaultSelectortCode) {
        _interfaceDefaultSelectortCode = interfaceDefaultSelectortCode;
    }

    public String getSenderSubscriberType() {
        return _senderSubscriberType;
    }

    public void setSenderSubscriberType(String senderSubscriberType) {
        _senderSubscriberType = senderSubscriberType;
    }

    public int getSno() {
        return _sno;
    }

    public void setSno(int sno) {
        _sno = sno;
    }

}
