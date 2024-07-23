package com.web.user.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//import org.apache.struts.upload.FormFile;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;

public class UserModel implements Serializable {


	private static final long serialVersionUID = 1L;
	private static final Log _log = LogFactory.getLog(UserModel.class.getName());
    private CategoryVO categoryVO;
    private ChannelUserVO channelUserVO;
    private String requestType;

   
    private ArrayList userList;

    
    private ArrayList categoryList;
    private String categoryCode;
    private String categoryCodeDesc;
    private String userName;
    private ArrayList selectDomainList;
    private String domainCode;
    private String domainCodeDesc;
    private String channelCategoryCode;
    private String channelCategoryCodeDummy;
    private String channelCategoryDesc;
    private ArrayList origCategoryList;
    private ArrayList origParentCategoryList;
    private ArrayList parentCategoryList;
    private String parentCategoryCode;
    private String parentCategoryDesc;
    private String parentDomainCode;
    private String parentDomainDesc;
    private String parentDomainTypeDesc;
    private String channelUserName;
    private ArrayList associatedGeographicalList;

   
    private ArrayList searchList;
    private String[] searchTextArray;
    private int searchIndex;
    private String[] searchUserId;
    private String[] distributorSearchFlag;

    
    private String userType;
    private ArrayList userGradeList;
    private String userGradeId;
    private String userGradeIdDesc;
    private ArrayList trannferProfileList;
    private String trannferProfileId;
    private String trannferProfileIdDesc;
    private ArrayList commissionProfileList;
    private String commissionProfileSetId;
    private String commissionProfileSetIdDesc;
    private String insuspend;
    private String outsuspend;
    private String userCode;
    
    private boolean userCodeFlag;
    private String level1ApprovedBy;
    private Date level1ApprovedOn;
    private String level2ApprovedBy;
    private Date level2ApprovedOn;
    private String outletCode = null;
    private String outletCodeDesc = null;
    private String subOutletCode = null;
    private String subOutletCodeDesc = null;
    private ArrayList outletList;
    private ArrayList subOutletList;

    
    private String webLoginID;
    private String oldWebLoginID;
    private String showPassword;
    private String webPassword;
    private String confirmPassword;
    private String parentID;
    private String ownerID;
    private String allowedIPs;
    private String[] allowedDays;
    private String allowedFormTime;
    private String allowedToTime;
    private String empCode;
    private String status;
    private String statusDesc;
    private String previousStatus;
    private ArrayList statusList;
    private String email;
    private Date passwordModifiedOn;
    private String contactPerson;
    private String contactNo;
    private String designation;
    private String divisionCode;
    private String divisionDesc;
    private ArrayList divisionList;
    private String departmentCode;
    private String departmentDesc;
    private ArrayList departmentList;
    private String msisdn;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String ssn;
    private String userNamePrefixCode = null;
    private String userNamePrefixDesc = null;
    private ArrayList userNamePrefixList;
    private String externalCode;
    private String shortName;
    private String appointmentDate;
    
    private ArrayList<UserPhoneVO> msisdnList;
    private ArrayList oldMsisdnList;
    private String primaryRadio;
    private ArrayList phoneProfileList;
    private String roleType;
    private ArrayList geographicalList;
    private String geographicalCode;
    private String[] geographicalCodeArray;
    private ArrayList domainSearchList;
    private String[] searchDomainTextArray;
    private String[] searchDomainCode;
    private String searchDomainName;
    private ArrayList graphDomainList;
    private String domainId;
    private String grphDomainTypeName;
    private String userGeographyCode;
    
	private ArrayList networkList;
    

    
    private HashMap rolesMap;
    private HashMap rolesMapSelected;
    private String[] roleFlag;
    private String[] roleFlagOld;
    private ArrayList domainList;
    private String[] domainCodes;
    private ArrayList servicesList;
    private String[] servicesTypes;

    
    private ArrayList productsList;
    private String[] productCodes;
    private String userId;
    private long lastModified;
    
    private long time;

    private String oldPassword = null;
    private String newPassword = null;
    private String confirmNewPassword = null;

    
    private String searchMsisdn;
    private String networkCode;
    private String networkName;
    private String searchLoginId = null;
    private String searchCriteria;
    private String parentName = null;
    private String parentMsisdn = null;
    private String parentCategoryName = null;
    private String ownerName = null;
    private String ownerMsisdn = null;
    private String ownerCategoryName = null;

    
    private String[] approvalArray;

 
    private String[] resumeArray;

    
    private ArrayList userBalanceList;
    private ArrayList agentBalanceList;
    private String viewType;
    private boolean domainShowFlag;


    private UserTransferCountsVO userTransferCountsVO;
    private TransferProfileVO transferProfileVO;
    private boolean subscriberOutCountFlag;
    private boolean unctrlTransferFlag;
    private String creationType;
    private String creationTypeDesc;
    private String batchID;


    private ArrayList mpayProfileList = null;
    private String mpayProfileID = "";
    private String mpayProfileIDWithGrad = null;
    private String mpayProfileDesc = null;
    private String mcommerceServiceAllow = "N";
    private String lowBalAlertAllow = "N";
    
    private String oldOtherInfo;
    private String newOtherInfo;
    private String otherInfo;


    private String loginUserID;
    private String loginUserDomainCode;
    private String loginUserCategoryCode;

