package com.btsl.tool.userrollback;

/**
 * @(#)UserRollBackVo.java
 *                         Copyright(c) 2010, Comviva Technologies Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Ashish Kumar Todia June 02,2010 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 */
public class UserRollBackVo {

    private String _msisdn;
    private int _lineNumber;
    private String _oldUserID;
    private String _newUserId;

    private String _newUserGeog;
    private String _oldUserGeog;

    private String _newUserPhoneLang;
    private String _newUserNetworkCode;
    private String _newUserCountry;

    private String _oldUserPhoneLang;
    private String _oldUserNetworkCode;
    private String _oldUserCountry;

    private String _newUserParentName;
    private String _newUserParentMsisdn;
    private String _oldUserParentName;
    private String _oldUserParentMsisdn;

    private String _oldUserName;
    private String _newUserName;

    public int getLineNumber() {
        return _lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        _lineNumber = lineNumber;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public String getNewUserId() {
        return _newUserId;
    }

    public void setNewUserId(String newUserId) {
        _newUserId = newUserId;
    }

    public String getOldUserID() {
        return _oldUserID;
    }

    public void setOldUserID(String oldUserID) {
        _oldUserID = oldUserID;
    }

    public String getNewUserCountry() {
        return _newUserCountry;
    }

    public void setNewUserCountry(String newUserCountry) {
        _newUserCountry = newUserCountry;
    }

    public String getNewUserGeog() {
        return _newUserGeog;
    }

    public void setNewUserGeog(String newUserGeog) {
        _newUserGeog = newUserGeog;
    }

    public String getNewUserNetworkCode() {
        return _newUserNetworkCode;
    }

    public void setNewUserNetworkCode(String newUserNetworkCode) {
        _newUserNetworkCode = newUserNetworkCode;
    }

    public String getNewUserParentMsisdn() {
        return _newUserParentMsisdn;
    }

    public void setNewUserParentMsisdn(String newUserParentMsisdn) {
        _newUserParentMsisdn = newUserParentMsisdn;
    }

    public String getNewUserParentName() {
        return _newUserParentName;
    }

    public void setNewUserParentName(String newUserParentName) {
        _newUserParentName = newUserParentName;
    }

    public String getNewUserPhoneLang() {
        return _newUserPhoneLang;
    }

    public void setNewUserPhoneLang(String newUserPhoneLang) {
        _newUserPhoneLang = newUserPhoneLang;
    }

    public String getOldUserCountry() {
        return _oldUserCountry;
    }

    public void setOldUserCountry(String oldUserCountry) {
        _oldUserCountry = oldUserCountry;
    }

    public String getOldUserNetworkCode() {
        return _oldUserNetworkCode;
    }

    public void setOldUserNetworkCode(String oldUserNetworkCode) {
        _oldUserNetworkCode = oldUserNetworkCode;
    }

    public String getOldUserPhoneLang() {
        return _oldUserPhoneLang;
    }

    public void setOldUserPhoneLang(String oldUserPhoneLang) {
        _oldUserPhoneLang = oldUserPhoneLang;
    }

    public String getOldUserGeog() {
        return _oldUserGeog;
    }

    public void setOldUserGeog(String oldUserGeog) {
        _oldUserGeog = oldUserGeog;
    }

    public String getOldUserParentMsisdn() {
        return _oldUserParentMsisdn;
    }

    public void setOldUserParentMsisdn(String oldUserParentMsisdn) {
        _oldUserParentMsisdn = oldUserParentMsisdn;
    }

    public String getOldUserParentName() {
        return _oldUserParentName;
    }

    public void setOldUserParentName(String oldUserParentName) {
        _oldUserParentName = oldUserParentName;
    }

    public String getNewUserName() {
        return _newUserName;
    }

    public void setNewUserName(String newUserName) {
        _newUserName = newUserName;
    }

    public String getOldUserName() {
        return _oldUserName;
    }

    public void setOldUserName(String oldUserName) {
        _oldUserName = oldUserName;
    }

}
