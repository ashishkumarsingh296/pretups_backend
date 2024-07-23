package com.btsl.ota.services.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * 
 * @(#)ServicesVO.java Copyright(c) 2003, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 *                     --------------------------------------------------------
 *                     ----------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     ----------
 *                     Abhijit Singh 04/11/03 Initial Creation
 *                     Gurjeet Singh Bedi 23/12/03 Modified
 *                     --------------------------------------------------------
 *                     ----------
 */

public class ServicesVO implements Serializable {
    protected java.lang.String _userType;
    protected java.lang.String _userTypeName;
    protected int _position;
    protected long _offSet;
    protected long _length;
    protected ArrayList _wmlErrorList;
    protected int _validityPeriod;
    protected java.lang.String _positionList;
    protected java.lang.String _serviceID;
    protected java.lang.String _serviceSetID;
    protected java.lang.String _name;
    protected java.lang.String _majorVersion;
    protected java.lang.String _minorVersion;
    protected java.lang.String _status;
    protected java.lang.String _circle;
    protected java.lang.String _description;
    protected java.lang.String _label1;
    protected java.lang.String _label2;
    protected java.lang.String _wml;
    protected java.lang.String _operation;
    protected java.lang.String _typeOfEnquiry;
    protected boolean _encrypt;
    protected java.lang.String _byteCode;
    protected java.lang.String _createdBy;
    protected java.lang.String _modifiedBy;
    protected java.lang.String _smscGatewayNo;
    protected java.util.Date _createdOn;
    protected java.util.Date _modifiedOn;
    protected java.lang.String _compareHexString;
    protected java.lang.String _langMenuData;
    protected ArrayList _userTypeList;
    protected java.lang.String _msisdn;
    protected java.lang.String _transactionId;
    protected java.lang.String _key;
    protected java.lang.String _allowedToUsers;
    protected int _radioIndex;
    protected String _version;
    protected String _modifiedOnAsString;

    /**
     * To get the value of modifiedOnAsString field
     * 
     * @return modifiedOnAsString.
     */
    public String getModifiedOnAsString() {
        return _modifiedOnAsString;
    }

    /**
     * To set the value of modifiedOnAsString field
     */
    public void setModifiedOnAsString(String modifiedOnAsString) {
        _modifiedOnAsString = modifiedOnAsString;
    }

    /**
     * To set the value of modifiedOn field
     */
    public void setModifiedOn(java.util.Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * To set the value of version field
     */
    public void setVersion(String version) {
        _version = version;
    }

    /**
     * To get the value of version field
     * 
     * @return version.
     */
    public String getVersion() {
        return _majorVersion + " " + _minorVersion;
    }

    public java.lang.String getCircle() {
        return _circle;
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
    public java.util.Date getModifiedOn() {
        return _modifiedOn;
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
    public void setCircle(java.lang.String string) {
        _circle = string;
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
        _modifiedOn = date;
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
    public java.lang.String getByteCode() {
        return _byteCode;
    }

    /**
     * @return
     */
    public java.lang.String getWml() {
        return _wml;
    }

    /**
     * @param string
     */
    public void setByteCode(java.lang.String string) {
        _byteCode = string;
    }

    /**
     * @param string
     */
    public void setWml(java.lang.String string) {
        _wml = string;
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
    public java.lang.String getName() {
        return _name;
    }

    /**
     * @param string
     */
    public void setName(java.lang.String string) {
        _name = string;
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
    public boolean isEncrypt() {
        return _encrypt;
    }

    /**
     * @param b
     */
    public void setEncrypt(boolean b) {
        _encrypt = b;
    }

    /**
     * @return
     */
    public java.lang.String getPositionList() {
        return _positionList;
    }

    /**
     * @param string
     */
    public void setPositionList(java.lang.String string) {
        _positionList = string;
    }

    /**
     * @return
     */
    public ArrayList getUserTypeList() {
        return _userTypeList;
    }

    /**
     * @param list
     */
    public void setUserTypeList(ArrayList list) {
        _userTypeList = list;
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
    public long getOffSet() {
        return _offSet;
    }

    /**
     * @param i
     */
    public void setLength(long i) {
        _length = i;
    }

    /**
     * @param i
     */
    public void setOffSet(long i) {
        _offSet = i;
    }

    /**
     * @return
     */
    public java.lang.String getSmscGatewayNo() {
        return _smscGatewayNo;
    }

    /**
     * @param string
     */
    public void setSmscGatewayNo(java.lang.String string) {
        _smscGatewayNo = string;
    }

    /**
     * @return
     */
    public int getValidityPeriod() {
        return _validityPeriod;
    }

    /**
     * @param i
     */
    public void setValidityPeriod(int i) {
        _validityPeriod = i;
    }

    /**
     * @return
     */
    public ArrayList getWmlErrorList() {
        return _wmlErrorList;
    }

    /**
     * @param list
     */
    public void setWmlErrorList(ArrayList list) {
        _wmlErrorList = list;
    }

    /**
     * @return
     */
    public java.lang.String getUserTypeName() {
        return _userTypeName;
    }

    /**
     * @param string
     */
    public void setUserTypeName(java.lang.String string) {
        _userTypeName = string;
    }

    /**
     * @return
     */
    public java.lang.String getTypeOfEnquiry() {
        return _typeOfEnquiry;
    }

    /**
     * @param string
     */
    public void setTypeOfEnquiry(java.lang.String string) {
        _typeOfEnquiry = string;
    }

    /**
     * @return
     */
    public java.lang.String getCompareHexString() {
        return _compareHexString;
    }

    /**
     * @param string
     */
    public void setCompareHexString(java.lang.String string) {
        _compareHexString = string;
    }

    /**
     * @return
     */
    public java.lang.String getLangMenuData() {
        return _langMenuData;
    }

    /**
     * @param string
     */
    public void setLangMenuData(java.lang.String string) {
        _langMenuData = string;
    }

    /**
     * @return
     */
    public java.lang.String getKey() {
        return _key;
    }

    /**
     * @return
     */
    public java.lang.String getMsisdn() {
        return _msisdn;
    }

    /**
     * @return
     */
    public java.lang.String getTransactionId() {
        return _transactionId;
    }

    /**
     * @param string
     */
    public void setKey(java.lang.String string) {
        _key = string;
    }

    /**
     * @param string
     */
    public void setMsisdn(java.lang.String string) {
        _msisdn = string;
    }

    /**
     * @param string
     */
    public void setTransactionId(java.lang.String string) {
        _transactionId = string;
    }

    /**
     * @return
     */
    public java.lang.String getAllowedToUsers() {
        return _allowedToUsers;
    }

    /**
     * @param string
     */
    public void setAllowedToUsers(java.lang.String string) {
        _allowedToUsers = string;
    }

    /**
     * To get the value of radioIndex field
     * 
     * @return radioIndex.
     */
    public int getRadioIndex() {
        return _radioIndex;
    }

    /**
     * To set the value of radioIndex field
     */
    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }
}
