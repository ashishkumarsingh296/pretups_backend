package com.btsl.pretups.channeluser.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class Data {
	
	@JsonProperty("geographyCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */)
	public String getGeographyCode() {
		return geographyCode;
	}



	
	public void setGeographyCode(String geographyCode) {
		this.geographyCode = geographyCode;
	}



	@JsonProperty("parentMsisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72654676", required = true/* , defaultValue = "" */)
	public String getParentMsisdn() {
		return parentMsisdn;
	}




	public void setParentMsisdn(String parentMsisdn) {
		this.parentMsisdn = parentMsisdn;
	}



	@JsonProperty("parentExternalCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "11165", required = true/* , defaultValue = "" */)
	public String getParentExternalCode() {
		return parentExternalCode;
	}




	public void setParentExternalCode(String parentExternalCode) {
		this.parentExternalCode = parentExternalCode;
	}



	@JsonProperty("userCatCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */)
	public String getUserCatCode() {
		return userCatCode;
	}




	public void setUserCatCode(String userCatCode) {
		this.userCatCode = userCatCode;
	}



	@JsonProperty("userName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya", required = true/* , defaultValue = "" */)
	public String getUserName() {
		return userName;
	}




	public void setUserName(String userName) {
		this.userName = userName;
	}



	@JsonProperty("shortName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya", required = true/* , defaultValue = "" */)
	public String getShortName() {
		return shortName;
	}




	public void setShortName(String shortName) {
		this.shortName = shortName;
	}



	@JsonProperty("userNamePrefix")
	@io.swagger.v3.oas.annotations.media.Schema(example = "MR", required = true/* , defaultValue = "" */)
	public String getUserNamePrefix() {
		return userNamePrefix;
	}




	public void setUserNamePrefix(String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
	}



	@JsonProperty("subscriberCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "11326", required = true/* , defaultValue = "" */)
	public String getSubscriberCode() {
		return subscriberCode;
	}




	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}



	@JsonProperty("externalCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12232d", required = true/* , defaultValue = "" */)
	public String getExternalCode() {
		return externalCode;
	}




	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}



	@JsonProperty("contactPerson")
	@io.swagger.v3.oas.annotations.media.Schema(example = "RAHUL", required = true/* , defaultValue = "" */)
	public String getContactPerson() {
		return contactPerson;
	}




	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}



	@JsonProperty("contactNumber")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72888818", required = true/* , defaultValue = "" */)
	public String getContactNumber() {
		return contactNumber;
	}




	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}



	@JsonProperty("ssn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2321", required = true/* , defaultValue = "" */)
	public String getSsn() {
		return ssn;
	}




	public void setSsn(String ssn) {
		this.ssn = ssn;
	}



	@JsonProperty("address1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */)
	public String getAddress1() {
		return address1;
	}




	public void setAddress1(String address1) {
		this.address1 = address1;
	}



	@JsonProperty("address2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */)
	public String getAddress2() {
		return address2;
	}




	public void setAddress2(String address2) {
		this.address2 = address2;
	}



	@JsonProperty("city")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Delhi", required = true/* , defaultValue = "" */)
	public String getCity() {
		return city;
	}




	public void setCity(String city) {
		this.city = city;
	}



	@JsonProperty("state")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DELHI", required = true/* , defaultValue = "" */)
	public String getState() {
		return state;
	}




	public void setState(String state) {
		this.state = state;
	}



	@JsonProperty("country")
	@io.swagger.v3.oas.annotations.media.Schema(example = "INDIA", required = true/* , defaultValue = "" */)
	public String getCountry() {
		return country;
	}




	public void setCountry(String country) {
		this.country = country;
	}



	@JsonProperty("emailid")
	@io.swagger.v3.oas.annotations.media.Schema(example = "arya1870@gmail.com", required = true/* , defaultValue = "" */)
	public String getEmailid() {
		return emailid;
	}




	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}



	@JsonProperty("webloginid")
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya", required = true/* , defaultValue = "" */)
	public String getWebloginid() {
		return webloginid;
	}




	public void setWebloginid(String webloginid) {
		this.webloginid = webloginid;
	}



	@JsonProperty("webpassword")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getWebpassword() {
		return webpassword;
	}




	public void setWebpassword(String webpassword) {
		this.webpassword = webpassword;
	}



	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72876879", required = true/* , defaultValue = "" */)
	public Msisdn getMsisdn() {
		return msisdn;
	}




	public void setMsisdn(Msisdn msisdn) {
		this.msisdn = msisdn;
	}



	@JsonProperty("longitude")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
	public String getLongitude() {
		return longitude;
	}




	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}



	@JsonProperty("latitude")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
	public String getLatitude() {
		return latitude;
	}




	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}



	@JsonProperty("documentType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
	public String getDocumentType() {
		return documentType;
	}




	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}



	@JsonProperty("documentNo")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
	public String getDocumentNo() {
		return documentNo;
	}




	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}



	@JsonProperty("paymentType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
	public String getPaymentType() {
		return paymentType;
	}




	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}



	@JsonProperty("lowbalalertself")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */)
	public String getLowbalalertself() {
		return lowbalalertself;
	}




	public void setLowbalalertself(String lowbalalertself) {
		this.lowbalalertself = lowbalalertself;
	}



	@JsonProperty("lowbalalertparent")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */)
	public String getLowbalalertparent() {
		return lowbalalertparent;
	}




	public void setLowbalalertparent(String lowbalalertparent) {
		this.lowbalalertparent = lowbalalertparent;
	}



	@JsonProperty("lowbalalertother")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */)
	public String getLowbalalertother() {
		return lowbalalertother;
	}




	public void setLowbalalertother(String lowbalalertother) {
		this.lowbalalertother = lowbalalertother;
	}



	@JsonProperty("appointmentdate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20/4/20", required = true/* , defaultValue = "" */)
	public String getAppointmentdate() {
		return appointmentdate;
	}




	public void setAppointmentdate(String appointmentdate) {
		this.appointmentdate = appointmentdate;
	}



	@JsonProperty("allowedip")
	@io.swagger.v3.oas.annotations.media.Schema(example = "172.30.24.118", required = true/* , defaultValue = "" */)
	public String getAllowedip() {
		return allowedip;
	}




	public void setAllowedip(String allowedip) {
		this.allowedip = allowedip;
	}



	@JsonProperty("alloweddays")
	@io.swagger.v3.oas.annotations.media.Schema(example = "SU,MON", required = true/* , defaultValue = "" */)
	public String getAlloweddays() {
		return alloweddays;
	}




	public void setAlloweddays(String alloweddays) {
		this.alloweddays = alloweddays;
	}



	@JsonProperty("pin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getPin() {
		return pin;
	}




	public void setPin(String pin) {
		this.pin = pin;
	}



	@JsonProperty("insuspend")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */)
	public String getInsuspend() {
		return insuspend;
	}




	public void setInsuspend(String insuspend) {
		this.insuspend = insuspend;
	}



	@JsonProperty("outsuspend")
	@io.swagger.v3.oas.annotations.media.Schema(example = "N", required = true/* , defaultValue = "" */)
	public String getOutsuspend() {
		return outsuspend;
	}




	public void setOutsuspend(String outsuspend) {
		this.outsuspend = outsuspend;
	}



	@JsonProperty("usergrade")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */)
	public String getUsergrade() {
		return usergrade;
	}




	public void setUsergrade(String usergrade) {
		this.usergrade = usergrade;
	}



	@JsonProperty("services")
	@io.swagger.v3.oas.annotations.media.Schema(example = "EVD,PPB,RC,GRC", required = true/* , defaultValue = "" */)
	public String getServices() {
		return services;
	}




	public void setServices(String services) {
		this.services = services;
	}



	@JsonProperty("grouprole")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ADDUSER,INITVOMS,VIEWUSER", required = true/* , defaultValue = "" */)
	public String getGrouprole() {
		return grouprole;
	}




	public void setGrouprole(String grouprole) {
		this.grouprole = grouprole;
	}




	@JsonProperty("geographyCode")
	private String geographyCode;
	
	@JsonProperty("parentMsisdn")
	private String parentMsisdn;
	
	@JsonProperty("parentExternalCode")
	private String parentExternalCode;
	
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
	
	@JsonProperty("msisdn")
	private Msisdn msisdn;
	
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
	
	@JsonProperty("pin")
	private String pin;
	
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
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
	public String getFax() {
		return fax;
	}


	public void setFax(String fax) {
		this.fax = fax;
	}

	@JsonProperty("company")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Comviva", required = true/* , defaultValue = "" */)
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}

	@JsonProperty("company")
	private String company;
	@JsonProperty("grouprole")
	private String grouprole;
	
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
	public String getExtnwcode() {
		return extnwcode;
	}

	@JsonProperty("empcode")
	private String empcode;


	@JsonProperty("empcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12728", required = true/* , defaultValue = "" */)
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
	@io.swagger.v3.oas.annotations.media.Schema(example = "20/04/20", required = true/* , defaultValue = "" */)
	public String getAllowedTimeFrom() {
		return allowedTimeFrom;
	}

	public void setAllowedTimeFrom(String allowedTimeFrom) {
		this.allowedTimeFrom = allowedTimeFrom;
	}
	@JsonProperty("allowedTimeTo")
	@io.swagger.v3.oas.annotations.media.Schema(example = "20/04/20", required = true/* , defaultValue = "" */)
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

	public class Msisdn{
		String primarymsisdn;
		@JsonProperty("primarymsisdn")
		@io.swagger.v3.oas.annotations.media.Schema(example = "72766677", required = true/* , defaultValue = "" */)
		public String getPrimarymsisdn() {
			return primarymsisdn;
		}
		public void setPrimarymsisdn(String primarymsisdn) {
			this.primarymsisdn = primarymsisdn;
		}
		@JsonProperty("msisdn2")
		@io.swagger.v3.oas.annotations.media.Schema(example = "7276666688", required = true/* , defaultValue = "" */)
		public String getMsisdn2() {
			return msisdn2;
		}
		public void setMsisdn2(String msisdn2) {
			this.msisdn2 = msisdn2;
		}
		@JsonProperty("msisdn3")
		@io.swagger.v3.oas.annotations.media.Schema(example = "7299937463", required = true/* , defaultValue = "" */)
		public String getMsisdn3() {
			return msisdn3;
		}
		public void setMsisdn3(String msisdn3) {
			this.msisdn3 = msisdn3;
		}
		String msisdn2;
		String msisdn3;
		
	}
	


}
