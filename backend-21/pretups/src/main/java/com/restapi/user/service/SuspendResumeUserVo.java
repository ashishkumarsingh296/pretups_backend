package com.restapi.user.service;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class SuspendResumeUserVo extends OAuthUser{
	@JsonProperty("data")
    private SuspendResumeUserDetailsData suspendResumeUserDetailsData = null;

	public SuspendResumeUserDetailsData getSuspendResumeUserDetailsData() {
		return suspendResumeUserDetailsData;
	}

	public void setSuspendResumeUserDetailsData(SuspendResumeUserDetailsData suspendResumeUserDetailsData) {
		this.suspendResumeUserDetailsData = suspendResumeUserDetailsData;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SuspendResumeUserVo [suspendResumeUserDetailsData=");
		builder.append(suspendResumeUserDetailsData);
		builder.append("]");
		return builder.toString();
	}

	
}

class SuspendResumeUserDetailsData {

	@JsonProperty("userType")
	private String userType;
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "channel/staff", required = true/* , defaultValue = "" */, description= "Request")
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@JsonProperty("reqType")
	private String reqType;
	@io.swagger.v3.oas.annotations.media.Schema(example = "suspend/resume", required = true/* , defaultValue = "" */, description= "Request")
	public String getReqType() {
		return reqType;
	}
    
	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("loginid")
	private String loginid;
	@JsonProperty("remarks")
	private String remarks;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "72522233", required = false/* , defaultValue = "" */, description= "MSISDN")
	public String getMsisdn() {
		return msisdn;
	}
	
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = false/* , defaultValue = "" */, description= "LoginID")
	public String getLoginid() {
		return loginid;
	}
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "remarks", required = true/* , defaultValue = "" */, description= "Remarks")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SuspendResumeUserVo [msisdn=");
		builder.append(msisdn);
		builder.append(", loginid=");
		builder.append(loginid);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}