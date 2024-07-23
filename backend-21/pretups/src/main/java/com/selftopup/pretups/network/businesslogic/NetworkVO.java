package com.selftopup.pretups.network.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

/**
 * @(#)NetworkVO.java
 *                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                    All Rights Reserved
 * 
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Mohit Goel 26/05/2005 Initial Creation
 * 
 *                    This class holds the values coming from the DB
 * 
 */
public class NetworkVO implements Serializable {

    // --------------------------------------------------------- Instance
    // Variables

    /** networkCode property */
    private String _networkCode;

    /** networkName property */
    private String _networkName;

    /** remarks property */
    private String _remarks;

    /** language2Message property */
    private String _language2Message;

    /** countryCode property */
    private String _countryPrefixCode;

    /** status property */
    private String _status;
    private String _statusDesc;

    /** networkShortName property */
    private String _networkShortName;

    /** address1 property */
    private String _address1;

    /** tax2Value property */
    private String _text2Value;

    /** companyName property */
    private String _companyName;

    /** tax1Value property */
    private String _text1Value;

    /** address2 property */
    private String _address2;

    /** zipCode property */
    private String _zipCode;

    /** country property */
    private String _country;

    /** language1Message property */
    private String _language1Message;

    /** reportHeaderName property */
    private String _reportHeaderName;

    /** state property */
    private String _state;

    /** erpNetworkCode property */
    private String _erpNetworkCode;

    /** networkType property */
    private String _networkType;

    /** city property */
    private String _city;

    private String _serviceSetID;

    /** createdBy property */
    private String _createdBy;

    /** modifiedBy property */
    private String _modifiedBy;

    /** createdOn property */
    private Date _createdOn;

    private long _lastModified;

    /** modifiedOnNew property */
    private Date _modifiedOn;

    private Timestamp _modifiedTimeStamp;

    public Timestamp getModifiedTimeStamp() {
        return _modifiedTimeStamp;
    }

    public void setModifiedTimeStamp(Timestamp modifiedTimeStamp) {
        _modifiedTimeStamp = modifiedTimeStamp;
    }

    /** Sets the networkName value */
    public void setNetworkName(String v) {
        _networkName = v;
    }

    /** Returns the networkName value */
    public String getNetworkName() {
        return _networkName;
    }

    /** Sets the networkCode value */
    public void setNetworkCode(String v) {
        _networkCode = v;
    }

    /** Returns the networkCode value */
    public String getNetworkCode() {
        return _networkCode;
    }

    /** Sets the network Short Name value */
    public void setNetworkShortName(String v) {
        _networkShortName = v;
    }

    /** Returns the network Short Name value */
    public String getNetworkShortName() {
        return _networkShortName;
    }

    /** Sets the company Name value */
    public void setCompanyName(String v) {
        _companyName = v;
    }

    /** Returns the company Name value */
    public String getCompanyName() {
        return _companyName;
    }

    /** Sets the report Header Name value */
    public void setReportHeaderName(String v) {
        _reportHeaderName = v;
    }

    /** Returns the report Header Name value */
    public String getReportHeaderName() {
        return _reportHeaderName;
    }

    /** Sets the erp Network Code value */
    public void setErpNetworkCode(String v) {
        _erpNetworkCode = v;
    }

    /** Returns the erp Network Code value */
    public String getErpNetworkCode() {
        return _erpNetworkCode;
    }

    /** Sets the address1 value */
    public void setAddress1(String v) {
        _address1 = v;
    }

    /** Returns the address1 value */
    public String getAddress1() {
        return _address1;
    }

    /** Sets the address2 value */
    public void setAddress2(String v) {
        _address2 = v;
    }

    /** Returns the address2 value */
    public String getAddress2() {
        return _address2;
    }

    /** Sets the city value */
    public void setCity(String v) {
        _city = v;
    }

    /** Returns the city value */
    public String getCity() {
        return _city;
    }

    /** Sets the state value */
    public void setState(String v) {
        _state = v;
    }

    /** Returns the state value */
    public String getState() {
        return _state;
    }

    /** Sets the zip Code value */
    public void setZipCode(String v) {
        _zipCode = v;
    }

    /** Returns the zip Code value */
    public String getZipCode() {
        return _zipCode;
    }

    /** Sets the country value */
    public void setCountry(String v) {
        _country = v;
    }

    /** Returns the country value */
    public String getCountry() {
        return _country;
    }

    /** Sets the network Type value */
    public void setNetworkType(String v) {
        _networkType = v;
    }

    /** Returns the network Type value */
    public String getNetworkType() {
        return _networkType;
    }

    /** Sets the status value */
    public void setStatus(String v) {
        _status = v;
    }

    /** Returns the status value */
    public String getStatus() {
        return _status;
    }

