package com.restapi.channelAdmin.requestVO;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ApprovalBarredForDltRequestVO {
	@JsonProperty("requestType")
	private String requestType;
	
	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("action")
	private String action;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "BARREDAPPROVAL1/BARREDAPPROVAL2", required = true/* , defaultValue = "" */,description="requestType")
	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "appdist", required = true/* , defaultValue = "" */,description="login Id")
	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "A/R", required = true/* , defaultValue = "" */,description="")
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "test remarks", required = true/* , defaultValue = "" */,description="")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
