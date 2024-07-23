package com.restapi.channelAdmin.requestVO;


public class O2CTxnReversalListRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "OT201222.1756.720001", required = true/* , defaultValue = "" */)
	private String transactionID;

	@io.swagger.v3.oas.annotations.media.Schema(example = "72757575", required = true/* , defaultValue = "" */)
	private String msisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "TRF/SALE", required = true/* , defaultValue = "" */)
	private String transferCategory;

	@io.swagger.v3.oas.annotations.media.Schema(example = "29/06/21", required = true/* , defaultValue = "" */)
	private String fromDate;

	@io.swagger.v3.oas.annotations.media.Schema(example = "29/07/21", required = true/* , defaultValue = "" */)
	private String toDate;

	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String geography;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String domain;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String category;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String ownerUsername;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String ownerUserId;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "NGD0000002760", required = true/* , defaultValue = "" */)
	private String userName;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "NGD0000002760", required = true/* , defaultValue = "" */)
	private String userId;

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getTransferCategory() {
		return transferCategory;
	}

	public void setTransferCategory(String transferCategory) {
		this.transferCategory = transferCategory;
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

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

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
	

	public String getOwnerUsername() {
		return ownerUsername;
	}

	public void setOwnerUsername(String ownerUsername) {
		this.ownerUsername = ownerUsername;
	}

	public String getOwnerUserId() {
		return ownerUserId;
	}

	public void setOwnerUserId(String ownerUserId) {
		this.ownerUserId = ownerUserId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2CTxnReversalListRequestVO [transactionID=").append(transactionID).append(", msisdn=")
				.append(msisdn).append(", transferCategory=").append(transferCategory).append(", fromDate=")
				.append(fromDate).append(", toDate=").append(toDate).append(", geography=").append(geography)
				.append(", domain=").append(domain).append(", category=").append(category).append(", ownerUsername=")
				.append(ownerUsername).append(", userID=").append(userId).append("]");
		return builder.toString();
	}

	
	
	
	
}
