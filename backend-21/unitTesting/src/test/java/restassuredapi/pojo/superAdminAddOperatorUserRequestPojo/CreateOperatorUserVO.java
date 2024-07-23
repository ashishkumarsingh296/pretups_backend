package restassuredapi.pojo.superAdminAddOperatorUserRequestPojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateOperatorUserVO {

	private String address1;
	private String address2;
	private String allowedTimeFrom;
	private String allowedTimeTo;
	private String alloweddays;
	private String allowedip;
	private String appointmentdate;
	private String city;
	private String commissionProfileID;
	private String company;
	private String contactNumber;
	private String contactPerson;
	private String controlGroup;
	private String country;
	private String departmentCode;
	private String designation;
	private String divisionCode;
	private String documentNo;
	private String documentType;
	private String domain;
	private String emailid;
	private String empcode;
	private String externalCode;
	private String extnwcode;
	private String fax;
	private String firstName;
	private String geographicalDomain;
	private String geographyCode;
	private String insuspend;
	private Object language;
	private String lastName;
	private String latitude;
	private String lmsProfileId;
	private String longitude;
	private String lowbalalertother;
	private String lowbalalertparent;
	private String lowbalalertself;
	private String mobileNumber;
	private ArrayList<CreateOperatorUserMsisdn> msisdn;
	private String multipleGeographyLoc;
	private String otherEmail;
	private String outletCode;
	private String subOutletCode;
	private String outsuspend;
	private String parentUser;
	private String paymentType;
	private String roleType;
	private String roles;
	private String services;
	private String shortName;
	private String ssn;
	private String state;
	private String subscriberCode;
	private String transferProfile;
	private String transferRuleType;
	private String userCatCode;
	private String userCode;
	private String userDomainCodes;
	private String parentCategory;
	private String userName;
	private String userNamePrefix;
	private String voucherSegments;
	private String voucherTypes;
	private String webloginid;
	private String webpassword;
	private String confirmwebpassword;
	private String usergrade;
	private String userProducts;
	private String ownerUser;
	
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
	public String getAllowedTimeFrom() {
		return allowedTimeFrom;
	}
	public void setAllowedTimeFrom(String allowedTimeFrom) {
		this.allowedTimeFrom = allowedTimeFrom;
	}
	public String getAllowedTimeTo() {
		return allowedTimeTo;
	}
	public void setAllowedTimeTo(String allowedTimeTo) {
		this.allowedTimeTo = allowedTimeTo;
	}
	public String getAlloweddays() {
		return alloweddays;
	}
	public void setAlloweddays(String alloweddays) {
		this.alloweddays = alloweddays;
	}
	public String getAllowedip() {
		return allowedip;
	}
	public void setAllowedip(String allowedip) {
		this.allowedip = allowedip;
	}
	public String getAppointmentdate() {
		return appointmentdate;
	}
	public void setAppointmentdate(String appointmentdate) {
		this.appointmentdate = appointmentdate;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCommissionProfileID() {
		return commissionProfileID;
	}
	public void setCommissionProfileID(String commissionProfileID) {
		this.commissionProfileID = commissionProfileID;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public String getControlGroup() {
		return controlGroup;
	}
	public void setControlGroup(String controlGroup) {
		this.controlGroup = controlGroup;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
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
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getEmailid() {
		return emailid;
	}
	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}
	public String getEmpcode() {
		return empcode;
	}
	public void setEmpcode(String empcode) {
		this.empcode = empcode;
	}
	public String getExternalCode() {
		return externalCode;
	}
	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}
	public String getExtnwcode() {
		return extnwcode;
	}
	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
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
	public String getGeographicalDomain() {
		return geographicalDomain;
	}
	public void setGeographicalDomain(String geographicalDomain) {
		this.geographicalDomain = geographicalDomain;
	}
	public String getGeographyCode() {
		return geographyCode;
	}
	public void setGeographyCode(String geographyCode) {
		this.geographyCode = geographyCode;
	}
	public String getInsuspend() {
		return insuspend;
	}
	public void setInsuspend(String insuspend) {
		this.insuspend = insuspend;
	}
	public Object getLanguage() {
		return language;
	}
	public void setLanguage(Object language) {
		this.language = language;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLmsProfileId() {
		return lmsProfileId;
	}
	public void setLmsProfileId(String lmsProfileId) {
		this.lmsProfileId = lmsProfileId;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLowbalalertother() {
		return lowbalalertother;
	}
	public void setLowbalalertother(String lowbalalertother) {
		this.lowbalalertother = lowbalalertother;
	}
	public String getLowbalalertparent() {
		return lowbalalertparent;
	}
	public void setLowbalalertparent(String lowbalalertparent) {
		this.lowbalalertparent = lowbalalertparent;
	}
	public String getLowbalalertself() {
		return lowbalalertself;
	}
	public void setLowbalalertself(String lowbalalertself) {
		this.lowbalalertself = lowbalalertself;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public ArrayList<CreateOperatorUserMsisdn> getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(ArrayList<CreateOperatorUserMsisdn> msisdn) {
		this.msisdn = msisdn;
	}
	public String getMultipleGeographyLoc() {
		return multipleGeographyLoc;
	}
	public void setMultipleGeographyLoc(String multipleGeographyLoc) {
		this.multipleGeographyLoc = multipleGeographyLoc;
	}
	public String getOtherEmail() {
		return otherEmail;
	}
	public void setOtherEmail(String otherEmail) {
		this.otherEmail = otherEmail;
	}
	public String getOutletCode() {
		return outletCode;
	}
	public void setOutletCode(String outletCode) {
		this.outletCode = outletCode;
	}
	public String getSubOutletCode() {
		return subOutletCode;
	}
	public void setSubOutletCode(String subOutletCode) {
		this.subOutletCode = subOutletCode;
	}
	public String getOutsuspend() {
		return outsuspend;
	}
	public void setOutsuspend(String outsuspend) {
		this.outsuspend = outsuspend;
	}
	public String getParentUser() {
		return parentUser;
	}
	public void setParentUser(String parentUser) {
		this.parentUser = parentUser;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getServices() {
		return services;
	}
	public void setServices(String services) {
		this.services = services;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getSubscriberCode() {
		return subscriberCode;
	}
	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}
	public String getTransferProfile() {
		return transferProfile;
	}
	public void setTransferProfile(String transferProfile) {
		this.transferProfile = transferProfile;
	}
	public String getTransferRuleType() {
		return transferRuleType;
	}
	public void setTransferRuleType(String transferRuleType) {
		this.transferRuleType = transferRuleType;
	}
	public String getUserCatCode() {
		return userCatCode;
	}
	public void setUserCatCode(String userCatCode) {
		this.userCatCode = userCatCode;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getUserDomainCodes() {
		return userDomainCodes;
	}
	public void setUserDomainCodes(String userDomainCodes) {
		this.userDomainCodes = userDomainCodes;
	}
	public String getParentCategory() {
		return parentCategory;
	}
	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNamePrefix() {
		return userNamePrefix;
	}
	public void setUserNamePrefix(String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
	}
	public String getVoucherSegments() {
		return voucherSegments;
	}
	public void setVoucherSegments(String voucherSegments) {
		this.voucherSegments = voucherSegments;
	}
	public String getVoucherTypes() {
		return voucherTypes;
	}
	public void setVoucherTypes(String voucherTypes) {
		this.voucherTypes = voucherTypes;
	}
	public String getWebloginid() {
		return webloginid;
	}
	public void setWebloginid(String webloginid) {
		this.webloginid = webloginid;
	}
	public String getWebpassword() {
		return webpassword;
	}
	public void setWebpassword(String webpassword) {
		this.webpassword = webpassword;
	}
	public String getConfirmwebpassword() {
		return confirmwebpassword;
	}
	public void setConfirmwebpassword(String confirmwebpassword) {
		this.confirmwebpassword = confirmwebpassword;
	}
	public String getUsergrade() {
		return usergrade;
	}
	public void setUsergrade(String usergrade) {
		this.usergrade = usergrade;
	}
	public String getUserProducts() {
		return userProducts;
	}
	public void setUserProducts(String userProducts) {
		this.userProducts = userProducts;
	}
	public String getOwnerUser() {
		return ownerUser;
	}
	public void setOwnerUser(String ownerUser) {
		this.ownerUser = ownerUser;
	}

}
