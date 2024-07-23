package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TotTrnxDetailMsg {
	@JsonProperty("fromDate")
	String fromDate;
	
	@JsonProperty("toDate")
	String toDate;
	
	@JsonProperty("status")
	String status;
	
	@JsonProperty("msisdn")
	String msisdn;
	
	@JsonProperty("loginId")
	String loginId;
	
	@JsonProperty("password")
	String password;
	
	@JsonProperty("extCode")
	String extCode;
	
	@JsonProperty("transactionID")
	String transactionID;
	
	@JsonProperty("extnwcode")
	String extnwcode;
	
	@JsonProperty("fromRow")
	String fromRow;
	
	@JsonProperty("toRow")
	String toRow;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getExtCode() {
		return extCode;
	}

	public void setExtCode(String extCode) {
		this.extCode = extCode;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getExtnwcode() {
		return extnwcode;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}

	public String getFromRow() {
		return fromRow;
	}

	public void setFromRow(String fromRow) {
		this.fromRow = fromRow;
	}

	public String getToRow() {
		return toRow;
	}

	public void setToRow(String toRow) {
		this.toRow = toRow;
	}

	@Override
	public String toString() {
		return "TotTrnxDetailMsg [fromDate=" + fromDate + ", toDate=" + toDate + ", status=" + status + ", msisdn="
				+ msisdn + ", loginId=" + loginId + ", password=" + password + ", extCode=" + extCode
				+ ", transactionID=" + transactionID + ", extnwcode=" + extnwcode + ", fromRow=" + fromRow + ", toRow="
				+ toRow + "]";
	}
	

}
