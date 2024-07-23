/*
 * @PrivateRchrgForm.java
 * --------------------------------------------------------------------
 * Created By Created On History
 * --------------------------------------------------------------------
 * Babu Kunwar 05-Sep-2011 Intial Creation
 * --------------------------------------------------------------------
 * CopyRight Comviva Technologies Ltd. 2009
 */
package com.btsl.pretups.privaterecharge.web;

import java.util.Date;

import jakarta.servlet.ServletRequest;

//import org.apache.struts.action.ActionMapping;
//import org.apache.struts.validator.ValidatorActionForm;

import com.btsl.pretups.common.PretupsI;

public class PrivateRchrgForm /*extends ValidatorActionForm*/ {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String _subscriberMsisdn = "";
    private String _subscriberName = "";
    private String _sidGenerationType = "";
    private String _subscriberSID = "";
    private String _previousSID = "";
    private Date _createdOn = null;
    private String _createdBy = "";

    public PrivateRchrgForm() {
        setSidGenerationType(PretupsI.SID_AUTO_FILTER);
    }

    public String getPreviousSID() {
        return _previousSID;
    }

    public void setPreviousSID(String previousSID) {
        _previousSID = previousSID;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public String getSubscriberMsisdn() {
        return _subscriberMsisdn;
    }

    public void setSubscriberMsisdn(String subscriberMsisdn) {
        _subscriberMsisdn = subscriberMsisdn;
    }

    public String getSubscriberName() {
        return _subscriberName;
    }

    public void setSubscriberName(String subscriberName) {
        _subscriberName = subscriberName;
    }

    public String getSidGenerationType() {
        return _sidGenerationType;
    }

    public void setSidGenerationType(String sidGenerationType) {
        _sidGenerationType = sidGenerationType;
    }

    public String getSubscriberSID() {
        return _subscriberSID;
    }

    public void setSubscriberSID(String subscriberSID) {
        _subscriberSID = subscriberSID;
    }

   /* @Override
    public void reset(ActionMapping mapping, ServletRequest request) {
        super.reset(mapping, request);
        _subscriberMsisdn = "";
        _subscriberName = "";
        _sidGenerationType = "";
        _subscriberSID = "";
    }*/

    public void flush() {
        _subscriberMsisdn = null;
        _subscriberName = null;
        //_sidGenerationType = null;
        _subscriberSID = null;
    }

}