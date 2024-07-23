package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;




@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class UserOtpValidationRequestParentVO {

	@JsonProperty("reqGatewayLoginId")
	private String reqGatewayLoginId;
	
	@JsonProperty("data")
	UserOtpRequestMessage data;
	
	@JsonProperty("sourceType")
	private String sourceType;
	
	@JsonProperty("reqGatewayType")
	private String reqGatewayType;
	
	@JsonProperty("reqGatewayPassword")
	private String reqGatewayPassword;
	
	@JsonProperty("servicePort")
	private String servicePort;
	
	@JsonProperty("reqGatewayCode")
	private String reqGatewayCode;

	
	
	@JsonProperty("reqGatewayLoginId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = true/* , defaultValue = "" */)
	public String getReqGatewayLoginId() {
		return reqGatewayLoginId;
	}

	public void setReqGatewayLoginId(String reqGatewayLoginId) {
		this.reqGatewayLoginId = reqGatewayLoginId;
	}

	
	@JsonProperty("data")
	public UserOtpRequestMessage getData() {
		return data;
	}

	public void setData(UserOtpRequestMessage data) {
		this.data = data;
	}

	
	@JsonProperty("sourceType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = true/* , defaultValue = "" */)
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	
	@JsonProperty("reqGatewayType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = true/* , defaultValue = "" */)
	public String getReqGatewayType() {
		return reqGatewayType;
	}

	public void setReqGatewayType(String reqGatewayType) {
		this.reqGatewayType = reqGatewayType;
	}

	
	@JsonProperty("reqGatewayPassword")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getReqGatewayPassword() {
		return reqGatewayPassword;
	}

	public void setReqGatewayPassword(String reqGatewayPassword) {
		this.reqGatewayPassword = reqGatewayPassword;
	}

	
	@JsonProperty("servicePort")
	@io.swagger.v3.oas.annotations.media.Schema(example = "190", required = true/* , defaultValue = "" */)
	public String getServicePort() {
		return servicePort;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}

	
	@JsonProperty("reqGatewayCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = true/* , defaultValue = "" */)
	public String getReqGatewayCode() {
		return reqGatewayCode;
	}

	public void setReqGatewayCode(String reqGatewayCode) {
		this.reqGatewayCode = reqGatewayCode;
	}

	@Override
	public String toString() {
		return "UserOTPValidationRequestParentVO [reqGatewayLoginId=" + reqGatewayLoginId + ", data=" + data
				+ ", sourceType=" + sourceType + ", reqGatewayType=" + reqGatewayType + ", reqGatewayPassword="
				+ reqGatewayPassword + ", servicePort=" + servicePort + ", reqGatewayCode=" + reqGatewayCode + "]";
	}

}
