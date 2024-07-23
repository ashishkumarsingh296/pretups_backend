/*
 * @# SystemPreferenceForm.java
 * This class is a FormBean of SystemPreference Module.
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jun 16, 2005 Sandeep Goel Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.preference.web;

import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;

/**
 */
public class PreferenceForm {

    private String _preferenceCode = null;
    private String _networkCode = null;
    private String _controlCode = null;
    private String _serviceCode = null;
    private String _module = null;// _moduleCode changing it to _moduleCd
                                  // because it conflicts with url modulecode

    private String _networkDescription = null;
    private String _controlDescription = null;
    private String _serviceDescription = null;
    private String _moduleDescription = null;

    private String _name = null;
    private String _type = null;
    private String _valueType = null;
    private String _defaultValue = null;
    private String _minValue = null;
    private String _maxValue = null;
    private String _maxSize = null;
    private String _description = null;
    private String _modifiedAllowed = null;
    private Date _createdOn = null;
    private String _createdBy = null;
    private Date _modifiedOn = null;
    private String _modifiedBy = null;

    private ArrayList _preferenceList = null;
    private ArrayList _serviceClassList = null;
    private ArrayList _moduleList = null;
    private ArrayList _controlList = null;
    private long _lastModifiedTime;
    private boolean _isDisable;
    private ArrayList _allowedValuesList = null;
    private String _preferenceType = null;
    private String _preferenceTypeDesc = null;
    private ArrayList _preferenceTypeList = null;
    private String _valueTypeDesc = null;

    public int getAllowedValuesListSize() {
        if (_allowedValuesList != null) {
            return _allowedValuesList.size();
        }
        return 0;
    }

    public int getPreferenceListSize() {
        if (_preferenceList != null) {
            return _preferenceList.size();
        }
        return 0;
    }

    public PreferenceCacheVO getPreferenceIndexed(int i) {
        return (PreferenceCacheVO) _preferenceList.get(i);
    }

    public void setPreferenceIndexed(int i, PreferenceCacheVO preferenceCacheVO) {
        _preferenceList.set(i, preferenceCacheVO);
    }

    /**
     * Method flush.
     * This method is used to reset all the fiels of the formBean.
     */
    public void flush() {
        _preferenceCode = null;
        _name = null;
        _type = null;
        _valueType = null;
        _defaultValue = null;
        _minValue = null;
        _maxValue = null;
        _maxSize = null;
        _description = null;
        _modifiedAllowed = null;
        _createdOn = null;
        _createdBy = null;
        _modifiedOn = null;
        _modifiedBy = null;
        _preferenceList = null;
        _lastModifiedTime = 0;
        _networkCode = null;
        _controlCode = null;
        _serviceCode = null;
        _module = null;
        _networkDescription = null;
        _controlDescription = null;
        _serviceDescription = null;
        _moduleDescription = null;
        _serviceClassList = null;
        _moduleList = null;
        _isDisable = true;
        _allowedValuesList = null;
        _preferenceType = null;
        _preferenceTypeDesc = null;
        _preferenceTypeList = null;
        _valueTypeDesc = null;

    }

    /**
     * Method semiFlush.
     * This method is to reset all the fields which are required to display the
     * data.
     */
    public void semiFlush() {
        _preferenceCode = null;
        _name = null;
        _type = null;
        _valueType = null;
        _defaultValue = null;
        _minValue = null;
        _maxValue = null;
        _maxSize = null;
        _description = null;
        _modifiedAllowed = null;
        _createdOn = null;
        _createdBy = null;
        _modifiedOn = null;
        _modifiedBy = null;
        _valueTypeDesc = null;

    }

    /**
     * This method gives the value of createdBy
     * 
     * @return String
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * This method is used to set the value of createdBy.
     * 
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * This method gives the value of defaultValue
     * 
     * @return String
     */
    public String getDefaultValue() {
        return _defaultValue;
    }

    /**
     * This method is used to set the value of defaultValue.
     * 
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        _defaultValue = defaultValue;
    }

    /**
     * This method gives the value of description
     * 
     * @return String
     */
    public String getDescription() {
        return _description;
    }

    /**
     * This method is used to set the value of description.
     * 
     * @param description
     */
    public void setDescription(String description) {
        _description = description;
    }

