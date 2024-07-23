package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * Help class - SendOtpForForgotPinRequestParentVO
 * 
 *
 */

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class SendOtpForForgotPinData {
	

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
	
	
	@JsonProperty("msisdn")
	private String msisdn;

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
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = false/* , defaultValue = "" */)
	public String getLanguage2() {
		return language2;
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



	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */)
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


	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("C2CTotalTrfRequestMessage [extcode=");
		sb.append(extcode);
		sb.append(", loginid=");
		sb.append(loginid);
		sb.append(", language2=");
		sb.append(language2);
		sb.append(", language1=");
		sb.append(language1);
		sb.append(", extnwcode=");
		sb.append(extnwcode);
		sb.append(", password=");
		sb.append( ", msisdn=");
		sb.append(msisdn);
		return sb.toString();
		}

}

