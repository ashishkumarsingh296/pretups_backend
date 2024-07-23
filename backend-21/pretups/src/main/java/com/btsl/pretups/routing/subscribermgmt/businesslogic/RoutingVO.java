package com.btsl.pretups.routing.subscribermgmt.businesslogic;

import java.io.Serializable;
import java.util.Date;

/*
 * @# RoutingVO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Nov 24, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
public class RoutingVO implements Serializable {

    private String _msisdn;
    private String _interfaceID;
    private String _subscriberType;
    private String _externalInterfaceID;
    private String _status;
    private String _createdBy;
    private Date _createdOn;
    private String _modifiedBy;
    private Date _modifiedOn;
    private String _text1;
    private String _text2;

    public RoutingVO() {

    }

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

    public String getExternalInterfaceID() {
        return _externalInterfaceID;
    }

    public void setExternalInterfaceID(String externalInterfaceID) {
        _externalInterfaceID = externalInterfaceID;
    }

    public String getInterfaceID() {
        return _interfaceID;
    }

    public void setInterfaceID(String interfaceID) {
        _interfaceID = interfaceID;
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

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getSubscriberType() {
        return _subscriberType;
    }

    public void setSubscriberType(String subscriberType) {
        _subscriberType = subscriberType;
    }

    public String getText1() {
        return _text1;
    }

    public void setText1(String text1) {
        _text1 = text1;
    }

    public String getText2() {
        return _text2;
    }

    public void setText2(String text2) {
        _text2 = text2;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(" _msisdn = " + _msisdn);
        sbf.append(", _externalInterfaceID = " + _externalInterfaceID);
        sbf.append(", _interfaceID = " + _interfaceID);
        sbf.append(", _createdBy = " + _createdBy);
        sbf.append(", _modifiedBy = " + _modifiedBy);
        sbf.append(", _status = " + _status);
        sbf.append(", _subscriberType = " + _subscriberType);
        sbf.append(", _text1 = " + _text1);
        sbf.append(", _text2= " + _text2);
        sbf.append(", _createdOn = " + _createdOn);
        sbf.append(", _modifiedOn = " + _modifiedOn);
        return sbf.toString();
    }
}