    /**
     * This method gives the value of lastModifiedOn
     * 
     * @return long
     */
    public long getLastModifiedTime() {
        return _lastModifiedTime;
    }

    /**
     * This method is used to set the value of lastModifiedOn.
     * 
     * @param lastModifiedOn
     */
    public void setLastModifiedTime(long lastModifiedOn) {
        _lastModifiedTime = lastModifiedOn;
    }

    /**
     * This method gives the value of maxSize
     * 
     * @return String
     */
    public String getMaxSize() {
        return _maxSize;
    }

    /**
     * This method is used to set the value of maxSize.
     * 
     * @param maxSize
     */
    public void setMaxSize(String maxSize) {
        _maxSize = maxSize;
    }

    /**
     * This method gives the value of maxValue
     * 
     * @return String
     */
    public String getMaxValue() {
        return _maxValue;
    }

    /**
     * This method is used to set the value of maxValue.
     * 
     * @param maxValue
     */
    public void setMaxValue(String maxValue) {
        _maxValue = maxValue;
    }

    /**
     * This method gives the value of minValue
     * 
     * @return String
     */
    public String getMinValue() {
        return _minValue;
    }

    /**
     * This method is used to set the value of minValue.
     * 
     * @param minValue
     */
    public void setMinValue(String minValue) {
        _minValue = minValue;
    }

    /**
     * This method gives the value of modifiedAllowed
     * 
     * @return String
     */
    public String getModifiedAllowed() {
        return _modifiedAllowed;
    }

    /**
     * This method is used to set the value of modifiedAllowed.
     * 
     * @param modifiedAllowed
     */
    public void setModifiedAllowed(String modifiedAllowed) {
        _modifiedAllowed = modifiedAllowed;
    }

    /**
     * This method gives the value of modifiedBy
     * 
     * @return String
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * This method is used to set the value of modifiedBy.
     * 
     * @param modifiedBy
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * This method gives the value of name
     * 
     * @return String
     */
    public String getName() {
        return _name;
    }

    /**
     * This method is used to set the value of name.
     * 
     * @param name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * This method gives the value of preferenceCode
     * 
     * @return String
     */
    public String getPreferenceCode() {
        return _preferenceCode;
    }

    /**
     * This method is used to set the value of preferenceCode.
     * 
     * @param preferenceCode
     */
    public void setPreferenceCode(String preferenceCode) {
        _preferenceCode = preferenceCode;
    }

    /**
     * This method gives the value of preferenceList
     * 
     * @return ArrayList
     */
    public ArrayList getPreferenceList() {
        return _preferenceList;
    }

    /**
     * This method is used to set the value of preferenceList.
     * 
     * @param preferenceList
     */
    public void setPreferenceList(ArrayList preferenceList) {
        _preferenceList = preferenceList;
    }

    /**
     * This method gives the value of type
     * 
     * @return String
     */
    public String getType() {
        return _type;
    }

    /**
     * This method is used to set the value of type.
     * 
     * @param type
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * This method gives the value of valueType
     * 
     * @return String
     */
    public String getValueType() {
        return _valueType;
    }

    /**
     * This method is used to set the value of valueType.
     * 
     * @param valueType
     */
    public void setValueType(String valueType) {
        _valueType = valueType;
    }

    /**
     * This method gives the value of createdOn
     * 
     * @return Date
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * This method is used to set the value of createdOn.
     * 
     * @param createdOn
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * This method gives the value of modifiedOn
     * 
     * @return Date
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * This method is used to set the value of modifiedOn.
     * 
     * @param modifiedOn
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * This method gives the value of isDisable
     * 
     * @return boolean
     */
    public boolean getIsDisable() {
        return _isDisable;
    }

    /**
     * This method is used to set the value of isDisable.
     * 
     * @param isDisable
     */
    public void setIsDisable(boolean isDisable) {
        _isDisable = isDisable;
    }

    /**
     * This method gives the value of networkCode
     * 
     * @return String
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * This method is used to set the value of networkCode.
     * 
     * @param networkCode
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * This method gives the value of networkDescription
     * 
     * @return String
     */
    public String getNetworkDescription() {
        return _networkDescription;
    }

