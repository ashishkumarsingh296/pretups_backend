package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUserData;
import com.fasterxml.jackson.annotation.JsonProperty;



public class C2SRechargeReversalDetails extends OAuthUserData {
	@JsonProperty("txnid")
	private String txnid;
	
	@JsonProperty("txnid")
	@io.swagger.v3.oas.annotations.media.Schema(example = "R2657676.68768.999", required = true/* , defaultValue = "" */, description="Transaction ID")
	public String getTxnid() {
		return txnid;
	}

	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */, description="NetworkCode")
	public String getExtNwCode() {
		return extnwcode;
	}

	public void setExtNwCode(String extnwcode) {
		this.extnwcode = extnwcode;
	}
	

	
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)//@JsonProperty("loginid")
	private String loginid;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)//@JsonProperty("password")
	private String password;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)//@JsonProperty("extcode")
	private String extcode;
	
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
}
