package com.btsl.tool.usermigration;

/**
 * @(#)UserMigrationVO.java
 *                          Copyright(c) 2010, Comviva Technologies Ltd.
 *                          All Rights Reserved
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Vinay Singh June 05,2010 Initial Creation
 *                          Ashish Kumar Todia June 14,2010 Modification.
 *                          ----------------------------------------------------
 *                          --------------------------------------------
 */
public class UserMigrationVO {
    private String _fromParentMsisdn;
    private String _fromParentCatCode;
    private String _fromParentGeoCode;
    private String _fromUserMsisdn;
    private String _fromUserCatCode;
    private String _fromUserGeoCode;
    private String _fromUserID;
    private String _fromUserLoginID;
    private String _fromUserReferenceID;
    private String _fromUserStatus;

    // To user list variables
    private String _toParentMsisdn;
    private String _toParentCatCode;
    private String _toParentGeoCode;
    private String _toUserMsisdn;
    private String _toUserCatCode;
    private String _toUserGeoCode;
    private String _toUserCatCodeSeqNo;
    private String _toParentID;
    private String _toOwnerID;
    private String _networkCode;
    private String _toGeoDomainType;
    private boolean _isParentExist;
    private String _message;

    private String _fromUserParentId;
    private String _fromUserParentName; // for pushing sms.
    private String _toUserParentName; // for pushing sms.
    private String _phoneLang; // newly added for sms push.
    private String _country; // newly added for sms push.
    private String _fromUserName;

    private int _lineNumber;
    private String _activeChildUserCount;

    /**
     * @return the fromParentMsisdn
     */
    public String getFromParentMsisdn() {
        return _fromParentMsisdn;
    }

    /**
     * @param fromParentMsisdn
     *            the fromParentMsisdn to set
     */
    public void setFromParentMsisdn(String fromParentMsisdn) {
        _fromParentMsisdn = fromParentMsisdn;
    }

    /**
     * @return the fromParentCatCode
     */
    public String getFromParentCatCode() {
        return _fromParentCatCode;
    }

    /**
     * @param fromParentCatCode
     *            the fromParentCatCode to set
     */
    public void setFromParentCatCode(String fromParentCatCode) {
        _fromParentCatCode = fromParentCatCode;
    }

    /**
     * @return the fromParentGeoCode
     */
    public String getFromParentGeoCode() {
        return _fromParentGeoCode;
    }

    /**
     * @param fromParentGeoCode
     *            the fromParentGeoCode to set
     */
    public void setFromParentGeoCode(String fromParentGeoCode) {
        _fromParentGeoCode = fromParentGeoCode;
    }

    /**
     * @return the fromUserMsisdn
     */
    public String getFromUserMsisdn() {
        return _fromUserMsisdn;
    }

    /**
     * @param fromUserMsisdn
     *            the fromUserMsisdn to set
     */
    public void setFromUserMsisdn(String fromUserMsisdn) {
        _fromUserMsisdn = fromUserMsisdn;
    }

    /**
     * @return the fromUserCatCode
     */
    public String getFromUserCatCode() {
        return _fromUserCatCode;
    }

    /**
     * @param fromUserCatCode
     *            the fromUserCatCode to set
     */
    public void setFromUserCatCode(String fromUserCatCode) {
        _fromUserCatCode = fromUserCatCode;
    }

    /**
     * @return the fromUserGeoCode
     */
    public String getFromUserGeoCode() {
        return _fromUserGeoCode;
    }

    /**
     * @param fromUserGeoCode
     *            the fromUserGeoCode to set
     */
    public void setFromUserGeoCode(String fromUserGeoCode) {
        _fromUserGeoCode = fromUserGeoCode;
    }

    /**
     * @return the toUserCatCodeSeqNo
     */
    public String getToUserCatCodeSeqNo() {
        return _toUserCatCodeSeqNo;
    }

    /**
     * @param toUserCatCodeSeqNo
     *            the toUserCatCodeSeqNo to set
     */
    public void setToUserCatCodeSeqNo(String toUserCatCodeSeqNo) {
        _toUserCatCodeSeqNo = toUserCatCodeSeqNo;
    }

    /**
     * @return the fromUserID
     */
    public String getFromUserID() {
        return _fromUserID;
    }

    /**
     * @param fromUserID
     *            the fromUserID to set
     */
    public void setFromUserID(String fromUserID) {
        _fromUserID = fromUserID;
    }

    /**
     * @return the fromUserLoginID
     */
    public String getFromUserLoginID() {
        return _fromUserLoginID;
    }

    /**
     * @param fromUserLoginID
     *            the fromUserLoginID to set
     */
    public void setFromUserLoginID(String fromUserLoginID) {
        _fromUserLoginID = fromUserLoginID;
    }

    /**
     * @return the fromUserReferenceID
     */
    public String getFromUserReferenceID() {
        return _fromUserReferenceID;
    }

