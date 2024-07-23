package com.restapi.user.service;

import com.fasterxml.jackson.annotation.JsonProperty;



@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class VoucherTypeRequestVO {

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
	private TypeData data;
	
	@JsonProperty("data")
	public TypeData getData() {
		return data;
	}

	public void setData(TypeData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "VoucherTypeRequestVO [identifierType=" + identifierType + ", identifierValue=" + identifierValue
				+ ", data=" + data + "]";
	}
	
}

class TypeData{
	
	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	@JsonProperty("voucherList")
	private String voucherList;
	
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
	
	@JsonProperty("voucherList")
	@io.swagger.v3.oas.annotations.media.Schema(example = "E,ET", required = true/* , defaultValue = "" */)
	public String getVoucherType() {
		return voucherList;
	}

	public void setVoucherType(String voucherList) {
		this.voucherList = voucherList;
	}
	
	@Override
	public String toString() {
		return "TypeData [loginId=" + loginId + ", msisdn=" + msisdn + "]";
	}
	
}