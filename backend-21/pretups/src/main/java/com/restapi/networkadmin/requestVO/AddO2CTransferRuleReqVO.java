package com.restapi.networkadmin.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;



public class AddO2CTransferRuleReqVO {
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("domainCode")
	private String domainCode;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("toCategory")
	private String toCategory;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("transferAllowed")
	private String transferAllowed;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("focAllowed")
	private String focAllowed;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("dpAllowed")
	private String dpAllowed;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("withdrawAllowed")
	private String withdrawAllowed;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("returnAllowed")
	private String returnAllowed;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("firstApprovalLimit")
	private Long firstApprovalLimit;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("secondApprovalLimit")
	private Long secondApprovalLimit;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("productArray")
	private String[] productArray;

	
	
	
	
	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	
	public String getToCategory() {
		return toCategory;
	}

	public void setToCategory(String toCategory) {
		this.toCategory = toCategory;
	}

	public String getTransferAllowed() {
		return transferAllowed;
	}

	public void setTransferAllowed(String transferAllowed) {
		this.transferAllowed = transferAllowed;
	}

	public String getFocAllowed() {
		return focAllowed;
	}

	public void setFocAllowed(String focAllowed) {
		this.focAllowed = focAllowed;
	}

	public String getDpAllowed() {
		return dpAllowed;
	}

	public void setDpAllowed(String dpAllowed) {
		this.dpAllowed = dpAllowed;
	}

	public String getWithdrawAllowed() {
		return withdrawAllowed;
	}

	public void setWithdrawAllowed(String withdrawAllowed) {
		this.withdrawAllowed = withdrawAllowed;
	}

	public String getReturnAllowed() {
		return returnAllowed;
	}

	public void setReturnAllowed(String returnAllowed) {
		this.returnAllowed = returnAllowed;
	}

	public Long getFirstApprovalLimit() {
		return firstApprovalLimit;
	}

	public void setFirstApprovalLimit(Long firstApprovalLimit) {
		this.firstApprovalLimit = firstApprovalLimit;
	}

	public Long getSecondApprovalLimit() {
		return secondApprovalLimit;
	}

	public void setSecondApprovalLimit(Long secondApprovalLimit) {
		this.secondApprovalLimit = secondApprovalLimit;
	}

	public String[] getProductArray() {
		return productArray;
	}

	public void setProductArray(String[] productArray) {
		this.productArray = productArray;
	}
		
	
}
