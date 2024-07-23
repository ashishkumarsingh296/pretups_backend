package com.restapi.channelenquiry.service;

import com.fasterxml.jackson.annotation.JsonProperty;



public class AlertCounterSummaryRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "DD/MM/YY", required = true)
	@JsonProperty("reqDate")
	private String reqDate;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "MM/YY", required = true)
	@JsonProperty("reqMonth")
	private String reqMonth;

	
	@io.swagger.v3.oas.annotations.media.Schema(example = "HARYANA", required = true)
	@JsonProperty("geoCode")
	private String geoCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true)
	@JsonProperty("domainCode")
	private String domainCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "RET", required = true)
	@JsonProperty("catCode")
	private String catCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true)
	@JsonProperty("thresholdType")
	private String thresholdType;

	public String getReqDate() {
		return reqDate;
	}

	public void setReqDate(String reqDate) {
		this.reqDate = reqDate;
	}

	public String getReqMonth() {
		return reqMonth;
	}

	public void setReqMonth(String reqMonth) {
		this.reqMonth = reqMonth;
	}

	public String getGeoCode() {
		return geoCode;
	}

	public void setGeoCode(String geoCode) {
		this.geoCode = geoCode;
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

	public String getThresholdType() {
		return thresholdType;
	}

	public void setThresholdType(String thresholdType) {
		this.thresholdType = thresholdType;
	}

	@Override
	public String toString() {
		return "AlertCounterSummaryRequestVO [reqDate=" + reqDate + ", reqMonth=" + reqMonth + ", geoCode=" + geoCode
				+ ", domainCode=" + domainCode + ", catCode=" + catCode + ", thresholdType=" + thresholdType + "]";
	}
	
	
	
}
