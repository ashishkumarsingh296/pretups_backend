package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;




/**
 * Help class - C2C Recent Buy Service
 * 
 *
 */

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class C2STotalTrnxMessage {


	@JsonProperty("extcode")
	private String extcode;
	
	@JsonProperty("loginid")
	private String loginid;
	
	@JsonProperty("language2")
	private String language2;
	
	@JsonProperty("language1")
	private String language1;
	
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@JsonProperty("password")
	private String password;
	
	@JsonProperty("pin")
	private String pin;
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	@JsonProperty("fromDate")
	private String fromDate;
	
	@JsonProperty("toDate")
	private String toDate;

	// Getter Methods
	@JsonProperty("fromDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
	public String getFromDate() {
		return fromDate;
	}


	

	@JsonProperty("toDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
	public String getToDate() {
		return toDate;
	}




	@JsonProperty("extcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
	public String getExtcode() {
		return extcode;
	}

	
	@JsonProperty("loginid")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
	public String getLoginid() {
		return loginid;
	}

	@JsonProperty("language2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
	public String getLanguage2() {
		return language2;
	}

	
	@JsonProperty("language1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
	public String getLanguage1() {
		return language1;
	}

	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
	public String getExtnwcode() {
		return extnwcode;
	}


	@JsonProperty("password")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
	public String getPassword() {
		return password;
	}

	@JsonProperty("pin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
	public String getPin() {
		return pin;
	}

	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "9999999999", required = true/* , defaultValue = "" */)
	public String getMsisdn() {
		return msisdn;
	}

	// Setter Methods

	public void setExtcode(String extcode) {
		this.extcode = extcode;
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

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}




	@Override
	public String toString() {
		return "C2STotalTrnxMessage [extcode=" + extcode + ", loginid=" + loginid + ", language2=" + language2
				+ ", language1=" + language1 + ", extnwcode=" + extnwcode + ", password=" + password + ", pin=" + pin
				+ ", msisdn=" + msisdn + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
	


	

	
	

}
