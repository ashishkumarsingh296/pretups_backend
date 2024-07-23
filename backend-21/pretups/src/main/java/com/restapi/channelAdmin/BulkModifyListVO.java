package com.restapi.channelAdmin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

public class BulkModifyListVO {
	
	private String userID;
	private String userNamePrefix;
	private String firstName;
	private String lastName;
	private String webLoginID;
	private String webLoginPassword;
	private String mobileNumber;
	private String pin;
	private String geoDomainCode;
	private String groupRoleAllowed;
	private String roleCode;
	private String services;
	private String shortName;
	private String subscriberCode;
	private String externalCode;
	private String inSuspend;
	private String outSuspend;
	private String contactPerson;
	private String contactNumber;
	private String rsaSecureID;
	private String designation;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private String company;
	private String fax;
	private String language;
	private String email;
	private String allowLowBalAlert;
	private String trfRuleCode;
	private String longitude;
	private String latitude;
	private String documentType;
	private String documentNo;
	private String paymentType;
	private String commProfile;
	private String trfProfile;
	private String grade;
	private String voucherType;
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserNamePrefix() {
		return userNamePrefix;
	}
	public void setUserNamePrefix(String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
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
	public String getWebLoginID() {
		return webLoginID;
	}
	public void setWebLoginID(String webLoginID) {
		this.webLoginID = webLoginID;
	}
	public String getWebLoginPassword() {
		return webLoginPassword;
	}
	public void setWebLoginPassword(String webLoginPassword) {
		this.webLoginPassword = webLoginPassword;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getGeoDomainCode() {
		return geoDomainCode;
	}
	public void setGeoDomainCode(String geoDomainCode) {
		this.geoDomainCode = geoDomainCode;
	}
	public String getGroupRoleAllowed() {
		return groupRoleAllowed;
	}
	public void setGroupRoleAllowed(String groupRoleAllowed) {
		this.groupRoleAllowed = groupRoleAllowed;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
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
	public String getSubscriberCode() {
		return subscriberCode;
	}
	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}
	public String getExternalCode() {
		return externalCode;
	}
	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}
	public String getInSuspend() {
		return inSuspend;
	}
	public void setInSuspend(String inSuspend) {
		this.inSuspend = inSuspend;
	}
	public String getOutSuspend() {
		return outSuspend;
	}
	public void setOutSuspend(String outSuspend) {
		this.outSuspend = outSuspend;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getRsaSecureID() {
		return rsaSecureID;
	}
	public void setRsaSecureID(String rsaSecureID) {
		this.rsaSecureID = rsaSecureID;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAllowLowBalAlert() {
		return allowLowBalAlert;
	}
	public void setAllowLowBalAlert(String allowLowBalAlert) {
		this.allowLowBalAlert = allowLowBalAlert;
	}
	public String getTrfRuleCode() {
		return trfRuleCode;
	}
	public void setTrfRuleCode(String trfRuleCode) {
		this.trfRuleCode = trfRuleCode;
	}
	public String getLongitde() {
		return longitude;
	}
	public void setLongitude(String longitde) {
		this.longitude = longitde;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
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
	public String getCommProfile() {
		return commProfile;
	}
	public void setCommProfile(String commProfile) {
		this.commProfile = commProfile;
	}
	public String getTrfProfile() {
		return trfProfile;
	}
	public void setTrfProfile(String trfProfile) {
		this.trfProfile = trfProfile;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getVoucherType() {
		return voucherType;
	}
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}
	
}

