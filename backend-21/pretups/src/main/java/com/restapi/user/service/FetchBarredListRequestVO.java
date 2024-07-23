package com.restapi.user.service;



import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"msisdn",
"userName",
"fromDate",
"todate",
"domain",
"category",
"geography",
"userType",
"module",
"barredAs",
"barredtype"
})
@Generated("jsonschema2pojo")
	public class FetchBarredListRequestVO {
	
	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("userName")
	private String userName;
	@JsonProperty("fromDate")
	private String fromDate;
	@JsonProperty("todate")
	private String todate;
	@JsonProperty("domain")
	private String domain;
	@JsonProperty("category")
	private String category;
	@JsonProperty("geography")
	private String geography;
	@JsonProperty("userType")
	private String userType;
	@JsonProperty("module")
	private String module;
	@JsonProperty("barredAs")
	private String barredAs;
	@JsonProperty("barredtype")
	private String barredtype;
	
	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72147852369", required = true, defaultValue = "Msisdn")
	public String getMsisdn() {
	return msisdn;
	}
	
	@JsonProperty("msisdn")
	public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
	}
	
	@JsonProperty("userName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya_dist", required = true, defaultValue = "userName")
	public String getUserName() {
	return userName;
	}
	
	@JsonProperty("userName")
	public void setUserName(String userName) {
	this.userName = userName;
	}
	
	@JsonProperty("fromDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "01/05/21", required = true, defaultValue = "fromDate")
	public String getFromDate() {
	return fromDate;
	}
	
	@JsonProperty("fromDate")
	public void setFromDate(String fromDate) {
	this.fromDate = fromDate;
	}
	
	@JsonProperty("todate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "25/05/21", required = true, defaultValue = "toDate")
	public String getTodate() {
	return todate;
	}
	
	@JsonProperty("todate")
	public void setTodate(String todate) {
	this.todate = todate;
	}
	
	@JsonProperty("domain")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true, defaultValue = "domain")
	public String getDomain() {
	return domain;
	}
	
	@JsonProperty("domain")
	public void setDomain(String domain) {
	this.domain = domain;
	}
	
	@JsonProperty("category")
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true, defaultValue = "category")
	public String getCategory() {
	return category;
	}
	
	@JsonProperty("category")
	public void setCategory(String category) {
	this.category = category;
	}
	
	@JsonProperty("geography")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true, defaultValue = "geography")
	public String getGeography() {
	return geography;
	}
	
	@JsonProperty("geography")
	public void setGeography(String geography) {
	this.geography = geography;
	}
	
	@JsonProperty("userType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "STAFF", required = true, defaultValue = "userType")
	public String getUserType() {
	return userType;
	}
	
	@JsonProperty("userType")
	public void setUserType(String userType) {
	this.userType = userType;
	}
	
	@JsonProperty("module")
	@io.swagger.v3.oas.annotations.media.Schema(example = "C2S", required = true, defaultValue = "module")
	public String getModule() {
	return module;
	}
	
	@JsonProperty("module")
	public void setModule(String module) {
	this.module = module;
	}
	
	
	@JsonProperty("barredAs")
	@io.swagger.v3.oas.annotations.media.Schema(example = "SENDER", required = true, defaultValue = "barredAs")
	public String getBarredAs() {
	return barredAs;
	}
	
	@JsonProperty("barredAs")
	public void setBarredAs(String barredAs) {
	this.barredAs = barredAs;
	}
	

	@JsonProperty("barredtype")
	@io.swagger.v3.oas.annotations.media.Schema(example = "C2S:SL017", required = true, defaultValue = "barredType")
	public String getBarredtype() {
	return barredtype;
	}
	
	@JsonProperty("barredtype")
	public void setBarredtype(String barredtype) {
	this.barredtype = barredtype;
	}

	@Override
	public String toString() {
		return "FetchBarredListRequestVO [msisdn=" + msisdn + ", userName="
				+ userName + ", fromDate=" + fromDate + ", todate=" + todate
				+ ", domain=" + domain + ", category=" + category
				+ ", geography=" + geography + ", userType=" + userType
				+ ", module=" + module + ", barredAs=" + barredAs
				+ ", barredtype=" + barredtype + "]";
	}
	
	
}