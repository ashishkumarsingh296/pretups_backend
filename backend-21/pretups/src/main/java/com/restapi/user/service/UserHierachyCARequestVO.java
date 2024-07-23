package com.restapi.user.service;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserHierachyCARequestVO
{
	@JsonProperty("loginID")
	String loginID;
	
	@JsonProperty("msisdn")
	String msisdn;
	
	@JsonProperty("ownerUserId")
	String ownerName;
	
	@JsonProperty("parentCategory")
	String parentCategory;
	
	@JsonProperty("parentUserId")
	String parentUserId;
	
	@JsonProperty("userCategory")
	String userCategory;
	
	@JsonProperty("userStatus")
	String userStatus;
	
	@JsonProperty("advancedSearch")
	boolean advancedSearch;
	

	
	public String getParentUserId() {
		return parentUserId;
	}

	public void setParentUserId(String parentUserId) {
		this.parentUserId = parentUserId;
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
		builder.append("UserHierachyCARequestVO [loginID=").append(loginID).append(", msisdn=").append(msisdn)
				.append(", ownerName=").append(ownerName).append(", parentCategory=").append(parentCategory)
				.append(", parentUserId=").append(parentUserId).append(", userCategory=").append(userCategory)
				.append(", userStatus=").append(userStatus).append(", advancedSearch=").append(advancedSearch)
				.append("]");
		return builder.toString();
	}
}
