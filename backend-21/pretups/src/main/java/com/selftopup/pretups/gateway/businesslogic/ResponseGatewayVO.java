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
package com.selftopup.pretups.gateway.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.selftopup.util.Constants;

public class ResponseGatewayVO implements Serializable {
    private String _gatewayCode;

    private String _port;

    private String _servicePort;

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
        } else {
            _port = port;
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
        } else {
            _servicePort = servicePort;
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
        StringBuffer sbf = new StringBuffer();
        sbf.append("_gatewayCode=" + _gatewayCode);
        sbf.append(",_port=" + _port);
        sbf.append(",_servicePort=" + _servicePort);
        sbf.append(",_loginID=" + _loginID);
        sbf.append(",_destNo=" + _destNo);
        sbf.append(",_status=" + _status);
        return sbf.toString();
    }

    public boolean equals(ResponseGatewayVO responseGatewayVO) {
        boolean flag = false;

        if (responseGatewayVO != null && responseGatewayVO.getModifiedOnTimestamp().equals(this.getModifiedOnTimestamp())) {
            flag = true;
        }
        return flag;
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(700);

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
        sbf.append("Destination Num");
        sbf.append(middleSeperator);
        sbf.append(this.getDestNo());

        sbf.append(startSeperator);
        sbf.append("Status");
        sbf.append(middleSeperator);
        sbf.append(this.getStatus());

        sbf.append(startSeperator);
        sbf.append("Path");
        sbf.append(middleSeperator);
        sbf.append(this.getPath());

        return sbf.toString();

    }

    public String differences(ResponseGatewayVO gatewayVO) {

        StringBuffer sbf = new StringBuffer(10);

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

        if (!gatewayVO.getDestNo().equals(this.getDestNo())) {
            sbf.append(startSeperator);
            sbf.append("Destination Num");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getDestNo());
            sbf.append(middleSeperator);
            sbf.append(this.getDestNo());
        }

        if (!gatewayVO.getStatus().equals(this.getStatus())) {
            sbf.append(startSeperator);
            sbf.append("Status");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getStatus());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());
        }

        if (!gatewayVO.getPath().equals(this.getPath())) {
            sbf.append(startSeperator);
            sbf.append("Path ");
            sbf.append(middleSeperator);
            sbf.append(gatewayVO.getPath());
            sbf.append(middleSeperator);
            sbf.append(this.getPath());
        }
        return sbf.toString();
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
