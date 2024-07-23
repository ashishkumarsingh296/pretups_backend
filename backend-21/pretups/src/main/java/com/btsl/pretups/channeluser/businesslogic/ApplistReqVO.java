package com.btsl.pretups.channeluser.businesslogic;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApplistReqVO implements Serializable {
	
	@Setter
	@JsonProperty("reqTab")
	private String reqTab;
	@Setter
	@JsonProperty("mobileNumber")
	private String mobileNumber;
	@Setter
	@JsonProperty("loginID")
	private String loginID;
	
	@Setter
	@JsonProperty("domain")
	private String domain;
	@Setter
	@JsonProperty("category")
	private String category;
	@Setter
	@JsonProperty("geography")
	private String geography;
	@Setter
	@JsonProperty("approvalLevel")
	private String approvalLevel;
	@Setter
	@JsonProperty("status")
	private String status;
	@Setter
	@JsonProperty("loggedInUserUserid")
	private String loggedInUserUserid;

	@JsonProperty("userType")
	private String userType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "LOGIN_ID/MSISDN/ADVANCED", required = true, /*value = "",*/description="reqTab")
	public String getReqTab() {
		return reqTab;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "7256563635", required = true, /* value = "",*/ description="mobileNumber")
	public String getMobileNumber() {
		return mobileNumber;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya_dist", required = true, /*value = "",*/description="loginID")
	public String getLoginID() {
		return loginID;
	}

	@JsonProperty("domain")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */,description="Domain")
	public String getDomain() {
		return domain;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */,description="Category")
	public String getCategory() {
		return category;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "HARYANA", required = true/* , defaultValue = "" */,description="Geography")
	public String getGeography() {
		return geography;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "APPRV1/APPRV2/APPRV3", required = true/* , defaultValue = "" */,description="approvalLevel")
	public String getApprovalLevel() {
		return approvalLevel;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "N327843434", required = true/* , defaultValue = "" */,description="loggedInUserUserid")
	public String getLoggedInUserUserid() {
		return loggedInUserUserid;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "W/DR/SR/Y/N", required = true/* , defaultValue = "" */,description="loggedInUserUserid")
	public String getStatus() {
		return status;
	}


}
