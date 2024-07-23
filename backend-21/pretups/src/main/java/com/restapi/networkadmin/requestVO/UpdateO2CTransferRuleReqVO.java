package com.restapi.networkadmin.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;



public class UpdateO2CTransferRuleReqVO {
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("lastModifiedTime")
	private long lastModifiedTime;

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

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("parentAssocationAllowed")
	private String parentAssocationAllowed;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("toCategoryDes")
	private String toCategoryDes;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("transferRuleId")
	private String transferRuleId;
	
	
	public String getTransferRuleId() {
		return transferRuleId;
	}

	public void setTransferRuleId(String transferRuleId) {
		this.transferRuleId = transferRuleId;
	}

	public String getParentAssocationAllowed() {
		return parentAssocationAllowed;
	}

	public void setParentAssocationAllowed(String parentAssocationAllowed) {
		this.parentAssocationAllowed = parentAssocationAllowed;
	}

	public String getToCategoryDes() {
		return toCategoryDes;
	}

	public void setToCategoryDes(String toCategoryDes) {
		this.toCategoryDes = toCategoryDes;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

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
