package com.btsl.pretups.channel.profile.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class UserPinMgmtVO {
	
	@JsonProperty("remarks")
	String remarks;
	
	@JsonProperty("msisdn")
	String msisdn;

	@JsonProperty("remarks")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Remarks for API", required = true/* , defaultValue = "" */,description="Remarks")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "720651561", required = true/* , defaultValue = "" */, description="Msisdn")
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@Override
	public String toString() {
		return "UserPinMgmtVO [remarks=" + remarks + ", msisdn=" + msisdn + "]";
	}
	
	
}
