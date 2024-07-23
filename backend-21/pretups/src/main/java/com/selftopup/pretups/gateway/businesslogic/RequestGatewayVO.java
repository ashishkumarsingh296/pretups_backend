/*
 * @# RequestGatewayVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 7, 2005 Sandeep Goel Initial creation
 * Change 1 for the file Appslab_BugReport_Super and Network
 * Admin_PreTUPS5.0.xls
 * Bugs fixed, No.23 .Fixed on 16/10/06 by Nitin
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.gateway.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.selftopup.common.TypesI;
import com.selftopup.util.Constants;

public class RequestGatewayVO implements Serializable {
    private String _gatewayCode;
    private String _port;
    private String _servicePort;
    private String _loginID;
    private String _password;
    private String _encryptionLevel;
    private String _encryptionKey;
    private String _contentType;
    private String _authType;
    private String _status;
    private String _confirmPassword;
    private String _oldPassword;
    private String _decryptedPassword;

    private Date _modifiedOn;
    private Timestamp _modifiedOnTimestamp;
    private String _modifiedBy;
    private Date _createdOn;
    private String _createdBy;
    private long _lastModifiedTime = 0;// this field is to keep the last
                                       // modified time for the transaction
                                       // control during the transaction
    private String _underProcessCheckReqd = TypesI.YES;
    private String _updatePassword; // added for updatepassword change during
                                    // implementation of hashing.

    public void flushRequestVO() {
        this._gatewayCode = null;
        this._port = null;
        this._servicePort = null;
        this._loginID = null;
        this._password = null;
        this._encryptionLevel = null;
        this._encryptionKey = null;
        this._contentType = null;
        this._authType = null;
        this._status = null;
        this._confirmPassword = null;
        this._oldPassword = null;
        this._modifiedOn = null;
        this._modifiedBy = null;
        this._createdOn = null;
        this._createdBy = null;
        this._underProcessCheckReqd = TypesI.YES;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedOn) {
        _lastModifiedTime = lastModifiedOn;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public String getConfirmPassword() {
        return _confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        _confirmPassword = confirmPassword;
    }

    public RequestGatewayVO() {

    }

    public String getAuthType() {
        return _authType;
    }

    public void setAuthType(String authType) {
        _authType = authType;
    }

    public String getContentType() {
        return _contentType;
    }

    public void setContentType(String contentType) {
        _contentType = contentType;
    }

    public String getEncryptionKey() {
        return _encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        _encryptionKey = encryptionKey;
    }

    public String getEncryptionLevel() {
        return _encryptionLevel;
    }

    public void setEncryptionLevel(String encryptionLevel) {
        _encryptionLevel = encryptionLevel;
    }

    public String getGatewayCode() {
        return _gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        _gatewayCode = gatewayCode;
    }

    public String getLoginID() {
        return _loginID;
    }

    public void setLoginID(String loginID) {
        _loginID = loginID;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public String getPort() {
        return _port;
    }

    public void setPort(String port) {
        // change 1 Bug No 23 - start
        if (port != null)
            _port = port.trim();
        else
            _port = port;
        // change 1 Bug No 23 - end
    }

    public String getServicePort() {
        return _servicePort;
    }

    public void setServicePort(String servicePort) {
        // change 1 Bug No 23 - start
        if (servicePort != null)
            _servicePort = servicePort.trim();
        else
            _servicePort = servicePort;
        // change 1 Bug No 23 - end
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_gatewayCode=" + _gatewayCode);
        sbf.append(",_port=" + _port);
        sbf.append(",_servicePort=" + _servicePort);
        sbf.append(",_loginID=" + _loginID);
        sbf.append(",_encryptionLevel=" + _encryptionLevel);
        sbf.append(",_encryptionKey=" + _encryptionKey);
        sbf.append(",_contentType=" + _contentType);
        sbf.append(",_authType=" + _authType);
        sbf.append(",_status=" + _status);
        return sbf.toString();
    }

    public boolean equals(RequestGatewayVO requestGatewayVO) {
        boolean flag = false;

        if (requestGatewayVO != null && requestGatewayVO.getModifiedOnTimestamp().equals(this.getModifiedOnTimestamp())) {
            flag = true;
        }
        return flag;
    }

    public String logInfo() {
        StringBuffer sbf = new StringBuffer(10);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Port");
        sbf.append(middleSeperator);
        sbf.append(this.getPort());

        sbf.append(startSeperator);
        sbf.append("Service Port");
        sbf.append(middleSeperator);
        sbf.append(this.getServicePort());

        sbf.append(startSeperator);
        sbf.append("Login ID");
        sbf.append(middleSeperator);
        sbf.append(this.getLoginID());

        sbf.append(startSeperator);
        sbf.append("Password");
        sbf.append(middleSeperator);
        sbf.append(this.getPassword());

        sbf.append(startSeperator);
        sbf.append("Status");
        sbf.append(middleSeperator);
        sbf.append(this.getStatus());

        sbf.append(startSeperator);
        sbf.append("Encryption Level");
        sbf.append(middleSeperator);
        sbf.append(this.getEncryptionLevel());

        sbf.append(startSeperator);
        sbf.append("Encryption Key");
        sbf.append(middleSeperator);
        sbf.append(this.getEncryptionKey());

        sbf.append(startSeperator);
        sbf.append("Content Type");
        sbf.append(middleSeperator);
        sbf.append(this.getContentType());

        sbf.append(startSeperator);
        sbf.append("Auth Type ");
        sbf.append(middleSeperator);
        sbf.append(this.getAuthType());

        return sbf.toString();

    }

    public String differences(RequestGatewayVO gatewayVO) {

        StringBuffer sbf = new StringBuffer(700);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!gatewayVO.getPort().equals(this.getPort())) {
            sbf.append(startSeperator);
            sbf.append("Port");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getPort());
            sbf.append(middleSeperator);
            sbf.append(this.getPort());
        }

        if (!gatewayVO.getServicePort().equals(this.getServicePort())) {
            sbf.append(startSeperator);
            sbf.append("Serivce Port");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getServicePort());
            sbf.append(middleSeperator);
            sbf.append(this.getServicePort());
        }

        if (!gatewayVO.getLoginID().equals(this.getLoginID())) {
            sbf.append(startSeperator);
            sbf.append("Login ID");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getLoginID());
            sbf.append(middleSeperator);
            sbf.append(this.getLoginID());
        }

        if (!gatewayVO.getPassword().equals(this.getPassword())) {
            sbf.append(startSeperator);
            sbf.append("Password");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getPassword());
            sbf.append(middleSeperator);
            sbf.append(this.getPassword());
        }

        if (!gatewayVO.getStatus().equals(this.getStatus())) {
            sbf.append(startSeperator);
            sbf.append("Status");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getStatus());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());
        }

        if (!gatewayVO.getEncryptionLevel().equals(this.getEncryptionLevel())) {
            sbf.append(startSeperator);
            sbf.append("Encryption Level");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getEncryptionLevel());
            sbf.append(middleSeperator);
            sbf.append(this.getEncryptionLevel());
        }

        if (!gatewayVO.getEncryptionKey().equals(this.getEncryptionKey())) {
            sbf.append(startSeperator);
            sbf.append("Encryption Key");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getEncryptionKey());
            sbf.append(middleSeperator);
            sbf.append(this.getEncryptionKey());
        }

        if (!gatewayVO.getContentType().equals(this.getContentType())) {
            sbf.append(startSeperator);
            sbf.append("Content Type ");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getContentType());
            sbf.append(middleSeperator);
            sbf.append(this.getContentType());
        }

        if (!gatewayVO.getAuthType().equals(this.getAuthType())) {
            sbf.append(startSeperator);
            sbf.append("Auth Type");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getAuthType());
            sbf.append(middleSeperator);
            sbf.append(this.getAuthType());
        }

        return sbf.toString();
    }

    public String getOldPassword() {
        return _oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        _oldPassword = oldPassword;
    }

    public Timestamp getModifiedOnTimestamp() {
        return _modifiedOnTimestamp;
    }

    public void setModifiedOnTimestamp(Timestamp modifiedOnTimestamp) {
        _modifiedOnTimestamp = modifiedOnTimestamp;
    }

    /**
     * @return Returns the decryptedPassword.
     */
    public String getDecryptedPassword() {
        return _decryptedPassword;
    }

    /**
     * @param decryptedPassword
     *            The decryptedPassword to set.
     */
    public void setDecryptedPassword(String decryptedPassword) {
        _decryptedPassword = decryptedPassword;
    }

    public String getUnderProcessCheckReqd() {
        return _underProcessCheckReqd;
    }

    public void setUnderProcessCheckReqd(String underProcessCheckReqd) {
        _underProcessCheckReqd = underProcessCheckReqd;
    }

    public String getUpdatePassword() {
        return _updatePassword;
    }

    public void setUpdatePassword(String password) {
        _updatePassword = password;
    }

}
