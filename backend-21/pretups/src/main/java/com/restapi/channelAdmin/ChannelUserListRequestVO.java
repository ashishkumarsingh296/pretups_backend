package com.restapi.channelAdmin;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ChannelUserListRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("msisdn")
	private String msisdn;

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("loginID")
	private String loginID;

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("domain")
	private String domain;

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("userCategory")
	private String userCategory;

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("parentUserID")
	private String parentUserID;

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("geography")
	private String geography;

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("status")
	private String status;

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("extCode")
	private String extCode;

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUserCategory() {
		return userCategory;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	public String getParentUserID() {
		return parentUserID;
	}

	public void setParentUserID(String parentUserID) {
		this.parentUserID = parentUserID;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExtCode() {
		return extCode;
	}

	public void setExtCode(String extCode) {
		this.extCode = extCode;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ChannelUserListRequestVO [msisdn=");
		stringBuilder.append(msisdn);
		stringBuilder.append(", loginID=");
		stringBuilder.append(loginID);
		stringBuilder.append(", domain=");
		stringBuilder.append(domain);
		stringBuilder.append(", userCategory=");
		stringBuilder.append(userCategory);
		stringBuilder.append(", parentUserID=");
		stringBuilder.append(parentUserID);
		stringBuilder.append(", geography=");
		stringBuilder.append(geography);
		stringBuilder.append(", status=");
		stringBuilder.append(status);
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

}
