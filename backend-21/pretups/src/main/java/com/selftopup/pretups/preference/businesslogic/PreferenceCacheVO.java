/**
 * @(#)PreferenceCacheVO.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            <description>
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            avinash.kamthan Mar 15, 2005 Initital Creation
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 */

package com.selftopup.pretups.preference.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

/**
 * @author avinash.kamthan
 * 
 */
public class PreferenceCacheVO implements Serializable {

    private String _preferenceCode;
    private String _networkCode;
    private String _controlCode;
    private String _serviceCode;
    private String _moduleCode;
    private String _prefrenceName;
    private String _type;
    private String _valueType;
    private String _value;
    private String _minValue = null;
    private String _maxValue = null;
    private String _maxSize = null;
    private String _description = null;
    private String _modifiedAllowed = null;
    private Date _createdOn = null;
    private String _createdBy = null;
    private String _modifiedBy = null;
    private Date _modifiedOn;
    private Timestamp _modifiedTimeStamp;
    private int _noOfOtherPrefOtherThanAll = 0;

    /**
     * Field _lastModifiedTime.
     * This field is used to check that is the record is modified during the
     * transaction?
     */
    private long _lastModifiedTime;

    private String _fixedValue;
    private String _fixedValueList;
    private String _allowedValues;
    private boolean _disableAllow;
    private String _allowAction;
    private ArrayList _allowedValuesList = null;
    private String _moduleDescription = null;
    private String _valueTypeDesc = null;

