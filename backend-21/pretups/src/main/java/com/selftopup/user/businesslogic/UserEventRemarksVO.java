/*
 * @# UserEventRemarksVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Feb 15, 2011 Babu Kunwar Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Comviva Technologies LTD.
 */

package com.selftopup.user.businesslogic;

import java.util.Date;

//import org.apache.struts.action.ActionForm;

public class UserEventRemarksVO extends ActionForm {

    private static final long serialVersionUID = 1L;

    private String _userID = null;
    private String _eventType = null;
    private String _remarks = null;
    private String _createdBy = null;
    // private String _createdOn=null;
    private Date _createdOn = null;
    private String _module = null;
    private String _msisdn = null;
    private String _userType = null;

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return the eventType
     */
    public String getEventType() {
        return _eventType;
    }

    /**
     * @param eventType
     *            the eventType to set
     */
    public void setEventType(String eventType) {
        _eventType = eventType;
    }

    /**
     * @return the module
     */
    public String getModule() {
        return _module;
    }

    /**
     * @param module
     *            the module to set
     */
    public void setModule(String module) {
        _module = module;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            the msisdn to set
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return _remarks;
    }

    /**
     * @param remarks
     *            the remarks to set
     */
    public void setRemarks(String remarks) {
        _remarks = remarks;
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return _userID;
    }

    /**
     * @param userID
     *            the userID to set
     */
    public void setUserID(String userID) {
        _userID = userID;
    }

    /**
     * @return the userType
     */
    public String getUserType() {
        return _userType;
    }

    /**
     * @param userType
     *            the userType to set
     */
    public void setUserType(String userType) {
        _userType = userType;
    }

    /**
     * @return the createdOn
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            the createdOn to set
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

}
