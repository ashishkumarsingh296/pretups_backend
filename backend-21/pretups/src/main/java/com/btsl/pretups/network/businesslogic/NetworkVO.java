package com.btsl.pretups.network.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

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
	 /*
     * This is used to log the network details while suspending and activating
     * the
     * network, during save we check the dataList with dataListOld if any
     * network
     * is suspended or activated at that time we log the network details in the
     * logger file
     */
    private String[] dataListStatusOld;
    
    private List<NetworkVO> dataList;
	/** networkCode property */
    private String _networkCode;
    private String _code;
    /** networkName property */
    private String _networkName;
    private ArrayList _statusList;


    public  ArrayList getstatusList() {
		return _statusList;
	}

	public void setstatusList(ArrayList _statusList) {
		this._statusList = _statusList;
	}

	/** remarks property */

    public List<NetworkVO> getDataList() {
		return dataList;
	}

	public void setDataList(List<NetworkVO> dataList) {
		this.dataList = dataList;
	}

	/** remarks property */

    private String _remarks;

    public String getCode() {
		return _code;
	}

	public void setCode(String _code) {
		this._code = _code;
	}

	/** language2Message property */
    private String _language2Message = null;

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
    private String _language1Message = null;

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
    
 

	private String[] newNetworkCode;
    
    private String[] newNetworkStatus;
    
    private String[] newLanguage1Message;
    
    private String[] newLanguage2Message;
    
    public String[] getNewNetworkCode() {
		return newNetworkCode;
	}

	public void setNewNetworkCode(String[] newNetworkCode) {
		this.newNetworkCode = newNetworkCode;
	}

	public String[] getNewNetworkStatus() {
		return newNetworkStatus;
	}

	public void setNewNetworkStatus(String[] newNetworkStatus) {
		this.newNetworkStatus = newNetworkStatus;
	}

	public String[] getNewLanguage1Message() {
		return newLanguage1Message;
	}

	public void setNewLanguage1Message(String[] newLanguage1Message) {
		this.newLanguage1Message = newLanguage1Message;
	}

	public String[] getNewLanguage2Message() {
		return newLanguage2Message;
	}

	public void setNewLanguage2Message(String[] newLanguage2Message) {
		this.newLanguage2Message = newLanguage2Message;
	}

    
    

	
	public String[] getDataListStatusOld() {
		return dataListStatusOld;
	}

	public void setDataListStatusOld(String[] dataListStatusOld) {
		this.dataListStatusOld = dataListStatusOld;
	}

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
        if (v != null) {
            _language1Message = v.trim();
        }
    }

    /** Returns the language1 Message value */
    public String getLanguage1Message() {
        return _language1Message;
    }

    /** Sets the language2 Message value */
    public void setLanguage2Message(String v) {
        if (v != null) {
            _language2Message = v.trim();
        }
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
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("networkName=").append(_networkName).append(",");
        strBuild.append("networkCode=").append(_networkCode).append(",");
        strBuild.append("networkShortName=").append(_networkShortName).append(",");
        strBuild.append("companyName=").append(_companyName).append(",");
        strBuild.append("reportHeaderName=").append(_reportHeaderName).append(",");
        strBuild.append("erpNetworkCode=").append(_erpNetworkCode).append(",");
        strBuild.append("address1=").append(_address1).append(",");
        strBuild.append("address2=").append(_address2).append(",");
        strBuild.append("city=").append(_city).append(",");
        strBuild.append("state=").append(_state).append(",");
        strBuild.append("zipCode=").append(_zipCode).append(",");
        strBuild.append("country=").append(_country).append(",");
        strBuild.append("networkType=").append(_networkType).append(",");
        strBuild.append("status=").append(_status).append(",");
        strBuild.append("remarks=").append(_remarks).append(",");
        strBuild.append("language1Message=").append(_language1Message).append(",");
        strBuild.append("language2Message=").append(_language2Message).append(",");
        strBuild.append("text1Value=").append(_text1Value).append(",");
        strBuild.append("text2Value=").append(_text2Value).append(",");
        strBuild.append("countryCode=").append(_countryPrefixCode).append(",");
        strBuild.append("serviceSetID=").append(_serviceSetID).append(",");
        strBuild.append("createdBy=").append(_createdBy).append(",");
        strBuild.append("modifiedBy=").append(_modifiedBy).append(",");
        strBuild.append("createdOn=").append(_createdOn).append(",");
        strBuild.append("lastModified=").append(_lastModified).append(",");
        strBuild.append("modifiedOn=").append(_modifiedOn).append(",");
        
        return strBuild.toString();
    }

    public boolean equalsNetworkVO(NetworkVO p_networkVO) {
        boolean flag = false;
        if (this.getModifiedTimeStamp().equals(p_networkVO.getModifiedTimeStamp())) {
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
        strBuild.append("Name");
        strBuild.append(middleSeperator);
        strBuild.append(this.getNetworkName());

        strBuild.append(startSeperator);
        strBuild.append("Short Name");
        strBuild.append(middleSeperator);
        strBuild.append(this.getNetworkName());

        strBuild.append(startSeperator);
        strBuild.append("Company Name");
        strBuild.append(middleSeperator);
        strBuild.append(this.getCompanyName());

        strBuild.append(startSeperator);
        strBuild.append("ERP Code");
        strBuild.append(middleSeperator);
        strBuild.append(this.getErpNetworkCode());

        strBuild.append(startSeperator);
        strBuild.append("Status");
        strBuild.append(middleSeperator);
        strBuild.append(this.getStatus());

        strBuild.append(startSeperator);
        strBuild.append("Language Message 1");
        strBuild.append(middleSeperator);
        strBuild.append(this.getLanguage1Message());

        strBuild.append(startSeperator);
        strBuild.append("Language Message 2");
        strBuild.append(middleSeperator);
        strBuild.append(this.getLanguage2Message());

        strBuild.append(startSeperator);
        strBuild.append("Language Message 1");
        strBuild.append(middleSeperator);
        strBuild.append(this.getLanguage1Message());

        strBuild.append(startSeperator);
        strBuild.append("Prefix Code");
        strBuild.append(middleSeperator);
        strBuild.append(this.getCountryPrefixCode());

        return strBuild.toString();

    }


	@Override
	public native boolean equals(Object obj);

	/**
     * 
     * @param p_networkVO
     *            is currentNetwork
     * @return
     *         String
     */
    public String differnces(NetworkVO p_networkVO) {

        StringBuilder strBuild = new StringBuilder();

        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        if (!BTSLUtil.isNullString(this.getNetworkName()) && !BTSLUtil.isNullString(p_networkVO.getNetworkName()) && !BTSLUtil.compareLocaleString(this.getNetworkName(), p_networkVO.getNetworkName())) {
            strBuild.append(startSeperator);
            strBuild.append("Name");
            strBuild.append(middleSeperator);
            strBuild.append(p_networkVO.getNetworkName());
            strBuild.append(middleSeperator);
            strBuild.append(this.getNetworkName());
        }

        if (!BTSLUtil.isNullString(this.getNetworkShortName()) && !BTSLUtil.isNullString(p_networkVO.getNetworkShortName()) && !BTSLUtil.compareLocaleString(this.getNetworkShortName(), p_networkVO.getNetworkShortName())) {
            strBuild.append(startSeperator);
            strBuild.append("Short Name");
            strBuild.append(middleSeperator);
            strBuild.append(p_networkVO.getNetworkShortName());
            strBuild.append(middleSeperator);
            strBuild.append(this.getNetworkShortName());
        }

        if (!BTSLUtil.isNullString(this.getCompanyName()) && !BTSLUtil.isNullString(p_networkVO.getCompanyName()) && !BTSLUtil.compareLocaleString(this.getCompanyName(), p_networkVO.getCompanyName())) {

            strBuild.append(startSeperator);
            strBuild.append("Company Name");
            strBuild.append(middleSeperator);
            strBuild.append(p_networkVO.getCompanyName());
            strBuild.append(middleSeperator);
            strBuild.append(this.getCompanyName());
        }

        if (!BTSLUtil.isNullString(this.getErpNetworkCode()) && !BTSLUtil.isNullString(p_networkVO.getErpNetworkCode()) && !this.getErpNetworkCode().equals(p_networkVO.getErpNetworkCode())) {
            strBuild.append(startSeperator);
            strBuild.append("ERP Code");
            strBuild.append(middleSeperator);
            strBuild.append(p_networkVO.getErpNetworkCode());
            strBuild.append(middleSeperator);
            strBuild.append(this.getErpNetworkCode());
        }

        if (!BTSLUtil.isNullString(this.getStatus()) && !BTSLUtil.isNullString(p_networkVO.getStatus()) && !this.getStatus().equals(p_networkVO.getStatus())) {
            strBuild.append(startSeperator);
            strBuild.append("Status");
            strBuild.append(middleSeperator);
            strBuild.append(p_networkVO.getStatus());
            strBuild.append(middleSeperator);
            strBuild.append(this.getStatus());

        }

        if (this.getLanguage1Message() != null && !this.getLanguage1Message().equals(p_networkVO.getLanguage1Message())) {
            strBuild.append(startSeperator);
            strBuild.append("Language Message 1");
            strBuild.append(middleSeperator);
            strBuild.append(p_networkVO.getLanguage1Message());
            strBuild.append(middleSeperator);
            strBuild.append(this.getLanguage1Message());
        }

        if (!BTSLUtil.isNullString(this.getLanguage2Message()) && !BTSLUtil.isNullString(p_networkVO.getLanguage2Message()) && !BTSLUtil.compareLocaleString(this.getLanguage2Message(), p_networkVO.getLanguage2Message())) {

            strBuild.append(startSeperator);
            strBuild.append("Language Message 2");
            strBuild.append(middleSeperator);
            strBuild.append(p_networkVO.getLanguage2Message());
            strBuild.append(middleSeperator);
            strBuild.append(this.getLanguage2Message());
        }

        if (this.getCountryPrefixCode() != null && !this.getCountryPrefixCode().equals(p_networkVO.getCountryPrefixCode())) {
            strBuild.append(startSeperator);
            strBuild.append("Prefixes Code");
            strBuild.append(middleSeperator);
            strBuild.append(p_networkVO.getCountryPrefixCode());
            strBuild.append(middleSeperator);
            strBuild.append(this.getCountryPrefixCode());
        }
        return strBuild.toString();
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
