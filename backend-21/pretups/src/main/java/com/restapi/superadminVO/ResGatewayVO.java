package com.restapi.superadminVO;

import java.sql.Timestamp;
import java.util.Date;

public class ResGatewayVO {

	private String _gatewayCode;

    private String _port = null;

    private String _servicePort = null;

    private String _loginID;

    private String _password;

    private String _destNo;

    private String _status;

    private String _confirmPassword;
    private String _oldPassword;

    private Date _modifiedOn;
    private Timestamp _modifiedOnTimestamp;

    private String _modifiedBy;

    private Date _createdOn;

    private String _createdBy;
    private String _decryptedPassword;

    private String _path;
    private int _timeOut = 0;
    private long _lastModifiedTime = 0;// this field is to keep the last
                                       // modified time for the transaction
                                       // control during the transaction
    private String _updatePassword; // added for updatepassword change during
                                    // implementation of hashing.

	
    public String getOldPassword() {
        return _oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        _oldPassword = oldPassword;
    }

    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedOn) {
        _lastModifiedTime = lastModifiedOn;
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        _path = path;
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

    public ResGatewayVO() {

    }

    public String getDestNo() {
        return _destNo;
    }

    public void setDestNo(String destNo) {
        _destNo = destNo;
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
        }
        // change 1 Bug No 23 - end
    }

    public String getServicePort() {
        return _servicePort;
    }

    public void setServicePort(String servicePort) {
        // change 1 Bug No 23 - start
        if (servicePort != null) {
            _servicePort = servicePort.trim();
        }
        // change 1 Bug No 23 - end
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }
	
    public int getTimeOut() {
        return _timeOut;
    }

    public void setTimeOut(int timeOut) {
        _timeOut = timeOut;
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

    public String getUpdatePassword() {
        return _updatePassword;
    }

    public void setUpdatePassword(String password) {
        _updatePassword = password;
    }
	
	
}
