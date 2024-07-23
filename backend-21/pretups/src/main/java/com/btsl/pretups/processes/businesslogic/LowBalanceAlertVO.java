/**
 * @(#)LowBalanceAlertVO.java
 *                            Name Date History
 *                            --------------------------------------------------
 *                            ----------------------
 *                            Ankit Singhal 07/03/2007 Initial Creation
 *                            Harpreet kaur 27/09/2011 Updation
 *                            --------------------------------------------------
 *                            ----------------------
 *                            Copyright (c) 2007 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.processes.businesslogic;

import java.util.Locale;

public class LowBalanceAlertVO {

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
    private String _autoc2cquantity;
    private long _autoO2CTransactionAmount;

    public String toString() {
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

    public String getAutoc2cquantity() {
        return _autoc2cquantity;
    }

    public void setAutoc2cquantity(String autoc2cquantity) {
        _autoc2cquantity = autoc2cquantity;
    }

	public long getAutoO2CTransactionAmount() {
		return _autoO2CTransactionAmount;
	}

	public void setAutoO2CTransactionAmount(long _autoO2CTransactionAmount) {
		this._autoO2CTransactionAmount = _autoO2CTransactionAmount;
	}
    
    

}
