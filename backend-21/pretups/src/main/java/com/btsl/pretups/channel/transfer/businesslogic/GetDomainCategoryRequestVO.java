package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class GetDomainCategoryRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(hidden =true)
	@JsonProperty("reqGatewayLoginId")
	private String reqGatewayLoginId;
	
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true)
	private GetDomainCategoryData data;
	
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
	@io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = false /* , defaultValue = "" */, description =" Request Gateway Login Id")
	public String getReqGatewayLoginId() {
		return reqGatewayLoginId;
	}

	public void setReqGatewayLoginId(String reqGatewayLoginId) {
		this.reqGatewayLoginId = reqGatewayLoginId;
	}

	
	public GetDomainCategoryData getData() {
		return data;
	}

	public void setData(GetDomainCategoryData data) {
		this.data = data;
	}

	
	@JsonProperty("sourceType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = false/* , defaultValue = "" */ , description =  "Source Type")
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	
	@JsonProperty("reqGatewayType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */, description =  "Request Gateway Type")
	public String getReqGatewayType() {
		return reqGatewayType;
	}

	public void setReqGatewayType(String reqGatewayType) {
		this.reqGatewayType = reqGatewayType;
	}

	
	@JsonProperty("reqGatewayPassword")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = false/* , defaultValue = "" */, description= "Request Gateway Password")
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
	

}


//class GetDomainCategoryData{
//	
//	@JsonProperty("msisdn")
//	String msisdn;
//	
//	@JsonProperty("loginid")
//	String loginId;
//	
//	@JsonProperty("password")
//	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true, description = "Password")
//	String password;
//	
//	@JsonProperty("extcode")
//	@io.swagger.v3.oas.annotations.media.Schema(example = "ABC", required = true, description = "External Code")
//	String extCode;
//	
//	
//	@JsonProperty("extnwcode")
//	String extnwcode;
//	
//	@JsonProperty("msisdn2")
//	String msisdn2;
//	
//	@JsonProperty("pin")
//	String pin;
//	
//	@JsonProperty("language1")
//	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = false, description = "Language1")
//	String language1;
//	
//	@JsonProperty("language2")
//	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = false, description = "Language2")
//	String language2;
//	
//	public String getExtCode() {
//		return extCode;
//	}
//
//	public void setExtCode(String extCode) {
//		this.extCode = extCode;
//	}
//
//			
//	@JsonProperty("extnwcode")
//	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */, description = "External Network Code")
//	public String getExtnwcode() {
//		return extnwcode;
//	}
//
//	public void setExtnwcode(String extnwcode) {
//		this.extnwcode = extnwcode;
//	}
//	@JsonProperty("loginid")
//	@io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true/* , defaultValue = "" */, description = "Login Id")
//	public String getLoginId() {
//		return loginId;
//	}
//
//	public void setLoginId(String loginId) {
//		this.loginId = loginId;
//	}
//	@JsonProperty("password")
//	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */, description = "Password")
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//	
//	@JsonProperty("pin")
//	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */, description = "Pin")
//	public String getPin() {
//		return pin;
//	}
//
//	public void setPin(String pin) {
//		this.pin = pin;
//	}
//
//	@io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */, description ="Msisdn2")
//	public String getMsisdn2() {
//		return msisdn2;
//	}
//
//	public void setMsisdn2(String msisdn2) {
//		this.msisdn2 = msisdn2;
//	}
//
//
//	@JsonProperty("msisdn")
//	@io.swagger.v3.oas.annotations.media.Schema(example = "72525252"/* , defaultValue = "" */, description = "Msisdn")
//	public String getMsisdn() {
//		return msisdn;
//	}
//
//	public void setMsisdn(String msisdn) {
//		this.msisdn = msisdn;
//	}
//
//	@Override
//	public String toString() {
//		return "GetDomainCategoryData [msisdn=" + msisdn + ", loginId=" + loginId + ", password=" + password
//				+ ", extCode=" + extCode + ", extnwcode=" + extnwcode + ", msisdn2=" + msisdn2 + ", pin=" + pin
//				+ "]";
//	}
//	
//
//	
//}
