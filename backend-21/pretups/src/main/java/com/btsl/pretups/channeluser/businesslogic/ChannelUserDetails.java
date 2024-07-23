package com.btsl.pretups.channeluser.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ChannelUserDetails {
	
	
	@JsonProperty("authTypeAllowed")
	private String authTypeAllowed;

	public String getAuthTypeAllowed() {
		return authTypeAllowed;
	}

	public void setAuthTypeAllowed(String authTypeAllowed) {
		this.authTypeAllowed = authTypeAllowed;
	}

	@JsonProperty("domain")
	private String domain;
	
	@JsonProperty("oldLogin")
	private String oldLogin;
	
	public String getOldLogin() {
		return oldLogin;
	}

	public void setOldLogin(String oldLogin) {
		this.oldLogin = oldLogin;
	}

	@JsonProperty("domain")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */,description="Domain")
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}


	@JsonProperty("geographicalDomain")
	private String geographicalDomain;
	
	@JsonProperty("geographicalDomain")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DELHI", required = true/* , defaultValue = "" */,description="Geographical Domain")
	public String getGeographicalDomain() {
		return geographicalDomain;
	}

	public void setGeographicalDomain(String geographicalDomain) {
		this.geographicalDomain = geographicalDomain;
	}


	@JsonProperty("language")
	private String language;
	
	@JsonProperty("language")
	@io.swagger.v3.oas.annotations.media.Schema(example = "en_us", required = true/* , defaultValue = "" */, description="language")
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}


	@JsonProperty("parentCategory")
	private String parentCategory;
	@JsonProperty("parentCategory")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */, description="Parent Category")
	public String getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}
	@JsonProperty("parentUser")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */, description="Parent User")
	public String getParentUser() {
		return parentUser;
	}

	public void setParentUser(String parentUser) {
		this.parentUser = parentUser;
	}


	@JsonProperty("parentUser")
	private String parentUser;
	@JsonProperty("ownerUser")
	private String ownerUser;
	@JsonProperty("ownerUser")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Appdist", required = true/* , defaultValue = "" */, description="Owner User")
	public String getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(String ownerUser) {
		this.ownerUser = ownerUser;
	}

	@JsonProperty("geographyCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */, description="Geography Code")
	public String getGeographyCode() {
		return geographyCode;
	}



	
	public void setGeographyCode(String geographyCode) {
		this.geographyCode = geographyCode;
	}


	@JsonProperty("userCatCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */, description="User Category code")
	public String getUserCatCode() {
		return userCatCode;
	}




	public void setUserCatCode(String userCatCode) {
		this.userCatCode = userCatCode;
	}



	@JsonProperty("userName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya", required = true/* , defaultValue = "" */, description="User Name")
	public String getUserName() {
		return userName;
	}




	public void setUserName(String userName) {
		this.userName = userName;
	}



	@JsonProperty("shortName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya", required = true/* , defaultValue = "" */, description="Short Name")
	public String getShortName() {
		return shortName;
	}




	public void setShortName(String shortName) {
		this.shortName = shortName;
	}



	@JsonProperty("userNamePrefix")
	@io.swagger.v3.oas.annotations.media.Schema(example = "MR", required = true/* , defaultValue = "" */, description="User Name Prefix")
	public String getUserNamePrefix() {
		return userNamePrefix;
	}




	public void setUserNamePrefix(String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
	}



	@JsonProperty("subscriberCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "11326", required = true/* , defaultValue = "" */, description="Subscriber code")
	public String getSubscriberCode() {
		return subscriberCode;
	}




	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}



	@JsonProperty("externalCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12232d", required = true/* , defaultValue = "" */, description="External code")
	public String getExternalCode() {
		return externalCode;
	}




	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}



	@JsonProperty("contactPerson")
	@io.swagger.v3.oas.annotations.media.Schema(example = "RAHUL", required = true/* , defaultValue = "" */, description="Contact Person")
	public String getContactPerson() {
		return contactPerson;
	}




	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}



	@JsonProperty("contactNumber")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72888818", required = true/* , defaultValue = "" */, description="Contact Number")
	public String getContactNumber() {
		return contactNumber;
	}




	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}



	@JsonProperty("ssn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2321", required = true/* , defaultValue = "" */, description="SSN")
	public String getSsn() {
		return ssn;
	}




	public void setSsn(String ssn) {
		this.ssn = ssn;
	}



	@JsonProperty("address1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "rohini", required = true/* , defaultValue = "" */, description="Address line 1")
	public String getAddress1() {
		return address1;
	}




	public void setAddress1(String address1) {
		this.address1 = address1;
	}



	@JsonProperty("address2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ambala", required = true/* , defaultValue = "" */, description="Address line 2")
	public String getAddress2() {
		return address2;
	}




	public void setAddress2(String address2) {
		this.address2 = address2;
	}



	@JsonProperty("city")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Delhi", required = true/* , defaultValue = "" */, description="City")
	public String getCity() {
		return city;
	}




	public void setCity(String city) {
		this.city = city;
	}



	@JsonProperty("state")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DELHI", required = true/* , defaultValue = "" */, description="State")
	public String getState() {
		return state;
	}




	public void setState(String state) {
		this.state = state;
	}



	@JsonProperty("country")
	@io.swagger.v3.oas.annotations.media.Schema(example = "INDIA", required = true/* , defaultValue = "" */, description= "Country")
	public String getCountry() {
		return country;
	}




	public void setCountry(String country) {
		this.country = country;
	}



	@JsonProperty("emailid")
	@io.swagger.v3.oas.annotations.media.Schema(example = "arya1870@gmail.com", required = true/* , defaultValue = "" */, description="Email id")
	public String getEmailid() {
		return emailid;
	}




	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}



	@JsonProperty("webloginid")
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya", required = true/* , defaultValue = "" */, description="Web login id")
	public String getWebloginid() {
		return webloginid;
	}




	public void setWebloginid(String webloginid) {
		this.webloginid = webloginid;
	}


	@io.swagger.v3.oas.annotations.media.Schema(example = "5359410680b3a555", required = true/* , defaultValue = "" */, description="Web password")
	public String getWebpassword() {
		return webpassword;
	}




	public void setWebpassword(String webpassword) {
		this.webpassword = webpassword;
	}






	@JsonProperty("longitude")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */, description="Longitude")
	public String getLongitude() {
		return longitude;
	}




	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}



	@JsonProperty("latitude")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */, description="Latitude")
	public String getLatitude() {
		return latitude;
	}




	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}



	@JsonProperty("documentType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AADHAR", required = true/* , defaultValue = "" */, description="Document Type")
	public String getDocumentType() {
		return documentType;
	}




	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}



	@JsonProperty("documentNo")
	@io.swagger.v3.oas.annotations.media.Schema(example = "545646456546", required = true/* , defaultValue = "" */, description= "Document no.")
	public String getDocumentNo() {
		return documentNo;
	}




	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}



	@JsonProperty("paymentType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DD,CHQ,OTH,CASH,ONLINE", required = true/* , defaultValue = "" */, description="Payment Type")
	public String getPaymentType() {
		return paymentType;
	}




	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}



	@JsonProperty("lowbalalertself")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */, description="Low Balance alert to self")
	public String getLowbalalertself() {
		return lowbalalertself;
	}




	public void setLowbalalertself(String lowbalalertself) {
		this.lowbalalertself = lowbalalertself;
	}



	@JsonProperty("lowbalalertparent")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */, description="Low Balance alert to parent")
	public String getLowbalalertparent() {
		return lowbalalertparent;
	}




	public void setLowbalalertparent(String lowbalalertparent) {
		this.lowbalalertparent = lowbalalertparent;
	}



	@JsonProperty("lowbalalertother")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */, description= "Low Balance alert to others")
	public String getLowbalalertother() {
		return lowbalalertother;
	}




	public void setLowbalalertother(String lowbalalertother) {
		this.lowbalalertother = lowbalalertother;
	}



	@JsonProperty("appointmentdate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20/4/20", required = true/* , defaultValue = "" */, description="Appointment Date")
	public String getAppointmentdate() {
		return appointmentdate;
	}




	public void setAppointmentdate(String appointmentdate) {
		this.appointmentdate = appointmentdate;
	}



	@JsonProperty("allowedip")
	@io.swagger.v3.oas.annotations.media.Schema(example = "172.30.24.118", required = true/* , defaultValue = "" */, description="Allowed ip")
	public String getAllowedip() {
		return allowedip;
	}




	public void setAllowedip(String allowedip) {
		this.allowedip = allowedip;
	}



	@JsonProperty("alloweddays")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1,2,3", required = true/* , defaultValue = "" */, description="Allowed access days")
	public String getAlloweddays() {
		return alloweddays;
	}




	public void setAlloweddays(String alloweddays) {
		this.alloweddays = alloweddays;
	}

	@JsonProperty("insuspend")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */, description="Insuspend")
	public String getInsuspend() {
		return insuspend;
	}




	public void setInsuspend(String insuspend) {
		this.insuspend = insuspend;
	}



	@JsonProperty("outsuspend")
	@io.swagger.v3.oas.annotations.media.Schema(example = "N", required = true/* , defaultValue = "" */, description="Outsuspend")
	public String getOutsuspend() {
		return outsuspend;
	}




	public void setOutsuspend(String outsuspend) {
		this.outsuspend = outsuspend;
	}



	@JsonProperty("usergrade")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */, description="User grade")
	public String getUsergrade() {
		return usergrade;
	}




	public void setUsergrade(String usergrade) {
		this.usergrade = usergrade;
	}



	@JsonProperty("services")
	@io.swagger.v3.oas.annotations.media.Schema(example = "EVD,PPB,RC,GRC", required = true/* , defaultValue = "" */, description="Services")
	public String getServices() {
		return services;
	}




	public void setServices(String services) {
		this.services = services;
	}



	@JsonProperty("roles")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ADDUSER,INITVOMS,VIEWUSER", required = true/* , defaultValue = "" */, description= "Roles")
	public String getGrouprole() {
		return roles;
	}




	public void setGrouprole(String roles) {
		this.roles = roles;
	}


	@JsonProperty("firstName")
	private String firstName;
	
	@JsonProperty("firstName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "rahul", required = true/* , defaultValue = "" */, description= "First Name")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	@JsonProperty("lastName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "arya", required = true/* , defaultValue = "" */, description= "Last Name")
	public String getLastName() {
		return lastName;
	}




	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	@JsonProperty("lastName")
	private String lastName;

	@JsonProperty("designation")
	private String designation;
	
	@JsonProperty("designation")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Manager", required = true/* , defaultValue = "" */, description= "Designation")
	public String getDesignation() {
		return designation;
	}




	public void setDesignation(String designation) {
		this.designation = designation;
	}


	@JsonProperty("geographyCode")
	private String geographyCode;
	
	
	@JsonProperty("userCatCode")
	private String userCatCode;
	
	@JsonProperty("userName")
	private String userName;
	
	@JsonProperty("shortName")
	private String shortName;
	
	@JsonProperty("userNamePrefix")
	private String userNamePrefix;
	
	@JsonProperty("subscriberCode")
	private String subscriberCode;

	@JsonProperty("externalCode")
	private String externalCode;
	
	@JsonProperty("contactPerson")
	private String contactPerson;
	
	@JsonProperty("contactNumber")
	private String contactNumber;
	
	@JsonProperty("ssn")
	private String ssn;
	
	@JsonProperty("address1")
	private String address1;
	
	@JsonProperty("address2")
	private String address2;
	
	@JsonProperty("city")
	private String city;

	@JsonProperty("state")
	private String state;
	
	@JsonProperty("country")
	private String country;
	
	@JsonProperty("emailid")
	private String emailid;
	
	@JsonProperty("webloginid")
	private String webloginid;
	
	@JsonProperty("webpassword")
	private String webpassword;
	
	
	@JsonProperty("confirmwebpassword")
	private String confirmwebpassword;
	
	public String getConfirmwebpassword() {
		return confirmwebpassword;
	}

	public void setConfirmwebpassword(String confirmwebpassword) {
		this.confirmwebpassword = confirmwebpassword;
	}

	@JsonProperty("msisdn")
	 Msisdn []msisdn;
	
	@JsonProperty("msisdn")
	public Msisdn[] getMsisdn() {
		return msisdn;
	}




	public void setMsisdn(Msisdn[] msisdn) {
		this.msisdn = msisdn;
	}




	@JsonProperty("longitude")
	private String longitude;

	@JsonProperty("latitude")
	private String latitude;
	
	@JsonProperty("documentType")
	private String documentType;
	
	@JsonProperty("documentNo")
	private String documentNo;
	
	@JsonProperty("paymentType")
	private String paymentType;
	
	@JsonProperty("lowbalalertself")
	private String lowbalalertself;
	
	@JsonProperty("lowbalalertparent")
	private String lowbalalertparent;

	@JsonProperty("lowbalalertother")
	private String lowbalalertother;
	
	@JsonProperty("appointmentdate")
	private String appointmentdate;
	
	@JsonProperty("allowedip")
	private String allowedip;
	
	@JsonProperty("alloweddays")
	private String alloweddays;
	
	@JsonProperty("insuspend")
	private String insuspend;
	
	@JsonProperty("outsuspend")
	private String outsuspend;
	
	@JsonProperty("usergrade")
	private String usergrade;
	
	@JsonProperty("services")
	private String services;
	
	@JsonProperty("fax")
	private String fax;
	@JsonProperty("fax")
	@io.swagger.v3.oas.annotations.media.Schema(example = "3131", required = true/* , defaultValue = "" */, description= "Fax")
	public String getFax() {
		return fax;
	}


	public void setFax(String fax) {
		this.fax = fax;
	}

	@JsonProperty("company")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Comviva", required = true/* , defaultValue = "" */, description= "Company")
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}

	@JsonProperty("company")
	private String company;
	@JsonProperty("roles")
	private String roles;
	
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */, description= "External Network Code")
	public String getExtnwcode() {
		return extnwcode;
	}

	@JsonProperty("empcode")
	private String empcode;


	@JsonProperty("empcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12728", required = true/* , defaultValue = "" */, description= "Emp. Code")
	public String getEmpcode() {
		return empcode;
	}




	public void setEmpcode(String empcode) {
		this.empcode = empcode;
	}




	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}
	@JsonProperty("allowedTimeFrom")
	@io.swagger.v3.oas.annotations.media.Schema(example = "00:00", required = true/* , defaultValue = "" */, description ="Allowed Starting Time")
	public String getAllowedTimeFrom() {
		return allowedTimeFrom;
	}

	public void setAllowedTimeFrom(String allowedTimeFrom) {
		this.allowedTimeFrom = allowedTimeFrom;
	}
	@JsonProperty("allowedTimeTo")
	@io.swagger.v3.oas.annotations.media.Schema(example = "00:00", required = true/* , defaultValue = "" */, description= "Allowed Ending Time")
	public String getAllowedTimeTo() {
		return allowedTimeTo;
	}
	public void setAllowedTimeTo(String allowedTimeTo) {
		this.allowedTimeTo = allowedTimeTo;
	}
	@JsonProperty("allowedTimeFrom")
	private String allowedTimeFrom;
	
	@JsonProperty("allowedTimeTo")
	private String allowedTimeTo;

	
	@JsonProperty("voucherTypes")
	private String voucherTypes;

	@JsonProperty("voucherTypes")
	@io.swagger.v3.oas.annotations.media.Schema(example = "digital,physical", required = true/* , defaultValue = "" */, description= "VoucherTypes")
	public String getVoucherTypes() {
		return voucherTypes;
	}




	public void setVoucherTypes(String voucherTypes) {
		this.voucherTypes = voucherTypes;
	}
	
	@JsonProperty("commissionProfileID")
	private String commissionProfileID;
	
	
	@JsonProperty("commissionProfileID")
	@io.swagger.v3.oas.annotations.media.Schema(example = "13223", required = true/* , defaultValue = "" */, description= "Commission Profile Id")
	public String getCommissionProfileID() {
		return commissionProfileID;
	}

	public void setCommissionProfileID(String commissionProfileID) {
		this.commissionProfileID = commissionProfileID;
	}

	
	@JsonProperty("transferRuleType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "C2CTRFRUL1", required = true/* , defaultValue = "" */, description= "Transfer Rule Type")
	public String getTransferRuleType() {
		return transferRuleType;
	}

	public void setTransferRuleType(String transferRuleType) {
		this.transferRuleType = transferRuleType;
	}

	
	@JsonProperty("transferProfile")
	@io.swagger.v3.oas.annotations.media.Schema(example = "556", required = true/* , defaultValue = "" */, description= "Transfer Profile")
	public String getTransferProfile() {
		return transferProfile;
	}

	public void setTransferProfile(String transferProfile) {
		this.transferProfile = transferProfile;
	}

	@JsonProperty("transferRuleType")
	private String transferRuleType;
	
	
	@JsonProperty("transferProfile")
	private String transferProfile;

	@JsonProperty("roleType")
	private String roleType;

	@JsonProperty("roleType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */, description= "Role Type")
	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	
	@JsonProperty("userCode")
	private String userCode;

	@JsonProperty("userCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72762778", required = true/* , defaultValue = "" */, description = "User Code")
	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	
	@JsonProperty("controlGroup")
	private String controlGroup;
	
	@JsonProperty("controlGroup")
	@io.swagger.v3.oas.annotations.media.Schema(example = "N", required = true/* , defaultValue = "" */, description = "Control Group")
	public String getControlGroup() {
		return controlGroup;
	}


	public void setControlGroup(String controlGroup) {
		this.controlGroup = controlGroup;
	}


	@JsonProperty("lmsProfileId")
	private String lmsProfileId;

	@JsonProperty("lmsProfileId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "122", required = true/* , defaultValue = "" */, description = "Lms Profile Id")
	public String getLmsProfileId() {
		return lmsProfileId;
	}

	public void setLmsProfileId(String lmsProfileId) {
		this.lmsProfileId = lmsProfileId;
	}
	
	@JsonProperty("outletCode")
	private String outletCode;
	
	@JsonProperty("outletCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "TCOM", required = true/* , defaultValue = "" */, description = "Outlet Code")
	public String getOutletCode() {
		return outletCode;
	}


	public void setOutletCode(String outletCode) {
		this.outletCode = outletCode;
	}


	@JsonProperty("subOutletCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "SL026", required = true/* , defaultValue = "" */, description = "Sub Outlet Code")
	public String getSubOutletCode() {
		return subOutletCode;
	}




	public void setSubOutletCode(String subOutletCode) {
		this.subOutletCode = subOutletCode;
	}


	@JsonProperty("subOutletCode")
	private String subOutletCode;
	
	@JsonProperty("otherEmail")
	private String otherEmail;

	@JsonProperty("otherEmail")
	@io.swagger.v3.oas.annotations.media.Schema(example = "chetan.chawka@gmail.com", required = true/* , defaultValue = "" */, description = "Other Email")
	public String getOtherEmail() {
		return otherEmail;
	}

	public void setOtherEmail(String otherEmail) {
		this.otherEmail = otherEmail;
	}

	@JsonProperty("voucherSegments")
	private String voucherSegments;

	@JsonProperty("voucherSegments")
	@io.swagger.v3.oas.annotations.media.Schema(example = "LC,NL", required = true/* , defaultValue = "" */, description= "voucherSegments")
	public String getVoucherSegments() {
		return voucherSegments;
	}

	public void setVoucherSegments(String voucherSegments) {
		this.voucherSegments = voucherSegments;
	}

	@JsonProperty("userProducts")
	private String userProducts;

	@JsonProperty("userProducts")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ETOPUP,POSTETOPUP", required = true/* , defaultValue = "" */, description= "User Products")
	public String getUserProducts() {
		return userProducts;
	}

	public void setUserProducts(String userProducts) {
		this.userProducts = userProducts;
	}

	@JsonProperty("userDomainCodes")
	private String userDomainCodes;

	@JsonProperty("userDomainCodes")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ETOPUP,POSTETOPUP", required = true/* , defaultValue = "" */, description= "User Domains")
	public String getUserDomainCodes() {
		return userDomainCodes;
	}

	public void setUserDomainCodes(String userDomainCodes) {
		this.userDomainCodes = userDomainCodes;
	}
	
	
	@JsonProperty("departmentCode")
	private String departmentCode;

	@JsonProperty("departmentCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUT102,AUT232", required = true/* , defaultValue = "" */, description= "Department code")
	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	
	
	@JsonProperty("divisionCode")
	private String divisionCode;

	@JsonProperty("divisionCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIV101,DIV102", required = true/* , defaultValue = "" */, description= "Division code")
	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	
	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("mobileNumber")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72343434", required = true/* , defaultValue = "" */, description= "Mobile number")
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	 
	@JsonProperty("multipleGeographyLoc")
	private String multipleGeographyLoc;

	@JsonProperty("multipleGeographyLoc")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72343434", required = true/* , defaultValue = "" */, description= "Multiple Geographic locations")
	public String getMultipleGeographyLoc() {
		return multipleGeographyLoc;
	}

	public void setMultipleGeographyLoc(String multipleGeographyLoc) {
		this.multipleGeographyLoc = multipleGeographyLoc;
	}

	

	
		
	

}
