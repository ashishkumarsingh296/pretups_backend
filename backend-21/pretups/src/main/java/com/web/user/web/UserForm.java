package com.web.user.web;

/**
 * @(#)UserForm.java
 *                   Copyright(c) 2005, Bharti Telesoft Ltd.
 *                   All Rights Reserved
 * 
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Mohit Goel 22/06/2005 Initial Creation
 * 
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
/*
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorForm;*/

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.DateUtils;
import com.ibm.icu.util.Calendar;
import java.util.List;
import com.btsl.common.ListValueVO;
import com.btsl.user.businesslogic.UserLoanVO;

public class UserForm /*extends ValidatorForm*/ {
	
    
	private static final long serialVersionUID = 1L;
	public static final Log logger = LogFactory.getLog(UserForm.class.getName());
    private CategoryVO _categoryVO;
    private ChannelUserVO _channelUserVO;
    private String _requestType;

    // for searchUser.jsp //for viewLevelOneApprovalUser.jsp
    private ArrayList _userList;

    // for selectCategory.jsp/selectChannelCategory.jsp
    private ArrayList _categoryList;
    private String _categoryCode;
    private String _categoryCodeDesc;
    private String _userName;// also used on the jsp's selectCategoryForEdit,
                             // searchUser, addOperatorUser

    // selectChannelCategory.jsp
    private ArrayList _selectDomainList;
    private String _domainCode;
    private String _domainCodeDesc;
    private String _channelCategoryCode;
    private String _channelCategoryCodeDummy;// user to set the category drop
                                             // down value, in case of back
                                             // button press
    private String _channelCategoryDesc;
    private ArrayList _origCategoryList;
    private ArrayList _origParentCategoryList;
    private ArrayList _parentCategoryList;
    private String _parentCategoryCode;
    private String _parentCategoryDesc;
    private String _parentDomainCode;// this hold the domain code of the logged
                                     // in user
    private String _parentDomainDesc;
    private String _parentDomainTypeDesc;
    private String _channelUserName;
    private ArrayList _associatedGeographicalList;

    // searchParentUser.jsp
    private ArrayList _searchList;
    private String[] _searchTextArray;
    private int _searchIndex;
    private String[] _searchUserId;
    private String[] _distributorSearchFlag;

    // addChannelUser.jsp
    private String _userType;// used for Channel Person
    private ArrayList _userGradeList;
    private String _userGradeId;// used for Channel Person
    private String _userGradeName;
    private String _userGradeIdDesc;// used for Channel Person
    private ArrayList _trannferProfileList;
    private String _trannferProfileId;// used for Channel Person
    private String _trannferProfileIdDesc;// used for Channel Person
    private ArrayList _commissionProfileList;
    private String _commissionProfileSetId;// used for Channel Person
    private String _commissionProfileSetIdDesc;// used for Channel Person
    private String _insuspend;// used for Channel Person
    private String _outsuspend;// used for Channel Person
    private String _userCode;// used for Channel Person
    /*
     * whether User Code field will show on the jsp or not is depandent on the
     * System Preferences values
     * USER_CODE_REQUIRED
     * if it true user can enter the user code value from the screen
     * if it is false pass the primary msisdn number as user code value and hide
     * the user code
     * filed on jsp.
     * 
     * This flag is used to know the status of the userCode.
     */
    private boolean _userCodeFlag;// used for Channel Person
    private String _level1ApprovedBy;
    private Date _level1ApprovedOn;
    private String _level2ApprovedBy;
    private Date _level2ApprovedOn;
    private String _outletCode = null;
    private String _outletCodeDesc = null;
    private String _subOutletCode = null;
    private String _subOutletCodeDesc = null;
    private ArrayList _outletList;
    private ArrayList _subOutletList;

    // for addOperatorUser.jsp
    private String _webLoginID;
    private String _oldWebLoginID;
    private String _showPassword;
    private String _webPassword;
    private String _confirmPassword;
    private String _parentID;
    private String _ownerID;
    private String _allowedIPs;
    private String[] _allowedDays;
    private String[] _paymentTypes;
    private String _allowedFormTime;
    private String _allowedToTime;
    private String _empCode;
    private String _status;
    private String _statusDesc;
    private String _previousStatus;
    private ArrayList _statusList;
    private String _email;
    private Date _passwordModifiedOn;
    private String _contactPerson;
    private String _contactNo;
    private String _designation;
    private String _divisionCode;
    private String _divisionDesc;
    private ArrayList _divisionList;
    private String _departmentCode;
    private String _departmentDesc;
    private ArrayList _departmentList;
    private String _msisdn;
    private String _address1;
    private String _address2;
    private String _city;
    private String _state;
    private String _country;
    private String _ssn;
    private String _userNamePrefixCode = null;
    private String _userNamePrefixDesc = null;
    private ArrayList _userNamePrefixList;
    private String _externalCode;
    private String _shortName;
    private String _appointmentDate;
    // for assignPhone.jsp
    private ArrayList _msisdnList;// for sms information
    private ArrayList _oldMsisdnList;// for sms information
    private String _primaryRadio;// for primary number
    private ArrayList _phoneProfileList;
    private String _roleType;// whether the role is System Role or Group Role

    // for assignGeography.jsp
    private ArrayList _geographicalList;
    private String _geographicalCode;
    private String[] _geographicalCodeArray;
    private ArrayList _domainSearchList;
    private String[] _searchDomainTextArray;
    private String[] _searchDomainCode;
    private String _searchDomainName;
    private ArrayList _graphDomainList;
    private String _domainId;
    private String _grphDomainTypeName;
    private String _userGeographyCode;
    // networklist is used in case of assigning network to super users: super network admin and super cce
	private ArrayList _networkList;
    

    // for User roles assignRoles.jsp/changeRoleChannelUser.jsp
    private HashMap _rolesMap;// used on jsp "assignRoles.jsp" to show all roles
                              // info
    private HashMap _rolesMapSelected;// used on jsp "addOperator.jsp" to show
                                      // the only assign roles info
    private String[] _roleFlag;// store the role codes that are assigned to the
                               // user
    private String[] _roleFlagOld;// store the role codes (while deleting user
                                  // roles during change role check wehther
                                  // roles are assigned to the user initiallay
                                  // or not)

    // for user domains assignDomain.jsp
    private ArrayList _domainList;// store the complete list of the domains
    private String[] _domainCodes;// store the domain_codes asociated with the
                                  // user

    // for user services assignServices.jsp
    private ArrayList _servicesList;// store the complete list of the Services
    private String[] _servicesTypes;// store the service_type asociated with the
                                    // user

    // for user products assignProducts.jsp
    private ArrayList _productsList;// store the complete list of the Products
    private String[] _productCodes;// store the productCodes asociated with the
                                   // user

    // for selectUserForEdit.jsp, viewLevelOneApprovalUser.jsp
    // used while editing the user info
    private String userId;
    private long lastModified;
    // added for validating popup screens
    private long _time;

    private String _oldPassword = null;
    private String _newPassword = null;
    private String _confirmNewPassword = null;

    // for selectChannelCategoryForView.jsp
    private String _searchMsisdn;
    private String _networkCode;
    private String _networkName;
    private String _searchLoginId = null;
    private String _searchCriteria;// M = Mobile Search; L = LoginId Search; D =
                                   // Doamin,Category Search

    // for channelUserView.jsp
    private String _parentName = null;
    private String _parentMsisdn = null;
    private String _parentCategoryName = null;
    private String _ownerName = null;
    private String _ownerMsisdn = null;
    private String _ownerCategoryName = null;

    // for viewDSApprovalUsers.jsp
    private String[] _approvalArray;

    // for resumeChannelUsersList.jsp
    private String[] _resumeArray;

    // used for viewUserBalances added by ankit zindal
    private ArrayList _userBalanceList;
    private ArrayList _agentBalanceList;
    private String _viewType;
    private boolean _domainShowFlag;

    // used for view user counters( viewCounterDetails.jsp)
    private UserTransferCountsVO _userTransferCountsVO;
    private TransferProfileVO _transferProfileVO;
    private boolean _subscriberOutCountFlag;
    private boolean _unctrlTransferFlag;
    private String _creationType;
    private String _creationTypeDesc;
    private String _batchID;

    // for Zebra and Tango by sanjeew date 06/07/07
    private ArrayList _mpayProfileList = null;
    private String _mpayProfileID = "";
    private String _mpayProfileIDWithGrad = null;
    private String _mpayProfileDesc = null;
    private String _mcommerceServiceAllow = "N";
    private String _lowBalAlertAllow = "N";
    // End Zebra and Tango

    // add for encrypte the password/pin over the network
    private String _oldOtherInfo;// store old password/pin
    private String _newOtherInfo;// store new password/pin
    private String _otherInfo;// store conform password/pin

    // staff user
    private String _loginUserID;
    private String _loginUserDomainCode;
    private String _loginUserCategoryCode;
    // added by vikas kumar for Activation Bonus
    private ArrayList _activationProfileList;
    private String _setID;
    private String _tempSetID;
    private String _setName;
    private String _profileType;
    private String _deAssociate;

    // Add for auto generating password
    private String _pwdGenerateAllow = "N";
    private boolean _isStaff = false;
    private String _smsPin = null;
    private String _confirmPin = null;
    private String description = null;
    private boolean _deleteMsisdn = false;
    private boolean _msisdnInfoModified = false;
    // Add for default profile display for user other profile
    private String _categoryDefaultProfile;
    // Selecting category for channel user approval will be configurable by this
    // entry
    private String _isCategoryCodeNeeded;
    private boolean _isCceCategory = true;

    // added by rahuls ===============
    private String createdBy;
    private String createdOn;
    // Added By Babu Kunwar on 16/02/2011
    private String _eventRemarks;
    // Added By Babu Kunwar for showing user registration date
    private String _registrationDate = null;
    private String _msisdnTextArea;
    // added by nilesh : User profile Updation based on longitude and latitude
    private String _longitude = null;
    private String _latitude = null;

    // Added by Amit Raheja for setting alerts at the time of creation
    private String _otherEmail;
    private String _lowBalAlertToParent = "N";
    private String _lowBalAlertToOther = "N";
    private String _lowBalAlertToSelf = "N";
    // Added by Deepika Aggarwal
    private String _company;
    private String _fax;
    private String _firstName;
    private String _lastName;
    private ArrayList _userLanguageList;
    private String _userLanguage = null;
    private String _userLanguageDesc = null;
    // Added For CP User Registration
    private boolean _isSerAssignChnlAdm = false;

    // added for user default configuration
   // private FormFile _file;
    private String _domainName;
    private String _errorFlag;
    private ArrayList _errorList = null;
    private int _totalRecords;
    private String _noOfRecords;
    private int _domainListTSize;
    private HashMap _masterMap;
    // for RSA Authentication
    private String _rsaAuthentication;
    private boolean _rsaRequired = false;
    // Added for Staff user approval
    private ChannelUserVO _staffParentVO;
    private String _loginUserDomainID;

    // Add for transfer rule at user level
    private String _trannferRuleTypeId;// used for Channel Person
    private String _trannferRuleTypeIdDesc;// used for Channel Person
    private ArrayList _trannferRuleTypeList;
    // Added for user creation through SAP
    private String _primaryNumber = null;
    private String _phoneProfileDesc;

    // for Authentication Type
    private boolean _otpRequired = false;
    private String _authTypeAllowed;
    private String _pinGenerateAllow = "N";
    private String _showSmsPin = null;
    private String _confirmSmsPin = null;

    // Add for LMS Profile - Added by Aatif
    private String _lmsProfileId;
    private String _lmsProfileListIdDesc;
    private ArrayList _lmsProfileList;

    private String bttnAssociate;
    private String bttnDeAssociate;
    private String backBttn;
    private String msisdnForAssDeAss;
    private String channeluserType;
    private ArrayList channelUserTypeList;
    private String _downloadType = null;

    private String _assType;
    private String _assoMsisdn;
    private Date _associationCreatedOn;
    private Date _associationModifiedOn;
    private String _controlGroup = "N";
    
    private String _mainBal;
    private String _bonusBal;
    private String _totalBal;
    private String _documentType = null;
    private ArrayList _documentTypeList;
    private String _documentTypeDesc = null;
    private String _documentNo = null;   
    private String _paymentType = null;
    private ArrayList _paymentTypeList;
    private String _paymentTypeDesc = null;
    
    private String allowedUserTypeCreation=null;//allowed user type creation through Network admin
    
    private ArrayList _voucherList;// // store the complete list of the voucher type
    private String[] _voucherTypes;// // store the voucher type associated with the user
    private ArrayList segmentList;
    private String[] segments;
	
	
	private List<ListValueVO> loanProfileList = null;
    private String loanProfileId = null;
    private String loanProfileIdDesc = null;
	
	
    public String getTotalBal() {
        return _totalBal;
    }
    public void setTotalBal(String bal) {
        this._totalBal = bal;
    }
    public String getMainBal() {
        return _mainBal;
    }

    public void setMainBal(String bal) {
        this._mainBal = bal;
    }
    public String getBonBal() {
        return _bonusBal;
    }

