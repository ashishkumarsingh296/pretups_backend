package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Help class - C2C Recent Buy Service
 * 
 *
 */

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class AllTransactionRequest {


	@JsonProperty("extcode")
	private String extcode;
	
	@JsonProperty("loginid")
	private String loginid;
	
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
	
	@JsonProperty("serviceType")
	private String serviceType;

	// Getter Methods
	@JsonProperty("fromDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12/01/20", required = false/* , defaultValue = "" */)
	public String getFromDate() {
		return fromDate;
	}

	@JsonProperty("toDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12/02/20", required = false/* , defaultValue = "" */)
	public String getToDate() {
		return toDate;
	}




	@JsonProperty("extcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "9857041693", required = false/* , defaultValue = "" */)
	public String getExtcode() {
		return extcode;
	}

	
	@JsonProperty("loginid")
	@io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = false/* , defaultValue = "" */)
	public String getLoginid() {
		return loginid;
	}

	@JsonProperty("language1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = false/* , defaultValue = "" */)
	public String getLanguage1() {
		return language1;
	}

	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
	public String getExtnwcode() {
		return extnwcode;
	}


	@JsonProperty("password")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2468", required = false/* , defaultValue = "" */)
	public String getPassword() {
		return password;
	}

	@JsonProperty("pin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2468", required = false/* , defaultValue = "" */)
	public String getPin() {
		return pin;
	}

	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */)
	public String getMsisdn() {
		return msisdn;
	}

	@JsonProperty("serviceType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "RC", required = true/* , defaultValue = "" */)
	public String getServiceType() {
		return serviceType;
	}

	// Setter Methods

	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
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
	

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("C2CTotalTrfRequestMessage [extcode=");
		sb.append(extcode);
		sb.append(", loginid=");
		sb.append(loginid);
		sb.append(", language1=");
		sb.append(language1);
		sb.append(", extnwcode=");
		sb.append(extnwcode);
		sb.append(", password=");
		sb.append(password);
		sb.append(", pin=");
		sb.append(pin);
		sb.append( ", msisdn=");
		sb.append(msisdn);
		return sb.toString();
		}

	
	

}
