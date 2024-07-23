package com.btsl.pretups.channel.profile.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class SearchUserRequestVO {

	@JsonProperty("identifierType")
	private String identifierType;
	
	@JsonProperty("identifierValue")
	private String identifierValue;
	
	@JsonProperty("category")
	private int category;
	
	@JsonProperty("searchValue")
	private String searchValue;
	
	@JsonProperty("searchValue")
	@io.swagger.v3.oas.annotations.media.Schema(example = "%%", required = true/* , defaultValue = "" */)
	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	@JsonProperty("category")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */)
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	@JsonProperty("identifierType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true/* , defaultValue = "" */)
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
	
}