    public void setBonBal(String bal) {
        this._bonusBal = bal;
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

    public boolean isOtpRequired() {
        return _otpRequired;
    }

    public void setOtpRequired(boolean required) {
        _otpRequired = required;
    }

    /**
     * @return Returns the creationTypeDesc.
     */
    public String getCreationTypeDesc() {
        return this._creationTypeDesc;
    }

    /**
     * @param creationTypeDesc
     *            The creationTypeDesc to set.
     */
    public void setCreationTypeDesc(String creationTypeDesc) {
        this._creationTypeDesc = creationTypeDesc;
    }

    /**
     * @return Returns the batchID.
     */
    public String getBatchID() {
        return this._batchID;
    }

    /**
     * @param batchID
     *            The batchID to set.
     */
    public void setBatchID(String batchID) {
        this._batchID = batchID;
    }

    /**
     * @return Returns the creationType.
     */
    public String getCreationType() {
        return this._creationType;
    }

    /**
     * @param creationType
     *            The creationType to set.
     */
    public void setCreationType(String creationType) {
        this._creationType = creationType;
    }

    public void setMsisdnListIndexed(int i, UserPhoneVO vo) {
        _msisdnList.set(i, vo);
    }

    public UserPhoneVO getMsisdnListIndexed(int i) {
        if (_msisdnList == null) {
            return (new UserPhoneVO());
        }
        return (UserPhoneVO) _msisdnList.get(i);
    }

    /**
     * @return Returns the oldWebLoginID.
     */
    public String getOldWebLoginID() {
        return _oldWebLoginID;
    }

    /**
     * @param oldWebLoginID
     *            The oldWebLoginID to set.
     */
    public void setOldWebLoginID(String oldWebLoginID) {
        _oldWebLoginID = oldWebLoginID;
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

    /**
     * @return Returns the oldMsisdnList.
     */
    public ArrayList getOldMsisdnList() {
        return _oldMsisdnList;
    }

    /**
     * @param oldMsisdnList
     *            The oldMsisdnList to set.
     */
    public void setOldMsisdnList(ArrayList oldMsisdnList) {
        _oldMsisdnList = oldMsisdnList;
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

    /*
     * Flush all the contents of the form bean
     */
    public void flush() {
    	
    	_voucherList=null;
    	_voucherTypes=null;
    	
        _categoryVO = null;
        _servicesList = null;
        _requestType = null;
        // used while editing the user info
        userId = null;
        lastModified = 0;

        // for selectCategory.jsp
        _categoryList = null;
        _userName = null;
        _categoryCode = null;
        _categoryCodeDesc = null;

        // for addOperatorUser.jsp
        _webLoginID = null;
        _showPassword = null;
        _webPassword = null;
        _confirmPassword = null;
        _parentID = null;
        _ownerID = null;
        _allowedIPs = null;
        _allowedDays = null;
        _paymentTypes=null;
        _allowedFormTime = null;
        _allowedToTime = null;
        _empCode = null;
        _status = null;
        _statusDesc = null;
        _previousStatus = null;
        _statusList = null;
        _email = null;
        _passwordModifiedOn = null;
        _contactPerson = null;
        _contactNo = null;
        _designation = null;
        _divisionCode = null;
        _divisionDesc = null;
        _divisionList = null;
        _departmentCode = null;
        _departmentDesc = null;
        _departmentList = null;
        _msisdn = null;

        _address1 = null;
        _address2 = null;
        _city = null;
        _state = null;
        _country = null;
        _ssn = null;
        _userNamePrefixCode = null;
        _userNamePrefixDesc = null;
        _userNamePrefixList = null;
        _externalCode = null;
        _shortName = null;
        _appointmentDate = null;

        // for assignPhone.jsp
        _msisdnList = null;
        _primaryRadio = null;
        _phoneProfileList = null;
        _roleType = null;
        allowedUserTypeCreation = null;
        
        // for assignGeography.jsp
        _geographicalList = null;
        _geographicalCode = null;
        _geographicalCodeArray = null;
        _domainSearchList = null;
        _searchDomainTextArray = null;
        _searchDomainCode = null;
        _searchDomainName = null;
        _graphDomainList = null;
        _domainId = null;
        _grphDomainTypeName = null;
        _networkList=null;

        // for assighRoles.jsp/changeRoleChannelUser.jsp
        _rolesMap = null;
        _rolesMapSelected = null;
        _roleFlag = null;
        _roleFlagOld = null;

        // for assignDomain.jsp
        _domainList = null;
        _domainCodes = null;

        // for assignServices.jsp
        _servicesList = null;
        _servicesTypes = null;

        // for assignProducts.jsp
        _productsList = null;
        _productCodes = null;

        // for selectChannelCategory.jsp
        _selectDomainList = null;
        _domainCode = null;
        _domainCodeDesc = null;
        _channelCategoryCode = null;
        _channelCategoryCodeDummy = null;
        _channelCategoryDesc = null;
        _origCategoryList = null;
        _origParentCategoryList = null;
        _parentCategoryList = null;
        _parentCategoryCode = null;
        _parentCategoryDesc = null;
        _parentDomainCode = null;
        _parentDomainDesc = null;
        _parentDomainTypeDesc = null;
        _channelUserName = null;
        _associatedGeographicalList = null;

        // searchParentUser.jsp
        _searchList = null;
        _searchTextArray = null;
        _searchIndex = 0;
        _searchUserId = null;
        _distributorSearchFlag = null;

        // addChannelUser.jsp
        _userType = null;
        _userGradeId = null;
        _userGradeIdDesc = null;
        _trannferProfileId = null;
        _trannferProfileIdDesc = null;
        _commissionProfileSetId = null;
        _commissionProfileSetIdDesc = null;
        _insuspend = null;
        _outsuspend = null;
        _userCode = null;
        _userCodeFlag = false;
        _level1ApprovedBy = null;
        _level1ApprovedOn = null;
        _level2ApprovedBy = null;
        _level2ApprovedOn = null;
        _outletCode = null;
        _outletCodeDesc = null;
        _subOutletCode = null;
        _subOutletCodeDesc = null;
        _outletList = null;
        _subOutletList = null;

        // for selectChannelCategoryForView.jsp
        _searchMsisdn = null;
        _networkCode = null;
        _networkName = null;
        _searchLoginId = null;
        _searchCriteria = null;

        // for channelUserView.jsp
        _parentName = null;
        _parentMsisdn = null;
        _parentCategoryName = null;
        _ownerName = null;
        _ownerMsisdn = null;
        _ownerCategoryName = null;

        // for viewDSApprovalUsers.jsp
        _approvalArray = null;

        // for resumeChannelUsersList.jsp
        _resumeArray = null;

        _userBalanceList = null;
        _agentBalanceList = null;
        _viewType = null;

        // for view user counters
        _userTransferCountsVO = null;
        _transferProfileVO = null;
        _oldMsisdnList = null;
        _oldPassword = null;
        _newPassword = null;
        _confirmNewPassword = null;

        // for Zebra and Tango by sanjeew date 06/07/07
        _mpayProfileList = null;
        _mpayProfileID = "";
        _mpayProfileIDWithGrad = null;
        _mpayProfileDesc = null;
        _mcommerceServiceAllow = "N";
        _lowBalAlertAllow = "N";
        // end Zebra and Tango
        // for password encrypted
        _oldOtherInfo = null;
        _newOtherInfo = null;
        _otherInfo = null;
        _setID = null;
        _setName = null;
        // pin password auto gen.
        _pwdGenerateAllow = "N";
        _pinGenerateAllow = "D";
        _isStaff = false;
        // for default profile display
        _categoryDefaultProfile = null;
        _msisdn = null;
        _smsPin = null;
        _confirmPin = null;
        _showSmsPin = null;
        _confirmSmsPin = null;
        description = null;
        _deleteMsisdn = false;
        _eventRemarks = null;
        _otherEmail = null;
        _lowBalAlertToParent = "N";
        _lowBalAlertToOther = "N";
        _lowBalAlertToSelf = "N";
        // Added by Deepika Aggarwal
        _company = null;
        _fax = null;
        _firstName = null;
        _lastName = null;
        _userLanguageList = null;
        _userLanguage = null;
        _isSerAssignChnlAdm = false;
        _rsaAuthentication = null;
        _authTypeAllowed = null;
        _trannferRuleTypeId = null;
        _trannferRuleTypeIdDesc = null;
        _lmsProfileListIdDesc = null;
        _documentTypeList = null;
        _documentType = null;
        _documentTypeDesc = null;
        _documentNo = null;
        _paymentTypeList = null;
        _paymentType = null;
        _paymentTypeDesc = null;
    }

    /*
     * Flush the contents of the form bean that are concerned with the detail
     * screen
     */
    public void semiFlush() {
        // for addOperatorUser.jsp
    	
    	_voucherList=null;
    	_voucherTypes=null;
    	
        _channelUserName = null;
        _servicesList = null;
        _webLoginID = null;
        _showPassword = null;
        _webPassword = null;
        _confirmPassword = null;
        _allowedIPs = null;
        _allowedDays = null;
        _paymentTypes=null;
        _allowedFormTime = null;
        _allowedToTime = null;
        _empCode = null;
        _status = null;
        _statusDesc = null;
        _previousStatus = null;
        _statusList = null;
        _email = null;
        _passwordModifiedOn = null;
        _contactPerson = null;
        _contactNo = null;
        _designation = null;
        _divisionCode = null;
        _divisionDesc = null;
        _divisionList = null;
        _departmentCode = null;
        _departmentDesc = null;
        _departmentList = null;
        _msisdn = null;
        _address1 = null;
        _address2 = null;
        _city = null;
        _state = null;
        _country = null;
        _ssn = null;
        _userNamePrefixCode = null;
        _userNamePrefixDesc = null;
        _userNamePrefixList = null;
        _externalCode = null;
        _shortName = null;
        _appointmentDate = null;

        // for assignPhone.jsp
        _msisdnList = null;
        _primaryRadio = null;
        _phoneProfileList = null;
        _roleType = null;

        // for assignGeography.jsp
        _geographicalList = null;
        _geographicalCode = null;
        _geographicalCodeArray = null;
        _domainSearchList = null;
        _searchDomainTextArray = null;
        _searchDomainCode = null;
        _searchDomainName = null;
        _graphDomainList = null;
        _domainId = null;
        _grphDomainTypeName = null;

        // for assighRoles.jsp/changeRoleChannelUser.jsp
        _rolesMap = null;
        _rolesMapSelected = null;
        _roleFlag = null;
        _roleFlagOld = null;

        // for assignDomain.jsp
        _domainList = null;
        _domainCodes = null;

        // for assignServices.jsp
        _servicesList = null;
        _servicesTypes = null;

        // for assignProducts.jsp
        _productsList = null;
        _productCodes = null;

        // addChannelUser.jsp
        _userType = null;
        _userGradeId = null;
        _userGradeIdDesc = null;
        _trannferProfileId = null;
        _trannferProfileIdDesc = null;
        _commissionProfileSetId = null;
        _commissionProfileSetIdDesc = null;
        _insuspend = null;
        _outsuspend = null;
        _userCode = null;
        _userCodeFlag = false;
        _level1ApprovedBy = null;
        _level1ApprovedOn = null;
        _level2ApprovedBy = null;
        _level2ApprovedOn = null;
        _outletCode = null;
        _outletCodeDesc = null;
        _subOutletCode = null;
        _subOutletCodeDesc = null;
        _outletList = null;
        _subOutletList = null;
        _setID = null;
        _setName = null;
        _pwdGenerateAllow = "N";
        _pinGenerateAllow = "D";
        _msisdn = null;
        _smsPin = null;
        _confirmPin = null;
        _showSmsPin = null;
        _confirmSmsPin = null;
        description = null;
        _deleteMsisdn = false;
        _isSerAssignChnlAdm = false;
        _rsaAuthentication = null;
        _authTypeAllowed = null;
        allowedUserTypeCreation = null;
    }

    /**
     * @return Returns the categoryList.
     */
    public ArrayList getCategoryList() {
        return _categoryList;
    }

    /**
     * @param categoryList
     *            The categoryList to set.
     */
    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    /**
     * @return Returns the empCode.
     */
    public String getEmpCode() {
        if (_empCode != null) {
            return _empCode.trim();
        }

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
     * @return Returns the userName.
     */
    public String getUserName() {
        if (_userName != null) {
            return _userName.trim();
        }

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
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        if (_categoryCode != null) {
            return _categoryCode.trim();
        }
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    /**
     * @return Returns the categoryCodeDesc.
     */
    public String getCategoryCodeDesc() {
        if (_categoryCodeDesc != null) {
            return _categoryCodeDesc.trim();
        }

        return _categoryCodeDesc;
    }

    /**
     * @param categoryCodeDesc
     *            The categoryCodeDesc to set.
     */
    public void setCategoryCodeDesc(String categoryCodeDesc) {
        _categoryCodeDesc = categoryCodeDesc;
    }

    /**
     * @return Returns the allowedFormTime.
     */
    public String getAllowedFormTime() {
        if (_allowedFormTime != null) {
            return _allowedFormTime.trim();
        }

        return _allowedFormTime;
    }

    /**
     * @param allowedFormTime
     *            The allowedFormTime to set.
     */
    public void setAllowedFormTime(String allowedFormTime) {
        _allowedFormTime = allowedFormTime;
    }

    /**
     * @return Returns the allowedIPs.
     */
    public String getAllowedIPs() {
        if (_allowedIPs != null) {
            return _allowedIPs.trim();
        }

        return _allowedIPs;
    }

    /**
     * @param allowedIPs
     *            The allowedIPs to set.
     */
    public void setAllowedIPs(String allowedIPs) {
        _allowedIPs = allowedIPs;
    }

    /**
     * @return Returns the allowedToTime.
     */
    public String getAllowedToTime() {
        if (_allowedToTime != null) {
            return _allowedToTime.trim();
        }

        return _allowedToTime;
    }

    /**
     * @param allowedToTime
     *            The allowedToTime to set.
     */
    public void setAllowedToTime(String allowedToTime) {
        _allowedToTime = allowedToTime;
    }

    /**
     * @return Returns the confirmPassword.
     */
    public String getConfirmPassword() {
        if (_confirmPassword != null) {
            return _confirmPassword.trim();
        }

        return _confirmPassword;
    }

    /**
     * @param confirmPassword
     *            The confirmPassword to set.
     */
    public void setConfirmPassword(String confirmPassword) {
        _confirmPassword = confirmPassword;
    }

    /**
     * @return Returns the webLoginID.
     */
    public String getWebLoginID() {
        if (_webLoginID != null) {
            return _webLoginID.trim();
        }

        return _webLoginID;
    }

    /**
     * @param webLoginID
     *            The webLoginID to set.
     */
    public void setWebLoginID(String webLoginID) {
        _webLoginID = webLoginID;
    }

    /**
     * @return Returns the webPassword.
     */
    public String getWebPassword() {
        if (_webPassword != null) {
            return _webPassword.trim();
        }

        return _webPassword;
    }

    /**
     * @param webPassword
     *            The webPassword to set.
     */
    public void setWebPassword(String webPassword) {
        _webPassword = webPassword;
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
     * @return Returns the primaryRadio.
     */
    public String getPrimaryRadio() {
        if (_primaryRadio != null) {
            return _primaryRadio.trim();
        }

        return _primaryRadio;
    }

    /**
     * @param primaryRadio
     *            The primaryRadio to set.
     */
    public void setPrimaryRadio(String primaryRadio) {
        this._primaryRadio = primaryRadio;
    }

    /**
     * @return Returns the phoneProfileList.
     */
    public ArrayList getPhoneProfileList() {
        return _phoneProfileList;
    }

    /**
     * @param phoneProfileList
     *            The phoneProfileList to set.
     */
    public void setPhoneProfileList(ArrayList phoneProfileList) {
        _phoneProfileList = phoneProfileList;
    }

    /**
     * @return Returns the roleType.
     */
    public String getRoleType() {
        return _roleType;
    }

    /**
     * @param roleType
     *            The roleType to set.
     */
    public void setRoleType(String roleType) {
        _roleType = roleType;
    }

    /**
     * @return Returns the geographicalCode.
     */
    public String getGeographicalCode() {
        if (_geographicalCode != null) {
            return _geographicalCode.trim();
        }

        return _geographicalCode;
    }

    /**
     * @param geographicalCode
     *            The geographicalCode to set.
     */
    public void setGeographicalCode(String geographicalCode) {
        _geographicalCode = geographicalCode;
    }

    /**
     * @return Returns the geographicalCodeArray.
     */
    public String[] getGeographicalCodeArray() {
        return _geographicalCodeArray;
    }

    public int getGeographicalCodeArrayCount() {
        if (_geographicalCodeArray != null && _geographicalCodeArray.length > 0) {
            return _geographicalCodeArray.length;
        } else {
            return 0;
        }
    }

    /**
     * @param geographicalCodeArray
     *            The geographicalCodeArray to set.
     */
    public void setGeographicalCodeArray(String[] geographicalCodeArray) {
        _geographicalCodeArray = geographicalCodeArray;
    }

    /**
     * @return Returns the geographicalList.
     */
    public ArrayList getGeographicalList() {
        return _geographicalList;
    }

    /**
     * @param geographicalList
     *            The geographicalList to set.
     */
    public void setGeographicalList(ArrayList geographicalList) {
        _geographicalList = geographicalList;
    }

    /**
     * @return Returns the allowedDays.
     */
    public String[] getAllowedDays() {
        return _allowedDays;
    }

    /**
     * @param allowedDays
     *            The allowedDays to set.
     */
    public void setAllowedDays(String[] allowedDays) {
        this._allowedDays = allowedDays;
    }

    public String[] getPaymentTypes() {
        return _paymentTypes;
    }
    
    
    public void setPaymentTypes(String[] paymentTypes) {
        this._paymentTypes = paymentTypes;
    }
    /**
     * @return Returns the rolesMap.
     */
    public HashMap getRolesMap() {
        return _rolesMap;
    }

    /**
     * @param rolesMap
     *            The rolesMap to set.
     */
    public void setRolesMap(HashMap rolesMap) {
        _rolesMap = rolesMap;
    }

    public int getRolesMapCount() {
        if (_rolesMap != null && _rolesMap.size() > 0) {
            return _rolesMap.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the roleFlag.
     */
    public String[] getRoleFlag() {
        return _roleFlag;
    }

    /**
     * @param roleFlag
     *            The roleFlag to set.
     */
    public void setRoleFlag(String[] roleFlag) {
        _roleFlag = roleFlag;
    }

    /**
     * @return Returns the roleFlagOld.
     */
    public String[] getRoleFlagOld() {
        return _roleFlagOld;
    }

    /**
     * @param roleFlagOld
     *            The roleFlagOld to set.
     */
    public void setRoleFlagOld(String[] roleFlagOld) {
        _roleFlagOld = roleFlagOld;
    }

    /**
     * @return Returns the rolesMapSelected.
     */
    public HashMap getRolesMapSelected() {
        return _rolesMapSelected;
    }

    /**
     * @param rolesMapSelected
     *            The rolesMapSelected to set.
     */
    public void setRolesMapSelected(HashMap rolesMapSelected) {
        _rolesMapSelected = rolesMapSelected;
    }

    public int getRolesMapSelectedCount() {
        if (_rolesMapSelected != null && _rolesMapSelected.size() > 0) {
            return _rolesMapSelected.size();
        } else {
            return 0;
        }
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

    public int getDomainCodesCount() {
        if (_domainCodes != null && _domainCodes.length > 0) {
            return _domainCodes.length;
        } else {
            return 0;
        }
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
     * @return Returns the servicesList.
     */
    public ArrayList getServicesList() {
        return _servicesList;
    }

    /**
     * @param servicesList
     *            The servicesList to set.
     */
    public void setServicesList(ArrayList servicesList) {
        _servicesList = servicesList;
    }

    /**
     * @return Returns the servicesTypes.
     */
    public String[] getServicesTypes() {
        return _servicesTypes;
    }

    /**
     * @param servicesTypes
     *            The servicesTypes to set.
     */
    public void setServicesTypes(String[] servicesTypes) {
        _servicesTypes = servicesTypes;
    }

    public int getServicesTypesCount() {
        if (_servicesTypes != null && _servicesTypes.length > 0) {
            return _servicesTypes.length;
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the productCodes.
     */
    public String[] getProductCodes() {
        return _productCodes;
    }

    /**
     * @param productCodes
     *            The productCodes to set.
     */
    public void setProductCodes(String[] productCodes) {
        _productCodes = productCodes;
    }

    public int getProductCodesCount() {
        if (_productCodes != null && _productCodes.length > 0) {
            return _productCodes.length;
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the productsList.
     */
    public ArrayList getProductsList() {
        return _productsList;
    }

    /**
     * @param productsList
     *            The productsList to set.
     */
    public void setProductsList(ArrayList productsList) {
        _productsList = productsList;
    }

    /**
     * @return Returns the lastModified.
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified
     *            The lastModified to set.
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        if (userId != null) {
            return userId.trim();
        }

        return userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return Returns the userList.
     */
    public ArrayList getUserList() {
        return _userList;
    }

    /**
     * @param userList
     *            The userList to set.
     */
    public void setUserList(ArrayList userList) {
        _userList = userList;
    }

    public int getResultCount() {
        if (_userList != null && _userList.size() > 0) {
            return _userList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the showPassword.
     */
    public String getShowPassword() {
        if (_showPassword != null) {
            return _showPassword.trim();
        }

        return _showPassword;
    }

    /**
     * @param showPassword
     *            The showPassword to set.
     */
    public void setShowPassword(String showPassword) {
        _showPassword = showPassword;
    }

    /**
     * @return Returns the passwordModifiedOn.
     */
    public Date getPasswordModifiedOn() {
        return _passwordModifiedOn;
    }

    /**
     * @param passwordModifiedOn
     *            The passwordModifiedOn to set.
     */
    public void setPasswordModifiedOn(Date passwordModifiedOn) {
        _passwordModifiedOn = passwordModifiedOn;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        if (_email != null) {
            return _email.trim();
        }

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
        if (_status != null) {
            return _status.trim();
        }

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
        _statusList = statusList;
    }

    /**
     * @return Returns the contactNo.
     */
    public String getContactNo() {
        if (_contactNo != null) {
            return _contactNo.trim();
        }

        return _contactNo;
    }

    /**
     * @param contactNo
     *            The contactNo to set.
     */
    public void setContactNo(String contactNo) {
        _contactNo = contactNo;
    }

    public String getConfirmNewPassword() {
        if (_confirmNewPassword != null) {
            return _confirmNewPassword.trim();
        }

        return _confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        _confirmNewPassword = confirmNewPassword;
    }

    public String getNewPassword() {
        if (_newPassword != null) {
            return _newPassword.trim();
        }

        return _newPassword;
    }

    public void setNewPassword(String newPassword) {
        _newPassword = newPassword;
    }

    public String getOldPassword() {
        if (_oldPassword != null) {
            return _oldPassword.trim();
        }

        return _oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        _oldPassword = oldPassword;
    }

    /**
     * @return Returns the department.
     */
    public String getDepartmentCode() {
        if (_departmentCode != null) {
            return _departmentCode.trim();
        }

        return _departmentCode;
    }

    /**
     * @param department
     *            The department to set.
     */
    public void setDepartmentCode(String department) {
        _departmentCode = department;
    }

    /**
     * @return Returns the designation.
     */
    public String getDesignation() {
        if (_designation != null) {
            return _designation.trim();
        }

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
     * @return Returns the division.
     */
    public String getDivisionCode() {
        if (_divisionCode != null) {
            return _divisionCode.trim();
        }

        return _divisionCode;
    }

    /**
     * @param division
     *            The division to set.
     */
    public void setDivisionCode(String division) {
        _divisionCode = division;
    }

    /**
     * @return Returns the address1.
     */
    public String getAddress1() {
        if (_address1 != null) {
            return _address1.trim();
        }

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
        if (_address2 != null) {
            return _address2.trim();
        }

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
        if (_city != null) {
            return _city.trim();
        }

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
     * @return Returns the commissionProfile.
     */
    public String getCommissionProfileSetId() {
        if (_commissionProfileSetId != null) {
            return _commissionProfileSetId.split(":")[0];
        }

        return _commissionProfileSetId;
    }

    /**
     * @param commissionProfile
     *            The commissionProfile to set.
     */
    public void setCommissionProfileSetId(String commissionProfile) {
        _commissionProfileSetId = commissionProfile;
    }

    /**
     * @return Returns the contactPerson.
     */
    public String getContactPerson() {
        if (_contactPerson != null) {
            return _contactPerson.trim();
        }

        return _contactPerson;
    }

    /**
     * @param contactPerson
     *            The contactPerson to set.
     */
    public void setContactPerson(String contactPerson) {
        _contactPerson = contactPerson;
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        if (_country != null) {
            return _country.trim();
        }

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
        if (_externalCode != null) {
            return _externalCode.trim();
        }

        return _externalCode;
    }

    /**
     * @param externalCode
     *            The externalCode to set.
     */
    public void setExternalCode(String externalCode) {
        _externalCode = externalCode;
    }

    /**
     * @return Returns the inSuspend.
     */
    public String getInsuspend() {
        return _insuspend;
    }

    /**
     * @param inSuspend
     *            The inSuspend to set.
     */
    public void setInsuspend(String inSuspend) {
        _insuspend = inSuspend;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        if (_msisdn != null) {
            return _msisdn.trim();
        }

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
     * @return Returns the outSuspened.
     */
    public String getOutsuspend() {
        return _outsuspend;
    }

    /**
     * @param outSuspened
     *            The outSuspened to set.
     */
    public void setOutsuspend(String outSuspened) {
        _outsuspend = outSuspened;
    }

    /**
     * @return Returns the ownerID.
     */
    public String getOwnerID() {
        if (_ownerID != null) {
            return _ownerID.trim();
        }

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
        if (_parentID != null) {
            return _parentID.trim();
        }

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
     * @return Returns the ssn.
     */
    public String getSsn() {
        if (_ssn != null) {
            return _ssn.trim();
        }

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
        if (_state != null) {
            return _state.trim();
        }

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
     * @return Returns the transactionProfile.
     */
    public String getTrannferProfileId() {
        if (_trannferProfileId != null) {
            return _trannferProfileId.trim();
        }

        return _trannferProfileId;
    }

    /**
     * @param transactionProfile
     *            The transactionProfile to set.
     */
    public void setTrannferProfileId(String transactionProfile) {
        _trannferProfileId = transactionProfile;
    }

    /**
     * @return Returns the userCode.
     */
    public String getUserCode() {
        if (_userCode != null) {
            return _userCode.trim();
        }

        return _userCode;
    }

    /**
     * @param userCode
     *            The userCode to set.
     */
    public void setUserCode(String userCode) {
        _userCode = userCode;
    }

    /**
     * @return Returns the userCodeFlag.
     */
    public boolean isUserCodeFlag() {
        return _userCodeFlag;
    }

    /**
     * @param userCodeFlag
     *            The userCodeFlag to set.
     */
    public void setUserCodeFlag(boolean userCodeFlag) {
        _userCodeFlag = userCodeFlag;
    }

    /**
     * @return Returns the userGrade.
     */
    public String getUserGradeId() {
        if (_userGradeId != null) {
            return _userGradeId.trim();
        }

        return _userGradeId;
    }

    /**
     * @param userGrade
     *            The userGrade to set.
     */
    public void setUserGradeId(String userGrade) {
        _userGradeId = userGrade;
    }

    /**
     * @return Returns the userNamePrefixCode.
     */
    public String getUserNamePrefixCode() {
        return _userNamePrefixCode;
    }

    /**
     * @param userNamePrefixCode
     *            The userNamePrefixCode to set.
     */
    public void setUserNamePrefixCode(String userNamePrefixCode) {
        if (userNamePrefixCode != null) {
            _userNamePrefixCode = userNamePrefixCode.trim();
        }
    }

    /**
     * @return Returns the userNamePrefixDesc.
     */
    public String getUserNamePrefixDesc() {
        return _userNamePrefixDesc;
    }

    /**
     * @param userNamePrefixDesc
     *            The userNamePrefixDesc to set.
     */
    public void setUserNamePrefixDesc(String userNamePrefixDesc) {
        if (userNamePrefixDesc != null) {
            _userNamePrefixDesc = userNamePrefixDesc.trim();
        }
    }

    /**
     * @return Returns the userNamePrefixList.
     */
    public ArrayList getUserNamePrefixList() {
        return _userNamePrefixList;
    }

    /**
     * @param userNamePrefixList
     *            The userNamePrefixList to set.
     */
    public void setUserNamePrefixList(ArrayList userNamePrefixList) {
        _userNamePrefixList = userNamePrefixList;
    }

    /**
     * @return Returns the userType.
     */
    public String getUserType() {
        if (_userType != null) {
            return _userType.trim();
        }

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
     * @return Returns the shortName.
     */
    public String getShortName() {
        if (_shortName != null) {
            return _shortName.trim();
        }

        return _shortName;
    }

    /**
     * @param shortName
     *            The shortName to set.
     */
    public void setShortName(String shortName) {
        this._shortName = shortName;
    }

    /**
     * @return Returns the departmentList.
     */
    public ArrayList getDepartmentList() {
        return _departmentList;
    }

    /**
     * @param departmentList
     *            The departmentList to set.
     */
    public void setDepartmentList(ArrayList departmentList) {
        _departmentList = departmentList;
    }

    /**
     * @return Returns the divisionList.
     */
    public ArrayList getDivisionList() {
        return _divisionList;
    }

    /**
     * @param divisionList
     *            The divisionList to set.
     */
    public void setDivisionList(ArrayList divisionList) {
        _divisionList = divisionList;
    }

    /**
     * @return Returns the departmentDesc.
     */
    public String getDepartmentDesc() {
        if (_departmentDesc != null) {
            return _departmentDesc.trim();
        }

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
     * @return Returns the divisionDesc.
     */
    public String getDivisionDesc() {
        if (_divisionDesc != null) {
            return _divisionDesc.trim();
        }

        return _divisionDesc;
    }

    /**
     * @param divisionDesc
     *            The divisionDesc to set.
     */
    public void setDivisionDesc(String divisionDesc) {
        _divisionDesc = divisionDesc;
    }

    /**
     * @return Returns the statusDesc.
     */
    public String getStatusDesc() {
        if (_statusDesc != null) {
            return _statusDesc.trim();
        }

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
     * @return Returns the selectDomainList.
     */
    public ArrayList getSelectDomainList() {
        return _selectDomainList;
    }

    /**
     * @param selectDomainList
     *            The selectDomainList to set.
     */
    public void setSelectDomainList(ArrayList selectDomainList) {
        _selectDomainList = selectDomainList;
    }

    /**
     * @return Returns the domainCode.
     */
    public String getDomainCode() {
        if (_domainCode != null) {
            return _domainCode.trim();
        }

        return _domainCode;
    }

    /**
     * @param domainCode
     *            The domainCode to set.
     */
    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    /**
     * @return Returns the domainCodeDesc.
     */
    public String getDomainCodeDesc() {
        if (_domainCodeDesc != null) {
            return _domainCodeDesc.trim();
        }

        return _domainCodeDesc;
    }

    /**
     * @param domainCodeDesc
     *            The domainCodeDesc to set.
     */
    public void setDomainCodeDesc(String domainCodeDesc) {
        _domainCodeDesc = domainCodeDesc;
    }

    /**
     * @return Returns the origCategoryList.
     */
    public ArrayList getOrigCategoryList() {
        return _origCategoryList;
    }

    /**
     * @param origCategoryList
     *            The origCategoryList to set.
     */
    public void setOrigCategoryList(ArrayList origCategoryList) {
        this._origCategoryList = origCategoryList;
    }

    /**
     * @return Returns the parentCategoryCode.
     */
    public String getParentCategoryCode() {
        if (_parentCategoryCode != null) {
            return _parentCategoryCode.trim();
        }

        return _parentCategoryCode;
    }

    /**
     * @param parentCategoryCode
     *            The parentCategoryCode to set.
     */
    public void setParentCategoryCode(String parentCategoryCode) {
        _parentCategoryCode = parentCategoryCode;
    }

    /**
     * @return Returns the parentCategoryDesc.
     */
    public String getParentCategoryDesc() {
        if (_parentCategoryDesc != null) {
            return _parentCategoryDesc.trim();
        }

        return _parentCategoryDesc;
    }

    /**
     * @param parentCategoryDesc
     *            The parentCategoryDesc to set.
     */
    public void setParentCategoryDesc(String parentCategoryDesc) {
        _parentCategoryDesc = parentCategoryDesc;
    }

    /**
     * @return Returns the parentCategoryList.
     */
    public ArrayList getParentCategoryList() {
        return _parentCategoryList;
    }

    /**
     * @param parentCategoryList
     *            The parentCategoryList to set.
     */
    public void setParentCategoryList(ArrayList parentCategoryList) {
        _parentCategoryList = parentCategoryList;
    }

    /**
     * @return Returns the origParentCategoryList.
     */
    public ArrayList getOrigParentCategoryList() {
        return _origParentCategoryList;
    }

    /**
     * @param origParentCategoryList
     *            The origParentCategoryList to set.
     */
    public void setOrigParentCategoryList(ArrayList origParentCategoryList) {
        _origParentCategoryList = origParentCategoryList;
    }

    /**
     * @return Returns the parentDomainCode.
     */
    public String getParentDomainCode() {
        if (_parentDomainCode != null) {
            return _parentDomainCode.trim();
        }

        return _parentDomainCode;
    }

    /**
     * @param parentDomainCode
     *            The parentDomainCode to set.
     */
    public void setParentDomainCode(String parentDomainCode) {
        _parentDomainCode = parentDomainCode;
    }

    /**
     * @return Returns the parentDomainDesc.
     */
    public String getParentDomainDesc() {
        if (_parentDomainDesc != null) {
            return _parentDomainDesc.trim();
        }

        return _parentDomainDesc;
    }

    /**
     * @param parentDomainDesc
     *            The parentDomainDesc to set.
     */
    public void setParentDomainDesc(String parentDomainDesc) {
        _parentDomainDesc = parentDomainDesc;
    }

    /**
     * @return Returns the parentDomainTypeDesc.
     */
    public String getParentDomainTypeDesc() {
        if (_parentDomainTypeDesc != null) {
            return _parentDomainTypeDesc.trim();
        }

        return _parentDomainTypeDesc;
    }

    /**
     * @param parentDomainTypeDesc
     *            The parentDomainTypeDesc to set.
     */
    public void setParentDomainTypeDesc(String parentDomainTypeDesc) {
        _parentDomainTypeDesc = parentDomainTypeDesc;
    }

    public int getGeographicalListCount() {
        if (_geographicalList != null && _geographicalList.size() > 0) {
            return _geographicalList.size();
        } else {
            return 0;
        }
    }

    /*
     * return the Desccription of the geography on the basis of geographical
     * code
     */
    public String getGeographicalDesc(String domainCode) {
        String str = "";
        if (_geographicalList != null && _geographicalList.size() > 0) {
            for (int i = 0, j = _geographicalList.size(); i < j; i++) {
                UserGeographiesVO vo = (UserGeographiesVO) _geographicalList.get(i);
                if (domainCode.equals(vo.getGraphDomainCode())) {
                    str = vo.getGraphDomainName();
                    break;
                }
            }
        }
        return str;
    }

    public int getDomainListCount() {
        if (_domainList != null && _domainList.size() > 0) {
            return _domainList.size();
        } else {
            return 0;
        }
    }

    public int getProductsListCount() {
        if (_productsList != null && _productsList.size() > 0) {
            return _productsList.size();
        } else {
            return 0;
        }
    }

    public int getServicesListCount() {
        if (_servicesList != null && _servicesList.size() > 0) {
            return _servicesList.size();
        } else {
            return 0;
        }

    }

    /**
     * @return Returns the channelCategoryCode.
     */
    public String getChannelCategoryCode() {
        if (_channelCategoryCode != null) {
            return _channelCategoryCode.trim();
        }

        return _channelCategoryCode;
    }

    /**
     * @param channelCategoryCode
     *            The channelCategoryCode to set.
     */
    public void setChannelCategoryCode(String channelCategoryCode) {
        _channelCategoryCode = channelCategoryCode;
    }

    /**
     * @return Returns the channelCategoryCodeDummy.
     */
    public String getChannelCategoryCodeDummy() {
        return _channelCategoryCodeDummy;
    }

    /**
     * @param channelCategoryCodeDummy
     *            The channelCategoryCodeDummy to set.
     */
    public void setChannelCategoryCodeDummy(String channelCategoryCodeDummy) {
        _channelCategoryCodeDummy = channelCategoryCodeDummy;
    }

    /**
     * @return Returns the channelCategoryDesc.
     */
    public String getChannelCategoryDesc() {
        if (_channelCategoryDesc != null) {
            return _channelCategoryDesc.trim();
        }

        return _channelCategoryDesc;
    }

    /**
     * @param channelCategoryDesc
     *            The channelCategoryDesc to set.
     */
    public void setChannelCategoryDesc(String channelCategoryDesc) {
        _channelCategoryDesc = channelCategoryDesc;
    }

    /**
     * @return Returns the searchArray.
     */
    public String[] getSearchTextArray() {
        return _searchTextArray;
    }

    /**
     * @param searchArray
     *            The searchArray to set.
     */
    public void setSearchTextArray(String[] searchArray) {
        _searchTextArray = searchArray;
    }

    public void setSearchTextArraySize() {
        _searchTextArray = new String[_searchList.size()];
    }

    public String getSearchTextArrayIndexed(int i) {
        return _searchTextArray[i];
    }

    public void setSearchTextArrayIndexed(int i, String searchValue) {
        _searchTextArray[i] = searchValue;
    }

    /**
     * @return Returns the searchList.
     */
    public ArrayList getSearchList() {
        return _searchList;
    }

    public int getSearchListCount() {
        if (_searchList != null) {
            return _searchList.size();
        } else {
            return 0;
        }
    }

    /**
     * @param searchList
     *            The searchList to set.
     */
    public void setSearchList(ArrayList searchList) {
        _searchList = searchList;
    }

    /**
     * @return Returns the searchDoaminCodeArray.
     */
    public String[] getSearchUserId() {
        return _searchUserId;
    }

    /**
     * @param searchDoaminCodeArray
     *            The searchDoaminCodeArray to set.
     */
    public void setSearchUserId(String[] searchUserId) {
        _searchUserId = searchUserId;
    }

    public void setSearchUserIdSize() {
        _searchUserId = new String[_searchList.size()];
    }

    public String getSearchUserIdIndexed(int i) {
        return _searchUserId[i];
    }

    public void setSearchUserIdIndexed(int i, String searchUserIdValue) {
        _searchUserId[i] = searchUserIdValue;
    }

    /**
     * @return Returns the searchIndex.
     */
    public int getSearchIndex() {
        return _searchIndex;
    }

    /**
     * @param searchIndex
     *            The searchIndex to set.
     */
    public void setSearchIndex(int searchIndex) {
        _searchIndex = searchIndex;
    }

    /**
     * @return Returns the channelUserName.
     */
    public String getChannelUserName() {
        if (_channelUserName != null) {
            return _channelUserName.trim();
        }

        return _channelUserName;
    }

    /**
     * @param channelUserName
     *            The channelUserName to set.
     */
    public void setChannelUserName(String channelUserName) {
        _channelUserName = channelUserName;
    }

    /**
     * @return Returns the distributorSearchFlag.
     */
    public String[] getDistributorSearchFlag() {
        return _distributorSearchFlag;
    }

    /**
     * @param distributorSearchFlag
     *            The distributorSearchFlag to set.
     */
    public void setDistributorSearchFlag(String[] distributorSearchFlag) {
        _distributorSearchFlag = distributorSearchFlag;
    }

    public String getDistributorSearchFlagIndexed(int i) {
        return _distributorSearchFlag[i];
    }

    /**
     * @param distributorSearchFlag
     *            The distributorSearchFlag to set.
     */
    public void setDistributorSearchFlagIndexed(int i, String distributorSearchFlag) {
        _distributorSearchFlag[i] = distributorSearchFlag;
    }

    public void setDistributorSearchFlagSize() {
        _distributorSearchFlag = new String[_searchList.size()];
    }

    /**
     * @return Returns the commissionProfileSetIdDesc.
     */
    public String getCommissionProfileSetIdDesc() {
        if (_commissionProfileSetIdDesc != null) {
            return _commissionProfileSetIdDesc.trim();
        }

        return _commissionProfileSetIdDesc;
    }

    /**
     * @param commissionProfileSetIdDesc
     *            The commissionProfileSetIdDesc to set.
     */
    public void setCommissionProfileSetIdDesc(String commissionProfileSetIdDesc) {
        _commissionProfileSetIdDesc = commissionProfileSetIdDesc;
    }

    /**
     * @return Returns the trannferProfileIdDesc.
     */
    public String getTrannferProfileIdDesc() {
        if (_trannferProfileIdDesc != null) {
            return _trannferProfileIdDesc.trim();
        }

        return _trannferProfileIdDesc;
    }

    /**
     * @param trannferProfileIdDesc
     *            The trannferProfileIdDesc to set.
     */
    public void setTrannferProfileIdDesc(String trannferProfileIdDesc) {
        _trannferProfileIdDesc = trannferProfileIdDesc;
    }

    /**
     * @return Returns the userGradeIdDesc.
     */
    public String getUserGradeIdDesc() {
        if (_userGradeIdDesc != null) {
            return _userGradeIdDesc.trim();
        }

        return _userGradeIdDesc;
    }

    /**
     * @param userGradeIdDesc
     *            The userGradeIdDesc to set.
     */
    public void setUserGradeIdDesc(String userGradeIdDesc) {
        _userGradeIdDesc = userGradeIdDesc;
    }

    /**
     * @return Returns the commissionProfileList.
     */
    public ArrayList getCommissionProfileList() {
        return _commissionProfileList;
    }

    /**
     * @param commissionProfileList
     *            The commissionProfileList to set.
     */
    public void setCommissionProfileList(ArrayList commissionProfileList) {
        _commissionProfileList = commissionProfileList;
    }

    /**
     * @return Returns the trannferProfileList.
     */
    public ArrayList getTrannferProfileList() {
        return _trannferProfileList;
    }

    /**
     * @param trannferProfileList
     *            The trannferProfileList to set.
     */
    public void setTrannferProfileList(ArrayList trannferProfileList) {
        _trannferProfileList = trannferProfileList;
    }

    /**
     * @return Returns the userGradeList.
     */
    public ArrayList getUserGradeList() {
        return _userGradeList;
    }

    /**
     * @param userGradeList
     *            The userGradeList to set.
     */
    public void setUserGradeList(ArrayList userGradeList) {
        _userGradeList = userGradeList;
    }

    /**
     * @return Returns the associatedGeographicalList.
     */
    public ArrayList getAssociatedGeographicalList() {
        return _associatedGeographicalList;
    }

    /**
     * @param associatedGeographicalList
     *            The associatedGeographicalList to set.
     */
    public void setAssociatedGeographicalList(ArrayList associatedGeographicalList) {
        _associatedGeographicalList = associatedGeographicalList;
    }

    /**
     * @return Returns the domainSearchList.
     */
    public ArrayList getDomainSearchList() {
        return _domainSearchList;
    }

    /**
     * @param domainSearchList
     *            The domainSearchList to set.
     */
    public void setDomainSearchList(ArrayList domainSearchList) {
        _domainSearchList = domainSearchList;
    }

    /**
     * @return Returns the searchDomainTextArray.
     */
    public String[] getSearchDomainTextArray() {
        return _searchDomainTextArray;
    }

    /**
     * @param searchDomainTextArray
     *            The searchDomainTextArray to set.
     */
    public void setSearchDomainTextArray(String[] searchDomainTextArray) {
        _searchDomainTextArray = searchDomainTextArray;
    }

    /**
     * @return Returns the searchDomainTextArray.
     */
    public String getSearchDomainTextArrayIndexed(int i) {
        return _searchDomainTextArray[i];
    }

    /**
     * @param searchDomainTextArray
     *            The searchDomainTextArray to set.
     */
    public void setSearchDomainTextArrayIndexed(int i, String searchDomainTextArray) {
        _searchDomainTextArray[i] = searchDomainTextArray;
    }

    public void setSearchDomainTextArrayCount() {
        _searchDomainTextArray = new String[_domainSearchList.size()];
    }

    /**
     * @return Returns the searchDomainCode.
     */
    public String[] getSearchDomainCode() {
        return _searchDomainCode;
    }

    /**
     * @param searchDomainCode
     *            The searchDomainCode to set.
     */
    public void setSearchDomainCode(String[] searchDomainCode) {
        _searchDomainCode = searchDomainCode;
    }

    /**
     * @return Returns the searchDomainCode.
     */
    public String getSearchDomainCodeIndexed(int i) {
        return _searchDomainCode[i];
    }

    /**
     * @param searchDomainCode
     *            The searchDomainCode to set.
     */
    public void setSearchDomainCodeIndexed(int i, String searchDomainCode) {
        _searchDomainCode[i] = searchDomainCode;
    }

    /**
     * @return Returns the searchDomainCode.
     */
    public void setSearchDomainCodeCount() {
        _searchDomainCode = new String[_domainSearchList.size()];
    }

    /**
     * @return Returns the searchDomainName.
     */
    public String getSearchDomainName() {
        if (_searchDomainName != null) {
            return _searchDomainName.trim();
        }

        return _searchDomainName;
    }

    /**
     * @param searchDomainName
     *            The searchDomainName to set.
     */
    public void setSearchDomainName(String searchDomainName) {
        _searchDomainName = searchDomainName;
    }

    /**
     * @return Returns the graphDomainList.
     */
    public ArrayList getGraphDomainList() {
        return _graphDomainList;
    }

    /**
     * @param graphDomainList
     *            The graphDomainList to set.
     */
    public void setGraphDomainList(ArrayList graphDomainList) {
        _graphDomainList = graphDomainList;
    }

    public int getResultDomainCount() {
        if (_graphDomainList != null && _graphDomainList.size() > 0) {
            return _graphDomainList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the domainID.
     */
    public String getDomainId() {
        if (_domainId != null) {
            return _domainId.trim();
        }

        return _domainId;
    }

    /**
     * @param domainID
     *            The domainID to set.
     */
    public void setDomainId(String domainID) {
        _domainId = domainID;
    }

    /**
     * @return Returns the grphDomainTypeName.
     */
    public String getGrphDomainTypeName() {
        if (_grphDomainTypeName != null) {
            return _grphDomainTypeName.trim();
        }

        return _grphDomainTypeName;
    }

    /**
     * @param grphDomainTypeName
     *            The grphDomainTypeName to set.
     */
    public void setGrphDomainTypeName(String grphDomainTypeName) {
        _grphDomainTypeName = grphDomainTypeName;
    }

    /**
     * @return Returns the level1ApprovedBy.
     */
    public String getLevel1ApprovedBy() {
        if (_level1ApprovedBy != null) {
            return _level1ApprovedBy.trim();
        }

        return _level1ApprovedBy;
    }

    /**
     * @param level1ApprovedBy
     *            The level1ApprovedBy to set.
     */
    public void setLevel1ApprovedBy(String level1ApprovedBy) {
        _level1ApprovedBy = level1ApprovedBy;
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
        _level1ApprovedOn = level1ApprovedOn;
    }

    /**
     * @return Returns the level2ApprovedBy.
     */
    public String getLevel2ApprovedBy() {
        if (_level2ApprovedBy != null) {
            return _level2ApprovedBy.trim();
        }

        return _level2ApprovedBy;
    }

    /**
     * @param level2ApprovedBy
     *            The level2ApprovedBy to set.
     */
    public void setLevel2ApprovedBy(String level2ApprovedBy) {
        _level2ApprovedBy = level2ApprovedBy;
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
        _level2ApprovedOn = level2ApprovedOn;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the searchMsisdn.
     */
    public String getSearchMsisdn() {
        if (_searchMsisdn != null) {
            return _searchMsisdn.trim();
        }

        return _searchMsisdn;
    }

    /**
     * @param searchMsisdn
     *            The searchMsisdn to set.
     */
    public void setSearchMsisdn(String searchMsisdn) {
        _searchMsisdn = searchMsisdn;
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

    /**
     * @return Returns the searchLoginId.
     */
    public String getSearchLoginId() {
        return _searchLoginId;
    }

    /**
     * @param searchLoginId
     *            The searchLoginId to set.
     */
    public void setSearchLoginId(String searchLoginId) {
        if (searchLoginId != null) {
            _searchLoginId = searchLoginId.trim();
        }
    }

    /**
     * @return Returns the searchCriteria.
     */
    public String getSearchCriteria() {
        return _searchCriteria;
    }

    /**
     * @param searchCriteria
     *            The searchCriteria to set.
     */
    public void setSearchCriteria(String searchCriteria) {
        _searchCriteria = searchCriteria;
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
        if (outletCode != null) {
            _outletCode = outletCode.trim();
        }
    }

    /**
     * @return Returns the outletCodeDesc.
     */
    public String getOutletCodeDesc() {
        return _outletCodeDesc;
    }

    /**
     * @param outletCodeDesc
     *            The outletCodeDesc to set.
     */
    public void setOutletCodeDesc(String outletCodeDesc) {
        if (outletCodeDesc != null) {
            _outletCodeDesc = outletCodeDesc.trim();
        }
    }

    /**
     * @return Returns the outletList.
     */
    public ArrayList getOutletList() {
        return _outletList;
    }

    /**
     * @param outletList
     *            The outletList to set.
     */
    public void setOutletList(ArrayList outletList) {
        _outletList = outletList;
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
        if (subOutletCode != null) {
            _subOutletCode = subOutletCode.trim();
        }
    }

    /**
     * @return Returns the subOutletCodeDesc.
     */
    public String getSubOutletCodeDesc() {
        return _subOutletCodeDesc;
    }

    /**
     * @param subOutletCodeDesc
     *            The subOutletCodeDesc to set.
     */
    public void setSubOutletCodeDesc(String subOutletCodeDesc) {
        if (subOutletCodeDesc != null) {
            _subOutletCodeDesc = subOutletCodeDesc.trim();
        }
    }

    /**
     * @return Returns the subOutletList.
     */
    public ArrayList getSubOutletList() {
        return _subOutletList;
    }

    /**
     * @param subOutletList
     *            The subOutletList to set.
     */
    public void setSubOutletList(ArrayList subOutletList) {
        _subOutletList = subOutletList;
    }

    /**
     * @return Returns the appointmentDate.
     */

    public String getAppointmentDate() {
        return _appointmentDate;
    }

    /**
     * @param appointmentDate
     *            The appointmentDate to set.
     */
    public void setAppointmentDate(String appointmentDate) {
        _appointmentDate = appointmentDate;
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
     * @return Returns the approvalArray.
     */
    public String[] getApprovalArray() {
        return _approvalArray;
    }

    /**
     * @param approvalArray
     *            The approvalArray to set.
     */
    public void setApprovalArray(String[] approvalArray) {
        _approvalArray = approvalArray;
    }

    /**
     * @return Returns the approvalArray.
     */
    public String getApprovalArrayIndexed(int i) {
        return _approvalArray[i];
    }

    /**
     * @param approvalArray
     *            The approvalArray to set.
     */
    public void setApprovalArrayIndexed(int i, String approvalArray) {
        _approvalArray[i] = approvalArray;
    }

    /**
     * @return Returns the resumeArray.
     */
    public String[] getResumeArray() {
        return _resumeArray;
    }

    /**
     * @param resumeArray
     *            The resumeArray to set.
     */
    public void setResumeArray(String[] resumeArray) {
        _resumeArray = resumeArray;
    }

    public ArrayList getAgentBalanceList() {
        return _agentBalanceList;
    }

    public void setAgentBalanceList(ArrayList agentBalanceList) {
        _agentBalanceList = agentBalanceList;
    }

    public ArrayList getUserBalanceList() {
        return _userBalanceList;
    }

    public void setUserBalanceList(ArrayList userBalanceList) {
        _userBalanceList = userBalanceList;
    }

    public int getAgentBalanceListSize() {
        if (_agentBalanceList != null) {
            return _agentBalanceList.size();
        } else {
            return 0;
        }
    }

    public String getViewType() {
        return _viewType;
    }

    public void setViewType(String viewType) {
        _viewType = viewType;
    }

    public boolean isDomainShowFlag() {
        return _domainShowFlag;
    }

    public void setDomainShowFlag(boolean domainShowFlag) {
        _domainShowFlag = domainShowFlag;
    }

    /**
     * @return Returns the transferProfileVO.
     */
    public TransferProfileVO getTransferProfileVO() {
        return _transferProfileVO;
    }

    /**
     * @param transferProfileVO
     *            The transferProfileVO to set.
     */
    public void setTransferProfileVO(TransferProfileVO transferProfileVO) {
        _transferProfileVO = transferProfileVO;
    }

    /**
     * @return Returns the userTransferCountsVO.
     */
    public UserTransferCountsVO getUserTransferCountsVO() {
        return _userTransferCountsVO;
    }

    /**
     * @param userTransferCountsVO
     *            The userTransferCountsVO to set.
     */
    public void setUserTransferCountsVO(UserTransferCountsVO userTransferCountsVO) {
        _userTransferCountsVO = userTransferCountsVO;
    }

    /**
     * @return Returns the subscriberOutCountFlag.
     */
    public boolean isSubscriberOutCountFlag() {
        return _subscriberOutCountFlag;
    }

    /**
     * @param subscriberOutCountFlag
     *            The subscriberOutCountFlag to set.
     */
    public void setSubscriberOutCountFlag(boolean subscriberOutCountFlag) {
        _subscriberOutCountFlag = subscriberOutCountFlag;
    }

    /**
     * @return Returns the unctrlTransferFlag.
     */
    public boolean isUnctrlTransferFlag() {
        return _unctrlTransferFlag;
    }

    /**
     * @param unctrlTransferFlag
     *            The unctrlTransferFlag to set.
     */
    public void setUnctrlTransferFlag(boolean unctrlTransferFlag) {
        _unctrlTransferFlag = unctrlTransferFlag;
    }

    public long getTime() {
        return _time;
    }

    public void setTime(long time) {
        _time = time;
    }

    /**
     * @return Returns the mpayProfileDesc.
     */
    public String getMpayProfileDesc() {
        return _mpayProfileDesc;
    }

    /**
     * @param mpayProfileDesc
     *            The mpayProfileDesc to set.
     */
    public void setMpayProfileDesc(String mpayProfileDesc) {
        _mpayProfileDesc = mpayProfileDesc;
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
     * @return Returns the mpayProfileList.
     */
    public ArrayList getMpayProfileList() {
        return _mpayProfileList;
    }

    /**
     * @param mpayProfileList
     *            The mpayProfileList to set.
     */
    public void setMpayProfileList(ArrayList mpayProfileList) {
        _mpayProfileList = mpayProfileList;
    }

    /**
     * @return Returns the mpayProfileIDWithGrad.
     */
    public String getMpayProfileIDWithGrad() {
        return _mpayProfileIDWithGrad;
    }

    /**
     * @param mpayProfileIDWithGrad
     *            The mpayProfileIDWithGrad to set.
     */
    public void setMpayProfileIDWithGrad(String mpayProfileIDWithGrad) {
        _mpayProfileIDWithGrad = mpayProfileIDWithGrad;
    }

    /**
     * @return Returns the mcommerceServiceAllow.
     */
    public String getMcommerceServiceAllow() {
        return _mcommerceServiceAllow;
    }

    /**
     * @param mcommerceServiceAllow
     *            The mcommerceServiceAllow to set.
     */
    public void setMcommerceServiceAllow(String mcommerceServiceAllow) {
        _mcommerceServiceAllow = mcommerceServiceAllow;
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
     * @return Returns the _oldOtherInfo.
     */
    public String getOldOtherInfo() {
        return _oldOtherInfo;
    }

    /**
     * @param otherInfo
     *            The _oldOtherInfo to set.
     */
    public void setOldOtherInfo(String oldOtherInfo) {
        _oldOtherInfo = oldOtherInfo;
    }

    /**
     * @return Returns the _newOtherInfo.
     */
    public String getNewOtherInfo() {
        return _newOtherInfo;
    }

    /**
     * @param otherInfo
     *            The _newOtherInfo to set.
     */
    public void setNewOtherInfo(String newOtherInfo) {
        _newOtherInfo = newOtherInfo;
    }

    /**
     * @return Returns the _otherInfo.
     */
    public String getOtherInfo() {
        return _otherInfo;
    }

    /**
     * @param info
     *            The _otherInfo to set.
     */
    public void setOtherInfo(String otherInfo) {
        _otherInfo = otherInfo;
    }

    /**
     * @return Returns the loginUserCategoryCode.
     */
    public String getLoginUserCategoryCode() {
        return _loginUserCategoryCode;
    }

    /**
     * @param loginUserCategoryCode
     *            The loginUserCategoryCode to set.
     */
    public void setLoginUserCategoryCode(String loginUserCategoryCode) {
        _loginUserCategoryCode = loginUserCategoryCode;
    }

    /**
     * @return Returns the loginUserDomainCode.
     */
    public String getLoginUserDomainCode() {
        return _loginUserDomainCode;
    }

    /**
     * @param loginUserDomainCode
     *            The loginUserDomainCode to set.
     */
    public void setLoginUserDomainCode(String loginUserDomainCode) {
        _loginUserDomainCode = loginUserDomainCode;
    }

    /**
     * @return Returns the loginUserID.
     */
    public String getLoginUserID() {
        return _loginUserID;
    }

    /**
     * @param loginUserID
     *            The loginUserID to set.
     */
    public void setLoginUserID(String loginUserID) {
        _loginUserID = loginUserID;
    }

    /**
     * @return the _activationProfileList
     */
    public ArrayList getActivationProfileList() {
        return _activationProfileList;
    }

    /**
     * @param profileList
     *            the _activationProfileList to set
     */
    public void setActivationProfileList(ArrayList profileList) {
        _activationProfileList = profileList;
    }

    /**
     * @return the _setID
     */
    public String getSetID() {
        return _setID;
    }

    /**
     * @param _setid
     *            the _setID to set
     */
    public void setSetID(String _setid) {
        _setID = _setid;
    }

    /**
     * @return the _setName
     */
    public String getSetName() {
        return _setName;
    }

    /**
     * @param name
     *            the _setName to set
     */
    public void setSetName(String name) {
        _setName = name;
    }

    /**
     * @return the _profileType
     */
    public String getProfileType() {
        return _profileType;
    }

    /**
     * @param type
     *            the _profileType to set
     */
    public void setProfileType(String type) {
        _profileType = type;
    }

    /**
     * @return
     */
    public int getActivationProfileListSize() {
        if (_activationProfileList != null && _activationProfileList.size() > 0) {
            return _activationProfileList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the _deAssociate.
     */
    public String getDeAssociate() {
        return _deAssociate;
    }

    /**
     * @param associate
     *            The _deAssociate to set.
     */
    public void setDeAssociate(String associate) {
        _deAssociate = associate;
    }

    /**
     * @return Returns the _tempSetID.
     */
    public String getTempSetID() {
        return _tempSetID;
    }

    /**
     * @param setID
     *            The _tempSetID to set.
     */
    public void setTempSetID(String setID) {
        _tempSetID = setID;
    }

    /**
     * @return Returns the _pwdGenerateAllow.
     */
    public String getPwdGenerateAllow() {
        return _pwdGenerateAllow;
    }

    /**
     * @param generateAllow
     *            The _pwdGenerateAllow to set.
     */
    public void setPwdGenerateAllow(String generateAllow) {
        _pwdGenerateAllow = generateAllow;
    }

    /**
     * @return Returns the isStaff.
     */
    public boolean isStaff() {
        return _isStaff;
    }

    /**
     * @param isStaff
     *            The isStaff to set.
     */
    public void setStaff(boolean isStaff) {
        _isStaff = isStaff;
    }

    public String getCategoryDefaultProfile() {
        return _categoryDefaultProfile;
    }

    public void setCategoryDefaultProfile(String categoryDefaultProfile) {
        _categoryDefaultProfile = categoryDefaultProfile;
    }

    /**
     * @return Returns the confirmPin.
     */
    public String getConfirmPin() {
        return _confirmPin;
    }

    /**
     * @param confirmPin
     *            The confirmPin to set.
     */
    public void setConfirmPin(String confirmPin) {
        _confirmPin = confirmPin;
    }

    /**
     * @return Returns the smsPin.
     */
    public String getSmsPin() {
        return _smsPin;
    }

    /**
     * @param smsPin
     *            The smsPin to set.
     */
    public void setSmsPin(String smsPin) {
        _smsPin = smsPin;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the deleteMsisdn.
     */
    public boolean isDeleteMsisdn() {
        return _deleteMsisdn;
    }

    /**
     * @param deleteMsisdn
     *            The deleteMsisdn to set.
     */
    public void setDeleteMsisdn(boolean deleteMsisdn) {
        _deleteMsisdn = deleteMsisdn;
    }

    /**
     * @return Returns the msisdnInfoModified.
     */
    public boolean isMsisdnInfoModified() {
        return _msisdnInfoModified;
    }

    /**
     * @param msisdnInfoModified
     *            The msisdnInfoModified to set.
     */
    public void setMsisdnInfoModified(boolean msisdnInfoModified) {
        _msisdnInfoModified = msisdnInfoModified;
    }

    /**
     * @return Returns the _isCategoryCodeNeeded.
     */
    public String getIsCategoryCodeNeeded() {
        return _isCategoryCodeNeeded;
    }

    /**
     * @param categoryCodeNeeded
     *            The _isCategoryCodeNeeded to set.
     */
    public void setIsCategoryCodeNeeded(String categoryCodeNeeded) {
        _isCategoryCodeNeeded = categoryCodeNeeded;
    }

    public boolean getIsCceCategory() {
        return _isCceCategory;
    }

    public void setIsCceCategory(boolean cceCategory) {
        _isCceCategory = cceCategory;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * @return the eventRemarks
     */
    public String getEventRemarks() {
        return _eventRemarks;
    }

    /**
     * @param eventRemarks
     *            the eventRemarks to set
     */
    public void setEventRemarks(String eventRemarks) {
        _eventRemarks = eventRemarks;
    }

    public String getRegistrationDate() {
        return _registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        _registrationDate = registrationDate;
    }

    /**
     * @return Returns the AutoFOCAllowedmobileNoList.
     */
    public String getMsisdnTextArea() {
        return this._msisdnTextArea;
    }

    /**
     * @param mobileNoList
     *            The AutoFOCAllowedmobileNoList to set.
     */
    public void setMsisdnTextArea(String mobileNoList) {
        this._msisdnTextArea = mobileNoList;
    }

    /*
     * Added by nilesh: User profile updation based on longitude and latitude
     */
    public String getLongitude() {
        return _longitude;
    }

    public void setLongitude(String longitude) {
        _longitude = longitude;
    }

    public String getLatitude() {
        return _latitude;
    }

    public void setLatitude(String latitude) {
        _latitude = latitude;
    }

    public String getOtherEmail() {
        if (_otherEmail != null) {
            return _otherEmail.trim();
        }

        return _otherEmail;
    }

    public void setOtherEmail(String email) {
        _otherEmail = email;
    }

    public String getLowBalAlertToParent() {
        return _lowBalAlertToParent;
    }

    public void setLowBalAlertToParent(String balAlertToParent) {
        _lowBalAlertToParent = balAlertToParent;
    }

    public String getLowBalAlertToOther() {
        return _lowBalAlertToOther;
    }

    public void setLowBalAlertToOther(String balAlertToOther) {
        _lowBalAlertToOther = balAlertToOther;
    }

    public String getLowBalAlertToSelf() {
        return _lowBalAlertToSelf;
    }

    public void setLowBalAlertToSelf(String balAlertToSelf) {
        _lowBalAlertToSelf = balAlertToSelf;
    }

    // Added by Deepika Aggarwal
    public String getCompany() {
        return _company;
    }

    public void setCompany(String _company) {
        this._company = _company;
    }

    public String getFax() {
        return _fax;
    }

    public void setFax(String _fax) {
        this._fax = _fax;
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

    public ArrayList getUserLanguageList() {
        return _userLanguageList;
    }

    public void setUserLanguageList(ArrayList userLanguageList) {
        _userLanguageList = userLanguageList;
    }

    public String getUserLanguage() {
        return _userLanguage;
    }

    public void setUserLanguage(String language) {
        if (language != null) {
            _userLanguage = language.trim();
        }
    }

    public String getUserLanguageDesc() {
        return _userLanguageDesc;
    }

    public void setUserLanguageDesc(String userLanguageDesc) {
        if (userLanguageDesc != null) {
            _userLanguageDesc = userLanguageDesc.trim();
        }
    }

    // end added by deepika aggarwal

    // Added For CP User Registration
    public boolean getIsSerAssignChnlAdm() {
        return _isSerAssignChnlAdm;
    }

    public void setIsSerAssignChnlAdm(boolean serAssignChnlAdm) {
        _isSerAssignChnlAdm = serAssignChnlAdm;
    }

    /**
     * @return Returns the errorFlag.
     */
    public String getErrorFlag() {
        return _errorFlag;
    }

    /**
     * @param errorFlag
     *            The errorFlag to set.
     */
    public void setErrorFlag(String errorFlag) {
        _errorFlag = errorFlag;
    }

    /**
     * @return Returns the errorList.
     */
    public ArrayList getErrorList() {
        return _errorList;
    }

    /**
     * @param errorList
     *            The errorList to set.
     */
    public void setErrorList(ArrayList errorList) {
        _errorList = errorList;
    }

    /**
     * @return Returns the noOfRecords.
     */
    public String getNoOfRecords() {
        return _noOfRecords;
    }

    /**
     * @param noOfRecords
     *            The noOfRecords to set.
     */
    public void setNoOfRecords(String noOfRecords) {
        _noOfRecords = noOfRecords;
    }

    /**
     * @return Returns the totalRecords.
     */
    public int getTotalRecords() {
        return _totalRecords;
    }

    /**
     * @param totalRecords
     *            The totalRecords to set.
     */
    public void setTotalRecords(int totalRecords) {
        _totalRecords = totalRecords;
    }

    /**
     * To get the value of domainName field
     * 
     * @return domainName.
     */
    public String getDomainName() {
        return _domainName;
    }

    /**
     * To set the value of domainName field
     */
    public void setDomainName(String domainName) {
        if (domainName != null) {
            _domainName = domainName.trim();
        } else {
            _domainName = domainName;
        }
    }

    public HashMap getMasterMap() {
        return _masterMap;
    }

    public void setMasterMap(HashMap map) {
        _masterMap = map;
    }

    public int getDomainListTSize() {
        if (_domainList != null) {
            _domainListTSize = _domainList.size();
        } else {
            _domainListTSize = 0;
        }
        return _domainListTSize;
    }

    public void setDomainListTSize(int listTSize) {
        _domainListTSize = listTSize;
    }

   /* public FormFile getFile() {
        return _file;
    }

    public void setFile(FormFile _file) {
        this._file = _file;
    }
*/
    /**
     * @return Returns the rsaAuthentication.
     */
    public String getRsaAuthentication() {
        return _rsaAuthentication;
    }

    /**
     * @param rsaAuthentication
     *            The rsaAuthentication to set.
     */
    public void setRsaAuthentication(String rsaAuthentication) {
        _rsaAuthentication = rsaAuthentication;
    }

    /**
     * @return Returns the _rsaRequired.
     */
    public boolean isRsaRequired() {
        return _rsaRequired;
    }

    /**
     * @param rsaRequired
     *            The _rsaRequired to set.
     */
    public void setRsaRequired(boolean rsaRequired) {
        _rsaRequired = rsaRequired;
    }

    // Added for Staff user approval
    public ChannelUserVO getStaffParentVO() {
        return _staffParentVO;
    }

    public void setStaffParentVO(ChannelUserVO staffParentVO) {
        _staffParentVO = staffParentVO;
    }

    public String getLoginUserDomainID() {
        return _loginUserDomainID;
    }

    public void setLoginUserDomainID(String loginUserDomainID) {
        _loginUserDomainID = loginUserDomainID;
    }

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
     * @return Returns the trannferRuleTypeIdDesc.
     */
    public String getTrannferRuleTypeIdDesc() {
        if (_trannferRuleTypeIdDesc != null) {
            return _trannferRuleTypeIdDesc.trim();
        }

        return _trannferRuleTypeIdDesc;
    }

    /**
     * @param trannferRuleTypeIdDesc
     *            The trannferRuleTypeIdDesc to set.
     */
    public void setTrannferRuleTypeIdDesc(String trannferRuleTypeIdDesc) {
        _trannferRuleTypeIdDesc = trannferRuleTypeIdDesc;
    }

    /**
     * @return Returns the trannferRuleTypeList.
     */
    public ArrayList getTrannferRuleTypeList() {
        return _trannferRuleTypeList;
    }

    /**
     * @param trannferRuleTypeList
     *            The trannferRuleTypeList to set.
     */
    public void setTrannferRuleTypeList(ArrayList trannferRuleTypeList) {
        _trannferRuleTypeList = trannferRuleTypeList;
    }

    public String getAuthTypeAllowed() {
        return _authTypeAllowed;
    }

    public void setAuthTypeAllowed(String typeAllowed) {
        _authTypeAllowed = typeAllowed;
    }

    /**
     * @return Returns the primaryNumber.
     */
    public String getPrimaryNumber() {
        return _primaryNumber;
    }

    /**
     * @param primaryNumber
     *            The primaryNumber to set.
     */
    public void setPrimaryNumber(String primaryNumber) {
        if (primaryNumber != null) {
            _primaryNumber = primaryNumber.trim();
        }
    }

    /**
     * @return Returns the phoneProfileDesc.
     */
    public String getPhoneProfileDesc() {
        return _phoneProfileDesc;
    }

    /**
     * @param phoneProfileDesc
     *            The phoneProfileDesc to set.
     */
    public void setPhoneProfileDesc(String phoneProfileDesc) {
        _phoneProfileDesc = phoneProfileDesc;
    }

    public String getPinGenerateAllow() {
        return _pinGenerateAllow;
    }

    public void setPinGenerateAllow(String generateAllow) {
        _pinGenerateAllow = generateAllow;
    }

    public String getShowSmsPin() {
        return _showSmsPin;
    }

    public void setShowSmsPin(String smsPin) {
        _showSmsPin = smsPin;
    }

    public String getConfirmSmsPin() {
        return _confirmSmsPin;
    }

    public void setConfirmSmsPin(String smsPin) {
        _confirmSmsPin = smsPin;
    }

    /**
     * Returns the LMS Profile Id
     * 
     * @return
     */
    public String getLmsProfileId() {
        return _lmsProfileId;
    }

    /**
     * Sets the LMS ProfileId
     * 
     * @param profileId
     */
    public void setLmsProfileId(String profileId) {
        _lmsProfileId = profileId;
    }

    /**
     * Returns the LMS Profile list.
     * @return
     */
    public ArrayList getLmsProfileList() {
        return _lmsProfileList;
    }

    /**
     * Sets the LMS Profile list
     * 
     * @param profileList
     */
    public void setLmsProfileList(ArrayList profileList) {
        _lmsProfileList = profileList;
    }

    /**
     * Returns the LMS Profile list Ids
     * 
     * @return
     */
    public String getLmsProfileListIdDesc() {
        return _lmsProfileListIdDesc;
    }

    /**
     * Sets the LMS Profile list Ids
     * 
     * @param lmsProfileListIdDesc
     */
    public void setLmsProfileListIdDesc(String lmsProfileListIdDesc) {
        this._lmsProfileListIdDesc = lmsProfileListIdDesc;
    }

    public ChannelUserVO getChannelUserVO() {
        return _channelUserVO;
    }

    public void setChannelUserVO(ChannelUserVO channelUserVO) {
        _channelUserVO = channelUserVO;
    }

    /**
     * @return Returns the userGeographyCode.
     */
    public String getUserGeographyCode() {
        if (_userGeographyCode != null) {
            return _userGeographyCode.trim();
        }

        return _userGeographyCode;
    }

    /**
     * @param userGeographyCode
     *            sets userGeographyCode
     */
    public void setUserGeographyCode(String userGeographyCode) {
        _userGeographyCode = userGeographyCode;
    }

    public void setMsisdnForAssDeAss(String msisdnForAssDeAss) {
        this.msisdnForAssDeAss = msisdnForAssDeAss;
    }

    public String getBttnAssociate() {
        return bttnAssociate;
    }

    public void setBttnAssociate(String bttnAssociate) {
        this.bttnAssociate = bttnAssociate;
    }

    public String getBttnDeAssociate() {
        return bttnDeAssociate;
    }

    public void setBttnDeAssociate(String bttnDeAssociate) {
        this.bttnDeAssociate = bttnDeAssociate;
    }

    public String getBackBttn() {
        return backBttn;
    }

    public void setBackBttn(String backBttn) {
        this.backBttn = backBttn;
    }

    public String getChanneluserType() {
        return channeluserType;
    }

    public void setChanneluserType(String channeluserType) {
        this.channeluserType = channeluserType;
    }

    public String getMsisdnForAssDeAss() {
        return msisdnForAssDeAss;
    }

    public ArrayList getChannelUserTypeList() {
        return channelUserTypeList;
    }

    public void setChannelUserTypeList(ArrayList channelUserTypeList) {
        this.channelUserTypeList = channelUserTypeList;
    }

    public String getDownloadType() {
        return _downloadType;
    }

    /**
     * @param downloadType
     *            the downloadType to set
     */
    public void setDownloadType(String downloadType) {
        this._downloadType = downloadType;
    }

    public String getControlGroup() {
        return _controlGroup;
    }

    public void setControlGroup(String group) {
        _controlGroup = group;
    }
    
    /**
     * 
     * @param networkList
     * 			function to set the networklist for super network admin and super cce
     */
    public void setNetworkList(ArrayList networkList){
    	_networkList = networkList;
    }
    
    /**
     * 
     * @return networklist
     */
    public ArrayList getNetworkList(){
    	return _networkList;
    }
    public int getNetworkListCount() {
        if (_networkList != null && _networkList.size() > 0) {
            return _networkList.size();
        } else {
            return 0;
        }
    }
    
    /**
     * Method to display selected networks for super network admin and super cce
     * @param networkCode
     * @param ctr
     * @return
     */
    
    public String getNetworkDesc(String networkCode, int ctr) {
        String str = "";
        for(int i=0; i<_networkList.size(); i++) {
        	UserGeographiesVO geographyVO = (UserGeographiesVO)_networkList.get(i);
        	if(geographyVO.getGraphDomainCode().equals(networkCode)) str = geographyVO.getGraphDomainName();
        }
        return str;
    }
    
    /**
     * @return Returns the roleType.
     */
    public String getAllowedUserTypeCreation() {
        return allowedUserTypeCreation;
    }
    /**
     * @param roleType The roleType to set.
     */
    public void setAllowedUserTypeCreation(String allowedUserTypeCreations) {
    	allowedUserTypeCreation = allowedUserTypeCreations;
    }
    
    public ArrayList getDocumentTypeList() {
        return _documentTypeList;
    }

    public void setDocumentTypeList(ArrayList documentTypeList) {
        _documentTypeList = documentTypeList;
    }

    public String getDocumentType() {
        return _documentType;
    }

    public void setDocumentType(String documentType) {
        if (documentType != null) {
            _documentType = documentType.trim();
        }
    }

    public String getDocumentTypeDesc() {
        return _documentTypeDesc;
    }

    public void setDocumentTypeDesc(String documentTypeDesc) {
        if (documentTypeDesc != null) {
            _documentTypeDesc = documentTypeDesc.trim();
        }
    }
    
    public String getDocumentNo() {
        return _documentNo;
    }

    public void setDocumentNo(String documentNo) {
        if (documentNo != null) {
            _documentNo = documentNo.trim();
        }
    }
    
    public ArrayList getPaymentTypeList() {
        return _paymentTypeList;
    }

    public void setPaymentTypeList(ArrayList paymentTypeList) {
        _paymentTypeList = paymentTypeList;
    }

    public String getPaymentType() {
        return _paymentType;
    }

    public void setPaymentType(String paymentType) {
        if (paymentType != null) {
            _paymentType = paymentType.trim();
        }
    }

    public String getPaymentTypeDesc() {
        return _paymentTypeDesc;
    }

    public void setPaymentTypeDesc(String paymentTypeDesc) {
        if (paymentTypeDesc != null) {
            _paymentTypeDesc = paymentTypeDesc.trim();
        }
    }
    
    /**
     * To get the value of VoucherTypeList field
     * 
     * @return VoucherTypeList.
     */
    public ArrayList getVoucherList() {
        return  _voucherList;
    }

    /**
     * To set the value of VoucherTypeList field
     */
    public void setVoucherList(ArrayList voucherList) {
        this._voucherList = voucherList;
    }

	
	 /**
     * To get the value of VoucherTypeListCount field
     * 
     * @return VoucherTypeListCount.
     */
    public int getVoucherListCount() {
        
    	if (_voucherList != null && _voucherList.size() > 0) {
            return _voucherList.size();
        } else {
            return 0;
        }
        
    }
    
    
    /**
     * @return Returns the voucherTypes.
     */
    public String[] getVoucherTypes() {
        return _voucherTypes;
    }

    /**
     * @param voucherTypes
     *            The voucherTypes to set.
     */
    public void setVoucherTypes(String[] voucherTypes) {
        _voucherTypes = voucherTypes;
    }
    
    
    public int getVoucherTypesCount() {
        if (_voucherTypes != null && _voucherTypes.length > 0) {
            return _voucherTypes.length;
        } else {
            return 0;
        }
    }
	public String[] getSegments() {
		return segments;
	}
	public void setSegments(String[] segments) {
		this.segments = segments;
	}
	
	public int getSegmentsListCount() {
        
    	if (segmentList != null && segmentList.size() > 0) {
            return segmentList.size();
        } else {
            return 0;
        }
        
    }
	public ArrayList getSegmentList() {
		return segmentList;
	}
	public void setSegmentList(ArrayList segmentList) {
		this.segmentList = segmentList;
	}
	
	public int getSegmentsCount() {
        if (segments != null && segments.length > 0) {
            return segments.length;
        } else {
            return 0;
        }
    }
	
	
    
	
	public String getUserGradeName() {
		return _userGradeName;
	}
	public void setUserGradeName(String _userGradeName) {
		this._userGradeName = _userGradeName;
	}
	
	
	public List<ListValueVO> getLoanProfileList() {
		return loanProfileList;
	}
	public void setLoanProfileList(List<ListValueVO> loanProfileList) {
		this.loanProfileList = loanProfileList;
	}
	public String getLoanProfileId() {
		return loanProfileId;
	}
	public void setLoanProfileId(String loanProfileId) {
		this.loanProfileId = loanProfileId;
	}
	public String getLoanProfileIdDesc() {
		return loanProfileIdDesc;
	}
	public void setLoanProfileIdDesc(String loanProfileIdDesc) {
		this.loanProfileIdDesc = loanProfileIdDesc;
	}
	
	
	
	public void oldPinValidation(Connection p_con,String p_requestPin, ChannelUserVO p_senderVO) throws BTSLBaseException, SQLException{
		String methodName="oldPinValidation";
        int updateStatus = 0;
        boolean increaseInvalidPinCount = false;
        boolean isUserBarred = false;
        final SubscriberDAO subscriberDAO = new SubscriberDAO();
        final long pnBlckRstDuration = ((Long) PreferenceCache.getControlPreference(PreferenceI.C2S_PIN_BLK_RST_DURATION, p_senderVO.getNetworkID(),
                p_senderVO.getCategoryCode())).longValue();
        Integer c2sPinMaxLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_MAX_LENGTH);
        String pinpasEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
        final int maxPinBlckCnt = ((Integer) PreferenceCache.getControlPreference(PreferenceI.C2S_MAX_PIN_BLOCK_COUNT_CODE,
                p_senderVO.getNetworkID(), p_senderVO.getCategoryCode())).intValue();
        boolean checkpin = false;
	    UserPhoneVO userPhoneVO = new UserPhoneVO();
        userPhoneVO = p_senderVO.getUserPhoneVO();
	    final String decryptedPin = BTSLUtil.decryptText(userPhoneVO.getSmsPin());
  	    BarredUserDAO barredUserDAO = new BarredUserDAO();
	    boolean isBarred=barredUserDAO.isExists(p_con, "C2S", p_senderVO.getNetworkID(),userPhoneVO.getMsisdn(),PretupsI.CHANEL_BARRED_USER_TYPE_SENDER,PretupsI.BARRED_TYPE_PIN_INVALID );
    	if(isBarred){
    		userPhoneVO.setBarUserForInvalidPin(true);
    		EventHandler.handle(EventIDI.PIN_BLOCKED,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserForm[validatePIN]","","","","Sender Pin Blocked.");
            throw new BTSLBaseException("UserForm", methodName, "c2stranfer.c2srecharge.error.pin.blocked");
    	}
    	
		   if ("SHA".equalsIgnoreCase(pinpasEnDeCryptionType)) {
	        if (p_requestPin.length() > (int)c2sPinMaxLength) {
	            checkpin = decryptedPin.equals(p_requestPin);
	        } else {
	            checkpin = (!PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(decryptedPin, p_requestPin)));
	        }
	    } else {
	        checkpin = decryptedPin.equals(p_requestPin);
	    }
	    if (!checkpin) {
	    	increaseInvalidPinCount = true;
	        final int mintInDay = 24 * 60;
	        if (userPhoneVO.getFirstInvalidPinTime() != null) {
	            // Check if PIN counters needs to be reset after the
	            // reset duration
	            if (logger.isDebugEnabled()) {
	            	logger.debug(
	                    "UserForm",
	                    "p_senderVO.getModifiedOn().getTime()=" + p_senderVO.getModifiedOn().getTime() + " p_senderVO.getFirstInvalidPinTime().getTime()=" + userPhoneVO
	                        .getFirstInvalidPinTime().getTime() + " Diff=" + ((p_senderVO.getModifiedOn().getTime() - userPhoneVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) + " Allowed=" + pnBlckRstDuration);
	            }
	            final Calendar cal = BTSLDateUtil.getInstance();
	            cal.setTime(userPhoneVO.getModifiedOn());
	            final int d1 = cal.get(Calendar.DAY_OF_YEAR);
	            cal.setTime(userPhoneVO.getFirstInvalidPinTime());
	            final int d2 = cal.get(Calendar.DAY_OF_YEAR);
	            if (logger.isDebugEnabled()) {
	                logger.debug("UserForm", "Day Of year of Modified On=" + d1 + " Day Of year of FirstInvalidPinTime=" + d2);
	            }
	            if (d1 != d2 && pnBlckRstDuration <= mintInDay) {
                    // reset
                    userPhoneVO.setInvalidPinCount(1);
                    userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                } else if (d1 != d2 && pnBlckRstDuration > mintInDay && (d1 - d2) >= (pnBlckRstDuration / mintInDay)) {
                    // Reset
                    userPhoneVO.setInvalidPinCount(1);
                    userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                } else if (((userPhoneVO.getModifiedOn().getTime() - userPhoneVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) < pnBlckRstDuration) {
                    if ((userPhoneVO.getInvalidPinCount() - maxPinBlckCnt) == -1 && "changeSelfPin".equals(getRequestType())) {
                        // Set The flag that indicates that we need to
                        // bar the user because of PIN Change
                        userPhoneVO.setInvalidPinCount(0);
                        userPhoneVO.setFirstInvalidPinTime(null);
                        //Handling of Barred User Based on configuration in case of reaching the maximum Invalid PIN count
                        String onMaxInvalidPinPassUserBarredReq = Constants.getProperty("ONMAX_INVALID_PIN_PASSWORD_USER_BARRED_REQUIRED");
                        if (BTSLUtil.isNullString(onMaxInvalidPinPassUserBarredReq) || "null".equalsIgnoreCase(onMaxInvalidPinPassUserBarredReq) || PretupsI.NO.equals(onMaxInvalidPinPassUserBarredReq)) {
                        	onMaxInvalidPinPassUserBarredReq = PretupsI.NO;
                        } else if(PretupsI.YES.equals(onMaxInvalidPinPassUserBarredReq)){
                        	onMaxInvalidPinPassUserBarredReq = PretupsI.YES;
                        } else {
                        	onMaxInvalidPinPassUserBarredReq = PretupsI.NO;
                        }
                        if(PretupsI.YES.equalsIgnoreCase(onMaxInvalidPinPassUserBarredReq)) {
                        	userPhoneVO.setBarUserForInvalidPin(true);
                        	isUserBarred = true;
                        } else {
                        	isUserBarred = false;
                        }
                        
                    } else {
                        userPhoneVO.setInvalidPinCount(userPhoneVO.getInvalidPinCount() + 1);
                    }

                    if (userPhoneVO.getInvalidPinCount() == 0) {
                        userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                    }
                } else {
                    userPhoneVO.setInvalidPinCount(1);
                    userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                }
            } else {
                userPhoneVO.setInvalidPinCount(1);
                userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
            }
        } else {
            // initilize PIN Counters if ifPinCount>0
            if (userPhoneVO.getInvalidPinCount() > 0) {
                userPhoneVO.setInvalidPinCount(0);
                userPhoneVO.setFirstInvalidPinTime(null);
                updateStatus = new ChannelUserDAO().updateSmsPinCounter(p_con, userPhoneVO);
                if (updateStatus < 0) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validatePIN]", "",
                        userPhoneVO.getMsisdn(), "", "Not able to update invalid PIN count for users");
                    throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
                }
            }
        }
	    
	    //if max pin count is 1,block the pin after 1 wrong pin
	    if (userPhoneVO.getInvalidPinCount() == maxPinBlckCnt && "changeSelfPin".equals(getRequestType())) {
            // Set The flag that indicates that we need to
            // bar the user because of PIN Change
            userPhoneVO.setInvalidPinCount(0);
            userPhoneVO.setFirstInvalidPinTime(null);
            //Handling of Barred User Based on configuration in case of reaching the maximum Invalid PIN count
            String onMaxInvalidPinPassUserBarredReq = Constants.getProperty("ONMAX_INVALID_PIN_PASSWORD_USER_BARRED_REQUIRED");
            if (BTSLUtil.isNullString(onMaxInvalidPinPassUserBarredReq) || "null".equalsIgnoreCase(onMaxInvalidPinPassUserBarredReq) || PretupsI.NO.equals(onMaxInvalidPinPassUserBarredReq)) {
            	onMaxInvalidPinPassUserBarredReq = PretupsI.NO;
            } else if(PretupsI.YES.equals(onMaxInvalidPinPassUserBarredReq)){
            	onMaxInvalidPinPassUserBarredReq = PretupsI.YES;
            } else {
            	onMaxInvalidPinPassUserBarredReq = PretupsI.NO;
            }
            if(PretupsI.YES.equalsIgnoreCase(onMaxInvalidPinPassUserBarredReq)) {
            	userPhoneVO.setBarUserForInvalidPin(true);
            	isUserBarred = true;
            } else {
            	isUserBarred = false;
            }
        } 
	    
        if (increaseInvalidPinCount) {
            updateStatus = new ChannelUserDAO().updateSmsPinCounter(p_con, userPhoneVO);
            p_con.commit();
            if (updateStatus > 0 && !isUserBarred) {
            	 EventHandler.handle(EventIDI.INVALID_PIN,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserForm[validatePIN]","","","","Sender Invalid Pin.");
                 
                throw new BTSLBaseException("UserForm", methodName, "user.changepin.error.staffvalidatesmspinandoldpin");
            } else if (updateStatus > 0 && isUserBarred) {
            	 EventHandler.handle(EventIDI.PIN_BLOCKED,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserForm[validatePIN]","","","","Sender Pin Blocked.");
                 
                throw new BTSLBaseException("UserForm", methodName, "c2stranfer.c2srecharge.error.pin.blocked");
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserForm[validatePIN]", "",
                    userPhoneVO.getMsisdn(), "", "Not able to update invalid PIN count for users");
                throw new BTSLBaseException("UserForm", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
            }
        }
        if (p_con != null) {
            p_con.commit();
        }
	}
	
	
	
	
	
	//for Robi Client loan cr
	
		private String loanGiven;
	private String loanGivenAmountString;
	private long loanGivenAmount;
	private String lastLoanDateAsString;
	private Date lastLoanDate;
	private String loanSettlementId;
	private String loanAmountAsString;
	private long loanAmount;
	private long loanPremium;
	private String loanPremiumAsString;
	private long totalLoanAmount;
	private String totalLoanAmountAsString;
	private String loanEligible;
	
	private String loanEnqUserId;
	private String loanEnqUserName;
	
	public String getLoanGiven() {
		return loanGiven;
	}
	public void setLoanGiven(String loanGiven) {
		this.loanGiven = loanGiven;
	}
	public String getLoanGivenAmountString() {
		return loanGivenAmountString;
	}
	public void setLoanGivenAmountString(String loanGivenAmountString) {
		this.loanGivenAmountString = loanGivenAmountString;
	}
	public long getLoanGivenAmount() {
		return loanGivenAmount;
	}
	public void setLoanGivenAmount(long loanGivenAmount) {
		this.loanGivenAmount = loanGivenAmount;
	}
	public String getLastLoanDateAsString() {
		return lastLoanDateAsString;
	}
	public void setLastLoanDateAsString(String lastLoanDateAsString) {
		this.lastLoanDateAsString = lastLoanDateAsString;
	}
	public Date getLastLoanDate() {
		return lastLoanDate;
	}
	public void setLastLoanDate(Date lastLoanDate) {
		this.lastLoanDate = lastLoanDate;
	}
	public String getLoanSettlementId() {
		return loanSettlementId;
	}
	public void setLoanSettlementId(String loanSettlementId) {
		this.loanSettlementId = loanSettlementId;
	}
	
	public String getLoanAmountAsString() {
		return loanAmountAsString;
	}
	public void setLoanAmountAsString(String loanAmountAsString) {
		this.loanAmountAsString = loanAmountAsString;
	}
	public long getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(long loanAmount) {
		this.loanAmount = loanAmount;
	}
	public long getLoanPremium() {
		return loanPremium;
	}
	public void setLoanPremium(long loanPremium) {
		this.loanPremium = loanPremium;
	}
	public String getLoanPremiumAsString() {
		return loanPremiumAsString;
	}
	public void setLoanPremiumAsString(String loanPremiumAsString) {
		this.loanPremiumAsString = loanPremiumAsString;
	}
	public long getTotalLoanAmount() {
		return totalLoanAmount;
	}
	public void setTotalLoanAmount(long totalLoanAmount) {
		this.totalLoanAmount = totalLoanAmount;
	}
	public String getTotalLoanAmountAsString() {
		return totalLoanAmountAsString;
	}
	public void setTotalLoanAmountAsString(String totalLoanAmountAsString) {
		this.totalLoanAmountAsString = totalLoanAmountAsString;
	}
	public String getLoanEnqUserId() {
		return loanEnqUserId;
	}
	public void setLoanEnqUserId(String loanEnqUserId) {
		this.loanEnqUserId = loanEnqUserId;
	}
	public String getLoanEnqUserName() {
		return loanEnqUserName;
	}
	public void setLoanEnqUserName(String loanEnqUserName) {
		this.loanEnqUserName = loanEnqUserName;
	}
	
	
	public String getLoanEligible() {
		return loanEligible;
	}
	public void setLoanEligible(String loanEligible) {
		this.loanEligible = loanEligible;
	}
	
	
	
	
	private ArrayList<UserLoanVO> userLoanList;
	
	
	public ArrayList<UserLoanVO> getUserLoanList() {
		return userLoanList;
	}
	public void setUserLoanList(ArrayList<UserLoanVO> userLoanList) {
		this.userLoanList = userLoanList;
	}
	public void loanEnqFlush() {
		loanGiven = null;
		loanGivenAmountString = null;
		loanGivenAmount = 0;
		lastLoanDateAsString = null;
		lastLoanDate = null;
		loanSettlementId = null;
		loanAmountAsString = null;
		loanAmount = 0;
		loanPremium = 0;
		loanPremiumAsString = null;
		totalLoanAmount = 0;
		totalLoanAmountAsString = null;
		loanEnqUserId = null;
		loanEnqUserName = null;
		loanEligible = null;
	}
}
