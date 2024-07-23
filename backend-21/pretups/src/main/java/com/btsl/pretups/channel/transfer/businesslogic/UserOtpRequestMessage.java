package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;




/**
 * Help class - UserOtp Request Class
 * @author anshul.goyal2
 *
 */

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class UserOtpRequestMessage {


	
	
	@JsonProperty("loginid")
	private String loginid;
	
	
	@JsonProperty("msisdn")
	private String msisdn;
	
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@JsonProperty("language1")
	private String language1;
	
	@JsonProperty("language2")
	private String language2;
	
	@JsonProperty("otp")
	private String otp;
	
	@JsonProperty("newpin")
	private String newpin;
	
	@JsonProperty("confirmpin")
	private String confirmpin;
	
	
	

	
	@JsonProperty("loginid")
	@io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = false/* , defaultValue = "" */)
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
	@JsonProperty("otp")
	@io.swagger.v3.oas.annotations.media.Schema(example = "723000", required = true/* , defaultValue = "" */)
	public String getOtp() {
		return otp;
	}
	@JsonProperty("newpin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "7230", required = true/* , defaultValue = "" */)
	public String getNewpin() {
		return newpin;
	}
	@JsonProperty("confirmpin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "7230", required = true/* , defaultValue = "" */)
	public String getConfirmpin() {
		return confirmpin;
	}

	
	// Setter Methods
	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	public void setConfirmpin(String confirmpin) {
		this.confirmpin = confirmpin;
	}
	

	public void setNewpin(String newpin) {
		this.newpin = newpin;
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
		return "UserOtpRequestMessage [loginid=" + loginid + ", language2=" + language2
				+ ", language1=" + language1 + ", extnwcode=" + extnwcode + ", otp=" + otp + ", newPin="
				+ newpin +  ", confirmpin=" + confirmpin + ", msisdn=" + msisdn + "]";
	}

	
	

}
