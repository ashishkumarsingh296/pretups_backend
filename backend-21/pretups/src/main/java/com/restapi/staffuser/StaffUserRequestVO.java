package com.restapi.staffuser;

import java.util.Arrays;


import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class StaffUserRequestVO extends OAuthUser{
	
	
	@JsonProperty("data")
	StaffUserDetails staffUserDetailsdata = null;
	
	@JsonProperty("btchadmLoginInputData")
	BatchAdminInputParams batchInputParams; 
	
	

	public StaffUserDetails getStaffUserDetailsdata() {
		return staffUserDetailsdata;
	}

	public void setStaffUserDetailsdata(StaffUserDetails staffUserDetailsdata) {
		this.staffUserDetailsdata = staffUserDetailsdata;
	}

	public BatchAdminInputParams getBatchInputParams() {
		return batchInputParams;
	}

	public void setBatchInputParams(BatchAdminInputParams batchInputParams) {
		this.batchInputParams = batchInputParams;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StaffUserRequestVO [staffUserDetailsdata=");
		builder.append(staffUserDetailsdata);
		builder.append("]");
		return builder.toString();
	}

	
    

}

class StaffUserDetails{
	
	
	@JsonProperty("language")
	private String language;

	
	@JsonProperty("allowedip")
	private String allowedip;
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "en_US"/* , defaultValue = "" */, description="language")
	public String getLanguage() {
		return language;
	}

	@JsonProperty("designation")
	private  String designation;
	
	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}
    

	@io.swagger.v3.oas.annotations.media.Schema(example = "ADDUSER,INITVOMS,VIEWUSER", required = true/* , defaultValue = "" */, description= "Roles")
	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya"/* , defaultValue = "" */, description="User Name")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}



	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya"/* , defaultValue = "" */, description="Short Name")
	public String getShortName() {
		return shortName;
	}




	public void setShortName(String shortName) {
		this.shortName = shortName;
	}



	
	@io.swagger.v3.oas.annotations.media.Schema(example = "MR", required = true/* , defaultValue = "" */, description="User Name Prefix")
	public String getUserNamePrefix() {
		return userNamePrefix;
	}




	public void setUserNamePrefix(String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
	}




	@io.swagger.v3.oas.annotations.media.Schema(example = "11326"/* , defaultValue = "" */, description="Subscriber code")
	public String getSubscriberCode() {
		return subscriberCode;
	}




	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}


	
	@io.swagger.v3.oas.annotations.media.Schema(example = "72888818"/* , defaultValue = "" */, description="Contact Number")
	public String getContactNumber() {
		return contactNumber;
	}




	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}


	@io.swagger.v3.oas.annotations.media.Schema(example = "rohini"/* , defaultValue = "" */, description="Address line 1")
	public String getAddress1() {
		return address1;
	}




	public void setAddress1(String address1) {
		this.address1 = address1;
	}



	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ambala"/* , defaultValue = "" */, description="Address line 2")
	public String getAddress2() {
		return address2;
	}




	public void setAddress2(String address2) {
		this.address2 = address2;
	}



	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Delhi"/* , defaultValue = "" */, description="City")
	public String getCity() {
		return city;
	}




	public void setCity(String city) {
		this.city = city;
	}



	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DELHI"/* , defaultValue = "" */, description="State")
	public String getState() {
		return state;
	}




	public void setState(String state) {
		this.state = state;
	}



	
	@io.swagger.v3.oas.annotations.media.Schema(example = "INDIA"/* , defaultValue = "" */, description= "Country")
	public String getCountry() {
		return country;
	}




	public void setCountry(String country) {
		this.country = country;
	}



	
	@io.swagger.v3.oas.annotations.media.Schema(example = "arya1870@gmail.com", required = true/* , defaultValue = "" */, description="Email id")
	public String getEmailid() {
		return emailid;
	}




	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}



	
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya", required = true/* , defaultValue = "" */, description="Web login id")
	public String getWebloginid() {
		return webloginid;
	}




	public void setWebloginid(String webloginid) {
		this.webloginid = webloginid;
	}



	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */, description="Web password")
	public String getWebpassword() {
		return webpassword;
	}




	public void setWebpassword(String webpassword) {
		this.webpassword = webpassword;
	}




	@io.swagger.v3.oas.annotations.media.Schema(example = "20/04/20", description="Appointment Date")
	public String getAppointmentdate() {
		return appointmentdate;
	}




	public void setAppointmentdate(String appointmentdate) {
		this.appointmentdate = appointmentdate;
	}



	
	@io.swagger.v3.oas.annotations.media.Schema(example = "172.30.24.118"/* , defaultValue = "" */, description="Allowed ip")
	public String getAllowedip() {
		return allowedip;
	}




	public void setAllowedip(String allowedip) {
		this.allowedip = allowedip;
	}



	@io.swagger.v3.oas.annotations.media.Schema(example = "1,2,3", description="Allowed access days")
	public String getAlloweddays() {
		return alloweddays;
	}




	public void setAlloweddays(String alloweddays) {
		this.alloweddays = alloweddays;
	}






	@io.swagger.v3.oas.annotations.media.Schema(example = "EVD,PPB,RC,GRC", required = true/* , defaultValue = "" */, description="Services")
	public String getServices() {
		return services;
	}




	public void setServices(String services) {
		this.services = services;
	}


	@JsonProperty("firstName")
	private String firstName;

	@io.swagger.v3.oas.annotations.media.Schema(example = "rahul"/* , defaultValue = "" */, description= "First Name")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "arya"/* , defaultValue = "" */, description= "Last Name")
	public String getLastName() {
		return lastName;
	}




	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	@JsonProperty("lastName")
	private String lastName;

	@JsonProperty("userName")
	private String userName;
	
	@JsonProperty("shortName")
	private String shortName;
	
	@JsonProperty("userNamePrefix")
	private String userNamePrefix;
	
	@JsonProperty("subscriberCode")
	private String subscriberCode;
	
	@JsonProperty("contactNumber")
	private String contactNumber;

	
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
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */, description="Web password")
	public String getConfirmwebpassword() {
		return confirmwebpassword;
	}

	public void setConfirmwebpassword(String confirmwebpassword) {
		this.confirmwebpassword = confirmwebpassword;
	}

	@JsonProperty("msisdn")
	 Msisdn []msisdn;
	
	public Msisdn[] getMsisdn() {
		return msisdn;
	}




	public void setMsisdn(Msisdn[] msisdn) {
		this.msisdn = msisdn;
	}


	@JsonProperty("appointmentdate")
	private String appointmentdate;

	
	@JsonProperty("alloweddays")
	private String alloweddays;
	
	
	
	@JsonProperty("services")
	private String services;

	@JsonProperty("roles")
	private String roles;
	

	@io.swagger.v3.oas.annotations.media.Schema(example = "00:00"/* , defaultValue = "" */, description ="Allowed Starting Time")
	public String getAllowedTimeFrom() {
		return allowedTimeFrom;
	}

	public void setAllowedTimeFrom(String allowedTimeFrom) {
		this.allowedTimeFrom = allowedTimeFrom;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "23:59"/* , defaultValue = "" */, description= "Allowed Ending Time")
	public String getAllowedTimeTo() {
		return allowedTimeTo;
	}
	public void setAllowedTimeTo(String allowedTimeTo) {
		this.allowedTimeTo = allowedTimeTo;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StaffUserDetails [language=");
		builder.append(language);
		builder.append(", allowedip=");
		builder.append(allowedip);
		builder.append(", designation=");
		builder.append(designation);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", shortName=");
		builder.append(shortName);
		builder.append(", userNamePrefix=");
		builder.append(userNamePrefix);
		builder.append(", subscriberCode=");
		builder.append(subscriberCode);
		builder.append(", confirmwebpassword=");
		builder.append(confirmwebpassword);
		builder.append(", contactNumber=");
		builder.append(contactNumber);
		builder.append(", address1=");
		builder.append(address1);
		builder.append(", address2=");
		builder.append(address2);
		builder.append(", city=");
		builder.append(city);
		builder.append(", state=");
		builder.append(state);
		builder.append(", country=");
		builder.append(country);
		builder.append(", emailid=");
		builder.append(emailid);
		builder.append(", webloginid=");
		builder.append(webloginid);
		builder.append(", webpassword=");
		builder.append(webpassword);
		builder.append(", msisdn=");
		builder.append(Arrays.toString(msisdn));
		builder.append(", appointmentdate=");
		builder.append(appointmentdate);
		builder.append(", alloweddays=");
		builder.append(alloweddays);
		builder.append(", services=");
		builder.append(services);
		builder.append(", roles=");
		builder.append(roles);
		builder.append(", allowedTimeFrom=");
		builder.append(allowedTimeFrom);
		builder.append(", allowedTimeTo=");
		builder.append(allowedTimeTo);
		builder.append("]");
		return builder.toString();
	}

	@JsonProperty("allowedTimeFrom")
	private String allowedTimeFrom;
	
	@JsonProperty("allowedTimeTo")
	private String allowedTimeTo;
}

class Msisdn{
	
	@JsonProperty("phoneNo")
	String phoneNo;
	
	@JsonProperty("pin")
	String pin;
	
	@JsonProperty("confirmpin")
	String confirmpin;
	

	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getConfirmpin() {
		return confirmpin;
	}

	public void setConfirmpin(String confirmpin) {
		this.confirmpin = confirmpin;
	}


	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */)
	public String getIsprimary() {
		return isprimary;
	}

	public void setIsprimary(String isprimary) {
		this.isprimary = isprimary;
	}

	@JsonProperty("isprimary")
	String isprimary;


	@io.swagger.v3.oas.annotations.media.Schema(example = "72766677", required = true/* , defaultValue = "" */)
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	@JsonProperty("description")
	String description;

	@io.swagger.v3.oas.annotations.media.Schema(example = "phone no description", required = true/* , defaultValue = "" */)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}








