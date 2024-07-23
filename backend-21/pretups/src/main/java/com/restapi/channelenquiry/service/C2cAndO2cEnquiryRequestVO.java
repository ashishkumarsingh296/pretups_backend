package com.restapi.channelenquiry.service;



public class C2cAndO2cEnquiryRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "CT201222.1756.720001", required = true/* , defaultValue = "" */)
	private String transactionID;

	@io.swagger.v3.oas.annotations.media.Schema(example = "T", required = true/* , defaultValue = "" */)
	private String transferSubType;

	@io.swagger.v3.oas.annotations.media.Schema(example = "29/06/21", required = true/* , defaultValue = "" */)
	private String fromDate;

	@io.swagger.v3.oas.annotations.media.Schema(example = "29/07/21", required = true/* , defaultValue = "" */)
	private String toDate;

	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
	private String senderMsisdn;

	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
	private String receiverMsisdn;

	@io.swagger.v3.oas.annotations.media.Schema(example = "NGD0000002760", required = true/* , defaultValue = "" */)
	private String userID;

//	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
//	private String productCode;

	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String distributionType;

	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String orderStatus;

	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String domain;

	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String category;

	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String geography;

	@io.swagger.v3.oas.annotations.media.Schema(example = "CHANNEL/STAFF", required = true/* , defaultValue = "" */)
	private String userType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya_staff", required = true/* , defaultValue = "" */)
	private String staffLoginID;

	@io.swagger.v3.oas.annotations.media.Schema(example = "TRF/SALE", required = true/* , defaultValue = "" */)
	private String transferCategory;
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getTransferSubType() {
		return transferSubType;
	}

	public void setTransferSubType(String transferSubType) {
		this.transferSubType = transferSubType;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getSenderMsisdn() {
		return senderMsisdn;
	}

	public void setSenderMsisdn(String senderMsisdn) {
		this.senderMsisdn = senderMsisdn;
	}

	public String getReceiverMsisdn() {
		return receiverMsisdn;
	}

	public void setReceiverMsisdn(String receiverMsisdn) {
		this.receiverMsisdn = receiverMsisdn;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

//	public String getProductCode() {
//		return productCode;
//	}
//
//	public void setProductCode(String productCode) {
//		this.productCode = productCode;
//	}

	public String getDistributionType() {
		return distributionType;
	}

	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getStaffLoginID() {
		return staffLoginID;
	}

	public void setStaffLoginID(String staffLoginID) {
		this.staffLoginID = staffLoginID;
	}

	public String getTransferCategory() {
		return transferCategory;
	}

	public void setTransferCategory(String transferCategory) {
		this.transferCategory = transferCategory;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("C2cAndO2cEnquiryRequestVO [transactionID=");
		builder.append(transactionID);
		builder.append(", transferSubType=");
		builder.append(transferSubType);
		builder.append(", fromDate=");
		builder.append(fromDate);
		builder.append(", toDate=");
		builder.append(toDate);
		builder.append(", senderMsisdn=");
		builder.append(senderMsisdn);
		builder.append(", receiverMsisdn=");
		builder.append(receiverMsisdn);
		builder.append(", userID=");
		builder.append(userID);
		builder.append(", distributionType=");
		builder.append(distributionType);
		builder.append(", orderStatus=");
		builder.append(orderStatus);
		builder.append(", domain=");
		builder.append(domain);
		builder.append(", category=");
		builder.append(category);
		builder.append(", geography=");
		builder.append(geography);
		builder.append(", userType=");
		builder.append(userType);
		builder.append(", staffLoginID=");
		builder.append(staffLoginID);
		builder.append(", transferCategory=");
		builder.append(transferCategory);
		builder.append("]");
		return builder.toString();
	}

}