    public int getAllowedValuesListSize() {
        if (_allowedValuesList != null)
            return _allowedValuesList.size();
        return 0;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getPreferenceCode() {
        return _preferenceCode;
    }

    public void setPreferenceCode(String preferenceCode) {
        _preferenceCode = preferenceCode;
    }

    public String getPrefrenceName() {
        return _prefrenceName;
    }

    public void setPrefrenceName(String prefrenceName) {
        _prefrenceName = prefrenceName;
    }

    public String getServiceCode() {
        return _serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        _serviceCode = serviceCode;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        _value = value;
    }

    public String getValueType() {
        return _valueType;
    }

    public void setValueType(String valueType) {
        _valueType = valueType;
    }

    public String getControlCode() {
        return _controlCode;
    }

    public void setControlCode(String zoneCode) {
        _controlCode = zoneCode;
    }

    public String toString() {

        StringBuffer sbf = new StringBuffer();

        sbf.append("preferenceCode=" + _preferenceCode);
        sbf.append(",networkCode=" + _networkCode);
        sbf.append(",zoneCode=" + _controlCode);
        sbf.append(",serviceCode=" + _serviceCode);
        sbf.append(",moduleCode=" + _moduleCode);
        sbf.append(",prefrenceName=" + _prefrenceName);
        sbf.append(",type=" + _type);
        sbf.append(",valueType=" + _valueType);
        sbf.append(",value=" + _value);
        sbf.append(",minValue=" + _minValue);
        sbf.append(",maxValue=" + _maxValue);
        sbf.append(",maxSize=" + _maxSize);
        sbf.append(",description=" + _description);
        sbf.append(",modifiedAllowed=" + _modifiedAllowed);
        sbf.append(",createdOn=" + _createdOn);
        sbf.append(",createdBy=" + _createdBy);
        sbf.append(",modifiedOn=" + _modifiedOn);
        sbf.append(",modifiedBy=" + _modifiedBy);
        return sbf.toString();
    }

    public boolean equals(PreferenceCacheVO preferenceCacheVO) {
        boolean flag = false;
        if (this.getModifiedTimeStamp().equals(preferenceCacheVO.getModifiedTimeStamp())) {
            flag = true;
        }

        return flag;
    }

    public String getPreferenceLevel() {

        StringBuffer sbf = new StringBuffer(100);
        sbf.append(this.getPreferenceCode());
        if (this.getServiceCode() != null) {
            sbf.append(" At Service Level");
        } else if (this.getControlCode() != null) {
            sbf.append(" At Zone Level");
        } else if (this.getNetworkCode() != null) {
            sbf.append(" At Network Level");
        } else {
            sbf.append(" At System Level");
        }

        return sbf.toString();
    }

    public String differences(PreferenceCacheVO p_preferenceCacheVO) {

        StringBuffer sbf = new StringBuffer(100);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        if (this.getPrefrenceName() != null && p_preferenceCacheVO.getPrefrenceName() != null && !BTSLUtil.compareLocaleString(this.getPrefrenceName(), p_preferenceCacheVO.getPrefrenceName())) {
            sbf.append(startSeperator);
            sbf.append("Name");
            sbf.append(middleSeperator);
            sbf.append(p_preferenceCacheVO.getPrefrenceName());
            sbf.append(middleSeperator);
            sbf.append(this.getPrefrenceName());
        }

        if (this.getType() != null && p_preferenceCacheVO.getType() != null && !this.getType().equals(p_preferenceCacheVO.getType())) {
            sbf.append(startSeperator);
            sbf.append("Type");
            sbf.append(middleSeperator);
            sbf.append(p_preferenceCacheVO.getType());
            sbf.append(middleSeperator);
            sbf.append(this.getType());
        }

        if (this.getValueType() != null && p_preferenceCacheVO.getValueType() != null && !this.getValueType().equals(p_preferenceCacheVO.getValueType())) {
            sbf.append(startSeperator);
            sbf.append("Value Type");
            sbf.append(middleSeperator);
            sbf.append(p_preferenceCacheVO.getValueType());
            sbf.append(middleSeperator);
            sbf.append(this.getValueType());
        }

        if (this.getValue() != null && p_preferenceCacheVO.getValue() != null && !this.getValue().equals(p_preferenceCacheVO.getValue())) {
            sbf.append(startSeperator);
            sbf.append("Value");
            sbf.append(middleSeperator);
            sbf.append(p_preferenceCacheVO.getValue());
            sbf.append(middleSeperator);
            sbf.append(this.getValue());
        }

        return sbf.toString();
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(100);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        sbf.append(startSeperator);
        sbf.append("Name");
        sbf.append(middleSeperator);
        sbf.append(this.getPrefrenceName());

        sbf.append(startSeperator);
        sbf.append("Type");
        sbf.append(middleSeperator);
        sbf.append(this.getType());

        sbf.append(startSeperator);
        sbf.append("Value Type");
        sbf.append(middleSeperator);
        sbf.append(this.getValueType());

        sbf.append(startSeperator);
        sbf.append("Value");
        sbf.append(middleSeperator);
        sbf.append(this.getValue());

        return sbf.toString();
    }

    public String getModuleCode() {
        return _moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        _moduleCode = moduleCode;
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

    public Timestamp getModifiedTimeStamp() {
        return _modifiedTimeStamp;
    }

    public void setModifiedTimeStamp(Timestamp modifiedTimeStamp) {
        _modifiedTimeStamp = modifiedTimeStamp;
    }

    public int getNoOfOtherPrefOtherThanAll() {
        return _noOfOtherPrefOtherThanAll;
    }

    public void setNoOfOtherPrefOtherThanAll(int noOfOtherPrefOtherThanAll) {
        _noOfOtherPrefOtherThanAll = noOfOtherPrefOtherThanAll;
    }

    public String getFixedValue() {
        return _fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        _fixedValue = fixedValue;
    }

    public String getFixedValueList() {
        return _fixedValueList;
    }

    public void setFixedValueList(String fixedValueList) {
        _fixedValueList = fixedValueList;
    }

    public String getAllowedValues() {
        return _allowedValues;
    }

    public void setAllowedValues(String allowedValues) {
        _allowedValues = allowedValues;
    }

    public String getAllowAction() {
        return _allowAction;
    }

    public void setAllowAction(String allowAction) {
        _allowAction = allowAction;
    }

    public boolean getDisableAllow() {
        return _disableAllow;
    }

    public void setDisableAllow(boolean disableAllow) {
        _disableAllow = disableAllow;
    }

    public ArrayList getAllowedValuesList() {
        return _allowedValuesList;
    }

    public void setAllowedValuesList(ArrayList allowedValuesList) {
        _allowedValuesList = allowedValuesList;
    }

    public String getModuleDescription() {
        return _moduleDescription;
    }

    public void setModuleDescription(String moduleDescription) {
        _moduleDescription = moduleDescription;
    }

    public String getValueTypeDesc() {
        return _valueTypeDesc;
    }

    public void setValueTypeDesc(String valueTypeDesc) {
        _valueTypeDesc = valueTypeDesc;
    }

}
