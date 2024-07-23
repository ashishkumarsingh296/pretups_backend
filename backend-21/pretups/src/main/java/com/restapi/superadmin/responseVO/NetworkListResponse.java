package com.restapi.superadmin.responseVO;

import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.network.businesslogic.NetworkVO;

public class NetworkListResponse extends BaseResponse{
	private ArrayList _dataList;
	
	/*
     * This is used to log the network details while suspending and activating
     * the
     * network, during save we check the dataList with dataListOld if any
     * network
     * is suspended or activated at that time we log the network details in the
     * logger file
     */
    private String[] _dataListStatusOld;
    private String _requestType;
    private String _code;

    // defined for showing the Description of the selected dropdown value
    private String _statusDesc;
    private String _networkTypeDesc;

    /*
     * Populate the dropdowns
     * 
     * statusList for Status
     * networkTypeList for NetworkType
     */
    private ArrayList _statusList;
    private ArrayList _networkTypeList;

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
    private String _statuss;

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

    private String _serviceSetID = null;
    private String _serviceSetIDDesc = null;
    private ArrayList _serviceSetList;

    /** createdBy property */
    private String _createdBy;

    /** modifiedBy property */
    private String _modifiedBy;

    /** createdOn property */
    private Date _createdOn;

    /** modifiedOn property */
    private Date _modifiedOn;

    private long _lastModified;
    
    public void setDataListIndexed(int i, NetworkVO vo) {
        _dataList.set(i, vo);
    }

    public NetworkVO getDataListIndexed(int i) {
        return (NetworkVO) _dataList.get(i);
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
     * @return Returns the modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOn
     *            The modifiedOn to set.
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    // --------------------------------------------------------- Methods

    /** Sets the networkName value */
    public void setNetworkName(String v) {
        _networkName = v;
    }

    /** Returns the networkName value */
    public String getNetworkName() {
        if (_networkName != null) {
            return _networkName.trim();
        }

        return _networkName;
    }

    /** Sets the networkCode value */
    public void setNetworkCode(String v) {
        _networkCode = v;
    }

    /** Returns the networkCode value */
    public String getNetworkCode() {
        if (_networkCode != null) {
            return _networkCode.trim().toUpperCase();
        } else {
            return _networkCode;
        }
    }

    /** Sets the network Short Name value */
    public void setNetworkShortName(String v) {
        _networkShortName = v;
    }

    /** Returns the network Short Name value */
    public String getNetworkShortName() {
        if (_networkShortName != null) {
            return _networkShortName.trim();
        } else {
            return _networkShortName;
        }
    }

    /** Sets the company Name value */
    public void setCompanyName(String v) {
        _companyName = v;
    }

    /** Returns the company Name value */
    public String getCompanyName() {
        if (_companyName != null) {
            return _companyName.trim();
        } else {
            return _companyName;
        }
    }

    /** Sets the report Header Name value */
    public void setReportHeaderName(String v) {
        _reportHeaderName = v;
    }

    /** Returns the report Header Name value */
    public String getReportHeaderName() {
        if (_reportHeaderName != null) {
            return _reportHeaderName.trim();
        } else {
            return _reportHeaderName;
        }
    }

    /** Sets the erp Network Code value */
    public void setErpNetworkCode(String v) {
        _erpNetworkCode = v;
    }

    /** Returns the erp Network Code value */
    public String getErpNetworkCode() {
        if (_erpNetworkCode != null) {
            return _erpNetworkCode.trim();
        } else {
            return _erpNetworkCode;
        }
    }

    /** Sets the address1 value */
    public void setAddress1(String v) {
        _address1 = v;
    }

    /** Returns the address1 value */
    public String getAddress1() {
        if (_address1 != null) {
            return _address1.trim();
        } else {
            return _address1;
        }
    }

    /** Sets the address2 value */
    public void setAddress2(String v) {
        _address2 = v;
    }

    /** Returns the address2 value */
    public String getAddress2() {
        if (_address2 != null) {
            return _address2.trim();
        } else {
            return _address2;
        }
    }

    /** Sets the city value */
    public void setCity(String v) {
        _city = v;
    }

    /** Returns the city value */
    public String getCity() {
        if (_city != null) {
            return _city.trim();
        } else {
            return _city;
        }
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
        if (serviceSetID != null) {
            _serviceSetID = serviceSetID.trim();
        }
    }

    /**
     * @return Returns the serviceSetIDDesc.
     */
    public String getServiceSetIDDesc() {
        return _serviceSetIDDesc;
    }

    /**
     * @param serviceSetIDDesc
     *            The serviceSetIDDesc to set.
     */
    public void setServiceSetIDDesc(String serviceSetIDDesc) {
        if (serviceSetIDDesc != null) {
            _serviceSetIDDesc = serviceSetIDDesc.trim();
        }
    }

    /**
     * @return Returns the serviceSetList.
     */
    public ArrayList getServiceSetList() {
        return _serviceSetList;
    }

    /**
     * @param serviceSetList
     *            The serviceSetList to set.
     */
    public void setServiceSetList(ArrayList serviceSetList) {
        _serviceSetList = serviceSetList;
    }

    /** Sets the state value */
    public void setState(String v) {
        _state = v;
    }

    /** Returns the state value */
    public String getState() {
        if (_state != null) {
            return _state.trim();
        } else {
            return _state;
        }
    }

    /** Sets the zip Code value */
    public void setZipCode(String v) {
        _zipCode = v;
    }

    /** Returns the zip Code value */
    public String getZipCode() {
        if (_zipCode != null) {
            return _zipCode.trim();
        } else {
            return _zipCode;
        }
    }

    /** Sets the country value */
    public void setCountry(String v) {
        _country = v;
    }

    /** Returns the country value */
    public String getCountry() {
        if (_country != null) {
            return _country.trim();
        } else {
            return _country;
        }
    }

    /** Sets the network Type value */
    public void setNetworkType(String v) {
        _networkType = v;
    }

    /** Returns the network Type value */
    public String getNetworkType() {
        if (_networkType != null) {
            return _networkType.trim();
        } else {
            return _networkType;
        }
    }

    /** Sets the status value */
    public void setStatuss(String v) {
        _statuss = v;
    }

    /** Returns the status value */
    public String getStatuss() {
        if (_statuss != null) {
            return _statuss.trim();
        } else {
            return _statuss;
        }
    }

    /** Sets the remarks value */
    public void setRemarks(String v) {
        _remarks = v;
    }

    /** Returns the remarks value */
    public String getRemarks() {
        if (_remarks != null) {
            return _remarks.trim();
        } else {
            return _remarks;
        }
    }

    /** Sets the language1 Message value */
    public void setLanguage1Message(String v) {
        _language1Message = v;
    }

    /** Returns the language1 Message value */
    public String getLanguage1Message() {
        if (_language1Message != null) {
            return _language1Message.trim();
        } else {
            return _language1Message;
        }
    }

    /** Sets the language2 Message value */
    public void setLanguage2Message(String v) {
        _language2Message = v;
    }

    /** Returns the language2 Message value */
    public String getLanguage2Message() {
        if (_language2Message != null) {
            return _language2Message.trim();
        } else {
            return _language2Message;
        }
    }

    /** Sets the tax1 Value value */
    public void setText1Value(String v) {
        _text1Value = v;
    }

    /** Returns the tax1 Value value */
    public String getText1Value() {
        if (_text1Value != null) {
            return _text1Value.trim();
        } else {
            return _text1Value;
        }
    }

    /** Sets the tax2 Value value */
    public void setText2Value(String v) {
        _text2Value = v;
    }

    /** Returns the tax2 Value value */
    public String getText2Value() {
        if (_text2Value != null) {
            return _text2Value.trim();
        } else {
            return _text2Value;
        }
    }

    /** Sets the country Code value */
    public void setCountryPrefixCode(String v) {
        _countryPrefixCode = v;
    }

    /** Returns the country Code value */
    public String getCountryPrefixCode() {
        if (_countryPrefixCode != null) {
            return _countryPrefixCode.trim();
        } else {
            return _countryPrefixCode;
        }
    }
    
    public ArrayList getDataList() {
        return _dataList;
    }

    /**
     * @param dataList
     *            The dataList to set.
     */
    public void setDataList(ArrayList dataList) {
        this._dataList = dataList;
    }

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return _code;
    }

