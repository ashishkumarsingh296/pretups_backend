package com.btsl.pretups.channel.transfer.businesslogic;

public class ChannelUserDTO {
	String userName;
	String msisdn;
	String status;
	String statusCode;
	String Domain;
	String Category;
	String categoryCode;
	String parentName;
	String ownerName;
	String lastModifiedBy;
	String transactionProfile;
	String commissionProfile;
	String loginID;
	String grade;
	String geography;
	String parentID;
	String userID;
	String ownerID;
	String userType;
	
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


	public String getCategory() {
		return Category;
	}


	public void setCategory(String category) {
		Category = category;
	}


	public String getCategoryCode() {
		return categoryCode;
	}


	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}


	public String getParentName() {
		return parentName;
	}


	public void setParentName(String parentName) {
		this.parentName = parentName;
	}


	public String getOwnerName() {
		return ownerName;
	}


	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}


	public String getLastModifiedBy() {
		return lastModifiedBy;
	}


	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}


	public String getTransactionProfile() {
		return transactionProfile;
	}


	public void setTransactionProfile(String transactionProfile) {
		this.transactionProfile = transactionProfile;
	}


	public String getCommissionProfile() {
		return commissionProfile;
	}


	public void setCommissionProfile(String commissionProfile) {
		this.commissionProfile = commissionProfile;
	}


	public String getLoginID() {
		return loginID;
	}


	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}


	public String getGrade() {
		return grade;
	}


	public void setGrade(String grade) {
		this.grade = grade;
	}


	public String getGeography() {
		return geography;
	}


	public void setGeography(String geography) {
		this.geography = geography;
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


	public String getUserType() {
		return userType;
	}


	public void setUserType(String userType) {
		this.userType = userType;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GetChannelUsersMsg [userName=").append(userName).append(", msisdn=").append(msisdn)
				.append(", status=").append(status).append(", statusCode=").append(statusCode).append(", Domain=")
				.append(Domain).append(", Category=").append(Category).append(", categoryCode=").append(categoryCode)
				.append(", parentName=").append(parentName).append(", ownerName=").append(ownerName)
				.append(", lastModifiedBy=").append(lastModifiedBy).append(", transactionProfile=")
				.append(transactionProfile).append(", commissionProfile=").append(commissionProfile)
				.append(", contactPerson=")
				.append(", loginID=").append(loginID).append(", grade=").append(grade).append(", registeredDateTime=")
				.append(", geography=").append(geography).append(", parentID=").append(parentID).append(", userID=")
				.append(userID).append(", userType=").append(userType).append(", balanceList=")
				.append(", lastModified=").append(", registredDate=")
				.append(", lastTransaction=").append(", canChangeStatus=")
				.append("]");
		return builder.toString();
	}


	
	

}

