package com.restapi.user.service;

import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BarredVo extends BarredUserVO{
	
	@JsonProperty("domainCode")
	private String domainCode;
	@JsonProperty("domainName")
	private String domainName;
	@JsonProperty("categoryCode")
	private String categoryCode;
	@JsonProperty("barredAs")
	private String barredAs;
	@JsonProperty("categoryName")
	private String categoryName;
	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("domainCode")	
	public String getDomainCode() {
		return domainCode;
	}

	@JsonProperty("domainCode")
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	@JsonProperty("domainName")
	public String getDomainName() {
		return domainName;
	}

	@JsonProperty("domainName")
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@JsonProperty("barredAs")
	public String getBarredAs() {
	return barredAs;
	}
	
	@JsonProperty("barredAs")
	public void setBarredAs(String barredAs) {
	this.barredAs = barredAs;
	}

	@JsonProperty("categoryName")
	public String getCategoryName() {
		return categoryName;
	}

	@JsonProperty("categoryName")
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@JsonProperty("categoryCode")
	public String getCategoryCode() {
		return categoryCode;
	}
	
	@JsonProperty("categoryCode")
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	@JsonProperty("loginId")
	public String getLoginId() {
		return loginId;
	}

	@JsonProperty("loginId")
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	
  }	
