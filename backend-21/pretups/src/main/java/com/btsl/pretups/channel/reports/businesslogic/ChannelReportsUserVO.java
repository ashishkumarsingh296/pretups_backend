package com.btsl.pretups.channel.reports.businesslogic;

import java.io.Serializable;

/**
 * @author tarun.kumar
 *
 */
public class ChannelReportsUserVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String msisdn;
	private String userStatus;
	private String entryDateTime;
	private String transferId;
	private String transactionType;
	private String categoryName;
	private String productName;
	private String recordType;
	private String previousBalance;
	private String postBalance;
	private String thresholdValue;
	private String parentName;
	private String parentMsisdn;
	private String ownerName;
	private String ownerMsisdn;
	
	
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
	public String getEntryDateTime() {
		return entryDateTime;
	}
	public void setEntryDateTime(String entryDateTime) {
		this.entryDateTime = entryDateTime;
	}
	public String getTransferId() {
		return transferId;
	}
	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getPreviousBalance() {
		return previousBalance;
	}
	public void setPreviousBalance(String previousBalance) {
		this.previousBalance = previousBalance;
	}
	public String getPostBalance() {
		return postBalance;
	}
	public void setPostBalance(String postBalance) {
		this.postBalance = postBalance;
	}
	public String getThresholdValue() {
		return thresholdValue;
	}
	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}
	@Override
	public String toString() {
		return "ChannelReportsUserVO [userName=" + userName + ", msisdn="
				+ msisdn + ", userStatus=" + userStatus + ", entryDateTime="
				+ entryDateTime + ", transferId=" + transferId
				+ ", transactionType=" + transactionType + ", categoryName="
				+ categoryName + ", productName=" + productName
				+ ", recordType=" + recordType + ", previousBalance="
				+ previousBalance + ", postBalance=" + postBalance
				+ ", thresholdValue=" + thresholdValue + ", parentName="
				+ parentName + ", parentMsisdn=" + parentMsisdn
				+ ", ownerName=" + ownerName + ", ownerMsisdn=" + ownerMsisdn
				+ "]";
	}
	
	
	
}
