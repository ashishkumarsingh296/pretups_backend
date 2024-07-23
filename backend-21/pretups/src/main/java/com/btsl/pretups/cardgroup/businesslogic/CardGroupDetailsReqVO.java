package com.btsl.pretups.cardgroup.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * Created 24/10/2019
 * @author akhilesh.mittal1
 *
 */
public class CardGroupDetailsReqVO {

	@JsonProperty("identifierType")
	private String identifierType;
	
	@JsonProperty("identifierValue")
	private String identifierValue;
	
	@JsonProperty("data")
	Data data;

	// Getter Methods

	@JsonProperty("identifierType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "btnadm", required = true/* , defaultValue = "" */)
	public String getIdentifierType() {
		return identifierType;
	}

	@JsonProperty("identifierValue")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getIdentifierValue() {
		return identifierValue;
	}

	@JsonProperty("data")
	public Data getData() {
		return data;
	}

	// Setter Methods

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}

	public void setData(Data data) {
		this.data = data;
	}
}
