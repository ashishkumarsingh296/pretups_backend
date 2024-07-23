package com.restapi.channeluser.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;



public class ChannelUserSearchReqVo {
	

	@JsonProperty("searchType")
	private String searchType;
	
	@JsonProperty("userStatus")
	private String userStatus;
	
	@JsonProperty("mobileNumber")
	private String mobileNumber;
	
	@JsonProperty("loginID")
	private String loginID;
	
	@JsonProperty("domain")
	private String domain;
	
	@JsonProperty("category")
	private String category;
	
	@JsonProperty("geography")
	private String geography;
	
	@JsonProperty("loggedUserNeworkCode")
	private String loggedUserNeworkCode;
	
	
	@JsonIgnore
	private String loggedInUserUserid;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "LOGINID/MSISDN/ADVANCED", required = true/* , defaultValue = "" */,description="searchType")
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "SR/S/DR/Y/N/A", required = true/* , defaultValue = "" */,description="userStatus")
	public String getUserStatus() {
		return userStatus;
	}
	
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "7212345609", required = true/* , defaultValue = "" */,description="mobileNumber")
	public String getMobileNumber() {
		return mobileNumber;
	}
	
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "appdist", required = true/* , defaultValue = "" */,description="loginID")
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	
	@JsonProperty("domain")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */,description="Domain")
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */,description="Category")
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DELHI", required = true/* , defaultValue = "" */,description="Geography")
	public String getGeography() {
		return geography;
	}
	public void setGeography(String geography) {
		this.geography = geography;
	}
	
	
	//@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */,description="loggedInUserUserid")
	@JsonIgnore
	public String getLoggedInUserUserid() {
		return loggedInUserUserid;
	}
	
	@JsonIgnore
	public void setLoggedInUserUserid(String loggedInUserUserid) {
		this.loggedInUserUserid = loggedInUserUserid;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */,description="loggedUserNeworkCode")
	public String getLoggedUserNeworkCode() {
		return loggedUserNeworkCode;
	}
	public void setLoggedUserNeworkCode(String loggedUserNeworkCode) {
		this.loggedUserNeworkCode = loggedUserNeworkCode;
	}
	
	
	
	
	
	

}
