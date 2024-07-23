/**
 * @(#)PrivateRchrgVO.java
 *                         Copyright(c) 2009, Comviva technologies Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Created On History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Babu Kunwar 05-Sep-2011 Initital Creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 */

package com.btsl.pretups.privaterecharge.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class PrivateRchrgVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private long _requestID;
    private String _requestIDStr;
    private String _msisdn;
    private String _userSID;
    private String _requestGatewayType;
    private String _userName;
    private String _requestGatewayCode;
    private Date _createdOn;
    private String _createdBy;
    private Date _modifyOn;
    private String _modifyBy;
    private Integer _successTxn;
    private String _messageCode;
    private String[] _messageArguments;

    public long getRequestID() {
        return _requestID;
    }

    public void setRequestID(long requestID) {
        _requestID = requestID;
    }

    public String getRequestIDStr() {
        return _requestIDStr;
    }

    public void setRequestIDStr(String requestIDStr) {
        _requestIDStr = requestIDStr;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public String getUserSID() {
        return _userSID;
    }

    public void setUserSID(String userSID) {
        _userSID = userSID;
    }

    public String getRequestGatewayType() {
        return _requestGatewayType;
    }

    public void setRequestGatewayType(String requestGatewayType) {
        _requestGatewayType = requestGatewayType;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public String getRequestGatewayCode() {
        return _requestGatewayCode;
    }

    public void setRequestGatewayCode(String requestGatewayCode) {
        _requestGatewayCode = requestGatewayCode;
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

    public Date getModifyOn() {
        return _modifyOn;
    }

    public void setModifyOn(Date modifyOn) {
        _modifyOn = modifyOn;
    }

    public String getModifyBy() {
        return _modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        _modifyBy = modifyBy;
    }

    public Integer getSuccessTxn() {
        return _successTxn;
    }

    public void setSuccessTxn(Integer successTxn) {
        _successTxn = successTxn;
    }

    public String getMessageCode() {
        return _messageCode;
    }

    public void setMessageCode(String messageCode) {
        _messageCode = messageCode;
    }

    public String[] getMessageArguments() {
        return _messageArguments;
    }

    public void setMessageArguments(String[] messageArguments) {
        _messageArguments = messageArguments;
    }

}