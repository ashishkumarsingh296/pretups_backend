package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;


public class UserZeroBalanceCounterSummaryVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String userName;
	private String msisdn;
	private String userStatus;
	private String categoryName;
	private String parentName;
	private String parentMsisdn;
	private String ownerName ;
	private String ownerMsisdn;
	private String entryDate;
	private String recordType;
	private String thresholdCount;
	private String productName;
	
	
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
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getParentMsisdn() {
		return parentMsisdn;
	}
	public void setParentMsisdn(String parentMsisdn) {
		this.parentMsisdn = parentMsisdn;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerMsisdn() {
		return ownerMsisdn;
	}
	public void setOwnerMsisdn(String ownerMsisdn) {
		this.ownerMsisdn = ownerMsisdn;
	}
	public String getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(String entryDate) {
		this.entryDate = entryDate;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getThresholdCount() {
		return thresholdCount;
	}
	public void setThresholdCount(String thresholdCount) {
		this.thresholdCount = thresholdCount;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sbf = new StringBuilder();
		sbf.append("UserZeroBalanceCounterSummaryVO[userName=").append(userName);
		sbf.append(", userStatus=").append(userStatus);
		sbf.append(", categoryName=").append(categoryName);
		sbf.append(", parentName=").append(parentName);
		sbf.append(", parentMsisdn=").append(parentMsisdn);
		sbf.append(", ownerName=").append(ownerName);
		sbf.append(", ownerMsisdn=").append(ownerMsisdn);
		sbf.append(", entryDate=").append(entryDate);
		sbf.append(", recordType=").append(recordType);
		sbf.append(", thresholdCount=").append(thresholdCount);
		sbf.append(", productName=").append(productName).append("]");
		
		return sbf.toString();
	}
	
	

}
