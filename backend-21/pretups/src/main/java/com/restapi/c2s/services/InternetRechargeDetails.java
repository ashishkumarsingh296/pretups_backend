package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUserData;
import com.fasterxml.jackson.annotation.JsonProperty;



public class InternetRechargeDetails extends OAuthUserData{
	
	@JsonProperty("date")
	private String date;
	
	@JsonProperty("date")
	@io.swagger.v3.oas.annotations.media.Schema(example = "11/05/20"/* , defaultValue = "" */)
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
	public String getExtnwcode() {
		return extnwcode;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}
	
	
	//@JsonProperty("msisdn")
	//@io.swagger.v3.oas.annotations.media.Schema(example = "726576538", required = true/* , defaultValue = "" */)
	
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	@JsonProperty("pin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
	
	//@JsonProperty("loginid")
	//@io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true/* , defaultValue = "" */)
	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	
	//@JsonProperty("password")
	//@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	//@JsonProperty("extcode")
	//@io.swagger.v3.oas.annotations.media.Schema(example = "22435", required = true/* , defaultValue = "" */)
	public String getExtcode() {
		return extcode;
	}

	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}
	@JsonProperty("extrefnum")
	@io.swagger.v3.oas.annotations.media.Schema(example = "673727"/* , defaultValue = "" */)
	public String getExtrefnum() {
		return extrefnum;
	}

	public void setExtrefnum(String extrefnum) {
		this.extrefnum = extrefnum;
	}
	@JsonProperty("msisdn2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "728768738", required = true/* , defaultValue = "" */)
	public String getMsisdn2() {
		return msisdn2;
	}

	public void setMsisdn2(String msisdn2) {
		this.msisdn2 = msisdn2;
	}
	@JsonProperty("amount")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12", required = true/* , defaultValue = "" */)
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	@JsonProperty("language1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0"/* , defaultValue = "" */)
	public String getLanguage1() {
		return language1;
	}

	public void setLanguage1(String language1) {
		this.language1 = language1;
	}
	@JsonProperty("language2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0"/* , defaultValue = "" */)
	public String getLanguage2() {
		return language2;
	}

	public void setLanguage2(String language2) {
		this.language2 = language2;
	}
	@JsonProperty("selector")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */)
	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	@JsonProperty("extnwcode")
	private String extnwcode;
	
	//@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String msisdn;
	
	@JsonProperty("pin")
	private String pin;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)//@JsonProperty("loginid")
	private String loginid;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)//@JsonProperty("password")
	private String password;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)//@JsonProperty("extcode")
	private String extcode;
	
	@JsonProperty("extrefnum")
	private String extrefnum;
	
	@JsonProperty("msisdn2")
	private String msisdn2;
	
	@JsonProperty("amount")
	private String amount;
	
	@JsonProperty("language1")
	private String language1;
	
	@JsonProperty("language2")
	private String language2;
	
	@JsonProperty("selector")
	private String selector;
	
	@JsonProperty("notifMsisdn")
	private String notifMsisdn;

	@JsonProperty("notifMsisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72525252", required = true/* , defaultValue = "" */)
	public String getNotifMsisdn() {
		return notifMsisdn;
	}

	public void setNotifMsisdn(String notifMsisdn) {
		this.notifMsisdn = notifMsisdn;
	}

}
