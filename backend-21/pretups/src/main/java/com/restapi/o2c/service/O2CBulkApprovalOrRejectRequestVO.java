package com.restapi.o2c.service;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class O2CBulkApprovalOrRejectRequestVO  extends OAuthUser{
	@JsonProperty("data")
    private O2CBulkApprovalOrRejectRequestData o2CBulkApprovalOrRejectRequestData = null;

	
	@JsonProperty("data")
	public O2CBulkApprovalOrRejectRequestData getO2CBulkApprovalOrRejectRequestData() {
		return o2CBulkApprovalOrRejectRequestData;
	}
	@JsonProperty("data")
	public void setO2CBulkApprovalOrRejectRequestData(
			O2CBulkApprovalOrRejectRequestData o2cBulkApprovalOrRejectRequestData) {
		o2CBulkApprovalOrRejectRequestData = o2cBulkApprovalOrRejectRequestData;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2CBulkApprovalOrRejectRequestVO [o2CBulkApprovalOrRejectRequestData=");
		builder.append(o2CBulkApprovalOrRejectRequestData);
		builder.append("]");
		return builder.toString();
	}
	


}
class O2CBulkApprovalOrRejectRequestData{
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "T/W", required = true)
	@JsonProperty("service")
	private String service;
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = false)
	@JsonProperty("language1")
	private String language1;
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "2", required = false)
	@JsonProperty("language2")
	private String language2;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "eTopUP/PosteTopUP	", required = false)
	@JsonProperty("product")
	private String product;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "123", required = false)
	@JsonProperty("pin")
	private String pin;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "batchname", required = true)
	@JsonProperty("batchName")
	private String batchName;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "approval1/appraval2/approval3", required = true)
	@JsonProperty("requestType")
	private String requestType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "reject/approve", required = true)
	@JsonProperty("request")
	private String request;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "NGOB161220.011", required = true)
	@JsonProperty("BatchId")
	private String BatchId;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "remarks", required = false)
	@JsonProperty("Remarks")
	private String Remarks;
	
	@JsonProperty("language1")
	public String getLanguage1() {
	return language1;
	}
	
	@JsonProperty("service")
	public String getService() {
		return service;
	}
	
	public void setService(String service) {
		this.service = service;
	}
	
	@JsonProperty("requestType")
	public String getRequestType() {
		return requestType;
	}
	@JsonProperty("requestType")
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	@JsonProperty("BatchId")
	public String getBatchId() {
		return BatchId;
	}
	@JsonProperty("BatchId")
	public void setBatchId(String batchId) {
		BatchId = batchId;
	}
	@JsonProperty("Remarks")
	public String getRemarks() {
		return Remarks;
	}
	@JsonProperty("Remarks")
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

	@JsonProperty("language1")
	public void setLanguage1(String language1) {
	this.language1 = language1;
	}
	
	@JsonProperty("language2")
	public String getLanguage2() {
	return language2;
	}
	
	@JsonProperty("language2")
	public void setLanguage2(String language2) {
	this.language2 = language2;
	}
	
	@JsonProperty("request")
	public String getRequest() {
		return request;
	}
	
	
	@JsonProperty("request")
	public void setRequest(String request) {
		this.request = request;
	}
	@JsonProperty("product")
	public String getProduct() {
	return product;
	}
	
	@JsonProperty("product")
	public void setProduct(String product) {
	this.product = product;
	}
	
	@JsonProperty("pin")
	public String getPin() {
	return pin;
	}
	
	@JsonProperty("pin")
	public void setPin(String pin) {
	this.pin = pin;
	}
	
	@JsonProperty("batchName")
	public String getBatchName() {
	return batchName;
	}
	
	@JsonProperty("batchName")
	public void setBatchName(String batchName) {
	this.batchName = batchName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2CBulkApprovalOrRejectRequestData [service=");
		builder.append(service);
		builder.append(", language1=");
		builder.append(language1);
		builder.append(", language2=");
		builder.append(language2);
		builder.append(", product=");
		builder.append(product);
		builder.append(", pin=");
		builder.append(pin);
		builder.append(", batchName=");
		builder.append(batchName);
		builder.append(", requestType=");
		builder.append(requestType);
		builder.append(", request=");
		builder.append(request);
		builder.append(", BatchId=");
		builder.append(BatchId);
		builder.append(", Remarks=");
		builder.append(Remarks);
		builder.append("]");
		return builder.toString();
	}
	
	
}