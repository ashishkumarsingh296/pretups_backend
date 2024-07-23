package com.btsl.pretups.channel.reports.businesslogic;

public class UserClosingBalanceVO {
    private String _userId;
    private String _userName;
    private String _userMSISDN;
    private String _userCategory;
    private String _userGeography;
    private String _parentUserId;
    private String _parentUserName;
    private String _parentUserMSISDN;
    private String _ownerUserId;
    private String _ownerUserName;
    private String _ownerUserMSISDN;
    private String _grandUserId;
    private String _grandUserName;
    private String _grandUserMSISDN;
    private String _balanceString;
    private double _balance;

    public String getUserId() {
        return _userId;
    }

    public void setUserId(String id) {
        _userId = id;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String name) {
        _userName = name;
    }

    public String getUserMSISDN() {
        return _userMSISDN;
    }

    public void setUserMSISDN(String _usermsisdn) {
        _userMSISDN = _usermsisdn;
    }

    public String getUserCategory() {
        return _userCategory;
    }

    public void setUserCategory(String category) {
        _userCategory = category;
    }

    public String getUserGeography() {
        return _userGeography;
    }

    public void setUserGeography(String geography) {
        _userGeography = geography;
    }

    public String getParentUserId() {
        return _parentUserId;
    }

    public void setParentUserId(String userId) {
        _parentUserId = userId;
    }

    public String getParentUserName() {
        return _parentUserName;
    }

    public void setParentUserName(String userName) {
        _parentUserName = userName;
    }

    public String getParentUserMSISDN() {
        return _parentUserMSISDN;
    }

    public void setParentUserMSISDN(String userMSISDN) {
        _parentUserMSISDN = userMSISDN;
    }

    public String getOwnerUserId() {
        return _ownerUserId;
    }

    public void setOwnerUserId(String userId) {
        _ownerUserId = userId;
    }

    public String getOwnerUserName() {
        return _ownerUserName;
    }

    public void setOwnerUserName(String userName) {
        _ownerUserName = userName;
    }

    public String getOwnerUserMSISDN() {
        return _ownerUserMSISDN;
    }

    public void setOwnerUserMSISDN(String userMSISDN) {
        _ownerUserMSISDN = userMSISDN;
    }

    public String getBalanceString() {
        return _balanceString;
    }

    public void setBalanceString(String balString) {
        _balanceString = balString;
    }

    public double getBalance() {
        return _balance;
    }

    public void setBalance(long _balance) {
        this._balance = _balance;
    }

    public String getGrandUserId() {
        return _grandUserId;
    }

    public void setGrandUserId(String userId) {
        _grandUserId = userId;
    }

    public String getGrandUserName() {
        return _grandUserName;
    }

    public void setGrandUserName(String userName) {
        _grandUserName = userName;
    }

    public String getGrandUserMSISDN() {
        return _grandUserMSISDN;
    }

    public void setGrandUserMSISDN(String userMSISDN) {
        _grandUserMSISDN = userMSISDN;
    }

}
