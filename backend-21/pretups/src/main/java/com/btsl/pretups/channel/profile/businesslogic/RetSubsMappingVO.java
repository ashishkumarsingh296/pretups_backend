package com.btsl.pretups.channel.profile.businesslogic;

/**
 * @(#)RetSubsMappingVO.java
 *                           Copyright(c) 2008, Bharti Telesoft Ltd.
 *                           All Rights Reserved
 *                           This class refers to the Profile details in a
 *                           profile set version.
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           rahul.dutt 09/02/2009 Initital Creation
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 */
import java.io.Serializable;
import java.util.Date;

public class RetSubsMappingVO implements Serializable {
    private String _retailerMsisdn;
    private String _subscriberMsisdn;
    private Date _registeredOn;
    private Date _fromDate;
    private Date _toDate;
    private long _noOfActivatedSubs = 0;
    private String _retailername;
    // for correct mapping

    private String _newRetailerMsisdn;
    private String _newRetailername;
    private Date _newRegisteredOn;
    private String _prevRetailerID;
    private String _newRetailerID;
    private String _subscriberType;
    private String _setID;
    private String _version;
    private Date _expiryDate;
    private String _networkCode;
    private String _userID;
    private String _activationBonusGiven;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;

    private String _approvedBy;
    private Date _approvedOn;
    private String _status = null;
    // added by vikas kumar
    private String _parentName;
    private String _parentMsisdn;
    private String _ownerName;
    private String _ownerMsisdn;
    private String _activatedOn;
    private String _allowAction;

    private String _retailerId;
    private int _bonusDureation = 0;
    private String _message;

    // added for associate profile in batch by amit
    private String _loginId;
    private String _categoryCode;
    private String _profileType;

