package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class UserPaymentRequestMessage {


	@JsonProperty("extcode")
	private String extcode;
	
	@JsonProperty("loginid")
	private String loginid;
	
	@JsonProperty("language2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = false, description = "Language2")
	private String language2;
	
	@JsonProperty("language1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = false, description = "Language1")
	private String language1;
	
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@JsonProperty("password")
	private String password;
	
	@JsonProperty("pin")
	private String pin;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	@JsonProperty("type")
	private String type;
	

	// Getter Methods
	@JsonProperty("type")
	@io.swagger.v3.oas.annotations.media.Schema(example = "C2C", required = false/* , defaultValue = "" */, description = "Type")
	public String getType() {
		return type;
	}



	@JsonProperty("extcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1132s", required = true/* , defaultValue = "" */, description = "External Code")
	public String getExtcode() {
		return extcode;
	}

	
	@JsonProperty("loginid")
	@io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true/* , defaultValue = "" */, description = "Login Id")
	public String getLoginid() {
		return loginid;
	}

	@JsonProperty("language2")
	public String getLanguage2() {
		return language2;
	}

	
	@JsonProperty("language1")
	public String getLanguage1() {
		return language1;
	}

	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */, description = "External Network Code")
	public String getExtnwcode() {
		return extnwcode;
	}


	@JsonProperty("password")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */, description = "Password")
	public String getPassword() {
		return password;
	}

	@JsonProperty("pin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */, description = "Pin")
	public String getPin() {
		return pin;
	}

	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */, description = "Msisdn")
	public String getMsisdn() {
		return msisdn;
	}

	// Setter Methods

	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public void setLanguage2(String language2) {
		this.language2 = language2;
	}

	public void setLanguage1(String language1) {
		this.language1 = language1;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}



	@Override
	public String toString() {
		return "UserPaymentRequestMessage [extcode=" + extcode + ", loginid=" + loginid + ", language2=" + language2
				+ ", language1=" + language1 + ", extnwcode=" + extnwcode + ", password=" + password + ", pin=" + pin + ", msisdn=" + msisdn + "]";
	}

	
	

}
