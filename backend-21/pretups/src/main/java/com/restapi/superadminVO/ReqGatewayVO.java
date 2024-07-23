package com.restapi.superadminVO;

import java.sql.Timestamp;
import java.util.Date;

import com.btsl.common.TypesI;

public class ReqGatewayVO {
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
    //private Timestamp _modifiedOnTimestamp;
    private String _modifiedBy;
    private Date _createdOn;
    private String _createdBy;
    private long _lastModifiedTime = 0;// this field is to keep the last
                                       // modified time for the transaction
                                       // control during the transaction
    private String _underProcessCheckReqd = TypesI.YES;
    private String _updatePassword; // added for updatepassword change during
                                    // implementation of hashing.

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

    public ReqGatewayVO() {

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
    
    public String getOldPassword() {
        return _oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        _oldPassword = oldPassword;
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
