package com.btsl.pretups.channel.profile.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class CardGroupStatusVO {

	@JsonProperty("identifierType")
	private String identifierType;
	
	@JsonProperty("identifierValue")
	private String identifierValue;
	
	@JsonProperty("moduleCode")
	private String moduleCode;
	
	@JsonProperty("networkCode")
	private String networkCode;
		
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


	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class UserPasswordManagementVO {\n");
	    
	    sb.append("    identifierType: ").append(identifierType).append("\n");
	    sb.append("    identifierValue: ").append(identifierValue).append("\n");
	    sb.append("    moduleCode: ").append(moduleCode).append("\n");
	    sb.append("    networkCode: ").append(networkCode).append("\n");
	    return sb.toString();
	 }
	
}