    /**
     * @param code
     *            The code to set.
     */
    public void setCode(String code) {
        _code = code;
    }

    /**
     * @return Returns the networkTypeList.
     */
    public ArrayList getNetworkTypeList() {
        return _networkTypeList;
    }

    /**
     * @param networkTypeList
     *            The networkTypeList to set.
     */
    public void setNetworkTypeList(ArrayList networkTypeList) {
        this._networkTypeList = networkTypeList;
    }

    /**
     * @return Returns the statusList.
     */
    public ArrayList getStatusList() {
        return _statusList;
    }

    /**
     * @param statusList
     *            The statusList to set.
     */
    public void setStatusList(ArrayList statusList) {
        this._statusList = statusList;
    }

    public int getResultCount() {
        if (_dataList != null && !_dataList.isEmpty()) {
            return _dataList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the lastModifiedOn.
     */
    public long getLastModified() {
        return _lastModified;
    }

    /**
     * @param lastModifiedOn
     *            The lastModifiedOn to set.
     */
    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
    }

    /**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        _requestType = requestType;
    }

    /**
     * @return Returns the networkTypeDesc.
     */
    public String getNetworkTypeDesc() {
        return _networkTypeDesc;
    }

    /**
     * @param networkTypeDesc
     *            The networkTypeDesc to set.
     */
    public void setNetworkTypeDesc(String networkTypeDesc) {
        _networkTypeDesc = networkTypeDesc;
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
     * @return Returns the dataListStatusOld.
     */
    public String[] getDataListStatusOld() {
        return _dataListStatusOld;
    }

    /**
     * @param dataListStatusOld
     *            The dataListStatusOld to set.
     */
    public void setDataListStatusOld(String[] dataListStatusOld) {
        _dataListStatusOld = dataListStatusOld;
    }
}
