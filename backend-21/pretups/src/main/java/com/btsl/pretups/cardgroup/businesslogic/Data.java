package com.btsl.pretups.cardgroup.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * Created 24/10/2019
 * @author akhilesh.mittal1
 *
 */
public class Data {
	
	
	@JsonProperty("serviceTypeDesc")
	private String serviceTypeDesc;
	
	@JsonProperty("subServiceTypeDesc")
	private String subServiceTypeDesc;
		
	@JsonProperty("cardGroupSetId")
	private String cardGroupSetId;
	
	@JsonProperty("version")
	private String version;

	@JsonProperty("networkCode")
	private String networkCode;
	
	@JsonProperty("numberOfDays")
	private String numberOfDays;

	// Getter Methods

	@JsonProperty("serviceTypeDesc")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Voucher Consumption", required = true/* , defaultValue = "" */)
	public String getServiceTypeDesc() {
		return serviceTypeDesc;
	}

	@JsonProperty("subServiceTypeDesc")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Choice RC 1", required = true/* , defaultValue = "" */)
	public String getSubServiceTypeDesc() {
		return subServiceTypeDesc;
	}

	@JsonProperty("cardGroupSetId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "3118", required = true/* , defaultValue = "" */)
	public String getCardGroupSetId() {
		return cardGroupSetId;
	}

	@JsonProperty("version")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */)
	public String getVersion() {
		return version;
	}

	@JsonProperty("networkCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
	public String getNetworkCode() {
		return networkCode;
	}

	@JsonProperty("numberOfDays")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30", required = true/* , defaultValue = "" */)
	public String getNumberOfDays() {
		return numberOfDays;
	}

	// Setter Methods

	public void setServiceTypeDesc(String serviceTypeDesc) {
		this.serviceTypeDesc = serviceTypeDesc;
	}

	public void setSubServiceTypeDesc(String subServiceTypeDesc) {
		this.subServiceTypeDesc = subServiceTypeDesc;
	}

	public void setCardGroupSetId(String cardGroupSetId) {
		this.cardGroupSetId = cardGroupSetId;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public void setNumberOfDays(String numberOfDays) {
		this.numberOfDays = numberOfDays;
	}
}