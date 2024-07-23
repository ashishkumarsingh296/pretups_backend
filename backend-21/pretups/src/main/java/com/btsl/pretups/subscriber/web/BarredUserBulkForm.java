/*
 * #BarredUserForm.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jun 23, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.subscriber.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;

public class BarredUserBulkForm {
    private String _module; // name of the module
    private String _moduleName;
    private String _networkCode; // Network code
    private String _networkName;
    private String _msisdn = null; // msisdn
    private String _name; // user name
    private String _barredReason = null;// reason of barring
    private String _userType;// type of user
    private String _barredType;//
    private String _barredTypeName;
    private String _userTypeName;
    private String _createdOn;
    private String _createdBy;
    private String _modifiedOn;
    private String _modifiedBy;
    //private FormFile _fileName;
    private ArrayList _moduleList;
    private ArrayList _barredUserTypeList;
    private ArrayList _barredTypeList;
    private ArrayList _viewBarredList;
    private String _fileNameStr;
    private String _invalidMsisdnStr;
    private ArrayList _msisdnList;

    /**
     * @return Returns the invalidMsisdnStr.
     */
    public String getInvalidMsisdnStr() {
        return _invalidMsisdnStr;
    }

    /**
     * @param invalidMsisdnStr
     *            The invalidMsisdnStr to set.
     */
    public void setInvalidMsisdnStr(String invalidMsisdnStr) {
        _invalidMsisdnStr = invalidMsisdnStr;
    }

    /**
     * @return Returns the fileNameStr.
     */
    public String getFileNameStr() {
        return _fileNameStr;
    }

    /**
     * @param fileNameStr
     *            The fileNameStr to set.
     */
    public void setFileNameStr(String fileNameStr) {
        _fileNameStr = fileNameStr;
    }

    private String _toDate;
    private String _fromDate;

    public String getNetworkName() {
        return _networkName;
    }

    public void setNetworkName(String networkName) {
        _networkName = networkName;
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
        if (barredReason != null) {
            _barredReason = barredReason.trim();
        }
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
    public String getCreatedOn() {
        return _createdOn;
    }

    /**
     * To set the value of createdOn field
     */
    public void setCreatedOn(String createdOn) {
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
    public String getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * To set the value of modifiedOn field
     */
    public void setModifiedOn(String modifiedOn) {
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
        if (msisdn != null) {
            _msisdn = msisdn.trim();
        }
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
     * To get the value of userType field
     * 
     * @return userType.
     */
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
     * To get the value of moduleList field
     * 
     * @return moduleList.
     */
    public ArrayList getModuleList() {
        return _moduleList;
    }

    /**
     * To set the value of moduleList field
     */
    public void setModuleList(ArrayList moduleList) {
        _moduleList = moduleList;
    }

    /**
     * To get the value of barredTypeList field
     * 
     * @return barredTypeList.
     */
    public ArrayList getBarredTypeList() {
        return _barredTypeList;
    }

    /**
     * To set the value of barredTypeList field
     */
    public void setBarredTypeList(ArrayList barredTypeList) {
        _barredTypeList = barredTypeList;
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
     * To get the value of viewBarredList field
     * 
     * @return viewBarredList.
     */
    public ArrayList getViewBarredList() {
        return _viewBarredList;
    }

    /**
     * To set the value of viewBarredList field
     */
    public void setViewBarredList(ArrayList viewBarredList) {
        _viewBarredList = viewBarredList;
    }

    /**
     * To get the value of barredUserTypeList field
     * 
     * @return barredUserTypeList.
     */
    public ArrayList getBarredUserTypeList() {
        return _barredUserTypeList;
    }

    /**
     * To set the value of barredUserTypeList field
     */
    public void setBarredUserTypeList(ArrayList barredUserTypeList) {
        _barredUserTypeList = barredUserTypeList;
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

    public String getUserTypeName() {
        return _userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        _userTypeName = userTypeName;
    }

    public void semiFlush() {
        _module = null;
        _networkCode = null;
        _networkName = null;
        _msisdn = null;
        _name = null;
        _barredReason = null;
        _userType = null;
        _barredType = null;
        _createdOn = null;
        _createdBy = null;
        _modifiedOn = null;
        _modifiedBy = null;
    }

    // Flush all the form contents

    public void flush() {
        _module = null;
        _networkCode = null;
        _networkName = null;
        _msisdn = null;
        _name = null;
        _barredReason = null;
        _userType = null;
        _barredType = null;
        _createdOn = null;
        _createdBy = null;
        _modifiedOn = null;
        _modifiedBy = null;
        _moduleList = null;
        _barredTypeList = null;
        _userTypeName = null;
        _invalidMsisdnStr = null;
        _msisdnList = null;
    }


    public BarredUserVO getBarredUserVOIndexed(int index) {
        if (_viewBarredList != null) {
            return (BarredUserVO) _viewBarredList.get(index);
        } else {
            return null;
        }
    }


    /**
     * @return Returns the msisdnList.
     */
    public ArrayList getMsisdnList() {
        return _msisdnList;
    }

    /**
     * @param msisdnList
     *            The msisdnList to set.
     */
    public void setMsisdnList(ArrayList msisdnList) {
        _msisdnList = msisdnList;
    }
}
