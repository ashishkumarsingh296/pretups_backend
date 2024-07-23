package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class AutoCompleteRequestParentVO {
	@io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required= false/*, position =1*/,hidden=true)
	@JsonProperty("reqGatewayLoginId")
	private String reqGatewayLoginId;
	
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true)
	AutoCompleteUserDetailsData data;
    
	@io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required= false/* , position =5 */, description = "Source Type", hidden =true)
	@JsonProperty("sourceType")
	private String sourceType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required= false/* , position =3 */, description = "Request Gateway Type",hidden =true)
	@JsonProperty("reqGatewayType")
	private String reqGatewayType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required= false/* , position =2 */, description = "Request Gateway Password",hidden =true)
	@JsonProperty("reqGatewayPassword")
	private String reqGatewayPassword;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "190", required= false/* , position =4 */, description = "Service Port", hidden = true)
	@JsonProperty("servicePort")
	private String servicePort;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required= false/* , position =6 */, description = "Request Gateway Code", hidden =true)
	@JsonProperty("reqGatewayCode")
	private String reqGatewayCode;

	@JsonProperty("reqGatewayLoginId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = false/* , defaultValue = "" */)
	public String getReqGatewayLoginId() {
		return reqGatewayLoginId;
	}

	public void setReqGatewayLoginId(String reqGatewayLoginId) {
		this.reqGatewayLoginId = reqGatewayLoginId;
	}

	
	@JsonProperty("data")
	public AutoCompleteUserDetailsData getData() {
		return data;
	}

	public void setData(AutoCompleteUserDetailsData data) {
		this.data = data;
	}

	
	@JsonProperty("sourceType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = false/* , defaultValue = "" */)
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	
	@JsonProperty("reqGatewayType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */)
	public String getReqGatewayType() {
		return reqGatewayType;
	}

	public void setReqGatewayType(String reqGatewayType) {
		this.reqGatewayType = reqGatewayType;
	}

	
	@JsonProperty("reqGatewayPassword")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = false/* , defaultValue = "" */)
	public String getReqGatewayPassword() {
		return reqGatewayPassword;
	}

	public void setReqGatewayPassword(String reqGatewayPassword) {
		this.reqGatewayPassword = reqGatewayPassword;
	}

	
	@JsonProperty("servicePort")
	@io.swagger.v3.oas.annotations.media.Schema(example = "190", required = false/* , defaultValue = "" */)
	public String getServicePort() {
		return servicePort;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}

	
	@JsonProperty("reqGatewayCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */)
	public String getReqGatewayCode() {
		return reqGatewayCode;
	}

	public void setReqGatewayCode(String reqGatewayCode) {
		this.reqGatewayCode = reqGatewayCode;
	}
	
	@Override
	public String toString() {
		return "AutoCompleteRequestParentVO [reqGatewayLoginId=" + reqGatewayLoginId + ", data=" + data
				+ ", sourceType=" + sourceType + ", reqGatewayType=" + reqGatewayType + ", reqGatewayPassword="
				+ reqGatewayPassword + ", servicePort=" + servicePort + ", reqGatewayCode=" + reqGatewayCode + "]";
	}
}
