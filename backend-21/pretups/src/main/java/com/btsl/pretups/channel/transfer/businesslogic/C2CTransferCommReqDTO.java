
package com.btsl.pretups.channel.transfer.businesslogic;

public class C2CTransferCommReqDTO extends CommonDownloadReqDTO {
   

    private String distributionType;
	private String transferSubType;
	private String transferInout;
	private String domain;
    private String categoryCode;
    private String geography;
    private String user;
    private String transferCategory;
    private String includeStaffUserDetails;
    private String transferUserCategory;
    private String transferUser;
    private String senderMobileNumber;
    private String receiverMobileNumber;
    private String reqTab;
    
	
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getGeography() {
		return geography;
	}
	public void setGeography(String geography) {
		this.geography = geography;
	}
	


	public String getTransferSubType() {
		return transferSubType;
	}
	public void setTransferSubType(String transferSubType) {
		this.transferSubType = transferSubType;
	}
	public String getTransferInout() {
		return transferInout;
	}
	public void setTransferInout(String transferInout) {
		this.transferInout = transferInout;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getIncludeStaffUserDetails() {
		return includeStaffUserDetails;
	}
	public void setIncludeStaffUserDetails(String includeStaffUserDetails) {
		this.includeStaffUserDetails = includeStaffUserDetails;
	}
	public String getTransferUser() {
		return transferUser;
	}
	public void setTransferUser(String transferUser) {
		this.transferUser = transferUser;
	}
	
	public String getSenderMobileNumber() {
		return senderMobileNumber;
	}
	public void setSenderMobileNumber(String senderMobileNumber) {
		this.senderMobileNumber = senderMobileNumber;
	}
	public String getReceiverMobileNumber() {
		return receiverMobileNumber;
	}
	public void setReceiverMobileNumber(String receiverMobileNumber) {
		this.receiverMobileNumber = receiverMobileNumber;
	}
	public String getReqTab() {
		return reqTab;
	}
	public void setReqTab(String reqTab) {
		this.reqTab = reqTab;
	}
	public String getTransferCategory() {
		return transferCategory;
	}
	public void setTransferCategory(String transferCategory) {
		this.transferCategory = transferCategory;
	}
	public String getTransferUserCategory() {
		return transferUserCategory;
	}
	public void setTransferUserCategory(String transferUserCategory) {
		this.transferUserCategory = transferUserCategory;
	}
	public String getDistributionType() {
		return distributionType;
	}
	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LowThresholdDownloadReqDTO [ ");
		sb.append("From Date : ");
		sb.append( fromDate ).append(" ,");
		sb.append(",To Date : ");
		sb.append( toDate ).append(" ,");
		sb.append(",NetworkCode: ");
		sb.append( extnwcode ).append(" ,");
		sb.append(",categoryCode: ");
		sb.append( categoryCode ).append(" ,");
		sb.append(",domain: ");
		sb.append( domain).append(" ,");
		sb.append(",geography: ");
		sb.append( geography).append(" ,");
		sb.append(",user: ");
		sb.append( user).append(" ,");
		sb.append(",transferUser: ");
		sb.append( transferUser);
		sb.append(",transferUserCategory: ");
		sb.append( transferCategory);
		sb.append(",includeStaffUserDetails: ");
		sb.append( includeStaffUserDetails);
		sb.append(" ]");
		return sb.toString();
	}	

}
