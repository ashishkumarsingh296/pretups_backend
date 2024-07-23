package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class C2CTxnsForReversalRequestVO {

	@JsonProperty("txnID")
	private String txnId;
	
	@JsonProperty("senderMsisdn")
	private String senderMsisdn;
	
	@JsonProperty("rcvrMsisdn")
	private String rcvrMsisdn;
	
	@JsonProperty("senderLoginId")
	private String senderLoginId;
	
	@JsonProperty("senderUsername")
	private String senderUsername;
	
	@JsonProperty("domainCode")
	private String domainCode;
	
	@JsonProperty("categoryCode")
	private String categoryCode;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */)
	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "SE", required = true/* , defaultValue = "" */)
	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "CW200925.1528.630001", required = true/* , defaultValue = "" */)
	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "72525252", required = true/* , defaultValue = "" */)
	public String getSenderMsisdn() {
		return senderMsisdn;
	}

	public void setSenderMsisdn(String senderMsisdn) {
		this.senderMsisdn = senderMsisdn;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "72525252", required = true/* , defaultValue = "" */)
	public String getRcvrMsisdn() {
		return rcvrMsisdn;
	}

	public void setRcvrMsisdn(String rcvrMsisdn) {
		this.rcvrMsisdn = rcvrMsisdn;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya_dist", required = true/* , defaultValue = "" */)
	public String getSenderLoginId() {
		return senderLoginId;
	}

	public void setSenderLoginId(String senderLoginId) {
		this.senderLoginId = senderLoginId;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya_dist", required = true/* , defaultValue = "" */)
	public String getSenderUsername() {
		return senderUsername;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}	
}
