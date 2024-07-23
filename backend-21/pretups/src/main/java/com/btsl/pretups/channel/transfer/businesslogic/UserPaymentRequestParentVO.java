package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * 
 * @author anshul.goyal2
 *
 */


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class UserPaymentRequestParentVO {

	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	@JsonProperty("reqGatewayLoginId")
	private String reqGatewayLoginId;
	
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true)
	UserPaymentRequestMessage data;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	@JsonProperty("sourceType")
	private String sourceType;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	@JsonProperty("reqGatewayType")
	private String reqGatewayType;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	@JsonProperty("reqGatewayPassword")
	private String reqGatewayPassword;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	@JsonProperty("servicePort")
	private String servicePort;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	@JsonProperty("reqGatewayCode")
	private String reqGatewayCode;

	
	
	@JsonProperty("reqGatewayLoginId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = false/* , defaultValue = "" */, description = "Request Gateway Login Id")
	public String getReqGatewayLoginId() {
		return reqGatewayLoginId;
	}

	public void setReqGatewayLoginId(String reqGatewayLoginId) {
		this.reqGatewayLoginId = reqGatewayLoginId;
	}

	
	@JsonProperty("data")
	public UserPaymentRequestMessage getData() {
		return data;
	}

	public void setData(UserPaymentRequestMessage data) {
		this.data = data;
	}

	
	@JsonProperty("sourceType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = false/* , defaultValue = "" */, description = "Source Type")
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	
	@JsonProperty("reqGatewayType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */, description = "Request Gateway Type")
	public String getReqGatewayType() {
		return reqGatewayType;
	}

	public void setReqGatewayType(String reqGatewayType) {
		this.reqGatewayType = reqGatewayType;
	}

	
	@JsonProperty("reqGatewayPassword")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = false/* , defaultValue = "" */, description = "Request Gateway Password")
	public String getReqGatewayPassword() {
		return reqGatewayPassword;
	}

	public void setReqGatewayPassword(String reqGatewayPassword) {
		this.reqGatewayPassword = reqGatewayPassword;
	}

	
	@JsonProperty("servicePort")
	@io.swagger.v3.oas.annotations.media.Schema(example = "190", required = false/* , defaultValue = "" */, description = "Service Port")
	public String getServicePort() {
		return servicePort;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}

	
	@JsonProperty("reqGatewayCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */, description = "Request Gateway Code")
	public String getReqGatewayCode() {
		return reqGatewayCode;
	}

	public void setReqGatewayCode(String reqGatewayCode) {
		this.reqGatewayCode = reqGatewayCode;
	}

	@Override
	public String toString() {
		return "UserPaymentRequestParentVO [reqGatewayLoginId=" + reqGatewayLoginId + ", data=" + data
				+ ", sourceType=" + sourceType + ", reqGatewayType=" + reqGatewayType + ", reqGatewayPassword="
				+ reqGatewayPassword + ", servicePort=" + servicePort + ", reqGatewayCode=" + reqGatewayCode + "]";
	}

}
