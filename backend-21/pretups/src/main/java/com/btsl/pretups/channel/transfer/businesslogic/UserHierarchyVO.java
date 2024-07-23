package com.btsl.pretups.channel.transfer.businesslogic;

public class UserHierarchyVO {
	
	private String msisdn;
	private String firstName;
	private String lastName;
	private String loginId;
	private String categoryCode;
	private String categoryName;
	private String userNamePrefixCode;
	private String userNamePrefix;
	
	public String getUserNamePrefixCode() {
		return userNamePrefixCode;
	}
	public void setUserNamePrefixCode(String userNamePrefixCode) {
		this.userNamePrefixCode = userNamePrefixCode;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
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
	
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getUserNamePrefix() {
		return userNamePrefix;
	}
	public void setUserNamePrefix(String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
	}
	public String getCategoryName() {
		return categoryName;
	}
	@Override
	public String toString() {
		return "UserHierarchyVO [msisdn=" + msisdn + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", loginId=" + loginId + ", categoryCode=" + categoryCode + ", categoryName=" + categoryName 
				+ ", userNamePrefix=" + userNamePrefix + ", categoryName=" + categoryName + ", userNamePrefixCode=" + userNamePrefixCode+"]";
	}


	
	
	
	
}
