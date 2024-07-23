package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.ArrayList;
import java.util.Date;

import com.btsl.pretups.domain.businesslogic.CategoryVO;

/*
 * @(#)PersonalDetailsVO.java
 * Traveling object for users Personal details
 */

public class PersonalDetailsVO {
	private String prepaidBalance = "0";
	private String postpaidBalance= "0";
	private String domainCode;
	private String domainCodeDesc;
	private String categoryCode;
	private String categoryCodeDesc;
	private String parentCategory;
	private String parentName;
	private String geography;
	private String ownerName;
	private String namePrefix;
	private String shortName;
	private String firstName;
	private String lastName;
	private String status;
	private String statusDesc;
	private String msisdn;
	private String emailId;
	private String contactPerson;
	private String ssn;
	private String subscriberCode;
	private String externalCode;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	private String longitude;
	private String latitude;
	private String language;
	private String company;

	private String fax;
	private String appointmentDate;
	private String documentType;
	private String documentNo;
	private String userId;
	
	private String userLanguage;
	private String userLanguageDesc;
	private ArrayList userLanguageList;
	private String userOtherBalance;
	private ArrayList userBalanceList;
	private CategoryVO categoryVO;
	private String creationType;
	private String creationTypeDesc;
	private String designation;
	private String contactNumber;
	private String userName;
	private String otherEmailId;
	private String empCode;
	private String modifiedOn;
	private String modifiedByUserId;
	private String modifiedByUserName;
	private String geographyCode;
	private String outletCode;
	private String outletCodeDesc;
	private String subOutletCode;
	private String subOutletCodeDesc;
	private String parentGeographyCode;
	private String parentGeographyName;
	private String createdBy;
	private String createdOn;
	private String division;
	private String divisionCode;
	private String department;
	private String departmentCode;
	private String parentCategoryCode;
	private String ownerCategoryCode;
	private String ownerLoginId;
	private String parentLoginId;

	public String getAuthTypeAllowed() {
		return authTypeAllowed;
	}

	public void setAuthTypeAllowed(String authTypeAllowed) {
		this.authTypeAllowed = authTypeAllowed;
	}

	private String authTypeAllowed ;
	
	
	
	
	
