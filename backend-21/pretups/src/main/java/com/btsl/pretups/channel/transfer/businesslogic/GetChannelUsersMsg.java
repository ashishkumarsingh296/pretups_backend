package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.Date;
import java.util.List;

public class GetChannelUsersMsg {
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
	String contactPerson;
	Date lastTxnDatTime;
	String loginID;
	String grade;
	Date registeredDateTime;
	Date lastModifiedDateTime;
	String geography;
	String parentID;
	String userID;
	String userType;
	List<BalanceVO> balanceList;
	String lastModified;
	String registredDate;
	String lastTransaction;
	boolean canChangeStatus;

	public boolean isCanChangeStatus() {
		return canChangeStatus;
	}

	public void setCanChangeStatus(boolean canChangeStatus) {
		this.canChangeStatus = canChangeStatus;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public List<BalanceVO> getBalanceList() {
		return balanceList;
	}

	public void setBalanceList(List<BalanceVO> balanceList) {
		this.balanceList = balanceList;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
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

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Date getLastTxnDatTime() {
		return lastTxnDatTime;
	}

	public void setLastTxnDatTime(Date lastTxnDatTime) {
		this.lastTxnDatTime = lastTxnDatTime;
	}

	public Date getRegisteredDateTime() {
		return registeredDateTime;
	}

	public void setRegisteredDateTime(Date registeredDateTime) {
		this.registeredDateTime = registeredDateTime;
	}

	public Date getLastModifiedDateTime() {
		return lastModifiedDateTime;
	}

	public void setLastModifiedDateTime(Date lastModifiedDateTime) {
		this.lastModifiedDateTime = lastModifiedDateTime;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
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

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getRegistredDate() {
		return registredDate;
	}

	public void setRegistredDate(String registredDate) {
		this.registredDate = registredDate;
	}

	public String getLastTransaction() {
		return lastTransaction;
	}

	public void setLastTransaction(String lastTransaction) {
		this.lastTransaction = lastTransaction;
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
				.append(", contactPerson=").append(contactPerson).append(", lastTxnDatTime=").append(lastTxnDatTime)
				.append(", loginID=").append(loginID).append(", grade=").append(grade).append(", registeredDateTime=")
				.append(registeredDateTime).append(", lastModifiedDateTime=").append(lastModifiedDateTime)
				.append(", geography=").append(geography).append(", parentID=").append(parentID).append(", userID=")
				.append(userID).append(", userType=").append(userType).append(", balanceList=").append(balanceList)
				.append(", lastModified=").append(lastModified).append(", registredDate=").append(registredDate)
				.append(", lastTransaction=").append(lastTransaction).append(", canChangeStatus=")
				.append(canChangeStatus).append("]");
		return builder.toString();
	}


	
	

}

