package com.btsl.pretups.channel.transfer.businesslogic;

public class StaffUserDTO {
	String userName;
	String msisdn;
	String loginID;
	String status;
	String statusCode;
	String Domain;
	String channelUserName;
	String categoryName;
	String ownerName;
	String parentID;
	String parentLoginID;
	String userID;
	String ownerID;
	
	


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GetChannelUsersMsg [userName=").append(userName).append(", msisdn=").append(msisdn)
				.append(", status=").append(status).append(", statusCode=").append(statusCode).append(", Domain=")
				.append(Domain).append(", Category=")
				.append(", channelUserName=").append(channelUserName).append(", ownerName=").append(ownerName)
				.append(", contactPerson=")
				.append(", loginID=").append(loginID)
				.append("]");
		return builder.toString();
	}




	public String getCategoryName() {
		return categoryName;
	}




	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}




	public String getUserName() {
		return userName;
	}




	public void setUserName(String userName) {
		this.userName = userName;
	}




	public String getMsisdn() {
		return msisdn;
	}




	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}




	public String getLoginID() {
		return loginID;
	}




	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}




	public String getStatus() {
		return status;
	}




	public void setStatus(String status) {
		this.status = status;
	}




	public String getStatusCode() {
		return statusCode;
	}




	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}




	public String getDomain() {
		return Domain;
	}




	public void setDomain(String domain) {
		Domain = domain;
	}




	public String getChannelUserName() {
		return channelUserName;
	}




	public void setChannelUserName(String channelUserName) {
		this.channelUserName = channelUserName;
	}




	public String getOwnerName() {
		return ownerName;
	}




	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
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




	public String getOwnerID() {
		return ownerID;
	}




	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}




	public String getParentLoginID() {
		return parentLoginID;
	}




	public void setParentLoginID(String parentLoginID) {
		this.parentLoginID = parentLoginID;
	}


	
	

}

