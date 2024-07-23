package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "This is a data field")
public class C2CTrfUserDetailsReqMsg {

	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	@JsonProperty("pin")
	private String pin;

	@JsonProperty("loginid")
	private String loginid;
	
	@JsonProperty("password")
	private String password;
	
	@JsonProperty("extcode")
	private String extcode;
	
	@JsonProperty("msisdn2")
	private String msisdn2;
	
	@JsonProperty("language1")
	private String language1;
	
	@JsonProperty("language2")
	private String language2;
	
	@JsonProperty("c2ctrftype")
	private String c2ctrftype;
	
	

	// Getter Methods

	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */, description ="External Network Code")
	public String getExtnwcode() {
		return extnwcode;
	}
	
	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */, description = "Msisdn")
	public String getMsisdn() {
		return msisdn;
	}
	
	@JsonProperty("pin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2468", required = true/* , defaultValue = "" */, description = "Pin")
	public String getPin() {
		return pin;
	}
	
	@JsonProperty("loginid")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true/* , defaultValue = "" */, description = "Login Id")
	public String getLoginid() {
		return loginid;
	}
	
	@JsonProperty("password")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */,description = "Password")
	public String getPassword() {
		return password;
	}
	
	@JsonProperty("extcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ABC", required = true/* , defaultValue = "" */, description = "External Code")
	public String getExtcode() {
		return extcode;
	}

	@JsonProperty("msisdn2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "7278678601", required = true/* , defaultValue = "" */, description = "Msisdn2")
	public String getMsisdn2() {
		return msisdn2;
	}
	
	@JsonProperty("language1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = false/* , defaultValue = "" */, description = "Language1")
	public String getLanguage1() {
		return language1;
	}

	@JsonProperty("language2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = false/* , defaultValue = "" */, description = "Language2")
	public String getLanguage2() {
		return language2;
	}
	
	@JsonProperty("c2ctrftype")
	@io.swagger.v3.oas.annotations.media.Schema(example = "C", required = false/* , defaultValue = "" */, description = "C2C Transfer Type")
	public String getC2ctrftype() {
		return c2ctrftype;
	}

	// Setter Methods

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}
	
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	public void setPin(String pin) {
		this.pin = pin;
	}
	
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}
	
	public void setMsisdn2(String msisdn) {
		this.msisdn2 = msisdn;
	}

	public void setC2ctrftype(String c2ctrftype) {
		this.c2ctrftype = c2ctrftype;
	}

	public void setLanguage2(String language2) {
		this.language2 = language2;
	}

	public void setLanguage1(String language1) {
		this.language1 = language1;
	}
	
	@Override
	public String toString() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("C2CTrfUserDetailsReqMsg [");
		sbf.append("extnwcode=" + extnwcode);
		sbf.append("msisdn=" + msisdn);
		sbf.append("pin=" + pin);
		sbf.append("loginid=" + loginid);
		sbf.append("password=" + password);
		sbf.append("extcode=" + extcode);
		sbf.append("msisdn2=" + msisdn2);
		sbf.append("language1=" + language1);
		sbf.append("language2=" + language2);
		sbf.append("c2ctrftype=" + c2ctrftype);
		return sbf.toString();
	}	
}