    /**
     * This method is used to set the value of networkDescription.
     * 
     * @param networkDescription
     */
    public void setNetworkDescription(String networkDescription) {
        _networkDescription = networkDescription;
    }

    /**
     * This method gives the value of serviceCode
     * 
     * @return String
     */
    public String getServiceCode() {
        return _serviceCode;
    }

    /**
     * This method is used to set the value of serviceCode.
     * 
     * @param serviceCode
     */
    public void setServiceCode(String serviceCode) {
        _serviceCode = serviceCode;
    }

    /**
     * This method gives the value of serviceDescription
     * 
     * @return String
     */
    public String getServiceDescription() {
        return _serviceDescription;
    }

    /**
     * This method is used to set the value of serviceDescription.
     * 
     * @param serviceDescription
     */
    public void setServiceDescription(String serviceDescription) {
        _serviceDescription = serviceDescription;
    }

    /**
     * This method gives the value of zoneCode
     * 
     * @return String
     */
    public String getControlCode() {
        return _controlCode;
    }

    /**
     * This method is used to set the value of zoneCode.
     * 
     * @param zoneCode
     */
    public void setControlCode(String zoneCode) {
        _controlCode = zoneCode;
    }

    /**
     * This method gives the value of zoneDescription
     * 
     * @return String
     */
    public String getControlDescription() {
        return _controlDescription;
    }

    /**
     * This method is used to set the value of zoneDescription.
     * 
     * @param zoneDescription
     */
    public void setControlDescription(String zoneDescription) {
        _controlDescription = zoneDescription;
    }

    /**
     * This method gives the value of moduleCode
     * 
     * @return String
     */
    public String getModule() {
        return _module;
    }

    /**
     * This method is used to set the value of moduleCode.
     * 
     * @param moduleCode
     */
    public void setModule(String moduleCode) {
        _module = moduleCode;
    }

    /**
     * This method gives the value of moduleDescription
     * 
     * @return String
     */
    public String getModuleDescription() {
        return _moduleDescription;
    }

    /**
     * This method is used to set the value of moduleDescription.
     * 
     * @param moduleDescription
     */
    public void setModuleDescription(String moduleDescription) {
        _moduleDescription = moduleDescription;
    }

    /**
     * This method gives the value of serviceClassList
     * 
     * @return ArrayList
     */
    public ArrayList getServiceClassList() {
        return _serviceClassList;
    }

    /**
     * This method is used to set the value of serviceClassList.
     * 
     * @param serviceClassList
     */
    public void setServiceClassList(ArrayList serviceClassList) {
        _serviceClassList = serviceClassList;
    }

    /**
     * This method gives the value of moduleList
     * 
     * @return ArrayList
     */
    public ArrayList getModuleList() {
        return _moduleList;
    }

    /**
     * This method is used to set the value of moduleList.
     * 
     * @param moduleList
     */
    public void setModuleList(ArrayList moduleList) {
        _moduleList = moduleList;
    }

    public ArrayList getControlList() {
        return _controlList;
    }

    public void setControlList(ArrayList zoneList) {
        _controlList = zoneList;
    }

    public ArrayList getAllowedValuesList() {
        return _allowedValuesList;
    }

    public void setAllowedValuesList(ArrayList fixedValueList) {
        _allowedValuesList = fixedValueList;
    }

    public String getPreferenceType() {
        return _preferenceType;
    }

    public void setPreferenceType(String preferenceType) {
        _preferenceType = preferenceType;
    }

    public String getPreferenceTypeDesc() {
        return _preferenceTypeDesc;
    }

    public void setPreferenceTypeDesc(String preferenceTypeDesc) {
        _preferenceTypeDesc = preferenceTypeDesc;
    }

    public ArrayList getPreferenceTypeList() {
        return _preferenceTypeList;
    }

    public void setPreferenceTypeList(ArrayList preferenceTypeList) {
        _preferenceTypeList = preferenceTypeList;
    }

    public void setDisable(boolean isDisable) {
        _isDisable = isDisable;
    }

    public String getValueTypeDesc() {
        return _valueTypeDesc;
    }

    public void setValueTypeDesc(String valueTypeDesc) {
        _valueTypeDesc = valueTypeDesc;
    }

}
