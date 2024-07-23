package com.btsl.user.businesslogic;

import java.io.Serializable;

public class UserStatusVO implements Serializable {

    private String _networkCode = null;
    private String _gatewayType = null;
    private String _categoryCode = null;
    private String _userType = null;
    private String _domainCode = null;

    private String _userSenderAllowed = null;
    private String _userSenderDenied = null;
    private String _userSenderSuspended = null;
    private String _userReceiverAllowed = null;
    private String _userReceiverDenied = null;
    private String _userReceiverSuspended = null;
    private String _webLoginAllowed = null;
    private String _webLoginDenied = null;

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String code) {
        _networkCode = code;
    }

    public String getGatewayType() {
        return _gatewayType;
    }

    public void setGatewayType(String type) {
        _gatewayType = type;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String code) {
        _categoryCode = code;
    }

    public String getUserType() {
        return _userType;
    }

    public void setUserType(String type) {
        _userType = type;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String code) {
        _domainCode = code;
    }

    public String getUserSenderAllowed() {
        return _userSenderAllowed;
    }

    public void setUserSenderAllowed(String senderAllowed) {
        _userSenderAllowed = senderAllowed;
    }

    public String getUserSenderDenied() {
        return _userSenderDenied;
    }

    public void setUserSenderDenied(String senderDenied) {
        _userSenderDenied = senderDenied;
    }

    public String getUserSenderSuspended() {
        return _userSenderSuspended;
    }

    public void setUserSenderSuspended(String senderSuspended) {
        _userSenderSuspended = senderSuspended;
    }

    public String getUserReceiverAllowed() {
        return _userReceiverAllowed;
    }

    public void setUserReceiverAllowed(String receiverAllowed) {
        _userReceiverAllowed = receiverAllowed;
    }

    public String getUserReceiverDenied() {
        return _userReceiverDenied;
    }

    public void setUserReceiverDenied(String receiverDenied) {
        _userReceiverDenied = receiverDenied;
    }

    public String getUserReceiverSuspended() {
        return _userReceiverSuspended;
    }

    public void setUserReceiverSuspended(String receiverSuspended) {
        _userReceiverSuspended = receiverSuspended;
    }

    public String getWebLoginAllowed() {
        return _webLoginAllowed;
    }

    public void setWebLoginAllowed(String loginAllowed) {
        _webLoginAllowed = loginAllowed;
    }

    public String getWebLoginDenied() {
        return _webLoginDenied;
    }

    public void setWebLoginDenied(String loginDenied) {
        _webLoginDenied = loginDenied;
    }
}