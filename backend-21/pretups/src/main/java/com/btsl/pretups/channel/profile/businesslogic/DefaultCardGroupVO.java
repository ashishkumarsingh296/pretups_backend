package com.btsl.pretups.channel.profile.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class DefaultCardGroupVO {
	
	@JsonProperty("identifierType")
	String identifierType;
	
	@JsonProperty("identifierValue")
	String identifierValue;
	
	@JsonProperty("networkCode")
	String networkCode;
	
	@JsonProperty("userId")
	String userId;
	
	@JsonProperty("serviceTypeId")
	String serviceTypeId;
	
	@JsonProperty("subServiceTypeId")
	String subServiceTypeId;
	
	@JsonProperty("cardGroupSetId")
	String cardGroupSetId;
	
	@JsonProperty("moduleCode")
	String moduleCode;

	@JsonProperty("identifierType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "btnadm", required = true/* , defaultValue = "" */)
	public String getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	@JsonProperty("identifierValue")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getIdentifierValue() {
		return identifierValue;
	}

	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}

	@JsonProperty("networkCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	
	@JsonProperty("userId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "SYSTEM", required = true/* , defaultValue = "" */)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@JsonProperty("serviceTypeId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */)
	public String getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(String serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	@JsonProperty("subServiceTypeId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "RC", required = true/* , defaultValue = "" */)
	public String getSubServiceTypeId() {
		return subServiceTypeId;
	}

	public void setSubServiceTypeId(String subServiceTypeId) {
		this.subServiceTypeId = subServiceTypeId;
	}

	@JsonProperty("cardGroupSetId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "3099", required = true/* , defaultValue = "" */)
	public String getCardGroupSetId() {
		return cardGroupSetId;
	}

	public void setCardGroupSetId(String cardGroupSetId) {
		this.cardGroupSetId = cardGroupSetId;
	}

	@JsonProperty("moduleCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "P2P", required = true/* , defaultValue = "" */)
	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
}
