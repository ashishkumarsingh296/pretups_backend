package com.selftopup.pretups.routing.subscribermgmt.businesslogic;

/*
 * @# NumberPortVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Apr 02, 2007 Vikas yadav Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 */
import java.util.Date;

public class NumberPortVO {

    private String _msisdn = null;
    private String _subscriberType = null;
    private String _portType = null;
    private String _createdBy = null;
    private Date _createdOn = null;

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

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public String getPortType() {
        return _portType;
    }

    public void setPortType(String portType) {
        _portType = portType;
    }

    public String getSubscriberType() {
        return _subscriberType;
    }

    public void setSubscriberType(String subscriberType) {
        _subscriberType = subscriberType;
    }

    public String toString() {
        StringBuffer strbuff = new StringBuffer("_msisdn=" + _msisdn);
        strbuff.append(" _subscriberType=" + _subscriberType + "  _portType" + _portType + "  _createdBy=" + _createdBy + "  _createdOn=" + _createdOn);
        return strbuff.toString();
    }
}
