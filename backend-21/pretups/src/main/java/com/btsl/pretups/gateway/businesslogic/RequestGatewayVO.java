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
package com.btsl.pretups.gateway.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.btsl.common.TypesI;
import com.btsl.util.Constants;

public class RequestGatewayVO implements Serializable {
    private String _gatewayCode;
    private String _port = null;
    private String _servicePort = null;
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
        if (port != null) {
            _port = port.trim();
            // change 1 Bug No 23 - end
        }
    }

    public String getServicePort() {
        return _servicePort;
    }

    public void setServicePort(String servicePort) {
        // change 1 Bug No 23 - start
        if (servicePort != null) {
            _servicePort = servicePort.trim();
            // change 1 Bug No 23 - end
        }
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String toString() {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("_gatewayCode=").append(_gatewayCode);
        strBuild.append(",_port=").append(_port);
        strBuild.append(",_servicePort=").append(_servicePort);
        strBuild.append(",_loginID=").append(_loginID);
        strBuild.append(",_encryptionLevel=").append(_encryptionLevel);
        strBuild.append(",_encryptionKey=").append(_encryptionKey);
        strBuild.append(",_contentType=").append(_contentType);
        strBuild.append(",_authType=").append(_authType);
        strBuild.append(",_status=").append(_status);
        return strBuild.toString();
    }
    @Override
    public boolean equals(Object obj) {
	RequestGatewayVO requestGatewayVO =(RequestGatewayVO)obj;
        boolean flag = false;

        if (requestGatewayVO != null && requestGatewayVO.getModifiedOnTimestamp().equals(this.getModifiedOnTimestamp())) {
            flag = true;
        }
        return flag;
    }
	@Override
    public int hashCode(){
		return super.hashCode();
	}

    public String logInfo() {
        StringBuilder strBuild = new StringBuilder();

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        strBuild.append(startSeperator);
        strBuild.append("Port");
        strBuild.append(middleSeperator);
        strBuild.append(this.getPort());

        strBuild.append(startSeperator);
        strBuild.append("Service Port");
        strBuild.append(middleSeperator);
        strBuild.append(this.getServicePort());

        strBuild.append(startSeperator);
        strBuild.append("Login ID");
        strBuild.append(middleSeperator);
        strBuild.append(this.getLoginID());

        strBuild.append(startSeperator);
        strBuild.append("Password");
        strBuild.append(middleSeperator);
        strBuild.append(this.getPassword());

        strBuild.append(startSeperator);
        strBuild.append("Status");
        strBuild.append(middleSeperator);
        strBuild.append(this.getStatus());

        strBuild.append(startSeperator);
        strBuild.append("Encryption Level");
        strBuild.append(middleSeperator);
        strBuild.append(this.getEncryptionLevel());

        strBuild.append(startSeperator);
        strBuild.append("Encryption Key");
        strBuild.append(middleSeperator);
        strBuild.append(this.getEncryptionKey());

        strBuild.append(startSeperator);
        strBuild.append("Content Type");
        strBuild.append(middleSeperator);
        strBuild.append(this.getContentType());

        strBuild.append(startSeperator);
        strBuild.append("Auth Type ");
        strBuild.append(middleSeperator);
        strBuild.append(this.getAuthType());

        return strBuild.toString();

    }

    public String differences(RequestGatewayVO gatewayVO) {

        StringBuilder strBuild = new StringBuilder();

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!gatewayVO.getPort().equals(this.getPort())) {
            strBuild.append(startSeperator);
            strBuild.append("Port");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getPort());
            strBuild.append(middleSeperator);
            strBuild.append(this.getPort());
        }

        if (!gatewayVO.getServicePort().equals(this.getServicePort())) {
            strBuild.append(startSeperator);
            strBuild.append("Serivce Port");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getServicePort());
            strBuild.append(middleSeperator);
            strBuild.append(this.getServicePort());
        }

        if (!gatewayVO.getLoginID().equals(this.getLoginID())) {
            strBuild.append(startSeperator);
            strBuild.append("Login ID");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getLoginID());
            strBuild.append(middleSeperator);
            strBuild.append(this.getLoginID());
        }

        if (!gatewayVO.getPassword().equals(this.getPassword())) {
            strBuild.append(startSeperator);
            strBuild.append("Password");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getPassword());
            strBuild.append(middleSeperator);
            strBuild.append(this.getPassword());
        }

        if (!gatewayVO.getStatus().equals(this.getStatus())) {
            strBuild.append(startSeperator);
            strBuild.append("Status");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getStatus());
            strBuild.append(middleSeperator);
            strBuild.append(this.getStatus());
        }

        if (!gatewayVO.getEncryptionLevel().equals(this.getEncryptionLevel())) {
            strBuild.append(startSeperator);
            strBuild.append("Encryption Level");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getEncryptionLevel());
            strBuild.append(middleSeperator);
            strBuild.append(this.getEncryptionLevel());
        }

        if (!gatewayVO.getEncryptionKey().equals(this.getEncryptionKey())) {
            strBuild.append(startSeperator);
            strBuild.append("Encryption Key");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getEncryptionKey());
            strBuild.append(middleSeperator);
            strBuild.append(this.getEncryptionKey());
        }

        if (!gatewayVO.getContentType().equals(this.getContentType())) {
            strBuild.append(startSeperator);
            strBuild.append("Content Type ");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getContentType());
            strBuild.append(middleSeperator);
            strBuild.append(this.getContentType());
        }

        if (!gatewayVO.getAuthType().equals(this.getAuthType())) {
            strBuild.append(startSeperator);
            strBuild.append("Auth Type");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getAuthType());
            strBuild.append(middleSeperator);
            strBuild.append(this.getAuthType());
        }

        return strBuild.toString();
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
