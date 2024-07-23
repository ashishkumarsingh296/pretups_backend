package com.restapi.user.service;

import java.util.List;

import com.btsl.pretups.channel.transfer.businesslogic.BalanceVO;

public class UserHierarchyUIResponseData {

	String username;

	String msisdn;

	List<BalanceVO> balanceList;

	String parentID;

	String userID;
	
	String status;
	
	String statusCode;
	
	String category;
	
	String categoryCode;
	
	String loginId;
	
	int level;
	
	String userType;
	
	
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	List<UserHierarchyUIResponseData> childList;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public List<BalanceVO> getBalanceList() {
		return balanceList;
	}

	public void setBalanceList(List<BalanceVO> balanceList) {
		this.balanceList = balanceList;
	}

	public List<UserHierarchyUIResponseData> getChildList() {
		return childList;
	}

	public void setChildList(List<UserHierarchyUIResponseData> childList) {
		this.childList = childList;
	}
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserHierarchyUIResponseData [username=").append(username).append(", msisdn=").append(msisdn)
				.append(", balanceList=").append(balanceList).append(", parentID=").append(parentID).append(", userID=")
				.append(userID).append(", status=").append(status).append(", statusCode=").append(statusCode)
				.append(", category=").append(category).append(", categoryCode=").append(categoryCode)
				.append(", loginId=").append(loginId).append(", level=").append(level).append(", userType=")
				.append(userType).append(", childList=").append(childList).append("]");
		return builder.toString();
	}
	
		

}
