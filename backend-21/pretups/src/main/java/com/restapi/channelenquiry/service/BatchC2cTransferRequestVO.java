package com.restapi.channelenquiry.service;

import java.util.Date;



public class BatchC2cTransferRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "NGCB210709.002", required = true)
	private String batchId;
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true)
	private String domainCode;
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true)
	private String categoryCode;
	@io.swagger.v3.oas.annotations.media.Schema(example = "HARYANA", required = true)
	private String geographyCode;
	@io.swagger.v3.oas.annotations.media.Schema(example = "NGD0000002760", required = true)
	private String userId;
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true)
	private String productCode;
	@io.swagger.v3.oas.annotations.media.Schema(example = "01/07/21", required = true)
	private String fromDate;
	@io.swagger.v3.oas.annotations.media.Schema(example = "18/07/21", required = true)
	private String toDate;
	
	
	
	public BatchC2cTransferRequestVO() {
	}
	
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getGeographyCode() {
		return geographyCode;
	}
	public void setGeographyCode(String geographyCode) {
		this.geographyCode = geographyCode;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
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

	@Override
	public String toString() {
		return "BatchC2cTransferRequestVO [batchId=" + batchId + ", domainCode=" + domainCode + ", categoryCode="
				+ categoryCode + ", geographyCode=" + geographyCode + ", userId=" + userId + ", productCode="
				+ productCode + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
	
}