    /** Sets the remarks value */
    public void setRemarks(String v) {
        _remarks = v;
    }

    /** Returns the remarks value */
    public String getRemarks() {
        return _remarks;
    }

    /** Sets the language1 Message value */
    public void setLanguage1Message(String v) {
        if (v != null)
            _language1Message = v.trim();
        else
            _language1Message = v;
    }

    /** Returns the language1 Message value */
    public String getLanguage1Message() {
        return _language1Message;
    }

    /** Sets the language2 Message value */
    public void setLanguage2Message(String v) {
        if (v != null)
            _language2Message = v.trim();
        else
            _language2Message = v;
    }

    /** Returns the language2 Message value */
    public String getLanguage2Message() {
        return _language2Message;
    }

    /** Sets the tax1 Value value */
    public void setText1Value(String v) {
        _text1Value = v;
    }

    /** Returns the tax1 Value value */
    public String getText1Value() {
        return _text1Value;
    }

    /** Sets the tax2 Value value */
    public void setText2Value(String v) {
        _text2Value = v;
    }

    /** Returns the tax2 Value value */
    public String getText2Value() {
        return _text2Value;
    }

    /** Sets the country Code value */
    public void setCountryPrefixCode(String v) {
        _countryPrefixCode = v;
    }

    /** Returns the country Code value */
    public String getCountryPrefixCode() {
        return _countryPrefixCode;
    }

    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param modifiedBy
     *            The modifiedBy to set.
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * @return Returns the modifiedOnNew.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOnNew
     *            The modifiedOnNew to set.
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * Returns a comma delimited list of the name/value pairs.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("networkName=" + _networkName + ",");
        sb.append("networkCode=" + _networkCode + ",");
        sb.append("networkShortName=" + _networkShortName + ",");
        sb.append("companyName=" + _companyName + ",");
        sb.append("reportHeaderName=" + _reportHeaderName + ",");
        sb.append("erpNetworkCode=" + _erpNetworkCode + ",");
        sb.append("address1=" + _address1 + ",");
        sb.append("address2=" + _address2 + ",");
        sb.append("city=" + _city + ",");
        sb.append("state=" + _state + ",");
        sb.append("zipCode=" + _zipCode + ",");
        sb.append("country=" + _country + ",");
        sb.append("networkType=" + _networkType + ",");
        sb.append("status=" + _status + ",");
        sb.append("remarks=" + _remarks + ",");
        sb.append("language1Message=" + _language1Message + ",");
        sb.append("language2Message=" + _language2Message + ",");
        sb.append("text1Value=" + _text1Value + ",");
        sb.append("text2Value=" + _text2Value + ",");
        sb.append("countryCode=" + _countryPrefixCode + ",");
        sb.append("serviceSetID=" + _serviceSetID + ",");
        sb.append("createdBy=" + _createdBy + ",");
        sb.append("modifiedBy=" + _modifiedBy + ",");
        sb.append("createdOn=" + _createdOn + ",");
        sb.append("lastModified=" + _lastModified + ",");
        sb.append("modifiedOn=" + _modifiedOn + ",");

