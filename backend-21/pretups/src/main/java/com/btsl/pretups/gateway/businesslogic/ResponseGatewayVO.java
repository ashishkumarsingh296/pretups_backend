/*
 * @# ResponseGatewayVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 7, 2005 Sandeep Goel Initial creation
 * Change 1 for the file Appslab_BugReport_Super and Network
 * Admin_PreTUPS5.0.xls
 * Bugs fixed, No.23 and 7.Fixed on 16/10/06 by Nitin
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.gateway.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.btsl.util.Constants;

public class ResponseGatewayVO implements Serializable {
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

    public void flushResponseVO() {
        this._gatewayCode = null;
        this._port = null;
        this._servicePort = null;
        this._loginID = null;
        this._password = null;
        this._destNo = null;
        this._status = null;
        this._confirmPassword = null;
        this._oldPassword = null;
        this._modifiedOn = null;
        this._modifiedBy = null;
        this._createdOn = null;
        this._createdBy = null;
        this._path = null;
        this._timeOut = 0;
    }

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

    public ResponseGatewayVO() {

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

    public String toString() {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("_gatewayCode=").append(_gatewayCode);
        strBuild.append(",_port=").append(_port);
        strBuild.append(",_servicePort=").append(_servicePort);
        strBuild.append(",_loginID=").append(_loginID);
        strBuild.append(",_destNo=").append(_destNo);
        strBuild.append(",_status=").append(_status);
        return strBuild.toString();
    }

    public boolean equalsResponseGatewayVO(ResponseGatewayVO responseGatewayVO) {
        boolean flag = false;

        if (responseGatewayVO != null && responseGatewayVO.getModifiedOnTimestamp().equals(this.getModifiedOnTimestamp())) {
            flag = true;
        }
        return flag;
    }

    @Override
    public native int hashCode();

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
        strBuild.append("Destination Num");
        strBuild.append(middleSeperator);
        strBuild.append(this.getDestNo());

        strBuild.append(startSeperator);
        strBuild.append("Status");
        strBuild.append(middleSeperator);
        strBuild.append(this.getStatus());

        strBuild.append(startSeperator);
        strBuild.append("Path");
        strBuild.append(middleSeperator);
        strBuild.append(this.getPath());

        return strBuild.toString();

    }

    

	@Override
	public native boolean equals(Object obj);

	public String differences(ResponseGatewayVO gatewayVO) {

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

        if (!gatewayVO.getDestNo().equals(this.getDestNo())) {
            strBuild.append(startSeperator);
            strBuild.append("Destination Num");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getDestNo());
            strBuild.append(middleSeperator);
            strBuild.append(this.getDestNo());
        }

        if (!gatewayVO.getStatus().equals(this.getStatus())) {
            strBuild.append(startSeperator);
            strBuild.append("Status");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getStatus());
            strBuild.append(middleSeperator);
            strBuild.append(this.getStatus());
        }

        if (!gatewayVO.getPath().equals(this.getPath())) {
            strBuild.append(startSeperator);
            strBuild.append("Path ");
            strBuild.append(middleSeperator);
            strBuild.append(gatewayVO.getPath());
            strBuild.append(middleSeperator);
            strBuild.append(this.getPath());
        }
        return strBuild.toString();
    }

    public int getTimeOut() {
        return _timeOut;
    }

    public void setTimeOut(int timeOut) {
        _timeOut = timeOut;
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

    public String getUpdatePassword() {
        return _updatePassword;
    }

    public void setUpdatePassword(String password) {
        _updatePassword = password;
    }

}
