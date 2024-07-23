package com.restapi.user.service;

import com.fasterxml.jackson.annotation.JsonProperty;



@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class VoucherDenominationRequestVO {

	@JsonProperty("identifierType")
	private String identifierType;
	
	@JsonProperty("identifierValue")
	private String identifierValue;
	
	@JsonProperty("identifierType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true/* , defaultValue = "" */)
	public String getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	@JsonProperty("identifierValue")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getIdentifierValue() {
		return identifierValue;
	}

	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}
	@JsonProperty("data")
	private DenominationData data;

	@JsonProperty("data")
	public DenominationData getData() {
		return data;
	}

	public void setData(DenominationData data) {
		this.data = data;
	}
}

class DenominationData{
	
	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	@JsonProperty("voucherType")
	private String voucherType;
	
	@JsonProperty("voucherSegment")
	private String voucherSegment;
	
	@JsonProperty("voucherType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "digital", required = true/* , defaultValue = "" */)
	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	@JsonProperty("loginId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true/* , defaultValue = "" */)
	public String getLoginId() {
		return loginId;
	}

	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72545454", required = true/* , defaultValue = "" */)
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@JsonProperty("voucherSegment")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NL", required = true/* , defaultValue = "" */)
	public String getVoucherSegment() {
		return voucherSegment;
	}

	public void setVoucherSegment(String voucherSegment) {
		this.voucherSegment = voucherSegment;
	}

	@Override
	public String toString() {
		return "Data [loginId=" + loginId + ", msisdn=" + msisdn + ", voucherType=" + voucherType + ", voucherSegment="
				+ voucherSegment + "]";
	}
	
	
}