    private ArrayList activationProfileList;
    private String setID;
    private String tempSetID;
    private String setName;
    private String profileType;
    private String deAssociate;


    private String pwdGenerateAllow = "N";
    private boolean isStaff = false;
    private String smsPin = null;
    private String confirmPin = null;
    private String description = null;
    private boolean deleteMsisdn = false;
    private boolean msisdnInfoModified = false;

    private String categoryDefaultProfile;
    
    private String isCategoryCodeNeeded;
    private boolean isCceCategory = true;

   
    private String createdBy;
    private String createdOn;
    
    private String eventRemarks;
    
    private String registrationDate = null;
    private String msisdnTextArea;
    
    private String longitude = null;
    private String latitude = null;
    private String documentType = null;
    private String documentNo = null;
    private String paymentType = null;

    private String otherEmail;
    private String lowBalAlertToParent = "N";
    private String lowBalAlertToOther = "N";
    private String lowBalAlertToSelf = "N";
    // 
    private String company;
    private String fax;
    private String firstName;
    private String lastName;
    private ArrayList userLanguageList;
    private String userLanguage = null;
    private String userLanguageDesc = null;
    
    private boolean isSerAssignChnlAdm = false;

    
    //private FormFile file;
    private String domainName;
    private String errorFlag;
    private ArrayList errorList = null;
    private int totalRecords;
    private String noOfRecords;
    private int domainListTSize;
    
  
	private HashMap masterMap;
  
    private String rsaAuthentication;
    private boolean rsaRequired = false;
   
    private ChannelUserVO staffParentVO;
    private String loginUserDomainID;

    
    private String trannferRuleTypeId;
    private String trannferRuleTypeIdDesc;
    private ArrayList trannferRuleTypeList;

    private String primaryNumber = null;
    private String phoneProfileDesc;


    private boolean otpRequired = false;
    private String authTypeAllowed;
    private String pinGenerateAllow = "N";
    private String showSmsPin = null;
    private String confirmSmsPin = null;


    private String lmsProfileId;
    private String lmsProfileListIdDesc;
    private ArrayList lmsProfileList;

    private String bttnAssociate;
    private String bttnDeAssociate;
    private String backBttn;
    private String msisdnForAssDeAss;
    private String channeluserType;
    private ArrayList channelUserTypeList;
    private String downloadType = null;

    private String assType;
    private String assoMsisdn;
    private Date associationCreatedOn;
    private Date associationModifiedOn;
    private String controlGroup = "N";
    
    private String mainBal;
    private String bonusBal;
    private String totalBal;
    private String oldSmsPin;
    private String multiBox;
	private String allowedUserTypeCreation=null;
	private int geoDomainSize;
	private String ownerNameAndID;
	
	public String getOwnerNameAndID() {
		return ownerNameAndID;
	}

	public void setOwnerNameAndID(String ownerNameAndID) {
		this.ownerNameAndID = ownerNameAndID;
	}

	public int getGeoDomainSize() {
		return geoDomainSize;
	}

	public void setGeoDomainSize(int geoDomainSize) {
		this.geoDomainSize = geoDomainSize;
	}
    
	public String getMultiBox() {
		return multiBox;
	}

	public void setMultiBox(String multiBox) {
		this.multiBox = multiBox;
	}


	public CategoryVO getCategoryVO() {
		return categoryVO;
	}

	public void setCategoryVO(CategoryVO categoryVO) {
		this.categoryVO = categoryVO;
	}

	public ChannelUserVO getChannelUserVO() {
		return channelUserVO;
	}

