package com.btsl.pretups.channel.profile.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class CalculateVoucherTransferRuleVO {
	
	@JsonProperty("identifierType")
	String identifierType;
	
	@JsonProperty("identifierValue")
	String identifierValue;
	
	@JsonProperty("networkCode")
	String networkCode;
	
	@JsonProperty("userId")
	String userId;
	
	@JsonProperty("moduleType")
	String moduleType;
	
	@JsonProperty("gatewayCode")
	String gatewayCode;
	
	@JsonProperty("serviceType")
	String serviceType;
	
	@JsonProperty("subService")
	String subService;
	
	@JsonProperty("senderType")
	String senderType;
	
	@JsonProperty("senderServiceClass")
	String senderServiceClass;
	
	@JsonProperty("receiverType")
	String receiverType;
	
	@JsonProperty("receiverServiceClass")
	String receiverServiceClass;
	
	@JsonProperty("denomination")
	String denomination;
	
	@JsonProperty("voucherSegment")
	String voucherSegment;
	
	@JsonProperty("voucherType")
	String voucherType;
	
	@JsonProperty("productName")
	String productName;
	
	@JsonProperty("validityDate")
	String validityDate;
	
	@JsonProperty("applicableFrom")
	String applicableFrom;
	
	@JsonProperty("applicableTime")
	String applicableTime;

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

	@JsonProperty("moduleType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Peer to Peer", required = true/* , defaultValue = "" */)
	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	@JsonProperty("gatewayCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "EXTGW", required = true/* , defaultValue = "" */)
	public String getGatewayCode() {
		return gatewayCode;
	}

	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}

	@JsonProperty("serviceType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Voucher Consumption", required = true/* , defaultValue = "" */)
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	
	@JsonProperty("subService")
	@io.swagger.v3.oas.annotations.media.Schema(example = "CVG", required = true/* , defaultValue = "" */)
	public String getSubService() {
		return subService;
	}

	public void setSubService(String subService) {
		this.subService = subService;
	}

	@JsonProperty("senderType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Prepaid Subscriber", required = true/* , defaultValue = "" */)
	public String getSenderType() {
		return senderType;
	}

	public void setSenderType(String senderType) {
		this.senderType = senderType;
	}

	@JsonProperty("senderServiceClass")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL(ALL)", required = true/* , defaultValue = "" */)
	public String getSenderServiceClass() {
		return senderServiceClass;
	}

	public void setSenderServiceClass(String senderServiceClass) {
		this.senderServiceClass = senderServiceClass;
	}

	@JsonProperty("receiverType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Prepaid Subscriber", required = true/* , defaultValue = "" */)
	public String getReceiverType() {
		return receiverType;
	}

	public void setReceiverType(String receiverType) {
		this.receiverType = receiverType;
	}

	@JsonProperty("receiverServiceClass")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL(ALL)", required = true/* , defaultValue = "" */)
	public String getReceiverServiceClass() {
		return receiverServiceClass;
	}

	public void setReceiverServiceClass(String receiverServiceClass) {
		this.receiverServiceClass = receiverServiceClass;
	}

	@JsonProperty("denomination")
	@io.swagger.v3.oas.annotations.media.Schema(example = "102", required = true/* , defaultValue = "" */)
	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	@JsonProperty("voucherSegment")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
	public String getVoucherSegment() {
		return voucherSegment;
	}

	public void setVoucherSegment(String voucherSegment) {
		this.voucherSegment = voucherSegment;
	}

	@JsonProperty("voucherType")
	@io.swagger.v3.oas.annotations.media.Schema(required = false/* , defaultValue = "" */)
	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@JsonProperty("validityDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "23/08/19", required = true/* , defaultValue = "" */)
	public String getValidityDate() {
		return validityDate;
	}

	public void setValidityDate(String validityDate) {
		this.validityDate = validityDate;
	}

	@JsonProperty("applicableFrom")
	@io.swagger.v3.oas.annotations.media.Schema(example = "23/08/19", required = true/* , defaultValue = "" */)
	public String getApplicableFrom() {
		return applicableFrom;
	}

	public void setApplicableFrom(String applicableFrom) {
		this.applicableFrom = applicableFrom;
	}

	public String getApplicableTime() {
		return applicableTime;
	}

	@JsonProperty("applicableTime")
	@io.swagger.v3.oas.annotations.media.Schema(example = "16:23", required = true/* , defaultValue = "" */)
	public void setApplicableTime(String applicableTime) {
		this.applicableTime = applicableTime;
	}
	
	

}
