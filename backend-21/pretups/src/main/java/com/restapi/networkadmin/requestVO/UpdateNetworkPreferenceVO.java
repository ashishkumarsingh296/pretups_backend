package com.restapi.networkadmin.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;



public class UpdateNetworkPreferenceVO {

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("networkCode")
	private String networkCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("preferenceCode")
	private String preferenceCode;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("preferenceValue")
	private String preferenceValue;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("preferenceValueType")
	private String preferenceValueType;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("allowAction")
	private String allowAction;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("lastModifiedTime")
	private Long lastModifiedTime;

	
	
	
	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getPreferenceCode() {
		return preferenceCode;
	}

	public void setPreferenceCode(String preferenceCode) {
		this.preferenceCode = preferenceCode;
	}

	public String getPreferenceValue() {
		return preferenceValue;
	}

	public void setPreferenceValue(String preferenceValue) {
		this.preferenceValue = preferenceValue;
	}

	public String getPreferenceValueType() {
		return preferenceValueType;
	}

	public void setPreferenceValueType(String preferenceValueType) {
		this.preferenceValueType = preferenceValueType;
	}

	public String getAllowAction() {
		return allowAction;
	}

	public void setAllowAction(String allowAction) {
		this.allowAction = allowAction;
	}

	public Long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	
	
}
