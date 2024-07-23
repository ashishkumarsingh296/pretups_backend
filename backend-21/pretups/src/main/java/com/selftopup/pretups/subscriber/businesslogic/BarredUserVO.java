/*
 * #BarredUserVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jun 23, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.selftopup.pretups.subscriber.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class BarredUserVO implements Serializable {
    private String _module; // name of the module
    private String _moduleName;
    private String _networkCode; // Network code
    private String _networkName;
    private String _msisdn; // msisdn
    private String _name; // user name
    private String _barredReason;// reason of barring
    private String _userType;// type of user
    private String _barredType;//
    private String _barredTypeName;
    private Date _createdOn;
    private String _barredDate;
    private String _createdBy;
    private Date _modifiedOn;
    private String _modifiedBy;

    private String _toDate;
    private String _fromDate;
    private boolean _isBar;

    private String _multiBox;
    private String _userTypeDesc;// type of user

    public String toString() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" Module=" + _module);
        strBuff.append(" Module Name=" + _moduleName);
        strBuff.append(" Network Code=" + _networkCode);
        strBuff.append(" Network Name=" + _networkName);
        strBuff.append(" Msisdn=" + _msisdn);
        strBuff.append(" Name=" + _name);
        strBuff.append(" Barred Reason=" + _barredReason);
        strBuff.append(" Barred Type=" + _barredType);
        strBuff.append(" Barred Type Name=" + _barredTypeName);
        strBuff.append(" Created On=" + _createdOn);
        strBuff.append(" Barring Date=" + _barredDate);
        strBuff.append(" Created By=" + _createdBy);
        strBuff.append(" Modified On=" + _modifiedOn);
        strBuff.append(" Modified By=" + _modifiedBy);
        return strBuff.toString();
    }

    /**
     * To get the value of barredReason field
     * 
     * @return barredReason.
     */
    public String getBarredReason() {
        return _barredReason;
    }

    /**
     * To set the value of barredReason field
     */
    public void setBarredReason(String barredReason) {
        _barredReason = barredReason;
    }

    /**
     * To get the value of barredType field
     * 
     * @return barredType.
     */
    public String getBarredType() {
        return _barredType;
    }

    /**
     * To set the value of barredType field
     */
    public void setBarredType(String barredType) {
        _barredType = barredType;
    }

    /**
     * To get the value of barredTypeName field
     * 
     * @return barredTypeName.
     */
    public String getBarredTypeName() {
        return _barredTypeName;
    }

    /**
     * To set the value of barredTypeName field
     */
    public void setBarredTypeName(String barredTypeName) {
        _barredTypeName = barredTypeName;
    }

    /**
     * To get the value of createdBy field
     * 
     * @return createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * To set the value of createdBy field
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * To get the value of createdOn field
     * 
     * @return createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * To set the value of createdOn field
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * To get the value of modifiedBy field
     * 
     * @return modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * To set the value of modifiedBy field
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * To get the value of modifiedOn field
     * 
     * @return modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * To set the value of modifiedOn field
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * To get the value of module field
     * 
     * @return module.
     */
    public String getModule() {
        return _module;
    }

    /**
     * To set the value of module field
     */
    public void setModule(String module) {
        _module = module;
    }

    /**
     * To get the value of moduleName field
     * 
     * @return moduleName.
     */
    public String getModuleName() {
        return _moduleName;
    }

    /**
     * To set the value of moduleName field
     */
    public void setModuleName(String moduleName) {
        _moduleName = moduleName;
    }

    /**
     * To get the value of msisdn field
     * 
     * @return msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * To set the value of msisdn field
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * To get the value of name field
     * 
     * @return name.
     */
    public String getName() {
        return _name;
    }

    /**
     * To set the value of name field
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * To get the value of networkCode field
     * 
     * @return networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * To set the value of networkCode field
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * To get the value of networkName field
     * 
     * @return networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * To set the value of networkName field
     */
    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    public String getUserType() {
        return _userType;
    }

    /**
     * To set the value of userType field
     */
    public void setUserType(String userType) {
        _userType = userType;
    }

    /**
     * To get the value of barredDate field
     * 
     * @return barredDate.
     */
    public String getBarredDate() {
        return _barredDate;
    }

    /**
     * To set the value of barredDate field
     */
    public void setBarredDate(String barredDate) {
        _barredDate = barredDate;
    }

    /**
     * To get the value of fromDate field
     * 
     * @return fromDate.
     */
    public String getFromDate() {
        return _fromDate;
    }

    /**
     * To set the value of fromDate field
     */
    public void setFromDate(String fromDate) {
        _fromDate = fromDate;
    }

    /**
     * To get the value of toDate field
     * 
     * @return toDate.
     */
    public String getToDate() {
        return _toDate;
    }

    /**
     * To set the value of toDate field
     */
    public void setToDate(String toDate) {
        _toDate = toDate;
    }

    public String getMultiBox() {
        return _multiBox;
    }

    public void setMultiBox(String multiBox) {
        _multiBox = multiBox;
    }

    public String getUserTypeDesc() {
        return _userTypeDesc;
    }

    public void setUserTypeDesc(String userTypeDesc) {
        _userTypeDesc = userTypeDesc;
    }

    /**
     * @return Returns the isBar.
     */
    public boolean isBar() {
        return _isBar;
    }

    /**
     * @param isBar
     *            The isBar to set.
     */
    public void setBar(boolean isBar) {
        _isBar = isBar;
    }
}