	public void setChannelUserVO(ChannelUserVO channelUserVO) {
		this.channelUserVO = channelUserVO;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public ArrayList getUserList() {
		return userList;
	}

	public void setUserList(ArrayList userList) {
		this.userList = userList;
	}

	public ArrayList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryCodeDesc() {
		return categoryCodeDesc;
	}

	public void setCategoryCodeDesc(String categoryCodeDesc) {
		this.categoryCodeDesc = categoryCodeDesc;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ArrayList getSelectDomainList() {
		return selectDomainList;
	}

	public void setSelectDomainList(ArrayList selectDomainList) {
		this.selectDomainList = selectDomainList;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getDomainCodeDesc() {
		return domainCodeDesc;
	}

	public void setDomainCodeDesc(String domainCodeDesc) {
		this.domainCodeDesc = domainCodeDesc;
	}

	public String getChannelCategoryCode() {
		return channelCategoryCode;
	}

	public void setChannelCategoryCode(String channelCategoryCode) {
		this.channelCategoryCode = channelCategoryCode;
	}

	public String getChannelCategoryCodeDummy() {
		return channelCategoryCodeDummy;
	}

	public void setChannelCategoryCodeDummy(String channelCategoryCodeDummy) {
		this.channelCategoryCodeDummy = channelCategoryCodeDummy;
	}

	public String getChannelCategoryDesc() {
		return channelCategoryDesc;
	}

	public void setChannelCategoryDesc(String channelCategoryDesc) {
		this.channelCategoryDesc = channelCategoryDesc;
	}

	public ArrayList getOrigCategoryList() {
		return origCategoryList;
	}

	public void setOrigCategoryList(ArrayList origCategoryList) {
		this.origCategoryList = origCategoryList;
	}

	public ArrayList getOrigParentCategoryList() {
		return origParentCategoryList;
	}

	public void setOrigParentCategoryList(ArrayList origParentCategoryList) {
		this.origParentCategoryList = origParentCategoryList;
	}

	public ArrayList getParentCategoryList() {
		return parentCategoryList;
	}

	public void setParentCategoryList(ArrayList parentCategoryList) {
		this.parentCategoryList = parentCategoryList;
	}

	public String getParentCategoryCode() {
		return parentCategoryCode;
	}

	public void setParentCategoryCode(String parentCategoryCode) {
		this.parentCategoryCode = parentCategoryCode;
	}

	public String getParentCategoryDesc() {
		return parentCategoryDesc;
	}

	public void setParentCategoryDesc(String parentCategoryDesc) {
		this.parentCategoryDesc = parentCategoryDesc;
	}

	public String getParentDomainCode() {
		return parentDomainCode;
	}

	public void setParentDomainCode(String parentDomainCode) {
		this.parentDomainCode = parentDomainCode;
	}

	public String getParentDomainDesc() {
		return parentDomainDesc;
	}

	public void setParentDomainDesc(String parentDomainDesc) {
		this.parentDomainDesc = parentDomainDesc;
	}

	public String getParentDomainTypeDesc() {
		return parentDomainTypeDesc;
	}

	public void setParentDomainTypeDesc(String parentDomainTypeDesc) {
		this.parentDomainTypeDesc = parentDomainTypeDesc;
	}

	public String getChannelUserName() {
		return channelUserName;
	}

	public void setChannelUserName(String channelUserName) {
		this.channelUserName = channelUserName;
	}

	public ArrayList getAssociatedGeographicalList() {
		return associatedGeographicalList;
	}

	public void setAssociatedGeographicalList(ArrayList associatedGeographicalList) {
		this.associatedGeographicalList = associatedGeographicalList;
	}

	public ArrayList getSearchList() {
		return searchList;
	}

	public void setSearchList(ArrayList searchList) {
		this.searchList = searchList;
	}

	public String[] getSearchTextArray() {
		return searchTextArray;
	}

	public void setSearchTextArray(String[] searchTextArray) {
		this.searchTextArray = searchTextArray;
	}

	public int getSearchIndex() {
		return searchIndex;
	}

	public void setSearchIndex(int searchIndex) {
		this.searchIndex = searchIndex;
	}

	public String[] getSearchUserId() {
		return searchUserId;
	}

	public void setSearchUserId(String[] searchUserId) {
		this.searchUserId = searchUserId;
	}

	public String[] getDistributorSearchFlag() {
		return distributorSearchFlag;
	}

	public void setDistributorSearchFlag(String[] distributorSearchFlag) {
		this.distributorSearchFlag = distributorSearchFlag;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public ArrayList getUserGradeList() {
		return userGradeList;
	}

	public void setUserGradeList(ArrayList userGradeList) {
		this.userGradeList = userGradeList;
	}

	public String getUserGradeId() {
		return userGradeId;
	}

	public void setUserGradeId(String userGradeId) {
		this.userGradeId = userGradeId;
	}

	public String getUserGradeIdDesc() {
		return userGradeIdDesc;
	}

	public void setUserGradeIdDesc(String userGradeIdDesc) {
		this.userGradeIdDesc = userGradeIdDesc;
	}

	public ArrayList getTrannferProfileList() {
		return trannferProfileList;
	}

	public void setTrannferProfileList(ArrayList trannferProfileList) {
		this.trannferProfileList = trannferProfileList;
	}

	public String getTrannferProfileId() {
		return trannferProfileId;
	}

	public void setTrannferProfileId(String trannferProfileId) {
		this.trannferProfileId = trannferProfileId;
	}

	public String getTrannferProfileIdDesc() {
		return trannferProfileIdDesc;
	}

	public void setTrannferProfileIdDesc(String trannferProfileIdDesc) {
		this.trannferProfileIdDesc = trannferProfileIdDesc;
	}

	public ArrayList getCommissionProfileList() {
		return commissionProfileList;
	}

	public void setCommissionProfileList(ArrayList commissionProfileList) {
		this.commissionProfileList = commissionProfileList;
	}

	public String getCommissionProfileSetId() {
		return commissionProfileSetId;
	}

	public void setCommissionProfileSetId(String commissionProfileSetId) {
		this.commissionProfileSetId = commissionProfileSetId;
	}

	public String getCommissionProfileSetIdDesc() {
		return commissionProfileSetIdDesc;
	}

	public void setCommissionProfileSetIdDesc(String commissionProfileSetIdDesc) {
		this.commissionProfileSetIdDesc = commissionProfileSetIdDesc;
	}

	public String getInsuspend() {
		return insuspend;
	}

	public void setInsuspend(String insuspend) {
		this.insuspend = insuspend;
	}

	public String getOutsuspend() {
		return outsuspend;
	}

	public void setOutsuspend(String outsuspend) {
		this.outsuspend = outsuspend;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public boolean isUserCodeFlag() {
		return userCodeFlag;
	}

	public void setUserCodeFlag(boolean userCodeFlag) {
		this.userCodeFlag = userCodeFlag;
	}

	public String getLevel1ApprovedBy() {
		return level1ApprovedBy;
	}

	public void setLevel1ApprovedBy(String level1ApprovedBy) {
		this.level1ApprovedBy = level1ApprovedBy;
	}

	public Date getLevel1ApprovedOn() {
		return level1ApprovedOn;
	}

	public void setLevel1ApprovedOn(Date level1ApprovedOn) {
		this.level1ApprovedOn = level1ApprovedOn;
	}

	public String getLevel2ApprovedBy() {
		return level2ApprovedBy;
	}

	public void setLevel2ApprovedBy(String level2ApprovedBy) {
		this.level2ApprovedBy = level2ApprovedBy;
	}

	public Date getLevel2ApprovedOn() {
		return level2ApprovedOn;
	}

	public void setLevel2ApprovedOn(Date level2ApprovedOn) {
		this.level2ApprovedOn = level2ApprovedOn;
	}

	public String getOutletCode() {
		return outletCode;
	}

	public void setOutletCode(String outletCode) {
		this.outletCode = outletCode;
	}

	public String getOutletCodeDesc() {
		return outletCodeDesc;
	}

	public void setOutletCodeDesc(String outletCodeDesc) {
		this.outletCodeDesc = outletCodeDesc;
	}

	public String getSubOutletCode() {
		return subOutletCode;
	}

	public void setSubOutletCode(String subOutletCode) {
		this.subOutletCode = subOutletCode;
	}

	public String getSubOutletCodeDesc() {
		return subOutletCodeDesc;
	}

	public void setSubOutletCodeDesc(String subOutletCodeDesc) {
		this.subOutletCodeDesc = subOutletCodeDesc;
	}

	public ArrayList getOutletList() {
		return outletList;
	}

	public void setOutletList(ArrayList outletList) {
		this.outletList = outletList;
	}

	public ArrayList getSubOutletList() {
		return subOutletList;
	}

	public void setSubOutletList(ArrayList subOutletList) {
		this.subOutletList = subOutletList;
	}

	public String getWebLoginID() {
		return webLoginID;
	}

	public void setWebLoginID(String webLoginID) {
		this.webLoginID = webLoginID;
	}

	public String getOldWebLoginID() {
		return oldWebLoginID;
	}

	public void setOldWebLoginID(String oldWebLoginID) {
		this.oldWebLoginID = oldWebLoginID;
	}

	public String getShowPassword() {
		return showPassword;
	}

	public void setShowPassword(String showPassword) {
		this.showPassword = showPassword;
	}

	public String getWebPassword() {
		return webPassword;
	}

	public void setWebPassword(String webPassword) {
		this.webPassword = webPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public String getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	public String getAllowedIPs() {
		return allowedIPs;
	}

	public void setAllowedIPs(String allowedIPs) {
		this.allowedIPs = allowedIPs;
	}

	public String[] getAllowedDays() {
		return allowedDays;
	}

	public void setAllowedDays(String[] allowedDays) {
		this.allowedDays = allowedDays;
	}

	public String getAllowedFormTime() {
		return allowedFormTime;
	}

	public void setAllowedFormTime(String allowedFormTime) {
		this.allowedFormTime = allowedFormTime;
	}

	public String getAllowedToTime() {
		return allowedToTime;
	}

	public void setAllowedToTime(String allowedToTime) {
		this.allowedToTime = allowedToTime;
	}

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}

	public ArrayList getStatusList() {
		return statusList;
	}

	public void setStatusList(ArrayList statusList) {
		this.statusList = statusList;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getPasswordModifiedOn() {
		return passwordModifiedOn;
	}

	public void setPasswordModifiedOn(Date passwordModifiedOn) {
		this.passwordModifiedOn = passwordModifiedOn;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public String getDivisionDesc() {
		return divisionDesc;
	}

	public void setDivisionDesc(String divisionDesc) {
		this.divisionDesc = divisionDesc;
	}

	public ArrayList getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(ArrayList divisionList) {
		this.divisionList = divisionList;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDepartmentDesc() {
		return departmentDesc;
	}

	public void setDepartmentDesc(String departmentDesc) {
		this.departmentDesc = departmentDesc;
	}

	public ArrayList getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(ArrayList departmentList) {
		this.departmentList = departmentList;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getUserNamePrefixCode() {
		return userNamePrefixCode;
	}

	public void setUserNamePrefixCode(String userNamePrefixCode) {
		this.userNamePrefixCode = userNamePrefixCode;
	}

	public String getUserNamePrefixDesc() {
		return userNamePrefixDesc;
	}

	public void setUserNamePrefixDesc(String userNamePrefixDesc) {
		this.userNamePrefixDesc = userNamePrefixDesc;
	}

	public ArrayList getUserNamePrefixList() {
		return userNamePrefixList;
	}

	public void setUserNamePrefixList(ArrayList userNamePrefixList) {
		this.userNamePrefixList = userNamePrefixList;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(String appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public ArrayList<UserPhoneVO> getMsisdnList() {
		return msisdnList;
	}

	public void setMsisdnList(ArrayList<UserPhoneVO> msisdnList) {
		this.msisdnList = msisdnList;
	}

	public ArrayList getOldMsisdnList() {
		return oldMsisdnList;
	}

	public void setOldMsisdnList(ArrayList oldMsisdnList) {
		this.oldMsisdnList = oldMsisdnList;
	}

	public String getPrimaryRadio() {
		return primaryRadio;
	}

	public void setPrimaryRadio(String primaryRadio) {
		this.primaryRadio = primaryRadio;
	}

	public ArrayList getPhoneProfileList() {
		return phoneProfileList;
	}

	public void setPhoneProfileList(ArrayList phoneProfileList) {
		this.phoneProfileList = phoneProfileList;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public ArrayList getGeographicalList() {
		return geographicalList;
	}

	public void setGeographicalList(ArrayList geographicalList) {
		this.geographicalList = geographicalList;
	}

	public String getGeographicalCode() {
		return geographicalCode;
	}

	public void setGeographicalCode(String geographicalCode) {
		this.geographicalCode = geographicalCode;
	}

	public String[] getGeographicalCodeArray() {
		return geographicalCodeArray;
	}

	public void setGeographicalCodeArray(String[] geographicalCodeArray) {
		this.geographicalCodeArray = geographicalCodeArray;
	}

	public ArrayList getDomainSearchList() {
		return domainSearchList;
	}

	public void setDomainSearchList(ArrayList domainSearchList) {
		this.domainSearchList = domainSearchList;
	}

	public String[] getSearchDomainTextArray() {
		return searchDomainTextArray;
	}

	public void setSearchDomainTextArray(String[] searchDomainTextArray) {
		this.searchDomainTextArray = searchDomainTextArray;
	}

	public String[] getSearchDomainCode() {
		return searchDomainCode;
	}

	public void setSearchDomainCode(String[] searchDomainCode) {
		this.searchDomainCode = searchDomainCode;
	}

	public String getSearchDomainName() {
		return searchDomainName;
	}

	public void setSearchDomainName(String searchDomainName) {
		this.searchDomainName = searchDomainName;
	}

	public ArrayList getGraphDomainList() {
		return graphDomainList;
	}

	public void setGraphDomainList(ArrayList graphDomainList) {
		this.graphDomainList = graphDomainList;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getGrphDomainTypeName() {
		return grphDomainTypeName;
	}

	public void setGrphDomainTypeName(String grphDomainTypeName) {
		this.grphDomainTypeName = grphDomainTypeName;
	}

	public String getUserGeographyCode() {
		return userGeographyCode;
	}

	public void setUserGeographyCode(String userGeographyCode) {
		this.userGeographyCode = userGeographyCode;
	}

	public ArrayList getNetworkList() {
		return networkList;
	}

	public void setNetworkList(ArrayList networkList) {
		this.networkList = networkList;
	}

	public HashMap getRolesMap() {
		return rolesMap;
	}

	public void setRolesMap(HashMap rolesMap) {
		this.rolesMap = rolesMap;
	}

	public HashMap getRolesMapSelected() {
		return rolesMapSelected;
	}

	public void setRolesMapSelected(HashMap rolesMapSelected) {
		this.rolesMapSelected = rolesMapSelected;
	}

	public String[] getRoleFlag() {
		return roleFlag;
	}

	public void setRoleFlag(String[] roleFlag) {
		this.roleFlag = roleFlag;
	}

	public String[] getRoleFlagOld() {
		return roleFlagOld;
	}

	public void setRoleFlagOld(String[] roleFlagOld) {
		this.roleFlagOld = roleFlagOld;
	}

	public ArrayList getDomainList() {
		return domainList;
	}

	public void setDomainList(ArrayList domainList) {
		this.domainList = domainList;
	}

	public String[] getDomainCodes() {
		return domainCodes;
	}

	public void setDomainCodes(String[] domainCodes) {
		this.domainCodes = domainCodes;
	}

	public ArrayList getServicesList() {
		return servicesList;
	}

	public void setServicesList(ArrayList servicesList) {
		this.servicesList = servicesList;
	}

	public String[] getServicesTypes() {
		return servicesTypes;
	}

	public void setServicesTypes(String[] servicesTypes) {
		this.servicesTypes = servicesTypes;
	}

	public ArrayList getProductsList() {
		return productsList;
	}

	public void setProductsList(ArrayList productsList) {
		this.productsList = productsList;
	}

	public String[] getProductCodes() {
		return productCodes;
	}

	public void setProductCodes(String[] productCodes) {
		this.productCodes = productCodes;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}

	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}

	public String getSearchMsisdn() {
		return searchMsisdn;
	}

	public void setSearchMsisdn(String searchMsisdn) {
		this.searchMsisdn = searchMsisdn;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getSearchLoginId() {
		return searchLoginId;
	}

	public void setSearchLoginId(String searchLoginId) {
		this.searchLoginId = searchLoginId;
	}

	public String getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentMsisdn() {
		return parentMsisdn;
	}

	public void setParentMsisdn(String parentMsisdn) {
		this.parentMsisdn = parentMsisdn;
	}

	public String getParentCategoryName() {
		return parentCategoryName;
	}

	public void setParentCategoryName(String parentCategoryName) {
		this.parentCategoryName = parentCategoryName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerMsisdn() {
		return ownerMsisdn;
	}

	public void setOwnerMsisdn(String ownerMsisdn) {
		this.ownerMsisdn = ownerMsisdn;
	}

	public String getOwnerCategoryName() {
		return ownerCategoryName;
	}

	public void setOwnerCategoryName(String ownerCategoryName) {
		this.ownerCategoryName = ownerCategoryName;
	}

	public String[] getApprovalArray() {
		return approvalArray;
	}

	public void setApprovalArray(String[] approvalArray) {
		this.approvalArray = approvalArray;
	}

	public String[] getResumeArray() {
		return resumeArray;
	}

	public void setResumeArray(String[] resumeArray) {
		this.resumeArray = resumeArray;
	}

	public ArrayList getUserBalanceList() {
		return userBalanceList;
	}

	public void setUserBalanceList(ArrayList userBalanceList) {
		this.userBalanceList = userBalanceList;
	}

	public ArrayList getAgentBalanceList() {
		return agentBalanceList;
	}

	public void setAgentBalanceList(ArrayList agentBalanceList) {
		this.agentBalanceList = agentBalanceList;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public boolean isDomainShowFlag() {
		return domainShowFlag;
	}

	public void setDomainShowFlag(boolean domainShowFlag) {
		this.domainShowFlag = domainShowFlag;
	}

	public UserTransferCountsVO getUserTransferCountsVO() {
		return userTransferCountsVO;
	}

	public void setUserTransferCountsVO(UserTransferCountsVO userTransferCountsVO) {
		this.userTransferCountsVO = userTransferCountsVO;
	}

	public TransferProfileVO getTransferProfileVO() {
		return transferProfileVO;
	}

	public void setTransferProfileVO(TransferProfileVO transferProfileVO) {
		this.transferProfileVO = transferProfileVO;
	}

	public boolean isSubscriberOutCountFlag() {
		return subscriberOutCountFlag;
	}

	public void setSubscriberOutCountFlag(boolean subscriberOutCountFlag) {
		this.subscriberOutCountFlag = subscriberOutCountFlag;
	}

	public boolean isUnctrlTransferFlag() {
		return unctrlTransferFlag;
	}

	public void setUnctrlTransferFlag(boolean unctrlTransferFlag) {
		this.unctrlTransferFlag = unctrlTransferFlag;
	}

	public String getCreationType() {
		return creationType;
	}

	public void setCreationType(String creationType) {
		this.creationType = creationType;
	}

	public String getCreationTypeDesc() {
		return creationTypeDesc;
	}

	public void setCreationTypeDesc(String creationTypeDesc) {
		this.creationTypeDesc = creationTypeDesc;
	}

	public String getBatchID() {
		return batchID;
	}

	public void setBatchID(String batchID) {
		this.batchID = batchID;
	}

	public ArrayList getMpayProfileList() {
		return mpayProfileList;
	}

	public void setMpayProfileList(ArrayList mpayProfileList) {
		this.mpayProfileList = mpayProfileList;
	}

	public String getMpayProfileID() {
		return mpayProfileID;
	}

	public void setMpayProfileID(String mpayProfileID) {
		this.mpayProfileID = mpayProfileID;
	}

	public String getMpayProfileIDWithGrad() {
		return mpayProfileIDWithGrad;
	}

	public void setMpayProfileIDWithGrad(String mpayProfileIDWithGrad) {
		this.mpayProfileIDWithGrad = mpayProfileIDWithGrad;
	}

	public String getMpayProfileDesc() {
		return mpayProfileDesc;
	}

	public void setMpayProfileDesc(String mpayProfileDesc) {
		this.mpayProfileDesc = mpayProfileDesc;
	}

	public String getMcommerceServiceAllow() {
		return mcommerceServiceAllow;
	}

	public void setMcommerceServiceAllow(String mcommerceServiceAllow) {
		this.mcommerceServiceAllow = mcommerceServiceAllow;
	}

	public String getLowBalAlertAllow() {
		return lowBalAlertAllow;
	}

	public void setLowBalAlertAllow(String lowBalAlertAllow) {
		this.lowBalAlertAllow = lowBalAlertAllow;
	}

	public String getOldOtherInfo() {
		return oldOtherInfo;
	}

	public void setOldOtherInfo(String oldOtherInfo) {
		this.oldOtherInfo = oldOtherInfo;
	}

	public String getNewOtherInfo() {
		return newOtherInfo;
	}

	public void setNewOtherInfo(String newOtherInfo) {
		this.newOtherInfo = newOtherInfo;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public String getLoginUserID() {
		return loginUserID;
	}

	public void setLoginUserID(String loginUserID) {
		this.loginUserID = loginUserID;
	}

	public String getLoginUserDomainCode() {
		return loginUserDomainCode;
	}

	public void setLoginUserDomainCode(String loginUserDomainCode) {
		this.loginUserDomainCode = loginUserDomainCode;
	}

	public String getLoginUserCategoryCode() {
		return loginUserCategoryCode;
	}

	public void setLoginUserCategoryCode(String loginUserCategoryCode) {
		this.loginUserCategoryCode = loginUserCategoryCode;
	}

	public ArrayList getActivationProfileList() {
		return activationProfileList;
	}

	public void setActivationProfileList(ArrayList activationProfileList) {
		this.activationProfileList = activationProfileList;
	}

	public String getSetID() {
		return setID;
	}

	public void setSetID(String setID) {
		this.setID = setID;
	}

	public String getTempSetID() {
		return tempSetID;
	}

	public void setTempSetID(String tempSetID) {
		this.tempSetID = tempSetID;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public String getProfileType() {
		return profileType;
	}

	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}

	public String getDeAssociate() {
		return deAssociate;
	}

	public void setDeAssociate(String deAssociate) {
		this.deAssociate = deAssociate;
	}

	public String getPwdGenerateAllow() {
		return pwdGenerateAllow;
	}

	public void setPwdGenerateAllow(String pwdGenerateAllow) {
		this.pwdGenerateAllow = pwdGenerateAllow;
	}

	public boolean isStaff() {
		return isStaff;
	}

	public void setStaff(boolean isStaff) {
		this.isStaff = isStaff;
	}

	public String getSmsPin() {
		return smsPin;
	}

	public void setSmsPin(String smsPin) {
		this.smsPin = smsPin;
	}

	public String getConfirmPin() {
		return confirmPin;
	}

	public void setConfirmPin(String confirmPin) {
		this.confirmPin = confirmPin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDeleteMsisdn() {
		return deleteMsisdn;
	}

	public void setDeleteMsisdn(boolean deleteMsisdn) {
		this.deleteMsisdn = deleteMsisdn;
	}

	public boolean isMsisdnInfoModified() {
		return msisdnInfoModified;
	}

	public void setMsisdnInfoModified(boolean msisdnInfoModified) {
		this.msisdnInfoModified = msisdnInfoModified;
	}

	public String getCategoryDefaultProfile() {
		return categoryDefaultProfile;
	}

	public void setCategoryDefaultProfile(String categoryDefaultProfile) {
		this.categoryDefaultProfile = categoryDefaultProfile;
	}

	public String getIsCategoryCodeNeeded() {
		return isCategoryCodeNeeded;
	}

	public void setIsCategoryCodeNeeded(String isCategoryCodeNeeded) {
		this.isCategoryCodeNeeded = isCategoryCodeNeeded;
	}

	public boolean isCceCategory() {
		return isCceCategory;
	}

	public void setCceCategory(boolean isCceCategory) {
		this.isCceCategory = isCceCategory;
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

	public String getEventRemarks() {
		return eventRemarks;
	}

	public void setEventRemarks(String eventRemarks) {
		this.eventRemarks = eventRemarks;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getMsisdnTextArea() {
		return msisdnTextArea;
	}

	public void setMsisdnTextArea(String msisdnTextArea) {
		this.msisdnTextArea = msisdnTextArea;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getOtherEmail() {
		return otherEmail;
	}

	public void setOtherEmail(String otherEmail) {
		this.otherEmail = otherEmail;
	}

	public String getLowBalAlertToParent() {
		return lowBalAlertToParent;
	}

	public void setLowBalAlertToParent(String lowBalAlertToParent) {
		this.lowBalAlertToParent = lowBalAlertToParent;
	}

	public String getLowBalAlertToOther() {
		return lowBalAlertToOther;
	}

	public void setLowBalAlertToOther(String lowBalAlertToOther) {
		this.lowBalAlertToOther = lowBalAlertToOther;
	}

	public String getLowBalAlertToSelf() {
		return lowBalAlertToSelf;
	}

	public void setLowBalAlertToSelf(String lowBalAlertToSelf) {
		this.lowBalAlertToSelf = lowBalAlertToSelf;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public ArrayList getUserLanguageList() {
		return userLanguageList;
	}

	public void setUserLanguageList(ArrayList userLanguageList) {
		this.userLanguageList = userLanguageList;
	}

	public String getUserLanguage() {
		return userLanguage;
	}

	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}

	public String getUserLanguageDesc() {
		return userLanguageDesc;
	}

	public void setUserLanguageDesc(String userLanguageDesc) {
		this.userLanguageDesc = userLanguageDesc;
	}

	public boolean isSerAssignChnlAdm() {
		return isSerAssignChnlAdm;
	}

	public void setSerAssignChnlAdm(boolean isSerAssignChnlAdm) {
		this.isSerAssignChnlAdm = isSerAssignChnlAdm;
	}

/*
	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}
*/

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getErrorFlag() {
		return errorFlag;
	}

	public void setErrorFlag(String errorFlag) {
		this.errorFlag = errorFlag;
	}

	public ArrayList getErrorList() {
		return errorList;
	}

	public void setErrorList(ArrayList errorList) {
		this.errorList = errorList;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(String noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

	public int getDomainListTSize() {
		return domainListTSize;
	}

	public void setDomainListTSize(int domainListTSize) {
		this.domainListTSize = domainListTSize;
	}

	public HashMap getMasterMap() {
		return masterMap;
	}

	public void setMasterMap(HashMap masterMap) {
		this.masterMap = masterMap;
	}

	public String getRsaAuthentication() {
		return rsaAuthentication;
	}

	public void setRsaAuthentication(String rsaAuthentication) {
		this.rsaAuthentication = rsaAuthentication;
	}

	public boolean isRsaRequired() {
		return rsaRequired;
	}

	public void setRsaRequired(boolean rsaRequired) {
		this.rsaRequired = rsaRequired;
	}

	public ChannelUserVO getStaffParentVO() {
		return staffParentVO;
	}

	public void setStaffParentVO(ChannelUserVO staffParentVO) {
		this.staffParentVO = staffParentVO;
	}

	public String getLoginUserDomainID() {
		return loginUserDomainID;
	}

	public void setLoginUserDomainID(String loginUserDomainID) {
		this.loginUserDomainID = loginUserDomainID;
	}

	public String getTrannferRuleTypeId() {
		return trannferRuleTypeId;
	}

	public void setTrannferRuleTypeId(String trannferRuleTypeId) {
		this.trannferRuleTypeId = trannferRuleTypeId;
	}

	public String getTrannferRuleTypeIdDesc() {
		return trannferRuleTypeIdDesc;
	}

	public void setTrannferRuleTypeIdDesc(String trannferRuleTypeIdDesc) {
		this.trannferRuleTypeIdDesc = trannferRuleTypeIdDesc;
	}

	public ArrayList getTrannferRuleTypeList() {
		return trannferRuleTypeList;
	}

	public void setTrannferRuleTypeList(ArrayList trannferRuleTypeList) {
		this.trannferRuleTypeList = trannferRuleTypeList;
	}

	public String getPrimaryNumber() {
		return primaryNumber;
	}

	public void setPrimaryNumber(String primaryNumber) {
		this.primaryNumber = primaryNumber;
	}

	public String getPhoneProfileDesc() {
		return phoneProfileDesc;
	}

	public void setPhoneProfileDesc(String phoneProfileDesc) {
		this.phoneProfileDesc = phoneProfileDesc;
	}

	public boolean isOtpRequired() {
		return otpRequired;
	}

	public void setOtpRequired(boolean otpRequired) {
		this.otpRequired = otpRequired;
	}

	public String getAuthTypeAllowed() {
		return authTypeAllowed;
	}

	public void setAuthTypeAllowed(String authTypeAllowed) {
		this.authTypeAllowed = authTypeAllowed;
	}

	public String getPinGenerateAllow() {
		return pinGenerateAllow;
	}

	public void setPinGenerateAllow(String pinGenerateAllow) {
		this.pinGenerateAllow = pinGenerateAllow;
	}

	public String getShowSmsPin() {
		return showSmsPin;
	}

	public void setShowSmsPin(String showSmsPin) {
		this.showSmsPin = showSmsPin;
	}

	public String getConfirmSmsPin() {
		return confirmSmsPin;
	}

	public void setConfirmSmsPin(String confirmSmsPin) {
		this.confirmSmsPin = confirmSmsPin;
	}

	public String getLmsProfileId() {
		return lmsProfileId;
	}

	public void setLmsProfileId(String lmsProfileId) {
		this.lmsProfileId = lmsProfileId;
	}

	public String getLmsProfileListIdDesc() {
		return lmsProfileListIdDesc;
	}

	public void setLmsProfileListIdDesc(String lmsProfileListIdDesc) {
		this.lmsProfileListIdDesc = lmsProfileListIdDesc;
	}

	public ArrayList getLmsProfileList() {
		return lmsProfileList;
	}

	public void setLmsProfileList(ArrayList lmsProfileList) {
		this.lmsProfileList = lmsProfileList;
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

	public String getMsisdnForAssDeAss() {
		return msisdnForAssDeAss;
	}

	public void setMsisdnForAssDeAss(String msisdnForAssDeAss) {
		this.msisdnForAssDeAss = msisdnForAssDeAss;
	}

	public String getChanneluserType() {
		return channeluserType;
	}

	public void setChanneluserType(String channeluserType) {
		this.channeluserType = channeluserType;
	}

	public ArrayList getChannelUserTypeList() {
		return channelUserTypeList;
	}

	public void setChannelUserTypeList(ArrayList channelUserTypeList) {
		this.channelUserTypeList = channelUserTypeList;
	}

	public String getDownloadType() {
		return downloadType;
	}

	public void setDownloadType(String downloadType) {
		this.downloadType = downloadType;
	}

	public String getAssType() {
		return assType;
	}

	public void setAssType(String assType) {
		this.assType = assType;
	}

	public String getAssoMsisdn() {
		return assoMsisdn;
	}

	public void setAssoMsisdn(String assoMsisdn) {
		this.assoMsisdn = assoMsisdn;
	}

	public Date getAssociationCreatedOn() {
		return associationCreatedOn;
	}

	public void setAssociationCreatedOn(Date associationCreatedOn) {
		this.associationCreatedOn = associationCreatedOn;
	}

	public Date getAssociationModifiedOn() {
		return associationModifiedOn;
	}

	public void setAssociationModifiedOn(Date associationModifiedOn) {
		this.associationModifiedOn = associationModifiedOn;
	}

	public String getControlGroup() {
		return controlGroup;
	}

	public void setControlGroup(String controlGroup) {
		this.controlGroup = controlGroup;
	}

	public String getMainBal() {
		return mainBal;
	}

	public void setMainBal(String mainBal) {
		this.mainBal = mainBal;
	}

	public String getBonusBal() {
		return bonusBal;
	}

	public void setBonusBal(String bonusBal) {
		this.bonusBal = bonusBal;
	}

	public String getTotalBal() {
		return totalBal;
	}

	public void setTotalBal(String totalBal) {
		this.totalBal = totalBal;
	}

	public String getOldSmsPin() {
		return oldSmsPin;
	}

	public void setOldSmsPin(String oldSmsPin) {
		this.oldSmsPin = oldSmsPin;
	}

	public String getAllowedUserTypeCreation() {
		return allowedUserTypeCreation;
	}

	public void setAllowedUserTypeCreation(String allowedUserTypeCreation) {
		this.allowedUserTypeCreation = allowedUserTypeCreation;
	}
	
	public UserPhoneVO getMsisdnListIndexed(int i) {
        if (msisdnList == null) {
            return (new UserPhoneVO());
        }
        return (UserPhoneVO) msisdnList.get(i);
    }
	public void setMsisdnListIndexed(int i, UserPhoneVO vo) {
        msisdnList.set(i, vo);
    }
	
	
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

}
