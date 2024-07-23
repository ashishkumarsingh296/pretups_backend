package com.selftopup.pretups.user.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.selftopup.user.businesslogic.UserVO;
import com.selftopup.util.BTSLUtil;

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
    private String _userGrade;// used for Channel Person
    private String _userGradeName;// used for Channel Person
    private String _transferProfileID;// used for Channel Person
    private String _transferProfileName;// used for Channel Person
    private String _transferProfileStatus;// used for Channel Person
    private String _commissionProfileSetID;// used for Channel Person
    private String _commissionProfileSetName;// used for Channel Person
    private String _commissionProfileSetVersion;// used for Channel Person
    private String _commissionProfileStatus;
    private String _contactPerson;
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
    private Date _appointmentDate = null;
    private Date _passwordCountUpdatedOn = null;
    // added by sandeep goel to show information in the ICCID MSISDN KEY
    // MANAGEMENT Module
    private String _categoryName;

    private String _smsPin;
    private String _pinRequired;
    private int _invalidPinCount;
    private String _commissionProfileLang1Msg = null;
    private String _commissionProfileLang2Msg = null;
    private String _commissionProfileSuspendMsg = null;
    private String _transferCategory = null;
    private Date _commissionProfileApplicableFrom = null;
    private String _languageCode = null;
    private String _languageName = null;

    // Added by amit for bulk user module.
    private String _recordNumber;
    private String _groupRoleCode;
    private String _parentLoginID;
    private String _serviceTypes;
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
    // private String _msisdn=null;
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

    // for push message multiple mobile no
    private String _primaryMsisdnPin;
    // for store multiple mobile no in a comma seprated manner
    private String _multipleMsisdnlist;
    // for erp user firsttime reg
    private boolean _isFirstExternalUserModify = false;
    private String _smsMSisdn = null;
    private long _prefixId = 0;
    // This field is added(by Rajdeep) to get the low balance alert status for
    // the user's category
    private String _catLowBalanceAlertAllow;
    private String _catOutletAllowed;
    private Locale _parentLocale = null;
    private int maxUserLevel = 0;
    // added by nilesh
    private String _productCode = null;
    // This field is added by Vikas Jauhari for Alert MSISDN
    private String _alertMsisdn;
    // for RSA Authentication
    private boolean _rsaRequired = false;
    // Added by Harpreet for low balance Alert_Type
    private String _alertType;

    // Added by Harpreet for low balance Alert_Email
    private String _alertEmail;

    // Added by nilesh:User profile Updation on the basis of langitude and
    // latitude
    private String _longitude;
    private String _latitude;

    // added by gaurav pandey for channel user logs
    private String[] serviceTypeList;

    private String _trannferRuleTypeId;
    private String _cellID;
    private String _switchID;
    // Added For LMS by Vibhu- 3/1/2014
    private String _lmsProfile;
    // Added by Aatif
    private String _lmsProfileId; // used for Channel Person

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
        ChannelUserVO obj = (ChannelUserVO) arg0;
        try {
            if (this.getCategoryVO().getSequenceNumber() > obj.getCategoryVO().getSequenceNumber())
                return 1;
            return -1;
        } catch (Exception e) {
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

    public boolean isRsaRequired() {
        return _rsaRequired;
    }

    /**
     * @param _rsarequired
     *            the _rsarequired to set
     */
    public void setRsaRequired(boolean _rsarequired) {
        _rsaRequired = _rsarequired;
    }

    /**
     * @return Returns the recordNumber.
     */
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

    public String getContactPerson() {
        return _contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        _contactPerson = contactPerson;
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

    public Date getAppointmentDate() {
        return _appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        _appointmentDate = appointmentDate;
    }

    public Date getPasswordCountUpdatedOn() {
        return _passwordCountUpdatedOn;
    }

    public void setPasswordCountUpdatedOn(Date passwordCountUpdatedOn) {
        _passwordCountUpdatedOn = passwordCountUpdatedOn;
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
        StringBuffer sbf = new StringBuffer();
        sbf.append(super.toString());
        sbf.append("User Grade =" + _userGrade);
        sbf.append(",TransferProfileID =" + _transferProfileID);
        sbf.append(",CommissionProfileSetID =" + _commissionProfileSetID);
        sbf.append(",ContactPerson=" + _contactPerson);
        sbf.append(",InSuspend =" + _inSuspend);
        sbf.append(",OutSuspened=" + _outSuspened);
        sbf.append(",GeographicalCode =" + _geographicalCode);
        sbf.append(",OutletCode =" + _outletCode);
        sbf.append(",SubOutletCode=" + _subOutletCode);
        sbf.append(",ActivatedOn=" + _activatedOn);
        sbf.append(",UserBalance=" + _userBalance);

        // for Zebra and Tango by sanjeew date 06/07/07
        sbf.append(",Application ID=" + _applicationID);
        sbf.append(",MpayProfileID=" + _mpayProfileID);
        sbf.append(",UserProfileID=" + _userProfileID);
        sbf.append(",AccessType=" + _accessType);
        sbf.append(",M-Commerce Service Allow=" + _mcommerceServiceAllow);
        sbf.append(",Low Balance Alert Allow=" + _lowBalAlertAllow);
        // End Zebra and Tango
        sbf.append(",PrimaryMsisdnPin=" + _primaryMsisdnPin);
        // Added by Vikas Jauhari for Alert Msisdn
        sbf.append(",Alert Msisdn=" + _alertMsisdn);
        sbf.append(",_lmsProfile=" + _lmsProfile);
        sbf.append(",_lmsProfileId=" + _lmsProfileId);
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
        String userCatname = null;
        if (BTSLUtil.isNullString(_prevUserName) || BTSLUtil.isNullString(_prevCategoryCode))
            userCatname = "";
        else
            userCatname = _prevUserName + " (" + _prevCategoryCode + ")";
        return userCatname;
    }

    public String getPrevUserParentNameWithCategory() {
        String userParentCat = null;
        if (BTSLUtil.isNullString(_prevUserName) || BTSLUtil.isNullString(_prevCategoryCode) || BTSLUtil.isNullString(_prevParentName))
            userParentCat = "";
        else
            userParentCat = _prevUserName + " (" + _prevCategoryCode + ")" + "(" + _prevParentName + ")";
        return userParentCat;
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
    public String getLongitude() {
        return _longitude;
    }

    /**
     * @param alertType
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
     * @param alertType
     *            The latitude to set.
     */
    public void setLatitude(String latitude) {
        _latitude = latitude;
    }

    /**
     * @return Returns the transactionProfile.
     */
    public String getTrannferRuleTypeId() {
        if (_trannferRuleTypeId != null)
            return _trannferRuleTypeId.trim();

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
}
