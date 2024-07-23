package com.restapi.o2c.service;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class CommisionBulkApprovalOrRejectRequestVO extends OAuthUser{
	@JsonProperty("data")
    private CommisionBulkApprovalOrRejectRequestData commisionBulkApprovalOrRejectRequestData = null;

	
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("commisionBulkApprovalOrRejectRequestData [commisionBulkApprovalOrRejectRequestData=");
		builder.append(commisionBulkApprovalOrRejectRequestData);
		builder.append("]");
		return builder.toString();
	}




	public CommisionBulkApprovalOrRejectRequestData getCommisionBulkApprovalOrRejectRequestData() {
		return commisionBulkApprovalOrRejectRequestData;
	}




	public void setCommisionBulkApprovalOrRejectRequestData(
			CommisionBulkApprovalOrRejectRequestData commisionBulkApprovalOrRejectRequestData) {
		this.commisionBulkApprovalOrRejectRequestData = commisionBulkApprovalOrRejectRequestData;
	}
	


}
class CommisionBulkApprovalOrRejectRequestData{
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = false)
	@JsonProperty("language1")
	private String language1;
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "2", required = false)
	@JsonProperty("language2")
	private String language2;
	
	
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
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "NGDP210205.005", required = true)
	@JsonProperty("BatchId")
	private String BatchId;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "remarks", required = false)
	@JsonProperty("Remarks")
	private String Remarks;
	
	@JsonProperty("language1")
	public String getLanguage1() {
	return language1;
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
		builder.append("CommisionBulkApprovalOrRejectRequestData [language1=");
		builder.append(language1);
	
		builder.append(", language2=");
		builder.append(language2);
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