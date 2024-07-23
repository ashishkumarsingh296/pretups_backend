package com.btsl.pretups.channeluser.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



 public class Msisdn{
	
	@JsonProperty("phoneNo")
	String phoneNo;
	
	@JsonProperty("pin")
	String pin;
	
	@JsonProperty("confirmPin")
	String confirmPin;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getConfirmPin() {
		return confirmPin;
	}

	public void setConfirmPin(String confirmPin) {
		this.confirmPin = confirmPin;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */)
	public String getIsprimary() {
		return isprimary;
	}

	public void setIsprimary(String isprimary) {
		this.isprimary = isprimary;
	}

	@JsonProperty("isprimary")
	String isprimary;


	@io.swagger.v3.oas.annotations.media.Schema(example = "72766677", required = true/* , defaultValue = "" */)
	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	@JsonProperty("description")
	String description;

	@io.swagger.v3.oas.annotations.media.Schema(example = "phone no description", required = true/* , defaultValue = "" */)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "SE", required = true/* , defaultValue = "" */)
	public String getStkProfile() {
		return stkProfile;
	}

	public void setStkProfile(String stkProfile) {
		this.stkProfile = stkProfile;
	}

	@JsonProperty("stkProfile")
	String stkProfile;
	
	
}
