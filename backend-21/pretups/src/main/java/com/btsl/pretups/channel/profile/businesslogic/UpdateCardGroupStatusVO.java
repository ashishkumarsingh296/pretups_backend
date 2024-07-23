package com.btsl.pretups.channel.profile.businesslogic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;



public class UpdateCardGroupStatusVO {
		
	@JsonProperty("identifierType")
	String identifierType;
	
	@JsonProperty("identifierValue")
	String identifierValue;
	
	@JsonProperty("moduleCode")
	String moduleCode;
	
	@JsonProperty("networkCode")
	String networkCode;
	
	List<CardGroupSet>cardGroupSetList;

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

	@JsonProperty("moduleCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "P2P", required = true/* , defaultValue = "" */)
	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	@JsonProperty("networkCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public List<CardGroupSet> getCardGroupSetList() {
		return cardGroupSetList;
	}

	public void setCardGroupSetList(List<CardGroupSet> cardGroupSetList) {
		this.cardGroupSetList = cardGroupSetList;
	}
	
	

}
