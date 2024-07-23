package com.btsl.user.businesslogic;

/*
 * UserVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.ListValueVO;
import com.btsl.menu.MenuItem;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

public class UserVO implements Serializable {
    private String _userID;
    private String _userName;
    private String _networkID;
    private String _loginID;
    private String _password;
    private String _categoryCode;
    private CategoryVO _categoryVO;
    private String _parentID;
    private String _parentName;
    private String _ownerID;
    private String _allowedIps;
    private String _allowedDays;
    private String _paymentTypes;
    private String _fromTime;
    private String _toTime;
    private java.util.Date _lastLoginOn;
    private String _empCode;
    private String _status;
    private String _statusDesc;
    private String _previousStatus;
    private String _email;
    private Date _passwordModifiedOn = null;
    private String _contactNo;// used for Channel Person
    private String _designation;
    private String _divisionCode;
    private String _divisionDesc;
    private String _departmentCode;
    private String _departmentDesc;
    private String _msisdn;
    private String _userType;// used for Channel Person
    private String _webLoginID;
    private String _requestType;
    private String showPassword;
    private String confirmPassword;
	private String[] allowedDays;
	private String[] paymentTypes;
	private ArrayList paymentTypesList;
	private String createdOn;
	private String categoryCodeDesc;
	private String appintmentDate;
	private ArrayList statusList;
	private String userNamePrefixCode;
	private ArrayList userNamePrefixList;
	private String userNamePrefixDesc = null;
	private String userLanguage = null;
	private ArrayList userLanguageList;
	private String userLanguageDesc = null;
	private boolean isSerAssignChnlAdm = false;
	private ArrayList geographicalList;
	private String grphDomainTypeName;
	private String[] geographicalCodeArray;
	private String geographicalCode;
	private ArrayList networkList;
	private String[] roleFlag;
	private HashMap rolesMap;
	private String roleType;
    private ArrayList servicesList;// store the complete list of the Services
    private String[] servicesTypes;// store the service_type asociated with the
                                    // user
    private ArrayList productsList;// store the complete list of the Products
    private String[] productCodes;// store the productCodes asociated with the
                                   // user
    private HashMap rolesMapSelected;
    private String addCommProfOTFDetailId;
    private Long otfValue;
	private int otfCount;
	private String allowedUserTypeCreation=null;
	private  List agentBalanceList;
	private String _ownerCompany;
	private Date _suspendOn =null;
	private ArrayList<UserLoanVO> userLoanVOList ;
	
    public ArrayList<UserLoanVO> getUserLoanVOList() {
		return userLoanVOList;
	}

	public void setUserLoanVOList(ArrayList<UserLoanVO> userLoanVOList) {
		this.userLoanVOList = userLoanVOList;
	}
    
	public ArrayList getPaymentTypesList() {
		return paymentTypesList;
	}

	public void setPaymentTypesList(ArrayList paymentTypesList) {
		this.paymentTypesList = paymentTypesList;
	}
    public List getAgentBalanceList() {
		return agentBalanceList;
	}

	public void setAgentBalanceList(List agentBalanceList) {
		this.agentBalanceList = agentBalanceList;
	}

	public String getAddCommProfOTFDetailId() {
		return addCommProfOTFDetailId;
	}

	public void setAddCommProfOTFDetailId(String addCommProfOTFDetailId) {
		this.addCommProfOTFDetailId = addCommProfOTFDetailId;
	}

    public int getOtfCount() {
		return otfCount;
	}

	public void setOtfCount(int otfCount) {
		this.otfCount = otfCount;
	}
	
    
    
	public Long getOtfValue() {
		return otfValue;
	}

	public void setOtfValue(Long otfValue) {
		this.otfValue = otfValue;
	}

	/**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        if (_requestType != null) {
            return _requestType.trim();
        }
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        _requestType = requestType;
    }

    public String getWebLoginID() {
		return _webLoginID;
	}

	public void setWebLoginID(String _webLoginID) {
		this._webLoginID = _webLoginID;
	}
	private List<ListValueVO> _divisionList;
    public List<ListValueVO> getDivisionList() {
		return _divisionList;
	}

	public void setDivisionList(List<ListValueVO> _divisionList) {
		this._divisionList = _divisionList;
	}
	private List<ListValueVO> _departmentList;

	public List<ListValueVO> getDepartmentList() {
		return _departmentList;
	}

	public void setDepartmentList(List<ListValueVO> _departmentList) {
		this._departmentList = _departmentList;
	}
	private String _createdBy;
    private Date _createdOn;
    private String _createdByUserName;
    private String _createdOnAsString;
    private String _modifiedBy;
    private Date _modifiedOn;
    private String _address1;
    private String _address2;
    private String _city;
    private String _state;
    private String _country;
    private String _ssn;
    private String _userNamePrefix;
    private String _externalCode;
    private String _userCode;
    private String shortName;
    private String referenceID;// back-end entry

    private String _domainID;
    private String _domainName;
    private String _domainStatus;
    private String _networkName;
    private ArrayList<UserGeographiesVO> _geographicalAreaList;
    private ArrayList _associatedGeographicalList;
    public ArrayList getAssociatedGeographicalList() {
		return _associatedGeographicalList;
	}

	public void setAssociatedGeographicalList(ArrayList _associatedGeographicalList) {
		this._associatedGeographicalList = _associatedGeographicalList;
	}
	private ArrayList _domainList;
    private ArrayList _serviceList;
    private ArrayList _voucherList;
    private ArrayList segmentList;
    private ArrayList _menuItemList;
    private ArrayList _msisdnList;
    private String _networkStatus;
    private String _message;
    protected String _validRequestURLs;
    private SessionInfoVO _sessionInfoVO = null;
    private int _validStatus = 0; // 0=Invalid, 1=Duplicate login,2 =Valid

    private String _geographicalCodeStatus;
    private UserPhoneVO _userPhoneVO;
    private ArrayList _associatedServiceTypeList;
    // added by mohitg
    private long _lastModified;
    private String _level1ApprovedBy;
    private Date _level1ApprovedOn;
    private String _level2ApprovedBy;
    private Date _level2ApprovedOn;
    private Date _appointmentDate;

    private boolean _usingNewSTK = false;
    private String _fxedInfoStr;
    private boolean _updateSimRequired = false;
    private int _invalidPasswordCount = 0;
    private Date _passwordCountUpdatedOn;

    private String _parentMsisdn;
    private String _parentCategoryName;
    private String _ownerName;
    private String _ownerLoginId;
    private String _parentLoginId;


    private String _ownerMsisdn;
    private String _ownerCategoryName;
    private  List _userBalanceList;

    private String _reportHeaderName; // for report purpose

    private ArrayList _associatedProductTypeList;

    private String _domainTypeCode;
    // defined for logger

    private String _restrictedMsisdnAllow; // to check wheather
                                           // _restrictedMsisdn is allowed with
                                           // domainType or not
    /*
     * This message is set in
     * a)logoutMethod of the LoginAction class
     * c)invalidAlloweDays of the Common Action
     */
    private String _loggerMessage = null;
    private String _remoteAddress = null;
    private String _browserType = null;
    private Date _loginTime;

    private String _suspendedByUserName;
    private String _requetedByUserName;
    private String _currentRoleCode;
    private String _currentModule;
    private boolean _passwordModifyFlag = false;
    private String[] _domainCodes;
    private String _batchID;
    private String _batchName;
    private String _creationType;
    private String _remarks;
    private String _activeUserID;
    private boolean _isStaffUser = false;
    private String _moduleCodeString;
    private String _pageCodeString;
    private String _passwordReset = null;
    private String _pinReset = null;
    private String _activeUserMsisdn = null;
    private String _activeUserPin = null;
    private String _activeUserLoginId = null;
    // private boolean _msisdnAssigned=false;
    private ChannelUserVO _staffUserDetails = null;
    private String _contactPerson = null;
    // To load the MIS executed date range for reporting purpose.
    private Date _c2sMisToDate = null;
    private Date _c2sMisFromDate = null;
    private Date _p2pMisToDate = null;
    private Date _p2pMisFromDate = null;
    // added by nilesh:for user profile updation based on langitude and latitude
    private String _longitude = null;
    private String _latitude = null;
    // Added by Deepika Aggarwal
    private String _company;
    private String _fax;
    private String _firstName;
    private String _lastName;
    private String _language;

    // Added for Rsa Authentication
    private String _rsaFlag;

    private java.util.Date _oldLastLoginOn;
    private boolean _rsaAllowed;
    private boolean _rsavalidated;
    private boolean _rsaRequired = false;
	public void setRsaRequired(boolean _rsaRequired) {
		this._rsaRequired = _rsaRequired;
	}

	public boolean isRsaRequired() {
        return _rsaRequired;
    }

    private String info1 = null;
    private String info2 = null;
    private String info3 = null;
    private String info4 = null;
    private String info5 = null;
    private String info6 = null;
    private String info7 = null;
    private String info8 = null;
    private String info9 = null;
    private String info10 = null;
    private String info11 = null;
    private String info12 = null;
    private String info13 = null;
    private String info14 = null;
    private String info15 = null;

    private boolean _otpvalidated;
    private String _authTypeAllowed = null;
    // Added for OTP Authentication
    private String _authType;

    // *******************
    private String _assType;
    private String _assoMsisdn;
    private Date _associationCreatedOn;
    private Date _associationModifiedOn;

    private String _countryCode = null;
    private String _documentType = null;
    private String _documentNo = null;
    private String _paymentType = null;
	
    
    public String getCountryCode() {
        return _countryCode;
    }

    public void setCountryCode(String _countryCode) {
        this._countryCode = _countryCode;
    }

    public String getAssType() {
        return _assType;
    }

    public void setAssType(String assType) {
        this._assType = assType;
    }

    public String getAssoMsisdn() {
        return _assoMsisdn;
    }

    public void setAssoMsisdn(String assoMsisdn) {
        this._assoMsisdn = assoMsisdn;
    }

    public Date getAssociationCreatedOn() {
        return _associationCreatedOn;
    }

    public void setAssociationCreatedOn(Date assCreatOn) {
        this._associationCreatedOn = assCreatOn;
    }

    public Date getAssociationModifiedOn() {
        return _associationModifiedOn;
    }

    public void setAssociationModifiedOn(Date assoModOn) {
        this._associationModifiedOn = assoModOn;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public String getInfo3() {
        return info3;
    }

    public void setInfo3(String info3) {
        this.info3 = info3;
    }

    public String getInfo4() {
        return info4;
    }

    public void setInfo4(String info4) {
        this.info4 = info4;
    }

    public String getInfo5() {
        return info5;
    }

    public void setInfo5(String info5) {
        this.info5 = info5;
    }

    public String getInfo6() {
        return info6;
    }

    public void setInfo6(String info6) {
        this.info6 = info6;
    }

    public String getInfo7() {
        return info7;
    }

    public void setInfo7(String info7) {
        this.info7 = info7;
    }

    public String getInfo8() {
        return info8;
    }

    public void setInfo8(String info8) {
        this.info8 = info8;
    }

    public String getInfo9() {
        return info9;
    }

    public void setInfo9(String info9) {
        this.info9 = info9;
    }

    public String getInfo10() {
        return info10;
    }

    public void setInfo10(String info10) {
        this.info10 = info10;
    }

    public String getInfo11() {
        return info11;
    }

    public void setInfo11(String info11) {
        this.info11 = info11;
    }

    public String getInfo12() {
        return info12;
    }

    public void setInfo12(String info12) {
        this.info12 = info12;
    }

    public String getInfo13() {
        return info13;
    }

    public void setInfo13(String info13) {
        this.info13 = info13;
    }

    public String getInfo14() {
        return info14;
    }

    public void setInfo14(String info14) {
        this.info14 = info14;
    }

    public String getInfo15() {
        return info15;
    }

    public void setInfo15(String info15) {
        this.info15 = info15;
    }

    public java.util.Date getOldLastLoginOn() {
        return _oldLastLoginOn;
    }

    public void setOldLastLoginOn(java.util.Date oldLastLoginOn) {
        _oldLastLoginOn = oldLastLoginOn;
    }

    public Date getC2sMisFromDate() {
        return _c2sMisFromDate;
    }

    public void setC2sMisFromDate(Date misFromDate) {
        _c2sMisFromDate = misFromDate;
    }

    public Date getC2sMisToDate() {
        return _c2sMisToDate;
    }

    public void setC2sMisToDate(Date misToDate) {
        _c2sMisToDate = misToDate;
    }

    public Date getP2pMisFromDate() {
        return _p2pMisFromDate;
    }

    public void setP2pMisFromDate(Date misFromDate) {
        _p2pMisFromDate = misFromDate;
    }

    public Date getP2pMisToDate() {
        return _p2pMisToDate;
    }

    public void setP2pMisToDate(Date misToDate) {
        _p2pMisToDate = misToDate;
    }

    /**
     * @return the contactPerson
     */
    public String getContactPerson() {
        return _contactPerson;
    }

    /**
     * @param contactPerson
     *            the contactPerson to set
     */
    public void setContactPerson(String contactPerson) {
        _contactPerson = contactPerson;
    }

    /**
     * @return Returns the isStaffUser.
     */
    public boolean isStaffUser() {
        return this._isStaffUser;
    }

    /**
     * @param isStaffUser
     *            The isStaffUser to set.
     */
    public void setStaffUser(boolean isStaffUser) {
        this._isStaffUser = isStaffUser;
    }

    /**
     * @return Returns the activeUserID.
     */
    public String getActiveUserID() {
        return this._activeUserID;
    }

    /**
     * @param activeUserID
     *            The activeUserID to set.
     */
    public void setActiveUserID(String activeUserID) {
        this._activeUserID = activeUserID;
    }

    /**
     * @return Returns the remarks.
     */
    public String getRemarks() {
        return _remarks;
    }

    /**
     * @param remarks
     *            The remarks to set.
     */
    public void setRemarks(String remarks) {
        _remarks = remarks;
    }

    /**
     * @return Returns the batchName.
     */
    public String getBatchName() {
        return _batchName;
    }

    /**
     * @param batchName
     *            The batchName to set.
     */
    public void setBatchName(String batchName) {
        _batchName = batchName;
    }

    /**
     * @return Returns the batchID.
     */
    public String getBatchID() {
        return _batchID;
    }

    /**
     * @param batchID
     *            The batchID to set.
     */
    public void setBatchID(String batchID) {
        _batchID = batchID;
    }

    /**
     * @return Returns the creationType.
     */
    public String getCreationType() {
        return _creationType;
    }

    /**
     * @param creationType
     *            The creationType to set.
     */
    public void setCreationType(String creationType) {
        _creationType = creationType;
    }

    /**
     * @return Returns the domainCodes.
     */
    public String[] getDomainCodes() {
        return _domainCodes;
    }

    /**
     * @param domainCodes
     *            The domainCodes to set.
     */
    public void setDomainCodes(String[] domainCodes) {
        _domainCodes = domainCodes;
    }

    /**
     * @return Returns the passwordModifyFlag.
     */
    public boolean isPasswordModifyFlag() {
        return _passwordModifyFlag;
    }

    /**
     * @param passwordModifyFlag
     *            The passwordModifyFlag to set.
     */
    public void setPasswordModifyFlag(boolean passwordModifyFlag) {
        _passwordModifyFlag = passwordModifyFlag;
    }

    /**
     * @return Returns the loginTime.
     */
    public Date getLoginTime() {
        return _loginTime;
    }

    /**
     * @param loginTime
     *            The loginTime to set.
     */
    public void setLoginTime(Date loginTime) {
        _loginTime = loginTime;
    }

    /**
     * @return Returns the reportHeaderName.
     */
    public String getReportHeaderName() {
        return _reportHeaderName;
    }

    /**
     * @param reportHeaderName
     *            The reportHeaderName to set.
     */
    public void setReportHeaderName(String reportHeaderName) {
        _reportHeaderName = reportHeaderName;
    }

    public int getInvalidPasswordCount() {
        return _invalidPasswordCount;
    }

    public void setInvalidPasswordCount(int passwordCount) {
        _invalidPasswordCount = passwordCount;
    }

    /**
     * @return Returns the passwordCountUpdatedOn.
     */
    public Date getPasswordCountUpdatedOn() {
        return _passwordCountUpdatedOn;
    }

    /**
     * @param passwordCountUpdatedOn
     *            The passwordCountUpdatedOn to set.
     */
    public void setPasswordCountUpdatedOn(Date passwordCountUpdatedOn) {
        _passwordCountUpdatedOn = passwordCountUpdatedOn;
    }

    /**
     * @return Returns the address1.
     */
    public String getAddress1() {
        return _address1;
    }

    /**
     * @param address1
     *            The address1 to set.
     */
    public void setAddress1(String address1) {
        _address1 = address1;
    }

    /**
     * @return Returns the address2.
     */
    public String getAddress2() {
        return _address2;
    }

    /**
     * @param address2
     *            The address2 to set.
     */
    public void setAddress2(String address2) {
        _address2 = address2;
    }

    /**
     * @return Returns the city.
     */
    public String getCity() {
        return _city;
    }

    /**
     * @param city
     *            The city to set.
     */
    public void setCity(String city) {
        _city = city;
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return _country;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCountry(String country) {
        _country = country;
    }

    /**
     * @return Returns the externalCode.
     */
    public String getExternalCode() {
        return _externalCode;
    }

    /**
     * @param externalCode
     *            The externalCode to set.
     */
    public void setExternalCode(String externalCode) {
        _externalCode = externalCode;
    }

    public String getUserCode() {
        return _userCode;
    }

    public void setUserCode(String userCode) {
        _userCode = userCode;
    }

    /**
     * @return Returns the ssn.
     */
    public String getSsn() {
        return _ssn;
    }

    /**
     * @param ssn
     *            The ssn to set.
     */
    public void setSsn(String ssn) {
        _ssn = ssn;
    }

    /**
     * @return Returns the state.
     */
    public String getState() {
        return _state;
    }

    /**
     * @param state
     *            The state to set.
     */
    public void setState(String state) {
        _state = state;
    }

    /**
     * @return Returns the userNamePrefix.
     */
    public String getUserNamePrefix() {
        return _userNamePrefix;
    }

    /**
     * @param userNamePrefix
     *            The userNamePrefix to set.
     */
    public void setUserNamePrefix(String userNamePrefix) {
        _userNamePrefix = userNamePrefix;
    }

    /**
     * @return Returns the userType.
     */
    public String getUserType() {
        return _userType;
    }

    /**
     * @param userType
     *            The userType to set.
     */
    public void setUserType(String userType) {
        _userType = userType;
    }

    /**
     * @return Returns the referenceID.
     */
    public String getReferenceID() {
        return referenceID;
    }

    /**
     * @param referenceID
     *            The referenceID to set.
     */
    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    /**
     * @return Returns the shortName.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName
     *            The shortName to set.
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * Iterates through the users menu list to see if the current page is
     * allowed to the user or not
     * 
     * @return boolean
     */
    public boolean isAccessAllowed(String p_accessCode) {
        if (p_accessCode == null) {
            return false;
        }
        int menuItemListSize= _menuItemList.size();
        for (int loop = 0; loop < menuItemListSize; loop++) {
            MenuItem menuItem = (MenuItem) _menuItemList.get(loop);
            if (p_accessCode.equals(menuItem.getPageCode())) {
                _currentRoleCode = menuItem.getRoleCode();
                // _currentModule=menuItem.getModuleCode();
                return true;
            }
        }
        return false;
    }

    public boolean isAccessAllowed(String p_accessCode, String p_moduleCode) {
        if (p_accessCode == null) {
            return false;
        }
        for (int loop = 0; loop < _menuItemList.size(); loop++) {
            MenuItem menuItem = (MenuItem) _menuItemList.get(loop);
            if (p_accessCode.equals(menuItem.getPageCode()) && p_moduleCode.equals(menuItem.getModuleCode())) {
                return true;
            }
        }
        return false;
    }

    public boolean isModuleAccessAllowed(String p_accessCode) {
        if (p_accessCode == null) {
            return false;
        }
        int menuListSize = _menuItemList.size();
        for (int loop = 0; loop < menuListSize; loop++) {
            MenuItem menuItem = (MenuItem) _menuItemList.get(loop);
            if (p_accessCode.equals(menuItem.getModuleCode())) {
                _currentModule = menuItem.getModuleCode();
                return true;
            }
        }
        return false;
    }

    /**
     * @return Returns the sessionVO.
     */
    public SessionInfoVO getSessionInfoVO() {
        return _sessionInfoVO;
    }

    /**
     * @param sessionVO
     *            The sessionVO to set.
     */
    public void setSessionInfoVO(SessionInfoVO sessionInfoVO) {
        _sessionInfoVO = sessionInfoVO;
    }

    /**
     * @return Returns the allowedDays.
     */
    public String getAllowedDays() {
        return _allowedDays;
    }

    /**
     * @param allowedDays
     *            The allowedDays to set.
     */
    public void setAllowedDays(String allowedDays) {
        _allowedDays = allowedDays;
    }

    public String getPaymentTypes() {
        return _paymentTypes;
    }
    
    public void setPaymentTypes(String paymentTypes) {
        _paymentTypes = paymentTypes;
    }
    /**
     * @return Returns the categoryVO.
     */
    public CategoryVO getCategoryVO() {
        return _categoryVO;
    }

    /**
     * @param categoryVO
     *            The categoryVO to set.
     */
    public void setCategoryVO(CategoryVO categoryVO) {
        _categoryVO = categoryVO;
    }

    /**
     * @return Returns the domainID.
     */
    public String getDomainID() {
        return _domainID;
    }

    /**
     * @param domainID
     *            The domainID to set.
     */
    public void setDomainID(String domainID) {
        _domainID = domainID;
    }

    /**
     * @return Returns the empCode.
     */
    public String getEmpCode() {
        return _empCode;
    }

    /**
     * @param empCode
     *            The empCode to set.
     */
    public void setEmpCode(String empCode) {
        _empCode = empCode;
    }

    /**
     * @return Returns the fromTime.
     */
    public String getFromTime() {
        return _fromTime;
    }

    /**
     * @param fromTime
     *            The fromTime to set.
     */
    public void setFromTime(String fromTime) {
        _fromTime = fromTime;
    }

    /**
     * @return Returns the geographicalAreaList.
     */
    public ArrayList<UserGeographiesVO> getGeographicalAreaList() {
        return _geographicalAreaList;
    }

    /**
     * @param geographicalAreaList
     *            The geographicalAreaList to set.
     */
    public void setGeographicalAreaList(ArrayList<UserGeographiesVO> geographicalAreaList) {
        _geographicalAreaList = geographicalAreaList;
    }

    /**
     * @return Returns the lastLoginOn.
     */
    public java.util.Date getLastLoginOn() {
        return _lastLoginOn;
    }

    /**
     * @param lastLoginOn
     *            The lastLoginOn to set.
     */
    public void setLastLoginOn(java.util.Date lastLoginOn) {
        _lastLoginOn = lastLoginOn;
    }

    /**
     * @return Returns the loginID.
     */
    public String getLoginID() {
        return _loginID;
    }

    /**
     * @param loginID
     *            The loginID to set.
     */
    public void setLoginID(String loginID) {
        _loginID = loginID;
    }

    /**
     * @return Returns the menuItemList.
     */
    public ArrayList getMenuItemList() {
        return _menuItemList;
    }

    /**
     * @param menuItemList
     *            The menuItemList to set.
     */
    public void setMenuItemList(ArrayList menuItemList) {
        _menuItemList = menuItemList;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return _message;
    }

    /**
     * @param message
     *            The message to set.
     */
    public void setMessage(String message) {
        _message = message;
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

    /**
     * @return Returns the networkID.
     */
    public String getNetworkID() {
        return _networkID;
    }

    /**
     * @param networkID
     *            The networkID to set.
     */
    public void setNetworkID(String networkID) {
        _networkID = networkID;
    }

    /**
     * @return Returns the networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * @param networkName
     *            The networkName to set.
     */
    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    public String getNetworkNamewithNetworkCode() {
        if (!BTSLUtil.isNullString(_networkID)) {
            return _networkName + "-" + _networkID;
        } else {
            return _networkName;
        }
    }

    /**
     * @return Returns the ownerID.
     */
    public String getOwnerID() {
        return _ownerID;
    }

    /**
     * @param ownerID
     *            The ownerID to set.
     */
    public void setOwnerID(String ownerID) {
        _ownerID = ownerID;
    }

    /**
     * @return Returns the parentID.
     */
    public String getParentID() {
        return _parentID;
    }

    /**
     * @param parentID
     *            The parentID to set.
     */
    public void setParentID(String parentID) {
        _parentID = parentID;
    }

    /**
     * @return Returns the parentName.
     */
    public String getParentName() {
        return _parentName;
    }

    /**
     * @param parentName
     *            The parentName to set.
     */
    public void setParentName(String parentName) {
        _parentName = parentName;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return _password;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setPassword(String password) {
        _password = password;
    }

    /**
     * @return Returns the toTime.
     */
    public String getToTime() {
        return _toTime;
    }

    /**
     * @param toTime
     *            The toTime to set.
     */
    public void setToTime(String toTime) {
        _toTime = toTime;
    }

    /**
     * @return Returns the userID.
     */
    public String getUserID() {
        return _userID;
    }

    /**
     * @param userID
     *            The userID to set.
     */
    public void setUserID(String userID) {
        _userID = userID;
    }

    /**
     * @return Returns the userName.
     */
    public String getUserName() {
        return _userName;
    }

    /**
     * @param userName
     *            The userName to set.
     */
    public void setUserName(String userName) {
        _userName = userName;
    }

    /**
     * @return Returns the validRequestURLs.
     */
    public String getValidRequestURLs() {
        return _validRequestURLs;
    }

    /**
     * @param validRequestURLs
     *            The validRequestURLs to set.
     */
    public void setValidRequestURLs(String validRequestURLs) {
        _validRequestURLs = validRequestURLs;
    }

    public Date getPasswordModifiedOn() {
        return _passwordModifiedOn;
    }

    public void setPasswordModifiedOn(Date setPasswordModifiedOn) {
        _passwordModifiedOn = setPasswordModifiedOn;
    }

    public String getNetworkStatus() {
        return _networkStatus;
    }

    public void setNetworkStatus(String networkStatus) {
        _networkStatus = networkStatus;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    public int getValidStatus() {
        return _validStatus;
    }

    public void setValidStatus(int validStatus) {
        _validStatus = validStatus;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return Returns the allowedIps.
     */
    public String getAllowedIps() {
        return _allowedIps;
    }

    /**
     * @param allowedIps
     *            The allowedIps to set.
     */
    public void setAllowedIps(String allowedIps) {
        _allowedIps = allowedIps;
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

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return _email;
    }

    /**
     * @param email
     *            The email to set.
     */
    public void setEmail(String email) {
        _email = email;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the previousStatus.
     */
    public String getPreviousStatus() {
        return _previousStatus;
    }

    /**
     * @param previousStatus
     *            The previousStatus to set.
     */
    public void setPreviousStatus(String previousStatus) {
        _previousStatus = previousStatus;
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
        this._lastModified = lastModified;
    }

    public String getUserNamewithUserId() {
        if (!BTSLUtil.isNullString(this._userID)) {
            return this._userName + "(" + this._userID + ")";
        }
        return this._userName;
    }

    public String getUserNamewithLoginId() {
        if (!BTSLUtil.isNullString(this._loginID)) {
            return this._userName + "(" + this._loginID + ")";
        }
        return this._userName;
    }

    /**
     * @return Returns the contactN0.
     */
    public String getContactNo() {
        return _contactNo;
    }

    /**
     * @param contactN0
     *            The contactN0 to set.
     */
    public void setContactNo(String contactN0) {
        _contactNo = contactN0;
    }

    /**
     * @return Returns the departmentCode.
     */
    public String getDepartmentCode() {
        return _departmentCode;
    }

    /**
     * @param departmentCode
     *            The departmentCode to set.
     */
    public void setDepartmentCode(String departmentCode) {
        _departmentCode = departmentCode;
    }

    /**
     * @return Returns the departmentDesc.
     */
    public String getDepartmentDesc() {
        return _departmentDesc;
    }

    /**
     * @param departmentDesc
     *            The departmentDesc to set.
     */
    public void setDepartmentDesc(String departmentDesc) {
        _departmentDesc = departmentDesc;
    }

    /**
     * @return Returns the designation.
     */
    public String getDesignation() {
        return _designation;
    }

    /**
     * @param designation
     *            The designation to set.
     */
    public void setDesignation(String designation) {
        _designation = designation;
    }

    /**
     * @return Returns the divisionCode.
     */
    public String getDivisionCode() {
        return _divisionCode;
    }

    /**
     * @param divisionCode
     *            The divisionCode to set.
     */
    public void setDivisionCode(String divisionCode) {
        _divisionCode = divisionCode;
    }

    /**
     * @return Returns the divisionDesc.
     */
    public String getDivisionDesc() {
        return _divisionDesc;
    }

    /**
     * @param divisionDesc
     *            The divisionDesc to set.
     */
    public void setDivisionDesc(String divisionDesc) {
        _divisionDesc = divisionDesc;
    }

    public UserPhoneVO getUserPhoneVO() {
        return _userPhoneVO;
    }

    public void setUserPhoneVO(UserPhoneVO userPhoneVO) {
        _userPhoneVO = userPhoneVO;
    }

    public ArrayList getAssociatedServiceTypeList() {
        return _associatedServiceTypeList;
    }

    public void setAssociatedServiceTypeList(ArrayList associatedServiceTypeList) {
        _associatedServiceTypeList = associatedServiceTypeList;
    }

    /*
     * private String _address1;
     * private String _address2;
     * private String _city;
     * private String _state;
     */
    public String getFullAddress() {

        StringBuffer address = new StringBuffer();

        if (_address1 != null) {
            address.append(_address1);
        }
        if (_address2 != null) {
            address.append(" ");
            address.append(_address2);
        }
        if (_city != null) {
            address.append(" ");
            address.append(_city);
        }
        if (_state != null) {
            address.append(" ");
            address.append(_state);
        }
        if (_country != null) {
            address.append(" ");
            address.append(_country);
        }
        return address.toString();
    }

    public String getFxedInfoStr() {
        return _fxedInfoStr;
    }

    public void setFxedInfoStr(String fxedInfoStr) {
        _fxedInfoStr = fxedInfoStr;
    }

    public boolean isUsingNewSTK() {
        return _usingNewSTK;
    }

    public void setUsingNewSTK(boolean usingNewSTK) {
        _usingNewSTK = usingNewSTK;
    }

    public boolean isUpdateSimRequired() {
        return _updateSimRequired;
    }

    public void setUpdateSimRequired(boolean updateSimRequired) {
        _updateSimRequired = updateSimRequired;
    }

    /**
     * @return Returns the createdOnAsString.
     */
    public String getCreatedOnAsString() {
        return _createdOnAsString;
    }

    /**
     * @param createdOnAsString
     *            The createdOnAsString to set.
     */
    public void setCreatedOnAsString(String createdOnAsString) {
        _createdOnAsString = createdOnAsString;
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
     * @return Returns the level1ApprovedBy.
     */
    public String getLevel1ApprovedBy() {
        return _level1ApprovedBy;
    }

    /**
     * @param level1ApprovedBy
     *            The level1ApprovedBy to set.
     */
    public void setLevel1ApprovedBy(String level1ApprovedBy) {
        this._level1ApprovedBy = level1ApprovedBy;
    }

    /**
     * @return Returns the level1ApprovedOn.
     */
    public Date getLevel1ApprovedOn() {
        return _level1ApprovedOn;
    }

    /**
     * @param level1ApprovedOn
     *            The level1ApprovedOn to set.
     */
    public void setLevel1ApprovedOn(Date level1ApprovedOn) {
        this._level1ApprovedOn = level1ApprovedOn;
    }

    /**
     * @return Returns the level2ApprovedBy.
     */
    public String getLevel2ApprovedBy() {
        return _level2ApprovedBy;
    }

    /**
     * @param level2ApprovedBy
     *            The level2ApprovedBy to set.
     */
    public void setLevel2ApprovedBy(String level2ApprovedBy) {
        this._level2ApprovedBy = level2ApprovedBy;
    }

    /**
     * @return Returns the level2ApprovedOn.
     */
    public Date getLevel2ApprovedOn() {
        return _level2ApprovedOn;
    }

    /**
     * @param level2ApprovedOn
     *            The level2ApprovedOn to set.
     */
    public void setLevel2ApprovedOn(Date level2ApprovedOn) {
        this._level2ApprovedOn = level2ApprovedOn;
    }

    /**
     * @return Returns the createdByUserName.
     */
    public String getCreatedByUserName() {
        return _createdByUserName;
    }

    /**
     * @param createdByUserName
     *            The createdByUserName to set.
     */
    public void setCreatedByUserName(String createdByUserName) {
        _createdByUserName = createdByUserName;
    }

    /**
     * @return Returns the ownerCategoryName.
     */
    public String getOwnerCategoryName() {
        return _ownerCategoryName;
    }

    /**
     * @param ownerCategoryName
     *            The ownerCategoryName to set.
     */
    public void setOwnerCategoryName(String ownerCategoryName) {
        _ownerCategoryName = ownerCategoryName;
    }

    /**
     * @return Returns the ownerMsisdn.
     */
    public String getOwnerMsisdn() {
        return _ownerMsisdn;
    }

    /**
     * @param ownerMsisdn
     *            The ownerMsisdn to set.
     */
    public void setOwnerMsisdn(String ownerMsisdn) {
        _ownerMsisdn = ownerMsisdn;
    }

    /**
     * @return Returns the ownerName.
     */
    public String getOwnerName() {
        return _ownerName;
    }

    /**
     * @param ownerName
     *            The ownerName to set.
     */
    public void setOwnerName(String ownerName) {
        _ownerName = ownerName;
    }

    /**
     * @return Returns the parentCategoryName.
     */
    public String getParentCategoryName() {
        return _parentCategoryName;
    }

    /**
     * @param parentCategoryName
     *            The parentCategoryName to set.
     */
    public void setParentCategoryName(String parentCategoryName) {
        _parentCategoryName = parentCategoryName;
    }

    /**
     * @return Returns the parentMsisdn.
     */
    public String getParentMsisdn() {
        return _parentMsisdn;
    }

    /**
     * @param parentMsisdn
     *            The parentMsisdn to set.
     */
    public void setParentMsisdn(String parentMsisdn) {
        _parentMsisdn = parentMsisdn;
    }

    public String getDomainName() {
        return _domainName;
    }

    public void setDomainName(String domainName) {
        _domainName = domainName;
    }

    /**
     * @return Returns the userBalanceList.
     */
    public List getUserBalanceList() {
        return _userBalanceList;
    }

    /**
     * @param userBalanceList
     *            The userBalanceList to set.
     */
    public void setUserBalanceList(List userBalanceList) {
        _userBalanceList = userBalanceList;
    }

    /**
     * @return Returns the domainList.
     */
    public ArrayList getDomainList() {
        return _domainList;
    }

    /**
     * @param domainList
     *            The domainList to set.
     */
    public void setDomainList(ArrayList domainList) {
        _domainList = domainList;
    }

    /**
     * @return Returns the serviceList.
     */
    public ArrayList getServiceList() {
        return _serviceList;
    }

    /**
     * @param serviceList
     *            The serviceList to set.
     */
    public void setServiceList(ArrayList serviceList) {
        _serviceList = serviceList;
    }
    
    public ArrayList getVoucherList(){
    	return _voucherList;
    }
    
    public void setVoucherList(ArrayList voucherList){
    	
    	_voucherList=voucherList;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("User ID =" + _userID);
        sbf.append(",User Name =" + _userName);
        sbf.append(",Network ID =" + _networkID);
        sbf.append(",Login ID =" + BTSLUtil.maskParam(_loginID));
        sbf.append(",Category Code =" + _categoryCode);
        sbf.append(",Parent ID =" + _parentID);
        sbf.append(",Parent Name =" + _parentName);
        sbf.append(",Owner ID =" + _ownerID);
        sbf.append(",Allowed IPs =" + _allowedIps);
        sbf.append(",Allowed Days =" + _allowedDays);
        sbf.append(",From Time =" + _fromTime);
        sbf.append(",To Time =" + _toTime);
        sbf.append(",Last Log On =" + _lastLoginOn);
        sbf.append(",Parent ID =" + _parentID);
        sbf.append(",Emp Code =" + _empCode);
        sbf.append(",Status =" + _status);
        sbf.append(",Previous Status =" + _previousStatus);
        sbf.append(",Email =" + _email);
        sbf.append(",Password Modified On =" + _passwordModifiedOn);
        sbf.append(",Contact No =" + _contactNo);
        sbf.append(",Designation =" + _designation);
        sbf.append(",Division Code =" + _divisionCode);
        sbf.append(",Department Code =" + _departmentCode);
        sbf.append(",Msisdn =" + _msisdn);
        sbf.append(",User Type =" + _userType);
        sbf.append(",Cretated On =" + _createdOn);
        sbf.append(",Created By =" + _createdBy);
        sbf.append(",Address 1 =" + _address1);
        sbf.append(",Address 2 =" + _address2);
        sbf.append(",City=" + _city);
        sbf.append(",State =" + _state);
        sbf.append(",Country =" + _country);
        sbf.append(",SSN =" + _ssn);
        sbf.append(",User Name Prefix =" + _userNamePrefix);
        sbf.append(",External Code =" + _externalCode);
        sbf.append(",User Code =" + _userCode);
        sbf.append(",Short Name =" + shortName);
        sbf.append(",Reference ID =" + referenceID);
        sbf.append(",Domain ID =" + _domainID);
        sbf.append(",Domain Name =" + _domainName);
        sbf.append(",Network Name =" + _networkName);
        sbf.append(",Message =" + _message);
        sbf.append(",Valid Request URLs =" + _validRequestURLs);
        sbf.append(",Valid Status =" + _validStatus);
        sbf.append(",Geographical Status =" + _geographicalCodeStatus);
        sbf.append(",Appointment Date =" + _appointmentDate);
        sbf.append(",Owner Msisdn" + _ownerMsisdn);
        sbf.append(",Owner CategoryName =" + _ownerCategoryName);
        sbf.append(",OwnerID =" + _ownerID);
        sbf.append(",OwnerName =" + _ownerName);
        sbf.append(",OwnerLoginId =" + _ownerLoginId);
        sbf.append(",ParentLoginId =" + _parentLoginId);
        sbf.append(",Parent Msisdn" + _parentMsisdn);
        sbf.append(",ParentCategoryName =" + _parentCategoryName);
        sbf.append(",ParentID =" + _parentID);
        sbf.append(",ParentName =" + _parentName);
        sbf.append(",UsingNewSTK =" + _usingNewSTK);
        sbf.append(",FxedInfoStr =" + _fxedInfoStr);
        sbf.append(",UpdateSimRequired =" + _updateSimRequired);
        sbf.append(",InvalidPasswordCount =" + _invalidPasswordCount);
        sbf.append(",FxedInfoStr =" + _fxedInfoStr);
        sbf.append(",PasswordCountUpdatedOn =" + _passwordCountUpdatedOn);
        sbf.append(",Report Header Name" + _reportHeaderName);
        sbf.append(",Logger Messge" + _loggerMessage);
        sbf.append(",Remote Address" + _remoteAddress);
        sbf.append(",Browser Type" + _browserType);
        sbf.append(",Login Time" + _loginTime);
        sbf.append(",Active UserID" + _activeUserID);
        if ((PretupsI.YES).equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOWD_USR_TYP_CREATION))))
			sbf.append(",allowedUserTypeCreation= "+allowedUserTypeCreation);
        sbf.append(",Document Type" + _documentType);
        sbf.append(",Document No" + _documentNo);
        sbf.append(",Payment Type" + _paymentType);
        
        if (_sessionInfoVO != null) {
            sbf.append(",Session ID" + _sessionInfoVO.getSessionID());
        }
        return sbf.toString();
    }

    public String getGeographicalCodeStatus() {
        return _geographicalCodeStatus;
    }

    public void setGeographicalCodeStatus(String geographicalCodeStatus) {
        _geographicalCodeStatus = geographicalCodeStatus;
    }

    public ArrayList getAssociatedProductTypeList() {
        return _associatedProductTypeList;
    }

    public void setAssociatedProductTypeList(ArrayList associatedProductTypeList) {
        _associatedProductTypeList = associatedProductTypeList;
    }

    /**
     * @return Returns the loggerMessage.
     */
    public String getLoggerMessage() {
        return _loggerMessage;
    }

    /**
     * @param loggerMessage
     *            The loggerMessage to set.
     */
    public void setLoggerMessage(String loggerMessage) {
        if (loggerMessage != null) {
            _loggerMessage = loggerMessage.trim();
        }
    }

    /**
     * @return Returns the browserType.
     */
    public String getBrowserType() {
        return _browserType;
    }

    /**
     * @param browserType
     *            The browserType to set.
     */
    public void setBrowserType(String browserType) {
        if (browserType != null) {
            _browserType = browserType.trim();
        }
    }

    /**
     * @return Returns the remoteAddress.
     */
    public String getRemoteAddress() {
        return _remoteAddress;
    }

    /**
     * @param remoteAddress
     *            The remoteAddress to set.
     */
    public void setRemoteAddress(String remoteAddress) {
        if (remoteAddress != null) {
            _remoteAddress = remoteAddress.trim();
        }
    }

    /**
     * @return Returns the appointmentDate.
     */
    public Date getAppointmentDate() {
        return _appointmentDate;
    }

    /**
     * @param appointmentDate
     *            The appointmentDate to set.
     */
    public void setAppointmentDate(Date appointmentDate) {
        _appointmentDate = appointmentDate;
    }

    public String getUserNameWithCategory() {
        return _userName + " (" + _categoryCode + ")";
    }

    public String getRequetedByUserName() {
        return _requetedByUserName;
    }

    public String getRequetedOnAsString() {
        if (_modifiedOn != null) {
            try {
                return BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(_modifiedOn));
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getSuspendedByUserName() {
        return _suspendedByUserName;
    }

    public String getSuspendedOnAsString() {
        if (_modifiedOn != null) {
            try {
                return BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(_modifiedOn));
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * @return Returns the domainStatus.
     */
    public String getDomainStatus() {
        return _domainStatus;
    }

    /**
     * @param domainStatus
     *            The domainStatus to set.
     */
    public void setDomainStatus(String domainStatus) {
        _domainStatus = domainStatus;
    }

    /**
     * @param requetedByUserName
     *            The requetedByUserName to set.
     */
    public void setRequetedByUserName(String requetedByUserName) {
        _requetedByUserName = requetedByUserName;
    }

    /**
     * @param suspendedByUserName
     *            The suspendedByUserName to set.
     */
    public void setSuspendedByUserName(String suspendedByUserName) {
        _suspendedByUserName = suspendedByUserName;
    }

    public String getCurrentModule() {
        return _currentModule;
    }

    public void setCurrentModule(String currentModule) {
        _currentModule = currentModule;
    }

    public String getCurrentRoleCode() {
        return _currentRoleCode;
    }

    public void setCurrentRoleCode(String currentRoleCode) {
        _currentRoleCode = currentRoleCode;
    }

    public String getDomainTypeCode() {
        return _domainTypeCode;
    }

    public void setDomainTypeCode(String domainTypeCode) {
        _domainTypeCode = domainTypeCode;
    }

    public String getRestrictedMsisdnAllow() {
        return _restrictedMsisdnAllow;
    }

    public void setRestrictedMsisdnAllow(String restrictedMsisdnAllow) {
        _restrictedMsisdnAllow = restrictedMsisdnAllow;
    }

    /**
     * @return Returns the _moduleCodeString.
     */
    public String getModuleCodeString() {
        return _moduleCodeString;
    }

    /**
     * @param codeString
     *            The _moduleCodeString to set.
     */
    public void setModuleCodeString(String codeString) {
        _moduleCodeString = codeString;
    }

    /**
     * @return Returns the _pageCodeString.
     */
    public String getPageCodeString() {
        return _pageCodeString;
    }

    /**
     * @param codeString
     *            The _pageCodeString to set.
     */
    public void setPageCodeString(String codeString) {
        _pageCodeString = codeString;
    }

    public String getPasswordReset() {
        return _passwordReset;
    }

    public void setPasswordReset(String passwordReset) {
        _passwordReset = passwordReset;
    }

    public String getPinReset() {
        return _pinReset;
    }

    public void setPinReset(String pinReset) {
        _pinReset = pinReset;
    }

    /**
     * @return Returns the activeUserMsisdn.
     */
    public String getActiveUserMsisdn() {
        return _activeUserMsisdn;
    }

    /**
     * @param activeUserMsisdn
     *            The activeUserMsisdn to set.
     */
    public void setActiveUserMsisdn(String activeUserMsisdn) {
        _activeUserMsisdn = activeUserMsisdn;
    }

    /**
     * @return Returns the activeUserPin.
     */
    public String getActiveUserPin() {
        return _activeUserPin;
    }

    /**
     * @param activeUserPin
     *            The activeUserPin to set.
     */
    public void setActiveUserPin(String activeUserPin) {
        _activeUserPin = activeUserPin;
    }

    /**
     * @return Returns the activeUserLoginId.
     */
    public String getActiveUserLoginId() {
        return _activeUserLoginId;
    }

    /**
     * @param activeUserLoginId
     *            The activeUserLoginId to set.
     */
    public void setActiveUserLoginId(String activeUserLoginId) {
        _activeUserLoginId = activeUserLoginId;
    }

    /*    *//**
     * @return Returns the msisdnAssigned.
     */
    /*
     * public boolean isMsisdnAssigned() {
     * return _msisdnAssigned;
     * }
     *//**
     * @param msisdnAssigned
     *            The msisdnAssigned to set.
     */
    /*
     * public void setMsisdnAssigned(boolean msisdnAssigned) {
     * _msisdnAssigned = msisdnAssigned;
     * }
     */
    /**
     * @return Returns the staffUserDetails.
     */
    public ChannelUserVO getStaffUserDetails() {
        return _staffUserDetails;
    }

    /**
     * @param staffUserDetails
     *            The staffUserDetails to set.
     */
    public void setStaffUserDetails(ChannelUserVO staffUserDetails) {
        _staffUserDetails = staffUserDetails;
    }

    // added by nilesh
    /**
     * @return Returns the longitude.
     */
    public String getLongitude() {
        return _longitude;
    }

    /**
     * @param longitude
     *            The longitude to set.
     */
    public void setLongitude(String longitude) {
        _longitude = longitude;
    }

    /**
     * @return Returns the latitude.
     */
    public String getLatitude() {
        return _latitude;
    }

    /**
     * @param latitude
     *            The latitude to set.
     */
    public void setLatitude(String latitude) {
        _latitude = latitude;
    }

    // added by Deepika Aggarwal
    /**
     * @return Returns the company.
     */
    public String getCompany() {
        return _company;
    }

    /**
     * @param company
     *            The company to set.
     */
    public void setCompany(String company) {
        _company = company;
    }

    /**
     * @return Returns the fax.
     */
    public String getFax() {
        return _fax;
    }

    /**
     * @param fax
     *            The fax to set.
     */
    public void setFax(String fax) {
        _fax = fax;
    }

    public String getFirstName() {
        return _firstName;
    }

    public void setFirstName(String name) {
        _firstName = name;
    }

    public String getLastName() {
        return _lastName;
    }

    public void setLastName(String name) {
        _lastName = name;
    }

    public String getLanguage() {
        return _language;
    }

    public void setLanguage(String _language) {
        this._language = _language;
    }

    // end added by deepika aggarwal

    public String getRsaFlag() {
        return _rsaFlag;
    }

    public void setRsaFlag(String rsaFlag) {
        _rsaFlag = rsaFlag;
    }

    public boolean isRsaAllowed() {
        return _rsaAllowed;
    }

    public void setRsaAllowed(boolean p_rsaAllowed) {
        _rsaAllowed = p_rsaAllowed;
    }

    public boolean getRsavalidated() {
        return _rsavalidated;
    }

    public void setRsavalidated(boolean p_rsavalidated) {
        _rsavalidated = p_rsavalidated;
    }

    public String getAuthType() {
        return _authType;
    }

    public void setAuthType(String type) {
        _authType = type;
    }

    public boolean getOTPValidated() {
        return _otpvalidated;
    }

    public void setOTPValidated(boolean _otpvalidated) {
        this._otpvalidated = _otpvalidated;
    }

    public String getAuthTypeAllowed() {
        return _authTypeAllowed;
    }

    public void setAuthTypeAllowed(String typeAllowed) {
        _authTypeAllowed = typeAllowed;
    }

    public String getShowPassword() {
		return showPassword;
	}

	public void setShowPassword(String showPassword) {
		this.showPassword = showPassword;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	 
	 public void setAllowedDay(String[] allowedDays) {
			this.allowedDays = allowedDays;
		}
	 
	 public void setPaymentTypes(String[] paymentTypes) {
			this.paymentTypes = paymentTypes;
		}
	 
 	 public void setCreated_On(String createdOn) {
			this.createdOn = createdOn;
		}
	 
		
		 public String getCategoryCodeDesc() {
				return categoryCodeDesc;
			}

			public void setCategoryCodeDesc(String categoryCodeDesc) {
				this.categoryCodeDesc = categoryCodeDesc;
			}
			public String getAppintmentDate() {
				return appintmentDate;
			}

			public void setAppintmentDate(String appintmentDate) {
				this.appintmentDate = appintmentDate;
			}
			public ArrayList getStatusList() {
				return statusList;
			}

			public void setStatusList(ArrayList statusList) {
				this.statusList = statusList;
			}
			 public String getUserNamePrefixCode() {
			        return userNamePrefixCode;
			    }

			   
			    public void setUserNamePrefixCode(String userNamePrefixCodes) {
			        if (userNamePrefixCodes != null) {
			            userNamePrefixCode = userNamePrefixCode.trim();
			        }
			    }
			    
			    public ArrayList getUserNamePrefixList() {
					return userNamePrefixList;
				}

				public void setUserNamePrefixList(ArrayList userNamePrefixList) {
					this.userNamePrefixList = userNamePrefixList;
				}
				 public String getUserNamePrefixDesc() {
				        return userNamePrefixDesc;
				    }

				    public void setUserNamePrefixDesc(String userNamePrefixDescs) {
				        if (userNamePrefixDescs != null) {
				            userNamePrefixDesc = userNamePrefixDesc.trim();
				        }
				    }
				    
				    public String getUserLanguage() {
				        return userLanguage;
				    }

				    public void setUserLanguage(String language) {
				        if (language != null) {
				            userLanguage = language.trim();
				        }
				    }
				    
				    public ArrayList getUserLanguageList() {
				        return userLanguageList;
				    }

				    public void setUserLanguageList(ArrayList userLanguageLists) {
				        userLanguageList = userLanguageLists;
				    }
				    
				    public String getUserLanguageDesc() {
				        return userLanguageDesc;
				    }

				    public void setUserLanguageDesc(String userLanguageDescs) {
				        if (userLanguageDescs != null) {
				            userLanguageDesc = userLanguageDescs.trim();
				        }
				    }
				    
				    public boolean getIsSerAssignChnlAdm() {
				        return isSerAssignChnlAdm;
				    }

				    public void setIsSerAssignChnlAdm(boolean serAssignChnlAdm) {
				        isSerAssignChnlAdm = serAssignChnlAdm;
				    }
				    
				    public ArrayList getGeographicalList() {
				        return geographicalList;
				    }


				    public void setGeographicalList(ArrayList geographicalLists) {
				        geographicalList = geographicalLists;
				    }
				    
				    public String getGrphDomainTypeName() {
				        if (grphDomainTypeName != null) {
				            return grphDomainTypeName.trim();
				        }

				        return grphDomainTypeName;
				    }

				    public void setGrphDomainTypeName(String grphDomainTypeNames) {
				        grphDomainTypeName = grphDomainTypeNames;
				    }
				    public String[] getGeographicalCodeArray() {
				        return geographicalCodeArray;
				    }


				    public void setGeographicalCodeArray(String[] geographicalCodeArrays) {
				        geographicalCodeArray = geographicalCodeArrays;
				    }
				    
				    public String getGeographicalCode() {
				        if (geographicalCode != null) {
				            return geographicalCode.trim();
				        }

				        return geographicalCode;
				    }
				    
				    public void setGeographicalCode(String geographicalCodes) {
				        geographicalCode = geographicalCodes;
				    }
				    
				    public ArrayList getNetworkList(){
				    	return networkList;
				    }
				    
				    public void setNetworkList(ArrayList networkLists){
				    	networkList = networkLists;
				    }
				    public String[] getRoleFlag() {
				        return roleFlag;
				    }

				    public void setRoleFlag(String[] roleFlags) {
				        roleFlag = roleFlags;
				    }
				    public HashMap getRolesMap() {
				        return rolesMap;
				    }

				    public void setRolesMap(HashMap rolesMaps) {
				        rolesMap = rolesMaps;
				    }
				    
				    public String getRoleType() {
				        return roleType;
				    }

				    public void setRoleType(String roleTypes) {
				        roleType = roleTypes;
				    }
				    
				    public ArrayList getServicesList() {
				        return servicesList;
				    }

				    public void setServicesList(ArrayList servicesLists) {
				        servicesList = servicesLists;
				    }

				  
				    public String[] getServicesTypes() {
				        return servicesTypes;
				    }

				   
				    public void setServicesTypes(String[] servicesTypess) {
				        servicesTypes = servicesTypess;
				    }
				    
				    public String[] getProductCodes() {
				        return productCodes;
				    }

				    public void setProductCodes(String[] productCodess) {
				        productCodes = productCodess;
				    }
				    
				    public ArrayList getProductsList() {
				        return productsList;
				    }

				    public void setProductsList(ArrayList productsLists) {
				        productsList = productsLists;
				    }

				    public void setRolesMapSelected(HashMap rolesMapsSelected) {
				        rolesMapSelected = rolesMapsSelected;
				    }

				    public int getRolesMapSelectedCount() {
				        if (rolesMapSelected != null && rolesMapSelected.size() > 0) {
				            return rolesMapSelected.size();
				        } else {
				            return 0;
				        }
				    }
				    public String getAllowedUserTypeCreation() {
				        return allowedUserTypeCreation;
				    }
				    
				    public void setAllowedUserTypeCreation(String allowedUserTypeCreations) {
				    	allowedUserTypeCreation = allowedUserTypeCreations;
				    }
				    public String getDocumentType() {
				        return _documentType;
				    }
				    public void setDocumentType(String documentType) {
				    	_documentType = documentType;
				    }
				    public String getDocumentNo() {
				        return _documentNo;
				    }
				    public void setDocumentNo(String documentNo) {
				    	_documentNo = documentNo;
				    }
				    public String getPaymentType() {
				        return _paymentType;
				    }
				    public void setPaymentType(String paymentType) {
				    	_paymentType = paymentType;
				    }
				    
					public String getOwnerCompany() {
						return _ownerCompany;
					}

					public void setOwnerCompany(String company) {
						_ownerCompany = company;
					}

					public ArrayList getSegmentList() {
						return segmentList;
					}

					public void setSegmentList(ArrayList segmentList) {
						this.segmentList = segmentList;
					}
					
					
				    public void setSuspendedOn(Date suspendOn) {
				        
				    	this._suspendOn = suspendOn;
				    }

					public String getOwnerLoginId() {
						return _ownerLoginId;
					}

					public void setOwnerLoginId(String _ownerLoginId) {
						this._ownerLoginId = _ownerLoginId;
					}

					public String getParentLoginId() {
						return _parentLoginId;
					}

					public void setParentLoginId(String _parentLoginId) {
						this._parentLoginId = _parentLoginId;
					}
							    	    
}
