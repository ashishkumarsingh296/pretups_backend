package com.restapi.channelenquiry.service;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ClosingBalanceEnquiryRequestVO {


	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DD/MM/YY", required = true)
	@JsonProperty("fromDate")
	private String fromDate;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DD/MM/YY", required = true)
	@JsonProperty("toDate")
	private String toDate;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "HARYANA", required = true)
	@JsonProperty("zoneCode")
	private String zoneCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true)
	@JsonProperty("domainCode")
	private String domainCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "RET", required = true)
	@JsonProperty("catCode")
	private String catCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "rarya retailer", required = true)
	@JsonProperty("userName")
	private String userName;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true)
	@JsonProperty("fromAmount")
	private String fromAmount;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "999999", required = true)
	@JsonProperty("toAmount")
	private String toAmount;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "xlsx", required = true)
	@JsonProperty("fileType")
	private String fileType;

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(String zone) {
		this.zoneCode = zone;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getCatCode() {
		return catCode;
	}

	public void setCatCode(String catCode) {
		this.catCode = catCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFromAmount() {
		return fromAmount;
	}

	public void setFromAmount(String fromAmount) {
		this.fromAmount = fromAmount;
	}

	public String getToAmount() {
		return toAmount;
	}

	public void setToAmount(String toAmount) {
		this.toAmount = toAmount;
	}

	@Override
	public String toString() {
		return "ClosingBalanceEnquiryRequestVO [fromDate=" + fromDate + ", toDate=" + toDate + ", zone=" + zoneCode
				+ ", domainCode=" + domainCode + ", catCode=" + catCode + ", userName=" + userName + ", fromAmount="
				+ fromAmount + ", toAmount=" + toAmount + "]";
	}
	
	
	
}
