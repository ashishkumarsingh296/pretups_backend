package com.btsl.pretups.processes.businesslogic;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProgressiveMessageVO {

    private String _msisdn;
    private String _userId;
    private String _productCode;
    private String _productShortName;
    private long _balance;
    private Locale _locale;
    // added for OCI Changes by Ashish S dated as 16/07/2007
    private String _networkCode;

    private String _parentMsisdn;
    private String _parentUserId;
    // added by nilesh
    private String _categoryCode;
    private String _profileID;
    private String _alertMsisdn;

    // Alert Type is added for Low Balance Alert by Harpreet on 27/09/2011
    private String _alertType;
    private String _alertEmail;
    private String _userName;
    // Added by Amit Raheja
    private String _selfEmail;
    private String _parentEmail;
    private String _referenceAllowed = null;
    private long _lmsTarget;

    private String _profileType = null;
    private String _setId = null;
    private String _setName = null;
    private String _lastVersion = null;
    private String _promotionType = null;
    private String _refBasedAllowed = null;
    private String _type = null;
    private String _detailType = null;
    private String _detailSubType = null;
    private String _endRange = null;
    private Date _refFrom = null;
    private Date _refTo = null;
    private String _lmsProfile = null;
    private Date _modifiedOn = null;
    private ArrayList _targetUserList = null;
    private ArrayList _userTransactionAmountList = null;
    private String _version;
    private String _serviceCode;
    
    
    
    
    
   
	public String toString() 
	{
        StringBuffer sb = new StringBuffer();
        sb.append("_msisdn=" + _msisdn);
        sb.append(",_userId=" + _userId);
        sb.append(",_productCode=" + _productCode);
        sb.append(",_productShortName=" + _productShortName);
        sb.append("_balance=" + _balance);
        sb.append(",_locale=" + _locale);
        sb.append(",_parentMsisdn=" + _parentMsisdn);
        sb.append("_parentUserId= " + _parentUserId);
        sb.append("_alertMsisdn= " + _alertMsisdn);
        sb.append("_alertType= " + _alertType);
        sb.append("_alertEmail= " + _alertEmail);
        sb.append("_selfEmail= " + _selfEmail);
        sb.append("_parentEmail= " + _parentEmail);
        sb.append("_version= "+_version);
        return sb.toString();
    }

    /**
     * @return Returns the locale.
     */
    public Locale getLocale() {
        return _locale;
    }

    /**
     * @param locale
     *            The locale to set.
     */
    public void setLocale(Locale locale) {
        _locale = locale;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return Returns the balance.
     */
    public long getBalance() {
        return _balance;
    }

    /**
     * @param balance
     *            The balance to set.
     */
    public void setBalance(long balance) {
        _balance = balance;
    }

    /**
     * @return Returns the productCode.
     */
    public String getProductCode() {
        return _productCode;
    }

    /**
     * @param productCode
     *            The productCode to set.
     */
    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    /**
     * @return Returns the productShortName.
     */
    public String getProductShortName() {
        return _productShortName;
    }

    /**
     * @param productShortName
     *            The productShortName to set.
     */
    public void setProductShortName(String productShortName) {
        _productShortName = productShortName;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(String userId) {
        _userId = userId;
    }

    /**
     * @return Returns the _networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param code
     *            The _networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the parentMsisdn.
     */
    public String getParentMsisdn() {
        return _parentMsisdn;
    }

    /**
     * @param parentMsisdn
     *            The parentMsisdn to set.
     */
    public void setParentMsisdn(String parentMsisdn) {
        _parentMsisdn = parentMsisdn;
    }

    /**
     * @return Returns the parentUserId.
     */
    public String getParentUserId() {
        return _parentUserId;
    }

    /**
     * @param parentUserId
     *            The parentUserId to set.
     */
    public void setParentUserId(String parentUserId) {
        _parentUserId = parentUserId;
    }

    // added by nilesh
    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    public String getProfileID() {
        return _profileID;
    }

    public void setProfileID(String profileID) {
        _profileID = profileID;
    }

    public String getAlertMsisdn() {
        return _alertMsisdn;
    }

    public void setAlertMsisdn(String alert_msisdn) {
        _alertMsisdn = alert_msisdn;
    }

    // added by Harpreet

    /**
     * @return Returns the alertType.
     */

    public String getAlertType() {
        return _alertType;
    }

    /**
     * @param alertType
     *            The alertType to set.
     */
    public void setAlertType(String alert_type) {
        _alertType = alert_type;
    }

    /**
     * @return Returns the alertEMAIL.
     */
    public String getAlertEmail() {
        return _alertEmail;
    }

    public void setAlertEmail(String alert_Email) {
        _alertEmail = alert_Email;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String user_Name) {
        _userName = user_Name;
    }

    public String getSelfEmail() {
        return _selfEmail;
    }

    public void setSelfEmail(String email) {
        _selfEmail = email;
    }

    public String getParentEmail() {
        return _parentEmail;
    }

    public void setParentEmail(String email) {
        _parentEmail = email;
    }

    public String get_profileID() {
        return _profileID;
    }

    public void set_profileID(String _profileid) {
        _profileID = _profileid;
    }

    public String getReferenceAllowed() {
        return _referenceAllowed;
    }

    public void setReferenceAllowed(String referenceAllowed) {
        _referenceAllowed = referenceAllowed;
    }

    public long getLmsTarget() {
        return _lmsTarget;
    }

    public void setLmsTarget(long lmsTarget) {
        _lmsTarget = lmsTarget;
    }

    public String getDetailSubType() {
        return _detailSubType;
    }

    public void setDetailSubType(String subType) {
        _detailSubType = subType;
    }

    public String getDetailType() {
        return _detailType;
    }

    public void setDetailType(String type) {
        _detailType = type;
    }

    public String getEndRange() {
        return _endRange;
    }

    public void setEndRange(String range) {
        _endRange = range;
    }

    public String getLastVersion() {
        return _lastVersion;
    }

    public void setLastVersion(String version) {
        _lastVersion = version;
    }

    public String getLmsProfile() {
        return _lmsProfile;
    }

    public void setLmsProfile(String profile) {
        _lmsProfile = profile;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date on) {
        _modifiedOn = on;
    }

    public String getProfileType() {
        return _profileType;
    }

    public void setProfileType(String type) {
        _profileType = type;
    }

    public String getPromotionType() {
        return _promotionType;
    }

    public void setPromotionType(String type) {
        _promotionType = type;
    }

    public String getRefBasedAllowed() {
        return _refBasedAllowed;
    }

    public void setRefBasedAllowed(String basedAllowed) {
        _refBasedAllowed = basedAllowed;
    }

    public Date getRefFrom() {
        return _refFrom;
    }

    public void setRefFrom(Date from) {
        _refFrom = from;
    }

    public Date getRefTo() {
        return _refTo;
    }

    public void setRefTo(Date to) {
        _refTo = to;
    }

    public String getSetId() {
        return _setId;
    }

    public void setSetId(String id) {
        _setId = id;
    }

    public String getSetName() {
        return _setName;
    }

    public void setSetName(String name) {
        _setName = name;
    }

    public ArrayList getTargetUserList() {
        return _targetUserList;
    }

    public void setTargetUserList(ArrayList userList) {
        _targetUserList = userList;
    }

    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
    }

    public ArrayList getUserTransactionAmountList() {
        return _userTransactionAmountList;
    }

    public void setUserTransactionAmountList(ArrayList transactionAmountList) {
        _userTransactionAmountList = transactionAmountList;
    }

	public String getVersion() {
		return _version;
	}
	public void setVersion(String _version) {
		this._version = _version;
	}
	public String getServiceCode() {
		return _serviceCode;
	}
	public void setServiceCode(String _serviceCode) {
		this._serviceCode = _serviceCode;
	}
}
