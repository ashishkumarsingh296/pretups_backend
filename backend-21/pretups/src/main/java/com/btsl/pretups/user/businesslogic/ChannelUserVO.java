package com.btsl.pretups.user.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeCountersVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * @(#)ChannelUserVO.java
 *                        Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        Travelling object for channel user
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Gurjeet Singh Bedi 03/07/2005 Initial Creation
 *                        Harpreet kaur 28/09/2011 Updation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 */

public class ChannelUserVO extends UserVO implements Serializable, Comparable {
  //  public Log _log = LogFactory.getLog(this.getClass().getName());
    private String _userGrade;// used for Channel Person
    private String _userGradeName;// used for Channel Person
    private String _transferProfileID;// used for Channel Person
    private String _transferProfileName;// used for Channel Person
    private String _transferProfileStatus;// used for Channel Person
    private String _commissionProfileSetID;// used for Channel Person
    private String _commissionProfileSetName;// used for Channel Person
    private String _commissionProfileSetVersion;// used for Channel Person
    private String _commissionProfileStatus;
    private String _inSuspend;// used for Channel Person
    private String _outSuspened;// used for Channel Person
    private String _geographicalCode;
    private String _geographicalDesc;
    private String _outletCode;
    private String _subOutletCode;
    private Date _activatedOn;

    private String _transferRuleID; // user transfer rule id associated with
    // user

    private String _userlevel;
    private String _userIDPrefix;
    // added by sandeep goel to show information in the ICCID MSISDN KEY
    // MANAGEMENT Module
    private String _categoryName;
    private ArrayList<ListValueVO> _catgeoryList;
    private String _smsPin;
    private String _pinRequired;
    private int _invalidPinCount;
    private String _commissionProfileLang1Msg = null;
    private String _commissionProfileLang2Msg = null;
    private String _commissionProfileSuspendMsg = null;
    private String _transferCategory = null;
    private Date _commissionProfileApplicableFrom = null;
    private String _commissionProfileApplicableFromAsString = null;
    private String _languageCode = null;
    private String _languageName = null;
    private GroupTypeCountersVO _userControlGrouptypeCounters;
    private GroupTypeCountersVO _userChargeGrouptypeCounters;

    // Added by amit for bulk user module.
    private String _recordNumber;
    private String _groupRoleCode;
    private String _parentLoginID;
    private String _serviceTypes;
    private String _voucherTypes;
    private String segments;
    private String _parentStatus;
    private String _groupRoleFlag = null; // Added by Sanjeew Date 03/04/07

    // for Zebra and Tango
    // _applicationID,_mpayProfileID,_userProfileID,_accessType added by sanjeew
    // date 06/07/07
    private String _applicationID = null;
    private String _mpayProfileID = null;
    private String _userProfileID = null;
    private String _accessType = null;
    private String _mcommerceServiceAllow = null;

    // for low balance alert
    private String _lowBalAlertAllow;
    private String _primaryMsisdn;
    // End Zebra and Tango
    private String _userBalance = null;

    // Changes for transferred user report
    private long _balance;
    private String _balanceStr = null;
    private long _previousBalance;
    private String _prevBalanceStr = null;
    private ArrayList _trnsfrdUserHierhyList;
    private String _prevUserName = null;
    private String _prevParentName = null;
    private String _prevUserId = null;
    private String _prevCategoryCode = null;
    private String _msisdnPrefix = null;
    private String _networkCode = null;
    // End of transferred user report.

    private String _phoneProfile = null;

    private String _primaryMsisdnPin;
    private String _multipleMsisdnlist;
    private boolean _isFirstExternalUserModify = false;
    private String _smsMSisdn = null;
    private long _prefixId = 0;
    // This field is added(by Rajdeep) to get the low balance alert status for
    // the user's category
    private String _catLowBalanceAlertAllow;
    private String _catOutletAllowed;
    private Locale _parentLocale = null;
    private int maxUserLevel = 0;
    private String _productCode = null;
    private String _alertMsisdn;
    private String _alertType;

    // Added by Harpreet for low balance Alert_Email
    private String _alertEmail;


    // added by gaurav pandey for channel user logs
    private String[] serviceTypeList;

    private String _trannferRuleTypeId;
    private String _cellID;
    private String _switchID;
    // Added For LMS by Vibhu- 3/1/2014
    private String _lmsProfile;
    // added for OTP
    private String _otp = null;
    private Date _otpModifiedOn;
    private String _autoc2callowed;
    private String _autoc2cquantity = null;
    private String _channelUserID;
    private long _maxTxnAmount;
    // Added by Aatif
    private String _lmsProfileId; // used for Channel Person

    private String _parentGeographyCode;
    private String _decryptionKey;
    private String _imei;
    private ArrayList _asscMsisdnList;
    private Date _asscMsisdnDate;

    private String _optInOutStatus;
    private String _controlGroup;

    private Object _resetPinOTPMessage;
    private String _securityAnswer;

    private long _monthlyTransAmt;
    private String _gateway;
    private int _otpInvalidCount = 0;
    
    // Added by Sadhan for MObileApp Token last used Date.
    private Date tokenLastUsedDate;
    private String sosAllowed = null;
    private long sosAllowedAmount = 0;
    private long sosThresholdLimit = 0;
    private String _geographicalCodeforNewuser;/* Added to get value of geography code */
    /* Added to get value of geography code */
	
	private String lastSosTransactionId;
	private String lastSosStatus;
	private String lastSosProductCode;
	private String lrAllowed = null;
	private long lrMaxAmount = 0;
	private long lrTransferAmount =0;
	private boolean isReturnFlag = false;
	private String toCategoryCode;
	private String toUserName;
	private String fromUserName;
	private String fromCategoryCode;
	private String nameAndId;
	private String userPhonesId;
	private String _othCommSetId;
	private String dualCommissionType;
	private String parentCategoryCode;
	private String ownerCategoryCode;
	
	private String loanProfileId;
	
    private String _autoo2callowed;
	private long autoO2CThresholdLimit = 0;
	private long autoO2CTxnValue = 0;
	
	public String getLoanProfileId() {
		return loanProfileId;
	}
	public void setLoanProfileId(String loanProfileId) {
		
		this.loanProfileId=loanProfileId;
	}
	
	
	public String getParentCategoryCode() {
		return parentCategoryCode;
	}

	public void setParentCategoryCode(String parentCategoryCode) {
		this.parentCategoryCode = parentCategoryCode;
	}

	public String getOwnerCategoryCode() {
		return ownerCategoryCode;
	}

	public void setOwnerCategoryCode(String ownerCategoryCode) {
		this.ownerCategoryCode = ownerCategoryCode;
	}

	
	 
	public String getDualCommissionType() {
		return dualCommissionType;
	}

	public void setDualCommissionType(String dualCommissionType) {
		this.dualCommissionType = dualCommissionType;
	}

	public String getUserPhonesId() {
		return userPhonesId;
	}
	
	public void setUserPhonesId(String userPhonesId) {
		this.userPhonesId = userPhonesId;
	}

	public String getNameAndId() {
		return nameAndId;
	}

	public void setNameAndId(String nameAndId) {
		this.nameAndId = nameAndId;
	}
	public String getFromCategoryCode() {
		return fromCategoryCode;
	}
	public void setFromCategoryCode(String fromCategoryCode) {
		this.fromCategoryCode = fromCategoryCode;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getToCategoryCode() {
		return toCategoryCode;
	}
	public void setToCategoryCode(String toCategoryCode) {
		this.toCategoryCode = toCategoryCode;
	}
	public boolean isReturnFlag() {
		return isReturnFlag;
	}
	public void setReturnFlag(boolean isReturnFlag) {
		this.isReturnFlag = isReturnFlag;
	}
	public long getLrTransferAmount() {
		return lrTransferAmount;
	}
	public void setLrTransferAmount(long lrTransferAmount) {
		this.lrTransferAmount = lrTransferAmount;
	}
	public String getLrAllowed() {
		return lrAllowed;
	}
	public void setLrAllowed(String lrAllowed) {
		this.lrAllowed = lrAllowed;
	}
	public long getLrMaxAmount() {
		return lrMaxAmount;
	}
	public void setLrMaxAmount(long lrMaxAmount) {
		this.lrMaxAmount = lrMaxAmount;
	}
	
	public String getLastSosTransactionId() {
		return lastSosTransactionId;
	}
	public void setLastSosTransactionId(String lastSosTransactionId) {
		this.lastSosTransactionId = lastSosTransactionId;
	}
	public String getLastSosStatus() {
		return lastSosStatus;
	}
	public void setLastSosStatus(String lastSosStatus) {
		this.lastSosStatus = lastSosStatus;
	}
	public String getLastSosProductCode() {
		return lastSosProductCode;
	}
	public void setLastSosProductCode(String lastSosProductCode) {
		this.lastSosProductCode = lastSosProductCode;
	}
	public Date getTokenLastUsedDate() {
		return tokenLastUsedDate;
	}
	public void setTokenLastUsedDate(Date tokenLastUsedDate) {
		this.tokenLastUsedDate = tokenLastUsedDate;
	}
    public String getgeographicalCodeforNewuser() {
    	return _geographicalCodeforNewuser;
    }
    /* Added to set value of geography code */
    public void setgeographicalCodeforNewuser(String _geographicalCodeforNewuser) {
    	this._geographicalCodeforNewuser = _geographicalCodeforNewuser;
    }
    public String getGateway() {
        return _gateway;
    }

    public void setGateway(String _gateway) {
        this._gateway = _gateway;
    }

    /**
     * @return the _monthlyTransAmt
     */
    public long getMonthlyTransAmt() {
        return _monthlyTransAmt;
    }

    /**
     * @param _monthlyTransAmt
     *            the _monthlyTransAmt to set
     */
    public void setMonthlyTransAmt(long _monthlyTransAmt) {
        this._monthlyTransAmt = _monthlyTransAmt;
    }

    public String[] getServiceTypeList() {
        return serviceTypeList;
    }

    public void setServiceTypeList(String[] serviceTypeList) {
        this.serviceTypeList = serviceTypeList;
    }

    /**
     * @return the maxUserLevel
     */
    public int getMaxUserLevel() {
        return maxUserLevel;
    }

    /**
     * @param maxUserLevel
     *            the maxUserLevel to set
     */
    public void setMaxUserLevel(int maxUserLevel) {
        this.maxUserLevel = maxUserLevel;
    }

    // Sort the VO corresponding to SeqNum
    public int compareTo(Object arg0) {
        final String METHOD_NAME = "compareTo";
        final ChannelUserVO obj = (ChannelUserVO) arg0;
        try {
            if (this.getCategoryVO().getSequenceNumber() > obj.getCategoryVO().getSequenceNumber()) {
                return 1;
            }
            return -1;
        } catch (Exception e) {
         //   _log.errorTrace(METHOD_NAME, e);
            return 1;
        }
    }

    /**
     * @return Returns the groupRoleFlag.
     */
    public String getGroupRoleFlag() {
        return _groupRoleFlag;
    }

    /**
     * @param groupRoleFlag
     *            The groupRoleFlag to set.
     */
    public void setGroupRoleFlag(String groupRoleFlag) {
        _groupRoleFlag = groupRoleFlag;
    }

    /**
     * @return Returns the parentStatus.
     */
    public String getParentStatus() {
        return _parentStatus;
    }

    /**
     * @param parentStatus
     *            The parentStatus to set.
     */
    public void setParentStatus(String parentStatus) {
        _parentStatus = parentStatus;
    }

    /**
     * @return Returns the serviceTypes.
     */
    public String getServiceTypes() {
        return _serviceTypes;
    }

    /**
     * @param serviceTypes
     *            The serviceTypes to set.
     */
    public void setServiceTypes(String serviceTypes) {
        _serviceTypes = serviceTypes;
    }

    public String getVoucherTypes() {
        return _voucherTypes;
    }
    
    public void setVoucherTypes(String voucherTypes) {
        _voucherTypes = voucherTypes;
    }
    /**
     * @return Returns the parentLoginID.
     */
    public String getParentLoginID() {
        return _parentLoginID;
    }

    /**
     * @param parentLoginID
     *            The parentLoginID to set.
     */
    public void setParentLoginID(String parentLoginID) {
        _parentLoginID = parentLoginID;
    }

    /**
     * @return Returns the groupRoleCode.
     */
    public String getGroupRoleCode() {
        return _groupRoleCode;
    }

    /**
     * @param groupRoleCode
     *            The groupRoleCode to set.
     */
    public void setGroupRoleCode(String groupRoleCode) {
        _groupRoleCode = groupRoleCode;
    }

    public String getRecordNumber() {
        return _recordNumber;
    }

    /**
     * @param recordNumber
     *            The recordNumber to set.
     */
    public void setRecordNumber(String recordNumber) {
        _recordNumber = recordNumber;
    }

    /**
     * @return Returns the languageCode.
     */
    public String getLanguageCode() {
        return _languageCode;
    }

    /**
     * @param languageCode
     *            The languageCode to set.
     */
    public void setLanguageCode(String languageCode) {
        _languageCode = languageCode;
    }

    /**
     * @return Returns the languageName.
     */
    public String getLanguageName() {
        return _languageName;
    }

    /**
     * @param languageName
     *            The languageName to set.
     */
    public void setLanguageName(String languageName) {
        _languageName = languageName;
    }

    /**
     * @return Returns the invalidPinCount.
     */
    public int getInvalidPinCount() {
        return _invalidPinCount;
    }

    /**
     * @param invalidPinCount
     *            The invalidPinCount to set.
     */
    public void setInvalidPinCount(int invalidPinCount) {
        _invalidPinCount = invalidPinCount;
    }

    // ends here

    public String getUserGrade() {
        return _userGrade;
    }

    public void setUserGrade(String userGrade) {
        _userGrade = userGrade;
    }

    public String getCommissionProfileSetID() {
        return _commissionProfileSetID;
    }

    public void setCommissionProfileSetID(String commissionProfileSetID) {
        _commissionProfileSetID = commissionProfileSetID;
    }

    public String getTransferProfileID() {
        return _transferProfileID;
    }

    public void setTransferProfileID(String transferProfileID) {
        _transferProfileID = transferProfileID;
    }


    public String getInSuspend() {
        return _inSuspend;
    }

    public void setInSuspend(String inSuspend) {
        _inSuspend = inSuspend;
    }

    public String getOutSuspened() {
        return _outSuspened;
    }

    public void setOutSuspened(String outSuspened) {
        _outSuspened = outSuspened;
    }

    public String getCommissionProfileSetName() {
        return _commissionProfileSetName;
    }

    public void setCommissionProfileSetName(String commissionProfileSetName) {
        _commissionProfileSetName = commissionProfileSetName;
    }

    public String getUserGradeName() {
        return _userGradeName;
    }

    public void setUserGradeName(String userGradeName) {
        _userGradeName = userGradeName;
    }

    public String getCommissionProfileSetVersion() {
        return _commissionProfileSetVersion;
    }

    public void setCommissionProfileSetVersion(String commissionProfileSetVersion) {
        _commissionProfileSetVersion = commissionProfileSetVersion;
    }

    public String getTransferProfileName() {
        return _transferProfileName;
    }

    public void setTransferProfileName(String transferProfileName) {
        _transferProfileName = transferProfileName;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    public String getPinRequired() {
        return _pinRequired;
    }

    public void setPinRequired(String pinRequired) {
        _pinRequired = pinRequired;
    }

    public String getSmsPin() {
        return _smsPin;
    }

    public void setSmsPin(String smsPin) {
        _smsPin = smsPin;
    }

    public String getGeographicalCode() {
        return _geographicalCode;
    }

    public void setGeographicalCode(String userGeographicalCode) {
        _geographicalCode = userGeographicalCode;
    }

    public String getUserlevel() {
        return _userlevel;
    }

    public void setUserlevel(String userlevel) {
        _userlevel = userlevel;
    }

    public String getUserIDPrefix() {
        return _userIDPrefix;
    }

    public void setUserIDPrefix(String userIDPrefix) {
        _userIDPrefix = userIDPrefix;
    }



    public String getGeographicalDesc() {
        return _geographicalDesc;
    }

    public void setGeographicalDesc(String geographicalDesc) {
        _geographicalDesc = geographicalDesc;
    }

    public String getCommissionProfileStatus() {
        return _commissionProfileStatus;
    }

    public void setCommissionProfileStatus(String commissionProfileStatus) {
        _commissionProfileStatus = commissionProfileStatus;
    }

    public String getTransferProfileStatus() {
        return _transferProfileStatus;
    }

    public void setTransferProfileStatus(String transferProfileStatus) {
        _transferProfileStatus = transferProfileStatus;
    }

    public String toString() {
        final StringBuilder sbf = new StringBuilder();
        sbf.append(super.toString());
        sbf.append("User Grade =").append(_userGrade);
        sbf.append(",TransferProfileID =").append(_transferProfileID);
        sbf.append(",CommissionProfileSetID =").append(_commissionProfileSetID);
        sbf.append(",InSuspend =").append(_inSuspend);
        sbf.append(",OutSuspened=").append(_outSuspened);
        sbf.append(",GeographicalCode =").append(_geographicalCode);
        sbf.append(",OutletCode =").append(_outletCode);
        sbf.append(",SubOutletCode=").append(_subOutletCode);
        sbf.append(",ActivatedOn=").append(_activatedOn);
        sbf.append(",UserBalance=").append(_userBalance);

        // for Zebra and Tango by sanjeew date 06/07/07
        sbf.append(",Application ID=").append(_applicationID);
        sbf.append(",MpayProfileID=").append(_mpayProfileID);
        sbf.append(",UserProfileID=").append(_userProfileID);
        sbf.append(",AccessType=").append(_accessType);
        sbf.append(",M-Commerce Service Allow=").append(_mcommerceServiceAllow);
        sbf.append(",Low Balance Alert Allow=").append(_lowBalAlertAllow);
        // End Zebra and Tango
        sbf.append(",PrimaryMsisdnPin=").append(_primaryMsisdnPin);
        // Added by Vikas Jauhari for Alert Msisdn
        sbf.append(",Alert Msisdn=").append(_alertMsisdn);
        sbf.append(",_lmsProfile=").append(_lmsProfile);
        sbf.append(",_lmsProfileId=").append(_lmsProfileId);
        sbf.append(", sosAllowed = ").append(sosAllowed);
        sbf.append(", sosAllowedAmount = ").append(sosAllowedAmount);
        sbf.append(", sosThresholdLimit = ").append(sosThresholdLimit);
        sbf.append(", catgeoryList = ").append(_catgeoryList);
        
        return sbf.toString();
    }

    public String getTransferRuleID() {
        return _transferRuleID;
    }

    public void setTransferRuleID(String transferRuleID) {
        _transferRuleID = transferRuleID;
    }

    /**
     * @return Returns the outletCode.
     */
    public String getOutletCode() {
        return _outletCode;
    }

    /**
     * @param outletCode
     *            The outletCode to set.
     */
    public void setOutletCode(String outletCode) {
        _outletCode = outletCode;
    }

    /**
     * @return Returns the subOutletCode.
     */
    public String getSubOutletCode() {
        return _subOutletCode;
    }

    /**
     * @param subOutletCode
     *            The subOutletCode to set.
     */
    public void setSubOutletCode(String subOutletCode) {
        _subOutletCode = subOutletCode;
    }

    /**
     * @return Returns the activatedOn.
     */
    public Date getActivatedOn() {
        return _activatedOn;
    }

    /**
     * @param activatedOn
     *            The activatedOn to set.
     */
    public void setActivatedOn(Date activatedOn) {
        _activatedOn = activatedOn;
    }

    public String getCommissionProfileLang1Msg() {
        return _commissionProfileLang1Msg;
    }

    public void setCommissionProfileLang1Msg(String commissionProfileLang1Msg) {
        _commissionProfileLang1Msg = commissionProfileLang1Msg;
    }

    public String getCommissionProfileLang2Msg() {
        return _commissionProfileLang2Msg;
    }

    public void setCommissionProfileLang2Msg(String commissionProfileLang2Msg) {
        _commissionProfileLang2Msg = commissionProfileLang2Msg;
    }

    public String getCommissionProfileSuspendMsg() {
        return _commissionProfileSuspendMsg;
    }

    public void setCommissionProfileSuspendMsg(String commissionProfileSuspendMsg) {
        _commissionProfileSuspendMsg = commissionProfileSuspendMsg;
    }

    public String getTransferCategory() {
        return _transferCategory;
    }

    public void setTransferCategory(String transferCategory) {
        _transferCategory = transferCategory;
    }

    public Date getCommissionProfileApplicableFrom() {
        return _commissionProfileApplicableFrom;
    }

    public void setCommissionProfileApplicableFrom(Date commissionProfileApplicableFrom) {
        _commissionProfileApplicableFrom = commissionProfileApplicableFrom;
    }

    public GroupTypeCountersVO getUserChargeGrouptypeCounters() {
        return _userChargeGrouptypeCounters;
    }

    public void setUserChargeGrouptypeCounters(GroupTypeCountersVO userChargeGrouptypeCounters) {
        _userChargeGrouptypeCounters = userChargeGrouptypeCounters;
    }

    public GroupTypeCountersVO getUserControlGrouptypeCounters() {
        return _userControlGrouptypeCounters;
    }

    public void setUserControlGrouptypeCounters(GroupTypeCountersVO userControlGrouptypeCounters) {
        _userControlGrouptypeCounters = userControlGrouptypeCounters;
    }

    /**
     * @return Returns the accessType.
     */
    public String getAccessType() {
        return _accessType;
    }

    /**
     * @param accessType
     *            The accessType to set.
     */
    public void setAccessType(String accessType) {
        _accessType = accessType;
    }

    /**
     * @return Returns the applicationID.
     */
    public String getApplicationID() {
        return _applicationID;
    }

    /**
     * @param applicationID
     *            The applicationID to set.
     */
    public void setApplicationID(String applicationID) {
        _applicationID = applicationID;
    }

    /**
     * @return Returns the mpayProfileID.
     */
    public String getMpayProfileID() {
        return _mpayProfileID;
    }

    /**
     * @param mpayProfileID
     *            The mpayProfileID to set.
     */
    public void setMpayProfileID(String mpayProfileID) {
        _mpayProfileID = mpayProfileID;
    }

    /**
     * @return Returns the userProfileID.
     */
    public String getUserProfileID() {
        return _userProfileID;
    }

    /**
     * @param userProfileID
     *            The userProfileID to set.
     */
    public void setUserProfileID(String userProfileID) {
        _userProfileID = userProfileID;
    }

    /**
     * @return Returns the mCommerceServiceAllow.
     */
    public String getMcommerceServiceAllow() {
        return _mcommerceServiceAllow;
    }

    /**
     * @param commerceServiceAllow
     *            The mCommerceServiceAllow to set.
     */
    public void setMcommerceServiceAllow(String commerceServiceAllow) {
        _mcommerceServiceAllow = commerceServiceAllow;
    }

    /**
     * @return Returns the lowBalAlertAllow.
     */
    public String getLowBalAlertAllow() {
        return _lowBalAlertAllow;
    }

    /**
     * @param lowBalAlertAllow
     *            The lowBalAlertAllow to set.
     */
    public void setLowBalAlertAllow(String lowBalAlertAllow) {
        _lowBalAlertAllow = lowBalAlertAllow;
    }

    /**
     * @return Returns the primaryMsisdn.
     */
    public String getPrimaryMsisdn() {
        return _primaryMsisdn;
    }

    /**
     * @param primaryMsisdn
     *            The primaryMsisdn to set.
     */
    public void setPrimaryMsisdn(String primaryMsisdn) {
        _primaryMsisdn = primaryMsisdn;
    }

    /**
     * @return Returns the _balance.
     */
    public long getBalance() {
        return _balance;
    }

    /**
     * @param _balance
     *            The _balance to set.
     */
    public void setBalance(long balance) {
        this._balance = balance;
    }

    /**
     * @return Returns the _balanceStr.
     */
    public String getBalanceStr() {
        return _balanceStr;
    }

    /**
     * @param str
     *            The _balanceStr to set.
     */
    public void setBalanceStr(String balanceStr) {
        _balanceStr = balanceStr;
    }

    /**
     * @return Returns the _previousBalance.
     */
    public long getPreviousBalance() {
        return _previousBalance;
    }

    /**
     * @param balance
     *            The _previousBalance to set.
     */
    public void setPreviousBalance(long previousBalance) {
        _previousBalance = previousBalance;
    }

    /**
     * @return Returns the _trnsfrdUserHierhyList.
     */
    public ArrayList getTrnsfrdUserHierhyList() {
        return _trnsfrdUserHierhyList;
    }

    /**
     * @param userHierhyList
     *            The _trnsfrdUserHierhyList to set.
     */
    public void setTrnsfrdUserHierhyList(ArrayList trnsfrdUserHierhyList) {
        _trnsfrdUserHierhyList = trnsfrdUserHierhyList;
    }

    /**
     * @return Returns the _prevBalanceStr.
     */
    public String getPrevBalanceStr() {
        return _prevBalanceStr;
    }

    /**
     * @param balanceStr
     *            The _prevBalanceStr to set.
     */
    public void setPrevBalanceStr(String prevBalanceStr) {
        _prevBalanceStr = prevBalanceStr;
    }

    /**
     * @return Returns the _prevParentName.
     */
    public String getPrevParentName() {
        return _prevParentName;
    }

    /**
     * @param parentName
     *            The _prevParentName to set.
     */
    public void setPrevParentName(String parentName) {
        _prevParentName = parentName;
    }

    /**
     * @return Returns the _prevUserId.
     */
    public String getPrevUserId() {
        return _prevUserId;
    }

    /**
     * @param userId
     *            The _prevUserId to set.
     */
    public void setPrevUserId(String userId) {
        _prevUserId = userId;
    }

    /**
     * @return Returns the _prevUserName.
     */
    public String getPrevUserName() {
        return _prevUserName;
    }

    /**
     * @param userName
     *            The _prevUserName to set.
     */
    public void setPrevUserName(String userName) {
        _prevUserName = userName;
    }

    /**
     * @return Returns the _prevCategoryCode.
     */
    public String getPrevCategoryCode() {
        return _prevCategoryCode;
    }

    /**
     * @param categoryCode
     *            The _prevCategoryCode to set.
     */
    public void setPrevCategoryCode(String categoryCode) {
        _prevCategoryCode = categoryCode;
    }

    public String getPrevUserNameWithCategory() {
        /*
         * String userCatname=null;
         * if(BTSLUtil.isNullString(_prevUserName)||BTSLUtil.isNullString(
         * _prevCategoryCode))
         * userCatname="";
         * else
         * userCatname=_prevUserName+" ("+_prevCategoryCode+")";
         * return userCatname;
         */
        final StringBuilder userCatname = new StringBuilder();
        if (BTSLUtil.isNullString(_prevUserName) || BTSLUtil.isNullString(_prevCategoryCode)) {
            userCatname.append("");
        } else {
            userCatname.append(_prevUserName).append(" (").append(_prevCategoryCode).append(")");
        }
        return userCatname.toString();
    }

    public String getPrevUserParentNameWithCategory() {
        /*
         * String userParentCat= null;
         * if(BTSLUtil.isNullString(_prevUserName)||BTSLUtil.isNullString(
         * _prevCategoryCode) || BTSLUtil.isNullString(_prevParentName))
         * userParentCat="";
         * else
         * userParentCat=_prevUserName+" ("+_prevCategoryCode+")"+"("+
         * _prevParentName+")";
         * return userParentCat;
         */

        final StringBuilder userParentCat = new StringBuilder();
        if (BTSLUtil.isNullString(_prevUserName) || BTSLUtil.isNullString(_prevCategoryCode) || BTSLUtil.isNullString(_prevParentName)) {
            userParentCat.append("");
        } else {
            userParentCat.append(_prevUserName).append(" (").append(_prevCategoryCode).append(")").append("(").append(_prevParentName).append(")");
        }
        return userParentCat.toString();
    }

    public String getUserBalance() {
        return _userBalance;
    }

    public void setUserBalance(String userBalance) {
        _userBalance = userBalance;
    }

    public String getMsisdnPrefix() {
        return _msisdnPrefix;
    }

    public void setMsisdnPrefix(String msisdnPrefix) {
        _msisdnPrefix = msisdnPrefix;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the _primaryMsisdnPin.
     */
    public String getPrimaryMsisdnPin() {
        return _primaryMsisdnPin;
    }

    /**
     * @param msisdnPin
     *            The _primaryMsisdnPin to set.
     */
    public void setPrimaryMsisdnPin(String msisdnPin) {
        _primaryMsisdnPin = msisdnPin;
    }

    /**
     * @return Returns the _multipleMsisdnlist.
     */
    public String getMultipleMsisdnlist() {
        return _multipleMsisdnlist;
    }

    /**
     * @param msisdnlist
     *            The _multipleMsisdnlist to set.
     */
    public void setMultipleMsisdnlist(String msisdnlist) {
        _multipleMsisdnlist = msisdnlist;
    }

    /**
     * @return Returns the isFirstExternalUserModify.
     */
    public boolean isFirstExternalUserModify() {
        return _isFirstExternalUserModify;
    }

    /**
     * @param isFirstExternalUserModify
     *            The isFirstExternalUserModify to set.
     */
    public void setFirstExternalUserModify(boolean isFirstExternalUserModify) {
        _isFirstExternalUserModify = isFirstExternalUserModify;
    }

    /**
     * @return Returns the smsMSisdn.
     */
    public String getSmsMSisdn() {
        return _smsMSisdn;
    }

    /**
     * @param smsMSisdn
     *            The smsMSisdn to set.
     */
    public void setSmsMSisdn(String smsMSisdn) {
        _smsMSisdn = smsMSisdn;
    }

    /**
     * /**
     * 
     * @return Returns the phoneProfile.
     */
    public String getPhoneProfile() {
        return _phoneProfile;
    }

    /**
     * @param phoneProfile
     *            The phoneProfile to set.
     */
    public void setPhoneProfile(String phoneProfile) {
        _phoneProfile = phoneProfile;
    }

    /**
     * @return Returns the prefixId.
     */
    public long getPrefixId() {
        return _prefixId;
    }

    /**
     * @param prefixId
     *            The prefixId to set.
     */
    public void setPrefixId(long prefixId) {
        _prefixId = prefixId;
    }

    /**
     * @return Returns the _catLowBalanceAlertAllow.
     */
    public String getCatLowBalanceAlertAllow() {
        return _catLowBalanceAlertAllow;
    }

    /**
     * @param lowBalanceAlertAllow
     *            The _catLowBalanceAlertAllow to set.
     */
    public void setCatLowBalanceAlertAllow(String lowBalanceAlertAllow) {
        _catLowBalanceAlertAllow = lowBalanceAlertAllow;
    }

    /**
     * @return Returns the _catOutletAllowed.
     */
    public String getCatOutletAllowed() {
        return _catOutletAllowed;
    }

    /**
     * @param outletAllowed
     *            The _catOutletAllowed to set.
     */
    public void setCatOutletAllowed(String outletAllowed) {
        _catOutletAllowed = outletAllowed;
    }

    /**
     * @return Returns the parentLocale.
     */
    public Locale getParentLocale() {
        return _parentLocale;
    }

    /**
     * @param parentLocale
     *            The parentLocale to set.
     */
    public void setParentLocale(Locale parentLocale) {
        _parentLocale = parentLocale;
    }

    // added by nilesh

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getProductCode() {
        return _productCode;
    }

    /**
     * @return Returns the alertMsisdn.
     */

    public String getAlertMsisdn() {
        return _alertMsisdn;
    }

    /**
     * @param alertMsisdn
     *            The alertMsisdn to set.
     */
    public void setAlertMsisdn(String alertMsisdn) {
        _alertMsisdn = alertMsisdn;
    }

    // Added by Harpreet

    /**
     * @return Returns the alertType.
     */
    public String getAlertType() {
        return _alertType;
    }

    /**
     * @param alertType
     *            The alertType to set.
     */
    public void setAlertType(String alertType) {
        _alertType = alertType;
    }

    // Added by Harpreet

    /**
     * @return Returns the alertEmail.
     */
    public String getAlertEmail() {
        return _alertEmail;
    }

    /**
     * @param alertType
     *            The alertEmail to set.
     */
    public void setAlertEmail(String alertEmail) {
        _alertEmail = alertEmail;
    }

    // added by nilesh
    /**
     * @return Returns the longitude.
     */
    

    /**
     * @return Returns the latitude.
     */

    /**
     * @return Returns the transactionProfile.
     */
    public String getTrannferRuleTypeId() {
        if (_trannferRuleTypeId != null) {
            return _trannferRuleTypeId.trim();
        }

        return _trannferRuleTypeId;
    }

    /**
     * @param transactionRuleType
     *            The transactionRuleType to set.
     */
    public void setTrannferRuleTypeId(String transactionProfile) {
        _trannferRuleTypeId = transactionProfile;
    }

    /**
     * @return the _cellID
     */
    public String getCellID() {
        return _cellID;
    }

    /**
     * @param _cellid
     *            the _cellID to set
     */
    public void setCellID(String _cellid) {
        _cellID = _cellid;
    }

    /**
     * @return the _switchID
     */
    public String getSwitchID() {
        return _switchID;
    }

    /**
     * @param _switchid
     *            the _switchID to set
     */
    public void setSwitchID(String _switchid) {
        _switchID = _switchid;
    }

    public String getLmsProfile() {
        return _lmsProfile;
    }

    public void setLmsProfile(String lmsProfile) {
        _lmsProfile = lmsProfile;
    }

    public String getOTP() {
        return _otp;
    }

    public void setOTP(String _otp) {
        this._otp = _otp;
    }

    public Date getOtpModifiedOn() {
        return _otpModifiedOn;
    }

    public void setOtpModifiedOn(Date modifiedOn) {
        _otpModifiedOn = modifiedOn;
    }

    public String getAutoc2callowed() {
        return _autoc2callowed;
    }

    public void setAutoc2callowed(String autoc2callowed) {
        _autoc2callowed = autoc2callowed;
    }

    public String getAutoc2cquantity() {
        return _autoc2cquantity;
    }

    public void setAutoc2cquantity(String autoc2cquantity) {
        _autoc2cquantity = autoc2cquantity;
    }

    /**
     * @return Returns the channelUserID.
     */
    public String getChannelUserID() {
        return _channelUserID;
    }

    /**
     * @param channelUserID
     *            The channelUserID to set.
     */
    public void setChannelUserID(String channelUserID) {
        _channelUserID = channelUserID;
    }

    /**
     * @return Returns the maxTxnAmount.
     */
    public long getMaxTxnAmount() {
        return _maxTxnAmount;
    }

    /**
     * @param maxTxnAmount
     *            The maxTxnAmount to set.
     */
    public void setMaxTxnAmount(long maxTxnAmount) {
        _maxTxnAmount = maxTxnAmount;
    }

    /**
     * Gets LMS Profile Id
     * 
     * @return
     */
    public String getLmsProfileId() {
        return _lmsProfileId;
    }

    /**
     * Sets LMS Profile Id
     * 
     * @param profileId
     */
    public void setLmsProfileId(String profileId) {
        _lmsProfileId = profileId;
    }

    public String getParentGeographyCode() {
        return _parentGeographyCode;
    }

    public void setParentGeographyCode(String geographyCode) {
        _parentGeographyCode = geographyCode;
    }

    public void setDecryptionKey(String decryptionKey) {
        _decryptionKey = decryptionKey;
    }

    public String getDecryptionKey() {
        return _decryptionKey;
    }

    public void setImei(String imei) {
        _imei = imei;
    }

    public String getImei() {

        return _imei;
    }

    public ArrayList getAsscMsisdnList() {
        return _asscMsisdnList;
    }

    public void setAsscMsisdnList(ArrayList msisdnList) {
        _asscMsisdnList = msisdnList;
    }

    public Date getAsscMsisdnDate() {
        return _asscMsisdnDate;
    }

    public void setAsscMsisdnDate(Date msisdnDate) {
        _asscMsisdnDate = msisdnDate;
    }

    public void setOptInOutStatus(String optInOutStatus) {
        _optInOutStatus = optInOutStatus;
    }

    public String getOptInOutStatus() {

        return _optInOutStatus;
    }

    public String getControlGroup() {
        return _controlGroup;
    }

    public void setControlGroup(String _controlGroup) {
        this._controlGroup = _controlGroup;
    }

    public Object getResetPinOTPMessage() {
        return _resetPinOTPMessage;
    }

    public void setResetPinOTPMessage(Object message) {
        _resetPinOTPMessage = message;
    }

    public String getSecurityAnswer() {
        return _securityAnswer;
    }

    public void setSecurityAnswer(String answer) {
        _securityAnswer = answer;
    }

    public int getOtpInvalidCount() {
        return _otpInvalidCount;
    }

    public void setOtpInvalidCount(int invalidCount) {
        _otpInvalidCount = invalidCount;
    }
    
	
    public String getSosAllowed() {
		return sosAllowed;
	}
	public void setSosAllowed(String sosAllowed) {
		this.sosAllowed = sosAllowed;
	}
	public long getSosAllowedAmount() {
		return sosAllowedAmount;
	}
	public void setSosAllowedAmount(long sosAllowedAmount) {
		this.sosAllowedAmount = sosAllowedAmount;
	}
	public long getSosThresholdLimit() {
		return sosThresholdLimit;
	}
	public void setSosThresholdLimit(long sosThresholdLimit) {
		this.sosThresholdLimit = sosThresholdLimit;
	}
	//added by Ashutosh to clear the frequently used instance variables of the current instance
    public void clearInstance() {
    	this._userGrade = null;
        this._userGradeName = null;
        this._transferProfileID = null;
        this._transferProfileName = null;
        this._transferProfileStatus = null;
        this._commissionProfileSetID = null;
        this._commissionProfileSetName = null;
        this._commissionProfileSetVersion = null;
        this._commissionProfileStatus = null;
        this._inSuspend = null;
        this._outSuspened = null;
        this._geographicalCode = null;
        this._geographicalDesc = null;
        this._outletCode = null;
        this._subOutletCode = null;
        this._activatedOn = null;
        this._transferRuleID = null; 
        this._userlevel = null;
        this._userIDPrefix = null;
        this._categoryName = null;
        this._smsPin = null;
        this._pinRequired = null;
        this._invalidPinCount = 0;
        this._commissionProfileLang1Msg = null;
        this._commissionProfileLang2Msg = null;
        this._commissionProfileSuspendMsg = null;
        this._transferCategory = null;
        this._commissionProfileApplicableFrom = null;
        this._languageCode = null;
        this._languageName = null;
        this._userControlGrouptypeCounters=null;
        this._userChargeGrouptypeCounters = null;
        this._recordNumber = null;
        this._groupRoleCode = null;
        this._parentLoginID = null;
        this._serviceTypes = null;
        this._voucherTypes=null;
        this.segments=null;
        this._parentStatus = null;
        this._groupRoleFlag = null; 
        this._applicationID = null;
        this._mpayProfileID = null;
        this._userProfileID = null;
        this._accessType = null;
        this._mcommerceServiceAllow = null;
        this._lowBalAlertAllow = null;
        this._primaryMsisdn = null;
        this._userBalance = null;
        this._balance=0;
        this._balanceStr = null;
        this._previousBalance = 0;
        this._prevBalanceStr = null;
        this._trnsfrdUserHierhyList = null;
        this._prevUserName = null;
        this._prevParentName = null;
        this._prevUserId = null;
        this._prevCategoryCode = null;
        this._msisdnPrefix = null;
        this._networkCode = null;
        this._phoneProfile = null;
        this._primaryMsisdnPin = null;
        this._multipleMsisdnlist = null;
        this._isFirstExternalUserModify = false;
        this._smsMSisdn = null;
        this._prefixId = 0;
        this._catLowBalanceAlertAllow = null;
        this._catOutletAllowed = null;
        this._parentLocale = null;
        this.maxUserLevel = 0;
        this._productCode = null;
        this._alertMsisdn = null;
        this._alertType = null;
        this._alertEmail = null;
        this.serviceTypeList = null;
        this._trannferRuleTypeId = null;
        this._cellID = null;
        this._switchID = null;
        this._lmsProfile = null;
        this._otp = null;
        this._otpModifiedOn = null;
        this._autoc2callowed = null;
        this._autoc2cquantity = null;
        this._channelUserID = null;
        this._maxTxnAmount = 0;
        this._lmsProfileId = null; 
        this._parentGeographyCode = null;
        this._decryptionKey = null;
        this._imei = null;
        this._asscMsisdnList = null;
        this._asscMsisdnDate = null;
        this._optInOutStatus = null;
        this._controlGroup = null;
        this._resetPinOTPMessage = null;
        this._securityAnswer = null;
        this._monthlyTransAmt = 0;
        this._gateway = null;
        this._otpInvalidCount = 0; 
        this.loanProfileId=null;
    }

	public String getOthCommSetId() {
		return _othCommSetId;
	}

	public void setOthCommSetId(String othCommSetId) {
		_othCommSetId = othCommSetId;
	}

	/**
	 * @return the _commissionProfileApplicableFromAsString
	 */
	public String get_commissionProfileApplicableFromAsString() {
		return _commissionProfileApplicableFromAsString;
	}

	/**
	 * @param _commissionProfileApplicableFromAsString the _commissionProfileApplicableFromAsString to set
	 */
	public void set_commissionProfileApplicableFromAsString(
			String _commissionProfileApplicableFromAsString) {
		this._commissionProfileApplicableFromAsString = _commissionProfileApplicableFromAsString;
	}

	public String getSegments() {
		return segments;
	}

	public void setSegments(String segments) {
		this.segments = segments;
	}

	public void setCategoryList(ArrayList<ListValueVO> catgeoryList) {
		this._catgeoryList = catgeoryList;
		
	}

	public Object getCategoryList() {
		return _catgeoryList;
	}
	
	public static ChannelUserVO getInstance(){
		return new ChannelUserVO();
	}
	public long getAutoO2CThresholdLimit() {
		return autoO2CThresholdLimit;
	}
	public void setAutoO2CThresholdLimit(long autoO2CThresholdLimit) {
		this.autoO2CThresholdLimit = autoO2CThresholdLimit;
	}
	public long getAutoO2CTxnValue() {
		return autoO2CTxnValue;
	}
	public void setAutoO2CTxnValue(long autoO2CTxnValue) {
		this.autoO2CTxnValue = autoO2CTxnValue;
	}
	public String getAutoo2callowed() {
		return _autoo2callowed;
	}
	public void setAutoo2callowed(String _autoo2callowed) {
		this._autoo2callowed = _autoo2callowed;
	}
	
	
}