    /**
     * toString() method writes the parameters value to the console or log
     * 
     * @return String
     */
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("_retailerMsisdn=" + _retailerMsisdn + ",");
        sb.append("_subscriberMsisdn=" + _subscriberMsisdn + ",");
        sb.append("_registeredOn=" + _registeredOn + ",");
        sb.append("_fromDate=" + _fromDate + ",");
        sb.append("_toDate=" + _toDate + ",");
        sb.append("_noOfActivatedSubs=" + _noOfActivatedSubs + ",");
        sb.append("_retailername=" + _retailername + ",");
        sb.append("_newRetailerMsisdn=" + _newRetailerMsisdn + ",");
        sb.append("_newRetailername=" + _newRetailername + ",");
        sb.append("_newRegisteredOn=" + _newRegisteredOn + ",");
        sb.append("_prevRetailerID=" + _prevRetailerID);
        sb.append("_newRetailerID=" + _newRetailerID);
        sb.append("_subscriberType=" + _subscriberType);
        sb.append("_setID=" + _setID);
        sb.append("_version=" + _version);
        sb.append("_expiryDate=" + _expiryDate);
        sb.append("_networkCode=" + _networkCode);
        sb.append("_approvedBy=" + _approvedBy);
        sb.append("_approvedOn=" + _approvedOn);
        sb.append("_ownerMsisdn=" + _ownerMsisdn);
        sb.append("_parentMsisdn=" + _parentMsisdn);
        sb.append("_allowAction=" + _allowAction);
        sb.append("_status=" + _status);
        sb.append("_loginId=" + _loginId);
        sb.append("_categoryCode=" + _categoryCode);
        sb.append("_userID=" + _userID);
        sb.append("_createdOn=" + _createdOn);
        sb.append("_createdBy=" + _createdBy);
        sb.append("_modifiedOn=" + _modifiedOn);
        sb.append("_modifiedBy=" + _modifiedBy);
        sb.append("_parentName=" + _parentName);
        sb.append("_ownerName=" + _ownerName);
        sb.append("_activatedOn=" + _activatedOn);
        sb.append("_retailerId=" + _retailerId);
        sb.append("_bonusDureation=" + _bonusDureation);
        sb.append("_profileType=" + _profileType);
        sb.append("_activationBonusGiven=" + _activationBonusGiven);
        return sb.toString();
    }

    public Date getFromDate() {
        return _fromDate;
    }

    public void setFromDate(Date date) {
        _fromDate = date;
    }

    public long getNoOfActivatedSubs() {
        return _noOfActivatedSubs;
    }

    public void setNoOfActivatedSubs(long ofActivatedSubs) {
        _noOfActivatedSubs = ofActivatedSubs;
    }

    public Date getRegisteredOn() {
        return _registeredOn;
    }

    public void setRegisteredOn(Date on) {
        _registeredOn = on;
    }

    public String getRetailerMsisdn() {
        return _retailerMsisdn;
    }

    public void setRetailerMsisdn(String msisdn) {
        _retailerMsisdn = msisdn;
    }

    public String getSubscriberMsisdn() {
        return _subscriberMsisdn;
    }

    public void setSubscriberMsisdn(String msisdn) {
        _subscriberMsisdn = msisdn;
    }

    public Date getToDate() {
        return _toDate;
    }

    public void setToDate(Date date) {
        _toDate = date;
    }

    public String getRetailername() {
        return _retailername;
    }

    public void setRetailername(String _retailername) {
        this._retailername = _retailername;
    }

    public Date getNewRegisteredOn() {
        return _newRegisteredOn;
    }

    public void setNewRegisteredOn(Date registeredOn) {
        _newRegisteredOn = registeredOn;
    }

    public String getNewRetailerMsisdn() {
        return _newRetailerMsisdn;
    }

    public void setNewRetailerMsisdn(String retailerMsisdn) {
        _newRetailerMsisdn = retailerMsisdn;
    }

    public String getNewRetailername() {
        return _newRetailername;
    }

    public void setNewRetailername(String retailername) {
        _newRetailername = retailername;
    }

    public String getNewRetailerID() {
        return _newRetailerID;
    }

    public void setNewRetailerID(String retailerID) {
        _newRetailerID = retailerID;
    }

    public String getPrevRetailerID() {
        return _prevRetailerID;
    }

    public void setPrevRetailerID(String retailerID) {
        _prevRetailerID = retailerID;
    }

    public Date getExpiryDate() {
        return _expiryDate;
    }

    public void setExpiryDate(Date date) {
        _expiryDate = date;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    public String getSetID() {
        return _setID;
    }

    public void setSetID(String _setid) {
        _setID = _setid;
    }

    public String getSubscriberType() {
        return _subscriberType;
    }

    public void setSubscriberType(String type) {
        _subscriberType = type;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String _version) {
        this._version = _version;
    }

    public String getApprovedBy() {
        return _approvedBy;
    }

    public void setApprovedBy(String by) {
        _approvedBy = by;
    }

    public Date getApprovedOn() {
        return _approvedOn;
    }

    public void setApprovedOn(Date on) {
        _approvedOn = on;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String _status) {
        this._status = _status;
    }

    /**
     * @return Returns the activationBonusGiven.
     */
    public String getActivationBonusGiven() {
        return _activationBonusGiven;
    }

    /**
     * @param activationBonusGiven
     *            The activationBonusGiven to set.
     */
    public void setActivationBonusGiven(String activationBonusGiven) {
        _activationBonusGiven = activationBonusGiven;
    }

    /**
     * @return Returns the userID.
     */
    public String getUserID() {
        return _userID;
    }

    /**
     * @param userID
     *            The userID to set.
     */
    public void setUserID(String userID) {
        _userID = userID;
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

    // added by vikas kumar
    /**
     * @return the _ownerMsisdn
     */
    public String getOwnerMsisdn() {
        return _ownerMsisdn;
    }

    /**
     * @param msisdn
     *            the _ownerMsisdn to set
     */
    public void setOwnerMsisdn(String msisdn) {
        _ownerMsisdn = msisdn;
    }

    /**
     * @return the _ownerName
     */
    public String getOwnerName() {
        return _ownerName;
    }

    /**
     * @param name
     *            the _ownerName to set
     */
    public void setOwnerName(String name) {
        _ownerName = name;
    }

    /**
     * @return the _parentMsisdn
     */
    public String getParentMsisdn() {
        return _parentMsisdn;
    }

    /**
     * @param msisdn
     *            the _parentMsisdn to set
     */
    public void setParentMsisdn(String msisdn) {
        _parentMsisdn = msisdn;
    }

    /**
     * @return the _parentName
     */
    public String getParentName() {
        return _parentName;
    }

    /**
     * @param name
     *            the _parentName to set
     */
    public void setParentName(String name) {
        _parentName = name;
    }

    /**
     * @return the _activatedOn
     */
    public String getActivatedOn() {
        return _activatedOn;
    }

    /**
     * @param on
     *            the _activatedOn to set
     */
    public void setActivatedOn(String on) {
        _activatedOn = on;
    }

    /**
     * @return the _allowAction
     */
    public String getAllowAction() {
        return _allowAction;
    }

    /**
     * @param action
     *            the _allowAction to set
     */
    public void setAllowAction(String action) {
        _allowAction = action;
    }

    /**
     * @return the _retailerId
     */
    public String getRetailerId() {
        return _retailerId;
    }

    /**
     * @param id
     *            the _retailerId to set
     */
    public void setRetailerId(String id) {
        _retailerId = id;
    }

    /**
     * @return the _bonusDureation
     */
    public int getBonusDureation() {
        return _bonusDureation;
    }

    /**
     * @param dureation
     *            the _bonusDureation to set
     */
    public void setBonusDureation(int dureation) {
        _bonusDureation = dureation;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String _message) {
        this._message = _message;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    public String getLoginId() {
        return _loginId;
    }

    public void setLoginId(String loginId) {
        _loginId = loginId;
    }

    public String getProfileType() {
        return _profileType;
    }

    public void setProfileType(String profileType) {
        _profileType = profileType;
    }
    
    public static RetSubsMappingVO getInstance(){
    	return new RetSubsMappingVO();
    }
}
