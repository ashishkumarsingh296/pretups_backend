/**
 * @(#)UserServicesVO.java Copyright(c) 2003, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         --------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         --------------
 *                         Abhijit Singh 04/11/03 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------
 */

package com.btsl.ota.services.businesslogic;

import java.io.Serializable;

import com.btsl.util.BTSLUtil;

public class UserServicesVO implements Serializable {
    protected java.lang.String _userType;
    protected java.lang.String _profile;
    protected java.lang.String _serviceSetID;
    protected long _length;
    protected long _offset;
    protected int _position;
    protected java.lang.String _byteCode;
    protected java.lang.String _serviceID;
    protected java.lang.String _majorVersion;
    protected java.lang.String _minorVersion;
    protected java.lang.String _newServiceID;
    protected java.lang.String _newMajorVersion;
    protected java.lang.String _newMinorVersion;
    protected java.lang.String _status;
    protected java.lang.String _locationCode;
    protected java.lang.String _locationName;
    protected java.lang.String _description;
    protected java.lang.String _label1;
    protected java.lang.String _label2;
    protected java.lang.String _operation;
    protected java.lang.String _createdBy;
    protected java.lang.String _modifiedBy;
    protected java.util.Date _createdOn;
    protected java.util.Date _modifedOn;
    protected java.lang.String _simProfileId;
    private String _version;
    private String _serviceIDVersion;
    private String _serviceVersion;

    public String getServiceIDVersion() {
        return (_serviceID + "|" + _majorVersion + "|" + _minorVersion).trim();
    }

    public String getServiceVersion() {
        if ((!BTSLUtil.isNullString(_label1)) && (!BTSLUtil.isNullString(_majorVersion)) && (!BTSLUtil.isNullString(_minorVersion))) {
            return _label1 + "(" + _majorVersion + " " + _minorVersion + ")";
        } else {
            return "";
        }
    }

    public String getVersion() {
        if ((!BTSLUtil.isNullString(_majorVersion)) && (!BTSLUtil.isNullString(_minorVersion))) {
            return _majorVersion + " " + _minorVersion;
        } else {
            return "";
        }
    }

    /**
     * To set the value of version field
     */
    public void setVersion(String version) {
        _version = version;
    }

    /**
     * @return
     */
    public java.lang.String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @return
     */
    public java.util.Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @return
     */
    public java.lang.String getMajorVersion() {
        return _majorVersion;
    }

    /**
     * @return
     */
    public java.lang.String getMinorVersion() {
        return _minorVersion;
    }

    /**
     * @return
     */
    public java.util.Date getModifedOn() {
        return _modifedOn;
    }

    /**
     * @return
     */
    public java.lang.String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @return
     */
    public int getPosition() {
        return _position;
    }

    /**
     * @return
     */
    public java.lang.String getServiceID() {
        return _serviceID;
    }

    /**
     * @return
     */
    public java.lang.String getStatus() {
        return _status;
    }

    /**
     * @return
     */
    public java.lang.String getUserType() {
        return _userType;
    }

    /**
     * @param string
     */
    public void setCreatedBy(java.lang.String string) {
        _createdBy = string;
    }

    /**
     * @param date
     */
    public void setCreatedOn(java.util.Date date) {
        _createdOn = date;
    }

    /**
     * @param string
     */
    public void setMajorVersion(java.lang.String string) {
        _majorVersion = string;
    }

    /**
     * @param string
     */
    public void setMinorVersion(java.lang.String string) {
        _minorVersion = string;
    }

    /**
     * @param date
     */
    public void setModifedOn(java.util.Date date) {
        _modifedOn = date;
    }

    /**
     * @param string
     */
    public void setModifiedBy(java.lang.String string) {
        _modifiedBy = string;
    }

    /**
     * @param i
     */
    public void setPosition(int i) {
        _position = i;
    }

    /**
     * @param string
     */
    public void setServiceID(java.lang.String string) {
        _serviceID = string;
    }

    /**
     * @param string
     */
    public void setStatus(java.lang.String string) {
        _status = string;
    }

    /**
     * @param string
     */
    public void setUserType(java.lang.String string) {
        _userType = string;
    }

    /**
     * @return
     */
    public java.lang.String getDescription() {
        return _description;
    }

    /**
     * @return
     */
    public java.lang.String getLabel1() {
        return _label1;
    }

    /**
     * @return
     */
    public java.lang.String getLabel2() {
        return _label2;
    }

    /**
     * @param string
     */
    public void setDescription(java.lang.String string) {
        _description = string;
    }

    /**
     * @param string
     */
    public void setLabel1(java.lang.String string) {
        _label1 = string;
    }

    /**
     * @param string
     */
    public void setLabel2(java.lang.String string) {
        _label2 = string;
    }

    /**
     * @return
     */
    public java.lang.String getLocationCode() {
        return _locationCode;
    }

    /**
     * @return
     */
    public java.lang.String getLocationName() {
        return _locationName;
    }

    /**
     * @param string
     */
    public void setLocationCode(java.lang.String string) {
        _locationCode = string;
    }

    /**
     * @param string
     */
    public void setLocationName(java.lang.String string) {
        _locationName = string;
    }

    /**
     * @return
     */
    public java.lang.String getNewMajorVersion() {
        return _newMajorVersion;
    }

    /**
     * @return
     */
    public java.lang.String getNewMinorVersion() {
        return _newMinorVersion;
    }

    /**
     * @return
     */
    public java.lang.String getNewServiceID() {
        return _newServiceID;
    }

    /**
     * @param string
     */
    public void setNewMajorVersion(java.lang.String string) {
        _newMajorVersion = string;
    }

    /**
     * @param string
     */
    public void setNewMinorVersion(java.lang.String string) {
        _newMinorVersion = string;
    }

    /**
     * @param string
     */
    public void setNewServiceID(java.lang.String string) {
        _newServiceID = string;
    }

    /**
     * @return
     */
    public java.lang.String getProfile() {
        return _profile;
    }

    /**
     * @param string
     */
    public void setProfile(java.lang.String string) {
        _profile = string;
    }

    /**
     * @return
     */
    public long getLength() {
        return _length;
    }

    /**
     * @return
     */
    public long getOffset() {
        return _offset;
    }

    /**
     * @param l
     */
    public void setLength(long l) {
        _length = l;
    }

    /**
     * @param l
     */
    public void setOffset(long l) {
        _offset = l;
    }

    /**
     * @return
     */
    public java.lang.String getByteCode() {
        return _byteCode;
    }

    /**
     * @param string
     */
    public void setByteCode(java.lang.String string) {
        _byteCode = string;
    }

    /**
     * @return
     */
    public java.lang.String getOperation() {
        return _operation;
    }

    /**
     * @param string
     */
    public void setOperation(java.lang.String string) {
        _operation = string;
    }

    /**
     * @return
     */
    public java.lang.String getServiceSetID() {
        return _serviceSetID;
    }

    /**
     * @param string
     */
    public void setServiceSetID(java.lang.String string) {
        _serviceSetID = string;
    }

    /**
     * @return
     */
    public java.lang.String getSimProfileId() {
        return _simProfileId;
    }

    /**
     * @param string
     */
    public void setSimProfileId(java.lang.String string) {
        _simProfileId = string;
    }

}
