package com.restapi.user.service;

import com.fasterxml.jackson.annotation.JsonProperty;



@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class VoucherInfoRequestVO {

	@JsonProperty("identifierType")
	private String identifierType;
	
	@JsonProperty("identifierValue")
	private String identifierValue;
	
	@JsonProperty("identifierType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true/* , defaultValue = "" */, description = "Identifier Type")
	public String getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	@JsonProperty("identifierValue")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */, description = "Identifier Value")
	public String getIdentifierValue() {
		return identifierValue;
	}

	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("data")
	private InfoData data;

	@JsonProperty("data")
	public InfoData getData() {
		return data;
	}

	public void setData(InfoData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "VoucherInfoRequestVO [identifierType=" + identifierType + ", identifierValue=" + identifierValue
				+ ", data=" + data + "]";
	}
	
}

class InfoData{
	
	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	@JsonProperty("loginId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true/* , defaultValue = "" */, description = "Login Id")
	public String getLoginId() {
		return loginId;
	}

	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72545454", required = true/* , defaultValue = "" */, description = "Msisdn")
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@Override
	public String toString() {
		return "InfoData [loginId=" + loginId + ", msisdn=" + msisdn + "]";
	}
	
}