    /**
     * @param fromUserReferenceID
     *            the fromUserReferenceID to set
     */
    public void setFromUserReferenceID(String fromUserReferenceID) {
        _fromUserReferenceID = fromUserReferenceID;
    }

    /**
     * @return the toParentMsisdn
     */
    public String getToParentMsisdn() {
        return _toParentMsisdn;
    }

    /**
     * @param toParentMsisdn
     *            the toParentMsisdn to set
     */
    public void setToParentMsisdn(String toParentMsisdn) {
        _toParentMsisdn = toParentMsisdn;
    }

    /**
     * @return the toParentCatCode
     */
    public String getToParentCatCode() {
        return _toParentCatCode;
    }

    /**
     * @param toParentCatCode
     *            the toParentCatCode to set
     */
    public void setToParentCatCode(String toParentCatCode) {
        _toParentCatCode = toParentCatCode;
    }

    /**
     * @return the toParentGeoCode
     */
    public String getToParentGeoCode() {
        return _toParentGeoCode;
    }

    /**
     * @param toParentGeoCode
     *            the toParentGeoCode to set
     */
    public void setToParentGeoCode(String toParentGeoCode) {
        _toParentGeoCode = toParentGeoCode;
    }

    /**
     * @return the toUserMsisdn
     */
    public String getToUserMsisdn() {
        return _toUserMsisdn;
    }

    /**
     * @param toUserMsisdn
     *            the toUserMsisdn to set
     */
    public void setToUserMsisdn(String toUserMsisdn) {
        _toUserMsisdn = toUserMsisdn;
    }

    /**
     * @return the toUserCatCode
     */
    public String getToUserCatCode() {
        return _toUserCatCode;
    }

    /**
     * @param toUserCatCode
     *            the toUserCatCode to set
     */
    public void setToUserCatCode(String toUserCatCode) {
        _toUserCatCode = toUserCatCode;
    }

    /**
     * @return the toUserGeoCode
     */
    public String getToUserGeoCode() {
        return _toUserGeoCode;
    }

    /**
     * @param toUserGeoCode
     *            the toUserGeoCode to set
     */
    public void setToUserGeoCode(String toUserGeoCode) {
        _toUserGeoCode = toUserGeoCode;
    }

    /**
     * @return the networkCode
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            the networkCode to set
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return the fromUserStatus
     */
    public String getFromUserStatus() {
        return _fromUserStatus;
    }

    /**
     * @param fromUserStatus
     *            the fromUserStatus to set
     */
    public void setFromUserStatus(String fromUserStatus) {
        _fromUserStatus = fromUserStatus;
    }

    /**
     * @return the toParentID
     */
    public String getToParentID() {
        return _toParentID;
    }

    /**
     * @param toParentID
     *            the toParentID to set
     */
    public void setToParentID(String toParentID) {
        _toParentID = toParentID;
    }

    /**
     * @return the toOwnerID
     */
    public String getToOwnerID() {
        return _toOwnerID;
    }

    /**
     * @param toOwnerID
     *            the toOwnerID to set
     */
    public void setToOwnerID(String toOwnerID) {
        _toOwnerID = toOwnerID;
    }

    /**
     * @return the isParentExist
     */
    public boolean isParentExist() {
        return _isParentExist;
    }

    /**
     * @param isParentExist
     *            the isParentExist to set
     */
    public void setParentExist(boolean isParentExist) {
        _isParentExist = isParentExist;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return _message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        _message = message;
    }

    public int getLineNumber() {
        return _lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        _lineNumber = lineNumber;
    }

    public String getFromUserParentName() {
        return _fromUserParentName;
    }

    public void setFromUserParentName(String fromUserParentName) {
        _fromUserParentName = fromUserParentName;
    }

    public String getFromUserParentId() {
        return _fromUserParentId;
    }

    public void setFromUserParentId(String fromUserParentId) {
        _fromUserParentId = fromUserParentId;
    }

    public String getToUserParentName() {
        return _toUserParentName;
    }

    public void setToUserParentName(String toUserParentName) {
        _toUserParentName = toUserParentName;
    }

    public String getCountry() {
        return _country;
    }

    public void setCountry(String country) {
        _country = country;
    }

    public String getPhoneLang() {
        return _phoneLang;
    }

    public void setPhoneLang(String phoneLang) {
        _phoneLang = phoneLang;
    }

    public String getFromUserName() {
        return _fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        _fromUserName = fromUserName;
    }

    /**
     * @return the toGeoDomainType
     */
    public String getToGeoDomainType() {
        return _toGeoDomainType;
    }

    /**
     * @param toGeoDomainType
     *            the toGeoDomainType to set
     */
    public void setToGeoDomainType(String toGeoDomainType) {
        _toGeoDomainType = toGeoDomainType;
    }

    public String getActiveChildUserCount() {
        return _activeChildUserCount;
    }

    public void setActiveChildUserCount(String activeChildUserCount) {
        _activeChildUserCount = activeChildUserCount;
    }

}
