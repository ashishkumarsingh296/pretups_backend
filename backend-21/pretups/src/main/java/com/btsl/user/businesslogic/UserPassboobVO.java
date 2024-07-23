package com.btsl.user.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class UserPassboobVO {
	
	
	@JsonProperty("loginId")
	private String loginId;
	
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	
	@JsonProperty("fromDate")
	private String fromDate;
	
	
	@JsonProperty("toDate")
	private String toDate;

	

	@JsonProperty("loginId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "btnadm", required = true/* , defaultValue = "" */)
	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	

	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "7777777777", required = true/* , defaultValue = "" */)
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	

	@JsonProperty("fromDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "dd/MM/YYYY", required = true/* , defaultValue = "" */)
	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	

	@JsonProperty("toDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "dd/MM/YYYY", required = true/* , defaultValue = "" */)
	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	@Override
	public String toString() {
		return "UserPassboobVO [loginId=" + loginId + ", msisdn=" + msisdn + ", fromDate=" + fromDate + ", toDate="
				+ toDate + "]";
	}
	
	

}
