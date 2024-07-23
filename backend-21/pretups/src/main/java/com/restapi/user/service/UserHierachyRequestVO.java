package com.restapi.user.service;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserHierachyRequestVO
{
	@JsonProperty("loginID")
	String loginID;
	
	@JsonProperty("msisdn")
	String msisdn;
	
	@JsonProperty("ownerName")
	String ownerName;
	
	@JsonProperty("parentCategory")
	String parentCategory;
	
	@JsonProperty("userCategory")
	String userCategory;
	
	@JsonProperty("userStatus")
	String userStatus;
	
	@JsonProperty("advancedSearch")
	boolean advancedSearch;
	
	@JsonProperty("simpleSearch")
	boolean simpleSearch;
	
	
	public boolean isSimpleSearch() {
		return simpleSearch;
	}

	public void setSimpleSearch(boolean simpleSearch) {
		this.simpleSearch = simpleSearch;
	}

	public boolean isAdvancedSearch() {
		return advancedSearch;
	}

	public void setAdvancedSearch(boolean advancedSearch) {
		this.advancedSearch = advancedSearch;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}

	public String getUserCategory() {
		return userCategory;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserHierachyRequestVO [loginID=");
		builder.append(loginID);
		builder.append(", msisdn=");
		builder.append(msisdn);
		builder.append(", ownerName=");
		builder.append(ownerName);
		builder.append(", parentCategory=");
		builder.append(parentCategory);
		builder.append(", userCategory=");
		builder.append(userCategory);
		builder.append(", userStatus=");
		builder.append(userStatus);
		builder.append(", advancedSearch=");
		builder.append(advancedSearch);
		builder.append(", simpleSearch=");
		builder.append(simpleSearch);
		builder.append("]");
		return builder.toString();
	}
	
	
}
