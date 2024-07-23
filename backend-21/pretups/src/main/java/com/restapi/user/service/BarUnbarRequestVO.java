package com.restapi.user.service;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"module",
"msisn",
"userName",
"userType",
"bar"
})
	public class BarUnbarRequestVO {
	
	@JsonProperty("module")
	private String module;
	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("userName")
	private String userName;
	@JsonProperty("userType")
	private String userType;
	@JsonProperty("bar")
	private List<Bar> bar = null;
	
	@JsonProperty("module")
	@io.swagger.v3.oas.annotations.media.Schema(example = "C2S", required = true, defaultValue = "Module")
	public String getModule() {
	return module;
	}
	
	@JsonProperty("module")
	public void setModule(String module) {
	this.module = module;
	}
	
	@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true, defaultValue = "Msisdn")
	public String getMsisdn() {
	return msisdn;
	}
	
	@JsonProperty("msisdn")
	public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
	}
	
	@JsonProperty("userName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "deepdist", required = true, defaultValue = "User Name")
	public String getUserName() {
	return userName;
	}
	
	@JsonProperty("userName")
	public void setUserName(String userName) {
	this.userName = userName;
	}
	
	@JsonProperty("userType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "SENDER", required = true, defaultValue = "User Type")
	public String getUserType() {
	return userType;
	}
	
	@JsonProperty("userType")
	public void setUserType(String userType) {
	this.userType = userType;
	}
	
	@JsonProperty("bar")
	public List<Bar> getBar() {
	return bar;
	}
	
	@JsonProperty("bar")
	public void setBar(List<Bar> bar) {
	this.bar = bar;
	}
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"barringReason",
"barringType"
})
	class Bar {
	
	@JsonProperty("barringReason")
	private String barringReason;
	@JsonProperty("barringType")
	private String barringType;
	
	@JsonProperty("barringReason")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Reason is Barred by test", required = true, defaultValue = "Barring Reason")
	public String getBarringReason() {
	return barringReason;
	}
	
	@JsonProperty("barringReason")
	public void setBarringReason(String barringReason) {
	this.barringReason = barringReason;
	}
	
	@JsonProperty("barringType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "C2S:SL017", required = true, defaultValue = "Barring Type")
	public String getBarringType() {
	return barringType;
	}
	
	@JsonProperty("barringType")
	public void setBarringType(String barringType) {
	this.barringType = barringType;
	}
}