        return sb.toString();
    }

    public boolean equals(NetworkVO p_networkVO) {
        boolean flag = false;
        if (this.getModifiedTimeStamp().equals(p_networkVO.getModifiedTimeStamp())) {
            flag = true;
        }
        return flag;
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(10);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Name");
        sbf.append(middleSeperator);
        sbf.append(this.getNetworkName());

        sbf.append(startSeperator);
        sbf.append("Short Name");
        sbf.append(middleSeperator);
        sbf.append(this.getNetworkName());

        sbf.append(startSeperator);
        sbf.append("Company Name");
        sbf.append(middleSeperator);
        sbf.append(this.getCompanyName());

        sbf.append(startSeperator);
        sbf.append("ERP Code");
        sbf.append(middleSeperator);
        sbf.append(this.getErpNetworkCode());

        sbf.append(startSeperator);
        sbf.append("Status");
        sbf.append(middleSeperator);
        sbf.append(this.getStatus());

        sbf.append(startSeperator);
        sbf.append("Language Message 1");
        sbf.append(middleSeperator);
        sbf.append(this.getLanguage1Message());

        sbf.append(startSeperator);
        sbf.append("Language Message 2");
        sbf.append(middleSeperator);
        sbf.append(this.getLanguage2Message());

        sbf.append(startSeperator);
        sbf.append("Language Message 1");
        sbf.append(middleSeperator);
        sbf.append(this.getLanguage1Message());

        sbf.append(startSeperator);
        sbf.append("Prefix Code");
        sbf.append(middleSeperator);
        sbf.append(this.getCountryPrefixCode());

        return sbf.toString();

    }

    /**
     * 
     * @param p_networkVO
     *            is currentNetwork
     * @return
     *         String
     */
    public String differnces(NetworkVO p_networkVO) {

        StringBuffer sbf = new StringBuffer(10);

        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        if (!BTSLUtil.isNullString(this.getNetworkName()) && !BTSLUtil.isNullString(p_networkVO.getNetworkName()) && !BTSLUtil.compareLocaleString(this.getNetworkName(), p_networkVO.getNetworkName())) {
            sbf.append(startSeperator);
            sbf.append("Name");
            sbf.append(middleSeperator);
            sbf.append(p_networkVO.getNetworkName());
            sbf.append(middleSeperator);
            sbf.append(this.getNetworkName());
        }

        if (!BTSLUtil.isNullString(this.getNetworkShortName()) && !BTSLUtil.isNullString(p_networkVO.getNetworkShortName()) && !BTSLUtil.compareLocaleString(this.getNetworkShortName(), p_networkVO.getNetworkShortName())) {
            sbf.append(startSeperator);
            sbf.append("Short Name");
            sbf.append(middleSeperator);
            sbf.append(p_networkVO.getNetworkShortName());
            sbf.append(middleSeperator);
            sbf.append(this.getNetworkShortName());
        }

        if (!BTSLUtil.isNullString(this.getCompanyName()) && !BTSLUtil.isNullString(p_networkVO.getCompanyName()) && !BTSLUtil.compareLocaleString(this.getCompanyName(), p_networkVO.getCompanyName())) {

            sbf.append(startSeperator);
            sbf.append("Company Name");
            sbf.append(middleSeperator);
            sbf.append(p_networkVO.getCompanyName());
            sbf.append(middleSeperator);
            sbf.append(this.getCompanyName());
        }

        if (!BTSLUtil.isNullString(this.getErpNetworkCode()) && !BTSLUtil.isNullString(p_networkVO.getErpNetworkCode()) && !this.getErpNetworkCode().equals(p_networkVO.getErpNetworkCode())) {
            sbf.append(startSeperator);
            sbf.append("ERP Code");
            sbf.append(middleSeperator);
            sbf.append(p_networkVO.getErpNetworkCode());
            sbf.append(middleSeperator);
            sbf.append(this.getErpNetworkCode());
        }

        if (!BTSLUtil.isNullString(this.getStatus()) && !BTSLUtil.isNullString(p_networkVO.getStatus()) && !this.getStatus().equals(p_networkVO.getStatus())) {
            sbf.append(startSeperator);
            sbf.append("Status");
            sbf.append(middleSeperator);
            sbf.append(p_networkVO.getStatus());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());

        }

        if (this.getLanguage1Message() != null && !this.getLanguage1Message().equals(p_networkVO.getLanguage1Message())) {
            sbf.append(startSeperator);
            sbf.append("Language Message 1");
            sbf.append(middleSeperator);
            sbf.append(p_networkVO.getLanguage1Message());
            sbf.append(middleSeperator);
            sbf.append(this.getLanguage1Message());
        }

        if (!BTSLUtil.isNullString(this.getLanguage2Message()) && !BTSLUtil.isNullString(p_networkVO.getLanguage2Message()) && !BTSLUtil.compareLocaleString(this.getLanguage2Message(), p_networkVO.getLanguage2Message())) {

            sbf.append(startSeperator);
            sbf.append("Language Message 2");
            sbf.append(middleSeperator);
            sbf.append(p_networkVO.getLanguage2Message());
            sbf.append(middleSeperator);
            sbf.append(this.getLanguage2Message());
        }

        if (this.getCountryPrefixCode() != null && !this.getCountryPrefixCode().equals(p_networkVO.getCountryPrefixCode())) {
            sbf.append(startSeperator);
            sbf.append("Prefixes Code");
            sbf.append(middleSeperator);
            sbf.append(p_networkVO.getCountryPrefixCode());
            sbf.append(middleSeperator);
            sbf.append(this.getCountryPrefixCode());
        }
        return sbf.toString();
    }

    /**
     * @return Returns the lastModified.
     */
    public long getLastModified() {
        return _lastModified;
    }

    /**
     * @param lastModified
     *            The lastModified to set.
     */
    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
    }

    /**
     * @return Returns the statusDesc.
     */
    public String getStatusDesc() {
        return _statusDesc;
    }

    /**
     * @param statusDesc
     *            The statusDesc to set.
     */
    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
    }

    /**
     * @return Returns the serviceSetID.
     */
    public String getServiceSetID() {
        return _serviceSetID;
    }

    /**
     * @param serviceSetID
     *            The serviceSetID to set.
     */
    public void setServiceSetID(String serviceSetID) {
        _serviceSetID = serviceSetID;
    }
} // END CLASS