	/**
	 * @return the otherEmailId
	 */
	public String getOtherEmailId() {
		return otherEmailId;
	}
	/**
	 * @param otherEmailId the otherEmailId to set
	 */
	public void setOtherEmailId(String otherEmailId) {
		this.otherEmailId = otherEmailId;
	}
	/**
	 * @return the empCode
	 */
	public String getEmpCode() {
		return empCode;
	}
	/**
	 * @param empCode the empCode to set
	 */
	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPrepaidBalance() {
		return prepaidBalance;
	}
	public void setPrepaidBalance(String prepaidBalance) {
		this.prepaidBalance = prepaidBalance;
	}
	public String getPostpaidBalance() {
		return postpaidBalance;
	}
	public void setPostpaidBalance(String postpaidBalance) {
		this.postpaidBalance = postpaidBalance;
	}
	public String getNamePrefix() {
		return namePrefix;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public String getParentCategory() {
		return parentCategory;
	}
	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getGeography() {
		return geography;
	}
	public void setGeography(String geography) {
		this.geography = geography;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
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
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
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
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
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
	public String getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(String date) {
		this.appointmentDate = date;
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
	public ArrayList getUserLanguageList() {
		return userLanguageList;
	}
	public void setUserLanguageList(ArrayList userLanguageList) {
		this.userLanguageList = userLanguageList;
	}
	public String getUserOtherBalance() {
		return userOtherBalance;
	}
	public void setUserOtherBalance(String userOtherBalance) {
		this.userOtherBalance = userOtherBalance;
	}
	public ArrayList getUserBalanceList() {
		return userBalanceList;
	}
	public void setUserBalanceList(ArrayList userBalanceList) {
		this.userBalanceList = userBalanceList;
	}
	public CategoryVO getCategoryVO() {
		return categoryVO;
	}
	public void setCategoryVO(CategoryVO categoryVO) {
		this.categoryVO = categoryVO;
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
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
	public String getModifiedByUserId() {
		return modifiedByUserId;
	}
	public void setModifiedByUserId(String modifiedByUserId) {
		this.modifiedByUserId = modifiedByUserId;
	}
	public String getModifiedByUserName() {
		return modifiedByUserName;
	}
	public void setModifiedByUserName(String modifiedByUserName) {
		this.modifiedByUserName = modifiedByUserName;
	}
	
	public String getGeographyCode() {
		return geographyCode;
	}
	public void setGeographyCode(String geographyCode) {
		this.geographyCode = geographyCode;
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
	
	public String getOutletCodeDesc() {
		return outletCodeDesc;
	}
	public void setOutletCodeDesc(String outletCodeDesc) {
		this.outletCodeDesc = outletCodeDesc;
	}
	public String getSubOutletCodeDesc() {
		return subOutletCodeDesc;
	}
	public void setSubOutletCodeDesc(String subOutletCodeDesc) {
		this.subOutletCodeDesc = subOutletCodeDesc;
	}
	
	
	public String getParentGeographyCode() {
		return parentGeographyCode;
	}
	public void setParentGeographyCode(String parentGeographyCode) {
		this.parentGeographyCode = parentGeographyCode;
	}
	public String getParentGeographyName() {
		return parentGeographyName;
	}
	public void setParentGeographyName(String parentGeographyName) {
		this.parentGeographyName = parentGeographyName;
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
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PersonalDetailsVO [prepaidBalance=").append(prepaidBalance).append(", postpaidBalance=")
				.append(postpaidBalance).append(", domainCode=").append(domainCode).append(", domainCodeDesc=")
				.append(domainCodeDesc).append(", categoryCode=").append(categoryCode).append(", categoryCodeDesc=")
				.append(categoryCodeDesc).append(", parentCategory=").append(parentCategory).append(", parentName=")
				.append(parentName).append(", geography=").append(geography).append(", ownerName=").append(ownerName)
				.append(", namePrefix=").append(namePrefix).append(", shortName=").append(shortName)
				.append(", firstName=").append(firstName).append(", lastName=").append(lastName).append(", status=")
				.append(status).append(", statusDesc=").append(statusDesc).append(", msisdn=").append(msisdn)
				.append(", emailId=").append(emailId).append(", contactPerson=").append(contactPerson).append(", ssn=")
				.append(ssn).append(", subscriberCode=").append(subscriberCode).append(", externalCode=")
				.append(externalCode).append(", addressLine1=").append(addressLine1).append(", addressLine2=")
				.append(addressLine2).append(", city=").append(city).append(", state=").append(state)
				.append(", country=").append(country).append(", longitude=").append(longitude).append(", latitude=")
				.append(latitude).append(", language=").append(language).append(", company=").append(company)
				.append(", fax=").append(fax).append(", appointmentDate=").append(appointmentDate)
				.append(", documentType=").append(documentType).append(", documentNo=").append(documentNo)
				.append(", userId=").append(userId).append(", userLanguage=").append(userLanguage)
				.append(", userLanguageDesc=").append(userLanguageDesc).append(", userLanguageList=")
				.append(userLanguageList).append(", userOtherBalance=").append(userOtherBalance)
				.append(", userBalanceList=").append(userBalanceList).append(", categoryVO=").append(categoryVO)
				.append(", creationType=").append(creationType).append(", creationTypeDesc=").append(creationTypeDesc)
				.append(", designation=").append(designation).append(", contactNumber=").append(contactNumber)
				.append(", userName=").append(userName).append(", otherEmailId=").append(otherEmailId)
				.append(", empCode=").append(empCode).append(", modifiedOn=").append(modifiedOn)
				.append(", modifiedByUserId=").append(modifiedByUserId).append(", modifiedByUserName=")
				.append(modifiedByUserName).append(", geographyCode=").append(geographyCode).append(", outletCode=")
				.append(outletCode).append(", outletCodeDesc=").append(outletCodeDesc).append(", subOutletCode=")
				.append(", ownerLoginId=").append(ownerLoginId).append(", parentLoginId=").append(parentLoginId)
				.append(subOutletCode).append(", subOutletCodeDesc=").append(subOutletCodeDesc).append("]");
		return builder.toString();
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
	public String getDivisionCode() {
		return divisionCode;
	}
	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}
	public String getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}
	public String getOwnerLoginId() {
		return ownerLoginId;
	}
	public void setOwnerLoginId(String ownerLoginId) {
		this.ownerLoginId = ownerLoginId;
	}
	public String getParentLoginId() {
		return parentLoginId;
	}
	public void setParentLoginId(String parentLoginId) {
		this.parentLoginId = parentLoginId;
	}
	
}
